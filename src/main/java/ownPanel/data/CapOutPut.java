package ownPanel.data;

import java.util.List;

public class CapOutPut {
    private String fileName;
    private List<String> capacity;
    private List<String> clEfficiency;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public CapOutPut(String fileName, List<String> capacity,
                     List<String> clEfficiency) {
        this.fileName = fileName;
        this.capacity = capacity;
        this.clEfficiency = clEfficiency;
    }
}
