package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instamelody.instamelody.CommentsActivity;
import com.instamelody.instamelody.Models.Comments;
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Shubhansh Jaiswal on 06/04/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Comments> commentList = new ArrayList<>();

    public CommentsAdapter(Context context, ArrayList<Comments> commentList ) {
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

        Comments comments = commentList.get(listPosition);
        Picasso.with(holder.userProfileImage.getContext()).load(comments.getUserProfileImage()).into(holder.userProfileImage);
        holder.tvRealName.setText(comments.getTvRealName());
        holder.tvUsername.setText("@"+comments.getTvUsername());
        holder.tvTime.setText(comments.getTvTime());
        holder.tvMsg.setText(comments.getTvMsg());

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


}
