package wdttg.wheredidthetimego;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import wdttg.wheredidthetimego.graphics.Graph;
import wdttg.wheredidthetimego.history.LogEntry;
import wdttg.wheredidthetimego.history.LogRepository;
import wdttg.wheredidthetimego.history.Logger;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FirstTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FirstTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Graph pg;
    private ImageButton switchGraphButton;
    private Switch playSwitch;
    private ImageButton clearButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstTabFragment newInstance(String param1, String param2) {
        FirstTabFragment fragment = new FirstTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FirstTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first_tab, container, false);
        FrameLayout fl = (FrameLayout) v.findViewById(R.id.mainframe);
        TextView descriptionText = (TextView) v.findViewById(R.id.productivityText);
        switchGraphButton = (ImageButton) v.findViewById(R.id.switchGraphButton);
        playSwitch = (Switch) v.findViewById(R.id.playSwitch);
        clearButton = (ImageButton) v.findViewById(R.id.clearButton);

        // Retrieve data stored in the log repository.
        LogRepository repository = new LogRepository(v.getContext());
        long now = System.currentTimeMillis();
        long maxTimeRange = 12*60*60*1000;   // Max time range of 12 hours.
        List<LogEntry> entries = repository.getEntriesBetween(now - maxTimeRange, now);
        int numEntries = entries.size();

        long earliestTime = numEntries > 0 ? entries.get(0).getStartTime() : now - maxTimeRange;
        long latestTime = numEntries > 0 ? entries.get(numEntries - 1).getEndTime() : now;

        // Load the data into arrays to be displayed.
        float[] fakeData = new float[numEntries];
        String[] blah = new String[numEntries];

        // TODO - collect entry values into bins when the number of entries is very large.
        DateFormat formatter = new SimpleDateFormat("hh:mm a");
        for (int i=0; i<numEntries; i++){
            LogEntry entry = entries.get(i);
            if (numEntries >= 4 && i%(numEntries / 4) == 0){       // Add 4 time labels along the x-axis.
                Date date = new Date(entry.getStartTime());
                blah[i] = formatter.format(date);
            } else {
                blah[i] = "";
            }

            // Set the productivity if present.  Otherwise, use the default value.
            if (!entry.equals(null) && entry.getProductivity() != null) {
                fakeData[i] = entry.getProductivity().floatValue()*(float)100.0;
            } else {
                fakeData[i] = (float)50.0;
            }
        }
        // Set the x-axis label for the ending time.
        if (numEntries > 1){
            Date date = new Date(entries.get(numEntries - 1).getEndTime());
            blah[numEntries - 1] = formatter.format(date);
        }

        // Display the results, or a special message if no entries exist.
        if (numEntries > 0){
            pg = new Graph(v.getContext(), 2, fakeData, blah);
            fl.addView(pg);

            Log.d("num entries:", String.valueOf(numEntries));
//            float prod = repository.getAverageProductivityBetween(earliestTime, latestTime);
            float sum = 0;
            int numCounted = 0;
            for (LogEntry entry : entries) {
                if (entry.getProductivity() == null) {
                    continue;
                }

                sum += entry.getProductivity();
                numCounted++;
            }

            float prod = sum / numCounted;
            // end modified

            String prodPercent = String.valueOf(Math.round(prod*100));
            String newDescription = "In the last 12 hours, you've been "+
                    prodPercent+
                    "% productive!";
            if (prod > 0.85) newDescription += "  Awesome!";
            if (prod < 0.15) newDescription += "  Get focused!";

            descriptionText.setText(newDescription);
            switchGraphButton.setEnabled(true);
        } else {
            descriptionText.setText("No entries were recorded in the last 12 hours.  Hit the play start button to begin tracking.");
            switchGraphButton.setEnabled(false);
        }

        // Add listener for the play button and switch graph view button.
        switchGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg.changeType();
            }
        });
        playSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                View view = compoundButton.getRootView();
                boolean isLogging = Logger.isLogging(view.getContext());
                if ((isLogging && b == false) || (b && isLogging == false)) {
                    Logger.toggleLogging(view.getContext());
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the log repository when we start a new session.
                LogRepository repository = new LogRepository(view.getContext());
                repository.clearTable();
            }
        });

        // Restore the state of the play switch.
        boolean isLogging = Logger.isLogging(v.getContext());
        if (isLogging){
            playSwitch.setChecked(true);
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
