package wdttg.wheredidthetimego;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;


public class SliderActivity extends FragmentActivity implements ButtonChooser.OnFragmentInteractionListener,
        SliderChooser.OnFragmentInteractionListener{
    private ToggleButton modeToggleButton;
    private TextView timespanLabel;
    private String mode = "Button";
    private Fragment buttonChooser;
    private Fragment sliderChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        // Return here if we are restoring to avoid overlapping fragments.
        if (savedInstanceState != null){
            return;
        }

        // Get the current time range and update the time span label.
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int oldhour = minute < 30 ? hour - 1 : hour;
        int oldminute = minute < 30 ? minute + 30 : minute - 30;
        String am = hour < 12 ? "am" : "pm";
        String oldam = oldhour < 12 ? "am" : "pm";
        hour = am == "am" ? hour : hour - 12;
        oldhour = oldhour < 1 ? oldhour + 12 : oldhour;
        hour = hour < 1 ? hour + 12 : hour;
        oldhour = oldam == "am" ? oldhour : oldhour - 12;
        timespanLabel = (TextView) findViewById(R.id.timespan_label);
        timespanLabel.setText("From " + oldhour + ":" + oldminute + am +
                " to " + hour + ":" + minute + oldam + " I was");

        // Create a slider chooser fragment.
        sliderChooser = new SliderChooser();

        // Create a button chooser fragment, passing any intents that may be needed.
        buttonChooser = new ButtonChooser();
        buttonChooser.setArguments(getIntent().getExtras());

        // Get the default input type from preferences.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String inputMode = prefs.getString("input_mode", "Button" /* default value */);
        if (inputMode == "Button"){
            // Add the button chooser to the fragment container.
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, buttonChooser).commit();
        } else {
            // Add the slider chooser to the fragment container.
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, sliderChooser).commit();
        }

        // Register listener for mode toggle button.  It should switch between the discrete button
        // chooser and the slider chooser.
        modeToggleButton = (ToggleButton)findViewById(R.id.modeToggleButton);
        modeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace the contents of the fragment container.
                if (mode == "Button"){
                    transaction.replace(R.id.fragmentContainer, sliderChooser);
                } else {
                    transaction.replace(R.id.fragmentContainer, buttonChooser);
                }
                transaction.commit();

                // Update the mode.
                mode = b ? "Button" : "Slider";
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.slider, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Setup handlers for when the user has made a choice of productivity value.  For the button
    // fragment, 100 - productive, 50 - somewhat, and 0 - unproductive.
    @Override
    public void onButtonFragmentInteraction(int prodValue) {
        // Save prodValue for the current time period.
        // ...

        // Go back.
        onBackPressed();
    }

    @Override
    public void onSliderFragmentInteraction(int prodValue) {
        // Save prodValue for the current time period.
        // ...

        // Go back.
        onBackPressed();
    }
}
