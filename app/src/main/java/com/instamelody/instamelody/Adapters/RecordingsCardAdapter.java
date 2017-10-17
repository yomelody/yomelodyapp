package com.instamelody.instamelody.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.MessengerActivity;
import com.instamelody.instamelody.Models.JoinRecordingModel;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.StationCommentActivity;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.JoinRecording;
import static com.instamelody.instamelody.utils.Const.ServiceType.LIKESAPI;
import static com.instamelody.instamelody.utils.Const.ServiceType.PLAY_COUNT;

//import com.instamelody.instamelody.Models.RecJoinRecordingModel;

/**
 * Created by Saurabh Singh on 12//2016.
 * <p>
 * this will be used in recordings fragment
 */

public class RecordingsCardAdapter extends RecyclerView.Adapter<RecordingsCardAdapter.MyViewHolder> {

    public static final int REQUEST_RECORDING_COMMENT = 711;
    public static final int REQUEST_JOIN_COMMENT = 712;
    String genreName, mpid, MelodyName, profile;
    static String instrumentFile;
    public static MediaPlayer mp;
    int duration1, currentPosition;
    int length;
    ArrayList<String> mpids = new ArrayList<>();
    private ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    private ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    public static ArrayList<JoinRecordingModel> JoinList = new ArrayList<JoinRecordingModel>();
    //    public static ArrayList<RecJoinRecordingModel> RecJoinList = new ArrayList<RecJoinRecordingModel>();
    public ArrayList<MediaPlayer> JoinMp = new ArrayList<MediaPlayer>();
    String USER_TYPE = "user_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String LIKES = "likes";
    String TYPE = "type";
    String USERID = "userid";
    String FILEID = "fileid";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    String Topic = "topic";
    String Key_shared_by_user = "shared_by_user";
    String Key_shared_with = "shared_with";
    String Key_file_type = "file_type";
    Context context;
    String userId = "";
    String addedBy, Rec_id, profile_image, RecordingName, userNameRec;
    private RecyclerView.ViewHolder lastModifiedHoled = null;
    int lazycount = 0;
    private String ID = "id";
    private String KEY = "key";
    private String STATION = "station";
    private String GENRE = "genere";
    int JoinCount = 0;
    MediaPlayer JoinPlay;
    int currentSongIndex = 0;
    String position;
    String totaljoincount = "";
    int MinJoinCount = 0;
    int MaxJoinCount = 0;
    private Activity mActivity;
    Handler mHandler1;

