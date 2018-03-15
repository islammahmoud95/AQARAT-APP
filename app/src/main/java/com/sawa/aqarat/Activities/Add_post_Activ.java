package com.sawa.aqarat.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.AppController;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.MapActivity;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;
import com.sawa.aqarat.utilities.WorkaroundMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Add_post_Activ extends AppCompatActivity implements OnMapReadyCallback {


    ScrollView main_scroll_add;

    LinearLayout main_add_post_lay;

    RadioGroup user_type_group;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    private ArrayList<String> cities_array;
    private ArrayList<String> id_cities;

    private ArrayList<String> regions_array;
    private ArrayList<String> id_regions;

    private Spinner spinner_cities;
    private Spinner spinner_regions;


    RadioGroup category_group;


    private ArrayList<String> sub_cat_Array;
    private ArrayList<String> id_sub_cats;
    private Spinner spinner_sub_cats;


    private ArrayList<String> finishing_type_Array;
    private ArrayList<String> id_finishing_Type;
    private Spinner spinner_Finishing_type;


    String userType = "0";
    String city_id_pos;
    String region_id_pos;
    String category_ID_pos = "1"; // appartment
    String sub_Cat_ID_pos = "0";
    String finishing_Type_ID_pos = "0";


    EditText input_address;
    ImageButton map_loc_btn;
    private String mLatitude = null;
    private String mLongitude = null;
    String address_str;



    EditText input_description , input_area , input_room_num , input_floors_num , input_bathroom_num , input_apart_floor_num;
    EditText input_cost_price;


    String  description_str , area_str , room_num_str , floors_num_str , bathroom_num_str , apart_floor_num_str;
    String cost_price_str;


    AppCompatCheckBox negotiable_checkbox;
    String is_negotiable;


    Button confirm_btn, cancel_btn;


    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private static final String TAG = "TedPicker";
    ArrayList<Uri> image_uris = new ArrayList<Uri>();
    private ViewGroup mSelectedImagesContainer;

    ArrayList<String> images_base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Ads ads=new Ads(this);
        ads.returnnum();

        main_add_post_lay = findViewById(R.id.main_add_post_lay);
        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(this);


        //Initializing the ArrayList
        cities_array = new ArrayList<>();
        id_cities = new ArrayList<>();

        regions_array = new ArrayList<>();
        id_regions = new ArrayList<>();


        spinner_cities = findViewById(R.id.spinner_city);
        spinner_regions = findViewById(R.id.spinner_region);

        if (Check_Con.getInstance(this).isOnline()) {

            get_Cities();
            Utility.dialog_Show(dialog_bar, this);


            spinner_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    Log.d("asfsaf", id_cities.get(position));

                    city_id_pos = id_cities.get(position);

                    get_Regions(id_cities.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            spinner_regions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    Log.d("asfsaf", city_id_pos);

                    region_id_pos = id_regions.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } else {


            cities_array.add(getString(R.string.no_connect));
            regions_array.add(getString(R.string.no_connect));


            spinner_cities.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_items, cities_array));

            spinner_regions.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_items, regions_array));


            Snackbar snackbar = Snackbar.make(main_add_post_lay, getString(R.string.no_connect), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.connect_snackbar), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();
        }


        user_type_group = (RadioGroup) findViewById(R.id.user_type_group);
        user_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {


                View radioButton = user_type_group.findViewById(checkedId);
                int index = user_type_group.indexOfChild(radioButton);


                // order_additinal_Str for validation and sending to web service
                if (index == 0) // that's mean yes chosen
                {
                    userType = "0";

                } else if (index == 1) {

                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    is_User_Vip();

                    userType = "1";

                }

                Log.d("pay_method", userType);
            }
        });


        category_group = (RadioGroup) findViewById(R.id.category_group);
        category_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {


                View radioButton = category_group.findViewById(checkedId);
                int index = category_group.indexOfChild(radioButton);


                // 0 is apartment and 1 is floor
                if (index == 0) // that's mean yes chosen
                {
                    category_ID_pos = "1";

                    sub_cat_Array.clear();
                    id_sub_cats.clear();
                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    get_Sub_Cat();


                    input_room_num.setVisibility(View.VISIBLE);
                    room_num_str="";
                    input_floors_num.setVisibility(View.VISIBLE);
                    floors_num_str ="";
                    input_bathroom_num.setVisibility(View.VISIBLE);
                    bathroom_num_str = "";
                    input_apart_floor_num.setVisibility(View.VISIBLE);
                    apart_floor_num_str ="";
                    spinner_Finishing_type.setVisibility(View.VISIBLE);
                    finishing_Type_ID_pos ="";

                } else if (index == 1) {

                    category_ID_pos = "0";

                    sub_cat_Array.clear();
                    id_sub_cats.clear();
                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    get_Sub_Cat();


                    input_room_num.setVisibility(View.GONE);
                    room_num_str="";
                    input_floors_num.setVisibility(View.GONE);
                    floors_num_str ="";
                    input_bathroom_num.setVisibility(View.GONE);
                    bathroom_num_str = "";
                    input_apart_floor_num.setVisibility(View.GONE);
                    apart_floor_num_str ="";
                    spinner_Finishing_type.setVisibility(View.GONE);
                    finishing_Type_ID_pos ="";

                }

                Log.d("pay_method", userType);
            }
        });


        sub_cat_Array = new ArrayList<>();
        id_sub_cats = new ArrayList<>();
        spinner_sub_cats = findViewById(R.id.spinner_sub_cat);



        spinner_sub_cats.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_items, sub_cat_Array));

        spinner_sub_cats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                Log.d("asfsaf", id_sub_cats.get(position));

                sub_Cat_ID_pos = id_sub_cats.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        finishing_type_Array = new ArrayList<>();
        id_finishing_Type = new ArrayList<>();
        spinner_Finishing_type = findViewById(R.id.spinner_finishing_Type);


        spinner_Finishing_type.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_items, finishing_type_Array));

        spinner_Finishing_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                Log.d("asfsaf", id_finishing_Type.get(position));

                finishing_Type_ID_pos = id_finishing_Type.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        SupportMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_add_post);
        mapFragment.getMapAsync(this);

        main_scroll_add = (ScrollView) findViewById(R.id.main_scroll_add);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_add_post))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        main_scroll_add.requestDisallowInterceptTouchEvent(true);
                    }
                });






        input_address = findViewById(R.id.input_address);
        map_loc_btn = findViewById(R.id.map_loc_btn);
        map_loc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Location Permission already granted


                        Intent intent = new Intent(Add_post_Activ.this, MapActivity.class);
                        intent.putExtra("saved_lat",mLatitude);
                        intent.putExtra("saved_lng",mLongitude);
                        startActivityForResult(intent, RESULT_GET_LOCATION);

                    } else {
                        //Request Location Permission
                        checkLocationPermission();
                    }
                } else {

                    Intent intent = new Intent(Add_post_Activ.this, MapActivity.class);
                    startActivityForResult(intent, RESULT_GET_LOCATION);

                }
            }
        });




        input_description = findViewById(R.id.input_description);

        input_area = findViewById(R.id.input_area);


        input_room_num = findViewById(R.id.input_room_num);
        input_floors_num = findViewById(R.id.input_floors_num);
        input_bathroom_num = findViewById(R.id.input_bathroom_num);
        input_apart_floor_num = findViewById(R.id.input_apart_floor_num);


        input_cost_price = findViewById(R.id.input_cost_price);


        is_negotiable = "0";
        negotiable_checkbox = findViewById(R.id.negotiable_checkbox);
        negotiable_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    is_negotiable = "1";
                }else {
                    is_negotiable = "0";
                }
            }
        });



        images_base64 = new ArrayList<>();

        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);
        View getImages2 = findViewById(R.id.get_images2);
        getImages2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TedPermission.with(Add_post_Activ.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                             //   Toast.makeText(Add_post_Activ.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(Add_post_Activ.this, "Permission Denied\n" + deniedPermissions.toString(),
//                                        Toast.LENGTH_SHORT).show();
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
                        Create_AD_API();

                    }


                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_add_post_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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
        Utility.setupUI(main_add_post_lay, this);
        ((RadioButton)user_type_group.getChildAt(0)).setChecked(true);
    }



    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        mMap = googleMap;


    }

    public void setlatlng(String lat, String lng) {
        LatLng TutorialsPoint = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        BitmapDescriptor map_client = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_icon);

        mMap.addMarker(new MarkerOptions()
                .snippet("Place here")
                .position(TutorialsPoint)
                .icon(map_client)
                .title("ad Location")
                .flat(true)
                .draggable(true));


        CameraPosition cameraPosition = new CameraPosition.Builder().target(TutorialsPoint) // Sets the center
                // of the map to
                // location user
                .zoom(15) // Sets the zoom
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                                ActivityCompat.requestPermissions(Add_post_Activ.this,
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


    private static final int RESULT_GET_LOCATION = 22;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // map back data
        if (requestCode == RESULT_GET_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.hasExtra(MapActivity.KEY_LATITUDE)) {

                    mLatitude = data.getStringExtra(MapActivity.KEY_LATITUDE);
                    mLongitude = data.getStringExtra(MapActivity.KEY_LONGITUDE);

                    address_str = data.getStringExtra(MapActivity.KEY_Address);

                    input_address.setText(address_str);

                    //  Toast.makeText(Register_user_Activity.this, getResources().getString(R.string.app_name), Toast.LENGTH_LONG).show();

                    Log.d("safdsfsa", mLatitude);
                    Log.d("safdsfsa", mLongitude);
                    Log.d("safdsfsa", input_address.getText().toString());

                    mMap.clear();
                    setlatlng(mLatitude, mLongitude);


                }
            }
        }


        //  gallery get data

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (image_uris != null) {
                    showMedia();
                }
            }
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












    private void get_Finishing_Type() {
        //Creating a string request

        String tag_json_obj = "json_obj_count";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "finishingTypes?"
                + "lang=" + sessionManager.getLang(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("finishingg", response);


                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONArray data_array = obj.getJSONArray("Data");

                                //Parsing the fetched Json String to JSON Object
                                //  JSONArray jsonarray = new JSONArray(response);

                                for (int i = 0; i < data_array.length(); i++) {
                                    JSONObject jsonobject = data_array.getJSONObject(i);
                                    //   String name = jsonobject.getString("name");
                                    String id = jsonobject.getString("id");

                                    id_finishing_Type.add(id);
                                }


                                parse_getFinishing_Type(data_array);

                                dialog_bar.dismiss();
                                //Calling method getStudents to get the students from the JSON Array

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


                                Utility.dialog_error(dialog_bar, Add_post_Activ.this, s.toString());


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog_bar.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                Log.e("parms", params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void parse_getFinishing_Type(JSONArray j) {
        //Traversing through all the items in the json array

        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                finishing_type_Array.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        spinner_Finishing_type.setAdapter(new ArrayAdapter<>(this,
                R.layout.spinner_items, finishing_type_Array));

    }


    private Request.Priority mPriority_sub_cat = Request.Priority.HIGH;

    private void get_Sub_Cat() {
        //Creating a string request

        String tag_json_obj = "json_obj_count";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "categories/getSubCats?"
                + "type=" + category_ID_pos + "&" + "lang=" + sessionManager.getLang(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("tessst", response);

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONArray data_array = obj.getJSONArray("Data");
                                //Parsing the fetched Json String to JSON Object
                                //  JSONArray jsonarray = new JSONArray(response);

                                for (int i = 0; i < data_array.length(); i++) {
                                    JSONObject jsonobject = data_array.getJSONObject(i);
                                    //   String name = jsonobject.getString("name");
                                    String id = jsonobject.getString("id");

                                    id_sub_cats.add(id);
                                }


                                parse_getSub_Cat(data_array);

                                Utility.dialog_Show(dialog_bar, getApplicationContext());
                                get_Finishing_Type();

                                //Calling method getStudents to get the students from the JSON Array

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


                                Utility.dialog_error(dialog_bar, Add_post_Activ.this, s.toString());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog_bar.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                    }
                }) {


            @Override
            public Priority getPriority() {
                return mPriority_sub_cat;
            }


            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("lang", sessionManager.getLang());

                Log.e("parms", params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void parse_getSub_Cat(JSONArray j) {
        //Traversing through all the items in the json array

        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                sub_cat_Array.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //Setting adapter to show the items in the spinner

//        if (country_array.isEmpty())
//        {
//            country_array.add("No Countries");
//        }
        spinner_sub_cats.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_items, sub_cat_Array));

        //  dialog.dismiss();
    }


    private Request.Priority mPriority = Request.Priority.IMMEDIATE;

    private void is_User_Vip() {
        //Creating a string request

        String tag_json_obj = "json_obj_count";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/isUserVip",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("tessst", response);

                            JSONObject main_ob = new JSONObject(response);

                            Boolean status = main_ob.getBoolean("Status");

                            // if true that's ad is vip and false mean normal
                            if (status) {

                                dialog_bar.dismiss();


                            } else {


                                Utility.dialog_error(dialog_bar, getApplicationContext(),
                                        getString(R.string.u_are_not_vip_str));
                                dialog_bar.setTitleText(getString(R.string.special_ad_str));
                                dialog_bar.showCancelButton(true);
                                dialog_bar.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        RadioButton normal_post_radio = findViewById(R.id.normal_post);
                                        normal_post_radio.setChecked(true);
                                        sweetAlertDialog.dismiss();

                                    }
                                });


                                dialog_bar.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();

                                        Intent go_to_prem = new Intent(getApplicationContext() , Premium_Request_Act.class);
                                        startActivity(go_to_prem);

                                    }
                                });


                            }


                            //Calling method getStudents to get the students from the JSON Array

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog_bar.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                    }
                }) {


            @Override
            public Priority getPriority() {
                return mPriority;
            }


            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("lang", sessionManager.getLang());

                Log.e("parms", params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }


    private void get_Cities() {
        //Creating a string request

        String tag_json_obj = "json_obj_count";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "locations/cities",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("tessst", response);

                            JSONObject main_ob = new JSONObject(response);

                            JSONArray jsonarray = main_ob.getJSONArray("Data");


                            //Parsing the fetched Json String to JSON Object
                            //  JSONArray jsonarray = new JSONArray(response);

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                //   String name = jsonobject.getString("name");
                                String id = jsonobject.getString("id");

                                id_cities.add(id);
                            }


                            parse_getCity(jsonarray);

                            //Calling method getStudents to get the students from the JSON Array

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog_bar.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("lang", sessionManager.getLang());

                Log.e("parms", params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void parse_getCity(JSONArray j) {
        //Traversing through all the items in the json array

        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                cities_array.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //Setting adapter to show the items in the spinner

//        if (country_array.isEmpty())
//        {
//            country_array.add("No Countries");
//        }
        spinner_cities.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_items, cities_array));

        //  dialog.dismiss();
    }


    private void get_Regions(final String Country_id) {
        //Creating a string request

        String tag_json_obj = "json_obj_city";

        //  dialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "locations/zones",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        regions_array.clear();
                        id_regions.clear();

                        try {


                            JSONObject main_ob = new JSONObject(response);

                            JSONArray jsonarray = main_ob.getJSONArray("Data");

                            Log.d("tessst", response);
                            //Parsing the fetched Json String to JSON Object
                            //    JSONArray jsonarray = new JSONArray(response);

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                //   String name = jsonobject.getString("name");
                                String id = jsonobject.getString("id");
                                id_regions.add(id);
                                Log.d("safsafas", id_regions.toString());
                            }


                            parse_getRegions(jsonarray);


                            Utility.dialog_Show(dialog_bar, getApplicationContext());
                            get_Sub_Cat();

                            //Calling method getStudents to get the students from the JSON Array

                        } catch (JSONException e) {
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
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("city_id", Country_id);
                params.put("lang", sessionManager.getLang());

                Log.e("parm", params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void parse_getRegions(JSONArray j) {
        //Traversing through all the items in the json array

        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                regions_array.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //  dialog.dismiss();


//        if(city_array.isEmpty())
//        {
//            city_array = new ArrayList<>();
//            city_array.add("No Areas");
//        }
        //Setting adapter to show the items in the spinner
        spinner_regions.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_items, regions_array));
    }














    public boolean validate() {
        boolean valid = true;

        try {

            // ad type
            // city
            // region
            // cat apart land
            // sub cat
            //finishing type
            //lat
            //lng
            //address
            //descrip
            //area
            //no rooms
            //no floors
            //no bath
            // no apart floor
            //cost
            //add info
            //rent or not
            // images


            description_str = input_description.getText().toString().trim();
            area_str = input_area.getText().toString().trim();
            room_num_str = input_room_num.getText().toString().trim();
            floors_num_str = input_floors_num.getText().toString().trim();
            bathroom_num_str = input_bathroom_num.getText().toString().trim();
            apart_floor_num_str = input_apart_floor_num.getText().toString().trim();


            address_str =  input_address.getText().toString().trim();


            cost_price_str = input_cost_price.getText().toString().trim();


        } catch (Exception e) {

            Log.e("test", e.getMessage());
        }



        // this is apartment
        if (category_ID_pos.equals("1")){

            if (description_str.isEmpty() || description_str.length() <= 9) {
                input_description.setError("Description Must be At Least 10 char");
                scrollToPosition((View) input_description.getParent());
                valid = false;
            } else {
                input_description.setError(null);

            }


            if (area_str.isEmpty() || !Utility.isNotNull(area_str)) {
                input_area.setError("Area Must be not null");
                scrollToPosition((View) input_area.getParent());
                valid = false;
            } else {
                input_area.setError(null);

            }

            if (room_num_str.isEmpty() || !Utility.isNotNull(room_num_str)) {
                input_room_num.setError("Rooms must be not null");
                scrollToPosition((View) input_room_num.getParent());
                valid = false;
            } else {
                input_room_num.setError(null);

            }


            if (floors_num_str.isEmpty() || !Utility.isNotNull(floors_num_str)) {
                input_floors_num.setError("floors must be not null");
                scrollToPosition((View) input_floors_num.getParent());
                valid = false;
            } else {
                input_floors_num.setError(null);

            }


            if (bathroom_num_str.isEmpty() || !Utility.isNotNull(bathroom_num_str)) {
                input_bathroom_num.setError("Bathroom must be not null");
                scrollToPosition((View) input_bathroom_num.getParent());
                valid = false;
            } else {
                input_bathroom_num.setError(null);

            }


            if (apart_floor_num_str.isEmpty() || !Utility.isNotNull(apart_floor_num_str)) {
                input_apart_floor_num.setError("Floor number Must be not null");
                scrollToPosition((View) input_apart_floor_num.getParent());
                valid = false;
            } else {
                input_apart_floor_num.setError(null);

            }



            if (address_str.isEmpty() || address_str.length() <= 9) {
                input_address.setError("Address must be at least 10 char");
                scrollToPosition((View) input_address.getParent());
                valid = false;
            } else {
                input_address.setError(null);

            }

            if (!Utility.isNotNull(mLatitude)) {
                mLatitude = "";
            }
            if (!Utility.isNotNull(mLongitude)) {
                mLongitude = "";
            }



            if (cost_price_str.isEmpty() || !Utility.isNotNull(cost_price_str)) {
                input_cost_price.setError("Monthly Rent / Total Price must be not null");
                scrollToPosition((View) input_cost_price.getParent());
                valid = false;
            } else {
                input_cost_price.setError(null);

            }


            if (images_base64.size() < 0 || images_base64.isEmpty()) {
                Snackbar snackbar = Snackbar.make(main_add_post_lay, "Please Put Image At Least", Snackbar.LENGTH_LONG);
                snackbar.show();
                main_scroll_add.fullScroll(ScrollView.FOCUS_DOWN);
                valid = false;
            }

            // this is lands
        }else {


            if (description_str.isEmpty() || description_str.length() <= 9) {
                input_description.setError("Description Must be At Least 10 char");
                scrollToPosition((View) input_description.getParent());
                valid = false;
            } else {
                input_description.setError(null);

            }


            if (area_str.isEmpty() || !Utility.isNotNull(description_str)) {
                input_area.setError("Area Must be not null");
                scrollToPosition((View) input_area.getParent());
                valid = false;
            } else {
                input_area.setError(null);

            }



            if (address_str.isEmpty() || address_str.length() <= 9) {
                input_address.setError("Address must be at least 10 char");
                scrollToPosition((View) input_address.getParent());
                valid = false;
            } else {
                input_address.setError(null);
            }

            if (!Utility.isNotNull(mLatitude)) {
                mLatitude = "";
            }
            if (!Utility.isNotNull(mLongitude)) {
                mLongitude = "";
            }



            if (cost_price_str.isEmpty() || !Utility.isNotNull(cost_price_str)) {
                input_cost_price.setError("Monthly Rent / Total Price must be not null");
                scrollToPosition((View) input_cost_price.getParent());
                valid = false;
            } else {
                input_cost_price.setError(null);

            }


            if (images_base64.size() < 0 || images_base64.isEmpty()) {
                Snackbar snackbar = Snackbar.make(main_add_post_lay, "Please Put Image At Least", Snackbar.LENGTH_LONG);
                snackbar.show();
                main_scroll_add.fullScroll(ScrollView.FOCUS_DOWN);
                valid = false;
            } else {

            }

            // cause this is gone in view and not need in cycle
            finishing_Type_ID_pos = "";


        }




        return valid;
    }

    private void scrollToPosition(final View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                main_scroll_add.smoothScrollTo(0, view.getTop());
            }
        });
    }

    public void Create_AD_API() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/addNewPost",
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

                                Utility.dialog_error(dialog_bar, Add_post_Activ.this, s.toString());

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


                params.put("post_name", "ssfsfsfsa");
                params.put("city_id", city_id_pos);
                params.put("zone_id", region_id_pos);
                params.put("address", address_str);
                params.put("distance", area_str);
                params.put("price", cost_price_str);
                params.put("negotiable", is_negotiable);
                params.put("post_category_id", sub_Cat_ID_pos);
                params.put("desc", description_str);
                params.put("isSpecialAd", userType );
                params.put("estateType", category_ID_pos);




                params.put("lat", mLatitude);
                params.put("lng", mLongitude);
                params.put("number_rooms", room_num_str);
                params.put("number_bathrooms", bathroom_num_str);
                params.put("finishing_type_id", finishing_Type_ID_pos);
                params.put("number_all_floor", floors_num_str);
                params.put("number_of_floor", apart_floor_num_str);


                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));


                Log.d("dsfsdf", images_base64.toString());

                if (images_base64.size() != 0) {
                    int i = 0;
                    for (String object : images_base64) {
                        // you first send both data with same param name as friendnr[] ....
                        // now send with params friendnr[0],friendnr[1] ..and so on
                        params.put("images[" + (i++) + "]", object);
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


}
