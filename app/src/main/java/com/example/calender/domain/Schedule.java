package com.example.calender.domain;

public class Schedule {
    private int id;
    private int date;
    private String title;
    private String startTime;
    private String endTime;

    public Schedule() {}

    public Schedule(int id, int date, String title, String startTime, String endTime) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule(int date, String title, String startTime, String endTime) {
        this.date = date;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", date=" + date +
                ", title='" + title + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
