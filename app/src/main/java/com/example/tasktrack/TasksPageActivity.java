package com.example.tasktrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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


import static com.example.tasktrack.CalendarUtils.daysInWeekArray;
import static com.example.tasktrack.CalendarUtils.monthYearFromDate;


public class TasksPageActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, RecycleViewInterface{
    private Button newAddTaskBtn, newBackBtn, newDateBtn;
    private ImageButton prevWeekBtn,nextWeekBtn, newTask, home, generateSchedule;
    private ViewPager2 recycleView = null;
    private TaskSectionAdapter adapter;
    private ArrayList<TaskObj> tasks, completedTasks, incompleteTasks, todoTasks;

    //DB
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

    //Calendar
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;


    //New Task
    private AlertDialog.Builder createTaskDialogBuilder, completeTaskDialogBuilder;
    private AlertDialog createTaskDialog, startCompleteTaskDialog;
    private DatePickerDialog datePickerDialog;
    private EditText newTaskTitle, newTaskMoreDetails, newTaskExpDur, newTaskPriority;
    private Spinner newTaskTag;

    //Start+Complete Tasks
    private TextView startCompleteTaskTitle, startCompleteTaskDetails;
    private EditText completeTaskDuration, completeTaskDifficulty;
    private Button startTaskBtn, completeTaskBtn, incompleteTaskBtn, startCompleteBackBtn;
    private ConstraintLayout completeTaskLayout, startTaskLayout;

    private User user;

    enum taskSectionTypes{
        complete,
        incomplete,
        todo
    }
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
        newTask = findViewById(R.id.newTaskBtn);
        home = findViewById(R.id.homeBtn);
        recycleView = findViewById(R.id.taskSections);
        generateSchedule = findViewById(R.id.generateSchedule);
//        recycleView.setLayoutManager(new LinearLayoutManager(this));
//        recycleView.setItemAnimator(new DefaultItemAnimator());

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        prevWeekBtn = findViewById(R.id.prevWeek);
        nextWeekBtn = findViewById(R.id.nextWeek);


        //Get db instance
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        user = (User)intent.getExtras().getSerializable("user");

        Boolean isLoggedIn = DBUtilities.checkLoggedIn();
        if (!isLoggedIn) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent loggedOutIntent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(loggedOutIntent);
        } else {
            CalendarUtils.selectedDate = LocalDate.now();
            setWeekView();
            tasks = getTasks(this);

            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(TasksPageActivity.this, LandingPageActivity.class);
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
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

            generateSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Day today = new Day(tasks);
                    today.scheduleBins();
                    Intent genSchedule = new Intent(TasksPageActivity.this, GenerateScheduleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("today", today);
                    bundle.putSerializable("user", user);
                    genSchedule.putExtras(bundle);
                    startActivity(genSchedule);
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
    public void onCalanderItemClick(int position, LocalDate date) {
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
                                String taskExpDur = (document.getString("expDur"));
                                String taskPriority = (document.getString("priority"));
                                Boolean taskStarted = (document.getBoolean("started"));
                                Boolean taskCompleted = (document.getBoolean("completed"));
                                Boolean taskIncomplete = (document.getBoolean("incomplete"));
                                String taskDifficulty = (document.getString("difficulty"));
                                String actualDur = (document.getString("actualDur"));
                                String id = (document.getId());


                                TaskObj taskObj = new TaskObj(id, taskTitle, taskMoreDetails,
                                        taskTag, taskDate, taskExpDur, taskPriority, taskStarted,
                                        taskCompleted, taskIncomplete, taskDifficulty,null,
                                        null, actualDur);
                                tasks.add(taskObj);
                            }
                            onResume();
                        }
                    }
                });
        return tasks;
    }

    @Override
    public void onTaskItemClick(int pos) {
        startCompleteTaskDialog(pos);}

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("===========RESUMING=============");
        filterTasksByCompletion(tasks);
        // make the adapter and set it to recycleView from received tasks
        ArrayList<taskSectionTypes> sections = new ArrayList<>();
        sections.add(taskSectionTypes.todo);
        sections.add(taskSectionTypes.complete);
        sections.add(taskSectionTypes.incomplete);
        if(todoTasks.size()>0){System.out.println(todoTasks.get(0).getTitle());}
        System.out.println(completedTasks);
        System.out.println(incompleteTasks);
        adapter = new TaskSectionAdapter(this, R.layout.task_section_layout, tasks, incompleteTasks, completedTasks, todoTasks, sections, this);
        recycleView.setAdapter(adapter);
        setWeekView();
    }

    public void filterTasksByCompletion(ArrayList<TaskObj> tasks){
        completedTasks = new ArrayList<>(); todoTasks = new ArrayList<>(); incompleteTasks = new ArrayList<>();
        for (int i = 0; i<tasks.size(); i++){
            if (tasks.get(i).getCompleted() == true){
                completedTasks.add(tasks.get(i));
            }
            else if(tasks.get(i).getIncomplete() == true){
                incompleteTasks.add(tasks.get(i));
            }
            else{
                todoTasks.add(tasks.get(i));
            }
        }
    }

