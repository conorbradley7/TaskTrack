package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LandingPageActivity extends AppCompatActivity {

    private User user;
    private ArrayList<TaskObj> todaysTasks;

    private ImageView sideDrawerImg;

    private TextView welcomeMessage, landingNoTasksMsg, sideNavName;
    private Button taskPageBtn, logOut;
    private ImageButton statsPageBtn;
    private AnyChartView anyChart;

    private NavigationView sideNav;
    private View sideNavHeader;

    //DB
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

//==================================================================================================
    // OnCreate + OnResume
//==================================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        welcomeMessage = findViewById(R.id.welcomeMsg);
        landingNoTasksMsg = findViewById(R.id.landingNoTasksMsg);
        taskPageBtn = findViewById(R.id.tasksBtn);
        statsPageBtn = findViewById(R.id.statsBtn);
        anyChart = findViewById(R.id.any_chart_view);
        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        sideNav = findViewById(R.id.sideNavigation);
        logOut = sideNav.findViewById(R.id.logOutBtn);
        sideNavHeader = sideNav.getHeaderView(0);
        sideNavName = sideNavHeader.findViewById(R.id.sideNavName);
        sideDrawerImg = findViewById(R.id.side_drawer_img);

        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Completed", 8));
        data.add(new ValueDataEntry("Incomplete", 3));
        pie.data(data);
        anyChart.setChart(pie);

        //Get db instance
        mAuth = FirebaseAuth.getInstance();

        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(LandingPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LandingPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            user = getUserData(this);
            getTodaysTasks(this);
            taskPageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();

                    Intent intent = new Intent(LandingPageActivity.this, TasksPageActivity.class);
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            statsPageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LandingPageActivity.this, StatsActivity.class);
                    startActivity(intent);
                }
            });

            sideDrawerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });

            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBUtilities.signOut();
                    Intent intent = new Intent(LandingPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            sideNav.setItemIconTintList(null);
            NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
            NavigationUI.setupWithNavController(sideNav, navController);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("===========RESUMING=============");
        if (user != null){
            String name = user.getName();
            welcomeMessage.setText("Welcome Back " + name + "!");
            sideNavName.setText(name);
        }
        if (todaysTasks.size() != 0){
            landingNoTasksMsg.setVisibility(View.GONE);
            anyChart.setVisibility(View.VISIBLE);
            Stats todayStats = new Stats(todaysTasks);
            Pie progressPie = AnyChart.pie();
            List<DataEntry> todayCompleted = todayStats.getCompleteVsIncompleteData();
            progressPie.data(todayCompleted);
            anyChart.setChart(progressPie);
        }
    }

//==================================================================================================
    // Getting User Data From db
//==================================================================================================
    public User getUserData(Context context){
            System.out.println("getting usr_data");
            db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                System.out.println("getting result");
                                String dob = (document.getString("dob"));
                                String email = (document.getString("email"));
                                String gender = (document.getString("gender"));
                                String name = (document.getString("name"));
                                user = new User(email, name, dob, gender);
                                System.out.println("first");
                                }
                                onResume();
                            }
                        });
            return user;
         }

    public ArrayList<TaskObj> getTodaysTasks(Context context) {
        db = FirebaseFirestore.getInstance();
        todaysTasks = new ArrayList<>();
        String date = CalendarUtils.formattedDate(LocalDate.now());


        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks").whereEqualTo("date", date).get()
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

                                TaskObj taskObj = new TaskObj(id, taskTitle, taskMoreDetails,
                                        taskTag, taskDate, taskExpDur, taskPriority, taskStarted,
                                        taskCompleted, taskIncomplete, taskDifficulty, null,
                                        null, null);
                                todaysTasks.add(taskObj);
                            }
                            onResume();
                        }
                    }
                });
        return todaysTasks;
    }
    }