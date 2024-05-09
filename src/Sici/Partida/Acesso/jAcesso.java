package Sici.Partida.Acesso;

import Funcoes.Db;
import Funcoes.VariaveisGlobais;
import static Sici.Partida.Acesso.StringToDocumentToString.findInSubMenu;
import static Sici.Partida.Acesso.StringToDocumentToString.pegaListSubMenus;
import static Sici.Partida.Acesso.StringToDocumentToString.subMenuConverter;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class jAcesso extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    boolean toogleAltera = false;
    
    /**
     * Creates new form jAcesso
     */
    public jAcesso() {
        initComponents();
        
        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/acessos.svg",16,16);
        setFrameIcon(icone);        

        DefaultComboBoxModel listUsuariosModel = new DefaultComboBoxModel();
        for (classUsuarios item : ListaUsuarios()) {
            listUsuariosModel.addElement(new classUsuarios(item.getCodigo(), item.getNome(), item.getAcesso()));
        }
        cbUsuarios.setRenderer(new ListCellRenderModel());        
        cbUsuarios.setModel(listUsuariosModel);
        cbUsuarios.setSelectedIndex(0);
        
        jBtnAlterar.setEnabled(true);
        jBtnResetar.setEnabled(false);
        jBtnGravar.setEnabled(false);
        
        jBtnAlterar.addActionListener((ActionEvent evt) -> {
            cbUsuarios.setEnabled(false);
            toogleAltera = true;
            toogleDragAndDrop();
            
            jBtnAlterar.setEnabled(false);
            jBtnResetar.setEnabled(true);
            jBtnGravar.setEnabled(true);
        });
        
        jBtnResetar.addActionListener((ActionEvent evt) -> {
            ShowMenusUsuarios(null);
            jList1.requestFocus();
        });
        
        jBtnGravar.addActionListener((ActionEvent evt) -> {
            if (JOptionPane.showConfirmDialog(this, 
                    "Desena gravar?", 
                    "Atenção!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                ) == JOptionPane.NO_OPTION) {

                // Retorna ao estado original
                String xmlStr = null;
                try { xmlStr = ((classUsuarios)cbUsuarios.getSelectedItem()).getAcesso().trim(); } catch (Exception e) {}
                ShowMenusUsuarios(xmlStr);

                cbUsuarios.setEnabled(true);
                jBtnAlterar.setEnabled(true);
                jBtnResetar.setEnabled(false);
                jBtnGravar.setEnabled(false);
                
                cbUsuarios.requestFocus();
                return;
            } 
            
            toogleAltera = false;
            toogleDragAndDrop();

            cbUsuarios.setEnabled(true);
            jBtnAlterar.setEnabled(true);
            jBtnResetar.setEnabled(false);
            jBtnGravar.setEnabled(false);
            
            String xmlCadastros = "<Cadastros>";
            xmlCadastros = RetornaXmlMenus(xmlCadastros, jList2);
            
            String xmlCaixa = "<Caixa>";
            xmlCaixa = RetornaXmlMenus(xmlCaixa, jList3);
            
            String xmlMovimento = "<Movimento>";
            xmlMovimento = RetornaXmlMenus(xmlMovimento, jList4);
            
            String xmlRelatorios = "<Relatorios>";
            xmlRelatorios = RetornaXmlMenus(xmlRelatorios, jList5);
            
            String xmlGerencia = "<Gerencia>";
            xmlGerencia = RetornaXmlMenus(xmlGerencia, jList6);
            
            String xml = "<?xml version=\"1.0\"?><menu>" +
                         (isVazioMenuXml(xmlCadastros,"Cadastros") ? "" : xmlCadastros) +
                         (isVazioMenuXml(xmlCaixa,"Caixa") ? "" : xmlCaixa) +                   
                         (isVazioMenuXml(xmlMovimento,"Movimento") ? "" : xmlMovimento) +                   
                         (isVazioMenuXml(xmlRelatorios,"Relatorios") ? "" : xmlRelatorios) +                   
                         (isVazioMenuXml(xmlGerencia,"Gerencia") ? "" : xmlGerencia) + 
                         "</menu>";

            classUsuarios usuariosValue = ((classUsuarios)cbUsuarios.getSelectedItem());
            usuariosValue.setAcesso(xml);
            String updateSQL = "UPDATE cadfun SET acesso = :acesso WHERE f_cod = :codigo;";
            if (conn.CommandExecute(updateSQL, new Object[][] {
                {"string", "acesso", xml},
                {"string", "codigo", usuariosValue.getCodigo()}
            }) == 0) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar no servidor!");
            }
            
            cbUsuarios.requestFocus();
        });
    }

    private boolean isVazioMenuXml(String xml, String menu) {
        String _menu = "<" + menu + ">" + "</" + menu + ">";
        return xml.equalsIgnoreCase(_menu);
    }
    
    private String RetornaXmlMenus(String menu, JList Lista) {
        String xml = menu;
        for (int i = 0; i <= Lista.getModel().getSize() - 1; i++) {
            DefaultListModel item =  (DefaultListModel) Lista.getModel();
            classMenu itm = (classMenu) item.elementAt(i);
            xml += "<Sub code=\"" + itm.getId() + "\"";                   
            if (itm.getBotoes() != null) {
                String[] _botoes = itm.getBotoes().split(" ");
                String Botoes = "";
                for (String btn : _botoes) {
                    String[] _btn = btn.split("=");
                    Botoes += _btn[0] + "=" + _btn[1] + " ";
                }
                xml += " " + Botoes.trim();
            } 
            xml += " />";
        }
        return xml += "</" + menu.substring(1);
    }
    
    private void toogleDragAndDrop() {
        jList1.setDragEnabled(toogleAltera);        
        jList2.setDragEnabled(toogleAltera);        
        jList3.setDragEnabled(toogleAltera);        
        jList4.setDragEnabled(toogleAltera);        
        jList5.setDragEnabled(toogleAltera);        
        jList6.setDragEnabled(toogleAltera);        
    }
    
    private List<classUsuarios> ListaUsuarios() {
        ResultSet rst = conn.OpenTable("SELECT f_cod, usuario f_nome, acesso FROM cadfun ORDER BY Upper(f_nome);", null);
        List<classUsuarios> lista = new ArrayList<>();
        try {
            while (rst.next()) {
                String _cod = rst.getString("f_cod");
                String _nome = rst.getString("f_nome");
                String _acesso = rst.getString("acesso");
                
                lista.add(new classUsuarios(_cod, _nome, _acesso));                
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rst);

        return lista;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cbUsuarios = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList<>();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList<>();
        jBtnAlterar = new javax.swing.JButton();
        jBtnGravar = new javax.swing.JButton();
        jBtnResetar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Acessos dos usuários no sistema.");
        setMaximumSize(new java.awt.Dimension(1050, 506));
        setMinimumSize(new java.awt.Dimension(1050, 506));

        jLabel1.setText("Selecione o usuário:");

        cbUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUsuariosActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rotinas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(241, 428));
        jPanel1.setMinimumSize(new java.awt.Dimension(241, 428));
        jPanel1.setPreferredSize(new java.awt.Dimension(241, 428));

        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cadastro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel2.setMaximumSize(new java.awt.Dimension(270, 195));
        jPanel2.setMinimumSize(new java.awt.Dimension(270, 195));
        jPanel2.setPreferredSize(new java.awt.Dimension(270, 195));

        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList2MouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Caixa", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel3.setMaximumSize(new java.awt.Dimension(263, 195));
        jPanel3.setMinimumSize(new java.awt.Dimension(263, 195));
        jPanel3.setPreferredSize(new java.awt.Dimension(263, 195));

        jList3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList3MouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jList3);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Movimento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel4.setMaximumSize(new java.awt.Dimension(270, 221));
        jPanel4.setMinimumSize(new java.awt.Dimension(270, 221));

        jList4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList4MouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(jList4);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Relatorios", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel5.setMaximumSize(new java.awt.Dimension(270, 221));
        jPanel5.setMinimumSize(new java.awt.Dimension(270, 221));
        jPanel5.setPreferredSize(new java.awt.Dimension(270, 221));

        jList5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList5MouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(jList5);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gerencia", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel6.setMaximumSize(new java.awt.Dimension(241, 428));
        jPanel6.setMinimumSize(new java.awt.Dimension(241, 428));
        jPanel6.setPreferredSize(new java.awt.Dimension(241, 428));

        jList6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList6MouseReleased(evt);
            }
        });
        jScrollPane6.setViewportView(jList6);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
        );

        jBtnAlterar.setText("Alterar");

        jBtnGravar.setText("Gravar");
        jBtnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnGravarActionPerformed(evt);
            }
        });

        jBtnResetar.setText("Resetar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jBtnAlterar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnResetar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBtnGravar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel2, jPanel3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnAlterar)
                    .addComponent(jBtnGravar)
                    .addComponent(jBtnResetar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUsuariosActionPerformed
        String xmlStr = null;
        try { xmlStr = ((classUsuarios)cbUsuarios.getSelectedItem()).getAcesso().trim(); } catch (Exception e) {}
        ShowMenusUsuarios(xmlStr);
        
    }//GEN-LAST:event_cbUsuariosActionPerformed

    private void jList2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseReleased
        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (toogleAltera) ModificaBotoes(jList2);
        }
    }//GEN-LAST:event_jList2MouseReleased

    private void jList3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList3MouseReleased
        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (toogleAltera) ModificaBotoes(jList3);
        }
    }//GEN-LAST:event_jList3MouseReleased

    private void jList4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList4MouseReleased
        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (toogleAltera) ModificaBotoes(jList4);
        }
    }//GEN-LAST:event_jList4MouseReleased

    private void jList5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList5MouseReleased
        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (toogleAltera) ModificaBotoes(jList5);
        }
    }//GEN-LAST:event_jList5MouseReleased

    private void jList6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList6MouseReleased
        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (toogleAltera) ModificaBotoes(jList6);
        }
    }//GEN-LAST:event_jList6MouseReleased

    private void jBtnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnGravarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBtnGravarActionPerformed

    private void ShowMenusUsuarios(String xmlStr) {
        // Carrega todos os itens de menu
        List<classSubMenu> menuRotinasAll = new ArrayList<>();        
        String selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 ORDER BY autoid;";
        ResultSet rs = conn.OpenTable(selectSQL, null);        
        try {
            while (rs.next()) {
                menuRotinasAll.add(
                        new classSubMenu(
                                rs.getInt("autoid"), 
                                rs.getString("botoes"), 
                                rs.getString("nome"), 
                                rs.getString("rotina")
                        )
                );
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        
        // Filtra itens dos sumenus no menus excluido-os
        List<String> subMenus = null;
        if (xmlStr != null) {
            subMenus = pegaListSubMenus(xmlStr);        
            for (String sub : subMenus) {
                if (sub == null) continue;

                List<classSubMenu> _sub = subMenuConverter(sub);
                for (classSubMenu ssub : _sub) {
                    int nPos = findInSubMenu(ssub.getId(), menuRotinasAll);
                    if (nPos > -1) menuRotinasAll.remove(nPos);
                }
            }
        }
        
        // Carrega itens filtrado do menu
        DefaultListModel listData1 = new DefaultListModel();
        for (classSubMenu mnu : menuRotinasAll) {
            selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 AND autoid = :id ORDER BY autoid;";
            rs = conn.OpenTable(selectSQL, new Object[][] {{"int", "id", mnu.getId()}});

            try {
                while (rs.next()) {
                    String icones = null;
                    try { icones = rs.getString("icone").trim(); } catch (Exception sqlEx) { icones = null; }                
                    if (icones == null) icones = null; else icones = "menuIcons/" + icones;

                    listData1.addElement(new classMenu(
                        rs.getInt("autoid"), 
                        rs.getString("nome"),
                        rs.getString("atalho"), 
                        icones, 
                        rs.getString("rotina"), 
                        rs.getBoolean("senha"),
                        rs.getString("botoes"),
                        rs.getString("tooltips"))
                    );
                }
            } catch (SQLException sqlEx) {}
            conn.CloseTable(rs);          
        }
        
        DefaultListModel listData2 = new DefaultListModel();
        DefaultListModel listData3 = new DefaultListModel();
        DefaultListModel listData4 = new DefaultListModel();
        DefaultListModel listData5 = new DefaultListModel();
        DefaultListModel listData6 = new DefaultListModel();
        if (subMenus != null) {       
            // Cadastros
            String _Cadastros = subMenus.get(0);
            if (_Cadastros != null) {
                List<classSubMenu> _sub = subMenuConverter(_Cadastros);
                for (classSubMenu ssub : _sub) {
                    selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 AND autoid = :id ORDER BY autoid;";
                    rs = conn.OpenTable(selectSQL, new Object[][] {{"int", "id", ssub.getId()}});

                    try {
                        while (rs.next()) {
                            String icones = null;
                            try { icones = rs.getString("icone").trim(); } catch (Exception sqlEx) { icones = null; }                
                            if (icones == null) icones = null; else icones = "menuIcons/" + icones;

                            listData2.addElement(new classMenu(
                                rs.getInt("autoid"), 
                                rs.getString("nome"),
                                rs.getString("atalho"), 
                                icones, 
                                rs.getString("rotina"), 
                                rs.getBoolean("senha"),
                                ssub.getBotoes(),
                                rs.getString("tooltips"))
                            );
                        }
                    } catch (SQLException sqlEx) {}
                    conn.CloseTable(rs);          
                }
            }

            String _Caixa = subMenus.get(1);
            if (_Caixa != null) {
                List<classSubMenu> _sub = subMenuConverter(_Caixa);
                for (classSubMenu ssub : _sub) {
                    selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 AND autoid = :id ORDER BY autoid;";
                    rs = conn.OpenTable(selectSQL, new Object[][] {{"int", "id", ssub.getId()}});

                    try {
                        while (rs.next()) {
                            String icones = null;
                            try { icones = rs.getString("icone").trim(); } catch (Exception sqlEx) { icones = null; }                
                            if (icones == null) icones = null; else icones = "menuIcons/" + icones;

                            listData3.addElement(new classMenu(
                                rs.getInt("autoid"), 
                                rs.getString("nome"),
                                rs.getString("atalho"), 
                                icones, 
                                rs.getString("rotina"), 
                                rs.getBoolean("senha"),
                                ssub.getBotoes(),
                                rs.getString("tooltips"))
                            );
                        }
                    } catch (SQLException sqlEx) {}
                    conn.CloseTable(rs);          
                }
            }

            String _Movimento = subMenus.get(2);
            if (_Movimento != null) {
                List<classSubMenu> _sub = subMenuConverter(_Movimento);
                for (classSubMenu ssub : _sub) {
                    selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 AND autoid = :id ORDER BY autoid;";
                    rs = conn.OpenTable(selectSQL, new Object[][] {{"int", "id", ssub.getId()}});

                    try {
                        while (rs.next()) {
                            String icones = null;
                            try { icones = rs.getString("icone").trim(); } catch (Exception sqlEx) { icones = null; }                
                            if (icones == null) icones = null; else icones = "menuIcons/" + icones;

                            listData4.addElement(new classMenu(
                                rs.getInt("autoid"), 
                                rs.getString("nome"),
                                rs.getString("atalho"), 
                                icones, 
                                rs.getString("rotina"), 
                                rs.getBoolean("senha"),
                                ssub.getBotoes(),
                                rs.getString("tooltips"))
                            );
                        }
                    } catch (SQLException sqlEx) {}
                    conn.CloseTable(rs);          
                }
            }

            String _Relatorios = subMenus.get(3);
            if (_Relatorios != null) {
                List<classSubMenu> _sub = subMenuConverter(_Relatorios);
                for (classSubMenu ssub : _sub) {
                    selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 AND autoid = :id ORDER BY autoid;";
                    rs = conn.OpenTable(selectSQL, new Object[][] {{"int", "id", ssub.getId()}});

                    try {
                        while (rs.next()) {
                            String icones = null;
                            try { icones = rs.getString("icone").trim(); } catch (Exception sqlEx) { icones = null; }                
                            if (icones == null) icones = null; else icones = "menuIcons/" + icones;

                            listData5.addElement(new classMenu(
                                rs.getInt("autoid"), 
                                rs.getString("nome"),
                                rs.getString("atalho"), 
                                icones, 
                                rs.getString("rotina"), 
                                rs.getBoolean("senha"),
                                ssub.getBotoes(),
                                rs.getString("tooltips"))
                            );
                        }
                    } catch (SQLException sqlEx) {}
                    conn.CloseTable(rs);          
                }
            }

            String _Gerencia = subMenus.get(4);
            if (_Gerencia != null) {
                List<classSubMenu> _sub = subMenuConverter(_Gerencia);
                for (classSubMenu ssub : _sub) {
                    selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 AND autoid = :id ORDER BY autoid;";
                    rs = conn.OpenTable(selectSQL, new Object[][] {{"int", "id", ssub.getId()}});

                    try {
                        while (rs.next()) {
                            String icones = null;
                            try { icones = rs.getString("icone").trim(); } catch (Exception sqlEx) { icones = null; }                
                            if (icones == null) icones = null; else icones = "menuIcons/" + icones;

                            listData6.addElement(new classMenu(
                                rs.getInt("autoid"), 
                                rs.getString("nome"),
                                rs.getString("atalho"), 
                                icones, 
                                rs.getString("rotina"), 
                                rs.getBoolean("senha"),
                                ssub.getBotoes(),
                                rs.getString("tooltips"))
                            );
                        }
                    } catch (SQLException sqlEx) {}
                    conn.CloseTable(rs);          
                }
            }
        }
        
        SimpleListDTSManager simpleListDTSManage1 = new SimpleListDTSManager();
        SimpleListDTSManager simpleListDTSManage2 = new SimpleListDTSManager();
        SimpleListDTSManager simpleListDTSManage3 = new SimpleListDTSManager();
        SimpleListDTSManager simpleListDTSManage4 = new SimpleListDTSManager();
        SimpleListDTSManager simpleListDTSManage5 = new SimpleListDTSManager();
        SimpleListDTSManager simpleListDTSManage6 = new SimpleListDTSManager();
        
        jList1.setModel(listData1);
        jList1.setCellRenderer(new MenuRenderModel());
        jList1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList1.setDragEnabled(toogleAltera);
        jList1.setTransferHandler(simpleListDTSManage1);   
        
        jList2.setModel(listData2);
        jList2.setCellRenderer(new MenuRenderModel());
        jList2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList2.setDragEnabled(toogleAltera);
        jList2.setTransferHandler(simpleListDTSManage2);   
        
        jList3.setModel(listData3);
        jList3.setCellRenderer(new MenuRenderModel());
        jList3.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList3.setDragEnabled(toogleAltera);
        jList3.setTransferHandler(simpleListDTSManage3);   
        
        jList4.setModel(listData4);
        jList4.setCellRenderer(new MenuRenderModel());
        jList4.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList4.setDragEnabled(toogleAltera);
        jList4.setTransferHandler(simpleListDTSManage4);                   
        
        jList5.setModel(listData5);
        jList5.setCellRenderer(new MenuRenderModel());
        jList5.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList5.setDragEnabled(toogleAltera);
        jList5.setTransferHandler(simpleListDTSManage5);   
        
        jList6.setModel(listData6);
        jList6.setCellRenderer(new MenuRenderModel());
        jList6.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList6.setDragEnabled(toogleAltera);
        jList6.setTransferHandler(simpleListDTSManage6);                   
        
        jList1.setDropMode(DropMode.INSERT);
        jList2.setDropMode(DropMode.INSERT);
        jList3.setDropMode(DropMode.INSERT);
        jList4.setDropMode(DropMode.INSERT);
        jList5.setDropMode(DropMode.INSERT);
        jList6.setDropMode(DropMode.INSERT);
        
        jList1.setBorder(new EmptyBorder(0, 0, 0, 0));
        jList2.setBorder(new EmptyBorder(0, 0, 0, 0));
        jList3.setBorder(new EmptyBorder(0, 0, 0, 0));
        jList4.setBorder(new EmptyBorder(0, 0, 0, 0));
        jList5.setBorder(new EmptyBorder(0, 0, 0, 0));
        jList6.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        jPanel1.setBorder(new RoundedBorder(Color.GRAY, 2, 10));        
        jPanel2.setBorder(new RoundedBorder(Color.GRAY, 2, 10));
        jPanel3.setBorder(new RoundedBorder(Color.GRAY, 2, 10));
        jPanel4.setBorder(new RoundedBorder(Color.GRAY, 2, 10));
        jPanel5.setBorder(new RoundedBorder(Color.GRAY, 2, 10));
        jPanel6.setBorder(new RoundedBorder(Color.GRAY, 2, 10));
        
    }
  
    private void ModificaBotoes(JList jLista) {
        //JList jLista = (JList) e.getSource();
        if (jLista.getSelectedIndex() == -1) return;
        DefaultListModel item =  (DefaultListModel) jLista.getModel();
        classMenu itm = (classMenu) item.elementAt(jLista.getSelectedIndex());
        if (itm.getBotoes() != null) {
            JDialog dialog = new JDialog();
            dialog.setTitle(".:: Opções de " + itm.getDescricao());
            
            JPanel cbxPanel = new JPanel();            
            int x = 5; int cbxWidth = 0; int cbxHeigth = 0;
            String[] botoes = itm.getBotoes().split(" ");
            for (String btn : botoes) {
                String[] _btn = btn.split("=");
                 JCheckBox _cbx = new JCheckBox(_btn[0]);
                 
                 FontMetrics metrics = _cbx.getFontMetrics(_cbx.getFont());
                 cbxHeigth = metrics.getHeight();
                 int adv = metrics.stringWidth(_btn[0]);
                 cbxWidth += adv;
                 
                 _cbx.setSelected(new Boolean(_btn[1].replace("\"", "")));
                 _cbx.setVisible(true);
                 
                 cbxPanel.add(_cbx);
            }
            
            cbxPanel.setSize(cbxWidth, cbxHeigth );
            cbxPanel.setLocation(0, 0);
            cbxPanel.setVisible(true);

            JPanel btnPanel = new JPanel();
            btnPanel.setVisible(true);
            
            JButton btn = new JButton("Gravar");
            btn.addActionListener((ActionEvent evt) -> {
                String xmlXtrasBotoes = "";
                for (Component c : dialog.getComponents()) {
                    if (c instanceof JRootPane) {
                        for (Component d : ((JRootPane)c).getComponents()) {
                            if (d instanceof JLayeredPane) {
                                for (Component m : ((JLayeredPane)d).getComponents()) {
                                    if (m instanceof JPanel) {
                                        for (Component q : ((JPanel)m).getComponents()) {
                                            if (q instanceof JPanel) {
                                                for (Component z : ((JPanel)q).getComponents()) {
                                                    if (z instanceof JCheckBox) {
                                                        xmlXtrasBotoes += ((JCheckBox)z).getText().trim() + "=\"" + (((JCheckBox)z).isSelected() ? "true" : "false") + "\" ";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                xmlXtrasBotoes = xmlXtrasBotoes.trim();
                itm.setBotoes(xmlXtrasBotoes);
            });
            
            btn.setEnabled(true);
            btn.setVisible(true);

            FontMetrics metrics = btn.getFontMetrics(btn.getFont());
            int btnWidth = metrics.stringWidth("Gravar");
            
            btnPanel.add(btn);
            cbxPanel.add(btnPanel);
                        
            dialog.add(cbxPanel);
            dialog.setSize(cbxWidth + (btnWidth * 10), 80);
            dialog.setResizable(false);
            dialog.setModal(true);
            dialog.setLocationByPlatform(true);
            dialog.setVisible(true);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbUsuarios;
    private javax.swing.JButton jBtnAlterar;
    private javax.swing.JButton jBtnGravar;
    private javax.swing.JButton jBtnResetar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    // End of variables declaration//GEN-END:variables
}