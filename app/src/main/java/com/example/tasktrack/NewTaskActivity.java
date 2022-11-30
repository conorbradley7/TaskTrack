package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewTaskActivity extends AppCompatActivity {
    EditText taskTitle;
    Button addTaskBtn;
    private FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);


        taskTitle = findViewById(R.id.taskTitle);
        addTaskBtn = findViewById(R.id.addTaskBtn);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = taskTitle.getText().toString().trim();
                createTask(title);
            }
        });
    }

    private void createTask(String title){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (TextUtils.isEmpty(title)) {
            taskTitle.setError("Please Enter A Name For The Task!");
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(NewTaskActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewTaskActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else{

        Map<String,Object> task = new HashMap<>();
        task.put("title", title);
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewTaskActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewTaskActivity.this, TasksPageActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewTaskActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }
}