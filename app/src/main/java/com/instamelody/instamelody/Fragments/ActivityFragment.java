package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
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
import com.instamelody.instamelody.Adapters.ActivityCardAdapter;
import com.instamelody.instamelody.Models.ActivityModel;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;

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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.utils.Const.ServiceType.ACTIVITY;
import static com.instamelody.instamelody.utils.Const.ServiceType.RECORDINGS;
import static com.instamelody.instamelody.utils.RMethod.getServerDiffrenceDate;

/**
 * Created by Saurabh Singh on 12/16/2016.
 */

public class ActivityFragment extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lmactivity;
    private RecyclerView recyclerView;
    String USER_ID = "user_id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "responce";
    String id = "id";
    ProgressDialog progressDialog;
    private static ArrayList<ActivityModel> arraylist;

    public ActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewActivity);
        recyclerView.setHasFixedSize(true);
        lmactivity = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lmactivity);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        arraylist = new ArrayList<ActivityModel>();
        String position, userId;
        SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userId = loginSharedPref.getString("userId", null);
        if(userId!=null) {
            //fetchActivityData(userId);
            new FetchActivityDetails().execute(userId);
        }
//        else {
//            Toast.makeText(getApplicationContext(), "Log in to Chat", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getApplicationContext().startActivity(intent);
//        }
        return view;
    }


    private class FetchActivityDetails extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params) {

            try {
                String UserID = params[0];
                fetchActivityData(UserID);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            progressDialog.dismiss();
        }
    }
    public void fetchActivityData(final String userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ACTIVITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject;
                        JSONArray jsonArray;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                ArrayList<ActivityModel> list = new ArrayList<ActivityModel>();
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                   /* arraylist.add(new ActivityModel(
                                            ActivityData.id_[i],
                                            ActivityData.userProfileImage[i],
                                            ActivityData.UserNameArray1[i],
                                            ActivityData.Topic[i],
                                            ActivityData.Time[i]


                                    ));
*/
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    arraylist.add(new ActivityModel(
                                            Integer.parseInt(c.getString("id")),
//                                             Integer.parseInt(c.getString("id")),
                                            c.getString("activity_name"),
                                            c.getString("topic"),
                                            DateTime(c.getString("activity_created_time")),
                                            c.getString("profile_pick")
                                    ));


                                    adapter = new ActivityCardAdapter(arraylist, getActivity());


                                }
                                adapter = new ActivityCardAdapter(arraylist);

                                recyclerView.setAdapter(adapter);
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
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, userId);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
        requestQueue1.add(stringRequest);
    }
    public String DateTime(String send_at) {
        Date dtdate=getServerDiffrenceDate(send_at);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String val = "";
        try {
            Date oldDate = dateFormat.parse(send_at);
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();

            long diff = currentDate.getTime() - dtdate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long years=(days/365);
            if (days == 0) {
                if (hours == 0) {
                    if (minutes == 0) {
                        if (seconds <= 5) {
                            val = "Just now";
                        } else {
                            val = String.valueOf(seconds) + " " + "secs"+" ago";
                        }
                    } else if (minutes == 1) {
                        val = String.valueOf(minutes) + " " + "min" +" ago";
                    } else {
                        val = String.valueOf(minutes) + " " + "mins"+" ago";
                    }
                } else if (hours == 1) {
                    val = String.valueOf(hours) + " " + "hour"+" ago";
                } else {
                    val = String.valueOf(hours) + " " + "hrs"+" ago";
                }
            } else if (days == 1) {
                val = "1 day";
            }
            else {
                long year=(days/365);
                if(year>0) {
                    val = String.valueOf(days / 365) + "year" + " ago";
                }
                else {
                    val = days + "day" + " ago";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return val;
    }
    private static void log(String[] availableIDs) {

        System.out.println("\nHere are all list of timezones for your reference:");
        for (String temp : availableIDs) {
            System.out.println(temp);
        }
    }
    public static Date getDiffrenceBetween(String Date,String ZoneName) {
        String clientDnT = Date ;// "2017-06-01 07:20:00";
        Date date2=null;
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try{
            dff.setTimeZone(TimeZone.getTimeZone("UTC"));
            date2 = dff.parse(clientDnT);
            dff.setTimeZone(TimeZone.getDefault());
            String formattedDate = dff.format(date2);
        }
        catch(Exception e){
            System.err.println(e);
        }

        return date2;

    }
}
