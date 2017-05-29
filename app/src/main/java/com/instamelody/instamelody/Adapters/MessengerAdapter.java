package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.Models.Chat;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shubhansh Jaiswal on 04/05/17.
 */

public class MessengerAdapter extends RecyclerView.Adapter<MessengerAdapter.MyViewHolder> {

    ArrayList<Chat> chatList = new ArrayList<>();
    Context context;
    String receiverId = "";

    public MessengerAdapter(ArrayList<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMsg, tvTime, tvUserName;
        ImageView userProfileImage;
        RelativeLayout rlComplete;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            rlComplete = (RelativeLayout) itemView.findViewById(R.id.rlComplete);

            rlComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    receiverId = chatList.get(getAdapterPosition()).getReceiverID();
                    SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                    editor.putString("receiverId", receiverId);
                    editor.commit();

//                    editor.putString("receiverId", chatList.get(getAdapterPosition()).getReceiverID());
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_messenger, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {

        Chat chat = chatList.get(listPosition);
        holder.tvUserName.setText(chat.getReceiverName());
        holder.tvMsg.setText(chat.getMessage());
        holder.tvTime.setText(chat.getSendAt());
        Picasso.with(holder.userProfileImage.getContext()).load(chat.getUserProfileImage()).into(holder.userProfileImage);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
