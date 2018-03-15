package com.sawa.aqarat.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sawa.aqarat.ADS.AlarmReceiver;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.UnderlinedTextView;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Register_Activity extends AppCompatActivity {


    EditText username_edt, phone_edt, email_edt, password_edt, confirm_pass_edt;
    String userName_str = "", phone_num_str = "", pass_str = "", pass_aga_str = "", email_str = "";

    Button reg_btn;

    // spinners


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;


    ScrollView main_reg_scroll;


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    CircleImageView profile_image;
    private String userChoosenTask;
    String image1_str;


    AppCompatCheckBox mAgree;

    //google +
    private static final int RC_SIGN_IN = 11;
    private GoogleApiClient mGoogleApiClient;


    String social_id = "";
    CallbackManager callbackManager;
    public String social_email, userID;
    String imageURL = "";
    TimeZone tz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        View myView = findViewById(R.id.circle_shape_lay);
        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
        TextView title_act_name = findViewById(R.id.title_act_name);
        title_act_name.setText(getTitle());
        Display display = getWindowManager().getDefaultDisplay();
        Shape_Animation(this, display, myView, myView_bottom ,title_act_name);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        main_reg_scroll = findViewById(R.id.main_reg_scroll);
        username_edt = findViewById(R.id.input_name);
        phone_edt = findViewById(R.id.input_phone);
        email_edt = findViewById(R.id.input_email);
        password_edt = findViewById(R.id.input_password);
        confirm_pass_edt = findViewById(R.id.input_confirm_pass);

        profile_image = findViewById(R.id.user_image);

        mAgree = findViewById(R.id.reg_iAgree_checkbox);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        tz = TimeZone.getDefault();

        callbackManager = CallbackManager.Factory.create();
        ImageButton face = findViewById(R.id.facebook_log_btn);
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.dialog_Show(dialog_bar, getApplicationContext());
                LoginManager.getInstance().logInWithReadPermissions(Register_Activity.this,
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



                                            // we empty bitmap to send face img in register
                                            bitmap = null;
                                            profile_image.setBackground(null);

                                            Glide.with(getApplicationContext()).load(imageURL).animate(R.anim.zoom_in)
                                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                    .into(profile_image);

                                            profile_image.setBorderColor(getResources().getColor(R.color.purple_color));
                                            profile_image.setBorderWidth(20);
                                            dialog_bar.dismiss();

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
                Toast.makeText(Register_Activity.this, R.string.falid_facebook, Toast.LENGTH_SHORT).show();
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
                Utility.dialog_Show(dialog_bar, getApplicationContext());
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });


        ImageButton cam_picker_btn = findViewById(R.id.cam_picker_btn);
        cam_picker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TedPermission.with(Register_Activity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {

                                userChoosenTask = "Take Photo";
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CAMERA);

                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Toast.makeText(Register_Activity.this, "Permission Denied\n" + deniedPermissions.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setDeniedMessage("you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setGotoSettingButton(true)
                        .setDeniedTitle(R.string.app_name)

                        .check();



            }
        });

        ImageButton gallery_picker_btn = findViewById(R.id.gallery_picker_btn);
        gallery_picker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TedPermission.with(Register_Activity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {

                                userChoosenTask = "Choose from Library";
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);//
                                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Toast.makeText(Register_Activity.this, "Permission Denied\n" + deniedPermissions.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setDeniedMessage("you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setGotoSettingButton(true)
                        .setDeniedTitle(R.string.app_name)

                        .check();

            }
        });


        UnderlinedTextView terms_btn = findViewById(R.id.terms_btn);
        terms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    Terms_API();


                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_reg_scroll, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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

        reg_btn = findViewById(R.id.submit_reg_btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (validate()) {
                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                        Register_API();
                    }

                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_reg_scroll, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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

        UnderlinedTextView login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_to_login = new Intent(getApplicationContext(), Login_Activity.class);
                go_to_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(go_to_login);
                finish();
            }
        });


        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        Intent alarmIntent = new Intent(Register_Activity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Register_Activity.this, 0, alarmIntent, 0);


    }

    @Override
    protected void onResume() {
        super.onResume();
        cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
      //  start();
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

        try {

            // required
            userName_str = username_edt.getText().toString().trim();
            phone_num_str = phone_edt.getText().toString().trim();
            email_str = email_edt.getText().toString().trim();
            pass_str = password_edt.getText().toString().trim();
            pass_aga_str = confirm_pass_edt.getText().toString().trim();


        } catch (Exception e) {

            Log.e("test", e.getMessage());
        }


        if (userName_str.isEmpty() || userName_str.length() <= 5) {
            username_edt.setError(getString(R.string.valid_user));
            scrollToPosition((View) username_edt.getParent());
            valid = false;
        } else {
            username_edt.setError(null);

        }

        if (phone_num_str.isEmpty() || !Patterns.PHONE.matcher(phone_num_str).matches() || !Utility.isValidMobile(phone_num_str)) {
            phone_edt.setError(getString(R.string.valid_number));
            scrollToPosition((View) phone_edt.getParent());
            valid = false;
        } else {
            phone_edt.setError(null);

        }


        if (email_str.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email_str).matches() || !Utility.validate(email_str)) {
            email_edt.setError(getString(R.string.valid_email));
            scrollToPosition((View) email_edt.getParent());
            valid = false;
        } else {
            email_edt.setError(null);

        }


        if (pass_str.isEmpty() || Utility.passLength(pass_str)) {
            password_edt.setError(getString(R.string.valid_pass));
            scrollToPosition((View) password_edt.getParent());
            valid = false;
        } else {
            password_edt.setError(null);

        }


        if (!Utility.isMatching(pass_str, pass_aga_str)) {
            confirm_pass_edt.setError(getString(R.string.valid_confirm));
            scrollToPosition((View) confirm_pass_edt.getParent());
            valid = false;
        } else {
            confirm_pass_edt.setError(null);

        }


        if (!mAgree.isChecked()) {

            Toast.makeText(getApplicationContext(), R.string.accept_policy_reg, Toast.LENGTH_LONG).show();
            scrollToPosition((View) mAgree.getParent());
            valid = false;
        }

        return valid;
    }

    private void scrollToPosition(final View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                main_reg_scroll.smoothScrollTo(0, view.getTop());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);


                    } else if (userChoosenTask.equals("Choose from Library")) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);//
                        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                    }

                } else {
                    //code for deny
                    Toast.makeText(getApplicationContext(), R.string.acsess_denied, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    public void Terms_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "sitePolicy",
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
                                String terms_txt = stat_reg.getString("text");
                                show_Terms_Dialog(terms_txt);

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

                                Utility.dialog_error(dialog_bar, Register_Activity.this, s.toString());

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


    private void show_Terms_Dialog(String terms_txt) {
        final Dialog dialog = new Dialog(Register_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setContentView(R.layout.dialog_terms);
        dialog.setCancelable(true);

        TextView mTermsString = dialog.findViewById(R.id.reg_terms_tv);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mTermsString.setText(Html.fromHtml(terms_txt, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        } else {
            mTermsString.setText(Html.fromHtml(terms_txt));
        }


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


    public void Register_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/register",
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


                                String server_token = stat_reg.getString("api_token");
                                Intent activ_reg = new Intent(getApplicationContext(), Activition_activity.class);
                                activ_reg.putExtra("SERVER_TOKEN", server_token);
                                startActivity(activ_reg);

                                Toast.makeText(getApplicationContext(), R.string.valid_active_account, Toast.LENGTH_LONG).show();

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

                                Utility.dialog_error(dialog_bar, Register_Activity.this, s.toString());

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

                String image ;
                if (bitmap == null)
                {
                    image = imageURL;
                }else
                {
                    image = getStringImage(bitmap);
                }


                Map<String, String> params = new HashMap<>();
                // params.put("login", loginJsonObject.toString());

                params.put("f_name", userName_str);
                params.put("email", email_str);
                params.put("password", pass_str);
                params.put("password_confirmation", pass_aga_str);
                params.put("phone_num", phone_num_str);
                params.put("source", "0");
                params.put("lang", sessionManager.getLang());
                params.put("timeZone", tz.getID());
                params.put("social_id", social_id);
                params.put("profile_pic", image);

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


    private Bitmap bitmap;
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

                    userName_str = account.getDisplayName();
                    username_edt.setText(userName_str);


                    social_email = account.getEmail();
                    email_edt.setText(social_email);
                    email_edt.setEnabled(false);


                    imageURL = String.valueOf(account.getPhotoUrl());
                    Log.d("userid", imageURL);

                    Glide.with(getApplicationContext()).load(imageURL).animate(R.anim.zoom_in)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(profile_image);

                    profile_image.setBorderColor(getResources().getColor(R.color.purple_color));
                    profile_image.setBorderWidth(20);

                    // we empty bitmap to send face img in register
                    bitmap = null;

                    dialog_bar.dismiss();

                } else {
                    Log.d("goooogle", "empty");
                    dialog_bar.dismiss();
                }


            } else {
                // Google Sign In failed, update UI appropriately
                Log.d("failled", "faaileed");
                dialog_bar.dismiss();

            }
        }


        //facebook social
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);

            }
        }


    }


    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm;


        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                int width = bm.getWidth();
                int height = bm.getHeight();

                float bitmapRatio = (float) width / (float) height;
                if (bitmapRatio > 0) {
                    width = 800;
                    height = (int) (width / bitmapRatio);
                } else {
                    height = 600;
                    width = (int) (height * bitmapRatio);
                }
                bitmap = Bitmap.createScaledBitmap(bm, width, height, true);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        profile_image.setBackground(null);
        profile_image.setImageBitmap(bitmap);
        image1_str = getStringImage(bitmap);
        profile_image.setBorderColor(getResources().getColor(R.color.purple_color));
        profile_image.setBorderWidth(20);


    }


    // from camera
    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (thumbnail != null) {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, bytes);

            int width = thumbnail.getWidth();
            int height = thumbnail.getHeight();

            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 0) {
                width = 800;
                height = (int) (width / bitmapRatio);
            } else {
                height = 600;
                width = (int) (height * bitmapRatio);
            }

            bitmap = Bitmap.createScaledBitmap(thumbnail, width, height, true);
        }

        profile_image.setBackground(null);
        profile_image.setImageBitmap(bitmap);
        image1_str = getStringImage(bitmap);
        profile_image.setBorderColor(getResources().getColor(R.color.purple_color));
        profile_image.setBorderWidth(20);

    }


    // encode to base64
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] imageBytes = baos.toByteArray();

        //"data:image/jpeg;base64,"
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("tess", encodedImage);
        return encodedImage;
    }


    private void setProfileToView(JSONObject jsonObject) {
        try {


            social_email = jsonObject.getString("email");
            email_edt.setText(social_email);
            email_edt.setEnabled(false);


            userName_str = jsonObject.getString("name");
            username_edt.setText(userName_str);


            userID = jsonObject.getString("id");
            social_id = userID;


            userID = jsonObject.getString("id");
            social_id = userID;


            Log.d("userid", social_id);

            Log.v("EMAIL", social_email);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
