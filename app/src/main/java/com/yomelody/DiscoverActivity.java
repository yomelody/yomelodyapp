package com.yomelody;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yomelody.Adapters.DiscoverAdapter;
import com.yomelody.Adapters.RecordingsCardAdapter;
import com.yomelody.Models.AdvertisePagingData;
import com.yomelody.Models.Genres;
import com.yomelody.Models.RecordingsModel;
import com.yomelody.Models.RecordingsPool;
import com.yomelody.Parse.ParseContents;
import com.yomelody.Services.LogoutService;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.Const;
import com.yomelody.utils.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

import static com.yomelody.app.Config.PUSH_NOTIFICATION;
import static com.yomelody.utils.Const.ServiceType.ADVERTISEMENT;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.GENERE;
import static com.yomelody.utils.Const.ServiceType.RECORDINGS;
import static com.yomelody.utils.Const.ServiceType.TOTAL_COUNT;

/**
 * Created by Saurabh Singh on 05/17/2016.
 */

public class DiscoverActivity extends AppCompatActivity {

    ImageView discover, message, ivBackButton, audio_feed, ivFilterDiscover, ivHomeDiscover, ivProfile;
    Button appBarSearchDiscoverBtn;
    EditText subEtFilterName, subEtFilterInstruments, subEtFilterBPM;
    TextView appBarMainTextDiscover, message_count;

    TabHost host;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    ArrayList<AdvertisePagingData> pagingDataArrayList = new ArrayList<>();
    private String ID = "id";
    private String KEY = "key";
    private String STATION = "station";
    private String GENRE = "genere";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";
    private String USER_NAME = "username";
    private String KEY_SEARCH = "search";
    private String COUNT = "count";

    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String genreString = "1";
    String userId, userNameLogin;
    String strName, strSearch, strArtist, strInstruments, strBPM;
    String titleString;
    String userIdNormal, userIdFb, userIdTwitter, artistName, Instruments, BPM;
    int statusNormal, statusFb, statusTwitter;
    ProgressDialog progressDialog;
    //    LongOperation myTask = null;
    RelativeLayout rlDiscoverSearch, rlViewPagerMain;
    android.support.v7.widget.SearchView search2;
    Button btnCancel;
    ViewPager recyclerViewPager;
    DiscoverAdapter adapterAdvertiseMent;
    Activity mActivity;
    RecyclerView rv;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    int totalCount = 0;
    LinearLayoutManager linearLayoutManager;
    private String msgUnsuccess = "No record found.";
    private final int count = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String limit = "limit";
    Button Melodybut;
    LinearLayout ll_dots;
    private TextView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        mActivity = DiscoverActivity.this;
        progressDialog = new ProgressDialog(DiscoverActivity.this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        clearSharedPref();
        Melodybut = (Button) findViewById(R.id.Melodybut);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        ivProfile = (ImageView) findViewById(R.id.ivProfileD);
        message_count = (TextView) findViewById(R.id.message_count);
        rlViewPagerMain = (RelativeLayout) findViewById(R.id.rlViewPagerMain);
        recyclerViewPager = (ViewPager) findViewById(R.id.vp_slider);
        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);
        advertisePaging();


        adapterAdvertiseMent = new DiscoverAdapter(pagingDataArrayList, DiscoverActivity.this);
        recyclerViewPager.setAdapter(adapterAdvertiseMent);
        recyclerViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userId = loginSharedPref.getString("userId", null);

        SharedPreferences loginFbSharedPref = this.getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
        userIdFb = loginFbSharedPref.getString("userId", null);
        statusFb = loginFbSharedPref.getInt("status", 0);
        SharedPreferences loginTwitterSharedPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        userIdTwitter = loginTwitterSharedPref.getString("userId", null);
        statusTwitter = loginTwitterSharedPref.getInt("status", 0);

