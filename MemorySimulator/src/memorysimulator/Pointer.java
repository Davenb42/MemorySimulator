package memorysimulator;

public class Pointer {
    private final int pointerID;
    private final int byteSize;

    public Pointer(int pointerID, int byteSize) {
        this.pointerID = pointerID;
        this.byteSize = byteSize;
    }

    public int getPointerID() {
        return pointerID;
    }

    public int getByteSize() {
        return byteSize;
    }
}
