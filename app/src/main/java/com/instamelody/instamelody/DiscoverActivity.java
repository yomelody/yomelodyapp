package com.instamelody.instamelody;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Models.RecordingsData;
import com.instamelody.instamelody.Models.RecordingsModel;

import java.util.ArrayList;

import static com.instamelody.instamelody.R.id.tabHost;

/**
 * Created by Shubahansh Jaiswal on 11/29/2016.
 */

public class DiscoverActivity extends Activity {

    ImageView discover, message, ivBackButton, ivHomeButton,audio_feed;
    TabHost host;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<RecordingsModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        ivBackButton = (ImageView)findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView)findViewById(R.id.ivHomeButton);
        discover = (ImageView)findViewById(R.id.discover);
        message = (ImageView)findViewById(R.id.message);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDiscover);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<RecordingsModel>();
        for (int i = 0; i < RecordingsData.id_.length; i++) {
            /*data.add(new RecordingsModel(
                    RecordingsData.id_[i],
                    RecordingsData.userProfileImage[i],
                    RecordingsData.userRecordingCover[i],
                    RecordingsData.RecordingNameArray[i],
                    RecordingsData.UserNameArray[i],
                    RecordingsData.RecordingDateArray[i],
                    RecordingsData.IncludeCountArray[i],
                    RecordingsData.ContributeLengthArray[i],
                    RecordingsData.IncludedDateArray[i],
                    RecordingsData.GenresArray[i],
                    RecordingsData.ViewCountArray[i],
                    RecordingsData.LikeCountArray[i],
                    RecordingsData.CommentCountArray[i],
                    RecordingsData.ShareCountArray[i]
            ));*/
        }

        adapter = new RecordingsCardAdapter(data);
        recyclerView.setAdapter(adapter);

        host = (TabHost) findViewById(tabHost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("all");
        spec.setContent(R.id.tab1);
        spec.setIndicator("all");
        host.addTab(spec);

        spec = host.newTabSpec("Hip Hop");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Hip Hop");
        host.addTab(spec);

        spec = host.newTabSpec("Pop");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Pop");
        host.addTab(spec);

        spec = host.newTabSpec("Rock");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Rock");
        host.addTab(spec);

        spec = host.newTabSpec("Reggae");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Reggae");
        host.addTab(spec);

        spec = host.newTabSpec("EDM");
        spec.setContent(R.id.tab1);
        spec.setIndicator("EDM");
        host.addTab(spec);

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DiscoverActivity.this,StationActivity.class);
                startActivity(i);
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

    }
}
