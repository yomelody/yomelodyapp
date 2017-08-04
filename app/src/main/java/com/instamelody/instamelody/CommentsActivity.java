package com.instamelody.instamelody;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.utils.Const.ServiceType.COMMENTS;
import static com.instamelody.instamelody.utils.Const.ServiceType.COMMENT_LIST;

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

    CommentsAdapter adapter;
    ImageView ivBackButton, ivHomeButton, ivCamera, tvImgChat;
    TextView tvCancel, tvSend, tvRegId, tvMsgChat;
    RecyclerView recyclerView;
    RelativeLayout rlCommentSend;
    EditText etComment;

    String instruments, bpm, genre, melodyName, userName, duration, date, plays, likes, comments, shares, profilePic, cover, melodyID, fileType;
    static ArrayList<Comments> commentList = new ArrayList<>();
    String COMMENT = "comment";
    String FILE_TYPE = "file_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String TOPIC = "topic";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

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
        Picasso.with(ivMelodyCover.getContext()).load(cover).into(ivMelodyCover);
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

                SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                String userId = loginSharedPref.getString("userId", null);
                if (userId != null) {

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
    }

    public void getComments() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
