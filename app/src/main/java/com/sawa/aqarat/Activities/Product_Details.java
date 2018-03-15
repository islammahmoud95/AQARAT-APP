package com.sawa.aqarat.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
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
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.Adapter.Comments_all_Adapter;
import com.sawa.aqarat.Models.Comments_all_Items;
import com.sawa.aqarat.Parser.ParseMy_All_Comments;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.ImageLoader.ImageGesture;
import com.sawa.aqarat.utilities.MapActivity;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Product_Details extends AppCompatActivity {


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    String product_id;


    ConstraintLayout poduct_details_mainLay;


    RecyclerView comments_recycler;
    LinearLayoutManager manager_lay;
    Comments_all_Adapter comments_adapter;


    LinearLayout reply_lay;
    TextInputEditText content_reply_txt;
    String content_str;
    Button comment_btn;


    TextView owner_name_txt, owner_number_txt, estate_type_txt, area_txt, rent_price_txt, city_region_txt,
            full_address_txt, num_rooms_txt, num_bathrooms, floor_number_txt, num_of_floors_txt, describ_txt,
    finishing_type_txt ,ad_type_txt;

    ImageButton location_btn;

    LinearLayout wish_list_btn, contact_us_btn;


    LinearLayout num_rooms_lay , num_bathrooms_lay , floor_number_lay , num_of_floors_lay , finishing_type_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //  setTitle(R.string.product_details);
        }

        Intent intent = getIntent();
        if (intent != null) {
            product_id = intent.getStringExtra("PRODUCT_ID");

        }
        Ads ads=new Ads(this);
        ads.returnnum();

        file_maps = new ArrayList<>();
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        poduct_details_mainLay = findViewById(R.id.poduct_details_mainLay);

        owner_name_txt = findViewById(R.id.owner_name_txt);
        owner_number_txt = findViewById(R.id.owner_number_txt);
        estate_type_txt = findViewById(R.id.estate_type_txt);
        area_txt = findViewById(R.id.area_txt);
        rent_price_txt = findViewById(R.id.rent_price_txt);
        city_region_txt = findViewById(R.id.city_region_txt);
        full_address_txt = findViewById(R.id.full_address_txt);
        num_rooms_txt = findViewById(R.id.num_rooms_txt);
        num_bathrooms = findViewById(R.id.num_bathrooms);
        floor_number_txt = findViewById(R.id.floor_number_txt);
        num_of_floors_txt = findViewById(R.id.num_of_floors_txt);
        describ_txt = findViewById(R.id.describ_txt);
        finishing_type_txt = findViewById(R.id.finishing_type_txt);

        location_btn = findViewById(R.id.location_btn);

        ad_type_txt = findViewById(R.id.ad_type_txt);


        num_rooms_lay = findViewById(R.id.num_rooms_lay);
        num_bathrooms_lay = findViewById(R.id.num_bathrooms_lay);
        num_of_floors_lay = findViewById(R.id.num_of_floors_lay);
        floor_number_lay = findViewById(R.id.floor_number_lay);
        finishing_type_lay = findViewById(R.id.finishing_type_lay);



        manager_lay = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        comments_recycler = findViewById(R.id.comments_recycle);
        comments_recycler.setLayoutManager(manager_lay);
        comments_recycler.setAdapter(new RecyclerView.Adapter() {
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

        comments_recycler.setHasFixedSize(true);
        comments_recycler.setNestedScrollingEnabled(false);



        reply_lay = (LinearLayout) findViewById(R.id.reply_lay);
        content_reply_txt = (TextInputEditText) findViewById(R.id.content_reply_txt);


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Get_My_Details();
            Utility.dialog_Show(dialog_bar , getApplicationContext());


        } else {

            Snackbar snackbar = Snackbar
                    .make(poduct_details_mainLay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();

        }



        contact_us_btn = findViewById(R.id.contact_us_btn);
        contact_us_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sessionManager.isLoggedIn()) {

                    if (owner_number_txt != null) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + owner_number_txt.getText().toString()));
                        startActivity(intent);

                    } else {

                        Snackbar snackbar = Snackbar.make(poduct_details_mainLay, R.string.no_num_contact, Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }


                } else {

                    Intent go_to_login = new Intent(Product_Details.this, Login_Activity.class);
                    startActivity(go_to_login);
                }

            }
        });



        wish_list_btn = findViewById(R.id.wish_list_btn);
        wish_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sessionManager.isLoggedIn()) {

                    if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                        Utility.dialog_Show(dialog_bar , getApplicationContext());
                        Add_to_WishList_API();

                    } else {
                        Snackbar snackbar = Snackbar
                                .make(poduct_details_mainLay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });

                        snackbar.show();
                    }

                } else {

                    Intent go_to_login = new Intent(Product_Details.this, Login_Activity.class);
                    startActivity(go_to_login);
                }

            }
        });


        content_reply_txt = findViewById(R.id.content_reply_txt);
        comment_btn = (Button) findViewById(R.id.comments_btn);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sessionManager.isLoggedIn()) {
                    if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                        if (validate()) {

                            Utility.dialog_Show(dialog_bar , getApplicationContext());
                            Send_Comments();
                        }

                    } else {

                        Snackbar snackbar = Snackbar
                                .make(poduct_details_mainLay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });

                        snackbar.show();

                    }

                } else {

                    Intent go_to_login = new Intent(Product_Details.this, Login_Activity.class);
                    startActivity(go_to_login);
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
            content_reply_txt.setError("Please Input Text");
            valid = false;
        } else {
            content_reply_txt.setError(null);
        }


        return valid;
    }


    private SliderLayout mDemoSlider;
    ArrayList<String> file_maps;

    public String url_img;

    private Request.Priority mPriority_Category = Request.Priority.IMMEDIATE;

    public void Get_My_Details() {
        String url;
        if (sessionManager.isLoggedIn()){
            url = Utility.URL + "posts/details?" + "post_id=" + product_id + "&" + "lang=" + sessionManager.getLang()
                    + "&" + "api_token=" + sessionManager.getUserDetails().get("Server_token");
        }else {
            url = Utility.URL + "posts/details?" + "post_id=" + product_id + "&" + "lang=" + sessionManager.getLang();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {

                            JSONObject main_obj = new JSONObject(response);
                            Boolean status = main_obj.getBoolean("Status");
                            if (status) {

                                JSONObject data_obj = main_obj.getJSONObject("Data");


                                String id = data_obj.getString("id");


                                owner_name_txt.setText(data_obj.getString("username"));

                                owner_number_txt.setText(data_obj.getString("phone_num"));
                                owner_number_txt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + owner_number_txt.getText().toString()));
                                        startActivity(intent);
                                    }
                                });

                                estate_type_txt.setText(data_obj.getString("category"));


                                String city_str = data_obj.getString("city");
                                String region_str = data_obj.getString("zone");
                                city_region_txt.setText(city_str + " , " + region_str);



                                final String isVip_str = data_obj.getString("isPostVip");

                                if (isVip_str.equals("0")){

                                    ad_type_txt.setVisibility(View.GONE);

                                }else {

                                    if (sessionManager.getLang().equals("ar")){

                                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ad_type_txt.getLayoutParams();
                                        params = new ConstraintLayout.LayoutParams(params); // copy the original ones
                                        params.horizontalBias = 1.0f; // here is one modification for example. modify anything else you want :)
                                        ad_type_txt.setLayoutParams(params); // request the view to use the new modified params

                                    }
                                    ad_type_txt.setText(R.string.special_ad_str);
                                }


                                //=========================
                                // finishing type
                                // if comments empty return true
                                // ad type if lands or real estat 0 or 1

                                /// 0 lands and 1 real estate
                                String ad_type_str = data_obj.getString("estateType");
                                if (ad_type_str.equals("0")){

                                    num_rooms_lay.setVisibility(View.GONE);
                                    num_bathrooms_lay.setVisibility(View.GONE);
                                    num_of_floors_lay.setVisibility(View.GONE);
                                    floor_number_lay.setVisibility(View.GONE);
                                    finishing_type_lay.setVisibility(View.GONE);

                                }else {

                                    num_rooms_txt.setText(data_obj.getString("number_rooms"));
                                    num_bathrooms.setText(data_obj.getString("number_bathrooms"));
                                    floor_number_txt.setText(data_obj.getString("number_of_floor"));
                                    num_of_floors_txt.setText(data_obj.getString("number_all_floor"));
                                    finishing_type_txt.setText(data_obj.getString("finishingType"));
                                }



                                final String lat = data_obj.getString("lat");
                                final String lng = data_obj.getString("lng");
                                if (lat.equals("") || lng.equals("")) {
                                    location_btn.setVisibility(View.GONE);
                                } else {

                                    location_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            //Initialize Google Play Services
                                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                                                        PackageManager.PERMISSION_GRANTED) {
                                                    //Location Permission already granted


                                                    Intent intent = new Intent(Product_Details.this,
                                                            MapActivity.class);
                                                    intent.putExtra("saved_lat", lat);
                                                    intent.putExtra("saved_lng", lng);
                                                    startActivity(intent);

                                                } else {
                                                    //Request Location Permission
                                                    checkLocationPermission();
                                                }
                                            } else {

                                                Intent intent = new Intent(Product_Details.this, MapActivity.class);
                                                startActivity(intent);

                                            }
                                        }
                                    });

                                }

                                area_txt.setText(data_obj.getString("distance"));
                                rent_price_txt.setText(data_obj.getString("price"));
                                full_address_txt.setText(data_obj.getString("address"));
                                describ_txt.setText(data_obj.getString("desc"));


                                JSONArray productsDetails = data_obj.getJSONArray("images");
                                for (int i = 0; i < productsDetails.length(); i++) {
                                    url_img = productsDetails.getString(i);
                                    Log.e("URL", url_img);
                                    file_maps.add(url_img);
                                }
                                for (final String name : file_maps) {
                                    TextSliderView textSliderView = new TextSliderView(Product_Details.this);
                                    textSliderView
                                            .description(data_obj.getString("price"))
                                            .image(name)
                                            .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                                @Override
                                                public void onSliderClick(BaseSliderView slider) {

                                                    //  if (isStoragePermissionGranted())
                                                    //  {
                                                    Intent i = new Intent(Product_Details.this, ImageGesture.class);
                                                    i.putExtra("URL_OF_IMAGES", file_maps.toString().replace("[", "").replace("]", ""));
                                                    Log.d("URL_OF_IMAGE", file_maps.toString().replace("[", "").replace("]", ""));
                                                    startActivity(i);
                                                    //   }
                                                }
                                            });

                                    //add your extra information
                                    textSliderView.bundle(new Bundle());
                                    textSliderView.getBundle().putString("extra", name);

                                    mDemoSlider.addSlider(textSliderView);
                                }
                                //RotateUp

                                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                if (file_maps.size() <= 1) {

                                    mDemoSlider.stopAutoCycle();
                                }

                                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                                mDemoSlider.setDuration(6000);
                                //  mDemoSlider.addOnPageChangeListener(Product_details_Activ.this);


                                Get_Comments();

                            } else {

                                StringBuilder s = new StringBuilder();
                                String street;
                                JSONArray st = main_obj.getJSONArray("Errors");
                                for (int i = 0; i < st.length(); i++) {
                                    street = st.getString(i);
                                    s.append(street);
                                    s.append("\n");
                                    Log.i("teeest", s.toString());
                                    // loop and add it to array or arraylist
                                }


                                Utility.dialog_error(dialog_bar, Product_Details.this, s.toString());

                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());
                            dialog_bar.dismiss();
                            e.printStackTrace();

                        }


                        View myView = findViewById(R.id.circle_shape_lay);
                        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
                        Display display = getWindowManager().getDefaultDisplay();
                        TextView title_act_name = findViewById(R.id.title_act_name);
                        title_act_name.setText(R.string.details_str);
                        Shape_Animation(getApplicationContext(), display, myView, myView_bottom , title_act_name);
                        Button back_btn = findViewById(R.id.back_btn);
                        back_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });


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


    public void Get_Comments() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "comments/get?" +
                "post_id=" + product_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {


                            JSONObject main_obj = new JSONObject(response);
                            Boolean status = main_obj.getBoolean("Status");
                            if (status) {

                                showProduct_Comments(String.valueOf(main_obj));

                                dialog_bar.dismiss();

                            } else {

                                StringBuilder s = new StringBuilder();
                                String street;
                                JSONArray st = main_obj.getJSONArray("Errors");
                                for (int i = 0; i < st.length(); i++) {
                                    street = st.getString(i);
                                    s.append(street);
                                    s.append("\n");
                                    Log.i("teeest", s.toString());
                                    // loop and add it to array or arraylist
                                }


                                Utility.dialog_error(dialog_bar, Product_Details.this, s.toString());

                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());
                            dialog_bar.dismiss();
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

    private void showProduct_Comments(String json) {

        ParseMy_All_Comments parseProducts;
        parseProducts = new ParseMy_All_Comments(json);

        productsArrayList_Category = parseProducts.parseProducts();

        comments_adapter = new Comments_all_Adapter(this, productsArrayList_Category);
        comments_recycler.setAdapter(comments_adapter);

        comments_adapter.notifyDataSetChanged();


    }

    ArrayList<Comments_all_Items> productsArrayList_Category = new ArrayList<>();


    public void Send_Comments() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/comments/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {


                            JSONObject main_obj = new JSONObject(response);
                            Boolean status = main_obj.getBoolean("Status");
                            if (status) {

                                content_reply_txt.setText("");
                                content_str ="";
                                Get_Comments();


                            } else {

                                StringBuilder s = new StringBuilder();
                                String street;
                                JSONArray st = main_obj.getJSONArray("Errors");
                                for (int i = 0; i < st.length(); i++) {
                                    street = st.getString(i);
                                    s.append(street);
                                    s.append("\n");
                                    Log.i("teeest", s.toString());
                                    // loop and add it to array or arraylist
                                }


                                Utility.dialog_error(dialog_bar, Product_Details.this, s.toString());

                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());
                            dialog_bar.dismiss();
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

                params.put("post_id", product_id);
                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("comment", content_str);


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


    private void Add_to_WishList_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/addToWish",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                dialog_bar.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                dialog_bar.setTitleText(getString(R.string.sucsess_dialog_str));
                                dialog_bar.setContentText("This Post Successfully Added to your Wishlist");
                                dialog_bar.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismiss();

                                    }
                                });


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


                                Utility.dialog_error(dialog_bar , getApplicationContext(), s.toString());
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

                params.put("api_token" ,  sessionManager.getUserDetails().get("Server_token"));
                params.put("post_id" ,  product_id);

                Log.d("ProductsParams", params.toString());

                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this).setTitle(R.string.loc_permission_need)
                        .setMessage(R.string.app_need_location_permission)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Product_Details.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}