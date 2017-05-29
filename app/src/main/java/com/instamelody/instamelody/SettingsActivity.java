package com.instamelody.instamelody;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shubahansh Jaiswal on 11/29/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    TextView tvDone, tvSignIn;
    SeekBar seekBarDisc;
    RelativeLayout rlSocialConnect, rlSubscription, rlMyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rlSocialConnect = (RelativeLayout)findViewById(R.id.rlSocialConnect);
        rlSubscription = (RelativeLayout)findViewById(R.id.rlSubscription);
        rlMyAccount =  (RelativeLayout)findViewById(R.id.rlMyAccount);
        tvDone = (TextView)findViewById(R.id.tvDone);
        tvSignIn = (TextView)findViewById(R.id.tvSignIn);
//        seekBarDisc = (SeekBar)findViewById(R.id.seekBarDisc);

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
