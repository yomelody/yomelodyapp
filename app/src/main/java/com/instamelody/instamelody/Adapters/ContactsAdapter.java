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
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.BASE_URL;
import static com.instamelody.instamelody.utils.Const.ServiceType.CREATE_GROUP;
import static com.instamelody.instamelody.utils.Const.ServiceType.USER_CHAT_ID;

/**
 * Created by Shubhansh Jaiswal on 04/05/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Contacts> contactsList = new ArrayList<>();
    String rsList[];
    String senderID = "";
    String recieverId = "";
    public static String recId = "";
    String recieverName = "";
    String recieverImage = "";
    public static int Count = 0;

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

            getItemCount();
            rsList = new String[contactsList.size()];
            userProfileImage = (ImageView) itemView.findViewById(R.id.userProfileImage);
            tvRealName = (TextView) itemView.findViewById(R.id.tvRealName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            grey_circle = (ImageView) itemView.findViewById(R.id.grey_circle);
            blue_circle = (ImageView) itemView.findViewById(R.id.blue_circle);
            rlComplete = (RelativeLayout) itemView.findViewById(R.id.rlComplete);

            rlComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
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
                        senderID = userId;

                        if (grey_circle.getVisibility() == View.VISIBLE) {
                            grey_circle.setVisibility(View.GONE);
                            blue_circle.setVisibility(View.VISIBLE);
                            Count = Count + 1;
                            recieverId = contactsList.get(getAdapterPosition()).getUser_id();
                            rsList[getAdapterPosition()] = recieverId;
                            recId = "";

                            for (int i = 0; i < rsList.length; i++) {
                                if (rsList[i] != null) {
                                    recId = recId + "," + rsList[i];
                                }
                            }

                            if (recId.contains(",null")) {
                                String REGEX = ",null";
                                Pattern p = Pattern.compile(REGEX);
                                Matcher m = p.matcher(recId);
                                recId = m.replaceAll("");
                            }

                            if (recId.startsWith(",")) {
                                recId = recId.substring(1, recId.length());
                            }

                            String fname = contactsList.get(getAdapterPosition()).getfName();
                            String lname = contactsList.get(getAdapterPosition()).getlName();
                            recieverName = fname + " " + lname;
                            recieverImage = contactsList.get(getAdapterPosition()).getUserProfileImage();

                            if (Count > 0) {
                                ContactsActivity.btnCancel.setVisibility(View.GONE);
                                ContactsActivity.btnOK.setVisibility(View.VISIBLE);
                                SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                                if (Count > 1) {
//                                recieverName = "New Group";
//                                editor.putString("receiverName", recieverName);
                                    editor.putString("chatType", "group");
                                } else {
                                    editor.putString("chatType", "single");
                                }
                                editor.commit();
                            }

                        } else {
                            blue_circle.setVisibility(View.GONE);
                            grey_circle.setVisibility(View.VISIBLE);
                            Count = Count - 1;

                            rsList[getAdapterPosition()] = "null";

                            recId = "";
                            for (int i = 0; i < rsList.length; i++) {
                                if (rsList[i] != "null") {
                                    recId = recId + "," + rsList[i];
                                }
                            }

                            if (recId.contains(",null")) {
                                String REGEX = ",null";
                                Pattern p = Pattern.compile(REGEX);
                                Matcher m = p.matcher(recId);
                                recId = m.replaceAll("");
                            }

                            if (recId.startsWith(",")) {
                                recId = recId.substring(1, recId.length());
                            }

                            if (Count < 1) {
                                ContactsActivity.btnOK.setVisibility(View.GONE);
                                ContactsActivity.btnCancel.setVisibility(View.VISIBLE);
                            }

                            SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                            editor.putString("chatId", "");
                            editor.commit();
                        }

//                    Toast.makeText(context, recId, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                        editor.putString("senderId", senderID);
                        editor.putString("receiverId", recId);
//                    editor.putString("receiverName", recieverName);
                        editor.putString("receiverImage", recieverImage);
                        editor.commit();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });

            userProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sendrID = contactsList.get(getAdapterPosition()).getUser_id();
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("showProfileUserId", sendrID);
                    view.getContext().startActivity(intent);
                }
            });
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
        try {
            Contacts contacts = contactsList.get(listPosition);
            Picasso.with(holder.userProfileImage.getContext()).load(contacts.getUserProfileImage()).into(holder.userProfileImage);
            holder.tvRealName.setText(contacts.getfName() + " " + contacts.getlName());
            String s = "@" + contacts.getUserName();
            holder.tvUserName.setText(s);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

}
