package wdttg.wheredidthetimego.history;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Date;


/**
 * Created by matthewrasmussen on 11/12/14.
 */
public class NotificationGenerator extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {



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

    private static void setPeriodEnd(Context context, long end) {
        Editor editor = timingEditor(context);
        editor.putLong("periodEnd", end);
        editor.commit();
    }

    private static long getPeriodStart(Context context) {
        return timingPreferences(context).getLong("periodStart", 0);
    }

    private static long getPeriodEnd(Context context) {
        return timingPreferences(context).getLong("periodEnd", 0);
    }


    public static void startLogging(Context context) {
        stopLogging(context);
        setPeriodStart(context, new Date().getTime());


    }

    public static void stopLogging(Context context) {

    }


}
