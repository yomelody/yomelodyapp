package com.instamelody.instamelody.Services;

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
import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.HomeActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StationActivity;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.instamelody.instamelody.utils.Const.PUSH_NOTIFICATION;
import static com.instamelody.instamelody.utils.Const.READ_NOTIFICATION;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    NotificationUtils notificationUtils;
    String flagSoundPlayedAlready = "false";
    public static final int ID_SMALL_NOTIFICATION = 235;
    String notificationType="";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("fbs", "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d("fbs", "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d("fbs", "FCM Data Message: " + remoteMessage.getData());
        String chat_id = "";
        if (remoteMessage == null)
            return;


        if (remoteMessage.getNotification() != null) { // Check if message contains a notification payload.
            Log.e("fbs", "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
            /*if (remoteMessage.getData()!=null && remoteMessage.getData().containsKey("notification_type")){
                notificationType = remoteMessage.getData().get("notification_type");
            }

            try {
                AppHelper.sop("remoteMessage==="+notificationType);
                if (!TextUtils.isEmpty(notificationType) && notificationType.equalsIgnoreCase("Activity")){
                    // like comment and share notification case
                    AppHelper.sop("remoteMessage=if=="+notificationType);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("notification_data",remoteMessage.getData().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    showSmallNotification(remoteMessage.getNotification().getTitle(),
                            remoteMessage.getNotification().getBody(),intent);
                }else {
                    handleNotification(remoteMessage.getNotification().getBody());
                    AppHelper.sop("remoteMessage=else=="+notificationType);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }*/

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
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
            flagSoundPlayedAlready = "true";
            AppHelper.sop("handleNotification=if==="+message);


        } else {
            // If the app is in background, firebase itself handles the notification
            AppHelper.sop("handleNotification=else==="+message);
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e("fbs", "push json: " + json.toString());
        AppHelper.sop("handleDataMessage==="+json);
        try {
            JSONObject data = new JSONObject(json.toString());
            JSONObject body = data.getJSONObject("body");
            if (body.has("status")) {
                String status = body.getString("status");
                String chatId = body.getString("chatID");
                Log.e("fbs", "status: " + status);
                Log.e("fbs", "chatId: " + chatId);
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) { // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(READ_NOTIFICATION);
                    pushNotification.putExtra("status", status);
                    pushNotification.putExtra("chatId", chatId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                } else {
                    // app is in background, do nothing
                }
            } else {
                String message = body.getString("message");
                String senderId = body.getString("senderid");
                String senderName = body.getString("sender_name");
                String chatId = body.getString("chat_id");
                String fileUrl = "", title = "", fileId = "";
                if (body.has("file_url")) {
                    fileUrl = body.getString("file_url");
                }
                if (body.has("title")) {
                    title = body.getString("title");
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
                    Intent pushNotification = new Intent(PUSH_NOTIFICATION);
                    pushNotification.putExtra("chatId", chatId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    // play notification sound
                    if (flagSoundPlayedAlready.equals("false")) {
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                    }
                } else { // app is in background, show the notification in notification tray
                    Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    resultIntent.putExtra("sender_name", senderName);
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("chat_id", chatId);

                    if (TextUtils.isEmpty(fileUrl)) { // check for image attachment
                        showNotificationMessage(getApplicationContext(), title, senderId, senderName, message, chatId, /*file_id,*/ resultIntent);
                    } else if (TextUtils.isEmpty(fileId)) {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, senderId, senderName, message, chatId, /*file_id,*/ resultIntent, fileUrl);
                    } else { // image is present, show notification with image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, senderId, senderName, message, chatId, /*file_id,*/ resultIntent, fileUrl);
                    }
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
                PendingIntent.getActivity(getApplicationContext(), ID_SMALL_NOTIFICATION, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
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
//        notificationManager.cancelAll();
        AppHelper.sop("showSmallNotification==call");
    }
}