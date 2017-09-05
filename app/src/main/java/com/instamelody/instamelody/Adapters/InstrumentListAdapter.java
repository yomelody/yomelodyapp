package com.instamelody.instamelody.Adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ByteArrayPool;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instamelody.instamelody.MelodyActivity;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.MixingData;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StudioActivity;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.StudioActivity.bytes;
import static com.instamelody.instamelody.StudioActivity.formateMilliSeccond;
import static com.instamelody.instamelody.StudioActivity.isRecording;

/**
 * Created by Saurabh Singh on 06/04/17
 */

public class InstrumentListAdapter extends RecyclerView.Adapter<InstrumentListAdapter.MyViewHolder> {

    static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    ArrayList<String> vocalsound = new ArrayList<>();
    String audioValue;
    static String audioUrl;
    private static String instrumentFile;
    int length;
    String coverPicStudio;
    int statusNormal, statusFb, statusTwitter;
    String userName, profilePic;
    String fbName, fbUserName, fbId;
    String instrumentName, melodyName;
    int rvLength;
    Context context;

    SoundPool mSoundPool;
    public static ArrayList<String> instruments_url = new ArrayList<String>();
    View mLayout;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    ArrayList instrument_url_count = new ArrayList();
    ArrayList<String> fetch_url_arrayList = new ArrayList<>();
    static int duration1, currentPosition;
    double InstrumentId = 0, Volume = 0, Base = 0, Treble = 0, Pan = 0, Pitch = 0, Reverb = 0, Compression = 0, Delay = 0, Tempo = 0;
    double threshold = 0.125, ratio = 2, attack = 20, release = 250, makeup = 1, knee = 2.82843, mix = 1;
    String InstaURL = null, PositionId;
    List aa;
    int InstrumentListPosition;
    public static List<MediaPlayer> mp_start = new ArrayList<MediaPlayer>();


    final int MY_PERMISSIONS_REQUEST_MICROPHONE = 200;
    final int MY_PERMISSIONS_REQUEST_STORAGE = 201;
    final int SAMPLING_RATE = 44100;
    private int mBufferSize;
    private short[] mAudioBuffer;
    private boolean mShouldContinue = true;
    MediaPlayer pts;
    int duration;
    short MAX_STRENGTH_FOR_BASS = 1000;
    ArrayList<ViewHolder> lstViewHolder = new ArrayList<ViewHolder>();

    public InstrumentListAdapter(ArrayList<MelodyInstruments> instrumentList, Context context) {
        this.instrumentList = instrumentList;
        this.context = context;

    }

    /*For Treble base and volume*/
    private BassBoost bass;
    private Equalizer equalizer;
    short lowerEquilizerBandLevel;
    short UpperEquilizerBandLevel;
    short numberFrequencyBands;
    short eqaulizerBandIndex;
    private TextToSpeech tts;
    /*VoiceEffecter manager;
    VoiceEffecter.Parameters parameters;*/
    ViewHolder viewHolder;

    public static String audioFilePath;
    MediaRecorder recorder = null;
    int InstrumentCountSize = 0;
    public static RecordingThread mRecordingThread;
    private boolean hasLoadButton = true;

    public boolean isHasLoadButton() {
        return hasLoadButton;
    }

