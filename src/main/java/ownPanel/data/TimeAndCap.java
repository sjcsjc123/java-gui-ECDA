package ownPanel.data;

public class TimeAndCap {
    private String time;
    private String cap;

    public TimeAndCap(String time, String cap) {
        this.time = time;
        this.cap = cap;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    @Override
    public String toString() {
        return "TimeAndCap{" +
                "time='" + time + '\'' +
                ", cap='" + cap + '\'' +
                '}';
    }
}
