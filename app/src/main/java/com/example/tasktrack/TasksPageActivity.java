package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class TasksPageActivity extends AppCompatActivity {
    private Button logOut, newTask;
    private RecyclerView recycleView = null;
    private DataAdapter adapter;
    private ArrayList<String> tasks = null;

    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

    private TextView pageTitle;

//==================================================================================================
    // OnCreate
    // => Check Logged In
    // => Get Task Data
    // => Button Listeners
//==================================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tasks_page);

        logOut = findViewById(R.id.logOutBtn);
        newTask = findViewById(R.id.newTaskBtn);

        pageTitle = findViewById(R.id.pageTitle);

        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setItemAnimator(new DefaultItemAnimator());

        mAuth = FirebaseAuth.getInstance();

        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
//          String name = getUserName();
            tasks = getTasks(this);

            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBUtilities.signOut();
                    Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            newTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TasksPageActivity.this, NewTaskActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


//==================================================================================================
    // Getting & Displaying Tasks
    // => Get tasks from firebase
    // => OnComplete... Invoke onResume to populate the views with data
//==================================================================================================

    public ArrayList<String> getTasks(Context context) {
        db = FirebaseFirestore.getInstance();
        System.out.println("==========" + mAuth.getCurrentUser().getUid());
        ArrayList<String> tasks = new ArrayList<String>();

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tasks.add(document.getString("title"));
                                System.out.println("first");
                                onResume();
                            }
                        }
                    }
                });
        return tasks;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // make the adapter and set it to recycleView from received tasks
        System.out.println("===================>"+tasks);
        adapter = new DataAdapter(this, R.layout.row_layout, tasks);
        recycleView.setAdapter(adapter);
    }

}



