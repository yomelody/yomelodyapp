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
 //   EditText etBio;
    TextView textViewName, tvStation, tvDate,tvEdit;
    CircleImageView userBioImage;
    String firstName, userNameLogin, profilePicLogin, Name, userName, profilePic, fbName, fbUserName, fbId, userId;
    int statusNormal, statusFb, statusTwitter;
    UserDetails userDetails;

    public BioFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bio, container, false);
        ivEdit = (ImageView) view.findViewById(R.id.ivEdit);
//        etBio = (EditText) view.findViewById(R.id.etBio);
//        etBio.setTag(etBio.getKeyListener());
  //      etBio.setKeyListener(null);
        textViewName = (TextView) view.findViewById(R.id.textViewName);
        tvStation = (TextView) view.findViewById(R.id.tvStation);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvEdit=(TextView)view.findViewById(R.id.tvEdit);

        SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getActivity().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getActivity().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String dateString = sdf.format(date);
        tvDate.setText("Created:" + " " + dateString);


        return view;
    }
}