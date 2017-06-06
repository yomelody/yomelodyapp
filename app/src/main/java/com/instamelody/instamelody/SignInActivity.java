package com.instamelody.instamelody;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instamelody.instamelody.Models.HandelLogin;
import com.instamelody.instamelody.app.Config;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Configuration;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

import static android.R.attr.id;
import static android.R.attr.name;
import static com.instamelody.instamelody.R.drawable.twitter;

/**
 * Created by Saurabh Singh on 01/04/2017
 */
public class SignInActivity extends AppCompatActivity {

    String KEY_FNAME = "f_name";
    String KEY_LNAME = "l_name";
    String KEY_EMAIL_SIGN_UP = "email";
    String KEY_USERNAME = "username";
    String KEY_USER_TYPE = "usertype";
    String KEY_APP_ID = "appid";
    String KEY_GENDER = "gender";
    String KEY_DOB = "dob";
    String KEY_DEVICE_TOKEN_SIGN_UP = "device_token";
    String KEY_PROFILE_PIC = "profile_pic";


    String REGISTER_URL = "http://35.165.96.167/api/registration.php";

    String DeviceToken, f_name, l_name, userId, dob,fbProfilePic;
    TextView tvSettings, tvDone, tvSignUp, tvFirstName, tvUserName;
    String LOGIN_URL = "http://35.165.96.167/api/login.php";
    String KEY = "key";
    String KEY_EMAIL = "email";
    String KEY_PASSWORD = "password";
    String KEY_DEVICE_TOKEN = "devicetoken";
    String KEY_DEVICE_TYPE = "device_type";
    static String TWITTER_CONSUMER_KEY = "HPEUPWqatYYqdX2BXXZCwhRa3";
    static String TWITTER_CONSUMER_SECRET = "INlgRJqcVyxZe8tzfDhBZ0kYONTlWBY5NO8akXcnzVhERWL67I";
    int temp = 0;

    EditText etEmail, etPassword;
    TextView emailRequired, passwordRequired;
    //    String deviceToken;
//    String TestdeviceToken;
    ImageView ivuserimg;
    Button btnLogIn, btnClearEmail, btnClearPass;
    RelativeLayout btnfblogin, btnRlTwitterlogin;
    private CallbackManager mcallbckmanager;
    LoginButton fbloginbtn;
    String name, username, firstNamefb, lastNamefb, email, gender, birthday;
    String flag, user_id, First_name, Last_name, emailfinal, profilePic, coverPic, lastLogin, userName, fbId, fbEmail;
    HandelLogin obj = new HandelLogin();
    ProgressDialog progressDialog;

    private TwitterAuthClient client;
    private TwitterLoginButton twitterLoginButton;
    private boolean customButtonLogin;

    //this callback is the same for default and custom login metods
    private Callback<TwitterSession> authCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            String output = "Status: " +
                    "Your login was successful " +
                    result.data.getUserName() +
                    "Auth Token Received: " +
                    result.data.getAuthToken().token;