    public RecordingsCardAdapter(Context context, ArrayList<RecordingsModel> recordingList, ArrayList<RecordingsPool> recordingsPools) {
        this.recordingList = recordingList;
        this.recordingsPools = recordingsPools;
        this.lazycount = recordingList.size();
        this.context = context;
        mActivity = (Activity) context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
        TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount, txtJoinCount, TemptxtJoinCount;
        ImageView userProfileImage, ivRecordingCover, ivLikeButton, ivCommentButton, ivShareButton, ivDislikeButton, ivStationPre, ivStationNext;
        ImageView ivJoin, ivStationPlay, ivStationPause;
        SeekBar seekBarRecordings;
        RelativeLayout rlProfilePic, rlLike;
        ProgressDialog progressDialog;
        MediaPlayer mediaPlayerJoin;

        public MyViewHolder(View itemView) {
            super(itemView);
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            ivRecordingCover = (ImageView) itemView.findViewById(R.id.ivRecordingCover);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvRecordingName = (TextView) itemView.findViewById(R.id.tvRecordingName);
            tvRecordingDate = (TextView) itemView.findViewById(R.id.tvRecordingDate);
            tvRecordingGenres = (TextView) itemView.findViewById(R.id.tvRecordingGenres);
            tvContributeDate = (TextView) itemView.findViewById(R.id.tvContributeDate);
            tvContributeLength = (TextView) itemView.findViewById(R.id.tvContributeLength);
            tvIncludedCount = (TextView) itemView.findViewById(R.id.tvIncludedCount);
            tvViewCount = (TextView) itemView.findViewById(R.id.tvViewCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
            tvShareCount = (TextView) itemView.findViewById(R.id.tvShareCount);
            ivJoin = (ImageView) itemView.findViewById(R.id.ivJoin);
            ivStationPlay = (ImageView) itemView.findViewById(R.id.ivStationPlay);
            ivStationPause = (ImageView) itemView.findViewById(R.id.ivStationPause);
            seekBarRecordings = (SeekBar) itemView.findViewById(R.id.seekBarRecordings);
            rlProfilePic = (RelativeLayout) itemView.findViewById(R.id.rlProfilePic);
            ivLikeButton = (ImageView) itemView.findViewById(R.id.ivLikeButton);
            ivDislikeButton = (ImageView) itemView.findViewById(R.id.ivDislikeButton);
            ivCommentButton = (ImageView) itemView.findViewById(R.id.ivCommentButton);
            ivShareButton = (ImageView) itemView.findViewById(R.id.ivShareButton);
            rlLike = (RelativeLayout) itemView.findViewById(R.id.rlLike);
            ivStationPre = (ImageView) itemView.findViewById(R.id.ivStationPre);
            ivStationNext = (ImageView) itemView.findViewById(R.id.ivStationNext);
            txtJoinCount = (TextView) itemView.findViewById(R.id.txtJoinCount);
            TemptxtJoinCount = (TextView) itemView.findViewById(R.id.TemptxtJoinCount);
            SharedPreferences editorGenre = getApplicationContext().getSharedPreferences("prefGenreName", MODE_PRIVATE);
            genreName = editorGenre.getString("GenreName", null);

            SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
            SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
            SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

            if (loginSharedPref.getString("userId", null) != null) {
                userId = loginSharedPref.getString("userId", null);
            } else if (fbPref.getString("userId", null) != null) {
                userId = fbPref.getString("userId", null);
            } else if (twitterPref.getString("userId", null) != null) {
                userId = twitterPref.getString("userId", null);
            }


            ivJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!userId.equals("") && userId != null) {
                        String instruments, bpm, genre, recordName, userName, duration, date, plays, likes, comments, shares, melodyID, LikeStatus;
                        RecordingsModel rm = recordingList.get(getAdapterPosition());
                        RecordingsModel recording = recordingList.get(getAdapterPosition());

                        if (ivDislikeButton.getVisibility() == VISIBLE) {
                            LikeStatus = "1";
                        } else {
                            LikeStatus = "0";
                        }
                        addedBy = rm.getAddedBy();
                        Rec_id = rm.getRecordingId();
                        userNameRec = rm.getUserName();
                        profile_image = rm.getUserProfilePic();
                        RecordingName = rm.getRecordingName();

                        genre = tvRecordingGenres.getText().toString().trim();
                        recordName = tvRecordingName.getText().toString().trim();
                        duration = tvContributeLength.getText().toString().trim();
                        date = tvRecordingDate.getText().toString().trim();
                        plays = tvViewCount.getText().toString().trim();
                        userName = tvUserName.getText().toString().trim();
                        likes = tvLikeCount.getText().toString().trim();
                        comments = tvCommentCount.getText().toString().trim();
                        shares = tvShareCount.getText().toString().trim();

                        int pos = getAdapterPosition();

                        SharedPreferences.Editor editor = context.getSharedPreferences("commentData", MODE_PRIVATE).edit();


                        editor.putString("genre", genre);
                        editor.putString("melodyName", recordName);
                        editor.putString("userName", userName);
                        editor.putString("duration", duration);
                        editor.putString("date", date);
                        editor.putString("plays", plays);
                        editor.putString("likes", likes);
                        editor.putString("comments", comments);
                        editor.putString("shares", shares);
//                    editor.putString("bitmapProfile", profile);
//                    editor.putString("bitmapCover", cover);
//                    editor.putString("melodyID", );
                        editor.putString("fileType", "admin_melody");
                        editor.commit();


                        try {
                            SharedPreferences.Editor record = context.getSharedPreferences("RecordingData", MODE_PRIVATE).edit();
                            record.putString("AddedBy", addedBy);
                            record.putString("Recording_id", Rec_id);
                            record.putString("UserNameRec", userNameRec);
                            record.putString("UserProfile", profile_image);
                            record.putString("RecordingName", RecordingName);
                            record.commit();
                            Intent intent = new Intent(context, JoinActivity.class);
                            mActivity.startActivityForResult(intent, REQUEST_JOIN_COMMENT);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(context, "Log in to join this Recording", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }


                }
            });
            rlLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String position;

                    String MelodyName;
                    if (!userId.equals("") && userId != null) {
                        //Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
                        //position = mpids.get(getAdapterPosition() + 1);

                        RecordingsModel recording = recordingList.get(getAdapterPosition());
                        MelodyName = recording.getRecordingName();
                        position = recording.getRecordingId();

                        if (ivDislikeButton.getVisibility() == VISIBLE) {
                            ivLikeButton.setVisibility(VISIBLE);
                            ivDislikeButton.setVisibility(GONE);
                            String like = tvLikeCount.getText().toString().trim();
                            int likeValue = Integer.parseInt(like) - 1;
                            like = String.valueOf(likeValue);
                            tvLikeCount.setText(like);
                            fetchLikeState(userId, position, "0", MelodyName);


                        } else if (ivDislikeButton.getVisibility() == GONE) {
                            ivLikeButton.setVisibility(GONE);
                            ivDislikeButton.setVisibility(VISIBLE);
                            String like = tvLikeCount.getText().toString().trim();
                            int likeValue = Integer.parseInt(like) + 1;
                            like = String.valueOf(likeValue);
                            tvLikeCount.setText(like);
                            fetchLikeState(userId, position, "1", MelodyName);
                        }
                    } else {
                        Toast.makeText(context, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }
                }
            });

            ivShareButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!userId.equals("") && userId != null) {

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Share with InstaMelody chat?");
//                        alertDialog.setMessage("Choose yes to share in chat.");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("audioShareData", MODE_PRIVATE).edit();
                                RecordingsModel recording = recordingList.get(getAdapterPosition());
                                editor.putString("recID", recording.getRecordingId());
                                editor.apply();
                                Intent intent = new Intent(mActivity, MessengerActivity.class);
                                intent.putExtra("Previous", "station");
                                intent.putExtra("share", recordingList.get(getAdapterPosition()));
                                intent.putExtra("file_type", "user_recording");
                                context.startActivity(intent);
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                RecordingsModel recording = recordingList.get(getAdapterPosition());
                                String RecordingURL = recording.getrecordingurl();
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "InstaMelody Music Hunt");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, RecordingURL);
                                shareIntent.setType("image/jpeg");
                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(Intent.createChooser(shareIntent, "Choose Sharing option!"));
                            }
                        });
                        alertDialog.show();

                    } else {
                        Toast.makeText(context, "Log in to Share this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }

                }
            });
            ivCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!userId.equals("") && userId != null) {
                        //Toast.makeText(context, "comment", Toast.LENGTH_SHORT).show();
                        String instruments, bpm, genre, melodyName, userName, duration, date, plays, likes,
                                comments, shares, melodyID, RecordingURL, CoverUrl, LikeStatus, ProfilePick;

                        RecordingsModel recording = recordingList.get(getAdapterPosition());

                        if (ivDislikeButton.getVisibility() == VISIBLE) {
                            LikeStatus = "1";
                        } else {
                            LikeStatus = "0";
                        }
                        genre = tvRecordingGenres.getText().toString().trim();
                        melodyName = tvRecordingName.getText().toString().trim();
                        userName = tvUserName.getText().toString().trim();
                        duration = tvContributeLength.getText().toString().trim();
                        date = tvRecordingDate.getText().toString().trim();
                        plays = tvViewCount.getText().toString().trim();
                        likes = tvLikeCount.getText().toString().trim();
                        comments = tvCommentCount.getText().toString().trim();
                        shares = tvShareCount.getText().toString().trim();
                        int pos = getAdapterPosition();
                        melodyID = mpids.get(pos);
                        RecordingURL = recording.getrecordingurl();
                        CoverUrl = recording.getRecordingCover();
                        ProfilePick = recording.getUserProfilePic();
                        SharedPreferences.Editor editor = context.getSharedPreferences("commentData", MODE_PRIVATE).edit();
                        editor.putString("instruments", "0");
                        editor.putString("bpm", "0");
                        editor.putString("genre", genre);
                        editor.putString("melodyName", melodyName);
                        editor.putString("userName", userName);
                        editor.putString("duration", duration);
                        editor.putString("date", date);
                        editor.putString("plays", plays);
                        editor.putString("likes", likes);
                        editor.putString("comments", comments);
                        editor.putString("shares", shares);
                        editor.putString("bitmapProfile", ProfilePick);
                        editor.putString("melodyID", melodyID);
                        editor.putString("fileType", "user_recording");
                        editor.putString("RecordingURL", RecordingURL);
                        editor.putString("CoverUrl", CoverUrl);
                        editor.putString("LikeStatus", LikeStatus);
                        editor.commit();

                        Intent intent = new Intent(context, StationCommentActivity.class);
                        intent.putExtra("likes", likes);
                        intent.putExtra("LikeStatus", LikeStatus);
                        intent.putExtra("recording_modle", recording);
                        intent.putExtra("recording_pool", recordingsPools.get(getAdapterPosition()));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mActivity.startActivityForResult(intent, REQUEST_RECORDING_COMMENT);

                        /*if(context instanceof StationActivity){

                        }
                        else {
                            Intent intent = new Intent(context, CommentsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }*/

                    } else {
                        Toast.makeText(context, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }

                }
            });

            if (!(context instanceof ProfileActivity)) {
                rlProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String showProfileUserId = recordingList.get(getAdapterPosition()).getAddedBy();
                        Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                        intent.putExtra("showProfileUserId", showProfileUserId);
                        view.getContext().startActivity(intent);
                    }
                });
            }


            seekBarRecordings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    int mCurrentPosition = currentPosition / 1000;
                    int mDuration = duration1 / 1000;
                    UtilsRecording utilRecording = new UtilsRecording();
                    int progress1 = utilRecording.getProgressPercentage(mCurrentPosition, mDuration);

                    if (mp != null && fromUser) {
                        try {
                            int playPositionInMilliseconds = duration1 / 100 * seekBarRecordings.getProgress();
                            mp.seekTo(playPositionInMilliseconds);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }

//                        seekBar.setProgress(progress);
                    } else {
                        // the event was fired from code and you shouldn't call player.seekTo()
                    }

