package Sici.Login;

import Funcoes.Criptografia;
import Funcoes.Dates;
import Funcoes.VariaveisGlobais;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Properties;

public class jLicensa extends javax.swing.JDialog {
    private boolean retorno = false;
    private final String mFile = System.getProperty("user.dir") + "/Sici.aut";
    
    public boolean isRetorno() { return retorno; }
    
    public jLicensa(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        setLocationRelativeTo(null);
        
        jInformar.setText(VariaveisGlobais.Licenca);
        
        TxbLicensa.setText("");
        TxbLicensa.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        TxbLicensa = new javax.swing.JTextField();
        btnContinuar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jInformar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Licensa expirada");
        setAlwaysOnTop(true);
        setResizable(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        jLabel1.setText("<html><font size=5>A licensa de uso do programa <font color=#ff00ff><b>Sici</b></font> esta <font color=red><b>expirada</B></font>.</font><br>\nEntre em contato com a <font color=blue><b>WellSoft</b></font> para solicitar um código de renovação.<br>\n<br>\n<b>Tel.: <font color=#ff00ff>(21)9 7665-9897</font> ou pelo E-Mail: <font color=#ff00ff>wellspinto@gmail.com</font></b><br>\n<br>\n<i><b>*<font color=red>Observação:</font></b><br>\n\tA renovação da licensa muitas das vezes ocorre automaticamente, bastando<br>\npara isso que sua conecção com a internet esteja ativa.<br> \nSe por algum motivo não ocorrer entre em contato para mais informações.</i><br></html>");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Licensa:");

        btnContinuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Asp.net_0031_color1_16x16.gif"))); // NOI18N
        btnContinuar.setText("Continuar");
        btnContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Informar:");

        jInformar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jInformar.setForeground(new java.awt.Color(102, 0, 102));
        jInformar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnContinuar)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(7, 7, 7)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jInformar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TxbLicensa, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jInformar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TxbLicensa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnContinuar)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarActionPerformed
        if (TxbLicensa.getText().trim().equals("")) {
            retorno = false;
        } else {
            try {
                String crypData = TxbLicensa.getText().trim();
                String dcrypData = Criptografia.decrypt(TxbLicensa.getText().trim(), Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
                if (!Dates.isDateValid(dcrypData, "dd-MM-yyyy")) {
                    retorno = false;
                    return;
                }
                
                Properties p = new Properties();
                // Remotes
                String _remote = System.getProperty("Remote", null);
                if (_remote != null) {
                    p.setProperty("Remote", _remote);
                }
                for (int w = 0; w <= 99; w++) {
                    String remotn = System.getProperty("Remote"  + new DecimalFormat("00").format(w), null);
                    if (remotn == null) continue;
                    p.setProperty("Remote"   + new DecimalFormat("00").format(w), remotn);
                }
                
                // Id do sistema
                p.setProperty("Key", System.getProperty("Key", "7kf51b"));
                p.setProperty("Id", System.getProperty("Id", "gq8nm8MHmfGY+4BghFpWAw=="));
                p.setProperty("Reg", crypData);
                
                String _host = System.getProperty("Host", null);
                if (_host != null) {
                    p.setProperty("Host", _host);
                    p.setProperty("User", System.getProperty("User", null));
                    p.setProperty("Pwd", System.getProperty("Pwd", null));
                }
                
                FileOutputStream outFile = new FileOutputStream(mFile);

                p.store(outFile,"Sici.aut");
                outFile.close();

                System.setProperty("Reg", crypData);
                retorno = true;
            } catch (Exception ex) {
                retorno = false;
            }
        }
        dispose();
    }//GEN-LAST:event_btnContinuarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jLicensa dialog = new jLicensa(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField TxbLicensa;
    private javax.swing.JButton btnContinuar;
    private javax.swing.JLabel jInformar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
