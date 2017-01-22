package com.myapp.handbook.domain;

import java.util.List;

/**
 * Created by SAshutosh on 1/14/2017.
 */

public class CalendarEvents {

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    List<Event> events;

}
