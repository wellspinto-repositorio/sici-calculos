package Movimento;

import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class jExtorno extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;

    /** Creates new form jExtorno */
    public jExtorno() {
        initComponents();   
        
        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/extorno.svg",16,16);
        setFrameIcon(icone);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jAutenticacao = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jValor = new javax.swing.JFormattedTextField();
        jbtExtornar = new javax.swing.JButton();
        jbtSair = new javax.swing.JButton();
        jDesc = new javax.swing.JComboBox();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Extorno ::.");
        setVisible(true);

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

        jLabel2.setText("Descrição:");

        jLabel3.setText("Valor:");

        jValor.setEditable(false);
        jValor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jValor.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jValor.setText("0,00");
        jValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValorActionPerformed(evt);
            }
        });

        jbtExtornar.setText("Extornar");
        jbtExtornar.setEnabled(false);
        jbtExtornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtExtornarActionPerformed(evt);
            }
        });

        jbtSair.setText("Sair");
        jbtSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSairActionPerformed(evt);
            }
        });

        jDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDescActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDesc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jAutenticacao, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtExtornar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtSair, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAutenticacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtExtornar)
                        .addComponent(jbtSair))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jAutenticacaoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jAutenticacaoFocusLost
        jbtExtornar.setEnabled(Extornar(jAutenticacao.getText().trim()));
    }//GEN-LAST:event_jAutenticacaoFocusLost

    private void jAutenticacaoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jAutenticacaoFocusGained
        jAutenticacao.selectAll();
        jValor.setText("0,00");
        jDesc.removeAllItems();
    }//GEN-LAST:event_jAutenticacaoFocusGained

    private boolean ExtornaAvisos(String nrAut) {
        String sql = ""; boolean sucesso = true;
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM avisos WHERE autenticacao = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM cheques WHERE ch_autenticacao = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM razao WHERE av_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("UPDATE retencao SET marca = '', rt_aut = '0' WHERE  rt_aut = '&1.';", new String[] {nrAut});
            conn.CommandExecute(sql);
            
            //sql = FuncoesGlobais.Subst("UPDATE antecipados SET dtpagamento = Null, at_aut = '0' WHERE  at_aut = '&1.';", new String[] {nrAut});
            //conn.CommandExecute(sql);
        } catch (Exception e) {sucesso = false;}
        return sucesso;
    }
    
    private boolean ExtornaRecibos(String nrAut) {
        String sql = ""; boolean sucesso = true;
        try {
            sql = "DELETE FROM cheques WHERE ch_autenticacao = '%s'"; sql = String.format(sql, nrAut);
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM razao WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM retencao WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM imposto WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM extrato WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("UPDATE recibo SET TAG = ' ', autenticacao = '0' WHERE autenticacao = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);        

            //sql = FuncoesGlobais.Subst("UPDATE antecipados SET dtrecebimento = Null, rc_aut = '0' WHERE  rc_aut = '&1.';", new String[] {nrAut});
            //conn.CommandExecute(sql);
        } catch (Exception e) {sucesso = false;}
        return sucesso;
    }
    
    private boolean ExtornaExtratos(String nrAut) {
        String sql = ""; boolean sucesso = true;
        try {
            // Retonar saldo anterior
            String mRGPRP = "", sdant = "";
            try {
                mRGPRP = conn.ReadFieldsTable(new String[] {"cx_rgprp"}, "caixa", FuncoesGlobais.Subst("cx_doc = 'ET' AND cx_aut = '&1.'", new String[] {nrAut}))[0][3].toString();
                Object[][] vrsdant = conn.ReadFieldsTable(new String[] {"pr_sdant"}, "extrato", FuncoesGlobais.Subst("et_aut = '&1.'", new String[] {nrAut}));
                if (vrsdant != null) { sdant = vrsdant[0][3].toString(); } else { sdant = null; }
            } catch (SQLException ex) {}
            if(sdant != null) {
                sql = FuncoesGlobais.Subst("UPDATE proprietarios SET saldoant = '&1.' WHERE rgprp = '&2.';", new String[] {sdant, mRGPRP});
                conn.CommandExecute(sql);
            }
            
            sql = FuncoesGlobais.Subst("DELETE FROM extrato WHERE et_aut = '&1.' AND rgimv = '' AND contrato = '';", new String[] {nrAut});
            conn.CommandExecute(sql);
            
            sql = FuncoesGlobais.Subst("UPDATE extrato SET pr_sdant = '0', et_aut = '0', tag = ' ' WHERE et_aut = '&1.' AND rgimv != '' AND contrato != '';", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("UPDATE avisos SET tag = ' ', et_aut = '0' WHERE et_aut = '&1.';", new String[] {nrAut});
            conn.CommandExecute(sql);        
            
            // Extornar o pdf
            
        } catch (Exception e) {sucesso = false;}
        return sucesso;
    }
    
    private boolean ExtornaDespesas(String nrAut) {
        String sql = ""; boolean sucesso = true;
        try {
            String sAut = FuncoesGlobais.StrZero(nrAut, 6);
            sql = FuncoesGlobais.Subst("DELETE FROM despesas WHERE autentica = '&1.'", new String[] {sAut});
            conn.CommandExecute(sql);        
        } catch (Exception e) {sucesso = false;}
        return sucesso;
    }
    
    private boolean ExtornaDespositos(String nrAut) {
        String sql = ""; boolean sucesso = true;
        try {
            sql = FuncoesGlobais.Subst("INSERT INTO cheques (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_ncaixa, ch_autenticacao) (SELECT ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_ncaixa, ch_autenticacao FROM Chequesbck WHERE ch_autent = '&1.')", new String[] {FuncoesGlobais.StrZero(nrAut,6)});
            conn.CommandExecute(sql);
            sql = FuncoesGlobais.Subst("DELETE FROM chequesbck WHERE ch_autent = '&1.'", new String[] {FuncoesGlobais.StrZero(nrAut,6)});
            conn.CommandExecute(sql);
        } catch (Exception e) {sucesso = false;}
        return sucesso;
    }
    
    private boolean ExtornaBO(String nrAut) {
        String sql = ""; boolean sucesso = true;
        try {
            sql = FuncoesGlobais.Subst("DELETE FROM razao WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM retencao WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM imposto WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM extrato WHERE rc_aut = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("UPDATE recibo SET TAG = ' ', autenticacao = '0' WHERE autenticacao = '&1.'", new String[] {nrAut});
            conn.CommandExecute(sql);
            
            //sql = FuncoesGlobais.Subst("UPDATE antecipados SET dtrecebimento = Null, rc_aut = '0' WHERE  rc_aut = '&1.';", new String[] {nrAut});
            //conn.CommandExecute(sql);
        } catch (Exception e) {sucesso = false;}
        return sucesso;
    }
    
    private boolean ExtornaAdiantamentos(String nrAut) {
        String sql = ""; boolean sucesso = true;
        conn.ExisteFuncRMVADIANTA();
        try {
            sql = "UPDATE adavisos SET data = null, logado = null, ad_aut = '0' WHERE ad_aut = " + StringManager.Left(nrAut,nrAut.length() -1) + ";";
            conn.CommandExecute(sql);
            
            sql = "UPDATE recibo SET campo = rmvAdianta(campo) WHERE campo LIKE '%:AD%" + StringManager.Left(nrAut,nrAut.length() -1) + "%@%';";
            conn.CommandExecute(sql);
        } catch (Exception e) {sucesso = false;}
        return sucesso;
    }
    
    private void jbtExtornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtExtornarActionPerformed

        String sql = ""; String sItem = jDesc.getSelectedItem().toString();
        boolean sucesso = false; String nrAut = jAutenticacao.getText().trim();
        String tpExt = "";
        if (sItem.indexOf("PC") > -1) {
            JOptionPane.showMessageDialog(null, "Não é permitido extornar passagens de caixa!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            jAutenticacao.requestFocus();            
            return;
        } else if (sItem.indexOf("AV") > -1) {
            sucesso = ExtornaAvisos(nrAut);
            tpExt = "AVISO";
        } else if (sItem.indexOf("RC") > -1) {
            sucesso = ExtornaRecibos(nrAut);
            tpExt = "RECIBO";
        } else if (sItem.indexOf("ET") > -1) {
            sucesso = ExtornaExtratos(nrAut);
            tpExt = "EXTRATO";
        } else if (sItem.indexOf("DP") > -1) {
            sucesso = ExtornaDespesas(nrAut);
            tpExt = "DESPESA";
        } else if (sItem.indexOf("DS") > -1) {
            sucesso = ExtornaDespositos(nrAut);
            tpExt = "DEPOSITO";
        } else if (sItem.indexOf("BO") > -1) {
            sucesso = ExtornaBO(nrAut);
            tpExt = "BO";
        } else if (sItem.indexOf("AD") > -1) {
            sucesso = ExtornaAdiantamentos(nrAut);
            tpExt = "ADIANTAMENTO";
        }

        String msg = "Documento extornado com SUCESSO!!!";
        if (sucesso) {
            try {
                // Avisa ao caixa
                sql = FuncoesGlobais.Subst("UPDATE caixa SET cx_doc = CONCAT(cx_doc,'X') WHERE cx_aut = '&1.'", new String[] {jAutenticacao.getText()});
                conn.CommandExecute(sql);

                sql = FuncoesGlobais.Subst("DELETE FROM auxiliar WHERE rc_aut = '&1.'", new String[] {jAutenticacao.getText()});
                conn.CommandExecute(sql);

                conn.Auditor("EXTORNO:" + tpExt, nrAut);
            } catch (Exception e) {msg = "Erro ao Extonar!!!\n\nAvise o superte técnico...";}
        }
        JOptionPane.showMessageDialog(null, msg, "Atenção", JOptionPane.INFORMATION_MESSAGE);
        jAutenticacao.requestFocus();
    }//GEN-LAST:event_jbtExtornarActionPerformed

    private void jbtSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtSairActionPerformed

    private void jDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDescActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jDescActionPerformed

    private void jValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jValorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jValorActionPerformed

    private boolean Extornar(String nAut) {
        boolean bret = false;
        String sql = "SELECT * FROM caixa WHERE TRIM(UPPER(cx_logado)) = '&1.' AND cx_aut = &2. AND NOT cx_doc LIKE '%X'";
        sql = FuncoesGlobais.Subst(sql, new String[] {VariaveisGlobais.usuario.trim().toUpperCase(), nAut});
        ResultSet rs = conn.OpenTable(sql, null);

        jDesc.removeAllItems();
        float fTot = 0;
        try {
            while (rs.next()) {
                // verifica quando for cheque se o mesmo não foi depositado
                Object[][] cheque = conn.ReadFieldsTable(new String[] {"ch_autenticacao"}, "Chequesbck", "ch_autenticacao = " + nAut);
                if (cheque == null) {
                    jDesc.addItem(rs.getString("cx_hora") + " " + rs.getString("cx_oper") + " " + rs.getString("cx_doc") + " " +
                            rs.getString("cx_tipopg") + " " + LerValor.floatToCurrency(rs.getFloat("cx_vrdn") + rs.getFloat("cx_vrch"),2));

                    fTot += rs.getFloat("cx_vrdn") + rs.getFloat("cx_vrch");
                    bret = true;
                    
                } else {bret = false; break;}
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rs);

        jValor.setValue(fTot);

        return bret;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jAutenticacao;
    private javax.swing.JComboBox jDesc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JFormattedTextField jValor;
    private javax.swing.JButton jbtExtornar;
    private javax.swing.JButton jbtSair;
    // End of variables declaration//GEN-END:variables
}
