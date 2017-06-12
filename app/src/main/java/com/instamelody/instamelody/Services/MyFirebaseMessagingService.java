package com.instamelody.instamelody.Services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.app.Config;
import com.instamelody.instamelody.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    NotificationUtils notificationUtils;
    String flagSoundPlayedAlready = "false";

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

    private void handleNotification(String result) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {// app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            String message = "";
//            String senderName = "";
            String chatId = "";
            try {
                JSONObject json = new JSONObject(result);
//                message = json.getString("message");
//                senderName = json.getString("sender_name");
                chatId = json.getString("chat_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            pushNotification.putExtra("message", message);
//            pushNotification.putExtra("senderName",senderName);
            pushNotification.putExtra("chatId", chatId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//            play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
            flagSoundPlayedAlready = "true";
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e("fbs", "push json: " + json.toString());
        try {
            JSONObject bodyObj = json.getJSONObject("body");
////            String file_id = messageObj.getString("file_id");
//            String senderid = messageObj.getString("senderid");
//            String sender_name = messageObj.getString("sender_name");
//            String message = messageObj.getString("message");
//            String chat_id = messageObj.getString("chat_id");
//            String title = "One Message Received";
//            String imageUrl = messageObj.getString("image");
//            String file_id = json.getString("file_id");

<<<<<<< HEAD
//            String file_id = json.getString("file_id");
            String senderid = json.getString("senderid");
            String sender_name = json.getString("sender_name");
            String message = json.getString("message");
            String chat_id = json.getString("chat_id");
=======
            String senderid = bodyObj.getString("senderid");
            String sender_name = bodyObj.getString("sender_name");
            String message = bodyObj.getString("message");
            String chat_id = bodyObj.getString("chat_id");
>>>>>>> a3144c396752e9ff44531aff5fd3f07c14e9d135
            String title = "One Message Received";
//            String imageUrl = json.getString("image");
            String imageUrl = "https://vignette2.wikia.nocookie.net/kochikame/images/0/0a/Ryotsu_%28manga_-_colour%29.jpg/revision/latest?cb=20140814094000";

//            Log.e("fbs", "file_id: " + file_id);
            Log.e("fbs", "sender_name " + sender_name);
            Log.e("fbs", "senderid: " + senderid);
            Log.e("fbs", "message: " + message);
            Log.e("fbs", "chat_id: " + chat_id);
            Log.e("fbs", "image: " + imageUrl);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) { // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                if (flagSoundPlayedAlready.equals("false")) {
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                }
            } else { // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                resultIntent.putExtra("sender_name", sender_name);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("chat_id", chat_id);
                if (TextUtils.isEmpty(imageUrl)) { // check for image attachment
                    showNotificationMessage(getApplicationContext(), title, senderid, sender_name, message, chat_id, /*file_id,*/ resultIntent);
                } else { // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, senderid, sender_name, message, chat_id, /*file_id,*/ resultIntent, imageUrl);
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
}

/*   try
    {
        JSONObject json = new JSONObject(remoteMessage.getData().toString());
        JSONObject msgobj = json.getJSONObject("message");
        chat_id = msgobj.getString("chat_id");
    } catch(
    JSONException e)

    {
        e.printStackTrace();
    }

        Log.d("str",chat_id);
    Bundle bundle = new Bundle();
        bundle.putString("chat_id",chat_id);
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
        notificationManager.notify(0,notificationBuilder.build());
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
}*/