        SharedPreferences filterPref = this.getSharedPreferences("FilterPref", MODE_PRIVATE);
        strName = filterPref.getString("stringFilter", null);
        SharedPreferences filterPrefArtist = this.getSharedPreferences("FilterPrefArtist", MODE_PRIVATE);
        strArtist = filterPrefArtist.getString("stringFilterArtist", null);
        SharedPreferences FilterInstruments = this.getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE);
        strInstruments = FilterInstruments.getString("stringFilterInstruments", null);
        SharedPreferences FilterBPM = this.getSharedPreferences("FilterPrefBPM", MODE_PRIVATE);
        strBPM = FilterBPM.getString("stringFilterBPM", null);

        if (statusNormal == 1) {
            userId = userIdNormal;
        } else if (statusFb == 1) {
            userId = userIdFb;
        } else if (statusTwitter == 1) {
            userId = userIdTwitter;
        }

        getTotalCount();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    getTotalCount();
                }
            }
        };

        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDiscover);
        ivFilterDiscover = (ImageView) findViewById(R.id.ivFilterDiscover);
        ivHomeDiscover = (ImageView) findViewById(R.id.ivHomeDiscover);
        appBarMainTextDiscover = (TextView) findViewById(R.id.appBarMainTextDiscover);
        rlDiscoverSearch = (RelativeLayout) findViewById(R.id.rlDiscoverSearch);
        search2 = (android.support.v7.widget.SearchView) findViewById(R.id.searchDiscover);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        appBarSearchDiscoverBtn = (Button) findViewById(R.id.appBarSearchDiscoverBtn);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        /*if (userId != null && userId != "") {
            adapter = new RecordingsCardAdapter(mActivity, recordingList, recordingsPools);
            fetchGenreNames();
        } else {
            Toast.makeText(getApplicationContext(), "Log in to view your Profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }*/

        adapter = new RecordingsCardAdapter(mActivity, recordingList, recordingsPools);
        fetchGenreNames();
        /*discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, DiscoverActivity.class);
                startActivity(intent);
            }
        });*/

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });
        Melodybut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), MelodyActivity.class);
                startActivity(it);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DiscoverActivity.this, StationActivity.class);
                startActivity(i);
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivHomeDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        ivFilterDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(DiscoverActivity.this);
//                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Filter Audio");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DiscoverActivity.this, android.R.layout.select_dialog_singlechoice);
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
                        if (strName.equals("Artist")) {
                            openDialog();
                        } else if (strName.equals("# of Instruments")) {
                            openDialogInstruments();
                        } else if (strName.equals("BPM")) {
                            openDialogBPM();
                        } else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(DiscoverActivity.this);
                            builderInner.setMessage(strName);
                            SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                            editorFilterString.putString("stringFilter", strName);
                            editorFilterString.apply();
                            SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                            editorSearchString.clear();
                            editorSearchString.apply();
                            builderInner.setTitle("Your Selected Item is");
                            recordingList.clear();
                            recordingsPools.clear();
                            fetchRecordingsFilter();
                        }
                    }
                });
                builderSingle.show();
            }
        });

        appBarSearchDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHomeDiscover.setVisibility(View.GONE);
                appBarMainTextDiscover.setVisibility(View.GONE);
                ivFilterDiscover.setVisibility(View.GONE);
                search2.setVisibility(View.VISIBLE);
                ((EditText) search2.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setHintTextColor(getResources().getColor(R.color.colorSearch));
                btnCancel.setVisibility(View.VISIBLE);
                appBarSearchDiscoverBtn.setVisibility(View.GONE);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlDiscoverSearch.setVisibility(View.VISIBLE);
                ivHomeDiscover.setVisibility(View.VISIBLE);
                appBarMainTextDiscover.setVisibility(View.VISIBLE);
                ivFilterDiscover.setVisibility(View.VISIBLE);
                search2.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                appBarSearchDiscoverBtn.setVisibility(View.VISIBLE);

            }
        });

        search2.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rlDiscoverSearch.setVisibility(View.VISIBLE);
                ivHomeDiscover.setVisibility(View.VISIBLE);
                appBarMainTextDiscover.setVisibility(View.VISIBLE);
                ivFilterDiscover.setVisibility(View.VISIBLE);
                appBarSearchDiscoverBtn.setVisibility(View.VISIBLE);
                search2.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                search2.isSubmitButtonEnabled();
                String searchContent = search2.getQuery().toString();
                SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                editorSearchString.putString("stringSearch", searchContent);
                editorSearchString.apply();
                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.clear();
                editorFilterString.apply();
                recordingList.clear();
                recordingsPools.clear();
                fetchSearchData();
                search2.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((EditText) search2.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setTextColor(getResources().getColor(R.color.colorSearch));
                return false;
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DiscoverActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });
    }

    private void callApi() {
        if (rv != null) {
            rv.setAdapter(adapter);
        }

        if (strName == null && strSearch == null) {
            fetchRecordings();
        } else if (strSearch != null) {
            fetchSearchData();
        } else if (strArtist != null) {
            fetchRecordingsFilterArtist();
        } else if (strInstruments != null && strName.equals("# of Instruments")) {
            fetchRecordingsFilterInstruments();
        } else if (strBPM != null && strName.equals("BPM")) {
            fetchRecordingsFilterBPM();
        } else {
            fetchRecordingsFilter();
        }
    }
    private void addBottomDots(int currentPage) {
        try {
            dots = new TextView[pagingDataArrayList.size()];

            ll_dots.removeAllViews();
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(Color.parseColor("#FFFFFF"));
                ll_dots.addView(dots[i]);
            }

            if (dots.length > 0)
                dots[currentPage].setTextColor(Color.parseColor("#000000"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void fetchGenreNames() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        TabHost.TabSpec spec;
                        final TabHost host = (TabHost) findViewById(R.id.tabHostDiscover);
                        host.setup();

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                /*myTask = new LongOperation();
                                myTask.execute();*/
                                callApi();
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
                                recordingList.clear();
                                recordingsPools.clear();
                                callApi();
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
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void advertisePaging() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADVERTISEMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=advertisePaging=" + response);
                        pagingDataArrayList.clear();
                        new ParseContents(getApplicationContext()).parseAdvertisePaging(response, pagingDataArrayList);
                        addBottomDots(0);
                        adapterAdvertiseMent.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=advertisePaging=" + params+"\nURL="+ADVERTISEMENT);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    public void fetchRecordings() {
        progressDialog.show();
        if (rv != null) {
            rv.setAdapter(adapter);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=fetchRecordings=" + response);
                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (!TextUtils.isEmpty(userId)){
                    params.put(ID, userId);
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=fetchRecordings=" + params + "\nURL==" + RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilter() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=filter=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
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
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (!TextUtils.isEmpty(userId)){
                    params.put(ID, userId);
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=filter=" + params + "\nURL==" + RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(DiscoverActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterName = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(this);
        builder2.setTitle("ArtistName");
        builder2.setMessage("Choose Artist Name to Search Artist");
        builder2.setView(subView);

        TextView title = new TextView(this);
        title.setText("ArtistName");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder2.setCustomTitle(title);

        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                artistName = subEtFilterName.getText().toString().trim();
                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.putString("stringFilterArtist", artistName);
                editorFilterArtist.apply();
                recordingList.clear();
                recordingsPools.clear();
                fetchRecordingsFilterArtist();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterName.getWindowToken(), 0);
            }
        });

        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterName.getWindowToken(), 0);
            }
        });

        builder2.show();
    }

    private void openDialogInstruments() {
        LayoutInflater inflater = LayoutInflater.from(DiscoverActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterInstruments = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder3 = new android.support.v7.app.AlertDialog.Builder(this);
        builder3.setTitle("Number of Instruments");
        builder3.setMessage("Give Instruments Value to Filter");
        builder3.setView(subView);

        TextView title = new TextView(this);
        title.setText("Instruments");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder3.setCustomTitle(title);

        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                Instruments = subEtFilterInstruments.getText().toString().trim();
                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.putString("stringFilterInstruments", Instruments);
                editorFilterInstruments.apply();
                recordingList.clear();
                recordingsPools.clear();
                fetchRecordingsFilterInstruments();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterInstruments.getWindowToken(), 0);

            }
        });

        builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterInstruments.getWindowToken(), 0);
            }
        });

        builder3.show();
    }

    private void openDialogBPM() {
        LayoutInflater inflater = LayoutInflater.from(DiscoverActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterBPM = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder3 = new android.support.v7.app.AlertDialog.Builder(this);
        builder3.setTitle("BPM");
        builder3.setMessage("Give BPM Value to Filter");
        builder3.setView(subView);

        TextView title = new TextView(this);
        title.setText("BPM");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder3.setCustomTitle(title);

        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                BPM = subEtFilterBPM.getText().toString().trim();
                SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                editorFilterBPM.putString("stringFilterBPM", BPM);
                editorFilterBPM.apply();
                recordingList.clear();
                recordingsPools.clear();
                fetchRecordingsFilterBPM();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterBPM.getWindowToken(), 0);

            }
        });

        builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterBPM.getWindowToken(), 0);
            }
        });

        builder3.show();
    }

    public void fetchRecordingsFilterArtist() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=artist=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (!TextUtils.isEmpty(userId)){
                    params.put(ID, userId);
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, artistName);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=artist=" + params + "\nURL==" + RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchSearchData() {
        progressDialog.show();
        SharedPreferences searchPref = this.getSharedPreferences("SearchPref", MODE_PRIVATE);
        strSearch = searchPref.getString("stringSearch", null);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=search=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = "";
                        isLoading = false;
                        isLastPage = false;
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (!TextUtils.isEmpty(userId)){
                    params.put(ID, userId);
                }
                params.put(KEY, STATION);
                params.put(KEY_SEARCH, strSearch);
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=search=" + params + "\nURL==" + RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterInstruments() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=instrument=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (!TextUtils.isEmpty(userId)){
                    params.put(ID, userId);
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, Instruments);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=instrument=" + params + "\nURL==" + RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterBPM() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=BPM=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (!TextUtils.isEmpty(userId)){
                    params.put(ID, userId);
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, BPM);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=BPM=" + params + "\nURL==" + RECORDINGS);
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
                rv = new RecyclerView(DiscoverActivity.this);
                rv.setHasFixedSize(true);
                linearLayoutManager = new LinearLayoutManager(DiscoverActivity.this);
                rv.setLayoutManager(linearLayoutManager);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(adapter);

                rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int totalItemCount = linearLayoutManager.getItemCount();
                        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
