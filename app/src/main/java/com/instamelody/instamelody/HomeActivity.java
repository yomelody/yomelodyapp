package com.instamelody.instamelody;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;
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

import static com.instamelody.instamelody.utils.Const.PUSH_NOTIFICATION;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.LOGOUT;

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
    int count = 0;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @TargetApi(16)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
//        HandelLogin obj = new HandelLogin();

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


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push notification is received
                    count = count + 1;
                    message_count.setVisibility(View.VISIBLE);
                    message_count.setText(count);
                    SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                    editor.putString("messageCount", String.valueOf(count));
                    editor.apply();
                }
            }
        };

//        Bitmap bitmap = getIntent().getParcelableExtra("BitmapImage");
//        Toast.makeText(this, ""+bitmap, Toast.LENGTH_LONG).show();
//        ivProfile.setImageBitmap(bitmap);

//        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin",MODE_PRIVATE);
//        firstName = loginSharedPref.getString("firstName",null);
//        if()
//        {
//
//        }

        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        firstName = loginSharedPref.getString("firstName", null);
        lastName = loginSharedPref.getString("lastName", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        userIdNormal = loginSharedPref.getString("userId", null);
        statusNormal = loginSharedPref.getInt("status", 0);


        SharedPreferences profileEditor = getApplicationContext().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
        SharedPreferences profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE);
        if (profileEditor.getString("updateId", null) != null) {
            if (profileImageEditor.getString("ProfileImage", null) != null) {
                ivProfile.setVisibility(View.GONE);
                userProfileImage.setVisibility(View.VISIBLE);
                userProfileImage.setDrawingCacheEnabled(true);
//            Picasso.with(HomeActivity.this).load(profileImageEditor.getString("ProfileImage", null)).into(userProfileImage);
                Picasso.with(HomeActivity.this).load(profileImageEditor.getString("ProfileImage", null)).into(userProfileImage);
            }
            SignOut.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.INVISIBLE);
            tvFirstName.setText(profileEditor.getString("updateFirstName", null) + " " + profileEditor.getString("updateLastName", null));
            tvUserName.setText("@" + profileEditor.getString("updateUserName", null));
        }


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
            final AtomicBoolean loaded = new AtomicBoolean();
            Picasso.with(HomeActivity.this).load(profilePicLogin).into(userProfileImage);
//            Picasso.with(HomeActivity.this)
//                    .load(profilePicLogin)
//                  .placeholder(Your Drawable Resource)   this is optional the image to display while the url image is downloading
//                  .error(Your Drawable Resource)         this is also optional if some error has occurred in downloading the image this image would be displayed
//                    .into(userProfileImage, new Callback.EmptyCallback() {
//                        @Override
//                        public void onSuccess() {
//                            loaded.set(true);
//                        }
//
//                        @Override
//                        public void onError() {
//                            super.onError();
//                        }
//                    });
//            if (loaded.get()) {
            // The image was immediately available.
//                userProfileImage.buildDrawingCache();
//                Bitmap bitmap = userProfileImage.getDrawingCache();
//                userProfileImage.setImageBitmap(bitmap);
//            }
        }

        //Toast.makeText(this, "" + firstName, Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "" + userNameLogin, Toast.LENGTH_SHORT).show();
        //tvFirstName.invalidate();
        //tvUserName.invalidate();

        /*SharedPreferences sharedPreferences2 = this.getSharedPreferences("uploading image response", MODE_PRIVATE);
        name2 = sharedPreferences2.getString("fname", null);
        userName2 = sharedPreferences2.getString("userName1", null);
        galleryPrfPic = sharedPreferences2.getString("picGallery", null);
        tvFirstName.setText(name2);
        tvUserName.setText(userName2);

        if (galleryPrfPic != null) {
            ivProfile.setVisibility(View.GONE);
            userProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(HomeActivity.this).load(galleryPrfPic).into(userProfileImage);
        }*/

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
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                tEditor.clear();
                tEditor.apply();
                SharedPreferences.Editor fbeditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
                fbeditor.clear();
                fbeditor.apply();
                SharedPreferences.Editor profileEditor = getApplicationContext().getSharedPreferences("ProfileUpdate", MODE_PRIVATE).edit();
                profileEditor.clear();
                profileEditor.apply();
                SharedPreferences.Editor profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE).edit();
                profileImageEditor.clear();
                profileImageEditor.apply();
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

                Intent intent = new Intent(getApplicationContext(), StationActivity.class);
                startActivity(intent);
            }
        });

        ivStudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

                    if (checkPermissions()) {
                        Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                        intent.putExtra("clickPosition", "fromHomeActivity");
                        startActivity(intent);
                    } else {
                        setPermissions();
                    }

                } else {
                    Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                    intent.putExtra("clickPosition", "fromHomeActivity");
                    startActivity(intent);
                }


            }
        });

        ivMelody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

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

                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);

            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });
    }

    public void displayExceptionMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    public void logOut() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String successmsg = response.toString();
                        Toast.makeText(HomeActivity.this, "" + successmsg, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            String flag = jsonObject.getString("flag");
                            String msg = jsonObject.getString("msg");
                            Toast.makeText(HomeActivity.this, "" + flag, Toast.LENGTH_SHORT).show();

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
                params.put(USER_ID, userIdNormal);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

//    public void registerSpecial(String firstName, String lastName, String userName, String email, String appId, String userType) {

       /* public void registerSpecial() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response.toString();
                        Toast.makeText(HomeActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Toast.makeText(HomeActivity.this, successmsg, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            String flag = jsonObject.getString("flag");
                            JSONObject rspns = jsonObject.getJSONObject("response");
                            fbId = rspns.getString("id");

                            profilepic2 = rspns.getString("profilepic");


                        } catch (JSONException e) {
                            e.printStackTrace();
                            String error = e.toString();
                            Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        String errormsg = error.toString();
                        Log.d("Error", errormsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_FNAME, fbFirstName);
                params.put(KEY_LNAME, fbLastName);
                params.put(KEY_USERNAME, fbUserName);
                params.put(KEY_EMAIL, fbEmail);
                params.put(KEY_APP_ID, fbId);
                params.put(KEY_USER_TYPE, "2");
                params.put(KEY_DEVICE_TOKEN,"xyz");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/

    @TargetApi(17)
    public boolean checkPermissions() {
        if ((ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}

