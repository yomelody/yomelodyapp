package com.instamelody.instamelody.Adapters;

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
import static com.instamelody.instamelody.Adapters.InstrumentListAdapter.audioUrl;
import static com.instamelody.instamelody.utils.Const.ServiceType.LIKESAPI;
import static com.instamelody.instamelody.utils.Const.ServiceType.PLAY_COUNT;
import static com.instamelody.instamelody.utils.Const.ServiceType.SHAREFILE;
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
    String melodyName,fetchRecordingUrl;

    String USER_TYPE = "user_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String LIKES = "likes";
    String TYPE = "type";
    String USERID = "userid";
    String FILEID = "fileid";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    String Topic ="topic";
    RecordingsPool recordingsPool;
    Context context;
    MediaPlayer mediaPlayer;
    int playerPos, TempLength=0;
    int duration, length;
    String mpid,MelodyName,TempRecordingid="0",Recordingid;
    String Key_shared_by_user = "shared_by_user";
    String Key_shared_with = "shared_with";
    String Key_file_type = "file_type";

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
        RelativeLayout rlSeekbarTracer, rlLike, rlPlay, rlComment,rlshare;




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
            rlshare=(RelativeLayout)itemView.findViewById(R.id.rlShare);


            // MelodyName=tvMelodyName.getText().toString().trim();
            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivPlay.setVisibility(GONE);
                    ivPause.setVisibility(VISIBLE);
                    melodySlider.setVisibility(VISIBLE);
                    rlSeekbarTracer.setVisibility(VISIBLE);

                    String position, userId,Melodyid;
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    userId = loginSharedPref.getString("userId", null);
                    position = Integer.toString(getAdapterPosition() + 1);


                    MelodyCard melody = melodyList.get(getAdapterPosition());
                    Melodyid=melody.getMelodyPackId();
                    if (userId != null) {
                        if(!TempRecordingid.equals(Melodyid))
                        {
                            fetchViewCount(userId, Melodyid);
                            String play = tvPlayCount.getText().toString().trim();
                            int playValue = Integer.parseInt(play) + 1;
                            play = String.valueOf(playValue);
                            tvPlayCount.setText(play);
                            TempRecordingid=Melodyid;
                            length=0;
                        }





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
                    TempLength=length;
                    melodySlider.setProgress(0);
                }
            });

            rlLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String position, userId;
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    userId = loginSharedPref.getString("userId", null);
                    String MelodyName;
                    //String positions = mpids.get(getAdapterPosition() + 1);
                    if (userId != null) {
//                        position = Integer.toString(getAdapterPosition() + 1);
                        //position = mpids.get(getAdapterPosition());
                        MelodyCard melody = melodyList.get(getAdapterPosition());
                        MelodyName=melody.getMelodyName();
                        position =melody.getMelodyPackId();
                        if (ivDislikeButton.getVisibility() == VISIBLE) {
                            ivLikeButton.setVisibility(VISIBLE);
                            ivDislikeButton.setVisibility(GONE);
                            String like = tvLikeCount.getText().toString().trim();
                            int likeValue = Integer.parseInt(like) - 1;
                            like = String.valueOf(likeValue);
                            tvLikeCount.setText(like);
                            fetchLikeState(userId, position, "0",MelodyName);

                        } else if (ivDislikeButton.getVisibility() == GONE) {

                            ivLikeButton.setVisibility(GONE);
                            ivDislikeButton.setVisibility(VISIBLE);
                            String like = tvLikeCount.getText().toString().trim();
                            int likeValue = Integer.parseInt(like) + 1;
                            like = String.valueOf(likeValue);
                            tvLikeCount.setText(like);
                            fetchLikeState(userId, position, "1",MelodyName);
                        }
                    } else {
                        Toast.makeText(context, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }
                }
            });

            rlshare.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    /*Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));*/

                    MelodyCard melody = melodyList.get(getAdapterPosition());
                    MelodyName=melody.getMelodyName();

                    MelodyCard recording = melodyList.get(getAdapterPosition());
                    String RecordingURL=recording.getMelodyURL();

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "InstaMelody Music Hunt"+"\n"+RecordingURL);

                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(shareIntent, "Hello."));
                    SetMelodyShare("","","");

                }

            });

            rlPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String position, userId;
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    userId = loginSharedPref.getString("userId", null);

                    if (userId != null) {
                        /*position = Integer.toString(getAdapterPosition() + 1);
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

        int likeStatus=melodyList.get(listPosition).getLikeStatus();
        if(likeStatus==0)
        {
            holder.ivDislikeButton.setVisibility(GONE);
            //holder.ivLikeButton.setVisibility(VISIBLE);
        }
        else
        {
            holder.ivDislikeButton.setVisibility(VISIBLE);
        }





    }

    @Override
    public int getItemCount() {
        return melodyList.size();
    }

    public void fetchLikeState(final String userId, final String pos, final String likeState, String LikeMelodyName) {
        MelodyName=LikeMelodyName;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LIKESAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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

    public void SetMelodyShare(final String file_id, final String shared_by_user,final String shared_with) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SHAREFILE,
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
    /*public void SetPlayCount(final String userid, final String Fileid) {

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
                params.put(USERID, userid);
                params.put(FILEID, Fileid);
                params.put(TYPE, "melody");
                params.put(USER_TYPE, "admin");
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }*/


}
