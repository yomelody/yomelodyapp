package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.instamelody.instamelody.HomeActivity;
import com.instamelody.instamelody.Models.UserDetails;
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.utils.AppHelper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CBPC 41 on 1/13/2017.
 */

public class BioFragment extends Fragment {

    ImageView ivEdit;
    EditText etBio;
    TextView textViewName,tvStation,tvDate;
    CircleImageView userBioImage;
    String firstName,userNameLogin,profilePicLogin,Name,userName,profilePic,fbName,fbUserName,fbId;
    int statusNormal,statusFb,statusTwitter;
    UserDetails userDetails;

    public BioFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bio, container, false);
        ivEdit = (ImageView)view.findViewById(R.id.ivEdit);
        etBio = (EditText)view.findViewById(R.id.etBio);
        etBio.setTag(etBio.getKeyListener());
        etBio.setKeyListener(null);
        textViewName = (TextView)view.findViewById(R.id.textViewName);
        tvStation = (TextView)view.findViewById(R.id.tvStation);
        tvDate = (TextView)view.findViewById(R.id.tvDate);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String dateString = sdf.format(date);
        tvDate.setText(dateString);


        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //etBio.isInEditMode();
                etBio.setKeyListener((KeyListener) etBio.getTag());
            }
        });

        userBioImage = (CircleImageView)view.findViewById(R.id.userBioImage);
        userBioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                startActivity(i);
            }
        });


        if (getArguments() != null && getArguments().containsKey("user_detail")) {
            userDetails = (UserDetails) getArguments().getSerializable("user_detail");

            textViewName.setText(userDetails.getFname());
            tvStation.setText(userDetails.getUsername());

            if (userDetails.getProfilepic() != null) {
                userBioImage.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(userDetails.getProfilepic()).into(userBioImage);
            }
            if(!TextUtils.isEmpty(userDetails.getDiscrisption())){
                etBio.setText(userDetails.getDiscrisption());
                AppHelper.sop(userDetails.getDiscrisption());
                etBio.setEnabled(false);
            }

        }
        else {
            etBio.setEnabled(true);
            SharedPreferences loginSharedPref = this.getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
            firstName = loginSharedPref.getString("firstName", null);
            userNameLogin = loginSharedPref.getString("userName", null);
            profilePicLogin = loginSharedPref.getString("profilePic", null);
            statusNormal = loginSharedPref.getInt("status", 0);

            if (statusNormal == 1){
                textViewName.setText(firstName);
                tvStation.setText(userNameLogin);
            }

            if (profilePicLogin != null) {
                // ivProfile.setVisibility(View.GONE);
                userBioImage.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(profilePicLogin).into(userBioImage);
            }


            SharedPreferences twitterPref = this.getActivity().getSharedPreferences("TwitterPref", MODE_PRIVATE);
            Name = twitterPref.getString("Name", null);
            userName = twitterPref.getString("userName", null);
            profilePic = twitterPref.getString("ProfilePic", null);
            statusTwitter = twitterPref.getInt("status", 0);

            if (statusTwitter == 1){
                textViewName.setText(Name);
                tvStation.setText(userName);
            }

            if (profilePic != null) {
                // ivProfile.setVisibility(View.GONE);
                userBioImage.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(profilePic).into(userBioImage);
            }


            SharedPreferences fbPref = this.getActivity().getSharedPreferences("MyFbPref", MODE_PRIVATE);
            fbName = fbPref.getString("FbName", null);
            fbUserName = fbPref.getString("userName", null);
            fbId = fbPref.getString("fbId",null);
            statusFb = fbPref.getInt("status", 0);

            if (statusFb == 1){
                textViewName.setText(fbName);
                tvStation.setText("@"+fbName);
            }

            if (fbId != null) {
                //ivProfile.setVisibility(View.GONE);
                userBioImage.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load("https://graph.facebook.com/" + fbId + "/picture").into(userBioImage);
            }
        }



        return view;
    }
}