package wdttg.wheredidthetimego.history;

import android.content.Context;
import android.util.Log;

/**
 * Hi! Use me to interact with adding and removing logs. I'll be filled in to actually interact
 * with the rest of Matthew's code later.
 *
 * Created by matthewrasmussen on 11/12/14.
 */
public class Logger {

    static Boolean isLogging = false;

    public interface LogFillingCallback {
        void callback(Context context, Double productivity);
    }

    /**
     * Implement me if you want to be the one responsible for filling out logs.
     */
    public interface LogFillingSubscriber {
        void fillLog(long start, long end, LogFillingCallback callback);
    }

    public static boolean isLogging(Context context) {
        return isLogging;
    }

    public static void startLogging(Context context) {
        Log.d("hi", "Started logging");
        isLogging = true;
        NotificationGenerator.startLogging(context);
    }

    public static void stopLogging(Context context) {
        Log.d("hi", "Stopped logging");
        isLogging = false;
        NotificationGenerator.stopLogging(context);
    }

    private static LogFillingSubscriber subscriber = null;

    /**
     * Please only register one subscriber. This is powered by pure singleton jank.
     * @param sub
     */
    public static void registerSubscriber(Context context, LogFillingSubscriber sub) {
        Log.d("hi", "Registered subscriber");
        subscriber = sub;
    }

    public static LogFillingSubscriber getSubscriber() {
        return subscriber;
    }


}
