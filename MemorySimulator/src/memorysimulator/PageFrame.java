package memorysimulator;

// Clase que representa los marcos de página de la memoria RAM
public class PageFrame {
    private int address;

    public PageFrame(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PageFrame{" + "address=" + address + '}';
    }
}
