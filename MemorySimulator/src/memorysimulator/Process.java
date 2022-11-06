
package memorysimulator;

import java.util.List;

public class Process {
    private final List<PointerMemorySize> totalMem;

    public Process(List<PointerMemorySize> totalMem) {
        this.totalMem = totalMem;
    }
    
    public void addPointer(int pointerID, int byteSize){
        totalMem.add(new PointerMemorySize(pointerID, byteSize));
    }
}
