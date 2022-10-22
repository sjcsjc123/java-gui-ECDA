package ownPanel.data;

public class GittToString {
    private String time;
    private String e;
    private String i;

    public GittToString(String time, String e, String i) {
        this.time = time;
        this.e = e;
        this.i = i;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    @Override
    public String toString() {
        return "Gitt{" +
                "time='" + time + '\'' +
                ", e='" + e + '\'' +
                ", i='" + i + '\'' +
                '}';
    }

}
