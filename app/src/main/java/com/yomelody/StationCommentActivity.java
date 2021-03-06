package com.yomelody;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.yomelody.Adapters.RecordingsCardAdapter;
import com.yomelody.Models.ActivityModel;
import com.yomelody.Models.Comments;
import com.yomelody.Models.RecordingsModel;
import com.yomelody.Models.RecordingsPool;
import com.yomelody.Parse.ParseContents;
import com.yomelody.utils.AppHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.COMMENTS;
import static com.yomelody.utils.Const.ServiceType.COMMENT_LIST;
import static com.yomelody.utils.Const.ServiceType.JoinRecording;
import static com.yomelody.utils.Const.ServiceType.LIKESAPI;
import static com.yomelody.utils.Const.ServiceType.PLAY_COUNT;
import static com.yomelody.utils.Const.ServiceType.viewPost;
import static com.yomelody.utils.RMethod.getServerDiffrenceDate;

public class StationCommentActivity extends AppCompatActivity {

    TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
    TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount, txtJoinCount, TemptxtJoinCount;
    ImageView userProfileImage, ivRecordingCover, ivLikeButton, ivCommentButton, ivShareButton, ivDislikeButton, ivStationPre, ivStationNext;
    ImageView ivJoin, ivStationPlay, ivStationPause, ivBackButton, ivHomeButton;
    TextView tvCancel, tvSend;
    EditText etComment;
    SeekBar seekBarRecordings;
    RelativeLayout rlProfilePic, rlLike;
    ProgressDialog progressDialog;
    RecordingsModel recordingsModel;
    RecordingsPool recordingsPool;
    private String userId;
    private Activity mActivity;
    RecyclerView recyclerView;

    String Topic = "topic";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String LIKES = "likes";
    String TYPE = "type";

    MediaPlayer mp = null;
    String instrumentFile;
    String position;
    //    public static ArrayList<JoinRecordingModel> JoinList = new ArrayList<JoinRecordingModel>();
    public ArrayList<MediaPlayer> JoinMp = new ArrayList<MediaPlayer>();

    String USER_TYPE = "user_type";
    String USERID = "userid";
    String FILEID = "fileid";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    int duration1, length;

    ArrayList<Comments> commentList = new ArrayList<>();
    private CommentsAdapter adapter;

    String COMMENT = "comment";
    String FILE_TYPE = "file_type";
    String TOPIC = "topic";
    private Handler mHandler1;

