package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

    //User Data
    private User user;
    private ArrayList<TaskObj> todaysTasks;

    //Layout
    private ImageView sideDrawerImg;
    private TextView sideNavName;
    private Button logOut;
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

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        sideNav = findViewById(R.id.sideNavigation);
        logOut = sideNav.findViewById(R.id.logOutBtn);
        sideNavHeader = sideNav.getHeaderView(0);
        sideNavName = sideNavHeader.findViewById(R.id.sideNavName);
        sideDrawerImg = findViewById(R.id.side_drawer_img);

        sideNav.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(sideNav, navController);



        //Get db instance Check logged in
        mAuth = FirebaseAuth.getInstance();
        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(LandingPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LandingPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            user = getUserData(this);
            getTodaysTasks(this);

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

            //Manual "OnClicks" For Side Menu
            //Pass Data Between Fragments... bundles, transactions
            sideNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Bundle bundle = new Bundle();
                    if (user != null) {
                        if (todaysTasks != null) {
                            bundle.putSerializable("user", user);
                            bundle.putSerializable("todaysTasks", todaysTasks);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction sendData = fragmentManager.beginTransaction();
                            System.out.println("======="+item.getItemId());
                            System.out.println("======="+R.id.sideNavHome);

                            switch (item.getItemId()) {
                                case R.id.sideNavHome:
                                    HomeFragment homeFragment = new HomeFragment();
                                    homeFragment.setArguments(bundle);
                                    sendData.replace(R.id.navHostFragment, homeFragment).commit();
                                    break;
                                case R.id.sideNavProfile:
                                    ProfileFragment profileFragment = new ProfileFragment();
                                    profileFragment.setArguments(bundle);
                                    sendData.replace(R.id.navHostFragment, profileFragment).commit();
                                    break;
                                case R.id.sideNavTags:
                                    TagsFragment tagsFragment = new TagsFragment();
                                    tagsFragment.setArguments(bundle);
                                    sendData.replace(R.id.navHostFragment, tagsFragment).commit();
                                    break;
                            }
                        }
                    }
                    //Menu Cleanup... Update "checked" item, close drawer
                    for (int i=0; i<sideNav.getMenu().size();i++ ){
                        sideNav.getMenu().getItem(i).setChecked(false);
                    }
                    item.setChecked(true);
                    drawerLayout.close();
                    return false;
                }
            });
        }

    }

    @Override
    protected void onResume() {
        //Put user data in bundle
        //Start home fragment with bundle
        super.onResume();
        System.out.println("===========RESUMING=============");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction sendData = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        if (user != null){
            bundle.putSerializable("user", user);
            String name = user.getName();
            sideNavName.setText(name);
        }
        if (todaysTasks.size() != 0){
            bundle.putSerializable("todaysTasks", todaysTasks);
        }
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        sendData.replace(R.id.navHostFragment, homeFragment).commit();
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
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                System.out.println("getting result");
                                String dob = (document.getString("dob"));
                                String email = (document.getString("email"));
                                String gender = (document.getString("gender"));
                                String name = (document.getString("name"));
                                String bio = (document.getString("bio"));
                                user = new User(email, name, dob, gender, bio);
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