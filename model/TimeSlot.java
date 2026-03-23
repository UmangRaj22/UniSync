package model;

import java.util.Objects;

public class TimeSlot {
    private String day;
    private String time;

    public TimeSlot(String day, String time) {
        this.day = day;
        this.time = time;
    }

    public String getDay() { return day; }
    public String getTime() { return time; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot t = (TimeSlot) o;
        return day.equals(t.day) && time.equals(t.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, time);
    }
}