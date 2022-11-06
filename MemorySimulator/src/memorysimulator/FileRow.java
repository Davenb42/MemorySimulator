package memorysimulator;

public class FileRow {
    private int PID;
    private int pointerID;
    private int memSize;

    public FileRow(int PID, int pointerID, int memSize) {
        this.PID = PID;
        this.pointerID = pointerID;
        this.memSize = memSize;
    }

    public int getPID() {
        return PID;
    }

    public int getPointerID() {
        return pointerID;
    }

    public int getMemSize() {
        return memSize;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public void setPointerID(int pointerID) {
        this.pointerID = pointerID;
    }

    public void setMemSize(int memSize) {
        this.memSize = memSize;
    }

    @Override
    public String toString() {
        return "FileRow{" + "PID=" + PID + ", pointerID=" + pointerID + ", memSize=" + memSize + '}';
    }
}
