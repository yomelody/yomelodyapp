package com.instamelody.instamelody.Adapters;

import android.app.ProgressDialog;
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
import android.widget.Button;
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
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserMelodyCard;
import com.instamelody.instamelody.Models.UserMelodyPlay;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.StudioActivity;
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
import static com.instamelody.instamelody.Adapters.InstrumentListAdapter.audioUrl;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.LIKESAPI;
import static com.instamelody.instamelody.utils.Const.ServiceType.PLAY_COUNT;
/*import static com.instamelody.instamelody.R.id.melodySlider;
import static com.instamelody.instamelody.R.id.rlShare;
import static com.instamelody.instamelody.R.id.tab_host;*/

/**
 * Created by Shubhansh Jaiswal on 11/26/2016.
 * this is used in Melody activity
 */

public class MelodyCardListAdapter extends RecyclerView.Adapter<MelodyCardListAdapter.MyViewHolder> {
    String profile, cover;
    static ArrayList<MelodyCard> melodyList = new ArrayList<>();
    ArrayList<String> mpids = new ArrayList<>();
    private static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    ArrayList<UserMelodyCard> userMelodyCardArrayList = new ArrayList<>();
    ArrayList<UserMelodyPlay> userMelodyPlays = new ArrayList<>();
    String melodyName, fetchRecordingUrl;
    RecyclerView.LayoutManager layoutManager;
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
    RecordingsPool recordingsPool;
    Context context;
    public static MediaPlayer mediaPlayer;
    int playerPos, TempLength = 0;
    int duration, length;
    String mpid, MelodyName, TempRecordingid = "0", Recordingid;
    String Key_shared_by_user = "shared_by_user";
    String Key_shared_with = "shared_with";
    String Key_file_type = "file_type";
    String userId = "";

    private RecyclerView.ViewHolder lastModifiedHoled = null;


    public MelodyCardListAdapter(ArrayList<MelodyCard> melodyList, Context context) {
        this.melodyList = melodyList;
        this.context = context;
    }

    public MelodyCardListAdapter(ArrayList<UserMelodyCard> melodyList, ArrayList<UserMelodyPlay> melodyPools, Context context) {
        this.userMelodyCardArrayList = melodyList;
        this.userMelodyPlays = melodyPools;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvMelodyName, tvMelodyLength, tvBpmRate, tvInstrumentsUsed, tvMelodyGenre, tvMelodyDate, tv7, tv8, tv9;
        TextView tvPlayCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivMelodyCover, ivLikeButton, ivDislikeButton, ivPlayButton;
        Button btnMelodyAdd;
        SeekBar melodySlider;
        RelativeLayout rlSeekbarTracer, rlLike, rlPlay, rlComment, rlshare;
        ProgressDialog progressDialog;
        ImageView ivPlay, ivPause;

        public MyViewHolder(final View itemView) {
            super(itemView);

            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            ivMelodyCover = (ImageView) itemView.findViewById(R.id.ivMelodyCover);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvMelodyName = (TextView) itemView.findViewById(R.id.tvMelodyName);
            tvMelodyLength = (TextView) itemView.findViewById(R.id.tvMelodyLength);
            tvInstrumentsUsed = (TextView) itemView.findViewById(R.id.tvInstrumentsUsed);
            tvBpmRate = (TextView) itemView.findViewById(R.id.tvBpmRate);
            tvMelodyGenre = (TextView) itemView.findViewById(R.id.tvMelodyGenre);
            tvMelodyDate = (TextView) itemView.findViewById(R.id.tvMelodyDate);
            tvPlayCount = (TextView) itemView.findViewById(R.id.tvPlayCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
            tvShareCount = (TextView) itemView.findViewById(R.id.tvShareCount);
            ivPause = (ImageView) itemView.findViewById(R.id.ivPause);
            tv7 = (TextView) itemView.findViewById(R.id.tv7);
            tv8 = (TextView) itemView.findViewById(R.id.tv8);
            tv9 = (TextView) itemView.findViewById(R.id.tv9);
            btnMelodyAdd = (Button) itemView.findViewById(R.id.btnMelodyAdd);
            melodySlider = (SeekBar) itemView.findViewById(R.id.melodySlider);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
            ivLikeButton = (ImageView) itemView.findViewById(R.id.ivLikeButton);
            ivDislikeButton = (ImageView) itemView.findViewById(R.id.ivDislikeButton);
            rlSeekbarTracer = (RelativeLayout) itemView.findViewById(R.id.rlSeekbarTracer);
            rlLike = (RelativeLayout) itemView.findViewById(R.id.rlLike);
            rlPlay = (RelativeLayout) itemView.findViewById(R.id.rlPlay);
            rlComment = (RelativeLayout) itemView.findViewById(R.id.rlComment);
            rlshare = (RelativeLayout) itemView.findViewById(R.id.rlShare);

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


            rlLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String position;
                    String MelodyName;
                    //String positions = mpids.get(getAdapterPosition() + 1);
                    if (!userId.equals("") && userId != null) {
//                        position = Integer.toString(getAdapterPosition() + 1);
                        //position = mpids.get(getAdapterPosition());
                        MelodyCard melody = melodyList.get(getAdapterPosition());
                        MelodyName = melody.getMelodyName();
                        position = melody.getMelodyPackId();
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

            rlshare.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!userId.equals("") && userId != null) {

                        MelodyCard melody = melodyList.get(getAdapterPosition());
                        MelodyName = melody.getMelodyName();

                        MelodyCard recording = melodyList.get(getAdapterPosition());
                        String RecordingURL = recording.getMelodyURL();

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "InstaMelody Music Hunt" + "\n" + RecordingURL);

                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(Intent.createChooser(shareIntent, "Hello."));
                        SetMelodyShare("", "", "");
                    } else {
                        Toast.makeText(context, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }

                }

            });

            rlPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String position, userId;
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    userId = loginSharedPref.getString("userId", null);

                    if (userId != null) {
                       /* position = Integer.toString(getAdapterPosition() + 1);
                        String play = tvPlayCount.getText().toString().trim();
                        int playValue = Integer.parseInt(play) + 1;
                        play = String.valueOf(playValue);
                        tvPlayCount.setText(play);
                        fetchViewCount(userId, position);*/

                    } else {
                        //do nothing
                    }
                }
            });


            rlComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!userId.equals("") && userId != null) {
                        if (mediaPlayer != null) {
                            try {
                                ivPlay.setVisibility(VISIBLE);
                                ivPause.setVisibility(GONE);
                                try {
                                    mediaPlayer.pause();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }

                                melodySlider.setProgress(0);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        String instruments, bpm, genre, melodyName, userName, duration, date, plays, likes, comments, shares, melodyID, RecordingURL, CoverUrl, LikeStatus,ProfilePick;
                        MelodyCard melody = melodyList.get(getAdapterPosition());

                        if (ivDislikeButton.getVisibility() == VISIBLE) {
                            LikeStatus = "1";
                        } else {
                            LikeStatus = "0";
                        }

                        instruments = tvInstrumentsUsed.getText().toString().trim();
                        bpm = tvBpmRate.getText().toString().trim();
                        genre = tvMelodyGenre.getText().toString().trim();
                        melodyName = tvMelodyName.getText().toString().trim();
                        userName = tvUserName.getText().toString().trim();
                        duration = tvMelodyLength.getText().toString().trim();
                        date = tvMelodyDate.getText().toString().trim();
                        plays = tvPlayCount.getText().toString().trim();
                        likes = tvLikeCount.getText().toString().trim();
                        comments = tvCommentCount.getText().toString().trim();
                        shares = tvShareCount.getText().toString().trim();
                        int pos = getAdapterPosition();
                        melodyID = mpids.get(pos);
                        RecordingURL = melody.getMelodyURL();
                        CoverUrl = melody.getMelodyCover();
                        ProfilePick=melody.getUserProfilePic();
                        SharedPreferences.Editor editor = context.getSharedPreferences("commentData", MODE_PRIVATE).edit();
                        editor.putString("instruments", instruments);
                        editor.putString("bpm", bpm);
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
//                    editor.putString("bitmapCover", cover);
                        editor.putString("melodyID", melodyID);
                        editor.putString("fileType", "admin_melody");
                        editor.putString("RecordingURL", RecordingURL);
                        editor.putString("CoverUrl", CoverUrl);
                        editor.putString("LikeStatus", LikeStatus);
                        editor.commit();

                        SharedPreferences.Editor PreviousActivity = context.getSharedPreferences("PreviousActivity", MODE_PRIVATE).edit();
                        PreviousActivity.putString("PreviousActivityName", "MelodyActivity.class");
                        PreviousActivity.commit();
                        Intent intent = new Intent(context, CommentsActivity.class);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }

                }
            });


            btnMelodyAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String position = Integer.toString(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), StudioActivity.class);
                    intent.putExtra("clickPosition", position);
                    v.getContext().startActivity(intent);
                    StudioActivity.list.clear();
                    if (mediaPlayer != null) {
                        try {
                            mediaPlayer.reset();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        }

        private void primarySeekBarProgressUpdater() {
            Handler mHandler1 = new Handler();
            try {
                melodySlider.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final MelodyCard melody = melodyList.get(listPosition);

        profile = melody.getUserProfilePic();
        cover = melody.getMelodyCover();
        SharedPreferences.Editor editor1 = context.getSharedPreferences("commentData1", MODE_PRIVATE).edit();
        editor1.putString("cover", cover);
        editor1.commit();
        mpid = melody.getMelodyPackId();
        mpids.add(mpid);
        Picasso.with(holder.ivMelodyCover.getContext()).load(melody.getMelodyCover()).into(holder.ivMelodyCover);
        Picasso.with(holder.userProfileImage.getContext()).load(melody.getUserProfilePic()).into(holder.userProfileImage);
        holder.tvInstrumentsUsed.setText(melody.getInstrumentCount());
        holder.tvBpmRate.setText(melody.getMelodyBpm());
//        holder.tvMelodyGenre.setText(melody.getGenreId());
        holder.tvMelodyGenre.setText(melody.getGenreName());
        holder.tvUserName.setText(melody.getUserName());
        holder.tvMelodyName.setText(melody.getMelodyName());
        holder.tvMelodyLength.setText(melody.getMelodyLength());
        holder.tvMelodyDate.setText(melody.getMelodyCreated());
        holder.tvPlayCount.setText(String.valueOf(melody.getPlayCount()));
        holder.tvLikeCount.setText(String.valueOf(melody.getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(melody.getCommentCount()));
        holder.tvShareCount.setText(String.valueOf(melody.getShareCount()));

        final int likeStatus = melodyList.get(listPosition).getLikeStatus();
        if (likeStatus == 0) {
            holder.ivDislikeButton.setVisibility(GONE);
            //holder.ivLikeButton.setVisibility(VISIBLE);
        } else {
            holder.ivDislikeButton.setVisibility(VISIBLE);
        }


        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.progressDialog = new ProgressDialog(v.getContext());
                holder.progressDialog.setMessage("Loading...");
                holder.progressDialog.show();

                holder.ivPause.setVisibility(VISIBLE);
                holder.melodySlider.setVisibility(VISIBLE);
                holder.rlSeekbarTracer.setVisibility(VISIBLE);

                String position;
                position = melodyList.get(listPosition).getMelodyPackId();

                String play = holder.tvPlayCount.getText().toString().trim();
                int playValue = Integer.parseInt(play) + 1;
                play = String.valueOf(playValue);
                holder.tvPlayCount.setText(play);

                fetchViewCount(userId, position);
                ParseContents pc = new ParseContents(context);
                instrumentList = pc.getInstruments();
                if (listPosition < instrumentList.size()) {
                    audioUrl = melody.getMelodyURL();
                }


                if (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {

                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                            //holder.ivPause.setVisibility(GONE);

                            if (lastModifiedHoled != null) {
                                int lastPosition = lastModifiedHoled.getAdapterPosition();
                                lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(VISIBLE);
                                lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(GONE);
                           /* lastModifiedHoled.itemView.setBackgroundColor(Color.TRANSPARENT);
                            lastModifiedHoled.txtIndustry.setTextColor(context.getResources().getColor(R.color.text_color_blue));*/
                                notifyItemChanged(lastPosition);
                            }


                        /*View view = null;
                        holder.ivPause.setVisibility(VISIBLE);
                        view = holder.recyclerView.getChildAt(1);
                        holder.ivPause = (ImageView) view.findViewById(R.id.ivPause);
                        holder.ivPause.setVisibility(VISIBLE);*/


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
                        holder.progressDialog.dismiss();
                        lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(GONE);
                        lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(VISIBLE);
                        mediaPlayer.start();
                        holder.primarySeekBarProgressUpdater();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer MediaPlayer, int what, int extra) {
                        holder.progressDialog.dismiss();
                        return false;
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        duration = mediaPlayer.getDuration();
                        holder.progressDialog.dismiss();
                    }
                });

                lastModifiedHoled = holder;

                //        mediaPlayer.seekTo(length);
                //      mediaPlayer.start();
//                    if (mediaPlayer.equals(duration)) {
//                        try {
//                            playAudio(audioUrl);
//                            primarySeekBarProgressUpdater();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }


            }
        });


        holder.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivPlay.setVisibility(VISIBLE);
                holder.ivPause.setVisibility(GONE);
                try {
                    mediaPlayer.pause();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                holder.melodySlider.setProgress(0);
            }
        });
        holder.melodySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//                    int sliderSize = melodySlider.getWidth();
