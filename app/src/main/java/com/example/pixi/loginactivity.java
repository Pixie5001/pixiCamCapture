package com.example.pixi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class loginactivity extends AppCompatActivity {

    EditText loginusername, loginpassword;
    Button loginbutton;
    TextView signupredirecttext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        loginusername = findViewById(R.id.login_username);
        loginpassword = findViewById(R.id.login_password);
        signupredirecttext = findViewById(R.id.signupredirecttext);
        loginbutton = findViewById(R.id.login_button);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateUsername() | !validatePassword()){

                } else {
                    checkUser();
                }
            }
        });
        signupredirecttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginactivity.this, signupactivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateUsername() {
        String val = loginusername.getText().toString();
        if (val.isEmpty()) {
            loginusername.setError("user name cannot be empty");
            return false;
        } else {
            loginusername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginpassword.getText().toString();
        if (val.isEmpty()) {
            loginpassword.setError("Password cannot be empty");
            return false;
        } else {
            loginpassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = loginusername.getText().toString().trim();
        String userPassword = loginpassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    loginusername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if(!Objects.equals(passwordFromDB, userPassword)){
                        loginusername.setError(null);
                        Intent intent = new Intent(loginactivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        loginpassword.setError("Invalid Credential");
                        loginpassword.requestFocus();
                    }
                } else {
                    loginusername.setError("User does not exist");
                    loginusername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}