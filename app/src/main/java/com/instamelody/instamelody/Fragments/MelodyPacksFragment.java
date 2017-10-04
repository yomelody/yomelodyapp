package com.instamelody.instamelody.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
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
import com.instamelody.instamelody.Adapters.MelodyCardListAdapter;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserMelodyCard;
import com.instamelody.instamelody.Models.UserMelodyPlay;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StudioActivity;
import com.instamelody.instamelody.utils.AppHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.MELODY;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class MelodyPacksFragment extends Fragment {


    RecyclerView.Adapter adapter, adapter1;
    ArrayList<MelodyCard> melodyList = new ArrayList<>();
    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_GENRE_ID = "id";
    String KEY_RESPONSE = "response";//JSONArray
    String users_id = "users_id";
    String KEY = "key";
    String GENRE = "genere";
    String genreString = "1";

    String USER_ID = "users_id";
    String USERS_ID = "users_id";
    private String KEY_SEARCH = "search";
    private String USER_NAME = "username";
    private String COUNT = "count";

    String home;
    ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    String KEY_MSG = "msg";
    String packName;
    String packId = "0";
    String resp;
    String strName, strSearch, strArtist, strInstruments, strBPM;
    String userId;

    private String ID = "id";
    private String STATION = "station";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";
    private String SAVE_MELODY = "save_melody";
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    ArrayList<UserMelodyCard> userMelodyList = new ArrayList<>();
    ArrayList<UserMelodyPlay> melodyPools = new ArrayList<>();
    TabHost host = null;
    ProgressDialog progressDialog;
    LinearLayoutManager linearLayoutManager;
    int post = 0;
    Activity mActivity;
    RecyclerView rv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        fetchGenreNames();
        SharedPreferences filterPref = getActivity().getSharedPreferences("FilterPref", MODE_PRIVATE);
        strName = filterPref.getString("stringFilter", null);
        SharedPreferences searchPref = getActivity().getSharedPreferences("SearchPref", MODE_PRIVATE);
        strSearch = searchPref.getString("stringSearch", null);
        SharedPreferences filterPrefArtist = getActivity().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE);
        strArtist = filterPrefArtist.getString("stringFilterArtist", null);
        SharedPreferences FilterInstruments = getActivity().getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE);
        strInstruments = FilterInstruments.getString("stringFilterInstruments", null);
        SharedPreferences FilterBPM = getActivity().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE);
        strBPM = FilterBPM.getString("stringFilterBPM", null);

        SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getActivity().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getActivity().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
            //MelodyUser=userId;
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
            //MelodyUser=userId;
        }
        new LongOperation().execute();

//        adapter = new RecordingsCardAdapter(getActivity(), recordingList, recordingsPools);


