package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
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
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.Adapter.All_Products_Adapter;
import com.sawa.aqarat.Models.All_Products_Items;
import com.sawa.aqarat.Parser.ParseAll_Products;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.AppController;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.EndlessScrollListener;
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

public class Filter_Result_Activity extends AppCompatActivity {

    RecyclerView all_posts_recycler;
    LinearLayoutManager manager_lay;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    All_Products_Adapter all_posts_adapter;

    LinearLayout main_all_posts_lay;

    TextView no_items;


    Integer city_id_pos, region_id_pos, category_ID_pos, sub_Cat_ID_pos, price_from_str, price_to_str,
            room_num_from_str, room_num_to_str, area_from_str, area_to_str, floor_num_from_str, floor_num_to_str;


    int current_page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_result);
        Ads ads=new Ads(this);
        ads.returnnum();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            // get keys
            //   data_result = intent.getStringExtra("DATA_RESULT");
            if (!intent.getStringExtra("CITY_ID").equals("")){
                city_id_pos = Integer.parseInt(intent.getStringExtra("CITY_ID"));
            }else {
                city_id_pos = 0;
            }

            if (!intent.getStringExtra("REGION_ID").equals("")){
                region_id_pos = Integer.parseInt(intent.getStringExtra("REGION_ID"));
            }else {
                region_id_pos = 0;
            }

            if (!intent.getStringExtra("CATEGORY_ID").equals("")){
                category_ID_pos = Integer.parseInt(intent.getStringExtra("CATEGORY_ID"));
            }else {
                category_ID_pos = 0;
            }

            if (!intent.getStringExtra("SUB_CAT_ID").equals("")){
                sub_Cat_ID_pos = Integer.parseInt(intent.getStringExtra("SUB_CAT_ID"));
            }else {
                sub_Cat_ID_pos =0;
            }
            if (!intent.getStringExtra("PRICE_FROM").equals("")){
                price_from_str = Integer.parseInt(intent.getStringExtra("PRICE_FROM"));
            }else {
                price_from_str = 0;
            }

            if (!intent.getStringExtra("PRICE_TO").equals("")){
                price_to_str = Integer.parseInt(intent.getStringExtra("PRICE_TO"));
            }else {
                price_to_str=0;
            }
            if (!intent.getStringExtra("ROOMS_FROM").equals("")){
                room_num_from_str = Integer.parseInt(intent.getStringExtra("ROOMS_FROM"));
            }else {
                room_num_from_str=0;
            }
            if (!intent.getStringExtra("ROOMS_TO").equals("")){
                room_num_to_str = Integer.parseInt(intent.getStringExtra("ROOMS_TO"));
            }else {
                room_num_to_str=0;
            }
            if (!intent.getStringExtra("AREA_FROM").equals("")){
                area_from_str = Integer.parseInt(intent.getStringExtra("AREA_FROM"));
            }else {
                area_from_str =0;
            }
            if (!intent.getStringExtra("AREA_TO").equals("")){
                area_to_str = Integer.parseInt(intent.getStringExtra("AREA_TO"));
            }else {
                area_to_str=0;
            }
            if (!intent.getStringExtra("FLOOR_FROM").equals("")){
                floor_num_from_str = Integer.parseInt(intent.getStringExtra("FLOOR_FROM"));
            }else {
                floor_num_from_str = 0;
            }
            if (!intent.getStringExtra("FLOOR_TO").equals("")){
                floor_num_to_str = Integer.parseInt(intent.getStringExtra("FLOOR_TO"));
            }else {
                floor_num_to_str = 0;
            }


        }


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        main_all_posts_lay = findViewById(R.id.main_all_posts_lay);

        no_items = findViewById(R.id.no_items);

        manager_lay = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);



        all_posts_recycler = findViewById(R.id.get_my_posts_recycle);
        all_posts_recycler.setLayoutManager(manager_lay);
        all_posts_recycler.setAdapter(new RecyclerView.Adapter() {
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



        all_posts_recycler.setHasFixedSize(true);
        all_posts_recycler.setNestedScrollingEnabled(false);


        EndlessScrollListener scrollListener = new EndlessScrollListener(manager_lay) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Utility.dialog_Show(dialog_bar, getApplicationContext());
                Get_More_Endless(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        all_posts_recycler.addOnScrollListener(scrollListener);

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume", "1");


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Search_API(current_page);
            Utility.dialog_Show(dialog_bar, getApplicationContext());

        } else {

            Snackbar snackbar = Snackbar
                    .make(main_all_posts_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();

        }
    }



    private Request.Priority mPriority_Category = Request.Priority.IMMEDIATE;

    private void Search_API(int Page) {
        String tag_json_obj = "search_api";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "posts/search?"
                + "city_id=" + city_id_pos
                + "&" + "estateType=" + category_ID_pos
                + "&" + "Post_category_id=" + sub_Cat_ID_pos
                + "&" + "zone_id=" + region_id_pos
                + "&" + "price_from=" + price_from_str
                + "&" + "price_to=" + price_to_str
                + "&" + "area_from=" + area_from_str
                + "&" + "area_to=" + area_to_str
                + "&" + "rooms_from=" + room_num_from_str
                + "&" + "rooms_to=" + room_num_to_str
                + "&" + "floor_from=" + floor_num_from_str
                + "&" + "floor_to=" + floor_num_to_str
                + "&" + "page=" + Page,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("tessst", response);

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {


                                showProducts_Category(response);

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


                                Utility.dialog_error(dialog_bar, Filter_Result_Activity.this, s.toString());
                                dialog_bar.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        finish();
                                    }
                                });

                            }

                            //Calling method getStudents to get the students from the JSON Array

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
                        Log.e("error", error.getMessage() + "");

                    }

                })

        {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                Log.e("parm", params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void showProducts_Category(String json) {

        ParseAll_Products parseProducts;
        parseProducts = new ParseAll_Products(json);

        productsArrayList_Category = parseProducts.parseProducts();

        all_posts_adapter = new All_Products_Adapter(this, productsArrayList_Category);
        all_posts_recycler.setAdapter(all_posts_adapter);

        all_posts_adapter.notifyDataSetChanged();

        //show empty view if adapter empty
        if (productsArrayList_Category.isEmpty()) {
            all_posts_recycler.setVisibility(View.GONE);
            no_items.setVisibility(View.VISIBLE);
        } else {
            all_posts_recycler.setVisibility(View.VISIBLE);
            no_items.setVisibility(View.GONE);
        }


    }

    ArrayList<All_Products_Items> productsArrayList_Category = new ArrayList<>();









    public void Get_More_Endless(int Page) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "posts/search?"
                + "city_id=" + city_id_pos + "&" + "estateType=" + category_ID_pos + "&"
                + "Post_category_id=" + sub_Cat_ID_pos + "&" + "zone_id=" + region_id_pos + "&"
                + "price_from=" + price_from_str + "&" + "price_to=" + price_to_str + "&"
                + "area_from=" + area_from_str + "&" + "area_to=" + area_to_str + "&"
                + "rooms_from=" + room_num_from_str + "&" + "rooms_to=" + room_num_to_str + "&"
                + "floor_from=" + floor_num_from_str + "&" + "floor_to=" + floor_num_to_str + "&"
                + "page=" + Page,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                show_more_parse(response);

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


                                Utility.dialog_error(dialog_bar, Filter_Result_Activity.this, s.toString());

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


    private void show_more_parse(String json) {

        try {

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("Data");

            for (int i = 0; i < jsonArray.length(); i++) {
                All_Products_Items products = new All_Products_Items();

                JSONObject jo = jsonArray.getJSONObject(i);
                products.setPost_Id(jo.getString("id"));
                products.setPost_sender(jo.getString("username"));
                products.setPost_img(jo.getString("image"));
                products.setPost_price(jo.getString("price"));
                products.setProducts_desc(jo.getString("desc"));

                productsArrayList_Category.add(products);

                all_posts_adapter.notifyDataSetChanged();

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("productsArrayList", e.getMessage() + "");

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
