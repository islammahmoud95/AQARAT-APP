package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sawa.aqarat.Adapter.Interested_details_Adapter;
import com.sawa.aqarat.Models.All_Interested_Items;
import com.sawa.aqarat.Parser.ParseAll_Interested;
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

public class Interest_Details_Activity extends AppCompatActivity {

    String type_ID;


    RecyclerView all_posts_recycler;
    GridLayoutManager manager_lay;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    Interested_details_Adapter all_posts_adapter;

    LinearLayout main_all_posts_lay;

    TextView no_items;

    Button update_interest_btn;



    RadioGroup selection_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_interest_details);
        // getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        Intent intent = getIntent();
        if (intent != null) {
            type_ID = intent.getStringExtra("INTEREST_DETAILS");
        }
        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        main_all_posts_lay = findViewById(R.id.main_all_posts_lay);

        no_items = findViewById(R.id.no_items);

        manager_lay = new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.VERTICAL, false);


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



        selection_group = (RadioGroup) findViewById(R.id.selection_group);
        selection_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {


                View radioButton = selection_group.findViewById(checkedId);
                int index = selection_group.indexOfChild(radioButton);


                // order_additinal_Str for validation and sending to web service
                if (index == 0) // that's mean yes chosen
                {


                    check_items_array = new ArrayList<>();

                    int childCount = all_posts_recycler.getChildCount();

                    for (int i = 0; i < childCount; i++) {
                        if (all_posts_recycler.findViewHolderForLayoutPosition(i) instanceof Interested_details_Adapter.RecyclerViewHolder) {

                            all_posts_adapter.productsArrayList.get(i).setInterest_IsChecked("1");
                            all_posts_adapter.notifyDataSetChanged();

                        }
                    }

                } else if (index == 1) {

                    check_items_array = new ArrayList<>();

                    int childCount = all_posts_recycler.getChildCount();

                    for (int i = 0; i < childCount; i++) {
                        if (all_posts_recycler.findViewHolderForLayoutPosition(i) instanceof Interested_details_Adapter.RecyclerViewHolder) {

                            all_posts_adapter.productsArrayList.get(i).setInterest_IsChecked("0");
                            all_posts_adapter.notifyDataSetChanged();

                        }
                    }

                }
            }
        });


        final RadioButton selected_radio_bin = findViewById(R.id.select_radio_btn);
        selected_radio_bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_radio_bin.setChecked(false);
            }
        });


        final RadioButton unelected_radio_bin = findViewById(R.id.unselect_radio_btn);
        unelected_radio_bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unelected_radio_bin.setChecked(false);
            }
        });


        update_interest_btn = findViewById(R.id.update_interest_btn);
        update_interest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (validate()) {
                        send_Selected_Interested();
                        Utility.dialog_Show(dialog_bar, getApplicationContext());
                    }

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
        });


    }


    ArrayList<String> check_items_array;




    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume", "1");


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Get_All_My_Interested();
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

    public void Get_All_My_Interested() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL +
                "users/subCatForInterested?" + "type=" + type_ID + "&"
                + "api_token=" + sessionManager.getUserDetails().get("Server_token"),
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


                                Utility.dialog_error(dialog_bar, Interest_Details_Activity.this, s.toString());

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

        ParseAll_Interested parseProducts;
        parseProducts = new ParseAll_Interested(json);

        productsArrayList_Category = parseProducts.parseProducts();

        all_posts_adapter = new Interested_details_Adapter(this, productsArrayList_Category);
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

    ArrayList<All_Interested_Items> productsArrayList_Category = new ArrayList<>();









    public boolean validate() {



        check_items_array = new ArrayList<>();

        int childCount = all_posts_recycler.getChildCount();

        for (int i = 0; i < childCount; i++) {
            if (all_posts_recycler.findViewHolderForLayoutPosition(i) instanceof Interested_details_Adapter.RecyclerViewHolder) {

//                        try {
//                            JSONObject jsonObject = new JSONObject();
//
//                            if (all_posts_adapter.productsArrayList.get(i).getInterest_IsChecked().equals("1")) {
//
//                                jsonObject.put("checked_id", all_posts_adapter.productsArrayList.get(i).getInterest_Id());
//
//
//                                check_items_array.put(jsonObject);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


                //  JSONObject jsonObject = new JSONObject();

                if (all_posts_adapter.productsArrayList.get(i).getInterest_IsChecked().equals("1")) {

                    check_items_array.add(all_posts_adapter.productsArrayList.get(i).getInterest_Id());

                }
            }
        }


        Log.e("printAllEditTextValues", check_items_array.toString());


        return true;
    }



    public void send_Selected_Interested() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL +
                "users/updateMyInterested" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                dialog_bar.dismiss();

                                finish();
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


                                Utility.dialog_error(dialog_bar, Interest_Details_Activity.this, s.toString());

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

                if (check_items_array.size() != 0) {
                    int i = 0;
                    for (String object : check_items_array) {
                        // you first send both data with same param name as friendnr[] ....
                        // now send with params friendnr[0],friendnr[1] ..and so on
                        params.put("interestedList[" + (i++) + "]" , object);
                    }
                }

                params.put("type" , type_ID);

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