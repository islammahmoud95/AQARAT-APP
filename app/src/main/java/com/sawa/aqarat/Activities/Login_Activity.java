package com.sawa.aqarat.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sawa.aqarat.ADS.AlarmReceiver;
import com.sawa.aqarat.MainActivity;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.UnderlinedTextView;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;
import static com.sawa.aqarat.utilities.Utility.passLength;

public class Login_Activity extends AppCompatActivity {

    EditText input_email, input_password;

    String email_str, pass_str;


    Button login_btn;


    TextView skip_btn;

    UnderlinedTextView forget_pass_btn, register_btn;
    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;


    private ArrayList<String> city_array;
    private Spinner spinner_city;


    //google +
    private static final int RC_SIGN_IN = 11;
    private GoogleApiClient mGoogleApiClient;


    String social_id = "";
    CallbackManager callbackManager;
    public String social_email, userID;
    ImageButton face;
    String imageURL = "";

    LinearLayout login_main_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View myView = findViewById(R.id.circle_shape_lay);
        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
        TextView title_act_name = findViewById(R.id.title_act_name);
        title_act_name.setText(getTitle());
        Display display = getWindowManager().getDefaultDisplay();
        Shape_Animation(this, display, myView, myView_bottom , title_act_name);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        login_main_lay = findViewById(R.id.login_main_lay);

        //Initializing the ArrayList
        city_array = new ArrayList<>();
        spinner_city = findViewById(R.id.lang_spin);

        city_array.add("en");
        city_array.add("ar");

