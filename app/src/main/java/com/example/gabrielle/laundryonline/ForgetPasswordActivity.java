package com.example.gabrielle.laundryonline;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by gabrielle on 8/17/2016.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gabrielle on 6/17/2016.
 */
public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText emailTextInput;
    private Button resetButton;
    private String email;
    private DatabaseReference mDatabase;
    private SessionManager session;
    private TextView warningTV;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        session = new SessionManager(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        emailTextInput = (EditText) findViewById(R.id.emailInput);
        resetButton = (Button) findViewById(R.id.resetPassword);
        warningTV = (TextView) findViewById(R.id.warningSendReset);
        warningTV.setVisibility(View.INVISIBLE);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("resetpasswordlink","test1");
                resetPassword();
            }
        });
        //telephoneNumber = "081221084303";
    }
    public void resetPassword() {
        Log.d("resetpasswordlink","test2");
        email = emailTextInput.getText().toString();
        Log.d("resetpasswordlink","test3"+email);

        if(mAuth!=null){
            Log.d("resetpasswordlink","authnotnull");
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    warningTV.setVisibility(View.VISIBLE);
                    Log.d("resetpasswordlink","success");
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    // your code here
                                }
                            },
                            1000
                    );
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    warningTV.setText("Reset Password Failed");
                    Log.d("resetpassword","fail");
                    warningTV.setVisibility(View.VISIBLE);
                }
            });
        }else{
            Log.d("resetpasswordlink","authnull");
        }

    }
}
