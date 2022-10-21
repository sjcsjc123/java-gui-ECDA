package ownPanel.data;

import java.io.File;

public class FileByType {
    private File file;
    private String fileName;
    private Float dataType;

    public FileByType(File file, String fileName, Float dataType) {
        this.file = file;
        this.fileName = fileName;
        this.dataType = dataType;
    }

    //get
    public File getFile(){
        return file;
    }

    public String getFileName(){
        return fileName;
    }

    public Float getDataType(){
        return dataType;
    }

    //set
    public void setFile(File file) {
        this.file = file;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDataType(Float dataType) {
        this.dataType = dataType;
    }


    @Override
    public String toString() {
        return "{fileName:" + fileName + "}";
    }
}