//        SharedPreferences fromHome = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE);
//        home = fromHome.getString("click", null);
//        try {
//            if (home.equals("from home")) {
//                SharedPreferences.Editor FilterPref = getActivity().getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
//                FilterPref.clear();
//                FilterPref.apply();
//                StudioActivity.joinRecordingId=null;
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
        String joinRecordingId;
        SharedPreferences fromJoin = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE);
        joinRecordingId = fromJoin.getString("instrumentsPos", null);
        if (StudioActivity.joinRecordingId != null || joinRecordingId != null) {
            try {
                SharedPreferences.Editor FilterPref1 = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE).edit();
                FilterPref1.clear();
                FilterPref1.apply();
                StudioActivity.melodyPackId = null;
                //  Toast.makeText(getActivity(), "" + StudioActivity.joinRecordingId, Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

        RecordingsFragment rf = new RecordingsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.melodyPackFragment, rf);
        transaction.addToBackStack(null);
        transaction.commit();
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_melody_packs, container, false);
        mActivity=getActivity();
        return view;
    }

    public void fetchGenreNames() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            AppHelper.sop("response=="+response);
                            JSONObject jsonObject, genreJson;
                            JSONArray jsonArray;
                            String titleString;
                            TabHost.TabSpec spec;
                            try {
                                host = (TabHost) getActivity().findViewById(R.id.tabHostMelodyPacks);
                                host.setup();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }


                            try {
                                jsonObject = new JSONObject(response);
                                if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                    jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Genres genres = new Genres();
                                        genreJson = jsonArray.getJSONObject(i);
                                        titleString = genreJson.getString(KEY_GENRE_NAME);
                                        genres.setName(titleString);
                                        genres.setId(genreJson.getString(KEY_GENRE_ID));
                                        genresArrayList.add(genres);
                                        try {
                                            spec = host.newTabSpec(titleString);
                                            spec.setIndicator(titleString);
                                            spec.setContent(createTabContent());
                                            host.addTab(spec);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                                    @Override
                                    public void onTabChanged(String arg0) {
                                        packName = arg0;
                                        int currentTab = host.getCurrentTab();
                                        if (currentTab == 0) {
                                            packId = "";
                                        } else {
                                            packId = (genresArrayList.get(currentTab)).getId();
                                        }
                                        fetchMelodyPacks();
                                    }
                                });
                            } catch (NullPointerException e) {
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
//                            errorMsg = "AuthFailureError";
                            } else if (error instanceof ServerError) {
                                errorMsg = "We are facing problem in connecting to server";
                            } else if (error instanceof NetworkError) {
                                errorMsg = "We are facing problem in connecting to network";
                            } else if (error instanceof ParseError) {
//                            errorMsg = "ParseError";
                            }
                            try {
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                                Log.d("Error", errorMsg);
                            } catch (Throwable throwable) {
                                Log.d("Error", throwable.toString());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(SAVE_MELODY, "saverecording");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    AppHelper.sop("params=="+params+"\nURL=="+GENERE);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void fetchMelodyPacks() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response==="+response);
                        melodyList.clear();
                        instrumentList.clear();
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("unsuccess")) {
                                String str = jsonObject.getString(KEY_MSG);
                                if (str.equals("No pack found")) {
                                    str = "Sorry, no " + packName + " melody available.";
                                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
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
                        try {
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                        } catch (Throwable throwable) {
                            Log.d("Fetch Melody Packs Error", throwable.toString());
                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (packId.equals("7") && userId != null) {
                    params.put(USER_ID, userId);
                    params.put(FILE_TYPE, "user_melody");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else if (userId != null) {
                    params.put(users_id, userId);
                    params.put(GENRE, packId);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else {
                    params.put(GENRE, packId);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                }
                try {
                    SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    String userId = loginSharedPref.getString("userId", null);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
//                if (userId != null) {
////                    params.put(USER_ID, userId);
//                }
                AppHelper.sop("params==="+params+"\nURL=="+MELODY);
                return params;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilter() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=="+response);
//                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData1", response);
                        melodyList.clear();
                        instrumentList.clear();
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchMelodySearchData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=="+response);
                        String successMsg = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(successMsg);
                            String flag = jsonObject.getString("flag");
                            String msg = jsonObject.getString("msg");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();
                        melodyList.clear();
                        instrumentList.clear();
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(FILE_TYPE, "admin_melody");
                params.put(KEY_SEARCH, strSearch);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilterArtist() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        AppHelper.sop("response=="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        melodyList.clear();
                        instrumentList.clear();
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
<<<<<<< HEAD
//                params.put(ID, userId);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, strArtist);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+MELODY);
=======
                if (userId != null) {
                    params.put(ID, userId);
                    params.put(GENRE, genreString);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(FILTER_TYPE, strName);
                    params.put(USER_NAME, strArtist);
                    params.put(FILTER, "extrafilter");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    AppHelper.sop("params==" + params + "\nURL==" + MELODY);
                }else{
                    params.put(GENRE, genreString);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(FILTER_TYPE, strName);
                    params.put(USER_NAME, strArtist);
                    params.put(FILTER, "extrafilter");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    AppHelper.sop("params==" + params + "\nURL==" + MELODY);
                }
>>>>>>> bebffebeebc9e6e32449d2c4cc24bac86a3ae843
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilterInstruments() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=="+response);
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        melodyList.clear();
                        instrumentList.clear();
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put(ID, userId);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, strInstruments);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilterBPM() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        AppHelper.sop("response=="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        melodyList.clear();
                        instrumentList.clear();
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put(ID, userId);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, strBPM);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private TabHost.TabContentFactory createTabContent() {
        return new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                rv = new RecyclerView(getActivity());
                rv.setHasFixedSize(true);
                linearLayoutManager = new LinearLayoutManager(getActivity());
//                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
//                rv.setLayoutManager(lm);
                rv.setLayoutManager(linearLayoutManager);
                rv.setItemAnimator(new DefaultItemAnimator());
//                rv.addOnScrollListener(recyclerViewOnScrollListener);
                rv.setAdapter(adapter);
                linearLayoutManager = (LinearLayoutManager) rv.getLayoutManager();
                return rv;
            }
        };
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            post = linearLayoutManager.findLastVisibleItemPosition();
            //Toast.makeText(getActivity(), "111111   >>>" + String.valueOf(post), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getActivity(), String.valueOf(recordingList.size()), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getActivity(), "post "+String.valueOf(post+1), Toast.LENGTH_SHORT).show();
            //if (post + 1 == melodyList.size()) {

                //new FetchActivityDetails().execute(String.valueOf(melodyList.size() + 10), String.valueOf(instrumentList.size() + 10));

                //adapter.notifyDataSetChanged();
                //linearLayoutManager.scrollToPosition(post+1);

                //rv.setAdapter(adapter);
                //adapter.notifyItemInserted(recordingList.size()-1);
                //Toast.makeText(getActivity(), "list "+String.valueOf(recordingList.size()), Toast.LENGTH_SHORT).show();
            //}


        }
    };

    private class FetchActivityDetails extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            running = true;

            progressDialog = ProgressDialog.show(getActivity(), "Processing...", "Please Wait...");
            progressDialog.setCancelable(false);

        }

        boolean running;
        ProgressDialog progressDialog;

        protected String doInBackground(String... params) {

            try {

                final int Pos = Integer.parseInt(params[0]);
                fetchMelodyPacksMore(Pos);
                /*int i = 3;
                while(running){*/
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                    /*if(i-- == 0){
                        running = false;
                    }*/

                //}
                return null;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //progressDialog.setMessage(String.valueOf(values[0]));
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            //Toast.makeText(getActivity(), "1111", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            //Toast.makeText(getActivity(), "2222", Toast.LENGTH_SHORT).show();


        }
    }


    public void fetchMelodyPacksMore(final int position) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=="+response);
                        melodyList.clear();
                        instrumentList.clear();
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("unsuccess")) {
                                String str = jsonObject.getString(KEY_MSG);
                                if (str.equals("No pack found")) {
                                    str = "Sorry, no " + packName + " melody available.";
                                    Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
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
                        try {
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                        } catch (Throwable throwable) {
                            Log.d("Fetch Melody Packs Error", throwable.toString());
                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (packId.equals("7") && userId != null) {
                    params.put(USER_ID, userId);
                    params.put(FILE_TYPE, "user_melody");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else if (userId != null) {
                    params.put(users_id, userId);
                    params.put(GENRE, packId);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else {
                    params.put(GENRE, packId);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                }
                try {
                    SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    String userId = loginSharedPref.getString("userId", null);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
//                if (userId != null) {
////                    params.put(USER_ID, userId);
//                }
                AppHelper.sop("params=="+params+"\nURL=="+MELODY);
                return params;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    private class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            try {

                adapter = new MelodyCardListAdapter(melodyList, getActivity());
                if (rv!=null){
                    rv.setAdapter(adapter);
                }

                AppHelper.sop("LongOperation==onPreExecute");
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Processing...");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        protected String doInBackground(String... params) {
            if (strName == null && strSearch == null) {
                fetchMelodyPacks();
            } else if (strSearch != null) {
                fetchMelodySearchData();
            } else if (strArtist != null) {
                fetchMelodyFilterArtist();
            } else if (strInstruments != null && strName.equals("# of Instruments")) {
                fetchMelodyFilterInstruments();
            } else if (strBPM != null && strName.equals("BPM")) {
                fetchMelodyFilterBPM();
            } else {
                fetchMelodyFilter();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            progressDialog.dismiss();
            AppHelper.sop("onPostExecute=melodyList=="+melodyList);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("onActivityResult==called="+"requestCode=="+requestCode+"=resultCode="+resultCode+"=data="+data);
        if(MelodyCardListAdapter.REQUEST_MELODY_COMMENT==requestCode){
            if (resultCode==mActivity.RESULT_OK){
                new LongOperation().execute();
                AppHelper.sop("onActivityResult==called="+"resultCode=="+resultCode);
            }

        }
    }
}