//==================================================================================================
    // Create Task Popup
//==================================================================================================

    public void newTaskDialog() {
        createTaskDialogBuilder = new AlertDialog.Builder(this);
        final View createTaskPopupView = getLayoutInflater().inflate(R.layout.new_task_popup, null);
        newAddTaskBtn = createTaskPopupView.findViewById(R.id.addTaskBtn);
        newBackBtn = createTaskPopupView.findViewById(R.id.backBtn);
        newTaskTitle = createTaskPopupView.findViewById(R.id.taskTitleEditText);
        newTaskMoreDetails = createTaskPopupView.findViewById(R.id.taskMoreDetailsEditText);
        newTaskExpDur = createTaskPopupView.findViewById(R.id.taskExpDurationEditText);
        newTaskPriority = createTaskPopupView.findViewById(R.id.taskPriorityEditText);



        newDateBtn = createTaskPopupView.findViewById(R.id.profileFragDob);
        newDateBtn.setText(CalendarUtils.getTodaysDate());

        createTaskDialogBuilder.setView(createTaskPopupView);
        createTaskDialog = createTaskDialogBuilder.create();
        createTaskDialog.show();

        newTaskTag = createTaskPopupView.findViewById(R.id.profileFragGender);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, user.getTags());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        newTaskTag.setAdapter(adapter);

        newAddTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = newTaskTitle.getText().toString().trim();
                String moreDetails = newTaskMoreDetails.getText().toString().trim();
                String date = newDateBtn.getText().toString().trim();
                String tag = newTaskTag.getSelectedItem().toString().trim();
                String expDur = newTaskExpDur.getText().toString().trim();
                String priority = newTaskPriority.getText().toString().trim();

                TaskObj task = new TaskObj("", title, moreDetails, tag, date, expDur, priority,
                        false, false, true, null, null,
                        null, null);
                createTask(task);
            }
        });

        newBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTaskDialog.dismiss();
            }
        });

        System.out.println(newDateBtn);
        newDateBtn.setOnClickListener(new View.OnClickListener() {
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

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = CalendarUtils.makeDateString(day, month, year);
                System.out.println("=========>"+date);
                newDateBtn.setText(date);
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


//==================================================================================================
    // Pushing Task to DB
//==================================================================================================

    private void createTask(TaskObj task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String title = task.getTitle();
        String moreDetails = task.getMoreDetails();
        String tag = task.getTag();
        String date = task.getDate();
        String expDur = task.getExpDur();
        String priority = task.getPriority();


        if (TextUtils.isEmpty(title)) {
            newTaskTitle.setError("Please Enter A Name For The Task!");
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {

            Map<String, Object> dbTask = new HashMap<>();
            //INIT TASK ATTRIBUTES
            dbTask.put("title", title);
            dbTask.put("moreDetails", moreDetails);
            dbTask.put("tag", tag);
            dbTask.put("date", date);
            dbTask.put("expDur", expDur);
            dbTask.put("priority", priority);
            //COMPLETED TASK ATTRIBUTES
            dbTask.put("started", false);
            dbTask.put("completed", false);
            dbTask.put("incomplete", false);
            dbTask.put("actualDur", null);
            dbTask.put("difficulty", null);
            dbTask.put("startTime", null);
            dbTask.put("finishTime", null);


            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("tasks")
                    .add(dbTask)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(TasksPageActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            createTaskDialog.dismiss();
                            tasks = getTasks(TasksPageActivity.this);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TasksPageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            createTaskDialog.dismiss();
                            onResume();
                        }
                    });
        }
    }

//==================================================================================================
    // Task Start/Complete Popup
//==================================================================================================
    public void startCompleteTaskDialog(int pos){
        completeTaskDialogBuilder = new AlertDialog.Builder(this);
        final View completeTaskPopupView = getLayoutInflater().inflate(R.layout.complete_task_popup, null);

        completeTaskDialogBuilder.setView(completeTaskPopupView);
        startCompleteTaskDialog = completeTaskDialogBuilder.create();
        TaskObj task = todoTasks.get(pos);


        startCompleteTaskTitle = completeTaskPopupView.findViewById(R.id.completePopupTaskTitle);
        startCompleteTaskDetails = completeTaskPopupView.findViewById(R.id.completePopupTaskDetails);
        startCompleteBackBtn = completeTaskPopupView.findViewById(R.id.cancelTaskPopup);

        //Start Task Elements
        startTaskBtn = completeTaskPopupView.findViewById(R.id.startTaskBtn);
        startTaskLayout = completeTaskPopupView.findViewById(R.id.startTaskLayout);

        //Complete Task Elements
        completeTaskDuration = completeTaskPopupView.findViewById(R.id.taskActualDurationEditText);
        completeTaskDifficulty = completeTaskPopupView.findViewById(R.id.taskDiifficultyEditText);
        completeTaskBtn = completeTaskPopupView.findViewById(R.id.completeTask);
        incompleteTaskBtn = completeTaskPopupView.findViewById(R.id.incompleteTask);
        completeTaskLayout = completeTaskPopupView.findViewById(R.id.completeTaskLayout);
        startCompleteTaskDialog.show();

        startCompleteTaskTitle.setText(task.getTitle());
        startCompleteTaskDetails.setText(task.getMoreDetails());

        if (task.getStarted() == true){
            startTaskLayout.setVisibility(View.GONE);
            completeTaskLayout.setVisibility(View.VISIBLE);
        }

        startCompleteBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCompleteTaskDialog.dismiss();
            }
        });

        startTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTask(task);
            }
        });

        completeTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {completeTask(task, true);}
        });

        incompleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {completeTask(task, false);}
        });

    }


