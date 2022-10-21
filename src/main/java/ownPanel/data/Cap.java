package ownPanel.data;

import java.util.List;

public class Cap {
    private List<String> capacity;
    private List<String> clEfficiency;

    public Cap(List<String> capacity, List<String> clEfficiency) {
        this.capacity = capacity;
        this.clEfficiency = clEfficiency;
    }

    public List<String> getCapacity() {
        return capacity;
    }

    public void setCapacity(List<String> capacity) {
        this.capacity = capacity;
    }

    public List<String> getClEfficiency() {
        return clEfficiency;
    }

    public void setClEfficiency(List<String> clEfficiency) {
        this.clEfficiency = clEfficiency;
    }
}
