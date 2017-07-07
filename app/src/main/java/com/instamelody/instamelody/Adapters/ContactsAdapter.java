package com.instamelody.instamelody.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.instamelody.instamelody.ContactsActivity;
import com.instamelody.instamelody.Models.Contacts;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shubhansh Jaiswal on 04/05/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Contacts> contactsList = new ArrayList<>();
    ArrayList<String> rList = new ArrayList<String>();
    //    Set<String> recieverId = new HashSet<>();
    String USER_CHAT_ID = "http://52.41.33.64/api/user_chat_id.php";
    String recieverId = "";
    String recieverName = "";
    String receiverToken = "";
    String recieverImage = "";

    int Count = 0;

    public ContactsAdapter(Context context, ArrayList<Contacts> contactsList) {
        this.contactsList = contactsList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRealName, tvUserName;
        ImageView userProfileImage, grey_circle, blue_circle;
        RelativeLayout rlComplete;

        public MyViewHolder(final View itemView) {
            super(itemView);

            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            tvRealName = (TextView) itemView.findViewById(R.id.tvRealName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            grey_circle = (ImageView) itemView.findViewById(R.id.grey_circle);
            blue_circle = (ImageView) itemView.findViewById(R.id.blue_circle);
            rlComplete = (RelativeLayout) itemView.findViewById(R.id.rlComplete);

            rlComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String userId = "";
                    SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    SharedPreferences fbPref = context.getSharedPreferences("MyFbPref", MODE_PRIVATE);
                    SharedPreferences twitterPref = context.getSharedPreferences("TwitterPref", MODE_PRIVATE);

                    if (loginSharedPref.getString("userId", null) != null) {
                        userId = loginSharedPref.getString("userId", null);
                    } else if (fbPref.getString("userId", null) != null) {
                        userId = fbPref.getString("userId", null);
                    } else if (twitterPref.getString("userId", null) != null) {
                        userId = twitterPref.getString("userId", null);
                    }

                    if (grey_circle.getVisibility() == View.VISIBLE) {
                        grey_circle.setVisibility(View.GONE);
                        blue_circle.setVisibility(View.VISIBLE);
                        Count = Count + 1;
                        recieverId = contactsList.get(getAdapterPosition()).getUser_id();

//                        rList.add(Count - 1, recieverId);

                        /*List<String> groupNameList = Arrays.asList(rid.split(","));
                        String listnames = "";
                        if (groupNameList.size() == 1) {
                            tvUserName.setText(groupNameList.get(0));
                        } else {
                            for (int i = 1; i < groupNameList.size(); i++) {
                                listnames = listnames + ", " + groupNameList.get(i);
                            }
                            tvUserName.setText(listnames);
                        }*/

                        String fname = contactsList.get(getAdapterPosition()).getfName();
                        String lname = contactsList.get(getAdapterPosition()).getlName();
                        recieverName = fname + " " + lname;
                        recieverImage = contactsList.get(getAdapterPosition()).getUserProfileImage();
                        receiverToken = contactsList.get(getAdapterPosition()).getDeviceToken();
                        if (Count >= 1) {
                            ContactsActivity.btnCancel.setVisibility(View.GONE);
                            ContactsActivity.btnOK.setVisibility(View.VISIBLE);
                        }
                        if (!userId.equals("")) {
                            getChatId(userId, recieverId);
                        } else {
                            Toast.makeText(context, "Logged in user null id Error", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        blue_circle.setVisibility(View.GONE);
                        grey_circle.setVisibility(View.VISIBLE);
                        Count = Count - 1;
                        recieverId = receiverToken = "";
                        if (Count < 1) {
                            ContactsActivity.btnOK.setVisibility(View.GONE);
                            ContactsActivity.btnCancel.setVisibility(View.VISIBLE);
                        }

                        SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                        editor.putString("chatId", "");
                        editor.commit();
                    }

                    SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                    editor.putString("receiverId", recieverId);
                    editor.putString("receiverName", recieverName);
                    editor.putString("receiverImage", recieverImage);
                    editor.commit();
                }
            });

//            if (rList == null) {
//                Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, rList.toString(), Toast.LENGTH_SHORT).show();
//            }

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_contacts, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {

        Contacts contacts = contactsList.get(listPosition);
        Picasso.with(holder.userProfileImage.getContext()).load(contacts.getUserProfileImage()).into(holder.userProfileImage);
        holder.tvRealName.setText(contacts.getfName() + " " + contacts.getlName());
        String s = "@" + contacts.getUserName();
        holder.tvUserName.setText(s);
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    private void getChatId(final String user_id, final String reciever_id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_CHAT_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(context, " Shubz" + response, Toast.LENGTH_SHORT).show();

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            String chat_Id = jsonObject.getString("chatID");

                            if (chat_Id.equals("0")) {
                                chat_Id = "";
                            }

                            SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                            editor.putString("chatId", chat_Id);
                            editor.commit();

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
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("senderID", user_id);
                params.put("receiverID", reciever_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
