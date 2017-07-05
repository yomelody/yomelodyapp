package com.instamelody.instamelody;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
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
import com.google.gson.Gson;
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Fragments.ActivityFragment;
import com.instamelody.instamelody.Fragments.AudioFragment;
import com.instamelody.instamelody.Fragments.BioFragment;
import com.instamelody.instamelody.Fragments.ProfileActivityFragment;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsData;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserDetails;
import com.instamelody.instamelody.Parse.ParseContents;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.instamelody.instamelody.R.id.bio_fragment;
import static com.instamelody.instamelody.R.id.rlPartStation;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.RECORDINGS;
import static com.instamelody.instamelody.utils.Const.ServiceType.USERS_BIO;

/**
 * Created by Saurabh Singh on 01/09/2017
 */
public class ProfileActivity extends AppCompatActivity {

    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String KEY_RESULT = "result";
    String genreString = "1";
    String USER_ID = "user_id";
    String FOLLOWER_ID = "followerID";

    private String ID = "id";
    private String KEY = "key";
    private String GENRE = "genere";

    String flag;

    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    Button btnAudio, btnActivity, btnBio, btnCancel;
    RelativeLayout rlPartProfile, rlFragmentActivity, rlFragmentBio, rlSearch, rlFollow, rlMessage;
    ImageView ivBackButton, ivHomeButton, ivAudio_feed, ivDiscover, ivMessage, ivProfile, ivSound, userCover, ivToMelody;
    ImageView ivFollow, ivUnfollow;
    CircleImageView userProfileImageInProf;
    TextView tvNameInProf, tvUserNameInProf, tv_records, tv_fans, tv_following;
    String firstName, userNameLogin, profilePicLogin, Name, userName, profilePic, fbName, fbUserName, fbId, coverPic;
    String userId, records, fans, followers, followerId;
    String userIdNormal, userIdFb, userIdTwitter;
    int statusNormal, statusFb, statusTwitter, status;
    SearchView search1;
    ProgressDialog progressDialog;
    LongOperation myTask = null;
    TabHost host;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<RecordingsModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (bundle != null) {
            String checkUserId = bundle.getString("checkUserId");
            if (checkUserId != null) {
                userId = checkUserId;
            }
//            rlFollow.setVisibility(View.VISIBLE);
//            rlMessage.setVisibility(View.VISIBLE);
            flag = "1";
        } else {
            if (loginSharedPref.getString("userId", null) != null) {
                userId = loginSharedPref.getString("userId", null);
                status = 1;
            } else if (fbPref.getString("userId", null) != null) {
                userId = fbPref.getString("userId", null);
                status = 2;
            } else if (twitterPref.getString("userId", null) != null) {
                userId = twitterPref.getString("userId", null);
                status = 3;
            }
//            rlFollow.setVisibility(View.VISIBLE);
//            rlMessage.setVisibility(View.VISIBLE);
            flag = "2";
        }

