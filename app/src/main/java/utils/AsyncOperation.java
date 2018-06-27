package utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import br.com.rafaelverginelli.innovationbookcase.R;

/**
 * Created by rafael.verginelli on 7/12/16.
 */
public class AsyncOperation extends AsyncTask<Hashtable<String, Object>, Integer, Boolean> {

    private String TAG = "AsyncOperation";

    private Activity activity;
    private int TASK_ID = -1;
    private int OP_ID = -1;
    IAsyncOpCallback callback;

    public static final int TASK_ID_LIST_BOOKS = 0;

    public AsyncOperation(Activity activity, int taskId, int opId, IAsyncOpCallback callback) {
        this.activity = activity;
        this.TASK_ID = taskId;
        this.OP_ID = opId;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Hashtable<String, Object>... params) {

        if (!(UTILS.isNetworkAvailable(activity))) {
            CallNoConnectionMessage();
            return false;
        }

        try {
            switch (TASK_ID) {

                case TASK_ID_LIST_BOOKS: {
                    CallTaskListBooks(params[0]);
                }
                break;

            }
        } catch (Exception e) {
            UTILS.DebugLog(TAG, e);
        } catch (Throwable throwable) {
            UTILS.DebugLog(TAG, throwable);
        }

        return false;
    }

    public interface IAsyncOpCallback {
        void CallHandler(int opId, JSONObject response, boolean success);

        void OnAsyncOperationSuccess(int opId, JSONObject response);

        void OnAsyncOperationError(int opId, JSONObject response);
    }

    Handler noConnectionMessageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(activity, activity.getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show();
        }
    };

    private void CallNoConnectionMessage() {
        noConnectionMessageHandler.sendEmptyMessage(0);
    }

    private void CallTaskListBooks(Hashtable<String, Object> _params) {

        String query = "";
        if(_params.containsKey("query") && _params.get("query") != null){
            query = String.valueOf(_params.get("query"));
        }

        int page = 1;
        if(_params.containsKey("page") && _params.get("page") != null){
            page = (int)_params.get("page");
        }

        int limit = 10;
        if(_params.containsKey("limit") && _params.get("limit") != null){
            limit = (int)_params.get("limit");
        }

        String method = String.format(CONSTANTS.urlBooksAPI, query, ((page - 1) * limit) + 1, limit, CONSTANTS.basicInfo);

        UTILS.DebugLog(TAG, "Getting this: " + method);

        /*
        I chose Android Networking over OkHttp and Volley because those two were very slow
        during the first request. Took between 10 and 15 seconds to complete the request.
         */

        AndroidNetworking.get(method)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        UTILS.DebugLog(TAG, "Response: " + response.toString());
                        callback.CallHandler(OP_ID, response, (response != JSONObject.NULL));
                    }
                    @Override
                    public void onError(ANError error) {
                        JSONObject response = new JSONObject();
                        if(error != null){
                            try {
                                response.put("errorCode", error.getErrorCode());
                                response.put("errorBody", error.getErrorBody());
                            }
                            catch (JSONException e){
                                UTILS.DebugLog(TAG, e);
                            }
                        }
                        UTILS.DebugLog(TAG, "Error! Response: " + response.toString());
                        callback.CallHandler(OP_ID, response, (response != JSONObject.NULL));
                    }
                });

    }
}