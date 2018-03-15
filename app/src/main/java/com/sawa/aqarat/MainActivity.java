package com.sawa.aqarat;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sawa.aqarat.ADS.Ads;
import com.sawa.aqarat.ADS.AlarmReceiver;
import com.sawa.aqarat.Activities.About_us_Act;
import com.sawa.aqarat.Activities.Admin_Messages_activ;
import com.sawa.aqarat.Activities.Change_Lang_Activity;
import com.sawa.aqarat.Activities.Change_Pass_Act;
import com.sawa.aqarat.Activities.Contact_US;
import com.sawa.aqarat.Activities.FAQ_question_Activity;
import com.sawa.aqarat.Activities.Login_Activity;
import com.sawa.aqarat.Activities.My_Interested_Activity;
import com.sawa.aqarat.Activities.My_Posts_Activ;
import com.sawa.aqarat.Activities.Notifications_History_Activ;
import com.sawa.aqarat.Activities.Premium_Request_Act;
import com.sawa.aqarat.Activities.Register_Activity;
import com.sawa.aqarat.Activities.Tickets.Tickets_All_Activ;
import com.sawa.aqarat.Activities.Update_Profile;
import com.sawa.aqarat.utilities.Check_Con;
import com.sawa.aqarat.utilities.SessionManager;
import com.sawa.aqarat.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

import static com.sawa.aqarat.utilities.Utility.Shape_Animation;


public class MainActivity extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;


    SweetAlertDialog dialog_bar;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ads ads=new Ads(this);
        ads.returnnum();
        View myView = findViewById(R.id.circle_shape_lay);
        View myView_bottom = findViewById(R.id.circle_shape_lay_bottom);
        TextView title_act_name = findViewById(R.id.title_act_name);
        title_act_name.setText(getResources().getText(R.string.home_str));
        Display display = getWindowManager().getDefaultDisplay();
        Shape_Animation(this, display, myView, myView_bottom, title_act_name);


        dialog_bar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sessionManager = new SessionManager(getApplicationContext());

        drawerLayout =  findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout =  findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });


        Nav_names();

        setActionBar();
        if (sessionManager.isLoggedIn()) {
            createMenuList_user();
        } else {
            createMenuList_Guest();
        }


        ContentMainFragment contentFragment = ContentMainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_overlay, contentFragment).commit();
        viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);


          /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);


      //  start();

        //  cancel();

        // startAt10();

    }

    private PendingIntent pendingIntent;

    public void cancel_AD() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pendingIntent);
            Log.d("ads", "ads stopped");
        }

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this , MyAdService.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        stopService(intent);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancel_AD();
    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 2; //1000 * 60 * 2
        if (manager != null) {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000 * 60 * 2, interval, pendingIntent);
            Log.d("ads", "ads started");
        }
    }


