package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.instamelody.instamelody.Adapters.ActivityCardAdapter;
import com.instamelody.instamelody.Models.ActivityData;
import com.instamelody.instamelody.Models.ActivityModel;
import com.instamelody.instamelody.R;

import java.util.ArrayList;

/**
 * Created by CBPC 41 on 1/14/2017.
 */

public class ProfileActivityFragment extends Fragment {

    private static RecyclerView.Adapter activityAdapter;
    private RecyclerView.LayoutManager lmactivity;
    private RecyclerView recyclerView;

    private static ArrayList<ActivityModel> arraylist;

    public ProfileActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_profile, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewActivity);
        recyclerView.setHasFixedSize(true);
        lmactivity = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lmactivity);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arraylist = new ArrayList<ActivityModel>();

        for (int i = 0; i < ActivityData.id_.length; i++) {
//            arraylist.add(new ActivityModel(
//                    ActivityData.id_[i],
//                    ActivityData.userProfileImage[i],
//                    ActivityData.UserNameArray1[i],
//                    ActivityData.Topic[i],
//                    ActivityData.Time[i]
//
//            ));
        }

        activityAdapter = new ActivityCardAdapter(arraylist);

        recyclerView.setAdapter(activityAdapter);

        return view;
    }
}
