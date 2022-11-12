package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import static java.lang.Math.floor;
import static java.lang.Thread.sleep;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MemorySimulator {
    private static int alg;
    private static List<MMUPage> mmu;
    private static List<Process> processes;
    private static List<FileRow> pointers;
    private static Random random;
    private static int nextFreeLADDR = 1; // Next free logical address 
    private static int nextPageID = 1;
    
    
    // Obtener páginas 
    
    // Algoritmo LRU de reemplazo de páginas
    public static void replacementLRU(MMUPage page){
        
    }
    
    public static void agePages(){
        List<MMUPage> pages = loadedPages();
        for (MMUPage page:pages){
            page.setMark((int)floor(page.getMark()/2));
        }
    }
    
    // Algoritmo Second chance de reemplazo de páginas
    public static void replacementSecondChance(MMUPage page){
        if (!page.isLoaded()){
            
        }else{
            page.setMark(1);
        }
    }
    
    // Algoritmo Aging de reemplazo de páginas
    public static void replacementAging(MMUPage page){
        if (!page.isLoaded()){
            List<MMUPage> loadedPages = loadedPages();
            MMUPage pageToReplace = loadedPages.get(0);
            for (MMUPage loadedPage:loadedPages){
                if(pageToReplace.getMark() > loadedPage.getMark()){
                    pageToReplace = loadedPage;
                }
            }
            
            // Sustituir la página seleccionada por la página que se desea cambiar
            pageToReplace.setD_ADDR(getNextAvailableDADDR());
            pageToReplace.setLoaded(false);
            pageToReplace.setMark(0);
            page.setM_ADDR(pageToReplace.getM_ADDR());
            page.setLoaded(true);
            
            pageToReplace.setM_ADDR(-1);
            page.setD_ADDR(-1); 
        }else{
            page.setMark(page.getMark()+128);
        }
    }
    
    // Algoritmo Random de reemplazo de páginas
    public static void replacementRandom(MMUPage page){
        int ranNum = random.nextInt(100)+1;
        List<MMUPage> pages = loadedPages();
        
        MMUPage pageToReplace = pages.get(ranNum);
        
        // Sustituir la página seleccionada por la página que se desea cambiar
        pageToReplace.setD_ADDR(getNextAvailableDADDR());
        pageToReplace.setLoaded(false);
        page.setM_ADDR(pageToReplace.getM_ADDR());
        page.setLoaded(true);
        
        pageToReplace.setM_ADDR(-1);
        page.setD_ADDR(-1); 
    }
    
    public static int getNextAvailableDADDR(){
        int nextAvailableDADDR = 1;
        
        List<Integer> allocatedAddresses = new LinkedList<>();       
        
        for (MMUPage mmuPage: mmu){
            if (mmuPage.getM_ADDR() != -1){
                allocatedAddresses.add(mmuPage.getD_ADDR());
            }
        }
        
        Collections.sort(allocatedAddresses);
        
        for (Integer D_ADDR : allocatedAddresses){
            if (D_ADDR == nextAvailableDADDR) nextAvailableDADDR++;
            else break;
        }
        
        return nextAvailableDADDR;
    }

    public static List<MMUPage> getMMU(){
        return mmu;
    }

    public static List<Integer> emptyFrames(){
        List<Integer> emptyFrames = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());
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
    
    public static void loadPage(MMUPage page){
        List<Integer> emptyFrames = emptyFrames();
        if (!emptyFrames.isEmpty()){
            if (!page.isLoaded()){
                page.setM_ADDR(emptyFrames.get(0));
                page.setD_ADDR(-1);
                page.setLoaded(true);
            }else{
                switch(alg){
                    case 2 -> {
                        page.setMark(1);
                    }
                    case 3 -> {
                        page.setMark(page.getMark()+128);
                    }
                }
            }
        }else{
            // Reemplazar páginas
            switch(alg){
                case 1 -> {
                    replacementLRU(page);
                }
                case 2 -> {
                    replacementSecondChance(page);
                }
                case 3 -> {
                    replacementAging(page);
                }
                case 4 -> {
                    replacementRandom(page);
                }
            }
        }
    }
    
    public static List<MMUPage> getPages(PointerMemoryAddress address){
        int logicalAddress = address.getLogicalAdresses();
        List<MMUPage> pagesToLoad = new LinkedList<>();
            for (MMUPage page: mmu){
                if(page.getL_ADDR() == logicalAddress){
                    pagesToLoad.add(page);
                }
            }
        return pagesToLoad;
    }
    
    // Método para extraer los punteros de la lista
    public static List<FileRow> extractPointers(File file){
        List<FileRow> initialPointers = new LinkedList<>();
        String[] parts;
        
        try {
            try (Scanner reader = new Scanner(file)) {
                reader.nextLine(); // Ignorar primera lÃ­nea de tÃ­tulos
                while (reader.hasNextLine()) {
                    String pointer = reader.nextLine();
                    parts = pointer.split(",");
                    initialPointers.add(new FileRow(Integer.parseInt(parts[0].replace(" ", "")), Integer.parseInt(parts[1].replace(" ", "")), Integer.parseInt(parts[2].replace(" ", ""))));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado");
        }
        
        return initialPointers;
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
    
    // Revisar si ya se creo un puntero
    public static int findPointer(int pointerID, List<PointerMemorySize> memSizePointers){
        int pointerPos = -1;
        
        for (PointerMemorySize pointer : memSizePointers){
            if(pointer.getPointerID() == pointerID) pointerPos = memSizePointers.indexOf(pointer);
        }
        
        return pointerPos;
    }
    
    public static void initializeSimulator(File file, Random ran, int algorithm){
        // Inicializaciones
        mmu = new LinkedList();
        pointers = new LinkedList();
        processes = new LinkedList();
        random = ran;
        alg = algorithm;
        
        // Leer archivo de punteros y rellenar información de los procesos
        List<FileRow> initialPointers = extractPointers(file);
        
        // Barajar archivo de punteros y agregar accesos
        shuffleList(initialPointers); 
    }
    
    public static List<FileRow> getPointers(){
        return pointers;
    }
    
    // Ciclo principal para recorrer la lista de punteros
    public static void executeNextIteration(FileRow row) throws InterruptedException{
        Process process;
        int procPos = findProcess(row.getPID());

        if(procPos == -1){
            process = new Process(row.getPID());
            processes.add(process);
        } else {
            process = processes.get(procPos);
        }

        PointerMemoryAddress pointer;
        int pointerPos = findPointer(row.getPointerID(), process.getMemTotal());

        if (pointerPos == -1){

            process.addMemSyzePointer(row.getPointerID(), row.getMemSize());

            process.addMemAddrPointer(row.getPointerID(), nextFreeLADDR);
            while(row.getMemSize() >= 0){
                MMUPage mmuPage = new MMUPage(nextPageID, process.getPID(), false, nextFreeLADDR, -1, -1, 0, 0);
                mmu.add(mmuPage);
                row.setMemSize(row.getMemSize()-4096);
                nextPageID++;
            }

            nextFreeLADDR++;
        } 
        pointerPos = findPointer(row.getPointerID(), process.getMemTotal());

        pointer = process.getAllocatedMem().get(pointerPos); // Obtener LADDR del puntero actual

        // Obtener páginas de la MMU a asignar
        List<MMUPage> pagesToLoad = getPages(pointer);
        switch(alg){
                case 3 -> {
                    agePages();
                }
            }
        loadPages(pagesToLoad);

        sleep(4000);

        System.out.println(mmu.toString());
    }
    
    public static void main(String args[]){
        File file = new File("C:\\Users\\Reyner\\Downloads\\procesos.txt");
        Random ran = new Random();
        ran.setSeed(12345L); // Establecer semilla para los valores randomizados
        int algorithm = 4;
        /*
        try {
            executeSimulator(file, ran, algorithm);
        } catch (InterruptedException ex) {
            Logger.getLogger(MemorySimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
}
