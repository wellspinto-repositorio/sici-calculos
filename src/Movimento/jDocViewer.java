package Movimento;

import static Funcoes.FuncoesGlobais.disableLookInComboBox;
import Funcoes.Outlook;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.gmail.GmailAPI;
import static Funcoes.gmail.GmailOperations.createEmailWithAttachment;
import static Funcoes.gmail.GmailOperations.createMessageWithEmail;
import Funcoes.toPreview;
import Funcoes.toPrint;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.regex.PatternSyntaxException;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author samic
 */
public class jDocViewer extends javax.swing.JInternalFrame {
    String pasta; String tfiltro;

    String jEmailEmp = ""; String jSenhaEmail = ""; boolean jPop = false; boolean jAutentica = false;
    String jEndPopImap = ""; String jPortPopImap = ""; String jSmtp = ""; String jPortSmtp = "";
    String jAssunto = ""; String jMsgEmail = ""; String jFTP_Conta = ""; String jFTP_Porta = "";
    String jFTP_Usuario = ""; String jFTP_Senha = ""; 

    TableRowSorter<TableModel> sorter;
    
    /**
     * Creates new form jDocViewer
     */
    public jDocViewer() {
        initComponents();
        
        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/preview.svg",16,16);
        setFrameIcon(icone);        
        
        Date aano = new Date();        
        ano.setValue(1900 + aano.getYear());
        String[] ames = new String[] {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        mes.getModel().setValue(ames[aano.getMonth()]);
        
        jPasta.setEnabled(VariaveisGlobais.webswing);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tipo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        ano = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        mes = new javax.swing.JSpinner();
        bVersão = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tFiles = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnClear = new javax.swing.JLabel();
        filtro = new javax.swing.JTextField();
        btPreview = new javax.swing.JButton();
        jPasta = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSubject = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMensagem = new javax.swing.JTextArea();
        jbtSend = new javax.swing.JButton();
        jPara = new javax.swing.JTextField();
        jBuscar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: 2ª Via de Documentos Impressos...");
        setToolTipText("");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        tipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Recibos", "Boletas", "Extratos", "Relatórios", "Boletas B.Digitais", "Recibos PIX" }));
        tipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoActionPerformed(evt);
            }
        });

        jLabel1.setText("Ano:");

        ano.setModel(new javax.swing.SpinnerNumberModel(2014, 2014, 2050, 1));
        ano.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                anoStateChanged(evt);
            }
        });

        jLabel2.setText("Mês:");

        mes.setModel(new javax.swing.SpinnerListModel(new String[] {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"}));
        mes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mesStateChanged(evt);
            }
        });

        bVersão.setText("Versão Anterior");
        bVersão.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVersãoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ano, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mes, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(bVersão, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(ano, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(mes, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bVersão))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true), "[ Arquivos ]", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N

        tFiles.setAutoCreateRowSorter(true);
        tFiles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tFilesMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tFiles);

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel3.setText("Filtro:");

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/Clean_16x16.jpeg"))); // NOI18N
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnClearMouseClicked(evt);
            }
        });

        filtro.setBorder(null);
        filtro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filtroActionPerformed(evt);
            }
        });
        filtro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtroKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(filtro)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        btPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Actions-document-print-preview-icon.png"))); // NOI18N
        btPreview.setText("Preview");
        btPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPreviewActionPerformed(evt);
            }
        });

        jPasta.setText("Download");
        jPasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPastaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btPreview)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPasta, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPreview))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btPreview, jPasta});

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true), "( Enviar documento anexado por EMail )", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N

        jLabel4.setText("PARA:");
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel5.setText("ASSUNTO:");
        jLabel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel6.setText("MENSAGEM:");
        jLabel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jMensagem.setColumns(20);
        jMensagem.setRows(5);
        jScrollPane1.setViewportView(jMensagem);

        jbtSend.setText("Enviar");
        jbtSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSendActionPerformed(evt);
            }
        });

        jPara.setToolTipText("");

        jBuscar.setText("Buscar");
        jBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPara, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtSend, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSubject))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jPara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(53, 53, 53)
                            .addComponent(jbtSend)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoActionPerformed
        FillFiles();
    }//GEN-LAST:event_tipoActionPerformed

    private void anoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_anoStateChanged
        FillFiles();
    }//GEN-LAST:event_anoStateChanged

    private void mesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mesStateChanged
        FillFiles();
    }//GEN-LAST:event_mesStateChanged

    private void btnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseClicked
        filtro.setText(null);
        filtro.requestFocus();
    }//GEN-LAST:event_btnClearMouseClicked

    private void filtroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtroActionPerformed

    private void btPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPreviewActionPerformed
        int selRow = tFiles.getSelectedRow();
        int modelRow = tFiles.convertRowIndexToModel(selRow);
        String rdoc = (String) tFiles.getModel().getValueAt(modelRow, 0);
        String docName = pasta + rdoc;

        if (docName.substring(docName.length() - 3, docName.length()).equalsIgnoreCase("PDF")) {
            new toPreview(docName);
        } else {
            new toPrint(docName, "TERMICA","INTERNA");
        }
    }//GEN-LAST:event_btPreviewActionPerformed

    private void filtroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroKeyReleased
        if ("".equals(filtro.getText().trim())) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(filtro.getText().trim()));
            } catch (PatternSyntaxException pse) {
                   System.err.println("Bad regex pattern");
            }
        }
    }//GEN-LAST:event_filtroKeyReleased

    private void bVersãoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVersãoActionPerformed
        FillFiles();
        ano.setEnabled(!bVersão.isSelected());
        mes.setEnabled(!bVersão.isSelected());
    }//GEN-LAST:event_bVersãoActionPerformed

    private void jbtSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSendActionPerformed
        if (jSubject.getText().trim().equalsIgnoreCase("") || jMensagem.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "Campos assunto e mensagem não podem estar em branco!!!", "Atenção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selRow = tFiles.getSelectedRow();
        int modelRow = tFiles.convertRowIndexToModel(selRow);
        String rdoc = (String) tFiles.getModel().getValueAt(modelRow, 0);
        
        if (VariaveisGlobais.OUTLOOK) {
            Outlook email = new Outlook(true);
            try {            
                String To = jPara.getText().trim().toLowerCase();
                String Subject = jSubject.getText().trim();
                String Body = jMensagem.getDocument().getText(0, jMensagem.getDocument().getLength());
                String[] Attachments = new String[] {System.getProperty("user.dir") + "/" + pasta + rdoc};
                email.Send(To, null, Subject, Body, Attachments);
                if (!email.isSend()) {
                    JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                email = null;
            }
        } else {
            try {            
                String To = jPara.getText().trim().toLowerCase();
                String Subject = jSubject.getText().trim();
                String Body = jMensagem.getDocument().getText(0, jMensagem.getDocument().getLength());
                String[] Attachments = new String[] {System.getProperty("user.dir") + "/" + pasta + rdoc};

                Gmail service = GmailAPI.getGmailService();
                MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(System.getProperty("user.dir") + "/" + pasta + rdoc));
                Message message = createMessageWithEmail(Mimemessage);
                message = service.users().messages().send("me", message).execute();

                System.out.println("Message id: " + message.getId());
                System.out.println(message.toPrettyString());
                if (message.getId() == null) {
                    JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }//GEN-LAST:event_jbtSendActionPerformed

    private void jBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBuscarActionPerformed
        Object[][] busca = {}; String jcampo = ""; String jtabela = ""; String jwhere = "";

        if (jPara.getText().toUpperCase().subSequence(0, 1).equals("P")) {
            jcampo = "email";
            jtabela = "proprietarios";
            jwhere = "rgprp = '" + jPara.getText().toUpperCase().substring(1) + "'";
        } else if (jPara.getText().toUpperCase().subSequence(0, 1).equals("L")) {
            jcampo = "email";
            jtabela = "locatarios";
            jwhere = "contrato = '" + jPara.getText().toUpperCase().substring(1) + "'";
        } if (jPara.getText().toUpperCase().subSequence(0, 1).equals("F")) {
            jcampo = "email";
            jtabela = "fiadores";
            jwhere = "contrato = '" + jPara.getText().toUpperCase().substring(1) + "'";
        }
        try {
            busca = VariaveisGlobais.conexao.ReadFieldsTable(new String[] {jcampo}, jtabela, jwhere);
        } catch (Exception e) {}

        try {
            if (!busca[0][3].toString().trim().equalsIgnoreCase("")) {
                jPara.setText(busca[0][3].toString());
            }
        } catch (Exception e) {}
    }//GEN-LAST:event_jBuscarActionPerformed

    private void tFilesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tFilesMouseReleased
        int selRow = tFiles.getSelectedRow();
        int modelRow = tFiles.convertRowIndexToModel(selRow);
        String rdoc = (String) tFiles.getModel().getValueAt(modelRow, 0).toString().toLowerCase();
        
        if (rdoc.contains(".pdf")) {
            btPreview.setText("Preview");
        } else {
            btPreview.setText("Print");
        }
    }//GEN-LAST:event_tFilesMouseReleased

    private void jPastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPastaActionPerformed
        FillFilesPath();
    }//GEN-LAST:event_jPastaActionPerformed

   private void FillPastas(String pasta, String extencao) {
        tFiles.setEnabled(false);
        File[] boletas = listarArquivos(pasta, extencao);
        
        TableControl.Clear(tFiles);
        if (boletas != null) {
            // Seta Cabecario
            TableControl.header(tFiles, new String[][] {{"Arquivos da pasta"},{"1000"}});
            for (int i=0; i<boletas.length;i++) {
                TableControl.add(tFiles, new String[][]{{boletas[i].getName()},{"L"}}, true);
                if (boletas[i].getName().toLowerCase().contains(".pdf")) {
                    // adiciona icone pdf
                    //TableControl.alt(tFiles,  , tFiles.getRowCount(), 0);
                }
            }

            sorter = new TableRowSorter<TableModel>(tFiles.getModel());
            tFiles.setRowSorter(sorter);
        }
        tFiles.setEnabled(true);
    }
    
   public File[] listarArquivos(String caminhoDiretorio, final String extensao){  
      File F = new File(caminhoDiretorio);  
        
        
      File[] files = F.listFiles(new FileFilter() {  
  
         public boolean accept(File pathname) {  
            return pathname.getName().toLowerCase().endsWith(extensao);  
         }  
      });      
        
      return files;  
   }  
   
    private void FillFiles() {
        if (tipo.getSelectedItem().equals("Recibos")) {
            pasta = "reports/Recibos/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
            FillPastas(pasta, tfiltro);
        } else if (tipo.getSelectedItem().equals("Boletas")) {
            pasta = "reports/Boletas/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
            FillPastas(pasta, tfiltro);
        } else if (tipo.getSelectedItem().equals("Extratos")) {
            pasta = "reports/Extratos/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
            FillPastas(pasta, tfiltro);
        } else if (tipo.getSelectedItem().equals("Relatórios")) {
            pasta = "reports/Relatorios/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
            FillPastas(pasta, tfiltro);
        } else if (tipo.getSelectedItem().equals("Boletas B.Digitais")) {
            pasta = "reports/BoletasDigital/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
            FillPastas(pasta, tfiltro);
        } else if (tipo.getSelectedItem().equals("Recibos PIX")) {
            pasta = "reports/Pix/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
            FillPastas(pasta, tfiltro);
        } 
    }
    
    private void FillFilesPath() {
        int selRow = tFiles.getRowCount();
        if (selRow <= 0) {
            JOptionPane.showMessageDialog(this, "Esta pasta esta vazia!");
            return;
        }
        
        if (tipo.getSelectedItem().equals("Recibos")) {
            pasta = "reports/Recibos/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
        } else if (tipo.getSelectedItem().equals("Boletas")) {
            pasta = "reports/Boletas/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
        } else if (tipo.getSelectedItem().equals("Extratos")) {
            pasta = "reports/Extratos/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
        } else if (tipo.getSelectedItem().equals("Relatórios")) {
            pasta = "reports/Relatorios/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
        } else if (tipo.getSelectedItem().equals("Boletas B.Digitais")) {
            pasta = "reports/BoletasDigital/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
        } else if (tipo.getSelectedItem().equals("Recibos PIX")) {
            pasta = "reports/Pix/" + (!bVersão.isSelected() ? ano.getValue().toString() + "/" + mes.getValue() + "/" : "");
            tfiltro = (!bVersão.isSelected() ? "pdf" : "");
        }

        JFileChooser fileChooser = new JFileChooser();
        disableLookInComboBox(fileChooser);
        fileChooser.setControlButtonsAreShown(false);
        fileChooser.setFileHidingEnabled(true);

        // Defina o diretório inicial
        File initialDirectory = new File(pasta);
        fileChooser.setCurrentDirectory(initialDirectory);

        fileChooser.setDialogTitle("Download de arquivos.");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Arquivo selecionado: " + selectedFile.getAbsolutePath());
        }        
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner ano;
    private javax.swing.JCheckBox bVersão;
    private javax.swing.JButton btPreview;
    private javax.swing.JLabel btnClear;
    private javax.swing.JTextField filtro;
    private javax.swing.JButton jBuscar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextArea jMensagem;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jPara;
    private javax.swing.JButton jPasta;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jSubject;
    private javax.swing.JButton jbtSend;
    private javax.swing.JSpinner mes;
    private javax.swing.JTable tFiles;
    private javax.swing.JComboBox tipo;
    // End of variables declaration//GEN-END:variables
}
