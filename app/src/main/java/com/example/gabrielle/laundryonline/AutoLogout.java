package com.example.gabrielle.laundryonline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by gabrielle on 8/28/2016.
 */
public class AutoLogout extends BroadcastReceiver {
    private Intent intentGet;
    private int NOTIF_ID = 1;
    private SessionManager session;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("autologout","start");
        session= new SessionManager(context);
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        session.logoutUser();
    }
}
