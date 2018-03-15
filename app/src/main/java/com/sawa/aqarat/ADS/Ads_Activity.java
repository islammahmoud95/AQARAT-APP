package com.sawa.aqarat.ADS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sawa.aqarat.R;

public class Ads_Activity extends AppCompatActivity {
    TextView ad_name;
    ImageView ad_img;
    private ImageView close_ad;
    ConstraintLayout main_ad_lay;
    String ad_url_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_activity);
        DisplayMetrics dm=new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(dm);
       Display display=getWindowManager().getDefaultDisplay();

        getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        SharedPreferences sharedPreferences=getSharedPreferences("ADS",MODE_PRIVATE);
        sharedPreferences.getString("name"," ");
        ad_url_str=sharedPreferences.getString("url"," ");
        ad_img=findViewById(R.id.ad_img);
        ad_name=findViewById(R.id.ad_name);
        close_ad=findViewById(R.id.close_ad);
        main_ad_lay = findViewById(R.id.main_ad_lay);
        ad_name.setText(sharedPreferences.getString("name"," "));
        Glide.with(this).load(sharedPreferences.getString("img"," ")).into(ad_img);
        close_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        main_ad_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(ad_url_str));
                startActivity(i);
                finish();

            }
        });
    }
}
