package com.example.margoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.margoapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StatusActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;

    private boolean alreadyLoggedIn;

    private DatabaseReference alertsReference = FirebaseDatabase
            .getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference().child("Alerts");

    private static ArrayList<String> alertItems;

    private static Context status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        status = StatusActivity.this;

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        alreadyLoggedIn = MainActivity.getLoggedIn();

        alertItems = MainActivity.getAlertItems();

        getSupportFragmentManager().beginTransaction().replace(R.id.f1Fragment, statusFragment).commit();

            alertsReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                String addContent = snapshot.child("alertContent").getValue().toString();
                                alertItems.add(addContent);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

    }
    StatusFragment statusFragment = new StatusFragment();
    AlertsFragment alertsFragment = new AlertsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.status:
                getSupportFragmentManager().beginTransaction().replace(R.id.f1Fragment, statusFragment).commit();
                return true;
            case R.id.alerts:
                getSupportFragmentManager().beginTransaction().replace(R.id.f1Fragment, alertsFragment).commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.f1Fragment, settingsFragment).commit();
                return true;
            case R.id.logout:
                logOut();
                return true;
        }
        return false;
    }

    public static void showAlert() {
        String notRoomNum = MainActivity.getNotRoomNum();
        String notBedNum = MainActivity.getNotBedNum();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(status);
        builder1.setTitle("LEAKAGE DETECTED, PROCEED IMMEDIATELY!");
        builder1.setMessage("ROOM: " + notRoomNum + "\nBED: " + notBedNum);
        builder1.setCancelable(true);
        builder1.setIcon(android.R.drawable.ic_dialog_alert);

        builder1.setPositiveButton(
                "CONFIRM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder1.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        logOut();
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(StatusActivity.this, MainActivity.class));
    }
}
