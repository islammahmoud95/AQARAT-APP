package com.sawa.aqarat.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sawa.aqarat.Activities.Admin_Messages_activ;
import com.sawa.aqarat.Activities.Genral_Notify_detail_activ;
import com.sawa.aqarat.Activities.Product_Details;
import com.sawa.aqarat.Activities.Tickets.Ticket_Details_Activ;
import com.sawa.aqarat.MainActivity;
import com.sawa.aqarat.R;
import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MyFirebaseMsgService";


    String id_detail;
    String messageBody, title, notifytype;   // , type ;
    Intent intent;
    JSONObject obj;


    /*
     1 => TICKETS.
     2 => ORDER DONE FOR USER [Tech. finish order].
     3 => ORDER DONE FOR SP.
     4 => your initial bill No.1 has been created, please review it. [NORMAL USER] [Number 2 in Doc].
     5 => you have a new order number 1, please review it and accept it through 5 minutes [Number 1 in Doc].
     6 => after provider ASSIGN ORDER TO technical [3 IN DOC SECTION 2] & after provider change the technical [NUMBER six IN DOCS].
     7 => AFTER USER RATING HIS ORDER NOTIFY SP HAS THIS ORDER [YOUR ORDER NO. 1 HAS BEEN RATING].
     Zero => GENERAL NOTIFICATION BACK-END [REDIRECT ORDER 0 [NO REDIRECTION]].
     8 => WHEN ORDER ACCEPTED VIA SP USER NOTIFY (YOUR ORDER HAS BEEN ACCEPTED).
     */


    SessionManager sessionManager;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "From: " + remoteMessage.getData());


        sessionManager = new SessionManager(getApplicationContext());


        try {
            obj = new JSONObject(remoteMessage.getData());
            Log.d("responsenoti", remoteMessage.getData().toString());


            title = obj.getString("title");
            messageBody = obj.getString("msg");
            notifytype = obj.getString("notifyType");
            id_detail = obj.getString("id");


            sendJobRequestNotification();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    // 1 comments
    //2 TICKETS
    //3 ADS (PRODUCTS)
    //4 ADMIN MESSAGES
    //5 GENERAL NOTIFY

    private void sendJobRequestNotification() {

        int num = (int) System.currentTimeMillis();

        //if request vip accepted
        // type 1 for tickets from admin response
        switch (notifytype) {
            case "1": {

                intent = new Intent(this, Product_Details.class);
                intent.putExtra("PRODUCT_ID", id_detail);

                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle(title);
                notificationBuilder.setContentText(messageBody);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple_color));
                } else {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                }
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSound(defaultSoundUri);
                notificationBuilder.setContentIntent(pendingIntent2);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(num /* ID of notification */, notificationBuilder.build());

                // for user to rate service after done from tech
                break;
            }
            case "2": {

                intent = new Intent(this, Ticket_Details_Activ.class);
                intent.putExtra("TICKET_ID", id_detail);


                PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0/* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle(title);
                notificationBuilder.setContentText(messageBody);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple_color));
                } else {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                }
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSound(defaultSoundUri);
                notificationBuilder.setContentIntent(pendingIntent2);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(num/* ID of notification */, notificationBuilder.build());

                // for provider to view order details
                break;
            }
            case "3": {

                intent = new Intent(this, Product_Details.class);
                intent.putExtra("PRODUCT_ID", id_detail);


                PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0/* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle(title);
                notificationBuilder.setContentText(messageBody);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple_color));
                } else {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                }
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSound(defaultSoundUri);
                notificationBuilder.setContentIntent(pendingIntent2);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(num/* ID of notification */, notificationBuilder.build());


                // for provider to view order details
                break;
            }
            case "4": {
                intent = new Intent(this, Admin_Messages_activ.class);

                PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0/* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle(title);
                notificationBuilder.setContentText(messageBody);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple_color));
                } else {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                }

                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSound(defaultSoundUri);
                notificationBuilder.setContentIntent(pendingIntent2);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(num/* ID of notification */, notificationBuilder.build());

                // sent to technical to notify him about receiving new order
                break;
            }
            case "5": {

                intent = new Intent(this, Genral_Notify_detail_activ.class);
                  intent.putExtra("GENERAL_ID", id_detail);


                PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0/* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle(title);
                notificationBuilder.setContentText(messageBody);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple_color));
                } else {
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                }

                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setSound(defaultSoundUri);
                notificationBuilder.setContentIntent(pendingIntent2);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(num/* ID of notification */, notificationBuilder.build());


                // sent to technical to notify him about receiving new order
                break;
            }
        }


        // check if app opened call counters in app and if closed not call it
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//            Intent intent = new Intent("MyData");
//            intent.putExtra("send_count", "counter");
//
//            broadcaster.sendBroadcast(intent);
//
//        }
    }

    //   private LocalBroadcastManager broadcaster;

//    @Override
//    public void onCreate() {
//        broadcaster = LocalBroadcastManager.getInstance(this);
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MyFirebaseMessagingService.class));
    }


//    // TODO(developer): Handle FCM messages here.
//    // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.e(TAG,"From: "+remoteMessage.getFrom());
//
//    // Check if message contains a data payload.
//        if(remoteMessage.getData().
//
//    size() >0)
//
//    {
//        Log.e(TAG, "Message data payload: " + remoteMessage.getData());
//
//
//    }


//    //new code --------------------------------------
//
//        Log.e(TAG,"From: "+remoteMessage.getFrom());
//
//        if(remoteMessage ==null)
//            return;
//
//    // Check if message contains a notification payload.
//        if(remoteMessage.getNotification()!=null)
//
//    {
//        Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//        handleNotification(remoteMessage.getNotification().getBody());
//    }
//
//    // Check if message contains a data payload.
//        if(remoteMessage.getData().
//
//    size() >0)
//
//    {
//        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
//
//        try {
//            JSONObject json = new JSONObject(remoteMessage.getData().toString());
//            handleDataMessage(json);
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }
//    }


    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
//        else{
//            // If the app is in background, firebase itself handles the notification
//        }
    }

    /**
     * Showing notification with text only
     */
//    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
//    }
//
//    /**
//     * Showing notification with text and image
//     */
//    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
//    }


}
