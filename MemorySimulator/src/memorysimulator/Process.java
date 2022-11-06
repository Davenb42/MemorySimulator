
package memorysimulator;

import java.util.List;

public class Process {
    private final List<PointerMemorySize> memTotal;
    private final List<PointerMemoryAddress> allocatedMem;
    private final List<Integer> accessesList;

    public Process(List<PointerMemorySize> memTotal, List<PointerMemoryAddress> allocatedMem, List<Integer> accessesList) {
        this.memTotal = memTotal;
        this.allocatedMem = allocatedMem;
        this.accessesList = accessesList;
    }
    
    public void addMemSyzePointer(int pointerID, int byteSize){
        memTotal.add(new PointerMemorySize(pointerID, byteSize));
    }
    
    public void addMemAddrPointer(int pointerID, List<Integer> addresses){
        allocatedMem.add(new PointerMemoryAddress(pointerID, addresses));
    }
    
    public void addAccess(int pointerID){
        accessesList.add(pointerID);
    }

    public List<PointerMemorySize> getMemTotal() {
        return memTotal;
    }

    public List<PointerMemoryAddress> getAllocatedMem() {
        return allocatedMem;
    }

    public List<Integer> getAccessesList() {
        return accessesList;
    }
}
