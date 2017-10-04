package com.instamelody.instamelody;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.instamelody.instamelody.Adapters.InstrumentListAdapter;
import com.instamelody.instamelody.Adapters.MelodyCardListAdapter;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.MelodyMixing;
import com.instamelody.instamelody.Models.MixingData;
import com.instamelody.instamelody.Models.ModelPlayAllMediaPlayer;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.Const;
import com.instamelody.instamelody.utils.NotificationUtils;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.instamelody.instamelody.utils.VolleySingleton;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import static com.instamelody.instamelody.SignInActivity.TWITTER_CONSUMER_SECRET;
import static com.instamelody.instamelody.app.Config.PUSH_NOTIFICATION;
import static com.instamelody.instamelody.utils.Const.ServiceType.ADD_RECORDINGS;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.JOINED_USERS;
import static com.instamelody.instamelody.utils.Const.ServiceType.MELODY;
import static com.instamelody.instamelody.utils.Const.ServiceType.MixingAudio_InstrumentsAudio;
import static com.instamelody.instamelody.utils.Const.ServiceType.TOTAL_COUNT;
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
    private String USERID1 = "userid";
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

    List<String> genreList = new ArrayList<>();
    String IsMicConnected;
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
    public static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    public static boolean isRecording = false;
    public static MediaPlayer mediaPlayer;
    public static String audioFilePath;
    private static String instrumentFilePath;
    public static ArrayList<InstrumentListAdapter.ViewHolder> lstViewHolder = new ArrayList<InstrumentListAdapter.ViewHolder>();
    Uri audioUri;

    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray

    public static String firstName, userNameLogin, profilePicLogin, Name, userName, profilePic, fbName, fbUserName, fbId, melodyPackId, joinRecordingId, instrumentCount, haveJoinid;
    String selectedGenre;
    int statusNormal, statusFb, statusTwitter;
    String melodyName, instrumentName;
    public static Switch switchPublic;
    public static RelativeLayout rlMelodyButton, rlRecordingButton, rlRedoButton, rlListeningButton, rlSetCover, rlPublic;
    public static FrameLayout frameTrans, frameSync, frameProgress;
    public static ImageView ivBackButton, ivHomeButton, ivRecord, ivRecord_stop, ivRecord_play, ivRecord_pause, discover, ivSound, message, ivProfile, ivNewRecordCover;
    CircleImageView profile_image;
    TextView artist_name, noMelodyNote, message_count;
    public static RecyclerView recyclerViewInstruments;
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
    Timer timer;
    MediaPlayer mp;
    ShareDialog shareDialog;
    FacebookSdk.InitializeCallback i1;
    String fetchRecordingUrl;
    public static byte[] bytes, soundBytes;
    String idUpload;
    int InstrumentCountSize = 0;
    public static boolean mShouldContinue = true;
    ArrayList<String> urls;
    MediaPlayer[] media;
    List<MediaPlayer> mps = new ArrayList<MediaPlayer>();
    public static boolean playfrom_studio = false;
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
    private String RECORDING_ID = "rid";
    CallbackManager callbackManager;
    URL ShortUrl;
    String pos = "0";
    BroadcastReceiver mRegistrationBroadcastReceiver;
    int totalCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);
        progressDialog = new ProgressDialog(StudioActivity.this);
