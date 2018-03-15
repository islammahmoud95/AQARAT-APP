package com.sawa.aqarat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sawa.aqarat.Models.All_Banks_Items;
import com.sawa.aqarat.R;

import java.util.ArrayList;

public class All_Banks_Adapter extends RecyclerView.Adapter<All_Banks_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<All_Banks_Items> productsArrayList;

    private DataTransferInterface_banks dtInterface;


    public All_Banks_Adapter(Activity context, ArrayList<All_Banks_Items> productsArrayList, DataTransferInterface_banks dtInterface) {

        this.dtInterface = dtInterface;
        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_banks, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;


        String image = productsArrayList.get(position).getBank_img();
        if (!image.equals("")) {
            Glide.with(context).load(image).override(100, 80).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.subject_img);
            Log.d("image", image);

        }


        final String bank_id_str = productsArrayList.get(position).getBankID();
        final String bank_name_str = productsArrayList.get(position).getBankName();
        final String bank_acc_name_str = productsArrayList.get(position).getBank_Account_Name();
        final String bank_acc_number_str = productsArrayList.get(position).getBank_Account_Number();


        // for selecting first item in adapter
        // the first load is 0 and set it selected and in another select change background and items selected
        if (selected_position == position) {

            holder.card_view_banks.setCardBackgroundColor(context.getResources().getColor(R.color.purple_color));
            dtInterface.setValues_bank(bank_id_str, bank_name_str, bank_acc_name_str, bank_acc_number_str);

        } else {
            holder.card_view_banks.setCardBackgroundColor(context.getResources().getColor(R.color.white_one));
        }


        (holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return;
                }


                notifyItemChanged(selected_position);
                selected_position = holder.getAdapterPosition();
                notifyItemChanged(selected_position);

                dtInterface.setValues_bank(bank_id_str, bank_name_str, bank_acc_name_str, bank_acc_number_str);

            }
        });

    }

    private int selected_position = 0;

    public interface DataTransferInterface_banks {

        void setValues_bank(String id, String name, String acc_name, String acc_num);

    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        ImageView subject_img;
        CardView card_view_banks;

        Context context;
        ArrayList<All_Banks_Items> productsArrayList;


        RecyclerViewHolder(View view, Context context, final ArrayList<All_Banks_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;

            subject_img = view.findViewById(R.id.products_img);
            card_view_banks = view.findViewById(R.id.card_view_banks);
        }

    }


}