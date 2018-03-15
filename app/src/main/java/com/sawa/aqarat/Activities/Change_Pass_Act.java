package com.sawa.aqarat.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.sawa.aqarat.ADS.AlarmReceiver;
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

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;
import static com.sawa.aqarat.utilities.Utility.passLength;

public class Change_Pass_Act extends AppCompatActivity {

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;
    LinearLayout main_lay;


    EditText input_old_pass, input_new_password, input_Confirm_password;

    String old_pass_str, pass_new_str, confirm_pass_str;


    Button confirm_btn, cancel_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);


        View myView = findViewById(R.id.circle_shape_lay);
        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
        Display display = getWindowManager().getDefaultDisplay();
        TextView title_act_name = findViewById(R.id.title_act_name);
        title_act_name.setText(getTitle());
        Shape_Animation(getApplicationContext(), display, myView, myView_bottom , title_act_name);
        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());
        main_lay = findViewById(R.id.main_lay);

        input_old_pass = findViewById(R.id.input_old_pass);
        input_new_password = findViewById(R.id.input_new_password);
        input_Confirm_password = findViewById(R.id.input_Confirm_password);




        confirm_btn = findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (validate()) {
                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                        Change_pass_API();
                    }

                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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

        cancel_btn = findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        Intent alarmIntent = new Intent(Change_Pass_Act.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Change_Pass_Act.this, 0, alarmIntent, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
     //   start();
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



    public boolean validate() {
        boolean valid = true;


        old_pass_str = input_old_pass.getText().toString().trim();
        pass_new_str = input_new_password.getText().toString().trim();
        confirm_pass_str = input_Confirm_password.getText().toString().trim();


        if (old_pass_str.isEmpty() || passLength(old_pass_str)) {  //|| Utility.passLength(pass_str
            input_old_pass.setError(getString(R.string.valid_pass));
            valid = false;
        } else {
            input_old_pass.setError(null);
        }

        if (pass_new_str.isEmpty() || Utility.passLength(pass_new_str)) {
            input_new_password.setError(getString(R.string.valid_pass));
            valid = false;
        } else {
            input_new_password.setError(null);

        }

        if (!Utility.isMatching(pass_new_str, confirm_pass_str)) {
            input_Confirm_password.setError(getString(R.string.valid_confirm));
            valid = false;
        } else {
            input_Confirm_password.setError(null);

        }

        return valid;
    }


    public void Change_pass_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/changePassword",
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

                                Toast.makeText(getApplicationContext(), R.string.pass_chng_succ, Toast.LENGTH_LONG).show();

                                Intent go_to_main = new Intent(getApplicationContext(), MainActivity.class);
                                go_to_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(go_to_main);
                                finish();



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


                                Utility.dialog_error(dialog_bar, Change_Pass_Act.this, s.toString());

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
                params.put("lang", sessionManager.getLang());
                params.put("oldPassword", old_pass_str);
                params.put("newPassword", pass_new_str);
                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
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