//        instruments_count = InstrumentListAdapter.instruments_url;
//        Log.d("abc", "" + instruments_count.size());
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
        frameSync = (FrameLayout) findViewById(R.id.frameSync);
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

        getTotalCount();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    getTotalCount();
                }
            }
        };

        //playAll.setVisibility(View.VISIBLE);
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
        pauseAll.setVisibility(View.GONE);
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
        }
        try {
            melodyPackId = intent.getExtras().getString("clickPosition");
            haveJoinid = intent.getExtras().getString("haveJoinId");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


//        joinRecordingId = intent.getExtras().getString("clickPositionJoin");
        SharedPreferences filterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE);
        joinRecordingId = filterPref.getString("instrumentsPos", null);

        if (joinRecordingId != null && melodyPackId == null) {
            fetchInstrumentsForJoin(JoinActivity.addedBy, JoinActivity.RecId, Integer.parseInt(joinRecordingId));
            noMelodyNote.setVisibility(View.GONE);
            recyclerViewInstruments.setVisibility(View.VISIBLE);
            recyclerViewInstruments.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerViewInstruments.setLayoutManager(layoutManager);
            recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
            recyclerViewInstruments.setAdapter(adapter);
            //frameTrans.setVisibility(View.VISIBLE);
            frameSync.setVisibility(View.VISIBLE);
            if (instrumentList.size() > 0) {
                frameSync.setVisibility(View.VISIBLE);
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

            //      if (!melodyPackId.equals("fromHomeActivity")) {
            if (melodyPackId != null) {
                fetchInstruments(melodyPackId);
                JoinActivity.instrumentList.clear();
                noMelodyNote.setVisibility(View.GONE);
                recyclerViewInstruments.setVisibility(View.VISIBLE);
                recyclerViewInstruments.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewInstruments.setLayoutManager(layoutManager);
                recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
                adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
                recyclerViewInstruments.setAdapter(adapter);
                //frameTrans.setVisibility(View.VISIBLE);
                frameSync.setVisibility(View.VISIBLE);
                if (instrumentList.size() > 0) {
                    frameSync.setVisibility(View.VISIBLE);
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
        }
        String home, homeTostudio;
        SharedPreferences fromHome = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE);
        home = fromHome.getString("click", null);
        SharedPreferences fromHometoST = getApplicationContext().getSharedPreferences("HomeStudio", MODE_PRIVATE);
        homeTostudio = fromHometoST.getString("clickFromSt", null);
        try {
            if (home.equals("from home") || homeTostudio.equals("from home")) {
                SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
                FilterPref.clear();
                FilterPref.apply();
                SharedPreferences.Editor FilterPref1 = getApplicationContext().getSharedPreferences("HomeStudio", MODE_PRIVATE).edit();
                FilterPref1.clear();
                FilterPref1.apply();


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

        SharedPreferences profileEditor = getApplicationContext().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
        SharedPreferences profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE);
        if (profileImageEditor.getString("ProfileImage", null) != null) {
            ivProfile.setVisibility(View.GONE);
            profile_image.setVisibility(View.VISIBLE);
            Picasso.with(StudioActivity.this).load(profileImageEditor.getString("ProfileImage", null)).into(profile_image);
        }
        if (profileEditor.getString("updateId", null) != null) {
            artist_name.setText("@" + profileEditor.getString("updateUserName", null));
        }
        mRecordingThread = new RecordingThread();
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioBuffer = new short[mBufferSize / 2];
        // mDecibelFormat = getResources().getString(R.string.decibel_format);


        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/InstaMelody.mp3";

        rlInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudioActivity.this, ContactsActivity.class);
                intent.putExtra("Previous", "studioActivity");
                startActivity(intent);
            }
        });
        rlMelodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    handler.removeCallbacksAndMessages(null);

                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
                    }
                    if (mp_start.size() > 0) {
                        for (int i = 0; i <= mp_start.size() - 1; i++) {
                            try {
                                mp_start.get(i).stop();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (mpall != null) {
                        mpall.stop();
                        for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                            try {
                                mediaPlayersAll.get(i).stop();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                        mediaPlayersAll.clear();
                    }
                    if (mpInst != null) {
                        try {
                            mpInst.stop();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }

                    if (mRecordingThread != null) {
                        mRecordingThread.stopRunning();
                    }

                    if (isRecording) {
                        ivRecord.setEnabled(false);
                        handler.removeCallbacksAndMessages(null);

                        if (recorder != null) {
                            try {
                                recorder.stop();

                            } catch (RuntimeException ex) {
                                //Ignore
                            }
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


                    Intent intent = new Intent(StudioActivity.this, MelodyActivity.class);
                    startActivity(intent);
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("cover response", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    handler.removeCallbacksAndMessages(null);
                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
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
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                //finish();;
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    handler.removeCallbacksAndMessages(null);
                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
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

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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
                    handler.removeCallbacksAndMessages(null);
                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
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
                    handler.removeCallbacksAndMessages(null);
                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
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
                    handler.removeCallbacksAndMessages(null);
                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
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
                    handler.removeCallbacksAndMessages(null);
                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
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
                if (joinRecordingId == null) {
                    if (userId != null && melodyPackId != null) {
                        openDialog();
                        ivRecord.setVisibility(View.VISIBLE);
                        ivRecord.setEnabled(true);
                    } else if (userId == null) {
                        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(i);
                        Toast.makeText(StudioActivity.this, "SignIn to Save Recording", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    uploadRecordingsMixing("5");
                }

//                else if (melodyPackId == null) {
//                    Toast.makeText(StudioActivity.this, "Add Melody Packs to save recording", Toast.LENGTH_SHORT).show();
//                }

            }
        });


        rlRedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    handler.removeCallbacksAndMessages(null);
                    ivRecord_play.setVisibility(View.INVISIBLE);
                    rlRedoButton.setVisibility(View.INVISIBLE);
                    ivRecord.setVisibility(View.VISIBLE);
                    rlMelodyButton.setVisibility(View.VISIBLE);
                    StudioActivity.playAll.setVisibility(View.GONE);
                    StudioActivity.pauseAll.setVisibility(View.GONE);
                    if (lstViewHolder.size() > 0) {
                        lstViewHolder.clear();
                    }
                    StudioActivity.this.recreate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });

        rlSetCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                /* Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);*/
                    showFileChooser();

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

                LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = mInflater.inflate(R.layout.view_master_vol, null, false);
                final Dialog alertDialog = new Dialog(StudioActivity.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(customView);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
                wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
                alertDialog.show();
            }
        });

        // To get preferred buffer size and sampling rate.
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("Buffer Size & sample rate", "Size :" + size + " & Rate: " + rate);
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

    private static String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return formateMilliSeccond(Long.parseLong(durationStr));
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

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
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

//
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//        builder2.setMessage("Would you like to save as an instrumental or recording");
//        AlertDialog dialog = builder1.show();
//        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
//        messageText.setPadding(20, 20, 20, 20);
//        messageText.setGravity(Gravity.CENTER);
//        messageText.setTextColor(Color.WHITE);
//        messageText.setTextSize(20);
//        dialog.show();


        builder2.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadRecordingsMixing("5");
                // saveRecordings1();

                //  new LongOperation().execute();

                // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                //   dialog.cancel();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_MICROPHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    checkPermissions();
                    //    mRecordingThread.stopRecording();
                }
                break;

            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    checkPermissions();

                }
                break;
        }
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

    public void recordAudio() {
        AudioManager am1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.i("WiredHeadsetOn = ", am1.isWiredHeadsetOn() + "");
        if (am1.isWiredHeadsetOn() == true) {
            Toast.makeText(this, "Headset is connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Headset not connected", Toast.LENGTH_SHORT).show();
        }
        recorder = new MediaRecorder();
        //     recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(audioFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        try {
            recorder.prepare();
            recorder.start();

            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playAurdio() throws IOException {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Uri.parse(audioFilePath));
        duration = mp.getDuration();
        MediaExtractor();
        /*try {
            InputStream inputStream =
                    getContentResolver().openInputStream(Uri.fromFile(new File(audioFilePath)));

            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);

            Toast.makeText(this, "Recordin Finished"+ " " + soundBytes, Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            e.printStackTrace();
        }*/

    }



    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == requestCode && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
            ivNewRecordCover.setBackground(ob);
        }
    }*/

    public void external_audio() {
        //    String filePath = "android.resource://" + getPackageName() + "/" + R.raw.melody;;
        //mPlayer = MediaPlayer.create(this, R.raw.melody);

//        mPlayer = MediaPlayer.create(this, audioUri);
//        mPlayer.start();
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(instrumentFile);
            player.prepare();
            player.start();
        } catch (Exception e) {
            // TODO: handle exception
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
    // DownloadFileFromURL Modified by Abhishek

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            pDialog = new ProgressDialog(StudioActivity.this);
            pDialog.setMessage("Loading melody Packs ...");
            pDialog.setIndeterminate(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... url) {
            int count;
            try {

                URL aurl = new URL(MELODY);

                URLConnection connection = aurl.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(aurl.openStream(), 8192);

                Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

                OutputStream output;
                if (isSDPresent) {
                    // yes SD-card is present
                    //Code commented by Abhishek
                    //output = new FileOutputStream("sdcard/InstaMelody/Downloads/Melodies/" + melodyName + "/" + instrumentName + ".mp3");
                } else {

                    //Code commented by Abhishek
                    //   output = new FileOutputStream(getFilesDir() + "/InstaMelody/Downloads/Melodies/" + melodyName + "/" + instrumentName + ".mp3");
                }

                // Output stream to write file

                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

//                    publishProgress(""+(int)((total*100)/lengthOfFile));

                    // writing data to file
                    //  output.write(data, 0, count);
                }

                // flushing output
                //     output.flush();

                // closing streams
                //     output.close();
                input.close();

            } catch (Exception e) {
                Log.d("Error: ", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task
         **/
        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");
            pDialog.dismiss();
            //frameTrans.setVisibility(View.GONE);
//            frameSync.setVisibility(View.GONE);
            // tvDone.setEnabled(true);
        }
    }

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
                            recyclerViewInstruments.smoothScrollToPosition(instrumentList.size());
                            adapter.notifyDataSetChanged();

                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            ivRecord.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            switchFlag = "0";
                            frameProgress.refreshDrawableState();

//                            SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
//                            FilterPref.remove("instrumentsPos");
//                            FilterPref.apply();

                        } else {
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
                            recyclerViewInstruments.smoothScrollToPosition(instrumentList.size());
                            adapter.notifyDataSetChanged();

                            ivRecord_play.setVisibility(View.INVISIBLE);
                            rlRedoButton.setVisibility(View.INVISIBLE);
                            rlMelodyButton.setVisibility(View.VISIBLE);
                            ivRecord.setVisibility(View.VISIBLE);
                            switchPublic.setChecked(false);
                            frameProgress.refreshDrawableState();
                            switchFlag = "0";
//                            SharedPreferences.Editor FilterPref = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
//                            FilterPref.remove("instrumentsPos");
//                            FilterPref.apply();
//                            Toast.makeText(StudioActivity.this, "Saved as Recording", Toast.LENGTH_SHORT).show();
                        }
                        handler.removeCallbacksAndMessages(null);
                        if (StudioActivity.lstViewHolder.size() > 0) {
                            StudioActivity.lstViewHolder.clear();
                        }
                        if (joinRecordingId != null) {
                            Toast.makeText(StudioActivity.this, "Joined Successfully", Toast.LENGTH_SHORT).show();
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

                        SharedPreferences.Editor recEditor = getApplication().getSharedPreferences("Recording_MelodyDataResponse", MODE_PRIVATE).edit();
                        recEditor.clear();
                        recEditor.commit();

                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Url_recording", MODE_PRIVATE).edit();
                        editor.putString("Recording_url", melodyurl);
                        editor.commit();

                        SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                        editorT.putString("thumbnailUrl", "http://52.89.220.199/api/thumbnail_url.php/?cp=http://52.89.220.199/api/uploads/cover.jpg&rc=http://52.89.220.199/api/uploads/recordings/rec1503669372.mp3");
                        editorT.apply();

                        SharedPreferences switchFbEditor = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE);
                        int switchFbStatus = switchFbEditor.getInt("switch", 0);

                        if (switchFbStatus == 1) {
                            FbShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        } else if (switchFbStatus == 2 && switchFbStatus != 1) {
                            TweetShare();
                            SharedPreferences.Editor switchFbEditor1 = getApplicationContext().getSharedPreferences("SwitchStatus", MODE_PRIVATE).edit();
                            switchFbEditor1.clear();
                            switchFbEditor1.apply();
                        } else if (switchFbStatus == 3) {
                            FbShare();
                            TweetShare();
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
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put(Mixvocalsound, new DataPart("InstaMelody.mp3", soundBytes, "audio/amr"));
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
                    String flag = response1.getString("flag");
                    String flag2 = response1.getString("response");
                    Log.d("Result", flag2);
                    JSONObject r1 = response1.getJSONObject("response");
                    if (r1.has("melody")) {
                        urlRecording = r1.getString("melody");
                        thumbNail = r1.getString("thumbnail");
                    } else {
                        urlRecording = r1.getString("recording");
                        thumbNail = r1.getString("thumbnail");
                    }

                    if (flag.equals("success")) {

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
                        recEditor.commit();

                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Url_recording", MODE_PRIVATE).edit();
                        editor.putString("Recording_url", urlRecording);
                        editor.commit();

                        SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                        editorT.putString("thumbnailUrl", thumbNail);
                        editorT.apply();


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
                                    JSONObject coverR1 = coverResponse.getJSONObject("response");
                                    String coverPic = coverR1.getString("profilepic");

                                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("cover response", MODE_PRIVATE).edit();
                                    editor.putString("coverPicStudio", coverPic);
                                    editor.commit();
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
                                    if (i == 0) {
                                        genresName.add(i, "Genre");
                                    } else {
                                        genresName.add(i, genresArrayList.get(i).getName());
                                    }
                                    genresId.add(i, genresArrayList.get(i).getId());

                                }
                            }
                            /*genresArrayList.remove(0);
                            genresName.remove(0);
                            genresId.remove(0);*/
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

    public void killMediaPlayer_fromInstrument() {
        /*for (MediaPlayer mp : InstrumentListAdapter.mp_start) {
            try {
                mp.stop();
                mp.reset();
                mp.release();
                //     mp=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InstrumentListAdapter.mp_start.clear();*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            if (StudioActivity.mp_start != null) {

                for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                    StudioActivity.mp_start.get(i).stop();

                }
            }
            if (mpall != null) {
                mpall.stop();
                if (mediaPlayersAll.size() > 0) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        mediaPlayersAll.get(i).stop();
                    }
                }
            }
            if (StudioActivity.mpInst != null) {
                StudioActivity.mpInst.stop();
            }
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void closeInput(final View caller) {
        caller.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) caller.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(caller.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }


    public class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(StudioActivity.this);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        public String doInBackground(String... params) {
            saveRecordings1();
            //uploadRecordings(idUpload);
            return null;
        }

        public void onPostExecute(String result) {
            //    Toast.makeText(StudioActivity.this, value1 + "  " + "SAVE", Toast.LENGTH_SHORT).show();
            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
            adapter.notifyDataSetChanged();
            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
            adapter.notifyDataSetChanged();
            frameSync.setVisibility(View.GONE);
            progressDialog.dismiss();
        }

    }

    public class LongOperation1 extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(StudioActivity.this);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        public String doInBackground(String... params) {
            // saveRecordings1();
            uploadRecordings(idUpload);
            return null;
        }

        public void onPostExecute(String result) {
            //    Toast.makeText(StudioActivity.this, value1 + "  " + "SAVE", Toast.LENGTH_SHORT).show();
            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
            adapter.notifyDataSetChanged();
            adapter = new InstrumentListAdapter(instrumentList, getApplicationContext());
            adapter.notifyDataSetChanged();
            frameSync.setVisibility(View.GONE);
            progressDialog.dismiss();
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
        try {
            handler.removeCallbacksAndMessages(null);
            if (StudioActivity.mp_start != null) {

                for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                    if (StudioActivity.mp_start.get(i).isPlaying()) {
                        StudioActivity.mp_start.get(i).stop();
                    }

                }
            }
            if (mpall != null) {

                mpall.stop();
                if (mediaPlayersAll.size() > 0) {
                    for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        if (mediaPlayersAll.get(i).isPlaying()) {
                            mediaPlayersAll.get(i).stop();
                        }
                    }
                }
            }

            if (StudioActivity.mpInst != null) {
                StudioActivity.mpInst.stop();
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
        SharedPreferences editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE);
        String fetchThumbNailUrl = editorT.getString("thumbnailUrl", null);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(StudioActivity.this, "Recording Uploaded", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editorT = getApplicationContext().getSharedPreferences("thumbnail_url", MODE_PRIVATE).edit();
                editorT.clear();
                editorT.apply();
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
//                    .setImageUrl(Uri.parse(cover))
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

        /*Bundle bundle = getIntent().getExtras().getBundle(SHARE_DATA);
        String description = bundle.getString(SHARE_DESCRIPTION);
        String title = bundle.getString(SHARE_TITLE);
        String picture = bundle.getString(SHARE_PICTURE_LINK);
        String link = bundle.getString(SHARE_LINK);*/

        TweetComposer.Builder builder = null;


        builder = new TweetComposer.Builder(this)
//                    .text(title + "" + description)
                .text("Audio Url")
                .url(ShortUrl);
//                .image(Uri.parse(cover));
        builder.show();
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
}




