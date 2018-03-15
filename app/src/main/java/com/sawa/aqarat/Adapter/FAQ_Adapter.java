package com.sawa.aqarat.Adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.sawa.aqarat.Models.MyFAQ_Items;
import com.sawa.aqarat.R;

import java.util.ArrayList;


public class FAQ_Adapter extends RecyclerView.Adapter<FAQ_Adapter.RecyclerViewHolder> {

    private Activity context;

    private ArrayList<MyFAQ_Items> productsArrayList;

    private SparseBooleanArray expandState = new SparseBooleanArray();

    public FAQ_Adapter(Activity context, ArrayList<MyFAQ_Items> productsArrayList) {

        this.context = context;
        this.productsArrayList = productsArrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_faq, parent, false);
        return new RecyclerViewHolder(view, context, productsArrayList);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //   final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;

        final String faq_ID = productsArrayList.get(position).getFaqId();

        Log.d("sfsdf", faq_ID);


        String qes_str = productsArrayList.get(position).getFaqQes();
        holder.subject_qes_txt.setText(qes_str);


        String answer_str = productsArrayList.get(position).getFaqAnswer();
        holder.subject_answer_txt.setText(answer_str);


        holder.expandableLayout.setInRecyclerView(true);

        holder.expandableLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_med_low));

        holder.expandableLayout.setInterpolator(Utils.createInterpolator(Utils.BOUNCE_INTERPOLATOR));

        holder.expandableLayout.setExpanded(expandState.get(position));

        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                expandState.put(holder.getAdapterPosition(), true);
            }

            @Override
            public void onPreClose() {
                createRotateAnimator(holder.buttonLayout, 180f, 0f).start();
                expandState.put(holder.getAdapterPosition(), false);
            }
        });

        holder.buttonLayout.setRotation(expandState.get(position) ? 180f : 0f);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                holder.expandableLayout.toggle();
            }
        });


    }


    public void updateList(ArrayList<MyFAQ_Items> list) {
        productsArrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        Context context;
        ArrayList<MyFAQ_Items> productsArrayList;

        TextView subject_qes_txt;
        TextView subject_answer_txt;

        /**
         * You must use the ExpandableLinearLayout in the recycler view.
         * The ExpandableRelativeLayout doesn't work.
         */
        ExpandableLinearLayout expandableLayout;
        RelativeLayout buttonLayout;

        RecyclerViewHolder(View view, Context context, final ArrayList<MyFAQ_Items> productsArrayList) {

            super(view);

            this.context = context;
            this.productsArrayList = productsArrayList;


            subject_qes_txt =  view.findViewById(R.id.qes_txt);
            subject_answer_txt =  view.findViewById(R.id.answer_txt);
            expandableLayout = view.findViewById(R.id.expandableLayout);
            buttonLayout =  view.findViewById(R.id.button);

        }

    }


    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

}