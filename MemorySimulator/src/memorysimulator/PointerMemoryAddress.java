package memorysimulator;

import java.util.List;

public class PointerMemoryAddress extends Pointer{
    private final List<Integer> addresses;

    public PointerMemoryAddress(int pointerID, List<Integer> addresses) {
        super.pointerID = pointerID;
        this.addresses = addresses;
    }

    public int getPointerID() {
        return pointerID;
    }

    public List<Integer> getAdresses() {
        return addresses;
    }
}
