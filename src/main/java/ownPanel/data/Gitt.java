package ownPanel.data;

public class Gitt {
    private double time;
    private double e;
    private double i;

    public Gitt(double time, double e, double i) {
        this.time = time;
        this.e = e;
        this.i = i;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

    public double getI() {
        return i;
    }

    public void setI(double i) {
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
