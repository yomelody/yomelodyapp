package com.instamelody.instamelody.Adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
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

import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.ModelPlayAllMediaPlayer;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StudioActivity;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Abhishek Dubey on 23/08/17
 */

public class JoinInstrumentListAdp extends RecyclerView.Adapter<JoinInstrumentListAdp.MyViewHolder> {

    static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    static ArrayList<JoinedArtists> joinArtistList = new ArrayList<>();
    String audioValue;
    private static String instrumentFile;
    int length;
    String coverPicStudio;
    int statusFb, statusTwitter;
    String userName, profilePic;
    String fbName, fbUserName, fbId;
    String instrumentName, melodyName;
    Context context;
    public static ArrayList<String> instruments_url = new ArrayList<String>();
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    ArrayList instrument_url_count = new ArrayList();
    ArrayList<String> fetch_url_arrayList = new ArrayList<>();
    static int duration1, currentPosition;
    public static List<MediaPlayer> mp_start = new ArrayList<MediaPlayer>();
    public static int count = 0;
    int InstrumentCountSize = 0;
    // ArrayList<ViewHolder> lstViewHolder = new ArrayList<ViewHolder>();
    ViewHolder viewHolder;
    ImageView holderPlay, holderPause;
    SeekBar seekBar;
    int Compdurations = 0, tmpduration = 0, MaxMpSessionID;

