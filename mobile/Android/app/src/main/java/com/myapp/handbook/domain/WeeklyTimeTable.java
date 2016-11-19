package com.myapp.handbook.domain;

import java.util.List;

public class WeeklyTimeTable {

    String Day;
    List<TimeSlots> TimeSlots;

    public WeeklyTimeTable(String day, List<com.myapp.handbook.domain.TimeSlots> timeSlots) {
        Day = day;
        TimeSlots = timeSlots;
    }

    public String getDayOfWeek() {
        return Day;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.Day = dayOfWeek;
    }

    public List<TimeSlots> getTimeSlotsList() {
        return TimeSlots;
    }

    public void setTimeSlotsList(List<TimeSlots> timeSlotsList) {
        this.TimeSlots = timeSlotsList;
    }
}
