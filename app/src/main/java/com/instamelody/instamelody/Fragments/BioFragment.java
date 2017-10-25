package com.instamelody.instamelody.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.HomeActivity;
import com.instamelody.instamelody.Models.UserDetails;
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SocialActivity;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.Const;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.description;
import static com.instamelody.instamelody.utils.Const.ServiceType.social_status;

/**
 * Created by CBPC 41 on 1/13/2017.
 */

public class BioFragment extends Fragment {

    ImageView ivEdit;
 //   EditText etBio;
    TextView textViewName, tvStation, tvDate,tvEdit,tvBioDes;
    CircleImageView userBioImage;
    String firstName, userNameLogin, profilePicLogin, Name, userName, profilePic, fbName, fbUserName, fbId, userId;
    int statusNormal, statusFb, statusTwitter;
    UserDetails userDetails;
    private LinearLayout editLl;
    private Activity mActivity;
    private ProgressDialog progressDialog;
    private SharedPreferences.Editor editor;
    private String descriptionTxt="";

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

        mActivity = getActivity();
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        ivEdit = (ImageView) view.findViewById(R.id.ivEdit);
        textViewName = (TextView) view.findViewById(R.id.textViewName);
        tvStation = (TextView) view.findViewById(R.id.tvStation);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvEdit = (TextView)view.findViewById(R.id.tvEdit);
        tvBioDes = (TextView)view.findViewById(R.id.tvBioDes);
        editLl = (LinearLayout) view.findViewById(R.id.editLl);
        userBioImage = (CircleImageView) view.findViewById(R.id.userBioImage);

        SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getActivity().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getActivity().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
            descriptionTxt = loginSharedPref.getString("description", "");
            editor = mActivity.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();

        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
            descriptionTxt = fbPref.getString("description", "");
            editor = mActivity.getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
            descriptionTxt = twitterPref.getString("description", "");
            editor = mActivity.getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
        }

        if (getArguments()!=null){
            if (getArguments().getString("show_Profile_UserId").equalsIgnoreCase(userId)) {
                editLl.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(descriptionTxt)) {
                    tvBioDes.setText(descriptionTxt);
                }
            }
            else {
                editLl.setVisibility(View.GONE);
//                UserDetails userDetails= (UserDetails) getArguments().getSerializable("user_detail");
                tvBioDes.setText(((UserDetails) getArguments().getSerializable("user_detail")).getDiscrisption());
            }
            textViewName.setText("Artist: "+((UserDetails) getArguments().getSerializable("user_detail")).getFname()+
                   " " +((UserDetails) getArguments().getSerializable("user_detail")).getLname());
            tvStation.setText("Station: @"+((UserDetails) getArguments().getSerializable("user_detail")).getUsername());

            Picasso.with(mActivity).load(((UserDetails) getArguments().getSerializable("user_detail")).getProfilepic()).into(userBioImage);
            AppHelper.sop("Registration=date="+((UserDetails) getArguments().getSerializable("user_detail")).getRegisterdate());
//            String input = "Sat Feb 17 2012";
            Date datee = null;
            try {
                datee = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(((UserDetails) getArguments().getSerializable("user_detail")).getRegisterdate());

                long milliseconds = datee.getTime();
                long millisecondsFromNow = milliseconds - (new Date()).getTime();

                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = sdf.format(milliseconds);
                tvDate.setText("Created:" + " " + dateString);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        editLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLangDialog();
            }
        });

        return view;
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_bio, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();

        TextView cancelTv = dialogView.findViewById(R.id.cancelTv);
        TextView updateTv = dialogView.findViewById(R.id.updateTv);

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });

        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(((EditText)(dialogView.findViewById(R.id.desEt))).getText().toString().trim())){
                    Toast.makeText(mActivity,"Please enter description.",Toast.LENGTH_SHORT).show();
                }
                else {
                    b.dismiss();
                    setUserDescriptionApi(((EditText)(dialogView.findViewById(R.id.desEt))).getText().toString().trim());
                }
            }
        });

        b.show();
    }

    private void setUserDescriptionApi(final String des){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, description,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        AppHelper.sop("response==" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equalsIgnoreCase("success")){
                                tvBioDes.setText(des);
                                editor.putString("description", des);
                                editor.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("description", des);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params==" + params + "\nURL==" + description);
                return params;

            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);
    }
}