package com.yomelody.Adapters;


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
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ByteArrayPool;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yomelody.Models.MelodyInstruments;
import com.yomelody.Models.MixingData;
import com.yomelody.Models.ModelPlayAllMediaPlayer;
import com.yomelody.R;
import com.yomelody.StudioActivity;
import com.yomelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.yomelody.StudioActivity.bytes;
import static com.yomelody.StudioActivity.lstViewHolder;
import static com.yomelody.StudioActivity.mediaPlayersAll;
import static com.yomelody.StudioActivity.rlBase;

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
    boolean IsMute = false, IsSolo = false;
    int Compdurations = 0, tmpduration = 0, MaxMpSessionID;
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

    int InstrumentListCount = 0;

    final int MY_PERMISSIONS_REQUEST_MICROPHONE = 200;
    final int MY_PERMISSIONS_REQUEST_STORAGE = 201;
    final int SAMPLING_RATE = 44100;
    private int mBufferSize;
    private short[] mAudioBuffer;
    private boolean mShouldContinue = true;
    int duration;
    short MAX_STRENGTH_FOR_BASS = 1000;
    // ArrayList<ViewHolder> lstViewHolder = new ArrayList<ViewHolder>();
    MediaPlayer Mall;

    public InstrumentListAdapter(ArrayList<MelodyInstruments> instrumentList, Context context) {
        this.instrumentList = instrumentList;
        InstrumentListCount = instrumentList.size();
        this.context = context;

    }

    private Context context;

    public InstrumentListAdapter(Context context) {
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
    private boolean hasLoadButton = true;

    public boolean isHasLoadButton() {
        return hasLoadButton;
    }

    public void setHasLoadButton(boolean hasLoadButton) {
        this.hasLoadButton = hasLoadButton;
        notifyDataSetChanged();
    }

    boolean IsPlaySoloAll = false, IsPlayMuteAll = false, IsRepeateRec = false;
    boolean TempIsPlaySoloAll = false, TempIsPlayMuteAll = false, AllSolo = false;
    ArrayList ArRepeate = new ArrayList();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfileImage, ivInstrumentCover, ivPlay, ivPause, ivRepeatMelody;
        TextView tvInstrumentName, tvUserName, tvInstrumentLength, tvBpmRate, tvSync, tvDoneFxEq, tvFxButton, tvEqButton, tvMButton, tvSButton;

        RelativeLayout rlFX, rlEQ, fxContent, eqContent;
        SeekBar volumeSeekbar, sbTreble, sbBase, sbPan, sbPitch, sbReverb, sbCompression, sbDelay, sbTempo;

        FrameLayout frameInstrument;
        RelativeLayout rlSeekbarTracer, rlSync, rlrepeat, rlivDeleteMelody, rlMute, rlSolo;
        ImageView grey_circle, blue_circle;
        private int maxVolume = 0;
        private int curVolume = 0;
        SeekBar melodySlider;
        ProgressDialog progressDialog;
        public MediaPlayer mp;
        LinearLayout deleteLl;
        TextView cancelTv, deleteTv;

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
            fxContent = (RelativeLayout) itemView.findViewById(R.id.fxContent);
            eqContent = (RelativeLayout) itemView.findViewById(R.id.eqContent);
            tvDoneFxEq = (TextView) itemView.findViewById(R.id.tvDoneFxEq);
            tvFxButton = (TextView) itemView.findViewById(R.id.tvFxButton);
            tvEqButton = (TextView) itemView.findViewById(R.id.tvEqButton);
            tvInstrumentLength = (TextView) itemView.findViewById(R.id.tvInstrumentLength);
            tvInstrumentName = (TextView) itemView.findViewById(R.id.tvInstrumentName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            ivRepeatMelody = (ImageView) itemView.findViewById(R.id.ivRepeatMelody);
            rlrepeat = (RelativeLayout) itemView.findViewById(R.id.rlrepeat);
            rlMute = (RelativeLayout) itemView.findViewById(R.id.rlMute);
            rlSolo = (RelativeLayout) itemView.findViewById(R.id.rlSolo);
            tvMButton = (TextView) itemView.findViewById(R.id.tvMButton);
            tvSButton = (TextView) itemView.findViewById(R.id.tvSButton);
            rlivDeleteMelody = (RelativeLayout) itemView.findViewById(R.id.rlivDeleteMelody);
            deleteLl = (LinearLayout) itemView.findViewById(R.id.deleteLl);
            cancelTv = (TextView) itemView.findViewById(R.id.cancelTv);
            deleteTv = (TextView) itemView.findViewById(R.id.deleteTv);

            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);


            volumeSeekbar = (SeekBar) itemView.findViewById(R.id.sbVolume);
            sbTreble = (SeekBar) itemView.findViewById(R.id.sbTreble);
            sbBase = (SeekBar) itemView.findViewById(R.id.sbBase);
            sbReverb = (SeekBar) itemView.findViewById(R.id.sbReverb);
            sbCompression = (SeekBar) itemView.findViewById(R.id.sbCompression);
            sbDelay = (SeekBar) itemView.findViewById(R.id.sbDelay);
            sbTempo = (SeekBar) itemView.findViewById(R.id.sbTempo);
            sbPan = (SeekBar) itemView.findViewById(R.id.sbPan);
            sbPitch = (SeekBar) itemView.findViewById(R.id.sbPitch);
            frameInstrument = (FrameLayout) itemView.findViewById(R.id.frameInstrument);

            tvDoneFxEq.setVisibility(View.GONE);
            /*ivPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivPlay.setVisibility(v.VISIBLE);
                    ivPause.setVisibility(v.GONE);
                    mp.pause();
                    length = mp.getCurrentPosition();
                    melodySlider.setProgress(0);
                }
            });*/


            StudioActivity.playAll.setVisibility(View.GONE);
            audioFilePath =
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/InstaMelody.mp3";
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


            melodySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        int mCurrentPosition = currentPosition / 1000;
                        int mDuration = duration1 / 1000;
                        UtilsRecording utilRecording = new UtilsRecording();
                        int progress1 = utilRecording.getProgressPercentage(mCurrentPosition, mDuration);

                        if (mp != null && fromUser) {
                            int playPositionInMilliseconds = mp.getDuration() / 100 * melodySlider.getProgress();
                            mp.seekTo(playPositionInMilliseconds);
//                        seekBar.setProgress(progress);
                        } else {
                            // the event was fired from code and you shouldn't call player.seekTo()
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
        }


        private void primarySeekBarProgressUpdater() {
            //Handler mHandler1 = new Handler();
            try {

                melodySlider.setProgress((int) (((float) mp.getCurrentPosition() / mp.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (mp != null) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            primarySeekBarProgressUpdater();
                        }
                    };
                    StudioActivity.handler.postDelayed(notification, 100);
                    //mHandler1.postDelayed(notification, 100);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }


        }


    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_added, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    public static class ViewHolder {
        public SeekBar seekBar;
        public RelativeLayout TempRlRepeats;
        public ImageView holderPause, holderPlay;
        public TextView TxtMuteViewHolder, TxtSoloViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        try {
            //StudioActivity.playAll.setVisibility(View.VISIBLE);

            StudioActivity.setInsCount(instrumentList.size());

            if (lstViewHolder.size() < getItemCount()) {
                viewHolder = new ViewHolder();
                viewHolder.seekBar = (SeekBar) holder.melodySlider.findViewById(R.id.melodySlider);
                viewHolder.TempRlRepeats = (RelativeLayout) holder.rlrepeat.findViewById(R.id.rlrepeat);
                viewHolder.holderPause = (ImageView) holder.ivPause.findViewById(R.id.ivPause);
                viewHolder.holderPlay = (ImageView) holder.ivPlay.findViewById(R.id.ivPlay);
                viewHolder.TxtMuteViewHolder = (TextView) holder.tvMButton.findViewById(R.id.tvMButton);
                viewHolder.TxtSoloViewHolder = (TextView) holder.tvSButton.findViewById(R.id.tvSButton);
                StudioActivity.lstViewHolder.add(viewHolder);
            }
            // String aafs = FirebaseInstanceId.getInstance().getToken();
            final MelodyInstruments instruments = instrumentList.get(listPosition);
            //String abc = instrumentList.get(listPosition).getInstrumentFile();

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
//            if (userName != null) {
//                holder.tvUserName.setText("@" + userName);
//            } else if (fbName != null) {
//                holder.tvUserName.setText("@" + fbName);
//            } else
            holder.tvUserName.setText(instruments.getUserName());

            holder.tvInstrumentName.setText(instruments.getInstrumentName());
            String InstLen;
            if (instruments.getInstrumentLength() != null) {
                holder.tvInstrumentLength.setText(CalInstLen(instruments.getInstrumentLength()));
            } else {
                holder.tvInstrumentLength.setText(instruments.getInstrumentLength());
            }

            StudioActivity.playAll.setVisibility(View.VISIBLE);
            instrumentFile = instruments.getInstrumentFile();
            instrument_url_count.add(instrumentFile);
            Log.d("Instruments size", "" + instrumentFile);
            audioValue = instruments.getAudioType();

            holder.rlivDeleteMelody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!StudioActivity.IsRecordingStart) {
                            holder.deleteLl.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            holder.deleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //if (instrumentList.size() > 1) {
                            if (holder.mp != null) {
                                try {
                                    holder.mp.stop();
                                    holder.mp = null;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            if (StudioActivity.mpInst != null) {
                                try {
                                    StudioActivity.mpInst.stop();
                                    StudioActivity.mpInst = null;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }


                            for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                                if (i == holder.getAdapterPosition()) {
                                    try {
                                        StudioActivity.mediaPlayersAll.get(i).stop();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }

                            int newPosition = holder.getAdapterPosition();
                            instrumentList.remove(newPosition);
                            if (mediaPlayersAll.size() > 0) {
                                mediaPlayersAll.remove(newPosition);
                            }
                            StudioActivity.lstViewHolder.remove(newPosition);
                            notifyItemRemoved(newPosition);
                            StudioActivity.setInsCount(instrumentList.size());
//                        } else {
//                            Toast.makeText(getApplicationContext(), "You can't delete all instruments.", Toast.LENGTH_SHORT).show();
//                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });

            holder.cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.deleteLl.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            holder.rlMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //holder.tvMButton.setBackgroundColor(Color.GRAY);
                        //holder.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                        if (holder.mp != null && holder.mp.isPlaying()) {
                            if (IsMute == false) {

                                IsMute = true;

                                if (holder.mp != null && IsSolo == false) {
                                    holder.tvMButton.setBackgroundColor(Color.GRAY);
                                    holder.mp.setVolume(0, 0);
                                }
                            } else if (IsMute == true) {
                                IsMute = false;
                                holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }

                        if (StudioActivity.PlayAllModel.size() > 0) {

                            for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {


                                if (i == holder.getAdapterPosition()) {
                                    IsPlaySoloAll = StudioActivity.PlayAllModel.get(i).isSolo();
                                    IsPlayMuteAll = StudioActivity.PlayAllModel.get(i).isMute();

                                    if (IsPlayMuteAll == false) {
                                        StudioActivity.PlayAllModel.get(i).setMute(true);
                                        //StudioActivity.PlayAllModel.get(i).setSolo(false);
                                        holder.tvMButton.setBackgroundColor(Color.GRAY);
                                        holder.tvSButton.setBackgroundColor(Color.TRANSPARENT);
                                        StudioActivity.mediaPlayersAll.get(i).setVolume(0, 0);
                                    } else if (IsPlayMuteAll == true) {
                                        StudioActivity.PlayAllModel.get(i).setMute(false);
                                        //StudioActivity.PlayAllModel.get(i).setSolo(false);
                                        holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);
                                        holder.tvSButton.setBackgroundColor(Color.TRANSPARENT);
                                        StudioActivity.mediaPlayersAll.get(i).setVolume(1, 1);
                                    }

                                /*if (IsPlayMuteAll == false && IsPlaySoloAll == false) {
                                    StudioActivity.PlayAllModel.get(i).setMute(true);
                                    //StudioActivity.PlayAllModel.set(i,new ModelPlayAllMediaPlayer(true,true,false,StudioActivity.mpall));
                                    holder.tvMButton.setBackgroundColor(Color.GRAY);
                                    StudioActivity.mediaPlayersAll.get(i).setVolume(0, 0);
                                } else if (IsPlayMuteAll == true && IsPlaySoloAll == false) {
                                    StudioActivity.PlayAllModel.get(i).setMute(false);
                                    holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);
                                }else if( IsPlaySoloAll == true && IsPlayMuteAll == false){
                                    holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);
                                    StudioActivity.mediaPlayersAll.get(i).setVolume(0, 0);
                                }else if( IsPlaySoloAll == true && IsPlayMuteAll == true){
                                    StudioActivity.PlayAllModel.get(i).setMute(false);
                                    holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);
                                }*/

                                }

                            }


                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });


            holder.rlSolo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //holder.tvSButton.setBackgroundColor(Color.GRAY);
                        //holder.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                        if (holder.mp != null && holder.mp.isPlaying()) {
                            if (IsSolo == false) {
                                IsSolo = true;
                                if (holder.mp != null) {
                                    holder.tvSButton.setBackgroundColor(Color.GRAY);
                                    holder.mp.setVolume(1, 1);
                                }
                            } else if (IsSolo == true) {
                                IsSolo = false;
                                holder.tvSButton.setBackgroundColor(Color.TRANSPARENT);
                                if (IsSolo == false && IsMute == true) {
                                    holder.mp.setVolume(0, 0);
                                }
                            }
                        }

                        //if (IsPlaySoloAll == false) {
                        //holder.tvSButton.setBackgroundColor(Color.GRAY);
                        //StudioActivity.mediaPlayersAll.get(i).setVolume(1,1);
                        //StudioActivity.mediaPlayersAll
                        //StudioActivity.PlayAllModel.add(i,new ModelPlayAllMediaPlayer(false,false,false,StudioActivity.mpall));
                        if (StudioActivity.PlayAllModel.size() > 0) {
                            //IsPlaySoloAll = true;
                            for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                                IsPlaySoloAll = StudioActivity.PlayAllModel.get(i).isSolo();
                                IsPlayMuteAll = StudioActivity.PlayAllModel.get(i).isMute();
                                if (i == holder.getAdapterPosition()) {

                                    if (IsPlaySoloAll == false) {
                                        StudioActivity.PlayAllModel.get(i).setSolo(true);
                                        StudioActivity.PlayAllModel.get(i).setMute(false);
                                        holder.tvSButton.setBackgroundColor(Color.GRAY);
                                        holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);
                                        StudioActivity.mediaPlayersAll.get(i).setVolume(1, 1);
                                    } else if (IsPlaySoloAll == true) {
                                        StudioActivity.PlayAllModel.get(i).setSolo(false);
                                        StudioActivity.PlayAllModel.get(i).setMute(false);
                                        holder.tvSButton.setBackgroundColor(Color.TRANSPARENT);
                                        holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);

                                        for (int j = 0; j <= StudioActivity.mediaPlayersAll.size() - 1; j++) {
                                            TempIsPlaySoloAll = StudioActivity.PlayAllModel.get(j).isSolo();

                                            if (TempIsPlaySoloAll == true && j != holder.getAdapterPosition()) {
                                                StudioActivity.mediaPlayersAll.get(j).setVolume(1, 1);
                                                StudioActivity.PlayAllModel.get(j).setSolo(true);

                                            } else {
                                                StudioActivity.mediaPlayersAll.get(j).setVolume(0, 0);
                                                StudioActivity.PlayAllModel.get(j).setSolo(false);
                                                AllSolo = true;
                                            }

                                        }
                                        if (AllSolo == true) {
                                            for (int j = 0; j <= StudioActivity.mediaPlayersAll.size() - 1; j++) {
                                                StudioActivity.mediaPlayersAll.get(j).setVolume(1, 1);
                                                StudioActivity.PlayAllModel.get(j).setSolo(false);
                                                AllSolo = false;
                                            }
                                        }


                                    }

                                } else if (IsPlaySoloAll == false) {
                                    StudioActivity.mediaPlayersAll.get(i).setVolume(0, 0);
                                }

                            }
                        }
                        //}

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });


            holder.rlFX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        InstrumentListPosition = listPosition;

                        holder.frameInstrument.setVisibility(View.VISIBLE);
                        if (holder.fxContent.getVisibility() == View.VISIBLE) {
                            holder.fxContent.setVisibility(View.GONE);
                            holder.eqContent.setVisibility(View.GONE);
                            rlBase.setVisibility(View.VISIBLE);
                            holder.tvDoneFxEq.setVisibility(View.GONE);
                            StudioActivity.rlSync.setVisibility(View.VISIBLE);
                        } else if (holder.fxContent.getVisibility() == View.GONE) {
                            holder.fxContent.setVisibility(View.VISIBLE);
                            holder.tvDoneFxEq.setVisibility(View.VISIBLE);
                            holder.eqContent.setVisibility(View.GONE);
                            rlBase.setVisibility(View.GONE);
                            StudioActivity.rlSync.setVisibility(View.GONE);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });

            holder.rlEQ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {


                        InstrumentListPosition = listPosition;
                        holder.frameInstrument.setVisibility(View.VISIBLE);
                        if (holder.eqContent.getVisibility() == View.VISIBLE) {
                            holder.eqContent.setVisibility(View.GONE);
                            holder.fxContent.setVisibility(View.GONE);
                            holder.tvDoneFxEq.setVisibility(View.GONE);
                            rlBase.setVisibility(View.VISIBLE);
                            StudioActivity.rlSync.setVisibility(View.VISIBLE);

                        } else if (holder.eqContent.getVisibility() == View.GONE) {
                            holder.eqContent.setVisibility(View.VISIBLE);
                            holder.fxContent.setVisibility(View.GONE);
                            holder.tvDoneFxEq.setVisibility(View.VISIBLE);
                            rlBase.setVisibility(View.GONE);
                            StudioActivity.rlSync.setVisibility(View.GONE);


                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });


            holder.tvDoneFxEq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        holder.frameInstrument.setVisibility(View.GONE);
                        rlBase.setVisibility(View.VISIBLE);
                        StudioActivity.rlSync.setVisibility(View.VISIBLE);
                        if (StudioActivity.mpInst != null) {
                            StudioActivity.mpInst.pause();

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });

            holder.volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Volume = CalcuateEQProgressValue(progress);
                        //Toast.makeText(getApplicationContext(), String.valueOf(Volume), Toast.LENGTH_SHORT).show();
                        //Volume = progress;

                        if (StudioActivity.list.size() == 0) {
                            StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        } else {
                            StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        }
                        StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                        aa = StudioActivity.melodyMixing.getVocalsound();
                        holder.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }

            });
            holder.sbBase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Base = CalcuateEQProgressValue(progress);
                        //Toast.makeText(getApplicationContext(), String.valueOf(Base), Toast.LENGTH_SHORT).show();
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            holder.sbTreble.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Treble = CalcuateEQProgressValue(progress);
                        //Toast.makeText(getApplicationContext(), String.valueOf(Treble), Toast.LENGTH_SHORT).show();
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            holder.sbPan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Pan = progress;
                        int PanVolLeft = progress;
                        int PanVolRight = progress;
                        if (StudioActivity.list.size() == 0) {
                            StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        } else {
                            StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        }
                        StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                        aa = StudioActivity.melodyMixing.getVocalsound();

                        if (progress == 0) {
                            PanVolLeft = 1;
                            PanVolRight = 0;
                        } else if (progress == 1) {
                            PanVolLeft = 1;
                            PanVolRight = 1;
                        } else if (progress == 2) {
                            PanVolLeft = 0;
                            PanVolRight = 1;
                        }

                        for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                            //Toast.makeText(getApplicationContext(), "SBPAN "+i+" toholder "+String.valueOf(InstrumentListPosition), Toast.LENGTH_SHORT).show();
                            //if(i==InstrumentListPosition){
                            StudioActivity.mp_start.get(i).setVolume(PanVolLeft, PanVolRight);
                            //}
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            holder.sbCompression.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Compression = progress;
                        if (StudioActivity.list.size() == 0) {
                            StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        } else {
                            StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        }
                        StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                        aa = StudioActivity.melodyMixing.getVocalsound();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            holder.sbDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Delay = progress;
                        if (StudioActivity.list.size() == 0) {
                            StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        } else {
                            StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        }
                        StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                        aa = StudioActivity.melodyMixing.getVocalsound();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            holder.sbReverb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Reverb = progress;
                        if (StudioActivity.list.size() == 0) {
                            StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        } else {
                            StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        }
                        StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                        aa = StudioActivity.melodyMixing.getVocalsound();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            holder.sbTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    try {
                        Tempo = progress;
                        if (StudioActivity.list.size() == 0) {
                            StudioActivity.list.add(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        } else {
                            StudioActivity.list.set(InstrumentListPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo), String.valueOf(threshold), String.valueOf(ratio), String.valueOf(attack), String.valueOf(release), String.valueOf(makeup), String.valueOf(knee), String.valueOf(mix), InstaURL, PositionId));
                        }
                        StudioActivity.melodyMixing.setVocalsound(StudioActivity.list);
                        aa = StudioActivity.melodyMixing.getVocalsound();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            StudioActivity.playAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (StudioActivity.mVisualizer != null) {
                            StudioActivity.mVisualizer.release();
                        }

                        StudioActivity.recyclerViewInstruments.smoothScrollToPosition(instrumentList.size());
                        if (StudioActivity.mediaPlayersAll.size() > 0) {
                            StudioActivity.mediaPlayersAll.clear();
                        }
                        if (StudioActivity.PlayAllModel.size() > 0) {
                            StudioActivity.PlayAllModel.clear();
                        }
                        StudioActivity.handler.removeCallbacksAndMessages(null);
                        for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                            try {
                                if (StudioActivity.mp_start.get(i) != null) {
                                    StudioActivity.mp_start.get(i).stop();
                                    StudioActivity.mp_start.get(i).release();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                        IsPlaySoloAll = false;
                        IsPlayMuteAll = false;
                        executeAsyncTaskPlayAll();
                        StudioActivity.ivRecord.setEnabled(false);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            StudioActivity.pauseAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        StudioActivity.IsRepeteReAll = false;
                        if (StudioActivity.mVisualizer != null) {
                            StudioActivity.mVisualizer.release();
                        }
                        IsPlaySoloAll = false;
                        IsPlayMuteAll = false;
                        StudioActivity.recyclerViewInstruments.smoothScrollToPosition(0);
                        StudioActivity.pauseAll.setVisibility(View.GONE);
                        StudioActivity.playAll.setVisibility(View.VISIBLE);
                        StudioActivity.ivRecord.setEnabled(true);
                        InstrumentCountSize = 0;


                        StudioActivity.handler.removeCallbacksAndMessages(null);


                        for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                            final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                            final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;
                            final SeekBar seekBar = StudioActivity.lstViewHolder.get(i).seekBar;
                            final TextView txtMutes = StudioActivity.lstViewHolder.get(i).TxtMuteViewHolder;
                            final TextView txtSolos = StudioActivity.lstViewHolder.get(i).TxtSoloViewHolder;
                            final RelativeLayout RlsRepets = StudioActivity.lstViewHolder.get(i).TempRlRepeats;
                            seekBar.setProgress(0);
                            holderPlay.setVisibility(View.VISIBLE);
                            holderPause.setVisibility(View.GONE);
                            txtMutes.setBackgroundColor(Color.TRANSPARENT);
                            txtSolos.setBackgroundColor(Color.TRANSPARENT);
                            RlsRepets.setBackgroundColor(Color.TRANSPARENT);
                            holderPause.setEnabled(true);
                            try {
                                StudioActivity.mediaPlayersAll.get(i).stop();
                                StudioActivity.mediaPlayersAll.get(i).release();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                        if (StudioActivity.mpall != null) {
                            try {
                                StudioActivity.mpall.stop();
                                StudioActivity.mpall.release();
                                StudioActivity.mpall = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (StudioActivity.mpInst != null) {
                            try {
                                StudioActivity.mpInst.stop();
                                StudioActivity.mpInst.release();
                                StudioActivity.mpInst = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (StudioActivity.mVisualizer != null) {
                            StudioActivity.mVisualizer.release();
                        }
                        instruments_url.add(instrumentFile);
                        instrumentFile = instruments.getInstrumentFile();
                        StudioActivity.handler.removeCallbacksAndMessages(null);
                        if (holder.mp != null) {
                            try {
                                holder.mp.stop();
                                holder.mp.release();
                                holder.mp = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {

                            try {
                                if (StudioActivity.mp_start.get(i) != null) {
                                    try {
                                        StudioActivity.mp_start.get(i).stop();
                                        StudioActivity.mp_start.get(i).release();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (StudioActivity.PlayAllModel.size() > 0) {
                            StudioActivity.PlayAllModel.clear();
                        }
                        for (int i = 0; i <= StudioActivity.lstViewHolder.size() - 1; i++) {

                            if (i != holder.getAdapterPosition()) {
                                final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                                final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;
                                final SeekBar seekBar = StudioActivity.lstViewHolder.get(i).seekBar;
                                final TextView txtMutes = StudioActivity.lstViewHolder.get(i).TxtMuteViewHolder;
                                final TextView txtSolos = StudioActivity.lstViewHolder.get(i).TxtSoloViewHolder;
                                final RelativeLayout RlsRepets = StudioActivity.lstViewHolder.get(i).TempRlRepeats;
                                seekBar.setProgress(0);
                                holderPlay.setVisibility(View.VISIBLE);
                                holderPause.setVisibility(View.GONE);
                                txtMutes.setBackgroundColor(Color.TRANSPARENT);
                                txtSolos.setBackgroundColor(Color.TRANSPARENT);
                                RlsRepets.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }
                        holder.ivPlay.setVisibility(v.GONE);
                        holder.ivPause.setVisibility(v.VISIBLE);
                        StudioActivity.mp_start.clear();
                        holder.mp = new MediaPlayer();

                        holder.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            holder.mp.setDataSource(instrumentFile);
                            //new PrepareIndividualMediaPlayer().execute(holder.mp);
                            holder.progressDialog = new ProgressDialog(v.getContext());
                            holder.progressDialog.setMessage("Loading...");
                            holder.progressDialog.show();
                            holder.mp.prepareAsync();

                            StudioActivity.mp_start.add(holder.mp);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        for (MediaPlayer instrument_media : StudioActivity.mp_start) {
                            instrument_media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mps) {
                                    holder.progressDialog.dismiss();
                                    initAudio(mps);
                                    mps.start();
                                    if (StudioActivity.IsRepeat) {
                                        holder.mp.setLooping(true);
                                    } else {
                                        holder.mp.setLooping(false);
                                    }

                                    bass = new BassBoost(0, mps.getAudioSessionId());
                                    int bassval = bass.getProperties().strength;

                                    equalizer = new Equalizer(0, mps.getAudioSessionId());
                                    equalizer.setEnabled(true);
                      /*  equalizer.getNumberOfBands(); //it tells you the number of equalizer in device.
                        equalizer.getNumberOfPresets();//like Normal Classic,Dance Flat,Folk Heavy Metal,Hip Hop,Jazz, Pop, Rock*/

                                    numberFrequencyBands = equalizer.getNumberOfBands();
                         /*Get the level range to used in settings the band level*/
                        /*Get lower limit of the range in millibels*/
                                    lowerEquilizerBandLevel = equalizer.getBandLevelRange()[0];
                        /*Get the upper level of the range in millibels*/
                                    UpperEquilizerBandLevel = equalizer.getBandLevelRange()[1];


                                    holder.sbTreble.setMax(UpperEquilizerBandLevel - lowerEquilizerBandLevel);
                                    for (short i = 0; i < numberFrequencyBands; i++) {
                                        eqaulizerBandIndex = i;
                                        holder.sbTreble.setProgress(equalizer.getBandLevel(eqaulizerBandIndex));
                                    }
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
                                holder.ivPlay.setVisibility(View.VISIBLE);
                                holder.melodySlider.setProgress(0);
                                return false;
                            }
                        });
                        holder.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                //duration1 = holder.mp.getDuration();
                                //currentPosition = holder.mp.getCurrentPosition();
                                //holder.progressDialog.dismiss();
                                StudioActivity.handler.removeCallbacksAndMessages(null);
                                holder.ivPlay.setVisibility(View.VISIBLE);
                                if (holder.mp != null) {
                                    try {
                                        holder.mp.setLooping(false);
                                        //holder.mp.stop();
                                        holder.mp.release();
                                        holder.mp = null;
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    holder.melodySlider.setProgress(0);


                                    IsMute = false;
                                    IsSolo = false;
                                    holder.tvSButton.setBackgroundColor(Color.TRANSPARENT);
                                    holder.tvMButton.setBackgroundColor(Color.TRANSPARENT);

                                } else {
                                    holder.mp.setLooping(false);
                                    holder.melodySlider.setProgress(0);

                                }
                            }
                        });


                        instrumentName = instruments.getInstrumentName();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });

            holder.ivPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (StudioActivity.mVisualizer != null) {
                            StudioActivity.mVisualizer.release();
                        }
                        holder.ivPlay.setVisibility(v.VISIBLE);
                        holder.ivPause.setVisibility(v.GONE);
                        StudioActivity.handler.removeCallbacksAndMessages(null);
                        holder.melodySlider.setProgress(0);
                        length = holder.mp.getCurrentPosition();
                        if (holder.mp != null) {
                            try {
                                holder.mp.stop();
                                holder.mp.release();
                                holder.mp = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }


                        for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                            try {

                                if (StudioActivity.mp_start.get(i) != null) {
                                    StudioActivity.mp_start.get(i).stop();
                                    StudioActivity.mp_start.get(i).release();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                        for (int i = 0; i <= StudioActivity.lstViewHolder.size() - 1; i++) {

                            if (i != holder.getAdapterPosition()) {
                                final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                                final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;
                                final SeekBar seekBar = StudioActivity.lstViewHolder.get(i).seekBar;
                                final TextView txtMutes = StudioActivity.lstViewHolder.get(i).TxtMuteViewHolder;
                                final TextView txtSolos = StudioActivity.lstViewHolder.get(i).TxtSoloViewHolder;
                                final RelativeLayout RlsRepets = StudioActivity.lstViewHolder.get(i).TempRlRepeats;
                                seekBar.setProgress(0);
                                holderPlay.setVisibility(View.VISIBLE);
                                holderPause.setVisibility(View.GONE);
                                txtMutes.setBackgroundColor(Color.TRANSPARENT);
                                txtSolos.setBackgroundColor(Color.TRANSPARENT);
                                RlsRepets.setBackgroundColor(Color.TRANSPARENT);
                            }

                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });

            holder.rlrepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (StudioActivity.IsRepeteReAll) {

                            if (StudioActivity.PlayAllModel.size() > 0) {
                                //IsPlaySoloAll = true;
                                for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {

                                    if (i == holder.getAdapterPosition()) {
                                        IsRepeateRec = StudioActivity.PlayAllModel.get(i).isRepete();
                                        if (IsRepeateRec) {
                                            StudioActivity.PlayAllModel.get(i).setRepete(false);
                                            holder.rlrepeat.setBackgroundColor(Color.TRANSPARENT);
                                            MediaPlayer mp = StudioActivity.PlayAllModel.get(i).getMediaPlayer();
                                            mp.setLooping(false);
                                        } else {
                                            StudioActivity.PlayAllModel.get(i).setRepete(true);
                                            holder.rlrepeat.setBackgroundColor(Color.GRAY);
                                            MediaPlayer mp = StudioActivity.PlayAllModel.get(i).getMediaPlayer();
                                            mp.setLooping(true);

                                        }
                                    }
                                }
                            }

                        } else {

                            if (StudioActivity.IsRepeat == false) {
                                //ArrayList arrayList=new ArrayList();
                                //ArRepeate.add(holder.getAdapterPosition(), 1);
                                StudioActivity.IsRepeat = true;
                                holder.rlrepeat.setBackgroundColor(Color.GRAY);
                                if (holder.mp != null) {
                                    if (holder.mp.isLooping() == false) {
                                        holder.mp.setLooping(true);
                                    } else if (holder.mp.isLooping() == true) {
                                        holder.mp.setLooping(false);
                                    }
                                }
                            } else if (StudioActivity.IsRepeat == true) {
                                StudioActivity.IsRepeat = false;
                                holder.rlrepeat.setBackgroundColor(Color.TRANSPARENT);
                                if (holder.mp != null) {
                                    if (holder.mp.isLooping() == false) {
                                        holder.mp.setLooping(true);
                                    } else if (holder.mp.isLooping() == true) {
                                        holder.mp.setLooping(false);
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                }
            });


            Intent i = new Intent("fetchingInstruments");
            i.putStringArrayListExtra("instruments", instrument_url_count);
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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


    public void executeAsyncTaskPlayAll() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e("AsyncTask", "onPreExecute");
                try {
                    StudioActivity.ivBackButton.setEnabled(false);
                    StudioActivity.ivHomeButton.setEnabled(false);
                    for (int i = 0; i <= StudioActivity.lstViewHolder.size(); i++) {
                        try {
                            final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                            final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;
                            final SeekBar seekBar = StudioActivity.lstViewHolder.get(i).seekBar;
                            holderPlay.setVisibility(View.VISIBLE);
                            holderPause.setVisibility(View.GONE);
                            seekBar.setProgress(0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    StudioActivity.frameProgress.setVisibility(View.VISIBLE);
                    StudioActivity.ivRecord.setEnabled(false);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                Log.v("AsyncTask", "doInBackground");
                String msg = null;
                // some calculation logic of msg variable
                try {
                    if (InstrumentCountSize == 0) {
                        StudioActivity.IsRepeteReAll = true;
                        InstrumentCountSize = instrumentList.size();
                        tmpduration = 0;
                        Compdurations = 0;
                        MaxMpSessionID = 0;
                        for (int i = 0; i < InstrumentCountSize; i++) {
                            Log.d("Instrument url :", "" + instrumentList.get(i).getInstrumentFile());
                            StudioActivity.mpall = new MediaPlayer();
                            StudioActivity.mpall.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            StudioActivity.mpall.setDataSource(instrumentList.get(i).getInstrumentFile());
                            try {

                                StudioActivity.mpall.prepare();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            StudioActivity.mediaPlayersAll.add(StudioActivity.mpall);
                            StudioActivity.PlayAllModel.add(i, new ModelPlayAllMediaPlayer(false, false, false, StudioActivity.mpall));

                            Compdurations = StudioActivity.mediaPlayersAll.get(i).getDuration();
                            if (Compdurations > tmpduration) {
                                tmpduration = Compdurations;
                                MaxMpSessionID = StudioActivity.mediaPlayersAll.get(i).getAudioSessionId();
                            }

                        }
                        StudioActivity.mpall.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (MaxMpSessionID == mp.getAudioSessionId() && StudioActivity.lstViewHolder.size() != 0) {
                                    StudioActivity.handler.removeCallbacksAndMessages(null);
                                    for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {

                                        StudioActivity.PlayAllModel.get(i).setRepete(false);
                                        if (i <= lstViewHolder.size() - 1) {
                                            final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                                            final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;
                                            final SeekBar seekBar = StudioActivity.lstViewHolder.get(i).seekBar;
                                            final TextView txtMutes = StudioActivity.lstViewHolder.get(i).TxtMuteViewHolder;
                                            final TextView txtSolos = StudioActivity.lstViewHolder.get(i).TxtSoloViewHolder;
                                            final RelativeLayout RlsRepets = StudioActivity.lstViewHolder.get(i).TempRlRepeats;
                                            seekBar.setProgress(0);
                                            holderPlay.setVisibility(View.VISIBLE);
                                            holderPause.setVisibility(View.GONE);
                                            txtMutes.setBackgroundColor(Color.TRANSPARENT);
                                            txtSolos.setBackgroundColor(Color.TRANSPARENT);
                                            RlsRepets.setBackgroundColor(Color.TRANSPARENT);
                                            holderPause.setEnabled(false);
                                            StudioActivity.playAll.setVisibility(View.VISIBLE);
                                            StudioActivity.pauseAll.setVisibility(View.GONE);

                                            StudioActivity.ivRecord.setEnabled(true);
                                            InstrumentCountSize = 0;

                                        }
                                        try {
                                            StudioActivity.mediaPlayersAll.get(i).stop();
                                            StudioActivity.mediaPlayersAll.get(i).release();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }

                                }
                            }
                        });
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.v("AsyncTask", "onPostExecute");
                try {

                    StudioActivity.playAll.setVisibility(View.GONE);
                    StudioActivity.pauseAll.setVisibility(View.VISIBLE);
                    StudioActivity.ivRecord.setEnabled(false);
                    //StudioActivity.waveform_view.setVisibility(View.VISIBLE);
                    //StudioActivity.frameProgress.setVisibility(View.GONE);
                    StudioActivity.ivBackButton.setEnabled(true);
                    StudioActivity.ivHomeButton.setEnabled(true);
                    StudioActivity.frameProgress.setVisibility(View.GONE);
                    if (StudioActivity.lstViewHolder.size() != 0) {
                        for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {

                            try {
                                initAudio(StudioActivity.mediaPlayersAll.get(i));
                                StudioActivity.mediaPlayersAll.get(i).start();

                            } catch (IllegalArgumentException e) {

                            } catch (IllegalStateException e) {

                            } catch (Exception e) {

                            }
                            if (StudioActivity.IsRepeat == true) {
                                StudioActivity.mediaPlayersAll.get(i).setLooping(true);
                            } else if (StudioActivity.IsRepeat == false) {
                                StudioActivity.mediaPlayersAll.get(i).setLooping(false);
                            }
                            if (i <= lstViewHolder.size() - 1) {
                                try {
                                    final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                                    final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;

                                    holderPlay.setVisibility(View.GONE);
                                    holderPause.setVisibility(View.VISIBLE);
                                    holderPause.setEnabled(false);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        RunSeekbar();
                    }


                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        };

        if (Build.VERSION.SDK_INT >= 18/*HONEYCOMB*/) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }


    public void RunSeekbar() {

        try {
            for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                final MediaPlayer pts;
                //final SeekBar seekBarf;
                pts = StudioActivity.mediaPlayersAll.get(i);

                final SeekBar seekBarf = StudioActivity.lstViewHolder.get(i).seekBar;

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int currentPosition = pts.getCurrentPosition() / 1000;
                            int duration = pts.getDuration() / 1000;
                            int progress = (currentPosition * 100) / duration;
                            //seekBar.setProgress((int) (((float) pts.getCurrentPosition() / pts.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                            seekBarf.setProgress(progress);
                            StudioActivity.handler.postDelayed(this, 1000);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                StudioActivity.handler.postDelayed(runnable, 1000);
            }


        } catch (Throwable ex) {
            ex.printStackTrace();
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

    public String CalInstLen(String instlen) {
        try {
            if (instlen != null) {
                int minutes = Integer.parseInt(instlen) / 60;
                int seconds = Integer.parseInt(instlen) % 60;
                instlen = String.valueOf(minutes) + ":" + String.valueOf(seconds);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return instlen;
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

    public void initAudio(MediaPlayer mpst) {
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //mMediaPlayer = mpst;

        setupVisualizerFxAndUI(mpst);
        // Make sure the visualizer is enabled only when you actually want to
        // receive data, and
        // when it makes sense to receive data.
        StudioActivity.mVisualizer.setEnabled(true);
        // When the stream ends, we don't need to collect any more data. We
        // don't do this in
        // setupVisualizerFxAndUI because we likely want to have more,
        // non-Visualizer related code
        // in this callback.
        /*mpst.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        StudioActivity.mVisualizer.setEnabled(false);
                    }
                });*/


    }

    private void setupVisualizerFxAndUI(MediaPlayer mpvis) {

        // Create the Visualizer object and attach it to our media player.
        StudioActivity.mVisualizer = new Visualizer(mpvis.getAudioSessionId());
        StudioActivity.mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        StudioActivity.mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        StudioActivity.mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }
     /*private class PrepareInstruments extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            try {
                try {

                    for (int i = 0; i <= StudioActivity.lstViewHolder.size() - 1; i++) {
                        try {
                            final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                            final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;
                            final SeekBar seekBar = StudioActivity.lstViewHolder.get(i).seekBar;
                            holderPlay.setVisibility(View.VISIBLE);
                            holderPause.setVisibility(View.GONE);
                            seekBar.setProgress(0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    StudioActivity.frameProgress.setVisibility(View.VISIBLE);
                    if (StudioActivity.PlayAllModel.size() > 0) {
                        StudioActivity.PlayAllModel.clear();
                    }
                    if (StudioActivity.mediaPlayersAll.size() > 0) {
                        StudioActivity.mediaPlayersAll.clear();
                    }
                    StudioActivity.handler.removeCallbacksAndMessages(null);

                    for (int i = 0; i <= StudioActivity.mp_start.size() - 1; i++) {
                        try {

                            if (StudioActivity.mp_start.get(i) != null) {
                                StudioActivity.mp_start.get(i).stop();
                                StudioActivity.mp_start.get(i).release();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

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
                    tmpduration = 0;
                    Compdurations = 0;
                    MaxMpSessionID = 0;
                    //InstrumentCountSize = MelodyInstruments.getInstrumentCount();

                    InstrumentCountSize = instrumentList.size();
                    for (int i = 0; i < InstrumentCountSize; i++) {
                        Log.d("Instrument url----------------:", "" + instrumentList.get(i).getInstrumentFile());
                        StudioActivity.mpall = new MediaPlayer();
                        StudioActivity.mpall.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        StudioActivity.mpall.setDataSource(instrumentList.get(i).getInstrumentFile());
                        try {

                            StudioActivity.mpall.prepare();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        StudioActivity.mediaPlayersAll.add(StudioActivity.mpall);
                        StudioActivity.PlayAllModel.add(i, new ModelPlayAllMediaPlayer(false, false, false, StudioActivity.mpall));

                        Compdurations = StudioActivity.mediaPlayersAll.get(i).getDuration();
                        if (Compdurations > tmpduration) {
                            tmpduration = Compdurations;
                            MaxMpSessionID = StudioActivity.mediaPlayersAll.get(i).getAudioSessionId();
                        }
                    }


                    StudioActivity.mpall.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            if (MaxMpSessionID == mp.getAudioSessionId()) {
                                StudioActivity.handler.removeCallbacksAndMessages(null);
                                for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {

                                    final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                                    final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;
                                    final SeekBar seekBar = StudioActivity.lstViewHolder.get(i).seekBar;
                                    final TextView txtMutes = StudioActivity.lstViewHolder.get(i).TxtMuteViewHolder;
                                    final TextView txtSolos = StudioActivity.lstViewHolder.get(i).TxtSoloViewHolder;
                                    final RelativeLayout RlsRepets = StudioActivity.lstViewHolder.get(i).TempRlRepeats;
                                    seekBar.setProgress(0);
                                    holderPlay.setVisibility(View.VISIBLE);
                                    holderPause.setVisibility(View.GONE);
                                    txtMutes.setBackgroundColor(Color.TRANSPARENT);
                                    txtSolos.setBackgroundColor(Color.TRANSPARENT);
                                    RlsRepets.setBackgroundColor(Color.TRANSPARENT);
                                    holderPause.setEnabled(true);
                                    StudioActivity.playAll.setVisibility(View.VISIBLE);
                                    StudioActivity.pauseAll.setVisibility(View.GONE);
                                    //StudioActivity.mediaPlayersAll.get(i).stop();
                                }
                            }

                            //}
                        }
                    });
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                StudioActivity.playAll.setVisibility(View.GONE);
                StudioActivity.ivRecord.setVisibility(View.GONE);
                StudioActivity.rlMelodyButton.setVisibility(View.GONE);
                StudioActivity.ivRecord_stop.setVisibility(View.VISIBLE);
                StudioActivity.rlRecordingButton.setVisibility(View.VISIBLE);
                StudioActivity.pauseAll.setVisibility(View.VISIBLE);
                StudioActivity.pauseAll.setEnabled(false);
                for (int i = 0; i <= StudioActivity.mediaPlayersAll.size() - 1; i++) {
                    final MediaPlayer pts;

                    pts = StudioActivity.mediaPlayersAll.get(i);
                    try {
                        pts.start();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (IsRepeat == true) {
                        pts.setLooping(true);
                    } else if (IsRepeat == false) {
                        pts.setLooping(false);
                    }
                    StudioActivity.waveform_view.setVisibility(View.VISIBLE);

                    StudioActivity.waveform_view.setEnabled(true);
                    final ImageView holderPlay = StudioActivity.lstViewHolder.get(i).holderPlay;
                    final ImageView holderPause = StudioActivity.lstViewHolder.get(i).holderPause;

                    holderPlay.setVisibility(View.GONE);
                    holderPause.setVisibility(View.VISIBLE);
                    holderPause.setEnabled(false);

                }
                StudioActivity.frameProgress.setVisibility(View.GONE);
                //recordAudio();
                RunSeekbar();


                StudioActivity.chrono.setBase(SystemClock.elapsedRealtime());
                StudioActivity.chrono.start();

            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }*/
}