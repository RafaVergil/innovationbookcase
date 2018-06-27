package br.com.rafaelverginelli.innovationbookcase;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import utils.CustomAppCompatActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MediaPlayActivity extends CustomAppCompatActivity {

    public static final String MEDIA_PLAY_KEY_CONTENT_TYPE = "contentType";
    public static final String MEDIA_PLAY_KEY_CONTENT = "content";
    public static final String MEDIA_PLAY_KEY_SEEKBAR = "seekbar";

    public static final int MEDIA_PLAY_CONTENT_TYPE_IMAGE = 0;
    public static final int MEDIA_PLAY_CONTENT_TYPE_VIDEO = 1;
    public static final int MEDIA_PLAY_CONTENT_TYPE_URL_VID = 2;

    private int contentType = -1;
    private String content = "";
    private int seekBar = 0;

    ImageView imgMediaPlay;
    VideoView videoMediaPlay;
    WebView webView;

    PhotoViewAttacher photoViewAttacher;

    FrameLayout frameBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);

        frameBase = (FrameLayout)findViewById(R.id.frameBase);

        imgMediaPlay = (ImageView)findViewById(R.id.imgMediaPlay);
        videoMediaPlay = (VideoView)findViewById(R.id.videoMediaPlay);
        webView = (WebView)findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(this.getFilesDir().getAbsolutePath() + "/cache");
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDatabasePath(this.getFilesDir().getAbsolutePath() + "/databases");

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(frameBase);
        videoMediaPlay.setMediaController(mediaController);

        Bundle b = getIntent().getExtras();

        if(b != null){
            contentType = b.getInt(MEDIA_PLAY_KEY_CONTENT_TYPE, -1);
            content = b.getString(MEDIA_PLAY_KEY_CONTENT, "");
            seekBar = b.getInt(MEDIA_PLAY_KEY_SEEKBAR, 0);

            if( (contentType >= 0) && (content.length() > 0) ){
                ConfigureMedia();
            }
            else{
                CallError(getString(R.string.error_media_error));
            }
        }
        else{
            CallError(getString(R.string.error_media_error));
        }
    }

    void ConfigureMedia(){
        switch (contentType){
            case MEDIA_PLAY_CONTENT_TYPE_IMAGE:
                imgMediaPlay.setVisibility(View.VISIBLE);
                videoMediaPlay.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);

                Picasso.with(this)
                        .load(content)
                        .fit()
                        .centerInside()
                        .into(imgMediaPlay, new Callback() {

                            @Override
                            public void onSuccess() {
                                if (photoViewAttacher != null) {
                                    photoViewAttacher.update();
                                } else {
                                    photoViewAttacher = new PhotoViewAttacher(imgMediaPlay);
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });

                break;

            case MEDIA_PLAY_CONTENT_TYPE_VIDEO:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                imgMediaPlay.setVisibility(View.GONE);
                videoMediaPlay.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);

                Uri uri = Uri.parse(content);
                videoMediaPlay.setVideoURI(uri);

                videoMediaPlay.seekTo(seekBar);

                videoMediaPlay.start();
                break;

            case MEDIA_PLAY_CONTENT_TYPE_URL_VID:

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                imgMediaPlay.setVisibility(View.GONE);
                videoMediaPlay.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);

                webView.loadUrl(content);// + "?rel=0&amp;autoplay=1");
                break;

            default:
                CallError(getString(R.string.error_media_error));
                break;
        }
    }

    void CallError(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
