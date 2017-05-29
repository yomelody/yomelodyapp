package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instamelody.instamelody.Models.AudioModel;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Saurabh Singh on 4/18/2017.
 * this will be use in AudioFragment
 *
 */

public class AudioCardAdapter extends RecyclerView.Adapter<AudioCardAdapter.MyViewHolder> {

    private ArrayList<AudioModel> data = new ArrayList<>();

    Context context;

    public AudioCardAdapter(Context context, ArrayList<AudioModel> recordingList) {
        this.data = recordingList;
        this.context = context;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
        TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivRecordingCover;

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
        }
    }

    public AudioCardAdapter(ArrayList<AudioModel> recordingList) {
        this.data = recordingList;
    }

    @Override
    public AudioCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_audio, parent, false);

        AudioCardAdapter.MyViewHolder myViewHolder = new AudioCardAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final AudioCardAdapter.MyViewHolder holder, final int listPosition) {

        AudioModel recording = data.get(listPosition);
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

//        Picasso.with(holder.ivRecordingCover.getContext()).load(data.get(listPosition).getIvRecordingCover()).into(holder.ivRecordingCover);
//        Picasso.with(holder.userProfileImage.getContext()).load(data.get(listPosition).getUserProfileImage()).into(holder.userProfileImage);
        tvRecordingGenres.setText(data.get(listPosition).getTvUserName());
        tvRecordingName.setText(data.get(listPosition).getTvRecordingName());
        tvContributeLength.setText(data.get(listPosition).getTvContributeLength());
        tvRecordingDate.setText(data.get(listPosition).getTvRecordingDate());
        //    tvContributeDate.setText(recordingList.get(listPosition).getTvContributeDate());
        tvViewCount.setText(String.valueOf(data.get(listPosition).getTvViewCount()));
        tvLikeCount.setText(String.valueOf(data.get(listPosition).getTvLikeCount()));
        tvCommentCount.setText(String.valueOf(data.get(listPosition).getTvCommentCount()));
        tvShareCount.setText(String.valueOf(data.get(listPosition).getTvShareCount()));
        tvIncludedCount.setText(String.valueOf(data.get(listPosition).getTvIncludedCount()));


    }

    @Override
    public int getItemCount() {
        return 0;
    }
}


