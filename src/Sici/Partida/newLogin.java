package Sici.Partida;

import Funcoes.Criptografia;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FTPDownloadLicenceFile;
import Funcoes.VariaveisGlobais;
import Sici.Login.jLicensa;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/**
 *
 * @author YOGA 510
 */
public class newLogin extends javax.swing.JFrame {
    Db conn = VariaveisGlobais.conexao;
    
    private int tryLicense = 0;
    
    /**
     * Creates new form login
     */
    public newLogin() {
        setUndecorated(true);
        try {
            setBackground(new Color(0,0,0,0));
        } catch (IllegalArgumentException ex) {}
        
        setSize(478, 267);
        initComponents();
        
        // Icone
        setIconImage(new ImageIcon(getClass().getResource("/Figuras/logo.png")).getImage());
        
        // background
        URL url = getClass().getResource("/Figuras/login.png");
        
        ImageIcon icone = new ImageIcon(url);
        JLabel1.setIcon(icone);
        setLocationRelativeTo(null);         

        // Unidades de acesso remoto new
        jUnidade.removeAllItems();
        for (int w=0; w < VariaveisGlobais.units.size(); w++) {
            jUnidade.addItem(VariaveisGlobais.units.get(w)[0].toString());
        }
        
        StartLogin();
        if (Certification()) {
            String crySenha  = System.getProperty("Key", "7kf51b");        
            if (crySenha != "7kf51b") {
                VariaveisGlobais.sqlPwd = Criptografia.decrypt(crySenha, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
            } else VariaveisGlobais.sqlPwd = crySenha;                    
        }        
    }

    private void StartLogin() {
        jUnidade.setVisible(true);
        jUnidade.setEnabled(true);
        jUsuario.setText("");
        jUsuario.setEnabled(false);
        jSenha.setText("");
        jSenha.setEnabled(false);
        
        jStatus.setVisible(false);
        jUnidade.requestFocus();        
    }

    private boolean Certification() {      
        VariaveisGlobais.Licenca = System.getProperty("Id", "gq8nm8MHmfGY+4BghFpWAw==");
        
        // Validar se esta na Validação
        String crypData = System.getProperty("Reg", "16-11-2022");
        String decrypData = Criptografia.decrypt(crypData, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
        Date DataCryp = Dates.StringtoDate(decrypData, "dd-MM-yyyy");
        Date DataServ = new Date(); //DbMain.getDateTimeServer();
        int expData = Dates.DateDiff("D", DataCryp, DataServ);
        System.out.println("progressão: " + Math.abs(expData) + " " + (expData <= 0 ? "positiva" : "negativa"));
        if (expData > 0) {
            String _netHost = System.getProperty("Host", null);
            String _netUser = System.getProperty("User", null);
            String _netPwd = System.getProperty("Pwd", null);
            if (_netHost != null) {
                // Valida numero de tentativas
                tryLicense++;

                String netHost = Criptografia.decrypt(_netHost, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
                String netUser = Criptografia.decrypt(_netUser, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
                String netPwd = Criptografia.decrypt(_netPwd, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
            
                FTPDownloadLicenceFile ftp = new FTPDownloadLicenceFile();
                if (ftp.Licence(netHost, netUser, netPwd)) {
                    System.out.println("Licensa renovada com sucesso.");
                    System.out.println("Revalidando liçensa...");
                    
                    if (tryLicense <= 2) {
                        return Certification();
                    }                     
                }
            }
                        
            jLicensa _licensa = new jLicensa(null, true);
            _licensa.setVisible(true);
            boolean retorno = _licensa.isRetorno();
            if (retorno) {
                return Certification();
            }
            
            System.exit(0);
        }                        
                
        return true;
    }
    
    private int ChecaData() {
        String tmpData = null;
        try {
            tmpData = conn.ReadParameters("PARTIDA");
        } catch (Exception e) {}
        if (tmpData == null) return -1;
        
        Date sData = Dates.StringtoDate(tmpData, "dd-MM-yyyy");
        Date tData = new Date();
        
        int nDias = Dates.DateDiff(Dates.DIA, sData, tData);
        
        return nDias;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jUnidade = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jUsuario = new javax.swing.JTextField();
        jSenha = new javax.swing.JPasswordField();
        jStatus = new javax.swing.JTextField();
        JLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(".:: Imobilis - Sistema Imobiliário");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jUnidade.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jUnidadeFocusGained(evt);
            }
        });
        jUnidade.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jUnidadeMouseClicked(evt);
            }
        });
        jUnidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jUnidadeKeyPressed(evt);
            }
        });
        getContentPane().add(jUnidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 190, -1));

        jLabel4.setText("Usuário:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 172, 50, -1));

        jLabel3.setText("Estação:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 142, -1, -1));

        jLabel5.setText("Senha:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 202, -1, -1));

        jUsuario.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jUsuarioFocusGained(evt);
            }
        });
        jUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jUsuarioKeyPressed(evt);
            }
        });
        getContentPane().add(jUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 170, 190, -1));

        jSenha.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jSenha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSenhaFocusGained(evt);
            }
        });
        jSenha.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jSenhaKeyPressed(evt);
            }
        });
        getContentPane().add(jSenha, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 190, -1));

        jStatus.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jStatus.setForeground(new java.awt.Color(255, 0, 0));
        getContentPane().add(jStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 230, 190, -1));

        JLabel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                JLabel1MouseDragged(evt);
            }
        });
        JLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JLabel1MousePressed(evt);
            }
        });
        getContentPane().add(JLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 267));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jUnidadeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUnidadeFocusGained
        jUnidade.setEnabled(true);
        jUsuario.setText("");
        jUsuario.setEnabled(false);
        jSenha.setText("");
        jSenha.setEnabled(false);
    }//GEN-LAST:event_jUnidadeFocusGained

    private void jUnidadeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jUnidadeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            enterUnit();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }//GEN-LAST:event_jUnidadeKeyPressed

    private void enterUnit() {
        // Fax login no banco de dados
        int pos = jUnidade.getSelectedIndex();
        Object[] selUnit = VariaveisGlobais.units.get(pos);
        String _host = selUnit[1].toString().substring(0,selUnit[1].toString().indexOf(":"));
        int _port = Integer.parseInt(selUnit[1].toString().substring(selUnit[1].toString().indexOf(":") + 1));
        String _dbname = selUnit[2].toString();

        VariaveisGlobais.sqlAlias = selUnit[0].toString();
        VariaveisGlobais.sqlHost = _host;
        VariaveisGlobais.sqlPort = _port;
        VariaveisGlobais.sqlDbName = _dbname;                        
        conn = new Db(this);
        if (conn == null) {
            jUnidade.requestFocus();
            return;
        } 

        VariaveisGlobais.conexao = conn;
        jUnidade.setEnabled(true);
        jUsuario.setEnabled(true);
        jSenha.setEnabled(false);
        jUsuario.requestFocus();
    }
    
    private void jUsuarioFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUsuarioFocusGained
        jUnidade.setEnabled(true);
        jUsuario.setText("");
        jUsuario.setEnabled(true);
        jSenha.setText("");
        jSenha.setEnabled(false);
    }//GEN-LAST:event_jUsuarioFocusGained

    private void jUsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jUsuarioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (jUsuario.getText().trim().equalsIgnoreCase("")) {
                jUsuario.requestFocus();
                return;
            }
            
            jUnidade.setEnabled(true);
            jUsuario.setEnabled(true);
            jSenha.setEnabled(true);
            jSenha.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            conn.CloseDb();
            conn = null;
            jUsuario.setText("");
            jUnidade.requestFocus();
        }
    }//GEN-LAST:event_jUsuarioKeyPressed

    private void jSenhaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSenhaKeyPressed
        if (evt.getKeyCode()==KeyEvent.VK_ENTER) { 
            // Aqui checa se o usuario é valido
            try {
                String mfuncao = "", mnome = "", mcpf = "", mprotocolo = "";
                ResultSet rs = conn.OpenTable("SELECT usuario, senha, f_funcao, f_nome, f_cpf, acesso FROM cadfun WHERE usuario = '" + jUsuario.getText().trim() + "' AND senha = '" + jSenha.getText().trim() + "';", null);
                if (rs.next()) {
                    jStatus.setText("");
                    mfuncao = rs.getString("f_funcao");
                    mnome = rs.getString("f_nome");
                    mcpf = rs.getString("f_cpf");
                    mprotocolo = rs.getString("acesso").trim();
                } else {
                    jStatus.setText("Usuário ou Senha invalido!!!");
                    jUsuario.setText("");
                    jSenha.setText("");
                    jUsuario.requestFocus();
                    return;
                }
                
                VariaveisGlobais.usuario = jUsuario.getText().toLowerCase().trim();
                VariaveisGlobais.funcao = mfuncao.toLowerCase().trim();
                VariaveisGlobais.protocolomenu = mprotocolo;
                
                conn.CloseTable(rs);
                
                // Aqui checa se esta tentando retoragir data
                int nDias = ChecaData(); boolean bSplash = false;
                if (nDias > 0) {
                    // Grava nova data de Partida
                    conn.SaveParameters(new String[] {"PARTIDA",Dates.DateFormata("dd-MM-yyyy", new Date()),"TEXTO"});                    
                    bSplash = true;
                } else if (nDias < 0) {
                     JOptionPane.showMessageDialog(jSenha, "A Data não pode ser retroativa...", "Atenção!!!", ERROR_MESSAGE);
                }
                
                dispose();                                
                
                final boolean _bSplash = bSplash;
                JWindow aguarde = new JWindow();
                //aguarde.setSize(300, 50);

                JLabel label = new JLabel("Carregando...");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 24));
                label.setForeground(Color.RED);
                label.setSize(300,50);
                
                JProgressBar pgb = new JProgressBar();
                pgb.setIndeterminate(true);
                
                aguarde.getContentPane().setLayout(new BorderLayout());
                aguarde.getContentPane().add(label, BorderLayout.NORTH);
                aguarde.getContentPane().add(pgb, BorderLayout.CENTER);
                aguarde.pack();

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (screenSize.width - aguarde.getWidth()) / 2;
                int y = (screenSize.height - aguarde.getHeight()) / 2;
                aguarde.setLocation(x, y);

                //aguarde.setSize(300, 50);
                aguarde.setVisible(true);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        SiciMenu pr = new SiciMenu(_bSplash);
                        pr.setExtendedState(Frame.MAXIMIZED_BOTH);                
                        pr.setVisible(true);
                        
                        return null;
                    }

                    @Override
                    protected void done() {
                        aguarde.dispose();
                    }
                };
                worker.addPropertyChangeListener(new PropertyChangeListener() {
                    
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                            worker.removePropertyChangeListener(this);
                            aguarde.dispose();
                        }
                    }
                });

                worker.execute();                

