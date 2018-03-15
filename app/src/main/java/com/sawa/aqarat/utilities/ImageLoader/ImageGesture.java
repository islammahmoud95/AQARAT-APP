package com.sawa.aqarat.utilities.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;
import java.util.ArrayList;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ImageGesture extends Activity {
    String url;

    SweetAlertDialog dialog_bar;

    private int currentImage = 0;
    ArrayList<String> list ;
    ImageButton nextimg,previousImg;
    SessionManager sessionManager;
    private TouchImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zomming);




        image =  findViewById(R.id.my_full_image);
        nextimg = findViewById(R.id.next_img);
        previousImg = findViewById(R.id.previous_img);

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.getLang().equals("ar"))
        {
            nextimg.setRotation(180);
            previousImg.setRotation(180);
        }


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);


        Intent intent = getIntent();

        // IF url is single image thats mean name URL_OF_IMAGE
        //if more thant on image that's mean URL_OF_IMAGES

        if (intent.getStringExtra("URL_OF_IMAGE") == null) {


            url = intent.getStringExtra("URL_OF_IMAGES");

            list = new ArrayList<>(Arrays.asList(url.split(", ")));

            Utility.dialog_Show(dialog_bar , this);

//            ImageLoader imageLoader_menu = new ImageLoader(this);
//            imageLoader_menu.DisplayImage(url, image);

            Glide.with(this).load(list.get(currentImage)).placeholder(R.mipmap.ic_launcher)
                    .animate(R.anim.zoom_in).diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                    dialog_bar.dismiss();
                    return false;
                }
            }).into(image);

            Log.d("imaaage", "empty");




            nextimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //      currentImage++;
//                if ( currentImage >= list.size()) {
//                    currentImage = 0;
//
//                }

                    Log.d("safsafsfstop", String.valueOf(list.size()));
                    Log.d("curimage", list.get(currentImage));


                    if (currentImage < list.size()-1) {
                        currentImage++;

                    } else {

                        currentImage = 0;
                    }

                    Utility.dialog_Show(dialog_bar , ImageGesture.this);

                    Glide.with(ImageGesture.this).load(list.get(currentImage)).placeholder(R.mipmap.ic_launcher)
                            .animate(R.anim.zoom_in).diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            dialog_bar.dismiss();
                            return false;
                        }
                    }).into(image);




                }
            });

            previousImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Log.d("safsafsfstop", String.valueOf(list.size()));
                    Log.d("safsafsfstop", String.valueOf(currentImage));


                    if (currentImage > 0) {
                        currentImage--;

                    } else {

                        currentImage = list.size()-1;
                    }

                    Utility.dialog_Show(dialog_bar , getApplicationContext());


                    Glide.with(ImageGesture.this).load(list.get(currentImage)).placeholder(R.drawable.app_title_img)
                            .animate(R.anim.zoom_in).diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            dialog_bar.dismiss();
                            return false;
                        }
                    }).into(image);


                    Log.d("safsafsfs",list.get(currentImage));


                }});


        } else {

            nextimg.setVisibility(View.GONE);
            previousImg.setVisibility(View.GONE);

            url = intent.getStringExtra("URL_OF_IMAGE");


            Log.d("imaaage", url);

            Utility.dialog_Show(dialog_bar , this);


            Glide.with(this).load(url).animate(R.anim.zoom_in).placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                    dialog_bar.dismiss();
                    return false;
                }
            }).into(image);

        }

    }

}
