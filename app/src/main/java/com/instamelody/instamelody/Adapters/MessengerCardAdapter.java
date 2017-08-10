package com.instamelody.instamelody.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instamelody.instamelody.Models.MessengerModel;
import com.instamelody.instamelody.R;

import java.util.ArrayList;

/**
 * Created by Shubhansh Jaiswal on 1/4/2017.
 * this has no use ,must be deleted later
 */

public class MessengerCardAdapter extends RecyclerView.Adapter<MessengerCardAdapter.MyViewHolder> {
    private ArrayList<MessengerModel> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMsg, tvTime, tvUserName;
        ImageView userProfileImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            this.tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
            this.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            this.userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
        }
    }

    public MessengerCardAdapter(ArrayList<MessengerModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_messenger, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView tvUserName = holder.tvUserName;
        TextView tvMsgPrecise = holder.tvMsg;
        TextView tvTime = holder.tvTime;
        ImageView userProfileImage = holder.userProfileImage;

        tvUserName.setText(dataSet.get(listPosition).getTvUserName());
        tvMsgPrecise.setText(dataSet.get(listPosition).getTvMsg());
        tvTime.setText(dataSet.get(listPosition).getTvTime());
        userProfileImage.setImageResource(dataSet.get(listPosition).getUserProfileImage());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}




