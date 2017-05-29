package com.instamelody.instamelody;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Fragments.ActivityFragment;
import com.instamelody.instamelody.Fragments.BioFragment;
import com.instamelody.instamelody.Fragments.ProfileActivityFragment;
import com.instamelody.instamelody.Models.RecordingsData;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Parse.ParseContents;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.instamelody.instamelody.R.id.bio_fragment;
import static com.instamelody.instamelody.R.id.rlPartStation;
import static com.instamelody.instamelody.R.id.tabHost;

/**
 * Created by Saurabh Singh on 01/09/2017
 */
public class ProfileActivity extends AppCompatActivity {

    String GENRE_NAMES_URL = "http://35.165.96.167/api/genere.php";
    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String genreString = "1";

    private String RECORDING_URL = "http://35.165.96.167/api/recordings.php";
    private String ID = "id";
    private String KEY = "station";
    private String GENRE = "genere";


    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    Button btnAudio, btnActivity, btnBio, appBarSidebtnMusicCircle;
    RelativeLayout rlPartProfile, rlFragmentActivity, rlFragmentBio;
    ImageView ivBackButton, ivHomeButton, ivAudio_feed, ivDiscover, ivMessage, ivProfile;
    CircleImageView userProfileImageInProf;
    TextView tvNameInProf, tvUserNameInProf;
    String firstName, userNameLogin, profilePicLogin, Name, userName, profilePic, fbName, fbUserName, fbId;
    String userId;
    int statusNormal, statusFb, statusTwitter;

    TabHost host;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<RecordingsModel> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fetchGenreNames();
        fetchRecordings();

        SharedPreferences loginSharedPref1 = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userId = loginSharedPref1.getString("userId", null);

        adapter = new RecordingsCardAdapter(this, recordingList);

        btnAudio = (Button) findViewById(R.id.btnAudio);
        btnActivity = (Button) findViewById(R.id.btnActivity);
        btnBio = (Button) findViewById(R.id.btnBio);
        appBarSidebtnMusicCircle = (Button) findViewById(R.id.appBarSidebtnMusicCircle);
        appBarSidebtnMusicCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, MelodyActivity.class);
                startActivity(i);
            }
        });

        rlPartProfile = (RelativeLayout) findViewById(R.id.rlPartProfile);
        rlFragmentActivity = (RelativeLayout) findViewById(R.id.rlFragmentActivity);
        rlFragmentBio = (RelativeLayout) findViewById(R.id.rlFragmentBio);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewProfile);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        ivAudio_feed = (ImageView) findViewById(R.id.audio_feed);
        ivDiscover = (ImageView) findViewById(R.id.discover);
        ivMessage = (ImageView) findViewById(R.id.message);
        ivProfile = (ImageView) findViewById(R.id.profile);

        userProfileImageInProf = (CircleImageView) findViewById(R.id.userProfileImageInProf);
        tvNameInProf = (TextView) findViewById(R.id.tvNameInProf);
        tvUserNameInProf = (TextView) findViewById(R.id.tvUserNameInProf);


        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        firstName = loginSharedPref.getString("firstName", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        statusNormal = loginSharedPref.getInt("status", 0);

        if (statusNormal == 1) {
            tvNameInProf.setText(firstName);
            tvUserNameInProf.setText(userNameLogin);
        }

        if (profilePicLogin != null) {
            //ivProfile.setVisibility(View.GONE);
            userProfileImageInProf.setVisibility(View.VISIBLE);
            Picasso.with(ProfileActivity.this).load(profilePicLogin).into(userProfileImageInProf);
        }


        SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        Name = twitterPref.getString("Name", null);
        userName = twitterPref.getString("userName", null);
        profilePic = twitterPref.getString("ProfilePic", null);
        statusTwitter = twitterPref.getInt("status", 0);

        if (statusTwitter == 1) {
            tvNameInProf.setText(Name);
            tvUserNameInProf.setText(userName);
        }

        if (profilePic != null) {
            //ivProfile.setVisibility(View.GONE);
            userProfileImageInProf.setVisibility(View.VISIBLE);
            Picasso.with(ProfileActivity.this).load(profilePic).into(userProfileImageInProf);
        }


        SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
        fbName = fbPref.getString("FbName", null);
        fbUserName = fbPref.getString("userName", null);
        fbId = fbPref.getString("fbId", null);
        statusFb = fbPref.getInt("status", 0);

        if (statusFb == 1) {
            tvNameInProf.setText(fbName);
            tvUserNameInProf.setText(fbUserName);
        }

        if (fbId != null) {
            //ivProfile.setVisibility(View.GONE);
            userProfileImageInProf.setVisibility(View.VISIBLE);
            Picasso.with(ProfileActivity.this).load("https://graph.facebook.com/" + fbId + "/picture").into(userProfileImageInProf);
        }


        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnBio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#FFFFFF"));

                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
                rlPartProfile.setVisibility(View.VISIBLE);
                getFragmentManager().popBackStack();

            }
        });

        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
                rlPartProfile.setVisibility(View.GONE);

                ProfileActivityFragment pactf = new ProfileActivityFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.activity_profile, pactf);
                fragmentTransaction.commit();
            }
        });

        btnBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnBio.setBackgroundColor(Color.parseColor("#FFFFFF"));
                rlPartProfile.setVisibility(View.GONE);


                BioFragment bioFragment = new BioFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_profile, bioFragment);
                fragmentTransaction.commit();
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

        ivDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        ivAudio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, StationActivity.class);
                startActivity(i);
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
                        String titleString;
                        TabHost.TabSpec spec;
                        final TabHost host = (TabHost) findViewById(R.id.tabHostProfile);
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
                        Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void fetchRecordings() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);
                        recordingList.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList);
                        adapter.notifyDataSetChanged();
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
                params.put(KEY, "");
                params.put(GENRE, genreString);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private TabHost.TabContentFactory createTabContent() {
        return new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                RecyclerView rv = new RecyclerView(ProfileActivity.this);
                rv.setHasFixedSize(true);
                RecyclerView.LayoutManager lm = new LinearLayoutManager(ProfileActivity.this);
                rv.setLayoutManager(lm);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(adapter);
                return rv;
            }
        };
    }

}
