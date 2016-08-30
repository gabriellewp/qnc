package com.example.gabrielle.laundryonline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.gabrielle.laundryonline.db.LaundryOrder;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class OrderHistoryActivity extends AppCompatActivity{
    private List<LaundryOrder> orderListPast = new ArrayList<>();
    private List<LaundryOrder> orderListPresent = new ArrayList<>();
    private List<LaundryOrder> orderListFuture = new ArrayList<>();
   // private List<LaundryOrder> orderList = new ArrayList<>();
    private RecyclerView rvpast,rvpresent,rvfuture;
    private HistoryAdapter mAdapterPast, mAdapterPresent, mAdapterFuture;
    private String mUserEmail, mUser_name, laundryOrderDate;
    private Button createOrderButton;
    private SessionManager sessionmgr;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private View mProgressView, mUIView;
    private Intent intentToDetailOrder, intentAddOrder;
    private Calendar calendar;
    private Date datenow,dateLaundryOrder;
    private DateFormat dateFormat;
    private LinearLayout warningLayout;

    private ScrollView orderListScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        Intent intent = getIntent();
        sessionmgr = new SessionManager(getApplicationContext());
        intentToDetailOrder = new Intent(this, ShowOrderDetailActivity.class);
        //warningLayout = (LinearLayout) findViewById(R.id.warningLayout);
        //orderListScrollView = (Line) findViewById(R.id.orderListScrollView);
        //createOrderButton = (Button) findViewById(R.id.buatPesananButton);
        intentAddOrder = new Intent(this, ShowAllAddressActivity.class);
//

        //mAuth.signOut();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgressView = findViewById(R.id.showOrderProgress);
        mUIView = findViewById(R.id.ui);
        rvpast = (RecyclerView)findViewById(R.id.rvpast);
        rvpresent = (RecyclerView)findViewById(R.id.rvpresent);
        rvfuture = (RecyclerView)findViewById(R.id.rvfuture);

        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        calendar = Calendar.getInstance();

        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),0,0,0);
        calendar.clear(Calendar.MILLISECOND);
        datenow = calendar.getTime();
        //calendar = DateUtils.truncate(calendar, Calendar.DATE);
        Log.d("datenowincreate",datenow.toString());

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

    }
    public void setLayout(){
        Log.d("orderlistPast12:",orderListPast.size()+"");
        Log.d("orderlistPresent12:",orderListPresent.size()+"");
        Log.d("orderlistFuture12:",orderListFuture.size()+"");
        mAdapterPast = new HistoryAdapter(orderListPast,0,this);
        mAdapterPresent = new HistoryAdapter(orderListPresent,1,this);
        mAdapterFuture = new HistoryAdapter(orderListFuture,2,this);



//
//        final LinearLayoutManager llmfuture = new LinearLayoutManager(this){
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
        final LinearLayoutManager llmfuture = new LinearLayoutManager(this);
        llmfuture.setOrientation(LinearLayoutManager.VERTICAL);
//        final LinearLayoutManager llmpresent = new LinearLayoutManager(this){
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
        final LinearLayoutManager llmpresent = new LinearLayoutManager(this);
        llmpresent.setOrientation(LinearLayoutManager.VERTICAL);
//        final LinearLayoutManager llmpast = new LinearLayoutManager(this) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
        final LinearLayoutManager llmpast = new LinearLayoutManager(this);
        llmpast.setOrientation(LinearLayoutManager.VERTICAL);

        rvpast.setLayoutManager(llmpast);
        rvpast.setItemAnimator(new DefaultItemAnimator());
        rvpast.setAdapter(mAdapterPast);
        rvpast.addItemDecoration(new DividerItemLine(this, LinearLayoutManager.VERTICAL));
        rvpast.addOnItemTouchListener(new RecyclerTouchListener(this, rvpast, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                LaundryOrder lo = orderListPast.get(position);
//                intentToDetailOrder.putExtra("orderID",lo.getOrderID());
//                intentToDetailOrder.putExtra("timeRange",0);
//                startActivity(intentToDetailOrder);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        rvpresent.setLayoutManager(llmpresent);
        rvpresent.setItemAnimator(new DefaultItemAnimator());
        rvpresent.setAdapter(mAdapterPresent);
        rvpresent.addItemDecoration(new DividerItemLine(this, LinearLayoutManager.VERTICAL));
        rvpresent.addOnItemTouchListener(new RecyclerTouchListener(this, rvpresent, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                LaundryOrder lo = orderListPresent.get(position);
//                intentToDetailOrder.putExtra("orderID",lo.getOrderID());
//                intentToDetailOrder.putExtra("timeRange",1);
//                startActivity(intentToDetailOrder);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        rvfuture.setLayoutManager(llmfuture);
        rvfuture.setItemAnimator(new DefaultItemAnimator());
        rvfuture.setAdapter(mAdapterFuture);
        rvfuture.addItemDecoration(new DividerItemLine(this, LinearLayoutManager.VERTICAL));
        rvfuture.addOnItemTouchListener(new RecyclerTouchListener(this, rvfuture, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                LaundryOrder lo = orderListFuture.get(position);
//                intentToDetailOrder.putExtra("orderID",lo.getOrderID());
//                intentToDetailOrder.putExtra("timeRange",2);
//                startActivity(intentToDetailOrder);
            }

                                    @Override
                                            public void onLongClick(View view, int position) {

            }
        }));
        Log.d("mAdapaterFuturet",mAdapterFuture.getItemCount()+"");
        Log.d("mAdapterPresent",mAdapterPresent.getItemCount()+"");
        Log.d("mAdapaterPast",mAdapterPast.getItemCount()+"");
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUIView.setVisibility(show ? View.GONE : View.VISIBLE);
            //Log.d("checkerrorbreak", "test8");
            mUIView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUIView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            // Log.d("checkerrorbreak", "test9"+shortAnimTime);
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

    public void addOrder(View v){
        Intent intentAddOrder = new Intent();
        intentAddOrder.putExtra("user_email",mUserEmail);

    }
    private void getOrderData(){

        //Query queryRef = mDatabase.orderByChild("laudryOrders/username_email").equalTo(mUserEmail);
        Log.d("orderhistoryuseruid",sessionmgr.getUidPreferences());
        mDatabase.child("laundryOrders").orderByChild("username_id").equalTo(sessionmgr.getUidPreferences()).addValueEventListener((new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("orderhistorystrig",snapshot.getValue().toString());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.d("orderhistorystrigds",postSnapshot.getValue().toString());
                    LaundryOrder lo = postSnapshot.getValue(LaundryOrder.class);
                    lo.setOrderID(postSnapshot.getKey());
                    laundryOrderDate = lo.getTakenDate();

                    try{
                        dateLaundryOrder = dateFormat.parse(laundryOrderDate);
                        Log.d("datenow",datenow.toString());
                        Log.d("datelaundryorder",dateLaundryOrder.toString());
                        //Calendar nowCal = Calendar.getInstance();
                        //nowCal.set(dateLaundryOrder);
                        if(dateLaundryOrder.after(datenow)){
                            Log.d("addfuture","test1");
                            orderListFuture.add(lo);
                        }else if(dateLaundryOrder.equals(datenow)){
                            Log.d("addpresent","test2");
                            orderListPresent.add(lo);
                        }else if(dateLaundryOrder.before(datenow)){
                            Log.d("addpast","test3");
                            orderListPast.add(lo);
                        }
                    }catch (Exception e){

                    }

                    //orderList.add(lo);
                    Log.d("idoflaundryorder",postSnapshot.getKey());

                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        }));




        //mAdapter.notifyDataSetChanged();

    }
    public void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to confirm order?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public interface ClickListener{
        void onClick (View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private OrderHistoryActivity.ClickListener clickListener;
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OrderHistoryActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
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
                getOrderData();
                Thread.sleep(7000);
            }catch(InterruptedException e){
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result==1){
                setLayout();
                Log.d("resultgetorderdata","1");
                if(orderListPast.size()==0&&orderListPresent.size()==0&&orderListFuture.size()==0){
                    Log.d("nohistoryatall","'");
                    //orderListScrollView.setVisibility(View.GONE);
                    //warningLayout.setVisibility(View.VISIBLE);
                }else{
                    Log.d("setlayout","anyhistory");

                }
            }

            showProgress(false);

        }
    }
}
