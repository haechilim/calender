package com.example.calender;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.calender.domain.Schedule;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleAdapter extends BaseAdapter {
    private Context context;
    private static List<Schedule> list = new ArrayList<>();

    public ScheduleAdapter(Context context) {
        this.context = context;
    }

    public void add(String title, String startTime, String endTime) {
        list.add(new Schedule(title, startTime, endTime));
    }

    public void clear() {
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater layoutInflater = context.getSystemService(LayoutInflater.class);

        return layoutInflater.inflate(R.layout.schedule_list_item, parent);
    }
}
