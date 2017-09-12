package com.instamelody.instamelody;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.instamelody.instamelody.Adapters.JoinInstrumentListAdp;
import com.instamelody.instamelody.Adapters.JoinListAdapter;
import com.instamelody.instamelody.Models.Comments;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Parse.ParseContents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.COMMENT_LIST;
import static com.instamelody.instamelody.utils.Const.ServiceType.JOINED_USERS;

public class JoinCommentActivity extends AppCompatActivity {
    private String RECORDING_ID = "rid";
    RecyclerView recyclerView, recyclerViewComment;
    CommentsAdapter adapter;
    static ArrayList<Comments> commentList = new ArrayList<>();
    String COMMENT = "comment";
    String FILE_TYPE = "file_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String TOPIC = "topic";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    String userId = "";
    TextView tvPlayCount, tvLikeCount, tvCommentCount, tvShareCount;
    RelativeLayout rlCommentSend;
    TextView tvCancel, tvSend;
    EditText etComment;
    String melodyID, fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_comment);
        tvPlayCount = (TextView) findViewById(R.id.tvPlayCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewArtists);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewComment = (RecyclerView) findViewById(R.id.recyclerViewComment);
        rlCommentSend = (RelativeLayout) findViewById(R.id.rlComment);
        etComment = (EditText) findViewById(R.id.etComment);
        etComment.setHintTextColor(Color.parseColor("#7B888F"));
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvSend = (TextView) findViewById(R.id.tvSend);

        if (JoinActivity.addedBy != null && JoinActivity.RecId != null) {
            try {
                getJoined_users(JoinActivity.addedBy, JoinActivity.RecId);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        Intent intent = getIntent();
        String pos = intent.getExtras().getString("Position");
        Toast.makeText(getApplicationContext(), pos, Toast.LENGTH_SHORT).show();
        try {
            tvPlayCount.setText(JoinActivity.Joined_artist.get(Integer.parseInt(pos)).getPlay_counts());
            tvLikeCount.setText(JoinActivity.Joined_artist.get(Integer.parseInt(pos)).getLike_counts());
            tvCommentCount.setText(JoinActivity.Joined_artist.get(Integer.parseInt(pos)).getComment_counts());
            tvShareCount.setText(JoinActivity.Joined_artist.get(Integer.parseInt(pos)).getShare_counts());
            melodyID = JoinActivity.Joined_artist.get(Integer.parseInt(pos)).getRecording_id();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        fileType = "user_recording";
        getComments();
        RecyclerView.LayoutManager lm1 = new LinearLayoutManager(getApplicationContext());
        recyclerViewComment.setLayoutManager(lm1);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setItemViewCacheSize(10);
        recyclerViewComment.setDrawingCacheEnabled(true);
        recyclerViewComment.setItemAnimator(new DefaultItemAnimator());
        adapter = new CommentsAdapter(getApplicationContext(), commentList);
        recyclerViewComment.setAdapter(adapter);

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

    }

    public void getJoined_users(final String addedBy, final String RecId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JOINED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);
                        JoinActivity.Joined_artist.clear();
                        JoinActivity.instrumentList.clear();
                        //new ParseContents(getApplicationContext()).parseJoin(response, JoinActivity.Joined_artist, JoinActivity.instrumentList);
                        JoinActivity.adapter = new JoinListAdapter(JoinActivity.Joined_artist, getApplicationContext());
                        recyclerView.setAdapter(JoinActivity.adapter);
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
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, addedBy);
                params.put(RECORDING_ID, RecId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getComments() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        commentList.clear();
                        adapter.notifyDataSetChanged();
                        recyclerViewComment.smoothScrollToPosition(adapter.getItemCount());
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

}
