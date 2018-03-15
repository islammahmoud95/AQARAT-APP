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
import com.sawa.aqarat.Adapter.My_WishList_Adapter;
import com.sawa.aqarat.Models.My_WishList_Items;
import com.sawa.aqarat.Parser.ParseMy_WishList;
import com.sawa.aqarat.R;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;


public class My_WishList extends AppCompatActivity {


    RecyclerView my_wishList_recycler;
    LinearLayoutManager manager_lay;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    //  Ticket_all_Adapter tickets_all_adapter;
    My_WishList_Adapter my_wishList_adapter;

    LinearLayout main_all_posts_lay;

    TextView no_items;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wish_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Ads ads=new Ads(this);
        ads.returnnum();
        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        main_all_posts_lay = findViewById(R.id.main_all_posts_lay);

        no_items = findViewById(R.id.no_items);

        manager_lay = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);





        my_wishList_recycler = findViewById(R.id.get_my_posts_recycle);
        my_wishList_recycler.setLayoutManager(manager_lay);
        my_wishList_recycler.setAdapter(new RecyclerView.Adapter() {
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



        my_wishList_recycler.setHasFixedSize(true);
        my_wishList_recycler.setNestedScrollingEnabled(false);


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume", "1");


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Get_My_Wishlist();
            Utility.dialog_Show(dialog_bar , getApplicationContext());


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

    public void Get_My_Wishlist() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL +
                "users/getWishlist?" + "api_token=" + sessionManager.getUserDetails().get("Server_token"),
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


                                Utility.dialog_error(dialog_bar , My_WishList.this, s.toString());

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

        ParseMy_WishList parseProducts;
        parseProducts = new ParseMy_WishList(json);

        productsArrayList_Category = parseProducts.parseProducts();

//        if (!productsArrayList.isEmpty()) {
        my_wishList_adapter = new My_WishList_Adapter(this, productsArrayList_Category);
        my_wishList_recycler.setAdapter(my_wishList_adapter);
//            productsArrayList.clear();

        //   } else {
        my_wishList_adapter.notifyDataSetChanged();
        //     }


        //show empty view if adapter empty
        if (productsArrayList_Category.isEmpty()) {
            my_wishList_recycler.setVisibility(View.GONE);
            no_items.setVisibility(View.VISIBLE);
        }else {
            my_wishList_recycler.setVisibility(View.VISIBLE);
            no_items.setVisibility(View.GONE);
        }


    }

    ArrayList<My_WishList_Items> productsArrayList_Category = new ArrayList<>();


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