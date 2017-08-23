package com.instamelody.instamelody;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.instamelody.instamelody.Adapters.InstrumentListAdapter;
import com.instamelody.instamelody.Adapters.JoinInstrumentListAdp;
import com.instamelody.instamelody.Adapters.JoinListAdapter;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Parse.ParseContents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.utils.Const.ServiceType.JOINED_USERS;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class JoinActivity extends AppCompatActivity {
    RecyclerView.Adapter adapter, adapter1;
    ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();
    ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    RecyclerView recyclerViewInstruments;
    RecyclerView recyclerViewComment;
    RelativeLayout rlIncluded;
    private String USER_ID = "userid";
    private String RECORDING_ID = "rid";
    String addedBy, RecId;
    public static TextView play_count, tvLikeCount, tvCommentCount, tvShareCount;
    public static ImageView ivJoinPlay, ivJoinPause, ivLikeButton, ivDislikeButton;
    public static RelativeLayout rlLike, rlComment, rlCommentFooter_join, joinFooter;
    FrameLayout joinMiddile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        play_count = (TextView) findViewById(R.id.tvPlayCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        ivJoinPlay = (ImageView) findViewById(R.id.ivJoinPlay);
        ivJoinPause = (ImageView) findViewById(R.id.ivJoinPause);
        rlLike = (RelativeLayout) findViewById(R.id.rlLikeContainer);
        ivLikeButton = (ImageView) findViewById(R.id.ivLikeButton);
        ivDislikeButton = (ImageView) findViewById(R.id.ivDislikeButton);
        rlComment = (RelativeLayout) findViewById(R.id.rlComment);
        rlIncluded = (RelativeLayout) findViewById(R.id.rlIncluded);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewArtists);
        recyclerViewComment = (RecyclerView) findViewById(R.id.recyclerViewComment);
        rlCommentFooter_join = (RelativeLayout) findViewById(R.id.rlCommentFooter_join);
        joinFooter = (RelativeLayout) findViewById(R.id.joinFooter);
        joinMiddile=(FrameLayout)findViewById(R.id.joinMiddile);
        recyclerViewInstruments = (RecyclerView) findViewById(R.id.recyclerViewInstruments);
        recyclerViewInstruments.setVisibility(View.VISIBLE);
        recyclerViewInstruments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewInstruments.setLayoutManager(layoutManager);
        recyclerViewInstruments.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        adapter = new RecentImagesAdapter(fileArray, getApplicationContext());
//        recyclerView.setAdapter(adapter);

        rlIncluded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        Intent intent = getIntent();
        addedBy = intent.getExtras().getString("AddedBy");
        RecId = intent.getExtras().getString("Recording_id");
        if (addedBy != null && RecId != null) {
            try {
                getJoined_users(addedBy, RecId);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewComment.setVisibility(View.VISIBLE);
                recyclerViewInstruments.setVisibility(View.GONE);
                joinFooter.setVisibility(View.GONE);

                rlCommentFooter_join.setVisibility(View.VISIBLE);
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
                        Joined_artist.clear();
                        instrumentList.clear();
                        new ParseContents(getApplicationContext()).parseJoin(response, Joined_artist, instrumentList);
                        adapter = new JoinListAdapter(Joined_artist, getApplicationContext());
                        adapter1 = new JoinInstrumentListAdp(instrumentList, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        recyclerViewInstruments.setAdapter(adapter1);
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
