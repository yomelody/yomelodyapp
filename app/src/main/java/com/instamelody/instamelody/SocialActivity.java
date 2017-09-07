package com.instamelody.instamelody;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;
import com.github.gorbin.asne.twitter.TwitterSocialNetwork;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.jlubecki.soundcloud.webapi.android.auth.AuthenticationCallback;
import com.jlubecki.soundcloud.webapi.android.auth.SoundCloudAuthenticator;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

import static android.R.attr.data;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class SocialActivity extends AppCompatActivity {

    public static Switch switchFb, switchTwitter, switchSoundCloud, switchGoogle;
    String fetchRecordingUrl, fetchThumbNailUrl;
    CallbackManager callbackManager;

    static String TWITTER_CONSUMER_KEY = "HPEUPWqatYYqdX2BXXZCwhRa3";
    static String TWITTER_CONSUMER_SECRET = "INlgRJqcVyxZe8tzfDhBZ0kYONTlWBY5NO8akXcnzVhERWL67I";
    TwitterAuthClient client;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    SignInButton googleSignIn;
    PlusOneButton plus_one_button;
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    String cover;
    TextView tvDone,tvFirstNameSocial,tvUserNameSocial;
    String userId, firstName, lastName, userNameLogin, profilePicLogin;
    String userIdTwitter, firstNameTwitter, lastNameTwitter, emailFinalTwitter, profilePicTwitter, userNameTwitter;
    String userIdFb, firstNameFb, lastNameFb, userNameFb;
    int statusNormal;
    CircleImageView userProfileImageSocial;
    ImageView ivLogoContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends"));


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // connection failed, should be handled
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        switchFb = (Switch) findViewById(R.id.switchFb);
        switchTwitter = (Switch) findViewById(R.id.switchTwitter);
        switchSoundCloud = (Switch) findViewById(R.id.switchSoundCloud);
        switchGoogle = (Switch) findViewById(R.id.switchGoogle);
        googleSignIn = (SignInButton) findViewById(R.id.googleSignIn);
        SharedPreferences loginSharedPref1 = this.getSharedPreferences("Url_recording", MODE_PRIVATE);
        fetchRecordingUrl = loginSharedPref1.getString("Recording_url", null);
        SharedPreferences editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        fetchThumbNailUrl = editorT.getString("thumbnailUrl", null);
        plus_one_button = (PlusOneButton) findViewById(R.id.plus_one_button);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvFirstNameSocial = (TextView) findViewById(R.id.tvFirstNameSocial);
        tvUserNameSocial = (TextView) findViewById(R.id.tvUserNameSocial);
        userProfileImageSocial = (CircleImageView) findViewById(R.id.userProfileImageSocial);
        ivLogoContainer = (ImageView) findViewById(R.id.ivLogoContainer);


        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userId = loginSharedPref.getString("userId", null);
        firstName = loginSharedPref.getString("firstName", null);
        lastName = loginSharedPref.getString("lastName", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        statusNormal = loginSharedPref.getInt("status", 0);

//        seekBarDisc = (SeekBar)findViewById(R.id.seekBarDisc);

        SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        SharedPreferences profileEditor = getApplicationContext().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
        SharedPreferences profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE);
        if (profileImageEditor.getString("ProfileImage",null) != null){
            ivLogoContainer.setVisibility(View.GONE);
            userProfileImageSocial.setVisibility(View.VISIBLE);
            Picasso.with(SocialActivity.this).load(profileImageEditor.getString("ProfileImage",null)).into(userProfileImageSocial);
        }
        if (profileEditor.getString("updateId",null) != null){
            tvFirstNameSocial.setText(profileEditor.getString("updateFirstName", null) + " "+profileEditor.getString("updateLastName",null));
            tvUserNameSocial.setText("@" +profileEditor.getString("updateUserName",null));
        }

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }

        SharedPreferences twitterEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        userIdTwitter = twitterEditor.getString("userId", null);
        firstNameTwitter = twitterEditor.getString("firstName", null);
        lastNameTwitter = twitterEditor.getString("lastName", null);
        userNameTwitter = twitterEditor.getString("userName", null);
        emailFinalTwitter = twitterEditor.getString("emailFinal", null);
        profilePicTwitter = twitterEditor.getString("profilePic", null);


        SharedPreferences fbEditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
        userIdFb = fbEditor.getString("userId", null);
        firstNameFb = fbEditor.getString("firstName", null);
        lastNameFb = fbEditor.getString("lastName", null);
        userNameFb = fbEditor.getString("userName", null);


        SharedPreferences editor1 = this.getSharedPreferences("commentData1", MODE_PRIVATE);
        cover = editor1.getString("cover", null);

        if (statusNormal == 1) {
            tvFirstNameSocial.setText(firstName + " " + lastName);
            tvUserNameSocial.setText("@" + userNameLogin);
        }

        if (profilePicLogin != null) {
            ivLogoContainer.setVisibility(View.GONE);
            userProfileImageSocial.setVisibility(View.VISIBLE);
            Picasso.with(SocialActivity.this).load(profilePicLogin).into(userProfileImageSocial);

        }

        if (userIdFb != null) {
            tvFirstNameSocial.setText(firstNameFb + "  " + lastNameFb);
            tvUserNameSocial.setText("@"+ userNameFb);
//                SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
            String fbId = fbPref.getString("fbId", null);
            ivLogoContainer.setVisibility(View.GONE);
            userProfileImageSocial.setVisibility(View.VISIBLE);
            Picasso.with(SocialActivity.this).load("https://graph.facebook.com/" + fbId + "/picture").into(userProfileImageSocial);
        } else if (userIdTwitter != null) {
            tvFirstNameSocial.setText(firstNameTwitter + "  " + lastNameTwitter);
            tvUserNameSocial.setText("@"+ userNameTwitter);
//                SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
            String profilePic1 = twitterPref.getString("profilePic", null);
            ivLogoContainer.setVisibility(View.GONE);
            userProfileImageSocial.setVisibility(View.VISIBLE);
            Picasso.with(SocialActivity.this).load(profilePic1).into(userProfileImageSocial);
        }


        switchFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchFb.isChecked()) {
                    SharedPreferences.Editor switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatusFb", MODE_PRIVATE).edit();
                    switchFbEditor.putBoolean("switchFb", true);
                    switchFbEditor.apply();
                } else {
                    SharedPreferences.Editor switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatusFb", MODE_PRIVATE).edit();
                    switchFbEditor.putBoolean("switchFb", false);
                    switchFbEditor.apply();
                }
            }
        });

        switchTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchTwitter.isChecked()) {
                    SharedPreferences.Editor switchTwitterEditor = getApplicationContext().getSharedPreferences("SwitchStatusTwitter", MODE_PRIVATE).edit();
                    switchTwitterEditor.putBoolean("switchTwitter", true);
                    switchTwitterEditor.apply();
                } else {
                    SharedPreferences.Editor switchTwitterEditor = getApplicationContext().getSharedPreferences("SwitchStatusTwitter", MODE_PRIVATE).edit();
                    switchTwitterEditor.putBoolean("switchTwitter", false);
                    switchTwitterEditor.apply();
                }
            }
        });

        switchSoundCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchSoundCloud.isChecked()) {
                    SharedPreferences.Editor switchSoundCloudEditor = getApplicationContext().getSharedPreferences("SwitchStatusSoundCloud", MODE_PRIVATE).edit();
                    switchSoundCloudEditor.putBoolean("switchSoundCloud", true);
                    switchSoundCloudEditor.apply();
                } else {
                    SharedPreferences.Editor switchSoundCloudEditor = getApplicationContext().getSharedPreferences("SwitchStatusSoundCloud", MODE_PRIVATE).edit();
                    switchSoundCloudEditor.putBoolean("switchSoundCloud", false);
                    switchSoundCloudEditor.apply();
                }
                if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "Do recordings to Share", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                    intent.putExtra("clickPosition", "fromSocialActivity");
                    startActivity(intent);
                    switchSoundCloud.setEnabled(false);
                } else {
                    Intent intent = new Intent("com.soundcloud.android.SHARE")
                            .putExtra(Intent.EXTRA_STREAM, Uri.parse(fetchRecordingUrl))
                            .putExtra("com.soundcloud.android.extra.title", "Demo");
                    // more metadata can be set, see below

                    try {
                        // takes the user to the SoundCloud sharing screen
                        startActivityForResult(intent, 0);
                    } catch (ActivityNotFoundException e) {
                        // SoundCloud Android app not installed, show a dialog etc.
                    }
                }
            }
        });

        switchGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "Do recordings to Share", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                    intent.putExtra("clickPosition", "fromSocialActivity");
                    startActivity(intent);
                    switchGoogle.setEnabled(false);
                } else {
                    /*plus_one_button.setVisibility(View.VISIBLE);
                    plus_one_button.setEnabled(true);
                    plus_one_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent shareIntent = new PlusShare.Builder(SocialActivity.this)
                                    .setType("text/plain")
                                    .setText("Welcome to the Google+ platform.")
                                    .setContentUrl(Uri.parse(fetchThumbNailUrl))
                                    .getIntent();
                            startActivityForResult(shareIntent, 0);
                        }
                    });*/
                }
                if (switchGoogle.isChecked()) {
                    SharedPreferences.Editor switchGoogleEditor = getApplicationContext().getSharedPreferences("SwitchStatusGoogle", MODE_PRIVATE).edit();
                    switchGoogleEditor.putBoolean("switchGoogle", true);
                    switchGoogleEditor.apply();
                } else {
                    SharedPreferences.Editor switchGoogleEditor = getApplicationContext().getSharedPreferences("SwitchStatusGoogle", MODE_PRIVATE).edit();
                    switchGoogleEditor.putBoolean("switchGoogle", false);
                    switchGoogleEditor.apply();
                }
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SocialActivity.this,SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            //  twitter related handling
            client.onActivityResult(requestCode, resultCode, data);
            switchTwitter.setChecked(false);
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            plus_one_button.setVisibility(View.GONE);
            switchGoogle.setChecked(false);
