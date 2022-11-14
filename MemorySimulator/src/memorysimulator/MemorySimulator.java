package memorysimulator;

import java.util.List;
import java.io.File;
import java.util.Scanner;  
import java.io.FileNotFoundException;
import static java.lang.Math.floor;
import static java.lang.Thread.sleep;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Collections;
import java.util.ArrayList;

public class MemorySimulator {
    private static int alg;
    private static int iterationCounter = 0;
    private static List<MMUPage> mmu;
    private static List<Process> processes;
    private static List<FileRow> pointers;
    private static Random random;
    private static int nextFreeLADDR = 1; // Next free logical address 
    private static int nextPageID = 1;
    private static int normalSimTime = 0;
    private static int thrashingSimTime = 0;
    
    //Opt Data
    private static List<MMUPage> mmuOpt;
    private static List<Process> processesOpt;
    private static int nextFreeLADDROpt = 1;
    private static int nextPageIDOpt = 1;
    private static int normalSimTimeOpt = 0;
    private static int thrashingSimTimeOpt = 0;
    
    
    // Obtener páginas 
    
    public static void agePages(){
        List<MMUPage> pages = loadedPages();
        for (MMUPage page:pages){
            page.setMark((int)floor(page.getMark()/2));
        }
    }
    
    public static void advanceTime(){
        List<MMUPage> pages = loadedPages();
        for (MMUPage page:pages){
            page.setLoadedT(page.getLoadedT()+1);
        }
    }
    
    // Algoritmo Optimo
    public static void replacementOptimal(MMUPage page){
        if (!page.isLoaded()){
            List<MMUPage> loadedPagesOpt = loadedPagesOpt();
            MMUPage pageToReplaceOpt = null;
            List<MMUPage> pagesToCallOpt = new LinkedList<>();
            int sizeOpt = 0;
            for (FileRow fileRow : pointers){
                if(sizeOpt >= iterationCounter+1){
                    if(findProcessOpt(fileRow.getPID())!=-1){
                        int posOpt = findProcessOpt(fileRow.getPID());
                        List<PointerMemorySize> memSizePointersOpt = processesOpt.get(posOpt).getMemTotal();
                        if (findPointer(fileRow.getPointerID(), memSizePointersOpt)!=-1){
                            int pointerPosOpt = findPointer(fileRow.getPointerID(), memSizePointersOpt);
                            List<MMUPage> pagesToCheckOpt = getPagesOpt(processesOpt.get(posOpt).getAllocatedMem().get(pointerPosOpt));
                            for(MMUPage pageToCheckOpt : pagesToCheckOpt){
                                pagesToCallOpt.add(pageToCheckOpt);
                            }
                        }
                    }
                }
                sizeOpt++;
            }
            
            //Borrar las paginas que si se llamaran de nuevo para determinar si alguna no se volverá a llamar
            for (MMUPage pageToCall : pagesToCallOpt){
                if(loadedPagesOpt.contains(pageToCall)){
                    loadedPagesOpt.remove(pageToCall);
                }
            }
            
            if(loadedPagesOpt.isEmpty()){ //No hay paginas que no se volverán a llamar
                //Rellenar las paginas cargadas
                loadedPagesOpt = loadedPages();
                //La pagina que está cargada y está más lejos de ser llamada de nuevo será la página a reemplazar 
                for (MMUPage pageToCallOpt : pagesToCallOpt){
                    if(loadedPagesOpt.contains(pageToCallOpt)){ //La página a cargar ya está cargada
                        if(pageToReplaceOpt != pageToCallOpt){ //Es diferente a la que se encontró
                            if(pageToReplaceOpt != null){ //No es la primera iteración
                                loadedPagesOpt.remove(pageToReplaceOpt); //Se encontró un candidato nuevo, no necesitamos al anterior
                            }
                            pageToReplaceOpt = pageToCallOpt; //Se asigna un nuevo candidato
                        }
                    }
                }
            }else{
                //La primer pagina que no se volverá a llamar será la página a reemplazar
                pageToReplaceOpt = loadedPagesOpt.get(0);
            }
            // Sustituir la página seleccionada por la página que se desea cambiar
            pageToReplaceOpt.setD_ADDR(getNextAvailableDADDR());
            pageToReplaceOpt.setLoaded(false);
            pageToReplaceOpt.setLoadedT(0);
            page.setM_ADDR(pageToReplaceOpt.getM_ADDR());
            page.setLoaded(true);
            
            pageToReplaceOpt.setM_ADDR(-1);
            page.setD_ADDR(-1);
            thrashingSimTimeOpt+=5;
        }else{
            normalSimTimeOpt++;
        }
    }
    
