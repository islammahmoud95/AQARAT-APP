package com.sawa.aqarat.Activities.Tickets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;


public class Create_Ticket extends AppCompatActivity {

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    LinearLayout main_create_ticket_lay;


    EditText reason_edt, description_edt;

    String reason_str, description_str;

    Button confirm_btn, cancel_btn;


    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private static final String TAG = "TedPicker";
    ArrayList<Uri> image_uris = new ArrayList<>();
    private ViewGroup mSelectedImagesContainer;

    ArrayList<String> images_base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);
        Ads ads=new Ads(this);
        ads.returnnum();
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

        main_create_ticket_lay = findViewById(R.id.main_create_ticket_lay);


        reason_edt = findViewById(R.id.reason_edt);
        description_edt = findViewById(R.id.description_edt);


        images_base64 = new ArrayList<>();

        mSelectedImagesContainer =  findViewById(R.id.selected_photos_container);
        View getImages2 = findViewById(R.id.get_images2);
        getImages2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TedPermission.with(Create_Ticket.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Toast.makeText(Create_Ticket.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                                Config config = new Config();
                                config.setCameraHeight(R.dimen.app_camera_height);
                                config.setToolbarTitleRes(R.string.app_name);
                                config.setSelectionMin(1);
                                config.setSelectionLimit(3);
                                config.setSelectedBottomHeight(R.dimen.bottom_height);
                                config.setFlashOn(true);


                                getImages(config);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Toast.makeText(Create_Ticket.this, "Permission Denied\n" + deniedPermissions.toString(),
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



        confirm_btn = findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


                    if (validate()) {

                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                        Create_Ticket_API();

                    }


                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_create_ticket_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.setupUI(main_create_ticket_lay, this);
    }


    private void getImages(Config config) {


        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(this, ImagePickerActivity.class);

        if (image_uris != null) {
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, image_uris);
        }


        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);

    }




    private void showMedia() {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();
        if (image_uris.size() >= 1) {
            mSelectedImagesContainer.setVisibility(View.VISIBLE);
        }

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());


        images_base64.clear();

        for (Uri uri : image_uris) {

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image);

            Glide.with(this)
                    .load(uri.toString())
                    .fitCenter()
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));


            Glide.with(this)
                    .load(uri.toString())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            String one_img = getStringImage(resource);
                            images_base64.add(one_img);
                        }
                    });
            Log.d("images_size", String.valueOf(images_base64.size()));

        }

    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();

        //"data:image/jpeg;base64,"
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d("tess", encodedImage);
        return encodedImage;
    }


    public boolean validate() {
        boolean valid = true;

        try {

            reason_str = reason_edt.getText().toString().trim();
            description_str = description_edt.getText().toString().trim();


        } catch (Exception e) {

            Log.e("test", e.getMessage());
        }


        if (reason_str.isEmpty() || reason_str.length() <= 5) {
            reason_edt.setError(getString(R.string.valid_reason_name));

            valid = false;
        } else {
            reason_edt.setError(null);

        }


        if (description_str.isEmpty() || description_str.length() <= 9) {
            description_edt.setError(getString(R.string.valid_describtion));
            valid = false;
        } else {
            description_edt.setError(null);

        }


        return valid;
    }


    public void Create_Ticket_API() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/createNewTicket",
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

                                finish();

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

                                Utility.dialog_error(dialog_bar, Create_Ticket.this, s.toString());

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

                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("reason", reason_str);
                params.put("ticketContent", description_str);

                Log.d("dsfsdf", images_base64.toString());

                if (images_base64.size() != 0) {
                    int i = 0;
                    for (String object : images_base64) {
                        // you first send both data with same param name as friendnr[] ....
                        // now send with params friendnr[0],friendnr[1] ..and so on
                        params.put("image[" + (i++) + "]", object);
                    }
                }


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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private int PICK_IMAGE_Profile = 33;
    Uri filePath;


    CircleImageView profile_image;
    LinearLayout upload_profile_btn;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

//        Bitmap bitmap_data_imgs;
//        //  profile image get data
//        if (requestCode == PICK_IMAGE_Profile && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
//            filePath = intent.getData();
//
//            try {
//                bitmap_data_imgs = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//
//
//                int width = bitmap_data_imgs.getWidth();
//                int height = bitmap_data_imgs.getHeight();
//
//                float bitmapRatio = (float) width / (float) height;
//                if (bitmapRatio > 0) {
//                    width = 500;
//                    height = (int) (width / bitmapRatio);
//                } else {
//                    height = 500;
//                    width = (int) (height * bitmapRatio);
//                }
//                Bitmap image_thumb = Bitmap.createScaledBitmap(bitmap_data_imgs, width, height, true);
//
//
//                profile_image.setBackground(null);
//                profile_image.setImageBitmap(image_thumb);
//
//
//                //  byteArray = getByteArrayFromImageView(profile_image);
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (image_uris != null) {
                    showMedia();
                }
            }
        }

    }
}
