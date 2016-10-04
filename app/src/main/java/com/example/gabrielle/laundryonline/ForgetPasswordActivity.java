package com.example.gabrielle.laundryonline;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
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
    private Button resetButton,cancelButton;
    private String email;
    private DatabaseReference mDatabase;
    private SessionManager session;
    private TextView warningTV;
    private FirebaseAuth mAuth;
    private View progressBar;
    private LinearLayout ll;
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
        cancelButton = (Button)findViewById(R.id.batalKirimButton);
        progressBar = findViewById(R.id.forgetPasswordProgress);
        ll = (LinearLayout) findViewById(R.id.verificationCodeLayout);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("resetpasswordlink","test1");
                emailTextInput.setError(null);
                resetPassword();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToLoginPage = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                startActivity(backToLoginPage);
            }
        });

        //telephoneNumber = "081221084303";
    }
    public void showDialog(final String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            finish();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public void resetPassword() {
        //Log.d("resetpasswordlink","test2");

        boolean cancel = false;
        View focusView = null;
        email = emailTextInput.getText().toString();
        if(email.length()==0||!isEmailValid(email) ){
            emailTextInput.setError("Email tidak valid");
            focusView=emailTextInput;
            cancel=true;
        }
        //Log.d("resetpasswordlink","test3"+email);
        if(cancel){
            focusView.requestFocus();
        }else{
            ll.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if(mAuth!=null){
                //Log.d("resetpasswordlink","authnotnull");
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        showDialog("EMAIL TERKIRIM \r\n SILAHKAN CEK EMAIL ANDA \r\n UNTUK MENGATUR ULANG \r\n KATA KUNCI");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        ll.setVisibility(View.VISIBLE);
                        emailTextInput.setError("EMAIL TIDAK DITEMUKAN");

                    }
                });
            }else{
                //Log.d("resetpasswordlink","authnull");
            }
        }
    }
}
