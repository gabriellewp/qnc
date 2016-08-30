package com.example.gabrielle.laundryonline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.facebook.FacebookSdk;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //private DBHandler dbHandler;

    // UI references.
    private EditText mEmailView, mPasswordView;
    private TextView forgetPasswordTV;
    private View mProgressView;
    private View mLoginFormView;
    private Intent intentFromLandingPage, intentGetToken, intentForgetPassword;
    private Intent intentShowOrderOption;
    private SessionManager session;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private boolean firebaseLogin;
    private String email, telNumber;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 9001;
    private LoginButton facebookLoginButton;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private FirebaseUser user;
    private List<User> arrOfUser= new ArrayList<>();
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        // Set up the login form.
        session = new SessionManager(getApplicationContext());
        AppEventsLogger.activateApp(this);
        intentFromLandingPage = getIntent();
        calendar = Calendar.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseLogin = false;
        intentGetToken = new Intent(this,GetPhoneTokenActivity.class);
        intentForgetPassword = new Intent(this, ForgetPasswordActivity.class);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("usersignin","preparecreatenweacc");
                    mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("lgoinwithfb","test1");
                            if(!dataSnapshot.exists()){ //login with fb and google
                                Log.d("nochildwithfacebookid",user.getUid());
                                User newUser = new User();
                                newUser.setUsername_ID(user.getUid());
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(calendar.getTime());
                                newUser.setJoinDate(formattedDate);
                                //newUser.setEmail(user.getEmail());

                                mDatabase.child("users").child(user.getUid()).setValue(newUser);
                                try{
                                    Thread.sleep(7000);
                                }catch(Exception e) {

                                }
                            }else{
                                Log.d("childwiththisfacebookid","");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.d("authstatelistener" ,"onAuthStateChanged:signed_in:" + user.getUid());
                    mDatabase.child("users").child(user.getUid()).addValueEventListener((new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            User userSnapshot = snapshot.getValue(User.class);

                            if(userSnapshot.getVerifiedTelNumber()==0){
                                telNumber = userSnapshot.getTelNumber();
                                try{
                                    Thread.sleep(2000);
                                }catch(Exception e) {

                                }
                                Log.d("telNumberusersnapshot",telNumber);
                                intentGetToken.putExtra("telNumber",telNumber);
                                intentGetToken.putExtra("uid",user.getUid());

                                showProgress(false);
                                startActivity(intentGetToken);
                            }else if(userSnapshot.getVerifiedTelNumber()==1){
                                session.createLoginUID(user.getUid());
                                //tambahin alarm manager untuk auto logout
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DATE, 5);
                                long when = calendar.getTimeInMillis();
                                Log.d("logoutday",calendar.getTime()+"");
                                Intent myIntent = new Intent(LoginActivity.this , AutoLogout.class);
                                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                                int uniqueID = (int)when;
                                PendingIntent pi = PendingIntent.getBroadcast(LoginActivity.this, uniqueID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.set(AlarmManager.RTC, when, pi);

                                showProgress(false);
                                startActivity(intentShowOrderOption);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    }));

                } else {
                    // User is signed out
                    Log.d("authstatelistener123", "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };
        Button mBackButton = (Button) findViewById(R.id.batal_login_email_button);
        mBackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent backToLandingPage = new Intent(view.getContext(),LandingPageActivity.class);
                startActivity(backToLandingPage);
            }
        });
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.createLoginOption(0);
                attemptLogin();
            }
        });
        forgetPasswordTV = (TextView) findViewById(R.id.forgetpassword);
        SpannableString ss = new SpannableString(getResources().getString(R.string.forget_password));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //showDialog("Your request has been sent to your email");
                startActivity(intentForgetPassword);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgetPasswordTV.setText(ss);
        forgetPasswordTV.setMovementMethod(LinkMovementMethod.getInstance());
        forgetPasswordTV.setHighlightColor(Color.TRANSPARENT);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //init intent

        intentShowOrderOption = new Intent(this,ShowOptionActivity.class);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookLoginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("facebookatemptlogin","success");
                        session.createLoginOption(1);

                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("facebookatemptlogin","cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("facebookatemptlogin","fail"+exception.toString());
                    }
                });
        ImageButton fb = (ImageButton) findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginButton.performClick();
            }
        });

        //google sign_in
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(LoginActivity.this.getResources().getString(R.string.server_client_id))
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
        Log.d("scopearray",gso.getScopeArray().length+"");
        googlesigninIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("googlelogin","test1");
                session.createLoginOption(2);
                googleSignIn();
            }
        });

        googleLoginButton.setSize(SignInButton.SIZE_STANDARD);
        googleLoginButton.setScopes(gso.getScopeArray());
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("googlelogin","test1");
                session.createLoginOption(2);
                googleSignIn();
            }
        });

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
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (email.length()==0) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (email.length()!=0&&!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (password.length()!=0 && !isPasswordValid(password)) {
            mPasswordView.setError("Password too short");
            focusView = mPasswordView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuth.signInWithEmailAndPassword(email, password)

                    .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                        }
                    })
                    .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showProgress(false);
                            mPasswordView.setError("Email and Password don't match");
                            mPasswordView.requestFocus();
                        }
                    });

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("onConnectionFailed:" ,connectionResult.toString());
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("googlesignin","onactivityresult");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d("googlesignin","onactivityresultrcsignin");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.d("statuscodegooglesignin",statusCode+"");
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Log.d("googlesignin","success");
                GoogleSignInAccount account = result.getSignInAccount();
               // mFullName = acct.getDisplayName();
               // mEmail = acct.getEmail();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d("googlesignin","failed");
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
        Log.d( "firebaseAuthWithGoogle:" ,acct.getId());
        // [START_EXCLUDE silent]
        showProgress(true);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {



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
    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("handleFacebooktoken",token.getToken()+"");
        // [START_EXCLUDE silent]
        showProgress(true);
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {


                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("fbsignin:onComplete:", task.isSuccessful()+"");

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("fbsigninCredentialfail", task.getException());

                        }else{
                            //showProgress(false);
                            //startActivity(intentShowOrderOption);
                        }

// [START_EXCLUDE]

// [END_EXCLUDE]
}
        });
        }

        }