//==================================================================================================
    // Start Task
//==================================================================================================

    private void startTask(TaskObj task){
        Timestamp currTime = Timestamp.now();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("tasks").document(task.getId()).update("started", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(TasksPageActivity.this, "Task Started!", Toast.LENGTH_SHORT).show();
                            startCompleteTaskDialog.dismiss();
                            tasks = getTasks(TasksPageActivity.this);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TasksPageActivity.this, "Task Start Failed", Toast.LENGTH_SHORT).show();
                            startCompleteTaskDialog.dismiss();
                            onResume();
                        }
                    });

        }

    }

//==================================================================================================
    // Complete Task
//==================================================================================================
    private void completeTask(TaskObj task, Boolean complete){
        String actualDur = completeTaskDuration.getText().toString().trim();
        String difficulty = completeTaskDifficulty.getText().toString().trim();
        Timestamp currTime = Timestamp.now();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(TasksPageActivity.this, "Error! Signed Out, Please Log In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TasksPageActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            if (complete) {
                db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("tasks").document(task.getId()).update("completed", true, "actualDur", actualDur, "difficulty", difficulty)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(TasksPageActivity.this, "Task Started!", Toast.LENGTH_SHORT).show();
                                startCompleteTaskDialog.dismiss();
                                tasks = getTasks(TasksPageActivity.this);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TasksPageActivity.this, "Task Start Failed", Toast.LENGTH_SHORT).show();
                                startCompleteTaskDialog.dismiss();
                                onResume();
                            }
                        });
            }
            else{
                db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("tasks").document(task.getId()).update("incomplete", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(TasksPageActivity.this, "Task Started!", Toast.LENGTH_SHORT).show();
                                startCompleteTaskDialog.dismiss();
                                tasks = getTasks(TasksPageActivity.this);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TasksPageActivity.this, "Task Start Failed", Toast.LENGTH_SHORT).show();
                                startCompleteTaskDialog.dismiss();
                                onResume();
                            }
                        });
            }

        }
    }

}



