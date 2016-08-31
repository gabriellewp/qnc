package com.example.gabrielle.laundryonline;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gabrielle.laundryonline.db.LaundryOrder;
import com.example.gabrielle.laundryonline.db.UserAddressDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by gabrielle on 7/23/2016.
 */
public class ConfirmScheduleActivity extends AppCompatActivity {
    private Intent intentDateActivity,intentToShowOption;
    private HashMap<String, String> hashMap;
    private TextView labelTV, completeTV, detailTV,date1TV,jam1TV,date2TV, jam2TV;
    private EditText noteED;
    private String noteEDStr;
    private ImageButton prevButton, nextButton;
    private LinearLayout progressBarLayout;
    private SessionManager session;
    private DatabaseReference mDatabase;
    private View progressBar;
    private String dateTime1Str,dateTime2Str, hour1, hour2;
    private int year1,month1,date1,year2,month2,date2,uniqueID;
    private Calendar calDate1Done, calDate2Done;
    private String date1Done, date2Done;
    private SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_confirm_schedule);
        Log.d("confirmschedule","test1");
        intentDateActivity = getIntent();
        session= new SessionManager(getApplicationContext());
        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        intentToShowOption = new Intent(this, ShowOptionActivity.class);
        hashMap = (HashMap<String, String>)intentDateActivity.getSerializableExtra("map");
        Log.v("HashMapTest", hashMap.get("label")); //label,address,detail
        labelTV = (TextView) findViewById(R.id.labelAddress);
        completeTV = (TextView) findViewById(R.id.completeAddress);
        detailTV = (TextView) findViewById(R.id.detailAddress);
        date1TV = (TextView) findViewById(R.id.jadwal1Date);
        jam1TV = (TextView) findViewById(R.id.jadwal1Jam);
        date2TV = (TextView) findViewById(R.id.jadwal2Date);
        jam2TV = (TextView) findViewById(R.id.jadwal2Jam);
        noteED = (EditText) findViewById(R.id.specialInstruction);
        prevButton = (ImageButton) findViewById(R.id.prev_button);
        progressBar = findViewById(R.id.schedule_progress);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        nextButton = (ImageButton) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEDStr = noteED.getText().toString();
                submitSchedule();
                //rvCalendar1.getLayoutManager().findscrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
            }
        });
        labelTV.setText(hashMap.get("label"));
        completeTV.setText(hashMap.get("address"));
        detailTV.setText(hashMap.get("detail"));
        hour1 = intentDateActivity.getStringExtra("hour1");
        hour2 =  intentDateActivity.getStringExtra("hour2");
        date1TV.setText(intentDateActivity.getStringExtra("date1"));
        jam1TV.setText(hour1);
        date2TV.setText(intentDateActivity.getStringExtra("date2"));
        jam2TV.setText(hour2);
        dateTime1Str = intentDateActivity.getStringExtra("dateTime1");
        dateTime2Str = intentDateActivity.getStringExtra("dateTime2");
        try{
            calDate1Done = Calendar.getInstance();
            calDate1Done.setTime(sdf.parse(dateTime1Str));
            calDate1Done.add(Calendar.DATE, 3);
            date1Done = sdf.format(calDate1Done.getTime());

            calDate2Done = Calendar.getInstance();
            calDate2Done.setTime(sdf.parse(dateTime2Str));
            calDate2Done.add(Calendar.DATE, 3);
            date2Done = sdf.format(calDate2Done.getTime());
        }catch (Exception e){

        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("confirmschedule","test2");
    }
    public void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Your new schedule is saved")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(intentToShowOption);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void submitSchedule(){
        progressBar.setVisibility(View.VISIBLE);
        LaundryOrder lo1 = new LaundryOrder();
        lo1.setAddressLabel(intentDateActivity.getStringExtra("label"));
        lo1.setNote(noteEDStr);
        lo1.setOrderID("");
        lo1.setOrderStatus(1);
        lo1.setPackage_id(0);
        lo1.setPaymentStatus(1);
        lo1.setPrice(5000);
        lo1.setReturnDate(date1Done);
        lo1.setReturnTime(Integer.parseInt(hour1));
        lo1.setTakenDate(dateTime1Str);
        lo1.setTakenTime(Integer.parseInt(hour1));
        lo1.setUsername_id(session.getUidPreferences());
        lo1.setWeight(10);
        lo1.setRating("0.0");

        LaundryOrder lo2 = new LaundryOrder();
        lo2.setAddressLabel(intentDateActivity.getStringExtra("label"));
        lo2.setNote(noteEDStr);
        lo2.setOrderID("");
        lo2.setOrderStatus(1);
        lo2.setPackage_id(0);
        lo2.setPaymentStatus(1);
        lo2.setPrice(7000);
        lo2.setReturnDate(date2Done);
        lo2.setReturnTime(Integer.parseInt(hour2));
        lo2.setTakenDate(dateTime2Str);
        lo2.setTakenTime(Integer.parseInt(hour2));
        lo2.setUsername_id(session.getUidPreferences());
        lo2.setWeight(11);
        lo2.setRating("0.0");

        mDatabase.child("laundryOrders").push().setValue(lo1);
        mDatabase.child("laundryOrders").push().setValue(lo2);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // your code here
                    }
                },
                5000
        );
        Calendar calendaralarm = Calendar.getInstance();

        long when;
        try{
            calendaralarm.setTime(sdf.parse(dateTime1Str));// all done
            //calendaralarm.set(year1,month1,date1,Integer.parseInt(hour1),0,0);
            Log.d("calendaralarmdate1",calendaralarm.getTime()+"");
            when  = calendaralarm.getTimeInMillis();
            Intent myIntent = new Intent(this , Receiver.class);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            uniqueID = (int)when;
            PendingIntent pi = PendingIntent.getBroadcast(this, uniqueID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, when, pi);

            calendaralarm.setTime(sdf.parse(dateTime1Str));// all done
            Log.d("calendaralarmdate2",calendaralarm.getTime()+"");
            when  = calendaralarm.getTimeInMillis();
            myIntent = new Intent(this , Receiver.class);
            alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            uniqueID = (int)when;
            pi = PendingIntent.getBroadcast(this, uniqueID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, when, pi);

        }catch (Exception e){}


        Log.d("calendaralarmdate",calendaralarm.getTime()+"");

        progressBar.setVisibility(View.GONE);
        showDialog();
    }

}
