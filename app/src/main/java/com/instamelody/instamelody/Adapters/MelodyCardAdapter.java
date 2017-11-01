package com.instamelody.instamelody.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.R;

/**
 * Created by Shubhansh Jaiswal on 11/26/2016.
 * <p>
 * this has no use in app till now.
 */

public class MelodyCardAdapter extends RecyclerView.Adapter<MelodyCardAdapter.ViewHolder> {
    private static final String TAG = "MelodyCardAdapter";

    private List<MelodyCard> melodyAddedList, melodyList;
    private String[] mDataSet;
    private int[] mDataSetTypes;

    public static final int ListMelody = 0;
    public static final int ListAddedMelody = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class MelodyListViewHolder extends ViewHolder {
        public TextView tvUserName, tvMelodyName, tvMelodyLength, tvBpmRate, tvInstrumentsUsed, tvMelodyGenre, tvMelodyDate;
        public TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount;
        public CircleImageView userProfileImage;
        public ImageView melodyCover;

        public MelodyListViewHolder(View v) {
            super(v);

            this.tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            this.tvMelodyName = (TextView) v.findViewById(R.id.tvMelodyName);
            this.tvMelodyLength = (TextView) v.findViewById(R.id.tvMelodyLength);
            this.tvBpmRate = (TextView) v.findViewById(R.id.tvBpmRate);
            this.tvInstrumentsUsed = (TextView) v.findViewById(R.id.tvInstrumentsUsed);
            this.tvMelodyGenre = (TextView) v.findViewById(R.id.tvMelodyGenre);
            this.tvMelodyDate = (TextView) v.findViewById(R.id.tvMelodyDate);

            this.tvViewCount = (TextView) v.findViewById(R.id.tvViewCount);
            this.tvLikeCount = (TextView) v.findViewById(R.id.tvLikeCount);
            this.tvCommentCount = (TextView) v.findViewById(R.id.tvCommentCount);
            this.tvShareCount = (TextView) v.findViewById(R.id.tvShareCount);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        try {
            if (viewHolder.getItemViewType() == ListMelody) {
                MelodyListViewHolder holder = (MelodyListViewHolder) viewHolder;
                MelodyCard melodyCard = melodyAddedList.get(position);
                holder.tvUserName.setText("Abhishek");
                holder.tvMelodyName.setText("Dubey");
//            holder.tvMelodyLength.setText(melodyCard.getTvMelodyLength());
//            holder.tvBpmRate.setText(melodyCard.getTvBpmRate());
//            holder.tvInstrumentsUsed.setText(melodyCard.getTvInstrumentsUsed());
//            holder.tvMelodyGenre.setText(melodyCard.getTvMelodyGenre());
//            holder.tvMelodyDate.setText(melodyCard.getTvMelodyDate());
//
//            holder.tvViewCount.setText(melodyCard.getTvViewCount());
//            holder.tvLikeCount.setText(melodyCard.getTvLikeCount());
//            holder.tvCommentCount.setText(melodyCard.getTvCommentCount());
//            holder.tvShareCount.setText(melodyCard.getTvShareCount());

            } else if (viewHolder.getItemViewType() == ListAddedMelody) {
                AddedMelodyListViewHolder holder = (AddedMelodyListViewHolder) viewHolder;
                MelodyCard melodyCard = melodyAddedList.get(position);
                holder.tvUserName.setText("Abhishek");
                holder.tvMelodyName.setText("Dubey");
//            holder.tvMelodyLength.setText(melodyCard.getTvMelodyLength());
//            holder.tvBpmRate.setText(melodyCard.getTvBpmRate());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class AddedMelodyListViewHolder extends ViewHolder {
        public TextView tvUserName, tvMelodyName, tvMelodyLength, tvBpmRate;
        public CircleImageView userProfileImage;
        public ImageView melodyCover;

        public AddedMelodyListViewHolder(View v) {
            super(v);

            this.tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            this.tvMelodyName = (TextView) v.findViewById(R.id.tvMelodyName);
            this.tvMelodyLength = (TextView) v.findViewById(R.id.tvMelodyLength);
            this.tvBpmRate = (TextView) v.findViewById(R.id.tvBpmRate);
        }
    }

    public MelodyCardAdapter(String[] dataSet, int[] dataSetTypes) {
        mDataSet = dataSet;
        mDataSetTypes = dataSetTypes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == ListMelody) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_melody_list, viewGroup, false);
            return new MelodyListViewHolder(v);
        } else if (viewType == ListAddedMelody) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_melody_added, viewGroup, false);
            return new AddedMelodyListViewHolder(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_melody_list, viewGroup, false);
            return new MelodyListViewHolder(v);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSetTypes[position];
    }
}
