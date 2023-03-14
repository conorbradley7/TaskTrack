package com.example.tasktrack;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Button taskPageBtn;
    private ImageButton statsPageBtn;

    private User user;
    private ArrayList<TaskObj> todaysTasks;

    private TextView welcomeMessage, landingNoTasksMsg, completionPercentage, completionFraction;

    private ProgressBar progressRing;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        taskPageBtn = (Button) view.findViewById(R.id.tasksBtn);
        statsPageBtn = (ImageButton) view.findViewById(R.id.statsBtn);
        welcomeMessage = (TextView) view.findViewById(R.id.welcomeMsg);
        landingNoTasksMsg = (TextView) view.findViewById(R.id.landingNoTasksMsg);
        progressRing = (ProgressBar) view.findViewById(R.id.progressRingView);
        completionFraction = (TextView) view.findViewById(R.id.completionFractionTV);
        completionPercentage = (TextView) view.findViewById(R.id.completionPercentageTV);


        if (this.getArguments() != null) {
            user = (User) this.getArguments().getSerializable("user");
            todaysTasks = (ArrayList<TaskObj>) this.getArguments().getSerializable("todaysTasks");

            if (user != null) {
                welcomeMessage.setText("Welcome Back " + user.getName() + "!");
                taskPageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(getActivity(), TasksPageActivity.class);
                        bundle.putSerializable("user", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                statsPageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(getActivity(), StatsActivity.class);
                        bundle.putSerializable("user", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                if (todaysTasks != null) {
                    if (todaysTasks.size() != 0) {
                        landingNoTasksMsg.setVisibility(View.GONE);
                        progressRing.setVisibility(View.VISIBLE);
                        completionFraction.setVisibility(View.VISIBLE);
                        completionPercentage.setVisibility(View.VISIBLE);

//                        Stats todayStats = new Stats(todaysTasks);
//                        Pie progressPie = AnyChart.pie();
//                        List<DataEntry> todayCompleted = todayStats.getCompleteVsIncompleteData();
//                        progressPie.data(todayCompleted);
//                        progressRing.setChart(progressPie);
                    }
                }
            }
        }
        return view;
    }

}