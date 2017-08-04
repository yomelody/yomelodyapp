package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.instamelody.instamelody.CommentsActivity;
import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.UserMelodyCard;
import com.instamelody.instamelody.Models.UserMelodyPlay;
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by CBPC 41 on 7/28/2017.
 */

public class UserMelodyAdapter extends RecyclerView.Adapter<UserMelodyAdapter.MyViewHolder> {

    ArrayList<UserMelodyCard> userMelodyList = new ArrayList<>();
    ArrayList<UserMelodyPlay> melodyPools = new ArrayList<>();
    String melodyFile;
    MediaPlayer mp;
    int duration1, currentPosition;
    Context context;



    public UserMelodyAdapter(ArrayList<UserMelodyCard> userMelodyList,  ArrayList<UserMelodyPlay> melodyPools ,Context context){
        this.userMelodyList = userMelodyList;
        this.melodyPools = melodyPools;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
        TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivRecordingCover, ivLikeButton, ivCommentButton, ivShareButton, ivDislikeButton;
        ImageView ivJoin, ivStationPlay, ivStationPause;
        SeekBar seekBarRecordings;
        RelativeLayout rlProfilePic, rlLike;

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
            rlProfilePic = (RelativeLayout) itemView.findViewById(R.id.rlProfilePic);
            ivLikeButton = (ImageView) itemView.findViewById(R.id.ivLikeButton);
            ivDislikeButton = (ImageView) itemView.findViewById(R.id.ivDislikeButton);
            ivCommentButton = (ImageView) itemView.findViewById(R.id.ivCommentButton);
            ivShareButton = (ImageView) itemView.findViewById(R.id.ivShareButton);
            rlLike = (RelativeLayout) itemView.findViewById(R.id.rlLike);


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
            duration1 = mp.getDuration();
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
    public UserMelodyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recordings, parent, false);

        UserMelodyAdapter.MyViewHolder myViewHolder = new UserMelodyAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final UserMelodyAdapter.MyViewHolder holder, int position) {
        UserMelodyCard userMelodyCard = userMelodyList.get(position);
        Picasso.with(holder.ivRecordingCover.getContext()).load(userMelodyCard.getCover_url()).into(holder.ivRecordingCover);
        Picasso.with(holder.userProfileImage.getContext()).load(userMelodyCard.getProfile_url()).into(holder.ivRecordingCover);
        holder.tvUserName.setText(userMelodyCard.getUsername());
        holder.tvRecordingName.setText(userMelodyCard.getName());

        UserMelodyPlay userMelodyPlay = melodyPools.get(position);
        melodyFile = userMelodyPlay.getMelodyurl();


        holder.ivStationPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    playAudio();
                    holder.primarySeekBarProgressUpdater();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void playAudio() throws IOException {


        mp = new MediaPlayer();
        mp.setDataSource(melodyFile);
        mp.prepare();
        mp.start();
        duration1 = mp.getDuration();
        currentPosition = mp.getCurrentPosition();
    }
}
