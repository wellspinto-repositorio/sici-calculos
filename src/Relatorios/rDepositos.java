package Relatorios;

import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import Funcoes.toPreview;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 *
 * @author supervisor
 */
public class rDepositos extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;

    /** Creates new form rDepositos */
    public rDepositos() {
        initComponents();
        
        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/deposito.svg",16,16);
        setFrameIcon(icone);        
        
    }

    private void GerarPrint() {
        
        String query = "SELECT e.rgprp, e.rgimv, e.contrato, p.nome AS Proprietario, p.banco, p.agencia, p.conta, l.nomerazao AS Locatario, e.dtrecebimento FROM extrato e, proprietarios p, locatarios l WHERE e.rgprp = p.rgprp AND e.contrato = l.contrato AND e.dtrecebimento = '" + Dates.DateFormata("yyyy-MM-dd", mjDtPrint.getDate()) + "' AND TRIM(p.conta) <> '' ORDER BY Lower(p.nome);";
        ResultSet rs = conn.OpenTable(query, null);

        //implementação da interface JRDataSource para DataSource ResultSet
        JRResultSetDataSource jrRS = new JRResultSetDataSource( rs );

        new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

        String outFileName = pathName + "Depositarios_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
        String fileName = "reports/RelDeposito.jasper";

        //executa o relatório
        Map parametros = new HashMap();
        String sData = Dates.DateFormata("yyyy-MM-dd", mjDtPrint.getDate());
        parametros.put("datamovto", sData);
        parametros.put("logo", "resources/logos/boleta/" + VariaveisGlobais.marca.trim() + ".gif");

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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        mjDtPrint = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jbtnGerar = new javax.swing.JButton();

        setClosable(true);
        setTitle(".:: Relatório de Depositarios");
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Digite a data a listar (dd/mm/aaaa):");

        jbtnGerar.setText("Gerar Relatório");
        jbtnGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnGerarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mjDtPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnGerar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mjDtPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnGerar)
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGerarActionPerformed
        jbtnGerar.setEnabled(false);
        GerarPrint();
        jbtnGerar.setEnabled(true);
    }//GEN-LAST:event_jbtnGerarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnGerar;
    private com.toedter.calendar.JDateChooser mjDtPrint;
    // End of variables declaration//GEN-END:variables
}

// SELECT a.rgprp, p.nome, a.rgimv, a.contrato, l.nomerazao, a.dtvencimento, a.autenticacao, if(a.tag = 'X', (SELECT t.dtrecebimento FROM auxiliar t WHERE t.rgprp = a.rgprp AND t.rgimv = a.rgimv and t.contrato = a.contrato AND t.rc_aut = a.autenticacao AND t.conta = 'REC'),'') as dtrecebimento, StrVal(Mid(a.campo,InStr(a.campo,'@')+1)) AS vradiantado FROM recibo a, proprietarios p, locatarios l where (a.rgprp = p.rgprp) AND (a.contrato = l.contrato) AND instr(a.campo,'@') ORDER BY lower(p.nome);