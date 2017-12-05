package com.yomelody.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yomelody.MessengerActivity;
import com.yomelody.R;
import com.yomelody.utils.Const;
import com.yomelody.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.yomelody.utils.Const.PUSH_NOTIFICATION;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    NotificationUtils notificationUtils;
    String flagSoundPlayedAlready = "false";
    public static final int ID_SMALL_NOTIFICATION = 235;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("fbs", "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d("fbs", "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d("fbs", "FCM Data Message: " + remoteMessage.getData());
        String chat_id = "";

        if (remoteMessage.getData().size() > 0) { // Check if message contains a data payload.
            Log.e("fbs", "Data Payload: " + remoteMessage.getData().toString());
            try {
                //   JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Map<String, String> data = remoteMessage.getData();
                Log.d("FCM data playload mes", data.toString());
                String res = data.get("body");
                String click_action = data.get("click_action");
                String title = data.get("title");
                handleDataMessage(res, title);
            } catch (Exception e) {
                Log.e("fbs", "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        try {
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {// app is in foreground, broadcast the push message
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
                flagSoundPlayedAlready = "true";

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void handleDataMessage(String response, String title) {

        try {

            JSONObject body = new JSONObject(response);

            String message = body.getString("message");
            String senderId = body.getString("senderid");
            String senderName = body.getString("sender_name");
            String chatId = body.getString("chat_id");
            String fileUrl = "", fileId = "";
            if (body.has("file_url")) {
                fileUrl = body.getString("file_url");
            }

            if (body.has("file_id")) {
                fileId = body.getString("file_id");
            }
            Log.e("fbs", "message: " + message);
            Log.e("fbs", "file_id: " + fileId);
            Log.e("fbs", "senderId: " + senderId);
            Log.e("fbs", "senderName " + senderName);
            Log.e("fbs", "chatId: " + chatId);
            Log.e("fbs", "fileUrl: " + fileUrl);
            Log.e("fbs", "title: " + title);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) { // app is in foreground, broadcast the push message

                Intent pushNotification = new Intent(Const.PUSH_NOTIFICATION);
                pushNotification.putExtra("chatId", chatId);
                pushNotification.putExtra("sender_name", senderName);
                try {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // play notification sound
                if (flagSoundPlayedAlready.equals("false")) {
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                }

            } else { // app is in background, show the notification in notification tray
                try {
                    Intent resultIntent = new Intent(getApplicationContext(), MessengerActivity.class);
//                    resultIntent.putExtra("sender_name", senderName);
//                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("chat_id", chatId);

                    if (TextUtils.isEmpty(fileUrl)) { // check for image attachment
                        handleNotification(message);
                        showNotificationMessage(getApplicationContext(), title, senderId, senderName, message, chatId, /*file_id,*/ resultIntent);
                    } else if (TextUtils.isEmpty(fileId)) {
                        handleNotification(message);
                        showNotificationMessageWithBigImage(getApplicationContext(), title, senderId, senderName, message, chatId, /*file_id,*/ resultIntent, fileUrl);
                    } else { // image is present, show notification with image
                        handleNotification(message);
                        showNotificationMessageWithBigImage(getApplicationContext(), title, senderId, senderName, message, chatId, /*file_id,*/ resultIntent, fileUrl);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (JSONException e) {
            Log.e("fbs", "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e("fbs", "Exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String senderId, String senderName, String message, String chatId, /*String fileId,*/ Intent intent) { //Showing notification with text only
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(senderId, title, senderName, message, chatId, /*fileId,*/ intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String senderId, String senderName, String message, String chatId, /*String fileId,*/ Intent intent, String imageUrl) { //Showing notification with text and image
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(senderId, title, senderName, message, chatId, /*fileId,*/ intent, imageUrl);
    }

    public void showSmallNotification(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, ID_SMALL_NOTIFICATION, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.instamelody_logo).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.instamelody_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.instamelody_logo))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
        notificationManager.cancelAll();
    }
}