package com.sawa.aqarat.Adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sawa.aqarat.Models.Comments_all_Items;
import com.sawa.aqarat.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Comments_all_Adapter extends RecyclerView.Adapter<Comments_all_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<Comments_all_Items> productsArrayList;


    public Comments_all_Adapter(Activity context, ArrayList<Comments_all_Items> productsArrayList) {

        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_comments, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;


        String coment_img_str = productsArrayList.get(position).getComment_Image();
        if (!coment_img_str.equals("")) {
            Glide.with(context).load(coment_img_str).animate(R.anim.zoom_in)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.subject_sender_image);
            Log.d("image", coment_img_str);

        }



        String userName_str = productsArrayList.get(position).getComment_UserName();
        holder.subject_userName_txt.setText(userName_str);


        String content_str = productsArrayList.get(position).getComment_Content();
        holder.subject_content_txt.setText(content_str);




    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        CircleImageView subject_sender_image;
        TextView subject_userName_txt;
        TextView subject_content_txt;
       // TextView subject_date_txt;
        Context context;
        ArrayList<Comments_all_Items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<Comments_all_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;

            subject_sender_image = itemView.findViewById(R.id.sender_image);

            subject_userName_txt = view.findViewById(R.id.userName_txt);
            subject_content_txt = view.findViewById(R.id.content_txt);
           // subject_date_txt = view.findViewById(R.id.date_txt);


        }

    }


}