package com.example.calender;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.calender.domain.Schedule;
import com.example.calender.helper.Constants;
import com.example.calender.service.ScheduleService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private long selectedDate = -1;
    private int selectedScheduleId;
    private long todayCellTag;
    private int[] scheduleIds = { R.id.schedule1, R.id.schedule2, R.id.schedule3, R.id.schedule4,
            R.id.schedule5, R.id.schedule6, R.id.schedule7, R.id.schedule8 };
    private List<Long> calendarTags = new ArrayList<>();
    private ScheduleService scheduleService;
    private ViewGroup container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        scheduleService = new ScheduleService(this);

        renderCalender();
        bindEvents();
        resetCurrentSchedule();
        markSchedule();
        updateScheduleList();
    }

    private void renderCalender() {
        Calendar calendar = today();
        int last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        TableRow tableRow = null;

        todayCellTag = calendar.getTimeInMillis();

        for(int date = 1; date <= last; date++) {
            calendar.set(Calendar.DATE, date);

            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DATE) == 1) {
                tableRow = new TableRow(this);
                TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                tableRow.setBackgroundColor(Color.parseColor("#f7f7f7"));
                container.addView(tableRow, layoutParams);
            }

            if(tableRow == null) continue;

            LinearLayout linearLayout = new LinearLayout(this);
            TableRow.LayoutParams linearLayoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setBackground(getResources().getDrawable(R.drawable.ic_launcher_background, null));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));
            linearLayout.setTag(calendar.getTimeInMillis());
            calendarTags.add(calendar.getTimeInMillis());
            if(calendar.get(Calendar.DATE) == 1) linearLayoutParams.column = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            TextView dateView = new TextView(this);
            LinearLayout.LayoutParams dateViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 3);
            dateView.setGravity(Gravity.CENTER);
            dateView.setTag("day");
            dateView.setTextColor(Color.parseColor(todayCellTag == calendar.getTimeInMillis() ? "#FF1B19" : "#000000"));
            dateView.setTextSize(getResources().getDimension(R.dimen.dateFontSize));
            dateView.setText(String.valueOf(date));

            TextView scheduleMarkView = new TextView(this);
            LinearLayout.LayoutParams scheduleMarkParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            scheduleMarkView.setGravity(Gravity.CENTER);
            scheduleMarkView.setTag("schedule");
            scheduleMarkView.setTextColor(Color.parseColor("#D3CFCF"));
            scheduleMarkView.setText("");

            tableRow.addView(linearLayout, linearLayoutParams);
            linearLayout.addView(dateView, dateViewParams);
            linearLayout.addView(scheduleMarkView, scheduleMarkParams);
        }
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() / 1000 * 1000);
        return calendar;
    }

    private void bindEvents() {
        for(int i = 0; i < calendarTags.size(); i++) {
            container.findViewWithTag(calendarTags.get(i)).setOnClickListener(this);
        }

        for(int i = 0; i < scheduleIds.length; i++) {
            findViewById(scheduleIds[i]).setOnClickListener(this);
        }

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCurrentSchedule();

                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_IS_NEW_SCHEDULE, true);
                startScheduleActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.findViewWithTag("day") != null) {
            if (selectedDate != -1) clearPreviousDay();
            selectCurrentDay(view);

            findViewById(R.id.add).setEnabled(true);

            selectedDate = (long)view.getTag();

            updateScheduleList();
        }
        else if(view.findViewWithTag("title") != null) {
            if(view.getTag() == null) return;

            Schedule schedule = scheduleService.findById((int)view.getTag());

            if(schedule == null) return;

            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_IS_NEW_SCHEDULE, false);
            intent.putExtra(Constants.KEY_TITLE, schedule.getTitle());
            intent.putExtra(Constants.KEY_START_TIME, schedule.getStartTime());
            intent.putExtra(Constants.KEY_END_TIME, schedule.getEndTime());
            startScheduleActivity(intent);

            selectedScheduleId = schedule.getId();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Constants.RC_SUCCESS) {
            String editTextTitle = data.getStringExtra(Constants.KEY_TITLE);
            String editTextStart = data.getStringExtra(Constants.KEY_START_TIME);
            String editTextEnd = data.getStringExtra(Constants.KEY_END_TIME);

            if(!currentScheduleExists()) scheduleService.add(new Schedule(selectedDate, editTextTitle, editTextStart, editTextEnd));
            else scheduleService.update(selectedScheduleId, editTextTitle, editTextStart, editTextEnd);

            markSchedule();
            updateScheduleList();
        }
        else if(resultCode == Constants.RC_DELETE) {
            scheduleService.delete(selectedScheduleId);
            markSchedule();
            updateScheduleList();
        }
    }

    private void startScheduleActivity(Intent intent) {
        intent.setClass(MainActivity.this, ScheduleActivity.class);
        startActivityForResult(intent, 100);
    }

    private void updateScheduleList() {
        List<Schedule> schedules = scheduleService.findByDate(selectedDate);

        resetScheduleList(scheduleIds);

        for(int i = 0; i < schedules.size(); i++) {
            if(i >= scheduleIds.length) break;

            Schedule schedule = schedules.get(i);
            View view = findViewById(scheduleIds[i]);

            view.setTag(schedule.getId());
            ((TextView) view.findViewWithTag("title")).setText(schedule.getTitle());
            ((TextView) view.findViewWithTag("startTime")).setText(schedule.getStartTime());
            ((TextView) view.findViewWithTag("endTime")).setText(schedule.getEndTime());
        }
    }

    private void markSchedule() {
        resetScheduleMark();

        List<Schedule> schedules = scheduleService.list();

        for(Schedule schedule : schedules) {
            ((TextView)container.findViewWithTag(schedule.getDate()).findViewWithTag("schedule")).setText("●");
        }
    }

    private void resetScheduleList(int[] scheduleIds) {
        for(int scheduleId : scheduleIds) {
            View view = findViewById(scheduleId);

            view.setTag(null);
            ((TextView) view.findViewWithTag("startTime")).setText("");
            ((TextView) view.findViewWithTag("endTime")).setText("");
            ((TextView) view.findViewWithTag("title")).setText("");
        }
    }

    private void resetScheduleMark() {
        for(int i = 0; i < calendarTags.size(); i++) {
            ((TextView)container.findViewWithTag(calendarTags.get(i)).findViewWithTag("schedule")).setText("");
        }
    }

    private void selectCurrentDay(View view) {
        TextView day = view.findViewWithTag("day");
        int backResId = (long)view.getTag() == todayCellTag ? R.drawable.layout_today : R.drawable.layout_oval;
        changeCellStyle(day, backResId, Color.rgb(0xff, 0xff, 0xff));
    }

    private void clearPreviousDay() {
        TextView target = container.findViewWithTag(selectedDate).findViewWithTag("day");
        int color = selectedDate == todayCellTag ? Color.rgb(0xff, 0x1b, 0x19) : Color.rgb(0x00, 0x00, 0x00);
        changeCellStyle(target, R.drawable.layout_border, color);
    }

    private void changeCellStyle(TextView view, int backResId, int textColor) {
        view.setBackgroundResource(backResId);
        view.setTextColor(textColor);
    }

    private void resetCurrentSchedule() {
        selectedScheduleId = -1;
    }

    private boolean currentScheduleExists() {
        return selectedScheduleId >= 0;
    }
}