package br.com.rafaelverginelli.innovationbookcase;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapters.BooksAdapter;
import models.ResourceModel;
import utils.AsyncOperation;
import utils.CustomAppCompatActivity;
import utils.UTILS;

public class MainActivity extends CustomAppCompatActivity implements AsyncOperation.IAsyncOpCallback {

    private static final int OP_LIST_BOOKS = 0;
    private static final int OP_LIST_BOOKS_PAGE = 1;

    private boolean isRequestingBooks = false;

    private LinearLayout contentView;
    private EditText etxtSearch;
    private ImageView btnClear;
    private ListView listViewBooks;
    private TextView txtNoResults;
    private ProgressBar pbLoading;

    private BooksAdapter adapter = null;
    private RecyclerView.LayoutManager mLayoutManager = null;
    private List<ResourceModel> books = null;

    private Timer searchRequestTimer = null;
    private int maxTimeToRequest = 1250;
    int limit = 10;
    int margin = 5;

    int lastPageRequested = 0;

    private String query = "";

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        lastPageRequested = 0;
        adapter = null;
        listViewBooks.setAdapter(null);
        if (books != null){
            books.clear();
        }

        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            UTILS.DebugLog(TAG, e);
        }

        this.query = query;
    }

    private boolean isShowingKeyboard = false;

    public boolean isShowingKeyboard() {
        return isShowingKeyboard;
    }

    public void setShowingKeyboard(boolean showingKeyboard) {
        this.isShowingKeyboard = showingKeyboard;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentView = (LinearLayout)findViewById(R.id.contentView);
        etxtSearch = (EditText)findViewById(R.id.etxtSearch);
        btnClear = (ImageView)findViewById(R.id.btnClear);
        listViewBooks = (ListView)findViewById(R.id.listViewBooks);
        txtNoResults = (TextView)findViewById(R.id.txtNoResults);
        pbLoading = (ProgressBar)findViewById(R.id.pbLoading);

        txtNoResults.setText(R.string.search_first_access);
        txtNoResults.setVisibility(View.VISIBLE);

        listViewBooks.setVisibility(View.GONE);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( (etxtSearch.getText() != null) && (!etxtSearch.getText().toString().isEmpty()) ) {
                    etxtSearch.setText("");
                }
            }
        });

        etxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRequestTimer != null) {
                    searchRequestTimer.cancel();
                }
                setQuery(charSequence.toString());
            }

            @Override
            public void afterTextChanged(final Editable editable) {

                searchRequestTimer = new Timer();
                searchRequestTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        GetBooks();
                    }
                }, maxTimeToRequest);
            }
        });

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                UTILS.DebugLog(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15f) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    setShowingKeyboard(true);
                }
                else {
                    // keyboard is closed
                    setShowingKeyboard(false);
                }
            }
        });

        listViewBooks.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(isShowingKeyboard()){
                    UTILS.hideSoftKeyboard(MainActivity.this);
                }
                return false;
            }
        });

        listViewBooks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

                if(getQuery().isEmpty()){
                    return;
                }

                int total = 0;
                if(adapter != null){
                    total = adapter.getCount();
                }

                if(total < limit){
                    if(!getQuery().isEmpty()) {
                        return;
                    }
                }

                if(firstVisibleItem >= (totalItemCount - margin)){

                    if(isRequestingBooks){
                        return;
                    }

                    isRequestingBooks = true;
                    int page = (total / limit) + 1;

                    if(page > lastPageRequested){
                        lastPageRequested = page;
                        GetBooks(page);
                    }

                }

            }
        });


    }

    void ConfigureNoBooks(){
        txtNoResults.setText(R.string.search_no_results);
        pbLoading.setVisibility(View.GONE);
        listViewBooks.setVisibility(View.GONE);
        txtNoResults.setVisibility(View.VISIBLE);
    }

    void ConfigureBooks(List<ResourceModel> books){

        if(books == null || books.isEmpty()){
            ConfigureNoBooks();
            return;
        }

        pbLoading.setVisibility(View.GONE);
        txtNoResults.setVisibility(View.GONE);
        listViewBooks.setVisibility(View.VISIBLE);

        if(adapter == null) {
            adapter = new BooksAdapter(this, R.layout.list_item_book, books, new BooksAdapter.IBookSelectable() {
                @Override
                public void OnBookSelected(ResourceModel book, BooksAdapter.Holder holder) {
                    Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
                    intent.putExtra(BookDetailsActivity.KEY_BOOK, book);

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(MainActivity.this,
                                    (View) holder.imgCover, getString(R.string.transaction_book_cover));
                    startActivity(intent, options.toBundle());
                }
            });

            listViewBooks.setAdapter(adapter);
        }
        else {
            adapter.setData(books);
        }

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.attention);
        alertDialog.setMessage(String.format(getString(R.string.confirmation_quit_app), getString(R.string.app_name)));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent newIntent = new Intent();
                        newIntent.setAction(Intent.ACTION_MAIN);
                        newIntent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(newIntent);
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    void GetBooks(){

        isRequestingBooks = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbLoading.setVisibility(View.VISIBLE);
                txtNoResults.setVisibility(View.GONE);
                listViewBooks.setVisibility(View.GONE);

                Hashtable<String, Object> params = new Hashtable<>();
                params.put("query", getQuery());
                params.put("page", 1);
                params.put("limit", limit);
                new AsyncOperation(MainActivity.this, AsyncOperation.TASK_ID_LIST_BOOKS,
                        OP_LIST_BOOKS, MainActivity.this).execute(params);
            }
        });

    }

    void GetBooks(final int page){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Hashtable<String, Object> params = new Hashtable<>();
                params.put("query", getQuery());
                params.put("page", page);
                params.put("limit", limit);
                new AsyncOperation(MainActivity.this, AsyncOperation.TASK_ID_LIST_BOOKS,
                        OP_LIST_BOOKS_PAGE, MainActivity.this).execute(params);
            }
        });

    }

    @Override
    public void CallHandler(int opId, JSONObject response, boolean success) {
        Message message = new Message();
        Bundle b = new Bundle();
        b.putInt("opId", opId);
        b.putString("response", response.toString());
        b.putBoolean("success", success);
        message.setData(b);
        handlerOp.sendMessage(message);
    }

    private Handler handlerOp = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int opId = -1;
            JSONObject response = null;
            boolean success = false;

            try {
                opId = message.getData().getInt("opId");
                response = new JSONObject(message.getData().getString("response"));
                success = message.getData().getBoolean("success");
            } catch (JSONException e) {
                UTILS.DebugLog(TAG, "Error getting handlers params.");
                return false;
            }

            if (success) {
                OnAsyncOperationSuccess(opId, response);
            } else {
                OnAsyncOperationError(opId, response);
            }
            return false;
        }
    });

    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {

        switch (opId){
            case OP_LIST_BOOKS:
            case OP_LIST_BOOKS_PAGE: {
                isRequestingBooks = false;
                try{
                    if( (response.has("items")) && (response.get("items") != JSONObject.NULL) ){
                        if(opId == OP_LIST_BOOKS_PAGE){
                            books.addAll(books == null ? 0 : books.size(),
                                    ResourceModel.parseResourceList(response.getJSONArray("items")));
                        }
                        else {
                            books = ResourceModel.parseResourceList(response.getJSONArray("items"));
                        }
                        ConfigureBooks(books);
                        return;
                    }
                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }
                ConfigureNoBooks();
            }
            break;

        }

    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

        switch (opId){

            case OP_LIST_BOOKS: {
                isRequestingBooks = false;
                ConfigureNoBooks();
            }
            break;

            case OP_LIST_BOOKS_PAGE: {
                isRequestingBooks = false;
            }
            break;
        }
    }
}
