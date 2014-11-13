package wdttg.wheredidthetimego.history;

import android.content.Context;

/**
 * Hi! Use me to interact with adding and removing logs. I'll be filled in to actually interact
 * with the rest of Matthew's code later.
 *
 * Created by matthewrasmussen on 11/12/14.
 */
public class Logger {

    public interface LogFillingCallback {
        void callback(Context context, float productivity);
    }

    /**
     * Implement me if you want to be the one responsible for filling out logs.
     */
    public interface LogFillingSubscriber {
        void fillLog(long start, long end, LogFillingCallback callback);
    }

    public static boolean isLogging(Context context) {
        return false;
    }

    public static void startLogging(Context context) {

    }

    public static void stopLogging(Context context) {

    }

    /**
     * Please only register one subscriber. This is powered by pure singleton jank.
     * @param subscriber
     */
    public static void registerSubscriber(Context context, LogFillingSubscriber subscriber) {

    }


}
