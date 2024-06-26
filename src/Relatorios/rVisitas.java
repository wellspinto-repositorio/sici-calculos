package Relatorios;

import Funcoes.AutoCompletion;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.jTableControl;
import Funcoes.toPreview;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.swing.ComboBoxEditor;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class rVisitas extends javax.swing.JInternalFrame {
    boolean bExecNome = false, bExecCodigo = false, eBloq = false;
    Db conn = VariaveisGlobais.conexao;
    jTableControl tabela = new jTableControl(true);

    /**
     * Creates new form rVisitas
     */
    public rVisitas() {
        initComponents();
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        FillCombos();
        AutoCompletion.enable(jrgprp);
        AutoCompletion.enable(jnome);
        
        ComboBoxEditor edit = jnome.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                FillTable();
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                CleanScreen();
            }
        });

        ComboBoxEditor editContrato = jrgprp.getEditor();
        Component compContrato = editContrato.getEditorComponent();
        compContrato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                CleanScreen();
            }
        });
        
    }

    private void CleanScreen() {
        TableControl.delall(atable);
        jprint.setEnabled(false);
    }
    
    private void FillTable() {
        String _rgprp = jrgprp.getSelectedItem().toString().trim();
        String _sql = "SELECT DISTINCT rgimv, end FROM visitas WHERE rgimv REGEXP '^" + _rgprp.substring(0, _rgprp.length() - 2) + "[0-9][0-9]'" + " ORDER BY rgimv;";
        
        CleanScreen();
        ResultSet rs = conn.OpenTable(_sql, null);
        Integer[] tam = {70,500};
        String[] col = {"rgimv","endereco"};
        Boolean[] edt = {false,false};
        String[] aln = {"L","L"};
        Object[][] data = {};
        try {
            if (rs.next()) {
                String _rgimv = rs.getString("rgimv");
                String _ender = rs.getString("end");
                
                Object[] dado = {_rgimv, _ender};
                data = tabela.insert(data, dado);                
            }
        } catch (Exception e) {}
        conn.CloseTable(rs);
        tabela.Show(atable, data, tam, aln, col, edt);
}
    
    private void FillCombos() {
        String sSql = "SELECT rgprp as codigo, UPPER(nome) as nome FROM proprietarios ORDER BY UPPER(nome);";

        jrgprp.removeAllItems();
        jnome.removeAllItems();
        if (!sSql.isEmpty()) {
            ResultSet imResult = this.conn.OpenTable(sSql, null);

            try {
                while (imResult.next()) {
                    jrgprp.addItem(String.valueOf(imResult.getInt("codigo")));
                    jnome.addItem(imResult.getString("nome"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            conn.CloseTable(imResult);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jprint = new javax.swing.JButton();
        jclose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        atable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jrgprp = new javax.swing.JComboBox();
        jnome = new javax.swing.JComboBox();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Relatorio de Visitas");

        jprint.setText("Imprimir");
        jprint.setEnabled(false);
        jprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jprintActionPerformed(evt);
            }
        });

        jclose.setText("Fechar");
        jclose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcloseActionPerformed(evt);
            }
        });

        atable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        atable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        atable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                atableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(atable);

        jLabel2.setText("Rgpgp:");

        jrgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrgprpActionPerformed(evt);
            }
        });

        jnome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jnomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jclose))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jnome, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jrgprp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jnome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jprint)
                    .addComponent(jclose))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jrgprpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrgprpActionPerformed
        if (!bExecNome) {
            int pos = jrgprp.getSelectedIndex();
            if (jnome.getItemCount() > 0) {bExecCodigo = true; jnome.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jrgprpActionPerformed

    private void jnomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jnomeActionPerformed
        if (!bExecCodigo) {
            int pos = jnome.getSelectedIndex();
            if (jrgprp.getItemCount() > 0) {bExecNome = true; jrgprp.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jnomeActionPerformed

    private void atableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_atableMouseClicked
        int rowsel[] = atable.getSelectedRows();
        jprint.setEnabled(rowsel.length != 0);
    }//GEN-LAST:event_atableMouseClicked

    private void jcloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jcloseActionPerformed

    private void jprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jprintActionPerformed
        int rowsel[] = atable.getSelectedRows();
        if (rowsel.length == 0) return;
        
        for (int i=0;i<rowsel.length;i++) {
            int selrow = atable.convertRowIndexToModel(rowsel[i]);
            ListaVisitas(atable.getModel().getValueAt(selrow, 0).toString().trim());
        }
    }//GEN-LAST:event_jprintActionPerformed

    private void ListaVisitas(String rgimv) {        
        String query = "SELECT * FROM visitas WHERE rgimv = '" + rgimv + "' ORDER BY rgimv;";
        ResultSet rs = conn.OpenTable(query, null);

        //implementação da interface JRDataSource para DataSource ResultSet
        JRResultSetDataSource jrRS = new JRResultSetDataSource( rs );

        new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

        String outFileName = pathName + "Visitas_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
        String fileName = "reports/Visitas.jasper";

        //executa o relatório
        Map parametros = new HashMap();
        parametros.put("prgimv", rgimv);
        //parametros.put("logo", "resources/logos/boleta/" + VariaveisGlobais.marca.trim() + ".gif");

        try {
            /* Preenche o relatório com os dados. Gera o arquivo BibliotecaPessoal.jrprint    */
            //JasperFillManager.fillReportToFile( fileName, parametros, jrRS );
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrRS);

            JRExporter exporter = new JRPdfExporter();

            // Configure the exporter (set output file name and print object)
            
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();
        
            new toPreview(outFileName);
//            if (!"jasper".equals(VariaveisGlobais.reader)) {
//                ComandoExterno ce = new ComandoExterno();
//                ComandoExterno.ComandoExterno(VariaveisGlobais.reader + " " + outFileName);
//            } else {
//                JasperViewer viewer = new JasperViewer(print, false);
//                viewer.show();
//            }

        } catch (Exception ex) {ex.printStackTrace();}
        conn.CloseTable(rs);        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable atable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jclose;
    private javax.swing.JComboBox jnome;
    private javax.swing.JButton jprint;
    private javax.swing.JComboBox jrgprp;
    // End of variables declaration//GEN-END:variables
}
