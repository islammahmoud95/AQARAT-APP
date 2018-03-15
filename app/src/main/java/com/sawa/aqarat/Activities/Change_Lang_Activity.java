package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.MainActivity;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Change_Lang_Activity extends AppCompatActivity {

    ImageButton eng_lang;
    ImageButton ar_lang;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    LinearLayout main_lang_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang);
        Ads ads=new Ads(this);
        ads.returnnum();
        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        View myView = findViewById(R.id.circle_shape_lay);
        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
        Display display = getWindowManager().getDefaultDisplay();
        TextView title_act_name = findViewById(R.id.title_act_name);
        title_act_name.setText(getTitle());
        Shape_Animation(getApplicationContext(), display, myView, myView_bottom, title_act_name);
        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        main_lang_lay = findViewById(R.id.main_lang_lay);
        sessionManager = new SessionManager(getApplicationContext());


        eng_lang = (ImageButton) findViewById(R.id.english_lang);
        eng_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eng_lang.setSelected(true);
                ar_lang.setSelected(false);

                if (sessionManager.isLoggedIn()) {
                    if (Check_Con.getInstance(getApplicationContext()).isOnline()) {
                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                        send_New_Lang("en");
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(main_lang_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });

                        snackbar.show();
                    }

                } else {
                    setLocale("en");
                }

            }
        });

        ar_lang = (ImageButton) findViewById(R.id.arabic_lang);
        ar_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eng_lang.setSelected(false);
                ar_lang.setSelected(true);

                if (sessionManager.isLoggedIn()) {
                    if (Check_Con.getInstance(getApplicationContext()).isOnline()) {
                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                        send_New_Lang("ar");
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(main_lang_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });

                        snackbar.show();
                    }
                }else {
                    setLocale("ar");
                }

            }
        });


        Glide.with(getApplicationContext())
                .load(R.drawable.arabic)
                .asGif()
                .placeholder(R.drawable.arabic)
                .into(ar_lang);


        Glide.with(getApplicationContext())
                .load(R.drawable.english)
                .asGif()
                .placeholder(R.drawable.english)
                .into(eng_lang);


    }

    @Override
    protected void onResume() {
        super.onResume();
        eng_lang.setSelected(false);
        ar_lang.setSelected(false);
    }

    public void setLocale(String lang) {


        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        sessionManager.set_user_lang(lang);


        Intent refresh = new Intent(this, MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);


    }


    private Request.Priority mPriority_Category = Request.Priority.IMMEDIATE;

    public void send_New_Lang(final String LANG) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/changeLanguage",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                dialog_bar.dismiss();

                                setLocale(LANG);

                            } else {

                                StringBuilder s = new StringBuilder();
                                String street;
                                JSONArray st = obj.getJSONArray("Errors");
                                for (int i = 0; i < st.length(); i++) {
                                    street = st.getString(i);
                                    s.append(street);
                                    s.append("\n");
                                    Log.i("teeest", s.toString());
                                    // loop and add it to array or arraylist
                                }


                                Utility.dialog_error(dialog_bar, Change_Lang_Activity.this, s.toString());

                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());

                            e.printStackTrace();

                        }

                    }
                },


                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog_bar.dismiss();

                        Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();

                        Log.e("error", error.getMessage() + "");
                    }

                })

        {

            @Override
            public Priority getPriority() {
                return mPriority_Category;
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("lang", LANG);

                Log.d("ProductsParams", params.toString());

                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
