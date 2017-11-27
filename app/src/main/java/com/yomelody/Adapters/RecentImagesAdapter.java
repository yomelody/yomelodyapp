package com.yomelody.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import com.yomelody.ChatActivity;
import com.yomelody.Models.Message;
import com.yomelody.Models.RecentImagesModel;
import com.yomelody.R;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.ImageCompressor;
import com.yomelody.utils.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.yomelody.ChatActivity.CHAT_ID;
import static com.yomelody.ChatActivity.CHAT_ID_;
import static com.yomelody.ChatActivity.FILE;
import static com.yomelody.ChatActivity.FILE_TYPE;
import static com.yomelody.ChatActivity.IS_READ;
import static com.yomelody.ChatActivity.RECEIVER_ID;
import static com.yomelody.ChatActivity.SENDER_ID;
import static com.yomelody.ChatActivity.TITLE;
import static com.yomelody.ChatActivity.alertDialog;
import static com.yomelody.ChatActivity.cAdapter;
import static com.yomelody.ChatActivity.chatId;
import static com.yomelody.ChatActivity.chatList;
import static com.yomelody.ChatActivity.chatType;
import static com.yomelody.ChatActivity.flagFileType;
import static com.yomelody.ChatActivity.group;
import static com.yomelody.ChatActivity.groupList;
import static com.yomelody.ChatActivity.ivRecieverProfilePic;
import static com.yomelody.ChatActivity.receiverId;
import static com.yomelody.ChatActivity.receiverImage;
import static com.yomelody.ChatActivity.receiverName;
import static com.yomelody.ChatActivity.recyclerViewChat;
import static com.yomelody.ChatActivity.rlNoMsg;
import static com.yomelody.ChatActivity.rlTxtContent;
import static com.yomelody.ChatActivity.senderId;
import static com.yomelody.ChatActivity.tvRecieverName;
import static com.yomelody.ChatActivity.tvUserName;
import static com.yomelody.ChatActivity.userId;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.CHAT;
import static com.yomelody.utils.Const.ServiceType.MESSAGE_LIST;
import static com.yomelody.utils.Const.ServiceType.READ_STATUS;

/**
 * Created by Shubhansh Jaiswal on 23/01/17.
 */

public class RecentImagesAdapter extends RecyclerView.Adapter<RecentImagesAdapter.MyViewHolder> {

    ArrayList<RecentImagesModel> fileList = new ArrayList<>();
    Context context;
    public static int pos;
    String sendImageName = "";
    Bitmap sendImageBitmap;

