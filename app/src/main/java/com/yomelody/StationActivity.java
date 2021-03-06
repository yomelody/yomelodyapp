package com.yomelody;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

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
import com.yomelody.Adapters.ActivityCardAdapter;
import com.yomelody.Fragments.ActivityFragment;
import com.yomelody.Fragments.AudioFragment;
import com.yomelody.Fragments.UsersFragment;
import com.yomelody.Models.RecordingsModel;
import com.yomelody.Models.RecordingsPool;
import com.yomelody.Services.LogoutService;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.Const;
import com.yomelody.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.yomelody.app.Config.PUSH_NOTIFICATION;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.TOTAL_COUNT;

/**
 * Created by Saurabh Singh on 1/13/2017.
 */

public class StationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    Button btnActivity, btnAudio, btnCancel,btnUser;
    RelativeLayout rlFragmentActivity, rlPartStation, rlSearch;
    public static ImageView ivBackButton, ivHomeButton, discover, message, ivProfile, audio_feed, ivStationSearch,ivStationSearchActivity, ivMelodyStation, ivFilter;
    EditText subEtFilterName, subEtFilterInstruments, subEtFilterBPM;
    TextView message_count;
    TabHost host;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    private MenuItem searchMenuItem;
    SearchView searchView, search1;
    ListView list;
    ArrayAdapter<String> instrumentAdapter;
    String[] values = new String[]{"Latest", "Trending", "Favourite", "Artist", "# of Instruments", "BPM"};

    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    private String ID = "id";
    private String KEY = "key";
    private String STATION = "station";
    private String GENRE = "genere";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";
    private String KEY_SEARCH = "search";

    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String genreString = "1";
    String userId, userNameLogin;
    String userIdNormal, userIdFb, userIdTwitter;
    int statusNormal, statusFb, statusTwitter;
    String strName, strSearch;
    String titleString;
    String searchGet, search5;
    String artistName, Instruments, BPM;
    ProgressDialog progressDialog;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    AudioFragment af;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    int totalCount = 0;
    Activity mActivity;
    public static boolean IsActivity=false;
    private boolean isUsers=false;
    ActivityFragment actf;
    UsersFragment usersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        mActivity = StationActivity.this;
        clearSharePrefStation();

        message = (ImageView) findViewById(R.id.message);
        discover = (ImageView) findViewById(R.id.discover);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        ivStationSearch = (ImageView) findViewById(R.id.ivStationSearch);
        ivStationSearchActivity = (ImageView) findViewById(R.id.ivStationSearchActivity);
        ivMelodyStation = (ImageView) findViewById(R.id.ivMelodyStation);
        ivFilter = (ImageView) findViewById(R.id.ivFilter);
        btnActivity = (Button) findViewById(R.id.btnActivity);
        btnAudio = (Button) findViewById(R.id.btnAudio);
        btnUser = (Button) findViewById(R.id.btnUser);
        rlFragmentActivity = (RelativeLayout) findViewById(R.id.rlFragmentActivity);
        rlPartStation = (RelativeLayout) findViewById(R.id.rlPartStation);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        search1 = (SearchView) findViewById(R.id.search1);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        list = (ListView) findViewById(R.id.list);
        message_count = (TextView) findViewById(R.id.message_count);

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
        IsActivity=false;
        getTotalCount();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    getTotalCount();
                }
            }
        };

        if (getIntent()!=null && getIntent().hasExtra("notification_type")){
            btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
            btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
            btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
            btnActivity.setEnabled(false);
            btnAudio.setEnabled(true);
            IsActivity=true;
            clearSharePrefStation();
            ivFilter.setVisibility(View.GONE);
            actf = new ActivityFragment();
            getFragmentManager().beginTransaction().replace(R.id.activity_station, actf).commit();
        }
        else {
            af = new AudioFragment();
            getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();
            btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
            btnUser.setBackgroundColor(Color.parseColor("#E4E4E4"));
            btnAudio.setBackgroundColor(Color.parseColor("#FFFFFF"));
            btnAudio.setEnabled(false);
            btnActivity.setEnabled(true);
            ivStationSearchActivity.setVisibility(View.GONE);
        }

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnUser.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnAudio.setEnabled(false);
                btnActivity.setEnabled(true);
                btnUser.setEnabled(true);
                IsActivity=false;
                isUsers=false;
                ivStationSearchActivity.setVisibility(View.VISIBLE);

                //new FetchActivityDetails().execute(userId);
                clearSharePrefStation();

                AudioFragment af = new AudioFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();


            }
        });

        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnUser.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnActivity.setEnabled(false);
                btnAudio.setEnabled(true);
                btnUser.setEnabled(true);
                IsActivity=true;
                isUsers=false;
                clearSharePrefStation();
                ivFilter.setVisibility(View.GONE);
                actf = new ActivityFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, actf).commit();


            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnUser.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnUser.setEnabled(false);
                btnActivity.setEnabled(true);
                btnAudio.setEnabled(true);
                IsActivity=false;
                isUsers=true;
                clearSharePrefStation();
                ivFilter.setVisibility(View.GONE);
                usersFragment = new UsersFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, usersFragment).commit();
            }
        });

        ivStationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                searchMenuItem.setVisible(position == 0);
                rlSearch.setVisibility(View.GONE);
                search1.setVisibility(View.VISIBLE);
                ((EditText) search1.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setHintTextColor(getResources().getColor(R.color.colorSearch));
                btnCancel.setVisibility(View.VISIBLE);
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSearch.setVisibility(View.VISIBLE);
                search1.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);

                clearSharePrefStation();

