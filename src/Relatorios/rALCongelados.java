package Relatorios;

import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.toPreview;
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
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 *
 * @author YOGA 510
 */
public class rALCongelados extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    String[] month;
    int[] dmonth;
    int[] nmonth;

    /**
     * Creates new form rALCongelados
     */
    public rALCongelados() {
        initComponents();
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);
        
        this.month = new String[]{"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        this.dmonth = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        this.nmonth = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        MesRef.setValue(this.month[(new Date()).getMonth()]);
        AnoRef.setValue(1900 + new Date().getYear());        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jRadioButtonTodos = new javax.swing.JRadioButton();
        jRadioButtonNesteMes = new javax.swing.JRadioButton();
        btnListar = new javax.swing.JButton();
        MesRef = new javax.swing.JSpinner();
        AnoRef = new javax.swing.JSpinner();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Relatório de Alugueis Congelados (SEM GERAÇÃO)");

        jLabel1.setText("Listar Alugueis Congelados:");

        buttonGroup1.add(jRadioButtonTodos);
        jRadioButtonTodos.setSelected(true);
        jRadioButtonTodos.setText("Todos");

        buttonGroup1.add(jRadioButtonNesteMes);
        jRadioButtonNesteMes.setText("Em");

        btnListar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Actions-document-print-preview-icon.png"))); // NOI18N
        btnListar.setText("Listar");
        btnListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListarActionPerformed(evt);
            }
        });

        MesRef.setModel(new javax.swing.SpinnerListModel(new String[] {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"}));

        AnoRef.setModel(new javax.swing.SpinnerNumberModel(2018, 2010, 2030, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonTodos)
                        .addGap(37, 37, 37)
                        .addComponent(jRadioButtonNesteMes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(MesRef, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AnoRef, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnListar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonTodos)
                    .addComponent(jRadioButtonNesteMes)
                    .addComponent(MesRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AnoRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnListar)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarActionPerformed
        List<cALCongelados> lista = new ArrayList<>();
        String selectSQL = "";
        String legenda = "";
        
        if (jRadioButtonTodos.isSelected()) {
            // Todos
            selectSQL = "SELECT c.rgprp, c.rgimv, c.contrato, (SELECT l.nomerazao FROM locatarios l WHERE l.contrato = c.contrato) AS nomeloca, " + 
                        "c.dtinicio, c.dttermino, c.dtadito, c.dtvencimento, c.dtultrecebimento FROM carteira c WHERE InStr(campo,'01:1:') AND " + 
                        "mid(campo,17,4) = '0000' ORDER BY 4;";
            legenda = "TODOS OS MESES DE TODOS OS ANOS";
        } else {
            // Neste Mês / Ano
            selectSQL = "SELECT c.rgprp, c.rgimv, c.contrato, (SELECT l.nomerazao FROM locatarios l WHERE l.contrato = c.contrato) AS nomeloca, " + 
                        "c.dtinicio, c.dttermino, c.dtadito, c.dtvencimento, c.dtultrecebimento FROM carteira c WHERE InStr(campo,'01:1:') AND " + 
                        "mid(campo,17,4) = '0000' AND Month(StrDate(c.dtvencimento)) = " +  
                        this.nmonth[FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString())] + 
                        " AND Year(StrDate(c.dtvencimento)) = " + AnoRef.getValue() + " ORDER BY 4;";
            legenda = "MÊS DE " + MesRef.getValue() + " DO ANO DE " + AnoRef.getValue();
        } 
        ResultSet Result = this.conn.OpenTable(selectSQL, null);
        try {
            while (Result.next()) {
                cALCongelados values = new cALCongelados(
                       Result.getString("rgprp"),
                       Result.getString("rgimv"),
                       Result.getString("contrato"),
                       Result.getString("nomeloca"),
                       Dates.StringtoDate(Result.getString("dtvencimento"),"dd-MM-yyyy"),
                       Dates.StringtoDate(Result.getString("dtinicio"),"dd-MM-yyyy"),
                       Dates.StringtoDate(Result.getString("dttermino"),"dd-MM-yyyy"),
                       Dates.StringtoDate(Result.getString("dtadito"),"dd-MM-yyyy"),
                       Dates.StringtoDate(Result.getString("dtultrecebimento"),"dd-MM-yyyy")
                );
                lista.add(values);
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(Result);
        
        Map parametros = new HashMap();
        parametros.put("parameter1", legenda);
        
        try {
            JRDataSource jrds = new JRBeanCollectionDataSource(lista);
            String fileName = "reports/rALCongelados.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrds);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
            String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
            // Configure the exporter (set output file name and print object)
            String outFileName = pathName + "ALCongelados_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
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
    }//GEN-LAST:event_btnListarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner AnoRef;
    private javax.swing.JSpinner MesRef;
    private javax.swing.JButton btnListar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButtonNesteMes;
    private javax.swing.JRadioButton jRadioButtonTodos;
    // End of variables declaration//GEN-END:variables
}
