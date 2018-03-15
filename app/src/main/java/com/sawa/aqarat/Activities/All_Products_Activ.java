package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
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

public class All_Products_Activ extends AppCompatActivity {

    RecyclerView all_posts_recycler;
    LinearLayoutManager manager_lay;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    All_Products_Adapter all_posts_adapter;

    LinearLayout main_all_posts_lay;

    TextView no_items;

    String sub_cat_ID;


    FloatingActionButton add_ticket_btn;

    int current_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        Ads ads=new Ads(this);
        ads.returnnum();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            sub_cat_ID = intent.getStringExtra("SUB_CAT_ID");
            setTitle(intent.getStringExtra("Sub_Cat_Name"));
        }


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        main_all_posts_lay = findViewById(R.id.main_all_posts_lay);

        no_items = findViewById(R.id.no_items);

        manager_lay = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        add_ticket_btn = findViewById(R.id.add_post_btn);
        add_ticket_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sessionManager.isLoggedIn()) {
                    Intent add_post_intent = new Intent(getApplicationContext(), Add_post_Activ.class);
                    startActivity(add_post_intent);
                }else {
                    Intent login_intent = new Intent(getApplicationContext(), Login_Activity.class);
                    startActivity(login_intent);
                }

            }
        });


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


        all_posts_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && add_ticket_btn.isShown())
                    add_ticket_btn.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    add_ticket_btn.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
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

            Get_All_My_Posts(current_page);
            Utility.dialog_Show(dialog_bar, getApplicationContext());
            dialog_bar.show();


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

    public void Get_All_My_Posts(int Page) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL +
                "posts/get?" + "sub_cat_id=" + sub_cat_ID + "&" + "page=" + Page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {

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


                                Utility.dialog_error(dialog_bar, All_Products_Activ.this, s.toString());

                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());

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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL +
                "posts/get?" + "sub_cat_id=" + sub_cat_ID + "&" + "page=" + Page,
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


                                Utility.dialog_error(dialog_bar, All_Products_Activ.this, s.toString());

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