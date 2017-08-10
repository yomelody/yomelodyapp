package com.instamelody.instamelody;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.net.Network;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ADMIN on 6/8/2017.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context,Intent arg1){
        boolean isConnected=arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(isConnected){
            Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();

            //showSnack(true);
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }
        else {

            Toast.makeText(context, am.getClass().toString(), Toast.LENGTH_LONG).show();
            //showSnack(false);
            Intent i = new Intent(context, am.getClass());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        }

    }
    public void CheckConnection(boolean connect) {

        if (connect) {

        }
    }

}
