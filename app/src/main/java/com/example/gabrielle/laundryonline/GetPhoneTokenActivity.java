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
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.vision.barcode.Barcode;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by gabrielle on 8/16/2016.
 */
public class GetPhoneTokenActivity extends AppCompatActivity {
    private EditText phoneNumber;
    private Button verifyButton;
    private Intent intentPhoneVerification;
    private String uid, telNumber;
    private Context _context;
    private View mProgressView, mGetTokenView;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_token);
        Intent intent = getIntent();
        mProgressView = (View) findViewById(R.id.phoneVerificationProgress);
        mGetTokenView = (View) findViewById(R.id.verificationCodeLayout);
        phoneNumber = (EditText)findViewById(R.id.telephoneNumber);
        verifyButton = (Button) findViewById(R.id.verifyingCodeButton);
        intentPhoneVerification = new Intent(this, PhoneVerificationActivity.class);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        telNumber = intent.getStringExtra("telNumber");
        uid = intent.getStringExtra("uid");
        phoneNumber.setText(telNumber);
        _context = getApplicationContext();
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telNumber = phoneNumber.getText().toString();
                if(telNumber.length()!=0){
                    showProgress(true);
                    new ServletPostAsyncTask().execute(new Pair<Context,String>(_context,"Manfred"));
                }else{
                    phoneNumber.setError("Nomor telepon tidak boleh kosong");
                }

            }
        });

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mGetTokenView.setVisibility(show ? View.GONE : View.VISIBLE);
            mGetTokenView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mGetTokenView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mGetTokenView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Get Token Failed")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    class ServletPostAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
        private Context context;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            Exception ex;
            IOException iox;

            try {
                // Set up the request
                //URL url = new URL("http://android-app-backend.appspot.com/hello");
                mDatabase.child("users").child(uid).child("telNumber").setValue(telNumber);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){

                }
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
                nameValuePairs.put("phone_number",telNumber);
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
                String returnString = "";
                String messag="";
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
                intentPhoneVerification.putExtra("uid",uid);
                intentPhoneVerification.putExtra("telephoneNumber", telNumber);
                intentPhoneVerification.putExtra("intent","intentfromgettoken");
                showProgress(false);
                startActivity(intentPhoneVerification);
            }else{
                showProgress(false);
                showDialog();
            }
        }
    }

}

