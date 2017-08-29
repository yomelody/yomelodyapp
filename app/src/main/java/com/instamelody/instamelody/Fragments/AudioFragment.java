package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

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
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Models.AudioModel;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;

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
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.RECORDINGS;

/**
 * Created by Saurabh Singh on 4/18/2017.
 */

public class AudioFragment extends Fragment {

    ArrayList<AudioModel> audioList = new ArrayList<>();
    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    private String ID = "id";
    private String KEY = "key";
    private String STATION = "station";
    private String GENRE = "genere";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";
    private String KEY_SEARCH = "search";
    private String USER_NAME = "username";
    private String COUNT = "count";


    String recordingId, addedBy, recordingTopic, userName, dateAdded, likeCount, playCount, commentCount, shareCount, profileUrl, coverUrl, genre, recordings;

    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String genreString = "1";

    RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<RecordingsModel> data;
    ProgressDialog pDialog;
    String userId;
    String userIdNormal, userIdFb, userIdTwitter;
    int statusNormal, statusFb, statusTwitter;
    ProgressDialog progressDialog;
    // LongOperation myTask = null;
    String strName, strSearch, strArtist, strInstruments, strBPM;
    TabHost host = null;

    public AudioFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }
        new LongOperation().execute();
    }

    public void fetchGenreNames() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        String titleString;
                        TabHost.TabSpec spec;
                        try {
                            host = (TabHost) getActivity().findViewById(R.id.tabHostAudio);
                            host.setup();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                //   myTask = new LongOperation();
                                //   myTask.execute();
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
                                    try {
                                        SharedPreferences.Editor editorGenre = getActivity().getSharedPreferences("prefGenreName", MODE_PRIVATE).edit();
                                        editorGenre.putString("GenreName", titleString);
                                        editorGenre.apply();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                ClearSharedPref();
                            }
                        } catch (JSONException e) {
                            ClearSharedPref();
                            e.printStackTrace();
                        }
                        try {
                            if (host.getCurrentTab() == 0) {
//                            Toast.makeText(getActivity(), "All " + host.getCurrentTab(), Toast.LENGTH_SHORT).show();
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
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        try {
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
//                                fetchRecordings();
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
//                                Toast.makeText(getActivity(), "beta: " + genreString, Toast.LENGTH_SHORT).show();
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
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void fetchRecordings() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);

                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        try{
                            adapter.notifyDataSetChanged();
                            ClearSharedPref();
                        }catch (NullPointerException e){
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
               /* params.put(KEY, STATION);
                params.put(GENRE, genreString);*/
                if (userId != null) {
                    params.put(ID, userId);
                    params.put(KEY, STATION);
                    params.put(GENRE, genreString);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                } else {
                    params.put(KEY, STATION);
                    params.put(GENRE, genreString);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                }
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void fetchRecordingsFilter() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData1", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        ClearSharedPref();
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchSearchData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successMsg = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(successMsg);
                            String flag = jsonObject.getString("flag");
                            String msg = jsonObject.getString("msg");
                            if (flag.equals("unsuccess")) {
                                ClearSharedPref();
                                //Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                            }
                            ClearSharedPref();

                        } catch (JSONException e) {
                            ClearSharedPref();
                            e.printStackTrace();
                        }
//                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        Log.d("ReturnDataS", response);
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(KEY_SEARCH, strSearch);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterArtist() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
                            ClearSharedPref();
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            ClearSharedPref();
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData2", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, strArtist);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterInstruments() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
                            ClearSharedPref();
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            ClearSharedPref();
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData1", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, strInstruments);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterBPM() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
                            ClearSharedPref();
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            ClearSharedPref();
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData1", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, strBPM);
                params.put(FILTER, "extrafilter");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().findViewById(R.id.activity_station);
    }

    private TabHost.TabContentFactory createTabContent() {
        return new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                RecyclerView rv = new RecyclerView(getActivity());
                rv.setHasFixedSize(true);
                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(lm);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(adapter);
                return rv;
            }
        };
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            fetchGenreNames();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        protected String doInBackground(String... params) {

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
            adapter = new RecordingsCardAdapter(getActivity(), recordingList, recordingsPools);
            return null;
        }

        protected void onPostExecute(String result) {

            progressDialog.dismiss();
        }

    }

    void ClearSharedPref() {
        try {

            SharedPreferences.Editor FilterPref = getActivity().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
            FilterPref.clear();
            FilterPref.apply();
            SharedPreferences.Editor SearchPref = getActivity().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
            SearchPref.clear();
            SearchPref.apply();
            SharedPreferences.Editor FilterPrefArtist = getActivity().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
            FilterPrefArtist.clear();
            FilterPrefArtist.apply();
            SharedPreferences.Editor FilterPrefInstruments = getActivity().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
            FilterPrefInstruments.clear();
            FilterPrefInstruments.apply();
            SharedPreferences.Editor FilterPrefBPM = getActivity().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
            FilterPrefBPM.clear();
            FilterPrefBPM.apply();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}