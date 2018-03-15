package com.sawa.aqarat.ADS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Issak.Nabil on 3/12/2018.
 */

public class Ads {
    boolean x;
    int num ;
    SharedPreferences numshared;
    SharedPreferences sharedPreferences;
    Context context;
    private SessionManager sessionManager;


    public Ads(Context context) {
        this.context=context;
sessionManager=new SessionManager(context);
        sharedPreferences = context.getSharedPreferences("ADS",Context.MODE_PRIVATE);
        numshared = context.getSharedPreferences("counter",Context.MODE_PRIVATE);
        num=numshared.getInt("num",0);
        num = num + 1;
       // Toast.makeText(context,String.valueOf(num),Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor=numshared.edit();
        editor.putInt("num",num);
        editor.commit();


    }


    public void getprefixes(final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url;

            url = Utility.URL + "ads/getRandomAds?lang="+sessionManager.getLang();


        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {

                            Log.d("My", s);
                            JSONObject e = new JSONObject(s);
                            JSONObject a = e.getJSONObject("Data");
                          //  Toast.makeText(context,a.getString("name"),Toast.LENGTH_LONG).show();
                            // Toast.makeText(context,String.valueOf(c.length()),Toast.LENGTH_LONG).show();
                            if(e.getString("Data")!=null) {
                                SharedPreferences.Editor adsdetailed = sharedPreferences.edit();
                                adsdetailed.putInt("id", a.getInt("id"));
                                adsdetailed.putString("name", a.getString("name"));
                                adsdetailed.putString("img", a.getString("img"));
                                adsdetailed.putString("url", a.getString("url"));
                                adsdetailed.apply();
                                Intent intent = new Intent(context, Ads_Activity.class);
                                context.startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("gggg",e.getMessage());
                        }
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "check your network connection", Toast.LENGTH_LONG).show();
                        // L.T(context,"there is a trouble with connectivity \n please check your network connection");
                        Toast.makeText(context, "error message sessions: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        requestQueue.add(request);


    }
    public void returnnum() {

        if (num>=3 ) {
            getprefixes(context);

            numshared = context.getSharedPreferences("counter",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=numshared.edit();
            editor.clear();
            editor.commit();

        }
    }


}
