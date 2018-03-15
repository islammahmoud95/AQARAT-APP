package com.sawa.aqarat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sawa.aqarat.Models.All_Interested_Items;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.SessionManager;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Interested_details_Adapter extends RecyclerView.Adapter<Interested_details_Adapter.RecyclerViewHolder> {

    private Activity context;

    public ArrayList<All_Interested_Items> productsArrayList;


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    public Interested_details_Adapter(Activity context, ArrayList<All_Interested_Items> productsArrayList) {

        dialog_bar = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(context);


        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_interested, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;


        final String name_str = productsArrayList.get(position).getInterest_Name();
        holder.check_item.setText(name_str);



        //in some cases, it will prevent unwanted situations
        holder.check_item.setOnCheckedChangeListener(null);


        final String isChecked_str = productsArrayList.get(position).getInterest_IsChecked();
        if (isChecked_str.equals("0")){

            //if 0, your checkbox will be unselected, else selected
            holder.check_item.setChecked(false);
            holder.check_item.setBackgroundColor(ContextCompat.getColor(context , R.color.gray_light));
            holder.check_item.setTextColor(ContextCompat.getColor(context , R.color.trans_black));
        }else {
            holder.check_item.setChecked(true);
            holder.check_item.setBackgroundColor(ContextCompat.getColor(context , R.color.purple_color));
            holder.check_item.setTextColor(ContextCompat.getColor(context , R.color.white_one));

        }



        holder.check_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    holder.check_item.setChecked(true);
                    holder.check_item.setBackgroundColor(ContextCompat.getColor(context , R.color.purple_color));
                    holder.check_item.setTextColor(ContextCompat.getColor(context , R.color.white_one));
                    productsArrayList.get(holder.getAdapterPosition()).setInterest_IsChecked("1");
                }else {
                    holder.check_item.setChecked(false);
                    holder.check_item.setBackgroundColor(ContextCompat.getColor(context , R.color.gray_light));
                    holder.check_item.setTextColor(ContextCompat.getColor(context , R.color.trans_black));
                    productsArrayList.get(holder.getAdapterPosition()).setInterest_IsChecked("0");
                }

            }
        });



        (holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isChecked_str.equals("0")){
                    holder.check_item.setChecked(true);
                    productsArrayList.get(holder.getAdapterPosition()).setInterest_IsChecked("1");

                }else {
                    holder.check_item.setChecked(false);
                    productsArrayList.get(holder.getAdapterPosition()).setInterest_IsChecked("0");

                }


            }
        });


    }


//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        CheckBox check_item;

        Context context;
        ArrayList<All_Interested_Items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<All_Interested_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;

            check_item = view.findViewById(R.id.check_item);


        }
    }



}