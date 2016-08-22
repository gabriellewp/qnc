package com.example.gabrielle.laundryonline;

/**
 * Created by gabrielle on 6/7/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInstaller;
import android.util.Log;

public class SessionManager {
    private SharedPreferences sp;
    private Editor editor;
    private Context _context;
    //private INT_MODE = 0;
    private static final String LOGIN_STATUS =  "loginStatus";
    private static final String LOGIN_OPTION = "loginOption";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "firebaseUID";
    public SessionManager(Context _context){
        this._context = _context;
        sp = _context.getSharedPreferences("LaundryOnlinePref",0);
        editor = sp.edit();
        editor.putBoolean(LOGIN_STATUS,false);
    }
    public void createLoginEmail(String email) {
        editor.putBoolean(LOGIN_STATUS,true);
        editor.putString(KEY_EMAIL,email);
        editor.commit();
    }
    public void createLoginUID(String uid){
        editor.putBoolean(LOGIN_STATUS,true);
        editor.putString(KEY_UID,uid);
        editor.commit();
    }
    public void createLoginOption(int loginoption){
        //0 for email auth, 1 for fb, 2 for google
        editor.putInt(LOGIN_OPTION,loginoption);
        editor.commit();
    }

    public  String getEmailPreferences() {

        //SharedPreferences prefs = context.getSharedPreferences("LaundryOnline",	Context.MODE_PRIVATE);
        String position = sp.getString(KEY_EMAIL, null);
        return position;
    }
    public  String getUidPreferences() {

        //SharedPreferences prefs = context.getSharedPreferences("LaundryOnline",	Context.MODE_PRIVATE);
        String position = sp.getString(KEY_UID, null);
        return position;
    }
    public int getLoginOption(){
        int position = sp.getInt(LOGIN_OPTION,0);
        return position;
    }
    public boolean isLogin(){
        if(this.isLoggedIn()){
            return true;
        }else{
            return false;
        }
    }
    public void checkLogin(){
        Log.d("testcheckloggedin","----3");
        if(!this.isLoggedIn()){
            Log.d("testcheckloggedin","----");
            Intent i =  new Intent(_context,LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            Log.d("testcheckloggedin","++++");
            //return false;

        }else if(this.isLoggedIn()){
            Log.d("testcheckloggedin","----1");
            Intent ioption = new Intent(_context,ShowOptionActivity.class);
            ioption.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ioption.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ioption.putExtra("user_email",getEmailPreferences());
            Log.d("testcheckloggedin","----2");
            _context.startActivity(ioption);
        }
    }
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }
    public boolean isLoggedIn(){
        return sp.getBoolean(LOGIN_STATUS,false);
    }
}
