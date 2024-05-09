package Movimento;

import Funcoes.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.ComboBoxEditor;
import javax.swing.ImageIcon;

public class jVisitas extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    boolean bExecNome = false, bExecCodigo = false, eBloq = false;
    boolean insert = false;
    
    /**
     * Creates new form jVisitas
     */
    public jVisitas() {
        initComponents();
        
        // Icone da tela
        ImageIcon icone = new FlatSVGIcon("menuIcons/visitas.svg",16,16);
        setFrameIcon(icone);
        
        ComboBoxEditor edit = jRgimv.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                jbtDevChaves.setEnabled(false);
                jbtChaves.setEnabled(false);
                CleanDados();
                ShowEditDados(false);
                jbtGravar.setEnabled(false);
                jbtcancelar.setEnabled(false);
                jHistorico.setEnabled(false);
                insert = false;
            }
        });
        
        try {conn.CreateVisitas();} catch (Exception e) {}
        ComboBoxEditor edit1 = jEndereco.getEditor();
        Component comp1 = edit1.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                Object[][] dados = null;
                try {
                  dados = conn.ReadFieldsTable(
                  new String[] {"dv_nome","dv_docto","dv_telefone","dv_dthrETD"},
                  "visitas", 
                  FuncoesGlobais.Subst("rgimv = '&1.' AND " + 
                          "(isnull(dv_dthrETA) OR Trim(dv_dthrETA) = '')",
                    new String[] {jRgimv.getSelectedItem().toString().trim()}));
                } catch (Exception err) {}
                if (dados != null) {
                    insert = false;
                    jNome.setText(dados[0][3].toString());
                    jDocto.setText(dados[1][3].toString());
                    jTelefone.setText(dados[2][3].toString());
                    try {jDataHora.setText(Dates.DateFormata("dd/MM/yyyy HH:mm", 
                            Dates.StringtoDate(dados[3][3].toString(),"yyyy/MM/dd HH:mm")));
                    } catch (Exception err) {err.printStackTrace();}
                    jbtDevChaves.setEnabled(true);
                    jbtChaves.setEnabled(false);
                    ShowEditDados(false);
                    jbtGravar.setEnabled(false);
                    jbtcancelar.setEnabled(false);
                } else {
                    insert = true;
                    jbtDevChaves.setEnabled(false);
                    jbtChaves.setEnabled(true);
                    CleanDados();
                    ShowEditDados(false);
                    jHistorico.setEnabled(false);
                    jbtGravar.setEnabled(false);
                    jbtcancelar.setEnabled(false);
                }
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                jbtDevChaves.setEnabled(false);
                jbtChaves.setEnabled(false);
                ShowEditDados(false);
                jbtGravar.setEnabled(false);
                jbtcancelar.setEnabled(false);
                jHistorico.setEnabled(false);
                insert = false;
            }
        });

        this.setSize(this.getWidth(), this.getHeight() - jpnlHistorico.getHeight());
        
        FillImoveisVazios();
        AutoCompletion.enable(jRgimv);
        AutoCompletion.enable(jEndereco);
        
        jRgimv.requestFocus();
    }

    private void ShowEditDados(boolean edit) {
        jNome.setEnabled(edit);
        jDocto.setEnabled(edit);
        jTelefone.setEnabled(edit);
        jDataHora.setEditable(false);
        if (edit) {CleanDados(); jNome.requestFocus();}
    }
    
    private void CleanDados() {
        try {
            jNome.setText("");
        jDocto.setText("");
        jTelefone.setText("");
        jDataHora.setText("");
        jHistorico.setText("");
        } catch (Exception err) {err.printStackTrace();}
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
        jRgimv = new javax.swing.JComboBox();
        jEndereco = new javax.swing.JComboBox();
        jbtChaves = new javax.swing.JButton();
        jbtDevChaves = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jDataHora = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jNome = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jDocto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTelefone = new javax.swing.JFormattedTextField();
        jpnlHistorico = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jHistorico = new javax.swing.JTextArea();
        jbtGravar = new javax.swing.JButton();
        jbtcancelar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Visitas ao imóvel - Chaves");
        setVisible(true);

        jLabel1.setText("Imóvel:");

        jRgimv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgimvActionPerformed(evt);
            }
        });

        jEndereco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEnderecoActionPerformed(evt);
            }
        });

        jbtChaves.setText("Entrega das Chaves");
        jbtChaves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtChavesActionPerformed(evt);
            }
        });

        jbtDevChaves.setText("Devolução das Chaves");
        jbtDevChaves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtDevChavesActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(254, 209, 165));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "[ Dados do Visitante ]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Data/Hora:");

        jDataHora.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm"))));

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel3.setText("Nome:");

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel4.setText("Documento:");

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel5.setText("Telefone:");

        try {
            jTelefone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jNome)
                    .addComponent(jDocto)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDataHora, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jDocto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jDataHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jpnlHistorico.setBackground(new java.awt.Color(165, 207, 235));
        jpnlHistorico.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "[ Histórico ]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        jHistorico.setColumns(20);
        jHistorico.setRows(5);
        jScrollPane1.setViewportView(jHistorico);

        javax.swing.GroupLayout jpnlHistoricoLayout = new javax.swing.GroupLayout(jpnlHistorico);
        jpnlHistorico.setLayout(jpnlHistoricoLayout);
        jpnlHistoricoLayout.setHorizontalGroup(
            jpnlHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlHistoricoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jpnlHistoricoLayout.setVerticalGroup(
            jpnlHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnlHistoricoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jbtGravar.setText("Gravar");
        jbtGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtGravarActionPerformed(evt);
            }
        });

        jbtcancelar.setText("Cancelar");
        jbtcancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtcancelarActionPerformed(evt);
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
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jEndereco, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jpnlHistorico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jbtChaves, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtDevChaves, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(151, 151, 151))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jbtGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtcancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtChaves)
                    .addComponent(jbtDevChaves))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpnlHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtGravar)
                    .addComponent(jbtcancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtChavesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtChavesActionPerformed
        jbtGravar.setEnabled(true);
        jbtcancelar.setEnabled(true);
        jHistorico.setEnabled(false);
        jbtDevChaves.setEnabled(false);
        jbtChaves.setEnabled(false);
        
        jDataHora.setText(Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()));
        ShowEditDados(true);
    }//GEN-LAST:event_jbtChavesActionPerformed

    private void jbtDevChavesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtDevChavesActionPerformed
        jbtDevChaves.setEnabled(false);
        jbtChaves.setEnabled(false);
        jHistorico.setEnabled(true);
        jbtGravar.setEnabled(true);
        jbtcancelar.setEnabled(true);
        jHistorico.requestFocus();
    }//GEN-LAST:event_jbtDevChavesActionPerformed

    private void jRgimvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRgimvActionPerformed
        if (!bExecNome) {
            int pos = jRgimv.getSelectedIndex();
            if (jEndereco.getItemCount() > 0) {bExecCodigo = true; jEndereco.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jRgimvActionPerformed

    private void jEnderecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEnderecoActionPerformed
        if (!bExecCodigo) {
            int pos = jEndereco.getSelectedIndex();
            if (jRgimv.getItemCount() > 0) {bExecNome = true; jRgimv.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jEnderecoActionPerformed

    private void jbtcancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtcancelarActionPerformed
        CleanDados();
        ShowEditDados(false);
        jHistorico.setEnabled(false);
        jbtGravar.setEnabled(false);
        jbtcancelar.setEnabled(false);
        jbtDevChaves.setEnabled(false);
        jbtChaves.setEnabled(true);        
        jRgimv.requestFocus();
    }//GEN-LAST:event_jbtcancelarActionPerformed

    private void jbtGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtGravarActionPerformed
        
        SaveDados((insert ? "ETD" : "ETA"));
        
        CleanDados();
        ShowEditDados(false);
        jbtGravar.setEnabled(false);
        jbtcancelar.setEnabled(false);
        jbtDevChaves.setEnabled(false);
        jbtChaves.setEnabled(true);
        jHistorico.setEnabled(false);
        jRgimv.requestFocus();
    }//GEN-LAST:event_jbtGravarActionPerformed

 private void FillImoveisVazios() {
        String sSql = "SELECT * FROM imoveis WHERE Lower(Trim(situacao)) = 'vazio' ORDER BY rgprp, rgimv;";
        ResultSet imResult = this.conn.OpenTable(sSql, null);

        try {
            while (imResult.next()) {
                String trgimv = String.valueOf(imResult.getInt("rgimv"));
                String tend = imResult.getString("end").trim() + ", " + imResult.getString("num").trim() + " " + imResult.getString("compl").trim() ;

                jRgimv.addItem(trgimv);
                jEndereco.addItem(tend);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        conn.CloseTable(imResult);

    }
 
    private void SaveDados(String operacao) {
        String sql = ""; 
        if (operacao.equals("ETD")) {
            sql = "INSERT INTO visitas (rgimv, end, dv_nome, dv_docto, " +
                "dv_telefone, dv_dthrETD) VALUES ('&1.','&2.','&3.'," +
                "'&4.','&5.','&6.')";
            sql = FuncoesGlobais.Subst(sql, new String[] {
                  jRgimv.getSelectedItem().toString().trim(),
                  jEndereco.getSelectedItem().toString().trim(),
                  jNome.getText().trim().toUpperCase(),
                  jDocto.getText().trim().toUpperCase(),
                  jTelefone.getText(), 
                  Dates.DateFormata("yyyy/MM/dd HH:mm", new Date())});
        } else {
            // ETA
            sql = "UPDATE visitas SET dv_dthrETA = '&1.', dv_historico = " +
                  "'&2.' WHERE rgimv = '&3.' AND dv_nome = '&4.';";
            sql = FuncoesGlobais.Subst(sql, new String[] {
                  Dates.DateFormata("yyyy/MM/dd HH:mm", new Date()),
                  jHistorico.getText(),
                  jRgimv.getSelectedItem().toString(),
                  jNome.getText().trim().toUpperCase()});
        }
        try { conn.CommandExecute(sql); } catch (Exception err) {}
    }   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField jDataHora;
    private javax.swing.JTextField jDocto;
    private javax.swing.JComboBox jEndereco;
    private javax.swing.JTextArea jHistorico;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jNome;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox jRgimv;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField jTelefone;
    private javax.swing.JButton jbtChaves;
    private javax.swing.JButton jbtDevChaves;
    private javax.swing.JButton jbtGravar;
    private javax.swing.JButton jbtcancelar;
    private javax.swing.JPanel jpnlHistorico;
    // End of variables declaration//GEN-END:variables

}

//CREATE TABLE `WinGerGambasPasseli`.`visitas` (
//  `ord` INTEGER  NOT NULL AUTO_INCREMENT,
//  `rgimv` VARCHAR(6)  NOT NULL,
//  `end` VARCHAR(100)  NOT NULL,
//  `dv_nome` VARCHAR(60) ,
//  `dv_docto` VARCHAR(60) ,
//  `dv_telefone` VARCHAR(15) ,
//  `dv_dthrETD` DATETIME ,
//  `dv_dthrETA` DATETIME ,
//  `dv_historico` TEXT ,
//  PRIMARY KEY (`ord`)
//)