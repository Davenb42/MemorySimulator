package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class MemorySimulator {
    private static List<PageFrame> ramOpt; 
    private static List<PageFrame> ramAlg; 
    private static List<Page> hardDisk; 

    // Método para extraer los punteros de la lista
    public static List<String> extractPointers(File file){
        List<String> pointers = new LinkedList<>();
        
        try {
            try (Scanner reader = new Scanner(file)) {
                reader.nextLine(); // Ignorar primera lÃ­nea de tÃ­tulos
                while (reader.hasNextLine()) {
                    String pointer = reader.nextLine();
                    pointers.add(pointer);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado");
        }
        
        return pointers;
    }
    
    public static void executeSimulator(File file) {
        // Inicializaciones
        ramOpt = new LinkedList<>();
        ramAlg = new LinkedList<>();
        hardDisk = new LinkedList();
        
        // Rellenar la lista de marcos de pÃ¡gina
        for(int addressCounter = 0; addressCounter < 100; addressCounter++){
            PageFrame pageFrame = new PageFrame(addressCounter);
            ramOpt.add(pageFrame);
        }
        
        // Leer archivo de punteros
        
        List<String> pointers = extractPointers(file);
        System.out.println(ramOpt.toString());
    }
    
    public static void main(String args[]){
        File file = new File("C:\\Users\\Reyner\\Downloads\\procesos.txt");
        executeSimulator(file);
    }
}
