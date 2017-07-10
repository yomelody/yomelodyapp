package com.instamelody.instamelody.Services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;
import static com.instamelody.instamelody.utils.Const.REGISTRATION_COMPLETE;
import static com.instamelody.instamelody.utils.Const.SHARED_PREF;

/**
 * Created by Shubahansh Jaiswal on 11/29/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //  Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        //  TODO: Implement this method to send any registration to your app's servers.
        //   sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        //  Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences fcmPref = getApplicationContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = fcmPref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}