//            handleSignInResult(result);
        } else {
            if (callbackManager.onActivityResult(requestCode, resultCode, data))
                switchFb.setChecked(false);
            return;
        }
    }


    protected void onResume() {
        super.onResume();
        SharedPreferences switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatusFb", MODE_PRIVATE);
        switchFb.setChecked(switchFbEditor.getBoolean("switchFb", false));
        SharedPreferences switchTwitterEditor = getApplicationContext().getSharedPreferences("SwitchStatusTwitter", MODE_PRIVATE);
        switchTwitter.setChecked(switchTwitterEditor.getBoolean("switchTwitter", false));
        SharedPreferences switchSoundCloudEditor = getApplicationContext().getSharedPreferences("SwitchStatusSoundCloud", MODE_PRIVATE);
        switchSoundCloud.setChecked(switchSoundCloudEditor.getBoolean("switchSoundCloud", false));
        SharedPreferences switchGoogleEditor = getApplicationContext().getSharedPreferences("SwitchStatusGoogle", MODE_PRIVATE);
        switchGoogle.setChecked(switchGoogleEditor.getBoolean("switchGoogle", false));
        plus_one_button.initialize(fetchThumbNailUrl, PLUS_ONE_REQUEST_CODE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (switchFb.isChecked()) {
            switchFb.setChecked(true);
        }
        if (switchTwitter.isChecked()) {
            switchTwitter.setChecked(true);
        }
        if (switchSoundCloud.isChecked()) {
            switchSoundCloud.setChecked(true);
        }
        if (switchGoogle.isChecked()) {
            switchGoogle.setChecked(true);
        }
    }
}
