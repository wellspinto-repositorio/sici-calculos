package Movimento.Taxas;

import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import Funcoes.jTableControl;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author supervisor
 */
public class jAtuTaxasImvs extends javax.swing.JDialog {
    Db conn = VariaveisGlobais.conexao;
    jTableControl tabela = new jTableControl(true);
    TableRowSorter<TableModel> sorter;
    String taxa = ""; String cdtaxa = "";
    String iptucod = "";
    
    /**
     * Creates new form jAtuTaxasImvs
     */
    public jAtuTaxasImvs(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        // centraliza
        setLocationRelativeTo(null);
        
        ListaImoveis();
    }

    public void setTaxa(String value) {this.taxa = value;}
    public void setCdTaxa(String value) {this.cdtaxa = value;}
    public String getIptuCod() { return this.iptucod; }
    
    private void ListaImoveis() {
        String sql = "SELECT i.rgprp, i.rgimv, CONCAT(i.end,',',i.num,' ',i.compl) as endereco FROM imoveis i ORDER BY i.rgimv;";
        sql = FuncoesGlobais.Subst(sql, new String[] {VariaveisGlobais.usuario.toLowerCase().trim()});

        ResultSet rs = conn.OpenTable(sql, null);
        Integer[] tam = {90,90,240};
        String[] col = {"rgprp","rgimv","endereco"};
        Boolean[] edt = {false,false,false};
        String[] aln = {"L","L","L"};
        Object[][] data = {};
        try {
            while (rs.next()) {
                String drgprp = rs.getString("rgprp");
                String drgimv = rs.getString("rgimv");
                String dend = rs.getString("endereco");
                
                Object[] dado = {drgprp, drgimv, dend};
                data = tabela.insert(data, dado);
            }
        } catch (SQLException ex) {ex.printStackTrace();}

        conn.CloseTable(rs);
        tabela.Show(atable, data, tam, aln, col, edt);
        
        sorter = new TableRowSorter<TableModel>(atable.getModel());
        atable.setRowSorter(sorter);

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
        atable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        busca = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Selecione os Imoveis para a taxa");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        atable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        atable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(atable);

        jLabel1.setText("Buscar:");

        busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscaKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(busca)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscaKeyReleased
        if ("".equals(busca.getText().trim())) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(busca.getText().trim()));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_buscaKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (atable.getSelectedRows().length > 0) {
            // Testa se o codigo é de IPTU (816;817)
            String[] aaTaxa = this.taxa.split(":");
            if (this.cdtaxa.equalsIgnoreCase("816") || this.cdtaxa.equalsIgnoreCase("817")) {
                String tMat = "";
                while (tMat.equalsIgnoreCase("") || tMat == null) {
                    tMat = JOptionPane.showInputDialog("Entre com a Matricula do IPTU:\nNão Ultilizar pontos, traços...\nSomente Números:");
                }
                iptucod = tMat;
                aaTaxa[1] = tMat.trim();
                this.taxa = FuncoesGlobais.join(aaTaxa, ":");
            }
            
            int srows = atable.getSelectedRows().length;
            int[] rows = atable.getSelectedRows();
            for (int i=0;i<srows;i++) {
                int modelRow = atable.convertRowIndexToModel(rows[i]);
                
                String trgimv = atable.getModel().getValueAt(modelRow, 1).toString();
                Object[][] ucampos = null; String vcampos = "";
                try {
                    ucampos =conn.ReadFieldsTable(new String[] {"matriculas"}, "imoveis", "rgimv = '" + trgimv + "'");
                } catch (Exception e) {}
                if (ucampos != null) {
                    if (ucampos[0][3] == null) {
                        //
                    } else if (ucampos[0][3].toString().isEmpty()) {
                        //
                    } else {
                        vcampos = ucampos[0][3].toString();
                    }
                }
                if (!vcampos.contains(this.taxa)) {
                    vcampos += this.taxa + ";";
                    String sql = "UPDATE imoveis SET matriculas = '" + vcampos + "' WHERE rgimv = '" + trgimv + "';";
                    conn.CommandExecute(sql);
                    System.out.println(sql);
                }
            }
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(jAtuTaxasImvs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jAtuTaxasImvs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jAtuTaxasImvs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jAtuTaxasImvs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jAtuTaxasImvs dialog = new jAtuTaxasImvs(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable atable;
    private javax.swing.JTextField busca;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}