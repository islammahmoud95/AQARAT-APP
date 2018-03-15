package com.sawa.aqarat.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sawa.aqarat.MainActivity;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class Genral_Notify_detail_activ extends AppCompatActivity {

    String general_Id;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    FrameLayout main_general_lay;

    TextView title_txt;
    TextView content_txt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_genral_notify_detail_activ);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        Intent main_intent = getIntent();
        if (main_intent != null) {
            general_Id = main_intent.getStringExtra("GENERAL_ID");

        }

        Log.d("idddd" , general_Id + "null");

        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        main_general_lay = findViewById(R.id.main_general_lay);

        title_txt = findViewById(R.id.head_dialog);
        content_txt = findViewById(R.id.content_txt);



        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Get_general_msg();
            Utility.dialog_Show(dialog_bar, this);

        } else {

            Snackbar snackbar = Snackbar
                    .make(main_general_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();

        }


        Button close_btn =  findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    public void Get_general_msg() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "users/history/admin?"
                + "api_token=" + sessionManager.getUserDetails().get("Server_token")
                + "&" + "notify_id=" + general_Id,
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


                                String title_str = data_obj.getString("title");
                                String content_str = data_obj.getString("msg");

                                title_txt.setText(title_str);
                                content_txt.setText(content_str);


                                dialog_bar.dismiss();


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


                                Utility.dialog_error(dialog_bar, Genral_Notify_detail_activ.this, s.toString());

                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());

                            e.printStackTrace();

                        }

//                        View myView = findViewById(R.id.circle_shape_lay);
//                        RelativeLayout myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
//                        Display display = getWindowManager().getDefaultDisplay();
//                        TextView title_act_name = findViewById(R.id.title_act_name);
//                        title_act_name.setText(getTitle());
//                        Shape_Animation(getApplicationContext(), display, myView, myView_bottom , title_act_name);
//                        ImageButton back_btn = findViewById(R.id.back_btn);
//                        back_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                finish();
//                            }
//                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_bar.dismiss();
                Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                Log.e("errror", error.getMessage() + "");

            }

        })

        {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("lang", sessionManager.getLang());
                Log.e("Params", params.toString());

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(stringRequest);
    }

}




