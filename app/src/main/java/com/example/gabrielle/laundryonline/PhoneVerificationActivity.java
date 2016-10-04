package com.example.gabrielle.laundryonline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.AuthResult;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by gabrielle on 6/17/2016.
 */
public class PhoneVerificationActivity extends AppCompatActivity {
    private EditText codeTextInput;
    private Button verifyButton,batalKonfirmasiButton;
    private View mProgressView;
    private View mVerifyCodeView;
    private MyAsyncTask myAsyncTask;
    private String telephoneNumber,uid, intentStr;
    private String code;
    private DatabaseReference mDatabase;
    private Intent intentShowOption;
    private SessionManager session;
    private TextView kirimUlangTV;
    private Context _context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        Intent intent = getIntent();
        session = new SessionManager(getApplicationContext());
        intentShowOption = new Intent(this,ShowOptionActivity.class);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        telephoneNumber = intent.getStringExtra("telephoneNumber");
        uid = intent.getStringExtra("uid");
        intentStr = intent.getStringExtra("intent");
        codeTextInput = (EditText) findViewById(R.id.codeInput);
        kirimUlangTV = (TextView) findViewById(R.id.kirimUlangKode);
        SpannableString ss = new SpannableString("KIRIM ULANG KODE");
        _context = getApplicationContext();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //showDialog("Your request has been sent to your email");
                showProgress(true);
                new ServletPostAsyncTask().execute(new Pair<Context,String>(_context,"Manfred"));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(),0,16,0);
        kirimUlangTV.setText(ss);
        kirimUlangTV.setMovementMethod(LinkMovementMethod.getInstance());
        kirimUlangTV.setHighlightColor(Color.TRANSPARENT);
        verifyButton = (Button) findViewById(R.id.verifyingCodeButton);
        batalKonfirmasiButton = (Button) findViewById(R.id.batalKonfirmasiButton);
        mProgressView = (View) findViewById(R.id.phoneVerificationProgress);
        mVerifyCodeView = (View) findViewById(R.id.verificationCodeLayout);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode();
            }
        });
        batalKonfirmasiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });
        //telephoneNumber = "081221084303";
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mVerifyCodeView.setVisibility(show ? View.GONE : View.VISIBLE);
            mVerifyCodeView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mVerifyCodeView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mVerifyCodeView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void verifyCode() {

        code = codeTextInput.getText().toString();
        //Log.d("code",code);
        View focusView = null;
        boolean cancel = false;
        if (code.length() == 0) {

            focusView = codeTextInput;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            //start background task
            //Log.d("checkerrorbreak", "test2");
            showProgress(true);
            //Log.d("checkerrorbreak", "test3");
            myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute();

        }

    }
    public void showDialog1(){
        TextView msg = new TextView(this);
        String text = "KONFIRMASI BERHASIL \r\n SELAMAT DATANG \r\n TERIMA KASIH TELAH BERGABUNG DENGAN QUICK N' CLEAN LAUNDRY. NIKMATI LAYANAN LAUNDRY BERLANGGAN PROFESIONAL DALAM" +
                "GENGGAMAN TANGAN ANDA.";
        msg.setText(text);
        msg.setPadding(10,10,10,10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(15);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(msg);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(intentShowOption);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showDialog2(final String msg){
        TextView msg2 = new TextView(this);
        msg2.setText(msg);
        msg2.setPadding(10,10,10,10);
        msg2.setGravity(Gravity.CENTER);
        msg2.setTextSize(15);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(msg2);
        alertDialogBuilder
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
                nameValuePairs.put("phone_number",telephoneNumber);
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
                        //Log.d("returnString",returnString);
                        //Log.d("returnMessage",messag);
                    }catch (JSONException jse){

                    }

                    return returnString;
                }else{
                    return "false";
                }
                //return "Error: " + responseCode + " " + connection.getResponseMessage();

            } catch (IOException e) {
                iox = e;
            }


            return "false";

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
            String text;
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
                text = "KODE TELAH DIKIRIM ULANG \r\n KE NOMOR TELEPON ANDA";
                showDialog2(text);
            }else{
                showProgress(false);
                text = "KODE TIDAK VALID";
                showDialog2(text);
            }
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
            StringBuilder result = new StringBuilder();
            try {
                // Set up the request

                //curl -i 'https://api.authy.com/protected/json/phones/verification/check?api_key=KEY&phone_number=111-111-111&country_code=1&verification_code=1234'
                URL url = new URL("https://api.authy.com/protected/json/phones/verification/check?api_key=3qfrirXCVmG1fhegtPCkuftoQiub3YgV&phone_number="+telephoneNumber+"&country_code=62&verification_code="+code);
                //Log.d("checkerrorbreak", "test4");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Log.d("checkerrorbreak", "test5");
                connection.setRequestMethod("GET");
                //Log.d("checkerrorbreak", "test8");
                int responseCode = connection.getResponseCode();
                //Log.d("responsecode", responseCode+"");
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                String returnString;
                String messag;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    //Log.d("resultfromget",result.toString());
                    try{
                        JSONObject myObject = new JSONObject(result.toString());
                        returnString = myObject.getString("success");
                        messag = myObject.getString("message");
                        //Log.d("returnString",returnString);
                        //Log.d("returnMessage",messag);
                        if(returnString.equals("true")){
                            mDatabase.child("users").child(uid).child("verifiedTelNumber").setValue(1);
                            return 1;
                        }else {
                            return 0;
                        }

                    }catch (JSONException jse){

                    }
                    return 1;
                    //response.toString();
                }else{
                    //Log.d("resultfromgeterror",connection.getResponseMessage());
                    return 0;
                }
                //Log.d("checkerrorbreak", "test9");
                //return "Error: " + responseCode + " " + connection.getResponseMessage();
            } catch (IOException e) {
                Log.d("exception",e.toString());

            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            if(result==1){
                super.onPostExecute(result);
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                // your code here
                            }
                        },
                        3000
                );
                showProgress(false);
                if(intentStr.equals("intentfromcreateacc")){
                    session.createLoginUID(uid);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 5);
                    long when = calendar.getTimeInMillis();
                    Intent myIntent = new Intent(PhoneVerificationActivity.this , AutoLogout.class);
                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    int uniqueID = (int)when;
                    PendingIntent pi = PendingIntent.getBroadcast(PhoneVerificationActivity.this, uniqueID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC, when, pi);
                }
                showDialog1();
                //startActivity(intentShowOption);
            }else{
                showProgress(false);
                codeTextInput.setError("Wrong verification code");
            }

        }
    }
}
