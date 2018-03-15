package com.sawa.aqarat.ADS;

/**
 * Created by Issak.Nabil on 2/11/2018.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.NotificationUtils;
import com.sawa.aqarat.utilities.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {


    Context context_parm;

    @Override
    public void onReceive(Context context, Intent intent) {

        context_parm = context;

        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();

      //  Log.d("ads" , "I'm running ad started");


//        intent = new Intent(context, MainActivity.class);
//        intent.putExtra("PRODUCT_ID", "5");
//
//        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0 /* Request code */,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
//        notificationBuilder.setContentTitle("sfafsf");
//        notificationBuilder.setContentText("sfsaf");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            notificationBuilder.setSmallIcon(R.drawable.app_title_img);
//            notificationBuilder.setColor(ContextCompat.getColor(context, R.color.white_one));
//        } else {
//            notificationBuilder.setSmallIcon(R.drawable.app_title_img);
//        }
//        notificationBuilder.setAutoCancel(true);
//        notificationBuilder.setSound(defaultSoundUri);
//        notificationBuilder.setContentIntent(pendingIntent2);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(150 /* ID of notification */, notificationBuilder.build());


          /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        if (Check_Con.getInstance(context).isOnline()) {

            if (!NotificationUtils.isAppIsInBackground(context)) {
                Get_AD_API();
            }else {
                cancel_AD();
            }

        } else {

            Log.d("ads" , "no internet connection");

        }

    }
    private PendingIntent pendingIntent;
    public void cancel_AD() {
        AlarmManager manager = (AlarmManager) context_parm.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pendingIntent);
            Log.d("ads", "ads stopped");
        }

    }

    public void Get_AD_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "ads/getRandomAds",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api", response);

                        try {

                            // JSON Object
                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONObject data_obj = obj.getJSONObject("Data");

                                final String url_ad = data_obj.getString("url");

                                final String name_ad_str = data_obj.getString("name");

                                final String image_ad = data_obj.getString("img");
                                if (!image_ad.equals("")) {

                                    Glide.with(context_parm)
                                            .load(image_ad)
                                            .asBitmap()
                                            .placeholder(R.drawable.add_photo)
                                            .error(R.drawable.add_photo)
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                    Bitmap bimtapImage = resource;
                                                    //Convert this bimtapImage to byte array

                                                    try {

                                                        Intent intent = new Intent(context_parm, AD_Activity.class);
                                                        intent.putExtra("AD_IMG", bimtapImage);
                                                        intent.putExtra("AD_NAME", name_ad_str);
                                                        intent.putExtra("AD_URL", url_ad);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        context_parm.startActivity(intent);

                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }



                                                }

                                                @Override
                                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                    super.onLoadFailed(e, errorDrawable);
                                                }
                                            });
                                }


                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());

                            e.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context_parm, R.string.server_down, Toast.LENGTH_LONG).show();
                Log.e("errror", error.getMessage() + "");

            }

        })

        {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                Log.e("Params", params.toString());

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(context_parm);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(stringRequest);
    }

}