//                Toast.makeText(StationActivity.this, "" + searchContent, Toast.LENGTH_SHORT).show();
            }
        });

        search1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rlSearch.setVisibility(View.VISIBLE);
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
                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.putString("stringFilterArtist", artistName);
                editorFilterArtist.apply();
                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.putString("stringFilterInstruments", Instruments);
                editorFilterInstruments.apply();
                if(IsActivity){
                    btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                    btnUser.setBackgroundColor(Color.parseColor("#E4E4E4"));
                    btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    //btnActivity.setEnabled(false);
                    //btnAudio.setEnabled(true);
                    //IsActivity=false;
                    ivFilter.setVisibility(View.GONE);
                    search1.setQuery("", false);
                    search1.clearFocus();
                    search1.setIconified(true);
                    ActivityFragment actf = new ActivityFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_station, actf).commit();
                }
                else if (isUsers){
                    btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                    btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                    btnUser.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                    btnUser.setEnabled(false);
//                    btnActivity.setEnabled(true);
//                    btnAudio.setEnabled(true);
//                    IsActivity=false;
//                    isUsers=true;
                    ivFilter.setVisibility(View.GONE);
                    search1.setQuery("", false);
                    search1.clearFocus();
                    search1.setIconified(true);
                    usersFragment = new UsersFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_station, usersFragment).commit();

                }
                else {
                    search1.setQuery("", false);
                    search1.clearFocus();
                    search1.setIconified(true);
                    //IsActivity=false;
                    btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                    btnUser.setBackgroundColor(Color.parseColor("#E4E4E4"));
                    btnAudio.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    //btnAudio.setEnabled(false);
                    //btnActivity.setEnabled(true);
                    AudioFragment af = new AudioFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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

                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        ivMelodyStation.setOnClickListener(new View.OnClickListener() {
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
                        if (strName.equals("Artist")) {
                            openDialog();
                        } else if (strName.equals("# of Instruments")) {
                            openDialogInstruments();
                        } else if (strName.equals("BPM")) {
                            openDialogBPM();
                        } else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(StationActivity.this);
                            builderInner.setMessage(strName);
                            SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                            editorFilterString.putString("stringFilter", strName);
                            editorFilterString.apply();
                            SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                            editorSearchString.clear();
                            editorSearchString.apply();
                            SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                            editorFilterArtist.putString("stringFilterArtist", artistName);
                            editorFilterArtist.apply();
                            SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                            editorFilterInstruments.putString("stringFilterInstruments", Instruments);
                            editorFilterInstruments.apply();
                            SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                            editorFilterBPM.clear();
                            editorFilterBPM.apply();
                            builderInner.setTitle("Your Selected Item is");
                            AudioFragment af = new AudioFragment();
                            getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();
                            /*if(IsActivity){
                                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                                btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                btnActivity.setEnabled(false);
                                btnAudio.setEnabled(true);
                                IsActivity=false;
                                search1.setQuery("",false);
                                search1.setQueryHint("search...");
                                search1.clearFocus();
                                clearSharePrefStation();
                                ivFilter.setVisibility(View.GONE);
                                ActivityFragment actf = new ActivityFragment();
                                getFragmentManager().beginTransaction().replace(R.id.activity_station, actf).commit();
                            }else {
                                AudioFragment af = new AudioFragment();
                                getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();

                            }*/
                        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
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
        search1 = (SearchView) searchMenuItem.getActionView();
        search1.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        search1.setSubmitButtonEnabled(true);
        search1.setOnQueryTextListener(this);


        searchGet = (String) search1.getQuery();
        Log.d("msg", searchGet);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        rlSearch.setVisibility(View.VISIBLE);
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
        if(IsActivity){
            btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
            btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //btnActivity.setEnabled(false);
            //btnAudio.setEnabled(true);
            IsActivity=false;
            search1.setQuery("", false);
            search1.clearFocus();
            search1.setIconified(true);
            ivFilter.setVisibility(View.GONE);
            ActivityFragment actf = new ActivityFragment();
            getFragmentManager().beginTransaction().replace(R.id.activity_station, actf).commit();
        }else {
            IsActivity=false;
            search1.setQuery("", false);
            search1.clearFocus();
            search1.setIconified(true);
            AudioFragment af = new AudioFragment();
            getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();
        }
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }


    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(StationActivity.this);
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

                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.putString("stringFilter", strName);
                editorFilterString.apply();

                SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                editorFilterBPM.clear();
                editorFilterBPM.apply();

                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.clear();
                editorFilterInstruments.apply();

                AudioFragment af = new AudioFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();

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
        LayoutInflater inflater = LayoutInflater.from(StationActivity.this);
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

                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.putString("stringFilter", strName);
                editorFilterString.apply();

                SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                editorFilterBPM.clear();
                editorFilterBPM.apply();

                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.clear();
                editorFilterArtist.apply();

                AudioFragment af = new AudioFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();


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
        LayoutInflater inflater = LayoutInflater.from(StationActivity.this);
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

                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.putString("stringFilter", strName);
                editorFilterString.apply();

                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.clear();
                editorFilterArtist.apply();

                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.clear();
                editorFilterInstruments.apply();

                AudioFragment af = new AudioFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();


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

    public void clearSharePrefStation() {
        SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
        editorFilterBPM.clear();
        editorFilterBPM.apply();

        SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
        editorFilterArtist.clear();
        editorFilterArtist.apply();

        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();

        SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
        editorFilterInstruments.clear();
        editorFilterInstruments.apply();

        SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
        editorSearchString.clear();
        editorSearchString.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //startService(new Intent(this, LogoutService.class));
        /*if (RecordingsCardAdapter.mp != null) {
            try {
                RecordingsCardAdapter.mp.reset();
                RecordingsCardAdapter.mp.release();
                RecordingsCardAdapter.mp=null;
                StationActivity.this.recreate();
                try {
                    //AudioFragment af = new AudioFragment();
                    //getFragmentManager().beginTransaction().replace(R.id.activity_station, af).commit();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("onActivityResult=of=StationActivity=requestCode" + requestCode + "=resultCode=" + resultCode + "=data=" + data);
        if (requestCode== ActivityCardAdapter.REQUEST_USER_ACTIVITY){
            if (actf != null) {
                actf.onActivityResult(requestCode, resultCode, data);
            }
        }
        else {
            if (af != null) {
                af.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTotalCount();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Const.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
        //LogoutService.timer.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //LogoutService.timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LogoutService.timer.cancel();
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
                params.put("userid", userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}