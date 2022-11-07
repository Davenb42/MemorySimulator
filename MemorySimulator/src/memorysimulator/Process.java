
package memorysimulator;

import java.util.LinkedList;
import java.util.List;

public class Process {
    private final int PID;
    private final List<PointerMemorySize> memTotal;
    private final List<PointerMemoryAddress> allocatedMem;
    private final List<Integer> accessesList;

    public Process(int PID) {
        this.memTotal = new LinkedList<>();
        this.allocatedMem = new LinkedList<>();
        this.accessesList = new LinkedList<>();
        this.PID = PID;
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

    public int getPID() {
        return PID;
    }

    @Override
    public String toString() {
        return "Process{" + "PID=" + PID + ", memTotal=" + memTotal + ", allocatedMem=" + allocatedMem + ", accessesList=" + accessesList + '}';
    }
}
