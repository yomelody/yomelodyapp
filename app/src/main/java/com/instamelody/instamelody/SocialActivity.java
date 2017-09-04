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
import android.widget.Switch;
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
    ShareDialog shareDialog;
    URL ShortUrl;
    String ShortUrlId;
    static String TWITTER_CONSUMER_KEY = "HPEUPWqatYYqdX2BXXZCwhRa3";
    static String TWITTER_CONSUMER_SECRET = "INlgRJqcVyxZe8tzfDhBZ0kYONTlWBY5NO8akXcnzVhERWL67I";
    TwitterAuthClient client;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    SignInButton googleSignIn;
    PlusOneButton plus_one_button;
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    String cover;
    Bitmap bitmap;
    SocialNetworkManager commonShare;
    public static final int TWITTER = TwitterSocialNetwork.ID;
    public static final int FACEBOOK = FacebookSocialNetwork.ID;
    public static final int GOOGLE_PLUS = GooglePlusSocialNetwork.ID;
    /*String TWITTER_CONSUMER_KEY = "HPEUPWqatYYqdX2BXXZCwhRa3";
    String TWITTER_CONSUMER_SECRET = getApplicationContext().getString(R.string.TWITTER_CONSUMER_SECRET);
    String TWITTER_CALLBACK_URL = "oauth://ASNE";*/
    int switchBtn = 0;


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


//        mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);

        switchFb = (Switch) findViewById(R.id.switchFb);
        switchTwitter = (Switch) findViewById(R.id.switchTwitter);
        switchSoundCloud = (Switch) findViewById(R.id.switchSoundCloud);
        switchGoogle = (Switch) findViewById(R.id.switchGoogle);
        googleSignIn = (SignInButton) findViewById(R.id.googleSignIn);
//        commonShare =(SocialNetworkManager) getFragmentManager().findFragmentByTag()
        SharedPreferences loginSharedPref1 = this.getSharedPreferences("Url_recording", MODE_PRIVATE);
        fetchRecordingUrl = loginSharedPref1.getString("Recording_url", null);

        SharedPreferences editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        fetchThumbNailUrl = editorT.getString("thumbnailUrl", null);

//        new newShortAsync().execute();

        plus_one_button = (PlusOneButton) findViewById(R.id.plus_one_button);
//        new URLShort().execute();


        /*SharedPreferences editor = this.getSharedPreferences("commentData", MODE_PRIVATE);
        cover = editor.getString("bitmapCover", null);*/


        SharedPreferences editor1 = this.getSharedPreferences("commentData1", MODE_PRIVATE);
        cover = editor1.getString("cover", null);


        switchFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchFb.isChecked()) {
//                    switchFb.setChecked(true);
                    switchBtn = 1;
                    SharedPreferences.Editor switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                    switchFbEditor.putInt("switch", switchBtn);
                    switchFbEditor.apply();
                }
                /*if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "Do recordings to Share", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                    intent.putExtra("clickPosition", "fromSocialActivity");
                    startActivity(intent);
                    switchFb.setEnabled(false);
                }*/ /*else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SocialActivity.this);
                    builder1.setMessage("Wants to share InstaMelody music on facebook??");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FbShare();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    switchFb.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }*/
            }
        });

        switchTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchTwitter.isChecked()) {
//                    switchTwitter.setChecked(true);
                    switchBtn = 2;
                    SharedPreferences.Editor switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                    switchFbEditor.putInt("switch", switchBtn);
                    switchFbEditor.apply();

                }
                /*if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "Do recordings to Share", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                    intent.putExtra("clickPosition", "fromSocialActivity");
                    startActivity(intent);
                    switchTwitter.setEnabled(false);
                }*/ /*else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SocialActivity.this);
                    builder1.setMessage("Wants to share InstaMelody music on twitter??");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                new newShortAsync().execute();
                                    TweetShare();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    switchTwitter.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }*/

            }
        });

        switchSoundCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*boolean isAppInstalled = appInstalledOrNot("com.check.application");

                if(isAppInstalled) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getPackageManager()
                            .getLaunchIntentForPackage("com.check.application");
                    startActivity(LaunchIntent);
                } else {
                    // Do whatever we want to do if application not installed
                    // For example, Redirect to play store

                    Toast.makeText(SocialActivity.this, "Install  SoundCloud Application from PlayStore to SHARE", Toast.LENGTH_SHORT).show();
                }*/
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
                    plus_one_button.setVisibility(View.VISIBLE);
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
                    });
                }

            }
        });

        if ((switchTwitter.isChecked() && switchFb.isChecked())) {
//            switchTwitter.setChecked(true);
//            switchFb.setChecked(true);
            switchBtn = 3;
            SharedPreferences.Editor switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
            switchFbEditor.putInt("switch", switchBtn);
            switchFbEditor.apply();
        }
    }

    public void FbShare() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(SocialActivity.this, "Recording Uploaded", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                editorT.clear();
                editorT.apply();
            }

            @Override
            public void onCancel() {

                Toast.makeText(SocialActivity.this, "Recording not Uploaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

            }

        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(fetchThumbNailUrl))
                    .setImageUrl(Uri.parse(cover))
                    .build();
            shareDialog.show(linkContent, ShareDialog.Mode.FEED);
        }
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
        /*Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    public void TweetShare() {

        try {
            ShortUrl = new URL(fetchThumbNailUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        /*Bundle bundle = getIntent().getExtras().getBundle(SHARE_DATA);
        String description = bundle.getString(SHARE_DESCRIPTION);
        String title = bundle.getString(SHARE_TITLE);
        String picture = bundle.getString(SHARE_PICTURE_LINK);
        String link = bundle.getString(SHARE_LINK);*/

        TweetComposer.Builder builder = null;


        builder = new TweetComposer.Builder(this)
