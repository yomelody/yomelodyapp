package com.yomelody.Services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.yomelody.HomeActivity;
import com.yomelody.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.LOGOUT;

/**
 * Created by ADMIN on 11-12-2017.
 */

public class LogoutService extends Service {
    public static CountDownTimer timer;
    String userId;
    String USER_ID = "user_id";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }

        timer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Some code
                Log.v("LogOutService:", "Service Started");
            }

            public void onFinish() {
                try {
                    Log.v("LogOutService:", "Call Logout by Service");
                    // Code for Logout
                    clearPreff();
                    LoginManager.getInstance().logOut();
                    logOut();
                    stopSelf();
                    Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(mIntent);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }.start();

    }
//    @Override
//    public void onStart(Intent intent, int startid) {
//        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        //timer = new CountDownTimer(1 *60 * 1000, 1000) {
//            timer = new CountDownTimer(30000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                //Some code
//                Log.v("LogOutService:", "Service Started");
//            }
//
//            public void onFinish() {
//                Log.v("LogOutService:", "Call Logout by Service");
//                // Code for Logout
//                clearPreff();
//                LoginManager.getInstance().logOut();
//                logOut();
//                stopSelf();
//            }
//        };
//    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void clearPreff(){
        SharedPreferences.Editor editor = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        SharedPreferences.Editor tEditor = getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
        tEditor.clear();
        tEditor.commit();
        SharedPreferences.Editor fbeditor = getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
        fbeditor.clear();
        fbeditor.commit();
        SharedPreferences.Editor profileEditor1 = getSharedPreferences("ProfileUpdate", MODE_PRIVATE).edit();
        profileEditor1.clear();
        profileEditor1.commit();
        SharedPreferences.Editor profileImageEditor1 = getSharedPreferences("ProfileImage", MODE_PRIVATE).edit();
        profileImageEditor1.clear();
        profileImageEditor1.commit();

        SharedPreferences.Editor socialStatusPreff = getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE).edit();
        socialStatusPreff.clear();
        socialStatusPreff.commit();
    }
    public void logOut() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String successmsg = response.toString();
                        //Toast.makeText(HomeActivity.this, "" + successmsg, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            String flag = jsonObject.getString("flag");
                            String msg = jsonObject.getString("msg");
                            //Toast.makeText(HomeActivity.this, "" + flag, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "Parse error";
                        } else if (error == null) {

                        }

                        if (!errorMsg.equals("")) {
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                            error.printStackTrace();
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put(USER_ID, userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
