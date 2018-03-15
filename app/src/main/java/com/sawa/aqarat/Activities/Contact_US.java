package com.sawa.aqarat.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;

public class Contact_US extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    LinearLayout main_contact_us_lay;

    String lat_str = "0.0",
            lng_str = "0.0",
            email_str,
            phone_num_str;


    EditText messgage_body_edt;
    String messgage_body_str = "";
    TextView phone_num_txt, address_txt , email_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Ads ads=new Ads(this);
        ads.returnnum();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            setTitle(R.string.contact_us_str);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_contact);
        mapFragment.getMapAsync(this);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());


        main_contact_us_lay = findViewById(R.id.main_contact_lay);




        phone_num_txt = findViewById(R.id.phone_num_txt);

        address_txt = findViewById(R.id.address_txt);

        email_txt = findViewById(R.id.email_txt);

        CircleImageView call_action_btn = findViewById(R.id.call_image);
        call_action_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (phone_num_str != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone_num_str));
                    startActivity(intent);

                } else {

                    Snackbar snackbar = Snackbar.make(main_contact_us_lay, R.string.no_num_contact, Snackbar.LENGTH_LONG);
                    snackbar.show();

                }


            }
        });


        messgage_body_edt = findViewById(R.id.content_reply_txt);

        Button send_email_contact = findViewById(R.id.send_message_btn);
        send_email_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Check_Con.getInstance(getApplicationContext()).isOnline()) {


                    if (validate()) {

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email_str});
                        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us_str));
                        i.putExtra(Intent.EXTRA_TEXT, messgage_body_str);
                        try {
                            startActivity(Intent.createChooser(i, getString(R.string.send_email)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(Contact_US.this, R.string.no_email_client, Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {


                    Snackbar snackbar = Snackbar
                            .make(main_contact_us_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
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


        if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

            Utility.dialog_Show(dialog_bar, this);

            get_contact_details();

        } else {

            Snackbar snackbar = Snackbar
                    .make(main_contact_us_lay, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });

            snackbar.show();
        }

        Utility.setupUI(main_contact_us_lay, this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        mMap = googleMap;


    }


    public boolean validate() {
        boolean valid = true;

        messgage_body_str = messgage_body_edt.getText().toString().trim();


        if (messgage_body_str.length() <= 5) {
            messgage_body_edt.setError(getString(R.string.valid_message));
            valid = false;
        } else {
            messgage_body_edt.setError(null);

        }


        return valid;
    }


    public void setlatlng(String lat, String lng) {
        LatLng TutorialsPoint = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        BitmapDescriptor map_client = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_icon);

        mMap.addMarker(new MarkerOptions()
                .snippet(getString(R.string.visit_us))
                .position(TutorialsPoint)
                .icon(map_client)
                .title("My Location")
                .flat(true)
                .draggable(true));


        CameraPosition cameraPosition = new CameraPosition.Builder().target(TutorialsPoint) // Sets the center
                // of the map to
                // location user
                .zoom(15) // Sets the zoom
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    public void get_contact_details() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.URL + "contactUs",

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api_res", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                JSONObject stat_reg = obj.getJSONObject("Data");




                                lat_str = stat_reg.getString("latitude");
                                lng_str = stat_reg.getString("longtiude");


                                setlatlng(lat_str, lng_str);



                                address_txt.setText(stat_reg.getString("address_en"));


                                phone_num_str = stat_reg.getString("phone");
                                phone_num_txt.setText(phone_num_str);


                                email_str = stat_reg.getString("email");
                                email_txt.setText(email_str);

                                Log.d("sfsdf", lat_str + lng_str + email_str + phone_num_str);


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


                                Utility.dialog_error(dialog_bar , Contact_US.this, s.toString());

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
                // params.put("login", loginJsonObject.toString());

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