//                        AppHelper.sop("onScrolled==visibleItemCount"+visibleItemCount+"=totalItemCount="+
//                                totalItemCount+ "=firstVisibleItemPosition="+firstVisibleItemPosition);
                        if (!isLoading && !isLastPage) {
//                            AppHelper.sop("isLoading==isLastPage");

                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                                    firstVisibleItemPosition >= 0 && totalItemCount >= count) {
                                isLoading = true;
                                if (AppHelper.checkNetworkConnection(mActivity)) {
                                    callApi();
                                }
                            }
                        }
                    }
                });

                return rv;
            }
        };
    }

    private void clearSharedPref() {
        try {

            SharedPreferences.Editor FilterPref = mActivity.getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
            FilterPref.clear();
            FilterPref.apply();
            SharedPreferences.Editor SearchPref = mActivity.getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
            SearchPref.clear();
            SearchPref.apply();
            SharedPreferences.Editor FilterPrefArtist = mActivity.getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
            FilterPrefArtist.clear();
            FilterPrefArtist.apply();
            SharedPreferences.Editor FilterPrefInstruments = mActivity.getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
            FilterPrefInstruments.clear();
            FilterPrefInstruments.apply();
            SharedPreferences.Editor FilterPrefBPM = mActivity.getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
            FilterPrefBPM.clear();
            FilterPrefBPM.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("onActivityResult=requestCode=" + requestCode + "=resultCode=" + resultCode);
        if (requestCode == RecordingsCardAdapter.REQUEST_RECORDING_COMMENT) {
            if (resultCode == RESULT_OK) {
                recordingList.clear();
                recordingsPools.clear();
                callApi();
            }
            else {
                SharedPreferences socialStatusPref = getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE);
                if (socialStatusPref.getBoolean(Const.REC_SHARE_STATUS, false)) {
                    SharedPreferences.Editor socialStatusPrefEditor = getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE).edit();
                    socialStatusPrefEditor.putBoolean(Const.REC_SHARE_STATUS, false);
                    socialStatusPrefEditor.apply();
                    recordingList.clear();
                    recordingsPools.clear();
                    callApi();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //startService(new Intent(this, LogoutService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTotalCount();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Const.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    public void getTotalCount() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TOTAL_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(HomeActivity.this, "" + response.toString();, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                String str = jsonObject.getString("newMessage");
                                totalCount = Integer.parseInt(str);
                                if (totalCount > 0) {
                                    message_count.setText(str);
                                    message_count.setVisibility(View.VISIBLE);
                                } else {
                                    message_count.setVisibility(View.GONE);
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
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            errorMsg = "There is either no connection or it timed out.";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "ServerError";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "Network Error";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                if (!TextUtils.isEmpty(userId)){
                    params.put("userid", userId);
                }
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
