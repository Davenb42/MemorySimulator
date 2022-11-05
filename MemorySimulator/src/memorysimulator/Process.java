
package memorysimulator;

import java.util.List;

public class Process {
    private List<Pointer> totalMem;

    public Process(List<Pointer> totalMem) {
        this.totalMem = totalMem;
    }
    
    public void addPointer(int pointerID, int byteSize){
        totalMem.add(new Pointer(pointerID, byteSize));
    }
}
