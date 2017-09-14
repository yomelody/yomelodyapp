package com.instamelody.instamelody.Adapters;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.instamelody.instamelody.CommentsActivity;
import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.JoinCommentActivity;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.JoinedUserProfile;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.ProfileActivity;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.StudioActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.JOINED_USERS;
import static com.instamelody.instamelody.utils.Const.ServiceType.LIKESAPI;
import static com.instamelody.instamelody.utils.Const.ServiceType.PLAY_COUNT;

/**
 * Created by Macmini on 22/08/17.
 */

public class JoinListAdapter extends RecyclerView.Adapter<JoinListAdapter.MyViewHolder> {
    Context context;
    String USER_TYPE = "user_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String LIKES = "likes";
    String TYPE = "type";
    String USERID = "userid";
    String FILEID = "fileid";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    String Topic = "topic";
    ProgressDialog progressDialog;
    MediaPlayer mp;
    String userId = "";
    RelativeLayout rlLike;
    public static int click_pos = 0;
    int posForStudio = 0;
    int counter = 0;
    String tempUserID;
    private ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();

    public JoinListAdapter(ArrayList<JoinedArtists> Joined_artist, Context context) {
        this.Joined_artist = Joined_artist;
        this.context = context;
        setHasStableIds(true);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView join_image;
        TextView Join_usr_name;
        ImageView redCross;

        public MyViewHolder(View itemView) {
            super(itemView);
            join_image = (ImageView) itemView.findViewById(R.id.ivImageName);
            Join_usr_name = (TextView) itemView.findViewById(R.id.tvUserName);
            redCross = (ImageView) itemView.findViewById(R.id.redCross);
            SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
            SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
            SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

            if (loginSharedPref.getString("userId", null) != null) {
                userId = loginSharedPref.getString("userId", null);
            } else if (fbPref.getString("userId", null) != null) {
                userId = fbPref.getString("userId", null);
            } else if (twitterPref.getString("userId", null) != null) {
                userId = twitterPref.getString("userId", null);
            }
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_join_image, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final JoinedArtists joinArt = Joined_artist.get(position);
        holder.Join_usr_name.setText(joinArt.getJoined_usr_name());
        Picasso.with(holder.join_image.getContext()).load(joinArt.getJoined_image()).into(holder.join_image);
        try {
            JoinActivity.play_count.setText(joinArt.getPlay_counts());
            JoinActivity.tvLikeCount.setText(joinArt.getLike_counts());
            JoinActivity.tvCommentCount.setText(joinArt.getComment_counts());
            JoinActivity.tvShareCount.setText(joinArt.getShare_counts());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //  tempUserID = joinArt.getUser_id();
        getJoined_users(JoinActivity.addedBy, JoinActivity.RecId, String.valueOf(click_pos));
        if (position == 0) {
            holder.redCross.setVisibility(VISIBLE);
        }
        holder.join_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   holder.redCross.setVisibility(VISIBLE);
                String user_id = JoinActivity.listProfile.get(position).getUserId();
                String status = JoinActivity.listProfile.get(position).getStatus();
                if (user_id.equals(joinArt.getUser_id()) && status.equals("0")) {
                    posForStudio = position;
                    getJoined_users(JoinActivity.addedBy, JoinActivity.RecId, String.valueOf(position));
                    JoinActivity.listProfile.set(position, new JoinedUserProfile(user_id, "1"));
                } else {
                    String showProfileUserId = JoinActivity.addedBy;
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("showProfileUserId", user_id);
                    v.getContext().startActivity(intent);
                    JoinActivity.listProfile.set(position, new JoinedUserProfile(user_id, "0"));
                }

            }
        });
        JoinActivity.rlJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    String position = Integer.toString();
                Intent intent = new Intent(v.getContext(), StudioActivity.class);
                intent.putExtra("clickPositionJoin", String.valueOf(posForStudio));
                v.getContext().startActivity(intent);
                StudioActivity.list.clear();
            }
        });

        JoinActivity.ivJoinPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinActivity.ivJoinPlay.setVisibility(v.GONE);
                JoinActivity.ivJoinPause.setVisibility(v.VISIBLE);
                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                String play = JoinActivity.play_count.getText().toString().trim();
                int playValue = Integer.parseInt(play) + 1;
                String recording_id = joinArt.getRecording_id().trim();

                if (!userId.equals("") && userId != null) {
                    JoinActivity.play_count.setText(String.valueOf(playValue));
                    fetchViewCount(userId, recording_id);
                }

                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mp.setDataSource(joinArt.getRecording_url());
                    mp.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressDialog.dismiss();
                        mp.start();
                    }
                });
                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        progressDialog.dismiss();
                        return false;
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        progressDialog.dismiss();
                    }
                });

            }
        });
        JoinActivity.ivJoinPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinActivity.ivJoinPlay.setVisibility(v.VISIBLE);
                JoinActivity.ivJoinPause.setVisibility(v.GONE);
                mp.pause();
            }
        });

        JoinActivity.rlLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userId.equals("") && userId != null) {
                    //Toast.makeText(context, "like", Toast.LENGTH_SHORT).show();
                    //position = mpids.get(getAdapterPosition() + 1);


                    String RecordingName = joinArt.getRecording_name();
                    String position = joinArt.getRecording_id();

                    if (JoinActivity.ivDislikeButton.getVisibility() == VISIBLE) {
                        JoinActivity.ivLikeButton.setVisibility(VISIBLE);
                        JoinActivity.ivDislikeButton.setVisibility(GONE);
                        String like = JoinActivity.tvLikeCount.getText().toString().trim();
                        int likeValue = Integer.parseInt(like) - 1;
                        like = String.valueOf(likeValue);
                        JoinActivity.tvLikeCount.setText(like);
                        fetchLikeState(userId, position, "0", RecordingName);


                    } else if (JoinActivity.ivDislikeButton.getVisibility() == GONE) {
                        JoinActivity.ivLikeButton.setVisibility(GONE);
                        JoinActivity.ivDislikeButton.setVisibility(VISIBLE);
                        String like = JoinActivity.tvLikeCount.getText().toString().trim();
                        int likeValue = Integer.parseInt(like) + 1;
                        like = String.valueOf(likeValue);
                        JoinActivity.tvLikeCount.setText(like);
                        fetchLikeState(userId, position, "1", RecordingName);
                    }
                } else {
                    Toast.makeText(context, "Log in to like this Recording", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, SignInActivity.class);
                    context.startActivity(intent);
                }

            }
        });

        JoinActivity.rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent intent = new Intent(context, JoinCommentActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Position", String.valueOf(position));
                    context.startActivity(intent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return Joined_artist.size();
    }

    public void fetchViewCount(final String userId, final String pos) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PLAY_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //       Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject, respObject;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                respObject = jsonObject.getJSONObject(KEY_RESPONSE);
                                String str = respObject.getString("play_count");
                                //      Toast.makeText(context, "" + str, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //       Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(USER_TYPE, "user");
                params.put(USERID, userId);
                params.put(FILEID, pos);
                //    params.put(TYPE, "admin_melody");
                params.put(TYPE, "recording");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(context);
        requestQueue1.add(stringRequest);
    }

    public void fetchLikeState(final String userId, final String pos, final String likeState, final String LikeRecordingName) {

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LIKESAPI,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                            Log.d("Like status response----", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                            String errorMsg = error.toString();
                            Log.d("Error", errorMsg);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Topic, LikeRecordingName);
                    params.put(USER_ID, userId);
                    params.put(FILE_ID, pos);
                    params.put(TYPE, "user_recording");
                    params.put(LIKES, likeState);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    return params;
                }
            };
            RequestQueue requestQueue1 = Volley.newRequestQueue(context);
            requestQueue1.add(stringRequest);
        } catch (Exception ex) {

        }

    }

    public void getJoined_users(final String addedBy, final String RecId, final String position) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, JOINED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);
                        JoinActivity.instrumentList.clear();
//                        if (click_pos == 0) {
//                            new ParseContents(getApplicationContext()).parseJoinInstrument(response, JoinActivity.instrumentList, String.valueOf(click_pos));
//                            JoinActivity.adapter1 = new JoinInstrumentListAdp(JoinActivity.instrumentList, getApplicationContext());
//                            JoinActivity.recyclerViewInstruments.setAdapter(JoinActivity.adapter1);
//                        } else {
                        new ParseContents(getApplicationContext()).parseJoinInstrument(response, JoinActivity.instrumentList, position);
                        JoinActivity.adapter1 = new JoinInstrumentListAdp(JoinActivity.instrumentList, getApplicationContext());
                        JoinActivity.recyclerViewInstruments.setAdapter(JoinActivity.adapter1);
                        //   }

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
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", addedBy);
                params.put("rid", RecId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
