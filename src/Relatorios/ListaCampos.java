package Relatorios;

import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.toPreview;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class ListaCampos extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    
    public ListaCampos() {
        initComponents();

        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/relatorioVencimentos.svg",16,16);
        setFrameIcon(icone);        

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);
        
        FillCampos();
        periodode.setDate(Dates.primeiraDataMes(new Date()));
        periodoate.setDate(new Date());
        
        cbCampos.requestFocus();
    }

    private void FillCampos() {
        cbCampos.removeAllItems();
        String query = "SELECT `cart_codigo`, `cart_descr`, `cart_ordem` FROM `lancart` ORDER BY `cart_codigo`;";
        ResultSet rs = conn.OpenTable(query, null);
        try {
            while (rs.next()) {
                cbCampos.addItem(rs.getString("cart_codigo") + " - " + rs.getString("cart_descr") + " > " + rs.getString("cart_ordem"));
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cbCampos = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        periodode = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel3 = new javax.swing.JLabel();
        periodoate = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        btnlistar = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setClosable(true);
        setTitle(".:: Relatório de campos em Recebimentos");

        jLabel1.setText("Campo:");

        jLabel2.setText("Período de");

        periodode.setDate(new java.util.Date(-2208977612000L));

        jLabel3.setText("até");

        periodoate.setDate(new java.util.Date(-2208977612000L));

        btnlistar.setText("Listar");
        btnlistar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnlistarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(periodode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(periodoate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnlistar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(periodoate, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(periodode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cbCampos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnlistar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnlistarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlistarActionPerformed
        if (Dates.DateDiff(Dates.DIA, periodode.getDate(), periodoate.getDate()) < 0) {
            JOptionPane.showMessageDialog(this, "Data inicial maoir que Data final.");
            periodode.requestFocus();
        }
        
        if (cbCampos.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um campo a listar.");
            cbCampos.requestFocus();
        }
        
        btnlistar.setEnabled(false);
        
        ExecutorService executor = Executors.newFixedThreadPool(1);
                
        Runnable relatorioConcluidoCallback = () -> {
            // Aqui você colocaria o código para atualizar o botão
            System.out.println("Relatório concluído. Atualizando o botão...");
        };
        
        executor.submit(() -> {
            int progress = 0;
            String selCampo = cbCampos.getSelectedItem().toString();
            String cdCampo = selCampo.substring(0,2);
            String nmCampo = selCampo.substring(5, selCampo.indexOf(">") - 1).trim();
            String odCampo = selCampo.substring(selCampo.indexOf(">") + 1).trim();
            String buscarPor = cdCampo + ":" + odCampo + ":";
            String query = "SELECT e.`rgprp`, e.`rgimv`, e.`contrato`, l.`nomerazao` `nome`, RetAvValorRid2(substring(e.`campo`,instr(e.`campo`,'" + buscarPor + "'))) `valor`, e.`dtvencimento`, e.`dtrecebimento` FROM `extrato` e INNER JOIN `locatarios` l ON e.`contrato` = l.`contrato` WHERE InStr(e.`campo`,'" + buscarPor + "') AND (e.`dtvencimento` BETWEEN :dataini AND :datafim) ORDER BY e.`dtvencimento`;";
            ResultSet rs = conn.OpenTable(query, new Object[][]{
                {"date", "dataini", periodode.getDate()},
                {"date", "datafim", periodoate.getDate()}
            });
            int totalRecords = conn.RecordCount(rs);
            int currentRecord = 0; progressBar.setValue(0);

            List<ClassListaCampos> lista = new ArrayList<>();
            try {
                while (rs.next()) {
                    ClassListaCampos values = new ClassListaCampos(
                            rs.getString("rgprp"), 
                            rs.getString("rgimv"), 
                            rs.getString("contrato"), 
                            rs.getString("nome"),
                            rs.getDate("dtvencimento"), 
                            rs.getDate("dtrecebimento"), 
                            rs.getBigDecimal("valor")
                    );
                    lista.add(values);
                    
                    currentRecord++;
                    progress = (int) ((currentRecord / (double) totalRecords) * 100);
                    progressBar.setValue(progress);

                    // Adicione um pequeno atraso para demonstração
                    try { Thread.sleep(100); } catch (InterruptedException iEx) {}      
                }
            } catch (SQLException sqlEx) {}
            conn.CloseTable(rs);

            btnlistar.setEnabled(progress >= 100);
            
            Map parametros = new HashMap();
            parametros.put("periodo_de", Dates.DateFormata("dd-MM-yyyy", periodode.getDate()));
            parametros.put("periodo_ate", Dates.DateFormata("dd-MM-yyyy", periodoate.getDate()));
            parametros.put("nomecampo", nmCampo);

            try {
                JRDataSource jrds = new JRBeanCollectionDataSource(lista);
                String fileName = "reports/ListaCamposPortaitA4.jasper";
                JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrds);

                // Create a PDF exporter
                JRExporter exporter = new JRPdfExporter();

                new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                // Configure the exporter (set output file name and print object)
                String outFileName = pathName + "ListaCamposPortaitA4_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                // Export the PDF file
                exporter.exportReport();

                new toPreview(outFileName);
            } catch (JRException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }                                
        });        
        
        
    }//GEN-LAST:event_btnlistarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnlistar;
    private javax.swing.JComboBox<String> cbCampos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private com.toedter.calendar.JDateChooser periodoate;
    private com.toedter.calendar.JDateChooser periodode;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
