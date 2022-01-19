package com.example.margoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button goRegister;
    private Button logIn;
    private TextView forgotPassword;
    private EditText loginUsername, loginPassword;

    private DatabaseReference databaseReference = FirebaseDatabase
            .getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference().child("Patients");
    private DatabaseReference alertsReference = FirebaseDatabase
            .getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference().child("Alerts");
    private static String notRoomNum;
    private static String notBedNum;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private String currentTime;

    private static ArrayList<String> alertItems;

    private static boolean alreadyLoggedIn;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        goRegister = (Button) findViewById(R.id.gotoRegister);
        goRegister.setOnClickListener(this);

        logIn = (Button) findViewById(R.id.logIn);
        logIn.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.forgotBttn);
        forgotPassword.setOnClickListener(this);

        loginUsername = (EditText) findViewById(R.id.userName);
        loginPassword = (EditText) findViewById(R.id.passWord);

        progressBar = (ProgressBar) findViewById(R.id.load);

        alertItems = new ArrayList<String>();

        alertItems.clear();

        //Redirect to main page if already logged in
        try {
            FirebaseAuth.getInstance().getCurrentUser().getUid();
            alreadyLoggedIn = true;
            startActivity(new Intent(MainActivity.this, StatusActivity.class));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "You must log-in", Toast.LENGTH_SHORT).show();
            alreadyLoggedIn = false;
        }



        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("condition").getValue().toString().equals("LEAK")) {
                    Toast.makeText(getApplicationContext(), "DEMO", Toast.LENGTH_SHORT).show();
                    notRoomNum = snapshot.child("roomNumber").getValue().toString();
                    notBedNum = snapshot.child("bedNumber").getValue().toString();
                    currentTime = Calendar.getInstance().getTime().toString();

                    String alertContent = "Room: " + notRoomNum + "\nBed: " + notBedNum + "\nTime: " + currentTime;

                    Alert nAlert = new Alert(alertContent);

                    try {
                        FirebaseDatabase.getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Alerts")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentTime).setValue(nAlert);
                    } catch (Exception e) {
                        FirebaseDatabase.getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Alerts")
                            .child("Unknown User").child(currentTime).setValue(nAlert);
                    }

                    StatusActivity.showAlert();

                    notification();
                }
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

    private void notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("leak", "leak", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "leak")
                .setSmallIcon(R.drawable.margo_logo)
                .setAutoCancel(true)
                .setContentTitle("LEAKAGE DETECTED")
                .setContentText("Room Number: " + notRoomNum +
                        " Bed Number: " + notBedNum);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.gotoRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.logIn:
                userLogin();
                break;

            case R.id.forgotBttn:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    public static ArrayList<String> getAlertItems(){
        return MainActivity.alertItems;
    }

    public static boolean getLoggedIn(){
        return MainActivity.alreadyLoggedIn;
    }

    public static String getNotRoomNum(){
        return MainActivity.notRoomNum;
    }

    public static String getNotBedNum(){
        return MainActivity.notBedNum;
    }

    private void userLogin() {
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (username.isEmpty()){
            loginUsername.setError("E-Mail field cannot be empty.");
            loginUsername.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            loginUsername.setError("Please enter a valid e-mail.");
            loginUsername.requestFocus();
            return;
        }
        if (password.isEmpty()){
            loginPassword.setError("Password field cannot be empty.");
            loginUsername.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Create a child and remove it to trigger onChildAdded
                    alertsReference
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("A").setValue("A");
                    alertsReference
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("A").removeValue();
                    //redirect to status screen
                    startActivity(new Intent(MainActivity.this, StatusActivity.class));

                }else{
                    Toast.makeText(MainActivity.this, "Invalid username or password!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
