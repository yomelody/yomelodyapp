package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.instamelody.instamelody.CommentsActivity;
import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserMelodyCard;
import com.instamelody.instamelody.Models.UserMelodyPlay;
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.utils.UtilsRecording;
import com.squareup.picasso.Picasso;

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
import static com.instamelody.instamelody.utils.Const.ServiceType.LIKESAPI;
import static com.instamelody.instamelody.utils.Const.ServiceType.PLAY_COUNT;

/**
 * Created by Saurabh Singh on 12//2016.
 * <p>
 * this will be used in recordings fragment
 */

public class RecordingsCardAdapter extends RecyclerView.Adapter<RecordingsCardAdapter.MyViewHolder> {

    String genreName, mpid, MelodyName, profile;
    static String instrumentFile;
    static MediaPlayer mp;
    int duration1, currentPosition;
    int length;
    ArrayList<String> mpids = new ArrayList<>();
    private ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    private ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    ArrayList<UserMelodyCard> userMelodyList = new ArrayList<>();
    ArrayList<UserMelodyPlay> melodyPools = new ArrayList<>();
    //String LIKE_MELODY_URL = "http://35.165.96.167/api/likes.php";
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

    public RecordingsCardAdapter(Context context, ArrayList<RecordingsModel> recordingList, ArrayList<RecordingsPool> recordingsPools) {
        this.recordingList = recordingList;
        this.recordingsPools = recordingsPools;
        this.context = context;
    }

    public RecordingsCardAdapter( ArrayList<UserMelodyCard> userMelodyList,  ArrayList<UserMelodyPlay> melodyPools ,Context context) {
        this.userMelodyList = userMelodyList;
        this.melodyPools = melodyPools;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
        TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivRecordingCover, ivLikeButton, ivCommentButton, ivShareButton, ivDislikeButton;
        ImageView ivJoin, ivStationPlay, ivStationPause;
        SeekBar seekBarRecordings;
        RelativeLayout rlProfilePic, rlLike;

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

            SharedPreferences editorGenre = getApplicationContext().getSharedPreferences("prefGenreName", MODE_PRIVATE);
            genreName = editorGenre.getString("GenreName", null);

            ivJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String instruments, bpm, genre, recordName, userName, duration, date, plays, likes, comments, shares, melodyID;

                    genre = tvRecordingGenres.getText().toString().trim();
                    recordName = tvRecordingName.getText().toString().trim();
                    duration = tvContributeLength.getText().toString().trim();
                    date = tvRecordingDate.getText().toString().trim();
                    plays = tvViewCount.getText().toString().trim();
//                    tvIncludedCount.getText().toString().trim();
//                    tvContributeDate.getText().toString().trim();
                    userName = tvUserName.getText().toString().trim();
                    likes = tvLikeCount.getText().toString().trim();
                    comments = tvCommentCount.getText().toString().trim();
                    shares = tvShareCount.getText().toString().trim();

//                    duration = tvMelodyLength.getText().toString().trim();
//                    date = tvMelodyDate.getText().toString().trim();
//                    plays = tvPlayCount.getText().toString().trim();
//                    instruments = tvInstrumentsUsed.getText().toString().trim();
//                    bpm = tvBpmRate.getText().toString().trim();
//                    genre = tvMelodyGenre.getText().toString().trim();
//                    melodyName = tvMelodyName.getText().toString().trim();

                    int pos = getAdapterPosition();
//                    melodyID = mpids.get(pos);

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

                    Intent intent = new Intent(context, JoinActivity.class);
                    context.startActivity(intent);
                }
            });
            rlLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String position, userId;
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    userId = loginSharedPref.getString("userId", null);
                    String MelodyName;
                    if (userId != null) {
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
                    /*Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));*/

                    RecordingsModel recording = recordingList.get(getAdapterPosition());
                    String RecordingURL = recording.getrecordingurl();
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "InstaMelody Music Hunt");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, RecordingURL);
                    shareIntent.setType("image/jpeg");
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(shareIntent, "Hello."));
                }
            });
            ivCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "comment", Toast.LENGTH_SHORT).show();
                    String instruments, bpm, genre, melodyName, userName, duration, date, plays, likes, comments, shares, melodyID;

                    //instruments = tvInstrumentsUsed.getText().toString().trim();
                    //bpm = tvBpmRate.getText().toString().trim();
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
                    editor.putString("bitmapProfile", profile);
