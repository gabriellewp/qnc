package com.example.gabrielle.laundryonline;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by gabrielle on 8/27/2016.
 */
public class Receiver extends BroadcastReceiver {
    private Intent intentGet;
    private int NOTIF_ID = 1;

    @Override
    public void onReceive(Context context,Intent intent) {
        Log.d("startservice","intentservice");
        NOTIF_ID = (int)System.currentTimeMillis();
        Log.d("startserviceext",NOTIF_ID+"");
        Intent notifIntent = new Intent(context, LandingPageActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,NOTIF_ID, notifIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Jadwal Penjemputan"+NOTIF_ID)
                .setContentText("Bentar lagi dijemput nih laundry")
                .setGroup("laundryonline")
                .setContentIntent(pi);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

// Build the notification and issues it with notification manager.
        notificationManager.notify(NOTIF_ID, mBuilder.build());
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(NOTIF_ID, mBuilder.build());
    }
//    @Override
//    public IBinder onBind(Intent arg0)
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        //Log.i(TAG, "Received start id " + startId + ": " + intent);
//        //Log.e(TAG, "call me redundant BABY!  onStartCommand service");
//        // this service is NOT supposed to execute anything when it is called
//        // because it may be called inumerous times in repetition
//        // all of its action is in the onCreate - so as to force it to happen ONLY once
//        return 1;
//    }
//    @Override
//    public void onCreate()
//    {
//        // TODO Auto-generated method stub
//        super.onCreate();
//
//        Intent intent = new Intent(this, LandingPageActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Jadwal Penjemputan")
//                .setContentText("Bentar lagi dijemput nih laundry")
//                .setContentIntent(pi);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, mBuilder.build());
//    }
}