    public RecentImagesAdapter(ArrayList<RecentImagesModel> filesArray, Context context) {
        this.fileList = filesArray;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryImages;

        public MyViewHolder(View mView) {
            super(mView);
            this.galleryImages = (ImageView) mView.findViewById(R.id.galleryImages);

            galleryImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences.Editor selectedImagePos = context.getSharedPreferences("selectedImagePos", MODE_PRIVATE).edit();
                    selectedImagePos.putString("pos", String.valueOf(getAdapterPosition()));
                    selectedImagePos.apply();
                    sendImageName = fileList.get(getAdapterPosition()).getName();
                    sendImageBitmap = fileList.get(getAdapterPosition()).getBitmap();
                    sendImage(userId, sendImageName, sendImageBitmap);
                    alertDialog.cancel();
                }


            });

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.veiw_image, parent, false);
        return new MyViewHolder(view);
    }

    @TargetApi(19)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int listPosition) {
        try {
            Bitmap sendImageBitmap;
            ImageCompressor ic = new ImageCompressor(context);
            sendImageBitmap = ic.compressImage(fileList.get(listPosition).getFilepath());
            ChatActivity.fileInfo.get(listPosition).setBitmap(sendImageBitmap);
            holder.galleryImages.setImageBitmap(sendImageBitmap);
//        Picasso.with(context).load(new File(fileList.get(listPosition).getFilepath())).into(holder.galleryImages);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    private void sendImage(final String user_Id, final String imageName, final Bitmap bit) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, CHAT,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String str = new String(response.data);
                        AppHelper.sop("Chat.php Response for image :- " + str);
                        flagFileType = "0";
//                            Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(str);
                            String flag=json.getString("flag");
                            if(flag.equals("success")){
                                JSONObject jsonMsg = json.getJSONObject("usermsg");
                                String chat_id = jsonMsg.getString("chat_id");
                                SharedPreferences.Editor editor =  context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                                editor.putString("chatId", chat_id);
                                chatId=chat_id;
                                getChatMsgs(chat_id);
                            }
                            else{
                                JSONObject jsonMsg = json.getJSONObject("usermsg");
                                String chat_id = jsonMsg.getString("chat_id");
                                getChatMsgs(chat_id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
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
                    errorMsg = "Parse error";
                } else if (error == null) {

                }

                if (!errorMsg.equals("")) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.d("Error", errorMsg);
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (chatType.equals("group")) {
                    if (senderId.equals(user_Id)) {
                        params.put(RECEIVER_ID, receiverId);
                        params.put("groupName", tvUserName.getText().toString().trim());
                    } else {
                        group = senderId + "," + receiverId;
                        groupList = new ArrayList<>(Arrays.asList(group.split(",")));
                        groupList.remove(groupList.indexOf(user_Id));
                        String s = groupList.toString().replaceAll(", ", ",");
                        receiverId = s.substring(1, s.length() - 1).trim();
                        params.put(RECEIVER_ID, receiverId);
                        params.put("groupName", tvUserName.getText().toString().trim());
                    }
                } else {
                    if (receiverId.equals(user_Id)) {
                        params.put(RECEIVER_ID, senderId);
                    } else {
                        params.put(RECEIVER_ID, receiverId);
                    }
                }

                params.put(FILE_TYPE, "image");
                params.put(SENDER_ID, user_Id);
                params.put(CHAT_ID, chatId);
                params.put(TITLE, "message");
                //  params.put(MESSAGE, message);
                params.put(IS_READ, "0");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put(FILE, new DataPart(imageName, AppHelper.getFileDataFromDrawable(context, bit), "image/jpeg"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        int socketTimeout = 60000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        multipartRequest.setRetryPolicy(policy);

        requestQueue.add(multipartRequest);
        //     VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }

    public void getChatMsgs(final String chat_Id) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, MESSAGE_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            if (ChatAdapter.sharedAudioList.size() > 0) {
                                ChatAdapter.sharedAudioList.clear();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        if (rlNoMsg.getVisibility() == View.VISIBLE && rlTxtContent.getVisibility() == View.VISIBLE) {
                            rlNoMsg.setVisibility(View.GONE);
                            rlTxtContent.setVisibility(View.GONE);
                        }

                        chatList.clear();
//                        audioDetailsList.clear();
//                        sharedAudioList.clear();
                        String usrId = userId;

                        cAdapter.notifyDataSetChanged();
                        JSONObject jsonObject;
                        JSONArray resultArray, audiosDetailsArray, sharedAudiosArray;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equals("success")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                resultArray = result.getJSONArray("message LIst");
                                if (resultArray.length() > 0) {
                                    for (int i = 0; i < resultArray.length(); i++) {
                                        Message message = new Message();
                                        JSONObject chatJson = resultArray.getJSONObject(i);
                                        message.setId(chatJson.getString("id"));
                                        message.setSenderId(chatJson.getString("senderID"));
                                        message.setProfilePic(chatJson.getString("sender_pic"));
                                        message.setMessage(chatJson.getString("message"));
                                        message.setFileType(chatJson.getString("file_type"));
                                        message.setFile(chatJson.getString("file_url"));
                                        message.setFileId(chatJson.getString("file_ID"));
                                        message.setIsRead(chatJson.getString("isread"));
                                        message.setCreatedAt(chatJson.getString("sendat"));
                                        message.setRecCount((chatJson.getString("Rec_count")));
                                        if (!chatJson.get("Audioshared").equals(null) && !chatJson.get("Audioshared").equals("")) {
                                            message.setAudioDetails(chatJson.getJSONArray("Audioshared"));
                                        }
                                        if (chatJson.getString("isread").equals("0") && (!chatJson.getString("senderID").equals(usrId))) {
                                            readStatus(chatJson.getString("id"), chatJson.getString("chatID"));
                                        }
                                        chatList.add(i, message);
                                    }
                                    recyclerViewChat.smoothScrollToPosition(chatList.size() - 1);
                                    rlNoMsg.setVisibility(View.GONE);
                                    rlTxtContent.setVisibility(View.GONE);
                                } else {
                                    tvRecieverName.setText(" " + receiverName);
                                    Picasso.with(ivRecieverProfilePic.getContext()).load(receiverImage).into(ivRecieverProfilePic);
                                    rlNoMsg.setVisibility(View.VISIBLE);
                                    rlTxtContent.setVisibility(View.VISIBLE);
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
                            errorMsg = "Parse error";
                        } else if (error == null) {

                        }

                        if (!errorMsg.equals("")) {
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                            error.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CHAT_ID_, chat_Id);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void readStatus(final String msgIds, final String chatIds) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, READ_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String str = response;
//                Toast.makeText(ChatActivity.this, str + "readStatus", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
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
                    errorMsg = "Parse error";
                } else if (error == null) {

                }

                if (!errorMsg.equals("")) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.d("Error", errorMsg);
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                try {
                    params.put("messageID", msgIds);
                    params.put("chatID", chatIds);
                    params.put("user_id", userId);
                    if (chatType.equals("group")) {
                        params.put("chat_type", "group");
                    } else {
                        params.put("chat_type", "single");
                    }
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}

