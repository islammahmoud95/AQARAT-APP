package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
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
import com.sawa.aqarat.Adapter.My_Interest_main_Adapter;
import com.sawa.aqarat.Models.My_Interest_items;
import com.sawa.aqarat.Parser.ParseInterst_Apart;
import com.sawa.aqarat.Parser.ParseInterst_Lands;
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

public class My_Interested_Activity extends AppCompatActivity {


    SweetAlertDialog dialog_bar;
    SessionManager session;


    LinearLayout main_interest_lay;


    RecyclerView my_Lands_recycler;
    LinearLayoutManager manager_lands_lay;
    My_Interest_main_Adapter lands_adapter;
    LinearLayout main_lands_lay;
    TextView no_items_lands;



    RecyclerView my_Apart_recycler;
    LinearLayoutManager manager_Apart_lay;
    My_Interest_main_Adapter apart_adapter;
    LinearLayout main_apart_lay;
    TextView no_items_apart;


    FloatingActionButton add_land_btn , add_apart_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_interested);

        main_interest_lay = findViewById(R.id.main_interest_lay);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        session = new SessionManager(getApplicationContext());



        main_lands_lay = findViewById(R.id.lands_lay);

        no_items_lands = findViewById(R.id.no_items_lands);

        manager_lands_lay = new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false);

        my_Lands_recycler = findViewById(R.id.interest_lands_recycle);


        my_Lands_recycler.setLayoutManager(manager_lands_lay);
        my_Lands_recycler.setAdapter(new RecyclerView.Adapter() {
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


        my_Lands_recycler.setHasFixedSize(true);
        my_Lands_recycler.setNestedScrollingEnabled(false);



        DividerItemDecoration dividerItemDecor_land = new DividerItemDecoration(my_Lands_recycler.getContext(),
                manager_lands_lay.getOrientation());
        my_Lands_recycler.addItemDecoration(dividerItemDecor_land);

        add_land_btn = findViewById(R.id.add_land_btn);
        add_land_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent go_to_detail = new Intent(getApplicationContext() , Interest_Details_Activity.class);
                go_to_detail.putExtra("INTEREST_DETAILS"  , "0");
                startActivity(go_to_detail);
            }
        });




        main_apart_lay = findViewById(R.id.apart_lay);

        no_items_apart = findViewById(R.id.no_items_apart);

        manager_Apart_lay = new LinearLayoutManager(this,  GridLayoutManager.VERTICAL, false);

        my_Apart_recycler = findViewById(R.id.apart_lands_recycle);


        my_Apart_recycler.setLayoutManager(manager_Apart_lay);
        my_Apart_recycler.setAdapter(new RecyclerView.Adapter() {
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


        my_Apart_recycler.setHasFixedSize(true);
        my_Apart_recycler.setNestedScrollingEnabled(false);


        DividerItemDecoration dividerItemDecor_apart = new DividerItemDecoration(my_Apart_recycler.getContext(),
                manager_Apart_lay.getOrientation());
        my_Apart_recycler.addItemDecoration(dividerItemDecor_apart);

        add_apart_btn = findViewById(R.id.add_apart_btn);
        add_apart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent go_to_detail = new Intent(getApplicationContext() , Interest_Details_Activity.class);
                go_to_detail.putExtra("INTEREST_DETAILS"  , "1");
                startActivity(go_to_detail);
            }
        });





    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume", "1");


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Get_My_list_Sub_Cat();
            Utility.dialog_Show(dialog_bar, this);


        } else {

            Snackbar snackbar = Snackbar
                    .make(main_interest_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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

    public void Get_My_list_Sub_Cat() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "users/getInterested?"
                + "api_token=" + session.getUserDetails().get("Server_token"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONObject data_obj = obj.getJSONObject("Data");



                                JSONArray lands_array = data_obj.getJSONArray("lands");

                                //show empty view if adapter empty
                                if (lands_array.length() <= 0 ) {
                                    my_Lands_recycler.setVisibility(View.GONE);
                                    no_items_lands.setVisibility(View.VISIBLE);
                                } else {
                                    my_Lands_recycler.setVisibility(View.VISIBLE);
                                    no_items_lands.setVisibility(View.GONE);
                                }
                                JSONArray apart_array = data_obj.getJSONArray("realEstate");

                                //show empty view if adapter empty
                                if (apart_array.length() <= 0 ) {
                                    my_Apart_recycler.setVisibility(View.GONE);
                                    no_items_apart.setVisibility(View.VISIBLE);
                                } else {
                                    my_Apart_recycler.setVisibility(View.VISIBLE);
                                    no_items_apart.setVisibility(View.GONE);
                                }


                                showProducts_Category(String.valueOf(data_obj));


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


                                Utility.dialog_error(dialog_bar, My_Interested_Activity.this, s.toString());

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

        ParseInterst_Lands parseProducts;
        parseProducts = new ParseInterst_Lands(json);

        productsArrayList_Category = parseProducts.parseProducts();

        lands_adapter = new My_Interest_main_Adapter(this, productsArrayList_Category);
        my_Lands_recycler.setAdapter(lands_adapter);

        lands_adapter.notifyDataSetChanged();




        ParseInterst_Apart parseProducts2;
        parseProducts2 = new ParseInterst_Apart(json);

        productsArrayList_Category = parseProducts2.parseProducts();

        apart_adapter = new My_Interest_main_Adapter(this, productsArrayList_Category);
        my_Apart_recycler.setAdapter(apart_adapter);

        apart_adapter.notifyDataSetChanged();










    }

    ArrayList<My_Interest_items> productsArrayList_Category = new ArrayList<>();





    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