               /* Toast.makeText(SignInActivity.this, "" + output, Toast.LENGTH_LONG).show();*/
            //loginTwitter(result);
            TwitterSession session = result.data;
            Twitter twitter = Twitter.getInstance();
            TwitterApiClient api = twitter.core.getApiClient(session);
            AccountService service = api.getAccountService();
            Call<User> user = service.verifyCredentials(true, true);
            user.enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> userResult) {
                    name = userResult.data.name;
                    //tvFirstName.setText(name);
                    //tvFirstName.invalidate();
                    String email = userResult.data.email;
                    username = userResult.data.screenName;
                    //tvUserName.setText("@" + username);
                    //tvUserName.invalidate();
                    long id = userResult.data.id;
                    String photoUrlNormalSize = userResult.data.profileImageUrl;
                    // Picasso.with(SignInActivity.this).load(photoUrlNormalSize).into(ivuserimg);
                    //String photoUrlBiggerSize   = userResult.data.profileImageUrl.replace("_normal", "_bigger");
                    //String photoUrlMiniSize     = userResult.data.profileImageUrl.replace("_normal", "_mini");
                    //String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");

                       /* Toast.makeText(SignInActivity.this, "" + id, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + name, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + email, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + username, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + photoUrlNormalSize, Toast.LENGTH_LONG).show();*/

                    // _normal (48x48px) | _bigger (73x73px) | _mini (24x24px)

//                        Toast.makeText(SignInActivity.this, ""+photoUrlNormalSize, Toast.LENGTH_LONG).show();
//                        Toast.makeText(SignInActivity.this, ""+name, Toast.LENGTH_LONG).show();
//                        Toast.makeText(SignInActivity.this, ""+username, Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                    tEditor.putString("Name", name);
                    tEditor.putString("email", email);
                    tEditor.putString("userName", username);
                    tEditor.putString("ProfilePic", photoUrlNormalSize);
                    tEditor.putLong("ID", id);
                    tEditor.putInt("status", 1);
                    tEditor.commit();

                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                }

                @Override
                public void failure(TwitterException exc) {
                    Log.d("TwitterKit", "Verify Credentials Failure", exc);
                }
            });
        }

        @Override
        public void failure(TwitterException exception) {
            Log.d("Twitter Kit", "Login With Twitter", exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);

        final TextView tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        final TextView tvUserName = (TextView) findViewById(R.id.tvUserName);

        tvSettings = (TextView) findViewById(R.id.tvSettings);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        emailRequired = (TextView) findViewById(R.id.emailRequired);
        passwordRequired = (TextView) findViewById(R.id.passwordRequired);
        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        ivuserimg = (ImageView) findViewById(R.id.ivLogoContainer);
        btnClearEmail = (Button) findViewById(R.id.btnClearEmail);
        btnClearPass = (Button) findViewById(R.id.btnClearPass);
        btnfblogin = (RelativeLayout) findViewById(R.id.FbLogin);
        mcallbckmanager = CallbackManager.Factory.create();
        fbloginbtn = (LoginButton) findViewById(R.id.FbLoginReal);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailRequired.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordRequired.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        initCustomLogin();
//        initTwitterLogin();

        fbloginbtn.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends", "user_about_me"));

        fbloginbtn.registerCallback(mcallbckmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //      Toast.makeText(SignInActivity.this, ""+loginResult, Toast.LENGTH_SHORT).show();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),

                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //Toast.makeText(SignInActivity.this, "" + object, Toast.LENGTH_LONG).show();

                                try {
                                    fbId = object.getString("id");
                                    //Picasso.with(SignInActivity.this).load("https://graph.facebook.com/" + id + "/picture").into(ivuserimg);
                                    firstNamefb = object.getString("first_name");
                                    lastNamefb = object.getString("last_name");
                                    fbEmail = object.getString("email");
                                    //tvFirstName.setText(namefb);
                                    //tvUserName.setText("@" + namefb);
                                    gender = object.getString("gender");
                                    birthday = object.getString("birthday");
                                    String temp = object.getString("email");
                                    fbProfilePic = "https://graph.facebook.com/" + fbId + "/picture";
                                    username = temp.substring(0, temp.indexOf("@"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences.Editor fbEditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
                                fbEditor.putString("fbId", fbId);
                                fbEditor.putString("FbEmail", fbEmail);
                                fbEditor.putString("FbName", firstNamefb);
                                fbEditor.putString("FbLastName", lastNamefb);
                                fbEditor.putString("FbGender", gender);
                                fbEditor.putString("Birthday", birthday);
                                fbEditor.putString("UserName", username);
                                fbEditor.putInt("status", 1);
                                fbEditor.commit();

                                registerSpecial();
                                Intent i = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(i);

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                //   Toast.makeText(SignInActivity.this,fbImg, Toast.LENGTH_SHORT).show();
                request.executeAsync();

                //   Toast.makeText(SignInActivity.this, ""+id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, "LoginCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



       /* btnTwitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                String output = "Status: " +
                        "Your login was successful " +
                        result.data.getUserName() +
                        "Auth Token Received: " +
                        result.data.getAuthToken().token;

               *//* Toast.makeText(SignInActivity.this, "" + output, Toast.LENGTH_LONG).show();*//*
                //loginTwitter(result);
                TwitterSession session = result.data;
                Twitter twitter = Twitter.getInstance();
                TwitterApiClient api = twitter.core.getApiClient(session);
                AccountService service = api.getAccountService();
                Call<User> user = service.verifyCredentials(true, true);
                user.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> userResult) {
                        name = userResult.data.name;
                        //tvFirstName.setText(name);
                        //tvFirstName.invalidate();
                        String email = userResult.data.email;
                        username = userResult.data.screenName;
                        //tvUserName.setText("@" + username);
                        //tvUserName.invalidate();
                        long id = userResult.data.id;
                        String photoUrlNormalSize = userResult.data.profileImageUrl;
                        // Picasso.with(SignInActivity.this).load(photoUrlNormalSize).into(ivuserimg);
                        //String photoUrlBiggerSize   = userResult.data.profileImageUrl.replace("_normal", "_bigger");
                        //String photoUrlMiniSize     = userResult.data.profileImageUrl.replace("_normal", "_mini");
                        //String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");

                       *//* Toast.makeText(SignInActivity.this, "" + id, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + name, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + email, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + username, Toast.LENGTH_LONG).show();
                        Toast.makeText(SignInActivity.this, "" + photoUrlNormalSize, Toast.LENGTH_LONG).show();*//*

                        // _normal (48x48px) | _bigger (73x73px) | _mini (24x24px)

//                        Toast.makeText(SignInActivity.this, ""+photoUrlNormalSize, Toast.LENGTH_LONG).show();
//                        Toast.makeText(SignInActivity.this, ""+name, Toast.LENGTH_LONG).show();
//                        Toast.makeText(SignInActivity.this, ""+username, Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                        tEditor.putString("Name", name);
                        tEditor.putString("email", email);
                        tEditor.putString("userName", username);
                        tEditor.putString("ProfilePic", photoUrlNormalSize);
                        tEditor.putLong("ID", id);
                        tEditor.putInt("status", 1);
                        tEditor.commit();

                        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                    }

                    @Override
                    public void failure(TwitterException exc) {
                        Log.d("TwitterKit", "Verify Credentials Failure", exc);
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("Twitter Kit", "Login With Twitter", exception);
            }
        });
*//*
        if ((!etEmail.getText().toString().trim().equals("")) && (!etPassword.getText().toString().trim().equals(""))) {
//                    Toast.makeText(SignInActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
            emailRequired.setVisibility(View.VISIBLE);
            emailRequired.setText("required");
            btnLogIn.setEnabled(true);*/


        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((etEmail.getText().toString().trim().equals("")) && (etPassword.getText().toString().trim().equals(""))) {
                    emailRequired.setVisibility(View.VISIBLE);
                    emailRequired.setText("required");
                    passwordRequired.setVisibility(View.VISIBLE);
                    passwordRequired.setText("required");
                } else {
                    LogIn();
                }

            }
        });

        btnClearEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etEmail.getText().clear();

            }
        });

        btnClearPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etPassword.getText().clear();

            }
        });

        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnfblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_about_me"));
               /* LoginManager.getInstance().logOut();*/
            }
        });
    }

    private void initCustomLogin() {
        client = new TwitterAuthClient();

        ImageView customLoginButton = (ImageView) findViewById(R.id.ivTwitter);
        customLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.authorize(SignInActivity.this, authCallback);
                customButtonLogin = true;
            }
        });
    }

