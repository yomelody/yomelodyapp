package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shubhansh Jaiswal on 17/01/17.
 * This adapter class identifies the current logged in user messages by user id and align the messages left or right by inflating two different xml layouts.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    Context context;
    ArrayList<Message> chatList = new ArrayList<>();

    private static String TAG = ChatAdapter.class.getSimpleName();
    private String userId;
    int flag = 0;
    private int SELF = 100;
    private int SELF_AUDIO = 101;
    private int OTHER = 102;
    private int OTHER_AUDIO = 103;

    public ChatAdapter(Context context, ArrayList<Message> chatList) {
        this.chatList = chatList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chatMessage, timeStamp;
        ImageView userProfileImage, chatImage;
        RelativeLayout rlChatImage, rlBelowImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            chatMessage = (TextView) itemView.findViewById(R.id.chatMessage);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            chatImage = (ImageView) itemView.findViewById(R.id.chatImage);
            rlChatImage = (RelativeLayout) itemView.findViewById(R.id.rlChatImage);
            rlBelowImage = (RelativeLayout) itemView.findViewById(R.id.rlBelowImage);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SELF) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_self, parent, false);
        } else if (viewType == SELF_AUDIO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_recording_self, parent, false);
        } else if (viewType == OTHER_AUDIO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_recording_other, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_other, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {

        Message message = chatList.get(position);
        SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = context.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = context.getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
            flag = 1;
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
            flag = 2;
        }
        if (message.getSenderId().equals(userId)) {

//            if (isAudio.equals("True")) {
//                return SELF_AUDIO;
//            } else {
            return SELF;
//            }
        } else {
//            if (isAudio.equals("True")) {
//                return OTHER_AUDIO;
//            }
        }
        return position;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final int itemType = getItemViewType(position);
        if (itemType == SELF_AUDIO || itemType == OTHER_AUDIO) {
            Message message = chatList.get(position);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).into(holder.userProfileImage);
            holder.timeStamp.setText(message.getCreatedAt());
        } else {
            Message message = chatList.get(position);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).into(holder.userProfileImage);
            if(!message.getFile().equals("")){
                holder.rlChatImage.setVisibility(View.VISIBLE);
                Picasso.with(holder.chatImage.getContext()).load(message.getFile()).into(holder.chatImage);
//                holder.rlBelowImage.setVisibility(View.VISIBLE);
            }
            holder.chatMessage.setText(message.getMessage());
            holder.timeStamp.setText(message.getCreatedAt());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}




