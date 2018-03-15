package com.sawa.aqarat.utilities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.sawa.aqarat.R;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hu.aut.utillib.circular.animation.CircularAnimationUtils;


public class Utility {

   // toredo.redray.com.sa

    //192.168.1.14:81
    public static String URL ="https://aqarat.sawa4.com.eg/api/";


    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate Email with regular expression
     *
     * @ email
     * Return true for Valid Email and false for Invalid Email
     */

    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }



    public static boolean isValidMobile(String phone) {
        boolean check;
        check = !Pattern.matches("^\\+[0-9]{10,13}$", phone) && phone.length() > 5;
        return check;
    }
    /**
     * Checks for Null String object
     *
     * @ txt
     * return true for not null and false for null String object
     */


    public static boolean isNotNull(String txt){
        return txt != null && txt.trim().length() > 0;
    }
    public static boolean passLength(String s1) {
        if (s1.length()<5) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean userNameLength(String s1) {
        if (s1.length()>3) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isMatching(String s1, String s2) {
        if (s1.equals(s2)) {
            return true;
        } else {
            return false;
        }
    }




    private static final int MY_PERMISSIONS_REQUEST_Write_EXTERNAL_STORAGE = 108;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission_Write(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_Write_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_Write_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }




    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() !=null) {
            // or use  getWindow().getDecorView().getRootView().getWindowToken()
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    public static void setupUI(View view, final Activity activity) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView,activity);
            }
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;




    public static void Shape_Animation(Context context , Display display ,View myView , View myView_bottom , TextView title_act_name){


        int screenWidth;
        int screenHeight;


      //  DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
    //    windowManager.getDefaultDisplay().getMetrics(metrics);


        if (android.os.Build.VERSION.SDK_INT >= 17) {
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        } else {
            DisplayMetrics displaymetrics = new DisplayMetrics();
          //  getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            windowManager.getDefaultDisplay().getMetrics(displaymetrics);
            screenWidth = displaymetrics.widthPixels;
            screenHeight = displaymetrics.heightPixels;

//            screenWidth = display.getWidth();
//            screenHeight = display.getHeight();

        }


//        myView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myView.setVisibility(View.INVISIBLE);
//            }
//        });
//        fab = (ImageButton) findViewById(R.id.fab);
//
//        if (myView.getVisibility() == View.VISIBLE) {
//            return;
//        }

        //
        // Pre-calculations
        //
        // get the final radius for the clipping circle

        int[] myViewLocation = new int[2];
        myView.getLocationInWindow(myViewLocation);

        if (Locale.getDefault().getLanguage().equals("ar")){
            // myView.setRotation(180);
            myView.setScaleX(-1);
            myView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            title_act_name.setScaleX(-1);
            myView_bottom.setScaleX(-1);
        }

        float finalRadius = CircularAnimationUtils.hypo(screenWidth - myViewLocation[0], screenHeight - myViewLocation[1]);
        int[] center = CircularAnimationUtils.getCenter(myView, (View) myView.getParent());

        ObjectAnimator animator =
                CircularAnimationUtils.createCircularReveal(myView, center[0], center[1], 0, finalRadius);

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setStartDelay(500);
        animator.setDuration(1500);
        animator.start();





        myView_bottom.getLocationInWindow(myViewLocation);

        int[] center_bottom = CircularAnimationUtils.getCenter(myView_bottom, (View) myView_bottom.getParent());

        ObjectAnimator animator_bottom =
                CircularAnimationUtils.createCircularReveal(myView_bottom, center_bottom[0], center_bottom[1], 0, finalRadius);

        animator_bottom.setInterpolator(new AccelerateDecelerateInterpolator());
        animator_bottom.setStartDelay(1000);
        animator_bottom.setDuration(1000);
        animator_bottom.start();


    }



    public static void dialog_error(SweetAlertDialog dialog , Context context, String content){

        dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        dialog.setTitleText(context.getString(R.string.oops_str));
        dialog .setContentText(content);
        dialog.setConfirmText(context.getString(R.string.ok_dialog_str));
        dialog.setCancelText(context.getString(R.string.cancel_str));
        dialog.showCancelButton(false);
        dialog.setCancelClickListener(null);
        dialog.setCancelable(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        dialog.show();



    }

    public static void dialog_Show(final SweetAlertDialog dialog , final Context context){



        dialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

        dialog .setTitleText(context.getResources().getString(R.string.loading_str));
        dialog.setContentText(context.getResources().getString(R.string.plz_wait));
        dialog.setCancelable(false);
        dialog.showCancelButton(false);
        dialog.show();



    }

    public static void dialog_success(SweetAlertDialog dialog , Context context){


        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        dialog.getProgressHelper().setBarColor(ContextCompat.getColor(context , R.color.gray_btn_bg_pressed_color));
        dialog .setTitleText(context.getResources().getString(R.string.sucsess_dialog_str));
        dialog.setContentText("");
        dialog.setCancelable(true);
        dialog.showCancelButton(false);
        dialog.setCancelClickListener(null);
        dialog.setConfirmClickListener(null);
        dialog.show();


    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }




    private static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }



}
