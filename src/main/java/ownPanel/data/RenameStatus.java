package ownPanel.data;

public class RenameStatus {

    private boolean flag;
    private String newFileName;

    public RenameStatus(boolean flag, String newFileName) {
        this.flag = flag;
        this.newFileName = newFileName;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    @Override
    public String toString() {
        return "RenameStatus{" +
                "flag=" + flag +
                ", newFileName=" + newFileName +
                '}';
    }

}
