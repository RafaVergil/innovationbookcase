package br.com.rafaelverginelli.innovationbookcase;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import utils.CustomAppCompatActivity;

public class SplashScreenActivity extends CustomAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final TextView txtTitle = (TextView)findViewById(R.id.txtTitle);
        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(txtTitle, "alpha",
                0f, 1f);
        animAlpha.setDuration(2000);
        animAlpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                ObjectAnimator animScaleX = ObjectAnimator.ofFloat(txtTitle, "scaleX",
                        0.5f, 1.0f);
                animScaleX.setDuration(1850);
                animScaleX.start();

                ObjectAnimator animScaleY = ObjectAnimator.ofFloat(txtTitle, "scaleY",
                        0.5f, 1.0f);
                animScaleY.setDuration(1850);
                animScaleY.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                SplashScreenActivity.this.startActivity(
                        new Intent(SplashScreenActivity.this, MainActivity.class));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animAlpha.start();
    }
}
