package com.instamelody.instamelody.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    private ArrayList<RecordingsModel> recordingList = new ArrayList<>();

    Context context;

    public RecordingsCardAdapter(Context context, ArrayList<RecordingsModel> recordingList) {
        this.recordingList = recordingList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
        TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivRecordingCover;
        ImageView ivJoin;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            this.ivRecordingCover = (ImageView) itemView.findViewById(R.id.ivRecordingCover);
            this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            this.tvRecordingName = (TextView) itemView.findViewById(R.id.tvRecordingName);
            this.tvRecordingDate = (TextView) itemView.findViewById(R.id.tvRecordingDate);
            this.tvRecordingGenres = (TextView) itemView.findViewById(R.id.tvRecordingGenres);
            this.tvContributeDate = (TextView) itemView.findViewById(R.id.tvContributeDate);
            this.tvContributeLength = (TextView) itemView.findViewById(R.id.tvContributeLength);
            this.tvIncludedCount = (TextView) itemView.findViewById(R.id.tvIncludedCount);
            this.tvViewCount = (TextView) itemView.findViewById(R.id.tvViewCount);
            this.tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            this.tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
            this.tvShareCount = (TextView) itemView.findViewById(R.id.tvShareCount);
            this.ivJoin = (ImageView) itemView.findViewById(R.id.ivJoin);

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
        }
    }

    public RecordingsCardAdapter(ArrayList<RecordingsModel> recordingList) {
        this.recordingList = recordingList;
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
        TextView tvRecordingGenres = holder.tvRecordingGenres;
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


        Picasso.with(holder.ivRecordingCover.getContext()).load(recordingList.get(listPosition).getRecordingCover()).into(holder.ivRecordingCover);
        Picasso.with(holder.userProfileImage.getContext()).load(recordingList.get(listPosition).getUserProfilePic()).into(holder.userProfileImage);
        tvRecordingGenres.setText(recordingList.get(listPosition).getGenreId());
        tvUserName.setText(recordingList.get(listPosition).getUserName());
        tvRecordingName.setText(recordingList.get(listPosition).getRecordingName());
//        tvRecordingGenres.setText(recording.getGenreName());


//        tvContributeLength.setText(recordingList.get(listPosition).getTvContributeLength());
        tvRecordingDate.setText(recordingList.get(listPosition).getRecordingCreated());
        //    tvContributeDate.setText(recordingList.get(listPosition).getTvContributeDate());
        tvViewCount.setText(String.valueOf(recordingList.get(listPosition).getPlayCount()));
        tvLikeCount.setText(String.valueOf(recordingList.get(listPosition).getLikeCount()));
        tvCommentCount.setText(String.valueOf(recordingList.get(listPosition).getCommentCount()));
        tvShareCount.setText(String.valueOf(recordingList.get(listPosition).getShareCount()));

//        tvIncludedCount.setText(String.valueOf(recordingList.get(listPosition).getTvIncludedCount()));
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }
}
