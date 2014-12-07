package wdttg.wheredidthetimego;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import wdttg.wheredidthetimego.history.LogEntry;
import wdttg.wheredidthetimego.history.LogRepository;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SecondTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecondTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SecondTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondTabFragment newInstance(String param1, String param2) {
        SecondTabFragment fragment = new SecondTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SecondTabFragment() {
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
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second_tab, container, false);

        // get the listview
        expListView = (ExpandableListView) v.findViewById(R.id.logList);

        // preparing list data
        prepareListData(v);

        listAdapter = new ExpandableListAdapter(expListView.getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        Log.d("wow:", "Setup!");
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Log.d("wow:", "On group click!");
                return false;
            }
        });
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                Log.d("wow:", "On group expand!");
            }
        });
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                Log.d("wow:", "On group collapse!");
            }
        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                Log.d("wow:", "On Child click!");
                return false;
            }
        });

        return v;
    }

    private void prepareListData(View v) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        final long MILLISECONDS_PER_DAY = 86400000;
        final long MILLISECONDS_PER_HALF_HOUR = 1800000;

        LogRepository repository = new LogRepository(v.getContext());
        List<LogEntry> entries;
        int numEntries;

        // Adding child data
        long day = System.currentTimeMillis();
        for (int i = 0; i < 30; i++) {
            Date currDate = new Date(day);
            String date = new SimpleDateFormat("EEE, MMM d yyyy").format(currDate);
            listDataHeader.add(date);

            // Get the appropriate data within the time range.
            entries = repository.getEntriesBetween(day - MILLISECONDS_PER_DAY, day);
            numEntries = entries.size();

            List<String> logs = new ArrayList<String>();
            if (numEntries > 0){
                try{
                    float avgProductivity = repository.getAverageProductivityBetween(day - MILLISECONDS_PER_DAY, day);
                    logs.add("The average productivity was " + String.valueOf(avgProductivity) + "%");
                    for (int j=0; j<numEntries; j++){
                        logs.add(entries.get(j).getProductivity().toString());
                    }
                } catch (Exception e){
                    logs.add("Strange...I wasn't able to read your saved logs for this date.");
                }
            } else {
                logs.add("Sorry, no productivity entries were found for this date.");
            }

            listDataChild.put(listDataHeader.get(i), logs);

            // Check the previous date.
            day -= MILLISECONDS_PER_DAY;
        }


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
