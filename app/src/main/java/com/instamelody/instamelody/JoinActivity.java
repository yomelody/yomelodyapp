package com.instamelody.instamelody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Adapters.InstrumentListAdapter;
import com.instamelody.instamelody.Adapters.JoinInstrumentListAdp;
import com.instamelody.instamelody.Adapters.JoinListAdapter;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Parse.ParseContents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.JOINED_USERS;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class JoinActivity extends AppCompatActivity {
    public static RecyclerView.Adapter adapter, adapter1;
    public static ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();
    public static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    String pos = "0";
    public static RecyclerView recyclerView;
    public static RecyclerView recyclerViewInstruments;
    RelativeLayout rlIncluded;
    public static RelativeLayout rlJoinButton;
    private String USER_ID = "userid";
    private String RECORDING_ID = "rid";
    public static String addedBy, RecId;
    public static TextView play_count, tvLikeCount, tvCommentCount, tvShareCount;
    public static ImageView ivJoinPlay, ivJoinPause, ivLikeButton, ivDislikeButton;
    public static RelativeLayout rlLike, rlComment, joinFooter;
    public static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        play_count = (TextView) findViewById(R.id.tvPlayCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        ivJoinPlay = (ImageView) findViewById(R.id.ivJoinPlay);
        ivJoinPause = (ImageView) findViewById(R.id.ivJoinPause);
        rlLike = (RelativeLayout) findViewById(R.id.rlLikeContainer);
        ivLikeButton = (ImageView) findViewById(R.id.ivLikeButton);
        ivDislikeButton = (ImageView) findViewById(R.id.ivDislikeButton);
        rlComment = (RelativeLayout) findViewById(R.id.rlComment);
        rlIncluded = (RelativeLayout) findViewById(R.id.rlIncluded);
        rlJoinButton = (RelativeLayout) findViewById(R.id.rlJoinButton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewArtists);
        joinFooter = (RelativeLayout) findViewById(R.id.joinFooter);
        recyclerViewInstruments = (RecyclerView) findViewById(R.id.recyclerViewInstruments);
        recyclerViewInstruments.setVisibility(View.VISIBLE);
        recyclerViewInstruments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewInstruments.setLayoutManager(layoutManager);
        recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        rlIncluded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        rlJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        SharedPreferences filterPref = getApplicationContext().getSharedPreferences("RecordingData", MODE_PRIVATE);
        addedBy = filterPref.getString("AddedBy", null);
        RecId = filterPref.getString("Recording_id", null);
        if (addedBy != null && RecId != null) {
            try {

//                getJoined_Local();
                  getJoined_users(addedBy, RecId);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    public void getJoined_Local() {
        String response = "{\n" +
                "  \"flag\": \"success\",\n" +
                "  \"response\": [\n" +
                "    {\n" +
                "      \"user_id\": \"36\",\n" +
                "      \"user_name\": \"@Shubh\",\n" +
                "      \"profile_pic\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-original-imaehsyjr7ptj9yj.jpeg\",\n" +
                "      \"joined_artists\": \"15\",\n" +
                "      \"recording_id\": \"5\",\n" +
                "      \"recording_name\": \"Crazy Life\",\n" +
                "      \"recording_url\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-origiimaehsyjr7ptj9yj.jpeg\",\n" +
                "      \"recording_cover\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-origiimaehsyjyj.jpeg\",\n" +
                "      \"recording_duration\": \"152\",\n" +
                "      \"recording_date\": \"2017-07-21 19:14:53\",\n" +
                "      \"like_status\": \"0\",\n" +
                "      \"play_counts\": \"0\",\n" +
                "      \"like_counts\": \"1\",\n" +
                "      \"share_counts\": \"0\",\n" +
                "      \"comment_counts\": \"0\",\n" +
                "      \"instruments\": [\n" +
                "        {\n" +
                "          \"instrument_id\": \"21\",\n" +
                "          \"instruments_name\": \"ring\",\n" +
                "          \"instruments_type\": \"admin\",\n" +
                "          \"melodypackid\": \"22\",\n" +
                "          \"bpm\": \"160000\",\n" +
                "          \"file_size\": \"884572\",\n" +
                "          \"instrument_url\": \"http://52.89.220.199/api/uploads/pics/1500664493Main_Phir_Bhi_Tumko_Chahunga_Ringtone.mp3\",\n" +
                "          \"duration\": \"39\",\n" +
                "          \"uploadeddate\": \"2017-07-21 19:14:53\",\n" +
                "          \"profilepic\": \"http://52.89.220.199/api/uploads/profilepics/1500664493rahul_gandhi_760_1487948971_749x421.jpeg\",\n" +
                "          \"coverpic\": \"http://52.89.220.199/api/uploads/coverpics/1500664493rahul_gandhi_760_1487948971_749x421.jpeg\",\n" +
                "          \"username\": \"@Shubh\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"instrument_id\": \"20\",\n" +
                "          \"instruments_name\": \"gdfgf\",\n" +
                "          \"instruments_type\": \"admin\",\n" +
                "          \"melodypackid\": \"22\",\n" +
                "          \"bpm\": \"320000\",\n" +
                "          \"file_size\": \"0\",\n" +
                "          \"instrument_url\": \"http://52.89.220.199/api/uploads/pics/1500493233Big_Blood_-_04_-_El_Debarge.mp3\",\n" +
                "          \"duration\": \"25\",\n" +
                "          \"uploadeddate\": \"2017-07-20 18:17:05\",\n" +
                "          \"profilepic\": \"http://52.89.220.199/api/uploads/profilepics/1500493233EMDS-stacked.png\",\n" +
                "          \"coverpic\": \"http://52.89.220.199/api/uploads/coverpics/1500574625Hydras.jpg\",\n" +
                "          \"username\": \"@Shubh\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"user_id\": \"36\",\n" +
                "      \"user_name\": \"@Shubh\",\n" +
                "      \"profile_pic\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-original-imaehsyjr7ptj9yj.jpeg\",\n" +
                "      \"joined_artists\": \"15\",\n" +
                "      \"recording_id\": \"5\",\n" +
                "      \"recording_name\": \"Crazy Life\",\n" +
                "      \"recording_url\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-origiimaehsyjr7ptj9yj.jpeg\",\n" +
                "      \"recording_cover\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-origiimaehsyjyj.jpeg\",\n" +
                "      \"recording_duration\": \"152\",\n" +
                "      \"recording_date\": \"2017-07-21 19:14:53\",\n" +
                "      \"like_status\": \"0\",\n" +
                "      \"play_counts\": \"0\",\n" +
                "      \"like_counts\": \"1\",\n" +
                "      \"share_counts\": \"0\",\n" +
                "      \"comment_counts\": \"0\",\n" +
                "      \"instruments\": [\n" +
                "        {\n" +
                "          \"instrument_id\": \"21\",\n" +
                "          \"instruments_name\": \"ring\",\n" +
                "          \"instruments_type\": \"admin\",\n" +
                "          \"melodypackid\": \"22\",\n" +
                "          \"bpm\": \"160000\",\n" +
                "          \"file_size\": \"884572\",\n" +
                "          \"instrument_url\": \"http://52.89.220.199/api/uploads/pics/1500664493Main_Phir_Bhi_Tumko_Chahunga_Ringtone.mp3\",\n" +
                "          \"duration\": \"39\",\n" +
                "          \"uploadeddate\": \"2017-07-21 19:14:53\",\n" +
                "          \"profilepic\": \"http://52.89.220.199/api/uploads/profilepics/1500664493rahul_gandhi_760_1487948971_749x421.jpeg\",\n" +
                "          \"coverpic\": \"http://52.89.220.199/api/uploads/coverpics/1500664493rahul_gandhi_760_1487948971_749x421.jpeg\",\n" +
                "          \"username\": \"@Shubh\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"instrument_id\": \"20\",\n" +
                "          \"instruments_name\": \"gdfgf\",\n" +
                "          \"instruments_type\": \"admin\",\n" +
                "          \"melodypackid\": \"22\",\n" +
                "          \"bpm\": \"320000\",\n" +
                "          \"file_size\": \"0\",\n" +
                "          \"instrument_url\": \"http://52.89.220.199/api/uploads/pics/1500493233Big_Blood_-_04_-_El_Debarge.mp3\",\n" +
                "          \"duration\": \"25\",\n" +
                "          \"uploadeddate\": \"2017-07-20 18:17:05\",\n" +
                "          \"profilepic\": \"http://52.89.220.199/api/uploads/profilepics/1500493233EMDS-stacked.png\",\n" +
                "          \"coverpic\": \"http://52.89.220.199/api/uploads/coverpics/1500574625Hydras.jpg\",\n" +
                "          \"username\": \"@Shubh\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"user_id\": \"36\",\n" +
                "      \"user_name\": \"@Shubh\",\n" +
                "      \"profile_pic\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-original-imaehsyjr7ptj9yj.jpeg\",\n" +
                "      \"joined_artists\": \"15\",\n" +
                "      \"recording_id\": \"5\",\n" +
                "      \"recording_name\": \"Crazy Life\",\n" +
                "      \"recording_url\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-origiimaehsyjr7ptj9yj.jpeg\",\n" +
                "      \"recording_cover\": \"http://52.89.220.199/api//uploads/profilepics/1500662364427d14012-saara-origiimaehsyjyj.jpeg\",\n" +
                "      \"recording_duration\": \"152\",\n" +
                "      \"recording_date\": \"2017-07-21 19:14:53\",\n" +
                "      \"like_status\": \"0\",\n" +
                "      \"play_counts\": \"0\",\n" +
                "      \"like_counts\": \"1\",\n" +
                "      \"share_counts\": \"0\",\n" +
                "      \"comment_counts\": \"0\",\n" +
                "      \"instruments\": [\n" +
                "        {\n" +
                "          \"instrument_id\": \"21\",\n" +
                "          \"instruments_name\": \"ring\",\n" +
                "          \"instruments_type\": \"admin\",\n" +
                "          \"melodypackid\": \"22\",\n" +
                "          \"bpm\": \"160000\",\n" +
                "          \"file_size\": \"884572\",\n" +
                "          \"instrument_url\": \"http://52.89.220.199/api/uploads/pics/1500664493Main_Phir_Bhi_Tumko_Chahunga_Ringtone.mp3\",\n" +
                "          \"duration\": \"39\",\n" +
                "          \"uploadeddate\": \"2017-07-21 19:14:53\",\n" +
                "          \"profilepic\": \"http://52.89.220.199/api/uploads/profilepics/1500664493rahul_gandhi_760_1487948971_749x421.jpeg\",\n" +
                "          \"coverpic\": \"http://52.89.220.199/api/uploads/coverpics/1500664493rahul_gandhi_760_1487948971_749x421.jpeg\",\n" +
                "          \"username\": \"@Shubh\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"instrument_id\": \"20\",\n" +
                "          \"instruments_name\": \"gdfgf\",\n" +
                "          \"instruments_type\": \"admin\",\n" +
                "          \"melodypackid\": \"22\",\n" +
                "          \"bpm\": \"320000\",\n" +
                "          \"file_size\": \"0\",\n" +
                "          \"instrument_url\": \"http://52.89.220.199/api/uploads/pics/1500493233Big_Blood_-_04_-_El_Debarge.mp3\",\n" +
                "          \"duration\": \"25\",\n" +
                "          \"uploadeddate\": \"2017-07-20 18:17:05\",\n" +
                "          \"profilepic\": \"http://52.89.220.199/api/uploads/profilepics/1500493233EMDS-stacked.png\",\n" +
                "          \"coverpic\": \"http://52.89.220.199/api/uploads/coverpics/1500574625Hydras.jpg\",\n" +
                "          \"username\": \"@Shubh\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        Joined_artist.clear();
        instrumentList.clear();
        new ParseContents(getApplicationContext()).parseJoin(response, Joined_artist);
        adapter = new JoinListAdapter(Joined_artist, getApplicationContext());
        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        if (pos.equals("0")) {
            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, pos);
            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
            recyclerViewInstruments.setAdapter(adapter1);
        } else {
            pos = intent.getExtras().getString("Value");
            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, pos);
            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
            recyclerViewInstruments.setAdapter(adapter1);
        }

    }


    public void getJoined_users(final String addedBy, final String RecId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JOINED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);
                        Joined_artist.clear();
                        instrumentList.clear();
                        new ParseContents(getApplicationContext()).parseJoin(response, Joined_artist);
                        adapter = new JoinListAdapter(Joined_artist, getApplicationContext());
                        recyclerView.setAdapter(adapter);

                        if (pos.equals("0")) {
                            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, pos);
                            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter1);
                        } else {
                            Intent intent1 = getIntent();
                            pos = intent1.getExtras().getString("Value");
                            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, pos);
                            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, addedBy);
                params.put(RECORDING_ID, RecId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
