package com.yomelody.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.yomelody.Models.ActivityModel;
import com.yomelody.ProfileActivity;
import com.squareup.picasso.Picasso;
import com.yomelody.R;
import com.yomelody.StationCommentActivity;

import java.util.ArrayList;

/**
 * Created by Saurabh Singh on 12/21/2016.
 */

public class ActivityCardAdapter extends RecyclerView.Adapter<ActivityCardAdapter.MyViewHolder> {

    private ArrayList<ActivityModel> activityList = new ArrayList<>();
    Context context;
    public static final int REQUEST_USER_ACTIVITY = 11;

    public ActivityCardAdapter(ArrayList<ActivityModel> activityList, Context context) {
        this.activityList = activityList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvmsg, tvtopic, tvtime,tvfFirst,tvSecond;
        ImageView userprofileimage;
        CardView mCardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.userprofileimage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            this.tvmsg = (TextView) itemView.findViewById(R.id.msg);
            this.tvtopic = (TextView) itemView.findViewById(R.id.topic);
            this.tvtime = (TextView) itemView.findViewById(R.id.time);
            this.tvfFirst = (TextView) itemView.findViewById(R.id.msgFrom);
            this.tvSecond = (TextView) itemView.findViewById(R.id.msgTo);
            this.mCardView = (CardView) itemView.findViewById(R.id.card_activity);

            userprofileimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String showProfileUserId = activityList.get(getAdapterPosition()).getCreatedByUserId();
                        Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                        intent.putExtra("showProfileUserId", showProfileUserId);
                        view.getContext().startActivity(intent);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
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
        try {
            TextView tvmsg = holder.tvmsg;
            TextView tvtopic = holder.tvtopic;
            TextView tvtime = holder.tvtime;
            TextView tvfrom = holder.tvfFirst;
            TextView tvsecond = holder.tvSecond;
            ImageView userprofileimage = holder.userprofileimage;
            userprofileimage.setImageResource(R.drawable.profile1);

            ActivityModel am = activityList.get(listPosition);
            String url = activityList.get(listPosition).getUserImgURL();
            if (url.equals(null) || url.equals("")) {

            } else {
                Picasso.with(holder.userprofileimage.getContext()).load(am.getUserImgURL()).into(holder.userprofileimage);
            }
            tvmsg.setText(activityList.get(listPosition).getTvmsg());
            tvtopic.setText(activityList.get(listPosition).gettvtopic());
            tvtime.setText(activityList.get(listPosition).getTvtime());
            tvfrom.setText(activityList.get(listPosition).getMsgsecond_user());
            tvsecond.setText(activityList.get(listPosition).getMsgfirst_user());
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StationCommentActivity.class);
                    intent.putExtra("Activity_Model",activityList.get(listPosition));
                    ((Activity)context).startActivityForResult(intent,REQUEST_USER_ACTIVITY);
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
}
