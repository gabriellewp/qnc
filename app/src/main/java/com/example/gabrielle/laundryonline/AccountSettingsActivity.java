package com.example.gabrielle.laundryonline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gabrielle on 7/17/2016.
 */
public class AccountSettingsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private SessionManager session;
    private DatabaseReference mDatabase;
    private Button logoutButton, changePassword;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        session= new SessionManager(getApplicationContext());
        //FirebaseUser user = firebaseAuth.getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        logoutButton = (Button) findViewById(R.id.logoutButton);
        changePassword = (Button) findViewById(R.id.changePassword);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new SessionManager(getApplicationContext());
                mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                session.logoutUser();

            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

}
