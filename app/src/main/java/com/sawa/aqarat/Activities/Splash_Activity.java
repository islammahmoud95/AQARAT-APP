package com.sawa.aqarat.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.sawa.aqarat.MainActivity;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Splash_anime.ShimmerFrameLayout;
import com.sawa.aqarat.utilities.Splash_anime.view.ResizeCallbackImageView;

import java.util.Locale;

public class Splash_Activity extends AppCompatActivity {

    SessionManager sessionManager;
    private ShimmerFrameLayout mShimmerViewContainer;

    ResizeCallbackImageView mIvSplash;

    ImageView right_Img , left_Img , center_Img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(getApplicationContext());

        String languageToLoad = sessionManager.getLang(); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        sessionManager.set_user_lang(languageToLoad);


        // this shimmer
        setmShimmerViewContainer();


        // this for moving hand left and press
        mIvSplash = (ResizeCallbackImageView) findViewById(R.id.hand_icon);
        right_Img = (ImageView)findViewById(R.id.right_img);
        center_Img = (ImageView)findViewById(R.id.center_img);
        left_Img = (ImageView)findViewById(R.id.left_img);



        Animation animation_left_in = AnimationUtils.loadAnimation(Splash_Activity.this, R.anim.slide_in_right);
        animation_left_in.setStartOffset(1000);
        left_Img.startAnimation(animation_left_in);

        Animation animation_center_in = new AlphaAnimation(0, 1);
        animation_center_in.setStartOffset(2000);
        animation_center_in.setDuration(1000);
        center_Img.startAnimation(animation_center_in);

        Animation animation_right_in = AnimationUtils.loadAnimation(Splash_Activity.this, R.anim.slide_in_left);
        animation_right_in.setStartOffset(1000);
        right_Img.startAnimation(animation_right_in);



        Animation animation = AnimationUtils.loadAnimation(Splash_Activity.this, R.anim.enter);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        mIvSplash.startAnimation(animationSet);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mIvSplash,"translationX",+1000,0),
                ObjectAnimator.ofFloat(mIvSplash,"alpha",0,1)
                //  ObjectAnimator.ofFloat(text,"translationX",-200,0)
        );
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setStartDelay(2500);
        animatorSet.setDuration(1000);
        animatorSet.addListener(new AnimatorListenerAdapter(){

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);



            }

            @Override
            public void onAnimationEnd(Animator animation) {

                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(
                        ObjectAnimator.ofFloat(mIvSplash,"scaleX", 1f, 0.7f, 5f),
                        ObjectAnimator.ofFloat(mIvSplash,"scaleY", 1f, 0.7f, 5f),
                        ObjectAnimator.ofFloat(mIvSplash,"alpha",1,0)
                );

                animatorSet2.setInterpolator(new AccelerateInterpolator());
                animatorSet2.setDuration(1000);
                animatorSet2.start();

                Animation animation_left_out = AnimationUtils.loadAnimation(Splash_Activity.this, R.anim.slide_out_left);
                animation_left_out.setStartOffset(500);
                animation_left_out.setDuration(1000);
                left_Img.startAnimation(animation_left_out);

                Animation animation_center_out =  new AlphaAnimation(1, 0);
                animation_center_out.setStartOffset(500);
                animation_center_out.setDuration(1000);
                center_Img.startAnimation(animation_center_out);

                Animation animation_right_out = AnimationUtils.loadAnimation(Splash_Activity.this, R.anim.slide_out_right);
                animation_right_out.setStartOffset(500);
                animation_right_out.setDuration(1000);
                right_Img.startAnimation(animation_right_out);

            }
        });
        animatorSet.start();



        int SPLASH_DISPLAY_LENGTH = 4500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean isFirstTime = SessionManager.isFirst(Splash_Activity.this);

                if (isFirstTime) {
                  /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(Splash_Activity.this, Login_Activity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    /* Create an Intent that will start the login-Activity. */
                    Intent mainIntent = new Intent(Splash_Activity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }

            }
        }, SPLASH_DISPLAY_LENGTH);


    }





    public void setmShimmerViewContainer (){


        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);

        mShimmerViewContainer.setBaseAlpha(0);
        mShimmerViewContainer.setDuration(1500);
        mShimmerViewContainer.setDropoff(0.1f);
        mShimmerViewContainer.setIntensity(0.35f);
       // mShimmerViewContainer.setRepeatMode(ObjectAnimator.REVERSE);
        mShimmerViewContainer.setMaskShape(ShimmerFrameLayout.MaskShape.LINEAR);
        mShimmerViewContainer.setRepeatCount(0);

        mShimmerViewContainer.startShimmerAnimation();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // here put fade animation or any

                mShimmerViewContainer.setBaseAlpha(1);

                Animation animation2 = AnimationUtils.loadAnimation(Splash_Activity.this, R.anim.alpha);

                mShimmerViewContainer.setAnimation(animation2);

            }
        },1600);
        //   mPresetToast = Toast.makeText(this, "Spotlight", Toast.LENGTH_SHORT);

    }


    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }


}
