package com.instamelody.instamelody;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.instamelody.instamelody.Adapters.InstrumentListAdapter;
import com.instamelody.instamelody.Adapters.JoinInstrumentListAdp;
import com.instamelody.instamelody.Adapters.MelodyCardListAdapter;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.MelodyMixing;
import com.instamelody.instamelody.Models.MixingData;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.instamelody.instamelody.utils.VolleySingleton;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static com.instamelody.instamelody.SignInActivity.TWITTER_CONSUMER_KEY;
import static com.instamelody.instamelody.SocialActivity.TWITTER_CONSUMER_SECRET;
import static com.instamelody.instamelody.utils.Const.ServiceType.ADD_RECORDINGS;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.JOINED_USERS;
import static com.instamelody.instamelody.utils.Const.ServiceType.MELODY;
import static com.instamelody.instamelody.utils.Const.ServiceType.MixingAudio_InstrumentsAudio;
import static com.instamelody.instamelody.utils.Const.ServiceType.UPLOAD_COVER_MELODY_FILE;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class StudioActivity extends AppCompatActivity {

    int duration, idx;
    long startTime;
    long countUp, countUp_milli, timeElapsed;
    String asText;
    String value, value1;
    String userIdNormal, userIdFb, userIdTwitter;
    String userId;
    String switchFlag = "0";
    String instrumentFile;
    String receiveInstruments;
    String packName, addedByUser, coverPick, recGenre, bpm, likeCount, shareCount, commentCount, playCount, melodyUrl, audioFileType, audioFileSize, addDate,
            melodyRecDuration, Public;

    final int MY_PERMISSIONS_REQUEST_MICROPHONE = 200;
    final int MY_PERMISSIONS_REQUEST_STORAGE = 201;
    private static final int SAMPLING_RATE = 44100;
    int MY_SOCKET_TIMEOUT_MS = 30000;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private String FILE_RECORDING = "";
    private String RECORDING_TYPE = "recording_type";
    private String USER_ID = "user_id";
    private String RECORDING_NAME = "topic_name";
    private String RECORDING_GENRE = "genere";
    private String RECORDING_DURATION = "duration";
    private String SHARE_PUBLIC = "public_flag";
    private String RECORDING_BPM = "bpm";
    private String ADMIN_INSTRUMENT_ID = "admin_instruments_ids";
    private String USER_INSTRUMENT_ID = "user_instruments_ids";

    private String FILE_TYPE = "file_type";
    private String IS_MELODY = "isMelody";
    private String ID_MELODY_REC = "melodyOrRecordingID";
    private String FILE1 = "file1";
    private String USER_ID1 = "user_id";
    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    ArrayList<String> genresName = new ArrayList<>();
    ArrayList<String> genresId = new ArrayList<>();
    String KEY_GENRE_ID = "id";
    TwitterAuthClient client;

    List<String> genreList = new ArrayList<>();

    private int mBufferSize;
    private short[] mAudioBuffer;
    //private String mDecibelFormat;
    private View view;
    long elapsedMillis;
    MediaPlayer mPlayer;
    Timer myTimer;
    public static Chronometer chrono;
    ImageView audio_feed, grey_circle, blue_circle;
    public static TextView tvPublic, tvDone, tvInfo, recording_date, melody_date, melody_detail;
    EditText subEtTopicName;
    Spinner sp;
    RadioGroup rgR;
    RadioButton radioButton;
    public static TextView mDecibelView;
    public static com.instamelody.instamelody.utils.WaveformView waveform_view;
    public static RecordingThread mRecordingThread;
    public static MediaRecorder recorder;
    private final int requestCode = 20;
    public static ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();
    static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    public static boolean isRecording = false;
    public static MediaPlayer mediaPlayer;
    public static String audioFilePath;
    private static String instrumentFilePath;
    Uri audioUri;
    public static final Handler handler = new Handler();
    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray

    String firstName, userNameLogin, profilePicLogin, Name, userName, profilePic, fbName, fbUserName, fbId, melodyPackId, joinRecordingId, instrumentCount;
    String selectedGenre;
    int statusNormal, statusFb, statusTwitter;
    String melodyName, instrumentName, joinRecordingName, joinInstrumentName;
    public static Switch switchPublic;
    public static RelativeLayout rlMelodyButton, rlRecordingButton, rlRedoButton, rlListeningButton, rlSetCover, rlInviteButton, rlPublic;
    public static FrameLayout frameTrans, frameSync, frameProgress;
    public static ImageView ivBackButton, ivHomeButton, ivRecord, ivRecord_stop, ivRecord_play, ivRecord_pause, discover, message, ivProfile, ivNewRecordCover;
    CircleImageView profile_image;
    TextView artist_name, noMelodyNote;
    RecyclerView recyclerViewInstruments;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    public static ProgressDialog progressDialog, pDialog;
    public static ProgressBar frameprog;
    // LongOperation myTask = null;
    RelativeLayout rlSync;
    // public static MediaPlayer mpInst;
    static int duration1, currentPosition;
    //SeekBar melodySlider;
    String array[] = {""};
    ArrayList<String> instruments_count = new ArrayList<String>();
    ArrayList<String> instrument_count_Join = new ArrayList<>();
    Timer timer;
    MediaPlayer mp;
    ShareDialog shareDialog;
    FacebookSdk.InitializeCallback i1;
    String fetchRecordingUrl;
    public static byte[] bytes, soundBytes;
    String idUpload;
    int InstrumentCountSize = 0;
    private boolean mShouldContinue = true;
    ArrayList<String> urls;
    MediaPlayer[] media;
    List<MediaPlayer> mps = new ArrayList<MediaPlayer>();
    public static boolean playfrom_studio = false;
    public static String recordingDuration;
    public static long stop_rec_time;
    public static String time_stop;
    int count = 0;
    public static FrameLayout frameInstrument;
    public static RelativeLayout rlFX, rlEQ, eqContent, fxContent, RltvFxButton, RltvEqButton;
    public static TextView tvDoneFxEq, tvInstrumentLength, tvUserName, tvInstrumentName, tvBpmRate;
    public static ImageView userProfileImage, ivInstrumentCover, FramesivPause, FramesivPlay;
    public static SeekBar FramemelodySlider;
    public static SeekBar volumeSeekbar, sbTreble, sbBase, sbPan, sbPitch, sbReverb, sbCompression, sbDelay, sbTempo;
    public static MelodyMixing melodyMixing = new MelodyMixing();
    public static ArrayList<MixingData> list = new ArrayList<MixingData>();
    public static MediaPlayer mpInst;
    String Mixrecording = "recording";
    String MixisMelody = "isMelody";
    String Mixtopic_name = "topic_name";
    String Mixuser_id = "user_id";
    String Mixpublic_flag = "public_flag";
    String MixrecordWith = "recordWith";
    String Mixgenere = "genere";
    String Mixbpms = "bpm";
    String Mixdurations = "duration";
    String Mixvocalsound = "vocalsound";
    String MixCommand = "Command";
    String MixparentRecordingID = "parentRecordingID";
    CallbackManager callbackManager;
    URL ShortUrl;
    boolean fbValue, twitterValue, googleValue;
    public static MediaPlayer mpall;
    public static ArrayList<MediaPlayer> mediaPlayersAll = new ArrayList<MediaPlayer>();
    int TWEETER_REQ_CODE = 0;
    private String RECORDING_ID = "rid";
    private String USERID = "userid";


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);
//        instruments_count = InstrumentListAdapter.instruments_url;
//        Log.d("abc", "" + instruments_count.size());
        frameprog = (ProgressBar) findViewById(R.id.frameProg);
        message = (ImageView) findViewById(R.id.message);
        discover = (ImageView) findViewById(R.id.discover);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        grey_circle = (ImageView) findViewById(R.id.grey_circle);
        blue_circle = (ImageView) findViewById(R.id.blue_circle);
        rlMelodyButton = (RelativeLayout) findViewById(R.id.rlMelodyButton);
        rlRecordingButton = (RelativeLayout) findViewById(R.id.rlRecordingButton);
        rlRedoButton = (RelativeLayout) findViewById(R.id.rlRedoButton);
        rlListeningButton = (RelativeLayout) findViewById(R.id.rlListeningButton);
        ivRecord = (ImageView) findViewById(R.id.ivRecord);
        ivRecord_stop = (ImageView) findViewById(R.id.ivRecord_stop);
        tvPublic = (TextView) findViewById(R.id.tvPublic);
        recording_date = (TextView) findViewById(R.id.recording_date);
        melody_date = (TextView) findViewById(R.id.melody_date);
        switchPublic = (Switch) findViewById(R.id.switchPublic);
        ivRecord_play = (ImageView) findViewById(R.id.ivRecord_play);
        ivRecord_pause = (ImageView) findViewById(R.id.ivRecord_pause);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        artist_name = (TextView) findViewById(R.id.artist_name);
        melody_detail = (TextView) findViewById(R.id.melody_detail);
        rlFX = (RelativeLayout) findViewById(R.id.rlFX);
        rlEQ = (RelativeLayout) findViewById(R.id.rlEQ);
        fxContent = (RelativeLayout) findViewById(R.id.fxContent);
        eqContent = (RelativeLayout) findViewById(R.id.eqContent);
        tvDoneFxEq = (TextView) findViewById(R.id.tvDoneFxEq);
        RltvFxButton = (RelativeLayout) findViewById(R.id.RltvFxButton);
        RltvEqButton = (RelativeLayout) findViewById(R.id.RltvEqButton);
        frameInstrument = (FrameLayout) findViewById(R.id.frameInstrument);
        tvInstrumentLength = (TextView) findViewById(R.id.tvInstrumentLength);
        tvInstrumentName = (TextView) findViewById(R.id.tvInstrumentName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvBpmRate = (TextView) findViewById(R.id.tvBpmRate);
        userProfileImage = (ImageView) findViewById(R.id.userProfileImage);
        ivInstrumentCover = (ImageView) findViewById(R.id.ivInstrumentCover);
        volumeSeekbar = (SeekBar) findViewById(R.id.sbVolume);
        sbTreble = (SeekBar) findViewById(R.id.sbTreble);
        sbBase = (SeekBar) findViewById(R.id.sbBase);
        sbReverb = (SeekBar) findViewById(R.id.sbReverb);
        sbCompression = (SeekBar) findViewById(R.id.sbCompression);
        sbDelay = (SeekBar) findViewById(R.id.sbDelay);
        sbTempo = (SeekBar) findViewById(R.id.sbTempo);
        sbPan = (SeekBar) findViewById(R.id.sbPan);
        sbPitch = (SeekBar) findViewById(R.id.sbPitch);
        FramesivPause = (ImageView) findViewById(R.id.FramesivPause);
        FramesivPlay = (ImageView) findViewById(R.id.FramesivPlay);
        FramemelodySlider = (SeekBar) findViewById(R.id.FramemelodySlider);
        frameProgress = (FrameLayout) findViewById(R.id.frameProgress);
        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        firstName = loginSharedPref.getString("firstName", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        statusNormal = loginSharedPref.getInt("status", 0);
        userIdNormal = loginSharedPref.getString("userId", null);
        SharedPreferences loginFbSharedPref = this.getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
        userIdFb = loginFbSharedPref.getString("userId", null);
        statusFb = loginFbSharedPref.getInt("status", 0);
        SharedPreferences loginTwitterSharedPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        userIdTwitter = loginTwitterSharedPref.getString("userId", null);
        statusTwitter = loginTwitterSharedPref.getInt("status", 0);
        // frameprog.setVisibility(View.GONE);
        if (statusNormal == 1) {
            userId = userIdNormal;
        } else if (statusFb == 1) {
            userId = userIdFb;
        } else if (statusTwitter == 1) {
            userId = userIdTwitter;
        }

        rlSetCover = (RelativeLayout) findViewById(R.id.rlSetCover);
        ivNewRecordCover = (ImageView) findViewById(R.id.ivNewRecordCover);
        chrono = (Chronometer) findViewById(R.id.chrono);
        waveform_view = (com.instamelody.instamelody.utils.WaveformView) findViewById(R.id.waveform_view);
        mDecibelView = (TextView) findViewById(R.id.decibel_view);
        startTime = SystemClock.elapsedRealtime();
        //  myTimer = new Timer();
        //   timeElapsed = SystemClock.elapsedRealtime() - chrono.getBase();
        rlInviteButton = (RelativeLayout) findViewById(R.id.rlInviteButton);
        rlPublic = (RelativeLayout) findViewById(R.id.rlPublic);
        recyclerViewInstruments = (RecyclerView) findViewById(R.id.recyclerViewInstruments);
        noMelodyNote = (TextView) findViewById(R.id.noMelodyNote);
        tvDone = (TextView) findViewById(R.id.tvDone);
        frameTrans = (FrameLayout) findViewById(R.id.frameTrans);
        frameSync = (FrameLayout) findViewById(R.id.frameSync);
        rlSync = (RelativeLayout) findViewById(R.id.rlSync);
        SharedPreferences loginSharedPref1 = this.getSharedPreferences("Url_recording", MODE_PRIVATE);
        fetchRecordingUrl = loginSharedPref1.getString("Recording_url", null);

        fetchGenreNames();
        elapsedMillis = SystemClock.elapsedRealtime() - chrono.getBase();
        int hours = (int) (timeElapsed / 3600000);
        int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
        int seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;

        final String formate = "" + hours + "" + minutes + "" + seconds;

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String dateString = sdf.format(date);
        recording_date.setText(dateString);
        melody_date.setText(dateString);


        final Intent intent = getIntent();
        if (intent == null) {
        } /*else if (intent.getExtras().getString("clickPosition").equals("fromHomeActivity")) {
            //do nothing
        } else if (intent.getExtras().getString("clickPosition").equals("fromSocialActivity")) {
            //do nothing
        }*/ else {
            joinRecordingId = intent.getExtras().getString("clickPositionJoin");
            if (joinRecordingId != null) {
                fetchJoinInstruments(joinRecordingId);
                noMelodyNote.setVisibility(View.GONE);
                recyclerViewInstruments.setVisibility(View.VISIBLE);
                recyclerViewInstruments.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewInstruments.setLayoutManager(layoutManager);
                recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
                adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                recyclerViewInstruments.setAdapter(adapter);
                frameTrans.setVisibility(View.VISIBLE);
                frameSync.setVisibility(View.VISIBLE);
                if (instrumentList.size() > 0) {
                    frameSync.setVisibility(View.VISIBLE);
                }

                ArrayList<JoinedArtists> JoinArtist = new ArrayList<>();
                JoinArtist = JoinInstrumentListAdp.returnJoinList();

                try {
                    joinRecordingName = JoinArtist.get(Integer.parseInt(joinRecordingId)).getRecording_name();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < instrumentList.size(); i++) {
                    MelodyInstruments instrumentsJoin = instrumentList.get(i);
                    joinInstrumentName = instrumentsJoin.getInstrumentName();
                }

                LocalBroadcastManager.getInstance(this).registerReceiver(mInstrumentJoin, new IntentFilter("fetchingInstrumentsJoin"));

            }

        } /*else {
            melodyPackId = intent.getExtras().getString("clickPosition");

            if (melodyPackId != null) {
                fetchInstruments(melodyPackId);

                noMelodyNote.setVisibility(View.GONE);
                recyclerViewInstruments.setVisibility(View.VISIBLE);
                recyclerViewInstruments.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewInstruments.setLayoutManager(layoutManager);
                recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
                adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                recyclerViewInstruments.setAdapter(adapter);
                frameTrans.setVisibility(View.VISIBLE);
                frameSync.setVisibility(View.VISIBLE);
                if (instrumentList.size() > 0) {
                    frameSync.setVisibility(View.VISIBLE);
                }


                ArrayList<MelodyCard> arrayMelody = new ArrayList<>();

                arrayMelody = MelodyCardListAdapter.returnMelodyList();
                try {
                    melodyName = arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }


                for (int i = 0; i < instrumentList.size(); i++) {
                    MelodyInstruments instruments = instrumentList.get(i);
                    instrumentName = instruments.getInstrumentName();
                }

                LocalBroadcastManager.getInstance(this).registerReceiver(mInstruments, new IntentFilter("fetchingInstruments"));

            }
        }*/


        grey_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grey_circle.setVisibility(View.GONE);
                blue_circle.setVisibility(View.VISIBLE);
            }
        });

        blue_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blue_circle.setVisibility(View.GONE);
                grey_circle.setVisibility(View.VISIBLE);
            }
        });


        if (statusNormal == 1) {
            artist_name.setText("@" + userNameLogin);
        }

        if (profilePicLogin != null) {
            profile_image.setVisibility(View.VISIBLE);
            Picasso.with(StudioActivity.this).load(profilePicLogin).into(profile_image);
        }


        SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        Name = twitterPref.getString("Name", null);
        userName = twitterPref.getString("userName", null);
        profilePic = twitterPref.getString("ProfilePic", null);
        statusTwitter = twitterPref.getInt("status", 0);

        if (statusTwitter == 1) {
            artist_name.setText("@" + userName);
        }

        if (profilePic != null) {
            //ivProfile.setVisibility(View.GONE);
            profile_image.setVisibility(View.VISIBLE);
            Picasso.with(StudioActivity.this).load(profilePic).into(profile_image);
        }

        SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
        fbName = fbPref.getString("FbName", null);
        fbUserName = fbPref.getString("userName", null);
        fbId = fbPref.getString("fbId", null);
        statusFb = fbPref.getInt("status", 0);

        if (statusFb == 1) {
            artist_name.setText("@" + fbName);
        }

        if (fbId != null) {
            profile_image.setVisibility(View.VISIBLE);
            Picasso.with(StudioActivity.this).load("https://graph.facebook.com/" + fbId + "/picture").into(profile_image);
        }
        mRecordingThread = new RecordingThread();
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioBuffer = new short[mBufferSize / 2];


        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/InstaMelody.mp3";


        //editor.apply();
        rlMelodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (StudioActivity.mpall != null) {
                    StudioActivity.mpall.stop();
                    for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                        StudioActivity.mediaPlayersAll.get(i).stop();

                    }
                }

                if (mRecordingThread != null) {
                    mRecordingThread.stopRunning();
                }

                if (isRecording) {
                    StudioActivity.ivRecord.setEnabled(false);
                    StudioActivity.handler.removeCallbacksAndMessages(null);

                    if (recorder != null) {
                        try {
                            recorder.stop();

                        } catch (RuntimeException ex) {
                            //Ignore
                        }
                    }
                    recorder.release();
                    recorder = null;
                    isRecording = false;
                    StudioActivity.tvDone.setEnabled(true);
                    StudioActivity.chrono.stop();
                } else {
                    try {

                        StudioActivity.rlRecordingButton.setEnabled(true);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }


                Intent intent = new Intent(StudioActivity.this, MelodyActivity.class);
                startActivity(intent);
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("cover response", MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mpall != null) {
                    mpall.stop();
                    if (mediaPlayersAll.size() > 0) {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            mediaPlayersAll.get(i).stop();
                        }
                    }
                }
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);

            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mpall != null) {
                    mpall.stop();
                    if (mediaPlayersAll.size() > 0) {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            mediaPlayersAll.get(i).stop();
                        }
                    }
                }
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);

            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mpall != null) {
                    mpall.stop();
                    if (mediaPlayersAll.size() > 0) {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            mediaPlayersAll.get(i).stop();
                        }
                    }
                }
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mpall != null) {
                    mpall.stop();
                    if (mediaPlayersAll.size() > 0) {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            mediaPlayersAll.get(i).stop();
                        }
                    }
                }
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mpall != null) {
                    mpall.stop();
                    if (mediaPlayersAll.size() > 0) {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            mediaPlayersAll.get(i).stop();
                        }
                    }
                }
                Intent i = new Intent(StudioActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mpall != null) {
                    mpall.stop();
                    if (mediaPlayersAll.size() > 0) {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            mediaPlayersAll.get(i).stop();
                        }
                    }
                }
                Intent i = new Intent(StudioActivity.this, StationActivity.class);
                startActivity(i);
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null && melodyPackId != null) {
                    openDialog();
                    ivRecord.setVisibility(View.VISIBLE);
                    ivRecord.setEnabled(true);
                } else if (userId == null) {
                    Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(i);
                    Toast.makeText(StudioActivity.this, "SignIn to Save Recording", Toast.LENGTH_SHORT).show();
                } else if (melodyPackId == null) {
                    Toast.makeText(StudioActivity.this, "Add Melody Packs to save recording", Toast.LENGTH_SHORT).show();
                }
            }
        });


        rlRedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivRecord_play.setVisibility(View.INVISIBLE);
                rlRedoButton.setVisibility(View.INVISIBLE);
                ivRecord.setVisibility(View.VISIBLE);
                rlMelodyButton.setVisibility(View.VISIBLE);
                StudioActivity.this.recreate();


            }
        });

        rlSetCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               /* Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);*/
                showFileChooser();

            }
        });

        rlInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
            }
        });


        rlPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userId != null) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudioActivity.this);
                    alertDialog.setTitle("Make Public?");
                    alertDialog.setMessage("As a moderator, feel free to make public or private anytime.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (switchFlag == "1") {
                                switchPublic.setChecked(false);
                                switchFlag = "0";
                            } else {
                                switchPublic.setChecked(true);
                                switchFlag = "1";
                            }

                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            switchPublic.setChecked(false);
                            switchFlag = "0";
                        }
                    });
                    alertDialog.show();
                } else {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudioActivity.this);
                    alertDialog.setTitle("Make Public?");
                    alertDialog.setMessage("As a moderator, feel free to make public or private anytime.");
                    alertDialog.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(i);
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }


            }
        });


        // To get preferred buffer size and sampling rate.
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("Buffer Size & sample rate", "Size :" + size + " & Rate: " + rate);


    }

    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return String.valueOf(seconds);
    }


    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(StudioActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtTopicName = (EditText) subView.findViewById(R.id.dialogEtTopicName);
        sp = (Spinner) subView.findViewById(R.id.spinnerGenre);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genresName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(dataAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(StudioActivity.this);
        builder.setView(sp);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedGenre = String.valueOf(position + 1);
                selectedGenre = genresId.get(position);
//                Toast.makeText(StudioActivity.this, ""+selectedGenre, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder.setTitle("Save As");
        builder.setMessage("Choose Topic Name and Genre if you want to be noticed");
        builder.setView(subView);

        TextView title = new TextView(this);
        title.setText("Save As");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder.setCustomTitle(title);

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                openDialog2();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtTopicName.getWindowToken(), 0);


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtTopicName.getWindowToken(), 0);
            }
        });

        builder.show();


    }


    private void openDialog2() {
        LayoutInflater inflater = LayoutInflater.from(StudioActivity.this);
        View subView1 = inflater.inflate(R.layout.dialog_layout2, null);

        rgR = (RadioGroup) subView1.findViewById(R.id.rgR);


        rgR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                for (int i = 0; i < rg.getChildCount(); i++) {
                    RadioButton btn = (RadioButton) rg.getChildAt(i);
                    if (btn.getId() == checkedId) {
                        value1 = btn.getText().toString();
                        value = String.valueOf(i + 1);
                        // do something with text
                        return;
                    }
                }
            }
        });


        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Save As");
        builder2.setMessage("Would you like to save as an instrumental or recording");
        builder2.setView(subView1);

        TextView title = new TextView(this);
        title.setText("Save As");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder2.setCustomTitle(title);


        builder2.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadRecordingsMixing("5");
                //saveRecordings1();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtTopicName.getWindowToken(), 0);
            }
        });

        builder2.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtTopicName.getWindowToken(), 0);
            }
        });
        builder2.show();
    }


    private class RecordingThread extends Thread {


        @Override
        public void run() {

            //  android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            try {

                recorder.startRecording();
                Log.d("Recording issue", "SampleRate" + SAMPLING_RATE + "BufferSize" + mBufferSize + "AudioBuffer" + mAudioBuffer);
            } catch (IllegalStateException e) {
                Log.d("Recording issue", e.toString());
            }


            while (shouldContinue()) {
                recorder.read(mAudioBuffer, 0, mBufferSize / 2);
                waveform_view.updateAudioData(mAudioBuffer);
                updateDecibelLevel();

            }

            try {

                recorder.stop();


            } catch (IllegalStateException e) {
                e.printStackTrace();
            }


            recorder.release();
        }


        private synchronized boolean shouldContinue() {
            return mShouldContinue;
        }

        public synchronized void stopRunning() {
            mShouldContinue = false;
        }


        private void updateDecibelLevel() {

            double sum = 0;

            for (short rawSample : mAudioBuffer) {
                double sample = rawSample / 32768.0;
                sum += sample * sample;
            }

            double rms = Math.sqrt(sum / mAudioBuffer.length);
            final double db = 20 * Math.log10(rms);

            // Update the text view on the main thread.
            mDecibelView.post(new Runnable() {
                @Override
                public void run() {
                    // mDecibelView.setText(String.format(mDecibelFormat, db));
                }
            });
        }
    }

    public void fetchInstruments(String melodyPackId) {
        final String mpid = melodyPackId;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Instrument list Response", response);

                        instrumentList.clear();
                        new ParseContents(getApplicationContext()).parseInstrumentsAttached(response, instrumentList, mpid);

                        InstrumentCountSize = MelodyInstruments.getInstrumentCount();
                        adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(FILE_TYPE, "admin_melody");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void fetchJoinInstruments(String joinRecordingId) {
        final String JoinRecordingId = joinRecordingId;
        SharedPreferences filterPref = getApplicationContext().getSharedPreferences("RecordingData", MODE_PRIVATE);
        final String userIdJoin = filterPref.getString("AddedBy", null);
        final String RecId = filterPref.getString("Recording_id", null);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JOINED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Instrument list Response", response);
                        instrumentList.clear();
                        new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, JoinRecordingId);
                        InstrumentCountSize = MelodyInstruments.getInstrumentCount();
                        adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USERID, userId);
                params.put(RECORDING_ID, RecId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                 return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }


    // DownloadFileFromURL Modified by Abhishek


    public String melodyName() {
        return melodyName;
    }

    public String instrumentName() {
        return instrumentName;
    }


    public void saveRecordings1() {
        progressDialog = new ProgressDialog(StudioActivity.this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response.toString();
                        try {
                            JSONObject recordResponse = new JSONObject(successmsg);
                            String flag = recordResponse.getString("flag");
                            String msg1 = recordResponse.getString("msg");
                            String response2 = recordResponse.getString("response");

                            JSONObject recResponse = recordResponse.getJSONObject("response");


                            JSONObject melodyData = recResponse.getJSONObject("melody_data");
                            idUpload = melodyData.getString("id");
                            packName = melodyData.getString("packname");
                            addedByUser = melodyData.getString("added_by_user");
                            recGenre = melodyData.getString("gener");
                            bpm = melodyData.getString("bpm");
                            likeCount = melodyData.getString("like_count");
                            shareCount = melodyData.getString("share_count");
                            commentCount = melodyData.getString("comment_count");
                            playCount = melodyData.getString("play_count");

                            addDate = melodyData.getString("add_date");
                            melodyRecDuration = melodyData.getString("duration");
                            Public = melodyData.getString("public");
                            if (flag.equals("success")) {

                                uploadRecordings(melodyData.getString("id"));

                            } else {
                                Toast.makeText(StudioActivity.this, response, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String error = e.toString();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StudioActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        String errormsg = error.toString();
                        Log.d("Error", errormsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ADMIN_INSTRUMENT_ID, String.valueOf(MelodyInstruments.getInstrumentId()));
                params.put(RECORDING_TYPE, value);
                params.put(USER_ID, userId);
                params.put(RECORDING_NAME, subEtTopicName.getText().toString().trim());
                params.put(RECORDING_GENRE, selectedGenre);
                params.put(RECORDING_DURATION, recordingDuration);
                params.put(SHARE_PUBLIC, switchFlag);
                params.put(RECORDING_BPM, "128");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(600000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void uploadRecordingsMixing(final String id) {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, MixingAudio_InstrumentsAudio, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String urlRecording;
                String resultResponse = new String(response.data);
                Log.d("Server Data", resultResponse);
                SharedPreferences loginSharedPref = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                String userName = loginSharedPref.getString("userName", null);
                String recPic = loginSharedPref.getString("profilePic", null);

                try {
                    JSONObject response1 = new JSONObject(resultResponse);
                    String melodyurl;
                    String flag = response1.getString("flag");
                    String msgflag = response1.getString("msg");
                    JSONObject r1 = response1.getJSONObject("response");

                    if (msgflag.equals("Melody created")) {
                        JSONObject MelodyResponseDetails = r1.getJSONObject("melody_data");
                        melodyurl = "http://52.89.220.199/api/" + MelodyResponseDetails.getString("melodyurl");
                        //urlRecording = r1.getString("melody");
                    } else {
                        JSONObject RecordingResponseDetails = r1.getJSONObject("melody_data");
                        melodyurl = "http://52.89.220.199/api/" + RecordingResponseDetails.getString("melodyurl");
                        // urlRecording = r1.getString("recording");
                    }

                    if (flag.equals("success")) {
                        if (msgflag.equals("Melody created")) {
                            tvDone.setEnabled(false);
                            MelodyInstruments melodyInstruments = new MelodyInstruments();
                            melodyInstruments.setInstrumentName(packName);
                            melodyInstruments.setInstrumentBpm(bpm);
                            melodyInstruments.setInstrumentFile("Blank");
                            melodyInstruments.setInstrumentLength(melodyRecDuration);
                            melodyInstruments.setUserProfilePic(recPic);
                            melodyInstruments.setInstrumentCover("#00FDFE");
                            melodyInstruments.setInstrumentCreated(addDate);
                            melodyInstruments.setUserName(userName);
                            melodyInstruments.setInstrumentFile(melodyurl);
                            instrumentList.add(melodyInstruments);
                            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            switchFlag = "0";
                        } else {

                            tvDone.setEnabled(false);
                            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            switchFlag = "0";
                            Toast.makeText(StudioActivity.this, "Saved as Recording", Toast.LENGTH_SHORT).show();
                        }

                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        SharedPreferences.Editor recEditor = getApplication().getSharedPreferences("Recording_MelodyDataResponse", MODE_PRIVATE).edit();
                        recEditor.clear();
                        recEditor.apply();

                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Url_recording", MODE_PRIVATE).edit();
                        editor.putString("Recording_url", melodyurl);
                        editor.apply();

                        SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                        editorT.putString("thumbnailUrl", "http://bit.ly/2xCRux3");
                        editorT.apply();

                        SharedPreferences switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatusFb", MODE_PRIVATE);
                        fbValue = switchFbEditor.getBoolean("switchFb", false);

                        SharedPreferences switchTwitterEditor = getApplicationContext().getSharedPreferences("SwitchStatusTwitter", MODE_PRIVATE);
                        twitterValue = switchTwitterEditor.getBoolean("switchTwitter", false);

                        SharedPreferences switchSoundCloudEditor = getApplicationContext().getSharedPreferences("SwitchStatusSoundCloud", MODE_PRIVATE);
                        boolean soundCloudValue = switchSoundCloudEditor.getBoolean("switchSoundCloud", false);

                        SharedPreferences switchGoogleEditor = getApplicationContext().getSharedPreferences("SwitchStatusGoogle", MODE_PRIVATE);
                        googleValue = switchGoogleEditor.getBoolean("switchGoogle", false);

                        if (fbValue == true && ((fbValue && twitterValue)) != true) {
                            FbShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        } else if (twitterValue == true && (fbValue && twitterValue) != true) {
                            TweetShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        } else if (googleValue == true && ((fbValue && twitterValue)) != true) {
                            googlePlusShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        }
                        if ((fbValue && twitterValue) == true) {
                            FbShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("return message", resultResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("error msg", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(MixisMelody, value1);
                params.put(Mixtopic_name, subEtTopicName.getText().toString().trim());
                params.put(Mixuser_id, userId);
                params.put(Mixpublic_flag, switchFlag);
                params.put(MixrecordWith, "withoutMike");
                params.put(Mixgenere, selectedGenre);
                params.put(Mixbpms, "128");
                params.put(Mixdurations, recordingDuration);
                params.put(MixCommand, "");
//Saurabh
                Intent intent = getIntent();
                String pos = intent.getExtras().getString("Position");
                String melodyID = JoinActivity.Joined_artist.get(Integer.parseInt(pos)).getRecording_id();
                params.put(MixparentRecordingID, melodyID);
                //params.put(Mixrecording, list.toString());

                JSONArray myarray = new JSONArray();
                try {
                    for (int i = 0; i <= list.size() - 1; i++) {
                        //arr[i]="questionId_"+i+"_"+"ans_"+i;
                        //jsonObject.put("params_"+i,arr[i]);
                        //obj.put("Id", id);
                        //jsonObject.put(Mixrecording, list.get(i).toString());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", list.get(i).id);
                        jsonObject.put("Volume", list.get(i).volume);
                        jsonObject.put("Bass", list.get(i).bass);
                        jsonObject.put("Treble", list.get(i).treble);
                        jsonObject.put("Pan", list.get(i).pan);
                        jsonObject.put("Pitch", list.get(i).pitch);
                        jsonObject.put("Reverb", list.get(i).reverb);
                        jsonObject.put("Compression", list.get(i).compression);
                        jsonObject.put("Delay", list.get(i).delay);
                        jsonObject.put("Tempo", list.get(i).tempo);
                        jsonObject.put("threshold", list.get(i).threshold);
                        jsonObject.put("ratio", list.get(i).ratio);
                        jsonObject.put("attack", list.get(i).attack);
                        jsonObject.put("release", list.get(i).release);
                        jsonObject.put("makeup", list.get(i).makeup);
                        jsonObject.put("knee", list.get(i).knee);
                        jsonObject.put("mix", list.get(i).mix);
                        jsonObject.put("fileurl", list.get(i).fileurl);
                        jsonObject.put("PositionId", list.get(i).positionId);

                        myarray.put(i, jsonObject);

                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                params.put(Mixrecording, myarray.toString());
                params.put("command", "SaveRecord");
                //params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put(Mixvocalsound, new DataPart("InstaMelody.mp3", soundBytes, "audio/amr"));
                return params;
            }
           /* @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                JSONObject paramss = new JSONObject();
                try {
                    for (int i=0; i <list.size();i++){
                        paramss.put(Config.EVENT_ID, eventIDBeacon.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return paramss;
            };*/

        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }


    public void uploadRecordings(final String id) {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_COVER_MELODY_FILE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String urlRecording;
                String thumbNail;
                String resultResponse = new String(response.data);
                Log.d("Server Data", resultResponse);
                SharedPreferences loginSharedPref = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                String userName = loginSharedPref.getString("userName", null);
                String recPic = loginSharedPref.getString("profilePic", null);

                try {
                    JSONObject response1 = new JSONObject(resultResponse);
                    String flag = response1.getString("0");
                    String flag2 = response1.getString("flag");
                    Log.d("Result", flag2);
                    JSONObject r1 = response1.getJSONObject("0");
                    if (r1.has("melody")) {
                        urlRecording = r1.getString("melody");
                        thumbNail = r1.getString("thumbnail");
                    } else {
                        urlRecording = r1.getString("recording");
                        thumbNail = r1.getString("thumbnail");
                    }

                    if (flag2.equals("success")) {

                        //adapter.notifyItemInserted(instrumentList.size()-1);
                        if (r1.has("melody")) {
                            tvDone.setEnabled(false);
                            MelodyInstruments melodyInstruments = new MelodyInstruments();
                            melodyInstruments.setInstrumentName(packName);
                            melodyInstruments.setInstrumentBpm(bpm);
                            melodyInstruments.setInstrumentFile("Blank");
                            melodyInstruments.setInstrumentLength(melodyRecDuration);
                            melodyInstruments.setUserProfilePic(recPic);
                            melodyInstruments.setInstrumentCover("#00FDFE");
                            melodyInstruments.setInstrumentCreated(addDate);
                            melodyInstruments.setUserName(userName);
                            melodyInstruments.setInstrumentFile(urlRecording);
//                        melodyInstruments.setAudioType("recording");
                            instrumentList.add(melodyInstruments);
                            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            switchFlag = "0";
                        } else {

                            tvDone.setEnabled(false);
                            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            switchFlag = "0";
                            Toast.makeText(StudioActivity.this, "Saved as Recording", Toast.LENGTH_SHORT).show();
                            //   StudioActivity.this.ivRecord.setVisibility(View.VISIBLE);
                /*waveform_view.setVisibility(View.GONE);
                recording_time.setText("00:00:00");*/
                            // StudioActivity.this.recreate();
                        }

                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
//                        InputMethodManager inputManager = (InputMethodManager)
//                                getSystemService(Context.INPUT_METHOD_SERVICE);
//                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                                InputMethodManager.HIDE_NOT_ALWAYS);

                        SharedPreferences.Editor recEditor = getApplication().getSharedPreferences("Recording_MelodyDataResponse", MODE_PRIVATE).edit();
                        recEditor.clear();
                        recEditor.apply();

                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Url_recording", MODE_PRIVATE).edit();
                        editor.putString("Recording_url", urlRecording);
                        editor.apply();

                        SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                        editorT.putString("thumbnailUrl", thumbNail);
                        editorT.apply();

                       /* SharedPreferences switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE);
                        int switchFbStatus = switchFbEditor.getInt("switch", 0);*/

                        SharedPreferences switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatusFb", MODE_PRIVATE);
                        fbValue = switchFbEditor.getBoolean("switchFb", false);

                        SharedPreferences switchTwitterEditor = getApplicationContext().getSharedPreferences("SwitchStatusTwitter", MODE_PRIVATE);
                        twitterValue = switchTwitterEditor.getBoolean("switchTwitter", false);

                        SharedPreferences switchSoundCloudEditor = getApplicationContext().getSharedPreferences("SwitchStatusSoundCloud", MODE_PRIVATE);
                        boolean soundCloudValue = switchSoundCloudEditor.getBoolean("switchSoundCloud", false);

                        SharedPreferences switchGoogleEditor = getApplicationContext().getSharedPreferences("SwitchStatusGoogle", MODE_PRIVATE);
                        googleValue = switchGoogleEditor.getBoolean("switchGoogle", false);

                        if (fbValue == true && ((fbValue && twitterValue)) != true) {
                            FbShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        } else if (twitterValue == true && (fbValue && twitterValue) != true) {
                            TweetShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        } else if (googleValue == true && ((fbValue && twitterValue)) != true) {
                            googlePlusShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        }
                        if ((fbValue && twitterValue && googleValue) == true) {
                            FbShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        }


                    }

                    if (flag.equals("success")) {
                        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_COVER_MELODY_FILE, new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
//                                myTask = new LongOperation();
//                                myTask.execute();
                                // new LongOperation().execute();
                                String resultResponse = new String(response.data);

                                Toast.makeText(StudioActivity.this, "Cover also Uploaded", Toast.LENGTH_SHORT).show();
//                                + resultResponse
                                try {
                                    JSONObject coverResponse = new JSONObject(resultResponse);
                                    String flag = coverResponse.getString("flag");
                                    String flag2 = coverResponse.getString("response");
                                    JSONObject coverR1 = coverResponse.getJSONObject("response");
                                    String coverPic = coverR1.getString("profilepic");

                                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("cover response", MODE_PRIVATE).edit();
                                    editor.putString("coverPicStudio", coverPic);
                                    editor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("return message", resultResponse);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                error.printStackTrace();

//                                Toast.makeText(getApplicationContext(), "Cover is not Uploaded", Toast.LENGTH_SHORT).show();

//                                +error.toString();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put(FILE_TYPE, "2");
                                params.put(IS_MELODY, value1);
                                params.put(ID_MELODY_REC, id);
                                params.put(USER_ID1, userId);
                                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                                return params;
                            }

                            @Override
                            protected Map<String, DataPart> getByteData() {
                                Map<String, DataPart> params = new HashMap<>();
                                // file name could found file base or direct access from real path
                                // for now just get bitmap data from ImageView
                                params.put(FILE1, new DataPart("CoverImg.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), ivNewRecordCover.getDrawable()), "image/jpeg"));
                                return params;
                            }

                        };

                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Toast.makeText(getApplicationContext(), resultResponse, Toast.LENGTH_LONG).show();
                Log.d("return message", resultResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("error msg", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(FILE_TYPE, "3");
                params.put(IS_MELODY, value1);
                params.put(ID_MELODY_REC, id);
//                params.put(ID_MELODY_REC, "150");
                params.put(USER_ID1, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

//                params.put(FILE1, new DataPart("InstaMelody.mp3", audioFilePath.getBytes(), "audio/mpeg"));
//                params.put(FILE1,new DataPart("InstaMelody.mp3",MediaExtractor(),"audio/mpeg"));
                params.put(FILE1, new DataPart("InstaMelody.mp3", soundBytes, "audio/amr"));
                return params;
            }


        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }

    public void fetchGenreNames() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        String titleString;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Genres genres = new Genres();
                                    genreJson = jsonArray.getJSONObject(i);
                                    titleString = genreJson.getString(KEY_GENRE_NAME);
                                    genres.setName(titleString);
                                    genres.setId(genreJson.getString(KEY_GENRE_ID));
                                    genresArrayList.add(genres);
                                    genresName.add(i, genresArrayList.get(i).getName());
                                    genresId.add(i, genresArrayList.get(i).getId());
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
                        Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void playAudioRecycler() throws IOException {

        /*for (int i = 0; i < InstrumentCountSize; i++) {
            Log.d("Instrument url----------------:", "" + instrumentList.get(i).getInstrumentFile());
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(instrumentList.get(i).getInstrumentFile());
            mp.prepare();
            mps.add(mp);

        }

        for (MediaPlayer mp : mps) {
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();

                }
            });
        }*/
        try {

            for (int i = 0; i < InstrumentCountSize; i++) {
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(instrumentList.get(i).getInstrumentFile());
                mps.add(mp);

            }

            for (MediaPlayer mp : mps) {
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();

                    }
                });
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }/*catch (Throwable e) {
            e.printStackTrace();
        }*/

    }

    public void killMediaPlayer() {
        for (MediaPlayer mp : mps) {
            try {
                mp.stop();
                mp.reset();
                mp.release();
                //     mp=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mps.clear();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) || requestCode == TWEETER_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(this, "Share on Tweeter Done", Toast.LENGTH_SHORT).show();
                if ((fbValue && twitterValue && googleValue) == true) {
                    googlePlusShare();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                Toast.makeText(this, "Share on Tweeter Done", Toast.LENGTH_SHORT).show();
            }
            //  twitter related handling
//            client.onActivityResult(requestCode, resultCode, data);
        } else {
            if (callbackManager.onActivityResult(requestCode, resultCode, data))
                return;
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                ivNewRecordCover.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (mpall != null) {
                mpall.stop();
                if (mediaPlayersAll.size() > 0) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        mediaPlayersAll.get(i).stop();

                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }


    @Override
    protected void onDestroy() {
        try {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public BroadcastReceiver mInstruments = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            instruments_count = intent.getStringArrayListExtra("instruments");
        }
    };

    public BroadcastReceiver mInstrumentJoin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            instrument_count_Join = intent.getStringArrayListExtra("instrumentsJoin");
        }
    };

    public void playNext() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mp.reset();
                for (int i = 0; i < instruments_count.size(); i++) {
                    mp = MediaPlayer.create(StudioActivity.this, Uri.parse(instruments_count.get(++i)));
                    mp.start();
                    if (instruments_count.size() > i + 1) {
                        playNext();
                    }
                }

            }
        }, mp.getDuration() + 100);
    }

    public Class<ByteArrayPool> MediaExtractor() {
        File file = new File(audioFilePath);
        int size = (int) file.length();
        bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ByteArrayPool.class;
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            try {
                if (mRecordingThread != null) {
                    mRecordingThread.stopRunning();
                }
                if (mpall != null) {
                    mpall.stop();
                    if (mediaPlayersAll.size() > 0) {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            mediaPlayersAll.get(i).stop();

                        }
                    }
                }
                StudioActivity.tvDone.setEnabled(true);
                StudioActivity.chrono.stop();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void FbShare() {
        SharedPreferences editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        String fetchThumbNailUrl = editorT.getString("thumbnailUrl", null);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if ((fbValue && twitterValue) == true) {
                    progressDialog = new ProgressDialog(StudioActivity.this);
                    progressDialog.setTitle("Processing...");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    TweetShare();
                } /*else if ((fbValue && twitterValue && googleValue) == true) {
                    TweetShare();
//                    googlePlusShare();
                }*/
                Toast.makeText(StudioActivity.this, "Recording Uploaded", Toast.LENGTH_SHORT).show();
                /*SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                editorT.clear();
                editorT.apply();*/
            }

            @Override
            public void onCancel() {

                Toast.makeText(StudioActivity.this, "Recording not Uploaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {

            }

        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(fetchThumbNailUrl))
                    .build();
            shareDialog.show(linkContent, ShareDialog.Mode.FEED);
        }
    }

    public void TweetShare() {
        SharedPreferences editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        String fetchThumbNailUrl = editorT.getString("thumbnailUrl", null);

        try {
            ShortUrl = new URL(fetchThumbNailUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());
        TweetComposer.Builder builder = null;
        builder = new TweetComposer.Builder(this)
                .text("Audio Url")
                .url(ShortUrl);
//        builder.show();
        Intent i = new TweetComposer.Builder(this)
                .text("")
                .url(ShortUrl)
                .createIntent();
        startActivityForResult(i, TWEETER_REQ_CODE);
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

    }

    public void googlePlusShare() {
        SharedPreferences editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        String fetchThumbNailUrl = editorT.getString("thumbnailUrl", null);
        Intent shareIntent = new PlusShare.Builder(StudioActivity.this)
                .setType("text/plain")
                .setText("")
                .setContentUrl(Uri.parse(fetchThumbNailUrl))
                .getIntent();
        startActivityForResult(shareIntent, 0);
    }

}