        spinner_city.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, city_array));

        if (sessionManager.getLang().equals("en")) {

            spinner_city.setSelection(0);
        } else {
            spinner_city.setSelection(1);
        }


        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                setLocale(city_array.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);


        callbackManager = CallbackManager.Factory.create();
        face = findViewById(R.id.facebook_log_btn);
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.dialog_Show(dialog_bar, getApplicationContext());
                LoginManager.getInstance().logInWithReadPermissions(Login_Activity.this,
                        Arrays.asList("public_profile", "user_friends", "email"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("Main", response.toString());

                        setProfileToView(object);

                        Bundle params = new Bundle();
                        params.putString("fields", "id,email,picture.type(large)");
                        new GraphRequest(AccessToken.getCurrentAccessToken(), userID, params, HttpMethod.GET, new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                if (response != null) {
                                    try {
                                        JSONObject data = response.getJSONObject();
                                        if (data.has("picture")) {

                                            imageURL = data.getJSONObject("picture").getJSONObject("data").getString("url");

//                                            Glide.with(getApplicationContext()).load(imageURL).thumbnail(0.5f).animate(R.anim.from_down).diskCacheStrategy(DiskCacheStrategy.RESULT)
//                                                    .into(profile_image);

                                            Log.v("MYIMAGE", String.valueOf(imageURL));

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).executeAsync();


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                dialog_bar.dismiss();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Login_Activity.this, R.string.falid_facebook, Toast.LENGTH_SHORT).show();
                dialog_bar.dismiss();
                Log.e("FACEBOOKERROR", String.valueOf(exception));
            }
        });


        ImageButton google_sign_btn = findViewById(R.id.google_login_btn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        google_sign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {
                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                } else {
                    Snackbar snackbar = Snackbar
                            .make(login_main_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


        skip_btn = findViewById(R.id.skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTaskRoot()) {
                    Intent go_to_main = new Intent(Login_Activity.this, MainActivity.class);
                    startActivity(go_to_main);
                    finish();
                } else {
                    finish();
                }
            }
        });

        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


                    if (validate()) {
                        Utility.dialog_Show(dialog_bar, Login_Activity.this);
                        Login_API();
                    }


                } else {

                    Snackbar snackbar = Snackbar
                            .make(login_main_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


        forget_pass_btn = findViewById(R.id.forget_pass_btn);
        forget_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent_forget = new Intent(Login_Activity.this, Forget_Pass_act.class);
                startActivity(intent_forget);

            }
        });


        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent go_to_reg = new Intent(getApplicationContext(), Register_Activity.class);
                startActivity(go_to_reg);

            }
        });

        Intent alarmIntent = new Intent(Login_Activity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Login_Activity.this, 0, alarmIntent, 0);

    }


    @Override
    protected void onResume() {
        super.onResume();
        cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // start();
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

        email_str = input_email.getText().toString().trim();
        pass_str = input_password.getText().toString().trim();

        if (email_str.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email_str).matches() || !Utility.validate(email_str)) {
            input_email.setError(getString(R.string.valid_email));
            valid = false;
        } else {
            input_email.setError(null);
        }

        if (pass_str.isEmpty() || passLength(pass_str)) {  //|| Utility.passLength(pass_str
            input_password.setError(getString(R.string.valid_pass));
            valid = false;
        } else {
            input_password.setError(null);
        }


        return valid;
    }


    public void Login_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/login",
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


                                JSONObject stat_reg = obj.getJSONObject("Data");
                                String stat_type = stat_reg.getString("activation_str");

                                // this for user
                                switch (stat_type) {
                                    case "waiting_Active": {

                                        Toast.makeText(getApplicationContext(), R.string.valid_active_account, Toast.LENGTH_LONG).show();

                                        String server_token = stat_reg.getString("api_token");
                                        Intent activ_reg = new Intent(getApplicationContext(), Activition_activity.class);
                                        activ_reg.putExtra("SERVER_TOKEN", server_token);
                                        startActivity(activ_reg);


                                        //this for provider
                                        break;
                                    }
                                    case "active": {

                                        String userID = stat_reg.getString("id");
                                        String userName = stat_reg.getString("name");
                                        String userEmail = stat_reg.getString("email");
                                        String userPone = stat_reg.getString("phone_num");
                                        String userPic = stat_reg.getString("img");
                                        String server_token = stat_reg.getString("api_token");
                                        boolean is_VIP = stat_reg.getBoolean("isUserVIP");


                                        sessionManager.createLoginSession(userName, server_token,
                                                userPic, userEmail, userID, userPone , is_VIP );

                                        Intent activ_reg = new Intent(getApplicationContext(), MainActivity.class);
                                        activ_reg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(activ_reg);

                                        Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();

                                        finish();
                                        //this for all
                                        break;
                                    }
                                    case "deactivated": {


                                        StringBuilder s = new StringBuilder(150);
                                        String street;
                                        JSONArray st = obj.getJSONArray("Errors");
                                        for (int i = 0; i < st.length(); i++) {
                                            street = st.getString(i);
                                            s.append(street);
                                            s.append("\n");
                                            Log.i("teeest", s.toString());
                                            // loop and add it to array or arraylist
                                        }


                                        Utility.dialog_error(dialog_bar, Login_Activity.this, s.toString());

                                        break;
                                    }
                                }

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

                                Utility.dialog_error(dialog_bar, Login_Activity.this, s.toString());

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

                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                sessionManager.setFirebase_Token(refreshedToken);

                Map<String, String> params = new HashMap<>();
                // params.put("login", loginJsonObject.toString());

                params.put("email", email_str);
                params.put("password", pass_str);
                params.put("device_token", refreshedToken);
                params.put("lang", sessionManager.getLang());
                params.put("source", "0");
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


    public void Login_Social_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/loginWithSocial",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api_res", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");

                            if (status) {


                                JSONObject stat_reg = obj.getJSONObject("Data");
                                String stat_type = stat_reg.getString("activation_str");

//                                // this for user
                                switch (stat_type) {
                                    case "waiting_Active": {

                                        Toast.makeText(getApplicationContext(), R.string.valid_active_account, Toast.LENGTH_LONG).show();

                                        String server_token = stat_reg.getString("api_token");
                                        Intent activ_reg = new Intent(getApplicationContext(), Activition_activity.class);
                                        activ_reg.putExtra("SERVER_TOKEN", server_token);
                                        startActivity(activ_reg);


                                        //this for provider
                                        break;
                                    }
                                    case "active": {

                                        String userID = stat_reg.getString("id");
                                        String userName = stat_reg.getString("name");
                                        String userEmail = stat_reg.getString("email");
                                        String userPone = stat_reg.getString("phone_num");
                                        String userPic = stat_reg.getString("img");
                                        String server_token = stat_reg.getString("api_token");
                                        boolean is_VIP = stat_reg.getBoolean("isUserVIP");


                                        sessionManager.createLoginSession(userName, server_token,
                                                userPic, userEmail, userID, userPone , is_VIP);
                                        sessionManager.setFirebase_Token(FirebaseInstanceId.getInstance().getToken());
                                        Intent activ_reg = new Intent(getApplicationContext(), MainActivity.class);
                                        activ_reg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(activ_reg);

                                        Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();

                                        finish();
                                        //this for all
                                        break;
                                    }
                                    case "deactivated": {


                                        StringBuilder s = new StringBuilder(150);
                                        String street;
                                        JSONArray st = obj.getJSONArray("Errors");
                                        for (int i = 0; i < st.length(); i++) {
                                            street = st.getString(i);
                                            s.append(street);
                                            s.append("\n");
                                            Log.i("teeest", s.toString());
                                            // loop and add it to array or arraylist
                                        }


                                        Utility.dialog_error(dialog_bar, Login_Activity.this, s.toString());

                                        break;
                                    }
                                }
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


                                Utility.dialog_error(dialog_bar, Login_Activity.this, s.toString());

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

                String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                Map<String, String> params = new HashMap<>();
                // params.put("login", loginJsonObject.toString());


                params.put("social_id", social_id);
                params.put("device_token", refreshedToken);
                params.put("lang", sessionManager.getLang());
                params.put("source", "0");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //google back data

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {


                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                if (account != null) {

                    social_id = account.getId();
                    Log.d("userid", social_id);


                    Utility.dialog_Show(dialog_bar, Login_Activity.this);
                    Login_Social_API();

                } else {
                    Log.d("goooogle", "empty");
                }


            } else {
                // Google Sign In failed, update UI appropriately
                Log.d("failled", "faaileed");
                dialog_bar.dismiss();

            }
        }


        //facebook social
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }

    private void setProfileToView(JSONObject jsonObject) {
        try {


            userID = jsonObject.getString("id");

            social_id = userID;

            Utility.dialog_Show(dialog_bar, Login_Activity.this);
            Login_Social_API();

            Log.v("PICTURE", social_id);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        sessionManager.set_user_lang(lang);


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