        if (userId != null) {
            if (flag.equals("1")) {
                fetchUserBio();
            }
            if (flag.equals("2")) {
//                fetchUserFromPrefs(status);
                fetchUserFromPrefs(Integer.parseInt(userId));
            }
            fetchGenreNames();
            fetchRecordings();

        } else {
            Toast.makeText(getApplicationContext(), "Log in to view your Profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

        adapter = new RecordingsCardAdapter(this, recordingList, recordingsPools);

        search1 = (SearchView) findViewById(R.id.searchOnProf);
        btnAudio = (Button) findViewById(R.id.btnAudio);
        btnActivity = (Button) findViewById(R.id.btnActivity);
        btnBio = (Button) findViewById(R.id.btnBio);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        ivSound = (ImageView) findViewById(R.id.ivSound);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        ivToMelody = (ImageView) findViewById(R.id.ivToMelody);

        rlPartProfile = (RelativeLayout) findViewById(R.id.rlPartProfile);
        rlFragmentActivity = (RelativeLayout) findViewById(R.id.rlFragmentActivity);
        rlFragmentBio = (RelativeLayout) findViewById(R.id.rlFragmentBio);
        rlFollow = (RelativeLayout) findViewById(R.id.rlFollow);
        rlMessage = (RelativeLayout) findViewById(R.id.rlMessage);
        ivUnfollow = (ImageView) findViewById(R.id.ivUnfollow);
        ivFollow = (ImageView) findViewById(R.id.ivFollow);

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
        userCover = (ImageView) findViewById(R.id.userCover);
        tvNameInProf = (TextView) findViewById(R.id.tvNameInProf);
        tvUserNameInProf = (TextView) findViewById(R.id.tvUserNameInProf);
        tv_records = (TextView) findViewById(R.id.tv_records);
        tv_fans = (TextView) findViewById(R.id.tv_fans);
        tv_following = (TextView) findViewById(R.id.tv_following);

        ivToMelody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, MelodyActivity.class);
                startActivity(i);
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

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnBio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#FFFFFF"));

//                List<Fragment> fragments = getSupportFragmentManager().getFragments();
//                if (fragments != null) {
//                    for (Fragment fragment : fragments) {
//                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//                    }
//                }
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

        ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivFollow.setVisibility(View.GONE);
                ivUnfollow.setVisibility(View.VISIBLE);
                Follow(followerId);
            }
        });

        ivUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivUnfollow.setVisibility(View.GONE);
                ivFollow.setVisibility(View.VISIBLE);
                Follow(followerId);
            }
        });
    }

    public void fetchUserBio() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_BIO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        String rsp = response;
