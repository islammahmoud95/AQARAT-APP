package com.sawa.aqarat.ADS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sawa.aqarat.R;

import java.io.ByteArrayOutputStream;


public class AD_Activity extends AppCompatActivity {


    ConstraintLayout main_ad_lay;
    ImageView ad_img , close_ad;
    TextView ad_name_txt;


    Bitmap bitmap;
    String ad_name_str, ad_url_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_ad_activity);

        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);


        Intent intent = getIntent();
        if (intent != null) {

            bitmap = intent.getParcelableExtra("AD_IMG");
            ad_name_str = intent.getStringExtra("AD_NAME");
            ad_url_str = intent.getStringExtra("AD_URL");
        }


        main_ad_lay = findViewById(R.id.main_ad_lay);
        close_ad = findViewById(R.id.close_ad);

        ad_img = findViewById(R.id.ad_img);
        ad_name_txt = findViewById(R.id.ad_name);




        ad_name_txt.setText(ad_name_str);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Glide.with(this)
                .load(stream.toByteArray())
                .asBitmap()
                .error(R.drawable.app_title_img)
                .into(ad_img);


        main_ad_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(ad_url_str));
                startActivity(i);
                finish();

            }
        });


        close_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent alarmIntent = new Intent(AD_Activity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AD_Activity.this, 0, alarmIntent, 0);

    }


    @Override
    protected void onResume() {
        super.onResume();
        cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        start();
    }

    private PendingIntent pendingIntent;

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pendingIntent);
            Log.d("ads" , "ads stopped");
        }

    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 *60 * 2 ; //1000 * 60 * 2
        if (manager != null) {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000 *60 * 2, interval, pendingIntent);
            Log.d("ads" , "ads started");
        }
    }



}
