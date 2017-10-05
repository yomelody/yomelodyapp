package com.instamelody.instamelody;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Authentication;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.instamelody.instamelody.Models.HandelLogin;
import com.instamelody.instamelody.utils.AppHelper;
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
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

import static android.R.attr.id;
import static android.R.attr.theme;
import static com.instamelody.instamelody.utils.Const.SHARED_PREF;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.FORGOT_PASSWORD;
import static com.instamelody.instamelody.utils.Const.ServiceType.LOGIN;
import static com.instamelody.instamelody.utils.Const.ServiceType.REGISTER;

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
    String KEY_DEVICE_TYPE = "device_type";
    String KEY_PROFILE_PIC = "profile_pic_url";

    String DeviceToken = "";
    String f_name;
    String l_name;
    String userId;
    String dob, mobile;
    String fbProfilePic;
    String FbProf1;
    TextView tvSettings, tvDone, tvSignUp, tvFirstName, tvUserName;
    String KEY = "ApiAuthenticationKey";
    String KEY_EMAIL = "email";
    String KEY_PASSWORD = "password";
    String KEY_DEVICE_TOKEN = "devicetoken";
    static String TWITTER_CONSUMER_KEY = "HPEUPWqatYYqdX2BXXZCwhRa3";
    static String TWITTER_CONSUMER_SECRET = "INlgRJqcVyxZe8tzfDhBZ0kYONTlWBY5NO8akXcnzVhERWL67I";
    int temp = 0;

    EditText etEmail, etPassword;
    TextView emailRequired, passwordRequired, tvForgetPassword;
    //    String deviceToken;
//    String TestdeviceToken;
    ImageView ivuserimg;
    Button btnLogIn, btnClearEmail, btnClearPass;
    RelativeLayout btnfblogin, btnRlTwitterlogin, rlSoundCloud;
    private CallbackManager mcallbckmanager;
    LoginButton fbloginbtn;
    String name, username, firstNamefb, lastNamefb, email, gender, birthday;
    String flag, user_id, First_name, Last_name, emailfinal, profilePic, coverPic, lastLogin, userName, fbId, fbEmail;
    String fans, followers, records;
    HandelLogin obj = new HandelLogin();
    String photoUrlNormalSize;
    Long TwitterId;
    EditText subEtTopicName;
    private Lock lock;

    private TwitterAuthClient client;
    private TwitterLoginButton twitterLoginButton;
    private boolean customButtonLogin;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private boolean pendingPublishReauthorization = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);
        progressDialog = new ProgressDialog(SignInActivity.this);
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
        tvForgetPassword = (TextView) findViewById(R.id.tvForgetPassword);
        rlSoundCloud = (RelativeLayout) findViewById(R.id.rlSoundCloud);
        SharedPreferences fcmPref = getApplicationContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        DeviceToken = fcmPref.getString("regId", null);
