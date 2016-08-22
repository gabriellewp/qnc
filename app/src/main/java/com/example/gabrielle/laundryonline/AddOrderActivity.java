//package com.example.gabrielle.laundryonline;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.annotation.TargetApi;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.TextView;
//
//import com.example.gabrielle.laundryonline.db.LaundryOrder;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
///**
// * Created by gabrielle on 5/28/2016.
// */
//public class AddOrderActivity extends AppCompatActivity  {
//
//    private String mUserEmail;
//    private String mUserFirstName;
//    private EditText mAddress1;
//    private EditText mAddress2;
//    private EditText mPackage;
//    private String mTakenDate;
//    private String mReturnDate;
//    private EditText mPaymentMethod;
//    private DBHandler dbHandler;
//    private View mProgressView;
//    private View mAddOrderView;
//    private Intent intentNewAddressMap;
//    private int packageOptIdx;
//    private int paymentMethodIdx;
//    private int whichAddr;
//    private SessionManager sessionmgr;
//    private DatabaseReference mDatabase;
//    //private Button confirmOrderButton;
//    private AddAntarJemputOrder mOrderJemputTask = null;
//    @Override
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_order);
//        sessionmgr = new SessionManager(getApplicationContext());
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        intentNewAddressMap = new Intent(this,NewAddressMapActivity.class);
//        Intent intent = getIntent();
//        mUserEmail = sessionmgr.getEmailPreferences();
//        Log.d("addorderemail",mUserEmail);
//       // mUserFirstName = intent.getStringExtra("user_firstname");
//        dbHandler = new DBHandler(this);
//        mAddress1 = (EditText)findViewById(R.id.newAddress1);
//        mAddress1.setText(dbHandler.getCompletePickUpAddress(mUserEmail));
//        mAddress1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeAdress(1);
//            }
//        });
//        mAddress2 = (EditText)findViewById(R.id.newAddress2);
//        mAddress2.setText(dbHandler.getCompleteDropAddress(mUserEmail));
//        mAddress2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeAdress(2);
//            }
//        });
//        mTakenDate = "2/05/2016"; //date now
//        mReturnDate = "4/05/2016";
//
//        Button confDateButton = (Button) findViewById(R.id.configdatebutton);
//        confDateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent configDateIntent = new Intent(getApplicationContext(),DateConfigurationActivity.class);
//                startActivity(configDateIntent);
//            }
//        });
//        Button newOrderButton = (Button)findViewById(R.id.confirmorderbutton);
//        newOrderButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                createNewAntarJemputOrder();
//            }
//        });
//
//        mProgressView  = findViewById(R.id.add_order_progress);
//        mAddOrderView = findViewById(R.id.input_order_form);
//        Spinner packageSpinner = (Spinner) findViewById(R.id.packageOption);
//        Spinner paymentSpinner = (Spinner) findViewById(R.id.paymentMethod);
//        //mTakenPlace
//        // Creating adapter for spinner
//        final ArrayAdapter<String> packageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent){
//                View v  = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
//                }
//                return v;
//
//            }
//            @Override
//            public int getCount() {
//                return super.getCount()-1;
//            }
//        };
//        packageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        packageAdapter.add("wash&fold");
//        packageAdapter.add("wash&iron");
//        packageAdapter.add("Select Package");
//        packageSpinner.setAdapter(packageAdapter);
//        packageSpinner.setSelection(packageAdapter.getCount());
//        packageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // On selecting a spinner item
//                String item = parent.getItemAtPosition(position).toString();
//                packageOptIdx = parent.getSelectedItemPosition();
//                if(packageOptIdx!=packageAdapter.getCount()){
//                    showDialog(item);
//                }
//
//                // Showing selected spinner item
//                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//            }
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//            }
//        });
//
//        final ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent){
//                View v  = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
//                }
//                return v;
//
//            }
//            @Override
//            public int getCount() {
//                return super.getCount()-1; // you dont display last item. It is used as hint.
//            }
//        };
//        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        paymentAdapter.add("Veritrans");
//        paymentAdapter.add("Paypal");
//        paymentAdapter.add("Select Payment");
//        paymentSpinner.setAdapter(paymentAdapter);
//        paymentSpinner.setSelection(paymentAdapter.getCount());
//        paymentSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // On selecting a spinner item
//
//                String item = parent.getItemAtPosition(position).toString();
//                paymentMethodIdx = parent.getSelectedItemPosition();
//                if(paymentMethodIdx!=paymentAdapter.getCount()){
//                    showDialog(item);
//                }
//
//                // Showing selected spinner item
//                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//            }
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//            }
//        });
//    }
//
//    public void showDialog(String item){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage("Are you sure want to choose"+item+"?")
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
////                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////
////                        dialog.cancel();
////                    }
////                });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
//
//    public void changeAdress(int addressnumber){
//        intentNewAddressMap.putExtra("user_email",mUserEmail);
//        intentNewAddressMap.putExtra("addressnumber",addressnumber);
//        startActivity(intentNewAddressMap);
//    }
//    public void createNewAntarJemputOrder() {
//        if (mOrderJemputTask != null) {
//            return;
//        }
//        boolean cancel = false;
//        View focusView = null;
//        mAddress1.setError(null);
//        mAddress2.setError(null);
////        mPaymentMethod.setError(null);
////        mPackage.setError(null);
//        String address1 = mAddress1.getText().toString();
//        String address2 = mAddress2.getText().toString();
////        String paymentMethod = mPaymentMethod.getText().toString();
////        String chosenPackage = mPackage.getText().toString();
//        if (TextUtils.isEmpty(address1)) {
//            mAddress1.setError(getString(R.string.error_empty_address1));
//            cancel = true;
//        }
//        if (TextUtils.isEmpty(address2)) {
//            mAddress2.setError(getString(R.string.error_empty_address1));
//            cancel = true;
//        }
//
//        if(cancel){
//            focusView.requestFocus();
//        }else{
//            showProgress(true);
//
//            LaundryOrder lo = new LaundryOrder();
//            lo.setTakenDate(mTakenDate);
//            lo.setReturnDate(mReturnDate);
//            lo.setPackage_id(packageOptIdx);
//            lo.setPayment_method_id(paymentMethodIdx);
//            lo.setLaundryCategory(1);
//            //lo.setUsername_email(sessionmgr.getEmailPreferences());
//            lo.setUsername_id(sessionmgr.getUidPreferences());
//            mDatabase.child("laundryOrders").push().setValue(lo);
//            new java.util.Timer().schedule(
//                    new java.util.TimerTask() {
//                        @Override
//                        public void run() {
//                            // your code here
//                        }
//                    },
//                    5000
//            );
//            showProgress(false);
//            finish();
//            //mOrderJemputTask = new AddAntarJemputOrder(address1,address2,packageOptIdx,paymentMethodIdx,mTakenDate);
//            //mOrderJemputTask.execute((Void)null);
//        }
//    }
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mAddOrderView.setVisibility(show ? View.GONE : View.VISIBLE);
//
//            mAddOrderView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mAddOrderView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mAddOrderView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }
//
//    public class AddAntarJemputOrder extends AsyncTask<Void,Void,Boolean> {
//        private final String mAddress1;
//        private final String mAddress2;
//        private final int mPaymentMethod;
//        private final int mchosenPackage;
//        private final String takenDate;
//
//        AddAntarJemputOrder(String address1, String address2, int chosenPackage, int paymentMethod, String takenDate){
//            mAddress1 = address1;
//            mAddress2  = address2;
//            mchosenPackage = chosenPackage;
//            mPaymentMethod = paymentMethod;
//            this.takenDate = takenDate;
//        }
//        @Override
//        protected Boolean doInBackground(Void... params){
//            try{
//                Thread.sleep(2000);
//            }catch(InterruptedException e){
//                return false;
//            }
////            LaundryOrder lo=new LaundryOrder(mUserEmail, mchosenPackage, mPaymentMethod,0,10,takenDate);
////
////            dbHandler.addLaundryOrder(mUserEmail,lo);
////
////            int order_id = dbHandler.getLaundryOrderId(mUserEmail);
////
////            AntarJemputOrder ajo = new AntarJemputOrder();
////
////            ajo.setLaundryorder_id(order_id);
////            ajo.setPackage_id(mchosenPackage);
////            ajo.setTakenPlace(mAddress1);
////            ajo.setReturnPlace(mAddress2);
////
////            dbHandler.addAntarJemputOrder(order_id,ajo);
//            //Log.d("testaddorder","4");
//            return true;
//        }
//        @Override
//        protected void onPostExecute(final Boolean success){
//            if(success){
//                //Intent intentaddadress = new Intent();
//
//                finish();
//            }
//        }
//        @Override
//        protected void onCancelled(){
//            mOrderJemputTask = null;
//            showProgress(false);
//        }
//    }
//}
