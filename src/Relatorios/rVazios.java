package Relatorios;

import Funcoes.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashSet;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 *
 * @author supervisor
 */
public class rVazios extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;

    /** Creates new form rSeguros */
    public rVazios() {
        initComponents();

        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/ImoveisVazios.svg",16,16);
        setFrameIcon(icone);        
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        ShowEstatisticas();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnPreview = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pAtivos = new javax.swing.JLabel();
        pInativos = new javax.swing.JLabel();
        iAlugados = new javax.swing.JLabel();
        iVazios = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Relatório de Imóveis Vazios ::.");
        setVisible(true);

        jbtnPreview.setText("Listar Vazios");
        jbtnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPreviewActionPerformed(evt);
            }
        });

        jLabel1.setText("Total de Imóveis Alugados:");

        jLabel3.setText("Total de Imóveis (Ativos) Vazios:");

        jLabel2.setText("Total de Proprietários Ativos:");

        jLabel4.setText("Total de Proprietários Inativos:");

        pAtivos.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        pAtivos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pAtivos.setText("0000");
        pAtivos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pAtivos.setOpaque(true);

        pInativos.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        pInativos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pInativos.setText("0000");
        pInativos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pInativos.setOpaque(true);

        iAlugados.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        iAlugados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iAlugados.setText("0000");
        iAlugados.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        iAlugados.setOpaque(true);

        iVazios.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        iVazios.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iVazios.setText("0000");
        iVazios.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        iVazios.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(28, 28, 28)
                        .addComponent(pAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(iAlugados, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pInativos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(iVazios, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(pAtivos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(pInativos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(iAlugados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(iVazios))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPreview)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ShowEstatisticas() {
        String sql; float tivazios = 0; float tialugados = 0; float tpativos = 0; float tpinativos = 0;
        sql = "SELECT Count(p.rgprp) as ivazios FROM proprietarios p, imoveis i WHERE p.rgprp = i.rgprp AND Upper(Trim(i.situacao)) = 'VAZIO' AND Upper(Trim(p.status)) = 'ATIVO' ORDER BY i.rgimv;";
        ResultSet tmp = conn.OpenTable(sql, null);
        try {
            while (tmp.next()) {
                tivazios = tmp.getFloat("ivazios");
            }
        } catch (Exception err) {}
        conn.CloseTable(tmp);
        
        sql = "SELECT Count(p.rgprp) as ivazios FROM proprietarios p, imoveis i WHERE p.rgprp = i.rgprp AND Upper(Trim(i.situacao)) = 'OCUPADO' AND Upper(Trim(p.status)) = 'ATIVO' ORDER BY i.rgimv;";
        tmp = conn.OpenTable(sql, null);
        try {
            while (tmp.next()) {
                tialugados = tmp.getFloat("ivazios");
            }
        } catch (Exception err) {}
        conn.CloseTable(tmp);
        
        sql = "SELECT Count(p.rgprp) as pativos FROM proprietarios p WHERE Upper(Trim(p.status)) = 'ATIVO';";
        tmp = conn.OpenTable(sql, null);
        try {
            while (tmp.next()) {
                tpativos = tmp.getFloat("pativos");
            }
        } catch (Exception err) {}
        conn.CloseTable(tmp);

        sql = "SELECT Count(p.rgprp) as pativos FROM proprietarios p WHERE Upper(Trim(p.status)) <> 'ATIVO';";
        tmp = conn.OpenTable(sql, null);
        try {
            while (tmp.next()) {
                tpinativos = tmp.getFloat("pativos");
            }
        } catch (Exception err) {}
        conn.CloseTable(tmp);
        
        pAtivos.setText(FuncoesGlobais.StrZero(String.valueOf(tpativos),4));
        pInativos.setText(FuncoesGlobais.StrZero(String.valueOf(tpinativos),4));
        
        iVazios.setText(FuncoesGlobais.StrZero(String.valueOf(tivazios),4));
        iAlugados.setText(FuncoesGlobais.StrZero(String.valueOf(tialugados),4));
    }
    
    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        jbtnPreview.setEnabled(false);
        RelVazios();
        jbtnPreview.setEnabled(true);
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void RelVazios() {
        try {
            String fileName = "reports/rVazios.jasper";
            JasperPrint print = JasperFillManager.fillReport(fileName, null, conn.conn);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            new jDirectory("reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
            String pathName = "reports/Relatorios/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";
            
            // Configure the exporter (set output file name and print object)
            String outFileName = pathName + "Vazios_" + Dates.DateFormata("ddMMyyyy", new Date()) + ".pdf";
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();
            
            new toPreview(outFileName);
//            if (!"jasper".equals(VariaveisGlobais.reader)) {
//                ComandoExterno ce = new ComandoExterno();
//                ComandoExterno.ComandoExterno(VariaveisGlobais.reader + " " + outFileName);
//                //ce.ComandoExterno("lp " + FileNamePdf);
//            } else {
//                JasperViewer viewer = new JasperViewer(print, false);
//                viewer.show();
//            }
                
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel iAlugados;
    private javax.swing.JLabel iVazios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JLabel pAtivos;
    private javax.swing.JLabel pInativos;
    // End of variables declaration//GEN-END:variables
}
