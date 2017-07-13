package com.instamelody.instamelody;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Fragments.AudioFragment;
import com.instamelody.instamelody.Fragments.MelodyPacksFragment;
import com.instamelody.instamelody.Fragments.RecordingsFragment;
import com.instamelody.instamelody.Fragments.SubscriptionsFragment;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
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
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class MelodyActivity extends AppCompatActivity {

    public Button btnMelodyPacks, btnRecordings, btnSubscriptions, melodySearchButton,btnCancel;
    RelativeLayout rlMelodySearchButton,rlMelodySearch;
    AppBarLayout appBarMelody;
    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    private MenuItem searchMenuItem;
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    String GENRE_NAMES_URL = "http://35.165.96.167/api/genere.php";
    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String resp = "";

    ImageView discover, message, ivBackButton, ivHomeButton, ivProfile,audio_feed,ivMelodyFilter;
    SearchView searchView,search1;
    String searchGet;
    String strName;
    TextView appBarMainText;
    ProgressDialog progressDialog;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    LongOperation myTask = null;
    //  LinearLayout tab1,llFragSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);


        search1 = (SearchView) findViewById(R.id.searchMelody);
        btnMelodyPacks = (Button) findViewById(R.id.btnMelodyPacks);
        melodySearchButton = (Button) findViewById(R.id.melodySearchButton);
        rlMelodySearchButton = (RelativeLayout) findViewById(R.id.rlMelodySearchButton);
        btnSubscriptions = (Button) findViewById(R.id.btnSubscriptions);
        rlMelodySearch = (RelativeLayout) findViewById(R.id.rlMelodySearch);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnRecordings = (Button) findViewById(R.id.btnRecordings);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        ivProfile = (ImageView) findViewById(R.id.userProfileImage);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        appBarMainText = (TextView) findViewById(R.id.appBarMainText);
        appBarMelody = (AppBarLayout) findViewById(R.id.appBarMelody);
        ivMelodyFilter = (ImageView) findViewById(R.id.ivMelodyFilter);
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

        melodySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHomeButton.setVisibility(View.GONE);
                appBarMainText.setVisibility(View.GONE);
                ivMelodyFilter.setVisibility(View.GONE);
                search1.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }
        });

        /*rlMelodySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlMelodySearch.setVisibility(View.VISIBLE);
                ivHomeButton.setVisibility(View.VISIBLE);
                appBarMainText.setVisibility(View.VISIBLE);
                ivMelodyFilter.setVisibility(View.VISIBLE);
                search1.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                search1.isSubmitButtonEnabled();
                String searchContent = search1.getQuery().toString();
                SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                editorSearchString.putString("stringSearch", searchContent);
                editorSearchString.apply();
                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.clear();
                editorFilterString.apply();
                AudioFragment af = new AudioFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_melody, af).commit();
            }
        });

        ivMelodyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MelodyActivity.this);
//                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Filter Audio");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MelodyActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Latest");
                arrayAdapter.add("Trending");
                arrayAdapter.add("Favorites");
                arrayAdapter.add("Artist");
                arrayAdapter.add("# of Instruments");
                arrayAdapter.add("BPM");


                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(MelodyActivity.this);
                        builderInner.setMessage(strName);
                        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                        editorFilterString.putString("stringFilter", strName);
                        editorFilterString.apply();
                        builderInner.setTitle("Your Selected Item is");
                        RecordingsFragment rf = new RecordingsFragment();
                        getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();
                    }
                });
                builderSingle.show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();
        SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
        editorSearchString.clear();
        editorSearchString.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();
        SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
        editorSearchString.clear();
        editorSearchString.apply();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener)this);

        searchGet = (String) searchView.getQuery();

        return true;
    }


    private class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MelodyActivity.this);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params) {
            AudioFragment aud_fag = new AudioFragment();
            aud_fag.fetchRecordingsFilter();
            SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
            editorFilterString.putString("stringFilter", strName);
            editorFilterString.apply();
            return null;
        }

        protected void onPostExecute(String result) {
            adapter = new RecordingsCardAdapter(getApplicationContext(),recordingList, recordingsPools);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

    }
}
