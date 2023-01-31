package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDate;


import static com.example.tasktrack.CalendarUtils.daysInMonthArray;
import static com.example.tasktrack.CalendarUtils.daysInWeekArray;
import static com.example.tasktrack.CalendarUtils.monthYearFromDate;




public class TasksPageActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    private Button logOut, newTask, popupAddTaskBtn, popupBackBtn, popupDateBtn, prevWeekBtn, nextWeekBtn;
    private RecyclerView recycleView = null;
    private DataAdapter adapter;
    private ArrayList<TaskObj> tasks = null;

    //DB
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

    //Calendar
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;


    //New Task
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private DatePickerDialog datePickerDialog;
    private EditText popupTaskTitle, popupTaskMoreDetails;
    private Spinner popupTaskTag;

//==================================================================================================
    // OnCreate
    // => Check Logged In
    // => Get Task Data
    // => Populate Task List
    // => Button Listeners... Popup Dialog
//==================================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tasks_page);

        //wire widgets
        logOut = findViewById(R.id.logOutBtn);
        newTask = findViewById(R.id.newTaskBtn);
        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setItemAnimator(new DefaultItemAnimator());

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        prevWeekBtn = findViewById(R.id.prevWeek);
        nextWeekBtn = findViewById(R.id.nextWeek);


        //Get db instance
        mAuth = FirebaseAuth.getInstance();

        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            CalendarUtils.selectedDate = LocalDate.now();
            setWeekView();
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
                    newTaskDialog();
                }
            });

            prevWeekBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
                    setWeekView();
                    tasks = getTasks(TasksPageActivity.this);
                }
            });

            nextWeekBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
                    setWeekView();
                    tasks = getTasks(TasksPageActivity.this);
                }
            });
        }
    }

//==================================================================================================
    // Calendar
//==================================================================================================
    public void setWeekView(){
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        tasks = getTasks(this);
        setWeekView();
    }


//==================================================================================================
    // Getting & Displaying Tasks
    // => Get tasks from firebase
    // => OnComplete... Invoke onResume to populate the views with data
//==================================================================================================

    public ArrayList<TaskObj> getTasks(Context context) {
        System.out.println("getting tasks");
        db = FirebaseFirestore.getInstance();
        ArrayList<TaskObj> tasks = new ArrayList<>();

        String date = CalendarUtils.formattedDate(CalendarUtils.selectedDate);
        System.out.println(date);

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("tasks").whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println("HEREEEE");
                        if (task.isSuccessful()) {
                            System.out.println("SUCCESS");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String taskTitle = (document.getString("title"));
                                String taskMoreDetails = (document.getString("moreDetails"));
                                String taskTag = (document.getString("tag"));
                                String taskDate = (document.getString("date"));
                                TaskObj taskObj = new TaskObj(taskTitle, taskMoreDetails, taskTag, taskDate);
                                tasks.add(taskObj);
                                System.out.println("first");
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
        System.out.println("===========RESUMING=============");
        // make the adapter and set it to recycleView from received tasks
        adapter = new DataAdapter(this, R.layout.row_layout, tasks);
        recycleView.setAdapter(adapter);
        setWeekView();
    }

//==================================================================================================
    // Create Task Popup
//==================================================================================================

    public void newTaskDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View taskPopupView = getLayoutInflater().inflate(R.layout.new_task_popup, null);
        popupAddTaskBtn = taskPopupView.findViewById(R.id.addTaskBtn);
        popupBackBtn = taskPopupView.findViewById(R.id.backBtn);
        popupTaskTitle = taskPopupView.findViewById(R.id.taskTitleEditText);
        popupTaskMoreDetails = taskPopupView.findViewById(R.id.taskMoreDetailsEditText);


        popupDateBtn = taskPopupView.findViewById(R.id.newTaskDate);
        popupDateBtn.setText(getTodaysDate());

        dialogBuilder.setView(taskPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        popupTaskTag = taskPopupView.findViewById(R.id.tagDropdown);

        //==========================================================================================
        //HARDCODED TAGS
        //TODO: Tags => User Attribute, User makes their own tags
        //==========================================================================================
        ArrayList<String> userTags = new ArrayList<>();
        userTags.add("School");
        userTags.add("Personal");
        userTags.add("Work");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, userTags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        popupTaskTag.setAdapter(adapter);

        popupAddTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = popupTaskTitle.getText().toString().trim();
                String moreDetails = popupTaskMoreDetails.getText().toString().trim();
                String date = popupDateBtn.getText().toString().trim();
                String tag = popupTaskTag.getSelectedItem().toString().trim();
                TaskObj task = new TaskObj(title, moreDetails, tag, date);
                createTask(task);
            }
        });

        popupBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        System.out.println(popupDateBtn);
        popupDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        initDatePicker();
    }

    //==============================================================================================
        //DATE PICKER
    //==============================================================================================

    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private  void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = makeDateString(day, month, year);
                System.out.println("=========>"+date);
                popupDateBtn.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        String strDay;
        if (day < 10){
            strDay = "0" + Integer.toString(day);
        }
        else{strDay = Integer.toString(day);}
        return getMonthFormat(month) + " " + strDay + " " + year;
    };

    private String getMonthFormat(int month){
        if (month == 1){
            return "January";
        }
        if (month == 2){
            return "February";
        }
        if (month == 3){
            return "March";
        }
        if (month == 4){
            return "April";
        }
        if (month == 5){
            return "May";
        }
        if (month == 6){
            return "June";
        }
        if (month == 7){
            return "July";
        }
        if (month == 8){
            return "August";
        }
        if (month == 9){
            return "September";
        }
        if (month == 10){
            return "October";
        }
        if (month == 11){
            return "November";
        }
        if (month == 12){
            return "December";
        }
        return "ERROR";
    }

    public void openDatePicker(){
        datePickerDialog.show();
    }


//==================================================================================================
    // Pushing Task to DB
//==================================================================================================

    private void createTask(TaskObj task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String title = task.getTitle();
        String moreDetails = task.getMoreDetails();
        String tag = task.getTag();
        String date = task.getDate();


        if (TextUtils.isEmpty(title)) {
            popupTaskTitle.setError("Please Enter A Name For The Task!");
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {

            Map<String, Object> dbTask = new HashMap<>();
            dbTask.put("title", title);
            dbTask.put("moreDetails", moreDetails);
            dbTask.put("tag", tag);
            dbTask.put("date", date);
            dbTask.put("completed", false);

            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("tasks")
                    .add(dbTask)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(TasksPageActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            tasks = getTasks(TasksPageActivity.this);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TasksPageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            onResume();
                        }
                    });
        }
    }
}



