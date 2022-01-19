package com.example.margoapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AlertsFragment extends Fragment implements View.OnClickListener {

    private DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference();

    private ListView alertList;

    private Button clear;

    private ArrayList<String> alertItems;


    public AlertsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        alertList = (ListView) view.findViewById(R.id.alertList);

        alertItems = MainActivity.getAlertItems();

        ArrayAdapter<String> menuItemsAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                alertItems
        );

        alertList.setAdapter(menuItemsAdapter);

        clear = (Button) view.findViewById(R.id.clear);

        clear.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.clear) {
            FirebaseDatabase.getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Alerts")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
            alertItems.clear();
            AlertsFragment fragment = new AlertsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.f1Fragment, fragment);
            fragmentTransaction.commit();
            Toast.makeText(getContext(), "List cleared", Toast.LENGTH_SHORT).show();
        }
    }


}
