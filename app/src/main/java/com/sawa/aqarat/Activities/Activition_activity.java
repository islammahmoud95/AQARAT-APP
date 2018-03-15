package com.sawa.aqarat.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.raycoarana.codeinputview.CodeInputView;
import com.raycoarana.codeinputview.OnCodeCompleteListener;
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

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Activition_activity extends AppCompatActivity {

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;


    TextView resend_btn;
    CodeInputView codeInputView;

    String activation_code = "0";

    LinearLayout main_activ_lay;

    String server_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_activition);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        View myView = findViewById(R.id.circle_shape_lay);
        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
        TextView title_act_name = findViewById(R.id.title_act_name);
        title_act_name.setText(getTitle());
        Display display = getWindowManager().getDefaultDisplay();
        Shape_Animation(this, display, myView, myView_bottom , title_act_name);

        Intent intent = getIntent();
        if (intent != null) {
            server_token = intent.getStringExtra("SERVER_TOKEN");
        }


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        main_activ_lay =  findViewById(R.id.main_activ_lay);


        resend_btn =  findViewById(R.id.resend);
        codeInputView =  findViewById(R.id.code);

        handler_sms();


        codeInputView.addOnCompleteListener(new OnCodeCompleteListener() {
            @Override
            public void onCompleted(String code) {

                activation_code = code;

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (activation_code.length() != 4) {
                        //Show error
                        codeInputView.setError("Your code is incorrect");
                    } else {

                        Utility.dialog_Show(dialog_bar , getApplicationContext());
                        sendActivationCode();

                    }


                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_activ_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            });

                    snackbar.show();
                }


            }
        });


        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


                    Utility.dialog_Show(dialog_bar , getApplicationContext());
                    handler_sms();

                    resendActivationCode();

                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_activ_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            });

                    snackbar.show();
                }


            }
        });

    }



    int time = 59;

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void handler_sms() {

        new CountDownTimer(59000, 1000) {
            public void onTick(long millisUntilFinished) {
                resend_btn.setEnabled(false);
                resend_btn.setText(getString(R.string.estmain_time, checkDigit(time)));
                time--;
            }

            public void onFinish() {
                resend_btn.setEnabled(true);
                resend_btn.setText(getString(R.string.click_here));
                time = 59;
            }
        }.start();

    }


    public void sendActivationCode() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/activeAccount",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                String msg = obj.getString("Message");

                                Toast.makeText(getApplicationContext() , msg , Toast.LENGTH_LONG).show();

                                Intent go_to_login = new Intent(getApplicationContext(), Login_Activity.class);
                                go_to_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(go_to_login);

                                finish();
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

                                codeInputView.setEditable(true);

                                codeInputView.setError(s.toString());


                                Utility.dialog_error(dialog_bar , Activition_activity.this, s.toString());

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

                dialog_bar.dismiss();
                Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                Log.e("errror", error.getMessage() + "");

            }

        })

        {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("code", activation_code);
                params.put("api_token", server_token);
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


    private void resendActivationCode() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/resendActiveCode",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                dialog_bar.dismiss();
                                codeInputView.setEditable(true);

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


                                codeInputView.setEditable(true);

                                codeInputView.setError(s.toString());


                                Utility.dialog_error(dialog_bar , Activition_activity.this, s.toString());

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

                dialog_bar.dismiss();
                Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                Log.e("errror", error.getMessage() + "");

            }

        })

        {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("api_token", server_token);
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
