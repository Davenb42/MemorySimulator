package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;

public class MemorySimulator {
    private static List<MMUPage> mmu;
    private static List<Process> processes;
    private static List<FileRow> pointers;
    private static Random random;

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
    
    public static void shuffleList(List<FileRow> initialPointers){
        int ranIndex;
        List<FileRow> copy = new LinkedList<>(initialPointers);
        
        while(!copy.isEmpty()){
            ranIndex = random.nextInt(initialPointers.size()*2)%initialPointers.size();
            for(FileRow row : pointers){
                if(row.getPID() == initialPointers.get(ranIndex).getPID()){
                    pointers.add(initialPointers.get(ranIndex));
                    copy.remove(initialPointers.get(ranIndex));
                }
            }
        }
    }
    
    public static void executeSimulator(File file, Random ran) {
        // Inicializaciones
        mmu = new LinkedList();
        pointers = new LinkedList();
        random = ran;
        // Leer archivo de punteros
        List<FileRow> initialPointers = extractPointers(file);
        shuffleList(initialPointers);
        System.out.println(pointers.toString());
    }
    
    public static void main(String args[]){
        File file = new File("C:\\Users\\Reyner\\Downloads\\procesos.txt");
        Random ran = new Random();
        ran.setSeed(12345L); // Establecer semilla para los valores randomizados
        executeSimulator(file, ran);
    }
}
