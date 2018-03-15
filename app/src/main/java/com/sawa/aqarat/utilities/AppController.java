package com.sawa.aqarat.utilities;


import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sawa.aqarat.R;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;


    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/changa_regular.ttf")
                .setFontAttrId(R.attr.fontPath).build());

        CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
        mInstance = this;


//        initialize and create the image loader logic
//        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
//            @Override
//            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
//                Glide.with(imageView.getContext()).load(uri)
//                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                        .placeholder(placeholder).into(imageView);
//
//            }
//
//            @Override
//            public void cancel(ImageView imageView) {
//                Glide.clear(imageView);
//            }
//
//            @Override
//            public Drawable placeholder(Context ctx, String tag) {
//                //define different placeholders for different imageView targets
//                //default tags are accessible via the DrawerImageLoader.Tags
//                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
//                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
//                    return DrawerUIUtils.getPlaceHolder(ctx);
//                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
//                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
//                } else if ("customUrlItem".equals(tag)) {
//                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
//                }
//
//                //we use the default one for
//                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
//
//                return super.placeholder(ctx, tag);
//            }
//        });
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }



    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }




    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }







//    public ImageLoader getImageLoader() {
//        getRequestQueue();
//        if (mImageLoader == null) {
//            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
//        }
//        return this.mImageLoader;
//    }
}