//    private void initTwitterLogin() {
//        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
//        twitterLoginButton.setCallback(authCallback);
//        customButtonLogin = false;
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (customButtonLogin) {
//            client.onActivityResult(requestCode, resultCode, data);
//            customButtonLogin = false;
//        } else if(!customButtonLogin) {
//            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
//            customButtonLogin = false;
//        } else{
//            if (mcallbckmanager.onActivityResult(requestCode, resultCode, data))
//                return;
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            client.onActivityResult(requestCode, resultCode, data);
        } else {
            if (mcallbckmanager.onActivityResult(requestCode, resultCode, data))
                return;
        }
    }

    private void LogIn() {

        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        DeviceToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences fcmPref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = fcmPref.edit();
        editor.putString("regId", DeviceToken);
        editor.commit();

        Log.d("DeviceToken", DeviceToken);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String successmsg = response.toString();

//                        Toast.makeText(SignInActivity.this, " Shubz" + successmsg, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(SignInActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                            }
                            JSONObject rspns = jsonObject.getJSONObject("response");
                            user_id = rspns.getString("user_id");
                            First_name = rspns.getString("First_name");
                            Last_name = rspns.getString("Last_name");
                            emailfinal = rspns.getString("email");
                            /*profilePic = "http://" + rspns.getString("profilepic");*/
                            String profPic1 = rspns.getString("profilepic");
                            profilePic = profPic1;

                            coverPic = rspns.getString("coverpic");
                            lastLogin = rspns.getString("lastlogin");
                            userName = rspns.getString("username");

                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                            editor.putString("userId", user_id);
                            editor.putString("firstName", First_name);
                            editor.putString("lastName", Last_name);
                            editor.putString("emailFinal", emailfinal);
                            editor.putString("profilePic", profilePic);
                            editor.putString("coverPic", coverPic);
                            editor.putString("lastLogin", lastLogin);
                            editor.putString("userName", userName);
                            editor.putInt("status", 1);
                            editor.commit();
                            obj.setId(1);
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("login_val", "1");
                            i.putExtras(bundle);
                            startActivity(i);

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
                        Toast.makeText(SignInActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY, "admin@123");
                params.put(KEY_EMAIL, email);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_DEVICE_TOKEN, DeviceToken);
                params.put(KEY_DEVICE_TYPE, "Android");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode, event);
    }


    public void registerSpecial() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response.toString();
                        Toast.makeText(SignInActivity.this, ""+successmsg, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            String flag = jsonObject.getString("flag");
                            JSONObject rspns = jsonObject.getJSONObject("response");
                            userId = rspns.getString("id");
                            username = rspns.getString("username");
                            f_name = rspns.getString("f_name");
                            l_name = rspns.getString("l_name");
                            email = rspns.getString("email");
//                            device_token = rspns.getString("device_token");
//                            profilepic = rspns.getString("profilepic");
//                            coverpic = rspns.getString("coverpic");
                            dob = rspns.getString("dob");

                            SharedPreferences.Editor fbEditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
//                            fbEditor.putString("fbId", fbId);
//                            fbEditor.putString("FbEmail", fbEmail);
//                            fbEditor.putString("FbName", f_name);
//                            fbEditor.putString("FbLastName", l_name);
//                            fbEditor.putString("FbGender", gender);
//                            fbEditor.putString("Birthday", dob);
//                            fbEditor.putString("UserName", username);
                            fbEditor.putString("userId", userId);
//                            fbEditor.putInt("status", 1);
                            fbEditor.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String error = e.toString();
                            Toast.makeText(SignInActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        String errormsg = error.toString();
                        Log.d("Error", errormsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_FNAME, firstNamefb);
                params.put(KEY_LNAME, lastNamefb);
                params.put(KEY_USERNAME, username);
                params.put(KEY_EMAIL_SIGN_UP, fbEmail);
                params.put(KEY_APP_ID, fbId);
                params.put(KEY_GENDER, gender);
                params.put(KEY_DOB, birthday);
                params.put(KEY_USER_TYPE, "2");
                params.put(KEY_DEVICE_TOKEN_SIGN_UP, DeviceToken);
                params.put(KEY_PROFILE_PIC,fbProfilePic);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}



