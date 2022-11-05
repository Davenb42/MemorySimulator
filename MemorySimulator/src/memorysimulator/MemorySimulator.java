
package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class MemorySimulator {

    public static List<String> extractPointers(String fileName){
        List<String> pointers = new LinkedList<>();
        
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
              String data = reader.nextLine();
              System.out.println(data);
            }
            reader.close();
          } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado");
          }
        
        return pointers;
    }
    
    public static void main(String[] args) {
        String fileName = "C:\\Users\\Reyner\\Downloads\\procesos.txt";
        List<String> res = extractPointers(fileName);
    }
}
