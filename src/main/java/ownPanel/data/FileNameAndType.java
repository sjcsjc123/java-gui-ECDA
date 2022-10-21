package ownPanel.data;

public class FileNameAndType {
    private String fileName;
    private String fileType;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public FileNameAndType(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "FileCalculate{" +
                "fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }

}
