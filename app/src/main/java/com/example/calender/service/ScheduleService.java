package com.example.calender.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.calender.domain.Schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleService extends SQLiteOpenHelper {
    SQLiteDatabase database;

    public ScheduleService(@Nullable Context context) {
        super(context, "scheduleDataBase", null, 1);
    }

    public void add(Schedule schedule) {
        database = this.getWritableDatabase();
        database.execSQL("insert into ScheduleTable(date, title, startTime, endTime)" +
                " values(" + schedule.getDate() + ", '" + schedule.getTitle() + "', '" + schedule.getStartTime() + "', '" + schedule.getEndTime() + "');");
        database.close();
    }

    public void delete(int id) {
        database = this.getWritableDatabase();
        database.execSQL("delete from ScheduleTable where id = " + id + ";");
        database.close();
    }

    public void update(int id, String title, String startTime, String endTime) {
        database = this.getWritableDatabase();
        database.execSQL("update ScheduleTable set title = '" + title + "', startTime = '" + startTime + "', endTime = '" + endTime + "' where id = " + id + ";");
        database.close();
    }

    public Schedule findById(int id) {
        Schedule schedule = new Schedule();

        database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from ScheduleTable where id = " + id + ";", null);
        while (cursor.moveToNext()) {
            schedule.setId(cursor.getInt(0));
            schedule.setDate(cursor.getLong(1));
            schedule.setTitle(cursor.getString(2));
            schedule.setStartTime(cursor.getString(3));
            schedule.setEndTime(cursor.getString(4));
        }
        database.close();

        return schedule;
    }

    public List<Schedule> findByDate(long date) {
        List<Schedule> result = new ArrayList<>();

        database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from ScheduleTable where date = " + date + ";", null);
        while (cursor.moveToNext()) {
            result.add(new Schedule(cursor.getInt(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        database.close();

        return result;
    }

    public List<Schedule> list() {
        List<Schedule> result = new ArrayList<Schedule>();

        database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from ScheduleTable;", null);
        while (cursor.moveToNext()) {
            result.add(new Schedule(cursor.getInt(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        database.close();

        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table ScheduleTable(id integer primary key autoincrement not null, date long not null, title char(20) not null, startTime char(10) not null, endTime char(10) not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
