package com.instamelody.instamelody;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class SocialActivity extends AppCompatActivity {

    Switch switchFb, switchTwitter, switchSoundCloud, switchGoogle;
    String fetchRecordingUrl,fetchThumbNailUrl;
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
    String cover;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


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
        SharedPreferences loginSharedPref1 = this.getSharedPreferences("Url_recording", MODE_PRIVATE);
        fetchRecordingUrl = loginSharedPref1.getString("Recording_url", null);

        SharedPreferences editorT = this.getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        fetchThumbNailUrl= editorT.getString("thumbnailUrl", null);

        plus_one_button = (PlusOneButton) findViewById(R.id.plus_one_button);
//        new URLShort().execute();


        /*SharedPreferences editor = this.getSharedPreferences("commentData", MODE_PRIVATE);
        cover = editor.getString("bitmapCover", null);*/


        SharedPreferences editor1 = this.getSharedPreferences("commentData1", MODE_PRIVATE);
        cover = editor1.getString("cover", null);


        switchFb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "No Recording Found to Share", Toast.LENGTH_SHORT).show();
                    switchFb.setEnabled(false);
                } else {
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
                }
            }
        });

        switchTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "No Recording Found to Share", Toast.LENGTH_SHORT).show();
                    switchTwitter.setEnabled(false);
                } else {
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
                }

            }
        });

        switchSoundCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "No Recording found to share", Toast.LENGTH_SHORT).show();
                    switchSoundCloud.setEnabled(false);
                }
            }
        });

        switchGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fetchThumbNailUrl == null) {
                    Toast.makeText(SocialActivity.this, "No Recording Found to Share", Toast.LENGTH_SHORT).show();
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
    }


    public void FbShare() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(SocialActivity.this, "Recording Uploaded", Toast.LENGTH_SHORT).show();
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
//                    .setImageUrl(Uri.parse(fetchThumbNailUrl))
                    .build();
            shareDialog.show(linkContent, ShareDialog.Mode.FEED);
        }

        /*Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT, "https://xd.adobe.com/view/2f9e84bb-cf4e-41e5-a795-85cb623f2c65/screen/922427a9-b96a-46c6-b2e6-ec56499fafc5/Studio-47/");
        intent.putExtra(Intent.EXTRA_TEXT, "https://xd.adobe.com/view/2f9e84bb-cf4e-41e5-a795-85cb623f2c65/screen/7ce670b3-b072-48f6-9c7f-87ce504c3c14/Studio-62/");

//        intent.putExtra(Intent.EXTRA_TEXT, fetchThumbNailUrl);

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
        startActivity(Intent.createChooser(intent, "Share"));*/
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            //  twitter related handling
            client.onActivityResult(requestCode, resultCode, data);
            switchTwitter.setEnabled(false);
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
                .url(ShortUrl)
                .image(Uri.parse(cover));

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
        // Refresh the state of the +1 button each time the activity receives focus.
        plus_one_button.initialize(fetchThumbNailUrl, PLUS_ONE_REQUEST_CODE);
    }
}
