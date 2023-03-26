package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, nameInput;
    private Button registerBtn, loginBtn, registerDobBtn;

    private FirebaseAuth mAuth;

    private DatePickerDialog datePickerDialog;

    private Spinner registerGender;



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
        registerDobBtn = findViewById(R.id.registerDob);
        registerGender = findViewById(R.id.registerGender);

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

        registerDobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
                System.out.println("HEREEE");
            }
        });

        ArrayList<String> genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");
        genders.add("Other");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        registerGender.setAdapter(adapter);

        initDatePicker();
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String gender = registerGender.getSelectedItem().toString().trim();;
        String dob = registerDobBtn.getText().toString().trim();;
        String bio = "";
        ArrayList<String> tags = new ArrayList<>();
        tags.add("School");
        tags.add("Home");
        tags.add("Work");

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
                            newUser.put("tags", tags);
                            newUser.put("dob", dob);
                            newUser.put("bio", bio);
                            newUser.put("gender", gender);
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

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = CalendarUtils.makeDateString(day, month, year);
                System.out.println("=========>"+date);
                registerDobBtn.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    public void openDatePicker(){
        datePickerDialog.show();
    }
}


