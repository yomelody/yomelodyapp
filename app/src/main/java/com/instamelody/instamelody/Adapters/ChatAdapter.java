package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.Models.AudioDetails;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.SharedAudios;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.instamelody.instamelody.ChatActivity.ivPausePlayer;
import static com.instamelody.instamelody.ChatActivity.ivPlayPlayer;

/**
 * Created by Shubhansh Jaiswal on 17/01/17.
 * This adapter class identifies the current logged in user messages by user id and align the messages left or right by inflating two different xml layouts.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    Context context;
    private ArrayList<Message> chatList = new ArrayList<>();
    private ArrayList<AudioDetails> audioDetailsList = new ArrayList<>();
    //    private ArrayList<SharedAudios> sharedAudioList = new ArrayList<>();
    public static MediaPlayer mediaPlayer;
    private String userId;
    private int SELF = 100;
    private int SELF_IMAGE = 101;
    private int SELF_AUDIO = 102;
    private int OTHER = 103;
    private int OTHER_IMAGE = 104;
    private int OTHER_AUDIO = 105;
    public static int playingAudio = 0;
    public static String str;
    public static ImageView ivPrev, ivNext;
    public static TextView tvNum;

    public ChatAdapter(Context context, ArrayList<Message> chatList) {
        this.chatList = chatList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chatMessage, chatImageName, timeStamp, tvUserName, tvMelodyName;
        ImageView userProfileImage, chatImage, ivPlay, ivSettings, ivTick, ivDoubleTick;
        RelativeLayout rlChatImage, rlBelowImage;
        SeekBar seekBarChat;

        public MyViewHolder(View itemView) {
            super(itemView);
            chatMessage = (TextView) itemView.findViewById(R.id.chatMessage);
            chatImageName = (TextView) itemView.findViewById(R.id.chatImageName);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            chatImage = (ImageView) itemView.findViewById(R.id.chatImage);
            rlChatImage = (RelativeLayout) itemView.findViewById(R.id.rlChatImage);
            rlBelowImage = (RelativeLayout) itemView.findViewById(R.id.rlBelowImage);
            tvMelodyName = (TextView) itemView.findViewById(R.id.tvMelodyName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
            ivSettings = (ImageView) itemView.findViewById(R.id.ivSettings);
            seekBarChat = (SeekBar) itemView.findViewById(R.id.seekBarChat);
            ivTick = (ImageView) itemView.findViewById(R.id.tick);
            ivDoubleTick = (ImageView) itemView.findViewById(R.id.doubleTick);
        }

        private void primarySeekBarProgressUpdater() {
            Handler mHandler1 = new Handler();
            try {
                seekBarChat.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (mediaPlayer.isPlaying()) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            primarySeekBarProgressUpdater();
                        }
                    };
                    mHandler1.postDelayed(notification, 100);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
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
            tvNum = (TextView) itemView.findViewById(R.id.tvNum);
            ivPrev = (ImageView) itemView.findViewById(R.id.ivPrev);
            ivNext = (ImageView) itemView.findViewById(R.id.ivNext);
        } else if (viewType == OTHER_IMAGE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_image_other, parent, false);
        } else if (viewType == OTHER_AUDIO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_recording_other, parent, false);
            tvNum = (TextView) itemView.findViewById(R.id.tvNum);
            ivPrev = (ImageView) itemView.findViewById(R.id.ivPrev);
            ivNext = (ImageView) itemView.findViewById(R.id.ivNext);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final int itemType = getItemViewType(position);
        if (itemType == SELF_AUDIO || itemType == OTHER_AUDIO) {
            Message message = chatList.get(position);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
            holder.timeStamp.setText(message.getCreatedAt());
            if (message.getIsRead().equals("1")) {
                holder.ivTick.setVisibility(View.GONE);
                holder.ivDoubleTick.setVisibility(View.VISIBLE);
            }
            JSONArray audiosDetailsArray = message.getAudioDetails();
            if (audiosDetailsArray.length() > 0) {
                for (int j = 0; j < audiosDetailsArray.length(); j++) {
                    try {
                        ArrayList<SharedAudios> sharedAudioList = new ArrayList<>();
                        JSONObject detailsJson = audiosDetailsArray.getJSONObject(j);
//                        detailsJson.getString("recording_id");
//                        detailsJson.getString("added_by");
//                        detailsJson.getString("name");
                        holder.tvMelodyName.setText(detailsJson.getString("recording_topic"));
                        String s = "@" + detailsJson.getString("user_name");
                        holder.tvUserName.setText(s);
                        if (!detailsJson.get("recordings").equals(null) && !detailsJson.get("recordings").equals("")) {
                            JSONArray sharedAudiosArray = detailsJson.getJSONArray("recordings");

                            if (sharedAudiosArray.length() > 0) {
                                for (int k = 0; k < sharedAudiosArray.length(); k++) {
                                    SharedAudios sharedAudios = new SharedAudios();
                                    JSONObject audioJson = sharedAudiosArray.getJSONObject(k);
                                    sharedAudios.setAddedById(audioJson.getString("added_by_id"));
                                    sharedAudios.setUserName(audioJson.getString("user_name"));
                                    sharedAudios.setName(audioJson.getString("name"));
                                    sharedAudios.setProfileUrl(audioJson.getString("profile_url"));
                                    sharedAudios.setDateAdded(audioJson.getString("date_added"));
                                    sharedAudios.setDuration(audioJson.getString("duration"));
                                    sharedAudios.setRecordingUrl(audioJson.getString("recording_url"));
                                    sharedAudioList.add(sharedAudios);
                                }
                            }
                        }
                        str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
                        tvNum.setText(str);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

//            holder.seekBarChat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                    if (mediaPlayer != null && fromUser) {
//                        try {
//                            int playPositionInMilliseconds = mediaPlayer.getDuration() / 100 * holder.seekBarChat.getProgress();
//                            mediaPlayer.seekTo(playPositionInMilliseconds);
//                        } catch (IllegalStateException e) {
//                            e.printStackTrace();
//                        }
////                        seekBar.setProgress(duration);
//                    } else {
//                        // the event was fired from code and you shouldn't call player.seekTo()
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                }
//            });

//            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ChatActivity.rlChatPlayer.setVisibility(View.VISIBLE);
//                    ivPlayPlayer.setVisibility(View.GONE);
//                    ivPausePlayer.setVisibility(View.VISIBLE);
//                    SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
//                    String audioUrl = sharedAudios.getRecordingUrl();
//                    Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(sharedAudios.getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.no_image)).into(ChatActivity.userProfileImagePlayer);
//                    ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
//                    ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
//                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
//                    ChatActivity.tvNumPlayer.setText(tvNum.getText().toString().trim());
//                    AudioOperator(audioUrl);
//                }
//            });
//
//            ivPrev.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    ChatActivity.rlChatPlayer.setVisibility(View.VISIBLE);
//                    ivPlayPlayer.setVisibility(View.GONE);
//                    ivPausePlayer.setVisibility(View.VISIBLE);
//                    playingAudio = playingAudio - 1;
//                    str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
//                    tvNum.setText(str);
//                    SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
//                    String audioUrl = sharedAudios.getRecordingUrl();
//                    Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(sharedAudios.getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.no_image)).into(ChatActivity.userProfileImagePlayer);
//                    ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
//                    ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
//                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
//                    ChatActivity.tvNumPlayer.setText(tvNum.getText().toString().trim());
//                    AudioOperator(audioUrl);
//                }
//            });
//
//            ivNext.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    ChatActivity.rlChatPlayer.setVisibility(View.VISIBLE);
//                    ivPlayPlayer.setVisibility(View.GONE);
//                    ivPausePlayer.setVisibility(View.VISIBLE);
//                    playingAudio = playingAudio + 1;
//                    str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
//                    tvNum.setText(str);
//                    SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
//                    String audioUrl = sharedAudios.getRecordingUrl();
//                    Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(sharedAudios.getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.no_image)).into(ChatActivity.userProfileImagePlayer);
//                    ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
//                    ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
//                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
//                    ChatActivity.tvNumPlayer.setText(tvNum.getText().toString().trim());
//                    AudioOperator(audioUrl);
//                }
//            });

        } else if (itemType == SELF_IMAGE || itemType == OTHER_IMAGE) {
            Message message = chatList.get(position);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
            holder.timeStamp.setText(message.getCreatedAt());
            if (message.getIsRead().equals("1")) {
                holder.ivTick.setVisibility(View.GONE);
                holder.ivDoubleTick.setVisibility(View.VISIBLE);
            }
            if (!message.getFile().equals("")) {
                holder.rlChatImage.setVisibility(View.VISIBLE);
                String picUrl = message.getFile();
                String picName = picUrl.substring(picUrl.lastIndexOf("/") + 1);
                Picasso.with(holder.chatImage.getContext()).load(picUrl).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.no_image)).into(holder.chatImage);
                holder.chatImageName.setText(picName);
            }
            if (!message.getMessage().equals("")) {
                holder.chatMessage.setVisibility(View.VISIBLE);
                holder.chatMessage.setText(message.getMessage());
            }
        } else {
            Message message = chatList.get(position);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
            holder.chatMessage.setText(message.getMessage());
            holder.timeStamp.setText(message.getCreatedAt());
            if (message.getIsRead().equals("1")) {
                holder.ivTick.setVisibility(View.GONE);
                holder.ivDoubleTick.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static void AudioOperator(String audioLink) {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audioLink);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
//                primarySeekBarProgressUpdater();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer MediaPlayer, int what, int extra) {
                return false;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                duration = mediaPlayer.getDuration();
            }
        });
    }
}