//        Log.d("DeviceToken", DeviceToken);


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
                                Log.d("Check", "" + object);

                                try {
                                    fbId = object.getString("id");
                                    //Picasso.with(SignInActivity.this).load("https://graph.facebook.com/" + id + "/picture").into(ivuserimg);
                                    firstNamefb = object.getString("first_name");
                                    lastNamefb = object.getString("last_name");
                                    fbEmail = object.getString("email");
                                    //tvFirstName.setText(namefb);
                                    //tvUserName.setText("@" + namefb);
                                    gender = object.getString("gender");
                                    //birthday = object.getString("birthday");
                                    String temp = object.getString("email");
                                    fbProfilePic = "https://graph.facebook.com/" + fbId + "/picture";
                                    username = temp.substring(0, temp.indexOf("@"));
                                    String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Log.d("1", profilePicUrl);
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
                                fbEditor.putString("profilePic", fbProfilePic);
                                fbEditor.putInt("status", 1);
                                fbEditor.commit();
                                registerSpecialFB();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
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

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((etEmail.getText().toString().trim().equals("")) && (etPassword.getText().toString().trim().equals(""))) {
                    emailRequired.setVisibility(View.VISIBLE);
                    emailRequired.setText("required");
                    passwordRequired.setVisibility(View.VISIBLE);
                    passwordRequired.setText("required");
                } else {
                    btnLogIn.setEnabled(false);
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

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
                tvForgetPassword.setEnabled(false);
            }
        });

        rlSoundCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSoundCloud();
                //   startActivity(lock.newIntent(getApplicationContext()));
            }
        });


        Auth0 auth0 = new Auth0("rPAyruyB5UnHEbfVMERs2qbyt8KsBe_m", "codingbrains.auth0.com");
        auth0.setOIDCConformant(true);
        lock = Lock.newBuilder(auth0, callback)
                .withAudience("https://codingbrains.auth0.com/userinfo")
                // ... Options
                .build(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        lock.onDestroy(this);
        lock = null;
    }


    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            //Authenticated
            credentials.getIdToken();
        }

        @Override
        public void onCanceled() {
            //User pressed back

        }

        @Override
        public void onError(LockException error) {
            //Exception occurred
        }
    };

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


            TwitterSession session1 = Twitter.getSessionManager().getActiveSession();
            TwitterAuthToken twitterAuthToken = session1.getAuthToken();
            TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
            twitterAuthClient.requestEmail(session1, new Callback<String>() {
                @Override
                public void success(Result<String> result) {
                    Toast.makeText(SignInActivity.this, "" + result, Toast.LENGTH_LONG).show();
                    Log.d("TwitterEmail", result.toString());
                }

                @Override
                public void failure(TwitterException exception) {

                }
            });
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
                    TwitterId = userResult.data.id;
                    photoUrlNormalSize = userResult.data.profileImageUrl;

                    SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                    tEditor.putString("Name", name);
                    tEditor.putString("email", email);
                    tEditor.putString("userName", username);
                    tEditor.putString("profilePic", photoUrlNormalSize);
                    tEditor.putLong("ID", id);
                    tEditor.putInt("status", 1);
                    tEditor.commit();
                    registrationSpecialTwitter();
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
            if (exception.equals("com.twitter.sdk.android.core.TwitterAuthException: Failed to get request token")) {
                Toast.makeText(SignInActivity.this, "Twitter Login failed : Device time may not be same as server time", Toast.LENGTH_SHORT).show();
            }
        }
    };

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

        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String successmsg = response.toString();
                        //Toast.makeText(SignInActivity.this, "" + successmsg, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                btnLogIn.setEnabled(true);
                                Toast.makeText(SignInActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                etEmail.setText("");
                                etPassword.setText("");

                            } else {
                                JSONObject rspns = jsonObject.getJSONObject("response");
                                user_id = rspns.getString("user_id");
                                userName = rspns.getString("username");
                                First_name = rspns.getString("First_name");
                                Last_name = rspns.getString("Last_name");
                                emailfinal = rspns.getString("email");
                                profilePic = rspns.getString("profilepic");
                                coverPic = rspns.getString("coverpic");
                                followers = rspns.getString("followers");
                                fans = rspns.getString("fans");
                                records = rspns.getString("records");
                                dob = rspns.getString("dob");
                                mobile = rspns.getString("mobile");

                                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                                editor.putString("userId", user_id);
                                editor.putString("userName", userName);
                                editor.putString("firstName", First_name);
                                editor.putString("lastName", Last_name);
                                editor.putString("emailFinal", emailfinal);
                                editor.putString("profilePic", profilePic);
                                editor.putString("coverPic", coverPic);
                                editor.putString("followers", followers);
                                editor.putString("fans", fans);
                                editor.putString("records", records);
                                editor.putString("dob", dob);
                                editor.putString("mobile", mobile);
                                editor.putInt("status", 1);
                                editor.commit();

                                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnLogIn.setEnabled(true);
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
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_EMAIL, email);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_DEVICE_TOKEN, DeviceToken);
                params.put(KEY_DEVICE_TYPE, "Android");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("param===" + params + "\nURL==" + LOGIN);
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void registerSpecialFB() {
        SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
        FbProf1 = fbPref.getString("profilePic", null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response.toString();
//                        Toast.makeText(SignInActivity.this, ""+successmsg, Toast.LENGTH_SHORT).show();
                        Log.d("print", successmsg);

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                JSONObject rspns = jsonObject.getJSONObject("response");
                                user_id = rspns.getString("id");
                                userName = rspns.getString("username");
                                First_name = rspns.getString("f_name");
                                Last_name = rspns.getString("l_name");
                                emailfinal = rspns.getString("email");
                                profilePic = rspns.getString("profilepic");
//                                coverPic = rspns.getString("coverpic");
                                followers = rspns.getString("followers");
                                fans = rspns.getString("fans");
                                records = rspns.getString("records");
//                                user_id = rspns.getString("dob");
//                                user_id = rspns.getString("device_token");
//                                user_id = rspns.getString("discrisption");
//                                user_id = rspns.getString("mobile");
//                                user_id = rspns.getString("device_type");

                                SharedPreferences.Editor fbEditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
                                fbEditor.putString("userId", user_id);
                                fbEditor.putString("firstName", First_name);
                                fbEditor.putString("lastName", Last_name);
                                fbEditor.putString("emailFinal", emailfinal);
                                fbEditor.putString("profilePic", profilePic);
                                fbEditor.putString("coverPic", coverPic);
//                                fbEditor.putString("lastLogin", lastLogin);
                                fbEditor.putString("userName", userName);
                                fbEditor.putString("fans", fans);
                                fbEditor.putString("followers", followers);
                                fbEditor.putString("records", records);
                                fbEditor.putInt("status", 1);
                                fbEditor.commit();
                            }

                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
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
                if (fbEmail == null) {
                    fbEmail = "amol@codingbrains.com";
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_FNAME, firstNamefb);
                params.put(KEY_LNAME, lastNamefb);
                params.put(KEY_USERNAME, firstNamefb + lastNamefb);
                params.put(KEY_EMAIL_SIGN_UP, fbEmail);
                params.put(KEY_APP_ID, fbId);
                params.put(KEY_USER_TYPE, "2");
                params.put(KEY_DEVICE_TOKEN_SIGN_UP, DeviceToken);
                params.put(KEY_DEVICE_TYPE, "android");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void registrationSpecialTwitter() {
        SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        photoUrlNormalSize = twitterPref.getString("profilePic", null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response.toString();
//                        Toast.makeText(SignInActivity.this, ""+successmsg, Toast.LENGTH_SHORT).show();
                        Log.d("print", successmsg);

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                JSONObject rspns = jsonObject.getJSONObject("response");
                                user_id = rspns.getString("id");
                                userName = rspns.getString("username");
                                First_name = rspns.getString("f_name");
                                Last_name = rspns.getString("l_name");
                                emailfinal = rspns.getString("email");
                                profilePic = rspns.getString("profilepic");
                                coverPic = rspns.getString("coverpic");
                                followers = rspns.getString("followers");
                                fans = rspns.getString("fans");
                                records = rspns.getString("records");
//                                user_id = rspns.getString("dob");
//                                user_id = rspns.getString("device_token");
//                                user_id = rspns.getString("discrisption");
//                                user_id = rspns.getString("mobile");
//                                user_id = rspns.getString("device_type");

                                SharedPreferences.Editor twitterEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                                twitterEditor.putString("userId", user_id);
                                twitterEditor.putString("firstName", First_name);
                                twitterEditor.putString("lastName", Last_name);
                                twitterEditor.putString("emailFinal", emailfinal);
                                twitterEditor.putString("profilePic", profilePic);
                                twitterEditor.putString("coverPic", coverPic);
                                twitterEditor.putString("userName", userName);
//                                twitterEditor.putString("lastLogin", lastLogin);
//                                twitterEditor.putString("userName", userName);
                                twitterEditor.putString("fans", fans);
                                twitterEditor.putString("followers", followers);
                                twitterEditor.putString("records", records);
                                twitterEditor.putInt("status", 1);
                                twitterEditor.commit();
                            }
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

                params.put(KEY_FNAME, name);
                params.put(KEY_LNAME, name);
                params.put(KEY_USERNAME, username);
                params.put(KEY_EMAIL_SIGN_UP, "codingbrains17@gmail.com");
                params.put(KEY_APP_ID, String.valueOf(TwitterId));
                params.put(KEY_USER_TYPE, "3");
                params.put(KEY_DEVICE_TOKEN_SIGN_UP, DeviceToken);
                params.put(KEY_DEVICE_TYPE, "android");
                params.put(KEY_PROFILE_PIC, photoUrlNormalSize);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout_password, null);

        subEtTopicName = (EditText) subView.findViewById(R.id.etForgetPassword);


        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
//        builder.setView(sp);


        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder.setTitle("Forget Password");
        builder.setMessage("Enter your email for Password");
        builder.setView(subView);

        TextView title = new TextView(this);
        title.setText("Forget Password");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder.setCustomTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                if (checkEmailValidity()) {
                    forgotPassword();
                }else {
                    tvForgetPassword.setEnabled(true);
                    Toast.makeText(SignInActivity.this, "Required Email", Toast.LENGTH_SHORT).show();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtTopicName.getWindowToken(), 0);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtTopicName.getWindowToken(), 0);
            }
        });

        builder.show();

    }

    private void forgotPassword() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FORGOT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String successmsg = response.toString();
                        tvForgetPassword.setEnabled(true);
                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            flag = jsonObject.getString("flag");
                            String password = jsonObject.getString("password");
                            if (flag.equals("Unsuccess")) {
                                tvForgetPassword.setEnabled(true);
                                Toast.makeText(SignInActivity.this, "Unregistered Email", Toast.LENGTH_SHORT).show();
                                tvForgetPassword.setEnabled(true);
                            } else {
                                Toast.makeText(SignInActivity.this, "The Link has been sent to your email address.", Toast.LENGTH_SHORT).show();
                            }
                            JSONObject rspns = jsonObject.getJSONObject("response");

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
                params.put(KEY_EMAIL, subEtTopicName.getText().toString().trim());
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loginSoundCloud() {
        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .start(SignInActivity.this, new AuthCallback() {
                    @Override
                    public void onFailure(@NonNull Dialog dialog) {
                        // Show error Dialog to user
                    }

                    @Override
                    public void onFailure(AuthenticationException exception) {
                        // Show error to user
                    }

                    @Override
                    public void onSuccess(@NonNull Credentials credentials) {
                        // Store credentials
                        String idToken = credentials.getIdToken();
                        String accessToken = credentials.getAccessToken();
                        SharedPreferences.Editor soundCloudCredentialE = getApplicationContext().getSharedPreferences("SoundCloudCred", MODE_PRIVATE).edit();
                        soundCloudCredentialE.putString("idToken", idToken);
                        soundCloudCredentialE.putString("accessToken", accessToken);
                        soundCloudCredentialE.apply();
                        // Navigate to your main activity
                    }
                });

    }

    private boolean checkEmailValidity() {
        boolean chk = true;
        if (!isValidMail(subEtTopicName.getText().toString().trim())|| subEtTopicName.getText().toString().trim().equals("")) {
            chk = false;
        }
        return chk;
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}



