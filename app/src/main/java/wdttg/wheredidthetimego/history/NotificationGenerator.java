package wdttg.wheredidthetimego.history;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;

import wdttg.wheredidthetimego.R;


/**
 * Created by matthewrasmussen on 11/12/14.
 */
public class NotificationGenerator extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("hi", "Received alarm - should trigger notification");

        long periodStart = getPeriodStart(context);
        NotificationSettings settings = NotificationSettings.getSettings(context);

        long periodEnd = periodStart + settings.timePeriod;
        setCurrentLogEntry(context, periodStart, periodEnd);

        String periodDescription = "" + settings.timePeriod/1000/60 + " minutes";

        Intent yesIntent = new Intent("wdttg.wheredidthetimego.yes");
        PendingIntent yesPending = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent noIntent = new Intent("wdttg.wheredidthetimego.no");
        PendingIntent noPending = PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent kindaIntent = new Intent("wdttg.wheredidthetimego.kinda");
        PendingIntent kindaPending = PendingIntent.getBroadcast(context, 0, kindaIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent tappedIntent = new Intent("wdttg.wheredidthetimego.tap");
        PendingIntent tappedPending = PendingIntent.getBroadcast(context, 0, tappedIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Impetus")
                .setContentText("Have you been productive for the last " + periodDescription + "?")
                .setAutoCancel(true)
                .addAction(0, "Yes", yesPending)
                .addAction(0, "Kinda", kindaPending)
                .addAction(0, "No", noPending)
                .setContentIntent(tappedPending)
                ;

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(123, builder.build());


    }

    public static class TapReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("hi", "Someone tapped on the notification itself");

            LogEntry current = getCurrentLogEntry(context);

            Logger.LogFillingCallback callback = new Logger.LogFillingCallback() {
                @Override
                public void callback(Context context, Double productivity) {
                    Log.d("hi", "Completing callback");
                    updateCurrentLogEntry(context, productivity);
                }
            };


            if (Logger.getSubscriber() != null) {
                Logger.getSubscriber().fillLog(current.getStartTime(), current.getEndTime(), callback);
            }

        }
    }

    public static class YesReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hi", "Tapped yes");
            updateCurrentLogEntry(context, 1.0);
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(123);
        }
    }

    public static class KindaReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hi", "Tapped kinda");
            updateCurrentLogEntry(context, NotificationSettings.getSettings(context).kindaProductivePercent);
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(123);
        }
    }

    public static class NoReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hi", "Tapped no");
            updateCurrentLogEntry(context, 0.0);
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(123);
        }
    }


    private static SharedPreferences timingPreferences(Context context) {
        return context.getSharedPreferences("WDTTGTimingPreferences", Context.MODE_PRIVATE);
    }

    private static Editor timingEditor(Context context) {
        return timingPreferences(context).edit();
    }

    private static void setPeriodStart(Context context, long start) {
        Editor editor = timingEditor(context);
        editor.putLong("periodStart", start);
        editor.commit();
    }

    public static LogEntry getCurrentLogEntry(Context context) {
        LogRepository repository = null;
        LogEntry toReturn = null;
        try {
            repository = new LogRepository(context);

            long entryId = timingPreferences(context).getLong("currentEntry", -1);
            toReturn = repository.getLogEntry(entryId);

        }  finally {
            if (repository != null) {
                repository.close();
            }

        return toReturn;
       }
    }

    public static void updateCurrentLogEntry(Context context, Double productivity) {
        LogRepository repository = null;
        try {
            repository = new LogRepository(context);

            long entryId = timingPreferences(context).getLong("currentEntry", -1);
            repository.updateLogEntry(entryId, productivity);

        }  finally {
            if (repository != null) {
                repository.close();
            }
        }
    }

    public static void setCurrentLogEntry(Context context, long begin, long end) {

        LogRepository repository = null;
        try {
            repository = new LogRepository(context);
            LogEntry entry = repository.createLogEntry(begin, end, null);

            Editor editor = timingEditor(context);
            editor.putLong("currentEntry", entry.getId());
            editor.commit();

        }  finally {
            if (repository != null) {
                repository.close();
            }
        }
    }


    private static long getPeriodStart(Context context) {
        return timingPreferences(context).getLong("periodStart", 0);
    }


    /**
     * Begins logging.
     * @param context
     */
    public static void startLogging(Context context) {
        stopLogging(context);
        setPeriodStart(context, new Date().getTime());

        Intent notify =  new Intent(context, NotificationGenerator.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notify, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        long interval = NotificationSettings.getSettings(context).timePeriod /4;

        Log.d("hi", "Started logging in NotificationGenerator - interval: " + interval);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + interval,
                interval, pendingIntent);
    }

    /**
     * Stops logging.
     * @param context
     */
    public static void stopLogging(Context context) {

        Intent notify =  new Intent(context, NotificationGenerator.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notify, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

    }


}
