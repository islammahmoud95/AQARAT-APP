package com.sawa.aqarat.Activities.Tickets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sawa.aqarat.R;
import com.sawa.aqarat.utilities.ImageLoader.ImageGesture;

import java.util.ArrayList;
import java.util.List;


public class Ticket_details_Adapter extends RecyclerView.Adapter<Ticket_details_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<Ticket_details_Items> productsArrayList;


    public Ticket_details_Adapter(Activity context, ArrayList<Ticket_details_Items> productsArrayList) {

        this.context = context;
        this.productsArrayList = productsArrayList;


    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        ViewHolder viewHolder = new ViewHolder(v);
//        return viewHolder;

        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tickets_details, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }


    @Override
    public void onBindViewHolder(Ticket_details_Adapter.RecyclerViewHolder holder, final int position) {


        holder.setIsRecyclable(false);

        final String tickt_ID = productsArrayList.get(position).getTicktId();

        Log.d("sfsdf", tickt_ID);


        String description_str = productsArrayList.get(position).getTicktDescription();
        holder.subject_description_txt.setText(description_str);


        String date_str = productsArrayList.get(position).getTicktDate();
        holder.subject_date_txt.setText(date_str);


        final String ticket_sender = productsArrayList.get(position).getTicktSender();
        Log.d("type", ticket_sender);

        //ticket sender 0 user ----- 1 admin

        switch (ticket_sender) {
            case "0":
                holder.line_color_view.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_color));
                holder.main_lay_item.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                break;
            case "1":
                holder.line_color_view.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_light_color));
                holder.main_lay_item.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                break;
            default:
                break;
        }


        final Ticket_details_Items superHero = productsArrayList.get(position);


        if (superHero.getTicktImages().size() == 0) {

            holder.scrollView.setVisibility(View.GONE);

        } else {

            //   holder.scrollView.setVisibility(View.VISIBLE);


            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT);

            for (int i = 0; i < superHero.getTicktImages().size(); i++) {

                final ImageView imageView = new ImageView(context);

                final int pos_img = i;

                Glide.with(context).load(superHero.getTicktImages().get(i)).diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(imageView);
                Log.d("imaaaaaaage", String.valueOf(superHero.getTicktImages().get(i)));

                imageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent go_to_view = new Intent(context, ImageGesture.class);
                        go_to_view.putExtra("URL_OF_IMAGE", superHero.getTicktImages().get(pos_img));
                        context.startActivity(go_to_view);

                    }
                });


                LLParams.setMargins(10, 0, 10, 10);
                imageView.setLayoutParams(LLParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.linear_images.addView(imageView);

            }


        }


    }


    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView subject_description_txt;
        TextView subject_date_txt;

        HorizontalScrollView scrollView;

        LinearLayout linear_images;

        View line_color_view;

        LinearLayout main_lay_item;

        Context context;
        List<Ticket_details_Items> productsArrayList;


        public RecyclerViewHolder(View view, Context context, final List<Ticket_details_Items> productsArrayList) {
            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;


            subject_description_txt = itemView.findViewById(R.id.description_txt);
            subject_date_txt = itemView.findViewById(R.id.date_txt);


            line_color_view = itemView.findViewById(R.id.line_color_view);

            scrollView = itemView.findViewById(R.id.scrollView_images);

            linear_images = itemView.findViewById(R.id.linear_images);

            main_lay_item = itemView.findViewById(R.id.main_lay_item);
        }
    }


}

