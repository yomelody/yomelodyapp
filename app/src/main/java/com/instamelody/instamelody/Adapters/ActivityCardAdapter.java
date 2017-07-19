package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.instamelody.instamelody.Models.ActivityModel;
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Saurabh Singh on 12/21/2016.
 */

public class ActivityCardAdapter extends RecyclerView.Adapter<ActivityCardAdapter.MyViewHolder> {

    private ArrayList<ActivityModel> activityList = new ArrayList<>();
    Context context;

    public ActivityCardAdapter(ArrayList<ActivityModel> activityList, Context context) {
        this.activityList = activityList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvmsg, tvtopic, tvtime;
        ImageView userprofileimage;
        CardView mCardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.userprofileimage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            this.tvmsg = (TextView) itemView.findViewById(R.id.msg);
            this.tvtopic = (TextView) itemView.findViewById(R.id.topic);
            this.tvtime = (TextView) itemView.findViewById(R.id.time);
            this.mCardView = (CardView) itemView.findViewById(R.id.card_activity);

            userprofileimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String showProfileUserId = activityList.get(getAdapterPosition()).getCreatedByUserId();
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    intent.putExtra("showProfileUserId", showProfileUserId);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }


    public ActivityCardAdapter(ArrayList<ActivityModel> data1) {
        this.activityList = data1;
    }

    @Override
    public ActivityCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_activity, parent, false);

        ActivityCardAdapter.MyViewHolder myViewHolder1 = new ActivityCardAdapter.MyViewHolder(view);
        return myViewHolder1;
    }

    @Override
    public void onBindViewHolder(final ActivityCardAdapter.MyViewHolder holder, final int listPosition) {
        TextView tvmsg = holder.tvmsg;
        TextView tvtopic = holder.tvtopic;
        TextView tvtime = holder.tvtime;
        ImageView userprofileimage = holder.userprofileimage;
        userprofileimage.setImageResource(R.drawable.profile1);

        ActivityModel am = activityList.get(listPosition);
        //if(am.getUserImgURL()=="null")

      //  Picasso.with(holder.userprofileimage.getContext()).load(am.getUserImgURL()).into(holder.userprofileimage);

        //activityList.get(listPosition).getUserImgURL();

        tvmsg.setText(activityList.get(listPosition).getTvmsg());
//        userprofileimage.setImageResource(activityList.get(listPosition).getUserImgURL());
//        Picasso.with(context.getUserImgURL).load(activityList.getString("profile_pick")).into(context.userProfileImage);
//        c.getString("activity_name"),
        tvmsg.setText(activityList.get(listPosition).getTvmsg());
        tvtopic.setText(activityList.get(listPosition).gettvtopic());
        tvtime.setText(activityList.get(listPosition).getTvtime());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
}
