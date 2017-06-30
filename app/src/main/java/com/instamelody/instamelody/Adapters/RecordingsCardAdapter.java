package com.instamelody.instamelody.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Saurabh Singh on 12//2016.
 * <p>
 * this will be used in recordings fragment
 */

public class RecordingsCardAdapter extends RecyclerView.Adapter<RecordingsCardAdapter.MyViewHolder> {

    String genreName;
    static String instrumentFile;
    static MediaPlayer mp;
    static int duration1, currentPosition;
    int length;

    private ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    private ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();

    Context context;

    public RecordingsCardAdapter(Context context, ArrayList<RecordingsModel> recordingList, ArrayList<RecordingsPool> recordingsPools) {
        this.recordingList = recordingList;
        this.recordingsPools = recordingsPools;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
        TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivRecordingCover;
        ImageView ivJoin, ivStationPlay,ivStationPause;
        SeekBar seekBarRecordings;

        public MyViewHolder(View itemView) {
            super(itemView);

            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            ivRecordingCover = (ImageView) itemView.findViewById(R.id.ivRecordingCover);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvRecordingName = (TextView) itemView.findViewById(R.id.tvRecordingName);
            tvRecordingDate = (TextView) itemView.findViewById(R.id.tvRecordingDate);
            tvRecordingGenres = (TextView) itemView.findViewById(R.id.tvRecordingGenres);
            tvContributeDate = (TextView) itemView.findViewById(R.id.tvContributeDate);
            tvContributeLength = (TextView) itemView.findViewById(R.id.tvContributeLength);
            tvIncludedCount = (TextView) itemView.findViewById(R.id.tvIncludedCount);
            tvViewCount = (TextView) itemView.findViewById(R.id.tvViewCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
            tvShareCount = (TextView) itemView.findViewById(R.id.tvShareCount);
            ivJoin = (ImageView) itemView.findViewById(R.id.ivJoin);
            ivStationPlay = (ImageView) itemView.findViewById(R.id.ivStationPlay);
            ivStationPause = (ImageView) itemView.findViewById(R.id.ivStationPause);
            seekBarRecordings = (SeekBar) itemView.findViewById(R.id.seekBarRecordings);

            SharedPreferences editorGenre = getApplicationContext().getSharedPreferences("prefGenreName", MODE_PRIVATE);
            genreName = editorGenre.getString("GenreName", null);

            ivJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String instruments, bpm, genre, recordName, userName, duration, date, plays, likes, comments, shares, melodyID;

                    genre = tvRecordingGenres.getText().toString().trim();
                    recordName = tvRecordingName.getText().toString().trim();
                    duration = tvContributeLength.getText().toString().trim();
                    date = tvRecordingDate.getText().toString().trim();
                    plays = tvViewCount.getText().toString().trim();
//                    tvIncludedCount.getText().toString().trim();
//                    tvContributeDate.getText().toString().trim();
                    userName = tvUserName.getText().toString().trim();
                    likes = tvLikeCount.getText().toString().trim();
                    comments = tvCommentCount.getText().toString().trim();
                    shares = tvShareCount.getText().toString().trim();

//                    duration = tvMelodyLength.getText().toString().trim();
//                    date = tvMelodyDate.getText().toString().trim();
//                    plays = tvPlayCount.getText().toString().trim();
//                    instruments = tvInstrumentsUsed.getText().toString().trim();
//                    bpm = tvBpmRate.getText().toString().trim();
//                    genre = tvMelodyGenre.getText().toString().trim();
//                    melodyName = tvMelodyName.getText().toString().trim();

                    int pos = getAdapterPosition();
//                    melodyID = mpids.get(pos);

                    SharedPreferences.Editor editor = context.getSharedPreferences("commentData", MODE_PRIVATE).edit();


                    editor.putString("genre", genre);
                    editor.putString("melodyName", recordName);
                    editor.putString("userName", userName);
                    editor.putString("duration", duration);
                    editor.putString("date", date);
                    editor.putString("plays", plays);
                    editor.putString("likes", likes);
                    editor.putString("comments", comments);
                    editor.putString("shares", shares);
//                    editor.putString("bitmapProfile", profile);
//                    editor.putString("bitmapCover", cover);
//                    editor.putString("melodyID", );
                    editor.putString("fileType", "admin_melody");
                    editor.commit();

                    Intent intent = new Intent(context, JoinActivity.class);
                    context.startActivity(intent);
                }
            });


            seekBarRecordings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    int mCurrentPosition = currentPosition / 1000;
                    int mDuration = duration1 / 1000;
                    UtilsRecording utilRecording = new UtilsRecording();
                    int progress1 = utilRecording.getProgressPercentage(mCurrentPosition, mDuration);

