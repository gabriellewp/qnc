package com.example.gabrielle.laundryonline;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.Calendar;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class ShowOptionActivity extends Activity {
    private TextView usernameTextView,dayTextView,dateTextView,monthyearTextView,timeTextView, saldoRemainingTextView;
    private Button mHistoryButton;
    private Button mAntarJemputButton;
    private Button mDropOffPointButton;
    private Button mAccountSettings;
    private Button mLogoutButton;
    private String user_email;
    private String user_firstname;
    private int remainingSaldo;
    private Intent intentHistory;
    private Intent intentAddOrder;
    private Intent intentAccountSettings;
    private Intent intentViewDropoffpoint;
    private SessionManager session;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;
    private CalendarView calendarView;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_show_option);

        usernameTextView = (TextView) findViewById(R.id.username_textview);
        dayTextView = (TextView)findViewById(R.id.dayNextOrderCalendar);
        dateTextView = (TextView)findViewById(R.id.dateNextOrderCalendar);
        monthyearTextView = (TextView) findViewById(R.id.monthYearNextOrderCalendar);
        timeTextView = (TextView) findViewById(R.id.timeNextOrderCalendar);
        saldoRemainingTextView = (TextView) findViewById(R.id.remainingSaldoTextview);

        mHistoryButton = (Button) findViewById(R.id.history_button);
        mAntarJemputButton = (Button) findViewById(R.id.antar_jemput_button);
        mAccountSettings = (Button) findViewById(R.id.settings);
        calendar = Calendar.getInstance();
        session= new SessionManager(getApplicationContext());
        //FirebaseUser user = firebaseAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        initializeCalendar();
        dayTextView.setText(getDay(calendar.get(Calendar.DAY_OF_WEEK)));
        dateTextView.setText("" +calendar.get(Calendar.DATE));
        monthyearTextView.setText(getMonth(calendar.get(Calendar.MONTH))+" "+calendar.get(Calendar.YEAR));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed i
                    Log.d("usershowoption","usersignin"+user.getUid());
//                    User user1 = mDatabase.;
//                    Log.d("usershowoption","usersigninname"+user_firstname);
                    mDatabase.addValueEventListener((new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                                Log.d("useruid",user.getUid());
//                                if(snapshot.child("users").child(user.getUid()).child("verifiedTelNumber").getValue(String.class).toString()=="0"){
//                                    showDialog();
//                                }
                                if(session.getLoginOption()==0){
                                    Log.d("getloginoption",0+"");
                                    //user_email = snapshot.child("users").child(user.getUid()).child("email").getValue(String.class).toString();
                                    user_firstname = snapshot.child("users").child(user.getUid()).child("firstName").getValue(String.class).toString();
                                    usernameTextView.setText("Welcome "+user_firstname+"!");
                                    remainingSaldo = snapshot.child("users").child(user.getUid()).child("remainingSaldo").getValue(Integer.class).intValue();
                                    Log.d("remainingsaldo",remainingSaldo+"");
                                    saldoRemainingTextView.setText("JUMLAH SALDO \r\n RP."+remainingSaldo);
                                    session.createLoginUID(user.getUid());
                                }else if(session.getLoginOption()==1){
                                    user_email = "gaby3@gmail.com";
                                    session.createLoginUID(user.getUid());
                                    usernameTextView.setText("Welcome "+"dummyfb"+"!");
                                    //remainingSaldo = snapshot.child("users").child(user.getUid()).child("remainingSaldo").getValue(String.class).toString();
                                    //saldoRemainingTextView.setText("JUMLAH SALDO \r\n RP."+remainingSaldo);
                                }else if(session.getLoginOption()==2){
                                    user_email = "gaby3@gmail.com";
                                    session.createLoginUID(user.getUid());
                                    usernameTextView.setText("Welcome "+"dummygoogle"+"!");
                                    //remainingSaldo = snapshot.child("users").child(user.getUid()).child("remainingSaldo").getValue(String.class).toString();
                                    //saldoRemainingTextView.setText("JUMLAH SALDO \r\n RP."+remainingSaldo);
                                    //mGoogleApiClient = getApiClient();
                                }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    }));
                } else {
                    // User is signed out
                    Log.d("authstatelistener1234" +
                            "", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

       // mDropOffPointButton = (Button) findViewById(R.id.dropoff_button);
        //mLogoutButton = (Button)findViewById(R.id.logout);

        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOrderHistory();
            }
        });
        mAntarJemputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                antarJemputOrder();
            }
        });
        mAccountSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(intentAccountSettings);
            }
        });
        /*COMMENTING DROPOFFPOINT CODE*/
//        mDropOffPointButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dropOffPointOrder();
//            }
//        });
        /*COMMENTING LOGOUT CODE*/

        intentHistory = new Intent(this, OrderHistoryActivity.class);
        intentAddOrder = new Intent(this, ShowAllAddressActivity.class);
        intentViewDropoffpoint = new Intent(this, DropOffActivity.class);
        intentAccountSettings = new Intent(this,AccountSettingsActivity.class);
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void initializeCalendar(){
        calendarView = (CalendarView)findViewById(R.id.calendarview);
        calendarView.setShowWeekNumber(false);
        // here we set Monday as the first day of the Calendar
        calendarView.setFirstDayOfWeek(2);
        //The background color for the selected week.
        calendarView.setSelectedWeekBackgroundColor(getResources().getColor(R.color.landingpage_background));
        calendarView.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));


    }
    public void viewOrderHistory() {
        intentHistory.putExtra("user_email", user_email);
        startActivity(intentHistory);
    }

    public void antarJemputOrder() {


        startActivity(intentAddOrder);
    }


    public void dropOffPointOrder() {
        startActivity(intentViewDropoffpoint);
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
    private String getDay(int index) {
        switch (index) {
            case Calendar.SUNDAY:
                return "SUN";
            case Calendar.MONDAY:
                return "MON";
            case Calendar.TUESDAY:
                return "TUE";
            case Calendar.WEDNESDAY:
                return "WED";
            case Calendar.THURSDAY:
                return "THUR";
            case Calendar.FRIDAY:
                return "FRI";
            case Calendar.SATURDAY:
                return "SAT";
        }
        return "";
    }

    private String getMonth(int index) {
        switch (index) {
            case Calendar.JANUARY:
                return "JANUARY";
            case Calendar.FEBRUARY:
                return "FEBRUARY";
            case Calendar.MARCH:
                return "MARCH";
            case Calendar.APRIL:
                return "APRIL";
            case Calendar.MAY:
                return "MAY";
            case Calendar.JUNE:
                return "JUNE";
            case Calendar.JULY:
                return "JULY";
            case Calendar.AUGUST:
                return "AUGUST";
            case Calendar.SEPTEMBER:
                return "SEPTEMBER";
            case Calendar.OCTOBER:
                return "OCTOBER";
            case Calendar.NOVEMBER:
                return "NOVEMBER";
            case Calendar.DECEMBER:
                return "DECEMBER";
        }
        return "";
    }

}