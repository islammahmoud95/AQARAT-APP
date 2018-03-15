package com.sawa.aqarat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.AppController;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Filter_Search_Activity extends AppCompatActivity {


    ScrollView main_scroll_filter;

    ConstraintLayout main_fliter_lay;


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


    String userType = "0";
    String city_id_pos;
    String region_id_pos;
    String category_ID_pos = "1"; // appartment
    String sub_Cat_ID_pos = "0";


    EditText input_price_from, input_price_to, input_area_from, input_area_to, input_room_num_from, input_room_num_to,
            input_floor_num_from, input_floor_num_to;

    TextView num_rooms_head_txt, floor_num_head_txt;


    String price_from_str, price_to_str, area_from_str = "", area_to_str = "", room_num_from_str, room_num_to_str,
            floor_num_from_str, floor_num_to_str;


    Button apply_btn, reset_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Ads ads=new Ads(this);
        ads.returnnum();
        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(this);


        main_scroll_filter = findViewById(R.id.main_scroll_filter);

        main_fliter_lay = findViewById(R.id.main_fliter_lay);


        input_price_from = findViewById(R.id.input_price_from);
        input_price_to = findViewById(R.id.input_price_to);

        input_area_from = findViewById(R.id.input_area_from);
        input_area_to = findViewById(R.id.input_area_to);

        input_room_num_from = findViewById(R.id.input_room_num_from);
        input_room_num_to = findViewById(R.id.input_room_num_to);

        input_floor_num_from = findViewById(R.id.input_floor_num_from);
        input_floor_num_to = findViewById(R.id.input_floor_num_to);


        num_rooms_head_txt = findViewById(R.id.num_rooms_head_txt);
        floor_num_head_txt = findViewById(R.id.floor_num_head_txt);


        //Initializing the ArrayList
        cities_array = new ArrayList<>();
        id_cities = new ArrayList<>();

        regions_array = new ArrayList<>();
        id_regions = new ArrayList<>();


        spinner_cities = findViewById(R.id.spinner_city);
        spinner_regions = findViewById(R.id.spinner_region);


        if (Check_Con.getInstance(this).isOnline()) {

            get_Sub_Cat();
            get_Cities();
            Utility.dialog_Show(dialog_bar, this);


            spinner_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    Log.d("asfsaf", id_cities.get(position));

                    city_id_pos = id_cities.get(position);

                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    get_Regions(id_cities.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            spinner_regions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


                    region_id_pos = id_regions.get(position);

                    Log.d("asfsaf", region_id_pos);
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


            Snackbar snackbar = Snackbar.make(main_fliter_lay, getString(R.string.no_connect), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.connect_snackbar), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();
        }


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


                    input_room_num_from.setVisibility(View.VISIBLE);
                    room_num_from_str = "";
                    input_room_num_to.setVisibility(View.VISIBLE);
                    room_num_to_str = "";
                    input_floor_num_from.setVisibility(View.VISIBLE);
                    floor_num_from_str = "";
                    input_floor_num_to.setVisibility(View.VISIBLE);
                    floor_num_to_str = "";

                    num_rooms_head_txt.setVisibility(View.VISIBLE);
                    floor_num_head_txt.setVisibility(View.VISIBLE);

                } else if (index == 1) {

                    category_ID_pos = "0";

                    sub_cat_Array.clear();
                    id_sub_cats.clear();
                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    get_Sub_Cat();


                    input_room_num_from.setVisibility(View.GONE);
                    room_num_from_str = "";
                    input_room_num_to.setVisibility(View.GONE);
                    room_num_to_str = "";
                    input_floor_num_from.setVisibility(View.GONE);
                    floor_num_from_str = "";
                    input_floor_num_to.setVisibility(View.GONE);
                    floor_num_to_str = "";

                    num_rooms_head_txt.setVisibility(View.GONE);
                    floor_num_head_txt.setVisibility(View.GONE);

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


        apply_btn = findViewById(R.id.apply_btn);
        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


                    if (validate()) {

                       ////here intent
                        Intent go_to_result = new Intent(getApplicationContext() , Filter_Result_Activity.class);
                        go_to_result.putExtra("CITY_ID" , city_id_pos);
                        go_to_result.putExtra("REGION_ID" , region_id_pos);
                        go_to_result.putExtra("CATEGORY_ID" , category_ID_pos);
                        go_to_result.putExtra("SUB_CAT_ID" , sub_Cat_ID_pos);
                        go_to_result.putExtra("PRICE_FROM" , price_from_str);
                        go_to_result.putExtra("PRICE_TO" , price_to_str);
                        go_to_result.putExtra("ROOMS_FROM" , room_num_from_str);
                        go_to_result.putExtra("ROOMS_TO" , room_num_to_str);
                        go_to_result.putExtra("AREA_FROM" , area_from_str);
                        go_to_result.putExtra("AREA_TO" , area_to_str);
                        go_to_result.putExtra("FLOOR_FROM" , floor_num_from_str);
                        go_to_result.putExtra("FLOOR_TO" , floor_num_to_str);
                        startActivity(go_to_result);

                    }


                } else {
                    Snackbar snackbar = Snackbar
                            .make(main_fliter_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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

        reset_btn = findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                spinner_cities.setSelection(0);
                spinner_regions.setSelection(0);
                spinner_sub_cats.setSelection(0);

                scrollToPosition((View) input_price_from.getParent());
                category_group.check(R.id.appartment_radio_btn);


                input_price_from.setText("");
                input_price_to.setText("");
                input_area_from.setText("");
                input_area_to.setText("");
                input_room_num_from.setText("");
                input_room_num_to.setText("");
                input_floor_num_from.setText("");
                input_floor_num_to.setText("");



            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.setupUI(main_fliter_lay, this);
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
        String tag_json_obj = "json_obj_city";
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
                            id_regions.add("0");
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                //   String name = jsonobject.getString("name");
                                String id = jsonobject.getString("id");
                                id_regions.add(id);
                                Log.d("safsafas", id_regions.toString());
                            }


                            parse_getRegions(jsonarray);


                            dialog_bar.dismiss();
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
        regions_array.add(getString(R.string.all_regions_str));

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


                                Utility.dialog_error(dialog_bar, Filter_Search_Activity.this, s.toString());

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


    public boolean validate() {
        boolean valid = true;

     //   params = new HashMap<>();
     //   params.clear();

        try {


            price_from_str = input_price_from.getText().toString().trim();
            price_to_str = input_price_to.getText().toString().trim();

            room_num_from_str = input_room_num_from.getText().toString().trim();
            room_num_to_str = input_room_num_to.getText().toString().trim();

            floor_num_from_str = input_floor_num_from.getText().toString().trim();
            floor_num_to_str = input_floor_num_to.getText().toString().trim();

            area_from_str = input_area_from.getText().toString().trim();
            area_to_str = input_area_to.getText().toString().trim();

        } catch (Exception e) {

            Log.e("test", e.getMessage());
        }


        // if price from smaller than to
        if (Utility.isNotNull(price_from_str) && Utility.isNotNull(price_to_str)
                && Integer.parseInt(price_from_str) <= Integer.parseInt(price_to_str)) {

            input_price_from.setError(null);
            input_price_to.setError(null);
        //    params.put("price_from", price_from_str);
        //    params.put("price_to", price_to_str);

        } else if (Utility.isNotNull(price_from_str) && !Utility.isNotNull(price_to_str)) {

            input_price_to.setError(getString(R.string.valid_user));
            scrollToPosition((View) input_price_to.getParent());
            valid = false;


        } else if (!Utility.isNotNull(price_from_str) && Utility.isNotNull(price_to_str)) {

            input_price_from.setError(getString(R.string.valid_user));
            scrollToPosition((View) input_price_from.getParent());
            valid = false;

        } else if (Utility.isNotNull(price_from_str) && Utility.isNotNull(price_to_str)
                && Integer.parseInt(price_from_str) >= Integer.parseInt(price_to_str)) {


            input_price_from.setError(getString(R.string.valid_user));
            input_price_to.setError(null);
            scrollToPosition((View) input_price_from.getParent());
            valid = false;
        }


        // if area from smaller than to
        if (Utility.isNotNull(area_from_str) && Utility.isNotNull(area_to_str)
                && Integer.parseInt(area_from_str) <= Integer.parseInt(area_to_str)) {

            input_area_from.setError(null);
            input_area_to.setError(null);
         //   params.put("area_from", area_from_str);
          //  params.put("area_to", area_to_str);

        } else if (Utility.isNotNull(area_from_str) && !Utility.isNotNull(area_to_str)) {

            input_area_to.setError(getString(R.string.valid_user));
            scrollToPosition((View) input_area_to.getParent());
            valid = false;

        } else if (!Utility.isNotNull(area_from_str) && Utility.isNotNull(area_to_str)) {

            input_area_from.setError(getString(R.string.valid_user));
            scrollToPosition((View) input_area_from.getParent());
            valid = false;

        } else if (Utility.isNotNull(area_from_str) && Utility.isNotNull(area_to_str)
                && Integer.parseInt(area_from_str) >= Integer.parseInt(area_to_str)) {

            input_area_from.setError(getString(R.string.valid_user));
            input_area_to.setError(null);
            scrollToPosition((View) input_area_from.getParent());
            valid = false;

        }


        // this is apartment
        if (category_ID_pos.equals("1")) {


            // if room num from smaller than to
            if (Utility.isNotNull(room_num_from_str) && Utility.isNotNull(room_num_to_str)
                    && Integer.parseInt(room_num_from_str) <= Integer.parseInt(room_num_to_str)) {

                input_room_num_from.setError(null);
                input_room_num_to.setError(null);
              //  params.put("rooms_from", room_num_from_str);
              //  params.put("rooms_to", room_num_to_str);

            } else if (Utility.isNotNull(room_num_from_str) && !Utility.isNotNull(room_num_to_str)) {

                input_room_num_to.setError(getString(R.string.valid_user));
                scrollToPosition((View) input_room_num_to.getParent());
                valid = false;

                input_room_num_to.setError(getString(R.string.valid_user));

            } else if (!Utility.isNotNull(room_num_from_str) && Utility.isNotNull(room_num_to_str)) {

                input_room_num_from.setError(getString(R.string.valid_user));
                scrollToPosition((View) input_room_num_from.getParent());
                valid = false;

                input_room_num_from.setError(getString(R.string.valid_user));

            } else if (Utility.isNotNull(room_num_from_str) && Utility.isNotNull(room_num_to_str)
                    && Integer.parseInt(room_num_from_str) >= Integer.parseInt(room_num_to_str)) {


                input_room_num_from.setError(getString(R.string.valid_user));
                input_room_num_to.setError(null);
                scrollToPosition((View) input_room_num_from.getParent());
                valid = false;
            }


            // if room num from smaller than to
            if (Utility.isNotNull(floor_num_from_str) && Utility.isNotNull(floor_num_to_str)
                    && Integer.parseInt(floor_num_from_str) <= Integer.parseInt(floor_num_to_str)) {


                input_floor_num_from.setError(null);
                input_floor_num_to.setError(null);
              //  params.put("floor_from", floor_num_from_str);
              //  params.put("floor_to", floor_num_to_str);

            } else if (Utility.isNotNull(floor_num_from_str) && !Utility.isNotNull(floor_num_to_str)) {

                input_floor_num_to.setError(getString(R.string.valid_user));
                scrollToPosition((View) input_floor_num_to.getParent());
                valid = false;

            } else if (!Utility.isNotNull(floor_num_from_str) && Utility.isNotNull(floor_num_to_str)) {

                input_floor_num_from.setError(getString(R.string.valid_user));
                scrollToPosition((View) input_floor_num_from.getParent());
                valid = false;

            } else if (Utility.isNotNull(floor_num_from_str) && Utility.isNotNull(floor_num_to_str)
                    && Integer.parseInt(floor_num_from_str) >= Integer.parseInt(floor_num_to_str)) {

                input_floor_num_from.setError(getString(R.string.valid_user));
                input_floor_num_to.setError(null);
                scrollToPosition((View) input_floor_num_from.getParent());
                valid = false;
            }


            // this is lands
        } else {


            input_room_num_from.setText("");
            input_room_num_to.setText("");
            room_num_to_str = "";
            room_num_from_str = "";


            input_floor_num_from.setText("");
            input_floor_num_to.setText("");
            floor_num_to_str = "";
            floor_num_from_str = "";


        }


        return valid;
    }

    private void scrollToPosition(final View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                main_scroll_filter.smoothScrollTo(0, view.getTop());
            }
        });
    }




   // Map<String, String> params;


}
