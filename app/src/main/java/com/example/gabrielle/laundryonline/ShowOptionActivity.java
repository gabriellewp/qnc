package com.example.gabrielle.laundryonline;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.style.ForegroundColorSpan;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class ShowOptionActivity extends Activity {
    private TextView usernameTextView,dayTextView,dateTextView,monthyearTextView,timeTextView, saldoRemainingTextView;
    private Button mHistoryButton;
    private Button mAntarJemputButton;
    private Button mDropOffPointButton;
    private Button mAccountSettings;

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
    private MaterialCalendarView calendarViewMaterial;
    private Calendar calendar;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_show_option);
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
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        dayTextView.setText(getDay(calendar.get(Calendar.DAY_OF_WEEK)));
        dateTextView.setText("" +calendar.get(Calendar.DATE));
        monthyearTextView.setText(getMonth(calendar.get(Calendar.MONTH))+" "+calendar.get(Calendar.YEAR));


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed i
                    Log.d("usershowoption","usersignin"+user.getUid());
                    uid = user.getUid();
//                    User user1 = mDatabase.;
//                    Log.d("usershowoption","usersigninname"+user_firstname);
                    mDatabase.addValueEventListener((new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                                Log.d("useruid",user.getUid());
//                                if(snapshot.child("users").child(user.getUid()).child("verifiedTelNumber").getValue(String.class).toString()=="0"){
//                                    showDialog();
//                                }
                            initializeCalendar();
                                if(session.getLoginOption()==0){
                                    Log.d("getloginoption",0+"");
                                    //user_firstname = snapshot.child("users").child(user.getUid()).child("firstName").getValue(String.class).toString();
                                    remainingSaldo = snapshot.child("users").child(user.getUid()).child("remainingSaldo").getValue(Integer.class).intValue();
                                    Log.d("remainingsaldo",remainingSaldo+"");
                                    saldoRemainingTextView.setText("JUMLAH SALDO \r\n RP."+remainingSaldo);
                                    session.createLoginUID(user.getUid());
                                }else if(session.getLoginOption()==1){
                                    session.createLoginUID(user.getUid());
                                    remainingSaldo = snapshot.child("users").child(user.getUid()).child("remainingSaldo").getValue(Integer.class).intValue();
                                    saldoRemainingTextView.setText("JUMLAH SALDO \r\n RP."+remainingSaldo);
                                    session.createLoginUID(user.getUid());
                                }else if(session.getLoginOption()==2){
                                    session.createLoginUID(user.getUid());
                                    remainingSaldo = snapshot.child("users").child(user.getUid()).child("remainingSaldo").getValue(Integer.class).intValue();
                                    Log.d("remainingSaldogsi",remainingSaldo+"");
                                    saldoRemainingTextView.setText("JUMLAH SALDO \r\n RP."+remainingSaldo);
                                    session.createLoginUID(user.getUid());
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
        //calendarView = (CalendarView)findViewById(R.id.calendarview);
        calendarViewMaterial = (MaterialCalendarView) findViewById(R.id.calendarView);
        final ArrayList<CalendarDay> list = new ArrayList<CalendarDay>();

        mDatabase.child("laundryOrders").orderByChild("username_id").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String orderDate;
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                    String delims = "[/]";
                    //int year, month, date;
                    orderDate = postSnapshot.child("takenDate").getValue(String.class).toString();
                    Date date;
                    Calendar cal = Calendar.getInstance();
                    try{
                       date = df.parse(orderDate);
                        Log.d("yearmonthdate",date.toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        cal.setTime(sdf.parse(orderDate));// all done
                    }catch(Exception e){

                    }

//                    Log.d("orderdate",orderDate);
//                    String[] tokens = orderDate.split(delims);
//                    year = Integer.parseInt(tokens[0]);
//                    month = Integer.parseInt(tokens[1])-1;
//                    date = Integer.parseInt(tokens[2]);



                    Log.d("calendar",cal.getTime()+"");
                    CalendarDay calendarDay =CalendarDay.from(cal);
                    list.add(calendarDay);
                    System.out.println(list.get(0));
                    Log.d("listofcal",list.toString());
                    calendarViewMaterial.addDecorator(new OrderDecorator(Color.RED, list));
                }
                try{

                }catch (Exception e){

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
   }

    public void viewOrderHistory() {
        //intentHistory.putExtra("user_email", user_email);
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
    class OrderDecorator implements DayViewDecorator {
        private int mColor;
        private HashSet<CalendarDay> mCalendarDayCollection;

        public OrderDecorator(int color, Collection<CalendarDay> dates) {
            mColor = color;
            mCalendarDayCollection = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return mCalendarDayCollection.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.calendar_circle_decorator));

        }
    }
}