                    if (mp != null && fromUser) {
                        int playPositionInMilliseconds = duration1 / 100 * seekBarRecordings.getProgress();
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
            seekBarRecordings.setProgress((int) (((float) mp.getCurrentPosition() / duration1) * 100));// This math construction give a percentage of "was playing"/"song length"
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
    public RecordingsCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recordings, parent, false);

        RecordingsCardAdapter.MyViewHolder myViewHolder = new RecordingsCardAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecordingsCardAdapter.MyViewHolder holder, final int listPosition) {

        RecordingsModel recording = recordingList.get(listPosition);
        /*final RecordingsPool recordingsPool = recordingsPools.get(listPosition);
        instrumentFile = recordingsPool.getRecordingUrl();*/


        final TextView tvRecordingGenres = holder.tvRecordingGenres;
        TextView tvUserName = holder.tvUserName;
        TextView tvRecordingName = holder.tvRecordingName;
        TextView tvContributeLength = holder.tvContributeLength;
        TextView tvRecordingDate = holder.tvRecordingDate;
        TextView tvViewCount = holder.tvViewCount;
        TextView tvLikeCount = holder.tvLikeCount;
        TextView tvCommentCount = holder.tvCommentCount;
        TextView tvShareCount = holder.tvShareCount;
        TextView tvIncludedCount = holder.tvIncludedCount;
        TextView tvContributeDate = holder.tvContributeDate;

//        if (recording.getRecordingCover().equals("")) {
////            Picasso.with(holder.ivRecordingCover.getContext()).load(R.drawable.cover3).into(holder.ivRecordingCover);
//        } else {
//            Picasso.with(holder.ivRecordingCover.getContext()).load(recording.getRecordingCover()).into(new Target() {
//
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    holder.ivRecordingCover.setBackground(new BitmapDrawable(context.getResources(), bitmap));
//                }
//
//                @Override
//                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
//                }
//
//                @Override
//                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
//                }
//            });
//        }
//        if (recording.getRecordingCover().equals("")) {
////            Picasso.with(holder.userProfileImage.getContext()).load(R.drawable.artist).into(holder.userProfileImage);
//        } else {
//            Picasso.with(holder.userProfileImage.getContext()).load(recording.getRecordingCover()).into(new Target() {
//
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    holder.userProfileImage.setBackground(new BitmapDrawable(context.getResources(), bitmap));
//                }
//
//                @Override
//                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
//                }
//
//                @Override
//                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
//                }
//            });
//        }


        Picasso.with(holder.ivRecordingCover.getContext()).load(recordingsPools.get(listPosition).getCoverUrl()).into(holder.ivRecordingCover);
//        Picasso.with(holder.userProfileImage.getContext()).load(recordingsPools.get(listPosition).getProfileUrl()).into(holder.userProfileImage);
        Picasso.with(holder.userProfileImage.getContext()).load(recordingList.get(listPosition).getUserProfilePic()).into(holder.userProfileImage);
//        tvRecordingGenres.setText(recordingList.get(listPosition).getGenreName());
        holder.tvUserName.setText(recordingList.get(listPosition).getUserName());
        holder.tvRecordingName.setText(recordingList.get(listPosition).getRecordingName());
        holder.tvRecordingGenres.setText("Genre:" + " " + recording.getGenreName());
        holder.tvContributeLength.setText(recordingsPools.get(listPosition).getDuration());
        holder.tvContributeDate.setText(recordingsPools.get(listPosition).getDateAdded());


//        tvContributeLength.setText(recordingList.get(listPosition).getTvContributeLength());
        holder.tvRecordingDate.setText(recordingList.get(listPosition).getRecordingCreated());
        //    tvContributeDate.setText(recordingList.get(listPosition).getTvContributeDate());
        holder.tvViewCount.setText(String.valueOf(recordingList.get(listPosition).getPlayCount()));
        holder.tvLikeCount.setText(String.valueOf(recordingList.get(listPosition).getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(recordingList.get(listPosition).getCommentCount()));
        holder.tvShareCount.setText(String.valueOf(recordingList.get(listPosition).getShareCount()));
        holder.ivStationPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.seekBarRecordings.setVisibility(View.VISIBLE);
                holder.ivStationPause.setVisibility(View.VISIBLE);
                try {
                    RecordingsPool recordingsPool = recordingsPools.get(listPosition);
                    instrumentFile = recordingsPool.getRecordingUrl();
                    Integer s = listPosition + 1;

                    if (getItemCount() > s && instrumentFile != null) {
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
                        holder.primarySeekBarProgressUpdater();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.ivStationPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivStationPlay.setVisibility(v.VISIBLE);
                holder.ivStationPause.setVisibility(v.GONE);
                mp.pause();
                length = mp.getCurrentPosition();
                holder.seekBarRecordings.setProgress(0);
            }
        });


//        holder.tvIncludedCount.setText(String.valueOf(recordingList.get(listPosition).getTvIncludedCount()));
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
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
}
