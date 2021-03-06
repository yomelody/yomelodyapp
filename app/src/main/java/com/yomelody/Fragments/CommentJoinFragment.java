package com.yomelody.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.yomelody.Adapters.CommentsAdapter;
import com.yomelody.Adapters.JoinListAdapter;
import com.yomelody.JoinActivity;
import com.yomelody.MessengerActivity;
import com.yomelody.Models.Comments;
import com.yomelody.Models.JoinedArtists;
import com.yomelody.Parse.ParseContents;
import com.yomelody.R;
import com.yomelody.SignInActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.yomelody.Adapters.JoinListAdapter.REQUEST_JOIN_TO_MESSANGER;
import static com.yomelody.JoinActivity.joinFooter;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.COMMENTS;
import static com.yomelody.utils.Const.ServiceType.COMMENT_LIST;


public class CommentJoinFragment extends Fragment {

    private String RECORDING_ID = "rid";
    RecyclerView recyclerViewComment;
    CommentsAdapter adapter;
    static ArrayList<Comments> commentList = new ArrayList<>();
    String COMMENT = "comment";
    String FILE_TYPE = "file_type";
    String USER_ID = "user_id";
    String FILE_ID = "file_id";
    String TOPIC = "topic";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    String userId = "";
    public static TextView tvPlayCount, tvLikeCount, tvCommentCount, tvShareCount;
    RelativeLayout rlCommentSend;
    TextView tvCancel, tvSend;
    EditText etComment;
    String melodyID;
    String fileType = "user_recording";
    ImageView ivShareButton;
    Activity mActivity;
    boolean check = false;

    public CommentJoinFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getComments();
        //  Toast.makeText(getActivity(), ""+ JoinListAdapter.click, Toast.LENGTH_SHORT).show();
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
        adapter = new CommentsAdapter(getActivity(), commentList);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment_join, container, false);

        tvPlayCount = (TextView) view.findViewById(R.id.tvPlayCount);
        tvLikeCount = (TextView) view.findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) view.findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) view.findViewById(R.id.tvShareCount);
        rlCommentSend = (RelativeLayout) view.findViewById(R.id.rlComment);
        etComment = (EditText) view.findViewById(R.id.etComment);
        etComment.setHintTextColor(Color.parseColor("#7B888F"));
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvSend = (TextView) view.findViewById(R.id.tvSend);
        ivShareButton = (ImageView) view.findViewById(R.id.ivShareButton);
        RecyclerView.LayoutManager lm1 = new LinearLayoutManager(getActivity());
        recyclerViewComment = (RecyclerView) view.findViewById(R.id.recyclerViewComment);
        recyclerViewComment.setLayoutManager(lm1);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setItemViewCacheSize(10);
        recyclerViewComment.setDrawingCacheEnabled(true);
        recyclerViewComment.setItemAnimator(new DefaultItemAnimator());
        recyclerViewComment.setAdapter(adapter);
        tvPlayCount.setText(JoinListAdapter.Joined_artist.get(0).getPlay_counts());
        tvLikeCount.setText(JoinListAdapter.Joined_artist.get(0).getLike_counts());
        tvCommentCount.setText(JoinListAdapter.Joined_artist.get(0).getComment_counts());
        tvShareCount.setText(JoinListAdapter.Joined_artist.get(0).getShare_counts());

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCancel.setVisibility(View.GONE);
                tvSend.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etComment.getText().toString().length() == 0) {
                    tvCancel.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);
                }
            }
        });

        ivShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
                alertDialog.setTitle(mActivity.getString(R.string.share_with_YoMelody));
//                        alertDialog.setMessage("Choose yes to share in chat.");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("audioShareData", MODE_PRIVATE).edit();
                        JoinedArtists recording = JoinListAdapter.Joined_artist.get(0);
                        editor.putString("recID", recording.getRecording_id());
                        editor.apply();
                        Intent intent = new Intent(mActivity, MessengerActivity.class);
                        intent.putExtra("Previ", "join");
                        intent.putExtra("share", JoinListAdapter.Joined_artist.get(0));
                        intent.putExtra("file_type", "user_recording");
                        mActivity.startActivityForResult(intent, REQUEST_JOIN_TO_MESSANGER);
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, "");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, JoinListAdapter.Joined_artist.get(0).getRecording_name());
                        shareIntent.putExtra(Intent.EXTRA_TEXT, JoinListAdapter.Joined_artist.get(0).getJoin_Thumbnail());
//                        shareIntent.setType("image/jpeg");


                        shareIntent.setType("text/plain");
                        /*shareIntent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.listen_to)+
                                " "+JoinListAdapter.Joined_artist.get(0).getRecording_name()+
                                "\n"+mActivity.getString(R.string.yomelody_music)
                                +"\n"+JoinListAdapter.Joined_artist.get(0).getJoin_Thumbnail());*/
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(Intent.createChooser(shareIntent, "Choose Sharing option!"));
                    }
                });
                alertDialog.show();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etComment.getText().clear();
                joinFooter.setVisibility(View.VISIBLE);
                closeKeyboard(getActivity(), tvCancel.getWindowToken());
                getFragmentManager().beginTransaction()
                        .remove(CommentJoinFragment.this).commit();
                String count = tvCommentCount.getText().toString().trim();
                JoinActivity.tvCommentCount.setText(count);
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userId.equals("") && userId != null) {

                    tvCancel.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);
                    String comment = etComment.getText().toString().trim();
                    etComment.getText().clear();
                    sendComment(comment, userId);
                    //    int commentCount = Integer.parseInt(tvCommentCount.getText().toString().trim()) + 1;

                    //      tvCommentCount.setText(String.valueOf(commentCount));

                } else {
                    Toast.makeText(getActivity(), "Log in to comment", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                }

            }
        });

        return view;
    }

    public void getComments() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        commentList.clear();
                        adapter.notifyDataSetChanged();
                        recyclerViewComment.smoothScrollToPosition(adapter.getItemCount());
                        new ParseContents(getActivity()).parseComments(response, commentList);

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
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(FILE_ID, JoinActivity.RecId);
                params.put(FILE_TYPE, fileType);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public void sendComment(final String cmnt, final String userId) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        adapter.notifyDataSetChanged();
                        getComments();
                        String count = tvCommentCount.getText().toString().trim();
                        int count_val = Integer.parseInt(count);
                        int count_final = count_val + 1;
                        tvCommentCount.setText(String.valueOf(count_final));
                        recyclerViewComment.smoothScrollToPosition(adapter.getItemCount());
                        new ParseContents(getActivity()).parseComments(response, commentList);
                        check = true;
                        Intent it = new Intent();
                        it.putExtra("comment", "success");
                        mActivity.setResult(Activity.RESULT_OK, it);

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
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(FILE_ID, JoinActivity.RecId);
                params.put(COMMENT, cmnt);
                params.put(FILE_TYPE, fileType);
                params.put(USER_ID, userId);
                params.put(TOPIC, JoinActivity.RecordingName);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


}
