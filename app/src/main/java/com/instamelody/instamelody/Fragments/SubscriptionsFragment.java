package com.instamelody.instamelody.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Models.SubscriptionPackage;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.utils.Const.ServiceType.ADVERTISEMENT;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.PACKAGES;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class SubscriptionsFragment extends Fragment {
    String KEY_SUBSCRIPTION = "key";
    ArrayList<SubscriptionPackage> subscriptionPackageArrayList = new ArrayList<>();

    TextView tvFreemium, descFreeLayers, tvPriceFree, tvStandard, tvStandardLayers, priceStandard, tvPremium,
            tvPremiumLayers, pricePremium, tvProducer, tvProducerLayers, priceProducer;

    Switch switchFree, switchStandard, switchPremium, switchProducer;

    public SubscriptionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptionPackage();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        tvFreemium = (TextView) view.findViewById(R.id.tvFreemium);
        descFreeLayers = (TextView) view.findViewById(R.id.descFreeLayers);
        tvPriceFree = (TextView) view.findViewById(R.id.tvPriceFree);
        tvStandard = (TextView) view.findViewById(R.id.tvStandard);
        tvStandardLayers = (TextView) view.findViewById(R.id.tvStandardLayers);
        priceStandard = (TextView) view.findViewById(R.id.priceStandard);
        tvPremium = (TextView) view.findViewById(R.id.tvPremium);
        tvPremiumLayers = (TextView) view.findViewById(R.id.tvPremiumLayers);
        pricePremium = (TextView) view.findViewById(R.id.pricePremium);
        tvProducer = (TextView) view.findViewById(R.id.tvProducer);
        tvProducerLayers = (TextView) view.findViewById(R.id.tvProducerLayers);
        priceProducer = (TextView) view.findViewById(R.id.priceProducer);


        TextView[] tv = new TextView[4];
        tv[0] = (TextView) view.findViewById(R.id.tvFreemium);
        tv[1] = (TextView) view.findViewById(R.id.tvStandard);
        tv[2] = (TextView) view.findViewById(R.id.tvPremium);
        tv[3] = (TextView) view.findViewById(R.id.tvProducer);


        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
            tv[i].setText(subscriptionPackageArrayList.get(i).getPackage_name());
        }

        TextView[] tv1 = new TextView[4];
        tv1[0] = (TextView) view.findViewById(R.id.descFreeLayers);
        tv1[1] = (TextView) view.findViewById(R.id.tvStandardLayers);
        tv1[2] = (TextView) view.findViewById(R.id.tvPremiumLayers);
        tv1[3] = (TextView) view.findViewById(R.id.tvProducerLayers);

        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
            tv1[i].setText(subscriptionPackageArrayList.get(i).getTotal_melody());
        }

        TextView[] tv2 = new TextView[4];
        tv2[0] = (TextView) view.findViewById(R.id.tvPriceFree);
        tv2[1] = (TextView) view.findViewById(R.id.priceStandard);
        tv2[2] = (TextView) view.findViewById(R.id.pricePremium);
        tv2[3] = (TextView) view.findViewById(R.id.priceProducer);

        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
            tv2[0].setText(subscriptionPackageArrayList.get(i).getCost());
        }

        switchFree = (Switch) view.findViewById(R.id.switchFree);
        switchStandard = (Switch) view.findViewById(R.id.switchStandard);
        switchPremium = (Switch) view.findViewById(R.id.switchPremium);
        switchProducer = (Switch) view.findViewById(R.id.switchProducer);
        return view;


    }

    public void subscriptionPackage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PACKAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("AdvertisementData", response);
                        subscriptionPackageArrayList.clear();
                        new ParseContents(getActivity()).parsePackageSubscription(response, subscriptionPackageArrayList);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_SUBSCRIPTION, "admin@123");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


}