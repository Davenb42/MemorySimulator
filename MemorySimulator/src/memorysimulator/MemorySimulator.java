package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemorySimulator {
    private static List<MMUPage> mmu;
    private static List<Process> processes;
    private static List<FileRow> pointers;
    private static Random random;
    private static int nextFreeDADDR = 1; // Next free disk address 
    
    public static List<Integer> emptyFrames(){
        List<Integer> emptyFrames = IntStream.rangeClosed(0, 99).boxed().collect(Collectors.toList());
        for (MMUPage mmuPage: mmu){
            if (mmuPage.getM_ADDR() != -1){
                emptyFrames.remove(mmuPage.getM_ADDR());
            }
        }
        return emptyFrames;
    }

    public static List<MMUPage> loadedPages(){
        List<MMUPage> loadedPages = new LinkedList<>();
        for (MMUPage mmuPage: mmu){
            if (mmuPage.isLoaded()){
                loadedPages.add(mmuPage);
            }
        }
        return loadedPages;
    }
    
    public static void loadPages(List<MMUPage> pages){
        for (MMUPage page: pages){
            loadPage(page);
        }
    }
    
    public static boolean loadPage(MMUPage page){
        List<Integer> emptyFrames = emptyFrames();
        if (!emptyFrames.isEmpty()){
            if (page.isLoaded()){
                page.setM_ADDR(emptyFrames.get(0));
                page.setD_ADDR(-1);
                page.setLoaded(true);
            }
            return true;
        }else{
            return false;
        }
    }
    
    public static Process getProcess(int PID){
        for (Process process: processes){
            if (process.getPID()==PID){
                return process;
            }
        }
        return null;
    }
    
    
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
    
    // Revisar si ya se creo un proceso
    public static int findProcess(int PID){
        int exists = -1;
        
        for (Process process : processes){
            if(process.getPID() == PID) exists = processes.indexOf(process);
        }
        
        return exists;
    }
    
    // Método para barajar (a veces duplicar) las lista de procesos ingresada por parámetro
    public static void shuffleList(List<FileRow> initialPointers){
        int ranIndex;
        List<FileRow> copy = new LinkedList<>(initialPointers);
        
        while(!copy.isEmpty()){
            ranIndex = random.nextInt(initialPointers.size()*2)%initialPointers.size();
            if(pointers.isEmpty()) { // Caso inicial
                pointers.add(initialPointers.get(ranIndex));
                copy.remove(initialPointers.get(ranIndex));
            } else {
                FileRow chosenRow = initialPointers.get(ranIndex);
                int rowPos = pointers.size();
                for(FileRow row : pointers){
                    // Insertar el puntero actual si este tiene un pointerID menor que la variable row, además estos deben tener el mismo PID
                    if(row.getPID() == initialPointers.get(ranIndex).getPID() && row.getPointerID() > initialPointers.get(ranIndex).getPointerID()){
                        rowPos = pointers.indexOf(row);
                        break;
                    }
                }
                pointers.add(rowPos, chosenRow);
                copy.remove(initialPointers.get(ranIndex));
            }
        }
    }
    
    public static void executeSimulator(File file, Random ran) {
        // Inicializaciones
        mmu = new LinkedList();
        pointers = new LinkedList();
        processes = new LinkedList();
        random = ran;
        
        
        // Leer archivo de punteros y rellenar información de los procesos
        List<FileRow> initialPointers = extractPointers(file);
        
        for (FileRow row : initialPointers){
            Process process;
            int procPos = findProcess(row.getPID());
            
            if(procPos == -1){
                process = new Process(row.getPID());
                processes.add(process);
            } else {
                process = processes.get(procPos);
            }
            process.addMemSyzePointer(row.getPointerID(), row.getMemSize());
            
            List<Integer> addresses = new LinkedList<>();
            
            while(row.getMemSize() >= 0){
                addresses.add(nextFreeDADDR++);
                row.setMemSize(row.getMemSize()-4096);
            }
            
            process.addMemAddrPointer(row.getPointerID(), addresses);
            
        }
        
        // Barajar archivo de punteros y agregar accesos
        shuffleList(initialPointers); 
        
        for (FileRow row : pointers){
            Process process;
            int procPos = findProcess(row.getPID());
            process = processes.get(procPos);
            
            process.addAccess(row.getPointerID());
        }
        
        System.out.println(processes.toString());
    }
    
    public static void main(String args[]){
        File file = new File("C:\\Users\\Reyner\\Downloads\\procesos.txt");
        Random ran = new Random();
        ran.setSeed(12345L); // Establecer semilla para los valores randomizados
        executeSimulator(file, ran);
    }
}
