package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, nameInput;
    private Button registerBtn, loginBtn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();

        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginAct);
        emailInput = findViewById(R.id.newTagTitleEditText);
        passwordInput = findViewById(R.id.registerPasswordEditText);
        nameInput = findViewById(R.id.registerNameEditText);

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(RegisterActivity.this, TasksPageActivity.class);
            startActivity(intent);
            finish();
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();

        //Validation
        boolean validEmail = validateEmail(email);
        boolean validPassword = validatePassword(password);
        boolean validName = validateName(name);
        if ( !validEmail || !validPassword  || !validName) {
            return;
        }

        //Create User on Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Create User Document in Firestore Users Collection
                        String userId;
                        userId = mAuth.getCurrentUser().getUid();
                        System.out.println(userId);
                        if (task.isSuccessful()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("email", email);
                            newUser.put("name", name);
                            db.collection("users").document(userId)
                                    .set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                            //User Created Launch Landing User Page
                                            Intent intent = new Intent(RegisterActivity.this, TasksPageActivity.class);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    boolean validateEmail(String email){
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(" Valid Email Required.");
            return false;
        }
        return true;
    }

    boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password Required. Password must be at least 6 characters long.");
            return false;
        } else if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters long.");
            return false;
        }
        return true;
    }

    boolean validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("User Name Required.");
            return false;
        }
        return true;
    }
}


