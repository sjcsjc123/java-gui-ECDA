package ownPanel.data;

import java.util.List;

public class CVOutPut {
    private List<CV> cvs;
    private String fileName;

    public CVOutPut(List<CV> cvs, String fileName) {
        this.cvs = cvs;
        this.fileName = fileName;
    }

    public void setCvs(List<CV> cvs) {
        this.cvs = cvs;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<CV> getCvs() {
        return cvs;
    }

    public String getFileName() {
        return fileName;
    }
}