    // Algoritmo LRU de reemplazo de páginas
    public static void replacementLRU(MMUPage page){
        if (!page.isLoaded()){
            List<MMUPage> loadedPages = loadedPages();
            MMUPage pageToReplace = loadedPages.get(0);
            
            for (MMUPage loadedPage:loadedPages){
                if(pageToReplace.getLoadedT() < loadedPage.getLoadedT()){
                    pageToReplace = loadedPage;
                }
            }
            
            // Sustituir la página seleccionada por la página que se desea cambiar
            pageToReplace.setD_ADDR(getNextAvailableDADDR());
            pageToReplace.setLoaded(false);
            pageToReplace.setLoadedT(0);
            page.setM_ADDR(pageToReplace.getM_ADDR());
            page.setLoaded(true);
            
            pageToReplace.setM_ADDR(-1);
            page.setD_ADDR(-1);
            thrashingSimTime+=5;
        }else{
            page.setLoadedT(0);
            normalSimTime++;
        }
    }
    
    // Algoritmo Second chance de reemplazo de páginas
    public static void replacementSecondChance(MMUPage page){
        if (!page.isLoaded()){
            List<MMUPage> loadedPages = loadedPages();
            MMUPage pageToReplace = loadedPages.get(0);
            for (MMUPage loadedPage:loadedPages){
                if(pageToReplace.getLoadedT() < loadedPage.getLoadedT()){
                    if ( loadedPage.getMark() == 0){
                        pageToReplace = loadedPage;
                    }else{
                        loadedPage.setMark(0);
                    }
                }
            }
            
            // Sustituir la página seleccionada por la página que se desea cambiar
            pageToReplace.setD_ADDR(getNextAvailableDADDR());
            pageToReplace.setLoaded(false);
            pageToReplace.setMark(0);
            pageToReplace.setLoadedT(0);
            page.setM_ADDR(pageToReplace.getM_ADDR());
            page.setLoaded(true);
            
            pageToReplace.setM_ADDR(-1);
            page.setD_ADDR(-1);
            thrashingSimTime+=5;
        }else{
            page.setMark(1);
            normalSimTime++;
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
            thrashingSimTime+=5;
        }else{
            page.setMark(page.getMark()+128);
            normalSimTime++;
        }
    }
    
    // Algoritmo Random de reemplazo de páginas
    public static void replacementRandom(MMUPage page){
        if (!page.isLoaded()){
            int ranNum = random.nextInt(5)+1;
            List<MMUPage> pages = loadedPages();

            MMUPage pageToReplace = pages.get(ranNum);

            // Sustituir la página seleccionada por la página que se desea cambiar
            pageToReplace.setD_ADDR(getNextAvailableDADDR());
            pageToReplace.setLoaded(false);
            page.setM_ADDR(pageToReplace.getM_ADDR());
            page.setLoaded(true);

            pageToReplace.setM_ADDR(-1);
            page.setD_ADDR(-1); 
            thrashingSimTime+=5;
        }else{
            normalSimTime++;
        }
    }
    
