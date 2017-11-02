package com.instamelody.instamelody.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.Models.AudioDetails;
import com.instamelody.instamelody.Models.JoinRecordingModel;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.SharedAudios;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StudioActivity;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.ChatActivity.chatType;
import static com.instamelody.instamelody.ChatActivity.flSeekbar;
import static com.instamelody.instamelody.ChatActivity.ivPausePlayer;
import static com.instamelody.instamelody.ChatActivity.ivPlayPlayer;
import static com.instamelody.instamelody.ChatActivity.progressDialog;
import static com.instamelody.instamelody.ChatActivity.rlChatPlayer;
import static com.instamelody.instamelody.ChatActivity.rlNothing;
import static com.instamelody.instamelody.ChatActivity.seekBarChata;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.JoinRecording;
import static com.instamelody.instamelody.utils.Const.ServiceType.READ_STATUS;

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
    //    public TextView tvNum;
    public static MediaPlayer mp;
    String parentRec;
    public static int origCount = 0;
    int pos;

    public ChatAdapter(Context context, ArrayList<Message> chatList) {
        this.chatList = chatList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chatMessage, chatImageName, timeStamp, tvUserName, tvMelodyName, tvNum;
        ImageView userProfileImage, chatImage, ivPlay, ivSettings, ivTick, ivDoubleTick;
        RelativeLayout rlChatImage, rlBelowImage;
        SeekBar seekBarChat;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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

            JSONArray audiosDetailsArray = message.getAudioDetails();
            if (audiosDetailsArray != null && audiosDetailsArray.length() > 0) {
                for (int j = 0; j < audiosDetailsArray.length(); j++) {
                    try {
                        JSONObject detailsJson = audiosDetailsArray.getJSONObject(j);
                        holder.tvMelodyName.setText(detailsJson.getString("recording_topic"));
                        String s = "@" + detailsJson.getString("user_name");
                        holder.tvUserName.setText(s);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            ivPrev.setEnabled(false);
            ivNext.setEnabled(false);
            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    if (ParseContents.sharedAudioList.size() > 0) {
                        ParseContents.sharedAudioList.clear();
                    }
                    new ParseContents(getApplicationContext()).parseSharedJoin(position, chatList);

                    // holder.progressDialog = new ProgressDialog(context);


                    try {
                        if (ParseContents.sharedAudioList.get(count).getRecordingUrl() != null) {
                            rlNothing.setVisibility(VISIBLE);
                            rlChatPlayer.setVisibility(VISIBLE);
                            flSeekbar.setVisibility(VISIBLE);
                            ivPlayPlayer.setVisibility(View.GONE);
                            ivPausePlayer.setVisibility(VISIBLE);
                            ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
                            ChatActivity.tvNumPlayer.setText(holder.tvNum.getText().toString().trim());
                            str = "(1" + " of " + ParseContents.sharedAudioList.size() + ")";
                            holder.tvNum.setText(str);
                            try {
                                if (mp.isPlaying()) {
                                    try {
                                        mp.reset();
                                        mp.release();
                                        mp = null;
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }


                            mp = new MediaPlayer();
                            pos = position;
                            try {
                                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            mp.setDataSource(ParseContents.sharedAudioList.get(count).getRecordingUrl());
                            mp.prepareAsync();
//                            mp.start();
//                            progressDialog.dismiss();
//                            holder.primarySeekBarProgressUpdater();
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                    progressDialog.dismiss();
                                    holder.primarySeekBarProgressUpdater();
                                }
                            });
                            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    progressDialog.dismiss();
                                    return false;

                                }
                            });
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mHandler1.removeCallbacksAndMessages(null);
                                    seekBarChata.setProgress(0);
                                    progressDialog.dismiss();
                                    mp.reset();
                                    mp.release();
                                }
                            });

