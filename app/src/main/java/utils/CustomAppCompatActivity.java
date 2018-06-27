package utils;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import br.com.rafaelverginelli.innovationbookcase.R;

/*
I use this class to handle events that I want to affect the activities, such as the TAG property,
Loading Dialog and Network listener.
 */

public class CustomAppCompatActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    public ProgressDialog pd;

    public void ShowLoadingDialog(){
        ShowLoadingDialog("", false);
    }

    public void ShowLoadingDialog(String message){
        ShowLoadingDialog(message, false);
    }

    public void ShowLoadingDialog(boolean cancelable){
        ShowLoadingDialog("", cancelable);
    }

    public void ShowLoadingDialog(String message, boolean cancelable){
        DismissLoadingDialog();
        pd = new ProgressDialog(this);
        pd.setMessage(message.isEmpty() ? getString(R.string.loading) : message);
        pd.setCancelable(cancelable);
        pd.show();
    }

    public void DismissLoadingDialog(){
        if(pd != null && pd.isShowing()){
            pd.cancel();
            pd.dismiss();
            pd = null;
        }
    }

    @Override
    protected void onDestroy() {
        DismissLoadingDialog();
        super.onDestroy();
    }

}
