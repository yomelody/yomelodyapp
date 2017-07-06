package com.instamelody.instamelody.Adapters;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pools;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
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
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StudioActivity;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.internal.Util;

import static android.R.attr.duration;
import static android.R.attr.resumeWhilePausing;
import static android.content.Context.MODE_PRIVATE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.R.attr.position;
import static com.instamelody.instamelody.R.id.melodySlider;
import static com.instamelody.instamelody.R.id.rlSeekbarTracer;
import static com.instamelody.instamelody.R.id.visible;

/**
 * Created by Saurabh Singh on 06/04/17
 */

public class InstrumentListAdapter extends RecyclerView.Adapter<InstrumentListAdapter.MyViewHolder> {

    static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    String audioValue;
    static String audioUrl;
    private static String audioFilePath;
    private static String instrumentFile;
    static MediaPlayer mp;
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

    public InstrumentListAdapter(ArrayList<MelodyInstruments> instrumentList, Context context) {
        this.instrumentList = instrumentList;
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
        }else
        holder.tvUserName.setText("@" + instruments.getUserName());
        holder.tvInstrumentName.setText(instruments.getInstrumentName());

        holder.tvInstrumentLength.setText(instruments.getInstrumentLength());
        instrumentFile = instruments.getInstrumentFile();
        instrument_url_count.add(instrumentFile);
//        Toast.makeText(context, "" + instrumentFile, Toast.LENGTH_SHORT).show();
        Log.d("Instruments size", "" + instrumentList.get(listPosition));
        new DownloadInstruments().execute(instrumentFile);

        //  i.putExtra("instruments", instrumentFile);


        audioValue = instruments.getAudioType();
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivPlay.setVisibility(v.GONE);
                holder.ivPause.setVisibility(v.VISIBLE);
                instruments_url.add(instrumentFile);
                instrumentFile = instruments.getInstrumentFile();
                /*for (int i = 0; i < instrument_url_count.size(); i++) {
                    Iterator iter = instrument_url_count.iterator();
                    while (iter.hasNext()) {
                        // if here          
                        Toast.makeText(context, "" + iter.next(), Toast.LENGTH_SHORT).show();
//                        Log.d("count", (String) iter.next());
                    }
                }*/


                //   Log.d("instruments_url", instrumentFile);
                instrumentName = instruments.getInstrumentName();
//                Intent i = new Intent("fetchingInstruments");
//                i.putExtra("instruments", instrumentFile);
//                LocalBroadcastManager.getInstance(context).sendBroadcast(i);


                try {
                    Integer s = listPosition +1;

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

        /*for (int j = 0; j < instrument_url_count.size(); j++) {
            Iterator iter = instrument_url_count.iterator();
            while (iter.hasNext()) {
                fetch_url_arrayList.add((String) iter.next());
                // if here
//                       iter.forEachRemaining(fetch_url_arrayList::add);
//                Toast.makeText(context, "" + iter.next(), Toast.LENGTH_SHORT).show();
//                        Log.d("count", (String) iter.next());

            }
        }*/
        Iterator iter = instrument_url_count.iterator();
        while (iter.hasNext()) {
            fetch_url_arrayList.add((String) iter.next());
        }
        Log.d("collection", "" + fetch_url_arrayList);
        Intent i = new Intent("fetchingInstruments");
        i.putStringArrayListExtra("instruments", fetch_url_arrayList);
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


    public void playAudio1() throws IOException {
//        killMediaPlayer();
        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/InstaMelody.amr";
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

    public static ArrayList<MelodyInstruments> returnInstrumentsList() {
        return instrumentList;
    }

    class DownloadInstruments extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading melody Packs ...");
            pDialog.setIndeterminate(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
//            pDialog.show();

        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... url) {
            int count;
            OutputStream output;
            try {
                for (int i = 0; i < instrument_url_count.size(); i++) {
                    //{
                    URL aurl = new URL((String) instrument_url_count.get(i));

                    URLConnection connection = aurl.openConnection();
                    connection.connect();
                    // getting file length
                    int lengthOfFile = connection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(aurl.openStream());

                    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);


                    if (isSDPresent) {
                        // yes SD-card is present
                        output = new FileOutputStream("sdcard/InstaMelody/Downloads/Melodies/" + i + ".mp3");
                    } else {
                        // Sorry
                        output = new FileOutputStream(getApplicationContext().getFilesDir() + "/InstaMelody/Downloads/Melodies/" + i + ".mp3");
                    }

                    // Output stream to write file

                    byte data[] = new byte[1024];

                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lengthOfFile));
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    //   i++;
                }
                // }


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
//            frameSync.setVisibility(View.GONE);
            // tvDone.setEnabled(true);
        }
    }
}
