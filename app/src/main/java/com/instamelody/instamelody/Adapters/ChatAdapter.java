package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.Models.AudioDetails;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.SharedAudios;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
    MediaPlayer mediaPlayer;
    private String userId;
    private int SELF = 100;
    private int SELF_IMAGE = 101;
    private int SELF_AUDIO = 102;
    private int OTHER = 103;
    private int OTHER_IMAGE = 104;
    private int OTHER_AUDIO = 105;
    private int playingAudio = 0;

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
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final int itemType = getItemViewType(position);
        if (itemType == SELF_AUDIO || itemType == OTHER_AUDIO) {
            Message message = chatList.get(position);
            AudioDetails audioDetails = audioDetailsList.get(0);
            Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).into(holder.userProfileImage);
            holder.timeStamp.setText(message.getCreatedAt());
            holder.tvMelodyName.setText(audioDetails.getRecordingTopic());
            holder.tvUserName.setText(audioDetails.getUserName());
            final String str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
            holder.tvNum.setText(str);

            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatActivity.rlChatPlayer.setVisibility(View.VISIBLE);
                    SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
                    String audioUrl = sharedAudios.getRecordingUrl();
                    ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
                    ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
                    ChatActivity.tvNumPlayer.setText(holder.tvNum.getText().toString().trim());

//                    holder.progressDialog = new ProgressDialog(v.getContext());
//                    holder.progressDialog.setMessage("Loading...");
//                    holder.progressDialog.show();
//                    holder.ivPause.setVisibility(VISIBLE);
//                    holder.melodySlider.setVisibility(VISIBLE);
//                    holder.rlSeekbarTracer.setVisibility(VISIBLE);

//                    String position;
//                    position = melodyList.get(listPosition).getMelodyPackId();
//                    String play = holder.tvPlayCount.getText().toString().trim();
//                    int playValue = Integer.parseInt(play) + 1;
//                    play = String.valueOf(playValue);
//                    holder.tvPlayCount.setText(play);
//                    fetchViewCount(userId, position);
//                    ParseContents pc = new ParseContents(context);
//                    instrumentList = pc.getInstruments();
//                    if (listPosition < instrumentList.size()) {
//                        audioUrl = melody.getMelodyURL();
//                    }

                    if (mediaPlayer != null) {
                        try {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer = null;
//                                if (lastModifiedHoled != null) {
//                                    int lastPosition = lastModifiedHoled.getAdapterPosition();
//                                    lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(VISIBLE);
//                                    lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(GONE);
//                                    notifyItemChanged(lastPosition);
//                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(audioUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
//                            holder.progressDialog.dismiss();
//                            lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(GONE);
//                            lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(VISIBLE);
                            mediaPlayer.start();
//                            holder.primarySeekBarProgressUpdater();
                        }
                    });
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer MediaPlayer, int what, int extra) {
//                            holder.progressDialog.dismiss();
                            return false;
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            duration = mediaPlayer.getDuration();
//                            holder.progressDialog.dismiss();
                        }
                    });
//                    lastModifiedHoled = holder;
                }
            });

            holder.ivPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatActivity.rlChatPlayer.setVisibility(View.VISIBLE);
                    playingAudio = playingAudio - 1;
                    String str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
                    holder.tvNum.setText(str);
                    SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
                    String audioUrl = sharedAudios.getRecordingUrl();
                    ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
                    ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
                    ChatActivity.tvNumPlayer.setText(holder.tvNum.getText().toString().trim());

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
                        mediaPlayer.setDataSource(audioUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
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
                        }
                    });
                }
            });

            holder.ivNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatActivity.rlChatPlayer.setVisibility(View.VISIBLE);
                    playingAudio = playingAudio + 1;
                    String str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
                    holder.tvNum.setText(str);
                    SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
                    String audioUrl = sharedAudios.getRecordingUrl();
                    ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
                    ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
                    ChatActivity.tvNumPlayer.setText(holder.tvNum.getText().toString().trim());

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
                        mediaPlayer.setDataSource(audioUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
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
                        }
                    });
                }
            });

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