    public void setHasLoadButton(boolean hasLoadButton) {
        this.hasLoadButton = hasLoadButton;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfileImage, ivInstrumentCover, ivPlay, ivPause;
        TextView tvInstrumentName, tvUserName, tvInstrumentLength, tvBpmRate, tvSync, tvDoneFxEq, tvFxButton, tvEqButton;

        FrameLayout frameInstrument;
        RelativeLayout rlSeekbarTracer, rlSync;
        ImageView grey_circle, blue_circle;
        RelativeLayout rlFX, rlEQ, eqContent, fxContent;
        private int maxVolume = 0;
        private int curVolume = 0;
        SeekBar melodySlider;
        ProgressDialog progressDialog;
        public MediaPlayer mp;

        CardView card_melody;
        AudioManager audioManager;


        public MyViewHolder(View itemView) {
            super(itemView);

            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            ivInstrumentCover = (ImageView) itemView.findViewById(R.id.ivInstrumentCover);
            tvInstrumentName = (TextView) itemView.findViewById(R.id.tvInstrumentName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvInstrumentLength = (TextView) itemView.findViewById(R.id.tvInstrumentLength);
            tvBpmRate = (TextView) itemView.findViewById(R.id.tvBpmRate);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
            ivPause = (ImageView) itemView.findViewById(R.id.ivPause);
            melodySlider = (SeekBar) itemView.findViewById(R.id.melodySlider);
            rlSeekbarTracer = (RelativeLayout) itemView.findViewById(R.id.rlSeekbarTracer);
            card_melody = (CardView) itemView.findViewById(R.id.card_melody);
            tvSync = (TextView) itemView.findViewById(R.id.tvSync);
            rlSync = (RelativeLayout) itemView.findViewById(R.id.rlSync);
            blue_circle = (ImageView) itemView.findViewById(R.id.blue_circle);
            grey_circle = (ImageView) itemView.findViewById(R.id.grey_circle);
            rlFX = (RelativeLayout) itemView.findViewById(R.id.rlFX);
            rlEQ = (RelativeLayout) itemView.findViewById(R.id.rlEQ);
            /*fxContent = (RelativeLayout) itemView.findViewById(R.id.fxContent);
            eqContent = (RelativeLayout) itemView.findViewById(R.id.eqContent);*/
            fxContent = (RelativeLayout) itemView.findViewById(R.id.fxContent);
            eqContent = (RelativeLayout) itemView.findViewById(R.id.eqContent);
            tvDoneFxEq = (TextView) itemView.findViewById(R.id.tvDoneFxEq);
            tvFxButton = (TextView) itemView.findViewById(R.id.tvFxButton);
            tvEqButton = (TextView) itemView.findViewById(R.id.tvEqButton);
            tvInstrumentLength = (TextView) itemView.findViewById(R.id.tvInstrumentLength);
            tvInstrumentName = (TextView) itemView.findViewById(R.id.tvInstrumentName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);

            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            ivPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivPlay.setVisibility(v.VISIBLE);
                    ivPause.setVisibility(v.GONE);
                    mp.pause();
                    length = mp.getCurrentPosition();
                    melodySlider.setProgress(0);
                }
            });

            audioFilePath =
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/InstaMelody.mp3";
            mRecordingThread = new RecordingThread();
            mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            mAudioBuffer = new short[mBufferSize / 2];

            SharedPreferences coverSharePref = getApplicationContext().getSharedPreferences("cover response", MODE_PRIVATE);
            coverPicStudio = coverSharePref.getString("coverPicStudio", null);


            SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);

            userName = twitterPref.getString("userName", null);
            profilePic = twitterPref.getString("ProfilePic", null);
            statusTwitter = twitterPref.getInt("status", 0);


            SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
            fbName = fbPref.getString("FbName", null);
            fbUserName = fbPref.getString("userName", null);
            fbId = fbPref.getString("fbId", null);
            statusFb = fbPref.getInt("status", 0);


            /*melodySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int mCurrentPosition = currentPosition / 1000;
                    int mDuration = duration1 / 1000;
                    UtilsRecording utilRecording = new UtilsRecording();
                    int progress1 = utilRecording.getProgressPercentage(mCurrentPosition, mDuration);

                    if (mp != null && fromUser) {
                        int playPositionInMilliseconds = duration1 / 100 * melodySlider.getProgress();
                        mp.seekTo(playPositionInMilliseconds);
//                        seekBar.setProgress(progress);
                    } else {
                        // the event was fired from code and you shouldn't call player.seekTo()
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });*/
        }


        private void primarySeekBarProgressUpdater() {
            Handler mHandler1 = new Handler();
            try {

                melodySlider.setProgress((int) (((float) mp.getCurrentPosition() / mp.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (mp != null) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            primarySeekBarProgressUpdater();
                        }
                    };
                    mHandler1.postDelayed(notification, 100);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }


        }

        private void FrameprimarySeekBarProgressUpdater() {
            Handler mHandler1 = new Handler();
            try {
                StudioActivity.FramemelodySlider.setProgress((int) (((float) StudioActivity.mpInst.getCurrentPosition() / StudioActivity.mpInst.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (StudioActivity.mpInst != null) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            FrameprimarySeekBarProgressUpdater();
                        }
                    };
                    mHandler1.postDelayed(notification, 100);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }


        }

        private void FrameprimaryUpdater() {
            Handler mHandler1 = new Handler();
            try {
                StudioActivity.FramemelodySlider.setProgress((int) (((float) mp.getCurrentPosition() / mp.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (mp != null) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            FrameprimaryUpdater();
                        }
                    };
                    mHandler1.postDelayed(notification, 100);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
       /*private void FrameprimarySeekBarProgressUpdaterRecording() {
            Handler mHandler1 = new Handler();
            try {
                //holder.itemView.findViewById(R.id.melodySlider);
                melodySlider.setProgress((int) (((float) pts.getCurrentPosition() / pts.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (pts!=null) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            FrameprimarySeekBarProgressUpdaterRecording();
                        }
                    };
                    mHandler1.postDelayed(notification, 100);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }*/
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_added, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    static class ViewHolder {
        SeekBar seekBar;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        viewHolder = new ViewHolder();
        viewHolder.seekBar = (SeekBar) holder.melodySlider.findViewById(R.id.melodySlider);
        lstViewHolder.add(viewHolder);
        String aafs = FirebaseInstanceId.getInstance().getToken();
        final MelodyInstruments instruments = instrumentList.get(listPosition);
        String abc = instrumentList.get(listPosition).getInstrumentFile();

        // holder.melodySlider.setProgress(0);
        if (coverPicStudio != null) {
            Picasso.with(holder.ivInstrumentCover.getContext()).load(coverPicStudio).into(holder.ivInstrumentCover);
        } else if (instruments.getInstrumentCover().charAt(0) == '#') {
            String color = instruments.getInstrumentCover();
            holder.ivInstrumentCover.setBackgroundColor(Color.parseColor(color));
        } else {
            Picasso.with(holder.ivInstrumentCover.getContext()).load(instruments.getInstrumentCover()).into(holder.ivInstrumentCover);
        }
        if (profilePic != null) {
            Picasso.with(holder.userProfileImage.getContext()).load(profilePic).into(holder.userProfileImage);
        } else if (fbId != null) {
            Picasso.with(holder.userProfileImage.getContext()).load("https://graph.facebook.com/" + fbId + "/picture").into(holder.userProfileImage);
        } else
            Picasso.with(holder.userProfileImage.getContext()).load(instruments.getUserProfilePic()).into(holder.userProfileImage);
        holder.tvBpmRate.setText(instruments.getInstrumentBpm());
        if (userName != null) {
            holder.tvUserName.setText("@" + userName);
        } else if (fbName != null) {
            holder.tvUserName.setText("@" + fbName);
        } else
            holder.tvUserName.setText(instruments.getUserName());

        holder.tvInstrumentName.setText(instruments.getInstrumentName());

        holder.tvInstrumentLength.setText(instruments.getInstrumentLength());
        instrumentFile = instruments.getInstrumentFile();
        instrument_url_count.add(instrumentFile);
        Log.d("Instruments size", "" + instrumentFile);


        audioValue = instruments.getAudioType();
        holder.rlFX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String InstName = "", UserName = "", InstLength = "", BPM = "", ivInstrumentCover = "", ivUserProfileImage = "";
                InstrumentListPosition = listPosition;
                StudioActivity.FramesivPause.setVisibility(v.GONE);
                StudioActivity.FramesivPlay.setVisibility(v.VISIBLE);
                StudioActivity.frameInstrument.setVisibility(View.VISIBLE);
                InstName = instrumentList.get(listPosition).getInstrumentName();
                UserName = instrumentList.get(listPosition).getUserName();
                InstLength = instrumentList.get(listPosition).getInstrumentLength();
                BPM = "BPM: " + instrumentList.get(listPosition).getInstrumentBpm().replaceAll("BPM: ", "");
                ivInstrumentCover = instrumentList.get(listPosition).getInstrumentCover();
                ivUserProfileImage = instrumentList.get(listPosition).getUserProfilePic();
                InstaURL = instrumentList.get(listPosition).getInstrumentFile();
                StudioActivity.tvInstrumentName.setText(InstName);
                StudioActivity.tvUserName.setText(UserName);
                StudioActivity.tvInstrumentLength.setText(InstLength);
                StudioActivity.tvBpmRate.setText(BPM);
                new InstrumentCover().execute(ivInstrumentCover);
                new UserProfileCover().execute(ivUserProfileImage);
                //LoadInstrumentData(listPosition);
                if (StudioActivity.fxContent.getVisibility() == View.VISIBLE) {
                    StudioActivity.eqContent.setVisibility(View.GONE);
                } else {
                    StudioActivity.fxContent.setVisibility(View.VISIBLE);
                    if (holder.mp != null) {
                        StudioActivity.FramesivPause.setVisibility(v.VISIBLE);
                        StudioActivity.FramesivPlay.setVisibility(v.GONE);
                        StudioActivity.FramemelodySlider.setProgress(0);
                        holder.FrameprimaryUpdater();
                    }
                }


            }
        });

        holder.rlEQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String InstName = "", UserName = "", InstLength = "", BPM = "", ivInstrumentCover = "", ivUserProfileImage = "";
                InstrumentListPosition = listPosition;
                StudioActivity.FramesivPause.setVisibility(v.GONE);
                StudioActivity.FramesivPlay.setVisibility(v.VISIBLE);
                StudioActivity.frameInstrument.setVisibility(View.VISIBLE);
                InstName = instrumentList.get(listPosition).getInstrumentName();
                UserName = instrumentList.get(listPosition).getUserName();
                InstLength = instrumentList.get(listPosition).getInstrumentLength();
                BPM = "BPM: " + instrumentList.get(listPosition).getInstrumentBpm().replaceAll("BPM: ", "");
                ivInstrumentCover = instrumentList.get(listPosition).getInstrumentCover();
                ivUserProfileImage = instrumentList.get(listPosition).getUserProfilePic();
                InstaURL = instrumentList.get(listPosition).getInstrumentFile();
                StudioActivity.tvInstrumentName.setText(InstName);
                StudioActivity.tvUserName.setText(UserName);
                StudioActivity.tvInstrumentLength.setText(InstLength);
                StudioActivity.tvBpmRate.setText(BPM);
                new InstrumentCover().execute(ivInstrumentCover);
                new UserProfileCover().execute(ivUserProfileImage);
                //LoadInstrumentData(listPosition);
                if (StudioActivity.fxContent.getVisibility() == View.VISIBLE) {
                    StudioActivity.fxContent.setVisibility(View.GONE);
                } else {
                    StudioActivity.eqContent.setVisibility(View.VISIBLE);
                    if (holder.mp != null) {
                        StudioActivity.FramesivPause.setVisibility(v.VISIBLE);
                        StudioActivity.FramesivPlay.setVisibility(v.GONE);
                        StudioActivity.FramemelodySlider.setProgress(0);
                        holder.FrameprimaryUpdater();
                    }
                }


            }
        });
        StudioActivity.RltvFxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StudioActivity.fxContent.getVisibility() == View.VISIBLE) {
                    StudioActivity.fxContent.setVisibility(View.GONE);
                    StudioActivity.frameInstrument.setVisibility(View.GONE);
                    StudioActivity.eqContent.setVisibility(View.GONE);
                } else {
                    StudioActivity.frameInstrument.setVisibility(View.VISIBLE);
                    StudioActivity.eqContent.setVisibility(View.GONE);
                    StudioActivity.fxContent.setVisibility(View.VISIBLE);
                    if (StudioActivity.mpInst != null) {
                        StudioActivity.mpInst.pause();
                        StudioActivity.FramemelodySlider.setProgress(0);
                    }
                    if (holder.mp != null) {
                        holder.mp.pause();
                    }
                }
            }
        });
        StudioActivity.RltvEqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StudioActivity.eqContent.getVisibility() == View.VISIBLE) {
                    StudioActivity.eqContent.setVisibility(View.GONE);
                    StudioActivity.frameInstrument.setVisibility(View.GONE);
                    StudioActivity.fxContent.setVisibility(View.GONE);
                } else {
                    StudioActivity.frameInstrument.setVisibility(View.VISIBLE);
                    StudioActivity.fxContent.setVisibility(View.GONE);
                    StudioActivity.eqContent.setVisibility(View.VISIBLE);
                    if (StudioActivity.mpInst != null) {
                        StudioActivity.mpInst.pause();
                        StudioActivity.FramemelodySlider.setProgress(0);
                    }
                    if (holder.mp != null) {
                        holder.mp.pause();
                    }
                }
            }
        });

        StudioActivity.tvDoneFxEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudioActivity.fxContent.setVisibility(View.GONE);
                StudioActivity.frameInstrument.setVisibility(View.GONE);
                StudioActivity.eqContent.setVisibility(View.GONE);
                if (StudioActivity.mpInst != null) {
                    StudioActivity.mpInst.pause();
                    StudioActivity.FramemelodySlider.setProgress(0);
                }


            }
        });

        StudioActivity.volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch stopped");
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Volume = CalcuateEQProgressValue(progress);
                Toast.makeText(getApplicationContext(), String.valueOf(Volume), Toast.LENGTH_SHORT).show();
                //Volume = progress;

                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();
                holder.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);


            }

        });
        StudioActivity.sbBase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch stopped");
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Base = CalcuateEQProgressValue(progress);
                Toast.makeText(getApplicationContext(), String.valueOf(Base), Toast.LENGTH_SHORT).show();
                //Base = progress;
                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();
                //BassBoost bassBoost = new BassBoost(0, mpall.getAudioSessionId());
                //setBassBoost(bassBoost,progress);

                //BassBoost bassBoost = new BassBoost(0, mpall.getAudioSessionId());
                bass.setEnabled(true);
                BassBoost.Settings bassBoostSettingTemp = bass.getProperties();
                BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
                bassBoostSetting.strength = MAX_STRENGTH_FOR_BASS; // 1000
                bass.setProperties(bassBoostSetting);

                bass.setStrength((short) progress);
            }
        });
        StudioActivity.sbTreble.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Treble = CalcuateEQProgressValue(progress);
                Toast.makeText(getApplicationContext(), String.valueOf(Treble), Toast.LENGTH_SHORT).show();
                //Treble = progress;
                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();

                equalizer.setEnabled(true);
                equalizer.setBandLevel(eqaulizerBandIndex, (short) (progress + lowerEquilizerBandLevel));


            }
        });

        StudioActivity.sbPan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Pan = progress;
                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();
            }
        });
        StudioActivity.sbCompression.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Compression = progress;
                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();
            }
        });
        StudioActivity.sbDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Delay = progress;
                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();
            }
        });
        StudioActivity.sbReverb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Reverb = progress;
                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();
            }
        });

        StudioActivity.sbTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //Log.i(tag,"touch started");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Tempo = progress;
                if (StudioActivity.list.size() == 0) {
                    StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                } else {
                    StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
                StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                aa = StudioActivity.melodyMixing.getVocalsound();
            }
        });


        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.ivPlay.setVisibility(v.GONE);
                holder.ivPause.setVisibility(v.VISIBLE);
                instruments_url.add(instrumentFile);
                instrumentFile = instruments.getInstrumentFile();

                holder.progressDialog = new ProgressDialog(v.getContext());
                holder.progressDialog.setMessage("Loading...");
                holder.progressDialog.show();
                holder.mp = new MediaPlayer();

                holder.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    holder.mp.setDataSource(instrumentFile);
                    holder.mp.prepareAsync();
                    mp_start.add(holder.mp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (MediaPlayer instrument_media : mp_start) {
                    instrument_media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            holder.progressDialog.dismiss();
                            mp.start();
                            try {
                                holder.primarySeekBarProgressUpdater();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

                holder.mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        holder.progressDialog.dismiss();
                        return false;
                    }
                });
                holder.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        duration1 = holder.mp.getDuration();
                        currentPosition = holder.mp.getCurrentPosition();
                        holder.progressDialog.dismiss();
                    }
                });

                instrumentName = instruments.getInstrumentName();

            }
        });


        holder.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivPlay.setVisibility(v.VISIBLE);
                holder.ivPause.setVisibility(v.GONE);
                holder.mp.pause();
                length = holder.mp.getCurrentPosition();
                //holder.melodySlider.setProgress(0);
            }
        });

        StudioActivity.FramesivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StudioActivity.FramesivPlay.setVisibility(v.GONE);
                StudioActivity.FramesivPause.setVisibility(v.VISIBLE);
                instrumentFile = instrumentList.get(InstrumentListPosition).getInstrumentFile();
                instruments_url.add(instrumentFile);
                //instrumentFile = instruments.getInstrumentFile();

                holder.progressDialog = new ProgressDialog(v.getContext());
                holder.progressDialog.setMessage("Loading...");
                holder.progressDialog.show();
                if (StudioActivity.mpInst != null) {
                    StudioActivity.mpInst.stop();
                    StudioActivity.mpInst.release();
                }
                StudioActivity.mpInst = new MediaPlayer();
                StudioActivity.mpInst.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    StudioActivity.mpInst.setDataSource(instrumentFile);
                    StudioActivity.mpInst.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StudioActivity.mpInst.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        holder.progressDialog.dismiss();
                        mp.start();
                        bass = new BassBoost(0, mp.getAudioSessionId());
                        int bassval = bass.getProperties().strength;

                        equalizer = new Equalizer(0, mp.getAudioSessionId());
                        equalizer.setEnabled(true);
                      /*  equalizer.getNumberOfBands(); //it tells you the number of equalizer in device.
                        equalizer.getNumberOfPresets();//like Normal Classic,Dance Flat,Folk Heavy Metal,Hip Hop,Jazz, Pop, Rock*/

                        numberFrequencyBands = equalizer.getNumberOfBands();
                         /*Get the level range to used in settings the band level*/
                        /*Get lower limit of the range in millibels*/
                        lowerEquilizerBandLevel = equalizer.getBandLevelRange()[0];
                        /*Get the upper level of the range in millibels*/
                        UpperEquilizerBandLevel = equalizer.getBandLevelRange()[1];


                        StudioActivity.sbTreble.setMax(UpperEquilizerBandLevel - lowerEquilizerBandLevel);
                        for (short i = 0; i < numberFrequencyBands; i++) {
                            eqaulizerBandIndex = i;
                            StudioActivity.sbTreble.setProgress(equalizer.getBandLevel(eqaulizerBandIndex));
                        }


                        holder.FrameprimarySeekBarProgressUpdater();

                    }
                });
                StudioActivity.mpInst.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        holder.progressDialog.dismiss();
                        return false;
                    }
                });
                StudioActivity.mpInst.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        duration1 = StudioActivity.mpInst.getDuration();
                        currentPosition = StudioActivity.mpInst.getCurrentPosition();
                        holder.progressDialog.dismiss();
                    }
                });

                instrumentName = instruments.getInstrumentName();

            }
        });


        StudioActivity.FramesivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudioActivity.FramesivPlay.setVisibility(v.VISIBLE);
                StudioActivity.FramesivPause.setVisibility(v.GONE);

                if (StudioActivity.mpInst != null) {
                    StudioActivity.mpInst.pause();
                    length = StudioActivity.mpInst.getCurrentPosition();
                    StudioActivity.FramemelodySlider.setProgress(0);
                }
                if (holder.mp != null) {
                    holder.mp.stop();

                }

            }
        });


        StudioActivity.ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    new PrepareInstruments().execute();
                    /*for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                        //MediaPlayer pts;
                        pts = mediaPlayersAll.get(i);
                        pts.start();

                        //holder.melodySlider.findViewById(R.id.melodySlider);

                    }*/


                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }


        });
        StudioActivity.ivRecord_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StudioActivity.ivRecord_stop.setVisibility(View.GONE);
                StudioActivity.rlRecordingButton.setVisibility(View.GONE);
                StudioActivity.ivRecord_play.setVisibility(View.VISIBLE);
                StudioActivity.rlRedoButton.setVisibility(View.VISIBLE);
                StudioActivity.tvPublic.setVisibility(View.VISIBLE);
                StudioActivity.switchPublic.setVisibility(View.VISIBLE);

                if (mRecordingThread != null) {
                    mRecordingThread.stopRunning();
                }

                StudioActivity.handler.removeCallbacksAndMessages(null);
                if (isRecording) {
                    StudioActivity.ivRecord.setEnabled(false);

                    if (recorder != null) {
                        try {
                            recorder.stop();
                            recorder.release();
                            recorder = null;
                            isRecording = false;

                        } catch (RuntimeException ex) {
                            //Ignore
                        }
                    }

                } else {
                    try {
                        StudioActivity.rlRecordingButton.setEnabled(true);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    if (StudioActivity.mpall != null) {
                        StudioActivity.mpall.stop();
                        for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                            StudioActivity.mediaPlayersAll.get(i).stop();

                        }
                    }
                    StudioActivity.tvDone.setEnabled(true);
                    StudioActivity.chrono.stop();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                try {
                    InputStream inputStream =
                            getApplicationContext().getContentResolver().openInputStream(Uri.fromFile(new File(audioFilePath)));
                    StudioActivity.soundBytes = new byte[inputStream.available()];
                    StudioActivity.soundBytes = toByteArray(inputStream);
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    StudioActivity.recordingDuration = getDuration(new File(audioFilePath));
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                StudioActivity.stop_rec_time = SystemClock.elapsedRealtime() - StudioActivity.chrono.getBase();
                StudioActivity.time_stop = formateMilliSeccond(StudioActivity.stop_rec_time);


            }

        });

        StudioActivity.ivRecord_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(StudioActivity.this, "play", Toast.LENGTH_SHORT).show();
                StudioActivity.ivRecord_play.setVisibility(View.GONE);
                StudioActivity.rlRedoButton.setVisibility(View.GONE);
                StudioActivity.ivRecord_pause.setVisibility(View.VISIBLE);
                StudioActivity.rlListeningButton.setVisibility(View.VISIBLE);
                mShouldContinue = true;
                try {
                    playAurdio();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mShouldContinue == true) {
                    mRecordingThread = new RecordingThread();
                    mRecordingThread.start();
                } else {
                    mRecordingThread = new RecordingThread();
                    mRecordingThread.start();
                }

                //   onResume();
                // mixFiles();

                StudioActivity.chrono.setBase(SystemClock.elapsedRealtime());
                StudioActivity.chrono.start();

                StudioActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        StudioActivity.mediaPlayer.stop();
                        StudioActivity.chrono.stop();
                        StudioActivity.ivRecord_pause.setVisibility(View.INVISIBLE);
                        StudioActivity.rlListeningButton.setVisibility(View.INVISIBLE);
                        StudioActivity.ivRecord_play.setVisibility(View.VISIBLE);
                        StudioActivity.rlRedoButton.setVisibility(View.VISIBLE);
                        if (mRecordingThread != null) {
                            mRecordingThread.stopRunning();
                            mRecordingThread = null;
                        }
                    }
                });
            }
        });


        StudioActivity.ivRecord_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StudioActivity.this, "Pause", Toast.LENGTH_SHORT).show();
                StudioActivity.ivRecord_pause.setVisibility(View.INVISIBLE);
                StudioActivity.rlListeningButton.setVisibility(View.INVISIBLE);
                StudioActivity.ivRecord_play.setVisibility(View.VISIBLE);
                StudioActivity.rlRedoButton.setVisibility(View.VISIBLE);
                if (mRecordingThread != null) {
                    mRecordingThread.stopRunning();
                    mRecordingThread = null;
                }
                //          onPause();
                try {


                    if (StudioActivity.mpall != null) {
                        StudioActivity.mpall.stop();
                        for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                            StudioActivity.mediaPlayersAll.get(i).stop();

                        }
                    }
                    StudioActivity.chrono.stop();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }
        });

        StudioActivity.rlMelodyButton.setOnClickListener(new View.OnClickListener() {
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
                } else {
                    try {

                        StudioActivity.rlRecordingButton.setEnabled(true);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
                StudioActivity.tvDone.setEnabled(true);
                StudioActivity.chrono.stop();


                Intent intent = new Intent(getApplicationContext(), MelodyActivity.class);
                getApplicationContext().startActivity(intent);
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("cover response", MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
            }
        });

        Intent i = new Intent("fetchingInstruments");
        i.putStringArrayListExtra("instruments", instrument_url_count);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);

    }


    @Override
    public int getItemCount() {
        //  rvLength = instrumentList.size();
        return instrumentList.size();
    }

    public void onPause() {
        Bitmap mIcon11 = null;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == instrumentList.size()) ? R.layout.layout_button_sync : R.layout.card_melody_added;
    }

    private class InstrumentCover extends AsyncTask<String, Void, Bitmap> {
        //ImageView bmImage;

        public InstrumentCover() {
            //StudioActivity.ivInstrumentCover = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            StudioActivity.ivInstrumentCover.setImageBitmap(result);
        }
    }

    private class UserProfileCover extends AsyncTask<String, Void, Bitmap> {
        //ImageView bmImage;

        public UserProfileCover() {
            //StudioActivity.ivInstrumentCover = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            StudioActivity.userProfileImage.setImageBitmap(result);
        }
    }

    private static String getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return formateMilliSeccond(Long.parseLong(durationStr));
    }

    private void LoadInstrumentData(int listPosition) {
        try {

            PositionId = String.valueOf(listPosition);
            final MelodyInstruments instruments = instrumentList.get(listPosition);
            if (StudioActivity.list.size() == 0) {
                StudioActivity.list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
            } else {
                StudioActivity.list.indexOf(listPosition);
                boolean IsAvailable = false;
                for (int i = 0; i <= StudioActivity.list.size(); i++) {
                    int pos = Integer.parseInt(StudioActivity.list.get(i).positionId);
                    if (pos == listPosition) {
                        IsAvailable = true;
                    }
                }
                if (IsAvailable) {
                    StudioActivity.list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PrepareInstruments extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            try {
                try {
                    StudioActivity.frameProgress.setVisibility(View.VISIBLE);

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        protected Bitmap doInBackground(String... urls) {

            try {
                if (InstrumentCountSize == 0) {
                    InstrumentCountSize = MelodyInstruments.getInstrumentCount();

                    for (int i = 0; i < InstrumentCountSize; i++) {
                        Log.d("Instrument url----------------:", "" + instrumentList.get(i).getInstrumentFile());
                        StudioActivity.mpall = new MediaPlayer();
                        StudioActivity.mpall.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        StudioActivity.mpall.setDataSource(instrumentList.get(i).getInstrumentFile());
                        StudioActivity.mpall.prepare();
                        StudioActivity.mediaPlayersAll.add(StudioActivity.mpall);

                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                StudioActivity.ivRecord.setVisibility(View.GONE);
                StudioActivity.rlMelodyButton.setVisibility(View.GONE);
                StudioActivity.ivRecord_stop.setVisibility(View.VISIBLE);
                StudioActivity.rlRecordingButton.setVisibility(View.VISIBLE);
                StudioActivity.waveform_view.setVisibility(View.VISIBLE);
                StudioActivity.frameProgress.setVisibility(View.GONE);

                for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                    final MediaPlayer pts;
                    pts = StudioActivity.mediaPlayersAll.get(i);
                    pts.start();
                    final SeekBar seekBar = lstViewHolder.get(i).seekBar;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            int currentPosition = pts.getCurrentPosition() / 1000;
                            int duration = pts.getDuration() / 1000;
                            int progress = (currentPosition * 100) / duration;

                            //seekBar.setProgress((int) (((float) pts.getCurrentPosition() / pts.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                            seekBar.setProgress(progress);

                            StudioActivity.handler.postDelayed(this, 3000);
                        }
                    };

                    StudioActivity.handler.postDelayed(runnable, 3000);
                }
                recordAudio();
                if (!mRecordingThread.isAlive()) {
                    try {
                        mRecordingThread.start();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else {
                    mRecordingThread.stopRunning();
                }

                StudioActivity.chrono.setBase(SystemClock.elapsedRealtime());
                StudioActivity.chrono.start();

            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void recordAudio() {
        AudioManager am1 = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        Log.i("WiredHeadsetOn = ", am1.isWiredHeadsetOn() + "");
        if (am1.isWiredHeadsetOn() == true) {
            Toast.makeText(getApplicationContext(), "Headset is connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Headset not connected", Toast.LENGTH_SHORT).show();
        }


        try {

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(audioFilePath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            recorder.prepare();
            recorder.start();

            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                StudioActivity.waveform_view.updateAudioData(mAudioBuffer);
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

        private synchronized void stopRunning() {
            mShouldContinue = false;
        }
    }

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

    private void updateDecibelLevel() {

        double sum = 0;

        for (short rawSample : mAudioBuffer) {
            double sample = rawSample / 32768.0;
            sum += sample * sample;
        }

        double rms = Math.sqrt(sum / mAudioBuffer.length);
        final double db = 20 * Math.log10(rms);

        // Update the text view on the main thread.
        StudioActivity.mDecibelView.post(new Runnable() {
            @Override
            public void run() {
                // mDecibelView.setText(String.format(mDecibelFormat, db));
            }
        });
    }

    public int CalcuateEQProgressValue(int value) {
        int PrgVal = 0;
        try {

            if (value > 20) {
                PrgVal = value - 20;
            } else {
                PrgVal = value - 20;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return PrgVal;
    }


    public void playAurdio() throws IOException {
        try {
            StudioActivity.mediaPlayer = new MediaPlayer();
            StudioActivity.mediaPlayer.setDataSource(audioFilePath);
            StudioActivity.mediaPlayer.prepare();
            StudioActivity.mediaPlayer.start();
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Uri.parse(audioFilePath));
            duration = mp.getDuration();
            MediaExtractor();
        } catch (Throwable e) {
            e.printStackTrace();
        }

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

    public static void setBassBoost(BassBoost bassBoost, int percent) {
        try {
            bassBoost.setStrength((short) ((short) 1000 / 100 * percent));
            bassBoost.setEnabled(true);
        } catch (Exception e) {

        }
    }

    public static void setBassBoostOff(BassBoost bassBoost) {
        bassBoost.setEnabled(false);
    }

    private int getMinSupportedSampleRate() {
    /*
     * Valid Audio Sample rates
     *
     * @see <a
     * href="http://en.wikipedia.org/wiki/Sampling_%28signal_processing%29"
     * >Wikipedia</a>
     */
        final int validSampleRates[] = new int[]{8000, 11025, 16000, 22050,
                32000, 37800, 44056, 44100, 47250, 4800, 50000, 50400, 88200,
                96000, 176400, 192000, 352800, 2822400, 5644800};
    /*
     * Selecting default audio input source for recording since
     * AudioFormat.CHANNEL_CONFIGURATION_DEFAULT is deprecated and selecting
     * default encoding format.
     */
        for (int i = 0; i < validSampleRates.length; i++) {
            int MinSampleResult = AudioRecord.getMinBufferSize(validSampleRates[i],
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_DEFAULT);

            if (MinSampleResult != AudioRecord.ERROR
                    && MinSampleResult != AudioRecord.ERROR_BAD_VALUE && MinSampleResult > 0) {
                // return the mininum supported audio sample rate
                return validSampleRates[i];
            }
        }
        // If none of the sample rates are supported return -1 handle it in
        // calling method
        return -1;
    }


}
