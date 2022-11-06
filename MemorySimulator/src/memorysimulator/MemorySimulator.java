package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class MemorySimulator {
    private static List<MMUPage> mmu;
    private static List<Process> processes;
    private static List<FileRow> pointers;

    // Método para extraer los punteros de la lista
    public static List<FileRow> extractPointers(File file){
        List<FileRow> pointers = new LinkedList<>();
        String[] parts;
        
        try {
            try (Scanner reader = new Scanner(file)) {
                reader.nextLine(); // Ignorar primera lÃ­nea de tÃ­tulos
                while (reader.hasNextLine()) {
                    String pointer = reader.nextLine();
                    parts = pointer.split(",");
                    pointers.add(new FileRow(Integer.parseInt(parts[0].replace(" ", "")), Integer.parseInt(parts[1].replace(" ", "")), Integer.parseInt(parts[2].replace(" ", ""))));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado");
        }
        
        return pointers;
    }
    
    public static void executeSimulator(File file) {
        // Inicializaciones
        mmu = new LinkedList();
        
        // Leer archivo de punteros
        pointers = extractPointers(file);
        // System.out.println(pointers.toString());
        
        
    }
    
    public static void main(String args[]){
        File file = new File("C:\\Users\\Reyner\\Downloads\\procesos.txt");
        executeSimulator(file);
    }
}
