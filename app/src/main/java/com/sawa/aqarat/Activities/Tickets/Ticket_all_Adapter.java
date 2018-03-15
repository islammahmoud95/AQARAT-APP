package com.sawa.aqarat.Activities.Tickets;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sawa.aqarat.R;

import java.util.ArrayList;


public class Ticket_all_Adapter extends RecyclerView.Adapter<Ticket_all_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<Ticket_all_Items> productsArrayList;


    public Ticket_all_Adapter(Activity context, ArrayList<Ticket_all_Items> productsArrayList) {

        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_tickets, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;

        final String tickt_ID = productsArrayList.get(position).getTicktId();

        Log.d("sfsdf", tickt_ID);


        String reason_str = productsArrayList.get(position).getTicktReason();
        holder.subject_reason_txt.setText(reason_str);


        String description_str = productsArrayList.get(position).getTicktDescription();
        holder.subject_description_txt.setText(description_str);


        String date_str = productsArrayList.get(position).getTicktDate();
        holder.subject_date_txt.setText(date_str);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent go_to_details = new Intent(context , Ticket_Details_Activ.class);
                go_to_details.putExtra("TICKET_ID",tickt_ID);
                context.startActivity(go_to_details);

            }
        });




    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        TextView subject_reason_txt;
        TextView subject_description_txt;
        TextView subject_date_txt;

        Context context;
        ArrayList<Ticket_all_Items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<Ticket_all_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;


            subject_reason_txt = view.findViewById(R.id.reason_txt);
            subject_description_txt = view.findViewById(R.id.description_txt);
            subject_date_txt = view.findViewById(R.id.date_txt);


        }

    }


}