package com.example.margoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    View nView;
    private FirebaseAuth mAuth;

    private EditText regUsername;
    private EditText regEmail;
    private EditText regPassword;
    private EditText confirmPass;

    private Button register;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);

        Button backLogin = (Button) findViewById(R.id.backLogin);
        backLogin.setOnClickListener(this);

        regUsername = (EditText) findViewById(R.id.regName);
        regEmail = (EditText) findViewById(R.id.email);
        regPassword = (EditText) findViewById(R.id.regPassword);
        confirmPass = (EditText) findViewById(R.id.confirmPass);

        progressBar = (ProgressBar) findViewById(R.id.load);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLogin:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String username = regUsername.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        String confirm = confirmPass.getText().toString().trim();
        Intent goLogin = new Intent(this, MainActivity.class);

        if (username.isEmpty()) {
            regUsername.setError("Username field cannot be empty.");
            regUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            regEmail.setError("E-Mail field cannot be empty.");
            regEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            regEmail.setError("Please enter a valid e-mail.");
            regEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            regPassword.setError("Password field cannot be empty.");
            regPassword.requestFocus();
            return;
        }

        if (password.length()<4) {
            regPassword.setError("Password must be at least four characters long.");
            regPassword.requestFocus();
            return;
        }

        if (confirm.isEmpty()) {
            confirmPass.setError("You must confirm your password.");
            confirmPass.requestFocus();
            return;
        }

        if (!confirm.equals(password)) {
            confirmPass.setError("Password do not match.");
            regPassword.requestFocus();
            confirmPass.requestFocus();
            return;
        }

        register.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            User nUser = new User(username, email);

                            FirebaseDatabase.getInstance("https://margologin-a1211-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(nUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been successfully registered!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        switch(nView.getId()) {
                                            case R.id.register:
                                                startActivity(goLogin);
                                                break;
                                        }

                                    }else {
                                        Toast.makeText(RegisterActivity.this, "Unable to register user.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        register.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(RegisterActivity.this, "Unable to register user.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            register.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}