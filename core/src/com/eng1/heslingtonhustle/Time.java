package com.eng1.heslingtonhustle;

public class Time {
    private int time;
    private int dayNumber;

    private final String[] DAYS = new String[]{"Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday","Sunday"};

    public Time(int time) {
        this.time = time;
    }

    public Time() {
        this.time = 8;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        if (time<10){
            return "0"+time+":00";
        }
        return time+":00";
    }

    public void increaseTime(int activityLength) {
        this.time += activityLength;
    }

    public boolean canIncreaseTime(int activityLength){
        final int MAX_TIME = 24;
        return (time+activityLength <= MAX_TIME);
    }

    public void nextDay(){
        dayNumber++;
        reset();
    }

    public boolean isWeekOver(){
        return dayNumber == 7;
    }

    public void reset() {
        this.time = 8;
    }

    public String getDay() {
        return DAYS[dayNumber];
    }
}
