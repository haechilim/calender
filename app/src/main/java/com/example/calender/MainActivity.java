package com.example.calender;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.calender.domain.Schedule;
import com.example.calender.helper.Constants;
import com.example.calender.service.ScheduleService;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private long selectedDate = -1;
    private int selectedScheduleId;
    private long todayCellTag;
    private ListView scheduleList;
    private Button addButton;
    private ScheduleService scheduleService;
    private static CalenderView calendarView;
    private ScheduleAdapter scheduleAdapter;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = today();
        scheduleAdapter = new ScheduleAdapter(this);

        addButton = findViewById(R.id.add);
        calendarView = findViewById(R.id.container);
        scheduleService = new ScheduleService(this);
        scheduleList = findViewById(R.id.scheduleList);

        calendarView.setMainActivity(this);
        scheduleList.setAdapter(scheduleAdapter);

        drawCalender();
        bindEvents();
        resetCurrentSchedule();
        markSchedule();
        updateScheduleList();
    }

    public void nextMonth() {
        calendar.add(Calendar.MONTH, 1);
        redrawCalendar();
    }

    public void prevMonth() {
        calendar.add(Calendar.MONTH, -1);
        redrawCalendar();
    }

    private void redrawCalendar() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                clearCalendar();
                drawCalender();
                resetCurrentSchedule();
                markSchedule();
                updateScheduleList();
            }
        });
    }

    private void clearCalendar() {
        calendarView.removeViews(2, calendarView.getChildCount() - 2);
    }

    private void drawCalender() {
        selectedDate = -1;

        TextView markMonth = findViewById(R.id.month);
        markMonth.setText((calendar.get(Calendar.MONTH) + 1) + "월");

        int last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        TableRow tableRow = null;

        for(int date = 1; date <= last; date++) {
            calendar.set(Calendar.DATE, date);

            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DATE) == 1) {
                tableRow = new TableRow(this);
                TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                tableRow.setBackgroundColor(Color.parseColor("#f7f7f7"));
                calendarView.addView(tableRow, layoutParams);
            }

            if(tableRow == null) continue;

            LinearLayout linearLayout = new LinearLayout(this);
            TableRow.LayoutParams linearLayoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.setBackground(getResources().getDrawable(R.drawable.ic_launcher_background, null));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));
            linearLayout.setTag(calendar.getTimeInMillis());
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

            linearLayout.setOnClickListener(this);
        }
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.setTimeInMillis(calendar.getTimeInMillis() / 1000 * 1000);
        todayCellTag = calendar.getTimeInMillis();
        return calendar;
    }

    private void bindEvents() {
        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view.getTag() == null) return;

                Schedule schedule = scheduleService.findById((int)view.getTag());

                if(schedule == null) return;

                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_TITLE, schedule.getTitle());
                intent.putExtra(Constants.KEY_START_TIME, schedule.getStartTime());
                intent.putExtra(Constants.KEY_END_TIME, schedule.getEndTime());
                startScheduleActivity(intent);

                selectedScheduleId = schedule.getId();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCurrentSchedule();

                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_TITLE, "");
                intent.putExtra(Constants.KEY_START_TIME, "");
                intent.putExtra(Constants.KEY_END_TIME, "");
                startScheduleActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (selectedDate != -1) clearSelectedDay();
        selectCurrentDay(view);

        addButton.setEnabled(true);

        selectedDate = (long)view.getTag();

        updateScheduleList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ((TextView)calendarView.findViewWithTag(selectedDate).findViewWithTag("schedule")).setText("");

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
        intent.setClass(this, ScheduleActivity.class);
        startActivityForResult(intent, 100);
    }

    private void updateScheduleList() {
        scheduleAdapter.clear();

        List<Schedule> schedules = scheduleService.findByDate(selectedDate);

        for(int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);

            scheduleAdapter.add(schedule.getTitle(), schedule.getStartTime(), schedule.getEndTime());
        }

        scheduleAdapter.notifyDataSetChanged();
    }

    private void markSchedule() {
        clearSelectedDay();

        List<Schedule> schedules = scheduleService.list();

        for(Schedule schedule : schedules) {
            View dayView = calendarView.findViewWithTag(schedule.getDate());
            TextView markView = dayView != null ? dayView.findViewWithTag("schedule") : null;
            if(markView != null) markView.setText("●");
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

    private void selectCurrentDay(View view) {
        TextView day = view.findViewWithTag("day");
        int backResId = (long)view.getTag() == todayCellTag ? R.drawable.layout_today : R.drawable.layout_oval;
        changeCellStyle(day, backResId, Color.rgb(0xff, 0xff, 0xff));
    }

    private void clearSelectedDay() {
        if(selectedDate < 0) return;
        TextView target = calendarView.findViewWithTag(selectedDate).findViewWithTag("day");
        int color = selectedDate == todayCellTag ? Color.rgb(0xff, 0x1b, 0x19) : Color.rgb(0x00, 0x00, 0x00);
        changeCellStyle(target, R.drawable.layout_border, color);
    }

    private void changeCellStyle(TextView view, int backResId, int textColor) {
        view.setBackgroundResource(backResId);
        view.setTextColor(textColor);
    }

    private void resetCurrentSchedule() {
        selectedScheduleId = -1;
        addButton.setEnabled(false);
    }

    private boolean currentScheduleExists() {
        return selectedScheduleId >= 0;
    }
}