//                        Toast.makeText(getApplicationContext(), "" + rsp, Toast.LENGTH_SHORT).show();
//                        Log.d("ReturnData", response);
                        JSONObject jsonObject;
                        JSONArray jsonArray;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESULT);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    UserDetails userDetails = new UserDetails();
                                    JSONObject userJson = jsonArray.getJSONObject(i);

                                    followerId = userJson.getString("id");
                                    Name = userJson.getString("fname") + " " + userJson.getString("lname");
                                    if (!Name.equals("")) {
                                        tvNameInProf.setText(Name);
                                    }
                                    userName = userJson.getString("username");
                                    if (!userName.equals("")) {
                                        tvUserNameInProf.setText("@" + userName);
                                    }
                                    records = userJson.getString("records");
                                    if (!records.equals("")) {
                                        tv_records.setText("Records: " + records);
                                    }
                                    fans = userJson.getString("fans");
                                    if (!fans.equals("")) {
                                        tv_fans.setText("Fans: " + fans);
                                    }
                                    followers = userJson.getString("followers");
                                    if (!followers.equals("")) {
                                        tv_following.setText("Following: " + followers);
                                    }
                                    profilePic = userJson.getString("profilepic");
                                    userProfileImageInProf.setVisibility(View.VISIBLE);
                                    Picasso.with(ProfileActivity.this).load(profilePic).into(userProfileImageInProf);
                                    coverPic = userJson.getString("coverpic");
                                    Picasso.with(ProfileActivity.this).load(coverPic).into(userCover);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                        Toast.makeText(getApplicationContext(), errorMsg + " dumbo", Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY, "passed");
                params.put(USER_ID, userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchUserFromPrefs(final int stats) {

//        if (stats == 1) {
//            SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
//            Name = loginSharedPref.getString("firstName", null);
//            userName = loginSharedPref.getString("userName", null);
//            profilePic = loginSharedPref.getString("profilePic", null);
//
//        } else if (stats == 2) {
//            SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
//            Name = fbPref.getString("FbName", null);
//            userName = fbPref.getString("userName", null);
//            fbId = fbPref.getString("fbId", null);
//            profilePic = "https://graph.facebook.com/" + fbId + "/picture";
//
//        } else if (stats == 3) {
//            SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
//            Name = twitterPref.getString("Name", null);
//            userName = twitterPref.getString("userName", null);
//            profilePic = twitterPref.getString("ProfilePic", null);
//
//        }

//        tvNameInProf.setText(Name);
//        tvUserNameInProf.setText("@" + userName);
//        userProfileImageInProf.setVisibility(View.VISIBLE);
//        Picasso.with(ProfileActivity.this).load(profilePic).into(userProfileImageInProf);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_BIO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject;
                        JSONArray jsonArray;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESULT);
                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    UserDetails userDetails = new UserDetails();
                                    JSONObject userJson = jsonArray.getJSONObject(i);

                                    followerId = userJson.getString("id");
                                    Name = userJson.getString("fname") + " " + userJson.getString("lname");
                                    if (!Name.equals("")) {
                                        tvNameInProf.setText(Name);
                                    }
                                    userName = userJson.getString("username");
                                    if (!userName.equals("")) {
                                        tvUserNameInProf.setText("@" + userName);
                                    }
                                    records = userJson.getString("records");
                                    if (!records.equals("")) {
                                        tv_records.setText("Records: " + records);
                                    }
                                    fans = userJson.getString("fans");
                                    if (!fans.equals("")) {
                                        tv_fans.setText("Fans: " + fans);
                                    }
                                    followers = userJson.getString("followers");
                                    if (!followers.equals("")) {
                                        tv_following.setText("Following: " + followers);
                                    }
                                    profilePic = userJson.getString("profilepic");
                                    userProfileImageInProf.setVisibility(View.VISIBLE);
                                    Picasso.with(ProfileActivity.this).load(profilePic).into(userProfileImageInProf);
                                    coverPic = userJson.getString("coverpic");
                                    Picasso.with(ProfileActivity.this).load(coverPic).into(userCover);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                        Toast.makeText(getApplicationContext(), errorMsg + " dumbo", Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY, "passed");
                params.put(USER_ID, String.valueOf(stats));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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
                                myTask = new LongOperation();
                                myTask.execute();
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Genres genres = new Genres();
                                    genreJson = jsonArray.getJSONObject(i);
                                    titleString = genreJson.getString(KEY_GENRE_NAME);
                                    genres.setName(titleString);
                                    genres.setId(genreJson.getString(KEY_GENRE_ID));
                                    genresArrayList.add(genres);
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
                                genreString = arg0;
                                int currentTab = host.getCurrentTab();
                                if (currentTab == 0) {
                                    genreString = "";
                                } else {
                                    genreString = genresArrayList.get(currentTab).getId();
                                }
//                                genreString = String.valueOf(currentTab).trim();
                                fetchRecordings();

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
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
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

    public void Follow(final String follower_Id) {

        final String fid = follower_Id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FOLLOW_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

//                        String rsp = response;
//                        Toast.makeText(getApplicationContext(), "" + rsp, Toast.LENGTH_SHORT).show();
//                        Log.d("ReturnData", response);

                        JSONObject jsonObject, jsobj;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                jsobj = jsonObject.getJSONObject("response");
                                String msg = jsobj.getString("msg");
                                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                String followers = jsobj.getString("follow_count");
                                if (!followers.equals("")) {
                                    tv_following.setText("Following: " + followers);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                        Toast.makeText(getApplicationContext(), errorMsg + " dumbo", Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY, "passed");
                params.put(USER_ID, userId);
                params.put(FOLLOWER_ID, fid);
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

    private class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params) {

            try {
                //Getting data from server

                String filename = "myfile";
                String outputString = "Hello world!";

                URL aurl = new URL("http://35.165.96.167/api/upload_cover_melody_file.php");

                URLConnection connection = aurl.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(aurl.openStream(), 8192);

                try {
                    FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(outputString.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    FileInputStream inputStream = openFileInput(filename);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    r.close();
                    inputStream.close();
                    Log.d("File", "File contents: " + total);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            progressDialog.dismiss();
        }

    }

}