    private String totaljoincount = "";
    private int MinJoinCount = 1, HolderJoinCount = 1;
    private int PlayCounter = 0;
    private int PreviousAdapterIndex = 0, CurrentAdapterIndex = 0, FirstIndex = 0;
    int includedCount = 1, TempJoinCount = 0;
    private ActivityModel activityModel;
    private boolean isLastInstrument=false;
//    private Chronometer chrono;
    private long mStartTime = 0;
    private long time;
    String fileType="";
    String fileId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_comment);
        mActivity = StationCommentActivity.this;
        userProfileImage = (ImageView) findViewById(R.id.userProfileImage);
        ivRecordingCover = (ImageView) findViewById(R.id.ivRecordingCover);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvRecordingName = (TextView) findViewById(R.id.tvRecordingName);
        tvRecordingDate = (TextView) findViewById(R.id.tvRecordingDate);
        tvRecordingGenres = (TextView) findViewById(R.id.tvRecordingGenres);
        tvContributeDate = (TextView) findViewById(R.id.tvContributeDate);
        tvContributeLength = (TextView) findViewById(R.id.tvContributeLength);
        tvIncludedCount = (TextView) findViewById(R.id.tvIncludedCount);
        tvViewCount = (TextView) findViewById(R.id.tvViewCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        ivJoin = (ImageView) findViewById(R.id.ivJoin);
        ivStationPlay = (ImageView) findViewById(R.id.ivStationPlay);
        ivStationPause = (ImageView) findViewById(R.id.ivStationPause);
        seekBarRecordings = (SeekBar) findViewById(R.id.seekBarRecordings);
        rlProfilePic = (RelativeLayout) findViewById(R.id.rlProfilePic);
        ivLikeButton = (ImageView) findViewById(R.id.ivLikeButton);
        ivDislikeButton = (ImageView) findViewById(R.id.ivDislikeButton);
        ivCommentButton = (ImageView) findViewById(R.id.ivCommentButton);
        ivShareButton = (ImageView) findViewById(R.id.ivShareButton);
        rlLike = (RelativeLayout) findViewById(R.id.rlLike);
        ivStationPre = (ImageView) findViewById(R.id.ivStationPre);
        ivStationNext = (ImageView) findViewById(R.id.ivStationNext);
        txtJoinCount = (TextView) findViewById(R.id.txtJoinCount);
        TemptxtJoinCount = (TextView) findViewById(R.id.TemptxtJoinCount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewComment);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvSend = (TextView) findViewById(R.id.tvSend);
        etComment = (EditText) findViewById(R.id.etComment);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
//        chrono = (Chronometer) findViewById(R.id.chrono);

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

        if (getIntent()!=null && getIntent().hasExtra("Activity_Model")){
            activityModel = (ActivityModel) getIntent().getSerializableExtra("Activity_Model");
            AppHelper.sop("activityModel=="+activityModel+"=ID="+activityModel.getId_());
            if (activityModel!=null){
                getPost();
            }
        }
        else {
            setData();
            adapterWork();
        }
        /*else if (getIntent()!=null && getIntent().hasExtra("notification_bundle")){
            AppHelper.sop("notification_bun=="+getIntent().getBundleExtra("notification_bundle"));
            Bundle bundle=getIntent().getBundleExtra("notification_bundle");
            fileType=""+bundle.getString("file_type");
            fileId=""+bundle.getString("file_id");
            if (!TextUtils.isEmpty(fileType) && !TextUtils.isEmpty(fileId) ){
                getPost();
            }
        }*/


        ivJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!userId.equals("") && userId != null) {
                        String instruments, bpm, genre, recordName, userName, duration, date, plays, likes, comments, shares, melodyID;


                        genre = tvRecordingGenres.getText().toString().trim();
                        recordName = tvRecordingName.getText().toString().trim();
                        duration = tvContributeLength.getText().toString().trim();
                        date = tvRecordingDate.getText().toString().trim();
                        plays = tvViewCount.getText().toString().trim();
                        userName = tvUserName.getText().toString().trim();
                        likes = tvLikeCount.getText().toString().trim();
                        comments = tvCommentCount.getText().toString().trim();
                        shares = tvShareCount.getText().toString().trim();

//                    int pos = getAdapterPosition();

                        SharedPreferences.Editor editor = getSharedPreferences("commentData", MODE_PRIVATE).edit();


                        editor.putString("genre", genre);
                        editor.putString("melodyName", recordName);
                        editor.putString("userName", userName);
                        editor.putString("duration", duration);
                        editor.putString("date", date);
                        editor.putString("plays", plays);
                        editor.putString("likes", likes);
                        editor.putString("comments", comments);
                        editor.putString("shares", shares);
//                    editor.putString("bitmapProfile", profile);
//                    editor.putString("bitmapCover", cover);
//                    editor.putString("melodyID", );
                        editor.putString("fileType", "admin_melody");
                        editor.commit();

                        try {

                            RecordingsModel rm = recordingsModel;

                            SharedPreferences.Editor record = getSharedPreferences("RecordingData", MODE_PRIVATE).edit();
                            record.putString("AddedBy", rm.getAddedBy());
                            record.putString("Recording_id", rm.getRecordingId());
                            record.putString("UserNameRec", rm.getUserName());
                            record.putString("UserProfile", rm.getUserProfilePic());
                            record.putString("RecordingName", rm.getRecordingName());
                            record.commit();
                            Intent intent = new Intent(mActivity, JoinActivity.class);
                            startActivity(intent);
                            killMediaPlayer();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(mActivity, "Log in to join this Recording", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mActivity, SignInActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        rlLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String position;
                try {
                    String MelodyName;
                    if (!userId.equals("") && userId != null) {
                        //Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
                        //position = mpids.get(getAdapterPosition() + 1);

//                        RecordingsModel recording = recordingsModel;
                        if (recordingsModel != null) {
                            MelodyName = recordingsModel.getRecordingName();
                            position = recordingsModel.getRecordingId();

                            if (ivDislikeButton.getVisibility() == VISIBLE) {
                                ivLikeButton.setVisibility(VISIBLE);
                                ivDislikeButton.setVisibility(GONE);
                                String like = tvLikeCount.getText().toString().trim();
                                int likeValue = Integer.parseInt(like) - 1;
                                like = String.valueOf(likeValue);
                                tvLikeCount.setText(like);
                                fetchLikeState(userId, position, "0", MelodyName);


                            } else if (ivDislikeButton.getVisibility() == GONE) {
                                ivLikeButton.setVisibility(GONE);
                                ivDislikeButton.setVisibility(VISIBLE);
                                String like = tvLikeCount.getText().toString().trim();
                                int likeValue = Integer.parseInt(like) + 1;
                                like = String.valueOf(likeValue);
                                tvLikeCount.setText(like);
                                fetchLikeState(userId, position, "1", MelodyName);
                            }
                        }

                    } else {
                        Toast.makeText(mActivity, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mActivity, SignInActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        ivShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!userId.equals("") && userId != null) {

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
                    alertDialog.setTitle(mActivity.getString(R.string.share_with_YoMelody));
//                        alertDialog.setMessage("Choose yes to share in chat.");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("audioShareData", MODE_PRIVATE).edit();
                                RecordingsModel recording = recordingsModel;
                                if (recording != null) {
                                    editor.putString("recID", recording.getRecordingId());
                                    editor.apply();
                                    Intent intent = new Intent(mActivity, MessengerActivity.class);
                                    intent.putExtra("commingForm", "Station");
                                    intent.putExtra("share", recording);
                                    intent.putExtra("file_type", "user_recording");
                                    startActivityForResult(intent, RecordingsCardAdapter.REQUEST_RECORDING_COMMENT);
                                    finish();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            try {
                                RecordingsModel recording = recordingsModel;
                                if (recording != null) {
                                    String RecordingURL = recording.getThumnailUrl();
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
//                                    shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, recording.getRecordingName());
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, recording.getThumnailUrl());

                                    /*shareIntent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.listen_to)+
                                            " "+recording.getRecordingName()+"\n"+mActivity.getString(R.string.yomelody_music)
                                            +"\n"+RecordingURL);*/
                                    shareIntent.setType("text/plain");
                                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(Intent.createChooser(shareIntent, "Choose Sharing option!"));
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    });
                    alertDialog.show();

                } else {
                    Toast.makeText(mActivity, "Log in to Share this melody pack", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mActivity, SignInActivity.class);
                    startActivity(intent);
                }

            }
        });

        rlProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (recordingsModel != null) {
                        Intent intent = new Intent(mActivity, ProfileActivity.class);
                        intent.putExtra("showProfileUserId", recordingsModel.getAddedBy());
                        startActivity(intent);
                        killMediaPlayer();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        ivStationPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (FirstIndex == 0) {
                    FirstIndex = 1;
                    TempJoinCount = recordingsModel.getJoinUrl().size();
                }

                if (PreviousAdapterIndex != CurrentAdapterIndex) {
                    PlayCounter = 0;
                    MinJoinCount = 1;

                    try {
                        if (mHandler1 != null) {
                            mHandler1.removeCallbacksAndMessages(null);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                        /*lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                        lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                        SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
                        seekBar.setProgress(0);*/


                    PreviousAdapterIndex = CurrentAdapterIndex;
                    TempJoinCount = recordingsModel.getJoinUrl().size();
                }

                try {
                    if (PlayCounter >= 0) {
                        //lastModifiedHoled = holder;
                        if (isLastInstrument){
                            PlayCounter = 0;
                            MinJoinCount = 1;
                            isLastInstrument=false;
                        }
                        txtJoinCount.setText(UpdateCalJoinCount(TempJoinCount));
                        PlayAudio(0, "main");
                        //lastModifiedHoled = holder;

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        ivStationPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivStationPlay.setVisibility(v.VISIBLE);
                ivStationPause.setVisibility(v.GONE);
                if (mp != null) {
                    try {
                        length = mp.getCurrentPosition();
                        mp.stop();
                        mp.reset();
                        mp = null;
                        pauseTime();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                // seekBarRecordings.setProgress(0);
            }
        });


        ivStationPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (PlayCounter <= TempJoinCount - 1 && PlayCounter != 0) {
                        //lastModifiedHoled = holder;
                        PlayCounter = PlayCounter - 1;
                        stopTime();
                        PlayAudio(0, "pre");

                        //lastModifiedHoled = holder;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        ivStationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (PlayCounter < TempJoinCount - 1) {
                        //lastModifiedHoled = holder;
                        PlayCounter = PlayCounter + 1;
                        stopTime();
                        PlayAudio(0, "next");
                        //lastModifiedHoled = holder;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCancel.setVisibility(View.GONE);
                tvSend.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etComment.getText().toString().length() == 0) {
                    tvCancel.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);
                }
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* SharedPreferences prefsActivity = getSharedPreferences("PreviousActivity", MODE_PRIVATE);
                String PrevAct = prefsActivity.getString("instruments", null);
                Intent intent = new Intent(getApplicationContext(),);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();

            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (!userId.equals("") && userId != null) {

                        tvCancel.setVisibility(View.VISIBLE);
                        tvSend.setVisibility(View.GONE);
                        String comment = etComment.getText().toString().trim();
                        etComment.getText().clear();
                        sendComment(comment, userId);


                    } else {
                        Toast.makeText(getApplicationContext(), "Log in to comment", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    etComment.getText().clear();
                    Intent i = new Intent(StationCommentActivity.this, StationActivity.class);
                    startActivity(i);
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


    }

    private void adapterWork() {
        try {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(lm);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(10);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            adapter = new CommentsAdapter(getApplicationContext(), commentList);
            recyclerView.setAdapter(adapter);
            getComments();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void setData() {
        try {
            String totaljoincount = "";
            if (getIntent() != null) {
                if (recordingsModel==null){
                    recordingsModel = (RecordingsModel) getIntent().getSerializableExtra("recording_modle");
                }
                if (recordingsPool==null){
                    recordingsPool = (RecordingsPool) getIntent().getSerializableExtra("recording_pool");
                }

                tvUserName.setText(recordingsModel.getUserName());
                tvRecordingName.setText(recordingsModel.getRecordingName());
                tvIncludedCount.setText("Included: " + recordingsModel.getJoinCount());
                tvRecordingGenres.setText("Genre:" + " " + recordingsModel.getGenreName());
                tvContributeLength.setText(DateUtils.formatElapsedTime(Long.parseLong(recordingsPool.getDuration())));
//                chrono.setText(DateUtils.formatElapsedTime(Long.parseLong(recordingsPool.getDuration())));
                tvContributeDate.setText(convertDate(recordingsPool.getDateAdded()));

                if (recordingsModel.getJoinCount() == null) {
                    totaljoincount = "(" + "1" + " of " + "1" + ")";
                } else {
                    totaljoincount = CalJoinCount(Integer.parseInt(recordingsModel.getJoinCount()));
                }

                if (recordingsModel.getJoinCount() != null) {
                    TemptxtJoinCount.setText(recordingsModel.getJoinCount());
                } else {
                    TemptxtJoinCount.setText("0");
                }

                txtJoinCount.setText(totaljoincount);

                txtJoinCount.setText(totaljoincount);
                Picasso.with(this).load(recordingsModel.getUserProfilePic()).into(userProfileImage);
                Picasso.with(this)
                        .load(recordingsModel.getRecordingCover())
                        .placeholder(R.drawable.bg_cell)
                        .error(R.drawable.bg_cell)
                        .into(ivRecordingCover);

                tvRecordingDate.setText(convertDate(recordingsPool.getDateAdded()));
//                tvViewCount.setText(getIntent().getStringExtra("play_count"));
                tvViewCount.setText(""+recordingsModel.getPlayCount());
//                tvLikeCount.setText(getIntent().getStringExtra("likes"));
                tvLikeCount.setText(""+recordingsModel.getLikeCount());
                tvCommentCount.setText(String.valueOf(recordingsModel.getCommentCount()));
                tvShareCount.setText(""+recordingsModel.getShareCount());

//                String likeStatus = getIntent().getStringExtra("LikeStatus");
                String likeStatus = recordingsModel.getLikeStatus()+"";
                if (likeStatus.equalsIgnoreCase("0")) {
                    ivDislikeButton.setVisibility(GONE);
                } else {
                    ivDislikeButton.setVisibility(VISIBLE);
                }
                AppHelper.sop("likeStatus======" + likeStatus + "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void fetchPlayJoinAudio(final String RecId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JoinRecording,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject, respObject;
                        JSONArray jsonArray;

                        try {
//                            JoinList.clear();
//                            currentSongIndex = 0;
                            JoinMp.clear();

                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equals("success")) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                    respObject = jsonArray.getJSONObject(i);
                                    //JoinList.add(i, new JoinRecordingModel(respObject.getString("recording_duration"), respObject.getString("recording_url")));
                                    try {
                                        MediaPlayer mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mediaPlayer.setDataSource(respObject.getString("recording_url"));
                                        //mediaPlayer.prepare();
                                        JoinMp.add(mediaPlayer);

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //       Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("rid", RecId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(mActivity);
        requestQueue1.add(stringRequest);
    }

    private String CalJoinCount(int joincount) {
        String jCount = "";
        try {
            if (joincount == 0) {
                //currentSongIndex=1;
                jCount = "(" + String.valueOf(HolderJoinCount) + " of " + String.valueOf((joincount)) + ")";
            } else {

                jCount = String.valueOf(HolderJoinCount) + " of " + String.valueOf(joincount);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

    public void fetchLikeState(final String userId, final String pos, final String likeState, final String LikeMelodyName) {

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LIKESAPI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                            try {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("LIKE", "Like");
                                setResult(Activity.RESULT_OK, resultIntent);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
                            String errorMsg = error.toString();
                            Log.d("Error", errorMsg);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Topic, LikeMelodyName);
                    params.put(USER_ID, userId);
                    params.put(FILE_ID, pos);
                    params.put(TYPE, "user_recording");
                    params.put(LIKES, likeState);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(mActivity);
            requestQueue1.add(stringRequest);
        } catch (Exception ex) {

        }

    }

    public void fetchViewCount(final String userId, final String pos) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAY_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject, respObject;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                respObject = jsonObject.getJSONObject(KEY_RESPONSE);
                                String str = respObject.getString("play_count");

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("PLAY", "Play");
                                setResult(Activity.RESULT_OK, resultIntent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(USER_TYPE, "user");
                params.put(USERID, userId);
                params.put(FILEID, pos);
                //    params.put(TYPE, "admin_melody");
                params.put(TYPE, "recording");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(mActivity);
        requestQueue1.add(stringRequest);
    }

    private void primarySeekBarProgressUpdater() {
        mHandler1 = new Handler();
        try {
            ///    duration1 = mp.getDuration();
            seekBarRecordings.setProgress((int) (((float) mp.getCurrentPosition() / duration1) * 100));// This math construction give a percentage of "was playing"/"song length"
            if (mp != null && mp.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                mHandler1.postDelayed(notification, 100);
            } else {
                try {
                    seekBarRecordings.setProgress(0);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private String CalJoinCountNextRec(int Nextcount) {
        String jCount = "";
        try {
            MinJoinCount = MinJoinCount + 1;
            String tempN = String.valueOf(MinJoinCount);
            jCount = "(" + (tempN) + " of " + String.valueOf(Nextcount) + ")";


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

    private String CalJoinCountPrevRec(int Precount) {
        String jCount = "";
        try {
            //MinJoinCount=MinJoinCount-1;
            String tempP = String.valueOf(MinJoinCount);
            jCount = "(" + (tempP) + " of " + String.valueOf(Precount) + ")";


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

    public void getComments() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AppHelper.sop("response==" + response);
                            commentList.clear();


                            new ParseContents(getApplicationContext()).parseComments(response, commentList);
                            adapter.notifyDataSetChanged();
//                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                            recyclerView.scrollToPosition(commentList.size() - 1);
                        } catch (Exception ex) {
                            ex.printStackTrace();
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
//                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
//                            errorMsg = "ParseError";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(FILE_ID, recordingsModel.getRecordingId());
                params.put(FILE_TYPE, "user_recording");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params==" + params + "\nURL==" + COMMENT_LIST);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void sendComment(final String cmnt, final String userId) {

        AppHelper.hideSoftKeyboard(mActivity);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AppHelper.sop("response==" + response);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("PARAMETER", "Like");
                            setResult(Activity.RESULT_OK, resultIntent);

                            int commentCount = Integer.parseInt(tvCommentCount.getText().toString().trim()) + 1;

                            tvCommentCount.setText(String.valueOf(commentCount));
//                        adapter.notifyDataSetChanged();
                            getComments();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
//                        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
//                      new ParseContents(getApplicationContext()).parseComments(response, commentList);
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
//                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
//                            errorMsg = "ParseError";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(FILE_ID, recordingsModel.getRecordingId());
                params.put(COMMENT, cmnt);
                params.put(FILE_TYPE, "user_recording");
                params.put(USER_ID, userId);
                params.put(TOPIC, recordingsModel.getRecordingName());
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params==" + params + "\nURL==" + COMMENTS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getPost() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, viewPost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AppHelper.sop("response==" + response);

                            JSONObject jsonObject, json;
                            JSONArray instrumentArray, JoinedArray;

                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                json = jsonObject.getJSONObject(KEY_RESPONSE);
                                recordingsModel = new RecordingsModel();
                                JSONObject cardJson = json;
                                recordingsModel.setAddedBy(cardJson.getString("added_by"));

                                recordingsModel.setRecordingCreated(cardJson.getString("date_added"));
                                recordingsModel.setGenreId(cardJson.getString("genre"));
                                recordingsModel.setRecordingName(cardJson.getString("recording_topic"));
                                recordingsModel.setUserName(cardJson.getString("user_name"));
                                recordingsModel.setRecordingId(cardJson.getString("recording_id"));
                                recordingsModel.setPlayCount(cardJson.getInt("play_count"));
                                recordingsModel.setCommentCount(cardJson.getInt("comment_count"));
                                recordingsModel.setLikeCount(cardJson.getInt("like_count"));
                                recordingsModel.setJoinCount(cardJson.getString("join_count"));
                                recordingsModel.setLikeStatus(cardJson.getInt("like_status"));
                                recordingsModel.setJoinCount(cardJson.getString("join_count"));
                                recordingsModel.setThumnailUrl(cardJson.getString("thumbnail_url"));

                                if (cardJson.isNull("recording_url")) {
                                    recordingsModel.setrecordingurl("");
                                } else {
                                    recordingsModel.setrecordingurl(cardJson.getString("recording_url"));
                                }

                                recordingsModel.setShareCount(cardJson.getInt("share_count"));
                                recordingsModel.setRecordingCover(cardJson.getString("cover_url"));
                                recordingsModel.setUserProfilePic(cardJson.getString("profile_url"));
                                recordingsModel.setGenreName(cardJson.getString("genre_name"));

                                ArrayList arrayList = new ArrayList();


                                JoinedArray = cardJson.getJSONArray("joined");

                                try {
                                    if (JoinedArray.length() > 0) {
                                        for (int j = 0; j < JoinedArray.length(); j++) {
                                            JSONObject jsonObject1 = new JSONObject(JoinedArray.get(j).toString());

                                            //JSONArray jsonArray1=new JSONArray(jsonObject1.getJSONArray(jsonObject1));
                                            arrayList.add(jsonObject1.get("recording_url"));
                                        }
                                        recordingsModel.setJoinUrl(arrayList);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                if (cardJson.isNull("recordings")) {
                                    Log.d("Error", "pls check");
                                } else {
                                    instrumentArray = cardJson.getJSONArray("recordings");

                                    for (int j = 0; j < instrumentArray.length(); j++) {
                                        recordingsPool = new RecordingsPool();
                                        JSONObject instrumentJson = instrumentArray.getJSONObject(j);
                                        recordingsPool.setAddedById(instrumentJson.getString("added_by_id"));
                                        recordingsPool.setUserName(instrumentJson.getString("user_name"));
                                        recordingsPool.setName(instrumentJson.getString("name"));
                                        recordingsPool.setCoverUrl(instrumentJson.getString("cover_url"));
                                        recordingsPool.setProfileUrl(instrumentJson.getString("profile_url"));
                                        recordingsPool.setDateAdded(instrumentJson.getString("date_added"));
                                        recordingsPool.setDuration(instrumentJson.getString("duration"));
                                        recordingsPool.setRecordingUrl(instrumentJson.getString("recording_url"));
                                        recordingsPool.setInstruments(instrumentJson.getString("instruments"));
                                    }
                                }
                                setData();
                                adapterWork();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
//                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
//                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (activityModel!=null){
                    if (activityModel.getRecordingID() != null) {
                        params.put(FILE_ID, activityModel.getRecordingID() + "");
                        params.put(FILE_TYPE, "user_recording");
                    } else if (activityModel.getMelodyID() != null) {
                        params.put(FILE_ID, activityModel.getMelodyID() + "");
                        params.put(FILE_TYPE, "user_melody");
                    } else if (activityModel.getAdminmelodyid() != null) {
                        params.put(FILE_ID, activityModel.getAdminmelodyid() + "");
                        params.put(FILE_TYPE, "admin_melody");
                    }
                }/*else if (!TextUtils.isEmpty(fileType) && !TextUtils.isEmpty(fileId) ){
                    params.put(FILE_ID, fileId+"");
                    params.put(FILE_TYPE, fileType+"");
                }*/
                params.put(USER_ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params==" + params + "\nURL==" + viewPost);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

        Context context;
        ArrayList<Comments> commentList = new ArrayList<>();

        public CommentsAdapter(Context context, ArrayList<Comments> commentList) {
            this.commentList = commentList;
            this.context = context;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvRealName, tvUsername, tvMsg, tvTime;
            ImageView userProfileImage;


            public MyViewHolder(final View itemView) {
                super(itemView);

                userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
                tvRealName = (TextView) itemView.findViewById(R.id.tvRealName);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
                tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);

                userProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String showProfileUserId = commentList.get(getAdapterPosition()).getUser_id();
                            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                            intent.putExtra("showProfileUserId", showProfileUserId);
                            view.getContext().startActivity(intent);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int listPosition) {
            try {
                Comments comments = commentList.get(listPosition);
                Picasso.with(holder.userProfileImage.getContext()).load(comments.getUserProfileImage()).into(holder.userProfileImage);
                holder.tvRealName.setText(comments.getTvRealName());
                holder.tvUsername.setText("@" + comments.getTvUsername());
                holder.tvTime.setText(DateTime(comments.getTvTime()));
                holder.tvMsg.setText(comments.getTvMsg());
                //DateTime(c.getString(comments.getTvTime())
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public String DateTime(String send_at) {
            String val = "";
            val = getServerDiffrenceDate(send_at);
            return val;
        }

    }

    private String convertDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
            Date d = format.parse(date);
            SimpleDateFormat serverFormat = new SimpleDateFormat("MM/dd/yy");
            return serverFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        killMediaPlayer();
    }

    private void killMediaPlayer(){
        try {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp.reset();
                mp.release();
                mp = null;
                if (mHandler1 != null) {
                    mHandler1.removeCallbacksAndMessages(null);
                }
                stopTime();
                ivStationPlay.setVisibility(VISIBLE);
                ivStationPause.setVisibility(GONE);
                seekBarRecordings.setProgress(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PlayAudio(final int pisition, final String Type) {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        /*chrono.setBase(SystemClock.elapsedRealtime());
        chrono.stop();*/

        try {
            /*if (isLastInstrument && !(Type.equalsIgnoreCase("pre"))){
                isLastInstrument=false;
                PlayCounter = 0;
                MinJoinCount = 1;
                txtJoinCount.setText(UpdateCalJoinCount(TempJoinCount));
            }*/
            AppHelper.sop("PlayCounter="+PlayCounter+"=MinJoinCount="+MinJoinCount);
            instrumentFile = recordingsModel.getJoinUrl().get(PlayCounter).toString();// recordingList.get(pisition).getJoinUrl().get(PlayCounter).toString();
            if (instrumentFile != "") {
                try {
                    if (mp != null) {
                        try {

                            try {
                                mp.stop();
                                mp.release();
                                mp = null;
                                duration1 = 0;
                                length = 0;

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                mp = new MediaPlayer();
                try {
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                try {
                    mp.setDataSource(instrumentFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.prepareAsync();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        try {

                            mp.seekTo(length);
                            mp.start();
                            fetchViewCount(userId, position);
                            progressDialog.dismiss();
                            duration1 = mp.getDuration();
                            primarySeekBarProgressUpdater();
                            /*chrono.setFormat("00:"+"%s");
                            chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.start();*/
                            startTime();

                            if (Type == "main") {

                                //MinJoinCount = MinJoinCount + 1;
                            } else if (Type == "next") {

                                MinJoinCount = MinJoinCount + 1;

                                //tvIncludedCount.setText(UpdateCalJoinCount(TempJoinCount));
                                txtJoinCount.setText(UpdateCalJoinCount(TempJoinCount));
                            } else if (Type == "pre") {

                                MinJoinCount = MinJoinCount - 1;

                                //tvIncludedCount.setText(UpdateCalJoinCount(TempJoinCount));
                                txtJoinCount.setText(UpdateCalJoinCount(TempJoinCount));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

                        try {
                            ivStationPlay.setVisibility(VISIBLE);
                            ivStationPause.setVisibility(GONE);
                            progressDialog.dismiss();
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }
                            /*chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.stop();
                            chrono.setText(DateUtils.formatElapsedTime(Long.parseLong(recordingsPool.getDuration())));*/
                            stopTime();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return false;
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }
                            ivStationPlay.setVisibility(VISIBLE);
                            ivStationPause.setVisibility(GONE);
                            seekBarRecordings.setProgress(0);
                            length = 0;
                            duration1 = 0;
                            /*chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.stop();
                            chrono.setText(DateUtils.formatElapsedTime(Long.parseLong(recordingsPool.getDuration())));*/
                            stopTime();
                            if (PlayCounter < recordingsModel.getJoinUrl().size()-1){
                                PlayCounter = PlayCounter + 1;
                                MinJoinCount = MinJoinCount + 1;
                                txtJoinCount.setText(UpdateCalJoinCount(TempJoinCount));
                                PlayAudio(pisition,"main");
                            }else {
                                isLastInstrument=true;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });

                ivStationPlay.setVisibility(GONE);
                ivStationPause.setVisibility(VISIBLE);
            } else {
                Toast.makeText(StationCommentActivity.this, "Recording URL not found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ivStationPlay.setVisibility(VISIBLE);
            ivStationPause.setVisibility(GONE);
            ex.printStackTrace();
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mTickExecutor = new Runnable() {
        @Override
        public void run() {
            tick();
            mHandler.postDelayed(mTickExecutor,100);
        }
    };

    private void startTime(){
        mStartTime = SystemClock.elapsedRealtime()-time;
        mHandler.postDelayed(mTickExecutor, 00);
    }

    private void stopTime(){
        mStartTime = 0;
        time=0;
        mHandler.removeCallbacks(mTickExecutor);
        tvContributeLength.setText(DateUtils.formatElapsedTime(Long.parseLong(recordingsPool.
                getDuration())));

    }

    private void pauseTime(){
        mStartTime = time;
        mHandler.removeCallbacks(mTickExecutor);
    }

    private void tick() {
        time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time / 100) % 10;
        tvContributeLength.setText((minutes < 10 ? "0"+minutes : minutes)+":"+
                (seconds < 10 ? "0"+seconds : seconds));
        /*if (mRecorder != null) {
            amplitudes[i] = mRecorder.getMaxAmplitude();
            //Log.d("Voice Recorder","amplitude: "+(amplitudes[i] * 100 / 32767));
            if (i >= amplitudes.length -1) {
                i = 0;
            } else {
                ++i;
            }
        }*/
    }

    private String UpdateCalJoinCount(int joincount) {
        String jCount = "";
        try {
            if (joincount == 0) {
                //currentSongIndex=1;
                jCount = "(" + String.valueOf(MinJoinCount) + " of " + String.valueOf((joincount)) + ")";
            } else {

                jCount = String.valueOf(MinJoinCount) + " of " + String.valueOf(joincount);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

}
