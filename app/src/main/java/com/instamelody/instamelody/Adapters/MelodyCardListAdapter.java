package com.instamelody.instamelody.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.instamelody.instamelody.Fragments.MelodyPacksFragment;
import com.instamelody.instamelody.MelodyActivity;
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.ModelPlayAllMediaPlayer;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserMelodyCard;
import com.instamelody.instamelody.Models.UserMelodyPlay;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.StudioActivity;
import com.instamelody.instamelody.utils.AppHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.android.gms.internal.zzahg.runOnUiThread;
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

    public static final int REQUEST_MELODY_COMMENT = 001;
    String profile, cover;
    static ArrayList<MelodyCard> melodyList = new ArrayList<>();
    ArrayList<MelodyInstruments> melodyInstrumentsArrayList = new ArrayList<>();
    ArrayList<String> mpids = new ArrayList<>();
    private static ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    //    ArrayList<UserMelodyCard> userMelodyCardArrayList = new ArrayList<>();
//    ArrayList<UserMelodyPlay> userMelodyPlays = new ArrayList<>();
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
    private Activity mActivity;
    String position, audioUrl;
    private RecyclerView.ViewHolder lastModifiedHoled = null;
    int instCount = 1;
    private ArrayList<MediaPlayer> mediaPlayersAll = new ArrayList<MediaPlayer>();
    //    private MediaPlayer mpall;
    private int tempDuration = -1;
    private boolean isPausePressed = false;
    //    private MediaPlayer tempMediaPlayer;
    private Handler mHandler1;
    public static MediaPlayer mpall;
    int totalCount = 0, Compdurations = 0, tmpduration = 0, MaxMpSessionID;
    ProgressDialog progressDialog;

    public MelodyCardListAdapter(ArrayList<MelodyCard> melodyList, Context context) {
        this.melodyList = melodyList;

        this.context = context;
        mActivity = (Activity) context;
        mHandler1 = new Handler();
    }

    /*public MelodyCardListAdapter(ArrayList<UserMelodyCard> melodyList, ArrayList<UserMelodyPlay> melodyPools, Context context) {
        this.userMelodyCardArrayList = melodyList;
        this.userMelodyPlays = melodyPools;
        this.context = context;
    }*/


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvMelodyName, tvMelodyLength, tvBpmRate, tvInstrumentsUsed, tvMelodyGenre, tvMelodyDate, tv7, tv8, tv9;
        TextView tvPlayCount, tvLikeCount, tvCommentCount, tvShareCount;
        ImageView userProfileImage, ivMelodyCover, ivLikeButton, ivDislikeButton, ivPlayButton;
        Button btnMelodyAdd;
        SeekBar melodySlider;
        RelativeLayout rlSeekbarTracer, rlLike, rlPlay, rlComment, rlshare;

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

                        /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle(mActivity.getString(R.string.share_with_YoMelody));
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("audioShareData", MODE_PRIVATE).edit();
                                MelodyCard melody = melodyList.get(getAdapterPosition());
                                editor.putString("melodyID", melody.getMelodyPackId());
                                editor.apply();
                                Intent intent = new Intent(mActivity, MessengerActivity.class);
                                intent.putExtra("commingForm", "Melody");
                                intent.putExtra("share", melodyList.get(getAdapterPosition()));
                                intent.putExtra("file_type", "admin_melody");
                                mActivity.startActivityForResult(intent,MelodyCardListAdapter.REQUEST_MELODY_COMMENT);
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                MelodyCard melody = melodyList.get(getAdapterPosition());
                                MelodyName = melody.getMelodyName();

                                MelodyCard recording = melodyList.get(getAdapterPosition());
                                String RecordingURL = recording.getMelodyURL();

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.yomelody_music)
                                + "\n" + RecordingURL);

                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(Intent.createChooser(shareIntent, "Hello."));
                                SetMelodyShare("", "", "");
                            }
                        });
                        alertDialog.show();*/
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
                    try {
                        if (!userId.equals("") && userId != null) {
                            ivPlay.setVisibility(VISIBLE);
                            ivPause.setVisibility(GONE);
                            melodySlider.setProgress(0);
                            if (mediaPlayer != null) {
                                try {
                                    ivPlay.setVisibility(VISIBLE);
                                    ivPause.setVisibility(GONE);
                                    try {
                                        mediaPlayer.pause();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            String instruments, bpm, genre, melodyName, userName, duration, date, plays, likes, comments, shares, melodyID, RecordingURL, CoverUrl, LikeStatus, ProfilePick;
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
                            melodyID = melody.getMelodyPackId();
                            RecordingURL = melody.getMelodyURL();
                            CoverUrl = melody.getMelodyCover();
                            ProfilePick = melody.getUserProfilePic();
                            SharedPreferences.Editor editor = context.getSharedPreferences("commentData", MODE_PRIVATE).edit();
                            SharedPreferences file_type = getApplicationContext().getSharedPreferences("FileType", MODE_PRIVATE);
                            String file = "";
                            if (file_type.getString("File_Tyoe", null) != null) {
                                file = file_type.getString("File_Tyoe", null);
                            }

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
                            if (file.equals("user_melody") && file != null) {
                                editor.putString("fileType", "user_melody");
                            } else {
                                editor.putString("fileType", "admin_melody");
                            }
                            editor.putString("RecordingURL", RecordingURL);
                            editor.putString("CoverUrl", CoverUrl);
                            editor.putString("LikeStatus", LikeStatus);
                            editor.putString("adapterPosition", Integer.toString(getAdapterPosition()));
                            editor.commit();

                            SharedPreferences.Editor PreviousActivity = context.getSharedPreferences("PreviousActivity", MODE_PRIVATE).edit();
                            PreviousActivity.putString("PreviousActivityName", "MelodyActivity.class");
                            PreviousActivity.commit();
                            Intent intent = new Intent(context, CommentsActivity.class);
                            mActivity = (Activity) context;
                            mActivity.startActivityForResult(intent, REQUEST_MELODY_COMMENT);
                        } else {
                            Toast.makeText(context, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, SignInActivity.class);
                            context.startActivity(intent);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
            });


            btnMelodyAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String position = Integer.toString(getAdapterPosition());
                        StudioActivity.instrumentList.clear();
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
                        mActivity.finish();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });

        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_melody_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    static class ViewHolder {
        ImageView holderIvPlay, holderIvPause;
        SeekBar holderMelodySlider;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        try {
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
                    try {
                        progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Loading...");
                        //progressDialog.setCancelable(false);
                        progressDialog.show();
                        position = melodyList.get(listPosition).getMelodyPackId();
                        Compdurations = 0;
                        tmpduration = 0;
                        MaxMpSessionID = 0;
                        ParseContents pc = new ParseContents(context);
                        instrumentList = pc.getInstruments();
                        if (listPosition < instrumentList.size()) {
                            audioUrl = melody.getMelodyURL();
                        }
                        instCount = 1;
                        MelodyActivity.frameProgress.setVisibility(VISIBLE);
                        melodyInstrumentsArrayList.clear();
                        for (int i = 0; i < instrumentList.size(); i++) {
                            if (position.equalsIgnoreCase("" + instrumentList.get(i).getMelodyPacksId())) {
                                melodyInstrumentsArrayList.add(instrumentList.get(i));
                            }
                        }

                        if (melodyInstrumentsArrayList.size() > 0) {
                            audioUrl = melodyInstrumentsArrayList.get(0).getInstrumentFile();
                        }

                    /*Collections.sort(melodyInstrumentsArrayList, new Comparator<MelodyInstruments>(){
                        public int compare(MelodyInstruments s1, MelodyInstruments s2) {
                            int s1int=Integer.parseInt(s1.getInstrumentLength());
                            int s2int=(Integer.parseInt(s2.getInstrumentLength()));
                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                            return s1int > s2int ? -1 : (s1int < s2int) ? 1 : 0;
//                            return s1int.compareTo(s2int);
                        }
                    });*/

                        AppHelper.sop("=melodyInstrumentsArrayList=size=" + melodyInstrumentsArrayList.size() + "=position=" + position + "=listPosition=" + listPosition);

                        mHandler1.removeCallbacksAndMessages(null);
                        for (MediaPlayer mp : mediaPlayersAll) {
                            try {
                                if (mp != null) {
                                    duration = 0;
                                    length = 0;
                                    mp.stop();
                                    mp.reset();
                                    mp.release();

//                                AppHelper.sop("lastModifiedHoled=="+lastModifiedHoled);
                                    if (lastModifiedHoled != null) {
                                        lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(VISIBLE);
                                        lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(GONE);
                                        SeekBar seekBar = lastModifiedHoled.itemView.findViewById(R.id.melodySlider);
                                        seekBar.setProgress(0);
                                        lastModifiedHoled.itemView.findViewById(R.id.rlSeekbarTracer).setVisibility(GONE);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        holder.ivPlay.setVisibility(GONE);
                        holder.ivPause.setVisibility(VISIBLE);

                        mediaPlayersAll.clear();


                        new PrepareInstruments().execute();


                        lastModifiedHoled = holder;
                    }catch (Exception ex){
                        ex.printStackTrace();
                        progressDialog.dismiss();
                        lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(VISIBLE);
                        lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(GONE);
                    }



                }
            });


            holder.ivPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    for (MediaPlayer mediaPlayer : mediaPlayersAll) {
                        try {
                            holder.ivPlay.setVisibility(VISIBLE);
                            holder.ivPause.setVisibility(GONE);
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                            }
                            length = mediaPlayersAll.get(0).getCurrentPosition();
                            isPausePressed = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler1.removeCallbacksAndMessages(null);
                    holder.melodySlider.setProgress(0);
                }
            });
            holder.melodySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                    if (fromUser) {
//                        for (MediaPlayer mediaPlayer: mediaPlayersAll) {
                        try {
                            int playPositionInMilliseconds = mediaPlayersAll.get(0).getDuration() / 100 * holder.melodySlider.getProgress();
                            mediaPlayersAll.get(0).seekTo(playPositionInMilliseconds);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        }
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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

    public void fetchViewCount(final String userId, final String melody_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAY_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=fetchViewCount=" + response);
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
                params.put(FILEID, melody_id);
                //    params.put(TYPE, "admin_melody");
                params.put(TYPE, "melody");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=fetchViewCount=" + params + "\nURL=" + PLAY_COUNT);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }


    public void killMediaPlayer() {

        for (MediaPlayer mediaPlayer : mediaPlayersAll) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                mediaPlayer.release();
//                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mediaPlayersAll.clear();
        isPausePressed = false;
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

    private class PrepareInstruments extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
            try {
                try {



                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        protected Bitmap doInBackground(String... urls) {

            try {
                
                for (int i = 0; i < melodyInstrumentsArrayList.size(); i++) {
                    try {

                        MediaPlayer mpall = new MediaPlayer();
                        mpall.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mpall.setDataSource(melodyInstrumentsArrayList.get(i).getInstrumentFile());

                        mpall.prepare();
                        mediaPlayersAll.add(mpall);
                        Compdurations = mediaPlayersAll.get(i).getDuration();
                        if (Compdurations > tmpduration) {
                            tmpduration = Compdurations;
                            MaxMpSessionID = mediaPlayersAll.get(i).getAudioSessionId();
                        }


                        mpall.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                            @Override
                            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                switch (what) {
                                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                        //holder.progressDialog.show();
                                        break;
                                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                        //holder.progressDialog.dismiss();
                                        break;
                                }
                                return false;
                            }
                        });
                    } catch (Exception ex) {
                        progressDialog.dismiss();
                        lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(VISIBLE);
                        lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(GONE);
                        ex.printStackTrace();

                    }finally {

                    }
                }


            } catch (Throwable e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                for(MediaPlayer mediaPlayer:mediaPlayersAll){
                    try {
                        progressDialog.dismiss();
                        mediaPlayer.start();

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if(MaxMpSessionID==mp.getAudioSessionId()){
                                    mHandler1.removeCallbacksAndMessages(null);
                                    SeekBar seekBar= lastModifiedHoled.itemView.findViewById(R.id.melodySlider);
                                    lastModifiedHoled.itemView.findViewById(R.id.ivPlay).setVisibility(VISIBLE);
                                    lastModifiedHoled.itemView.findViewById(R.id.ivPause).setVisibility(GONE);
                                    seekBar.setProgress(0);
                                }
                            }
                        });
                    }catch (Exception ex){
                        ex.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                primarySeekBarProgressUpdater();


            } catch (IllegalStateException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }
    private void primarySeekBarProgressUpdater() {

        try {
            for (int i = 0; i <= mediaPlayersAll.size() - 1; i++) {
                final SeekBar seekBar= lastModifiedHoled.itemView.findViewById(R.id.melodySlider);
                final MediaPlayer pts;
                //final SeekBar seekBarf;
                if(MaxMpSessionID==mediaPlayersAll.get(i).getAudioSessionId()) {
                    pts = mediaPlayersAll.get(i);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {

                                int currentPosition = pts.getCurrentPosition() / 1000;
                                int duration = pts.getDuration() / 1000;
                                int progress = (currentPosition * 100) / duration;
                                //seekBar.setProgress((int) (((float) pts.getCurrentPosition() / pts.getDuration()) * 100));// This math construction give a percentage of "was playing"/"song length"
                                seekBar.setProgress(progress);
                                mHandler1.postDelayed(this, 1000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    };
                    mHandler1.postDelayed(runnable, 1000);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}