//                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                    mp.start();
//                                    holder.progressDialog.dismiss();
//                                    holder.primarySeekBarProgressUpdater();
//                                }
//                            });
//                            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                                @Override
//                                public boolean onError(MediaPlayer mp, int what, int extra) {
//                                    if (mp != null) {
//                                        try {
//                                            mp.stop();
//                                            mp.release();
//                                        } catch (Exception ex) {
//                                            ex.printStackTrace();
//                                        }
//                                    }
//                                    holder.progressDialog.dismiss();
//                                    return false;
//                                }
//                            });
//                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    mHandler1.removeCallbacksAndMessages(null);
//                                    seekBarChata.setProgress(0);
//                                    holder.progressDialog.dismiss();
//                                    mp.stop();
//                                    mp.release();
//                                }
//                            });
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
                    holder.ivPlay.performClick();
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
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            });

            ChatActivity.rlPrevPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ParseContents.sharedAudioList.size() > 0) {
                        ParseContents.sharedAudioList.clear();
                    }
                    pos = pos - 1;
                    if (pos < getItemCount()) {
                        new ParseContents(getApplicationContext()).parseSharedJoin(pos, chatList);
                        if (ParseContents.sharedAudioList.size() != 0) {
                            // holder.progressDialog = new ProgressDialog(view.getContext());
                            progressDialog.setMessage("Loading...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            try {
                                Log.d("Shared audio URL :-------", "" + ParseContents.sharedAudioList.get(count).getRecordingUrl());
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            try {
                                if (ParseContents.sharedAudioList.get(count).getRecordingUrl() != null) {
                                    rlNothing.setVisibility(VISIBLE);
                                    rlChatPlayer.setVisibility(VISIBLE);
                                    flSeekbar.setVisibility(VISIBLE);
                                    ivPlayPlayer.setVisibility(View.GONE);
                                    ivPausePlayer.setVisibility(VISIBLE);
                                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
                                    ChatActivity.tvNumPlayer.setText(holder.tvNum.getText().toString().trim());
                                    str = "(1" + " of " + ParseContents.sharedAudioList.size() + ")";
                                    holder.tvNum.setText(str);
                                    try {
                                        if (mp.isPlaying()) {
                                            try {
                                                //   mp.stop();
                                                mp.release();
                                                mp = null;
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
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
                                    mp.setDataSource(ParseContents.sharedAudioList.get(count).getRecordingUrl());
                                    mp.prepareAsync();

                                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                            progressDialog.dismiss();
                                            holder.primarySeekBarProgressUpdater();
                                        }
                                    });
                                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mp, int what, int extra) {
                                            if (mp != null) {
                                                try {
                                                    //  mp.stop();
                                                    mp.release();
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                            progressDialog.dismiss();
                                            return false;
                                        }
                                    });
                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            mHandler1.removeCallbacksAndMessages(null);
                                            seekBarChata.setProgress(0);
                                            progressDialog.dismiss();
                                            //  mp.stop();
                                            mp.release();
                                        }
                                    });


//                                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                        @Override
//                                        public void onPrepared(MediaPlayer mp) {
//                                            mp.start();
//                                            holder.progressDialog.dismiss();
//                                            holder.primarySeekBarProgressUpdater();
//                                        }
//                                    });
//                                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                                        @Override
//                                        public boolean onError(MediaPlayer mp, int what, int extra) {
//                                            if (mp != null) {
//                                                try {
//                                                    mp.stop();
//                                                    mp.release();
//                                                } catch (Exception ex) {
//                                                    ex.printStackTrace();
//                                                }
//                                            }
//                                            holder.progressDialog.dismiss();
//                                            return false;
//                                        }
//                                    });
//                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                        @Override
//                                        public void onCompletion(MediaPlayer mp) {
//                                            mHandler1.removeCallbacksAndMessages(null);
//                                            seekBarChata.setProgress(0);
//                                            holder.progressDialog.dismiss();
//                                            mp.stop();
//                                            mp.release();
//                                        }
//                                    });
                                }
                            } catch (Exception ex) {
                                progressDialog.dismiss();
                                ex.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "Sorry no recording found", Toast.LENGTH_SHORT).show();
                            ChatActivity.rlPrevPlayer.setEnabled(false);
                            ChatActivity.rlNextPlayer.setEnabled(true);
                        }
                    }
                }
            });

            ChatActivity.rlNextPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ParseContents.sharedAudioList.size() > 0) {
                        ParseContents.sharedAudioList.clear();
                    }
                    pos = pos + 1;
                    if (pos < getItemCount()) {
                        new ParseContents(getApplicationContext()).parseSharedJoin(pos, chatList);
                        if (ParseContents.sharedAudioList.size() != 0) {
                            // holder.progressDialog = new ProgressDialog(view.getContext());
                            progressDialog.setMessage("Loading...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            try {
                                Log.d("Shared audio URL :-------", "" + ParseContents.sharedAudioList.get(count).getRecordingUrl());
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                            try {
                                if (ParseContents.sharedAudioList.get(count).getRecordingUrl() != null) {
                                    rlNothing.setVisibility(VISIBLE);
                                    rlChatPlayer.setVisibility(VISIBLE);
                                    flSeekbar.setVisibility(VISIBLE);
                                    ivPlayPlayer.setVisibility(View.GONE);
                                    ivPausePlayer.setVisibility(VISIBLE);
                                    ChatActivity.tvAudioNamePlayer.setText(holder.tvMelodyName.getText().toString().trim());
                                    ChatActivity.tvNumPlayer.setText(holder.tvNum.getText().toString().trim());
                                    str = "(1" + " of " + ParseContents.sharedAudioList.size() + ")";
                                    holder.tvNum.setText(str);
                                    try {
                                        if (mp.isPlaying()) {
                                            try {
                                                //   mp.stop();
                                                mp.release();
                                                mp = null;
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
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
                                    mp.setDataSource(ParseContents.sharedAudioList.get(count).getRecordingUrl());
                                    mp.prepareAsync();

                                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                            progressDialog.dismiss();
                                            holder.primarySeekBarProgressUpdater();
                                        }
                                    });
                                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mp, int what, int extra) {
                                            if (mp != null) {
                                                try {
                                                    //mp.stop();
                                                    mp.release();
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                            progressDialog.dismiss();
                                            return false;
                                        }
                                    });
                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            mHandler1.removeCallbacksAndMessages(null);
                                            seekBarChata.setProgress(0);
                                            progressDialog.dismiss();
                                            //  mp.stop();
                                            mp.release();
                                        }
                                    });

//                                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                        @Override
//                                        public void onPrepared(MediaPlayer mp) {
//                                            mp.start();
//                                            holder.progressDialog.dismiss();
//                                            holder.primarySeekBarProgressUpdater();
//                                        }
//                                    });
//                                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                                        @Override
//                                        public boolean onError(MediaPlayer mp, int what, int extra) {
//                                            if (mp != null) {
//                                                try {
//                                                    mp.stop();
//                                                    mp.release();
//                                                } catch (Exception ex) {
//                                                    ex.printStackTrace();
//                                                }
//                                            }
//                                            holder.progressDialog.dismiss();
//                                            return false;
//                                        }
//                                    });
//                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                        @Override
//                                        public void onCompletion(MediaPlayer mp) {
//                                            mHandler1.removeCallbacksAndMessages(null);
//                                            seekBarChata.setProgress(0);
//                                            holder.progressDialog.dismiss();
//                                            mp.stop();
//                                            mp.release();
//                                        }
//                                    });
                                }
                            } catch (Exception ex) {
                                progressDialog.dismiss();
                                ex.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "Sorry no recording found", Toast.LENGTH_SHORT).show();
                            ChatActivity.rlNextPlayer.setEnabled(false);
                            ChatActivity.rlPrevPlayer.setEnabled(true);
                        }
                    }
                }
            });

            holder.ivSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, JoinActivity.class);
                        context.startActivity(intent);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                }
            });

        } else if (itemType == SELF_IMAGE || itemType == OTHER_IMAGE) {
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
        } else {
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
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
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
}