    public static int getNextAvailableDADDR(){
        int nextAvailableDADDR = 1;
        
        List<Integer> allocatedAddresses = new LinkedList<>();       
        
        for (MMUPage mmuPage: mmu){
            if (mmuPage.getD_ADDR() != -1){
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
        List<MMUPage> completePages = new LinkedList<>(mmuOpt);
        completePages.addAll(mmu);
        return completePages;
    }

    public static List<Integer> getEmptyFrames(){
        List<Integer> emptyFrames = IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());
        for (MMUPage mmuPage: mmu){
            if (mmuPage.getM_ADDR() != -1){
                emptyFrames.remove(emptyFrames.indexOf(mmuPage.getM_ADDR()));
            }
        }
        return emptyFrames;
    }
    
    public static List<Integer> getEmptyFramesOpt(){
        List<Integer> emptyFramesOpt = IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());
        for (MMUPage mmuPageOpt: mmuOpt){
            if (mmuPageOpt.getM_ADDR() != -1){
                emptyFramesOpt.remove(emptyFramesOpt.indexOf(mmuPageOpt.getM_ADDR()));
            }
        }
        return emptyFramesOpt;
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
    
    public static List<MMUPage> loadedPagesOpt(){
        List<MMUPage> loadedPagesOpt = new LinkedList<>();
        for (MMUPage mmuPageOpt: mmuOpt){
            if (mmuPageOpt.isLoaded()){
                loadedPagesOpt.add(mmuPageOpt);
            }
        }
        return loadedPagesOpt;
    }
    
    public static int loadedPagesQuantity(){
        List<MMUPage> loadedPages = new LinkedList<>();
        for (MMUPage mmuPage: mmu){
            if (mmuPage.isLoaded()){
                loadedPages.add(mmuPage);
            }
        }
        return loadedPages.size();
    }
    
    public static int unLoadedPagesQuantity(){
        List<MMUPage> unLoadedPages = new LinkedList<>();
        for (MMUPage mmuPage: mmu){
            if (!mmuPage.isLoaded()){
                unLoadedPages.add(mmuPage);
            }
        }
        return unLoadedPages.size();
    }
    
    public static int ramKB(){
        List<MMUPage> loadedPages = new LinkedList<>();
        for (MMUPage mmuPage: mmu){
            if (mmuPage.isLoaded()){
                loadedPages.add(mmuPage);
            }
        }
        return loadedPages.size()*4;
    }
    
    public static float ramPercentage(){
        return (loadedPagesQuantity())*(100/5);
    }
    
    public static int vRamKB(){
        List<MMUPage> unLoadedPages = new LinkedList<>();
        for (MMUPage mmuPage: mmu){
            if (mmuPage.getD_ADDR()!=-1){
                unLoadedPages.add(mmuPage);
            }
        }
        return unLoadedPages.size()*4;
    }
    
    public static float vRamPercentage(){
        List<MMUPage> loadedPages = new LinkedList<>();
        for (MMUPage mmuPage: mmu){
            if (mmuPage.getD_ADDR()!=-1){
                loadedPages.add(mmuPage);
            }
        }
        return (loadedPages.size()*4)*(100/1000000);
    }
    
    public static int processesQuantity(){
        return processes.size();
    }
    
    public static int getSimTime(){
        return normalSimTime+thrashingSimTime;
    }
    
    public static int getThrashingSimTime(){
        return thrashingSimTime;
    }
    
    public static float getThrashingPercentage(){
        return thrashingSimTime*(100/getSimTime());
    }
    
    // Opt datos
    public static int loadedPagesQuantityOpt(){
        List<MMUPage> loadedPagesOpt = new LinkedList<>();
        for (MMUPage mmuPageOpt: mmuOpt){
            if (mmuPageOpt.isLoaded()){
                loadedPagesOpt.add(mmuPageOpt);
            }
        }
        return loadedPagesOpt.size();
    }
    
    public static int unLoadedPagesQuantityOpt(){
        List<MMUPage> unLoadedPagesOpt = new LinkedList<>();
        for (MMUPage mmuPageOpt: mmuOpt){
            if (!mmuPageOpt.isLoaded()){
                unLoadedPagesOpt.add(mmuPageOpt);
            }
        }
        return unLoadedPagesOpt.size();
    }
    
    public static int ramKBOpt(){
        List<MMUPage> loadedPagesOpt = new LinkedList<>();
        for (MMUPage mmuPageOpt: mmuOpt){
            if (mmuPageOpt.isLoaded()){
                loadedPagesOpt.add(mmuPageOpt);
            }
        }
        return loadedPagesOpt.size()*4;
    }
    
    public static float ramPercentageOpt(){
        return (loadedPagesQuantityOpt())*(100/5);
    }
    
    public static int vRamKBOpt(){
        List<MMUPage> unLoadedPagesOpt = new LinkedList<>();
        for (MMUPage mmuPageOpt: mmuOpt){
            if (mmuPageOpt.getD_ADDR()!=-1){
                unLoadedPagesOpt.add(mmuPageOpt);
            }
        }
        return unLoadedPagesOpt.size()*4;
    }
    
    public static float vRamPercentageOpt(){
        List<MMUPage> loadedPagesOpt = new LinkedList<>();
        for (MMUPage mmuPageOpt: mmuOpt){
            if (mmuPageOpt.getD_ADDR()!=-1){
                loadedPagesOpt.add(mmuPageOpt);
            }
        }
        return (loadedPagesOpt.size()*4)*(100/1000000);
    }
    
    public static int processesQuantityOpt(){
        return processesOpt.size();
    }
    
    public static int getSimTimeOpt(){
        return normalSimTimeOpt+thrashingSimTimeOpt;
    }
    
    public static int getThrashingSimTimeOpt(){
        return thrashingSimTimeOpt;
    }
    
    public static float getThrashingPercentageOpt(){
        return thrashingSimTimeOpt*(100/getSimTimeOpt());
    }
    
    public static void loadPages(List<MMUPage> pages) throws InterruptedException{
        for (MMUPage page: pages){
            loadPage(page);
        }
    }
    
    public static void loadPagesOpt(List<MMUPage> pagesOpt){
        for (MMUPage pageOpt: pagesOpt){
            loadPageOpt(pageOpt);
        }
    }
    
    public static void loadPage(MMUPage page) throws InterruptedException{
        List<Integer> emptyFrames = getEmptyFrames();
        if (!emptyFrames.isEmpty()){
            if (!page.isLoaded()){
                page.setM_ADDR(emptyFrames.get(0));
                page.setD_ADDR(-1);
                page.setLoaded(true);
            }else{
                switch(alg){
                    case 1 -> {
                        page.setLoadedT(0);
                    }
                    case 2 -> {
                        page.setMark(1);
                    }
                    case 3 -> {
                        page.setMark(page.getMark()+128);
                    }
                }
            }
            sleep(1000);
            normalSimTime++;
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
            sleep(5000);
        }
    }
    
    public static void loadPageOpt(MMUPage pageOpt){
        List<Integer> emptyFramesOpt = getEmptyFramesOpt();
        if (!emptyFramesOpt.isEmpty()){
            if (!pageOpt.isLoaded()){
                pageOpt.setM_ADDR(emptyFramesOpt.get(0));
                pageOpt.setD_ADDR(-1);
                pageOpt.setLoaded(true);
            }
            normalSimTimeOpt++;
        }else{
            // Reemplazar páginas
            replacementOptimal(pageOpt);
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
    
    public static List<MMUPage> getPagesOpt(PointerMemoryAddress address){
        int logicalAddress = address.getLogicalAdresses();
        List<MMUPage> pagesToLoadOpt = new LinkedList<>();
            for (MMUPage pageOpt: mmuOpt){
                if(pageOpt.getL_ADDR() == logicalAddress){
                    pagesToLoadOpt.add(pageOpt);
                }
            }
        return pagesToLoadOpt;
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
                    initialPointers.add(new FileRow(Integer.parseInt(parts[0].replace(" ", "").replace("\t", "")), Integer.parseInt(parts[1].replace(" ", "").replace("\t", "")), Integer.parseInt(parts[2].replace(" ", "").replace("\t", ""))));
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
    
    public static int findProcessOpt(int PID){
        int exists = -1;
        for (Process processOpt : processesOpt){
            if(processOpt.getPID() == PID) exists = processesOpt.indexOf(processOpt);
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
        mmuOpt = new LinkedList();
        processesOpt = new LinkedList();
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
        // Alg Part
        Process process;
        Process processOpt;
        
        
        int procPos = findProcess(row.getPID());
        
        int procPosOpt = findProcessOpt(row.getPID());
        if(procPos == -1){
            process = new Process(row.getPID());
            processes.add(process);
        } else {
            process = processes.get(procPos);
        }
        if(procPosOpt == -1){
            processOpt = new Process(row.getPID());
            processesOpt.add(processOpt);
        } else {
            processOpt = processesOpt.get(procPosOpt);
        }

        PointerMemoryAddress pointer;
        int pointerPos = findPointer(row.getPointerID(), process.getMemTotal());

        if (pointerPos == -1){

            process.addMemSyzePointer(row.getPointerID(), row.getMemSize());

            process.addMemAddrPointer(row.getPointerID(), nextFreeLADDR);
            int size = row.getMemSize();
            while(row.getMemSize() >= 0){
                MMUPage mmuPage = new MMUPage(nextPageID, process.getPID(), false, nextFreeLADDR, -1, -1, 0, 0);
                mmu.add(mmuPage);
                row.setMemSize(row.getMemSize()-4096);
                nextPageID++;
            }
            row.setMemSize(size);
            nextFreeLADDR++;
        } 
        
        PointerMemoryAddress pointerOpt;
        int pointerPosOpt = findPointer(row.getPointerID(), processOpt.getMemTotal());
        
        if (pointerPosOpt == -1){

            processOpt.addMemSyzePointer(row.getPointerID(), row.getMemSize());

            processOpt.addMemAddrPointer(row.getPointerID(), nextFreeLADDROpt);

            int sizeOpt = row.getMemSize();
            while(row.getMemSize() >= 0){
                MMUPage mmuPageOpt = new MMUPage(nextPageIDOpt, processOpt.getPID(), false, nextFreeLADDROpt, -1, -1, 0, 0);
                mmuOpt.add(mmuPageOpt);
                row.setMemSize(row.getMemSize()-4096);
                nextPageIDOpt++;
            }
            row.setMemSize(sizeOpt);
            nextFreeLADDROpt++;
        } 
        
        
        pointerPos = findPointer(row.getPointerID(), process.getMemTotal());

        pointer = process.getAllocatedMem().get(pointerPos); // Obtener LADDR del puntero actual
        
        pointerPosOpt = findPointer(row.getPointerID(), processOpt.getMemTotal());

        pointerOpt = processOpt.getAllocatedMem().get(pointerPosOpt); // Obtener LADDR del puntero actual

        
        // Obtener páginas de la MMU a asignar
        List<MMUPage> pagesToLoad = getPages(pointer);
        switch(alg){
            case 1 -> {
                advanceTime();
            }
            case 2 -> {
                advanceTime();
            }
            case 3 -> {
                agePages();
            }
        }
        
        loadPages(pagesToLoad);
        
        List<MMUPage> pagesToLoadOpt = getPagesOpt(pointerOpt);

        
        loadPagesOpt(pagesToLoadOpt);
        
        iterationCounter++;
        //sleep(5000);
        //System.out.println(mmu.toString());
    }
    
    /*public static void main(String args[]){
        File file = new File("C:\\Users\\Dell\\Downloads\\procesos.txt");
        Random ran = new Random();
        ran.setSeed(12345L); // Establecer semilla para los valores randomizados
        int algorithm = 4;
        try {
            executeSimulator(file, ran, algorithm);
        } catch (InterruptedException ex) {
            Logger.getLogger(MemorySimulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */
}