//                    .text(title + "" + description)
                .text("Audio Url")
                .url(ShortUrl);
//                .image(Uri.parse(cover));
        builder.show();
    }

    public void googleShare() {

        /*GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInOptions.getAccount();*/


        /*Intent shareIntent = new PlusShare.Builder(SocialActivity.this)
                .setText("Android Testing for Google+")
                .setType("text/plain")
                .setContentUrl(
                        Uri.parse(fetchRecordingUrl))
                .getIntent()
                .setPackage("com.google.android.apps.plus");
        startActivityForResult(shareIntent, 0);*/






        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();*/
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(this, "" + acct.getDisplayName(), Toast.LENGTH_SHORT).show();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }


    protected void onResume() {
        super.onResume();
//        switchFb.setChecked(true);
//        switchTwitter.setChecked(true);
        // Refresh the state of the +1 button each time the activity receives focus.
        plus_one_button.initialize(fetchThumbNailUrl, PLUS_ONE_REQUEST_CODE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (switchFb.isChecked()){
            switchFb.setChecked(true);
        }
        if (switchTwitter.isChecked()){
            switchTwitter.setChecked(true);
        }
        if (switchSoundCloud.isChecked()){
            switchSoundCloud.setChecked(true);
        }
        if (switchGoogle.isChecked()){
            switchGoogle.setChecked(true);
        }
    }

    private class newShortAsync extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog;
        String longUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
            pDialog = new ProgressDialog(SocialActivity.this);
            pDialog.setMessage("Contacting Google Servers ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            longUrl = fetchThumbNailUrl;
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            System.out.println("JSON RESP:" + s);
            String response = s;
            try {
                JSONObject jsonObject = new JSONObject(response);
                ShortUrlId = jsonObject.getString("id");
                ShortUrl = new URL(ShortUrlId);
//                System.out.println("ID:" + id);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res = null;
            String json = "{longUrl:" + longUrl + "}";
            try {
                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyBmWJRuAcgoHTaljlTYsDtutkTb0HFhaHY");
//                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(40000);
                con.setConnectTimeout(40000);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                writer.write(json);
                writer.flush();
                writer.close();
                os.close();

                int status = con.getResponseCode();
                InputStream inputStream;
                if (status == HttpURLConnection.HTTP_OK)
                    inputStream = con.getInputStream();
                else
                    inputStream = con.getErrorStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                res = buffer.toString();

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return res;
        }
    }

    public void share() {
        Bundle postParams = new Bundle();
        postParams.putString(SocialNetwork.BUNDLE_LINK, fetchThumbNailUrl);
//        socialNetwork.requestPostLink(postParams, "", postingComplete);
    }

    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Toast.makeText(getApplicationContext(), "Error while sending: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    };

    private View.OnClickListener shareClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder ad = alertDialogInit("Would you like to post Link:", fetchThumbNailUrl);
            ad.setPositiveButton("Post link", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Bundle postParams = new Bundle();
                    postParams.putString(SocialNetwork.BUNDLE_NAME,
                            "Simple and easy way to add social networks for android application");
                    postParams.putString(SocialNetwork.BUNDLE_LINK, fetchThumbNailUrl);
//                    socialNetwork.requestPostLink(postParams, "", postingComplete);
                }
            });
            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                }
            });
            ad.create().show();
        }
    };

    public AlertDialog.Builder alertDialogInit(String title, String message) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(true);
        return ad;
    }

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;
            switch (view.getId()) {
                case R.id.switchFb:
                    networkId = FACEBOOK;
                    break;
                case R.id.switchTwitter:
                    networkId = TWITTER;
                    break;
                case R.id.switchGoogle:
                    networkId = GOOGLE_PLUS;
                    break;
            }
            SocialNetwork socialNetwork = commonShare.getSocialNetwork(networkId);
            if (!socialNetwork.isConnected()) {
                if (networkId != 0) {
                    socialNetwork.requestLogin();
//                    MainActivity.showProgress(socialNetwork, "Loading social person");
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
//                startProfile(socialNetwork.getID());
            }
        }
    };

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
    /*SocialNetwork socialNetwork = commonShare.getSocialNetwork(networkId);
        if(!socialNetwork.isConnected()) {
        if(networkId != 0) {
            socialNetwork.requestLogin();
            this.showProgress(socialNetwork, "Loading social person");
        } else {
            Toast.makeText(getApplicationContext(), "Wrong networkId", Toast.LENGTH_LONG).show();
        }
    } else {
        startProfile(socialNetwork.getID());
    }*/
}
