package com.instamelody.instamelody;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Update extends AppCompatActivity {

    EditText etuFirstName,etuLastName,etuEmail,etuUsername,etuPassWord,etuConfirmPassWord,etuPhone;
    Button btnClearFN,btnClearLN,btnClearEmail,btnClearUserName,btnClearPass,btnClearConfirmPass,btnClearDOB,btnClearPhone,
            buttonEditProfile,buttonUpdate;
    String userId, firstName,lastName,userNameLogin,profilePicLogin;
    int statusNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userId = loginSharedPref.getString("userId",null);
        firstName = loginSharedPref.getString("firstName", null);
        lastName = loginSharedPref.getString("lastName", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        statusNormal = loginSharedPref.getInt("status", 0);

        etuFirstName = (EditText) findViewById(R.id.etuFirstName);
        etuLastName = (EditText) findViewById(R.id.etuLastName);
        etuEmail = (EditText) findViewById(R.id.etuEmail);
        etuUsername = (EditText) findViewById(R.id.etuUsername);
        etuPassWord = (EditText) findViewById(R.id.etuPassWord);
        etuConfirmPassWord = (EditText) findViewById(R.id.etuConfirmPassWord);
        etuPhone = (EditText) findViewById(R.id.etuPhone);

        buttonEditProfile = (Button) findViewById(R.id.buttonEditProfile);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        btnClearFN = (Button) findViewById(R.id.btnClearFN);
        btnClearLN = (Button) findViewById(R.id.btnClearLN);
        btnClearEmail = (Button) findViewById(R.id.btnClearEmail);
        btnClearUserName = (Button) findViewById(R.id.btnClearUserName);
        btnClearPass = (Button) findViewById(R.id.btnClearPass);
        btnClearConfirmPass = (Button) findViewById(R.id.btnClearConfirmPass);
        btnClearDOB = (Button) findViewById(R.id.btnClearDOB);
        btnClearPhone = (Button) findViewById(R.id.btnClearPhone);

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonUpdate.setVisibility(View.VISIBLE);
                buttonEditProfile.setVisibility(View.GONE);

                etuFirstName.setClickable(true);
                etuFirstName.setCursorVisible(true);
                etuFirstName.setFocusable(true);

                etuLastName.setClickable(true);
                etuLastName.setCursorVisible(true);
                etuLastName.setFocusable(true);

                etuUsername.setClickable(true);
                etuUsername.setCursorVisible(true);
                etuUsername.setFocusable(true);

                etuPassWord.setClickable(true);
                etuPassWord.setCursorVisible(true);
                etuPassWord.setFocusable(true);

                etuConfirmPassWord.setClickable(true);
                etuConfirmPassWord.setCursorVisible(true);
                etuConfirmPassWord.setFocusable(true);

                etuPhone.setClickable(true);
                etuPhone.setCursorVisible(true);
                etuPhone.setFocusable(true);

            }
        });
    }
}
