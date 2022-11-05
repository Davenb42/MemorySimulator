
package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class MemorySimulator {
    private static List<PageFrame> ram; 
    private static List<Page> hardDisk; 

    // Método para extraer los punteros de la lista
    public static List<String> extractPointers(String fileName){
        List<String> pointers = new LinkedList<>();
        
        try {
            File file = new File(fileName);
            try (Scanner reader = new Scanner(file)) {
                reader.nextLine(); // Ignorar primera línea de títulos
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
    
    public static void main(String[] args) {
        // Inicializaciones
        ram = new LinkedList<>();
        hardDisk = new LinkedList();
        
        // Rellenar la lista de marcos de página
        for(int addressCounter = 0; addressCounter < 100; addressCounter++){
            PageFrame pageFrame = new PageFrame(addressCounter*4096);
            ram.add(pageFrame);
        }
        
        // Leer archivo de punteros
        String fileName = "C:\\Users\\Reyner\\Downloads\\procesos.txt";
        
        List<String> pointers = extractPointers(fileName);
        System.out.println(ram.toString());
    }
}
