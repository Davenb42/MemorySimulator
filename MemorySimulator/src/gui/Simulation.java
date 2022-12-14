/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import memorysimulator.FileRow;
import memorysimulator.MMUPage;
import memorysimulator.MemorySimulator;

/**
 *
 * @author XPC
 */
public class Simulation extends javax.swing.JFrame {

    /**
     * Creates new form Simulation
     */
    public Simulation() {
        initComponents();
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        for (int i = 0; i < tblRamOpt.getColumnModel().getColumnCount(); i++) {
            TableColumn column = tblRamOpt.getColumnModel().getColumn(i);
            column.setMaxWidth(1);
        }
        for (int i = 0; i < tblRamAlg.getColumnModel().getColumnCount(); i++) {
            TableColumn column = tblRamAlg.getColumnModel().getColumn(i);
            column.setMaxWidth(1);
        }
    }
    
    public void start(File file, Long seed, int algorithm){
        Random ran = new Random();
        ran.setSeed(seed); // Establecer semilla para los valores randomizados
        MemorySimulator.initializeSimulator(file, ran, algorithm);
    }
    
    public void startWorker(){
        SwingWorker w = new SwingWorker(){
            // Method
            @Override
            protected String doInBackground() throws Exception {
                List<FileRow> pointers = MemorySimulator.getPointers();
                // Defining what thread will do here
                for (FileRow row:pointers) {
                    
                    Thread.sleep(100);
                    try {
                        MemorySimulator.executeNextIteration(row);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    publish(MemorySimulator.getMMU());
                }
  
                String res = "Finished Execution";
                return res;
            }
  
            // Method
            @Override 
            protected void process(List mmu)
            {
                // Opt cargar datos
                int counter = 0;
                DefaultTableModel model = new DefaultTableModel();
                LinkedList<MMUPage> pages = (LinkedList<MMUPage>)mmu.get(mmu.size()-1);
                System.out.println(pages.toString());
                model.addColumn("PAGE ID"); 
                model.addColumn("PID");
                model.addColumn("LOADED"); 
                model.addColumn("L-ADDR");
                model.addColumn("M-ADDR");
                model.addColumn("D-ADDR");
                model.addColumn("LOADED-T");
                model.addColumn("MARK");
                for(MMUPage page : pages){
                    if(counter < pages.size()/2) {
                        model.addRow(new Object[]
                        {page.getPageID(), page.getPID(), page.isLoaded(), page.getL_ADDR(), 
                            page.getM_ADDR(), page.getD_ADDR(), page.getLoadedT(), page.getMark()});
                        counter++;
                    }
                }
                tblMmuOpt.setModel(model);
                
                counter = 0;
                model = new DefaultTableModel();
                model.addColumn("PAGE ID"); 
                model.addColumn("PID");
                model.addColumn("LOADED"); 
                model.addColumn("L-ADDR");
                model.addColumn("M-ADDR");
                model.addColumn("D-ADDR");
                model.addColumn("LOADED-T");
                model.addColumn("MARK");
                for(MMUPage page : pages){
                    if(counter >= pages.size()/2) {
                        model.addRow(new Object[]
                        {page.getPageID(), page.getPID(), page.isLoaded(), page.getL_ADDR(), 
                            page.getM_ADDR(), page.getD_ADDR(), page.getLoadedT(), page.getMark()});
                    }
                    counter++;
                }
                tblMmuAlg.setModel(model);
                
                model = new DefaultTableModel();
                model.addColumn("Processes");
                model.addColumn("Sim-Time");
                model.addRow(new Object[]{MemorySimulator.processesQuantityOpt(), MemorySimulator.getSimTimeOpt()+"s"});
                tblSimTimeOpt.setModel(model);
                model = new DefaultTableModel();
                model.addColumn("RAM KB");
                model.addColumn("RAM %");
                model.addColumn("V-RAM KB");
                model.addColumn("V-RAM %");
                model.addRow(new Object[]{MemorySimulator.ramKBOpt(), MemorySimulator.ramPercentageOpt()+"%",MemorySimulator.vRamKBOpt(),MemorySimulator.vRamPercentageOpt()+"%"});
                tblRamStatsOpt.setModel(model);
                model = new DefaultTableModel();
                model.addColumn("Loaded");
                model.addColumn("Unloaded");
                model.addColumn("Thrashing");
                model.addColumn("Thrashing %");
                model.addColumn("Fragmentaci??n");
                model.addRow(new Object[]{MemorySimulator.loadedPagesQuantityOpt(), MemorySimulator.unLoadedPagesQuantityOpt(),MemorySimulator.getThrashingSimTimeOpt()+"s",MemorySimulator.getThrashingPercentageOpt()+"%",0});
                tblFragmentationOpt.setModel(model);
                
                model = new DefaultTableModel();
                model.addColumn("Processes");
                model.addColumn("Sim-Time");
                model.addRow(new Object[]{MemorySimulator.processesQuantity(), MemorySimulator.getSimTime()+"s"});
                tblSimTimeAlg.setModel(model);
                model = new DefaultTableModel();
                model.addColumn("RAM KB");
                model.addColumn("RAM %");
                model.addColumn("V-RAM KB");
                model.addColumn("V-RAM %");
                model.addRow(new Object[]{MemorySimulator.ramKB(), MemorySimulator.ramPercentage()+"%",MemorySimulator.vRamKB(),MemorySimulator.vRamPercentage()+"%"});
                tblRamStatsAlg.setModel(model);
                model = new DefaultTableModel();
                model.addColumn("Loaded");
                model.addColumn("Unloaded");
                model.addColumn("Thrashing");
                model.addColumn("Thrashing %");
                model.addColumn("Fragmentaci??n");
                model.addRow(new Object[]{MemorySimulator.loadedPagesQuantity(), MemorySimulator.unLoadedPagesQuantity(),MemorySimulator.getThrashingSimTime()+"s",MemorySimulator.getThrashingPercentage()+"%",0});
                tblFragmentationAlg.setModel(model);
                
                
            }
        };
        // Executes the swingworker on worker thread
        w.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblRamOpt = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        lblRamAlg = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRamAlg = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblMmuOpt = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMmuAlg = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblFragmentationOpt = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblRamStatsOpt = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblSimTimeOpt = new javax.swing.JTable();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblSimTimeAlg = new javax.swing.JTable();
        jScrollPane14 = new javax.swing.JScrollPane();
        tblRamStatsAlg = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblFragmentationAlg = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblRamOpt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
            }
        ));
        tblRamOpt.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblRamOpt);

        jLabel1.setText("RAM - Opt");

        lblRamAlg.setText("RAM - [ALG]");

        tblRamAlg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
            }
        ));
        tblRamAlg.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(tblRamAlg);

        tblMmuOpt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "PAGE ID", "PID", "LOADED", "L-ADDR", "M-ADDR", "D-ADDR", "LOADED-T", "MARK"
            }
        ));
        jScrollPane3.setViewportView(tblMmuOpt);

        jLabel2.setText("MMU - OPT");

        jLabel3.setText("MMU - [ALG]");

        tblMmuAlg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "PAGE ID", "PID", "LOADED", "L-ADDR", "M-ADDR", "D-ADDR", "LOADED-T", "MARK"
            }
        ));
        jScrollPane4.setViewportView(tblMmuAlg);

        tblFragmentationOpt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "LOADED", "UNLOADED", "", "", "Fragmentation"
            }
        ));
        jScrollPane7.setViewportView(tblFragmentationOpt);

        jLabel4.setText("Pages");

        jLabel5.setText("Thrashing");

        tblRamStatsOpt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "RAM KB", "RAM %", "V-RAM KB", "V-RAM %"
            }
        ));
        jScrollPane11.setViewportView(tblRamStatsOpt);

        tblSimTimeOpt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Processes", "Sim-Time"
            }
        ));
        jScrollPane12.setViewportView(tblSimTimeOpt);

        tblSimTimeAlg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Processes", "Sim-Time"
            }
        ));
        jScrollPane13.setViewportView(tblSimTimeAlg);

        tblRamStatsAlg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "RAM KB", "RAM %", "V-RAM KB", "V-RAM %"
            }
        ));
        jScrollPane14.setViewportView(tblRamStatsAlg);

        jLabel8.setText("Pages");

        jLabel9.setText("Thrashing");

        tblFragmentationAlg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "LOADED", "UNLOADED", "", "", "Fragmentation"
            }
        ));
        jScrollPane10.setViewportView(tblFragmentationAlg);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane2)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 21, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(205, 205, 205)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(192, 192, 192))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(467, 467, 467))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblRamAlg)
                        .addGap(464, 464, 464))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(jLabel4)
                        .addGap(162, 162, 162)
                        .addComponent(jLabel5))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(88, 88, 88)
                            .addComponent(jLabel8)
                            .addGap(162, 162, 162)
                            .addComponent(jLabel9))
                        .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRamAlg)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //Start thread to do the algorithm in the background
        startWorker();
    }//GEN-LAST:event_formWindowOpened


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lblRamAlg;
    private javax.swing.JTable tblFragmentationAlg;
    private javax.swing.JTable tblFragmentationOpt;
    private javax.swing.JTable tblMmuAlg;
    private javax.swing.JTable tblMmuOpt;
    private javax.swing.JTable tblRamAlg;
    private javax.swing.JTable tblRamOpt;
    private javax.swing.JTable tblRamStatsAlg;
    private javax.swing.JTable tblRamStatsOpt;
    private javax.swing.JTable tblSimTimeAlg;
    private javax.swing.JTable tblSimTimeOpt;
    // End of variables declaration//GEN-END:variables
}
