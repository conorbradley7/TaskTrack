package com.example.tasktrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private DatePickerDialog datePickerDialog;
    private Button dobBtn, editBtn, cancelBtn, saveBtn;

    private ImageView profileFragPic;
    private TextView profileFragName, profileFragEmail;
    private EditText profileFragBio;
    private Spinner profileFragGender;


    private User user;
    private ArrayList<TaskObj> todaysTasks;

    //DB
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        profileFragPic = (ImageView) view.findViewById(R.id.profileFragPic);
        profileFragName = (TextView) view.findViewById(R.id.profileFragName);
        profileFragEmail = (TextView) view.findViewById(R.id.profileFragEmail);
        profileFragBio = (EditText) view.findViewById(R.id.profileFragBio);
        dobBtn = (Button) view.findViewById(R.id.profileFragDob);
        editBtn = (Button) view.findViewById(R.id.profileFragEditBtn);
        cancelBtn = (Button) view.findViewById(R.id.profileFragCancelBtn);
        saveBtn = (Button) view.findViewById(R.id.profileFragSaveBtn);

        profileFragGender = (Spinner) view.findViewById(R.id.profileFragGender);


        dobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBtn.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);

                profileFragBio.setEnabled(true);
                dobBtn.setEnabled(true);
                profileFragGender.setEnabled(true);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);

                profileFragBio.setEnabled(false);
                dobBtn.setEnabled(false);
                profileFragGender.setEnabled(false);

                if (user != null) {
                    setProfileAttributes(user);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);

                profileFragBio.setEnabled(false);
                dobBtn.setEnabled(false);
                profileFragGender.setEnabled(false);

                updateUserInDB();
                onResume();
            }
        });



        if (this.getArguments() != null) {
            user = (User) this.getArguments().getSerializable("user");
            todaysTasks = (ArrayList<TaskObj>) this.getArguments().getSerializable("todaysTasks");

            if (user != null) {
                setProfileAttributes(user);
                profileFragGender.setEnabled(false);
            }
        }



        initDatePicker();
        return view;
    }


//==================================================================================================
//DOB Picker
//==================================================================================================

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = CalendarUtils.makeDateString(day, month, year);
                System.out.println("=========>"+date);
                dobBtn.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
    }

    public void openDatePicker(){
        datePickerDialog.show();
    }

    public void setProfileAttributes(User user){
        profileFragName.setText(user.getName());
        profileFragEmail.setText(user.getEmail());
        profileFragBio.setText(user.getBio());
        dobBtn.setText(user.getDob());

        ArrayList<String> genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");
        genders.add("Other");

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        profileFragGender.setAdapter(adapter);
        profileFragGender.setSelection(getGenderIndex(user.getGender()));
    }

    public int getGenderIndex(String gender){
        if (gender == "Male") {return 0;}
        else if(gender == "Female"){return 1;}
        else{return 2;}
    }


    public void updateUserInDB(){
        db = FirebaseFirestore.getInstance();

        String bio = profileFragBio.getText().toString().trim();
        String dob = dobBtn.getText().toString().trim();
        String gender = profileFragGender.getSelectedItem().toString().trim();

        if (TextUtils.isEmpty(dob)) {
            dobBtn.setError("D.O.B Required");
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getActivity(), "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .update("bio", bio, "dob", dob, "gender", gender)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                            onResume();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Profile Update Failed", Toast.LENGTH_SHORT).show();
                            onResume();
                        }
                    });
        }
    }
}