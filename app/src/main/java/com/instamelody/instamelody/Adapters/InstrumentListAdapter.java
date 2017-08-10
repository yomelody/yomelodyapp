package com.instamelody.instamelody.Adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StudioActivity;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Saurabh Singh on 06/04/17
 */

public class InstrumentListAdapter extends RecyclerView.Adapter<InstrumentListAdapter.MyViewHolder> {

    static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    ArrayList<String> vocalsound = new ArrayList<>();
    String audioValue;
    static String audioUrl;
    private static String audioFilePath;
    private static String instrumentFile;
    int duration1, currentPosition;
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
    int InstrumentId = 0, Volume = 0, Base = 0, Treble = 0, Pan = 0, Pitch = 0, Reverb = 0, Compression = 0, Delay = 0, Tempo = 0;
    Boolean playfrom_studio = false;
    MelodyMixing melodyMixing = new MelodyMixing();
    ArrayList<MixingData> list = new ArrayList<MixingData>();
    List aa;

    public InstrumentListAdapter(ArrayList<MelodyInstruments> instrumentList, Context context) {
        this.instrumentList = instrumentList;
        this.context = context;

    }

    public InstrumentListAdapter(boolean playfromStudio, Context context) {
        this.playfrom_studio = playfromStudio;
        this.context = context;
    }

    public InstrumentListAdapter(boolean playfromStudio, Context context) {
        this.playfrom_studio = playfromStudio;
        this.context = context;
    }

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
        SeekBar melodySlider, volumeSeekbar, sbTreble, sbBase, sbPan, sbPitch, sbReverb, sbCompression, sbDelay, sbTempo;
        FrameLayout frameInstrument;
        RelativeLayout rlSeekbarTracer, rlSync;
        ImageView grey_circle, blue_circle;
        RelativeLayout rlFX, rlEQ, eqContent, fxContent;
        private int maxVolume = 0;
        private int curVolume = 0;

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
            volumeSeekbar = (SeekBar) itemView.findViewById(R.id.sbVolume);
            sbTreble = (SeekBar) itemView.findViewById(R.id.sbTreble);
            sbBase = (SeekBar) itemView.findViewById(R.id.sbBase);
            sbReverb = (SeekBar) itemView.findViewById(R.id.sbReverb);
            sbCompression = (SeekBar) itemView.findViewById(R.id.sbCompression);
            sbDelay = (SeekBar) itemView.findViewById(R.id.sbDelay);
            sbTempo = (SeekBar) itemView.findViewById(R.id.sbTempo);
            sbPan = (SeekBar) itemView.findViewById(R.id.sbPan);
            sbPitch = (SeekBar) itemView.findViewById(R.id.sbPitch);

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
//
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
            Handler mHandler1 = new Handler();
            melodySlider.setProgress((int) (((float) mp.getCurrentPosition() / mp.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
            if (mp.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                mHandler1.postDelayed(notification, 100);
            }
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_added, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;


    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {


        final MelodyInstruments instruments = instrumentList.get(listPosition);
        String abc = instrumentList.get(listPosition).getInstrumentFile();
//        Toast.makeText(context, "" + abc, Toast.LENGTH_SHORT).show();

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
//        Toast.makeText(context, "" + instrumentFile, Toast.LENGTH_SHORT).show();
        Log.d("Instruments size", "" + instrumentFile);

        list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
        //This line commented by Abhishek

        //   new DownloadInstruments().execute(instrumentFile);

        //  i.putExtra("instruments", instrumentFile);
//        if (playfrom_studio == true) {
//            holder.ivPlay.setVisibility(View.GONE);
//            holder.ivPause.setVisibility(View.VISIBLE);
//            holder.primarySeekBarProgressUpdater();
//        }
        // initControls();
        //holder.volumeSeekbar.setMax(holder.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        audioValue = instruments.getAudioType();
        holder.rlEQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.frameInstrument.setVisibility(View.VISIBLE);
                if (holder.eqContent.getVisibility() == View.VISIBLE) {
                    holder.eqContent.setVisibility(View.GONE);
                    holder.fxContent.setVisibility(View.GONE);
                    holder.frameInstrument.setVisibility(View.GONE);
                } else if (holder.eqContent.getVisibility() == View.GONE) {
                    holder.eqContent.setVisibility(View.VISIBLE);
                    holder.fxContent.setVisibility(View.GONE);
                }
            }
        });
        holder.rlFX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.frameInstrument.setVisibility(View.VISIBLE);
                if (holder.fxContent.getVisibility() == View.VISIBLE) {
                    holder.fxContent.setVisibility(View.GONE);
                    holder.eqContent.setVisibility(View.GONE);
                    holder.frameInstrument.setVisibility(View.GONE);
                } else if (holder.fxContent.getVisibility() == View.GONE) {
                    holder.fxContent.setVisibility(View.VISIBLE);
                    holder.eqContent.setVisibility(View.GONE);
                }
            }
        });
        holder.tvDoneFxEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.frameInstrument.setVisibility(View.GONE);
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

                Volume = progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
                holder.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
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

                Base = progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
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
                Treble = progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
            }
        });
        holder.sbBase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                Base=progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
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
                Reverb=progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
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
                Compression=progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
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
                Delay=progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
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
                Tempo=progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
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
                Pan=progress;
                if (list.size() == 0) {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo)));
                } else {
                    list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble),String.valueOf(Pan),String.valueOf(Pitch),String.valueOf(Reverb),String.valueOf(Compression),String.valueOf(Delay),String.valueOf(Tempo)));
                }
                melodyMixing.setVocalsound(list);
                aa = melodyMixing.getVocalsound();
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                holder.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        holder.progressDialog.dismiss();
                        mp.start();
                        holder.primarySeekBarProgressUpdater();

                    }
                });
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
                holder.melodySlider.setProgress(0);
            }
        });

        Intent i = new Intent("fetchingInstruments");
        i.putStringArrayListExtra("instruments", instrument_url_count);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
        if (playfrom_studio == true) {
            Toast.makeText(context, "hhjj", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public int getItemCount() {
        //  rvLength = instrumentList.size();
        return instrumentList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == instrumentList.size()) ? R.layout.layout_button_sync : R.layout.card_melody_added;
    }


}
