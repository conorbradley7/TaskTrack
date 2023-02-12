package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LandingPageActivity extends AppCompatActivity {

    private User user;

    private TextView welcomeMessage;
    private Button taskPageBtn;

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
        taskPageBtn = findViewById(R.id.tasksBtn);

        //Get db instance
        mAuth = FirebaseAuth.getInstance();

        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(LandingPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LandingPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            user = getUserData(this);

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
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("===========RESUMING=============");
        if (user != null){
            String name = user.getName();
            welcomeMessage.setText("Welcome Back " + name + "!");
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
    }