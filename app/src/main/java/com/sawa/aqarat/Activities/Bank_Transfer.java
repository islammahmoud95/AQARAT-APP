package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
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
import com.sawa.aqarat.Adapter.All_Banks_Adapter;
import com.sawa.aqarat.MainActivity;
import com.sawa.aqarat.Models.All_Banks_Items;
import com.sawa.aqarat.Parser.ParseMy_All_Banks;
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


public class Bank_Transfer extends AppCompatActivity implements All_Banks_Adapter.DataTransferInterface_banks {


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;


    RecyclerView banks_recycler;
    GridLayoutManager manager_lay;
    All_Banks_Adapter all_products_adapter;


    EditText ban_name_edt, ban_acc_name_edt, ban_acc_num_edt,
            amount_edt, legal_name_edt, process_num_edt;


    String ban_name_str = "",
            ban_acc_name_str = "",
            ban_acc_num_str = "",
            amount_str = "",
            legal_name_str = "",
            process_num_str = "",
            bank_id,
            payment_method, pack_ID;

    ScrollView transfer_scroll_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_banks);

        Ads ads=new Ads(this);
        ads.returnnum();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("PAYMENT_METHOD")) {
                payment_method = intent.getStringExtra("PAYMENT_METHOD");
                pack_ID = intent.getStringExtra("PACKAGE_ID");
                Log.d("sfsdf", pack_ID);
            }
        }


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        ban_name_edt = findViewById(R.id.bank_name_edt);
        ban_acc_name_edt = findViewById(R.id.account_name_edt);
        ban_acc_num_edt = findViewById(R.id.account_number_edt);

        amount_edt = findViewById(R.id.amount_edt);
        legal_name_edt = findViewById(R.id.legal_name_edt);
        process_num_edt = findViewById(R.id.process_num_edt);


        transfer_scroll_lay = findViewById(R.id.transfer_scroll_lay);


        manager_lay = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);

        banks_recycler = findViewById(R.id.banks_recycle);


        banks_recycler.setLayoutManager(manager_lay);
        banks_recycler.setAdapter(new RecyclerView.Adapter() {
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


        banks_recycler.setHasFixedSize(true);
        banks_recycler.setNestedScrollingEnabled(false);


        int spacingInPixels = 5;
        banks_recycler.addItemDecoration(new SpacesItemDecoration(spacingInPixels));


        Button transfer_bank_btn = findViewById(R.id.transfer_bank_btn);
        transfer_bank_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                    if (validate()) {

                        dialog_bar.show();
                        Transfer_bank_API();
                    }

                } else {

                    Snackbar snackbar = Snackbar
                            .make(banks_recycler, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume", "1");

        Utility.setupUI(transfer_scroll_lay, this);

        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Get_My_list_Banks();
            Utility.dialog_Show(dialog_bar, getApplicationContext());


        } else {

            Snackbar snackbar = Snackbar
                    .make(banks_recycler, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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

    public void Get_My_list_Banks() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL +
                "banks/all",
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

                                Utility.dialog_error(dialog_bar, Bank_Transfer.this, s.toString());

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

        ParseMy_All_Banks parseProducts;
        parseProducts = new ParseMy_All_Banks(json);

        productsArrayList_Category = parseProducts.parseProducts();

//        if (!productsArrayList.isEmpty()) {
        all_products_adapter = new All_Banks_Adapter(this, productsArrayList_Category, this);
        banks_recycler.setAdapter(all_products_adapter);
//            productsArrayList.clear();

        //   } else {
        all_products_adapter.notifyDataSetChanged();
        //     }


    }

    ArrayList<All_Banks_Items> productsArrayList_Category = new ArrayList<>();


    @Override
    public void setValues_bank(String id, String name, String acc_name, String acc_num) {

        bank_id = id;

        ban_name_edt.setText(name);
        ban_acc_name_edt.setText(acc_name);
        ban_acc_num_edt.setText(acc_num);


    }


    public boolean validate() {
        boolean valid = true;

        try {
            ban_name_str = ban_name_edt.getText().toString().trim();
            ban_acc_name_str = ban_acc_name_edt.getText().toString().trim();
            ban_acc_num_str = ban_acc_num_edt.getText().toString().trim();

            amount_str = amount_edt.getText().toString().trim();
            legal_name_str = legal_name_edt.getText().toString().trim();
            process_num_str = process_num_edt.getText().toString().trim();

        } catch (Exception e) {

            Log.e("exception", e.getMessage());
        }


        if (amount_str.isEmpty()) {
            amount_edt.setError(getString(R.string.valid_amount));
            valid = false;
        } else {
            amount_edt.setError(null);

        }

        if (legal_name_str.isEmpty()) {
            legal_name_edt.setError(getString(R.string.valid_legal_name));
            valid = false;
        } else {
            legal_name_edt.setError(null);

        }


        if (process_num_str.isEmpty()) {
            process_num_edt.setError(getString(R.string.valid_process_number));
            valid = false;
        } else {
            process_num_edt.setError(null);

        }


        return valid;
    }

    public void Transfer_bank_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/makeRequestUpgrade",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {


                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                Intent go_to_login = new Intent(Bank_Transfer.this, MainActivity.class);
                                go_to_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(go_to_login);
                                finish();


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

                                Utility.dialog_error(dialog_bar, Bank_Transfer.this, s.toString());

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
                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("paymentMethod", payment_method);
                params.put("package_id", pack_ID);
                params.put("bank_id", bank_id);
                params.put("process_num", process_num_str);
                params.put("amount", amount_str);
                params.put("legal_name", legal_name_str);


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
    public void onBackPressed() {
        super.onBackPressed();

        //all this cause clear_task_flag in address back finish app
        // so check if this last one open main activity

        if (isTaskRoot()) {
            Intent go_to_main = new Intent(this, MainActivity.class);
            startActivity(go_to_main);
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