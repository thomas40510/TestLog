/*
 * Copyright (c) 2018 Thomas Prévost / CE Sauveterre. Distribué sous license libre.
 */

package com.apogee.dev.testlog;

import com.google.firebase.messaging.FirebaseMessagingService;

    public class MyInstanceIdService extends FirebaseMessagingService {

    /*@Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }*/

    @Override
    public void onNewToken(String token){
        //sendRegistrationTokenToServer(token);
    }
}
