package com.cmpundhir.cm.cmsattendenceapp.model;

public class Attendence {
    String date,time,course;

    public Attendence() {
    }

    public Attendence(String date, String time, String course) {
        this.date = date;
        this.time = time;
        this.course = course;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
