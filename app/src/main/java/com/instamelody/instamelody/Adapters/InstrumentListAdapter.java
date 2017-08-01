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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.R;
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
    String audioValue;
    static String audioUrl;
    private static String audioFilePath;
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

    public static final int progress_bar_type = 0;
    ArrayList instrument_url_count = new ArrayList();
    ArrayList<String> fetch_url_arrayList = new ArrayList<>();
    boolean playfrom_studio = false;

    ProgressBar loader;
    RelativeLayout loader_v;
    boolean isPlayfrom_studio;

    public InstrumentListAdapter(ArrayList<MelodyInstruments> instrumentList, Context context) {
        this.instrumentList = instrumentList;
        this.context = context;

    }

    public InstrumentListAdapter(boolean playfrom_studio, Context context) {
        this.playfrom_studio = playfrom_studio;
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
        TextView tvInstrumentName, tvUserName, tvInstrumentLength, tvBpmRate, tvSync;
        SeekBar melodySlider;
        RelativeLayout rlSeekbarTracer, rlSync;
        ImageView grey_circle, blue_circle;
        ProgressDialog progressDialog;
        public MediaPlayer mp;
        int duration1, currentPosition;
        CardView card_melody;


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
            loader = (ProgressBar) itemView.findViewById(R.id.loader);

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

                    int mCurrentPosition = mp.getCurrentPosition() / 1000;
                    int mDuration = mp.getDuration() / 1000;
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
                    melodySlider.setProgress(0);
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


        //This line commented by Abhishek

        audioValue = instruments.getAudioType();

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
                        holder.duration1 = holder.mp.getDuration();
                        holder.currentPosition = holder.mp.getCurrentPosition();
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
