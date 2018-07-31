package com.yomelody.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yomelody.Fragments.AudioFragment;
import com.yomelody.JoinActivity;
import com.yomelody.MelodyActivity;
import com.yomelody.MessengerActivity;
import com.yomelody.Models.JoinRecordingModel;
import com.yomelody.Models.RecordingsModel;
import com.yomelody.Models.RecordingsPool;
import com.yomelody.ProfileActivity;
import com.yomelody.R;
import com.yomelody.SignInActivity;
import com.yomelody.StationActivity;
import com.yomelody.StationCommentActivity;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.JoinRecording;
import static com.yomelody.utils.Const.ServiceType.LIKESAPI;
import static com.yomelody.utils.Const.ServiceType.PLAY_COUNT;
import static com.yomelody.utils.Const.ServiceType.PUBLIC;

//import com.instamelody.instamelody.Models.RecJoinRecordingModel;

/**
 * Created by Saurabh Singh on 12//2016.
 * <p>
 * this will be used in recordings fragment
 */

public class RecordingsCardAdapter extends RecyclerView.Adapter<RecordingsCardAdapter.MyViewHolder> {

    public static final int REQUEST_RECORDING_COMMENT = 711;
    public static final int REQUEST_JOIN_COMMENT = 712;
    public static final int REQUEST_PROFILE_COMMENT = 713;
    String genreName, mpid, MelodyName, profile;
    static String instrumentFile;
    public static MediaPlayer mp = null;
    int duration1, currentPosition;
    int length;
    int includedCount = 1, TempJoinCount = 0;
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
    private int currentSongIndex = 0;
    private String position;
    private String totaljoincount = "";
    private int MinJoinCount = 1, HolderJoinCount = 1;
    private int PlayCounter = 0;
    private int PreviousAdapterIndex = 0, CurrentAdapterIndex = 0, FirstIndex = 0;
    int PreAdapterPos = 0;
    int MaxJoinCount = 0;
    private Activity mActivity;
    Handler mHandler1;
    boolean IsLiked = false;
    boolean isLastInstrument = false;
    private Fragment mFragment;
    private Long chronoLen;
    private long mStartTime = 0;
    private long time;

    public RecordingsCardAdapter(Context context, ArrayList<RecordingsModel> recordingList, ArrayList<RecordingsPool> recordingsPools) {
        this.recordingList = recordingList;
        this.recordingsPools = recordingsPools;
        this.lazycount = recordingList.size();
        this.context = context;
        mActivity = (Activity) context;

    }

    public RecordingsCardAdapter(Context context, ArrayList<RecordingsModel> recordingList, ArrayList<RecordingsPool> recordingsPools, Fragment frag) {
        this.recordingList = recordingList;
        this.recordingsPools = recordingsPools;
        this.lazycount = recordingList.size();
        this.context = context;
        mActivity = (Activity) context;
        mFragment = frag;
    }


