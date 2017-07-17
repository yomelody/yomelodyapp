package com.instamelody.instamelody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    TextView tvDone, tvSignIn, tvSignOut;
    SeekBar seekBarDisc;
    RelativeLayout rlSocialConnect, rlSubscription, rlMyAccount;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rlSocialConnect = (RelativeLayout) findViewById(R.id.rlSocialConnect);
        rlSubscription = (RelativeLayout) findViewById(R.id.rlSubscription);
        rlMyAccount = (RelativeLayout) findViewById(R.id.rlMyAccount);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);
        tvSignOut = (TextView) findViewById(R.id.tvSignOut);
//        seekBarDisc = (SeekBar)findViewById(R.id.seekBarDisc);

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

        if (userId != null) {
            tvSignIn.setVisibility(View.GONE);
            tvSignOut.setVisibility(View.VISIBLE);
        } else {
            tvSignOut.setVisibility(View.GONE);
            tvSignIn.setVisibility(View.VISIBLE);
        }

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

        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                tEditor.clear();
                tEditor.commit();
                SharedPreferences.Editor fbeditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
                fbeditor.clear();
                fbeditor.commit();
                LoginManager.getInstance().logOut();
                HomeActivity.SignOut.setVisibility(View.INVISIBLE);
                HomeActivity.SignIn.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
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

        rlMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);

            }
        });

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
