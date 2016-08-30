package com.example.gabrielle.laundryonline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by gabrielle on 6/17/2016.
 */
public class PhoneVerificationActivity extends AppCompatActivity {
    private EditText codeTextInput;
    private Button verifyButton;
    private View mProgressView;
    private View mVerifyCodeView;
    private MyAsyncTask myAsyncTask;
    private String telephoneNumber,uid, intentStr;
    private String code;
    private DatabaseReference mDatabase;
    private Intent intentShowOption;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        Intent intent = getIntent();
        session = new SessionManager(getApplicationContext());
        intentShowOption = new Intent(this,ShowOptionActivity.class);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        telephoneNumber = intent.getStringExtra("telephoneNumber");
        uid = intent.getStringExtra("uid");
        intentStr = intent.getStringExtra("intent");
        codeTextInput = (EditText) findViewById(R.id.codeInput);
        verifyButton = (Button) findViewById(R.id.verifyingCodeButton);
        mProgressView = (View) findViewById(R.id.phoneVerificationProgress);
        mVerifyCodeView = (View) findViewById(R.id.verificationCodeLayout);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });

        //telephoneNumber = "081221084303";
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mVerifyCodeView.setVisibility(show ? View.GONE : View.VISIBLE);
            mVerifyCodeView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mVerifyCodeView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mVerifyCodeView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void verifyCode() {

        code = codeTextInput.getText().toString();
        Log.d("code",code);
        View focusView = null;
        boolean cancel = false;
        if (code.length() == 0) {

            focusView = codeTextInput;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            //start background task
            Log.d("checkerrorbreak", "test2");
            showProgress(true);
            Log.d("checkerrorbreak", "test3");
            myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute();

        }

    }
    public void showDialog(){
        String text = "KONFIRMASI BERHASIL \r\n SELAMAT DATANG \r\n TERIMA KASIH TELAH BERGABUNG DENGAN QUICK N' CLEAN LAUNDRY. NIKMATI LAYANAN LAUNDRY BERLANGGAN PROFESIONAL DALAM" +
                "GENGGAMAN TANGAN ANDA.";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(text)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(intentShowOption);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    class MyAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            StringBuilder result = new StringBuilder();
            try {
                // Set up the request

                //curl -i 'https://api.authy.com/protected/json/phones/verification/check?api_key=KEY&phone_number=111-111-111&country_code=1&verification_code=1234'
                URL url = new URL("https://api.authy.com/protected/json/phones/verification/check?api_key=3qfrirXCVmG1fhegtPCkuftoQiub3YgV&phone_number="+telephoneNumber+"&country_code=62&verification_code="+code);
                Log.d("checkerrorbreak", "test4");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d("checkerrorbreak", "test5");
                connection.setRequestMethod("GET");
                Log.d("checkerrorbreak", "test8");
                int responseCode = connection.getResponseCode();
                Log.d("responsecode", responseCode+"");
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                String returnString;
                String messag;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.d("resultfromget",result.toString());
                    try{
                        JSONObject myObject = new JSONObject(result.toString());
                        returnString = myObject.getString("success");
                        messag = myObject.getString("message");
                        Log.d("returnString",returnString);
                        Log.d("returnMessage",messag);
                        if(returnString.equals("true")){
                            mDatabase.child("users").child(uid).child("verifiedTelNumber").setValue(1);
                            return 1;
                        }else {
                            return 0;
                        }

                    }catch (JSONException jse){

                    }
                    return 1;
                    //response.toString();
                }else{
                    Log.d("resultfromgeterror",connection.getResponseMessage());
                    return 0;
                }
                //Log.d("checkerrorbreak", "test9");
                //return "Error: " + responseCode + " " + connection.getResponseMessage();
            } catch (IOException e) {
                Log.d("exception",e.toString());
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            if(result==1){
                super.onPostExecute(result);
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                // your code here
                            }
                        },
                        3000
                );
                showProgress(false);
                if(intentStr.equals("intentfromcreateacc")){
                    session.createLoginUID(uid);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 5);
                    long when = calendar.getTimeInMillis();
                    Intent myIntent = new Intent(PhoneVerificationActivity.this , AutoLogout.class);
                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    int uniqueID = (int)when;
                    PendingIntent pi = PendingIntent.getBroadcast(PhoneVerificationActivity.this, uniqueID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC, when, pi);
                }
                showDialog();
                //startActivity(intentShowOption);
            }else{
                showProgress(false);
                codeTextInput.setError("Wrong verification code");
            }

        }
    }
}