//                SiciMenu pr = new SiciMenu(bSplash);
//                pr.setExtendedState(Frame.MAXIMIZED_BOTH);                
//                pr.setVisible(true);
//
            } catch (HeadlessException | SQLException  e) {
                jStatus.setText("Unidade não encontrada ou fora do ar!!!");
                e.printStackTrace();
            }
        }
        if (evt.getKeyCode()==KeyEvent.VK_ESCAPE) { 
            jSenha.setText("");
            jUsuario.requestFocus();
        }        
    }//GEN-LAST:event_jSenhaKeyPressed

    private void jSenhaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSenhaFocusGained
        jSenha.setText("");
    }//GEN-LAST:event_jSenhaFocusGained

    // Mouse coordinates
    private int oX;
    private int oY;    
    private void JLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JLabel1MousePressed
        oX = evt.getX();
        oY = evt.getY();
    }//GEN-LAST:event_JLabel1MousePressed

    private void JLabel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JLabel1MouseDragged
        int nX = evt.getXOnScreen();
        int nY = evt.getYOnScreen();
               
        setLocation(nX - oX, nY - oY);
    }//GEN-LAST:event_JLabel1MouseDragged

    private void jUnidadeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jUnidadeMouseClicked
        if (evt.getClickCount() == 2) {
            enterUnit();
        }
    }//GEN-LAST:event_jUnidadeMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new newLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField jSenha;
    private javax.swing.JTextField jStatus;
    private javax.swing.JComboBox<String> jUnidade;
    private javax.swing.JTextField jUsuario;
    // End of variables declaration//GEN-END:variables
}
