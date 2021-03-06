package com.yomelody.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yomelody.Models.Comments;
import com.yomelody.ProfileActivity;
import com.squareup.picasso.Picasso;
import com.yomelody.R;

import java.util.ArrayList;

import static com.yomelody.utils.RMethod.getServerDiffrenceDate;

/**
 * Created by Shubhansh Jaiswal on 06/04/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Comments> commentList = new ArrayList<>();

    public CommentsAdapter(Context context, ArrayList<Comments> commentList) {
        this.commentList = commentList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRealName, tvUsername, tvMsg, tvTime;
        ImageView userProfileImage;


        public MyViewHolder(final View itemView) {
            super(itemView);

            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            tvRealName = (TextView) itemView.findViewById(R.id.tvRealName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);

            userProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String showProfileUserId = commentList.get(getAdapterPosition()).getUser_id();
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    intent.putExtra("showProfileUserId", showProfileUserId);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {
        try {
            Comments comments = commentList.get(listPosition);
            Picasso.with(holder.userProfileImage.getContext()).load(comments.getUserProfileImage()).into(holder.userProfileImage);
            holder.tvRealName.setText(comments.getTvRealName());
            holder.tvUsername.setText("@" + comments.getTvUsername());
            //holder.tvTime.setText(DateTime(comments.getTvTime()));
            holder.tvTime.setText(comments.getDisplay_time());
            holder.tvMsg.setText(comments.getTvMsg());
            //DateTime(c.getString(comments.getTvTime())
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public String DateTime(String send_at) {
        String val = "";
        val = getServerDiffrenceDate(send_at);
        return val;
    }

}
