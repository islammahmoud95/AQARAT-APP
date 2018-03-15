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
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.ImageLoader.ImageGesture;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Ticket_Details_Activ extends AppCompatActivity {

    RecyclerView ticket_details_recycler;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    Ticket_details_Adapter tickets_details_adapter;

    RelativeLayout main_tickt_detail_lay;

    LinearLayoutManager manager_lay;

    LinearLayout reply_lay;

    String ticket_ID;

    ImageButton send_btn;

    ImageView upload_img_btn;

    TextInputEditText content_reply_txt;
    String content_str;


    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    ArrayList<Uri> image_uris = new ArrayList<Uri>();
    private ViewGroup mSelectedImagesContainer;
    ArrayList<String> images_base64;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);

        Ads ads=new Ads(this);
        ads.returnnum();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            setTitle(R.string.ticket_detail);
        }

        Intent intent = getIntent();
        if (intent != null) {
            ticket_ID = intent.getStringExtra("TICKET_ID");
        }

        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        main_tickt_detail_lay = findViewById(R.id.main_tickt_detail_lay);


        reply_lay = findViewById(R.id.reply_lay);


        content_reply_txt = findViewById(R.id.content_reply_txt);

        images_base64 = new ArrayList<>();


        manager_lay = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        manager_lay.setStackFromEnd(true);
        ticket_details_recycler = findViewById(R.id.ticket_details_recycle);
        ticket_details_recycler.setLayoutManager(manager_lay);
        ticket_details_recycler.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });


        ticket_details_recycler.setHasFixedSize(true);
        ticket_details_recycler.setNestedScrollingEnabled(false);

        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);
        upload_img_btn = findViewById(R.id.upload_img_btn);
        upload_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TedPermission.with(Ticket_Details_Activ.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Toast.makeText(Ticket_Details_Activ.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                                Config config = new Config();
                                config.setCameraHeight(R.dimen.app_camera_height);
                                config.setToolbarTitleRes(R.string.app_name);
                                config.setSelectionMin(1);
                                config.setSelectionLimit(1);
                                config.setSelectedBottomHeight(R.dimen.bottom_height);
                                config.setFlashOn(true);


                                getImages(config);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Toast.makeText(Ticket_Details_Activ.this, "Permission Denied\n" + deniedPermissions.toString(),
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


        send_btn = findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (validate()) {

                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                        post_Reply();
                    }


                } else {

                    Snackbar snackbar = Snackbar
                            .make(main_tickt_detail_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


    public boolean validate() {
        boolean valid = true;

        try {

            content_str = content_reply_txt.getText().toString().trim();


        } catch (Exception e) {

            Log.e("test", e.getMessage());
        }


        if (content_str.isEmpty()) {
            content_reply_txt.setError(getString(R.string.valid_message));
            valid = false;
        } else {
            content_reply_txt.setError(null);
        }


        return valid;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume", "1");


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Utility.dialog_Show(dialog_bar, getApplicationContext());
            Get_Tickets_Details();


        } else {

            Snackbar snackbar = Snackbar
                    .make(ticket_details_recycler, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();

        }

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
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

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

    private Request.Priority mPriority_Category = Request.Priority.IMMEDIATE;

    public void Get_Tickets_Details() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "users/getOneTicket?" +
                "api_token=" + sessionManager.getUserDetails().get("Server_token") +
                "&" + "ticket_id=" + ticket_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONObject data = obj.getJSONObject("Data");
                                // open or closed ticket
                                String status_ticket = data.getString("isClosed");

                                if (status_ticket.equals("1")) {
                                    reply_lay.setVisibility(View.GONE);
                                }


                                TextView subject_reason_txt = findViewById(R.id.reason_txt);
                                TextView subject_description_txt = findViewById(R.id.description_txt);
                                TextView subject_date_txt = findViewById(R.id.date_txt);
                                HorizontalScrollView scrollView = findViewById(R.id.scrollView_images);
                                LinearLayout linear_images = findViewById(R.id.linear_images);


                                LinearLayout main_lay_item = findViewById(R.id.main_lay_item);
                                main_lay_item.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


                                subject_reason_txt.setText(data.getString("reason"));
                                subject_description_txt.setText(data.getString("desc"));
                                subject_date_txt.setText(data.getString("created_at"));


                                final JSONArray images_array = data.getJSONArray("attachments");
                                ArrayList<String> ticket_images = new ArrayList<String>();


                                for (int j = 0; j < images_array.length(); j++) {
                                    //  powers.add(((String) jsonArray.get(j))+"\n");
                                    ticket_images.add(((String) images_array.get(j)));
                                    Log.d("sfsfs", String.valueOf(ticket_images));
                                }

                                if (images_array.length() == 0) {

                                    scrollView.setVisibility(View.GONE);

                                } else {


                                    // for prevent duplicating
                                    linear_images.removeAllViews();

                                    LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(200,
                                            LinearLayout.LayoutParams.MATCH_PARENT);


                                    for (int i = 0; i < images_array.length(); i++) {

                                        final ImageView imageView = new ImageView(Ticket_Details_Activ.this);


                                        final String img_url = images_array.get(i).toString();

                                        Glide.with(Ticket_Details_Activ.this).load(images_array.get(i)).
                                                diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(imageView);
                                        Log.d("imaaaaaaage", String.valueOf(images_array.get(i)));

                                        imageView.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                Intent go_to_view = new Intent(getApplicationContext(), ImageGesture.class);
                                                go_to_view.putExtra("URL_OF_IMAGE", img_url);
                                                startActivity(go_to_view);

                                            }
                                        });


                                        LLParams.setMargins(10, 0, 10, 10);
                                        imageView.setLayoutParams(LLParams);
                                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        linear_images.addView(imageView);

                                    }


                                }


                                showProducts_Category(String.valueOf(data));

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


                                Utility.dialog_error(dialog_bar, Ticket_Details_Activ.this, s.toString());

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


    private void showProducts_Category(String json) {

        ParseMy_tickets_details parseProducts;
        parseProducts = new ParseMy_tickets_details(json);

        productsArrayList_Category = parseProducts.parseProducts();

//        if (!productsArrayList.isEmpty()) {
        tickets_details_adapter = new Ticket_details_Adapter(this, productsArrayList_Category);
        ticket_details_recycler.setAdapter(tickets_details_adapter);

//            productsArrayList.clear();

        //   } else {
        tickets_details_adapter.notifyDataSetChanged();
        //     }


    }

    ArrayList<Ticket_details_Items> productsArrayList_Category = new ArrayList<>();


    public void post_Reply() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/postReplay",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api_res", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                // not diamiss cause call another api
                                content_reply_txt.setText("");
                                mSelectedImagesContainer.setVisibility(View.GONE);
                                images_base64.clear();
                                mSelectedImagesContainer.removeAllViews();
                                Get_Tickets_Details();

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

                                Utility.dialog_error(dialog_bar, Ticket_Details_Activ.this, s.toString());

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

                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("ticket_id", ticket_ID);
                params.put("ticketContent", content_str);


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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (image_uris != null) {
                    showMedia();
                }
            }
        }

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
