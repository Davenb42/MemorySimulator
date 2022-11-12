package memorysimulator;

import java.util.List;

public class PointerMemoryAddress extends Pointer{
    private final int logicalAddress;

    public PointerMemoryAddress(int pointerID, int logicalAddress) {
        super.pointerID = pointerID;
        this.logicalAddress = logicalAddress;
    }

    public int getPointerID() {
        return pointerID;
    }

    public int getLogicalAdresses() {
        return logicalAddress;
    }

    @Override
    public String toString() {
        return "PointerMemoryAddress{" + "pointerId=" + pointerID + ", logicalAddress=" + logicalAddress + '}';
    }
}