//
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        private void primarySeekBarProgressUpdater() {
            mHandler1 = new Handler();
            try {
                seekBarRecordings.setProgress((int) (((float) mp.getCurrentPosition() / duration1) * 100));// This math construction give a percentage of "was playing"/"song length"
                if (mp.isPlaying()) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            primarySeekBarProgressUpdater();
                        }
                    };
                    mHandler1.postDelayed(notification, 100);
                } else {
                    try {
                        seekBarRecordings.setProgress(0);
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recordings, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        RecordingsModel recording = recordingList.get(listPosition);
        lazycount = lazycount + 1;
        int includedCount = Integer.parseInt(recordingList.get(listPosition).getJoinCount());
        holder.tvIncludedCount.setText("Included: " + includedCount);

        mpid = recording.getRecordingId();
        mpids.add(mpid);

        holder.tvIncludedCount.setText("Included: " + includedCount);
        if (recordingList.get(listPosition).getJoinCount() == null) {
            totaljoincount = "(" + "0" + " of " + "1" + ")";
        } else {
            totaljoincount = CalJoinCount(Integer.parseInt(recordingList.get(listPosition).getJoinCount()));
        }

        if (recordingList.get(listPosition).getJoinCount() != null) {
            holder.TemptxtJoinCount.setText(recordingList.get(listPosition).getJoinCount());
        } else {
            holder.TemptxtJoinCount.setText("0");
        }


        holder.txtJoinCount.setText(totaljoincount);

        holder.txtJoinCount.setText(totaljoincount);
        Picasso.with(context).load(recordingList.get(listPosition).getUserProfilePic()).into(holder.userProfileImage);

        holder.tvUserName.setText(recordingList.get(listPosition).getUserName());
        holder.tvRecordingName.setText(recordingList.get(listPosition).getRecordingName());
        holder.tvRecordingGenres.setText("Genre:" + " " + recording.getGenreName());
//        holder.tvContributeLength.setText(recordingsPools.get(listPosition).getDuration());
//        holder.tvContributeDate.setText(recordingsPools.get(listPosition).getDateAdded());
//        holder.tvContributeDate.setText(recordingList.get(listPosition).getRecordingCreated());
        try {
            holder.tvContributeDate.setText(convertDate(recordingList.get(listPosition).getRecordingCreated()));
            holder.tvContributeLength.setText(DateUtils.formatElapsedTime(Long.parseLong(recordingsPools.get(listPosition).getDuration())));
        } catch (Throwable e) {
            e.printStackTrace();
        }



        int likeStatus = recordingList.get(listPosition).getLikeStatus();
        if (likeStatus == 0) {
            holder.ivDislikeButton.setVisibility(GONE);
        } else {
            holder.ivDislikeButton.setVisibility(VISIBLE);
        }
        try {
            holder.tvRecordingDate.setText(convertDate(recordingList.get(listPosition).getRecordingCreated()));
        } catch (Throwable e) {
            e.printStackTrace();
        }

        holder.tvViewCount.setText(String.valueOf(recordingList.get(listPosition).getPlayCount()));
        holder.tvLikeCount.setText(String.valueOf(recordingList.get(listPosition).getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(recordingList.get(listPosition).getCommentCount()));
        holder.tvShareCount.setText(String.valueOf(recordingList.get(listPosition).getShareCount()));

        holder.ivStationPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.progressDialog = new ProgressDialog(v.getContext());
                holder.progressDialog.setMessage("Loading...");
                holder.progressDialog.show();
                //currentSongIndex = currentSongIndex + 1;

                fetchPlayJoinAudio(recordingList.get(listPosition).getRecordingId());
                holder.ivStationPause.setVisibility(View.VISIBLE);
                //  try {
//                RecordingsPool recordingsPool = recordingsPools.get(listPosition);
//                instrumentFile = recordingsPool.getRecordingUrl();
                instrumentFile = recordingList.get(listPosition).getrecordingurl();
                Integer s = listPosition + 1;

                if (instrumentFile != "") {
                    try {
                        if (mp.isPlaying()) {

                            try {
                                mHandler1.removeCallbacksAndMessages(null);

                                try {
                                    mp.stop();
                                    mp.release();
                                    mp = null;
                                    duration1 = 0;
                                    length = 0;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                if (lastModifiedHoled != null) {
                                    int lastPosition = lastModifiedHoled.getAdapterPosition();
                                    lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                                    lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);

                                    //   notifyItemChanged(lastPosition);
                                }


                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    if (holder.ivStationPause.getVisibility() == VISIBLE) {
                        try {
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

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
                    }
                    String play = holder.tvViewCount.getText().toString().trim();
                    int playValue = Integer.parseInt(play) + 1;
                    play = String.valueOf(playValue);
                    holder.tvViewCount.setText(play);
                    position = recordingList.get(listPosition).getRecordingId();
                    fetchViewCount(userId, position);

                    mp.prepareAsync();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            holder.progressDialog.dismiss();
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(GONE);
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(VISIBLE);
                            mp.seekTo(length);
                            mp.start();
                            duration1 = mp.getDuration();
                            holder.primarySeekBarProgressUpdater();

                        }
                    });
                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            holder.progressDialog.dismiss();


                            try {
                                mHandler1.removeCallbacksAndMessages(null);
                                mp.stop();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            return false;
                        }
                    });
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            mHandler1.removeCallbacksAndMessages(null);
                            holder.progressDialog.dismiss();
                            holder.seekBarRecordings.setProgress(0);
                            length = 0;
                            duration1 = 0;
                            holder.ivStationPause.setVisibility(GONE);
                            holder.ivStationPlay.setVisibility(VISIBLE);

                        }
                    });


                } else {
                    holder.progressDialog.dismiss();
                    Toast.makeText(context, "Recording URL not found", Toast.LENGTH_SHORT).show();

                    // lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                    //lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                }
                try {
                    lastModifiedHoled = holder;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        holder.ivStationPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivStationPlay.setVisibility(v.VISIBLE);
                holder.ivStationPause.setVisibility(v.GONE);
                mHandler1.removeCallbacksAndMessages(null);
                try {
                    mp.pause();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                length = mp.getCurrentPosition();
                //holder.seekBarRecordings.setProgress(0);
            }
        });

        if (listPosition > 10) {

        }
        holder.ivStationPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    MinJoinCount = MinJoinCount - 1;

                    int NxtCount = Integer.parseInt(holder.TemptxtJoinCount.getText().toString());

                    if (MinJoinCount < NxtCount && MinJoinCount >= 0) {
                        fetchPlayJoinAudio(recordingList.get(listPosition).getRecordingId());
                        if (JoinMp != null) {
                            if (JoinMp.size() > 0) {
                                try {
                                    if (mp != null) {
                                        if (mp.isPlaying()) {
                                            try {
                                                mp.stop();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }


                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }


                                holder.progressDialog = new ProgressDialog(v.getContext());
                                holder.progressDialog.setMessage("Loading...");
                                holder.progressDialog.setCancelable(false);
                                holder.progressDialog.show();
                                mp = new MediaPlayer();
                                try {
                                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                //currentSongIndex = currentSongIndex - 1;

                                for (int i = 0; i <= JoinMp.size() - 1; i++) {

                                    if (MinJoinCount == i) {

                                        mp = JoinMp.get(i);


                                    }
                                }
                                mp.prepareAsync();
                                holder.txtJoinCount.setText(CalJoinCountPrevRec(NxtCount));

                                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        holder.progressDialog.dismiss();
                                        lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(GONE);
                                        lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(VISIBLE);

                                        holder.primarySeekBarProgressUpdater();

                                    }
                                });
                                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                    @Override
                                    public boolean onError(MediaPlayer mp, int what, int extra) {
                                        if (mp != null) {
                                            try {
                                                mp.stop();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                        for (int i = 0; i <= JoinMp.size() - 1; i++) {
                                            if (JoinMp.get(i).isPlaying()) {
                                                try {
                                                    JoinMp.get(i).stop();
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                        holder.progressDialog.dismiss();

                                        return false;
                                    }
                                });
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mHandler1.removeCallbacksAndMessages(null);
                                        if (mp != null) {
                                            try {
                                                mp.stop();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                        for (int i = 0; i <= JoinMp.size() - 1; i++) {
                                            if (JoinMp.get(i).isPlaying()) {
                                                try {
                                                    JoinMp.get(i).stop();
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                        holder.progressDialog.dismiss();
                                        holder.seekBarRecordings.setProgress(0);
                                        holder.ivStationPause.setVisibility(GONE);
                                        holder.ivStationPlay.setVisibility(VISIBLE);

                                    }
                                });
                            }
                        }
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        holder.ivStationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (MinJoinCount < 0) {
                        MinJoinCount = 0;
                    }
                    int NxtCount = Integer.parseInt(holder.TemptxtJoinCount.getText().toString());
                    if (MinJoinCount < NxtCount && MinJoinCount >= 0) {
                        fetchPlayJoinAudio(recordingList.get(listPosition).getRecordingId());
                        if (JoinMp != null) {
                            if (JoinMp.size() > 0) {
                                try {
                                    if (mp != null) {
                                        if (mp.isPlaying()) {
                                            mp.stop();

                                        }

                                    }

                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                holder.progressDialog = new ProgressDialog(v.getContext());
                                holder.progressDialog.setMessage("Loading...");
                                holder.progressDialog.setCancelable(false);
                                holder.progressDialog.show();
                                mp = new MediaPlayer();
                                try {
                                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                for (int i = 0; i <= JoinMp.size() - 1; i++) {

                                    if (MinJoinCount == i) {
                                        //currentSongIndex = currentSongIndex + 1;
                                        mp = JoinMp.get(i);
                                    }
                                }
                                mp.prepareAsync();
                                holder.txtJoinCount.setText(CalJoinCountNextRec(NxtCount));


                                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        try {
                                            mp.start();
                                            holder.progressDialog.dismiss();
                                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(GONE);
                                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(VISIBLE);

                                            holder.primarySeekBarProgressUpdater();
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });
                                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                    @Override
                                    public boolean onError(MediaPlayer mp, int what, int extra) {

                                        if (mp != null) {
                                            mp.stop();
                                        }
                                        for (int i = 0; i <= JoinMp.size() - 1; i++) {
                                            if (JoinMp.get(i).isPlaying()) {
                                                JoinMp.get(i).stop();
                                            }
                                        }
                                        holder.progressDialog.dismiss();
                                        return false;
                                    }
                                });
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mHandler1.removeCallbacksAndMessages(null);
                                        if (mp != null) {
                                            try {
                                                mp.stop();
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                        for (int i = 0; i <= JoinMp.size() - 1; i++) {
                                            if (JoinMp.get(i).isPlaying()) {
                                                try {
                                                    JoinMp.get(i).stop();
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                        holder.progressDialog.dismiss();
                                        holder.seekBarRecordings.setProgress(0);
                                        holder.ivStationPause.setVisibility(GONE);
                                        holder.ivStationPlay.setVisibility(VISIBLE);

                                    }
                                });


                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return recordingList.size();
    }


    private void killMediaPlayer() {
        if (mp != null) {
            try {
                mp.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchLikeState(final String userId, final String pos, final String likeState, String LikeMelodyName) {

        try {
            MelodyName = LikeMelodyName;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LIKESAPI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                            String errorMsg = error.toString();
                            Log.d("Error", errorMsg);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Topic, MelodyName);
                    params.put(USER_ID, userId);
                    params.put(FILE_ID, pos);
                    params.put(TYPE, "user_recording");
                    params.put(LIKES, likeState);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(context);
            requestQueue1.add(stringRequest);
        } catch (Exception ex) {

        }

    }


    public void fetchViewCount(final String userId, final String pos) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAY_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //       Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject, respObject;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                respObject = jsonObject.getJSONObject(KEY_RESPONSE);
                                String str = respObject.getString("play_count");
                                //      Toast.makeText(context, "" + str, Toast.LENGTH_SHORT).show();
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
                params.put(USER_TYPE, "user");
                params.put(USERID, userId);
                params.put(FILEID, pos);
                //    params.put(TYPE, "admin_melody");
                params.put(TYPE, "recording");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
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
                            currentSongIndex = 0;
                            JoinMp.clear();

                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equals("success")) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                    respObject = jsonArray.getJSONObject(i);
                                    //JoinList.add(i, new JoinRecordingModel(respObject.getString("recording_duration"), respObject.getString("recording_url")));
                                    try {
                                        MediaPlayer mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mediaPlayer.setDataSource(respObject.getString("recording_url"));
                                        //mediaPlayer.prepare();
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

    private String CalJoinCount(int joincount) {
        String jCount = "";
        try {
            if (joincount == 0) {
                //currentSongIndex=1;
                jCount = "(" + String.valueOf(MinJoinCount) + " of " + String.valueOf((joincount + 1)) + ")";
            } else {

                jCount = String.valueOf(MinJoinCount) + " of " + String.valueOf(joincount);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

    private String CalJoinCountNextRec(int Nextcount) {
        String jCount = "";
        try {
            MinJoinCount = MinJoinCount + 1;
            String tempN = String.valueOf(MinJoinCount);
            jCount = "(" + (tempN) + " of " + String.valueOf(Nextcount) + ")";


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

    private String CalJoinCountPrevRec(int Precount) {
        String jCount = "";
        try {
            //MinJoinCount=MinJoinCount-1;
            String tempP = String.valueOf(MinJoinCount);
            jCount = "(" + (tempP) + " of " + String.valueOf(Precount) + ")";


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

    public void releaseMediaPlayer() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    private String convertDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
        SimpleDateFormat serverFormat = new SimpleDateFormat("MM/dd/yy");
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ;

        return serverFormat.format(d);
    }

}
