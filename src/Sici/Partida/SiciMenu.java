package Sici.Partida;

import Funcoes.BackGroundDeskTopPane;
import Funcoes.CentralizaTela;
import Funcoes.Config;
import Funcoes.Criptografia;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.ProceduresSQL;
import Funcoes.VariaveisGlobais;
import Funcoes.gmail.GmailAPI;
import static Funcoes.gmail.GmailAPI.ReadJSon;
import static Funcoes.gmail.GmailOperations.createEmailWithAttachment;
import static Funcoes.gmail.GmailOperations.createMessageWithEmail;
import Funcoes.toPreview;
import static Sici.Partida.Acesso.StringToDocumentToString.pegaListSubMenus;
import static Sici.Partida.Acesso.StringToDocumentToString.subMenuConverter;
import Sici.Partida.Acesso.classSubMenu;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import java.awt.AWTKeyStroke;
import static java.awt.Adjustable.HORIZONTAL;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import static java.awt.Font.BOLD;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.json.JSONException;

public class SiciMenu extends JFrame {
    String regras = null;
    Db conn = VariaveisGlobais.conexao;
    jBuscaGlobalizada jBuscaGlobal;
    static boolean bSplash = false;
    
    private JMenuBar menuBar;    
    private int searchBarWidth = 300;
    private int searchBarHeigth = 30;
    private Color searchBarFocusColor = new Color(60,98,140);
    private JPopupMenu popupMenu = new JPopupMenu();
    private ButtonGroup bg = new ButtonGroup();
    private FlatTextField searchBar = new FlatTextField();    
    
    // Área de trabalho
    private JDesktopPane jDesktopPane1;
    private JToolBar jToolBar;
    private boolean toolbarOnTop = false;
    private boolean toolbarIsVisible = true;
    
    // Barra de progresso das msg auto
    private JPanel SplashPanel;
    private JLabel cobLabel;
    private JProgressBar cobProgres;
    
