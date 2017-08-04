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
    String receiverId = "", chatID = "", receiverName = "", receiverImage = "", senderId = "", userId;

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
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            rlComplete = (RelativeLayout) itemView.findViewById(R.id.rlComplete);

            rlComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    senderId = chatList.get(getAdapterPosition()).getSenderID();
                    receiverId = chatList.get(getAdapterPosition()).getReceiverID();
                    SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                    if(receiverId.contains(",")){
                        editor.putString("chatType", "group");
                    }else{
                        editor.putString("chatType", "single");
                    }
                    editor.commit();
                    receiverName = chatList.get(getAdapterPosition()).getReceiverName();
                    receiverImage = chatList.get(getAdapterPosition()).getUserProfileImage();
                    chatID = chatList.get(getAdapterPosition()).getChatID();

                    editor.putString("senderId", senderId);
                    editor.putString("receiverId", receiverId);
                    editor.putString("receiverName", receiverName);
                    editor.putString("receiverImage", receiverImage);
                    editor.putString("chatId", chatID);
                    editor.commit();

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("from", "MessengerActivity");
                    context.startActivity(intent);
                }
            });

            userProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String showProfileUserId;
                    String senderID = chatList.get(getAdapterPosition()).getSenderID();
                    String receiverID = chatList.get(getAdapterPosition()).getReceiverID();
                    if (senderID.equals(userId)) {
                        showProfileUserId = receiverID;
                    } else {
                        showProfileUserId = senderID;
                    }
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    intent.putExtra("showProfileUserId", showProfileUserId);
                    view.getContext().startActivity(intent);
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
