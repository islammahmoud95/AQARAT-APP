package com.sawa.aqarat.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sawa.aqarat.ADS.AlarmReceiver;
import com.sawa.aqarat.MainActivity;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Update_Profile extends AppCompatActivity {


    EditText username_edt, phone_edt;
    TextView email_edt;
    String userName_str = "", phone_num_str = "", email_str = "";

    Button update_btn, cancel_btn;


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;


    LinearLayout main_update_lay;


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    CircleImageView profile_image;
    private String userChoosenTask;
    String image1_str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

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

        main_update_lay = findViewById(R.id.main_update_lay);
        username_edt = findViewById(R.id.input_name);
        phone_edt = findViewById(R.id.input_phone);
        email_edt = findViewById(R.id.input_email);

        profile_image = findViewById(R.id.user_image);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        ImageButton cam_picker_btn = findViewById(R.id.cam_picker_btn);
        cam_picker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TedPermission.with(Update_Profile.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                userChoosenTask = "Take Photo";
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CAMERA);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Toast.makeText(Update_Profile.this, "Permission Denied\n" + deniedPermissions.toString(),
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

                TedPermission.with(Update_Profile.this)
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
                                Toast.makeText(Update_Profile.this, "Permission Denied\n" + deniedPermissions.toString(),
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


        update_btn = findViewById(R.id.confirm_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (validate()) {
                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                        send_Update_Prof();
                    }

                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_update_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


            Utility.dialog_Show(dialog_bar, getApplicationContext());
            get_Profile_API();

        } else {
            Snackbar snackbar = Snackbar
                    .make(main_update_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();
        }


        Intent alarmIntent = new Intent(Update_Profile.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Update_Profile.this, 0, alarmIntent, 0);

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

        try {
            // required
            userName_str = username_edt.getText().toString().trim();
            phone_num_str = phone_edt.getText().toString().trim();
            email_str = email_edt.getText().toString().trim();


        } catch (Exception e) {
            Log.e("test", e.getMessage());
        }

        if (userName_str.isEmpty() || userName_str.length() <= 5) {
            username_edt.setError(getString(R.string.valid_user));
            valid = false;
        } else {
            username_edt.setError(null);
        }


        if (phone_num_str.isEmpty() || !Patterns.PHONE.matcher(phone_num_str).matches() || !Utility.isValidMobile(phone_num_str)) {
            phone_edt.setError(getString(R.string.valid_number));
            valid = false;
        } else {
            phone_edt.setError(null);

        }


        if (email_str.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email_str).matches() || !Utility.validate(email_str)) {
            email_edt.setError(getString(R.string.valid_email));
            valid = false;
        } else {
            email_edt.setError(null);
        }

        return valid;
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


    public void get_Profile_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "users/getProfile?"
                + "api_token=" + sessionManager.getUserDetails().get("Server_token"),
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


                                JSONObject data_obj = obj.getJSONObject("Data");

                                username_edt.setText(data_obj.getString("f_name"));
                                email_edt.setText(data_obj.getString("email"));
                                phone_edt.setText(data_obj.getString("phone_num"));


                                String image_prof = data_obj.getString("profile_img");

                                profile_image.setBackground(null);

                                if (!image_prof.equals("")) {
                                    Glide.with(getApplicationContext()).load(image_prof).animate(R.anim.zoom_in)
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT).centerCrop()
                                            .into(profile_image);

                                    profile_image.setBorderColor(getResources().getColor(R.color.purple_color));
                                    profile_image.setBorderWidth(20);


                                    Log.d("image", image_prof);

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

                                Utility.dialog_error(dialog_bar, Update_Profile.this, s.toString());

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
                // params.put("login", loginJsonObject.toString());
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


    public void send_Update_Prof() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/editProfile",
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


                                JSONObject data_obj = obj.getJSONObject("Data");


                                String userID = data_obj.getString("id");
                                String userName = data_obj.getString("f_name");
                                String userEmail = data_obj.getString("email");
                                String userPone = data_obj.getString("phone_num");
                                String userPic = data_obj.getString("profile_img");
                                String server_token = data_obj.getString("api_token");
                                boolean is_VIP = data_obj.getBoolean("isUserVIP");


                                sessionManager.createLoginSession(userName, server_token,
                                        userPic, userEmail, userID, userPone , is_VIP );

                                Intent go_to_main = new Intent(getApplicationContext(), MainActivity.class);
                                go_to_main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(go_to_main);


                                Toast.makeText(getApplicationContext(), R.string.suc_prof_uptd, Toast.LENGTH_LONG).show();


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

                                Utility.dialog_error(dialog_bar, Update_Profile.this, s.toString());

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
                    image = "";
                }else
                {
                    image = getStringImage(bitmap);
                }


                Map<String, String> params = new HashMap<>();
                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("f_name", userName_str);
                params.put("phone_num", phone_num_str);
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


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