//    public void startAt10() {
//        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        int interval = 1000 * 60 * 20;
//
//        /* Set the alarm to start at 10:30 AM */
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 10);
//        calendar.set(Calendar.MINUTE, 30);
//
//        /* Repeating on every 20 minutes interval */
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                1000 * 60 * 20, pendingIntent);
//    }


    String CLOSE;
    String UPDATE_PROFILE;
    String CHANGE_LANGUAGE = "Change Language";
    String CHANGE_PASS = "Change Password";
    String MY_ADS = "My ADS";
    String MY_Interested = "My Interested";
    String BECOME_PREMUIM = "Become Premium";
    String TICKETS = "Tickets";
    String MY_NOTIFICATIONS = "My Notifications";
    String MY_MESSAGES = "My MESSAGES";
    String SHARE = "Share";
    String FAQ = "Faq";
    String CONTACT_US = "Contact US";
    String ABOUT_US = "About Us";
    String LOGOUT = "Logout";


    String LOGIN = "Login";
    String REGISTER = "Register";


    public void Nav_names() {
        CLOSE = getString(R.string.close_nav);
        UPDATE_PROFILE = getString(R.string.upd_prf_nav);
        CHANGE_LANGUAGE = getString(R.string.chng_lang_nav);
        CHANGE_PASS = getString(R.string.chng_pas_nav);
        MY_ADS = getString(R.string.my_ads_nav);
        MY_Interested = getString(R.string.my_nter_nav);
        BECOME_PREMUIM = getString(R.string.becom_prem_nav);
        TICKETS = getString(R.string.ticket_nav);
        MY_NOTIFICATIONS = getString(R.string.my_notify_nav);
        MY_MESSAGES = getString(R.string.my_messages_nav);
        SHARE = getString(R.string.shar_nav);
        FAQ = getString(R.string.faq_nav);
        CONTACT_US = getString(R.string.contact_us_nav);
        ABOUT_US = getString(R.string.about_us_nav);
        LOGOUT = getString(R.string.logout_nav);

        LOGIN = getString(R.string.login_nav);
        REGISTER = getString(R.string.register_nav);
    }


    private void createMenuList_user() {


        SlideMenuItem close_item = new SlideMenuItem(CLOSE, R.drawable.close_icon);
        list.add(close_item);
        SlideMenuItem upd_prof_item = new SlideMenuItem(UPDATE_PROFILE, R.drawable.profile_nav_icon);
        list.add(upd_prof_item);
        SlideMenuItem chng_lang_item = new SlideMenuItem(CHANGE_LANGUAGE, R.drawable.chng_lang_nav_icon);
        list.add(chng_lang_item);
        SlideMenuItem chng_pass_item = new SlideMenuItem(CHANGE_PASS, R.drawable.chng_pass_nav_icon);
        list.add(chng_pass_item);
        SlideMenuItem my_post_item = new SlideMenuItem(MY_ADS, R.drawable.my_ads_nav_icon);
        list.add(my_post_item);
        SlideMenuItem my_interest_item = new SlideMenuItem(MY_Interested, R.drawable.logout_nav_icon);
        list.add(my_interest_item);
        SlideMenuItem becam_prem_item = new SlideMenuItem(BECOME_PREMUIM, R.drawable.becam_prem_nav_icon);
        list.add(becam_prem_item);
        SlideMenuItem ticket_item = new SlideMenuItem(TICKETS, R.drawable.tickets_nav_icon);
        list.add(ticket_item);
        SlideMenuItem my_notify_item = new SlideMenuItem(MY_NOTIFICATIONS, R.drawable.notify_nav_icon);
        list.add(my_notify_item);
        SlideMenuItem my_messages_item = new SlideMenuItem(MY_MESSAGES, R.drawable.msgs_nav_icon);
        list.add(my_messages_item);
        SlideMenuItem share_item = new SlideMenuItem(SHARE, R.drawable.share_nav_icon);
        list.add(share_item);
        SlideMenuItem faq_item = new SlideMenuItem(FAQ, R.drawable.faq_nav_icon);
        list.add(faq_item);
        SlideMenuItem contct_us_item = new SlideMenuItem(CONTACT_US, R.drawable.contact_us_nav_icon);
        list.add(contct_us_item);
        SlideMenuItem abut_us_item = new SlideMenuItem(ABOUT_US, R.drawable.about_nav_icon);
        list.add(abut_us_item);
        SlideMenuItem logout_item = new SlideMenuItem(LOGOUT, R.drawable.logout_nav_icon);
        list.add(logout_item);
    }


    private void createMenuList_Guest() {

        SlideMenuItem close_item = new SlideMenuItem(CLOSE, R.drawable.close_icon);
        list.add(close_item);
        SlideMenuItem login_item = new SlideMenuItem(LOGIN, R.drawable.login_icon);
        list.add(login_item);
        SlideMenuItem register_item = new SlideMenuItem(REGISTER, R.drawable.reg_icon);
        list.add(register_item);
        SlideMenuItem chng_lang_item = new SlideMenuItem(CHANGE_LANGUAGE, R.drawable.chng_lang_nav_icon);
        list.add(chng_lang_item);
        SlideMenuItem share_item = new SlideMenuItem(SHARE, R.drawable.share_nav_icon);
        list.add(share_item);
        SlideMenuItem faq_item = new SlideMenuItem(FAQ, R.drawable.faq_nav_icon);
        list.add(faq_item);
        SlideMenuItem contct_us_item = new SlideMenuItem(CONTACT_US, R.drawable.contact_us_nav_icon);
        list.add(contct_us_item);
        SlideMenuItem abut_us_item = new SlideMenuItem(ABOUT_US, R.drawable.about_nav_icon);
        list.add(abut_us_item);
    }


    private void setActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition) {
        View view = findViewById(R.id.content_overlay);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();

        // here put intents

        ContentMainFragment contentFragment = ContentMainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_overlay, contentFragment).commit();
        return contentFragment;
    }


