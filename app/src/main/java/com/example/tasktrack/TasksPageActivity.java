package com.example.tasktrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;


public class TasksPageActivity extends AppCompatActivity {
    private Button logOut, newTask;
    private RecyclerView cardView = null;
    private DataAdapter adapter;
    private ArrayList<String> tasks = null;

    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_page);

        logOut = findViewById(R.id.logOutBtn);
        newTask = findViewById(R.id.newTaskBtn);

        pageTitle = findViewById(R.id.pageTitle);

        cardView = findViewById(R.id.cardView);
        cardView.setLayoutManager(new LinearLayoutManager(this));
        cardView.setItemAnimator(new DefaultItemAnimator());

        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            //String name = getUserName();
            if (tasks == null){
            tasks = DBUtilities.getTasks(this);
            }
            adapter = new DataAdapter(this, R.layout.row_layout, tasks);
            cardView.setAdapter(adapter);


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

    @Override
    protected void onResume() {
        super.onResume();
        // make the adapter and set it to recycleView
        adapter = new DataAdapter(this, R.layout.row_layout, tasks);
        cardView.setAdapter(adapter);
        System.out.println("recvd tasks " + tasks);
    }

}