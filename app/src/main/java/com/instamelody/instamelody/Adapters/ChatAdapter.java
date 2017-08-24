package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instamelody.instamelody.Models.AudioDetails;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.SharedAudios;
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
    private ArrayList<Message> chatList = new ArrayList<>();
    private ArrayList<AudioDetails> audioDetailsList = new ArrayList<>();
    private ArrayList<SharedAudios> sharedAudioList = new ArrayList<>();

    private String userId;
    private int SELF = 100;
    private int SELF_IMAGE = 101;
    private int SELF_AUDIO = 102;
    private int OTHER = 103;
    private int OTHER_IMAGE = 104;
    private int OTHER_AUDIO = 105;
    String playingAudio = "1";

//    public ChatAdapter(Context context, ArrayList<Message> chatList) {
//        this.chatList = chatList;
//        this.context = context;
//    }

    public ChatAdapter(Context context, ArrayList<Message> chatList, ArrayList<AudioDetails> audioDetailsList, ArrayList<SharedAudios> sharedAudioList) {
        this.chatList = chatList;
        this.audioDetailsList = audioDetailsList;
        this.sharedAudioList = sharedAudioList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chatMessage, timeStamp, tvMelodyName, tvUserName, tvNum;
        ImageView userProfileImage, chatImage, ivPlay, ivPrev, ivNext, ivSettings;
        RelativeLayout rlChatImage, rlBelowImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            chatMessage = (TextView) itemView.findViewById(R.id.chatMessage);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            chatImage = (ImageView) itemView.findViewById(R.id.chatImage);
            rlChatImage = (RelativeLayout) itemView.findViewById(R.id.rlChatImage);
            rlBelowImage = (RelativeLayout) itemView.findViewById(R.id.rlBelowImage);
            tvMelodyName = (TextView) itemView.findViewById(R.id.tvMelodyName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvNum = (TextView) itemView.findViewById(R.id.tvNum);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
            ivPrev = (ImageView) itemView.findViewById(R.id.ivPrev);
            ivNext = (ImageView) itemView.findViewById(R.id.ivNext);
            ivSettings = (ImageView) itemView.findViewById(R.id.ivSettings);

            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            ivPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            ivNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            ivSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == SELF) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_self, parent, false);
        } else if (viewType == SELF_IMAGE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_image_self, parent, false);
        } else if (viewType == SELF_AUDIO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_recording_self, parent, false);
        } else if (viewType == OTHER_IMAGE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_image_other, parent, false);
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
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }
        if (message.getSenderId().equals(userId)) {
            if (message.getFileType().equals("station") || message.getFileType().equals("admin_melody")) {
                return SELF_AUDIO;
            } else if (message.getFileType().equals("image")) {
                return SELF_IMAGE;
            } else {
                return SELF;
            }
        } else {
            if (message.getFileType().equals("station") || message.getFileType().equals("admin_melody")) {
                return OTHER_AUDIO;
            } else if (message.getFileType().equals("image")) {
                return OTHER_IMAGE;
            }
        }
        return position;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final int itemType = getItemViewType(position);
        if (itemType == SELF_AUDIO || itemType == OTHER_AUDIO) {
            Message message = chatList.get(position);
            AudioDetails audioDetails = audioDetailsList.get(0);
//            SharedAudios sharedAudios = sharedAudioList.get(sharedAudioList.size()-1);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).into(holder.userProfileImage);
            holder.timeStamp.setText(message.getCreatedAt());
            holder.tvMelodyName.setText(audioDetails.getRecordingTopic());
            holder.tvUserName.setText(audioDetails.getUserName());
            String str = "(" + playingAudio + " of " + String.valueOf(sharedAudioList.size() + ")");
            holder.tvNum.setText(str);
        } else if (itemType == SELF_IMAGE || itemType == OTHER_IMAGE) {
            Message message = chatList.get(position);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).into(holder.userProfileImage);
            if (!message.getFile().equals("")) {
                holder.rlChatImage.setVisibility(View.VISIBLE);
                Picasso.with(holder.chatImage.getContext()).load(message.getFile()).into(holder.chatImage);
            }
            if (!message.getMessage().equals("")) {
                holder.chatMessage.setVisibility(View.VISIBLE);
                holder.chatMessage.setText(message.getMessage());
            }
        } else {
            Message message = chatList.get(position);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).into(holder.userProfileImage);
            holder.chatMessage.setText(message.getMessage());
            holder.timeStamp.setText(message.getCreatedAt());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}




