package memorysimulator;

public class PointerMemorySize {
    private final int pointerID;
    private final int byteSize;

    public PointerMemorySize(int pointerID, int byteSize) {
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
