package com.sawa.aqarat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sawa.aqarat.Activities.All_Products_Activ;
import com.sawa.aqarat.Models.Sub_Cat_Items;
import com.sawa.aqarat.R;

import java.util.ArrayList;


public class Sub_Cat_Adapter extends RecyclerView.Adapter<Sub_Cat_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<Sub_Cat_Items> productsArrayList;


    public Sub_Cat_Adapter(Activity context, ArrayList<Sub_Cat_Items> productsArrayList) {

        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sub_cat, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;


        final String name_str = productsArrayList.get(position).getProductsName();
        holder.subject_name.setText(name_str);

        String image = productsArrayList.get(position).getProducts_img();
        if (!image.equals("")) {
            Glide.with(context).load(image).animate(R.anim.zoom_in)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).override(200,200).centerCrop()
                    .into(holder.subject_img);
            Log.d("image", image);

        }


        final String sub_cat_id = productsArrayList.get(position).getProductsId();
        (holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sub_cat_intent = new Intent(context, All_Products_Activ.class);
                sub_cat_intent.putExtra("SUB_CAT_ID", sub_cat_id);
                sub_cat_intent.putExtra("Sub_Cat_Name", name_str );
                context.startActivity(sub_cat_intent);

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


        Context context;
        ArrayList<Sub_Cat_Items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<Sub_Cat_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;

            subject_name = view.findViewById(R.id.item_txt);
            subject_img = view.findViewById(R.id.item_img);

        }
    }
}