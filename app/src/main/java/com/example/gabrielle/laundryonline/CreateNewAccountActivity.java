package com.example.gabrielle.laundryonline;

/**
 * Created by gabrielle on 5/28/2016.
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gabrielle.laundryonline.db.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CreateNewAccountActivity extends AppCompatActivity {
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mFrontNameView;
    private EditText mFamilyNameView;
    private EditText mTelephoneNumberView;
    private View mCreateAccountForm;
    private View mProgressView;
    private Button mNewAccountButton;
    private User newUser ;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private boolean createSuccess;
    private String uid;
    private String email;
    private String password;
    private String firstName;
    private String familyName;
    private String telephonenumber;
    private String address1;
    private String address2;
    private int remainingWeight; //kg
    private int verifiedUser; //0 = false or 1 =true
    private String joinDate;
    private Calendar calendar;
    private Intent intentPhoneVerificationActivity;
    private MailSender ms;
    private Context _context;
    private boolean isUserSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        Intent intent = getIntent();
        isUserSignIn = false;
        intentPhoneVerificationActivity = new Intent(this,PhoneVerificationActivity.class);
        ms = new MailSender("qnclaundry@gmail.com","cepatbersih");
        calendar= Calendar.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        _context = getApplicationContext();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("authstatelistenercreate" ,"onAuthStateChanged:signed_in:" + user.getUid());
                    uid = user.getUid();
                    isUserSignIn = true;
                } else {
                    // User is signed out
                    Log.d("authstatelistenercreate", "onAuthStateChanged:signed_out");
                    isUserSignIn = false;
                }

                // ...
            }
        };

        newUser = new User();
        mEmailView = (EditText) findViewById(R.id.newEmail);
        mPasswordView = (EditText) findViewById(R.id.newPassword);
        mFrontNameView = (EditText) findViewById(R.id.newFrontName);
        mFamilyNameView = (EditText)findViewById(R.id.newFamilyName) ;
        mTelephoneNumberView = (EditText) findViewById(R.id.newTelephoneNumber);
        Button mNewAccountButton = (Button) findViewById(R.id.create_new_account_button);
        mNewAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
        mCreateAccountForm = findViewById(R.id.input_user_detail_form);
        mProgressView = findViewById(R.id.login_progress);
        createSuccess=false;
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createNewAccount(){
        boolean cancel = false;
        View focusView = null;
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFrontNameView.setError(null);
        mFamilyNameView.setError(null);
        mTelephoneNumberView.setError(null);
        if(isUserSignIn==false){
            password = mPasswordView.getText().toString();
            firstName = mFrontNameView.getText().toString();
            familyName = mFamilyNameView.getText().toString();
            email = mEmailView.getText().toString();
            telephonenumber = mTelephoneNumberView.getText().toString();
            if(email.length()==0||!isEmailValid(email) ){
                mEmailView.setError("Email is not valid");
                focusView=mEmailView;
                cancel=true;
            }
            if(!isPasswordValid(password) ){
                mPasswordView.setError("Must contain more than 6 character");
                focusView=mPasswordView;
                cancel=true;
            }
            if(firstName.length()==0 ){
                mFrontNameView.setError(getString(R.string.error_empty_name));
                focusView=mFrontNameView;
                cancel=true;
            }
            if(familyName.length()==0){
                focusView=mFamilyNameView;
                familyName = "";
                cancel=true;
            }

            if(telephonenumber.length()==0 || !isPhoneNumberValid(telephonenumber) ){
                mTelephoneNumberView.setError("Must contain digit 0-9");
                focusView = mTelephoneNumberView;
                cancel=true;
            }
        }else{
            mEmailView.setText(email);
            mPasswordView.setText(password);
            mFrontNameView.setText(firstName);
            mFamilyNameView.setText(familyName);
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            //start background task
            showProgress(true);
            //code verifikasi nomor telpon, JANGAN DIAPUS
            if(isUserSignIn==false){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(CreateNewAccountActivity.this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                createSuccess = true;
                                User newUser = new User();
                                newUser.setUsername_ID(uid);
                                newUser.setEmail(email);
                                newUser.setPassword(password);
                                newUser.setTelNumber(telephonenumber);
                                newUser.setFirstName(firstName);
                                newUser.setFamilyName(familyName);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(calendar.getTime());
                                newUser.setJoinDate(formattedDate);
                                mDatabase.child("users").child(uid).setValue(newUser);
                                intentPhoneVerificationActivity.putExtra("telephoneNumber",telephonenumber);
                                intentPhoneVerificationActivity.putExtra("uid",uid);
                                isUserSignIn = true;
                                new ServletPostAsyncTask().execute(new Pair<Context,String>(_context,"Manfred"));
                            }
                        })
                        .addOnCompleteListener(CreateNewAccountActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("userauth", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    mEmailView.setError("Email already exist");
                                }else{
                                    createSuccess = true;
                                }
                            }
                        });
            }else{

                mDatabase.child("users").child(uid).child("telNumber").setValue(telephonenumber);
            }

        }

    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >5;
    }
    private boolean isPhoneNumberValid(String phoneNumber){
        String regex = "\\d+";
        if(phoneNumber.matches(regex)){
            return true;
        }else{
            return false;
        }
    }
    public void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Phone number can't be created")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCreateAccountForm.setVisibility(show ? View.GONE : View.VISIBLE);

            mCreateAccountForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateAccountForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mCreateAccountForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    class ServletPostAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
        private Context context;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            Exception ex;
            IOException iox;

                //dbhandler manajemen
                try {

                    String body = "Hi, "+firstName+","+"\r\n"+"You've registered at QnCLaundry. "+"\r\n"+"Follow the link below to confirm your email address: "+"\r\n"+"https://qncbackend.appspot.com/verifying?param="+uid;
                    ms.sendMail("Please complete your QnCLaundry signup.", body,
                            "qnclaundry@gmail.com", email);
                    Log.d("sendingemaildone","");
                } catch (Exception e) {
                    ex = e;
                    Log.e("SendMail", e.getMessage(), e);
                }
                try {
                    // Set up the request
                    //URL url = new URL("http://android-app-backend.appspot.com/hello");
                    URL url = new URL("https://api.authy.com/protected/json/phones/verification/start?api_key=3qfrirXCVmG1fhegtPCkuftoQiub3YgV");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    // Build name data request params
                    Map<String, String> nameValuePairs = new HashMap<>();
                    nameValuePairs.put("api_key", "3qfrirXCVmG1fhegtPCkuftoQiub3YgV");
                    nameValuePairs.put("via","sms");
                    nameValuePairs.put("country_code","62");
                    nameValuePairs.put("phone_number",telephonenumber);
                    nameValuePairs.put("locale","en");

                    String postParams = buildPostDataString(nameValuePairs);
                    // Execute HTTP Post
                    OutputStream outputStream = connection.getOutputStream();
                    Log.d("doinbackground","servlet6");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.d("doinbackground","servlet7");
                    writer.write(postParams);
                    writer.flush();
                    writer.close();
                    outputStream.close();
                    connection.connect();
                    Log.d("doinbackground","servlet8");
                    // Read response
                    int responseCode = connection.getResponseCode();
                    StringBuilder response = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    String returnString="";
                    String messag = "";

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        try{
                            JSONObject myObject = new JSONObject(response.toString());
                            returnString = myObject.getString("success");
                            messag = myObject.getString("message");
                            Log.d("returnString",returnString);
                            Log.d("returnMessage",messag);
                        }catch (JSONException jse){

                        }

                        return returnString;
                    }
                    //return "Error: " + responseCode + " " + connection.getResponseMessage();
                    return "false";
                } catch (IOException e) {
                    iox = e;
                }


            return "true";

        }

        private String buildPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            //api key authy: 3qfrirXCVmG1fhegtPCkuftoQiub3YgV
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            //starting phoneactivity
            if(result.equals("true")){
                Log.d("resultcreatenewaccount",result);
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                // your code here
                            }
                        },
                        4000
                );
                showProgress(false);
                //FirebaseAuth.getInstance().signOut();
                intentPhoneVerificationActivity.putExtra("intent","intentfromcreateacc");
                startActivity(intentPhoneVerificationActivity);
                Log.d("responsefrompost",result);
            }else{
                showProgress(false);
                showDialog();
                mTelephoneNumberView.setError("Please insert other number");
            }


        }
    }

}