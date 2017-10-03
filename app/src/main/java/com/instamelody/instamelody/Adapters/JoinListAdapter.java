package com.instamelody.instamelody.Adapters;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.instamelody.instamelody.CommentsActivity;
import com.instamelody.instamelody.Fragments.CommentJoinFragment;
import com.instamelody.instamelody.JoinActivity;
import com.instamelody.instamelody.JoinCommentActivity;
import com.instamelody.instamelody.MessengerActivity;
import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.Models.JoinedUserProfile;
import com.instamelody.instamelody.Models.MelodyInstruments;
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
import static com.instamelody.instamelody.utils.Const.ServiceType.JOIN_DELETE;
import static com.instamelody.instamelody.utils.Const.ServiceType.LIKESAPI;
import static com.instamelody.instamelody.utils.Const.ServiceType.PLAY_COUNT;
import static com.instamelody.instamelody.utils.Const.ServiceType.RECORDINGS;

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
    String RECORDING_ID = "rid";
    String STATUS = "status";
    ProgressDialog progressDialog;
    public static MediaPlayer mp;
    String userId = "";
    RelativeLayout rlLike;
    public static int click_pos = 0;
    int posForStudio = 0;
    private RecyclerView.ViewHolder lastModifiedHoled;
    int counter = 0;
    String tempUserID;
    public static ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();
    final int SAMPLING_RATE = 44100;
    private int mBufferSize;
    private short[] mAudioBuffer;
    public static RecordingThread mRecordingThread;
    public static boolean mShouldContinue = true;
    public static View rootview;
    int lastPosition;
    int realPosition = 0;
    ArrayList<ViewHolder> lstViewHolder = new ArrayList<ViewHolder>();
    ViewHolder viewHolder;
    ImageView redCross;
    boolean checkSt = true;
    int count = 0;
    boolean playSt = false;
    public static int click = 0;


    public JoinListAdapter(ArrayList<JoinedArtists> Joined_artist, Context context) {
        this.Joined_artist = Joined_artist;
        this.context = context;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView join_image;
        TextView Join_usr_name;
        ImageView redCross;
        RelativeLayout joined_profile;

        public MyViewHolder(View itemView) {
            super(itemView);
            rootview = itemView;
            progressDialog = new ProgressDialog(context);
            join_image = (ImageView) itemView.findViewById(R.id.ivImageName);
            Join_usr_name = (TextView) itemView.findViewById(R.id.tvUserName);
            redCross = (ImageView) itemView.findViewById(R.id.redCross);
            SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
            SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
            SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
            mRecordingThread = new RecordingThread();
            JoinActivity.tvIncluded.setText("Included : " + getItemCount());
            mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            mAudioBuffer = new short[mBufferSize / 2];
            if (loginSharedPref.getString("userId", null) != null) {
                userId = loginSharedPref.getString("userId", null);
            } else if (fbPref.getString("userId", null) != null) {
                userId = fbPref.getString("userId", null);
            } else if (twitterPref.getString("userId", null) != null) {
                userId = twitterPref.getString("userId", null);
            }

            JoinActivity.ivShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!userId.equals("") && userId != null) {

                        JoinedArtists joinArt = Joined_artist.get(getAdapterPosition());
                        String RecordingURL = joinArt.getRecording_url();
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "InstaMelody Music Hunt");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, RecordingURL);
                        shareIntent.setType("image/jpeg");
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(Intent.createChooser(shareIntent, "Choose Sharing option!"));

                    } else {
                        Toast.makeText(context, "Log in to Share this melody pack", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, SignInActivity.class);
                        context.startActivity(intent);
                    }

                }
            });


        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_join_image, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    static class ViewHolder {
        ImageView redCross, join_image;
        TextView Join_usr_name;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final JoinedArtists joinArt = Joined_artist.get(position);
        Log.d("Position", "" + position);
        click = getItemCount() - 1;
        viewHolder = new ViewHolder();
        viewHolder.redCross = (ImageView) holder.redCross.findViewById(R.id.redCross);
        viewHolder.join_image = (ImageView) holder.join_image.findViewById(R.id.ivImageName);
        viewHolder.Join_usr_name = (TextView) holder.Join_usr_name.findViewById(R.id.tvUserName);
        if (lstViewHolder.size() < getItemCount()) {
            lstViewHolder.add(viewHolder);
        }
        JoinedArtists join = Joined_artist.get(0);
        holder.Join_usr_name.setText(joinArt.getJoined_usr_name());
        Picasso.with(holder.join_image.getContext()).load(joinArt.getJoined_image()).into(holder.join_image);
        try {

            JoinActivity.play_count.setText(join.getPlay_counts());
            JoinActivity.tvLikeCount.setText(join.getLike_counts());
            JoinActivity.tvCommentCount.setText(join.getComment_counts());
            JoinActivity.tvShareCount.setText(join.getShare_counts());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //  tempUserID = joinArt.getUser_id();

        SharedPreferences loginSharedPref = context.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = context.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = context.getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }


        if (getItemCount() == 1 && userId.equals(Joined_artist.get(0).getUser_id())) {
            holder.redCross.setVisibility(VISIBLE);
        }

        JoinInstrumentListAdp.count = MelodyInstruments.getInstrumentCount();
        if (position == 0 && checkSt == true) {
            if (userId.equals(Joined_artist.get(0).getUser_id())) {
                lstViewHolder.get(position).redCross.setVisibility(VISIBLE);
            }
            lstViewHolder.get(position).join_image.setBackgroundColor(Color.parseColor("#656565"));
            lstViewHolder.get(position).Join_usr_name.setTextColor(Color.parseColor("#FFFFFF"));
            getJoined_users(JoinActivity.addedBy, JoinActivity.RecId, click_pos);
        }

//        if (userId.equals(joinArt.getUser_id())) {
//            holder.redCross.setVisibility(VISIBLE);
//        } else {
//            holder.redCross.setVisibility(GONE);
//        }

        holder.join_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    if (lstViewHolder.get(0).redCross.getVisibility() == VISIBLE) {
                        lstViewHolder.get(0).redCross.setVisibility(GONE);
                    }
                    lstViewHolder.get(0).join_image.setBackgroundColor(Color.parseColor("#383838"));
                    lstViewHolder.get(0).Join_usr_name.setTextColor(Color.parseColor("#275AAB"));
                }
                if (playSt == true) {
                    lstViewHolder.get(lastPosition).Join_usr_name.setTextColor(Color.parseColor("#275AAB"));
                    lstViewHolder.get(lastPosition).join_image.setBackgroundColor(Color.parseColor("#383838"));
                    lstViewHolder.get(lastPosition).redCross.setVisibility(GONE);
                }
                JoinActivity.txtCount.setText(count + position + 1 + " of " + getItemCount());
                realPosition = position;
                String user_id = JoinActivity.listProfile.get(position).getUserId();
                String status = JoinActivity.listProfile.get(position).getStatus();
                if (getItemCount() == 1) {
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("showProfileUserId", user_id);
                    v.getContext().startActivity(intent);
                    JoinActivity.listProfile.set(position, new JoinedUserProfile(user_id, "0"));
                }
                if (user_id.equals(joinArt.getUser_id()) && status.equals("0")) {

                    posForStudio = position;
                    if (lastModifiedHoled != null) {
                        lastPosition = lastModifiedHoled.getAdapterPosition();
                        if (userId.equals(Joined_artist.get(0).getUser_id())) {
                            lastModifiedHoled.itemView.findViewById(R.id.redCross).setVisibility(GONE);
                            holder.redCross.setVisibility(VISIBLE);
                        }
                        try {
                            lastModifiedHoled.itemView.findViewById(R.id.ivImageName).setBackgroundColor(Color.parseColor("#383838"));
                            lstViewHolder.get(lastPosition).Join_usr_name.setTextColor(Color.parseColor("#275AAB"));
                            lstViewHolder.get(position).Join_usr_name.setTextColor(Color.parseColor("#FFFFFF"));
                            holder.join_image.setBackgroundColor(Color.parseColor("#656565"));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }


                        // notifyItemChanged(lastPosition);
                    } else {
                        holder.join_image.setBackgroundColor(Color.parseColor("#656565"));
                        lstViewHolder.get(position).Join_usr_name.setTextColor(Color.parseColor("#FFFFFF"));
                        if (userId.equals(Joined_artist.get(0).getUser_id())) {
                            holder.redCross.setVisibility(VISIBLE);
                        }


                    }

                    getJoined_users(JoinActivity.addedBy, JoinActivity.RecId, position);
                    JoinInstrumentListAdp.count = MelodyInstruments.getInstrumentCount();
                    JoinActivity.listProfile.set(position, new JoinedUserProfile(user_id, "1"));
                    try {
                        if (position >= 0) {
                            JoinActivity.listProfile.set(lastPosition, new JoinedUserProfile(JoinActivity.listProfile.get(lastPosition).getUserId(), "0"));
                            checkSt = false;
                        }

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

//                    try {
//                        JoinActivity.listProfile.set(lastPosition, new JoinedUserProfile(user_id, "0"));
//                    } catch (IndexOutOfBoundsException e) {
//                        e.printStackTrace();
//                    }


                } else {
                    String showProfileUserId = JoinActivity.addedBy;
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("showProfileUserId", user_id);
                    v.getContext().startActivity(intent);
                    JoinActivity.listProfile.set(position, new JoinedUserProfile(user_id, "0"));
                }

                lastModifiedHoled = holder;

            }


        });


        holder.redCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, JOIN_DELETE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.d("ReturnData", response);
                                    try {
                                        JSONObject json = new JSONObject(response);
                                        if (json.getString("flag").equals("success")) {
                                            Joined_artist.remove(position);
                                            lstViewHolder.remove(position);
                                            getJoined_users(JoinActivity.addedBy, JoinActivity.RecId, click_pos);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, Joined_artist.size());

                                            Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
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
                                    Log.d("Error", errorMsg);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(STATUS, "1");
                            params.put(RECORDING_ID, joinArt.getRecording_id());
                            params.put(AuthenticationKeyName, AuthenticationKeyValue);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);


                } else {
                    Toast.makeText(context, "You can not remove yourself", Toast.LENGTH_SHORT).show();
                }

            }
        });
        JoinActivity.txtCount.setText(count + 1 + " of " + getItemCount());
        JoinActivity.rlJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    String position = Integer.toString();
                if (mp != null) {
                    mp.stop();
                }
                if (mRecordingThread != null) {
                    mRecordingThread.stopRunning();
                    mRecordingThread = null;
                    //    mShouldContinue=true;
                }
                if (JoinInstrumentListAdp.mp_start != null) {
                    for (int i = 0; i <= JoinInstrumentListAdp.mp_start.size() - 1; i++) {
                        JoinInstrumentListAdp.mp_start.get(i).stop();

                    }
                }
                // Toast.makeText(context, ""+posForStudio, Toast.LENGTH_SHORT).show();
                StudioActivity.instrumentList.clear();
                SharedPreferences.Editor editor = context.getSharedPreferences("clickPositionJoin", MODE_PRIVATE).edit();
                editor.putString("instrumentsPos", String.valueOf(posForStudio));
                editor.commit();
                Intent intent = new Intent(v.getContext(), StudioActivity.class);
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
                    if (posForStudio != 0) {
                        JoinedArtists join = Joined_artist.get(posForStudio);
                        mp.setDataSource(join.getRecording_url());
                        mp.prepareAsync();
                    } else {
                        JoinedArtists join = Joined_artist.get(0);
                        mp.setDataSource(join.getRecording_url());
                        mp.prepareAsync();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressDialog.dismiss();
                        mp.start();
                        JoinActivity.chrono.setBase(SystemClock.elapsedRealtime());
                        JoinActivity.chrono.start();
                        try {
                            if (mRecordingThread == null) {
                                mShouldContinue = true;
                                mRecordingThread = new RecordingThread();
                                mRecordingThread.start();
                            } else if (!mRecordingThread.isAlive()) {
                                try {
                                    mShouldContinue = true;
                                    mRecordingThread = new RecordingThread();
                                    mRecordingThread.start();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }

                            } else {
                                mRecordingThread.stopRunning();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }


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
                        JoinActivity.chrono.stop();
                        if (mRecordingThread != null) {
                            mRecordingThread.stopRunning();
                            mRecordingThread = null;
                            //    mShouldContinue=true;
                        }
                        JoinActivity.ivJoinPlay.setVisibility(VISIBLE);
                        JoinActivity.ivJoinPause.setVisibility(GONE);
                        progressDialog.dismiss();
                    }
                });

            }
        });

        JoinActivity.ivPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realPosition++;
                playSt = true;
                if (realPosition > position) {
                    // realPosition = 0;
                    realPosition = realPosition - 1;
                    JoinActivity.ivPlayNext.setEnabled(false);
                    JoinActivity.ivPlayPre.setEnabled(true);

                } else {
                    JoinActivity.ivPlayPre.setEnabled(true);
                    try {
                        if (mRecordingThread != null) {
                            mRecordingThread.stopRunning();
                            mRecordingThread = null;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    lastPosition = realPosition;
                    lstViewHolder.get(realPosition - 1).Join_usr_name.setTextColor(Color.parseColor("#275AAB"));
                    lstViewHolder.get(realPosition - 1).join_image.setBackgroundColor(Color.parseColor("#383838"));
                    lstViewHolder.get(realPosition - 1).redCross.setVisibility(GONE);
                    lstViewHolder.get(realPosition).Join_usr_name.setTextColor(Color.parseColor("#FFFFFF"));
                    lstViewHolder.get(realPosition).join_image.setBackgroundColor(Color.parseColor("#656565"));
                    if (userId.equals(Joined_artist.get(0).getUser_id())) {
                        lstViewHolder.get(realPosition).redCross.setVisibility(VISIBLE);
                    }

                    JoinActivity.txtCount.setText(realPosition + 1 + " of " + getItemCount());
                    JoinedArtists join = Joined_artist.get(realPosition);
                    JoinActivity.waveform_view.setVisibility(VISIBLE);
                    if (JoinActivity.ivJoinPlay.getVisibility() == VISIBLE) {
                        JoinActivity.ivJoinPlay.setVisibility(v.GONE);
                        JoinActivity.ivJoinPause.setVisibility(v.VISIBLE);
                    }
                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    if (mp != null) {
                        try {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                                mp = null;

                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }

                    String play = JoinActivity.play_count.getText().toString().trim();
                    int playValue = Integer.parseInt(play) + 1;
                    String recording_id = join.getRecording_id().trim();

                    if (!userId.equals("") && userId != null) {
                        JoinActivity.play_count.setText(String.valueOf(playValue));
                        fetchViewCount(userId, recording_id);
                    }

                    mp = new MediaPlayer();
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mp.setDataSource(join.getRecording_url());
                        mp.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            progressDialog.dismiss();
                            mp.start();
                            JoinActivity.chrono.setBase(SystemClock.elapsedRealtime());
                            JoinActivity.chrono.start();
                            try {
                                if (mRecordingThread == null) {
                                    mShouldContinue = true;
                                    mRecordingThread = new RecordingThread();
                                    mRecordingThread.start();
                                } else if (!mRecordingThread.isAlive()) {
                                    try {
                                        mShouldContinue = true;
                                        mRecordingThread = new RecordingThread();
                                        mRecordingThread.start();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    mRecordingThread.stopRunning();
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }


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
                            JoinActivity.chrono.stop();
                            if (mRecordingThread != null) {
                                mRecordingThread.stopRunning();
                                mRecordingThread = null;
                                //    mShouldContinue=true;
                            }
                            JoinActivity.ivJoinPlay.setVisibility(VISIBLE);
                            JoinActivity.ivJoinPause.setVisibility(GONE);


                            progressDialog.dismiss();
                        }
                    });
                }
                Log.d("Next play", "" + realPosition);

            }
        });

        JoinActivity.ivPlayPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (realPosition == 0) {
                    JoinActivity.ivPlayPre.setEnabled(false);
                    JoinActivity.ivPlayNext.setEnabled(true);
                } else {
                    try {
                        playSt = true;
                        lastPosition = realPosition - 1;
                        lstViewHolder.get(realPosition).Join_usr_name.setTextColor(Color.parseColor("#275AAB"));
                        lstViewHolder.get(realPosition).join_image.setBackgroundColor(Color.parseColor("#383838"));
                        lstViewHolder.get(realPosition).redCross.setVisibility(GONE);
                        lstViewHolder.get(realPosition - 1).Join_usr_name.setTextColor(Color.parseColor("#FFFFFF"));
                        lstViewHolder.get(realPosition - 1).join_image.setBackgroundColor(Color.parseColor("#656565"));
                        if (userId.equals(Joined_artist.get(0).getUser_id())) {
                            lstViewHolder.get(realPosition - 1).redCross.setVisibility(VISIBLE);
                        }

                        realPosition = realPosition - 1;
                        count = realPosition;
                        JoinActivity.ivPlayNext.setEnabled(true);
                        try {
                            if (mRecordingThread != null) {
                                mRecordingThread.stopRunning();
                                mRecordingThread = null;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        JoinActivity.txtCount.setText(count + 1 + " of " + getItemCount());
                        JoinedArtists join = Joined_artist.get(realPosition);
                        JoinActivity.waveform_view.setVisibility(VISIBLE);
                        if (JoinActivity.ivJoinPlay.getVisibility() == VISIBLE) {
                            JoinActivity.ivJoinPlay.setVisibility(v.GONE);
                            JoinActivity.ivJoinPause.setVisibility(v.VISIBLE);
                        }

                        progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        if (mp != null) {
                            try {
                                if (mp.isPlaying()) {
                                    mp.stop();
                                    mp.release();
                                    mp = null;

                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }

                        String play = JoinActivity.play_count.getText().toString().trim();
                        int playValue = Integer.parseInt(play) + 1;
                        String recording_id = join.getRecording_id().trim();

                        if (!userId.equals("") && userId != null) {
                            JoinActivity.play_count.setText(String.valueOf(playValue));
                            fetchViewCount(userId, recording_id);
                        }

                        mp = new MediaPlayer();
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mp.setDataSource(join.getRecording_url());
                            mp.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                progressDialog.dismiss();
                                mp.start();
                                JoinActivity.chrono.setBase(SystemClock.elapsedRealtime());
                                JoinActivity.chrono.start();
                                try {
                                    if (mRecordingThread == null) {
                                        mShouldContinue = true;
                                        mRecordingThread = new RecordingThread();
                                        mRecordingThread.start();
                                    } else if (!mRecordingThread.isAlive()) {
                                        try {
                                            mShouldContinue = true;
                                            mRecordingThread = new RecordingThread();
                                            mRecordingThread.start();
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        mRecordingThread.stopRunning();
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }


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
                                JoinActivity.chrono.stop();
                                if (mRecordingThread != null) {
                                    mRecordingThread.stopRunning();
                                    mRecordingThread = null;
                                    //    mShouldContinue=true;
                                }
                                JoinActivity.ivJoinPlay.setVisibility(VISIBLE);
                                JoinActivity.ivJoinPause.setVisibility(GONE);
                                progressDialog.dismiss();
                            }
                        });

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                }
                Log.d("Previous play", "" + realPosition);

            }
        });

        JoinActivity.ivJoinPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinActivity.ivJoinPlay.setVisibility(v.VISIBLE);
                JoinActivity.ivJoinPause.setVisibility(v.GONE);
                if (mRecordingThread != null) {
                    mRecordingThread.stopRunning();
                    mRecordingThread = null;
                    //    mShouldContinue=true;
                }
                mp.pause();
                JoinActivity.chrono.stop();
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

    public void getJoined_users(final String addedBy, final String RecId, final int pos) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, JOINED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);
                        JoinActivity.instrumentList.clear();
                        //     JoinActivity.listProfile.clear();
