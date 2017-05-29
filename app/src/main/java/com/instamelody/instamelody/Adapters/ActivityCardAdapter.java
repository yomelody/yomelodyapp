package com.instamelody.instamelody.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.instamelody.instamelody.Models.ActivityModel;
import com.instamelody.instamelody.R;

import java.util.ArrayList;

/**
 * Created by Saurabh Singh on 12/21/2016.
 */

public class ActivityCardAdapter extends RecyclerView.Adapter<ActivityCardAdapter.MyViewHolder>{
    private ArrayList<ActivityModel> dataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvmsg,tvtopic,tvtime;
        ImageView userprofileimage;
        CardView mCardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.userprofileimage = (ImageView)itemView.findViewById(R.id.userProfileImage);
            this.tvmsg = (TextView)itemView.findViewById(R.id.msg);
            this.tvtopic = (TextView)itemView.findViewById(R.id.topic);
            this.tvtime = (TextView)itemView.findViewById(R.id.time);
            this.mCardView = (CardView)itemView.findViewById(R.id.card_activity);
        }
    }


    public ActivityCardAdapter(ArrayList<ActivityModel> data1){this.dataset = data1;}

    @Override
    public ActivityCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_activity, parent, false);

        ActivityCardAdapter.MyViewHolder myViewHolder1 = new ActivityCardAdapter.MyViewHolder(view);
        return myViewHolder1;
    }
    @Override
    public void onBindViewHolder(final ActivityCardAdapter.MyViewHolder holder, final int listPosition){
        TextView tvmsg = holder.tvmsg;
        TextView tvtopic = holder.tvtopic;
        TextView tvtime = holder.tvtime;
        ImageView userprofileimage = holder.userprofileimage;
        userprofileimage.setImageResource(R.drawable.profile1);

        tvmsg.setText(dataset.get(listPosition).getTvmsg());
        userprofileimage.setImageResource(dataset.get(listPosition).getUserProfileImage());
        tvmsg.setText(dataset.get(listPosition).getTvmsg());
        tvtopic.setText(dataset.get(listPosition).gettvtopic());
        tvtime.setText(dataset.get(listPosition).getTvtime());
    }
    @Override
    public int getItemCount(){return dataset.size();}
}
