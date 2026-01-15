package com.example.timetable;

public class ListItem {
    private Long id;
    private int hour;
    private int minute;
    private String destination;
    private int platform;
    private int  hasTransfer;
    private String userMemo;
    private String remainingTime;


    public ListItem(Long id, int hour, int minute, String destination, int platform, int hasTransfer, String userMemo, String remainingTime) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.destination = destination;
        this.platform = platform;
        this.hasTransfer = hasTransfer;
        this.userMemo = userMemo;
        this.remainingTime = remainingTime;
    }

    public Long getId() { return id; }
    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public String getDestination() {
        return destination;
    }
    public int getPlatform() {
        return platform;
    }
    public int  isHasTransfer() {
        return hasTransfer;
    }
    public String getUserMemo() {
        return userMemo;
    }
    public String getRemainingTime() {
        return remainingTime;
    }


}