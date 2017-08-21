package com.instamelody.instamelody;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    TextView tvDone, tvSignIn, tvSignOut, tvFirstNameSettings, tvUserNameSettings;
    SeekBar seekBarDisc;
    ImageView ivLogoContainer;
    RelativeLayout rlSocialConnect, rlSubscription, rlMyAccount, rlTos, rlPrivacyPolicy,rlInviteContacts,rlRateApp;
    String userId, firstName, lastName, userNameLogin, profilePicLogin;
    String userIdTwitter, firstNameTwitter, lastNameTwitter, emailFinalTwitter, profilePicTwitter, userNameTwitter;
    String userIdFb, firstNameFb, lastNameFb,userNameFb;
    CircleImageView userProfileImageSettings;
    int statusNormal;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rlSocialConnect = (RelativeLayout) findViewById(R.id.rlSocialConnect);
        rlSubscription = (RelativeLayout) findViewById(R.id.rlSubscription);
        rlMyAccount = (RelativeLayout) findViewById(R.id.rlMyAccount);
        rlInviteContacts = (RelativeLayout)findViewById(R.id.rlInviteContacts);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        tvSignOut = (TextView) findViewById(R.id.tvSignOut);
        tvFirstNameSettings = (TextView) findViewById(R.id.tvFirstNameSettings);
        tvUserNameSettings = (TextView) findViewById(R.id.tvUserNameSettings);
        ivLogoContainer = (ImageView) findViewById(R.id.ivLogoContainer);
        userProfileImageSettings = (CircleImageView) findViewById(R.id.userProfileImageSettings);
        rlPrivacyPolicy = (RelativeLayout) findViewById(R.id.rlPrivacyPolicy);
        rlTos = (RelativeLayout) findViewById(R.id.rlTos);
        rlRateApp = (RelativeLayout) findViewById(R.id.rlRateApp);

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

        if (userId != null) {
            tvSignIn.setVisibility(View.GONE);
            tvSignOut.setVisibility(View.VISIBLE);
            if (statusNormal == 1) {
                tvFirstNameSettings.setText(firstName + " " + lastName);
                tvUserNameSettings.setText("@" + userNameLogin);
            }

            if (profilePicLogin != null) {
                ivLogoContainer.setVisibility(View.GONE);
                userProfileImageSettings.setVisibility(View.VISIBLE);
                Picasso.with(SettingsActivity.this).load(profilePicLogin).into(userProfileImageSettings);

            }

            if (userIdFb!= null){
                tvFirstNameSettings.setText(firstNameFb+"  "+lastNameFb);
                tvUserNameSettings.setText(userNameFb);
//                SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
                String fbId = fbPref.getString("fbId", null);
                ivLogoContainer.setVisibility(View.GONE);
                userProfileImageSettings.setVisibility(View.VISIBLE);
                Picasso.with(SettingsActivity.this).load("https://graph.facebook.com/" + fbId + "/picture").into(userProfileImageSettings);
            }else if (userIdTwitter!= null){
                tvFirstNameSettings.setText(firstNameTwitter+"  "+lastNameTwitter);
                tvUserNameSettings.setText(userNameTwitter);
//                SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
                String profilePic1 = twitterPref.getString("profilePic", null);
                ivLogoContainer.setVisibility(View.GONE);
                userProfileImageSettings.setVisibility(View.VISIBLE);
                Picasso.with(SettingsActivity.this).load(profilePic1).into(userProfileImageSettings);
            }
            rlMyAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Update.class);
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

            rlPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this,PrivacyPolicy.class);
                    startActivity(intent);
                }
            });

            rlTos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this,PrivacyPolicy.class);
                    startActivity(intent);
                }
            });

            rlSocialConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), SocialActivity.class);
                    startActivity(intent);

                }
            });

            rlSubscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), MelodyActivity.class);
                    startActivity(intent);

                }
            });

            rlRateApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Uri uri = Uri.parse("market://details?id=" + context.getPackageName
                            ());*/

                    Uri uri = Uri.parse("https://play.google.com/store?hl=en");

                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        /*startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?
                                        id=" + context.getPackageName())));*/
                    }
                }
            });

            rlInviteContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store?hl=en");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
                    startActivity(Intent.createChooser(intent, "Share"));
                }
            });

            tvSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                    tEditor.clear();
                    tEditor.apply();
                    SharedPreferences.Editor fbeditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
                    fbeditor.clear();
                    fbeditor.apply();
                    LoginManager.getInstance().logOut();
                    HomeActivity.SignOut.setVisibility(View.INVISIBLE);
                    HomeActivity.SignIn.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    SettingsActivity.this.recreate();
                }
            });

        } else {
            tvSignOut.setVisibility(View.GONE);
            tvSignIn.setVisibility(View.VISIBLE);
            rlMyAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);

                }
            });

            rlInviteContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "Sign in Share InstaMelody Application", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                }
            });

            rlRateApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Uri uri = Uri.parse("market://details?id=" + context.getPackageName
                            ());*/
                    Uri uri = Uri.parse("https://play.google.com/store?hl=en");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        /*startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?
                                        id=" + context.getPackageName())));*/
                    }
                }
            });


            tvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);

                }
            });

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);

                }
            });

            rlTos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this,PrivacyPolicy.class);
                    startActivity(intent);
                }
            });

            rlPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this,PrivacyPolicy.class);
                    startActivity(intent);
                }
            });



            tvSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();
                    SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                    tEditor.clear();
                    tEditor.apply();
                    SharedPreferences.Editor fbeditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
                    fbeditor.clear();
                    fbeditor.apply();
                    LoginManager.getInstance().logOut();
                    HomeActivity.SignOut.setVisibility(View.INVISIBLE);
                    HomeActivity.SignIn.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    SettingsActivity.this.recreate();
                }
            });

            rlSocialConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), SocialActivity.class);
                    startActivity(intent);

                }
            });

            rlSubscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), MelodyActivity.class);
                    startActivity(intent);

                }
            });

        /*rlMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);

            }
        });*/

//        seekBarDisc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Toast.makeText(getApplicationContext(),"seekbar progress: ", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
//            }
//        });
        }
    }
}
