package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.Adapter.Sub_Cat_Adapter;
import com.sawa.aqarat.Models.Sub_Cat_Items;
import com.sawa.aqarat.Parser.ParseMy_Sub_Cat;
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


public class Sub_Cat_Act extends AppCompatActivity {

    String cat_id;


    RecyclerView sub_Category_recycler;
    GridLayoutManager manager_lay;
    SweetAlertDialog dialog_bar;
    Sub_Cat_Adapter subCat_adapter;
    SessionManager session;
    String toolbar_name;
    LinearLayout sub_cat_lay;
    TextView no_items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat);

        Intent intent = getIntent();
        if (intent != null) {
            cat_id = intent.getStringExtra("Cat_ID");
            toolbar_name = intent.getStringExtra("Cat_Name");
        }
        Ads ads=new Ads(this);
        ads.returnnum();

        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        session = new SessionManager(getApplicationContext());


        sub_cat_lay = findViewById(R.id.sub_cat_lay);

        no_items = findViewById(R.id.no_items);

        manager_lay = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        sub_Category_recycler = findViewById(R.id.sub_cat_recycle);


        sub_Category_recycler.setLayoutManager(manager_lay);
        sub_Category_recycler.setAdapter(new RecyclerView.Adapter() {
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


        sub_Category_recycler.setHasFixedSize(true);
        sub_Category_recycler.setNestedScrollingEnabled(false);


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
                    .make(sub_Category_recycler, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "categories/getSubCats?"
                + "type=" + cat_id + "&" + "lang=" + session.getLang(),
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


                                Utility.dialog_error(dialog_bar, Sub_Cat_Act.this, s.toString());

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
                        title_act_name.setText(toolbar_name);
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

        ParseMy_Sub_Cat parseProducts;
        parseProducts = new ParseMy_Sub_Cat(json);

        productsArrayList_Category = parseProducts.parseProducts();

//        if (!productsArrayList.isEmpty()) {
        subCat_adapter = new Sub_Cat_Adapter(this, productsArrayList_Category);
        sub_Category_recycler.setAdapter(subCat_adapter);
//            productsArrayList.clear();

        //   } else {
        subCat_adapter.notifyDataSetChanged();
        //     }

        //show empty view if adapter empty
        if (productsArrayList_Category.isEmpty()) {
            sub_cat_lay.setVisibility(View.GONE);
            no_items.setVisibility(View.VISIBLE);
        } else {
            sub_cat_lay.setVisibility(View.VISIBLE);
            no_items.setVisibility(View.GONE);
        }
    }

    ArrayList<Sub_Cat_Items> productsArrayList_Category = new ArrayList<>();


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}