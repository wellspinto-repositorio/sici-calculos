package Sici.Locatarios;

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import Funcoes.AutoCompletion;
import Funcoes.Db;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.JTable;

/**
 *
 * @author supervisor
 */
public class jLocaInclusao extends javax.swing.JDialog {
    Db conn = VariaveisGlobais.conexao;

    public static final String OKCMD = "OK";
    public static final String CANCELCMD = "CANCEL";
    private Object returnVal = null;

    private String r_rgpgp = "";
    private String r_rgimv = "";
    private String r_tipoimovel = "";

    /** *//**
     * change statements here to get other return values
     * @return
     */
    public Object showDialog(){
        this.pack();
        this.setVisible(true);
        return this.returnVal;
    }

    /** Creates new form jLocaInclusao */
    public jLocaInclusao(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //Centraliza a janela.
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = (int) (dimension.getWidth() - this.getSize().getWidth() ) / 2;
        int y = (int) (dimension.getHeight() - this.getSize().getHeight()) / 2;
        this.setLocation(x,y);

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        FillCombos();

        AutoCompletion.enable(jRgprp);
        AutoCompletion.enable(jNomeProp);

    }

    private void FillCombos() {
        String sSql = "SELECT distinct p.rgprp, p.nome FROM proprietarios p, imoveis i WHERE p.rgprp = i.rgprp and Lower(Trim(i.situacao)) = 'vazio'  ORDER BY Lower(Trim(p.nome));";
        ResultSet imResult = this.conn.OpenTable(sSql, null);

        //ArrayList<String> aRgprp = new ArrayList<String>();
        //ArrayList<String> aNomeProp = new ArrayList<String>();

        try {
            while (imResult.next()) {
                jRgprp.addItem(String.valueOf(imResult.getInt("rgprp")));
                jNomeProp.addItem(imResult.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        conn.CloseTable(imResult);

        //seuCombo.setModel(new DefaultComboboxModel(new Vector(seuArrayList)));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.c
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jRgprp = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jNomeProp = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbImoveis = new javax.swing.JTable();
        btCancel = new javax.swing.JButton();
        btOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Incluir Contrato ::.");
        setAlwaysOnTop(true);
        setFont(new java.awt.Font("Agency FB", 0, 8)); // NOI18N
        setMaximumSize(new java.awt.Dimension(448, 219));
        setMinimumSize(new java.awt.Dimension(448, 219));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("RgProp:");

        jRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgprpActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Nome:");

        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        jScrollPane1.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jtbImoveis.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jtbImoveis.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jtbImoveis);

        btCancel.setMnemonic('r');
        btCancel.setText("Cancelar");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });

        btOk.setMnemonic('C');
        btOk.setText("Confirma");
        btOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jNomeProp, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btOk, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancel)
                    .addComponent(btOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        r_rgpgp = "";
        r_rgimv = "";
        r_tipoimovel = "";

        returnVal = jLocaInclusao.CANCELCMD;
        this.dispose();
    }//GEN-LAST:event_btCancelActionPerformed

    private void btOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOkActionPerformed
        int selRow = jtbImoveis.getSelectedRow();
        if (selRow > -1) {
            r_rgpgp = jRgprp.getSelectedItem().toString().trim();
            r_rgimv = (String) jtbImoveis.getModel().getValueAt(selRow, 0);
            r_tipoimovel = (String) jtbImoveis.getModel().getValueAt(selRow, 2);

            returnVal = jLocaInclusao.OKCMD;
        } else {
            r_rgpgp = "";
            r_rgimv = "";
            r_tipoimovel = "";
            returnVal = jLocaInclusao.CANCELCMD;

            //JOptionPane.showMessageDialog(null, "Você deve selecionar um imóvel!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            jtbImoveis.requestFocus();
            return;
        }

        this.dispose();
    }//GEN-LAST:event_btOkActionPerformed

    private void jRgprpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRgprpActionPerformed
        int pos = jRgprp.getSelectedIndex();
        if (jNomeProp.getItemCount() > 0) {
            jNomeProp.setSelectedIndex(pos);

            FillImoveisVazios(jtbImoveis, jRgprp.getSelectedItem().toString().trim());
        }
    }//GEN-LAST:event_jRgprpActionPerformed

    private void jNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomePropActionPerformed
        int pos = jNomeProp.getSelectedIndex();
        if (jRgprp.getItemCount() > 0) {
            jRgprp.setSelectedIndex(pos);

            FillImoveisVazios(jtbImoveis, jRgprp.getSelectedItem().toString().trim());
        }
    }//GEN-LAST:event_jNomePropActionPerformed

    private void FillImoveisVazios(JTable table, String tProp) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"rgimv","endereço","tipoimovel"},{"50","500","120"}});

        String sSql = "SELECT * FROM imoveis WHERE rgprp = '" + tProp + "' AND Lower(Trim(situacao)) = 'vazio' ORDER BY rgprp, rgimv;";
        ResultSet imResult = this.conn.OpenTable(sSql, null);

        try {
            while (imResult.next()) {
                String trgimv = String.valueOf(imResult.getInt("rgimv"));
                String ttpimovel = imResult.getString("tpimovel").toUpperCase();
                String tend = imResult.getString("end").trim() + ", " + imResult.getString("num").trim() + " " + imResult.getString("compl").trim() ;

                TableControl.add(table, new String[][]{{trgimv, tend, ttpimovel},{"C","L","C"}}, true);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        conn.CloseTable(imResult);

        btOk.setEnabled(jtbImoveis.getRowCount() > 0);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jLocaInclusao dialog = new jLocaInclusao(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public String getRgprp() {
        return r_rgpgp;
    }

    public String getRgimv() {
        return r_rgimv;
    }

    public String getContrato() {
        return "NOVO";
    }

    public String getTpImv() {
        return r_tipoimovel;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JComboBox jRgprp;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtbImoveis;
    // End of variables declaration//GEN-END:variables

}
