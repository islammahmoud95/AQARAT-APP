package com.sawa.aqarat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sawa.aqarat.Models.My_Interest_items;
import com.sawa.aqarat.R;

import java.util.ArrayList;


public class My_Interest_main_Adapter extends RecyclerView.Adapter<My_Interest_main_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<My_Interest_items> productsArrayList;


    public My_Interest_main_Adapter(Activity context, ArrayList<My_Interest_items> productsArrayList) {


        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_interest_my_items, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;


        final String name_str = productsArrayList.get(position).getInteerest_NAME();
        holder.subject_name.setText(name_str);


        final String product_id = productsArrayList.get(position).getInterest_ID();


    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        TextView subject_name;

        Context context;
        ArrayList<My_Interest_items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<My_Interest_items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;

            subject_name = view.findViewById(R.id.item_txt);

        }
    }

}