package com.instamelody.instamelody;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.instamelody.instamelody.utils.Const;
import com.instamelody.instamelody.utils.NotificationUtils;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.utils.Const.PUSH_NOTIFICATION;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.LOGOUT;
import static com.instamelody.instamelody.utils.Const.ServiceType.TOTAL_COUNT;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class HomeActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "HPEUPWqatYYqdX2BXXZCwhRa3";
    private static final String TWITTER_SECRET = "INlgRJqcVyxZe8tzfDhBZ0kYONTlWBY5NO8akXcnzVhERWL67I";
    public static final int MY_PERMISSIONS_REQUEST_MICROPHONE = 200;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 201;
    String KEY_FNAME = "f_name";
    String KEY_LNAME = "l_name";
    String KEY_EMAIL = "email";
    String KEY_USERNAME = "username";
    String KEY_USER_TYPE = "usertype";
    String KEY_APP_ID = "appid";
    String KEY_DEVICE_TOKEN = "device_token";
    String REGISTER_URL = "http://35.165.96.167/api/registration.php";
    String USER_ID = "user_id";

    SignUpActivity obj = new SignUpActivity();
    Button Settings;
    static Button SignIn;
    static Button SignOut;
    ImageView ivStation;
    ImageView ivStudio;
    ImageView ivMelody;
    ImageView ivProfile;
    RelativeLayout rlMessenger;
    Bitmap bitmap;
    public static HomeActivity fa;
    TextView tvFirstName, tvUserName, message_count;
    String Name, userName, profilePic, fbEmail, profilepic2, fbFirstName, fbUserName, fbLastName, fbProfilePic, name2, userName2, galleryPrfPic, fbId;
    String firstName, lastName, userNameLogin, profilePicLogin, userIdNormal;
    int statusNormal, statusFb, statusTwitter;
    CircleImageView userProfileImage;
    int totalCount = 0;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String joinRecordingId;

    @TargetApi(16)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        System.gc();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_home);
        if (checkPermissions()) {

        } else {
            setPermissions();
        }

        rlMessenger = (RelativeLayout) findViewById(R.id.rlMessenger);
        Settings = (Button) findViewById(R.id.btn_settings);
        SignIn = (Button) findViewById(R.id.btn_sign_in);
        SignOut = (Button) findViewById(R.id.btn_sign_out);
        ivStation = (ImageView) findViewById(R.id.ivStation);
        ivStudio = (ImageView) findViewById(R.id.ivStudio);
        ivMelody = (ImageView) findViewById(R.id.ivMelody);
        ivProfile = (ImageView) findViewById(R.id.ivLogoContainer);
        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        message_count = (TextView) findViewById(R.id.message_count);
        userProfileImage = (CircleImageView) findViewById(R.id.userProfileImage);

        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        firstName = loginSharedPref.getString("firstName", null);
        lastName = loginSharedPref.getString("lastName", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        userIdNormal = loginSharedPref.getString("userId", null);
        statusNormal = loginSharedPref.getInt("status", 0);

        getTotalCount();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    getTotalCount();
                }
            }
        };

        if (statusNormal == 1) {
            SignOut.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.INVISIBLE);
            tvFirstName.setText(firstName + " " + lastName);
            tvUserName.setText("@" + userNameLogin);
        }

        if (profilePicLogin != null) {
            ivProfile.setVisibility(View.GONE);
            userProfileImage.setVisibility(View.VISIBLE);
            userProfileImage.setDrawingCacheEnabled(true);
            Picasso.with(HomeActivity.this).load(profilePicLogin).into(userProfileImage);
        }

        SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        Name = twitterPref.getString("Name", null);
        userName = twitterPref.getString("userName", null);
        profilePic = twitterPref.getString("profilePic", null);
        statusTwitter = twitterPref.getInt("status", 0);

        if (statusTwitter == 1) {
            SignOut.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.INVISIBLE);
            tvFirstName.setText(Name);
            tvUserName.setText("@" + userName);
        }

        if (profilePic != null) {
            ivProfile.setVisibility(View.GONE);
            userProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(HomeActivity.this).load(profilePic).into(userProfileImage);
        }

        SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
        fbFirstName = fbPref.getString("FbName", null);
        fbLastName = fbPref.getString("FbLastName", null);
        fbId = fbPref.getString("fbId", null);
        fbUserName = fbPref.getString("UserName", null);
        statusFb = fbPref.getInt("status", 0);
        fbEmail = fbPref.getString("FbEmail", null);

