package com.instamelody.instamelody;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
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
import com.instamelody.instamelody.Fragments.CommentJoinFragment;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.JoinedUserProfile;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.ModelPlayAllMediaPlayer;
import com.instamelody.instamelody.Parse.ParseContents;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
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
    public static Chronometer chrono;
    private static final int SAMPLING_RATE = 44100;
    public static RecyclerView recyclerView;
    public static RecyclerView recyclerViewInstruments;
    RelativeLayout rlIncluded;
    public static TextView recording_date, melody_date;
    public static RelativeLayout rlJoinButton;
    private String USER_ID = "userid";
    private String RECORDING_ID = "rid";
    public static String addedBy, RecId, UserName, ProfileImageRec, RecordingName;
    public static TextView play_count, tvLikeCount, tvCommentCount, tvShareCount;
    public static ImageView ivJoinPlay, ivJoinPause, ivLikeButton, ivDislikeButton, ivPlayNext, ivPlayPre;
    public static RelativeLayout rlLike, rlComment;
    public static int position;
    ProgressDialog progressDialog;
    public static ArrayList<JoinedUserProfile> listProfile = new ArrayList<JoinedUserProfile>();
    public static com.instamelody.instamelody.utils.WaveformView waveform_view;
    private boolean mShouldContinue = true;
    public static TextView mDecibelView, recording_name, artist_name;
    public static ImageView profile_image, ivShareButton, ivRecordJoin;
    public static ImageView ivBackButton, ivHomeButton, playAll, pauseAll;
    public static TextView melody_detail, txtCount, tvIncluded;
    public static RelativeLayout joincenter, joinFooter, rlInviteButton;
    public static FrameLayout commentContainer, frameProgress;
    public static final Handler handler = new Handler();
    public static ArrayList<JoinInstrumentListAdp.ViewHolder> lstViewHolder = new ArrayList<JoinInstrumentListAdp.ViewHolder>();
    public static MediaPlayer mpall;
    public static ArrayList<MediaPlayer> mediaPlayersAll = new ArrayList<MediaPlayer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        progressDialog = new ProgressDialog(this);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        artist_name = (TextView) findViewById(R.id.artist_name);
        melody_detail = (TextView) findViewById(R.id.melody_detail);
        recording_name = (TextView) findViewById(R.id.recording_name);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        ivShareButton = (ImageView) findViewById(R.id.ivShareButton);
        ivPlayNext = (ImageView) findViewById(R.id.ivPlayNext);
        txtCount = (TextView) findViewById(R.id.txtCount);
        tvIncluded = (TextView) findViewById(R.id.tvIncluded);
        ivPlayPre = (ImageView) findViewById(R.id.ivPlayPre);
        ivRecordJoin = (ImageView) findViewById(R.id.ivRecordJoin);
        playAll = (ImageView) findViewById(R.id.playAll);
        pauseAll = (ImageView) findViewById(R.id.pauseAll);
        waveform_view = (com.instamelody.instamelody.utils.WaveformView) findViewById(R.id.waveform_view);
        mDecibelView = (TextView) findViewById(R.id.decibel_view);
        play_count = (TextView) findViewById(R.id.tvPlayCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        recording_date = (TextView) findViewById(R.id.recording_date);
        melody_date = (TextView) findViewById(R.id.melody_date);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        ivJoinPlay = (ImageView) findViewById(R.id.ivJoinPlay);
        ivJoinPause = (ImageView) findViewById(R.id.ivJoinPause);
        rlLike = (RelativeLayout) findViewById(R.id.rlLikeContainer);
        ivLikeButton = (ImageView) findViewById(R.id.ivLikeButton);
        ivDislikeButton = (ImageView) findViewById(R.id.ivDislikeButton);
        chrono = (Chronometer) findViewById(R.id.chrono);
        rlComment = (RelativeLayout) findViewById(R.id.rlComment);
        rlIncluded = (RelativeLayout) findViewById(R.id.rlIncluded);
        rlJoinButton = (RelativeLayout) findViewById(R.id.rlJoinButton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewArtists);
        joinFooter = (RelativeLayout) findViewById(R.id.joinFooter);
        recyclerViewInstruments = (RecyclerView) findViewById(R.id.recyclerViewInstruments);
        joincenter = (RelativeLayout) findViewById(R.id.joincenter);
        commentContainer = (FrameLayout) findViewById(R.id.commentContainer);
        rlInviteButton = (RelativeLayout) findViewById(R.id.rlInviteButton);
        recyclerViewInstruments.setVisibility(View.VISIBLE);
        recyclerViewInstruments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewInstruments.setLayoutManager(layoutManager);
        recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
        frameProgress = (FrameLayout) findViewById(R.id.frameProgress);
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


        rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    joinFooter.setVisibility(GONE);
                    //joincenter.setVisibility(GONE);
                    //  recyclerViewInstruments.setVisibility(GONE);
                    CommentJoinFragment af = new CommentJoinFragment();
                    getFragmentManager().beginTransaction().replace(R.id.commentContainer, af).commit();

//                    Intent intent = new Intent(context, JoinCommentActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("Position", String.valueOf(position));
//                    context.startActivity(intent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        });

        rlInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                    intent.putExtra("Previous", "JoinActivity");
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (JoinListAdapter.mp != null) {
                    JoinListAdapter.mp.stop();
                }
                if (JoinListAdapter.mRecordingThread != null) {
                    JoinListAdapter.mRecordingThread.stopRunning();
                    JoinListAdapter.mRecordingThread = null;
                    //    mShouldContinue=true;
                }
                if (JoinInstrumentListAdp.mp_start != null) {
                    for (int i = 0; i <= JoinInstrumentListAdp.mp_start.size() - 1; i++) {
                        JoinInstrumentListAdp.mp_start.get(i).stop();

                    }
                }
                if (mediaPlayersAll != null) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        mediaPlayersAll.get(i).stop();
                    }
                    mediaPlayersAll.clear();
                    lstViewHolder.clear();
                }
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JoinListAdapter.mp != null) {
                    JoinListAdapter.mp.stop();
                }
                if (JoinListAdapter.mRecordingThread != null) {
                    JoinListAdapter.mRecordingThread.stopRunning();
                    JoinListAdapter.mRecordingThread = null;
                    //    mShouldContinue=true;
                }
                if (JoinInstrumentListAdp.mp_start != null) {
                    for (int i = 0; i <= JoinInstrumentListAdp.mp_start.size() - 1; i++) {
                        JoinInstrumentListAdp.mp_start.get(i).stop();

                    }
                }
                if (mediaPlayersAll != null) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        mediaPlayersAll.get(i).stop();
                    }
                    mediaPlayersAll.clear();
                    lstViewHolder.clear();
                }


                finish();

            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JoinListAdapter.mp != null) {
                    JoinListAdapter.mp.stop();
                }
                if (JoinListAdapter.mRecordingThread != null) {
                    JoinListAdapter.mRecordingThread.stopRunning();
                    JoinListAdapter.mRecordingThread = null;
                    //    mShouldContinue=true;
                }
                if (JoinInstrumentListAdp.mp_start != null) {
                    for (int i = 0; i <= JoinInstrumentListAdp.mp_start.size() - 1; i++) {
                        JoinInstrumentListAdp.mp_start.get(i).stop();

                    }
                }
                if (mediaPlayersAll != null) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        mediaPlayersAll.get(i).stop();
                    }
                    mediaPlayersAll.clear();
                    lstViewHolder.clear();
                }
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });


        SharedPreferences filterPref = getApplicationContext().getSharedPreferences("RecordingData", MODE_PRIVATE);
        addedBy = filterPref.getString("AddedBy", null);
        RecId = filterPref.getString("Recording_id", null);
        UserName = filterPref.getString("UserNameRec", null);
        ProfileImageRec = filterPref.getString("UserProfile", null);
        RecordingName = filterPref.getString("RecordingName", null);
        Picasso.with(getApplicationContext()).load(ProfileImageRec).into(profile_image);
        recording_name.setText(RecordingName);
        artist_name.setText(UserName);
        if (addedBy != null && RecId != null) {
            try {
                // getJoined_Local();
                getJoined_users(addedBy, RecId);

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String dateString = sdf.format(date);
        recording_date.setText(dateString);
        melody_date.setText(dateString);

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
//        if (pos.equals("0")) {
//            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, pos);
//            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
//            recyclerViewInstruments.setAdapter(adapter1);
//        } else {
//            pos = intent.getExtras().getString("Value");
//            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, pos);
//            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
//            recyclerViewInstruments.setAdapter(adapter1);
//        }

    }


    public void getJoined_users(final String addedBy, final String RecId) {
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, JOINED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);
                        Joined_artist.clear();
                        instrumentList.clear();
                        listProfile.clear();
                        new ParseContents(getApplicationContext()).parseJoin(response, Joined_artist);
                        adapter = new JoinListAdapter(Joined_artist, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

//                        if (position == 0) {
//                            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, pos);
//                            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
//                            recyclerViewInstruments.setAdapter(adapter1);
//                        } else {
//
//                            //     Intent intent1 = getIntent();
//                            //     pos = intent1.getExtras().getString("Value");
//                            new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, String.valueOf(position));
//                            adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
//                            recyclerViewInstruments.setAdapter(adapter1);
//                        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if (JoinListAdapter.mp != null) {
            JoinListAdapter.mp.stop();
        }
        if (JoinListAdapter.mRecordingThread != null) {
            JoinListAdapter.mRecordingThread.stopRunning();
            JoinListAdapter.mRecordingThread = null;
            //    mShouldContinue=true;
        }

        if (JoinInstrumentListAdp.mp_start != null) {
            for (int i = 0; i <= JoinInstrumentListAdp.mp_start.size() - 1; i++) {
                JoinInstrumentListAdp.mp_start.get(i).stop();

            }
        }
        if (mediaPlayersAll != null) {
            for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                mediaPlayersAll.get(i).stop();
            }
            mediaPlayersAll.clear();
            lstViewHolder.clear();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (JoinListAdapter.mp != null) {
            JoinListAdapter.mp.stop();
        }
        if (JoinListAdapter.mRecordingThread != null) {
            JoinListAdapter.mRecordingThread.stopRunning();
            JoinListAdapter.mRecordingThread = null;
            //    mShouldContinue=true;
        }

        if (JoinInstrumentListAdp.mp_start != null) {
            for (int i = 0; i <= JoinInstrumentListAdp.mp_start.size() - 1; i++) {
                JoinInstrumentListAdp.mp_start.get(i).stop();

            }
        }
        if (mediaPlayersAll != null) {
            for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                mediaPlayersAll.get(i).stop();
            }
            mediaPlayersAll.clear();
            lstViewHolder.clear();
            try {
                if (pauseAll.getVisibility() == View.VISIBLE) {
                    pauseAll.setVisibility(GONE);
                    playAll.setVisibility(View.VISIBLE);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }


    }


}
