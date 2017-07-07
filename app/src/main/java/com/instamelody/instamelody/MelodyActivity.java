package com.instamelody.instamelody;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Fragments.MelodyPacksFragment;
import com.instamelody.instamelody.Fragments.RecordingsFragment;
import com.instamelody.instamelody.Fragments.SubscriptionsFragment;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Parse.ParseContents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Shubahansh Jaiswal on 11/29/2016.
 */

public class MelodyActivity extends AppCompatActivity {

    public Button btnMelodyPacks, btnRecordings, btnSubscriptions, btnAppBarSearchButton;
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    String GENRE_NAMES_URL = "http://35.165.96.167/api/genere.php";
    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String resp = "";

    ImageView discover, message, ivBackButton, ivHomeButton, ivProfile,audio_feed;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    //  LinearLayout tab1,llFragSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        btnMelodyPacks = (Button) findViewById(R.id.btnMelodyPacks);
        btnAppBarSearchButton = (Button) findViewById(R.id.appBarSearchButton);
        btnSubscriptions = (Button) findViewById(R.id.btnSubscriptions);
        btnRecordings = (Button) findViewById(R.id.btnRecordings);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        ivProfile = (ImageView) findViewById(R.id.userProfileImage);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        //  tab1 = (LinearLayout) findViewById(R.id.tab1);
        // llFragSubs = (LinearLayout) findViewById(R.id.llFragSubs);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMelody);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        MelodyPacksFragment mpf = new MelodyPacksFragment();
        getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();

        btnMelodyPacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnMelodyPacks.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnRecordings.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnSubscriptions.setBackgroundColor(Color.parseColor("#E4E4E4"));

                MelodyPacksFragment mpf = new MelodyPacksFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();
            }
        });

        btnRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMelodyPacks.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnRecordings.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnSubscriptions.setBackgroundColor(Color.parseColor("#E4E4E4"));

                RecordingsFragment rf = new RecordingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();
            }
        });

        btnSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnMelodyPacks.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnRecordings.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnSubscriptions.setBackgroundColor(Color.parseColor("#FFFFFF"));

                SubscriptionsFragment subf = new SubscriptionsFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_melody, subf).commit();
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        btnAppBarSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });

        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);

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

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MelodyActivity.this,StationActivity.class);
                startActivity(intent);
            }
        });

    }
}