//                    int rlivSize = rlSeekbarTracer.getWidth();
//
//                    tv7.setText(Integer.toString(sliderSize));
//                    tv8.setText(Integer.toString(progress));
//                    tv9.setText(Integer.toString(rlivSize));
//
//                    int maxProgress = melodySlider.getMax();
//                    int rate = (sliderSize - 20) / maxProgress;
//
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlSeekbarTracer.getLayoutParams();
//                    params.width = (40 + (progress * rate));
//                    rlSeekbarTracer.setLayoutParams(params);

//                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
//                    int mDuration = mediaPlayer.getDuration() / 1000;
                //   UtilsRecording utilRecording = new UtilsRecording();
                //   int progress1 = utilRecording.getProgressPercentage(mCurrentPosition, mDuration);

                if (mediaPlayer != null && fromUser) {
                    try {
                        int playPositionInMilliseconds = mediaPlayer.getDuration() / 100 * holder.melodySlider.getProgress();
                        mediaPlayer.seekTo(playPositionInMilliseconds);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

//                        seekBar.setProgress(progress);
                } else {
                    // the event was fired from code and you shouldn't call player.seekTo()
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    public MyViewHolder getViewHolder(int position) {
        MyViewHolder holder = null;
        /**
         Find ViewHolder here...
         If Found initialize the holder..
         holder = ?
         **/
        return holder;
    }

    @Override
    public int getItemCount() {
        return melodyList.size();
    }

    public void fetchLikeState(final String userId, final String pos, final String likeState, String LikeMelodyName) {
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
                params.put(TYPE, "admin_melody");
                params.put(LIKES, likeState);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
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
                params.put(USER_TYPE, "admin");
                params.put(USERID, userId);
                params.put(FILEID, pos);
                //    params.put(TYPE, "admin_melody");
                params.put(TYPE, "melody");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        //    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//            }
//        });
        mediaPlayer.seekTo(playerPos);
        //  mediaPlayer.start();
        duration = mediaPlayer.getDuration();
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static ArrayList<MelodyCard> returnMelodyList() {
        return melodyList;
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
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

}
