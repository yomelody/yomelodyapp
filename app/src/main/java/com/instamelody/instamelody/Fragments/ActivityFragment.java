package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import static com.instamelody.instamelody.utils.Const.ServiceType.ACTIVITY;

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
        if(userId!=null)
        fetchActivityData(userId);

        return view;
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

       /* //String dtStart = "2010-10-15T09:27:37Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = format.parse(send_at);
            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        String localTimes = date.format(currentLocalTime);

        long TimeDiff=getDiffrenceBetween(send_at,localTimes);*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String val = "";
        try {
            Date oldDate = dateFormat.parse(send_at);
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();

            long diff = currentDate.getTime() - oldDate.getTime();
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

        /*String val = "";
        Calendar localTime = Calendar.getInstance();

        int hour = localTime.get(Calendar.HOUR);
        int minute = localTime.get(Calendar.MINUTE);
        int second = localTime.get(Calendar.SECOND);
        int year = localTime.get(Calendar.YEAR);


        Calendar cal = Calendar.getInstance();
        java.util.TimeZone tz = cal.getTimeZone();
        Log.d("Time zone","="+tz.getDisplayName());



        Calendar c = new GregorianCalendar(TimeZone.getTimeZone("PST"));
        c.setTimeInMillis(new Date().getTime());
        int EastCoastHourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int EastCoastDayOfMonth = c.get(Calendar.DAY_OF_MONTH);

// Alaska
        c = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kolkata"));
        c.setTimeInMillis(new Date().getTime());
        int AlaskaHourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int AlaskaDayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        // Print the local time
        System.out.printf("Local time: %02d:%02d:%02d %02d\n", hour, minute, second, year);

        // Create a calendar object for representing a Singapore time zone.
      *//*  Calendar indiaTime = new GregorianCalendar(TimeZone.getTimeZone("Asia/Singapore"));
        indiaTime.setTimeInMillis(localTime.getTimeInMillis());
        hour = indiaTime.get(Calendar.HOUR);
        minute = indiaTime.get(Calendar.MINUTE);
        second = indiaTime.get(Calendar.SECOND);
        year = indiaTime.get(Calendar.YEAR);*//*

        // Print the local time in Germany time zone
        System.out.printf("India time: %02d:%02d:%02d %02d\n", hour, minute, second, year);

        // Here are all list of timezones for your reference
        //log(TimeZone.getAvailableIDs());*/

        return val;
    }
    private static void log(String[] availableIDs) {

        System.out.println("\nHere are all list of timezones for your reference:");
        for (String temp : availableIDs) {
            System.out.println(temp);
        }
    }
    public static long getDiffrenceBetween(String c_date,String old_date) {
        System.out.println("Cuttent Time   :::"+c_date);
        System.out.println("Saved/Old Time :::"+old_date);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date d1 = null;
        Date d2 = null;
        long min_In_Minutes=0;

        try {
            d1 = format.parse(c_date);
            d2 = format.parse(old_date);

            // in milliseconds
            long diff = d1.getTime() - d2.getTime();
            min_In_Minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

            System.out.println("Diff in Minutes :::"+min_In_Minutes);

            long diffSeconds = diff / (1000 % 60);
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.println(diffDays + " days, ");
            System.out.println(diffHours + " hours, ");
            System.out.println(diffMinutes + " minutes, ");
            System.out.println(diffSeconds + " seconds.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return min_In_Minutes;
    }
}
