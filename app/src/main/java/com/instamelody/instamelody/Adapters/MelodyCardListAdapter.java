package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
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
import com.instamelody.instamelody.Models.UserDetails;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.StudioActivity;
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
import static com.instamelody.instamelody.Adapters.InstrumentListAdapter.audioUrl;
import static com.instamelody.instamelody.R.id.melodySlider;

/**
 * Created by Shubhansh Jaiswal on 11/26/2016.
 * this is used in Melody activity
 */

public class MelodyCardListAdapter extends RecyclerView.Adapter<MelodyCardListAdapter.MyViewHolder> {
    String profile, cover;
    static ArrayList<MelodyCard> melodyList = new ArrayList<>();
    ArrayList<String> mpids = new ArrayList<>();
    private static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    String melodyName;
    String LIKE_MELODY_URL = "http://35.165.96.167/api/likes.php";
    String PLAY_MELODY_URL = "http://35.165.96.167/api/playcount.php";
    String USER_TYPE = "user_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String LIKES = "likes";
    String TYPE = "type";
    String USERID = "userid";
    String FILEID = "fileid";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";

    Context context;
    MediaPlayer mediaPlayer;
    int playerPos;
    int duration, length;
    String mpid;

    public MelodyCardListAdapter(ArrayList<MelodyCard> melodyList, Context context) {
        this.melodyList = melodyList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvMelodyName, tvMelodyLength, tvBpmRate, tvInstrumentsUsed, tvMelodyGenre, tvMelodyDate, tv7, tv8, tv9;
        TextView tvPlayCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivMelodyCover, ivPlay, ivPause, ivLikeButton, ivDislikeButton, ivPlayButton;
        Button btnMelodyAdd;
        SeekBar melodySlider;
        RelativeLayout rlSeekbarTracer, rlLike, rlPlay, rlComment;

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

            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivPlay.setVisibility(GONE);
                    ivPause.setVisibility(VISIBLE);
                    melodySlider.setVisibility(VISIBLE);
                    rlSeekbarTracer.setVisibility(VISIBLE);

                    String position, userId;
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    userId = loginSharedPref.getString("userId", null);
                    position = Integer.toString(getAdapterPosition() + 1);

                    if (userId != null) {
                        String play = tvPlayCount.getText().toString().trim();
                        int playValue = Integer.parseInt(play) + 1;
                        play = String.valueOf(playValue);
                        tvPlayCount.setText(play);

                        fetchViewCount(userId, position);
                        ParseContents pc = new ParseContents(context);
                        instrumentList = pc.getInstruments();
                        if (getAdapterPosition() < instrumentList.size()) {
                            audioUrl = instrumentList.get(getAdapterPosition()).getInstrumentFile();
                        }

                    } else {
                        ParseContents pc = new ParseContents(context);
                        instrumentList = pc.getInstruments();
                        if (getAdapterPosition() < instrumentList.size()) {
                            audioUrl = instrumentList.get(getAdapterPosition()).getInstrumentFile();
                        }
                    }

                    try {
                        playAudio(audioUrl);
                        primarySeekBarProgressUpdater();
                        audioUrl = "";
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    mediaPlayer.seekTo(length);
                    mediaPlayer.start();
                    if (mediaPlayer.equals(duration)) {
                        try {
                            playAudio(audioUrl);
                            primarySeekBarProgressUpdater();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            ivPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivPlay.setVisibility(VISIBLE);
                    ivPause.setVisibility(GONE);
                    mediaPlayer.pause();
                    length = mediaPlayer.getCurrentPosition();
                    melodySlider.setProgress(0);
                }
            });

            rlLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String position, userId;
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    userId = loginSharedPref.getString("userId", null);

                    if (userId != null) {
                        position = Integer.toString(getAdapterPosition() + 1);

                        if (ivLikeButton.getVisibility() == VISIBLE) {
                            ivLikeButton.setVisibility(GONE);
                            ivDislikeButton.setVisibility(VISIBLE);
                            String like = tvLikeCount.getText().toString().trim();
                            int likeValue = Integer.parseInt(like) + 1;
                            like = String.valueOf(likeValue);
                            tvLikeCount.setText(like);
                            fetchLikeState(userId, position, "1");

                        } else if (ivLikeButton.getVisibility() == GONE) {
                            ivLikeButton.setVisibility(VISIBLE);
                            ivDislikeButton.setVisibility(GONE);
                            String like = tvLikeCount.getText().toString().trim();
                            int likeValue = Integer.parseInt(like) - 1;
                            like = String.valueOf(likeValue);
                            tvLikeCount.setText(like);
                            fetchLikeState(userId, position, "0");
                        }
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
                        position = Integer.toString(getAdapterPosition() + 1);
                        String play = tvPlayCount.getText().toString().trim();
                        int playValue = Integer.parseInt(play) + 1;
                        play = String.valueOf(playValue);
                        tvPlayCount.setText(play);
                        fetchViewCount(userId, position);

                    } else {
                        //do nothing
                    }
                }
            });


            rlComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String instruments, bpm, genre, melodyName, userName, duration, date, plays, likes, comments, shares, melodyID;

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
                    editor.putString("bitmapProfile", profile);
//                    editor.putString("bitmapCover", cover);
                    editor.putString("melodyID", melodyID);
                    editor.putString("fileType", "admin_melody");
                    editor.commit();

                    Intent intent = new Intent(context, CommentsActivity.class);
                    context.startActivity(intent);

                }
            });

            melodySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()

            {

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
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            btnMelodyAdd.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {
                    String position = Integer.toString(getAdapterPosition());
                    Intent intent = new Intent(v.getContext(), StudioActivity.class);
                    intent.putExtra("clickPosition", position);
                    v.getContext().startActivity(intent);
                }
            });

        }

        private void primarySeekBarProgressUpdater() {
            Handler mHandler1 = new Handler();
            melodySlider.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / duration) * 100));// This math construction give a percentage of "was playing"/"song length"
            if (mediaPlayer.isPlaying()) {
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {
        MelodyCard melody = melodyList.get(listPosition);
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
    }

    @Override
    public int getItemCount() {
        return melodyList.size();
    }

    public void fetchLikeState(final String userId, final String pos, final String likeState) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LIKE_MELODY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
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
                params.put(USER_TYPE, "admin");
                params.put(USER_ID, userId);
                params.put(FILE_ID, pos);
                params.put(TYPE, "melody");
                params.put(LIKES, likeState);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

    public void fetchViewCount(final String userId, final String pos) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAY_MELODY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject, respObject;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                respObject = jsonObject.getJSONObject(KEY_RESPONSE);
                                String str = respObject.getString("play_count");
                                Toast.makeText(context, "" + str, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Map<String, String> params = new HashMap<>();
                params.put(USER_TYPE, "admin");
                params.put(USERID, userId);
                params.put(FILEID, pos);
                params.put(TYPE, "melody");
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.seekTo(playerPos);
        mediaPlayer.start();
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
}
