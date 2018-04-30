package com.yomelody;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.yomelody.Adapters.InstrumentListAdapter;
import com.yomelody.Adapters.MelodyCardListAdapter;
import com.yomelody.Models.Genres;
import com.yomelody.Models.MelodyCard;
import com.yomelody.Models.MelodyInstruments;
import com.yomelody.Models.MelodyMixing;
import com.yomelody.Models.MixingData;
import com.yomelody.Models.ModelPlayAllMediaPlayer;
import com.yomelody.Parse.ParseContents;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.Const;
import com.yomelody.utils.NotificationUtils;
import com.yomelody.utils.VisualizerView;
import com.yomelody.utils.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;
import com.yomelody.utils.WaveformView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.yomelody.app.Config.PUSH_NOTIFICATION;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.BASE_URL;
import static com.yomelody.utils.Const.ServiceType.GENERE;
import static com.yomelody.utils.Const.ServiceType.IsValidateSubPack;
import static com.yomelody.utils.Const.ServiceType.JOINED_USERS;
import static com.yomelody.utils.Const.ServiceType.MELODY;
import static com.yomelody.utils.Const.ServiceType.MixingAudio_InstrumentsAudio;
import static com.yomelody.utils.Const.ServiceType.TOTAL_COUNT;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class StudioActivity extends AppCompatActivity {

    int duration;
    long startTime;
    String value, value1;
    String userIdNormal, userIdFb, userIdTwitter;
    String userId;
    String switchFlag = "0";
    String switchFlagTemp = "0";
    MediaPlayer pts;
    AudioManager audioManager;
    final int MY_PERMISSIONS_REQUEST_MICROPHONE = 200;
    final int MY_PERMISSIONS_REQUEST_STORAGE = 201;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private String USER_ID = "user_id";

    private String FILE_TYPE = "file_type";
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    ArrayList<String> genresName = new ArrayList<>();
    ArrayList<String> genresId = new ArrayList<>();
    String KEY_GENRE_ID = "id";

    String IsMicConnected;
    private static byte[] buffer;
    public static Chronometer chrono;
    ImageView audio_feed, grey_circle, blue_circle;
    public static TextView tvPublic, tvDone, recording_date, melody_date, melody_detail;
    EditText subEtTopicName;
    Spinner sp;
    RadioGroup rgR;
    public static TextView mDecibelView;

    public static MediaRecorder recorder;
    public static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    public static boolean isRecording = false;
    public static MediaPlayer mediaPlayer = null;
    public static String audioFilePath;
    public static ArrayList<InstrumentListAdapter.ViewHolder> lstViewHolder = new ArrayList<InstrumentListAdapter.ViewHolder>();
    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray

    public static String firstName, userNameLogin, profilePicLogin, Name, userName, profilePic, TwitprofilePic, fbName, fbUserName, fbId, melodyPackId, joinRecordingId, instrumentCount, haveJoinid;
    String selectedGenre;
    int statusNormal, statusFb, statusTwitter;
    String melodyName, instrumentName;
    public static Switch switchPublic;
    public static RelativeLayout rlMelodyButton, rlRecordingButton, rlRedoButton, rlListeningButton, rlSetCover, rlPublic, rlSync;
    public static FrameLayout frameTrans, frameProgress;
    public static ImageView ivBackButton, ivHomeButton, ivRecord, ivRecord_stop, ivRecord_play, ivRecord_pause, discover, ivSound, message, ivProfile, ivNewRecordCover;
    CircleImageView profile_image;
    TextView artist_name, noMelodyNote, message_count;
    public static RecyclerView recyclerViewInstruments;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    public static ProgressDialog progressDialog, pDialog;
    public static ProgressBar frameprog;

    ArrayList<String> instruments_count = new ArrayList<String>();
    ShareDialog shareDialog;
    String fetchRecordingUrl;
    public static byte[] bytes, soundBytes;
    int InstrumentCountSize = 0;
    private boolean mShouldContinue = true;
    public static String recordingDuration;
    public static long stop_rec_time;
    public static String time_stop;
    int count = 0;
    public static RelativeLayout rlInviteButton, rlBase;
    public static TextView tvInstrumentLength, tvUserName, tvInstrumentName, tvBpmRate;
    public static ImageView userProfileImage, ivInstrumentCover, playAll, pauseAll;
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
    String MixCommand = "command";
    String MixparentRecordingID = "parentRecordingID";
    public static MediaPlayer mpall;
    public static ArrayList<MediaPlayer> mediaPlayersAll = new ArrayList<MediaPlayer>();
    public static ArrayList<ModelPlayAllMediaPlayer> PlayAllModel = new ArrayList<>();
    public static List<MediaPlayer> mp_start = new ArrayList<MediaPlayer>();
    public static final Handler handler = new Handler();
    CallbackManager callbackManager;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    //    private boolean fbSwitch, twitterSwitch, googleSwitch;
    private BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private String thumbnailUrl = "";
    private JSONObject MelodyResponseDetails;
    private Activity mActivity;
    final int REQUEST_GOOGLE_SHARE = 117;
    int totalCount = 0, Compdurations = 0, tmpduration = 0, MaxMpSessionID;
    String IscheckMelody = null;
    String IsHomeMeloduId = null;

    private static final int PERMISSION_RECORD_AUDIO = 0;
    private final int TWEET_COMPOSER_REQUEST_CODE = 29;
    private RecordWaveTask recordTask = null;
    public static VisualizerView mVisualizerView;
    public static Visualizer mVisualizer;
    public static boolean IsRepeat = false;
    int MelmaxVolume = 1;
    public static boolean IsRepeteReAll = false;
    private SharedPreferences socialStatusPref;
    public static String IsExp;
    public static int LayerCount = -1;
    boolean IsValidPack = false;
    int PackDuration = 0;
    public static boolean IsRecordingStart = false;
    private AsyncTask mMyTask = null;
    public static boolean IsDirect = false;
    private String CommonUserName = null;
    private static String previousScreen="";
    private String mMessage="";
    private JSONObject instJson=null;
    private AudioRecord audioRecord = null;
    public WaveformView waveform_view;
    private static final int SAMPLING_RATE = 44100;
    private int mBufferSize;
    private short[] mAudioBuffer;
    private RecordingThread mRecordingThread;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);
        mActivity = StudioActivity.this;
        progressDialog = new ProgressDialog(mActivity);
        socialStatusPref = getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE);
        rlBase = (RelativeLayout) findViewById(R.id.rlBase);
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
        rlSync = (RelativeLayout) findViewById(R.id.rlSync);
        //frameInstrument = (FrameLayout) findViewById(R.id.frameInstrument);
        tvInstrumentLength = (TextView) findViewById(R.id.tvInstrumentLength);
        tvInstrumentName = (TextView) findViewById(R.id.tvInstrumentName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvBpmRate = (TextView) findViewById(R.id.tvBpmRate);
        frameProgress = (FrameLayout) findViewById(R.id.frameProgress);
        ivSound = (ImageView) findViewById(R.id.ivSound);
        message_count = (TextView) findViewById(R.id.message_count);
        playAll = (ImageView) findViewById(R.id.playAll);
        pauseAll = (ImageView) findViewById(R.id.pauseAll);
        recyclerViewInstruments = (RecyclerView) findViewById(R.id.recyclerViewInstruments);
        rlInviteButton = (RelativeLayout) findViewById(R.id.rlInviteButton);
        rlPublic = (RelativeLayout) findViewById(R.id.rlPublic);
        noMelodyNote = (TextView) findViewById(R.id.noMelodyNote);
        tvDone = (TextView) findViewById(R.id.tvDone);
        frameTrans = (FrameLayout) findViewById(R.id.frameTrans);
        rlSync = (RelativeLayout) findViewById(R.id.rlSync);
        rlSetCover = (RelativeLayout) findViewById(R.id.rlSetCover);
        ivNewRecordCover = (ImageView) findViewById(R.id.ivNewRecordCover);
        chrono = (Chronometer) findViewById(R.id.chrono);
        mVisualizerView = (VisualizerView) findViewById(R.id.myvisualizerview);
        mDecibelView = (TextView) findViewById(R.id.decibel_view);
        waveform_view = findViewById(R.id.waveform_view);

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
        if (statusNormal == 1) {
            userId = userIdNormal;
            CommonUserName = loginSharedPref.getString("userName", null);
        } else if (statusFb == 1) {
            userId = userIdFb;
            CommonUserName = loginFbSharedPref.getString("firstName", null);
        } else if (statusTwitter == 1) {
            userId = userIdTwitter;
            CommonUserName = loginTwitterSharedPref.getString("userName", null);
        }


        getTotalCount();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    getTotalCount();
                }
            }
        };
        switchPublic.setVisibility(View.VISIBLE);

        TweeterSharingWork();
        IsDirect = false;
        playAll.setVisibility(View.GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startTime = SystemClock.elapsedRealtime();

        SharedPreferences loginSharedPref1 = this.getSharedPreferences("Url_recording", MODE_PRIVATE);
        fetchRecordingUrl = loginSharedPref1.getString("Recording_url", null);
        pauseAll.setVisibility(View.GONE);
        fetchGenreNames();


        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String dateString = sdf.format(date);
        recording_date.setText(dateString);
        melody_date.setText(dateString);

        rlPublic.setVisibility(View.GONE);
        final Intent intent = getIntent();
        if (intent != null) {
            try {
                melodyPackId = intent.getExtras().getString("clickPosition");
                MelodyCard melodyCard = (MelodyCard) intent.getSerializableExtra("melody_data_MelodyActivity");
                MelodyInstruments melodyInstruments = null;
                instrumentList.clear();
                if (melodyCard!=null){
                    melodyInstruments = new MelodyInstruments();
                    melodyInstruments.setInstrumentFile(melodyCard.getMelodyURL());
                    melodyInstruments.setInstrumentName(melodyCard.getMelodyName());
                    melodyInstruments.setInstrumentBpm("120");
                    melodyInstruments.setUserProfilePic(melodyCard.getUserProfilePic());
                    melodyInstruments.setInstrumentCover(melodyCard.getMelodyCover());
                    melodyInstruments.setUserName(melodyCard.getUserName());
                    melodyInstruments.setInstrumentLength(melodyCard.getMelodyDuration());
                    melodyInstruments.setInstrumentCreated(melodyCard.getMelodyCreated());
                    instrumentList.add(melodyInstruments);
                    setAdapter();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


        recordTask = (RecordWaveTask) getLastCustomNonConfigurationInstance();
        if (recordTask == null) {
            recordTask = new RecordWaveTask(this);
        } else {
            recordTask.setContext(this);
        }
        if (userId != null) {
            IsValidateSubscription();
        }

        //subscriptionPackage();

//        joinRecordingId = intent.getExtras().getString("clickPositionJoin");
        SharedPreferences filterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE);
        joinRecordingId = filterPref.getString("instrumentsPos", null);


        if (getIntent()!=null && getIntent().hasExtra("previous_screen")){
            previousScreen = getIntent().getStringExtra("previous_screen");
            AppHelper.sop("StudioActivity=previousScreen="+previousScreen);
            if (getIntent().hasExtra("melody_data")){
                mMessage=getIntent().getStringExtra("melody_data");
//                AppHelper.sop("mMessage=json="+mMessage.getMsgJson());
                AppHelper.sop("mMessage=json="+mMessage);
                instrumentList.clear();
                list.clear();
                JSONObject audioJson=null;
                try {
                    JSONObject json=new JSONObject(mMessage);
                    JSONArray audioSharedArr=json.getJSONArray("Audioshared");
                    for (int i=0; i<audioSharedArr.length();i++){
                        audioJson=audioSharedArr.getJSONObject(i);
                        String melodyurl="";
                        String melodyName="";
                        String melodyProfilePic="";
                        String melodyCoverPic="";
                        MelodyInstruments melodyInstruments = null;
                        if (audioJson.has("recordings")){
                            melodyInstruments = new MelodyInstruments();
                            JSONObject recJson=audioJson.getJSONArray("recordings").getJSONObject(0);
                            melodyName=audioJson.getString("recording_topic");
                            melodyurl=recJson.getString("recording_url");
                            melodyProfilePic=recJson.getString("profile_url");
                            melodyCoverPic=recJson.getString("coverpic_url");

                            melodyInstruments.setInstrumentFile(melodyurl);
                            melodyInstruments.setInstrumentName(melodyName);
                            melodyInstruments.setInstrumentBpm("120");
                            melodyInstruments.setUserProfilePic(melodyProfilePic);
                            melodyInstruments.setInstrumentCover(melodyCoverPic);
                            melodyInstruments.setUserName("@"+recJson.getString("user_name"));
                            melodyInstruments.setInstrumentLength(recJson.getString("duration"));
                            melodyInstruments.setInstrumentCreated(recJson.getString("date_added"));
                            instrumentList.add(melodyInstruments);
                        }
                        else if(audioJson.has("instruments")){
                            JSONArray instrumentArr=audioJson.getJSONArray("instruments");
                            for (int j=0; j<instrumentArr.length();j++){
                                instJson=instrumentArr.getJSONObject(j);
                                melodyInstruments = new MelodyInstruments();
                                melodyInstruments.setInstrumentBpm(instJson.getString("bpm"));
                                melodyInstruments.setInstrumentFile("Blank");
                                melodyInstruments.setInstrumentLength(instJson.getString("duration"));
                                melodyInstruments.setUserProfilePic(instJson.getString("profilepic"));
                                melodyInstruments.setInstrumentCover(instJson.getString("coverpic"));
                                melodyInstruments.setInstrumentCreated(instJson.getString("uploadeddate"));
                                melodyInstruments.setUserName(instJson.getString("username"));
                                if (TextUtils.isEmpty(melodyurl)){
                                    melodyInstruments.setInstrumentFile(instJson.getString("instrument_url"));
                                    melodyInstruments.setInstrumentName(instJson.getString("instruments_name"));
                                }
                                else {
                                    melodyInstruments.setInstrumentFile(melodyurl);
                                    melodyInstruments.setInstrumentName(melodyName);
                                    melodyInstruments.setInstrumentBpm("120");
                                    melodyInstruments.setUserProfilePic(melodyProfilePic);
                                    melodyInstruments.setInstrumentCover(melodyCoverPic);
                                }
                                list.add(j, new MixingData(instJson.getString("id"), "5", "-5", "20", "0", "44100", "0", "0", "0", "2000", "0", "0", "0", "0", "0", "0", "0", instJson.getString("instrument_url").replace(BASE_URL, ""), String.valueOf(j)));
                                instrumentList.add(melodyInstruments);
                            }

                        }
                    }

                    if(audioJson!=null && audioJson.has("instruments")){
                        setAdapter();
                    }


                    AppHelper.sop("instrumentList=size="+instrumentList.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        AppHelper.sop("StudioActivity=previousScreen=outif="+previousScreen);

        if (joinRecordingId != null && melodyPackId == null) {

            fetchInstrumentsForJoin(JoinActivity.addedBy, JoinActivity.RecId, Integer.parseInt(joinRecordingId));
            //melodyPackId=joinRecordingId;
            noMelodyNote.setVisibility(View.GONE);
            recyclerViewInstruments.setVisibility(View.VISIBLE);
            recyclerViewInstruments.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerViewInstruments.setLayoutManager(layoutManager);
            recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
            recyclerViewInstruments.setAdapter(adapter);

            //rlSync.setVisibility(View.VISIBLE);
            if (instrumentList.size() > 0) {
                //rlSync.setVisibility(View.VISIBLE);
                IsDirect = true;
            }

            ArrayList<MelodyCard> arrayMelody = new ArrayList<>();

            arrayMelody = MelodyCardListAdapter.returnMelodyList();
            try {
//                    melodyName = arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName();
                try {
                    if (arrayMelody.size() > 0) {
                        int i = Integer.parseInt(arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName());
                    }
                } catch (NumberFormatException ex) { // handle your exception
                    //melodyName = arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName();
                }

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < instrumentList.size(); i++) {
                MelodyInstruments instruments = instrumentList.get(i);
                instrumentName = instruments.getInstrumentName();
            }

            LocalBroadcastManager.getInstance(this).registerReceiver(mInstruments, new IntentFilter("fetchingInstruments"));

        } else {
            Intent stdintent = getIntent();

            if (intent == null) {
            } else {
                try {
                    IscheckMelody = stdintent.getExtras().getString("IsFromSignActivity");
                    IsHomeMeloduId = stdintent.getExtras().getString("melodyPackId");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }


            if (IscheckMelody == null) {
                if (melodyPackId != null) {
                    IsDirect = true;
                    if (instJson==null){
                        fetchInstruments(melodyPackId);
                    }
                    switchPublic.setVisibility(View.VISIBLE);

                    JoinActivity.instrumentList.clear();
                    noMelodyNote.setVisibility(View.GONE);
                    recyclerViewInstruments.setVisibility(View.VISIBLE);
                    recyclerViewInstruments.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerViewInstruments.setLayoutManager(layoutManager);
                    recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
                    adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());

                    //recyclerViewInstruments.smoothScrollToPosition(0);
                    recyclerViewInstruments.setAdapter(adapter);


                    //rlSync.setVisibility(View.VISIBLE);
                    if (instrumentList.size() > 0) {
                        //rlSync.setVisibility(View.VISIBLE);
                    }


                    ArrayList<MelodyCard> arrayMelody = new ArrayList<>();

                    arrayMelody = MelodyCardListAdapter.returnMelodyList();
                    try {
//                    melodyName = arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName();
                        try {
                            if (arrayMelody.size() > 0) {
                                int i = Integer.parseInt(arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName());
                            }
                        } catch (NumberFormatException ex) { // handle your exception
                            //melodyName = arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName();
                        }

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }


                    for (int i = 0; i < instrumentList.size(); i++) {
                        MelodyInstruments instruments = instrumentList.get(i);
                        instrumentName = instruments.getInstrumentName();
                    }

                    LocalBroadcastManager.getInstance(this).registerReceiver(mInstruments, new IntentFilter("fetchingInstruments"));


                }
            } else {
                try {

                    StudioActivity.playAll.setVisibility(View.VISIBLE);
                    StudioActivity.pauseAll.setVisibility(View.GONE);
                    StudioActivity.pauseAll.setEnabled(true);
                    tvDone.setEnabled(true);
                    StudioActivity.ivRecord_stop.setVisibility(View.GONE);
                    StudioActivity.rlRecordingButton.setVisibility(View.GONE);
                    StudioActivity.ivRecord_play.setVisibility(View.VISIBLE);
                    StudioActivity.rlRedoButton.setVisibility(View.VISIBLE);
                    if (IsHomeMeloduId != null && instJson==null) {
                        rlPublic.setVisibility(View.VISIBLE);
                        fetchInstruments(IsHomeMeloduId);
                        melodyPackId = IsHomeMeloduId;
                        JoinActivity.instrumentList.clear();
                        noMelodyNote.setVisibility(View.GONE);
                        recyclerViewInstruments.setVisibility(View.VISIBLE);
                        recyclerViewInstruments.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerViewInstruments.setLayoutManager(layoutManager);
                        recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
                        adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());

                        //recyclerViewInstruments.smoothScrollToPosition(0);
                        recyclerViewInstruments.setAdapter(adapter);

                        //frameTrans.setVisibility(View.VISIBLE);
                        //rlSync.setVisibility(View.VISIBLE);
                        if (instrumentList.size() > 0) {
                            //rlSync.setVisibility(View.VISIBLE);
                        }


                        ArrayList<MelodyCard> arrayMelody = new ArrayList<>();

                        arrayMelody = MelodyCardListAdapter.returnMelodyList();
                        try {
//                    melodyName = arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName();
                            try {
                                if (arrayMelody.size() > 0) {
                                    int i = Integer.parseInt(arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName());
                                }
                            } catch (NumberFormatException ex) { // handle your exception
                                //melodyName = arrayMelody.get(Integer.parseInt(melodyPackId)).getMelodyName();
                            }

                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }


                        for (int i = 0; i < instrumentList.size(); i++) {
                            MelodyInstruments instruments = instrumentList.get(i);
                            instrumentName = instruments.getInstrumentName();
                        }
                        IsHomeMeloduId = null;
                        IscheckMelody = null;
                        stdintent.removeExtra("IsFromSignActivity");
                        stdintent.removeExtra("melodyPackId");
                        LocalBroadcastManager.getInstance(this).registerReceiver(mInstruments, new IntentFilter("fetchingInstruments"));


                    }


                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }


        }
        String home, homeTostudio;
        SharedPreferences fromHome = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE);
        home = fromHome.getString("click", null);
        SharedPreferences fromHometoST = getApplicationContext().getSharedPreferences("HomeStudio", MODE_PRIVATE);
        homeTostudio = fromHometoST.getString("clickFromSt", null);
        try {
            if (home!=null && homeTostudio!=null){
                if (home.equals("from home") || homeTostudio.equals("from home")) {
                    SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
                    FilterPref.clear();
                    FilterPref.apply();
                    SharedPreferences.Editor FilterPref1 = getApplicationContext().getSharedPreferences("HomeStudio", MODE_PRIVATE).edit();
                    FilterPref1.clear();
                    FilterPref1.apply();


                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

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
        TwitprofilePic = twitterPref.getString("profilePic", null);// twitterPref.getString("ProfilePic", null);
        statusTwitter = twitterPref.getInt("status", 0);

        if (statusTwitter == 1) {
            artist_name.setText("@" + userName);
        }

        if (TwitprofilePic != null) {
            //ivProfile.setVisibility(View.GONE);
            profile_image.setVisibility(View.VISIBLE);
            Picasso.with(StudioActivity.this).load(TwitprofilePic).into(profile_image);
        }

        SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
        fbName = fbPref.getString("FbName", null);
        fbUserName = fbPref.getString("userName", null);
        fbId = fbPref.getString("fbId", null);
        statusFb = fbPref.getInt("status", 0);

        /*SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
        fbId = fbPref.getString("fbId", null);
        fbUserName = fbPref.getString("UserName", null);
        statusFb = fbPref.getInt("status", 0);*/


        if (statusFb == 1) {
            artist_name.setText("@" + fbName);
        }

        if (fbId != null) {
            //ivProfile.setVisibility(View.GONE);
            profile_image.setVisibility(View.VISIBLE);
            Picasso.with(StudioActivity.this).load("https://graph.facebook.com/" + fbId + "/picture").into(profile_image);
        }

        //SharedPreferences profileEditor = getApplicationContext().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
        //SharedPreferences profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE);
        //if (profileImageEditor.getString("ProfileImage", null) != null) {
        //    ivProfile.setVisibility(View.GONE);
        //    profile_image.setVisibility(View.VISIBLE);
        //    Picasso.with(StudioActivity.this).load(profileImageEditor.getString("ProfileImage", null)).into(profile_image);
        //}
        //if (profileEditor.getString("updateId", null) != null) {
        //    artist_name.setText("@" + profileEditor.getString("updateUserName", null));
        //}

//        audioFilePath =
//                Environment.getExternalStorageDirectory().getAbsolutePath()
//                        + "/YoMelody.mp3";
        try {
            String YoMelody = "YoMelody";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//            File image = File.createTempFile(
//                    YoMelody,  /* prefix */
//                    ".mp3",         /* suffix */
//                    storageDir      /* directory */
//            );
            audioFilePath = storageDir.toString() + "/YoMelody.mp3";

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        rlInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudioActivity.this, ContactsActivity.class);
                intent.putExtra("Previous", "studioActivity");
                startActivity(intent);
            }
        });
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;

                int s = ((int) (time - h * 3600000 - m * 60000) / 1000);

                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                chronometer.setText(t);
                AppHelper.sop("s=="+s+"=PackDuration="+PackDuration+"=IsRecordingStart="+IsRecordingStart);
                if (s >= PackDuration && IsRecordingStart && PackDuration!=0) {

                    Toast.makeText(mActivity, "Your recording Layer/Duration should be less than or equal to your subscription pack.", Toast.LENGTH_SHORT).show();
                    try {


                        try {
                            // if (!mMyTask.isCancelled() &&  mMyTask.getStatus() == AsyncTask.Status.RUNNING) {
                            mMyTask.cancel(true);
                            //}
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        IsRepeteReAll = false;
                        if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                            recordTask.cancel(false);
                        } else {
                            //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                        }
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        IsRecordingStart = false;
                        InstrumentCountSize = 0;
                        playAll.setVisibility(View.VISIBLE);
                        pauseAll.setVisibility(View.GONE);
                        pauseAll.setEnabled(true);

                        ivRecord_stop.setVisibility(View.GONE);
                        rlRecordingButton.setVisibility(View.GONE);
                        ivRecord_play.setVisibility(View.VISIBLE);
                        rlRedoButton.setVisibility(View.VISIBLE);
                        if (joinRecordingId != null) {
                            tvPublic.setVisibility(View.GONE);
                            switchPublic.setVisibility(View.GONE);
                        } else {
                            tvPublic.setVisibility(View.VISIBLE);
                            switchPublic.setVisibility(View.VISIBLE);
                        }

                        frameProgress.setVisibility(View.GONE);


                        if (isRecording) {

                        } else {
                            try {
                                rlRecordingButton.setEnabled(true);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        }

                        try {
                            for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                                PlayAllModel.get(i).setRepete(false);
                                if (i <= lstViewHolder.size() - 1) {
                                    final ImageView holderPlay = lstViewHolder.get(i).holderPlay;
                                    final ImageView holderPause = lstViewHolder.get(i).holderPause;
                                    final SeekBar seekBar = lstViewHolder.get(i).seekBar;
                                    final TextView txtMutes = lstViewHolder.get(i).TxtMuteViewHolder;
                                    final TextView txtSolos = lstViewHolder.get(i).TxtSoloViewHolder;
                                    final RelativeLayout RlsRepets = lstViewHolder.get(i).TempRlRepeats;
                                    seekBar.setProgress(0);
                                    holderPlay.setVisibility(View.VISIBLE);
                                    holderPause.setVisibility(View.GONE);
                                    txtMutes.setBackgroundColor(Color.TRANSPARENT);
                                    txtSolos.setBackgroundColor(Color.TRANSPARENT);
                                    RlsRepets.setBackgroundColor(Color.TRANSPARENT);
                                    holderPause.setEnabled(true);
                                    try {
                                        mediaPlayersAll.get(i).stop();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }


                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            tvDone.setEnabled(true);
                            chrono.stop();
                            if (mpall != null) {
                                try {
                                    mpall.stop();
                                    mpall.reset();
                                    mpall.release();
                                    mpall = null;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }


                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        if (mVisualizer != null) {
                            mVisualizerView.clearAnimation();
                            mVisualizer.release();
                            //mVisualizerView.clearFocus();
                        }

                        try {
                            InputStream inputStream =
                                    getApplicationContext().getContentResolver().openInputStream(Uri.fromFile(new File(audioFilePath)));
                            soundBytes = new byte[inputStream.available()];
                            soundBytes = toByteArray(inputStream);
                            inputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        stop_rec_time = SystemClock.elapsedRealtime() - chrono.getBase();


                        time_stop = formateMilliSeccond(stop_rec_time);

                        try {
                            recordingDuration = time_stop;
                            //Toast.makeText(StudioActivity.this, recordingDuration, Toast.LENGTH_SHORT).show();
                            try {
                                chrono.setBase(SystemClock.elapsedRealtime());
                                chrono.stop();
                                chrono.setText("00:00:00");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        chrono.setBase(SystemClock.elapsedRealtime());
        chrono.setText("00:00:00");
        rlMelodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IsRecordingStart = false;
                    if (StudioActivity.PlayAllModel.size() > 0) {
                        for (int i = 0; i <= StudioActivity.PlayAllModel.size() - 1; i++) {
                            StudioActivity.PlayAllModel.get(i).setRepete(false);
                        }
                    }

                    if (mVisualizer != null) {
                        mVisualizer.release();
                    }
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    }

                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
                    }
                    if (mediaPlayer != null) {
                        try {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (mp_start.size() > 0) {
                        for (int i = 0; i <= mp_start.size() - 1; i++) {
                            try {
                                mp_start.get(i).stop();
                                mp_start.get(i).release();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (mpall != null) {
                        try {
                            mpall.stop();
                            mpall = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            try {
                                mediaPlayersAll.get(i).stop();
                                mediaPlayersAll.get(i).release();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                        mediaPlayersAll.clear();
                    }
                    if (mpInst != null) {
                        try {
                            mpInst.stop();
                            mpInst = null;
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }


                    if (isRecording) {
                        // ivRecord.setEnabled(false);
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        try {
                            recorder.release();
                            recorder = null;
                            isRecording = false;
                            StudioActivity.tvDone.setEnabled(true);
                            StudioActivity.chrono.stop();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {

                            rlRecordingButton.setEnabled(true);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    PrepareInstruments prepareInstruments = new PrepareInstruments();
                    prepareInstruments.cancel(true);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(StudioActivity.this, MelodyActivity.class);
                    startActivity(intent);
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("cover response", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.commit();
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    CloseConnection();
                    PrepareInstruments prepareInstruments = new PrepareInstruments();
                    prepareInstruments.cancel(false);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    CloseConnection();
                    PrepareInstruments prepareInstruments = new PrepareInstruments();
                    prepareInstruments.cancel(true);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    CloseConnection();
                    PrepareInstruments prepareInstruments = new PrepareInstruments();
                    prepareInstruments.cancel(true);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    CloseConnection();
                    PrepareInstruments prepareInstruments = new PrepareInstruments();
                    prepareInstruments.cancel(true);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    CloseConnection();
                    PrepareInstruments prepareInstruments = new PrepareInstruments();
                    prepareInstruments.cancel(true);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(StudioActivity.this, ProfileActivity.class);
                    startActivity(i);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    CloseConnection();
                    PrepareInstruments prepareInstruments = new PrepareInstruments();
                    prepareInstruments.cancel(true);
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(StudioActivity.this, StationActivity.class);
                    startActivity(i);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsDirect) {
                    if (joinRecordingId == null) {
                        if (userId != null && melodyPackId != null) {
                            if (Integer.parseInt(recordingDuration)-1 > PackDuration && PackDuration != 0) {
                                Toast.makeText(StudioActivity.this, "Your recording duration should be less then or equal to your subscription pack.", Toast.LENGTH_SHORT).show();

                            } else {
                                openDialog();
                                ivRecord.setVisibility(View.VISIBLE);
                                ivRecord.setEnabled(true);
                            }
                        } else if (userId == null) {

                            Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                            i.putExtra("StudioBack", "ReturnStudioScreen");
                            i.putExtra("melodyPackId", melodyPackId);
                            startActivity(i);
                            Toast.makeText(StudioActivity.this, "SignIn to Save Recording", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        uploadRecordingsMixing();
                    }
                } else {
                    if (Integer.parseInt(recordingDuration) > PackDuration && PackDuration != 0) {
                        Toast.makeText(StudioActivity.this, "Your recording duration should be less then or equal to your subscription pack.", Toast.LENGTH_SHORT).show();

                    } else {
                        if (instrumentList.size() <= LayerCount || LayerCount==0) {
                            if (joinRecordingId == null) {
                                openDialog();
                                ivRecord.setVisibility(View.VISIBLE);
                                ivRecord.setEnabled(true);
                            } else {
                                uploadRecordingsMixing();
                            }
                        } else {
                            if (IsExp == "false") {
                                if (instrumentList.size() > LayerCount && LayerCount != 0) {
                                    Toast.makeText(StudioActivity.this, "You can add only " + LayerCount + " layers of instruments." + "please subscribed another pack.", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                Toast.makeText(StudioActivity.this, "Your subscription pack has been expired please subscribed.", Toast.LENGTH_SHORT).show();

                            }
                        }

                    }
                }

            }
        });


        rlRedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (mediaPlayer!=null){
                        try {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (IsDirect) {
//                    FirebaseCrash.report(new Exception("My first Android non-fatal error"));
//                    FirebaseCrash.log("My First Crash Log:");
//                    int b = 1 / 0;
//                    String a = 16 + "l";
                        IsHomeMeloduId = null;
                        IscheckMelody = null;
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        IsDirect = true;
                        IsRecordingStart = false;
                        ivRecord_play.setVisibility(View.INVISIBLE);
                        rlRedoButton.setVisibility(View.INVISIBLE);
                        ivRecord.setVisibility(View.VISIBLE);
                        rlMelodyButton.setVisibility(View.VISIBLE);
                        StudioActivity.playAll.setVisibility(View.GONE);
                        StudioActivity.pauseAll.setVisibility(View.GONE);
                        rlPublic.setVisibility(View.GONE);
                        playAll.setEnabled(true);
                        tvDone.setEnabled(false);
                        ivRecord.setVisibility(View.VISIBLE);
                        playAll.setVisibility(View.VISIBLE);
                        chrono.setBase(SystemClock.elapsedRealtime());
                        chrono.stop();
                        chrono.setText("00:00:00");
                        mVisualizerView.clearFocus();
                        if (mVisualizer != null) {
                            mVisualizer.release();
                        }
                        //StudioActivity.this.recreate();
                        if (lstViewHolder.size() > 0) {
                            instrumentList.clear();
                            lstViewHolder.clear();
                        }

                        if (joinRecordingId != null && melodyPackId == null) {
                            fetchInstrumentsForJoin(JoinActivity.addedBy, JoinActivity.RecId, Integer.parseInt(joinRecordingId));
                        } else {
                            fetchInstruments(melodyPackId);
                        }
                        switchPublic.setVisibility(View.VISIBLE);

                        JoinActivity.instrumentList.clear();

                        noMelodyNote.setVisibility(View.GONE);
                        recyclerViewInstruments.setVisibility(View.VISIBLE);
                        recyclerViewInstruments.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerViewInstruments.setLayoutManager(layoutManager);
                        recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
                        adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());

                        //recyclerViewInstruments.smoothScrollToPosition(0);
                        recyclerViewInstruments.setAdapter(adapter);


                        //rlSync.setVisibility(View.VISIBLE);
                        if (instrumentList.size() > 0) {
                            //rlSync.setVisibility(View.VISIBLE);
                        }
                        mMyTask = null;
                    } else {
                        IsHomeMeloduId = null;
                        IscheckMelody = null;
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        ivRecord_play.setVisibility(View.INVISIBLE);
                        rlRedoButton.setVisibility(View.INVISIBLE);
                        ivRecord.setVisibility(View.VISIBLE);
                        rlMelodyButton.setVisibility(View.VISIBLE);
                        StudioActivity.playAll.setVisibility(View.GONE);
                        StudioActivity.pauseAll.setVisibility(View.GONE);
                        rlPublic.setVisibility(View.GONE);
                        playAll.setEnabled(true);
                        tvDone.setEnabled(false);
                        ivRecord.setVisibility(View.VISIBLE);
                        playAll.setVisibility(View.VISIBLE);
                        chrono.setBase(SystemClock.elapsedRealtime());
                        chrono.stop();
                        chrono.setText("00:00:00");
                        mVisualizerView.clearFocus();
                        if (mVisualizer != null) {
                            mVisualizer.release();
                        }
                        mMyTask = null;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        rlSetCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (IsRecordingStart) {
                        Toast.makeText(StudioActivity.this, "Please stopped recording first.", Toast.LENGTH_SHORT).show();
                    } else {
                        showFileChooser();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        rlInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                    intent.putExtra("Previous", "studioActivity");
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });


        rlPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userId != null) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudioActivity.this);
                    alertDialog.setTitle("Make Public?");
                    alertDialog.setMessage("As a moderator feel free to make public or private anytime");
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
                    alertDialog.setMessage("As a moderator feel free to make public or private anytime");
                    alertDialog.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                            i.putExtra("StudioBack", "ReturnStudioScreen");
                            i.putExtra("melodyPackId", melodyPackId);
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


        ivSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View customView = mInflater.inflate(R.layout.view_master_vol, null, false);
                    final Dialog alertDialog = new Dialog(StudioActivity.this);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(customView);

                    ImageView closeMasterVol = (ImageView) alertDialog.findViewById(R.id.closeMasterVol);
                    com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar RecSeekbar = (com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar) alertDialog.findViewById(R.id.sbRec);
                    com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar MelSeekbar = (com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar) alertDialog.findViewById(R.id.sbMelody);

                    audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    //set max progress according to volume
                    //RecSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                    MelSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                    //get current volume
                    RecSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    MelSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    //Set the seek bar progress to 1
                    //RecSeekbar.setKeyProgressIncrement(1);
                    MelSeekbar.setKeyProgressIncrement(1);
                    //get max volume
                    //RecmaxVolume=RecSeekbar.getMax();
                    MelmaxVolume = MelSeekbar.getMax();

                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
                    wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    alertDialog.show();


                    closeMasterVol.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    RecSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            try {
                                float volume = (float) (1 - (Math.log(100 - progress) / Math.log(100)));
                                if (mediaPlayer != null) {
                                    mediaPlayer.setVolume(volume, volume);

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    MelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            try {
                                //Toast.makeText(StudioActivity.this, "Melody", Toast.LENGTH_SHORT).show();
                                float volume = (float) (1 - (Math.log(100 - progress) / Math.log(100)));
                                /*if(mediaPlayersAll.size()>0) {
                                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {

                                        pts = new MediaPlayer();
                                        // Create the Visualizer object and attach it to our media player.
                                        pts = mediaPlayersAll.get(i);
                                        try {
                                            pts.setVolume(volume,volume);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    }
                                }*/
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        mRecordingThread = new RecordingThread();
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioBuffer = new short[mBufferSize / 2];

        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IsRecordingStart = false;
                    if (IsDirect) {
                        StudioActivity.recyclerViewInstruments.smoothScrollToPosition(instrumentList.size());

                        if (!IsValidPack || instrumentList.size() <= LayerCount) {
                            mMyTask = new PrepareInstruments().execute();
                        } else {
                            if (IsExp == "false") {
                                if (LayerCount < instrumentList.size() && LayerCount != 0) {
                                    Toast.makeText(StudioActivity.this, "You can add only " + LayerCount + " layers of instruments." + "please subscribed another pack.", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                Toast.makeText(StudioActivity.this, "Your subscription pack has been expired please subscribed.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } else if (!IsDirect && instrumentList.size() > 0) {
                        mMyTask = new PrepareInstruments().execute();
                    } else {

                        playAll.setEnabled(false);
                        rlPublic.setVisibility(View.VISIBLE);
                        playAll.setVisibility(View.GONE);
                        ivRecord.setVisibility(View.GONE);
                        rlMelodyButton.setVisibility(View.GONE);
                        ivRecord_stop.setVisibility(View.VISIBLE);
                        rlRecordingButton.setVisibility(View.VISIBLE);
                        pauseAll.setVisibility(View.VISIBLE);
                        pauseAll.setEnabled(false);
                        chrono.setBase(SystemClock.elapsedRealtime());
                        chrono.start();
                        launchTask();

                        if (mediaPlayer == null){
                            mShouldContinue = true;
                            mVisualizerView.setVisibility(View.GONE);
                            waveform_view.setVisibility(View.VISIBLE);
                            try {
                                mRecordingThread.start();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            mVisualizerView.setVisibility(View.VISIBLE);
                            waveform_view.setVisibility(View.GONE);
                        }

                        try {

                            recyclerViewInstruments.setVisibility(View.VISIBLE);
                            recyclerViewInstruments.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerViewInstruments.setLayoutManager(layoutManager);
                            recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
                            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());

                            //recyclerViewInstruments.smoothScrollToPosition(0);
                            recyclerViewInstruments.setAdapter(adapter);
                            if (list.size() > 0) {
                                list.clear();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }


                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }


        });

        ivRecord_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    try {
                        // if (!mMyTask.isCancelled() &&  mMyTask.getStatus() == AsyncTask.Status.RUNNING) {
                        mMyTask.cancel(true);
                        //}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    IsRepeteReAll = false;
                    if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                        recordTask.cancel(false);
                    } else {
                        Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
                    }
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    }
                    IsRecordingStart = false;
                    InstrumentCountSize = 0;
                    playAll.setVisibility(View.VISIBLE);
                    pauseAll.setVisibility(View.GONE);
                    pauseAll.setEnabled(true);

                    ivRecord_stop.setVisibility(View.GONE);
                    rlRecordingButton.setVisibility(View.GONE);
                    ivRecord_play.setVisibility(View.VISIBLE);
                    rlRedoButton.setVisibility(View.VISIBLE);
                    if (joinRecordingId != null) {
                        tvPublic.setVisibility(View.GONE);
                        switchPublic.setVisibility(View.GONE);
                    } else {
                        tvPublic.setVisibility(View.VISIBLE);
                        switchPublic.setVisibility(View.VISIBLE);
                    }

                    frameProgress.setVisibility(View.GONE);


                    if (isRecording) {

                    } else {
                        try {
                            rlRecordingButton.setEnabled(true);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }

                    try {
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            PlayAllModel.get(i).setRepete(false);
                            if (i <= lstViewHolder.size() - 1) {
                                final ImageView holderPlay = lstViewHolder.get(i).holderPlay;
                                final ImageView holderPause = lstViewHolder.get(i).holderPause;
                                final SeekBar seekBar = lstViewHolder.get(i).seekBar;
                                final TextView txtMutes = lstViewHolder.get(i).TxtMuteViewHolder;
                                final TextView txtSolos = lstViewHolder.get(i).TxtSoloViewHolder;
                                final RelativeLayout RlsRepets = lstViewHolder.get(i).TempRlRepeats;
                                seekBar.setProgress(0);
                                holderPlay.setVisibility(View.VISIBLE);
                                holderPause.setVisibility(View.GONE);
                                txtMutes.setBackgroundColor(Color.TRANSPARENT);
                                txtSolos.setBackgroundColor(Color.TRANSPARENT);
                                RlsRepets.setBackgroundColor(Color.TRANSPARENT);
                                holderPause.setEnabled(true);
                                try {
                                    mediaPlayersAll.get(i).stop();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }


                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    try {
                        tvDone.setEnabled(true);
                        chrono.stop();
                        if (mpall != null) {
                            try {
                                mpall.stop();
                                mpall.reset();
                                mpall.release();
                                mpall = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }


                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (mVisualizer != null) {
                        mVisualizerView.clearAnimation();
                        mVisualizer.release();
                        //mVisualizerView.clearFocus();
                    }

                    try {
                        InputStream inputStream =
                                getApplicationContext().getContentResolver().openInputStream(Uri.fromFile(new File(audioFilePath)));
                        soundBytes = new byte[inputStream.available()];
                        soundBytes = toByteArray(inputStream);
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    stop_rec_time = SystemClock.elapsedRealtime() - chrono.getBase();
                    time_stop = formateMilliSeccond(stop_rec_time);
                    try {
                        recordingDuration = time_stop;
                        //Toast.makeText(StudioActivity.this, recordingDuration, Toast.LENGTH_SHORT).show();
                        try {
                            chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.stop();
                            chrono.setText("00:00:00");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (mRecordingThread != null) {
                    try {
                        mVisualizerView.setVisibility(View.VISIBLE);
                        waveform_view.setVisibility(View.GONE);
//                        mRecordingThread.stopRunning();
                        mRecordingThread.stop();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

            }

        });

        ivRecord_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Toast.makeText(StudioActivity.this, "play", Toast.LENGTH_SHORT).show();
                    ivRecord_play.setVisibility(View.GONE);
                    rlRedoButton.setVisibility(View.GONE);
                    ivRecord_pause.setVisibility(View.VISIBLE);
                    rlListeningButton.setVisibility(View.VISIBLE);
                    try {

                        playAurdio();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();

                    StudioActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            try {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            chrono.stop();
                            chrono.setText("00:00:00");
//                            if(Integer.parseInt(recordingDuration+1)==PackDuration){
//                                chrono.stop();
//                            }


                            ivRecord_pause.setVisibility(View.INVISIBLE);
                            rlListeningButton.setVisibility(View.INVISIBLE);
                            ivRecord_play.setVisibility(View.VISIBLE);
                            rlRedoButton.setVisibility(View.VISIBLE);

                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        StudioActivity.ivRecord_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ivRecord_pause.setVisibility(View.INVISIBLE);
                    rlListeningButton.setVisibility(View.INVISIBLE);
                    ivRecord_play.setVisibility(View.VISIBLE);
                    rlRedoButton.setVisibility(View.VISIBLE);


                    if (mediaPlayer != null) {
                        try {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                    if (mVisualizer != null) {
                        try {
                            mVisualizer.release();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                        /*if (mpall != null) {
                            try {
                                mpall.stop();
                                mpall.reset();
                                mpall.release();
                                mpall = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                                try {
                                    mediaPlayersAll.get(i).stop();
                                    mediaPlayersAll.get(i).reset();
                                    mediaPlayersAll.get(i).release();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }*/
                    chrono.stop();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        // To get preferred buffer size and sampling rate.
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("BufferSize&samplerate", "Size :" + size + " & Rate: " + rate);

    }


    private void setAdapter(){
        noMelodyNote.setVisibility(View.GONE);
        recyclerViewInstruments.setVisibility(View.VISIBLE);
        recyclerViewInstruments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewInstruments.setLayoutManager(layoutManager);
        recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());

        adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
        recyclerViewInstruments.setAdapter(adapter);
    }

    public int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public void playAurdio() throws IOException {
        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            duration = mediaPlayer.getDuration();
            initAudio(mediaPlayer.getAudioSessionId());
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    void TweeterSharingWork() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.twitter.sdk.android.tweetcomposer.UPLOAD_SUCCESS");
        intentFilter.addAction("com.twitter.sdk.android.tweetcomposer.UPLOAD_FAILURE");
        intentFilter.addAction("com.twitter.sdk.android.tweetcomposer.TWEET_COMPOSE_CANCEL");

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
                    // success
                    /*if (googleSwitch) {
                        GoogleShare();
                    }*/
                    AppHelper.sop("TweetUploadService.UPLOAD_SUCCESS..");
//                    final Long tweetId = intentExtras.getLong(TweetUploadService.EXTRA_TWEET_ID);
                } else if (TweetUploadService.UPLOAD_FAILURE.equals(intent.getAction())) {
                    // failure
                    /*if (googleSwitch) {
                        GoogleShare();
                    }*/
                    AppHelper.sop("TweetUploadService.UPLOAD_FAILURE..");
//                    final Intent retryIntent = intentExtras.getParcelable(TweetUploadService.EXTRA_RETRY_INTENT);
                } /*else if (TweetUploadService.TWEET_COMPOSE_CANCEL.equals(intent.getAction())) {
                    // cancel
                }*/
                /*if (googleSwitch) {
                    GoogleShare();
                }*/
                AppHelper.sop("TweetUploadService.BroadcastReceiver..");
            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    private boolean StopMediaPlayer(MediaPlayer mp) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
            mp.release();
            mp = null;
        }
        return true;
    }

    public class RecordingThread extends Thread {
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


        public synchronized boolean shouldContinue() {
            return mShouldContinue;
        }

        public synchronized void stopRunning() {
            mShouldContinue = false;
        }


        public void updateDecibelLevel() {

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

   /* private static String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return formateMilliSeccond(Long.parseLong(durationStr));
    }*/

    public String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        /*int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);*/

        int hours = (int) (milliseconds / 3600000);
        int minutes = (int) (milliseconds - hours * 3600000) / 60000;
        // int seconds = (int) (milliseconds - hours * 3600000 - minutes * 60000) / 1000;
        int seconds = (int) (milliseconds - hours * 3600000 * 60000) / 1000;
        return String.valueOf(seconds);
    }


    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(StudioActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtTopicName = (EditText) subView.findViewById(R.id.dialogEtTopicName);
        sp = (Spinner) subView.findViewById(R.id.spinnerGenre);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genresName) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;

                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                } else {
                    // Pass convertView as null to prevent reuse of special case views
                    v = super.getDropDownView(position, null, parent);
                }

                // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }

        };
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
                //&& !selectedGenre.equals("1"))
                String SubTopic = subEtTopicName.getText().toString();
                if (!SubTopic.trim().equals("")) {
                    openDialog2();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(subEtTopicName.getWindowToken(), 0);
                } else {
                    openDialog();
                    subEtTopicName.setHint("Enter Topic Name");
                    //Toast.makeText(StudioActivity.this, "Please enter topic name.", Toast.LENGTH_SHORT).show();
                    //openDialog();
                }


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
                uploadRecordingsMixing();

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


    @TargetApi(17)
    public boolean checkPermissions() {
        if ((ContextCompat.checkSelfPermission(StudioActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(StudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

            return true;
        } else {
            return false;
        }
    }

    @TargetApi(17)
    public void setPermissions() {
        if (ContextCompat.checkSelfPermission(StudioActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(StudioActivity.this, Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(StudioActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_MICROPHONE);
            } else {
                ActivityCompat.requestPermissions(StudioActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_MICROPHONE);
            }
        }

        if (ContextCompat.checkSelfPermission(StudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(StudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(StudioActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
            } else {
                ActivityCompat.requestPermissions(StudioActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
            }
        }

    }

    public void fetchInstruments(String melodyPackId) {
        final String mpid = melodyPackId;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Instrument list Response", response);

                        if (userId != null) {
                            IsValidateSubscription();
                        }

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
                AppHelper.sop("fetchInstruments=params=="+params+"\n URL=="+MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void fetchInstrumentsForJoin(final String addedBy, final String RecId, final int position) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JOINED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();


                        Log.d("ReturnData", response);
                        instrumentList.clear();
//                        if (click_pos == 0) {
//                            new ParseContents(getApplicationContext()).parseJoinInstrument(response, JoinActivity.instrumentList, String.valueOf(click_pos));
//                            JoinActivity.adapter1 = new JoinInstrumentListAdp(JoinActivity.instrumentList, getApplicationContext());
//                            JoinActivity.recyclerViewInstruments.setAdapter(JoinActivity.adapter1);
//                        } else {
                        new ParseContents(getApplicationContext()).parseJoinInstrument(response, instrumentList, position);
                        InstrumentCountSize = MelodyInstruments.getInstrumentCount();
                        adapter.notifyDataSetChanged();
                        //   }

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
                params.put("userid", addedBy);
                params.put("rid", RecId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    public void uploadRecordingsMixing() {
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        IsMicConnectet();
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
                        MelodyResponseDetails = r1.getJSONObject("melody_data");
                        melodyurl = BASE_URL + MelodyResponseDetails.getString("melodyurl");
                        //urlRecording = r1.getString("melody");
                    } else {
                        MelodyResponseDetails = r1.getJSONObject("melody_data");
                        melodyurl = BASE_URL + MelodyResponseDetails.getString("melodyurl");
                        // urlRecording = r1.getString("recording");
                    }
                    IsRecordingStart = false;
                    if (flag.equals("success")) {
                        IsValidateSubscription();
                        if (msgflag.equals("Melody created")) {
                            tvDone.setEnabled(false);
                            MelodyInstruments melodyInstruments = new MelodyInstruments();
                            melodyInstruments.setInstrumentName(MelodyResponseDetails.getString("packname"));
                            melodyInstruments.setInstrumentBpm(MelodyResponseDetails.getString("bpm"));
                            melodyInstruments.setInstrumentFile("Blank");
                            melodyInstruments.setInstrumentLength(MelodyResponseDetails.getString("duration"));
                            melodyInstruments.setUserProfilePic(recPic);
                            melodyInstruments.setInstrumentCover(BASE_URL + MelodyResponseDetails.getString("coverpic"));
                            melodyInstruments.setInstrumentCreated(MelodyResponseDetails.getString("add_date"));
                            melodyInstruments.setUserName("@" + CommonUserName);
                            melodyInstruments.setInstrumentFile(melodyurl);
                            instrumentList.add(melodyInstruments);
                            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter);
                            recyclerViewInstruments.smoothScrollToPosition(instrumentList.size());
                            adapter.notifyDataSetChanged();

                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            ivRecord.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            switchFlag = "0";
                            frameProgress.refreshDrawableState();


                        } else {
                            tvDone.setEnabled(false);
                            MelodyInstruments melodyInstruments = new MelodyInstruments();
                            melodyInstruments.setInstrumentName(MelodyResponseDetails.getString("packname"));
                            melodyInstruments.setInstrumentBpm(MelodyResponseDetails.getString("bpm"));
                            melodyInstruments.setInstrumentFile("Blank");
                            melodyInstruments.setInstrumentLength(MelodyResponseDetails.getString("duration"));
                            melodyInstruments.setUserProfilePic(recPic);
                            melodyInstruments.setInstrumentCover(BASE_URL + MelodyResponseDetails.getString("coverpic"));
                            melodyInstruments.setInstrumentCreated(MelodyResponseDetails.getString("add_date"));
                            melodyInstruments.setUserName("@" + CommonUserName);
                            melodyInstruments.setInstrumentFile(melodyurl);
                            instrumentList.add(melodyInstruments);
                            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                            recyclerViewInstruments.setAdapter(adapter);
                            recyclerViewInstruments.smoothScrollToPosition(instrumentList.size());
                            adapter.notifyDataSetChanged();

                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            ivRecord.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            frameProgress.refreshDrawableState();
                            switchFlag = "0";

                        }
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        if (StudioActivity.lstViewHolder.size() > 0) {
                            StudioActivity.lstViewHolder.clear();
                        }
                        if (joinRecordingId != null) {
                            Toast.makeText(StudioActivity.this, "Joined Successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StudioActivity.this, "Recording saved Successfully.", Toast.LENGTH_SHORT).show();
                        }
                        try {
                            SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
                            FilterPref.clear();
                            FilterPref.apply();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        try {
                            chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.stop();
                            chrono.setText("00:00:00");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        thumbnailUrl = r1.getJSONObject("melody_data").getString("thumbnail_url");

                        SharedPreferences.Editor recEditor = getApplication().getSharedPreferences("Recording_MelodyDataResponse", MODE_PRIVATE).edit();
                        recEditor.clear();
                        recEditor.commit();

                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Url_recording", MODE_PRIVATE).edit();
                        editor.putString("Recording_url", melodyurl);
                        editor.commit();

                        SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                        //editorT.putString("thumbnailUrl", "http://52.89.220.199/api/thumbnail_url.php/?cp=http://52.89.220.199/api/uploads/cover.jpg&rc=http://52.89.220.199/api/uploads/recordings/rec1503669372.mp3");
                        editorT.putString("thumbnailUrl", thumbnailUrl);
                        editorT.apply();

                        SharedPreferences switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE);
                        int switchFbStatus = switchFbEditor.getInt("switch", 0);

                        AppHelper.sop("fbSwitch=" + socialStatusPref.getBoolean(Const.FB_STATUS, false) +
                                "=twitterSwitch=" + socialStatusPref.getBoolean(Const.TWITTER_STATUS, false) +
                                "=googleSwitch=" + socialStatusPref.getBoolean(Const.GOOGLE_STATUS, false)+
                                "=previousScreen="+previousScreen);

                        if (socialStatusPref.getBoolean(Const.FB_STATUS, false)) {
                            FbShare();
                        } else if (socialStatusPref.getBoolean(Const.GOOGLE_STATUS, false)) {
                            GoogleShare();
                        } else if (socialStatusPref.getBoolean(Const.TWITTER_STATUS, false)) {
                            TweetShare();
                        }else if (previousScreen.equalsIgnoreCase("ChatActivity")
                                ||previousScreen.equalsIgnoreCase("JoinActivity")){
                            finish();
                        }else if (!value1.equalsIgnoreCase("Melody")){
                            if (switchFlagTemp.equalsIgnoreCase("1")){
                                Intent intent = new Intent(mActivity, StationActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (switchFlagTemp.equalsIgnoreCase("0")){
                                Intent intent = new Intent(mActivity, MelodyActivity.class);
                                intent.putExtra("MyRecording","MyRecording");
                                startActivity(intent);
                                finish();
                            }
                        }

                        /*if (joinRecordingId != null) {
                            Intent intent = new Intent(StudioActivity.this, JoinActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(StudioActivity.this, StationActivity.class);
                            startActivity(intent);
                        }*/
                    }

                } catch (JSONException e) {
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                    e.printStackTrace();
                }
//                Log.d("return message", resultResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMsg = "";
                if (error instanceof TimeoutError) {
                    errorMsg = "Internet connection timed out";
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                } else if (error instanceof NoConnectionError) {
                    errorMsg = "There is no connection";
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                } else if (error instanceof AuthFailureError) {
                    errorMsg = "AuthFailureError";
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                } else if (error instanceof ServerError) {
                    errorMsg = "We are facing problem in connecting to server";
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                } else if (error instanceof NetworkError) {
                    errorMsg = "We are facing problem in connecting to network";
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                } else if (error instanceof ParseError) {
                    errorMsg = "ParseError";
                    if (progressDialog != null) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                Log.d("Error", errorMsg);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                switchFlagTemp=switchFlag;
                params.put(Mixuser_id, userId);
                params.put(Mixpublic_flag, switchFlag);
                params.put(MixrecordWith, IsMicConnected);
                params.put(Mixgenere, selectedGenre);
                params.put(Mixbpms, "128");
                params.put(Mixdurations, recordingDuration);
                params.put(MixCommand, "SaveRecord");
                if (joinRecordingId != null) {
                    params.put(MixisMelody, "Recording");
                    params.put(MixparentRecordingID, JoinActivity.RecId);
                    params.put(Mixtopic_name, "");
                } else {
                    params.put(Mixtopic_name, subEtTopicName.getText().toString().trim());
                    params.put(MixisMelody, value1);
                    params.put(MixparentRecordingID, "");
                }
                //params.put(Mixrecording, list.toString());

                JSONArray myarray = new JSONArray();
                try {
                    for (int i = 0; i <= list.size() - 1; i++) {

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
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params===" + params + "\n URL=" + MixingAudio_InstrumentsAudio);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put(Mixvocalsound, new DataPart("InstaMelody.mp3", soundBytes, "audio/amr"));
                if (ivNewRecordCover.getDrawable() != null) {
                    AppHelper.sop("param=DataPart=if==" + params);
                    params.put("cover", new DataPart("CoverImg.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), ivNewRecordCover.getDrawable()), "image/jpeg"));
                }
                AppHelper.sop("param=DataPart===" + params);
                return params;
            }

        };
        //VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

        RequestQueue requestQueue = Volley.newRequestQueue(StudioActivity.this);
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        multipartRequest.setRetryPolicy(policy);
        requestQueue.add(multipartRequest);
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
                                    if (i == 0) {
                                        genresName.add(i, "Genre");
                                    } else {
                                        genresName.add(i, genresArrayList.get(i).getName());
                                    }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("requestCode=" + requestCode + "=resultCode=" + resultCode + "=data=" + data);
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
        } else if (requestCode == REQUEST_GOOGLE_SHARE) {
            if (socialStatusPref.getBoolean(Const.TWITTER_STATUS, false)) {
                TweetShare();
            }
            else if (previousScreen.equalsIgnoreCase("ChatActivity")
                    ||previousScreen.equalsIgnoreCase("JoinActivity")){
                finish();
            }
            else if (!value1.equalsIgnoreCase("Melody")){
                if (switchFlagTemp.equalsIgnoreCase("1")){
                    Intent intent = new Intent(mActivity, StationActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (switchFlagTemp.equalsIgnoreCase("0")){
                    Intent intent = new Intent(mActivity, MelodyActivity.class);
                    intent.putExtra("MyRecording","MyRecording");
                    startActivity(intent);
                    finish();
                }
            }

        }
        else if (requestCode==TWEET_COMPOSER_REQUEST_CODE){
            if (previousScreen.equalsIgnoreCase("ChatActivity")
                    ||previousScreen.equalsIgnoreCase("JoinActivity")){
                finish();
            }
            else if (!value1.equalsIgnoreCase("Melody")){
                if (switchFlagTemp.equalsIgnoreCase("1")){
                    Intent intent = new Intent(mActivity, StationActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (switchFlagTemp.equalsIgnoreCase("0")){
                    Intent intent = new Intent(mActivity, MelodyActivity.class);
                    intent.putExtra("MyRecording","MyRecording");
                    startActivity(intent);
                    finish();
                }
            }
        }
        else {
            if (callbackManager != null) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (mVisualizer != null) {
                mVisualizer.release();
            }
            if (StudioActivity.mp_start != null) {

                for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                    try {
                        StudioActivity.mp_start.get(i).stop();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
            if (mpall != null) {
                mpall.stop();
                if (mediaPlayersAll.size() > 0) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        try {
                            mediaPlayersAll.get(i).stop();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            if (StudioActivity.mpInst != null) {
                try {
                    StudioActivity.mpInst.stop();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        unregisterReceiver(mReceiver);
        if (mRecordingThread!=null){
            mRecordingThread.stopRunning();
        }

        super.onDestroy();
    }

    public BroadcastReceiver mInstruments = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            receiveInstruments = intent.getStringExtra("instruments");
//            Toast.makeText(StudioActivity.this, "" + receiveInstruments, Toast.LENGTH_SHORT).show();
            instruments_count = intent.getStringArrayListExtra("instruments");
        }
    };


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
//
//    public byte[] toByteArray(InputStream in) throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int read = 0;
//        byte[] buffer = new byte[1024];
//        while (read != -1) {
//            read = in.read(buffer);
//            if (read != -1)
//                out.write(buffer,0,read);
//        }
//        out.close();
//        return out.toByteArray();
//    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        byte[] buffer = new byte[8192];
        while ((read = in.read(buffer)) > 0) {


            out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //startService(new Intent(this, LogoutService.class));
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }

            if (mVisualizer != null) {
                mVisualizer.release();
            }

            if (StudioActivity.mp_start != null) {

                for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                    if (StudioActivity.mp_start.get(i).isPlaying()) {
                        try {
                            StudioActivity.mp_start.get(i).stop();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
            if (mpall != null) {

                mpall.stop();
                if (mediaPlayersAll.size() > 0) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        if (mediaPlayersAll.get(i).isPlaying()) {
                            try {
                                mediaPlayersAll.get(i).stop();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            if (StudioActivity.mpInst != null) {
                StudioActivity.mpInst.stop();
            }

            if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                recordTask.cancel(false);
            } else {
                //Toast.makeText(StudioActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
            }
            if (mMyTask!=null){
                mMyTask.cancel(true);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setInsCount(int instCount) {
        if (instCount == 0) {
            melody_detail.setText("No Instrumental");
        } else if (instCount == 1) {
            melody_detail.setText("" + instCount + " Instrumental");
        } else {
            melody_detail.setText("" + instCount + " Instrumentals");
        }
    }

    public void FbShare() {

        AppHelper.sop("thumbnailUrl==" + thumbnailUrl);

//        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(StudioActivity.this, "Recording Uploaded", Toast.LENGTH_SHORT).show();
                /*SharedPreferences.Editor editorT = getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                editorT.clear();
                editorT.apply();*/
                AppHelper.sop("FacebookCallback==onSuccess");
                if (socialStatusPref.getBoolean(Const.GOOGLE_STATUS, false)) {
                    GoogleShare();
                } else if (socialStatusPref.getBoolean(Const.TWITTER_STATUS, false)) {
                    TweetShare();
                }
                else if (previousScreen.equalsIgnoreCase("ChatActivity")
                        ||previousScreen.equalsIgnoreCase("JoinActivity")){
                    finish();
                }
                else if (!value1.equalsIgnoreCase("Melody")){
                    if (switchFlagTemp.equalsIgnoreCase("1")){
                        Intent intent = new Intent(mActivity, StationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (switchFlagTemp.equalsIgnoreCase("0")){
                        Intent intent = new Intent(mActivity, MelodyActivity.class);
                        intent.putExtra("MyRecording","MyRecording");
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(StudioActivity.this, "Recording not Uploaded", Toast.LENGTH_SHORT).show();
                AppHelper.sop("FacebookCallback==onCancel");
                if (socialStatusPref.getBoolean(Const.GOOGLE_STATUS, false)) {
                    GoogleShare();
                } else if (socialStatusPref.getBoolean(Const.TWITTER_STATUS, false)) {
                    TweetShare();
                }
                else if (previousScreen.equalsIgnoreCase("ChatActivity")
                        ||previousScreen.equalsIgnoreCase("JoinActivity")){
                    finish();
                }
                else if (!value1.equalsIgnoreCase("Melody")){
                    if (switchFlagTemp.equalsIgnoreCase("1")){
                        Intent intent = new Intent(mActivity, StationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (switchFlagTemp.equalsIgnoreCase("0")){
                        Intent intent = new Intent(mActivity, MelodyActivity.class);
                        intent.putExtra("MyRecording","MyRecording");
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onError(FacebookException error) {
                AppHelper.sop("FacebookCallback==onError");
                if (socialStatusPref.getBoolean(Const.GOOGLE_STATUS, false)) {
                    GoogleShare();
                } else if (socialStatusPref.getBoolean(Const.TWITTER_STATUS, false)) {
                    TweetShare();
                }
                else if (previousScreen.equalsIgnoreCase("ChatActivity")
                        ||previousScreen.equalsIgnoreCase("JoinActivity")){
                    finish();
                }
                else if (!value1.equalsIgnoreCase("Melody")){
                    if (switchFlagTemp.equalsIgnoreCase("1")){
                        Intent intent = new Intent(mActivity, StationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (switchFlagTemp.equalsIgnoreCase("0")){
                        Intent intent = new Intent(mActivity, MelodyActivity.class);
                        intent.putExtra("MyRecording","MyRecording");
                        startActivity(intent);
                        finish();
                    }
                }
            }

        });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(thumbnailUrl))
//                    .setImageUrl(Uri.parse(fetchThumbNailUrl))
                    .build();
            shareDialog.show(linkContent, ShareDialog.Mode.FEED);
        }
    }

    public void TweetShare() {

        try {
            AppHelper.sop("TweetShare call!!");
            /*TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
            Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());*/

            /*TweetComposer.Builder builder = new TweetComposer.Builder(this)
                    .text("")
                    .url(new URL(thumbnailUrl));
//                .image(Uri.parse(cover));
            builder.show();*/

            Intent intent = new TweetComposer.Builder(this)
                    .text("")
                    .url(new URL(thumbnailUrl))
                    .createIntent();
            startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);


        /*TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        Intent intent = new ComposerActivity.Builder(mActivity)
                .session(session)
                .card(ShortUrl)
                .text("Welcome to Twitter")
                .hashtags("#twitter")
                .createIntent();
        startActivityForResult(intent,107) ;*/

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void GoogleShare() {

        SharedPreferences editor = getSharedPreferences("Url_recording", MODE_PRIVATE);
        String contentUrl = editor.getString("Recording_url", "");

        SharedPreferences editorT = getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        String fetchThumbNailUrl = editorT.getString("thumbnailUrl", "");

        AppHelper.sop("contentUrl=" + contentUrl + "\nfetchThumbNailUrl" + fetchThumbNailUrl);

        Intent shareIntent = new PlusShare.Builder(this)
                .setType("text/plain")
                .setText("Welcome to the Google+ platform.")
                .setContentUrl(Uri.parse(thumbnailUrl))
                .getIntent();
        startActivityForResult(shareIntent, REQUEST_GOOGLE_SHARE);
    }

    private void IsMicConnectet() {
        AudioManager am1 = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        Log.i("WiredHeadsetOn = ", am1.isWiredHeadsetOn() + "");
        if (am1.isWiredHeadsetOn() == true) {
            IsMicConnected = "withMike";
            //Toast.makeText(getApplicationContext(), "Headset is connected", Toast.LENGTH_SHORT).show();
        } else {
            IsMicConnected = "withoutMike";
            //Toast.makeText(getApplicationContext(), "Headset not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTotalCount();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Const.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    public void getTotalCount() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TOTAL_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(HomeActivity.this, "" + response.toString();, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                String str = jsonObject.getString("newMessage");
                                totalCount = Integer.parseInt(str);
                                if (totalCount > 0) {
                                    message_count.setText(str);
                                    message_count.setVisibility(View.VISIBLE);
                                } else {
                                    message_count.setVisibility(View.GONE);
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
                        String errorMsg = "";
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            errorMsg = "There is either no connection or it timed out.";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "ServerError";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "Network Error";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put("userid", userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public static short[] bytesToShort(byte[] bytes) {
        //return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.BIG_ENDIAN);  // or LITTLE_ENDIAN
        short[] shorts = new short[bytes.length / 2];
        for (int i = 0; i < shorts.length; i++)
            shorts[i] = bb.getShort();

        return shorts;

    }


    private class PrepareInstruments extends AsyncTask<String, Void, Bitmap> {

        private AsyncTask<String, Void, Bitmap> asyncTask = null;

        protected void onPreExecute() {

            try {

                StudioActivity.ivBackButton.setEnabled(false);
                StudioActivity.ivHomeButton.setEnabled(false);
                IsRepeteReAll = false;
                InstrumentCountSize = 0;
                frameProgress.setVisibility(View.VISIBLE);
                if (PlayAllModel.size() > 0) {
                    PlayAllModel.clear();
                }
                if (mediaPlayersAll.size() > 0) {
                    mediaPlayersAll.clear();
                }
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }

                for (int i = 0; i <= mp_start.size() - 1; i++) {
                    try {

                        if (mp_start.get(i) != null) {
                            mp_start.get(i).stop();
                            mp_start.get(i).release();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onCancelled() {
            // Do something when async task is cancelled
            Log.d("Asysc operation ", "is cancel...");

        }

        protected Bitmap doInBackground(String... urls) {

            try {

                if (InstrumentCountSize == 0) {
                    tmpduration = 0;
                    Compdurations = 0;
                    MaxMpSessionID = 0;
                    IsRecordingStart = true;
                    InstrumentCountSize = instrumentList.size();
                    if (InstrumentCountSize > 0) {
                        try {
                            for (int i = 0; i < InstrumentCountSize; i++) {
                                Log.d("Instrument url-:", "" + instrumentList.get(i).getInstrumentFile());
                                mpall = new MediaPlayer();
                                mpall.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mpall.setDataSource(instrumentList.get(i).getInstrumentFile());
                                try {

                                    mpall.prepare();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                mediaPlayersAll.add(StudioActivity.mpall);
                                PlayAllModel.add(i, new ModelPlayAllMediaPlayer(false, false, false, StudioActivity.mpall));

                                Compdurations = StudioActivity.mediaPlayersAll.get(i).getDuration();
                                if (Compdurations > tmpduration) {
                                    tmpduration = Compdurations;
                                    MaxMpSessionID = StudioActivity.mediaPlayersAll.get(i).getAudioSessionId();
                                }

                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }


            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... values) {
            // Update user with progress bar or similar - runs on user interface thread

        }

        protected void onPostExecute(Bitmap result) {
            try {
                if (InstrumentCountSize > 0) {
                    IsRepeteReAll = true;
                    playAll.setEnabled(false);
                    rlPublic.setVisibility(View.VISIBLE);
                    playAll.setVisibility(View.GONE);
                    ivRecord.setVisibility(View.GONE);
                    rlMelodyButton.setVisibility(View.GONE);
                    ivRecord_stop.setVisibility(View.VISIBLE);
                    rlRecordingButton.setVisibility(View.VISIBLE);
                    pauseAll.setVisibility(View.VISIBLE);
                    pauseAll.setEnabled(false);
                    StudioActivity.ivBackButton.setEnabled(true);
                    StudioActivity.ivHomeButton.setEnabled(true);
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {

                        pts = new MediaPlayer();
                        // Create the Visualizer object and attach it to our media player.


                        pts = mediaPlayersAll.get(i);

                        try {
                            pts.start();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        try {
                            if (i <= lstViewHolder.size() - 1) {
                                final ImageView holderPlay = lstViewHolder.get(i).holderPlay;
                                final ImageView holderPause = lstViewHolder.get(i).holderPause;

                                holderPlay.setVisibility(View.GONE);
                                holderPause.setVisibility(View.VISIBLE);
                                holderPause.setEnabled(false);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                    initAudio(MaxMpSessionID);
                    frameProgress.setVisibility(View.GONE);
                    RunSeekbar();

                    if (ContextCompat.checkSelfPermission(StudioActivity.this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request permission
                        ActivityCompat.requestPermissions(StudioActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                PERMISSION_RECORD_AUDIO);
                        return;
                    }
                    launchTask();

                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                } else {
                    frameProgress.setVisibility(View.GONE);
                    playAll.setEnabled(false);
                    rlPublic.setVisibility(View.VISIBLE);
                    playAll.setVisibility(View.GONE);
                    ivRecord.setVisibility(View.GONE);
                    rlMelodyButton.setVisibility(View.GONE);
                    ivRecord_stop.setVisibility(View.VISIBLE);
                    rlRecordingButton.setVisibility(View.VISIBLE);
                    pauseAll.setVisibility(View.VISIBLE);
                    pauseAll.setEnabled(false);
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    launchTask();
                }

            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    launchTask();
                } else {
                    // Permission denied
                    Toast.makeText(this, "\uD83D\uDE41", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void launchTask() {
        switch (recordTask.getStatus()) {
            case RUNNING:
                Toast.makeText(this, "Task already running...", Toast.LENGTH_SHORT).show();
                return;
            case FINISHED:
                recordTask = new RecordWaveTask(this);
                break;
            case PENDING:
                if (recordTask.isCancelled()) {
                    recordTask = new RecordWaveTask(this);
                }
        }


        File wavFile = new File(audioFilePath);

//        File wavFile = new File(getFilesDir(), "recording_" + System.currentTimeMillis() / 1000 + ".wav");
//            Toast.makeText(this, wavFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        recordTask.execute(wavFile);

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        recordTask.setContext(null);
        return recordTask;
    }

    private class RecordWaveTask extends AsyncTask<File, Void, Object[]> {

        // Configure me!
        private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
        private static final int SAMPLE_RATE = 11025; // Hz 8000 11025 22050 44100
        private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
        //

        private final int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);

        private Context ctx;

        private RecordWaveTask(Context ctx) {
            setContext(ctx);
        }

        private void setContext(Context ctx) {
            this.ctx = ctx;
        }

        /**
         * Opens up the given file, writes the header, and keeps filling it with raw PCM bytes from
         * AudioRecord until it reaches 4GB or is stopped by the user. It then goes back and updates
         * the WAV header to include the proper final chunk sizes.
         *
         * @param files Index 0 should be the file to write to
         * @return Either an Exception (error) or two longs, the filesize, elapsed time in ms (success)
         */
        @Override
        protected Object[] doInBackground(File... files) {

            FileOutputStream wavOut = null;
            long startTime = 0;
            long endTime = 0;

            try {
                // Open our two resources
                audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);
                wavOut = new FileOutputStream(files[0]);

                // Write out the wav file header
                writeWavHeader(wavOut, CHANNEL_MASK, SAMPLE_RATE, ENCODING);

                // Avoiding loop allocations
                buffer = new byte[BUFFER_SIZE];
                boolean run = true;
                int read;
                long total = 0;

                // Let's go
                startTime = SystemClock.elapsedRealtime();
                audioRecord.startRecording();

                while (run && !isCancelled()) {
                    read = audioRecord.read(buffer, 0, buffer.length);

                    // WAVs cannot be > 4 GB due to the use of 32 bit unsigned integers.
                    if (total + read > 4294967295L) {
                        // Write as many bytes as we can before hitting the max size
                        for (int i = 0; i < read && total <= 4294967295L; i++, total++) {
                            wavOut.write(buffer[i]);


                        }
                        run = false;
                    } else {
                        // Write out the entire read buffer
                        wavOut.write(buffer, 0, read);
                        total += read;
                    }
                }
            } catch (IOException ex) {
                return new Object[]{ex};
            } finally {
                if (audioRecord != null) {
                    try {
                        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                            audioRecord.stop();
                            endTime = SystemClock.elapsedRealtime();
                        }
                    } catch (IllegalStateException ex) {
                        //
                    }
                    if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                        audioRecord.release();
                    }
                }
                if (wavOut != null) {
                    try {
                        wavOut.close();
                    } catch (IOException ex) {
                        //
                    }
                }
            }

            try {
                // This is not put in the try/catch/finally above since it needs to run
                // after we close the FileOutputStream
                updateWavHeader(files[0]);
            } catch (IOException ex) {
                return new Object[]{ex};
            }

            return new Object[]{files[0].length(), endTime - startTime};
        }

        /**
         * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
         * Two size fields are left empty/null since we do not yet know the final stream size
         *
         * @param out         The stream to write the header to
         * @param channelMask An AudioFormat.CHANNEL_* mask
         * @param sampleRate  The sample rate in hertz
         * @param encoding    An AudioFormat.ENCODING_PCM_* value
         * @throws IOException
         */
        private void writeWavHeader(OutputStream out, int channelMask, int sampleRate, int encoding) throws IOException {
            short channels;
            switch (channelMask) {
                case AudioFormat.CHANNEL_IN_MONO:
                    channels = 1;
                    break;
                case AudioFormat.CHANNEL_IN_STEREO:
                    channels = 2;
                    break;
                default:
                    throw new IllegalArgumentException("Unacceptable channel mask");
            }

            short bitDepth;
            switch (encoding) {
                case AudioFormat.ENCODING_PCM_8BIT:
                    bitDepth = 8;
                    break;
                case AudioFormat.ENCODING_PCM_16BIT:
                    bitDepth = 16;
                    break;
                case AudioFormat.ENCODING_PCM_FLOAT:
                    bitDepth = 32;
                    break;
                default:
                    throw new IllegalArgumentException("Unacceptable encoding");
            }

            writeWavHeader(out, channels, sampleRate, bitDepth);
        }

        /**
         * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
         * Two size fields are left empty/null since we do not yet know the final stream size
         *
         * @param out        The stream to write the header to
         * @param channels   The number of channels
         * @param sampleRate The sample rate in hertz
         * @param bitDepth   The bit depth
         * @throws IOException
         */
        private void writeWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
            // Convert the multi-byte integers to raw bytes in little endian format as required by the spec
            byte[] littleBytes = ByteBuffer
                    .allocate(14)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putShort(channels)
                    .putInt(sampleRate)
                    .putInt(sampleRate * channels * (bitDepth / 8))
                    .putShort((short) (channels * (bitDepth / 8)))
                    .putShort(bitDepth)
                    .array();

            // Not necessarily the best, but it's very easy to visualize this way
            out.write(new byte[]{
                    // RIFF header
                    'R', 'I', 'F', 'F', // ChunkID
                    0, 0, 0, 0, // ChunkSize (must be updated later)
                    'W', 'A', 'V', 'E', // Format
                    // fmt subchunk
                    'f', 'm', 't', ' ', // Subchunk1ID
                    16, 0, 0, 0, // Subchunk1Size
                    1, 0, // AudioFormat
                    littleBytes[0], littleBytes[1], // NumChannels
                    littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                    littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                    littleBytes[10], littleBytes[11], // BlockAlign
                    littleBytes[12], littleBytes[13], // BitsPerSample
                    // data subchunk
                    'd', 'a', 't', 'a', // Subchunk2ID
                    0, 0, 0, 0, // Subchunk2Size (must be updated later)
            });
        }

        /**
         * Updates the given wav file's header to include the final chunk sizes
         *
         * @param wav The wav file to update
         * @throws IOException
         */
        private void updateWavHeader(File wav) throws IOException {
            byte[] sizes = ByteBuffer
                    .allocate(8)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    // There are probably a bunch of different/better ways to calculate
                    // these two given your circumstances. Cast should be safe since if the WAV is
                    // > 4 GB we've already made a terrible mistake.
                    .putInt((int) (wav.length() - 8)) // ChunkSize
                    .putInt((int) (wav.length() - 44)) // Subchunk2Size
                    .array();

            RandomAccessFile accessWave = null;
            //noinspection CaughtExceptionImmediatelyRethrown
            try {
                accessWave = new RandomAccessFile(wav, "rw");
                // ChunkSize
                accessWave.seek(4);
                accessWave.write(sizes, 0, 4);

                // Subchunk2Size
                accessWave.seek(40);
                accessWave.write(sizes, 4, 4);
            } catch (IOException ex) {
                // Rethrow but we still close accessWave in our finally
                throw ex;
            } finally {
                if (accessWave != null) {
                    try {
                        accessWave.close();
                    } catch (IOException ex) {
                        //
                    }
                }
            }
        }

        @Override
        protected void onCancelled(Object[] results) {
            // Handling cancellations and successful runs in the same way
            onPostExecute(results);
        }

        @Override
        protected void onPostExecute(Object[] results) {
            Throwable throwable = null;
            if (results[0] instanceof Throwable) {
                // Error
                throwable = (Throwable) results[0];
                Log.e(RecordWaveTask.class.getSimpleName(), throwable.getMessage(), throwable);
            }

            // If we're attached to an activity
            if (ctx != null) {
                if (throwable == null) {
                    // Display final recording stats
                    double size = (long) results[0] / 1000000.00;
                    long time = (long) results[1] / 1000;
//                    Toast.makeText(ctx, String.format(Locale.getDefault(), "%.2f MB / %d seconds",
//                            size, time), Toast.LENGTH_LONG).show();
                } else {
                    // Error
                    //  Toast.makeText(ctx, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void initAudio(int mpst) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //mMediaPlayer = mpst;

        setupVisualizerFxAndUI(mpst);
        // Make sure the visualizer is enabled only when you actually want to
        // receive data, and
        // when it makes sense to receive data.
        mVisualizer.setEnabled(true);
        // When the stream ends, we don't need to collect any more data. We
        // don't do this in
        // setupVisualizerFxAndUI because we likely want to have more,
        // non-Visualizer related code
        // in this callback.
            /* mpst
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mVisualizer.setEnabled(false);
                    }
                });*/
        //mpst.start();

    }

    private void setupVisualizerFxAndUI(int mpvis) {

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mpvis);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }


    public void RunSeekbar() {

        try {
            for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {

                final MediaPlayer pts;
                //final SeekBar seekBarf;
                pts = mediaPlayersAll.get(i);
                final SeekBar seekBarf = lstViewHolder.get(i).seekBar;

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int currentPosition = pts.getCurrentPosition() / 1000;
                            int duration = pts.getDuration() / 1000;
                            int progress = (currentPosition * 100) / duration;
                            //seekBar.setProgress((int) (((float) pts.getCurrentPosition() / pts.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                            seekBarf.setProgress(progress);
                            handler.postDelayed(this, 1000);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }


        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void IsValidateSubscription() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, IsValidateSubPack,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        String titleString;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                IsExp = jsonObject.getString("is_expired");
                                LayerCount = Integer.parseInt(jsonObject.getString("layer"));
                                PackDuration = Integer.parseInt(jsonObject.getString("duration"));
                                if (IsExp == "false") {
                                    if (LayerCount == 0) {

                                    } else {
                                        if (instrumentList.size() > LayerCount) {
                                            Toast.makeText(StudioActivity.this, "You can add only " + LayerCount + " layers of instruments. " + "Please subscribed another pack.", Toast.LENGTH_SHORT).show();
                                            IsValidPack = true;
                                        }

                                    }

                                } else {
                                    Toast.makeText(StudioActivity.this, "Your subscription pack has been expired please subscribed.", Toast.LENGTH_SHORT).show();
                                    IsValidPack = true;
                                        /*SubscriptionsFragment subscriptionsFragment=new SubscriptionsFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.activity_studio, subscriptionsFragment).commit();*/
                                        /*Intent intent = new Intent(StudioActivity.this, SubscriptionsFragment.class);
                                        startActivity(intent);*/
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
                if (userId != null) {
                    params.put(USER_ID, userId);
                }
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void CloseConnection() {
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            IsDirect = false;
            IsRecordingStart = false;
            if (mVisualizer != null) {
                mVisualizer.release();
            }
            if (lstViewHolder.size() > 0) {
                lstViewHolder.clear();
            }
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (mp_start != null) {
                for (int i = 0; i <= mp_start.size() - 1; i++) {
                    try {
                        mp_start.get(i).stop();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            mp_start.clear();
            if (mpall != null) {
                try {
                    mpall.stop();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (mediaPlayersAll.size() > 0) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        try {
                            mediaPlayersAll.get(i).stop();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                mediaPlayersAll.clear();
            }

            if (mpInst != null) {
                try {
                    mpInst.stop();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (instrumentList.size() > 0) {
                instrumentList.clear();
            }
            chrono.stop();

            mMyTask.cancel(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}