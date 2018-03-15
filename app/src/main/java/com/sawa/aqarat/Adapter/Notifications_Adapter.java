package com.sawa.aqarat.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sawa.aqarat.Activities.Admin_Messages_activ;
import com.sawa.aqarat.Activities.Genral_Notify_detail_activ;
import com.sawa.aqarat.Activities.Product_Details;
import com.sawa.aqarat.Activities.Tickets.Ticket_Details_Activ;
import com.sawa.aqarat.Models.Notifications_Items;
import com.sawa.aqarat.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Notifications_Adapter extends RecyclerView.Adapter<Notifications_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<Notifications_Items> productsArrayList;


    public Notifications_Adapter(Activity context, ArrayList<Notifications_Items> productsArrayList) {

        this.context = context;
        this.productsArrayList = productsArrayList;


        // this used if activity opened via notification bar
//        Intent order_expand = (context).getIntent();
//        if (order_expand.hasExtra("TITLE")) {
//            String title_notify = order_expand.getStringExtra("TITLE");
//            String content_notify = order_expand.getStringExtra("CONTENT");
//            Log.d("title", title_notify);
//
//            new DroidDialog.Builder(context)
//                    .icon(R.drawable.matgar_logo_with_circle)
//                    .title(title_notify)
//                    .content(content_notify)
//                    .cancelable(true, true)
//                    .positiveButton(context.getString(R.string.ok_dialog),
//                            new DroidDialog.onPositiveListener() {
//                                @Override
//                                public void onPositive(Dialog droidDialog) {
//
//                                    droidDialog.dismiss();
//
//                                }
//                            })
//
//                    .typeface("Roboto-Regular.ttf")
//                    .animation(AnimUtils.AnimUpDown)
//                    .color(ContextCompat.getColor(context, R.color.red_normal), ContextCompat.
//                                    getColor(context, android.R.color.transparent),
//                            ContextCompat.getColor(context, R.color.red_dark))
//                    .divider(true, ContextCompat.getColor(context, R.color.red_dark))
//                    .show();
//        }


    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_notifications, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);

    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {

        String tickt_ID = productsArrayList.get(position).getNotify_Id();
        Log.d("sfsdf", tickt_ID);

        final String title_str = productsArrayList.get(position).getNotify_Title();
        holder.subject_title_txt.setText(title_str);

        final String description_str = productsArrayList.get(position).getNotify_Description();
        holder.subject_description_txt.setText(description_str);

        String is_seen_str = productsArrayList.get(position).getNotify_is_Seen();
        if (is_seen_str.equals("0")){
            holder.is_seen_status.setBackgroundResource(R.drawable.dot_empty_circle);
        }else {
            holder.is_seen_status.setBackgroundResource(R.drawable.dot_fill_circle);
        }

        final String redirect_id = productsArrayList.get(position).getNotify_Redirect_ID();
        final String redirect_type = productsArrayList.get(position).getNotify_Redirect_Type();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (redirect_type) {
                    case "1":
                        intent = new Intent(context, Product_Details.class);
                        intent.putExtra("PRODUCT_ID", redirect_id);
                        context.startActivity(intent);
                        break;
                    case "2":
                        intent = new Intent(context, Ticket_Details_Activ.class);
                        intent.putExtra("TICKET_ID", redirect_id);
                        context.startActivity(intent);
                        break;
                    case "3":
                        intent = new Intent(context, Product_Details.class);
                        intent.putExtra("PRODUCT_ID", redirect_id);
                        context.startActivity(intent);
                        break;
                    case "4":
                        intent = new Intent(context, Admin_Messages_activ.class);
                      //  intent.putExtra("BILL_ID", redirect_id);
                        context.startActivity(intent);
                        break;
                    case "5":
                        intent = new Intent(context, Genral_Notify_detail_activ.class);
                        intent.putExtra("GENERAL_ID", redirect_id);
                        context.startActivity(intent);
                        break;

                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView subject_title_txt;
        TextView subject_description_txt;
        TextView subject_date_txt;
        CircleImageView is_seen_status;


        Context context;
        ArrayList<Notifications_Items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<Notifications_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;

            subject_title_txt = view.findViewById(R.id.notify_title_txt);
            subject_description_txt = view.findViewById(R.id.description_txt);
            subject_date_txt = view.findViewById(R.id.date_txt);
            is_seen_status = view.findViewById(R.id.is_seen_status);
        }

    }


}