    public SiciMenu(boolean bSplash) {
        Config conf = new Config();
        VariaveisGlobais.LerConf(conf);
        VariaveisGlobais.LerGlobal(conf);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1245, 600));
        setMinimumSize(new Dimension(1245, 600));
        
        this.bSplash = bSplash;
        
        setTitle(".:: Imobilis ::.");
        setIconImage(new ImageIcon(getClass().getResource("/Figuras/logo.png")).getImage());
        
        // Busca Globalizada
        VariaveisGlobais.jBuscar = searchBar;
        
        InicializaVariaveis();

        // Leitura dos parametros gmail
        try { ReadJSon(); } catch (JSONException ex) { ex.printStackTrace(); }
        
        // Setar menu
        menuBar = montaMenu();        
        setJMenuBar(menuBar); 

        // Seta a àrea de trabalho e a toolbar
        jDesktopPane1 = new BackGroundDeskTopPane();
        getContentPane().add(jDesktopPane1, BorderLayout.CENTER);        

        jToolBar = ID();
        this.getContentPane().add(jToolBar, BorderLayout.NORTH);  
         
        InitBuscaGlobal();        
        InitSettings();
        
        //Centraliza a janela.
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = (int) (dimension.getWidth() - this.getSize().getWidth() ) / 2;
        int y = (int) (dimension.getHeight() - this.getSize().getHeight()) / 2;
        this.setLocation(x,y);

        /**
         * maximiza a janela
         */
        setLocationRelativeTo(null);
        setPreferredSize(new Dimension((int)dimension.getWidth(), (int)dimension.getHeight()));
        //setMinimumSize(new Dimension(800,600));
        this.setExtendedState( JFrame.MAXIMIZED_BOTH );
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        VariaveisGlobais.jPanePrin = jDesktopPane1;        
        jDesktopPane1.requestFocus();

        if (bSplash) {
            ProgressWindow window = new ProgressWindow();
            jDesktopPane1.add(window);
            window.setVisible(true);
            window.executeProcess();
            
            new ProceduresSQL().CriarMySqlProcedures("ALL", null);
        }
    }

    private class ProgressWindow extends JInternalFrame {       
        private JLabel label;
        private JProgressBar progressBar;
        
        public ProgressWindow() {
            super("Processando envio de menssagens...", true, true, true, true);
            setSize(300, 100);
            setLayout(new FlowLayout());
            
            label = new JLabel("Analizando recibos para mensagens automática...");
            add(label);
            
            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            progressBar.setPreferredSize(new Dimension(250, 20));
            add(progressBar);
        }
        
        public void executeProcess() {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                
                @Override
                protected Void doInBackground() throws Exception {
                    String _msgEmdia = "", _msgAtz = "";
                    try { _msgEmdia = conn.ReadParameters("COBAUTEMD"); } catch (Exception ex) { _msgEmdia = ""; }   
                    try { _msgAtz = conn.ReadParameters("COBAUTATZ"); } catch (Exception ex) { _msgAtz = ""; }        

                    List<classMensagens> listaMsg = new ArrayList<>();

                    String selectSQL = "SELECT datediff(CASE WHEN isnull(r.dtvencbol) THEN r.dtvencimento ELSE r.dtvencbol END, Now()) dias, r.contrato, " + 
                            "IF(ISNULL(r.dtvencbol), r.dtvencimento, r.dtvencbol) dtvencimento, r.nnumero, l.nomerazao, " + 
                            "l.cpfcnpj, concat(i.end, ', ', i.num, ' ', i.compl) as endereco, i.bairro, " + 
                            "i.cidade, l.email, l.celular, l.cobdias, l.cobemail, l.cobwhatsapp, r.boletapath FROM recibo r, locatarios l, imoveis i " + 
                            "WHERE (i.rgprp = r.rgprp AND i.rgimv = r.rgimv) AND " + 
                            "(l.rgprp = r.rgprp AND l.rgimv = r.rgimv AND l.contrato = r.contrato) AND " + 
                            "(NOT isnull(r.nnumero) AND (r.tag != 'X' OR r.tag != 'B') AND r.remessa = 'S') " + 
                            "AND l.cobaut = -1 AND datediff(CASE WHEN isnull(r.dtvencbol) THEN r.dtvencimento ELSE r.dtvencbol END, Now()) " + 
                            "between 0 AND 5 ORDER BY datediff(CASE WHEN isnull(r.dtvencbol) THEN r.dtvencimento ELSE r.dtvencbol END, Now());";
                    ResultSet rs = conn.OpenTable(selectSQL, null);
                    try {
                        boolean _bemail = false, _bwhatsapp = false;
                        String _email = "", _mensagem = "", _celular = "";
                        int _dias = 0, _ndias = 0; String _anexo = "";
                        int b = 0;
                        while (rs.next()) {
                            try { _bemail = rs.getBoolean("cobemail"); } catch (SQLException sqlEx) { _bemail = false; }
                            try { _bwhatsapp = rs.getBoolean("cobwhatsapp"); } catch (SQLException sqlEx) { _bwhatsapp = false; }
                            try { _dias = rs.getInt("cobdias"); } catch (SQLException sqlEx) { _dias = 0; }
                            try { _ndias = rs.getInt("dias"); } catch (SQLException sqlEx) { _ndias = 0; }
                            try { _email = rs.getString("email"); } catch (SQLException sqlEx) { _email = ""; }
                            try { _celular = rs.getString("celular"); } catch (SQLException sqlEx) { _celular = ""; }
                            try { _anexo = rs.getString("boletapath"); } catch (SQLException sqlEx) { _anexo = ""; }

                            boolean bMensagem = false;
                            if (rs.getInt("dias") == 0) {
                                _mensagem = _msgEmdia.replace("[dias]", String.valueOf(rs.getInt("dias"))).
                                        replace("[contrato]", rs.getString("contrato")).
                                        replace("[vencimento]", Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento"))).
                                        replace("[nnumero]", rs.getString("nnumero")).
                                        replace("[nome]", rs.getString("nomerazao")).
                                        replace("[cpfcnpj]", rs.getString("cpfcnpj")).
                                        replace("[endereco]", rs.getString("endereco")).
                                        replace("[bairro]", rs.getString("bairro")).
                                        replace("[cidade]", rs.getString("cidade")).
                                        replace("[hojeextenco]", Dates.DateFormata("dd 'de' MMMM 'de' yyyy", new Date())).
                                        replace("[hoje]", Dates.DateFormata("dd/MM/yyyy", new Date()));
                                bMensagem = true;
                            } else if ( (rs.getInt("dias") >= 1 && rs.getInt("dias") <= 5) && rs.getInt("dias") <= _dias) {
                                _mensagem = _msgAtz.replace("[dias]", String.valueOf(rs.getInt("dias"))).
                                        replace("[contrato]", rs.getString("contrato")).
                                        replace("[vencimento]", Dates.DateFormata("dd/MM/yyyy", rs.getDate("dtvencimento"))).
                                        replace("[nnumero]", rs.getString("nnumero")).
                                        replace("[nome]", rs.getString("nomerazao")).
                                        replace("[cpfcnpj]", rs.getString("cpfcnpj")).
                                        replace("[endereco]", rs.getString("endereco")).
                                        replace("[bairro]", rs.getString("bairro")).
                                        replace("[cidade]", rs.getString("cidade")).
                                        replace("[hojeextenco]", Dates.DateFormata("dd 'de' MMMM 'de' yyyy", new Date())).
                                        replace("[hoje]", Dates.DateFormata("dd/MM/yyyy", new Date()));
                                bMensagem = true;
                            }

                            if (bMensagem) listaMsg.add(new classMensagens(_bemail, _bwhatsapp, _email, _celular, _mensagem, _ndias, _anexo));
                        }
                    } catch (SQLException sqlEx) {}
                    conn.CloseTable(rs);

                    if (!listaMsg.isEmpty()) {
                        int totReg = listaMsg.size(); int b = 0;
                        label.setText("Enviando mensagens automáticas...");
                        progressBar.setMinimum(0);
                        progressBar.setMaximum(totReg);
                        progressBar.setValue(0);
                        for (classMensagens item : listaMsg) {
                            if (item.isbEmail()) {
                                File Attachment = new File(System.getProperty("user.dir") + "/" + item.getDocumento());
                                if (Attachment.exists()) {                               
                                    try {            
                                        String To = item.getEmail();
                                        String Subject = "Mensagem importante da " + VariaveisGlobais.dCliente.get("marca").toUpperCase().trim();
                                        String Body = item.getMensagem();

                                        Gmail service = GmailAPI.getGmailService();
                                        MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,Attachment);
                                        Message message = createMessageWithEmail(Mimemessage);
                                        message = service.users().messages().send("me", message).execute();
                                    } catch (HeadlessException | IOException | GeneralSecurityException | MessagingException  ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else if (item.isbWhatsapp()) {
                                // implantar
                            }     

                            Thread.sleep(100);
                            progressBar.setValue((b++ * 100) / totReg);
                        }
                    }
                    
                    return null;
                }
                
                @Override
                protected void done() {
                    dispose();
                }
            };
            
            worker.execute();
        }
    }
    
    private void InitSettings() {
        // Search Menu Options
        {
            boolean buscaProp = false; boolean buscaLoca = false;
            JCheckBoxMenuItem propCheckBoxMenuItem = new JCheckBoxMenuItem();
            JCheckBoxMenuItem imovCheckBoxMenuItem = new JCheckBoxMenuItem();
            if (AchaNoMenu("Proprietários")) {
                propCheckBoxMenuItem.setText("Proprietários");
                propCheckBoxMenuItem.setMnemonic('P');
                propCheckBoxMenuItem.addActionListener((e) -> {
                    jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.PROPRIETARIOS);
                });
                bg.add(propCheckBoxMenuItem);

                imovCheckBoxMenuItem.setText("Imóveis");
                imovCheckBoxMenuItem.setMnemonic('I');
                imovCheckBoxMenuItem.setSelected(false);
                imovCheckBoxMenuItem.addActionListener((e) -> {
                    jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.IMOVEIS);
                });
                bg.add(imovCheckBoxMenuItem);
                
                buscaProp = true;
            }
            
            JCheckBoxMenuItem locaCheckBoxMenuItem = new JCheckBoxMenuItem();
                JCheckBoxMenuItem fiadCheckBoxMenuItem = new JCheckBoxMenuItem();
            if (AchaNoMenu("Locatários")) {
                locaCheckBoxMenuItem.setText("Locatários");
                locaCheckBoxMenuItem.setMnemonic('L');
                locaCheckBoxMenuItem.addActionListener((e) -> {
                    jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.LOCATARIOS);
                });
                bg.add(locaCheckBoxMenuItem);

                fiadCheckBoxMenuItem.setText("Fiadores");
                fiadCheckBoxMenuItem.setMnemonic('F');
                fiadCheckBoxMenuItem.setSelected(false);
                fiadCheckBoxMenuItem.addActionListener((e) -> {
                    jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.FIADORES);
                });
                bg.add(fiadCheckBoxMenuItem);
                
                buscaLoca = true;
            }
            
            JCheckBoxMenuItem moviCheckBoxMenuItem = new JCheckBoxMenuItem();
            moviCheckBoxMenuItem.setText("Boletas");
            moviCheckBoxMenuItem.setMnemonic('I');
            moviCheckBoxMenuItem.setSelected(false);
            moviCheckBoxMenuItem.addActionListener((e) -> {
                jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.BOLETAS);
            });
            bg.add(moviCheckBoxMenuItem);

            if (buscaProp) popupMenu.add(propCheckBoxMenuItem);
            if (buscaProp) popupMenu.add(imovCheckBoxMenuItem);
            if (buscaLoca) popupMenu.add(locaCheckBoxMenuItem);
            if (buscaLoca) popupMenu.add(fiadCheckBoxMenuItem);
            popupMenu.addSeparator();
            popupMenu.add(moviCheckBoxMenuItem);
            
            if (buscaProp) {
                propCheckBoxMenuItem.setSelected(buscaProp);
                jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.PROPRIETARIOS);
            } else if (buscaLoca) {
                locaCheckBoxMenuItem.setSelected(buscaLoca);
                jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.LOCATARIOS);
            } else {
                moviCheckBoxMenuItem.setSelected(true);
                jBuscaGlobal.setBuscarpor(jBuscaGlobalizada.BuscarPOR.BOLETAS);
            }
        }         
        
        FlatSVGIcon themeIcon = new FlatSVGIcon("icons/moon.svg",16,16);
        themeIcon.setColorFilter( new FlatSVGIcon.ColorFilter( color -> Color.WHITE ) );
        JToggleButton themeSet = new JToggleButton((Icon)themeIcon);
        themeSet.setSelectedIcon((Icon)new FlatSVGIcon("icons/sun.svg",16,16));
        themeSet.setToolTipText("Tema escuro.");
        themeSet.addActionListener((e) -> {
            EventQueue.invokeLater(() -> {
                if (themeSet.isSelected()) {
                    themeSet.setToolTipText("Tema claro.");
                    FlatDarkLaf.setup();
                } else {
                    themeSet.setToolTipText("Tema escuro.");
                    FlatLightLaf.setup();
                }
                FlatLaf.updateUI();
                
                // Mudar cor do Icone de acordo com o tema
                Color colorIcon = FlatLaf.isLafDark() ? Color.WHITE : Color.BLACK;
                Function<Color, Color> mapper = (color -> { return colorIcon; });
                FlatSVGIcon.ColorFilter.getInstance().setMapper(mapper);
                try { SwingUtilities.windowForComponent(this).repaint(); } catch (Exception ex) {}
            });
        });
        
        JToolBar printerToolbar = new JToolBar();
        printerToolbar.add(themeSet);
        printerToolbar.addSeparator();
        
        FlatButton emailButton = new FlatButton();
        emailButton.setIcon((Icon)new FlatSVGIcon("icons/carta.svg",16,16));
        emailButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        emailButton.setFocusable(false);
        emailButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Hello User! How are you?", "User", 1));

        // seachBar Initializacion
        searchBar.setPreferredSize(new Dimension(searchBarWidth, searchBarHeigth));
        searchBar.setMaximumSize(new Dimension(searchBarWidth, searchBarHeigth));
        searchBar.setMinimumSize(new Dimension(searchBarWidth, searchBarHeigth));
        searchBar.setSize(searchBarWidth, searchBarHeigth);
        searchBar.setFocusable(true);
        searchBar.setRequestFocusEnabled(true);
        searchBar.putClientProperty("Component.focusWidth", 0);
        searchBar.putClientProperty("Component.focusColor", searchBarFocusColor);
        
        JButton searchHistoryButton = new JButton((Icon)new FlatSearchWithHistoryIcon(true));
        searchHistoryButton.setToolTipText("Search History");
        searchHistoryButton.addActionListener(e -> {
          popupMenu.show(searchHistoryButton, 0, searchHistoryButton.getHeight());
        });
        searchBar.putClientProperty("JTextField.leadingComponent", searchHistoryButton);
        
        JToggleButton desaCaseButton = new JToggleButton((Icon)new FlatSVGIcon("icons/user_group_add.svg", 16,16));
        desaCaseButton.setRolloverIcon((Icon)new FlatSVGIcon("icons/user_group_delete.svg",16,16));
        desaCaseButton.setSelectedIcon((Icon)new FlatSVGIcon("icons/user_group_delete.svg", 16,16));
        desaCaseButton.setToolTipText("Desativados");
        desaCaseButton.addActionListener((e) -> {
            jBuscaGlobal.setIsDeactived(desaCaseButton.isSelected());
        });
        
        JToggleButton matchCaseButton = new JToggleButton((Icon)new FlatSVGIcon("icons/matchCase.svg"));
        matchCaseButton.setRolloverIcon((Icon)new FlatSVGIcon("icons/matchCaseHovered.svg"));
        matchCaseButton.setSelectedIcon((Icon)new FlatSVGIcon("icons/matchCaseSelected.svg"));
        matchCaseButton.setToolTipText("Caso Sensitivo");
        matchCaseButton.addActionListener((e) -> {
            jBuscaGlobal.setIsCaseSensitive(matchCaseButton.isSelected());
        });
        
        JToggleButton wordsButton = new JToggleButton((Icon)new FlatSVGIcon("icons/words.svg"));
        wordsButton.setRolloverIcon((Icon)new FlatSVGIcon("icons/wordsHovered.svg"));
        wordsButton.setSelectedIcon((Icon)new FlatSVGIcon("icons/wordsSelected.svg"));
        wordsButton.setToolTipText("Frase Inteira");
        wordsButton.addActionListener((e) -> {
            jBuscaGlobal.setIsHeuristica(!wordsButton.isSelected());
        });
    
        searchBar.setPlaceholderText("Busca Globalizada...   <Esc> - fecha.");
        
        JToolBar searchToolbar = new JToolBar();
        searchToolbar.addSeparator();
        searchToolbar.add(desaCaseButton);
        searchToolbar.addSeparator();
        searchToolbar.add(matchCaseButton);
        searchToolbar.add(wordsButton);

        searchBar.putClientProperty("JTextField.trailingComponent", searchToolbar);
        searchBar.putClientProperty("JTextField.showClearButton", true);
        searchBar.putClientProperty("JTextField.showClearButton", true);
    
        menuBar.add(Box.createGlue());
        menuBar.add((Component)printerToolbar);
        menuBar.add((Component)searchBar);
        //menuBar.add((Component)emailButton);
    }
    
    private boolean AchaNoMenu(String value) {
        boolean retorno = false;
        
        // Setar os botoes
        String xmlStr = VariaveisGlobais.protocolomenu;
        if (xmlStr != null) {
            List<String> subMenus = pegaListSubMenus(xmlStr);
            for (String menus : subMenus) {
                if (menus == null) continue;

                List<classSubMenu> _sub = subMenuConverter(menus);
                for (classSubMenu ssub : _sub) {
                    if (ssub.getNome().equalsIgnoreCase(value)) {
                        retorno = true;
                        break;
                    }
                }
                if(retorno) break;
            }
        }
        return retorno;
    }
        
    private void InitBuscaGlobal() {
        jBuscaGlobal = new jBuscaGlobalizada();
        
        jBuscaGlobal.setVisible(false);
        jBuscaGlobal.setClosable(true);
        jBuscaGlobal.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jBuscaGlobal.setTitle(".:: Busca Globalizada");
        jBuscaGlobal.setVisible(true);
        jBuscaGlobal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jBuscaGlobal.setVisible(false);
            }
            
            public void focusGained(FocusEvent e) {
                int xpos = VariaveisGlobais.jPanePrin.getWidth() - getWidth();
                int ypos = 0; //40;
                jBuscaGlobal.setLocation(xpos, ypos);
            }
        });        

        jDesktopPane1.add(jBuscaGlobal);
        jBuscaGlobal.setBounds(380, 0, 570, 250);
        jBuscaGlobal.setVisible(false);
        
        jBuscaGlobal.setIsHeuristica(true);
        jBuscaGlobal.setIsDeactived(false);
    }
    
    private JMenuBar montaMenu() {
        //Cria a barra  
        JMenuBar barraMenu = new JMenuBar();  
        barraMenu.setBackground(new java.awt.Color(153, 153, 255));
        barraMenu.setMaximumSize(new java.awt.Dimension(146, 146));

        SetarMenu(barraMenu);
        
        // Cria o Sobre/About
        JMenu menuAbout = new JMenu("Sobre");  
        
        // Ligar e desligar a toolbar
        JCheckBoxMenuItem itemToolBar = new javax.swing.JCheckBoxMenuItem();
        itemToolBar.setIcon(new FlatSVGIcon("icons/toolbar.svg",16,16)); 
        itemToolBar.setText("ToolBar");
        itemToolBar.setSelected(true);
        itemToolBar.setToolTipText("Ligar ou Desligar ToolBar...");
        itemToolBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToolBar.setVisible(!toolbarIsVisible);
                toolbarIsVisible = !toolbarIsVisible;
                itemToolBar.setSelected(toolbarIsVisible);
            }
        });
        menuAbout.add(itemToolBar);
        
        if (new File(System.getProperty("user.dir") + "/resources/licença.pdf").exists()) {
            // Aqui vão os itens
            JMenuItem itemContrato = new javax.swing.JMenuItem();
            itemContrato.setIcon(new FlatSVGIcon("toolBarIcons/licensa.svg",16,16)); 
            itemContrato.setText("Licença de uso");
            itemContrato.setToolTipText("Informações legais...");
            itemContrato.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    String inFileName = System.getProperty("user.dir") + "/resources/licença.pdf";
                    new toPreview(inFileName);
                }
            });
            menuAbout.add(itemContrato);
        }
        
        JMenuItem itemSobre = new javax.swing.JMenuItem();
        itemSobre.setIcon(new FlatSVGIcon("toolBarIcons/about.svg",16,16)); // NOI18N
        itemSobre.setText("Sobre o Programa");
        itemSobre.setToolTipText("Informações sobre o Programa...");
        itemSobre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new jAbout(null, true).setVisible(true);
            }
        });
        menuAbout.add(itemSobre);
        
        //Adiciona o menu na barra  
        barraMenu.add(menuAbout);

        //Cria o menu  
        JMenu menuSair = new JMenu("Sair");  
        
        // Aqui vão os itens
        JMenuItem itemSair = new javax.swing.JMenuItem();
        itemSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        itemSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        itemSair.setText("Logout");
        itemSair.setToolTipText("Logout do sistema...");
        itemSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
                try {
                    (new Main()).main(new String[]{""});
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        menuSair.add(itemSair);

        // Aqui vão os itens
        JMenuItem itemFechar = new javax.swing.JMenuItem();
        itemFechar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        itemFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        itemFechar.setText("Encerrar o Sistema");
        itemFechar.setToolTipText("Sai completamente do programa...");
        itemFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.exit(0);
            }
        });
        menuSair.add(itemFechar);

        //Adiciona o menu na barra  
        barraMenu.add(menuSair);

        return barraMenu;
    }

    private void Setar(JMenu menuItem, JMenuItem item, classSubMenu ssub) {
        String selectSQL = "SELECT autoid, nome, atalho, icone, rotina, senha, botoes, tooltips FROM menuicones WHERE ativo = 1 AND autoid = :id ORDER BY autoid;";
        ResultSet rs = conn.OpenTable(selectSQL, new Object[][] {{"int", "id", ssub.getId()}});
        boolean acheiItem = false;
        try {
            while (rs.next()) {
                acheiItem = true;
                
                String _nome = rs.getString("nome"); 
                final String _rotina = rs.getString("rotina");
                
                String icones = null;
                try { icones = rs.getString("icone").trim(); } catch (Exception sqlEx) { icones = null; }                
                if (icones == null) icones = null; else icones = "menuIcons/" + icones;

                // Aqui vão os itens
                item = new javax.swing.JMenuItem();
                item.setName(String.valueOf(ssub.getId()));
                //item.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));

                // Setar Icones
                if (icones != null) {
                    ImageIcon ico = new FlatSVGIcon(icones,16,16);
                    try{ item.setIcon(ico); } catch (Exception e) {}
                }

                item.setText(_nome);
                item.setToolTipText(rs.getString("tooltips"));

                // Botoes
                final String botoes = (ssub != null ? ssub.getBotoes() : rs.getString("botoes")); 
                VariaveisGlobais.conexao = conn;
                
                item.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        Class classe = null;  
                        try {  
                            classe = Class.forName(_rotina);  
                            JInternalFrame frame = (JInternalFrame) classe.newInstance();                              
                            
                            if (botoes != null) {
                                Class[] args1 = new Class[1]; Method mtd = null;
                                args1[0] = String.class;
                                String _method = "setBotoes";
                                mtd = classe.getMethod(_method, args1);
                                Object[] _args = new Object[] {botoes};
                                mtd.invoke(frame, _args);
                            }
                            
                            jDesktopPane1.add(frame);
                            CentralizaTela.setCentro(frame, jDesktopPane1, 0, 0);
                            
                            jDesktopPane1.getDesktopManager().activateFrame(frame);
                            frame.requestFocus();
                            frame.setSelected(true);
                            frame.setVisible(true);                              
                        } catch (Exception ex) { ex.printStackTrace(); }  
                    }
                });
                
                // Torna o item visivel
                item.setVisible(true);

            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs); 
        
        if (acheiItem) menuItem.add(item);        
    }
    
    private void SetarMenu(JMenuBar barraMenu) {
        String xmlStr = VariaveisGlobais.protocolomenu;

        List<String> subMenus = null;
        
        // Cria os Menus e Itens
        JMenu menuItem = null; JMenuItem item = null;
        
        if (xmlStr != null) {
            // Pega os sub Menus do Sistema
            subMenus = pegaListSubMenus(xmlStr);        

            // Cadastros
            String _Cadastros = subMenus.get(0);
            if (_Cadastros != null) {
                // Cria Menu
                 menuItem = new JMenu("Cadastros");
                // Adiciona o item ao menu
                barraMenu.add(menuItem);
                 
                List<classSubMenu> _sub = subMenuConverter(_Cadastros);
                for (classSubMenu ssub : _sub) {
                    Setar(menuItem, item, ssub);
                }
            }

            String _Caixa = subMenus.get(1);
            if (_Caixa != null) {
                // Cria Menu
                 menuItem = new JMenu("Caixa");
                // Adiciona o item ao menu
                barraMenu.add(menuItem);

                List<classSubMenu> _sub = subMenuConverter(_Caixa);
                for (classSubMenu ssub : _sub) {
                    Setar(menuItem, item, ssub);
                }
            }

            String _Movimento = subMenus.get(2);
            if (_Movimento != null) {
                // Cria Menu
                 menuItem = new JMenu("Movimento");
                // Adiciona o item ao menu
                barraMenu.add(menuItem);

                List<classSubMenu> _sub = subMenuConverter(_Movimento);
                for (classSubMenu ssub : _sub) {
                    Setar(menuItem, item, ssub);
                }
            }

            String _Relatorios = subMenus.get(3);
            if (_Relatorios != null) {
                // Cria Menu
                 menuItem = new JMenu("Relatórios");
                // Adiciona o item ao menu
                barraMenu.add(menuItem);

                List<classSubMenu> _sub = subMenuConverter(_Relatorios);
                for (classSubMenu ssub : _sub) {
                    Setar(menuItem, item, ssub);
                }
            }

            String _Gerencia = subMenus.get(4);
            if (_Gerencia != null) {
                // Cria Menu
                 menuItem = new JMenu("Gerência");
                // Adiciona o item ao menu
                barraMenu.add(menuItem);

                List<classSubMenu> _sub = subMenuConverter(_Gerencia);
                for (classSubMenu ssub : _sub) {
                    Setar(menuItem, item, ssub);
                }
            }
        }
    }

    private JToolBar ID() {
        String _licensa = Criptografia.decrypt(VariaveisGlobais.Licenca.trim(), Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
        
        JLabel _usuarioLabel = new JLabel("Ultilizador: ");
        JLabel _usuarioValue = new JLabel(VariaveisGlobais.usuario);
        _usuarioValue.setIcon( new FlatSVGIcon("toolBarIcons/usuario.svg",16,16));
        //_usuarioValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        _usuarioValue.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        _usuarioValue.setSize(80, 22);
        _usuarioValue.setPreferredSize(new Dimension(80,22));
        _usuarioValue.setMaximumSize(new Dimension(80,22));
        _usuarioValue.setMinimumSize(new Dimension(50,22));

        JLabel _dataLoginLabel = new JLabel("Data Login: ");
        JLabel _dataLoginValue = new JLabel(new Date().toString());
        _dataLoginValue.setIcon( new FlatSVGIcon("toolBarIcons/data.svg",16,16));
        //_dataLoginValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        _dataLoginValue.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        _dataLoginValue.setSize(190, 22);
        _dataLoginValue.setPreferredSize(new Dimension(190,22));
        _dataLoginValue.setMaximumSize(new Dimension(190,22));
        _dataLoginValue.setMinimumSize(new Dimension(100,22));
        
        JLabel _dataLocalLabel = new JLabel("Local: ");
        JLabel _dataLocalValue = new JLabel(VariaveisGlobais.sqlAlias);
        String icoLocal = "";
        if (VariaveisGlobais.sqlAlias.trim().toUpperCase().contains("LOCAL")) {
            icoLocal = "toolBarIcons/localizacao.svg";
        } else {
            icoLocal = "toolBarIcons/nuvem.svg";
        }
        _dataLocalValue.setIcon( new FlatSVGIcon(icoLocal,16,16));
        //_dataLocalValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        _dataLocalValue.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        _dataLocalValue.setSize(120, 22);
        _dataLocalValue.setPreferredSize(new Dimension(120,22));
        _dataLocalValue.setMaximumSize(new Dimension(120,22));
        _dataLocalValue.setMinimumSize(new Dimension(93,22));
        
        String _so = System.getProperty("os.name");
        String _soIcon = null;
        if (_so.trim().toUpperCase().contains("WINDOWS")) {
            _soIcon = "toolBarIcons/windows.svg";
        } else if (_so.trim().toUpperCase().contains("LINUX")) {
            _soIcon = "toolBarIcons/linux.svg";            
        } else {
            _soIcon = "toolBarIcons/mac.svg";            
        }
        JLabel _dataSoLabel = new JLabel("S.O.: ");
        JLabel _dataSoValue = new JLabel(_so + " - " + System.getProperty("os.version"));
        _dataSoValue.setIcon( new FlatSVGIcon(_soIcon,16,16));
        //_dataSoValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        _dataSoValue.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        _dataSoValue.setSize(140, 22);
        _dataSoValue.setPreferredSize(new Dimension(140,22));
        _dataSoValue.setMaximumSize(new Dimension(140,22));
        _dataSoValue.setMinimumSize(new Dimension(90,22));
        
        JLabel _dataLicensaLabel = new JLabel("Licensa para: ");
        JLabel _dataLicensaValue = new JLabel(_licensa);
        _dataLicensaValue.setIcon( new FlatSVGIcon("toolBarIcons/licensa.svg",16,16));
        //_dataLicensaValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        _dataLicensaValue.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        _dataLicensaValue.setSize(90, 22);
        _dataLicensaValue.setPreferredSize(new Dimension(90,22));
        _dataLicensaValue.setMaximumSize(new Dimension(90,22));
        _dataLicensaValue.setMinimumSize(new Dimension(90,22));
        
        JLabel _dataImpressorasLabel = new JLabel("Impressoras: ");
        JLabel _dataImpressorasValue = new JLabel(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
        try {
        String[] _dataImpressoraThermica = VariaveisGlobais.Thermica.split(":");
        String[] _dataImpressoraNormal = VariaveisGlobais.Printer.split(":");
        JLabel iconLabel = new JLabel(new FlatSVGIcon("toolBarIcons/impressora.svg",64,64));
        String _dataImpressoraToolTip = "<html>"; 
              _dataImpressoraToolTip += "<table>";
              _dataImpressoraToolTip += "<tr>";
              _dataImpressoraToolTip += "<td rowspan='3' style='text-align:center;'>";
              _dataImpressoraToolTip += "<img src='file:///" + System.getProperty("user.dir") + File.separator + "resources" + File.separator + "impressora.jpg' width='64' height='64'>";
              _dataImpressoraToolTip += "</td>";
              _dataImpressoraToolTip += "<td><font size=4 color=red><b>Impressoras selecionadas: </b></font></td>";
              _dataImpressoraToolTip += "</tr>";
              _dataImpressoraToolTip += "<tr>";
              _dataImpressoraToolTip += "<td><font color=blue><b>Thermica: </b></font>" + _dataImpressoraThermica[1].toString() + "</td>";
              _dataImpressoraToolTip += "</tr>";
              _dataImpressoraToolTip += "<tr>";
              _dataImpressoraToolTip += "<td><font color=blue><b>Normal: </b></font>" + _dataImpressoraNormal[1].toString() + "</td>";
              _dataImpressoraToolTip += "</tr>";
              _dataImpressoraToolTip += "</table></html>";
        _dataImpressorasValue.setToolTipText(_dataImpressoraToolTip);
        } catch (Exception ex) {}
        _dataImpressorasValue.setIcon( new FlatSVGIcon("toolBarIcons/impressora.svg",16,16));
        //_dataImpressorasValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        _dataImpressorasValue.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        _dataImpressorasValue.setSize(90, 22);
        _dataImpressorasValue.setPreferredSize(new Dimension(90,22));
        _dataImpressorasValue.setMaximumSize(new Dimension(90,22));
        _dataImpressorasValue.setMinimumSize(new Dimension(80,22));
        _dataImpressorasValue.setForeground(new Color(0, 153, 0));
        _dataImpressorasValue.setCursor(new Cursor(Cursor.HAND_CURSOR));
        _dataImpressorasValue.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                _dataImpressorasValue.setForeground(!VariaveisGlobais.statPrinter ? new java.awt.Color(0, 153, 0) : Color.RED);
                VariaveisGlobais.statPrinter = !VariaveisGlobais.statPrinter;
                _dataImpressorasValue.setText(VariaveisGlobais.statPrinter ? "Ligada" : "Desligada");
            }
        });

        JLabel _dataCapsLock = new JLabel("[Caps]");
        _dataCapsLock.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        //_dataCapsLock.setSize(45, 22); // Era W:35
        //_dataCapsLock.setPreferredSize(new Dimension(70,22));
        //_dataCapsLock.setMaximumSize(new Dimension(70,22));
        //_dataCapsLock.setMinimumSize(new Dimension(70,22));

        JLabel _dataNumLock = new JLabel("[Num]");
        _dataNumLock.setFont(new java.awt.Font("Ubuntu", BOLD, 10));
        //_dataNumLock.setSize(40, 22); // Era W:30
        //_dataNumLock.setPreferredSize(new Dimension(60,22));
        //_dataNumLock.setMaximumSize(new Dimension(60,22));
        //_dataNumLock.setMinimumSize(new Dimension(60,22));

        // Atualiza as informações do relógio e das teclas
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK)) {
                    _dataNumLock.setForeground(new Color(73, 165, 64));
                } else {
                    _dataNumLock.setForeground(null);
                }

                if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
                    _dataCapsLock.setForeground(new Color(73, 165, 64));
                } else {
                    _dataCapsLock.setForeground(null);
                }
            }
        });
        timer.start();
        
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel1.add(Box.createRigidArea(new Dimension(10,0)));
        //panel1.add(_usuarioLabel);
        panel1.add(_usuarioValue);
        panel1.add(Box.createRigidArea(new Dimension(5,0)));
        //panel1.add(_dataLoginLabel);
        panel1.add(_dataLoginValue);
        panel1.add(Box.createRigidArea(new Dimension(5,0)));
        //panel1.add(_dataLocalLabel);
        panel1.add(_dataLocalValue);
        panel1.add(Box.createRigidArea(new Dimension(5,0)));
        //panel1.add(_dataSoLabel);
        panel1.add(_dataSoValue);
        panel1.add(Box.createRigidArea(new Dimension(5,0)));
        //panel1.add(_dataLicensaLabel);
        panel1.add(_dataLicensaValue);
        
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel2.add(_dataCapsLock);
        panel2.add(Box.createRigidArea(new Dimension(3,0)));
        panel2.add(_dataNumLock);
        
        JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        //panel3.add(_dataImpressorasLabel);
        panel3.add(_dataImpressorasValue);
        
        JToolBar jToolBarStatus = new JToolBar();
        jToolBarStatus.addSeparator();
        
        JToggleButton upDown = new JToggleButton();
        upDown.setIcon(new FlatSVGIcon("icons/down.svg",6,6));
        upDown.setSelectedIcon(new FlatSVGIcon("icons/up.svg",6,6));
        upDown.setToolTipText("Mover a Barra de Status.");
        upDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (toolbarOnTop) {
                    remove(jToolBar);
                    jToolBar.setVisible(false);
                    add(jToolBar, BorderLayout.PAGE_START);
                    jToolBar.setVisible(true);
                    toolbarOnTop = false;
                } else {
                    remove(jToolBar);
                    jToolBar.setVisible(false);
                    add(jToolBar, BorderLayout.PAGE_END);
                    jToolBar.setVisible(true);
                    toolbarOnTop = true;
                }
                repaint();
            }
        });
        jToolBarStatus.add(upDown);
        
        JButton visibleButton = new JButton();
        visibleButton.setIcon(new FlatSVGIcon("icons/show.svg",8,6));
        visibleButton.setToolTipText("Tonar a Barra de Status invisível.");
        visibleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jToolBar.setVisible(!toolbarIsVisible);
                toolbarIsVisible = !toolbarIsVisible;
                repaint();
            }
        });
        jToolBarStatus.add(visibleButton);
        
        jToolBarStatus.addSeparator();
        jToolBarStatus.add(panel1, BorderLayout.WEST);
        jToolBarStatus.addSeparator();
        jToolBarStatus.add(panel2, BorderLayout.EAST);
        jToolBarStatus.addSeparator();
        jToolBarStatus.add(panel3, BorderLayout.EAST);
        
        jToolBarStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBarStatus.setFloatable(false);
        jToolBarStatus.setRollover(false);
        jToolBarStatus.setOrientation(HORIZONTAL);
        jToolBarStatus.setBorderPainted(true);

        return jToolBarStatus;
    }

    public void InicializaVariaveis() {
        //Db conn = VariaveisGlobais.conexao;
        try {
            VariaveisGlobais.dCliente.add("empresa", conn.ReadParameters("EMPRESA"));
            VariaveisGlobais.dCliente.add("endereco", conn.ReadParameters("ENDERECO"));
            VariaveisGlobais.dCliente.add("numero", conn.ReadParameters("NUMERO"));
            VariaveisGlobais.dCliente.add("complemento", conn.ReadParameters("COMPLEMENTO"));
            VariaveisGlobais.dCliente.add("bairro", conn.ReadParameters("BAIRRO"));
            VariaveisGlobais.dCliente.add("cidade", conn.ReadParameters("CIDADE"));
            VariaveisGlobais.dCliente.add("estado", conn.ReadParameters("ESTADO"));
            VariaveisGlobais.dCliente.add("cep", conn.ReadParameters("CEP"));
            VariaveisGlobais.dCliente.add("cnpj", conn.ReadParameters("CNPJ"));
            VariaveisGlobais.dCliente.add("tipodoc", conn.ReadParameters("TIPODOC"));
            VariaveisGlobais.dCliente.add("inscricao", conn.ReadParameters("INSCRICAO"));
            VariaveisGlobais.dCliente.add("tipoinsc", conn.ReadParameters("TIPOINSC"));
            VariaveisGlobais.dCliente.add("marca", conn.ReadParameters("MARCA"));
            VariaveisGlobais.dCliente.add("telefone", conn.ReadParameters("TELEFONE"));
            VariaveisGlobais.dCliente.add("hpage", conn.ReadParameters("HPAGE"));
            VariaveisGlobais.dCliente.add("email", conn.ReadParameters("EMAIL"));
            VariaveisGlobais.dCliente.add("recibo", conn.ReadParameters("RECIBO"));
        } catch (SQLException sqlEx) {}
        
        VariaveisGlobais.cContas.add("PR", "00");  // Proprietario
        VariaveisGlobais.cContas.add("LC", "01");  // Locatarios
        VariaveisGlobais.cContas.add("PC", "02");  // Passagem Caixa
        VariaveisGlobais.cContas.add("CX", "03");  // Fechamento Caixa
        VariaveisGlobais.cContas.add("NT", "04");  // Taxas
        VariaveisGlobais.cContas.add("AL", "05");  // Alugueres
        VariaveisGlobais.cContas.add("EN", "06");  // Encargos
        VariaveisGlobais.cContas.add("CM", "07");  // Comissão
        VariaveisGlobais.cContas.add("SO", "08");  // Sócio
        VariaveisGlobais.cContas.add("RT", "09");  // Retenção
        VariaveisGlobais.cContas.add("SD", "10");  // Saldo
        VariaveisGlobais.cContas.add("EP", "12");  // Expediente
        VariaveisGlobais.cContas.add("SG", "13");  // Seguro
        VariaveisGlobais.cContas.add("DC", "14");  // Desconto
        VariaveisGlobais.cContas.add("DF", "15");  // Diferença
        VariaveisGlobais.cContas.add("GG", "16");  // Adm Valores
        VariaveisGlobais.cContas.add("CA", "17");  // Contas da Adn
        VariaveisGlobais.cContas.add("AT", "18");  // Antecipações

        try {
            VariaveisGlobais.marca = conn.ReadParameters("MARCA").toLowerCase().trim();
        } catch (SQLException sqlEx) {}
        
        // logomarcas
        VariaveisGlobais.icoBoleta = System.getProperty("icoBoleta", VariaveisGlobais.marca + ".gif");
        VariaveisGlobais.icoExtrato = System.getProperty("icoExtrato", VariaveisGlobais.marca + ".gif");

        // Nomes das contas do sistema
        String sSql = "SELECT codigo, descr FROM adm;";
        ResultSet hresult = conn.OpenTable(sSql, null);
        try {
            while (hresult.next()) {
                String campo = hresult.getString("descr");
                String chave =  hresult.getString("codigo");
                VariaveisGlobais.dCliente.add(chave,campo);
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(hresult);
        
        try {regras = conn.ReadParameters("REGRAS");} catch (Exception ex) {}

        try {VariaveisGlobais.bcobol = conn.ReadParameters("BCOBOL");} catch (Exception ex) {VariaveisGlobais.bcobol = "itau";}
        
        // site
        try {VariaveisGlobais.siteIP = conn.ReadParameters("siteIP");} catch (Exception e) {VariaveisGlobais.siteIP = "";}
        try {VariaveisGlobais.siteUser = conn.ReadParameters("siteUser");} catch (Exception e) {VariaveisGlobais.siteUser = "";}
        try {VariaveisGlobais.sitePwd = conn.ReadParameters("sitePwd");} catch (Exception e) {VariaveisGlobais.sitePwd = "";}
        try {VariaveisGlobais.siteDbName = conn.ReadParameters("siteDbName");} catch (Exception e) {VariaveisGlobais.siteDbName = "";}
    }
    
    public static void main(boolean bSplash) {
        SiciMenu.bSplash = bSplash;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            }
        });
    }    
}