//                    editor.putString("bitmapCover", cover);
                    editor.putString("melodyID", melodyID);
                    editor.putString("fileType", "user_recording");
                    editor.commit();

                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });

            rlProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String showProfileUserId = recordingList.get(getAdapterPosition()).getAddedBy();
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    intent.putExtra("showProfileUserId", showProfileUserId);
                    view.getContext().startActivity(intent);
                }
            });

            seekBarRecordings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    int mCurrentPosition = currentPosition / 1000;
                    int mDuration = duration1 / 1000;
                    UtilsRecording utilRecording = new UtilsRecording();
                    int progress1 = utilRecording.getProgressPercentage(mCurrentPosition, mDuration);

                    if (mp != null && fromUser) {
                        int playPositionInMilliseconds = duration1 / 100 * seekBarRecordings.getProgress();
                        mp.seekTo(playPositionInMilliseconds);
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
            Handler mHandler1 = new Handler();
            duration1 = mp.getDuration();
            seekBarRecordings.setProgress((int) (((float) mp.getCurrentPosition() / duration1) * 100));// This math construction give a percentage of "was playing"/"song length"
            if (mp.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                mHandler1.postDelayed(notification, 100);
            }
        }
    }

    @Override
    public RecordingsCardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recordings, parent, false);

        RecordingsCardAdapter.MyViewHolder myViewHolder = new RecordingsCardAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecordingsCardAdapter.MyViewHolder holder, final int listPosition) {

        RecordingsModel recording = recordingList.get(listPosition);
        /*final RecordingsPool recordingsPool = recordingsPools.get(listPosition);
        instrumentFile = recordingsPool.getRecordingUrl();*/


        final TextView tvRecordingGenres = holder.tvRecordingGenres;
        TextView tvUserName = holder.tvUserName;
        TextView tvRecordingName = holder.tvRecordingName;
        TextView tvContributeLength = holder.tvContributeLength;
        TextView tvRecordingDate = holder.tvRecordingDate;
        TextView tvViewCount = holder.tvViewCount;
        TextView tvLikeCount = holder.tvLikeCount;
        TextView tvCommentCount = holder.tvCommentCount;
        TextView tvShareCount = holder.tvShareCount;
        TextView tvIncludedCount = holder.tvIncludedCount;
        TextView tvContributeDate = holder.tvContributeDate;


//        profile = melody.getUserProfilePic();
//        cover = melody.getMelodyCover();
        mpid = recording.getRecordingId();
        mpids.add(mpid);
//        if (recording.getRecordingCover().equals("")) {
////            Picasso.with(holder.ivRecordingCover.getContext()).load(R.drawable.cover3).into(holder.ivRecordingCover);
//        } else {
//            Picasso.with(holder.ivRecordingCover.getContext()).load(recording.getRecordingCover()).into(new Target() {
//
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    holder.ivRecordingCover.setBackground(new BitmapDrawable(context.getResources(), bitmap));
//                }
//
//                @Override
//                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
//                }
//
//                @Override
//                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
//                }
//            });
//        }
//        if (recording.getRecordingCover().equals("")) {
////            Picasso.with(holder.userProfileImage.getContext()).load(R.drawable.artist).into(holder.userProfileImage);
//        } else {
//            Picasso.with(holder.userProfileImage.getContext()).load(recording.getRecordingCover()).into(new Target() {
//
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    holder.userProfileImage.setBackground(new BitmapDrawable(context.getResources(), bitmap));
//                }
//
//                @Override
//                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
//                }
//
//                @Override
//                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
//                }
//            });
//        }


        Picasso.with(holder.ivRecordingCover.getContext()).load(recordingsPools.get(listPosition).getCoverUrl()).into(holder.ivRecordingCover);
//        Picasso.with(holder.userProfileImage.getContext()).load(recordingsPools.get(listPosition).getProfileUrl()).into(holder.userProfileImage);
        Picasso.with(holder.userProfileImage.getContext()).load(recordingList.get(listPosition).getUserProfilePic()).into(holder.userProfileImage);
//        tvRecordingGenres.setText(recordingList.get(listPosition).getGenreName());
        //        tvRecordingGenres.setText(recordingList.get(listPosition).getGenreName());

        holder.tvUserName.setText(recordingList.get(listPosition).getUserName());
        holder.tvRecordingName.setText(recordingList.get(listPosition).getRecordingName());
        holder.tvRecordingGenres.setText("Genre:" + " " + recording.getGenreName());
        holder.tvContributeLength.setText(recordingsPools.get(listPosition).getDuration());
        holder.tvContributeDate.setText(recordingsPools.get(listPosition).getDateAdded());

        int likeStatus = recordingList.get(listPosition).getLikeStatus();
        if (likeStatus == 0) {
            holder.ivDislikeButton.setVisibility(GONE);
            //holder.ivLikeButton.setVisibility(VISIBLE);
        } else {
            holder.ivDislikeButton.setVisibility(VISIBLE);
        }


//        tvContributeLength.setText(recordingList.get(listPosition).getTvContributeLength());
        holder.tvRecordingDate.setText(recordingList.get(listPosition).getRecordingCreated());
        //    tvContributeDate.setText(recordingList.get(listPosition).getTvContributeDate());
        holder.tvViewCount.setText(String.valueOf(recordingList.get(listPosition).getPlayCount()));
        holder.tvLikeCount.setText(String.valueOf(recordingList.get(listPosition).getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(recordingList.get(listPosition).getCommentCount()));
        holder.tvShareCount.setText(String.valueOf(recordingList.get(listPosition).getShareCount()));
        holder.ivStationPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.seekBarRecordings.setVisibility(View.VISIBLE);
                holder.ivStationPause.setVisibility(View.VISIBLE);
                try {
                    RecordingsPool recordingsPool = recordingsPools.get(listPosition);
                    instrumentFile = recordingsPool.getRecordingUrl();
                    Integer s = listPosition + 1;

                    if (getItemCount() >= s && instrumentFile != null) {
                        playAudio();
                        holder.primarySeekBarProgressUpdater();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mp.seekTo(length);
                mp.start();

                if (mp.equals(duration1)) {
                    try {
                        playAudio();
                        holder.primarySeekBarProgressUpdater();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.ivStationPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivStationPlay.setVisibility(v.VISIBLE);
                holder.ivStationPause.setVisibility(v.GONE);
                mp.pause();
                length = mp.getCurrentPosition();
                holder.seekBarRecordings.setProgress(0);
            }
        });



//        holder.tvIncludedCount.setText(String.valueOf(recordingList.get(listPosition).getTvIncludedCount()));
    }

    @Override
    public int getItemCount() {
        return recordingList.size();
    }


    public void playAudio() throws IOException {
        killMediaPlayer();

        mp = new MediaPlayer();
//        mp.setDataSource(audioFilePath);
        mp.setDataSource(instrumentFile);
        mp.prepare();
        mp.start();
        duration1 = mp.getDuration();
        currentPosition = mp.getCurrentPosition();
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
//                            Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
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
                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(context);
            requestQueue1.add(stringRequest);
        } catch (Exception ex) {

        }

    }

    public void SetMelodyShare(final String file_id, final String shared_by_user, final String shared_with) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAY_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //       Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject, respObject;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                //respObject = jsonObject.getJSONObject(KEY_RESPONSE);
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
                params.put(FILE_ID, file_id);
                params.put(Key_shared_by_user, shared_by_user);
                params.put(Key_shared_with, shared_with);
                params.put(Key_file_type, "admin_melody");
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }
}
