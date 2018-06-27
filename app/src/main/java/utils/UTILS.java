package utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

/*
Here I have a set of methods that will help me in many situations, such as creating and converting
files, checking for connectivity, debugging, rescaling, formatting and more.
*/
public class UTILS {

    private static boolean isDebugEnabled = true; //set to false before uploading to Play Store.

    public static synchronized void DebugLog(String TAG, Object o) {
        if(isDebugEnabled){
            System.out.println(TAG);
            System.out.println(String.valueOf(o));
            System.out.println("---");
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if(context == null){
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return ( (activeNetworkInfo != null) && (activeNetworkInfo.isConnected()) );
    }

    public static String getVolleyErrorDataString(byte[] data){
        String str = Base64.encodeToString(data, Base64.DEFAULT);
        return str;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

}
