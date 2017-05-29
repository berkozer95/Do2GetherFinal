package com.berkozer.do2getherfinal.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.berkozer.do2getherfinal.R;

import java.util.ArrayList;

public class WalkingFragment extends Fragment {
    public static ArrayList<String> walkingSteps = new ArrayList<>();
    public static ArrayAdapter<String> arrayAdapter;
    public static TextView duration, startingAddress, destinationAddress;


    public WalkingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.steps_fragment, container, false);

        ListView directionsLV = (ListView) view.findViewById(R.id.stepsListView);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, walkingSteps);

        duration = (TextView) view.findViewById(R.id.durationTextView);
        startingAddress = (TextView) view.findViewById(R.id.startingAddressTextView);
        destinationAddress = (TextView) view.findViewById(R.id.destinationAddressTextView);

        duration.setVisibility(View.GONE);
        startingAddress.setVisibility(View.GONE);
        destinationAddress.setVisibility(View.GONE);

        directionsLV.setAdapter(arrayAdapter);

        return view;
    }
}
