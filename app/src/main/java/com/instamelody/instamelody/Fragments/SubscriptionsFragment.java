package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.instamelody.instamelody.R;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class SubscriptionsFragment extends Fragment {

    public SubscriptionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subscriptions, container, false);
    }
}