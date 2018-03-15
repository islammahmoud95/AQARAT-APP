package com.sawa.aqarat.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.sawa.aqarat.ADS.Ads;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Forget_Pass_act extends AppCompatActivity {


    private EditText inputEmail;
    String email_str;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    LinearLayout main_forget_lay;

    Button forget_pass_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_act);

        View myView = findViewById(R.id.circle_shape_lay);
        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
        TextView title_act_name = findViewById(R.id.title_act_name);
        title_act_name.setText(getTitle());
        Display display = getWindowManager().getDefaultDisplay();
        Shape_Animation(this, display, myView, myView_bottom , title_act_name);

        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        main_forget_lay =  findViewById(R.id.main_forget_lay);


        inputEmail =  findViewById(R.id.input_email);


        forget_pass_btn =  findViewById(R.id.send_forget_btn);
        forget_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test_con();
            }
        });


    }


    public void test_con() {
        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


            if (validate()) {
                Utility.dialog_Show(dialog_bar , this);
                Forget_Pass();
            }


        } else {


            Snackbar snackbar = Snackbar
                    .make(main_forget_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();
        }
    }


    public boolean validate() {
        boolean valid = true;

        email_str = inputEmail.getText().toString().trim();


        if (email_str.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email_str).matches() || !Utility.validate(email_str)) {
            inputEmail.setError(getString(R.string.valid_email));
            valid = false;
        } else {
            inputEmail.setError(null);
        }


        return valid;
    }


    public void Forget_Pass() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/resetPassword",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api_res", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");

                            if (status) {
                                dialog_bar.dismiss();


                                show_Forget_Dialog();

                                // for error
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


                                Utility.dialog_error(dialog_bar , Forget_Pass_act.this, s.toString());

                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());
                            dialog_bar.dismiss();
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

                params.put("email", email_str);
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


    TextInputEditText new_pass_forget_edt;
    TextInputEditText reset_code_edt;
    TextInputEditText confirm_pass_edt;

    private void show_Forget_Dialog() {
        final Dialog dialog = new Dialog(Forget_Pass_act.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setContentView(R.layout.dialog_forget_pass);
        dialog.setCancelable(false);

        reset_code_edt = dialog.findViewById(R.id.input_verfy_code);
        new_pass_forget_edt = dialog.findViewById(R.id.input_new_password);
        confirm_pass_edt = dialog.findViewById(R.id.input_old_password);

        Button send_new_pass_btn = dialog.findViewById(R.id.reset_pass_btn);
        //  mRegister.setTypeface(mBaseTextFont);
        send_new_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (validate_dialog()) {

                        // dialog.dismiss();

                        Utility.dialog_Show(dialog_bar , getApplicationContext());

                        request_New_Pass_Dialog_api();

                    }

                } else {


                    Snackbar snackbar = Snackbar
                            .make(main_forget_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


        Button cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    String reset_code_str, new_Pass_Str , confirm_pass_str;

    public boolean validate_dialog() {
        boolean valid = true;

        try {


            reset_code_str = reset_code_edt.getText().toString().trim();
            new_Pass_Str = new_pass_forget_edt.getText().toString().trim();
            confirm_pass_str = confirm_pass_edt.getText().toString().trim();

        } catch (Exception e) {

            Log.e("test", e.getMessage());
        }


        if (new_Pass_Str.isEmpty() || new_Pass_Str.length() <= 5 ) {
            new_pass_forget_edt.setError(getString(R.string.valid_pass));
            valid = false;
        } else {
            new_pass_forget_edt.setError(null);
        }


        if (confirm_pass_str.isEmpty() || !Utility.isMatching(new_Pass_Str , confirm_pass_str)) {
            confirm_pass_edt.setError(getString(R.string.valid_confirm));
            valid = false;
        } else {
            confirm_pass_edt.setError(null);
        }


        if (reset_code_str.isEmpty() || reset_code_str.length() != 4) {
            reset_code_edt.setError("verification Code must be 4 char");

            valid = false;
        } else {
            reset_code_edt.setError(null);

        }


        return valid;
    }

    public void request_New_Pass_Dialog_api() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL  + "users/newPassword",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api_res", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {
                                dialog_bar.dismiss();

                                String msg = obj.getString("Message");

                                Utility.dialog_success(dialog_bar ,Forget_Pass_act.this);
                                dialog_bar.setContentText(msg);
                                dialog_bar.setCancelable(false);

                                new Handler().postDelayed(new Runnable() { // Tried new Handler(Looper.myLopper()) also
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            dialog_bar.dismiss();

                                            Intent go_to_login = new Intent(getApplicationContext(), Login_Activity.class);
                                            go_to_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(go_to_login);
                                            finish();

                                        }
                                    }, 2000);




                                Toast.makeText(getApplicationContext(), R.string.pass_chng_succ, Toast.LENGTH_LONG).show();


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

                                Utility.dialog_error(dialog_bar,Forget_Pass_act.this, s.toString());

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
                // params.put("login", loginJsonObject.toString());
                params.put("email", email_str);
                params.put("resetCode", reset_code_str);
                params.put("newPassword", new_Pass_Str);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
