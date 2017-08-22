package com.instamelody.instamelody;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
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
    RecyclerView.Adapter adapter,adapter1;
    ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();
    ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    RecyclerView recyclerViewInstruments;
    RelativeLayout rlIncluded;
    private String USER_ID = "userid";
    private String RECORDING_ID = "rid";
    String addedBy, RecId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        rlIncluded = (RelativeLayout) findViewById(R.id.rlIncluded);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewArtists);
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
        addedBy=  intent.getExtras().getString("AddedBy");
        RecId=  intent.getExtras().getString("Recording_id");
        if(addedBy !=null && RecId !=null){
            try {
                getJoined_users(addedBy,RecId);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }




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
                        new ParseContents(getApplicationContext()).parseJoin(response, Joined_artist,instrumentList);
                        adapter= new JoinListAdapter(Joined_artist,getApplicationContext());
                      //  adapter1= new InstrumentListAdapter(instrumentList,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    //    recyclerViewInstruments.setAdapter(adapter1);
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
