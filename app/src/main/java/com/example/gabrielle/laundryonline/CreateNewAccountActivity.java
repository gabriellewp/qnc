package com.example.gabrielle.laundryonline;

/**
 * Created by gabrielle on 5/28/2016.
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gabrielle.laundryonline.db.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class CreateNewAccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private EditText mEmailView,mPasswordView,mFrontNameView,mFamilyNameView,mTelephoneNumberView;
    private View mCreateAccountForm, mProgressView;
    private Button mNewAccountButton, mBatalButton;
    private User newUser ;
    private SessionManager session;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private boolean createSuccess;
    private String uid,email, password, firstName, familyName, telephonenumber,joinDate;
    private Calendar calendar;
    private Intent intentPhoneVerificationActivity, intentGetToken, intentShowOption;
    private MailSender ms;
    private Context _context;
    private boolean isUserSignIn;
    private TextView perjanjianTV;
    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseUser user;
    private boolean isCreatebySocialMedia;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_create_new_account);
        Intent intent = getIntent();

        session = new SessionManager(getApplicationContext());
        AppEventsLogger.activateApp(this);
        isCreatebySocialMedia = false;
        isUserSignIn = false;
        intentPhoneVerificationActivity = new Intent(this,PhoneVerificationActivity.class);
        intentGetToken = new Intent(this, GetPhoneTokenActivity.class);
        ms = new MailSender("qnclaundry@gmail.com","cepatbersih");
        calendar= Calendar.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        _context = getApplicationContext();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Log.d("authstatelistenercreate" ,"onAuthStateChanged:signed_in:" + user.getUid());
                    uid = user.getUid();
                    isUserSignIn = true;
                    if(isCreatebySocialMedia){
                        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Log.d("lgoinwithfb","test1");
                                if(!dataSnapshot.exists()){ //login with fb and google
                                    //Log.d("nochildwithfacebookid",user.getUid());
                                    User newUser = new User();
                                    newUser.setUsername_ID(user.getUid());
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                    String formattedDate = df.format(calendar.getTime());
                                    newUser.setJoinDate(formattedDate);
                                    //newUser.setEmail(user.getEmail());

                                    mDatabase.child("users").child(user.getUid()).setValue(newUser);
                                    intentGetToken.putExtra("telNumber","");
                                    intentGetToken.putExtra("uid",user.getUid());
                                    session.createLoginUID(user.getUid());
                                    showProgress(false);
                                    startActivity(intentGetToken);
                                    try{
                                        Thread.sleep(7000);
                                    }catch(Exception e) {

                                    }
                                }else{
                                    //Log.d("childwiththisfacebookid","");
                                    intentShowOption = new Intent(CreateNewAccountActivity.this,ShowOptionActivity.class);

                                    mDatabase.child("users").child(user.getUid()).addValueEventListener((new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            User userSnapshot = snapshot.getValue(User.class);

                                            if(userSnapshot.getVerifiedTelNumber()==0){

                                                try{
                                                    Thread.sleep(2000);
                                                }catch(Exception e) {

                                                }
                                                //Log.d("telNumberusersnapshot","");
                                                intentGetToken.putExtra("telNumber","");
                                                intentGetToken.putExtra("uid",user.getUid());

                                                showProgress(false);
                                                startActivity(intentGetToken);
                                            }else if(userSnapshot.getVerifiedTelNumber()==1){
                                                session.createLoginUID(user.getUid());
                                                //tambahin alarm manager untuk auto logout
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.add(Calendar.DATE, 5);
                                                long when = calendar.getTimeInMillis();
                                                //Log.d("logoutday",calendar.getTime()+"");
                                                Intent myIntent = new Intent(CreateNewAccountActivity.this , AutoLogout.class);
                                                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                                                int uniqueID = (int)when;
                                                PendingIntent pi = PendingIntent.getBroadcast(CreateNewAccountActivity.this, uniqueID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC, when, pi);
                                                showProgress(false);
                                                startActivity(intentShowOption);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError firebaseError) {
                                            System.out.println("The read failed: " + firebaseError.getMessage());
                                        }
                                    }));
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    // User is signed out
                    //Log.d("authstatelistenercreate", "onAuthStateChanged:signed_out");
                    isUserSignIn = false;
                }
            }
        };

        newUser = new User();
        mEmailView = (EditText) findViewById(R.id.newEmail);
        mPasswordView = (EditText) findViewById(R.id.newPassword);
        mFrontNameView = (EditText) findViewById(R.id.newFrontName);
        mFamilyNameView = (EditText)findViewById(R.id.newFamilyName) ;
        mTelephoneNumberView = (EditText) findViewById(R.id.newTelephoneNumber);

        SpannableString ss = new SpannableString("DENGAN MENYETUJUI, ANDA MENYETUJUI SYARAT & KETENTUAN SERTA  \r\n KEBIJAKAN PRIVASI YANG DITETAPKAN OLEH QNC LAUNDRY");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(CreateNewAccountActivity.this, ShowAgreementActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 35, 81, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(),35,81,0);
        perjanjianTV = (TextView) findViewById(R.id.perjanjian);
        perjanjianTV.setText(ss);
        perjanjianTV.setMovementMethod(LinkMovementMethod.getInstance());
        perjanjianTV.setHighlightColor(Color.TRANSPARENT);

        mNewAccountButton = (Button) findViewById(R.id.create_new_account_button);
        mNewAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
        mBatalButton = (Button) findViewById(R.id.bataldaftar);
        mBatalButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToLandingPage = new Intent(CreateNewAccountActivity.this,LandingPageActivity.class);
                startActivity(backToLandingPage);
            }
        });
        mCreateAccountForm = findViewById(R.id.textfield_user_detail_form);
        mProgressView = findViewById(R.id.login_progress);

        //facebook login button init
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookLoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        //Log.d("facebookatemptlogin","success");
                        session.createLoginOption(1);
                        isCreatebySocialMedia = true;
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        //Log.d("facebookatemptlogin","cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        //Log.d("facebookatemptlogin","fail"+exception.toString());

                    }
                });
        ImageButton fb = (ImageButton) findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginButton.performClick();
            }
        });

        //google button init
        //google sign_in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CreateNewAccountActivity.this.getResources().getString(R.string.server_client_id))
                .requestEmail()
                .build();
//
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addScope(gso.getScopeArray()[0])
                .addScope(gso.getScopeArray()[1])
                .addScope(gso.getScopeArray()[2])
                .build();
        SignInButton googleLoginButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        ImageButton googlesigninIB = (ImageButton)findViewById(R.id.googlesignin);
        //Log.d("scopearray",gso.getScopeArray().length+"");
        googlesigninIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("googlelogin","test1");
                session.createLoginOption(2);
                googleSignIn();
            }
        });

        googleLoginButton.setSize(SignInButton.SIZE_STANDARD);
        googleLoginButton.setScopes(gso.getScopeArray());
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("googlelogin","test1");
                session.createLoginOption(2);
                googleSignIn();
            }
        });
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
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        //Log.d("onConnectionFailed:" ,connectionResult.toString());
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
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
                                //Log.d("userauth", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    showProgress(false);
                                    mEmailView.setError("Tidak bisa buat akun baru");
                                }else{
                                    createSuccess = true;
                                }
                            }
                        }).addOnFailureListener(CreateNewAccountActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgress(false);
                        mEmailView.setError("Akun sudah ada");
                    }
                });
            }else{
                //auth with username and password
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
                    String link  = "<a href=\\\"WWW.google.es\\\">ACTIVAR CUENTA</a>";
                    String body = "Hi, "+firstName+","+"\r\n"+"You've registered at QnCLaundry. "+"\r\n"+"Click the link below to confirm your email address: "+"\r\n"+"https://qncbackend.appspot.com/verifying?param="+uid;

                    ms.sendMail("Please complete your QnCLaundry signup.", body,
                            "qnclaundry@gmail.com", email);
                    //Log.d("sendingemaildone","");
                } catch (Exception e) {
                    ex = e;
                    //Log.e("SendMail", e.getMessage(), e);
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
                    //Log.d("doinbackground","servlet6");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    //Log.d("doinbackground","servlet7");
                    writer.write(postParams);
                    writer.flush();
                    writer.close();
                    outputStream.close();
                    connection.connect();
                    //Log.d("doinbackground","servlet8");
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
                            //Log.d("returnString",returnString);
                            //Log.d("returnMessage",messag);
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
                //Log.d("resultcreatenewaccount",result);
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
                //Log.d("responsefrompost",result);
            }else{
                showProgress(false);
                showDialog();
                mTelephoneNumberView.setError("Please insert other number");
            }


        }
    }
    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d("handleFacebooktoken",token.getToken()+"");
        // [START_EXCLUDE silent]
        showProgress(true);
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(CreateNewAccountActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        isCreatebySocialMedia = true;

                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d("fbsignin:onComplete:", task.isSuccessful()+"");

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Log.w("fbsigninCredentialfail", task.getException());

                        }else{
                            //showProgress(false);
                            //startActivity(intentShowOrderOption);
                        }

// [START_EXCLUDE]

// [END_EXCLUDE]
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("googlesignin","onactivityresult");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //Log.d("googlesignin","onactivityresultrcsignin");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            //Log.d("statuscodegooglesignin",statusCode+"");
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                //Log.d("googlesignin","success");
                GoogleSignInAccount account = result.getSignInAccount();
                // mFullName = acct.getDisplayName();
                // mEmail = acct.getEmail();
                isCreatebySocialMedia = true;
                firebaseAuthWithGoogle(account);
            } else {
                //Log.d("googlesignin","failed");
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                GoogleSignInAccount account = result.getSignInAccount();
                //Log.d("googlesignin","acc.email:"+account.getEmail());
                showProgress(false);

                //updateUI(null);
                // [END_EXCLUDE]
            }
        }else {
            /* Otherwise, it's probably the request by the Facebook login button, keep track of the session */
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d( "firebaseAuthWithGoogle:" ,acct.getId());
        // [START_EXCLUDE silent]
        showProgress(true);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(CreateNewAccountActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        isCreatebySocialMedia = true;

                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d("signInWithCredential:onComplete:" ,task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("signInWithCredential", task.getException());
                            showProgress(false);
                            showDialog("Authentication failed");
                        }else{
                            //session.createLoginOption(2);

                            //startActivity(intentShowOrderOption);
                        }
                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    public void googleSignIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    public void showDialog(String warning){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(warning)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}