//     switch (slideMenuItem.getName()) {
//        case ContentMainFragment.CLOSE:
//            return screenShotable;
//        case Content
//        default:
//            return replaceFragment(screenShotable, position);
//    }


    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {

        Intent item_intent;
        if (slideMenuItem.getName().equals(CLOSE)) {
            return screenShotable;
        } else if (slideMenuItem.getName().equals(UPDATE_PROFILE)) {
            item_intent = new Intent(getApplicationContext(), Update_Profile.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(CHANGE_LANGUAGE)) {
            item_intent = new Intent(getApplicationContext(), Change_Lang_Activity.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(CHANGE_PASS)) {
            item_intent = new Intent(getApplicationContext(), Change_Pass_Act.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(MY_ADS)) {
            item_intent = new Intent(getApplicationContext(), My_Posts_Activ.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(MY_Interested)) {
            item_intent = new Intent(getApplicationContext(), My_Interested_Activity.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(BECOME_PREMUIM)) {
            item_intent = new Intent(getApplicationContext(), Premium_Request_Act.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(TICKETS)) {
            item_intent = new Intent(getApplicationContext(), Tickets_All_Activ.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(MY_NOTIFICATIONS)) {
            item_intent = new Intent(getApplicationContext(), Notifications_History_Activ.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(MY_MESSAGES)) {
            item_intent = new Intent(getApplicationContext(), Admin_Messages_activ.class);
            startActivity(item_intent);
        }else if (slideMenuItem.getName().equals(SHARE)) {

            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Matgar");
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.sawa4.matgar \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, getString(R.string.app_name)));
            } catch (Exception e) {
                //e.toString();
            }

        } else if (slideMenuItem.getName().equals(FAQ)) {
            item_intent = new Intent(getApplicationContext(), FAQ_question_Activity.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(CONTACT_US)) {
            item_intent = new Intent(getApplicationContext(), Contact_US.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(ABOUT_US)) {
            item_intent = new Intent(getApplicationContext(), About_us_Act.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(LOGOUT)) {

            if (Check_Con.getInstance(getApplicationContext()).isOnline()) {

                Log_Out_API();
                Utility.dialog_Show(dialog_bar, this);

            } else {
                Snackbar snackbar = Snackbar
                        .make(drawerLayout, R.string.no_connect, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.connect_snackbar, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });

                snackbar.show();

            }
        } else if (slideMenuItem.getName().equals(LOGIN)) {
            item_intent = new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(item_intent);
        } else if (slideMenuItem.getName().equals(REGISTER)) {
            item_intent = new Intent(getApplicationContext(), Register_Activity.class);
            startActivity(item_intent);
        } else {
            return replaceFragment(screenShotable, position);
        }

        return null;
    }


    public void Log_Out_API() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL + "users/logout",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("api", response);

                        try {

                            // JSON Object

                            JSONObject obj = new JSONObject(response);
                            Boolean status = obj.getBoolean("Status");
                            if (status) {

                                sessionManager.logoutUser();
                                Intent go_to_login = new Intent(getApplicationContext(), MainActivity.class);
                                go_to_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(go_to_login);

                                createMenuList_Guest();

                                Toast.makeText(getApplicationContext(), "Successfully logout", Toast.LENGTH_LONG).show();

                                // to clear all notifications
                                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (nMgr != null) {
                                    nMgr.cancelAll();
                                }

                                finish();

                                dialog_bar.dismiss();

                            } else {

                                StringBuilder s = new StringBuilder();
                                String street;
                                JSONArray st = obj.getJSONArray("Errors");
                                for (int i = 0; i < st.length(); i++) {
                                    street = st.getString(i);
                                    s.append(street);
                                    s.append("\n");
                                    Log.i("teeest", s.toString());
                                    // loop and add it to array or arraylist
                                }

                                Utility.dialog_error(dialog_bar, MainActivity.this, s.toString());

                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            // _errorMsg.setText(e.getMessage());

                            e.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_bar.dismiss();
                Toast.makeText(getApplicationContext(), R.string.server_down, Toast.LENGTH_LONG).show();
                Log.e("errror", error.getMessage() + "");

            }

        })

        {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("api_token", sessionManager.getUserDetails().get("Server_token"));
                params.put("device_token", sessionManager.getFirebase_Token());
                Log.e("Params", params.toString());

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(stringRequest);
    }


    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
