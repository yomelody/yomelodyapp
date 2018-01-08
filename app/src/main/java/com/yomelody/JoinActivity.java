package com.yomelody;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.yomelody.Adapters.JoinInstrumentListAdp;
import com.yomelody.Adapters.JoinListAdapter;
import com.yomelody.Fragments.CommentJoinFragment;
import com.yomelody.Models.JoinedArtists;
import com.yomelody.Models.JoinedUserProfile;
import com.yomelody.Models.MelodyInstruments;
import com.yomelody.Parse.ParseContents;
import com.yomelody.Services.LogoutService;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.Const;
import com.yomelody.utils.VisualizerView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.JOINED_USERS;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class JoinActivity extends AppCompatActivity {
    public static RecyclerView.Adapter adapter, adapter1;
    public static ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();
    public static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    String pos = "0", userId;
    public static Chronometer chrono;
    private static final int SAMPLING_RATE = 44100;
    public static RecyclerView recyclerView;
    public static RecyclerView recyclerViewInstruments;
    LinearLayout rlIncluded;
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
    public static VisualizerView mVisualizerView;
    public static Visualizer mVisualizer;
    TextView tvDone;
    private Activity mActivity;
    public static boolean check_frag = false;
    private ImageView arrowIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        mActivity = JoinActivity.this;
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
        arrowIv = (ImageView) findViewById(R.id.arrowIv);

        mVisualizerView = (VisualizerView) findViewById(R.id.myvisualizerview);
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
        tvDone = (TextView) findViewById(R.id.tvDone);
        rlComment = (RelativeLayout) findViewById(R.id.rlComment);
        rlIncluded = (LinearLayout) findViewById(R.id.rlIncluded);
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
        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }


        rlIncluded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                recyclerView.getVisibility() == View.VISIBLE
                if (adapter != null) {
                    if (((JoinListAdapter) adapter).getHideProfImg().equalsIgnoreCase("hide")) {
//                    recyclerView.setVisibility(View.GONE);

                        ((JoinListAdapter) adapter).setHideProfImg("");
                        arrowIv.setImageResource(R.drawable.down_arrow_img);
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 180));
                        adapter.notifyDataSetChanged();


                    } else {
//                    recyclerView.setVisibility(View.VISIBLE);
                        ((JoinListAdapter) adapter).setHideProfImg("hide");
                        arrowIv.setImageResource(R.drawable.right_arrow_img);
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
                        adapter.notifyDataSetChanged();
                    }
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
                    CommentJoinFragment af = new CommentJoinFragment();
                    getFragmentManager().beginTransaction().replace(R.id.commentContainer, af).commit();
                    check_frag = true;
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
                    JoinListAdapter.mp.reset();
                }
                if (mVisualizer != null) {
                    mVisualizer.release();
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
                    JoinListAdapter.mp.reset();
                }
                if (mVisualizer != null) {
                    mVisualizer.release();
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
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivBackButton.performClick();
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JoinListAdapter.mp != null) {
                    JoinListAdapter.mp.reset();
                }
                if (mVisualizer != null) {
                    mVisualizer.release();
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
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                        adapter = new JoinListAdapter(Joined_artist, mActivity);
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
                params.put("Myloginid", userId);
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
            JoinListAdapter.mp.reset();
        }
        if (mVisualizer != null) {
            mVisualizer.release();
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
            JoinListAdapter.mp.reset();
        }
        if (mVisualizer != null) {
            mVisualizer.release();
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
        //startService(new Intent(this, LogoutService.class));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("JoinActivity onactivity Result" + "requestCode" + requestCode + ",resultCode" + resultCode + ",data" + data + "mActivity" + mActivity.RESULT_OK);
        if (JoinListAdapter.REQUEST_JOIN_TO_MESSANGER == requestCode) {
            if (resultCode == mActivity.RESULT_OK) {
                AppHelper.sop("onActivityResult==called=" + "resultCode==" + resultCode);
            } else {
                SharedPreferences socialStatusPref = mActivity.getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE);
                if (socialStatusPref.getBoolean(Const.REC_SHARE_STATUS, false)) {
                    SharedPreferences.Editor socialStatusPrefEditor = mActivity.getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE).edit();
                    socialStatusPrefEditor.putBoolean(Const.REC_SHARE_STATUS, false);
                    socialStatusPrefEditor.apply();
                    if (addedBy != null && RecId != null) {
                        try {
                            getJoined_users(addedBy, RecId);
                            Intent it = new Intent();
                            setResult(Activity.RESULT_OK, it);
                            if (check_frag) {
                                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.commentContainer)).commit();
                                joinFooter.setVisibility(View.VISIBLE);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }

    }
}
