package com.quest_exfo.backend.dto;
public class ScheduleMessage {
    private String title;
    private long minutesUntilStart;

    public ScheduleMessage(String title, long minutesUntilStart) {
        this.title = title;
        this.minutesUntilStart = minutesUntilStart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getMinutesUntilStart() {
        return minutesUntilStart;
    }

    public void setMinutesUntilStart(long minutesUntilStart) {
        this.minutesUntilStart = minutesUntilStart;
    }
}
