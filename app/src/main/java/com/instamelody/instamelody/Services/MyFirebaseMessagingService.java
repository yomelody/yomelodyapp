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
import android.support.v4.app.NotificationCompat;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.HomeActivity;
import com.instamelody.instamelody.MainActivity;
import com.instamelody.instamelody.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Context context = this;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.instamelody_logo);

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

        SharedPreferences prefs = getSharedPreferences("ContactsData", MODE_PRIVATE);
        String chatId = prefs.getString("chatId", null);
        ChatActivity ca = new ChatActivity();
        ca.getChatMsgs(chatId);

        if (remoteMessage.getData().size() > 0) {
            String str = remoteMessage.getData().toString();
            Log.d("FirebaseText",str);
        }
    }
}