//                        if (click_pos == 0) {
//                            new ParseContents(getApplicationContext()).parseJoinInstrument(response, JoinActivity.instrumentList, String.valueOf(click_pos));
//                            JoinActivity.adapter1 = new JoinInstrumentListAdp(JoinActivity.instrumentList, getApplicationContext());
//                            JoinActivity.recyclerViewInstruments.setAdapter(JoinActivity.adapter1);
//                        } else {
                        new ParseContents(getApplicationContext()).parseJoinInstrument(response, JoinActivity.instrumentList, pos);
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
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
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

    public class RecordingThread extends Thread {


        @Override
        public void run() {

            //  android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
            try {

                recorder.startRecording();
                Log.d("Recording issue", "SampleRate" + SAMPLING_RATE + "BufferSize" + mBufferSize + "AudioBuffer" + mAudioBuffer);
            } catch (IllegalStateException e) {
                Log.d("Recording issue", e.toString());
            }

            while (shouldContinue()) {
                recorder.read(mAudioBuffer, 0, mBufferSize / 2);
                JoinActivity.waveform_view.updateAudioData(mAudioBuffer);
                updateDecibelLevel();
            }

            try {
                recorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            recorder.release();

        }

        public synchronized boolean shouldContinue() {
            return mShouldContinue;
        }

        public synchronized void stopRunning() {
            mShouldContinue = false;
        }
    }

    private void updateDecibelLevel() {

        double sum = 0;

        for (short rawSample : mAudioBuffer) {
            double sample = rawSample / 32768.0;
            sum += sample * sample;
        }

        double rms = Math.sqrt(sum / mAudioBuffer.length);
        final double db = 20 * Math.log10(rms);

        // Update the text view on the main thread.
        JoinActivity.mDecibelView.post(new Runnable() {
            @Override
            public void run() {
                // mDecibelView.setText(String.format(mDecibelFormat, db));
            }
        });
    }
}
