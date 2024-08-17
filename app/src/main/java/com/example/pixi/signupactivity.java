package com.example.pixi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupactivity extends AppCompatActivity {

    EditText signupname, signupemail, signupusername, signuppassword;
    TextView loginredirecttext, adminredirect;  // Added adminredirect TextView
    Button signupbutton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);

        signupname = findViewById(R.id.signup_name);
        signupemail = findViewById(R.id.signup_email);
        signupusername = findViewById(R.id.signup_username);
        signuppassword = findViewById(R.id.signup_password);
        loginredirecttext = findViewById(R.id.loginredirecttext);
        adminredirect = findViewById(R.id.te1);  

        signupbutton = findViewById(R.id.signupbutton);

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = signupname.getText().toString();
                String email = signupemail.getText().toString();
                String username = signupusername.getText().toString();
                String password = signuppassword.getText().toString();

                Helperclass helperClass = new Helperclass(name, email, username, password);
                reference.child(name).setValue(helperClass);

                Toast.makeText(signupactivity.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(signupactivity.this, loginactivity.class);
                startActivity(intent);
            }
        });

        loginredirecttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signupactivity.this, loginactivity.class);
                startActivity(intent);
            }
        });

        adminredirect.setOnClickListener(v -> {
            Intent intent = new Intent(signupactivity.this, admin.class);
            startActivity(intent);
        });
    }
}
