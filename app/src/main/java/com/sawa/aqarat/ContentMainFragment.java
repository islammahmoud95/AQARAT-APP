package com.sawa.aqarat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sawa.aqarat.Activities.Filter_Search_Activity;
import com.sawa.aqarat.Activities.Login_Activity;
import com.sawa.aqarat.Activities.My_WishList;
import com.sawa.aqarat.Activities.Sub_Cat_Act;
import com.sawa.aqarat.utilities.SessionManager;

import io.codetail.widget.RevealFrameLayout;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentMainFragment extends Fragment implements ScreenShotable {

    SessionManager sessionManager;

    private View containerView;
    protected RevealFrameLayout mImageView;
   // protected int res;
    private Bitmap bitmap;

    public static ContentMainFragment newInstance() {
        ContentMainFragment contentFragment = new ContentMainFragment();
      //  Bundle bundle = new Bundle();
      //  bundle.putInt(Integer.class.getName(), resId);
      //  contentFragment.setArguments(bundle);
        return contentFragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   res = getArguments().getInt(Integer.class.getName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_fragment_main, container, false);
        mImageView =  rootView.findViewById(R.id.container_frame);
        sessionManager = new SessionManager(getActivity());
        mImageView.setClickable(true);
        mImageView.setFocusable(true);

        RelativeLayout land_btn = (RelativeLayout)rootView.findViewById(R.id.land_btn);
        land_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent land_intent = new Intent(getActivity() , Sub_Cat_Act.class);
                land_intent.putExtra("Cat_ID" , "0");
                land_intent.putExtra("Cat_Name" , getResources().getString(R.string.land_str));
                startActivity(land_intent);



            }
        });


        RelativeLayout real_stat_btn = (RelativeLayout)rootView.findViewById(R.id.real_stat_btn);
        real_stat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent land_intent = new Intent(getActivity() , Sub_Cat_Act.class);
                land_intent.putExtra("Cat_ID" , "1");
                land_intent.putExtra("Cat_Name" , getResources().getString(R.string.real_estate_str));
                startActivity(land_intent);



            }
        });


        RelativeLayout wish_list_btn = (RelativeLayout)rootView.findViewById(R.id.wish_list_btn);
        wish_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sessionManager.isLoggedIn()){
                    Intent land_intent = new Intent(getActivity() , My_WishList.class);
                    startActivity(land_intent);
                }else {
                    Intent go_to_login = new Intent(getActivity() , Login_Activity.class);
                    startActivity(go_to_login);
                }




            }
        });


        RelativeLayout search_btn = (RelativeLayout)rootView.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent land_intent = new Intent(getActivity() , Filter_Search_Activity.class);
                land_intent.putExtra("Cat_Name" , "Search");
                startActivity(land_intent);



            }
        });


      //  mImageView.setImageResource(res);
        return rootView;
    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(), containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                ContentMainFragment.this.bitmap = bitmap;
            }
        };

        thread.start();

    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }




}

