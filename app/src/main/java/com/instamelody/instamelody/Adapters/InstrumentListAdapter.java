package com.instamelody.instamelody.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.instamelody.instamelody.MainActivity;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.internal.Util;

import static android.R.attr.duration;
import static android.R.attr.resumeWhilePausing;
import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.R.attr.position;
import static com.instamelody.instamelody.R.id.melodySlider;
import static com.instamelody.instamelody.R.id.rlSeekbarTracer;
import static com.instamelody.instamelody.R.id.visible;

/**
 * Created by Saurabh Singh on 06/04/17
 */

public class InstrumentListAdapter extends RecyclerView.Adapter<InstrumentListAdapter.MyViewHolder> {

    ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    String audioValue;
    static String audioUrl;
    private static String audioFilePath;
    private static String instrumentFile;
    static MediaPlayer mp;
    int length;
    String coverPicStudio;
    String instrumentName;
    int rvLength;
    Context context;
    View mLayout;

    static int duration1, currentPosition;

    public InstrumentListAdapter(ArrayList<MelodyInstruments> instrumentList, Context context) {
        this.instrumentList = instrumentList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfileImage, ivInstrumentCover, ivPlay, ivPause;
        TextView tvInstrumentName, tvUserName, tvInstrumentLength, tvBpmRate, tvSync;
        SeekBar melodySlider;
        RelativeLayout rlSeekbarTracer, rlSync;
        ImageView grey_circle, blue_circle;


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
            melodySlider.setProgress((int) (((float) mp.getCurrentPosition() / duration1) * 100));// This math construction give a percentage of "was playing"/"song length"
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
        View rowView;

        switch (viewType) {
            case VIEW_TYPES.Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_added, parent, false);
                break;
            case VIEW_TYPES.Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_button_sync, parent, false);
                break;
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_added, parent, false);
                break;
        }
        return new MyViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final MelodyInstruments instruments = instrumentList.get(listPosition);
        Log.d("Insrtument size", "" + instruments);
        if (coverPicStudio != null) {
            Picasso.with(holder.ivInstrumentCover.getContext()).load(coverPicStudio).into(holder.ivInstrumentCover);
        } else if (instruments.getInstrumentCover().charAt(0) == '#') {
            String color = instruments.getInstrumentCover();
            holder.ivInstrumentCover.setBackgroundColor(Color.parseColor(color));
        } else {
            Picasso.with(holder.ivInstrumentCover.getContext()).load(instruments.getInstrumentCover()).into(holder.ivInstrumentCover);
        }
        Picasso.with(holder.userProfileImage.getContext()).load(instruments.getUserProfilePic()).into(holder.userProfileImage);
        holder.tvBpmRate.setText(instruments.getInstrumentBpm());
        holder.tvUserName.setText(instruments.getUserName());
        holder.tvInstrumentName.setText(instruments.getInstrumentName());
        holder.tvInstrumentLength.setText(instruments.getInstrumentLength());
        audioValue = instruments.getAudioType();
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivPlay.setVisibility(v.GONE);
                holder.ivPause.setVisibility(v.VISIBLE);
                instrumentFile = instruments.getInstrumentFile();
                instrumentName = instruments.getInstrumentName();

                Intent i = new Intent("fetchingInstruments");
                i.putExtra("instruments", instrumentFile);
                LocalBroadcastManager.getInstance(context).sendBroadcast(i);

                try {
                    Integer s = listPosition + 1;

                    if (getItemCount() > s && instrumentFile != null) {
                        playAudio();
                        holder.primarySeekBarProgressUpdater();
                    } else if (getItemCount() + 1 > s) {
                        if (getItemCount() == 1 || getItemCount() == 2) {
                            playAudio();
                            holder.primarySeekBarProgressUpdater();
                        } else
                            playAudio1();
                        holder.primarySeekBarProgressUpdater();
                    } else {
                        playAudio();
                        holder.primarySeekBarProgressUpdater();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                mp.seekTo(length);
                mp.start();
                if (mp.equals(duration1)) {
                    try {
                        playAudio();
                        playAudio1();
                        holder.primarySeekBarProgressUpdater();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        holder.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivPlay.setVisibility(v.VISIBLE);
                holder.ivPause.setVisibility(v.GONE);
                mp.pause();
                length = mp.getCurrentPosition();
                holder.melodySlider.setProgress(0);
            }
        });


    }

    @Override
    public int getItemCount() {
        //  rvLength = instrumentList.size();
        return instrumentList.size();
    }


    @Override
    public int getItemViewType(int position) {
//        return (position == instrumentList.size()) ? R.layout.layout_button_sync : R.layout.card_melody_added;

        if (instrumentList.get(position).isFooter())
            return VIEW_TYPES.Footer;
        else
            return VIEW_TYPES.Normal;

    }


    public void playAudio1() throws IOException {
//        killMediaPlayer();
        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/InstaMelody.mp3";
        mp = new MediaPlayer();
        mp.setDataSource(audioFilePath);
//        mp.setDataSource(instrumentFile);
        mp.prepare();
        mp.start();
        duration1 = mp.getDuration();
        currentPosition = mp.getCurrentPosition();

    }

    public void playAudio() throws IOException {
//            killMediaPlayer();
        mp = new MediaPlayer();
//        mp.setDataSource(audioFilePath);
        mp.setDataSource(instrumentFile);
        mp.prepare();
        mp.start();
        duration1 = mp.getDuration();
        currentPosition = mp.getCurrentPosition();
    }

    private void killMediaPlayer() {
        if (mp != null) {
            try {
                mp.release();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }
}
