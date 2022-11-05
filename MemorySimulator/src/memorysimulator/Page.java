package memorysimulator;

// Clase que representa la abstracción de una página en paginación
public class Page {
    private int virtualAddress;

    public Page(int virtualAddress) {
        this.virtualAddress = virtualAddress;
    }
    
    public int getVirtualAddress() {
        return virtualAddress;
    }

    public void setVirtualAddress(int virtualAddress) {
        this.virtualAddress = virtualAddress;
    }
}
