package com.example.gabrielle.laundryonline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.gabrielle.laundryonline.db.LaundryOrder;
import com.example.gabrielle.laundryonline.db.UserAddressDetails;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabrielle on 7/23/2016.
 */
public class ShowOrderDetailActivity extends AppCompatActivity {
    private Intent intentFromOrderHistory;
    private TextView idPesananTV, statusTV, tglpenjemputanTV, wktpenjemputanTV, alamatTV, noteTV,
    tanggalKirimTV, waktukirimTV,beratTV, hargaTV, jumlahpakaianTV, detailPakaianTV, kualitaslayananTV;
    private int timeRange;
    private String orderId,rating,review;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout outerLayout;
    private View mUIView, mProgressView;
    private float numRating;
    private LaundryOrder lo;
    private List<LaundryOrder> orderList = new ArrayList<>();
    private RatingBar ratingBarOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        lo = new LaundryOrder();
        intentFromOrderHistory = getIntent();
        timeRange = intentFromOrderHistory.getIntExtra("timeRange",0);
        orderId = intentFromOrderHistory.getStringExtra("orderID");
        mUIView = findViewById(R.id.uiOrderDetail);
        mProgressView = findViewById(R.id.detail_progress);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
        outerLayout = (LinearLayout) findViewById(R.id.detailOrderLayout);
        if(timeRange==0){
            outerLayout.setBackgroundColor(Color.parseColor("#9ddc8b"));
        }
        idPesananTV = (TextView)findViewById(R.id.idpesanan);
        statusTV = (TextView)findViewById(R.id.status);
        tglpenjemputanTV = (TextView)findViewById(R.id.tglpenjemputan);
        wktpenjemputanTV = (TextView)findViewById(R.id.wktpenjemputan);
        alamatTV = (TextView)findViewById(R.id.alamat);
        noteTV = (TextView)findViewById(R.id.instruksikhusus);
        tanggalKirimTV = (TextView)findViewById(R.id.tglpengiriman);
        waktukirimTV = (TextView)findViewById(R.id.wktpengiriman);
        beratTV = (TextView)findViewById(R.id.berat);
        hargaTV = (TextView)findViewById(R.id.harga);
        jumlahpakaianTV = (TextView)findViewById(R.id.jumlahpakaian);
        detailPakaianTV = (TextView)findViewById(R.id.detailpakaian);
        kualitaslayananTV = (TextView)findViewById(R.id.kualitaslayanan);
        ratingBarOrder = (RatingBar) findViewById(R.id.ratingBar1);


        idPesananTV.setText(orderId);

        Log.d("takendate1",lo.getTakenDate());

        Log.d("orderdetail","test1");

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUIView.setVisibility(show ? View.GONE : View.VISIBLE);

            mUIView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUIView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mUIView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void getData(){

        mDatabase.child("laundryOrders").child(orderId).addValueEventListener((new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("gotomdatabasevaluelist","");
                //Log.d("orderhistoryobject",snapshot.child("laundryOrders").getValue().toString());
                Log.d("orderhistorystrigxxx",snapshot.getValue().toString());
                LaundryOrder dbLo = snapshot.getValue(LaundryOrder.class);
                //orderList.add(dbLo);
                rating = snapshot.child("rating").getValue().toString();
                review = snapshot.child("review").getValue().toString();
                lo.setTakenDate(dbLo.getTakenDate());
                lo.setAddressLabel(dbLo.getAddressLabel());
                lo.setOrderStatus(dbLo.getOrderStatus());
                lo.setWeight(dbLo.getWeight());
                lo.setNote(dbLo.getNote());
                lo.setPrice(dbLo.getPrice());
                lo.setPackage_id(dbLo.getPackage_id());
                lo.setPaymentStatus(dbLo.getPaymentStatus());
                lo.setReturnDate(dbLo.getReturnDate());
                lo.setReturnTime(dbLo.getReturnTime());
                lo.setTakenDate(dbLo.getTakenDate());
                lo.setTakenTime(dbLo.getTakenTime());
                lo.setRating(dbLo.getRating());
                lo.setReview(dbLo.getReview());

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        }));

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

            try{
                getData();
                Thread.sleep(3000);
            }catch(InterruptedException e){
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //Log.d("orderlist3:",orderList.size()+"");
            Log.d("orderstatus1",lo.getOrderStatus()+"");
            if(lo.getOrderStatus()==0){
                statusTV.setText("complete");
            }else{
                statusTV.setText("not complete");
            }
            tglpenjemputanTV.setText(lo.getTakenDate());
            wktpenjemputanTV.setText(String.valueOf(lo.getTakenTime()));
            Log.d("addresslabel1",lo.getAddressLabel());
            alamatTV.setText(lo.getAddressLabel());
            noteTV.setText(lo.getNote());
            tanggalKirimTV.setText(lo.getReturnDate());
            waktukirimTV.setText(String.valueOf(lo.getReturnTime()));
            beratTV.setText(String.valueOf(lo.getWeight()));
            hargaTV.setText(String.valueOf(lo.getPrice()));
            jumlahpakaianTV.setText("");
            detailPakaianTV.setText("");
            kualitaslayananTV.setText("");
            //Log.d("ratingdrdb",lo.getRating());
            numRating = Float.parseFloat(rating);
            Log.d("test2",numRating+"");
            ratingBarOrder.setRating(numRating);


            showProgress(false);


        }
    }


}
