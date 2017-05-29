package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.instamelody.instamelody.Adapters.ActivityCardAdapter;
import com.instamelody.instamelody.Adapters.MelodyCardAdapter;
import com.instamelody.instamelody.Adapters.MelodyCardListAdapter;
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Models.ActivityData;
import com.instamelody.instamelody.Models.ActivityModel;
import com.instamelody.instamelody.Models.RecordingsData;
import com.instamelody.instamelody.Models.ActivityModel;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StationActivity;

import java.util.ArrayList;

/**
 * Created by Saurabh Singh on 12/16/2016.
 */

public class ActivityFragment extends Fragment {

    private static RecyclerView.Adapter activityAdapter;
    private RecyclerView.LayoutManager lmactivity;
    private RecyclerView recyclerView;

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

        for (int i = 0; i < ActivityData.id_.length; i++) {
            arraylist.add(new ActivityModel(
                    ActivityData.id_[i],
                    ActivityData.userProfileImage[i],
                    ActivityData.UserNameArray1[i],
                    ActivityData.Topic[i],
                    ActivityData.Time[i]

            ));
        }

        activityAdapter = new ActivityCardAdapter(arraylist);

        recyclerView.setAdapter(activityAdapter);

        return view;
    }
}
