package memorysimulator;

public class PointerMemorySize extends Pointer{
    private final int byteSize;

    public PointerMemorySize(int pointerID, int byteSize) {
        super.pointerID = pointerID;
        this.byteSize = byteSize;
    }

    public int getPointerID() {
        return pointerID;
    }

    public int getByteSize() {
        return byteSize;
    }
}
