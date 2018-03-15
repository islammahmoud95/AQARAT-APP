package com.sawa.aqarat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sawa.aqarat.Activities.Product_Details;
import com.sawa.aqarat.Models.All_Products_Items;
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


public class All_Products_Adapter extends RecyclerView.Adapter<All_Products_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<All_Products_Items> productsArrayList;


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    public All_Products_Adapter(Activity context, ArrayList<All_Products_Items> productsArrayList) {

        dialog_bar = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(context);


        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_products, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;


        final String sender_name_str = productsArrayList.get(position).getPost_sender();
        holder.subject_name.setText(sender_name_str);


        final String price_txt = productsArrayList.get(position).getPost_price();
        holder.subject_price_txt.setText(price_txt);


        final String isVip_str = productsArrayList.get(position).getProducts_iSvIP();

        if (isVip_str.equals("0")){

            holder.subject_ad_type.setVisibility(View.GONE);

        }else {

            if (sessionManager.getLang().equals("ar")){

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.subject_ad_type.getLayoutParams();
                params = new ConstraintLayout.LayoutParams(params); // copy the original ones
                params.horizontalBias = 1.0f; // here is one modification for example. modify anything else you want :)
                holder.subject_ad_type.setLayoutParams(params); // request the view to use the new modified params

            }
            holder.subject_ad_type.setText(R.string.special_ad_str);
        }


        final String desc_txt = productsArrayList.get(position).getProducts_desc();
        holder.subject_desc_txt.setText(desc_txt);


        String image = productsArrayList.get(position).getPost_img();
        if (!image.equals("")) {
            Glide.with(context).load(image).animate(R.anim.zoom_in)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).centerCrop()
                    .into(holder.subject_img);
            Log.d("image", image);

        }


        final String product_id = productsArrayList.get(position).getPost_Id();
        (holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sub_cat_intent = new Intent(context, Product_Details.class);
                sub_cat_intent.putExtra("PRODUCT_ID", product_id);
                //  sub_cat_intent.putExtra("Main_Cat_Name", name_str );
                context.startActivity(sub_cat_intent);

            }
        });

        holder.add_wish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Check_Con.getInstance(context).isOnline()) {

                    Add_to_WishList(product_id);
                    Utility.dialog_Show(dialog_bar , context);
                    dialog_bar.show();


                } else {

                    Snackbar snackbar = Snackbar
                            .make(holder.itemView, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            });

                    snackbar.show();

                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        TextView subject_name;
        ImageView subject_img;
        TextView subject_price_txt;
        TextView subject_desc_txt;
        TextView subject_ad_type;

        Button add_wish_btn;

        Context context;
        ArrayList<All_Products_Items> productsArrayList;

        RecyclerViewHolder(View view, Context context, final ArrayList<All_Products_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;

            subject_name = view.findViewById(R.id.sender_name_txt);
            subject_img = view.findViewById(R.id.item_img);
            subject_price_txt = view.findViewById(R.id.price_txt);
            subject_desc_txt = view.findViewById(R.id.desc_txt);
            add_wish_btn = view.findViewById(R.id.add_wish_btn);
            subject_ad_type = view.findViewById(R.id.ad_type_txt);

        }
    }



    private Request.Priority mPriority_Category = Request.Priority.IMMEDIATE;

    private void Add_to_WishList(final String post_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/addToWish",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("respoooooo", response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                dialog_bar.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                dialog_bar.setTitleText(context.getString(R.string.sucsess_dialog_str));
                                dialog_bar.setContentText("This Post Successfully Added to your Wishlist");
                                dialog_bar.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismiss();

                                    }
                                });


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


                                Utility.dialog_error(dialog_bar , context, s.toString());
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

                        Toast.makeText(context, R.string.server_down, Toast.LENGTH_LONG).show();

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
                params.put("post_id" ,  post_id);

                Log.d("ProductsParams", params.toString());

                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}