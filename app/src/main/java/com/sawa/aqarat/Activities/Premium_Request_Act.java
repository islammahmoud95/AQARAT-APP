package com.sawa.aqarat.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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


public class Premium_Request_Act extends AppCompatActivity {


    RadioGroup payment_group;

    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    ConstraintLayout main_payment_lay;

    private ArrayList<String> pack_pay_Array;
    private ArrayList<String> id_pack_pay;
    private Spinner spinner_pack_pay;
    String package_ID_pos = "0";

    String payment_str = "0";

    TextView posts_counter_txt, duration_txt, price_txt;

    LinearLayout amount_lay;
    EditText input_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_request);

        Ads ads=new Ads(this);
        ads.returnnum();
        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        main_payment_lay = findViewById(R.id.main_payment_lay);


        pack_pay_Array = new ArrayList<>();
        id_pack_pay = new ArrayList<>();
        spinner_pack_pay = findViewById(R.id.spinner_pack_pay);


        posts_counter_txt = findViewById(R.id.ads_counter_txt);
        duration_txt = findViewById(R.id.duration_txt);
        price_txt = findViewById(R.id.price_txt);

        amount_lay = findViewById(R.id.amount_lay);
        input_amount = findViewById(R.id.input_amount);

        if (Check_Con.getInstance(this).isOnline()) {

            get_Payment_Packages();
            Utility.dialog_Show(dialog_bar, this);


            spinner_pack_pay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    Log.d("asfsaf", id_pack_pay.get(position));

                    package_ID_pos = id_pack_pay.get(position);

                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    get_Package_Details(package_ID_pos);


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } else {


            pack_pay_Array.add(getString(R.string.no_connect));


            spinner_pack_pay.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_items, pack_pay_Array));


            Snackbar snackbar = Snackbar
                    .make(main_payment_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();
        }


        payment_group = findViewById(R.id.payment_group);
        payment_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {


                View radioButton = payment_group.findViewById(checkedId);
                int index = payment_group.indexOfChild(radioButton);


                // 0 is bank transfer and 1 is vodafone cash
                if (index == 0) // that's mean yes chosen
                {

                    payment_str = "0";
                    amount_lay.setVisibility(View.GONE);

                } else if (index == 1) {

                    payment_str = "1";
                    amount_lay.setVisibility(View.VISIBLE);

                }

            }
        });


        Button confirm_btn = findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (payment_str.equals("0")) {

                    Intent go_to_pay = new Intent(getApplicationContext(), Bank_Transfer.class);
                    go_to_pay.putExtra("PAYMENT_METHOD", payment_str);
                    go_to_pay.putExtra("PACKAGE_ID", package_ID_pos);
                    startActivity(go_to_pay);
                } else {

                    Utility.dialog_Show(dialog_bar, getApplicationContext());
                    Make_Payment();

                }

            }
        });


    }


    private void get_Payment_Packages() {
        //Creating a string request

        String tag_json_obj = "json_obj_count";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "packages/all?"
                + "lang=" + sessionManager.getLang(),
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

                                    id_pack_pay.add(id);
                                }


                                parse_getSub_Cat(data_array);

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


                                Utility.dialog_error(dialog_bar, Premium_Request_Act.this, s.toString());

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


    private void parse_getSub_Cat(JSONArray j) {
        //Traversing through all the items in the json array

        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                pack_pay_Array.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        spinner_pack_pay.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_items, pack_pay_Array));

    }


    private void get_Package_Details(String package_ID) {
        //Creating a string request

        String tag_json_obj = "json_obj_count";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "packages/one?"
                + "lang=" + sessionManager.getLang()
                + "&" + "package_id=" + package_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("tessst", response);

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONObject data_obj = obj.getJSONObject("Data");
                                String price_str = data_obj.getString("price");
                                String duration_str = data_obj.getString("duration");
                                String posts_count_str = data_obj.getString("posts_count");


                                posts_counter_txt.setText(posts_count_str);
                                duration_txt.setText(duration_str);
                                price_txt.setText(price_str);


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


                                Utility.dialog_error(dialog_bar, Premium_Request_Act.this, s.toString());

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


    private void Make_Payment() {
        //Creating a string request

        String tag_json_obj = "json_obj_count";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/makeRequestUpgrade",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("tessst", response);

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONObject data_obj = obj.getJSONObject("Data");
                                String price_str = data_obj.getString("price");
                                String duration_str = data_obj.getString("duration");
                                String posts_count_str = data_obj.getString("posts_count");


                                posts_counter_txt.setText(posts_count_str);
                                duration_txt.setText(price_str);
                                price_txt.setText(duration_str);


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


                                Utility.dialog_error(dialog_bar, Premium_Request_Act.this, s.toString());

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
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("lang", sessionManager.getLang());
                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("package_id", package_ID_pos);
                params.put("paymentMethod", payment_str);
                params.put("amount", input_amount.getText().toString());
                Log.e("parms", params.toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }


}
