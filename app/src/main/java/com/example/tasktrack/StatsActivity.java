package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

enum sectionTypes{
    taskCompletion,
    taskTags,
    taskTime,
    taskDifficulty
}

enum graphs{
    completeVsIncompletePie,
    tagsPie,
    difficultyBar,
    onTimePie
}

public class StatsActivity extends AppCompatActivity implements RecycleViewInterface{
    Button homeBtn;
    private ArrayList<TaskObj> tasks;
    private RecyclerView statSections;
    private StatsSectionAdapter adapter;

    //DB
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mAuth = FirebaseAuth.getInstance();

        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(StatsActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent loggedOutIntent = new Intent(StatsActivity.this, LoginActivity.class);
            startActivity(loggedOutIntent);
        } else {
            tasks = getTasks(this);
            homeBtn = findViewById(R.id.homeBtn);
            statSections = findViewById(R.id.stat_sections);

            statSections.setLayoutManager(new LinearLayoutManager(this));
            statSections.setItemAnimator(new DefaultItemAnimator());
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(StatsActivity.this, LandingPageActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


    public ArrayList<TaskObj> getTasks(Context context) {
        db = FirebaseFirestore.getInstance();
        tasks = new ArrayList<>();


        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String taskTitle = (document.getString("title"));
                                String taskMoreDetails = (document.getString("moreDetails"));
                                String taskTag = (document.getString("tag"));
                                String taskDate = (document.getString("date"));
                                String taskExpDur = (document.getString("expDur"));
                                String taskPriority = (document.getString("priority"));
                                Boolean taskStarted = (document.getBoolean("started"));
                                Boolean taskCompleted = (document.getBoolean("completed"));
                                Boolean taskIncomplete = (document.getBoolean("incomplete"));
                                String taskDifficulty = (document.getString("difficulty"));
                                String id = (document.getId());
                                TaskObj taskObj = new TaskObj(id, taskTitle, taskMoreDetails, taskTag, taskDate, taskExpDur, taskPriority, taskStarted, taskCompleted, taskIncomplete, taskDifficulty);
                                tasks.add(taskObj);
                            }
                            onResume();
                        }
                    }
                });
        return tasks;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("===========RESUMING_STATS=============");
        Stats stats = new Stats(tasks);
        ArrayList<sectionTypes> sections = new ArrayList<>();
        sections.add(sectionTypes.taskCompletion);
        sections.add(sectionTypes.taskTags);
        sections.add(sectionTypes.taskDifficulty);
        sections.add(sectionTypes.taskTime);
        System.out.println("HERE");
        adapter = new StatsSectionAdapter(this, R.layout.stat_section, tasks, sections, stats,this);
        statSections.setAdapter(adapter);
    }

    @Override
    public void onTaskItemClick(int position) {
        return;
    }
}