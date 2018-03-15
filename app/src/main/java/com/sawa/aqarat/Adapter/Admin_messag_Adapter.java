package com.sawa.aqarat.Adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sawa.aqarat.Models.Admin_messag_Items;
import com.sawa.aqarat.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Admin_messag_Adapter extends RecyclerView.Adapter<Admin_messag_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<Admin_messag_Items> productsArrayList;


    public Admin_messag_Adapter(Activity context, ArrayList<Admin_messag_Items> productsArrayList) {

        this.context = context;
        this.productsArrayList = productsArrayList;

    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_admin_msg, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //  final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;


        final String message_ID = productsArrayList.get(position).getAD_Msg_Id();

        Log.d("id", message_ID);


        String sender_img_str = productsArrayList.get(position).getAD_Msg_Sender_Img();

        if (!sender_img_str.equals("")) {
            Glide.with(context).load(sender_img_str).animate(R.anim.zoom_in)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.sender_image);
            Log.d("image", sender_img_str);

        }


        String message_str = productsArrayList.get(position).getAD_Msg_Message();
        holder.subject_mesg_txt.setText(message_str);


        String sender_stat_str = productsArrayList.get(position).getAD_Msg_Sender();

        // 0 for admin messages  gray
        // 1 for user messages   red
        Log.d("sender", sender_stat_str);
        if (sender_stat_str.equals("0")) {


            holder.text_background_lay.setBackgroundColor(context.getResources().getColor(R.color.purple_color));

            holder.main_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        } else {

            holder.text_background_lay.setBackgroundColor(context.getResources().getColor(R.color.purple_light_color));

            holder.main_layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        CircleImageView sender_image;
        TextView subject_mesg_txt;
        LinearLayout text_background_lay;
        RelativeLayout main_layout;


        Context context;
        ArrayList<Admin_messag_Items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<Admin_messag_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;


            sender_image =  view.findViewById(R.id.sender_image);
            subject_mesg_txt = view.findViewById(R.id.msg_txt);
            main_layout =  view.findViewById(R.id.main_layout);
            text_background_lay =  view.findViewById(R.id.text_background_lay);
        }

    }


}