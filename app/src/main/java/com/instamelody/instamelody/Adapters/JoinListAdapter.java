package com.instamelody.instamelody.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instamelody.instamelody.Models.JoinedArtists;
import com.instamelody.instamelody.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Macmini on 22/08/17.
 */

public class JoinListAdapter extends RecyclerView.Adapter<JoinListAdapter.MyViewHolder> {
    Context context;
    private ArrayList<JoinedArtists> Joined_artist = new ArrayList<>();


    public JoinListAdapter(ArrayList<JoinedArtists> Joined_artist, Context context) {
        this.Joined_artist = Joined_artist;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView join_image;
        TextView Join_usr_name;

        public MyViewHolder(View view) {
            super(view);
            join_image = (ImageView) view.findViewById(R.id.ivImageName);
            Join_usr_name = (TextView) view.findViewById(R.id.tvUserName);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_join_image, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JoinedArtists joinArt = Joined_artist.get(position);
        holder.Join_usr_name.setText(joinArt.getJoined_usr_name());
        Picasso.with(holder.join_image.getContext()).load(joinArt.getJoined_image()).into(holder.join_image);

    }

    @Override
    public int getItemCount() {
        return Joined_artist.size();
    }
}
