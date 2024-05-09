package Movimento;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class jSuperExtorno extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    
    public jSuperExtorno() {
        initComponents();

        // Icone da tela
        ImageIcon icone = new FlatSVGIcon("menuIcons/durable-icon.svg",16,16);
        setFrameIcon(icone);
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtExtornar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jAutenticacao = new javax.swing.JTextField();
        jbtSair = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Super EXTORNO");
        setMaximumSize(new java.awt.Dimension(216, 102));
        setMinimumSize(new java.awt.Dimension(216, 102));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);

        jbtExtornar.setText("Extornar");
        jbtExtornar.setEnabled(false);
        jbtExtornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtExtornarActionPerformed(evt);
            }
        });

        jLabel1.setText("Autenticação:");

        jAutenticacao.setForeground(new java.awt.Color(204, 0, 51));
        jAutenticacao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jAutenticacao.setText("000000");
        jAutenticacao.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jAutenticacaoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jAutenticacaoFocusLost(evt);
            }
        });

        jbtSair.setText("Sair");
        jbtSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSairActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAutenticacao, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbtExtornar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAutenticacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtExtornar)
                    .addComponent(jbtSair))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtExtornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtExtornarActionPerformed
        String sql = ""; String[] strings = {};
        try {
            sql = FuncoesGlobais.Subst("UPDATE recibo SET tag = '', autenticacao = 0 WHERE autenticacao = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);            
            strings = FuncoesGlobais.ArrayAdd(strings, "Recibos Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM avisos WHERE autenticacao = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);            
            strings = FuncoesGlobais.ArrayAdd(strings, "Avisos Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM Cheques WHERE ch_autenticacao = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Cheques Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM razao WHERE av_aut = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Razao Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM retencao WHERE rc_aut = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Retenções Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM imposto WHERE rc_aut = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Imposto Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM extrato WHERE rc_aut = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Extrato Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM despesas WHERE CAST(autentica AS UNSIGNED Int) = &1.", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Despesas Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM auxiliar WHERE rc_aut = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Auxiliar Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM caixa WHERE cx_aut = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Caixa Extornados!!!");
        } catch (Exception e) {}
        
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM caixabck WHERE cx_aut = '&1.'", new String[] {jAutenticacao.getText()});
            conn.CommandExecute(sql);
            strings = FuncoesGlobais.ArrayAdd(strings, "Caixabck Extornados!!!");
        } catch (Exception e) {}

        JOptionPane.showMessageDialog(null, "Extornado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jbtExtornarActionPerformed

    private void jbtSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtSairActionPerformed

    private void jAutenticacaoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jAutenticacaoFocusGained
        jbtExtornar.setEnabled(false);
    }//GEN-LAST:event_jAutenticacaoFocusGained

    private void jAutenticacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jAutenticacaoFocusLost
        jbtExtornar.setEnabled(true);
    }//GEN-LAST:event_jAutenticacaoFocusLost
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jAutenticacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jbtExtornar;
    private javax.swing.JButton jbtSair;
    // End of variables declaration//GEN-END:variables
}
