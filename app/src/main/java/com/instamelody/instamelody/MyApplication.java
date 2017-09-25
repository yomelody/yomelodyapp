package com.instamelody.instamelody;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by acer on 9/25/2017.
 */

public class MyApplication extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);
        MultiDex.install(newBase);
    }
}
