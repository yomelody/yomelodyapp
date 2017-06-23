package com.instamelody.instamelody;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.support.v7.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Adapters.AudioCardAdapter;
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Fragments.AudioFragment;
import com.instamelody.instamelody.Models.AudioModel;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsData;
import com.instamelody.instamelody.Models.RecordingsModel;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.instamelody.instamelody.Fragments.ActivityFragment;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Parse.ParseContents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.R.attr.position;
import static com.instamelody.instamelody.R.id.tabHostRecordings;
import static com.instamelody.instamelody.R.id.thing_proto;


/**
 * Created by Saurabh Singh on 1/13/2017.
 */

public class StationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    Button btnActivity, btnAudio, btnCancel;
    RelativeLayout rlFragmentActivity, rlPartStation, rlSearch;
    ImageView ivBackButton, ivHomeButton, discover, message, ivProfile, audio_feed, ivSound, ivSound1, ivFilter;
    EditText subEtFilterName;

    TabHost host;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    private MenuItem searchMenuItem;
    SearchView.OnQueryTextListener listener;
    SearchView searchView, search1;
    ListView list;
    ArrayAdapter<String> instrumentAdapter;
    String[] values = new String[]{"Latest", "Trending", "Favourite", "Artist", "# of Instruments", "BPM"};

    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    private String RECORDING_URL = "http://35.165.96.167/api/recordings.php";
    private String ID = "id";
    private String KEY = "key";
    private String STATION = "station";
    private String GENRE = "genere";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";

    String GENRE_NAMES_URL = "http://35.165.96.167/api/genere.php";
    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String genreString = "1";
    String userId, userNameLogin;
    String userIdNormal, userIdFb, userIdTwitter;
    int statusNormal, statusFb, statusTwitter;
    String strName;
    String titleString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);


        message = (ImageView) findViewById(R.id.message);
        discover = (ImageView) findViewById(R.id.discover);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        ivSound = (ImageView) findViewById(R.id.ivSound);
        ivSound1 = (ImageView) findViewById(R.id.ivSound1);
        ivFilter = (ImageView) findViewById(R.id.ivFilter);
        btnActivity = (Button) findViewById(R.id.btnActivity);
        btnAudio = (Button) findViewById(R.id.btnAudio);
        rlFragmentActivity = (RelativeLayout) findViewById(R.id.rlFragmentActivity);
        rlPartStation = (RelativeLayout) findViewById(R.id.rlPartStation);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        search1 = (SearchView) findViewById(R.id.search1);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        list = (ListView) findViewById(R.id.list);
        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userNameLogin = loginSharedPref.getString("userName", null);
        statusNormal = loginSharedPref.getInt("status", 0);
        userIdNormal = loginSharedPref.getString("userId", null);
        SharedPreferences loginFbSharedPref = this.getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
        userIdFb = loginFbSharedPref.getString("userId", null);
        statusFb = loginFbSharedPref.getInt("status", 0);
        SharedPreferences loginTwitterSharedPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        userIdTwitter = loginTwitterSharedPref.getString("userId", null);
        statusTwitter = loginTwitterSharedPref.getInt("status", 0);

        if (statusNormal == 1) {
            userId = userIdNormal;
        } else if (statusFb == 1) {
            userId = userIdFb;
        } else if (statusTwitter == 1) {
            userId = userIdTwitter;
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewAudio);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // newText is text entered by user to SearchView
                Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        };


        AudioFragment af = new AudioFragment();
        getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#FFFFFF"));


                AudioFragment af = new AudioFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();

//                getFragmentManager().popBackStack();

            }
        });

        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));


                ActivityFragment actf = new ActivityFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, actf).commit();
            }
        });

        ivSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                searchMenuItem.setVisible(position == 0);
                rlSearch.setVisibility(View.INVISIBLE);
                search1.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSearch.setVisibility(View.VISIBLE);
                search1.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
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

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StationActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StationActivity.this, StationActivity.class);
                startActivity(i);
            }
        });

        ivSound1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StationActivity.this, MelodyActivity.class);
                startActivity(i);
            }
        });

        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                list.setVisibility(View.VISIBLE);

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(StationActivity.this);
//                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Filter Audio");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(StationActivity.this, android.R.layout.select_dialog_singlechoice);
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
                        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                        editorFilterString.putString("stringFilter", strName);
                        editorFilterString.apply();
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(StationActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                fetchGenreNames();
//                                fetchRecordings();
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
            }
        });

        instrumentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(instrumentAdapter);

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void fetchGenreNames() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GENRE_NAMES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        TabHost.TabSpec spec;
                        final TabHost host = (TabHost) findViewById(R.id.tabHostAudio);
                        host.setup();

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    genreJson = jsonArray.getJSONObject(i);
                                    titleString = genreJson.getString(KEY_GENRE_NAME);
                                    spec = host.newTabSpec(titleString);
                                    spec.setIndicator(titleString);
                                    spec.setContent(createTabContent());
                                    host.addTab(spec);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (host.getCurrentTab() == 0) {
//                            Toast.makeText(getActivity(), "All " + host.getCurrentTab(), Toast.LENGTH_SHORT).show();
                        }

                        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                            @Override
                            public void onTabChanged(String arg0) {
                                int currentTab = host.getCurrentTab();
                                genreString = String.valueOf(currentTab).trim();
                                fetchRecordings();
//                                Toast.makeText(getActivity(), "beta: " + genreString, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordings() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);
                        recordingList.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList,recordingsPools);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();
        super.onDestroy();
    }

    private TabHost.TabContentFactory createTabContent() {
        return new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                RecyclerView rv = new RecyclerView(getApplicationContext());
                rv.setHasFixedSize(true);
                RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
                rv.setLayoutManager(lm);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(adapter);
                return rv;
            }
        };
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
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}