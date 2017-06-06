/*
package com.instamelody.instamelody.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.HomeActivity;
import com.instamelody.instamelody.MainActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.app.Config;
import com.instamelody.instamelody.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Context context = this;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("fbs", "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d("fbs", "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d("fbs", "FCM Data Message: " + remoteMessage.getData());
        String chat_id = "";

        if (remoteMessage.equals(null))
            return;
        if (remoteMessage.getNotification() != null) { // Check if message contains a notification payload.
            Log.e("fbs", "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) { // Check if message contains a data payload.
            Log.e("fbs", "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e("fbs", "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {// app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e("fbs", "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            Log.e("fbs", "title: " + title);
            Log.e("fbs", "message: " + message);
            Log.e("fbs", "isBackground: " + isBackground);
            Log.e("fbs", "payload: " + payload.toString());
            Log.e("fbs", "imageUrl: " + imageUrl);
            Log.e("fbs", "timestamp: " + timestamp);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) { // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else { // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e("fbs", "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e("fbs", "Exception: " + e.getMessage());
        }
    }

    */
/**
     * Showing notification with text only
     *//*

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    */
/**
     * Showing notification with text and image
     *//*

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

        try {
        JSONObject json = new JSONObject(remoteMessage.getData().toString());
        JSONObject msgobj = json.getJSONObject("message");
        chat_id = msgobj.getString("chat_id");
    } catch (JSONException e) {
        e.printStackTrace();
    }

        Log.d("str", chat_id);
    Bundle bundle = new Bundle();
        bundle.putString("chat_id", chat_id);
    Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtras(bundle);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.instamelody_logo);
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new
            NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.instamelody_logo)
            .setLargeIcon(icon)
            .setContentTitle("InstaMelody")
            .setContentText(remoteMessage.getNotification().getBody())
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
//
//        SharedPreferences prefs = getSharedPreferences("ContactsData", MODE_PRIVATE);
//        String chatId = prefs.getString("chatId", null);
//        ChatActivity ca = new ChatActivity();
//        ca.getChatMsgs(chatId);
//
//        if (remoteMessage.getData().size() > 0) {
//            String str = remoteMessage.getData().toString();
//            Log.d("FirebaseText",str);
//        }
}
}


*/
