package com.instamelody.instamelody.Adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instamelody.instamelody.ContactsActivity;
import com.instamelody.instamelody.Models.Contacts;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shubhansh Jaiswal on 04/05/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Contacts> contactsList = new ArrayList<>();
//    Set<String> recieverId = new HashSet<>();
    String recieverId = "";
    String receiverToken = "";

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
                    if (grey_circle.getVisibility() == View.VISIBLE) {
                        grey_circle.setVisibility(View.GONE);
                        blue_circle.setVisibility(View.VISIBLE);
                        Count = Count + 1;
                        recieverId = contactsList.get(getAdapterPosition()).getUser_id();
                        receiverToken = contactsList.get(getAdapterPosition()).getDeviceToken();
                        if (Count >= 1) {
                            ContactsActivity.btnCancel.setVisibility(View.GONE);
                            ContactsActivity.btnOK.setVisibility(View.VISIBLE);
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
                    }

                    SharedPreferences.Editor editor = context.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                    editor.putString("receiverId", recieverId);
                    editor.commit();

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
}
