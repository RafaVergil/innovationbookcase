package br.com.rafaelverginelli.innovationbookcase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import models.ResourceModel;
import utils.CustomAppCompatActivity;
import utils.UTILS;

public class BookDetailsActivity extends CustomAppCompatActivity {

    public static final String KEY_BOOK = "book";

    private Toolbar toolbar;
    private ImageView imgCover;
    private ImageView imgPortrait;
    private TextView txtTitle;
    private TextView txtSubtitle;
    private TextView txtAuthors;
    private TextView txtCategories;
    private TextView txtPublisher;
    private TextView txtDate;
    private TextView txtDescription;
    private Button btnMoreInfo;
    private Button btnPreview;

    private ResourceModel book = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        setTitle("");

        imgCover = (ImageView)findViewById(R.id.imgCover);
        imgPortrait = (ImageView)findViewById(R.id.imgPortrait);
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtSubtitle = (TextView)findViewById(R.id.txtSubtitle);
        txtAuthors = (TextView)findViewById(R.id.txtAuthors);
        txtCategories = (TextView)findViewById(R.id.txtCategories);
        txtPublisher = (TextView)findViewById(R.id.txtPublisher);
        txtDate = (TextView)findViewById(R.id.txtDate);
        txtDescription = (TextView)findViewById(R.id.txtDescription);
        btnMoreInfo = (Button)findViewById(R.id.btnMoreInfo);
        btnPreview = (Button)findViewById(R.id.btnPreview);


        if(getIntent().hasExtra(KEY_BOOK)){
            book = (ResourceModel)getIntent().getSerializableExtra(KEY_BOOK);
        }
        else {
            CallError();
            return;
        }

        ConfigureBook(book);
    }

    void CallError(){
        Toast.makeText(this, R.string.error_loading_book, Toast.LENGTH_LONG).show();
        finish();
    }

    void ConfigureBook(final ResourceModel book){
        if(book == null){
            CallError();
            return;
        }

        if(!book.getThumbnail().isEmpty()){
            Picasso.with(this).
                    load(book.getThumbnail()).
                    error(R.drawable.ic_book).
                    into(imgCover);

            Picasso.with(this).
                    load(book.getThumbnail()).
                    error(R.drawable.ic_book).
                    into(imgPortrait);
        }

        txtTitle.setVisibility(book.getTitle().isEmpty() ? View.GONE : View.VISIBLE);
        txtTitle.setText(book.getTitle());

        txtSubtitle.setVisibility(book.getSubtitle().isEmpty() ? View.GONE : View.VISIBLE);
        txtSubtitle.setText(book.getSubtitle());

        StringBuilder authors = new StringBuilder();
        int authorsSize = book.getAuthors().size();
        for(int i = 0; i < authorsSize; i++){
            authors.append(book.getAuthors().get(i));
            if(i < (authorsSize - 1)) {
                authors.append(getString(R.string.author_separator));
            }
        }
        txtAuthors.setVisibility(authors.toString().isEmpty() ? View.GONE : View.VISIBLE);
        txtAuthors.setText(String.format(getString(R.string.by_x), authors));

        StringBuilder categories = new StringBuilder();
        int categoriesSize = book.getCategories().size();
        for(int i = 0; i < categoriesSize; i++){
            categories.append(book.getCategories().get(i));
            if(i < (categoriesSize - 1)) {
                categories.append(getString(R.string.author_separator));
            }
        }
        txtCategories.setVisibility(categories.toString().isEmpty() ? View.GONE : View.VISIBLE);
        txtCategories.setText(categories);

        txtPublisher.setText(book.getPublisher().isEmpty() ? getString(R.string.text_is_empty) : book.getPublisher());

        if(book.getPublishDate().isEmpty()){
            txtDate.setText(getString(R.string.text_is_empty));
        }
        else {

            String dt = "";
            Date date = null;
            try {
                Locale currentLocale = getResources().getConfiguration().locale;
                date = new SimpleDateFormat("yyyy-MM-dd").parse(book.getPublishDate());
                dt = new SimpleDateFormat("MM/dd/yyyy", currentLocale).format(date);
            } catch (ParseException e) {
                UTILS.DebugLog(TAG, e);
                dt = book.getPublishDate();
            }

            txtDate.setText(dt);
        }

        txtDescription.setText(book.getDescription().isEmpty() ? getString(R.string.no_description) : book.getDescription());


        btnMoreInfo.setVisibility(book.getInfoLink().isEmpty() ? View.GONE : View.VISIBLE);
        btnPreview.setVisibility(book.getPreviewLink().isEmpty() ? View.GONE : View.VISIBLE);

        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(book.getInfoLink())));
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(book.getPreviewLink())));
            }
        });

        View.OnClickListener imageClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, MediaPlayActivity.class);
                intent.putExtra(MediaPlayActivity.MEDIA_PLAY_KEY_CONTENT, book.getThumbnail());
                intent.putExtra(MediaPlayActivity.MEDIA_PLAY_KEY_CONTENT_TYPE, MediaPlayActivity.MEDIA_PLAY_CONTENT_TYPE_IMAGE);
                startActivity(intent);
            }
        };

        if(!book.getThumbnail().isEmpty()){
            imgCover.setOnClickListener(imageClick);
            imgPortrait.setOnClickListener(imageClick);
        }
    }
}
