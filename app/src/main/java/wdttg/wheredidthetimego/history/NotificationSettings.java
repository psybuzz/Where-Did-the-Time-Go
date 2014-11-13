package wdttg.wheredidthetimego.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * Created by matthewrasmussen on 11/12/14.
 */
public class NotificationSettings {

    public enum NotificationInputType {
        Buttons,
        Slider
    }

    public enum NotificationSound {
        Default,
        None
    }

    public enum NotificationBuzzPattern {
        SingleLong,
        SingleShort,
        DoubleLong,
        DoubleShort
    }


    // The time period between notifications
    Long timePeriod;

    // The input type used for the in-app notification
    NotificationInputType defaultInputType;

    // The percentage productive to use when setting how productive the user has been
    Double kindaProductivePercent;

    // The sound to play when a notification appears
    NotificationSound sound;

    // The vibrations to play when a notification appears
    NotificationBuzzPattern buzzes;

    // The color of the LED
    Integer ledRGB;

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("WDTTGPreferenceKey", Context.MODE_PRIVATE);
    }

    public static NotificationSettings getSettings(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);

        NotificationSettings toReturn = new NotificationSettings();

        toReturn.timePeriod = preferences.getLong("timePeriod", 15L*60*1000);
        toReturn.defaultInputType = NotificationInputType
                .valueOf(preferences.getString("defaultInputType", "Buttons"));
        toReturn.kindaProductivePercent = (double)
                preferences.getFloat("kindaProductivePercent", 0.5f);
        toReturn.sound = NotificationSound
                .valueOf(preferences.getString("sound", "Default"));
        toReturn.buzzes = NotificationBuzzPattern
                .valueOf(preferences.getString("buzzes", "SingleLong"));
        toReturn.ledRGB = preferences.getInt("ledRGB", 0);

        return toReturn;
    }

    public static void setSettings(NotificationSettings settings, Context context) {

        SharedPreferences preferences = getSharedPreferences(context);
        Editor editor = preferences.edit();

        if (settings.timePeriod != null) {
            editor.putLong("timePeriod", settings.timePeriod);
        }

        if (settings.defaultInputType != null) {
            editor.putString("defaultInputType", settings.defaultInputType.name());
        }

        if (settings.kindaProductivePercent != null) {
            editor.putFloat("kindaProductivePercent", settings.kindaProductivePercent.floatValue());
        }

        if (settings.sound != null) {
            editor.putString("sound", settings.sound.name());
        }

        if (settings.buzzes != null) {
            editor.putString("buzzes", settings.buzzes.name());
        }

        if (settings.ledRGB != null) {
            editor.putInt("ledRGB", settings.ledRGB);
        }


        editor.commit();
    }


}