    ProgressDialog progressDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
        TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount, txtJoinCount, TemptxtJoinCount;
        ImageView userProfileImage, ivRecordingCover, ivLikeButton, ivCommentButton, ivShareButton, ivDislikeButton, ivStationPre, ivStationNext;
        ImageView ivJoin, ivStationPlay, ivStationPause;
        SeekBar seekBarRecordings;
        RelativeLayout rlProfilePic, rlLike;
        Switch publicPrivSwitch;
//        Chronometer chrono;
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
            publicPrivSwitch = (Switch) itemView.findViewById(R.id.publicPrivSwitch);
//            chrono = (Chronometer) itemView.findViewById(R.id.chrono);
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
                    try {
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
                                ivStationPlay.setVisibility(VISIBLE);
                                ivStationPause.setVisibility(GONE);
                                seekBarRecordings.setProgress(0);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(context, "Log in to join this Recording", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, SignInActivity.class);
                            context.startActivity(intent);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            rlLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            ivShareButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (!userId.equals("") && userId != null) {

                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle(mActivity.getString(R.string.share_with_YoMelody));
//                        alertDialog.setMessage("Choose yes to share in chat.");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("audioShareData", MODE_PRIVATE).edit();
                                    RecordingsModel recording = recordingList.get(getAdapterPosition());
                                    editor.putString("recID", recording.getRecordingId());
                                    editor.apply();
                                    Intent intent = new Intent(mActivity, MessengerActivity.class);
                                    intent.putExtra("commingForm", "Station");
                                    intent.putExtra("share", recordingList.get(getAdapterPosition()));
                                    intent.putExtra("file_type", "user_recording");
                                    mActivity.startActivityForResult(intent, REQUEST_RECORDING_COMMENT);
                                    releaseMediaPlayer();
                                    ivStationPlay.setVisibility(VISIBLE);
                                    ivStationPause.setVisibility(GONE);
                                    seekBarRecordings.setProgress(0);

                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    RecordingsModel recording = recordingList.get(getAdapterPosition());
                                    String RecordingURL = recording.getThumnailUrl();
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
//                                    shareIntent.setType("image/jpeg");
//                                    shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.listen_to));
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, recording.getRecordingName());
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, recording.getThumnailUrl());
                                    /*shareIntent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.listen_to)+" "+
                                            recording.getRecordingName()+"\n"+
                                            mActivity.getString(R.string.yomelody_music)
                                            +"\n"+RecordingURL);*/
                                    shareIntent.setType("text/plain");
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            ivCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!userId.equals("") && userId != null) {
                            releaseMediaPlayer();
                            ivStationPlay.setVisibility(VISIBLE);
                            ivStationPause.setVisibility(GONE);
                            seekBarRecordings.setProgress(0);

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
                            intent.putExtra("play_count", plays);
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            if (!(context instanceof ProfileActivity)) {
                rlProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String showProfileUserId = recordingList.get(getAdapterPosition()).getAddedBy();
                            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                            intent.putExtra("showProfileUserId", showProfileUserId);
                            //view.getContext().startActivity(intent);
                            mActivity.startActivityForResult(intent, REQUEST_RECORDING_COMMENT);
                            ivStationPlay.setVisibility(VISIBLE);
                            ivStationPause.setVisibility(GONE);
                            seekBarRecordings.setProgress(0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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

        try {
            RecordingsModel recording = recordingList.get(listPosition);
            lazycount = lazycount + 1;
            includedCount = Integer.parseInt(recordingList.get(listPosition).getJoinCount());
            holder.tvIncludedCount.setText("Included: " + includedCount);

            mpid = recording.getRecordingId();
            mpids.add(mpid);

            holder.tvIncludedCount.setText("Included: " + includedCount);
            if (recordingList.get(listPosition).getJoinCount() == null) {
                totaljoincount = "(" + "1" + " of " + "1" + ")";
            } else {
                totaljoincount = CalJoinCount(Integer.parseInt(recordingList.get(listPosition).getJoinCount()));
            }

            if (recordingList.get(listPosition).getJoinCount() != null) {
                holder.TemptxtJoinCount.setText(recordingList.get(listPosition).getJoinCount());
            } else {
                holder.TemptxtJoinCount.setText("0");
            }
            if (userId.equalsIgnoreCase(recordingList.get(listPosition).getAddedBy()) &&
                    ((context instanceof MelodyActivity) || (context instanceof StationActivity)
                            || (context instanceof ProfileActivity))) {
                holder.publicPrivSwitch.setVisibility(VISIBLE);
                holder.publicPrivSwitch.setOnCheckedChangeListener(null);
                if (recordingList.get(listPosition).getIsPublic().equalsIgnoreCase("1")){
                    holder.publicPrivSwitch.setChecked(true);
                }
                else {
                    holder.publicPrivSwitch.setChecked(false);
                }

                holder.publicPrivSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // do something, the isChecked will be
                        // true if the switch is in the On position
                        AppHelper.sop("isChecked="+isChecked+"=buttonView="+buttonView);
                        if (isChecked){
                            publicPrivateApi("1",recordingList.get(listPosition).getRecordingId());
                        }
                        else if (!isChecked){
                            publicPrivateApi("0",recordingList.get(listPosition).getRecordingId());
                        }
                    }
                });
            }
            else {
                holder.publicPrivSwitch.setVisibility(GONE);
            }

            holder.txtJoinCount.setText(totaljoincount);

            holder.txtJoinCount.setText(totaljoincount);

            Picasso.with(context).load(recordingList.get(listPosition).getUserProfilePic()).into(holder.userProfileImage);
            Picasso.with(context)
                    .load(recordingList.get(listPosition).getRecordingCover())
                    .placeholder(R.drawable.bg_cell)
                    .error(R.drawable.bg_cell)
                    .into(holder.ivRecordingCover);
            holder.tvUserName.setText(recordingList.get(listPosition).getUserName());
            holder.tvRecordingName.setText(recordingList.get(listPosition).getRecordingName());
            holder.tvRecordingGenres.setText("Genre:" + " " + recording.getGenreName());
//        holder.tvContributeLength.setText(recordingsPools.get(listPosition).getDuration());
//        holder.tvContributeDate.setText(recordingsPools.get(listPosition).getDateAdded());
//        holder.tvContributeDate.setText(recordingList.get(listPosition).getRecordingCreated());
            try {
                holder.tvContributeDate.setText(convertDate(recordingList.get(listPosition).getRecordingCreated()));
                holder.tvContributeLength.setText("00:" + DateUtils.formatElapsedTime(Long.parseLong(recordingsPools.get(listPosition).getDuration())));
//                holder.chrono.setText("00:" + DateUtils.formatElapsedTime(Long.parseLong(recordingsPools.get(listPosition).getDuration())));
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

                    try {
                        String play = holder.tvViewCount.getText().toString().trim();
                        int playValue = Integer.parseInt(play) + 1;
                        play = String.valueOf(playValue);
                        holder.tvViewCount.setText(play);

                        position = recordingList.get(listPosition).getRecordingId();
                        CurrentAdapterIndex = holder.getAdapterPosition();
                        if (FirstIndex == 0) {
                            FirstIndex = 1;
                            lastModifiedHoled = holder;
                            TempJoinCount = Integer.parseInt(recordingList.get(listPosition).getJoinCount());
                        }

                        if (PreviousAdapterIndex != CurrentAdapterIndex) {
                            PlayCounter = 0;
                            MinJoinCount = 1;
                            AppHelper.sop("Previous=current");
                            if (lastModifiedHoled != null) {
                                try {
                                    if (mHandler1 != null) {
                                        mHandler1.removeCallbacksAndMessages(null);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                                lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                                SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
                                seekBar.setProgress(0);
                                /*Chronometer chronometer=lastModifiedHoled.itemView.findViewById(R.id.chrono);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                chronometer.stop();
                                chronometer.setText("00:" + DateUtils.formatElapsedTime(Long.
                                        parseLong(recordingsPools.get(lastModifiedHoled.
                                                getAdapterPosition()).getDuration())));*/
                            }
                            lastModifiedHoled = holder;
                            PreviousAdapterIndex = CurrentAdapterIndex;
                            TempJoinCount = Integer.parseInt(recordingList.get(listPosition).getJoinCount());
                        }

                        try {
                            if (PlayCounter >= 0) {
                                //lastModifiedHoled = holder;
                                if (isLastInstrument){
                                    PlayCounter = 0;
                                    MinJoinCount = 1;
                                    isLastInstrument=false;
                                }
                                TextView tvIncludedCount = lastModifiedHoled.itemView.findViewById(R.id.txtJoinCount);
                                tvIncludedCount.setText(UpdateCalJoinCount(TempJoinCount));
                                PlayAudio(holder.getAdapterPosition(), "main");
                                //lastModifiedHoled = holder;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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

                    try {
                        length = mp.getCurrentPosition();
                        mp.stop();
                        mp.reset();
                        mp.release();
                        mp = null;
                        if (mHandler1 != null) {
                            mHandler1.removeCallbacksAndMessages(null);
                        }
                        /*chronoLen=SystemClock.elapsedRealtime() - holder.chrono.getBase();
                        holder.chrono.stop();*/
                        pauseTime();

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    //holder.seekBarRecordings.setProgress(0);
                }
            });

            if (listPosition > 10) {

            }
            holder.ivStationPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (CurrentAdapterIndex == holder.getAdapterPosition()) {
                            if (PlayCounter <= TempJoinCount - 1 && PlayCounter !=0) {
                                //lastModifiedHoled = holder;
                                PlayCounter = PlayCounter - 1;
                                length=0;
                                stopTime();
                                PlayAudio(holder.getAdapterPosition(), "pre");
                                //lastModifiedHoled = holder;
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
                        if (CurrentAdapterIndex == holder.getAdapterPosition()) {
                            if (PlayCounter < TempJoinCount - 1) {
                                //lastModifiedHoled = holder;
                                PlayCounter = PlayCounter + 1;
                                length=0;
                                stopTime();
                                PlayAudio(holder.getAdapterPosition(), "next");
                                //lastModifiedHoled = holder;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                            try {
                                JSONObject jsoneobj = new JSONObject(response);
                                String flag = jsoneobj.getString("flag");
                                if (flag.equals("success")) {
                                    IsLiked = true;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

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



    public void publicPrivateApi(final String isPublic, final String recID) {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PUBLIC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        AppHelper.sop("response=="+response);
                        try {
                            JSONObject jsoneobj = new JSONObject(response);
                            String flag = jsoneobj.getString("flag");
                            if (flag.equalsIgnoreCase("Success")) {
                                Toast.makeText(context, jsoneobj.getString("msg"), Toast.LENGTH_LONG).show();
                                if (context instanceof StationActivity){
                                    ((AudioFragment)mFragment).clearArrayList();
                                    ((AudioFragment)mFragment).callApi();
                                } else if (context instanceof ProfileActivity) {
                                    ((ProfileActivity)mActivity).clearArrayList();
                                    ((ProfileActivity)mActivity).callApi();
                                }
                            }
                            else {
                                Toast.makeText(context, "Something is wrong.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ispublic", isPublic);
                params.put("user_id", userId);
                params.put("rid", recID);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params="+params+"\nUrl="+PUBLIC);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

    public void releaseMediaPlayer() {
        try {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.reset();
                }
                mp.release();
                mp = null;
                if (mHandler1 != null) {
                    mHandler1.removeCallbacksAndMessages(null);
                }
                stopTime();
                resetViews();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetViews(){
        if (lastModifiedHoled!=null){
            lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
            SeekBar seekBar=lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
            seekBar.setProgress(0);
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

    private void PlayAudio(final int pisition, final String Type) {
        try {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            /*if (isLastInstrument && !(Type.equalsIgnoreCase("pre"))){
                isLastInstrument=false;
                PlayCounter = 0;
                MinJoinCount = 1;
                TextView tvIncludedCount = lastModifiedHoled.itemView.findViewById(R.id.txtJoinCount);
                tvIncludedCount.setText(UpdateCalJoinCount(TempJoinCount));
            }*/
            AppHelper.sop("PlayCounter="+PlayCounter+"=MinJoinCount="+MinJoinCount);
            instrumentFile = recordingList.get(pisition).getJoinUrl().get(PlayCounter).toString();

            if (instrumentFile != "") {
                try {
                    if (mp != null) {
                        try {

                            try {
                                mp.stop();
                                mp.release();
                                mp = null;
                                duration1 = 0;
                                length = 0;
//                                chronoLen=SystemClock.elapsedRealtime();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }

                            if (lastModifiedHoled != null) {
                                lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                                lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
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
                }
//                final Chronometer chrono=lastModifiedHoled.itemView.findViewById(R.id.chrono);
                mp.prepareAsync();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        try {
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(GONE);
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(VISIBLE);
                            mp.seekTo(length);
                            mp.start();
                            fetchViewCount(userId, position);
                            progressDialog.dismiss();
                            duration1 = mp.getDuration();
                            primarySeekBarProgressUpdater();

                            if (Type == "main") {

                            } else if (Type == "next") {
//                                PlayCounter = PlayCounter + 1;
                                MinJoinCount = MinJoinCount + 1;
                                TextView tvIncludedCount = lastModifiedHoled.itemView.findViewById(R.id.txtJoinCount);
                                tvIncludedCount.setText(UpdateCalJoinCount(TempJoinCount));
                            } else if (Type == "pre") {
//                                PlayCounter = PlayCounter - 1;
                                MinJoinCount = MinJoinCount - 1;
                                TextView tvIncludedCount = lastModifiedHoled.itemView.findViewById(R.id.txtJoinCount);
                                tvIncludedCount.setText(UpdateCalJoinCount(TempJoinCount));
                            }
                            /*chrono.setFormat("00:"+"%s");
                            chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.start();*/
                            startTime();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

                        try {
                            progressDialog.dismiss();
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                            SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
                            seekBar.setProgress(0);
                            /*chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.stop();
                            chrono.setText("00:" + DateUtils.formatElapsedTime(Long.parseLong(recordingsPools.get(pisition).getDuration())));*/
                            stopTime();
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
                            if (mHandler1 != null) {
                                mHandler1.removeCallbacksAndMessages(null);
                            }
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPlay).setVisibility(VISIBLE);
                            lastModifiedHoled.itemView.findViewById(R.id.ivStationPause).setVisibility(GONE);
                            SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
                            seekBar.setProgress(0);
                            length = 0;
                            duration1 = 0;
                            TextView tvContributeLength=lastModifiedHoled.itemView.findViewById(R.id.tvContributeLength);

                            /*chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.stop();
                            chrono.setText("00:" + DateUtils.formatElapsedTime(Long.parseLong(recordingsPools.get(pisition).getDuration())));*/

                            if (PlayCounter < recordingList.get(pisition).getJoinUrl().size()-1){
                                PlayCounter = PlayCounter + 1;
                                MinJoinCount = MinJoinCount + 1;
                                TextView tvIncludedCount = lastModifiedHoled.itemView.findViewById(R.id.txtJoinCount);
                                tvIncludedCount.setText(UpdateCalJoinCount(TempJoinCount));
                                PlayAudio(pisition,"main");
                            }else {
                                isLastInstrument=true;
                            }
                            stopTime();
                            tvContributeLength.setText("00:" + DateUtils.formatElapsedTime(Long.parseLong(recordingsPools.get(pisition).getDuration())));
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
    private Handler mHandler = new Handler();
    private Runnable mTickExecutor = new Runnable() {
        @Override
        public void run() {
            tick();
            mHandler.postDelayed(mTickExecutor,100);
        }
    };

    private void startTime(){
        mStartTime = SystemClock.elapsedRealtime()-time;
        mHandler.postDelayed(mTickExecutor, 00);
    }

    private void stopTime(){
        mStartTime = 0;
        time=0;
        mHandler.removeCallbacks(mTickExecutor);
        if (lastModifiedHoled!=null){
            TextView tvContributeLength=lastModifiedHoled.itemView.findViewById(R.id.tvContributeLength);
            tvContributeLength.setText("00:" + DateUtils.formatElapsedTime(Long.parseLong(
                    recordingsPools.get(lastModifiedHoled.getAdapterPosition()).getDuration())));
        }
    }

    private void pauseTime(){
        mStartTime = time;
        mHandler.removeCallbacks(mTickExecutor);
    }

    private void tick() {
        time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time / 100) % 10;
        TextView tvContributeLength = lastModifiedHoled.itemView.findViewById(R.id.tvContributeLength);
        tvContributeLength.setText("00:"+(minutes < 10 ? "0"+minutes : minutes)+":"+
                (seconds < 10 ? "0"+seconds : seconds));
        /*if (mRecorder != null) {
            amplitudes[i] = mRecorder.getMaxAmplitude();
            //Log.d("Voice Recorder","amplitude: "+(amplitudes[i] * 100 / 32767));
            if (i >= amplitudes.length -1) {
                i = 0;
            } else {
                ++i;
            }
        }*/
    }

    private void primarySeekBarProgressUpdater() {
        mHandler1 = new Handler();
        try {
            SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.seekBarRecordings);
            seekBar.setProgress(0);
            seekBar.setProgress((int) (((float) mp.getCurrentPosition() / duration1) * 100));// This math construction give a percentage of "was playing"/"song length"
            if (mp != null && mp.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                mHandler1.postDelayed(notification, 100);
            } else {
                try {
                    seekBar.setProgress(0);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private String CalJoinCount(int joincount) {
        String jCount = "";
        try {
            if (joincount == 0) {
                //currentSongIndex=1;
                jCount = "(" + String.valueOf(HolderJoinCount) + " of " + String.valueOf((joincount)) + ")";
            } else {

                jCount = String.valueOf(HolderJoinCount) + " of " + String.valueOf(joincount);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

    private String UpdateCalJoinCount(int joincount) {
        String jCount = "";
        try {
            if (joincount == 0) {
                //currentSongIndex=1;
                jCount = "(" + String.valueOf(MinJoinCount) + " of " + String.valueOf((joincount)) + ")";
            } else {

                jCount = String.valueOf(MinJoinCount) + " of " + String.valueOf(joincount);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jCount;
    }

}
