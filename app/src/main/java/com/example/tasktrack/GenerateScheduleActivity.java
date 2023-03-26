package com.example.tasktrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class GenerateScheduleActivity extends AppCompatActivity implements RecycleViewInterface {
    ViewPager2 recycleView;
    DayScheduleAdapter adapter;
    Day today;
    ArrayList<HourBin> hourBins;
    ImageButton homeBtn;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_schedule);

        Intent intent = getIntent();
        today = (Day)intent.getExtras().getSerializable("today");
        user = (User)intent.getExtras().getSerializable("user");
        hourBins = today.getHourBins();

        homeBtn = findViewById(R.id.homeBtn);
        recycleView = findViewById(R.id.hour_items);
        adapter = new DayScheduleAdapter(this, R.layout.schedule_section, hourBins, this);
        recycleView.setAdapter(adapter);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(GenerateScheduleActivity.this, LandingPageActivity.class);
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onTaskItemClick(int position) {
        return;
    }
}
