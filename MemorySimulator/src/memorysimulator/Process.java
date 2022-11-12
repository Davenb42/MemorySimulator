
package memorysimulator;

import java.util.LinkedList;
import java.util.List;

public class Process {
    private final int PID;
    private final List<PointerMemorySize> memTotal;
    private final List<PointerMemoryAddress> allocatedMem;

    public Process(int PID) {
        this.memTotal = new LinkedList<>();
        this.allocatedMem = new LinkedList<>();
        this.PID = PID;
    }
    
    public void addMemSyzePointer(int pointerID, int byteSize){
        memTotal.add(new PointerMemorySize(pointerID, byteSize));
    }
    
    public void addMemAddrPointer(int pointerID, int logicalAddress){
        allocatedMem.add(new PointerMemoryAddress(pointerID, logicalAddress));
    }

    public List<PointerMemorySize> getMemTotal() {
        return memTotal;
    }

    public List<PointerMemoryAddress> getAllocatedMem() {
        return allocatedMem;
    }

    public int getPID() {
        return PID;
    }

    @Override
    public String toString() {
        return "Process{" + "PID=" + PID + ", memTotal=" + memTotal + ", allocatedMem=" + allocatedMem + '}';
    }
}
