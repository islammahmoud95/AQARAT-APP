package com.sawa.aqarat.utilities;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    String userId;
    SessionManager sessionManager;


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);


    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.


        sessionManager = new SessionManager(getApplicationContext());

        //  sessionManager.checkLogin();

        if (sessionManager.isLoggedIn()) {

            HashMap<String, String> user = sessionManager.getUserDetails();
            userId = user.get("USER_ID");

            //  userId = sessionManager.getUserDetails().get("USER_ID");

            if (token != null) {
                token = FirebaseInstanceId.getInstance().getToken();
                sessionManager.setFirebase_Token(token);
                sendDeviceToken(token);
                Log.e(TAG, token + "");
            }


        }
        // sending gcm token to server
        //    Log.e(TAG, "sendRegistrationToServer: " + token);
    }


    private void sendDeviceToken(final String token) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.URL +
                sessionManager.getLang() + "/api/" + "users/updateDeviceToken",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error.getMessage()+"this error");


                    }

                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("server_token" ,  sessionManager.getUserDetails().get("Server_token"));
                params.put("firebase_token" ,  token);

                Log.e("deviceToken", params.toString());

                return params;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


}