    public JoinInstrumentListAdp(ArrayList<MelodyInstruments> instrumentList, Context context) {
        this.instrumentList = instrumentList;
        this.context = context;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfileImage, ivInstrumentCover, ivPlay, ivPause;
        TextView tvInstrumentName, tvUserName, tvInstrumentLength, tvBpmRate, tvSync, tvDoneFxEq, tvFxButton, tvEqButton;
        SeekBar melodySlider;

        ProgressDialog progressDialog;
        public MediaPlayer mp;

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
            tvInstrumentLength = (TextView) itemView.findViewById(R.id.tvInstrumentLength);
            tvInstrumentName = (TextView) itemView.findViewById(R.id.tvInstrumentName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            count = getItemCount();

//            JoinActivity.melody_detail.setText(count + " " + "Instrumentals");
//
//            if (count == 0) {
//                Toast.makeText(context, "No Instrument", Toast.LENGTH_SHORT).show();
//            }
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
                    try {
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
                    }catch (Exception ex){
                        ex.printStackTrace();
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
            try {
                melodySlider.setProgress((int) (((float) mp.getCurrentPosition() / mp.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (mp.isPlaying()) {
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


    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_added, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;


    }

    public static class ViewHolder {
        SeekBar melodySlider;
        ImageView ivPlay, ivPause;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        try {
            final MelodyInstruments instruments = instrumentList.get(listPosition);
            viewHolder = new ViewHolder();
            viewHolder.melodySlider = (SeekBar) holder.melodySlider.findViewById(R.id.melodySlider);
            viewHolder.ivPlay = (ImageView) holder.ivPlay.findViewById(R.id.ivPlay);
            viewHolder.ivPause = (ImageView) holder.ivPause.findViewById(R.id.ivPause);

            if (JoinActivity.lstViewHolder.size() < getItemCount()) {
                JoinActivity.lstViewHolder.add(viewHolder);
            }


            if (InstrumentCountSize == 0) {
                InstrumentCountSize = MelodyInstruments.getInstrumentCount();
            }

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
            } else{
                holder.tvUserName.setText(instruments.getUserName());
            }

            holder.tvInstrumentName.setText(instruments.getInstrumentName());

            holder.tvInstrumentLength.setText(CalInstLen(instruments.getInstrumentLength()));
            instrumentFile = instruments.getInstrumentFile();
            instrument_url_count.add(instrumentFile);
//        Toast.makeText(context, "" + instrumentFile, Toast.LENGTH_SHORT).show();
//            Log.d("Instruments size", "" + instrumentFile);
            AppHelper.sop("tvUserName="+userName+"=fbName=="+fbName+"=instruments="+instruments.getUserName());

            //StudioActivity.list.add(listPosition, new MixingData(String.valueOf(instruments.getInstrumentId()), String.valueOf(Volume), String.valueOf(Base), String.valueOf(Treble), String.valueOf(Pan), String.valueOf(Pitch), String.valueOf(Reverb), String.valueOf(Compression), String.valueOf(Delay), String.valueOf(Tempo),String.valueOf(threshold),String.valueOf(ratio),String.valueOf(attack),String.valueOf(release),String.valueOf(makeup),String.valueOf(knee),String.valueOf(mix),InstaURL,PositionId));


            audioValue = instruments.getAudioType();


            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JoinActivity.playAll.setEnabled(false);
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
                    JoinActivity.playAll.setEnabled(true);
                    holder.ivPlay.setVisibility(v.VISIBLE);
                    holder.ivPause.setVisibility(v.GONE);
                    holder.mp.pause();
                    length = holder.mp.getCurrentPosition();
                    holder.melodySlider.setProgress(0);
                }
            });

            JoinActivity.playAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (holder.mp.isPlaying()) {
                            JoinActivity.playAll.setEnabled(false);
                        } else {
                            JoinActivity.playAll.setEnabled(true);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    holder.ivPlay.setEnabled(false);
                    holder.ivPause.setEnabled(false);
                    holder.melodySlider.setEnabled(false);
                    JoinActivity.ivJoinPlay.setEnabled(false);
                    JoinActivity.ivJoinPause.setEnabled(false);
                    JoinActivity.ivPlayNext.setEnabled(false);
                    JoinActivity.ivPlayPre.setEnabled(false);
                    new PrepareInstrumentsForPlayAll().execute();
                }
            });

            JoinActivity.pauseAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //   JoinActivity.playAll.setVisibility(View.VISIBLE);
                    // JoinActivity.pauseAll.setVisibility(View.GONE);
                    // Pause button code ...
                    for (MediaPlayer mp : JoinActivity.mediaPlayersAll) {
                        try {
                            mp.stop();
                            mp.reset();
                            mp.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    for (int i = 0; i <= JoinActivity.mediaPlayersAll.size() - 1; i++) {
                        try {
                            holderPlay = JoinActivity.lstViewHolder.get(i).ivPlay;
                            holderPause = JoinActivity.lstViewHolder.get(i).ivPause;
                            seekBar = JoinActivity.lstViewHolder.get(i).melodySlider;
                            holderPlay.setVisibility(View.VISIBLE);
                            holderPause.setVisibility(View.GONE);
                            JoinActivity.playAll.setVisibility(View.VISIBLE);
                            JoinActivity.pauseAll.setVisibility(View.GONE);
                            seekBar.setProgress(0);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                    JoinActivity.mediaPlayersAll.clear();
                    holder.ivPlay.setEnabled(true);
                    holder.ivPause.setEnabled(true);
                    holder.melodySlider.setEnabled(true);
                    JoinActivity.ivJoinPlay.setEnabled(true);
                    JoinActivity.ivJoinPause.setEnabled(true);
                    JoinActivity.ivPlayNext.setEnabled(true);
                    JoinActivity.ivPlayPre.setEnabled(true);

                }
            });


            Intent i = new Intent("fetchingInstrumentsJoin");
            i.putStringArrayListExtra("instrumentsJoin", instrument_url_count);
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private class PrepareInstrumentsForPlayAll extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            try {
                try {
                    JoinActivity.frameProgress.setVisibility(View.VISIBLE);
                    JoinActivity.playAll.setVisibility(View.GONE);
                    JoinActivity.pauseAll.setVisibility(View.VISIBLE);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        protected Bitmap doInBackground(String... urls) {

            for (int i = 0; i < InstrumentCountSize; i++) {
                JoinActivity.mpall = new MediaPlayer();
                JoinActivity.mpall.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    JoinActivity.mpall.setDataSource(instrumentList.get(i).getInstrumentFile());
                    JoinActivity.mpall.prepare();
                    JoinActivity.mediaPlayersAll.add(JoinActivity.mpall);

                    Compdurations = JoinActivity.mediaPlayersAll.get(i).getDuration();
                    if (Compdurations > tmpduration) {
                        tmpduration = Compdurations;
                        MaxMpSessionID = JoinActivity.mediaPlayersAll.get(i).getAudioSessionId();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            JoinActivity.mpall.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (MaxMpSessionID == mediaPlayer.getAudioSessionId()) {
                        JoinActivity.handler.removeCallbacksAndMessages(null);
                        for (int i = 0; i <= JoinActivity.mediaPlayersAll.size() - 1; i++) {
                            try {
                                holderPlay = JoinActivity.lstViewHolder.get(i).ivPlay;
                                holderPause = JoinActivity.lstViewHolder.get(i).ivPause;
                                seekBar = JoinActivity.lstViewHolder.get(i).melodySlider;
                                holderPlay.setVisibility(View.VISIBLE);
                                holderPause.setVisibility(View.GONE);
                                JoinActivity.playAll.setVisibility(View.VISIBLE);
                                JoinActivity.pauseAll.setVisibility(View.GONE);
                                seekBar.setProgress(0);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                        JoinActivity.mediaPlayersAll.clear();
                        JoinActivity.ivJoinPlay.setEnabled(true);
                        JoinActivity.ivJoinPause.setEnabled(true);
                        JoinActivity.ivPlayNext.setEnabled(true);
                        JoinActivity.ivPlayPre.setEnabled(true);
                    }

                }
            });

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            JoinActivity.frameProgress.setVisibility(View.GONE);
            for (int i = 0; i <= JoinActivity.mediaPlayersAll.size() - 1; i++) {
                try {
                    JoinActivity.mediaPlayersAll.get(i).start();

                    final ImageView holderPlay = JoinActivity.lstViewHolder.get(i).ivPlay;
                    final ImageView holderPause = JoinActivity.lstViewHolder.get(i).ivPause;

                    holderPlay.setVisibility(View.GONE);
                    holderPause.setVisibility(View.VISIBLE);
                    holderPause.setEnabled(false);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
            RunSeekbar();

        }

    }

    public void RunSeekbar() {
        try {
            for (int i = 0; i <= JoinActivity.mediaPlayersAll.size() - 1; i++) {
                final MediaPlayer pts;
                pts = JoinActivity.mediaPlayersAll.get(i);


                final SeekBar seekBar = JoinActivity.lstViewHolder.get(i).melodySlider;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int currentPosition = pts.getCurrentPosition() / 1000;
                            int duration = pts.getDuration() / 1000;
                            int progress = (currentPosition * 100) / duration;

                            //seekBar.setProgress((int) (((float) pts.getCurrentPosition() / pts.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                            seekBar.setProgress(progress);

                            JoinActivity.handler.postDelayed(this, 1000);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                };
                JoinActivity.handler.postDelayed(runnable, 1000);
            }


        } catch (Throwable ex) {
            ex.printStackTrace();
        }
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


    }

    public static ArrayList<JoinedArtists> returnJoinList() {
        return joinArtistList;
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


    }


}
