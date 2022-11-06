package memorysimulator;

public class MMUPage {
    private int pageID;
    private int PID;
    private boolean loaded;
    private int L_ADDR;
    private int M_ADDR;
    private int D_ADDR;
    private int loadedT;
    private int mark;

    public MMUPage(int pageID, int PID, boolean loaded, int L_ADDR, int M_ADDR, int D_ADDR, int loadedT, int mark) {
        this.pageID = pageID;
        this.PID = PID;
        this.loaded = loaded;
        this.L_ADDR = L_ADDR;
        this.M_ADDR = M_ADDR;
        this.D_ADDR = D_ADDR;
        this.loadedT = loadedT;
        this.mark = mark;
    }
    
    public int getPageID() {
        return pageID;
    }

    public int getPID() {
        return PID;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public int getL_ADDR() {
        return L_ADDR;
    }

    public int getM_ADDR() {
        return M_ADDR;
    }

    public int getD_ADDR() {
        return D_ADDR;
    }

    public int getLoadedT() {
        return loadedT;
    }

    public int getMark() {
        return mark;
    }

    public void setPageID(int pageID) {
        this.pageID = pageID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setL_ADDR(int L_ADDR) {
        this.L_ADDR = L_ADDR;
    }

    public void setM_ADDR(int M_ADDR) {
        this.M_ADDR = M_ADDR;
    }

    public void setD_ADDR(int D_ADDR) {
        this.D_ADDR = D_ADDR;
    }

    public void setLoadedT(int loadedT) {
        this.loadedT = loadedT;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }    
}
