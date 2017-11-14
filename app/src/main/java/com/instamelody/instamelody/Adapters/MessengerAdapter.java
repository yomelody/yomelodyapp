package com.instamelody.instamelody.Adapters;

import android.app.Activity;
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
import com.instamelody.instamelody.MessengerActivity;
import com.instamelody.instamelody.Models.Chat;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.ProfileActivity;
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
    String receiverId = "", chatID = "", receiverName = "", groupName = "", receiverImage = "", senderId = "", userId, groupImage = "";
    public static int totalMsgCount = 0;
    private Activity mActivity;

    public MessengerAdapter(ArrayList<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        mActivity = (Activity) context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMsg, tvTime, tvUserName, message_count;
        ImageView userProfileImage;
        RelativeLayout rlComplete;

        public MyViewHolder(View itemView) {
            super(itemView);

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

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            message_count = (TextView) itemView.findViewById(R.id.message_count);
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            rlComplete = (RelativeLayout) itemView.findViewById(R.id.rlComplete);

            rlComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    senderId = chatList.get(getAdapterPosition()).getSenderID();
                    receiverId = chatList.get(getAdapterPosition()).getReceiverID();
                    receiverName = chatList.get(getAdapterPosition()).getReceiverName();
                    groupName = chatList.get(getAdapterPosition()).getGroupName();
                    receiverImage = chatList.get(getAdapterPosition()).getProfilePic();
                    chatID = chatList.get(getAdapterPosition()).getChatID();
                    groupImage = chatList.get(getAdapterPosition()).getGroupPic();
                    SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                    if (receiverId.contains(",")) {
                        editor.putString("chatType", "group");
                        editor.putString("receiverName", groupName);
                        editor.putString("groupImage", groupImage);
                    } else {
                        editor.putString("chatType", "single");
                        editor.putString("receiverName", receiverName);
                        editor.putString("receiverImage", receiverImage);
                    }
                    editor.commit();
                    editor.putString("senderId", senderId);
                    editor.putString("receiverId", receiverId);
                    editor.putString("chatId", chatID);
                    editor.commit();
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("commingForm","MessengerActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("from", "MessengerActivity");

                    if (mActivity.getIntent() != null && mActivity.getIntent().hasExtra("share")) {
                        if (mActivity.getIntent().hasExtra("Previ")) {
                            JoinedArtists mJoinModel = (JoinedArtists) mActivity.getIntent().getSerializableExtra("share");
                            intent.putExtra("share", mJoinModel);
                            intent.putExtra("file_type", mActivity.getIntent().getStringExtra("file_type"));
                            intent.putExtra("commingForm","Joined");
                        } else {
                            RecordingsModel mRecordingsModel = (RecordingsModel) mActivity.getIntent().getSerializableExtra("share");
                            intent.putExtra("share", mRecordingsModel);
                            intent.putExtra("file_type", mActivity.getIntent().getStringExtra("file_type"));
                            intent.putExtra("commingForm","Join");
                            intent.putExtra("commingForm","Station");
                        }

                    }

                    mActivity.startActivityForResult(intent, RecordingsCardAdapter.REQUEST_RECORDING_COMMENT);
                }
            });

            userProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String showProfileUserId;
                    String senderID = chatList.get(getAdapterPosition()).getSenderID();
                    String receiverID = chatList.get(getAdapterPosition()).getReceiverID();
                    String chatType = chatList.get(getAdapterPosition()).getChatType();
                    if (senderID.equals(userId)) {
                        showProfileUserId = receiverID;
                    } else {
                        showProfileUserId = senderID;
                    }
                    if (chatType.equals("single")) {
                        Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                        intent.putExtra("showProfileUserId", showProfileUserId);
                        view.getContext().startActivity(intent);
                    } else {

                    }
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
        try {
            holder.setIsRecyclable(false);

            Chat chat = chatList.get(listPosition);
            if (chat.getChatType().equals("group")) {
                holder.tvUserName.setText(chat.getGroupName());
                if (!chat.getProfilePic().equals("")) {
                    Picasso.with(holder.userProfileImage.getContext()).load(chat.getGroupPic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
                }
                holder.tvMsg.setText(chat.getSenderName() + " : " + chat.getMessage());
            } else {
                holder.tvUserName.setText(chat.getReceiverName());
                if (!chat.getProfilePic().equals("")) {
                    Picasso.with(holder.userProfileImage.getContext()).load(chat.getProfilePic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
                }
//            if (userId.equals(chat.getSenderID())){
//                holder.tvMsg.setText("You" + " : " + chat.getMessage());
//            }else{
//                holder.tvMsg.setText(chat.getSenderName() + " : " + chat.getMessage());
//            }

                holder.tvMsg.setText(chat.getMessage());
            }
            holder.tvTime.setText(chat.getSendAt());
            int msgCount;
            if (!chat.getNewMessages().equals("null") && !chat.getNewMessages().equals("0")) {
                holder.message_count.setVisibility(View.VISIBLE);
                holder.message_count.setText(chat.getNewMessages());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}