package com.instamelody.instamelody;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Adapters.CommentsAdapter;
import com.instamelody.instamelody.Models.Comments;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.utils.AppHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.COMMENTS;
import static com.instamelody.instamelody.utils.Const.ServiceType.COMMENT_LIST;
import static com.instamelody.instamelody.utils.Const.ServiceType.LIKESAPI;
import static com.instamelody.instamelody.utils.Const.ServiceType.PLAY_COUNT;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class CommentsActivity extends AppCompatActivity {

    TextView tvUserName, tvMelodyName, tvMelodyLength, tvBpmRate, tvInstrumentsUsed, tvMelodyGenre, tvMelodyDate, tv7, tv8, tv9;
    TextView tvPlayCount, tvLikeCount, tvCommentCount, tvShareCount;
    ImageView userProfileImage, ivMelodyCover, ivPlay, ivPause, ivLikeButton, ivDislikeButton, ivPlayButton;
    Button btnMelodyAdd;
    SeekBar melodySlider;
    RelativeLayout rlSeekbarTracer, rlLike, rlPlay, rlComment;
    String Topic = "topic";
    String LIKES = "likes";
    String TYPE = "type";

    String USER_TYPE = "user_type";
    String USERID = "userid";
    String FILEID = "fileid";

    CommentsAdapter adapter;
    ImageView ivBackButton, ivHomeButton, ivCamera, tvImgChat, ivShareButton;
    TextView tvCancel, tvSend, tvRegId, tvMsgChat;
    RecyclerView recyclerView;
    RelativeLayout rlCommentSend;
    EditText etComment;


    String instruments, bpm, genre, melodyName, userName, duration, date, plays, likes, comments, shares, profilePic, cover, melodyID, fileType, RecordingURL, LikeStatus, CoverUrl;
    static ArrayList<Comments> commentList = new ArrayList<>();
    String COMMENT = "comment";
    String FILE_TYPE = "file_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String TOPIC = "topic";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    String userId = "";
    ProgressDialog progressDialog;
    Activity mActivity;
    private MediaPlayer mediaPlayer;
    private String adapterPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mActivity=CommentsActivity.this;
        tvInstrumentsUsed = (TextView) findViewById(R.id.tvInstrumentsUsed);
        tvBpmRate = (TextView) findViewById(R.id.tvBpmRate);
        tvMelodyGenre = (TextView) findViewById(R.id.tvMelodyGenre);
        tvMelodyName = (TextView) findViewById(R.id.tvMelodyName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvMelodyLength = (TextView) findViewById(R.id.tvMelodyLength);
        tvMelodyDate = (TextView) findViewById(R.id.tvMelodyDate);
        tvPlayCount = (TextView) findViewById(R.id.tvPlayCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        ivMelodyCover = (ImageView) findViewById(R.id.ivMelodyCover);
        userProfileImage = (ImageView) findViewById(R.id.userProfileImage);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewComment);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        ivShareButton = (ImageView) findViewById(R.id.ivShareButton);
        ivLikeButton = (ImageView) findViewById(R.id.ivLikeButton);
        ivDislikeButton = (ImageView) findViewById(R.id.ivDislikeButton);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivPause = (ImageView) findViewById(R.id.ivPause);
        rlLike = (RelativeLayout) findViewById(R.id.rlLike);
        rlSeekbarTracer = (RelativeLayout) findViewById(R.id.rlSeekbarTracer);
        melodySlider = (SeekBar) findViewById(R.id.melodySlider);
        btnMelodyAdd = (Button) findViewById(R.id.btnMelodyAdd);

        SharedPreferences prefs = getSharedPreferences("commentData", MODE_PRIVATE);
        instruments = prefs.getString("instruments", null);
        bpm = prefs.getString("bpm", null);
        genre = prefs.getString("genre", null);
        melodyName = prefs.getString("melodyName", null);
        userName = prefs.getString("userName", null);
        duration = prefs.getString("duration", null);
        date = prefs.getString("date", null);
        plays = prefs.getString("plays", null);
        likes = prefs.getString("likes", null);
        comments = prefs.getString("comments", null);
        shares = prefs.getString("shares", null);
        profilePic = prefs.getString("bitmapProfile", null);
        cover = prefs.getString("bitmapCover", null);
        melodyID = prefs.getString("melodyID", null);
        fileType = prefs.getString("fileType", null);
        RecordingURL = prefs.getString("RecordingURL", null);
        LikeStatus = prefs.getString("LikeStatus", null);
        CoverUrl = prefs.getString("CoverUrl", null);
        adapterPosition = prefs.getString("adapterPosition", null);
        getComments();

        tvInstrumentsUsed.setText(instruments);
        tvBpmRate.setText(bpm);
        tvMelodyGenre.setText(genre);
        tvMelodyName.setText(melodyName);
        tvUserName.setText(userName);
        tvMelodyLength.setText(duration);
        tvMelodyDate.setText(date);
        tvPlayCount.setText(plays);
        tvLikeCount.setText(likes);
        tvCommentCount.setText(comments);
        tvShareCount.setText(shares);
        Picasso.with(ivMelodyCover.getContext()).load(CoverUrl).into(ivMelodyCover);
        Picasso.with(userProfileImage.getContext()).load(profilePic).into(userProfileImage);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CommentsAdapter(getApplicationContext(), commentList);
        recyclerView.setAdapter(adapter);

        rlCommentSend = (RelativeLayout) findViewById(R.id.rlComment);
        etComment = (EditText) findViewById(R.id.etComment);
        etComment.setHintTextColor(Color.parseColor("#7B888F"));
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvSend = (TextView) findViewById(R.id.tvSend);
        if (LikeStatus.equals("0")) {
            ivDislikeButton.setVisibility(View.GONE);
        } else {
            ivDislikeButton.setVisibility(View.VISIBLE);
        }

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
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCancel.setVisibility(View.GONE);
                tvSend.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etComment.getText().toString().length() == 0) {
                    tvCancel.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);
                }
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* SharedPreferences prefsActivity = getSharedPreferences("PreviousActivity", MODE_PRIVATE);
                String PrevAct = prefsActivity.getString("instruments", null);
                Intent intent = new Intent(getApplicationContext(),);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();

            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!userId.equals("") && userId != null) {

                    tvCancel.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);
                    String comment = etComment.getText().toString().trim();
                    etComment.getText().clear();
                    sendComment(comment, userId);
                    int commentCount = Integer.parseInt(tvCommentCount.getText().toString().trim()) + 1;

                    tvCommentCount.setText(String.valueOf(commentCount));

                } else {
                    Toast.makeText(getApplicationContext(), "Log in to comment", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etComment.getText().clear();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        ivShareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!userId.equals("") && userId != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Melody Songs" + "\n" + CoverUrl + "\n" + RecordingURL);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Melody Songs" + "\n" + RecordingURL);

                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(shareIntent, "Hello."));
                } else {
                    Toast.makeText(getApplicationContext(), "Log in to comment", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
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

                    /*RecordingsModel recording = commentList.get(Integer.parseInt(melodyID));
                    MelodyName = recording.getRecordingName();
                    position = recording.getRecordingId();*/

                    if (ivDislikeButton.getVisibility() == View.VISIBLE) {
                        ivLikeButton.setVisibility(View.VISIBLE);
                        ivDislikeButton.setVisibility(View.GONE);
                        String like = tvLikeCount.getText().toString().trim();
                        int likeValue = Integer.parseInt(like) - 1;
                        like = String.valueOf(likeValue);
                        tvLikeCount.setText(like);
                        SetLikeState(userId, melodyID, "0", melodyName);

                    } else if (ivDislikeButton.getVisibility() == View.GONE) {
                        ivLikeButton.setVisibility(View.GONE);
                        ivDislikeButton.setVisibility(View.VISIBLE);
                        String like = tvLikeCount.getText().toString().trim();
                        int likeValue = Integer.parseInt(like) + 1;
                        like = String.valueOf(likeValue);
                        tvLikeCount.setText(like);
                        SetLikeState(userId, melodyID, "1", melodyName);
                    }
                } else {
                    Toast.makeText(CommentsActivity.this, "Log in to like this melody pack", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CommentsActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                ivPause.setVisibility(VISIBLE);
                melodySlider.setVisibility(VISIBLE);
                rlSeekbarTracer.setVisibility(VISIBLE);

                String position;
                position = melodyID;

                String play = tvPlayCount.getText().toString().trim();
                int playValue = Integer.parseInt(play) + 1;
                play = String.valueOf(playValue);
                tvPlayCount.setText(play);

                fetchViewCount(userId, position);
                ParseContents pc = new ParseContents(mActivity);

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(RecordingURL);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        progressDialog.dismiss();
                        ivPlay.setVisibility(GONE);
                        ivPause.setVisibility(VISIBLE);
                        mediaPlayer.start();
                        primarySeekBarProgressUpdater();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer MediaPlayer, int what, int extra) {
                        progressDialog.dismiss();
                        return false;
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
//                        duration = mediaPlayer.getDuration();
                        ivPause.setVisibility(GONE);
                        ivPlay.setVisibility(VISIBLE);
                        melodySlider.setVisibility(GONE);
                        rlSeekbarTracer.setVisibility(GONE);
                        melodySlider.setProgress(0);
                        progressDialog.dismiss();
                    }
                });

            }
        });

        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPlay.setVisibility(VISIBLE);
                ivPause.setVisibility(GONE);
                try {
                    mediaPlayer.pause();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                melodySlider.setProgress(0);
            }
        });

        melodySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (mediaPlayer != null && fromUser) {
                    try {
                        int playPositionInMilliseconds = mediaPlayer.getDuration() / 100 * melodySlider.getProgress();
                        mediaPlayer.seekTo(playPositionInMilliseconds);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

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

        btnMelodyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String position = adapterPosition;
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
        RequestQueue requestQueue1 = Volley.newRequestQueue(mActivity);
        requestQueue1.add(stringRequest);
    }

    public void getComments() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=="+response);
                        commentList.clear();
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        new ParseContents(getApplicationContext()).parseComments(response, commentList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
//                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
//                            errorMsg = "ParseError";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(FILE_ID, melodyID);
                params.put(FILE_TYPE, fileType);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+COMMENT_LIST);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void sendComment(final String cmnt, final String userId) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("params=="+response);
                        adapter.notifyDataSetChanged();
                        getComments();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
//                        new ParseContents(getApplicationContext()).parseComments(response, commentList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
//                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
//                            errorMsg = "ParseError";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(FILE_ID, melodyID);
                params.put(COMMENT, cmnt);
                params.put(FILE_TYPE, fileType);
                params.put(USER_ID, userId);
                params.put(TOPIC, melodyName);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+COMMENTS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void SetLikeState(final String userId, final String pos, final String likeState, String LikeMelodyName) {
        try {

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
                            Toast.makeText(CommentsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            String errorMsg = error.toString();
                            Log.d("Error", errorMsg);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Topic, melodyName);
                    params.put(USER_ID, userId);
                    params.put(FILE_ID, pos);
                    params.put(TYPE, fileType);
                    params.put(LIKES, likeState);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(CommentsActivity.this);
            requestQueue1.add(stringRequest);
        } catch (Exception ex) {

        }

    }

    void share(String nameApp, String imagePath) {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/jpeg");
            List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
                    targetedShare.setType("image/jpeg"); // put here your mime type
                    if (info.activityInfo.packageName.toLowerCase().contains(nameApp) || info.activityInfo.name.toLowerCase().contains(nameApp)) {
                        targetedShare.putExtra(Intent.EXTRA_SUBJECT, "Sample Photo");
                        targetedShare.putExtra(Intent.EXTRA_TEXT, "This photo is created by App Name");
                        targetedShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
                        targetedShare.setPackage(info.activityInfo.packageName);
                        targetedShareIntents.add(targetedShare);
                    }
                }
                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            }
        } catch (Exception e) {
            Log.v("VM", "Exception while sending image on" + nameApp + " " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null ) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
