package com.yomelody.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yomelody.ChatActivity;
import com.yomelody.JoinActivity;
import com.yomelody.MelodyActivity;
import com.yomelody.Models.JoinRecordingModel;
import com.yomelody.Models.Message;
import com.yomelody.Models.SharedAudios;
import com.yomelody.Parse.ParseContents;
import com.yomelody.R;
import com.yomelody.StudioActivity;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.Const;
import com.yomelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.yomelody.ChatActivity.flSeekbar;
import static com.yomelody.ChatActivity.ivPausePlayer;
import static com.yomelody.ChatActivity.ivPlayPlayer;
import static com.yomelody.ChatActivity.progressDialog;
import static com.yomelody.ChatActivity.rlChatPlayer;
import static com.yomelody.ChatActivity.rlNothing;
import static com.yomelody.ChatActivity.seekBarChata;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.BASE_URL;
import static com.yomelody.utils.Const.ServiceType.HOST_URL;
import static com.yomelody.utils.Const.ServiceType.JoinRecording;

/**
 * Created by Shubhansh Jaiswal on 17/01/17.
 * This adapter class identifies the current logged in user messages by user id and align the messages left or right by inflating two different xml layouts.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    Context context;
    private ArrayList<Message> chatList = new ArrayList<>();
    public static ArrayList<SharedAudios> sharedAudioList = new ArrayList<>();
    public static ArrayList<JoinRecordingModel> JoinList = new ArrayList<JoinRecordingModel>();
    public ArrayList<MediaPlayer> JoinMp = new ArrayList<MediaPlayer>();
    int count = 0;
    Handler mHandler1 = new Handler();
    private String userId;
    private int SELF = 100;
    private int SELF_IMAGE = 101;
    private int SELF_AUDIO = 102;
    private int OTHER = 103;
    private int OTHER_IMAGE = 104;
    private int OTHER_AUDIO = 105;

    public static String str;
    public static ImageView ivPrev, ivNext;
//    public static TextView tvNum;
    public static MediaPlayer mp = null;
    String parentRec;
    public static int origCount = 0;
    int pos;
    private Message mMessage;
    static String instrumentFile;
    int duration1 = 0, length;
    private int MinJoinCount = 1, HolderJoinCount = 1;
    int includedCount = 1, TempJoinCount = 0;
    private int PlayCounter = 0;
    private int PreviousAdapterIndex = 0, CurrentAdapterIndex = 0, FirstIndex = 0;
    private MyViewHolder myViewHolder;
    private Activity mActivity;

    public ChatAdapter(Context context, ArrayList<Message> chatList) {
        this.chatList = chatList;
        this.context = context;
        mActivity = (Activity)context;
        mp = null;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chatMessage, chatImageName, timeStamp, tvUserName, tvMelodyName, tvNum, TemptvMelodyName;
        ImageView userProfileImage, chatImage, ivPlay, ivSettings, ivTick, ivDoubleTick;
        RelativeLayout rlChatImage, rlBelowImage, rlChatRecTop;
        SeekBar seekBarChat;
        ImageView plusIv;
        //  ProgressDialog progressDialog;


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
            tvNum = (TextView) itemView.findViewById(R.id.tvNum);
            TemptvMelodyName = (TextView) itemView.findViewById(R.id.TemptvMelodyName);
            rlChatRecTop = (RelativeLayout) itemView.findViewById(R.id.rlChatRecTop);
            plusIv = (ImageView) itemView.findViewById(R.id.plusIv);
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
            ivPrev = (ImageView) itemView.findViewById(R.id.ivPrev);
            ivNext = (ImageView) itemView.findViewById(R.id.ivNext);
        } else if (viewType == OTHER_IMAGE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_image_other, parent, false);
        } else if (viewType == OTHER_AUDIO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_view_recording_other, parent, false);
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
        try {
            if (loginSharedPref.getString("userId", null) != null) {
                userId = loginSharedPref.getString("userId", null);
            } else if (fbPref.getString("userId", null) != null) {
                userId = fbPref.getString("userId", null);
            } else if (twitterPref.getString("userId", null) != null) {
                userId = twitterPref.getString("userId", null);
            }
            if (message.getSenderId().equals(userId)) {
                if (message.getFileType().equals("station") ||
                        message.getFileType().equals("admin_melody") ||
                        message.getFileType().equals("user_melody")) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return position;
    }

    public void resetPlaycount() {
        TempJoinCount = 0;
        MinJoinCount = 1;
        PlayCounter = 0;
        FirstIndex=0;
        PreviousAdapterIndex = 0;
        CurrentAdapterIndex = 0;
        mp=null;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            final int itemType = getItemViewType(position);
            holder.setIsRecyclable(false);

            if (itemType == SELF_AUDIO || itemType == OTHER_AUDIO) {
                final Message message = chatList.get(position);
                if (!message.getProfilePic().equals("")) {
                    Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
                }
                holder.timeStamp.setText(message.getCreatedAt());
                String stroke = "(1" + " of " + message.getRecCount() + ")";
                holder.tvNum.setText(stroke);
                if (message.getIsRead().equals("1")) {
                    holder.ivTick.setVisibility(View.GONE);
                    holder.ivDoubleTick.setVisibility(VISIBLE);
                } else {
                    holder.ivDoubleTick.setVisibility(View.GONE);
                    holder.ivTick.setVisibility(VISIBLE);
                }


                JSONArray audiosDetailsArray = message.getAudioDetails();
                if (audiosDetailsArray != null && audiosDetailsArray.length() > 0) {
                    for (int j = 0; j < audiosDetailsArray.length(); j++) {
                        try {
                            JSONObject detailsJson = audiosDetailsArray.getJSONObject(j);
                            if (detailsJson.has("recording_id")) {
                                holder.rlChatRecTop.setVisibility(VISIBLE);
                                holder.plusIv.setVisibility(GONE);
                                holder.tvMelodyName.setText(detailsJson.getString("recording_topic"));
                                String s = "@" + detailsJson.getString("user_name");
                                holder.tvUserName.setText(s);
                                holder.TemptvMelodyName.setText(detailsJson.getString("name"));

                            }
                            else {
                                holder.rlChatRecTop.setVisibility(GONE);
                                holder.plusIv.setVisibility(VISIBLE);
                                holder.tvMelodyName.setText(detailsJson.getString("name"));
                                holder.TemptvMelodyName.setText(detailsJson.getString("name"));
                                if (detailsJson.has("melodypackid")){
                                    holder.tvUserName.setText(detailsJson.getString("username"));
                                }
                                else if (detailsJson.has("melody_id")){
                                    holder.tvUserName.setText("@"+detailsJson.getString("user_name"));
                                    holder.tvMelodyName.setText(detailsJson.getString("recording_topic"));
                                }
                            }
//                            if (detailsJson.has("melodypackid") || detailsJson.has("melody_id")) {}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                seekBarChata.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        try {
                            int mCurrentPosition = mp.getCurrentPosition() / 1000;
                            int mDuration = mp.getDuration() / 1000;
                            UtilsRecording utilRecording = new UtilsRecording();
                            int progress1 = utilRecording.getProgressPercentage(mCurrentPosition, mDuration);

                            if (mp != null && b) {
                                try {
                                    int playPositionInMilliseconds = mp.getDuration() / 100 * holder.seekBarChat.getProgress();
                                    mp.seekTo(playPositionInMilliseconds);
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
//                        seekBarChata.setProgress(progress);
                            } else {
                                // the event was fired from code and you shouldn't call player.seekTo()
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
//                ivPrev.setEnabled(false);
//                ivNext.setEnabled(false);

                holder.plusIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, StudioActivity.class);
                        intent.putExtra("previous_screen","ChatActivity");
                        intent.putExtra("melody_data",chatList.get(position).getMsgJson()+"");
//                        AppHelper.sop("melody_data="+chatList.get(position).getMsgJson());
                        mActivity.startActivityForResult(intent,ChatActivity.REQUEST_JOIN_MELO);
                    }
                });

                holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        mMessage = chatList.get(position);
                        if (ParseContents.sharedAudioList.size() > 0) {
                            ParseContents.sharedAudioList.clear();
                        }

                        myViewHolder=holder;

                        try {
                            new ParseContents(getApplicationContext()).parseSharedJoin(position, chatList);
                            if (ParseContents.sharedAudioList.get(count).getRecordingUrl() != null) {
                                rlNothing.setVisibility(VISIBLE);
                                rlChatPlayer.setVisibility(VISIBLE);
                                flSeekbar.setVisibility(VISIBLE);
                                ivPlayPlayer.setVisibility(View.GONE);
                                ivPausePlayer.setVisibility(VISIBLE);

                                if (message != null && message.getAudioDetails() != null && message.getAudioDetails().length() > 0) {
                                    try {
                                        JSONObject json = (JSONObject) message.getAudioDetails().get(0);
                                        AppHelper.sop("json===" + json);
                                        if (json.has("recording_id")) {
                                            ChatActivity.nextPreRl.setVisibility(VISIBLE);
                                        }
                                        else {
                                            ChatActivity.nextPreRl.setVisibility(GONE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
                                ChatActivity.tvNumPlayer.setText(holder.tvNum.getText().toString().trim());
                                ChatActivity.tvNamePlayer.setText(holder.TemptvMelodyName.getText().toString().trim());
                                ChatActivity.tvUserNamePlayer.setText(holder.tvUserName.getText().toString().trim());
                                if (ParseContents.sharedAudioList.get(0).getProfileUrl().toString() != "") {
                                    Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(ParseContents.sharedAudioList.get(0).getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(ChatActivity.userProfileImagePlayer);
                                }
                                //ParseContents.sharedAudioList.get(0).getProfileUrl()
                                str = "("+MinJoinCount + " of " + ParseContents.sharedAudioList.size() + ")";
                                holder.tvNum.setText(str);
                                if (FirstIndex == 0) {
                                    FirstIndex = 1;

                                    TempJoinCount = ParseContents.sharedAudioList.size();
                                }

                                if (PreviousAdapterIndex != CurrentAdapterIndex) {
                                    AppHelper.sop("PreviousAdapterIndex="+PreviousAdapterIndex
                                    +"=CurrentAdapterIndex="+CurrentAdapterIndex);
                                    PlayCounter = 0;
                                    MinJoinCount = 1;

                                    try {
                                        if (mHandler1 != null) {
                                            mHandler1.removeCallbacksAndMessages(null);
                                        }
                                        seekBarChata.setProgress(0);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    PreviousAdapterIndex = CurrentAdapterIndex;
                                    TempJoinCount = ParseContents.sharedAudioList.size();
                                    str = "("+MinJoinCount + " of " + ParseContents.sharedAudioList.size() + ")";
                                    holder.tvNum.setText(str);
                                }
                                CurrentAdapterIndex = holder.getAdapterPosition();
                                try {
                                    if (PlayCounter >= 0) {

                                        progressDialog.setMessage("Loading...");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        PlayAudio(holder.getAdapterPosition(), "main");

                                    }
                                } catch (Exception ex) {
                                    progressDialog.dismiss();
                                    ex.printStackTrace();
                                }
                            }
                        } catch (Exception ex) {
                            progressDialog.dismiss();
                            ex.printStackTrace();
                        }
                    }
                });
                ivPlayPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ivPausePlayer.setVisibility(View.VISIBLE);
                            ivPlayPlayer.setVisibility(GONE);
                            progressDialog.setMessage("Loading...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            PlayAudio(holder.getAdapterPosition(), "main");
                        } catch (Exception ex) {
                            progressDialog.dismiss();
                            ex.printStackTrace();
                        }
                    }
                });
                ChatActivity.rlNextPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //if (CurrentAdapterIndex == holder.getAdapterPosition()) {
                            if (PlayCounter < TempJoinCount - 1) {
                                progressDialog.setMessage("Loading...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                //lastModifiedHoled = holder;
                                PlayCounter = PlayCounter + 1;
                                ivPausePlayer.setVisibility(View.VISIBLE);
                                ivPlayPlayer.setVisibility(GONE);
                                try {
                                    if (ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl().toString() != "") {
                                        Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(ChatActivity.userProfileImagePlayer);
                                    }
                                } catch (Exception ex) {
                                    progressDialog.dismiss();
                                    ex.printStackTrace();
                                }
                                PlayAudio(holder.getAdapterPosition(), "next");
                                //lastModifiedHoled = holder;
                            }
                            //}
                        } catch (Exception ex) {
                            progressDialog.dismiss();
                            ex.printStackTrace();
                        }
                    }
                });
                ChatActivity.rlPrevPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //if (CurrentAdapterIndex == holder.getAdapterPosition()) {
                            if (PlayCounter <= TempJoinCount - 1 && PlayCounter != 0) {
                                progressDialog.setMessage("Loading...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                //lastModifiedHoled = holder;
                                PlayCounter = PlayCounter - 1;
                                ivPausePlayer.setVisibility(View.VISIBLE);
                                ivPlayPlayer.setVisibility(GONE);
                                try {
                                    if (ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl().toString() != "") {
                                        Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(ChatActivity.userProfileImagePlayer);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                PlayAudio(holder.getAdapterPosition(), "pre");
                                //lastModifiedHoled = holder;
                            }
                            //}
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });

                ivNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //if (CurrentAdapterIndex == holder.getAdapterPosition()) {

                            if (mp == null){

                                TempJoinCount = Integer.parseInt(message.getRecCount());
                                AppHelper.sop("TempJoinCount=ivNext=if="+TempJoinCount);
                                if (PlayCounter < TempJoinCount - 1) {
                                    AppHelper.sop("PlayCounter=ivNext=if="+PlayCounter);
                                    PlayCounter = PlayCounter + 1;
                                    MinJoinCount = MinJoinCount + 1;
                                    ChatActivity.tvNumPlayer.setText(UpdateCalJoinCount(TempJoinCount));
                                    holder.tvNum.setText(UpdateCalJoinCount(TempJoinCount));
                                }
                            }
                            else {
                                if (PlayCounter < TempJoinCount - 1) {

                                    progressDialog.setMessage("Loading...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    //lastModifiedHoled = holder;
                                    PlayCounter = PlayCounter + 1;
                                    ivPausePlayer.setVisibility(View.VISIBLE);
                                    ivPlayPlayer.setVisibility(GONE);
                                    try {
                                        if (ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl().toString() != "") {
                                            Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(ChatActivity.userProfileImagePlayer);
                                        }
                                    } catch (Exception ex) {
                                        progressDialog.dismiss();
                                        ex.printStackTrace();
                                    }
                                    PlayAudio(holder.getAdapterPosition(), "next");
                                    //lastModifiedHoled = holder;
                                }
                            }

                            //}
                        } catch (Exception ex) {
                            progressDialog.dismiss();
                            ex.printStackTrace();
                        }
                    }
                });
                ivPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //if (CurrentAdapterIndex == holder.getAdapterPosition()) {


                            if (mp==null){
                                TempJoinCount = Integer.parseInt(message.getRecCount());
                                AppHelper.sop("TempJoinCount=ivPrev=if="+TempJoinCount);
                                if (PlayCounter > 0) {
                                    AppHelper.sop("PlayCounter=ivPrev=if="+PlayCounter);
                                    PlayCounter = PlayCounter - 1;
                                    MinJoinCount = MinJoinCount - 1;
                                    ChatActivity.tvNumPlayer.setText(UpdateCalJoinCount(TempJoinCount));
                                    holder.tvNum.setText(UpdateCalJoinCount(TempJoinCount));
                                }
                            }
                            else {
                                if (PlayCounter <= TempJoinCount - 1 && PlayCounter != 0) {
                                    progressDialog.setMessage("Loading...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    //lastModifiedHoled = holder;
                                    PlayCounter = PlayCounter - 1;
                                    ivPausePlayer.setVisibility(View.VISIBLE);
                                    ivPlayPlayer.setVisibility(GONE);
                                    try {
                                        if (ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl().toString() != "") {
                                            Picasso.with(ChatActivity.userProfileImagePlayer.getContext()).load(ParseContents.sharedAudioList.get(PlayCounter).getProfileUrl()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(ChatActivity.userProfileImagePlayer);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    PlayAudio(holder.getAdapterPosition(), "pre");
                                    //lastModifiedHoled = holder;
                                }
                            }

                            //}
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });


                ivPausePlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ivPausePlayer.setVisibility(View.GONE);
                        ivPlayPlayer.setVisibility(VISIBLE);
                        if (mp != null) {
                            try {
                                mp.reset();
                                mp.release();
                                mp = null;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                    }
                });


                holder.ivSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Message message = chatList.get(position);
                        if (message != null && message.getAudioDetails() != null && message.getAudioDetails().length() > 0) {
                            try {
                                JSONObject json = (JSONObject) message.getAudioDetails().get(0);
                                AppHelper.sop("json===" + json);
                                if (json.has("recording_id")) {
                                    SharedPreferences.Editor record = mActivity.getSharedPreferences("RecordingData", MODE_PRIVATE).edit();
                                    record.putString("AddedBy", json.getString("added_by"));
                                    record.putString("Recording_id", json.getString("recording_id"));
                                    record.putString("UserNameRec", json.getString("user_name"));
                                    record.putString("RecordingName", json.getString("recording_topic"));
                                    record.putString("UserProfile", message.getProfilePic());
//                                    record.putString("previous_screen", "ChatActivity");
                                    record.commit();
                                    Intent intent = new Intent(mActivity, JoinActivity.class);
                                    intent.putExtra("previous_screen", "ChatActivity");
                                    mActivity.startActivityForResult(intent, ChatActivity.REQUEST_JOIN_REC);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });

            } else if (itemType == SELF_IMAGE || itemType == OTHER_IMAGE) {
                try {
                    Message message = chatList.get(position);
                    if (!message.getProfilePic().equals("")) {
                        Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
                    }
                    holder.timeStamp.setText(message.getCreatedAt());
                    if (message.getIsRead().equals("1")) {
                        holder.ivTick.setVisibility(View.GONE);
                        holder.ivDoubleTick.setVisibility(VISIBLE);
                    } else {
                        holder.ivDoubleTick.setVisibility(View.GONE);
                        holder.ivTick.setVisibility(VISIBLE);
                    }
                    if (!message.getFile().equals("")) {
                        holder.rlChatImage.setVisibility(VISIBLE);
                        String picUrl = message.getFile();
                        String picName = picUrl.substring(picUrl.lastIndexOf("/") + 1);
                        if (!picUrl.equals("")) {
                            Picasso.with(holder.chatImage.getContext()).load(picUrl).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.no_image)).into(holder.chatImage);
                        }
                        holder.chatImageName.setText(picName);
                    }
                    if (!message.getMessage().equals("")) {
                        holder.chatMessage.setVisibility(VISIBLE);
                        holder.chatMessage.setText(message.getMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    Message message = chatList.get(position);
                    if (!message.getProfilePic().equals("")) {
                        Picasso.with(holder.userProfileImage.getContext()).load(message.getProfilePic()).placeholder(context.getResources().getDrawable(R.drawable.loading)).error(context.getResources().getDrawable(R.drawable.artist)).into(holder.userProfileImage);
                    }
                    holder.chatMessage.setText(message.getMessage());
                    holder.timeStamp.setText(message.getCreatedAt());
                    if (message.getIsRead().equals("1")) {
                        holder.ivTick.setVisibility(View.GONE);
                        holder.ivDoubleTick.setVisibility(VISIBLE);
                    } else {
                        holder.ivDoubleTick.setVisibility(View.GONE);
                        holder.ivTick.setVisibility(VISIBLE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public Message getmMessage() {
        return mMessage;
    }

    public void fetchPlayJoinAudio(final String RecId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JoinRecording,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //       Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject, respObject;
                        JSONArray jsonArray;
                        try {
                            JoinList.clear();
                            JoinMp.clear();
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equals("success")) {
                                parentRec = jsonObject.getString("parentrec");
                                jsonArray = jsonObject.getJSONArray("response");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    respObject = jsonArray.getJSONObject(i);
                                    try {
                                        MediaPlayer mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mediaPlayer.setDataSource(respObject.getString("recording_url"));
                                        JoinMp.add(mediaPlayer);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //       Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("rid", RecId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

    private void PlayAudio(int pisition, final String Type) {
        try {
            AppHelper.sop("PlayAudio=PlayCounter=="+PlayCounter);
            instrumentFile = ParseContents.sharedAudioList.get(PlayCounter).getRecordingUrl();
            if (instrumentFile != "") {
                if (!instrumentFile.contains("http")){
                    instrumentFile = BASE_URL+instrumentFile;
                }
                try {
                    if (mp != null) {
                        try {

                            try {
                                mp.stop();
                                mp.release();
                                mp = null;
                                duration1 = 0;
                                length = 0;

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                mp = new MediaPlayer();
                try {
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                try {
                    mp.setDataSource(instrumentFile);

                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                mp.prepareAsync();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        try {
                            mp.start();

                            progressDialog.dismiss();
                            duration1 = mp.getDuration();
                            //primarySeekBarProgressUpdater();
                            primarySeekBarProgressUpdater();
                            if (Type == "main") {

                                //MinJoinCount = MinJoinCount + 1;
                            } else if (Type == "next") {

                                MinJoinCount = MinJoinCount + 1;
                                ChatActivity.tvNumPlayer.setText(UpdateCalJoinCount(TempJoinCount));
                                myViewHolder.tvNum.setText(UpdateCalJoinCount(TempJoinCount));
                            } else if (Type == "pre") {

                                MinJoinCount = MinJoinCount - 1;
                                ChatActivity.tvNumPlayer.setText(UpdateCalJoinCount(TempJoinCount));
                                myViewHolder.tvNum.setText(UpdateCalJoinCount(TempJoinCount));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

                        try {
                            ivPausePlayer.setVisibility(View.GONE);
                            ivPlayPlayer.setVisibility(VISIBLE);
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }
                            progressDialog.dismiss();
                           /* lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                            SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
                            seekBar.setProgress(0);*/
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return false;
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            ivPausePlayer.setVisibility(View.GONE);
                            ivPlayPlayer.setVisibility(VISIBLE);
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }
                            seekBarChata.setProgress(0);
                        /*lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                        lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                        SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
                        seekBar.setProgress(0);
                        length = 0;
                        duration1 = 0;*/
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });


            } else {
                Toast.makeText(context, "Recording URL not found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void primarySeekBarProgressUpdater() {
        try {
            seekBarChata.setProgress((int) (((float) mp.getCurrentPosition() / mp.getDuration()) * 100));
            if (mp.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                mHandler1.postDelayed(notification, 100);
            } else {
                try {
                    seekBarChata.setProgress(0);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String UpdateCalJoinCount(int joincount) {
        String jCount = "";
        try {
            if (joincount == 0) {
                //currentSongIndex=1;
                jCount = "(" + String.valueOf(MinJoinCount) + " of " + String.valueOf((joincount)) + ")";
            } else {

                jCount = "("+String.valueOf(MinJoinCount) + " of " + String.valueOf(joincount)+")";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }
}