//        registerSpecial();

        if (statusFb == 1) {
            SignOut.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.INVISIBLE);
            String fullName = fbFirstName + " " + fbLastName;
            tvFirstName.setText(fullName);
            tvUserName.setText("@" + fbFirstName);
        }
        if (fbId != null) {
            ivProfile.setVisibility(View.GONE);
            userProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(HomeActivity.this).load("https://graph.facebook.com/" + fbId + "/picture").into(userProfileImage);
        }

        SharedPreferences profileEditor = getApplicationContext().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
        SharedPreferences profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE);
        if (profileImageEditor.getString("ProfileImage", null) != null) {
            ivProfile.setVisibility(View.GONE);
            userProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(HomeActivity.this).load(profileImageEditor.getString("ProfileImage", null)).into(userProfileImage);
        }
        if (profileEditor.getString("updateId", null) != null) {
            SignOut.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.INVISIBLE);
            tvFirstName.setText(profileEditor.getString("updateFirstName", null) + " " + profileEditor.getString("updateLastName", null));
            tvUserName.setText("@" + profileEditor.getString("updateUserName", null));
        }


        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                LoginManager.getInstance().logOut();
                SignOut.setVisibility(View.INVISIBLE);
                SignIn.setVisibility(View.VISIBLE);
                logOut();
                HomeActivity.this.recreate();
            }
        });

        ivStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (StudioActivity.melodyPackId != null) {
                        StudioActivity.melodyPackId = null;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                try {
                    SharedPreferences fromHome = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE);
                    String home = fromHome.getString("click", null);
                    if (home != null) {
                        SharedPreferences.Editor FilterPref1 = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE).edit();
                        FilterPref1.clear();
                        FilterPref1.apply();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(getApplicationContext(), StationActivity.class);
                startActivity(intent);
            }
        });

        ivStudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences filterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE);
                    joinRecordingId = filterPref.getString("instrumentsPos", null);
                    if (joinRecordingId != null) {
                        SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
                        FilterPref.clear();
                        FilterPref.apply();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    if (StudioActivity.instrumentList != null) {
                        StudioActivity.instrumentList.clear();
                    }
                    if (StudioActivity.joinRecordingId != null) {
                        SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
                        FilterPref.remove("instrumentsPos");
                        FilterPref.apply();
                    }
                    if (StudioActivity.melodyPackId != null) {
                        StudioActivity.melodyPackId = null;
                    }
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("HomeStudio", MODE_PRIVATE).edit();
                    editor.putString("clickFromSt", "from home");
                    editor.commit();

                    if (checkPermissions()) {
                        Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                        //  intent.putExtra("clickPosition", "fromHomeActivity");
                        startActivity(intent);
                    } else {
                        setPermissions();
                    }

                } else {
                    Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                    //   intent.putExtra("clickPosition", "fromHomeActivity");
                    startActivity(intent);
                }


            }
        });

        ivMelody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences filterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE);
                    joinRecordingId = filterPref.getString("instrumentsPos", null);
                    if (joinRecordingId != null) {
                        SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
                        FilterPref.clear();
                        FilterPref.apply();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE).edit();
                editor.putString("click", "from home");
                editor.commit();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ||
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

                    if (checkPermissions()) {
                        Intent intent = new Intent(getApplicationContext(), MelodyActivity.class);
                        startActivity(intent);
                    } else {
                        setPermissions();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), MelodyActivity.class);
                    startActivity(intent);
                }
            }
        });

        rlMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (StudioActivity.melodyPackId != null) {
                        StudioActivity.melodyPackId = null;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);

            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (StudioActivity.melodyPackId != null) {
                        StudioActivity.melodyPackId = null;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });


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
                params.put(USER_ID, userIdNormal);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getTotalCount() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TOTAL_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(HomeActivity.this, "" + response.toString();, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                String str = jsonObject.getString("newMessage");
                                totalCount = Integer.parseInt(str);
                                if (totalCount > 0) {
                                    message_count.setText(str);
                                    message_count.setVisibility(View.VISIBLE);
                                } else {
                                    message_count.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "";
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            errorMsg = "There is either no connection or it timed out.";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "ServerError";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "Network Error";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put("userid", userIdNormal);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @TargetApi(17)
    public boolean checkPermissions() {
        if ((ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

            return true;
        } else {
            return false;
        }
    }

    @TargetApi(17)
    public void setPermissions() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_MICROPHONE);
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_MICROPHONE);
            }
        }

        if (ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_MICROPHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    checkPermissions();
                    //    mRecordingThread.stopRecording();
                }
                break;

            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    checkPermissions();

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTotalCount();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}

