package Movimento.PixCentral;

import Bancos.bancos;
import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.Outlook;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.gmail.GmailAPI;
import static Funcoes.gmail.GmailOperations.createEmailWithAttachment;
import static Funcoes.gmail.GmailOperations.createMessageWithEmail;
import Funcoes.jDirectory;
import Pix.PayLoad;
import Pix.ReciboPix;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import Sici.Partida.Collections;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.mail.internet.MimeMessage;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;
import javax.swing.Action;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.Document;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jdesktop.swingx.JXTreeTable;

public class CentralPix extends javax.swing.JInternalFrame {
    private JEditorPane _htmlPane = new JEditorPane();

    private final UndoManager undoManager = new UndoManager();
    private final UndoAction undoAction = new UndoAction();
    private final RedoAction redoAction = new RedoAction();

    private JXTreeTable treeTable;
    private List<BancosPix2> bancosPix;

    private JXTreeTable treeTablePrinted;
    
    Db conn = VariaveisGlobais.conexao;
    String[] month;
    int[] dmonth;
    
    private String _botoes = null;

    // Botoes que acompanham a tela
    private boolean _Geracao = true;
    private boolean _Email = true;
    
    public void setBotoes(String _botoes) {
        this._botoes = _botoes;
    }

    public CentralPix() {
        initComponents();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (_botoes != null) {
                    String[] btn = _botoes.split(" ");
                    int Pos = FuncoesGlobais.IndexOf(btn, "geracao");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Geracao = new Boolean(_btn[1].replace("\"", ""));
                    }

                    Pos = FuncoesGlobais.IndexOf(btn, "email");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Email = new Boolean(_btn[1].replace("\"", ""));
                    }
                }
                
                if (!_Geracao) jTabbedPaneBoletas.removeTabAt(jTabbedPaneBoletas.indexOfTab("Geração"));
                if (!_Email) jTabbedPaneBoletas.removeTabAt(jTabbedPaneBoletas.indexOfTab("E-Mail"));
            }
        });        

        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/pix.svg",16,16);
        setFrameIcon(icone);        
        
        jSemEnvio.setVisible(VariaveisGlobais.funcao.equalsIgnoreCase("sys"));
        
        lbl_Status.setText("");
        VencBoleto.setMinSelectableDate(Calendar.getInstance().getTime());  
        VencBoleto.setDate(new Date());
        JTextFieldDateEditor editor = (JTextFieldDateEditor) VencBoleto.getDateEditor();
        editor.setEditable(false);
        
        
        this.month = new String[]{"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        this.dmonth = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        MesRef.setValue(this.month[(new Date()).getMonth()]);
        AnoRef.setValue(1900 + new Date().getYear());
        
        LerBancosPix();
        
        EditorEmail();
    }

    private void LerBancosPix() {
        pixBanco.removeAllItems();
        
        String selectSql = "SELECT banco FROM bancos_pix ORDER BY id;";
        ResultSet imResult = this.conn.OpenTable(selectSql, null);

        try {
            while (imResult.next()) {
                pixBanco.addItem(imResult.getString("banco"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        conn.CloseTable(imResult);        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton2 = new javax.swing.JToggleButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPaneBoletas = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jemdia = new javax.swing.JRadioButton();
        jatrasados = new javax.swing.JRadioButton();
        jtodos = new javax.swing.JRadioButton();
        jperiodo = new javax.swing.JRadioButton();
        jPanel15 = new javax.swing.JPanel();
        VencBoleto = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel8 = new javax.swing.JLabel();
        jPeriodo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jInicial = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel2 = new javax.swing.JLabel();
        jFinal = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jContrato = new javax.swing.JFormattedTextField();
        jListar = new javax.swing.JButton();
        jGerar = new javax.swing.JButton();
        jProgress = new javax.swing.JProgressBar();
        jPanel6 = new javax.swing.JPanel();
        MesRef = new javax.swing.JSpinner();
        AnoRef = new javax.swing.JSpinner();
        lbl_Status = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        pixBanco = new javax.swing.JComboBox<>();
        ListaBancosPessoas = new javax.swing.JScrollPane();
        ListaBancosPessoasImpressas = new javax.swing.JScrollPane();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblEmails = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jProgressEmail = new javax.swing.JProgressBar();
        btnEnviarTodos = new javax.swing.JButton();
        btnEnviarSelecao = new javax.swing.JButton();
        btnEditarCadastro = new javax.swing.JButton();
        btnListarBoletasEmail = new javax.swing.JButton();
        jSemEnvio = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        edtSubJect = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        FontName = new javax.swing.JComboBox();
        FontSize = new javax.swing.JComboBox();
        FontBold = new javax.swing.JToggleButton();
        FontItalic = new javax.swing.JToggleButton();
        FontUnderLine = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JSeparator();
        TextAlignLeft = new javax.swing.JToggleButton();
        TextAlignCenter = new javax.swing.JToggleButton();
        TextAlignRigth = new javax.swing.JToggleButton();
        TextAlignJustified = new javax.swing.JToggleButton();
        FontColor = new javax.swing.JButton();
        FontBackColor = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        pScroll = new javax.swing.JScrollPane();

        jToggleButton2.setText("jToggleButton2");

        setClosable(true);
        setTitle(".:: Central de Geração e Impressão de Recebimentos Pix");

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Condição"));
        jPanel3.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        buttonGroup1.add(jemdia);
        jemdia.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jemdia.setSelected(true);
        jemdia.setText("Em Dia");
        jemdia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jemdiaActionPerformed(evt);
            }
        });

        buttonGroup1.add(jatrasados);
        jatrasados.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jatrasados.setText("Atrasados");
        jatrasados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jatrasadosActionPerformed(evt);
            }
        });

        buttonGroup1.add(jtodos);
        jtodos.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jtodos.setText("Todos");
        jtodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtodosActionPerformed(evt);
            }
        });

        buttonGroup1.add(jperiodo);
        jperiodo.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jperiodo.setText("Por período");
        jperiodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jperiodoActionPerformed(evt);
            }
        });

        VencBoleto.setDate(new java.util.Date(-2208977612000L));
        VencBoleto.setEnabled(false);
        VencBoleto.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        jLabel8.setText("Vencimento do Pix");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(16, 16, 16))
                    .addComponent(VencBoleto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(0, 0, 0)
                .addComponent(VencBoleto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jatrasados)
                    .addComponent(jtodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jperiodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jemdia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jemdia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jatrasados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtodos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jperiodo)
                .addContainerGap())
        );

        jPeriodo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Período"));
        jPeriodo.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jLabel1.setText("Inicio:");

        jInicial.setDate(new java.util.Date(-2208977612000L));
        jInicial.setEnabled(false);
        jInicial.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jLabel2.setText("Final:");

        jFinal.setDate(new java.util.Date(-2208977612000L));
        jFinal.setEnabled(false);
        jFinal.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N

        javax.swing.GroupLayout jPeriodoLayout = new javax.swing.GroupLayout(jPeriodo);
        jPeriodo.setLayout(jPeriodoLayout);
        jPeriodoLayout.setHorizontalGroup(
            jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPeriodoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jInicial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPeriodoLayout.setVerticalGroup(
            jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPeriodoLayout.createSequentialGroup()
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jInicial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Unitário"));
        jPanel4.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jLabel3.setText("Contrato:");

        jContrato.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        jContrato.setMinimumSize(new java.awt.Dimension(6, 20));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jListar.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jListar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/next.png"))); // NOI18N
        jListar.setText("Listar Locatários");
        jListar.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jListar.setIconTextGap(5);
        jListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jListarActionPerformed(evt);
            }
        });

        jGerar.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jGerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/pix-logo_24x24.png"))); // NOI18N
        jGerar.setText("Gerar Recbtos PIX");
        jGerar.setToolTipText("");
        jGerar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jGerar.setIconTextGap(8);
        jGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGerarActionPerformed(evt);
            }
        });

        jProgress.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgress.setStringPainted(true);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Mês de Referência"));
        jPanel6.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        MesRef.setModel(new javax.swing.SpinnerListModel(new String[] {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"}));

        AnoRef.setModel(new javax.swing.SpinnerNumberModel(2018, 2010, 2030, 1));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MesRef, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AnoRef)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MesRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AnoRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        lbl_Status.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        lbl_Status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel5.setBackground(new java.awt.Color(204, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Banco PIX"));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pixBanco, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pixBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jGerar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl_Status, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jListar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jListar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Status, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ListaBancosPessoas.setBackground(new java.awt.Color(255, 255, 255));

        ListaBancosPessoasImpressas.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setBackground(new java.awt.Color(153, 204, 255));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Asp.net_0031_color1_16x16.gif"))); // NOI18N
        jLabel6.setText("Pix já impressos, para reimprimi-los ultilize a função de 2ª via no menu de Relatórios");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel6.setIconTextGap(8);
        jLabel6.setOpaque(true);

        jLabel7.setBackground(new java.awt.Color(153, 204, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/plus.png"))); // NOI18N
        jLabel7.setText("Selecione na Lista os locatários para impressão do Pix.");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel7.setIconTextGap(8);
        jLabel7.setOpaque(true);

        jLabel4.setBackground(new java.awt.Color(255, 255, 0));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 51));
        jLabel4.setText("* Ao emitir PIX para uma pessoa que tenha sido emitido boleta, o PIX substitui o nosso numeo da boleta.");
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel4.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ListaBancosPessoas, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(ListaBancosPessoasImpressas))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ListaBancosPessoasImpressas, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ListaBancosPessoas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)))
                .addContainerGap())
        );

        jTabbedPaneBoletas.addTab("Geração", jPanel1);

        tblEmails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Contrato", "Nome", "Vencimento", "Pix", "nnumero", "FileName"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(tblEmails);

        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jProgressEmail.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgressEmail.setStringPainted(true);

        btnEnviarTodos.setText("Enviar Todas");
        btnEnviarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarTodosActionPerformed(evt);
            }
        });

        btnEnviarSelecao.setText("Enviar Selecionada(s)");
        btnEnviarSelecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarSelecaoActionPerformed(evt);
            }
        });

        btnEditarCadastro.setText("Editar Cadastro");
        btnEditarCadastro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCadastroActionPerformed(evt);
            }
        });

        btnListarBoletasEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/previous.png"))); // NOI18N
        btnListarBoletasEmail.setText("Listar Boletas para E-Mail");
        btnListarBoletasEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListarBoletasEmailActionPerformed(evt);
            }
        });

        jSemEnvio.setText("Sem envio - Só Gerente");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addComponent(btnEnviarTodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEnviarSelecao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditarCadastro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnListarBoletasEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSemEnvio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnListarBoletasEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSemEnvio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEnviarTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnviarSelecao, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEditarCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setText("Assunto:");

        FontName.setToolTipText("Tipo da Fonte");

        FontSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "6", "7", "8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "32", "64", "72" }));
        FontSize.setToolTipText("Tamanho da Fonte");

        FontBold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2532_text_bold.png"))); // NOI18N
        FontBold.setToolTipText("Negrito");

        FontItalic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2533_text_italic.png"))); // NOI18N
        FontItalic.setToolTipText("Itálico");

        FontUnderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2536_text_underline.png"))); // NOI18N
        FontUnderLine.setToolTipText("Sublinhado");

        TextAlignLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_left.png"))); // NOI18N
        TextAlignLeft.setSelected(true);
        TextAlignLeft.setToolTipText("Alinhamento a Esquerda");

        TextAlignCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_center.png"))); // NOI18N
        TextAlignCenter.setToolTipText("Alinhamento ao Centro");

        TextAlignRigth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_right.png"))); // NOI18N
        TextAlignRigth.setToolTipText("Alinhamento a Direita");

        TextAlignJustified.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_justify.png"))); // NOI18N
        TextAlignJustified.setToolTipText("Alinhamento Justificado");

        FontColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_foregroundcolor.png"))); // NOI18N
        FontColor.setToolTipText("Cor da Fonte");

        FontBackColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_backgroundcolor.png"))); // NOI18N
        FontBackColor.setToolTipText("Cor do Fundo");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(FontName, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FontBold, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontItalic, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontUnderLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(TextAlignLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignRigth, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TextAlignJustified, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(FontColor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(FontBackColor, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(257, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FontBold)
                        .addComponent(FontItalic)
                        .addComponent(FontUnderLine))
                    .addComponent(jSeparator3)
                    .addComponent(TextAlignCenter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextAlignRigth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextAlignLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextAlignJustified, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FontBackColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FontColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FontSize, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(FontName, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {FontBackColor, FontBold, FontColor, FontItalic, FontName, FontSize, FontUnderLine, TextAlignCenter, TextAlignJustified, TextAlignLeft, TextAlignRigth});

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edtSubJect))
                    .addComponent(pScroll)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(edtSubJect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneBoletas.addTab("E-Mail", jPanel9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPaneBoletas, javax.swing.GroupLayout.PREFERRED_SIZE, 869, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPaneBoletas)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGerarActionPerformed
        if (!AnalisaGeracao()) return;

        jGerar.setEnabled(false);

        String rgprp;
        String rgimv;
        String contrato = null;
        String vencto = null;
        String vectoBol = null;
        String nnumero = null;
        
        // Limpa lista de email
        for (int b = 0; b <= bancosPix.size() - 1; b++) {
            for (PessoasPix p : bancosPix.get(b).getPessoasPix()) {
                if (p.getTag()) {
                    rgprp = p.getRgprp();
                    rgimv = p.getRgimv();
                    contrato = p.getContrato();
                    vencto = p.getVencimentoRec();
                    vectoBol = p.getVencimentoBol();

                    if (vectoBol != null) {
                        if (vectoBol.trim().equalsIgnoreCase("")) vectoBol = null;
                    }

                    ReciboPix Bean1 = null;
                    try {
                        Bean1 = CreateReciboPix(rgprp, rgimv, contrato, vencto, vectoBol);

                        // Gravar no arquivo Boletas
                        String cSql = "INSERT INTO bloquetos (`rgprp`,`rgimv`,`contrato`," +
                        "`nome`,`vencimento`,`valor`,`nnumero`,`remessa`) " +
                        "VALUES (\"&1.\",\"&2.\",\"&3.\",\"&4.\",\"&5.\",\"&6.\",\"&7.\",\"&8.\")";
                        cSql = FuncoesGlobais.Subst(cSql, new String[] {
                            rgprp,
                            rgimv,
                            contrato,
                            Bean1.getsacDadosNome(),
                            Dates.StringtoString(vencto,"dd/MM/yyyy","yyyy-MM-dd"),
                            Bean1.getpixDadosVrdoc(),
                            Bean1.getpixNNumero(),
                            "S"
                        });
                        try {
                            if (conn.ExisteTabelaBloquetos()) conn.CommandExecute(cSql);
                        } catch (Exception e) {e.printStackTrace();}

                        List<ReciboPix> lista = new ArrayList<ReciboPix>();
                        lista.add(Bean1);

                        JRDataSource jrds = new JRBeanCollectionDataSource(lista);
                        try {
                            Map parametros = new HashMap();
                            Image logoPix = ImageIO.read(getClass().getResource("/Figuras/pix-logo_18x18.jpg"));
                            parametros.put("logoPix", logoPix);
                            
                            String fileName = "reports/ReciboPix.jasper";
                            JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

                            // Create a PDF exporter
                            JRExporter exporter = new JRPdfExporter();

                            new jDirectory("reports/Pix/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                            String pathName = "reports/Pix/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                            // Configure the exporter (set output file name and print object)
                            String outFileName = pathName + contrato + "_" + Bean1.getsacDadosNome() + "_" + vencto + "_" + pixBanco.getSelectedItem().toString().replace(" ", "") + "_" + Bean1.getpixNNumero()+ ".pdf";
                            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                            try {
                                String recUpdSql = "UPDATE recibo SET boletapath = '" + outFileName + "', remessa = 'S' WHERE contrato = '" + contrato + "' AND " +
                                "dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vencto, "dd/MM/yyyy")) + "';";
                                conn.CommandExecute(recUpdSql);
                            } catch (Exception err) {err.printStackTrace();}

                            // Export the PDF file
                            exporter.exportReport();
                        } catch (JRException e) {
                            e.printStackTrace();
                            System.exit(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    } catch (Exception e) {}
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Gerado com sucesso.");
        jListar.doClick();

        jGerar.setEnabled(true);
    }//GEN-LAST:event_jGerarActionPerformed

    private void jListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jListarActionPerformed
//        if (!jemdia.isSelected()) {
//            Object[] options = { "Sim", "Não" };
//            int n = JOptionPane.showOptionDialog(null,
//                "Você já alterou o vencimento para boletas em atraso ?",
//                "Atenção", JOptionPane.YES_NO_OPTION,
//                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
//            if (n == JOptionPane.NO_OPTION) {
//                VencBoleto.requestFocus();
//                return;
//            }
//        }
        
        jListar.setEnabled(false);

        if (jemdia.isSelected()) {
            EmDia();
        } else if (jatrasados.isSelected()) {
            Atrasados();
        } else if (jtodos.isSelected()) {
            Todos();
        } else {
            PorPeriodo();
        }

        jListar.setEnabled(true);
    }//GEN-LAST:event_jListarActionPerformed

    private void jperiodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jperiodoActionPerformed
        jInicial.setEnabled(true);
        jFinal.setEnabled(true);
        VencBoleto.setEnabled(true);
        VencBoleto.requestFocus();
    }//GEN-LAST:event_jperiodoActionPerformed

    private void jtodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtodosActionPerformed
        jInicial.setEnabled(false);
        jFinal.setEnabled(false);
        VencBoleto.setEnabled(true);
        VencBoleto.requestFocus();
    }//GEN-LAST:event_jtodosActionPerformed

    private void jatrasadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jatrasadosActionPerformed
        jInicial.setEnabled(false);
        jFinal.setEnabled(false);
        VencBoleto.setEnabled(true);
        VencBoleto.requestFocus();
    }//GEN-LAST:event_jatrasadosActionPerformed

    private void jemdiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jemdiaActionPerformed
        jInicial.setEnabled(false);
        jFinal.setEnabled(false);
        VencBoleto.setEnabled(false);
    }//GEN-LAST:event_jemdiaActionPerformed

    public static class FileTransferable implements Transferable {

        private List listOfFiles;

        public FileTransferable(List listOfFiles) {
            this.listOfFiles = listOfFiles;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return listOfFiles;
        }
    }
    
    private void btnListarBoletasEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarBoletasEmailActionPerformed
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados, r.dtvencbol, r.boletapath FROM recibo r, locatarios l, carteira c " + 
                     "where (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) AND (r.remessa = 'S' AND r.emailbol = 'N') ORDER BY l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);

        TableControl.header(tblEmails, new String[][] {{"contrato","nome","vencimento","Pix","nnumero","fileName","Status"},{"60","200","70","70","120","0","30"}});
        TableControl.Clear(tblEmails);
        
        int b = 0;
        
        // Atribuições
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tvenctoBol = "";
        String tnnumero = "";
        String tfilename = "";
        int rcount = 0;
        try {
            rcount = conn.RecordCount(rs);
            while (rs.next()) {
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tvenctoBol = null;
                try {tvenctoBol = rs.getString("dtvencbol");} catch (SQLException e) {}
                if (tvenctoBol != null) tvenctoBol = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(tvenctoBol.toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                tfilename = "";
                try {tfilename = rs.getString("boletapath");} catch (SQLException ex) {}
                TableControl.add(tblEmails, new String[][]{{tcontrato, tnome, tvencto, tvenctoBol, tnnumero, tfilename, ""},{"C","L","C","C","C","L","C"}}, true);
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgressEmail.setValue(pgs);
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rs);

        int pgs = 100;
        try {
            pgs = ((b++ * 100) / rcount) + 1;
        } catch (Exception e) {}
        jProgressEmail.setValue(pgs);

        lbl_Status.setText("");
        jProgressEmail.setValue(0);        
    }//GEN-LAST:event_btnListarBoletasEmailActionPerformed

    private void btnEnviarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarTodosActionPerformed
        btnEnviarTodos.setEnabled(false);

        // Seleciona todos
        tblEmails.selectAll();
        int selRows = tblEmails.getSelectedRowCount();
        if (selRows <= 0) {
            btnEnviarTodos.setEnabled(true);
            return;
        }
        
        if (edtSubJect.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo Assunto não pode ser vazio!");
            btnEnviarTodos.setEnabled(true);
            edtSubJect.requestFocus();
            return;
        }
        
        String htmlText = "";
        try {htmlText = _htmlPane.getDocument().getText(0, _htmlPane.getDocument().getEndPosition().getOffset());} catch (BadLocationException ba) {}
        if (htmlText.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "O campo Mensagem não pode ser vazio!");
            btnEnviarTodos.setEnabled(true);
            _htmlPane.requestFocus();
            return;
        }

        int[] selRow = tblEmails.getSelectedRows();

        String contrato = null;
        String vencto = "";
        String filename = "";
        String status = "";
        
        for (int i=0;i<=selRow.length - 1;i++) {
            int nRow = selRow[i];
            int modelRow = tblEmails.convertRowIndexToModel(nRow);

            contrato = tblEmails.getModel().getValueAt(modelRow, 0).toString();
            Object[][] EmailLocaDados = null;
            try {
                EmailLocaDados = conn.ReadFieldsTable(new String[] {"nomerazao","email"}, "locatarios", "contrato = '" + contrato + "'");
            } catch (SQLException e) {}
            
            String EmailLoca = null;
            if (EmailLocaDados != null) EmailLoca = EmailLocaDados[1][3].toString().toLowerCase();
            
            vencto = tblEmails.getModel().getValueAt(modelRow, 2).toString();
            try {filename = tblEmails.getModel().getValueAt(modelRow, 5).toString();} catch (Exception ex) {}
            String[] attachMent;
            if (filename.isEmpty()) {
                attachMent = new String[]{};
            } else {
                attachMent = new String[]{System.getProperty("user.dir") + "/" + filename};
            }
            status = tblEmails.getModel().getValueAt(modelRow, 6).toString();
            
            if (!status.equalsIgnoreCase("OK")) {
                if (VariaveisGlobais.OUTLOOK) {
                    Outlook email = new Outlook(true);
                    try {            
                        String To = EmailLoca.trim().toLowerCase();
                        String Subject = edtSubJect.getText().trim();
                        String Body = _htmlPane.getText();
                        String[] Attachments = attachMent;
                        email.Send(To, null, Subject, Body, Attachments);
                        if (!email.isSend()) {
                            //JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                        } else {
                            conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                                Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                            //JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        }
                        tblEmails.getModel().setValueAt(email.isSend() ? "Ok" : "Err", modelRow, 6);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        email = null;
                    }
                } else {
                    try {            
                        String To = EmailLoca.trim().toLowerCase();
                        String Subject = edtSubJect.getText().trim();
                        String Body = _htmlPane.getText();
                        String[] Attachments = attachMent;
                        
                        Gmail service = GmailAPI.getGmailService();
                        MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(System.getProperty("user.dir") + "/" + filename));
                        Message message = createMessageWithEmail(Mimemessage);
                        message = service.users().messages().send("me", message).execute();

                        System.out.println("Message id: " + message.getId());
                        System.out.println(message.toPrettyString());
                        if (message.getId() == null) {
                            //JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                        } else {
                            conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                                Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                            //JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        }
                        tblEmails.getModel().setValueAt(message.getId() != null ? "Ok" : "Err", modelRow, 6);
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        }
        
        tblEmails.clearSelection();
        btnEnviarTodos.setEnabled(true);
    }//GEN-LAST:event_btnEnviarTodosActionPerformed

    private void btnEnviarSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarSelecaoActionPerformed
        btnEnviarSelecao.setEnabled(false);
        int selRows = tblEmails.getSelectedRowCount();
        if (selRows <= 0) {
            btnEnviarSelecao.setEnabled(true);
            return;
        }
        
        if (edtSubJect.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo Assunto não pode ser vazio!");
            btnEnviarSelecao.setEnabled(true);
            edtSubJect.requestFocus();
            return;
        }
        
        String htmlText = "";
        try {htmlText = _htmlPane.getDocument().getText(0, _htmlPane.getDocument().getEndPosition().getOffset());} catch (BadLocationException ba) {}
        if (htmlText.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "O campo Mensagem não pode ser vazio!");
            btnEnviarSelecao.setEnabled(true);
            _htmlPane.requestFocus();
            return;
        }

        int[] selRow = tblEmails.getSelectedRows();

        String contrato = null;
        String vencto = "";
        String filename = "";
        String status = "";
        
        for (int i=0;i<=selRow.length - 1;i++) {
            int nRow = selRow[i];
            int modelRow = tblEmails.convertRowIndexToModel(nRow);

            contrato = tblEmails.getModel().getValueAt(modelRow, 0).toString();
            Object[][] EmailLocaDados = null;
            try {
                EmailLocaDados = conn.ReadFieldsTable(new String[] {"nomerazao","email"}, "locatarios", "contrato = '" + contrato + "'");
            } catch (SQLException e) {}
            
            String EmailLoca = null;
            if (EmailLocaDados != null) EmailLoca = EmailLocaDados[1][3].toString().toLowerCase();
            
            vencto = tblEmails.getModel().getValueAt(modelRow, 2).toString();
            try {filename = tblEmails.getModel().getValueAt(modelRow, 5).toString();} catch (Exception ex) {}
            String[] attachMent;
            if (filename.isEmpty()) {
                attachMent = new String[]{};
            } else {
                attachMent = new String[]{System.getProperty("user.dir") + "/" + filename};
            }
            status = tblEmails.getModel().getValueAt(modelRow, 6).toString();
            
            if (!status.equalsIgnoreCase("OK")) {
                if (VariaveisGlobais.OUTLOOK) {
                    Outlook email = new Outlook(true);
                    try {            
                        String To = EmailLoca.trim().toLowerCase();
                        String Subject = edtSubJect.getText().trim();
                        String Body = _htmlPane.getText();
                        String[] Attachments = attachMent;
                        email.Send(To, null, Subject, Body, Attachments);
                        if (!email.isSend()) {
                            //JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                        } else {
                            conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                                Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                            //JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        }
                        tblEmails.getModel().setValueAt(email.isSend() ? "Ok" : "Err", modelRow, 6);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        email = null;
                    }
                } else {
                    try {            
                        String To = EmailLoca.trim().toLowerCase();
                        String Subject = edtSubJect.getText().trim();
                        String Body = _htmlPane.getText();
                        String[] Attachments = attachMent;
                        
                        Gmail service = GmailAPI.getGmailService();
                        MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(System.getProperty("user.dir") + "/" + filename));
                        Message message = createMessageWithEmail(Mimemessage);
                        message = service.users().messages().send("me", message).execute();

                        System.out.println("Message id: " + message.getId());
                        System.out.println(message.toPrettyString());
                        if (message.getId() == null) {
                            //JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                        } else {
                            conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                                Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                            //JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        }
                        tblEmails.getModel().setValueAt(message.getId() != null ? "Ok" : "Err", modelRow, 6);
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        }
        
        tblEmails.clearSelection();
        btnEnviarSelecao.setEnabled(true);
    }//GEN-LAST:event_btnEnviarSelecaoActionPerformed

    private void btnEditarCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCadastroActionPerformed
        int selRows = tblEmails.getSelectedRowCount();
        if (selRows <= 0) {
            return;
        }
        
        int[] selRow = tblEmails.getSelectedRows();

        String contrato = null;
        for (int i=0;i<=0;i++) {
            int nRow = selRow[i];
            int modelRow = tblEmails.convertRowIndexToModel(nRow);

            contrato = tblEmails.getModel().getValueAt(modelRow, 0).toString();
            String _class = ""; String _method = ""; String[] _args = {};
            _class = "j4rent.Locatarios.jLocatarios";
            _method = "MoveToLoca";
            _args = new String[] {"contrato", contrato};
            
            try {
                Class classe = null;
                classe = Class.forName(_class);
                JInternalFrame frame = (JInternalFrame) classe.newInstance();

                Class[] args1 = new Class[2];
                args1[0] = String.class;
                args1[1] = String.class;
                Method mtd = classe.getMethod(_method, args1);
                mtd.invoke(frame, _args);

                VariaveisGlobais.jPanePrin.add(frame);
                CentralizaTela.setCentro(frame, VariaveisGlobais.jPanePrin, 0, 0);

                VariaveisGlobais.jPanePrin.getDesktopManager().activateFrame(frame);
                frame.requestFocus();
                frame.setSelected(true);
                frame.setVisible(true);
            } catch (Exception e) {e.printStackTrace();}
            
        }        
    }//GEN-LAST:event_btnEditarCadastroActionPerformed

    public void EmDia() {
        lbl_Status.setText("Criando Lista...");
        jProgress.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        int iAnoRef = Integer.valueOf(AnoRef.getValue().toString());
        int iDiaRef = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Date iDataRef = new Date(iAnoRef - 1900, FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString()), iDiaRef);
        if (Dates.DiffDate(iDataRef, new Date()) > 0) {
            iDiaRef = 1;
            iDataRef = new Date(iAnoRef - 1900, FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString()), iDiaRef);
        }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados FROM recibo r, locatarios l, carteira c " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento >= '" + 
                     Dates.DateFormata("yyyy-MM-dd", iDataRef) + 
                     "' and r.dtvencimento <= '" + Dates.DateFormata("yyyy-MM-dd", new Date(iAnoRef - 1900, FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString()), this.dmonth[FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString())])) + "') AND (Year(r.dtvencimento) = " + 
                     AnoRef.getValue() + ") " + sContrato + " ORDER BY l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosPix = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasBoleta = new ArrayList<PessoasPix>();

        // Boletos Printed
        List<BancosPix2> bancosPixPrinted = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasPixPrinted = new ArrayList<PessoasPix>();
        
        int b = 0;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = conn.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                String tenvio = "EMAIL";
                
                /**
                 * Pega dados do Banco
                 */                
                Object[][] Pix = PayLoad.LerBancoPIX(pixBanco.getSelectedItem().toString());
                tbcobol = Pix[1][3].toString();
                tbcobolnome = Pix[2][3].toString();
                                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosPix.add(new BancosPix2(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasPixPrinted.isEmpty()) bancosPixPrinted.add(new BancosPix2(oldBco, oldBcoNome, pessoasPixPrinted));
                    pessoasBoleta = new ArrayList<PessoasPix>();
                    pessoasPixPrinted = new ArrayList<PessoasPix>();
                }
                
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasPix(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasPixPrinted.add(new PessoasPix(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress.setValue(pgs);
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rs);

        int pgs = 100;
        try {
            pgs = ((b++ * 100) / rcount) + 1;
        } catch (Exception e) {}
        jProgress.setValue(pgs);

        if (!pessoasBoleta.isEmpty()) {
            bancosPix.add(new BancosPix2(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasPixPrinted.isEmpty()) {
            bancosPixPrinted.add(new BancosPix2(tbcobol, tbcobolnome, pessoasPixPrinted));
        }               
        
        // Boletas não impressas
        {
            PixTreeTableModel boletaTreeTableModel = new PixTreeTableModel(bancosPix);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            treeTable.setRootVisible(false);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            PixTreeTableModel boletaTreeTableModelPrinted = new PixTreeTableModel(bancosPixPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            treeTablePrinted.setRootVisible(false);
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        lbl_Status.setText("");
        jProgress.setValue(0);
    }
    
    public void Atrasados() {
        lbl_Status.setText("Criando Lista...");
        jProgress.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados FROM recibo r, locatarios l, carteira c " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento < '" + 
                     Dates.DateFormata("yyyy-MM-dd", new Date()) + "') AND (Year(r.dtvencimento) = " + 
                     AnoRef.getValue() + ") " + sContrato + " ORDER BY l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosPix = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasBoleta = new ArrayList<PessoasPix>();

        // Boletos Printed
        List<BancosPix2> bancosPixPrinted = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasPixPrinted = new ArrayList<PessoasPix>();
        
        int b = 0;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = conn.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                String tenvio = "EMAIL";

                /**
                 * Pega dados do Banco
                 */                
                Object[][] Pix = PayLoad.LerBancoPIX(pixBanco.getSelectedItem().toString());
                tbcobol = Pix[1][3].toString();
                tbcobolnome = Pix[2][3].toString();
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosPix.add(new BancosPix2(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasPixPrinted.isEmpty()) bancosPixPrinted.add(new BancosPix2(oldBco, oldBcoNome, pessoasPixPrinted));
                    pessoasBoleta = new ArrayList<PessoasPix>();
                    pessoasPixPrinted = new ArrayList<PessoasPix>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasPix(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasPixPrinted.add(new PessoasPix(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress.setValue(pgs);
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rs);

        int pgs = 100;
        try {
            pgs = ((b++ * 100) / rcount) + 1;
        } catch (Exception e) {}
        jProgress.setValue(pgs);

        if (!pessoasBoleta.isEmpty()) {
            bancosPix.add(new BancosPix2(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasPixPrinted.isEmpty()) {
            bancosPixPrinted.add(new BancosPix2(tbcobol, tbcobolnome, pessoasPixPrinted));
        }               
        
        // Boletas não impressas
        {
            PixTreeTableModel pixTreeTableModel = new PixTreeTableModel(bancosPix);
            treeTable = new JXTreeTable(pixTreeTableModel);
            treeTable.setRootVisible(false);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            PixTreeTableModel boletaTreeTableModelPrinted = new PixTreeTableModel(bancosPixPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            treeTablePrinted.setRootVisible(false);
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        lbl_Status.setText("");
        jProgress.setValue(0);
    }

    public void Todos() {
        lbl_Status.setText("Criando Lista...");
        jProgress.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados FROM recibo r, locatarios l, carteira c " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) " + sContrato + " ORDER BY l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosPix = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasBoleta = new ArrayList<PessoasPix>();

        // Boletos Printed
        List<BancosPix2> bancosPixPrinted = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasPixPrinted = new ArrayList<PessoasPix>();
        
        int b = 0;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = conn.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                String tenvio = "EMAIL";

                /**
                 * Pega dados do Banco
                 */                
                Object[][] Pix = PayLoad.LerBancoPIX(pixBanco.getSelectedItem().toString());
                tbcobol = Pix[1][3].toString();
                tbcobolnome = Pix[2][3].toString();
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosPix.add(new BancosPix2(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasPixPrinted.isEmpty()) bancosPixPrinted.add(new BancosPix2(oldBco, oldBcoNome, pessoasPixPrinted));
                    pessoasBoleta = new ArrayList<PessoasPix>();
                    pessoasPixPrinted = new ArrayList<PessoasPix>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasPix(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasPixPrinted.add(new PessoasPix(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress.setValue(pgs);
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rs);

        int pgs = 100;
        try {
            pgs = ((b++ * 100) / rcount) + 1;
        } catch (Exception e) {}
        jProgress.setValue(pgs);

        if (!pessoasBoleta.isEmpty()) {
            bancosPix.add(new BancosPix2(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasPixPrinted.isEmpty()) {
            bancosPixPrinted.add(new BancosPix2(tbcobol, tbcobolnome, pessoasPixPrinted));
        }               
        
        // Boletas não impressas
        {
            PixTreeTableModel boletaTreeTableModel = new PixTreeTableModel(bancosPix);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            treeTable.setRootVisible(false);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            PixTreeTableModel boletaTreeTableModelPrinted = new PixTreeTableModel(bancosPixPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            treeTablePrinted.setRootVisible(false);
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        lbl_Status.setText("");
        jProgress.setValue(0);
    }    
    
    public void PorPeriodo() {
        lbl_Status.setText("Criando Lista...");
        jProgress.setValue(0);
        
        String sContrato = "";
        if (!"".equals(jContrato.getText().trim())) { sContrato = " AND r.contrato = '" + jContrato.getText().trim() + "' "; }
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados FROM recibo r, locatarios l, carteira c " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null) AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento >= '" + 
                     Dates.DateFormata("yyyy-MM-dd", jInicial.getDate()) + "' AND r.dtvencimento <= '" + 
                     Dates.DateFormata("yyyy-MM-dd", jFinal.getDate()) + "') " + 
                     sContrato + " ORDER BY l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosPix = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasBoleta = new ArrayList<PessoasPix>();

        // Boletos Printed
        List<BancosPix2> bancosPixPrinted = new ArrayList<BancosPix2>();
        List<PessoasPix> pessoasPixPrinted = new ArrayList<PessoasPix>();
        
        int b = 0;
        
        // Atribuições
        String trgprp = "";
        String trgimv = "";
        String tcontrato = "";
        String tnome = "";
        String tvencto = "";
        String tnnumero = "";
        String tbcobol = "";
        String tbcobolnome = "";
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = conn.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));
                tnnumero = rs.getString("nnumero");
                String tenvio = "EMAIL";

                /**
                 * Pega dados do Banco
                 */                
                Object[][] Pix = PayLoad.LerBancoPIX(pixBanco.getSelectedItem().toString());
                tbcobol = Pix[1][3].toString();
                tbcobolnome = Pix[2][3].toString();
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosPix.add(new BancosPix2(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasPixPrinted.isEmpty()) bancosPixPrinted.add(new BancosPix2(oldBco, oldBcoNome, pessoasPixPrinted));
                    pessoasBoleta = new ArrayList<PessoasPix>();
                    pessoasPixPrinted = new ArrayList<PessoasPix>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());                
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasPix(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasPixPrinted.add(new PessoasPix(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                }
                
                oldBco = tbcobol; oldBcoNome = tbcobolnome;
                
                int pgs = ((b++ * 100) / rcount) + 1;
                jProgress.setValue(pgs);
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rs);

        int pgs = 100;
        try {
            pgs = ((b++ * 100) / rcount) + 1;
        } catch (Exception e) {}
        jProgress.setValue(pgs);

        if (!pessoasBoleta.isEmpty()) {
            bancosPix.add(new BancosPix2(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasPixPrinted.isEmpty()) {
            bancosPixPrinted.add(new BancosPix2(tbcobol, tbcobolnome, pessoasPixPrinted));
        }               
        
        // Boletas não impressas
        {
            PixTreeTableModel boletaTreeTableModel = new PixTreeTableModel(bancosPix);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            treeTable.setRootVisible(false);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            PixTreeTableModel boletaTreeTableModelPrinted = new PixTreeTableModel(bancosPixPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
            treeTablePrinted.setRootVisible(false);
            SetDisplayParametersPrinted(treeTablePrinted);
            ListaBancosPessoasImpressas.setViewportView(treeTablePrinted);
            ListaBancosPessoasImpressas.repaint();
        }
        
        lbl_Status.setText("");
        jProgress.setValue(0);
    }    

    private void SetDisplayParameters(JXTreeTable table) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRootVisible(false);
            table.setBounds(0, 0, ListaBancosPessoas.getWidth(), ListaBancosPessoas.getHeight());
            table.setVisible(true);
            //table.setRowHeight(50);

            table.getColumnModel().getColumn(0).setWidth(104);
            table.getColumnModel().getColumn(0).setPreferredWidth(104);
            table.getColumnModel().getColumn(0).setMinWidth(104);
            table.getColumnModel().getColumn(0).setMaxWidth(104);

            table.getColumnModel().getColumn(1).setWidth(215);
            table.getColumnModel().getColumn(1).setPreferredWidth(215);
//            table.getColumnModel().getColumn(1).setMinWidth(215);
//            table.getColumnModel().getColumn(1).setMaxWidth(215);

            table.getColumnModel().getColumn(2).setWidth(75);
            table.getColumnModel().getColumn(2).setPreferredWidth(75);
            table.getColumnModel().getColumn(2).setMinWidth(75);
            table.getColumnModel().getColumn(2).setMaxWidth(75);

            table.getColumnModel().getColumn(3).setWidth(75);
            table.getColumnModel().getColumn(3).setPreferredWidth(75);
            table.getColumnModel().getColumn(3).setMinWidth(75);
            table.getColumnModel().getColumn(3).setMaxWidth(75);

            table.getColumnModel().getColumn(4).setWidth(75);
            table.getColumnModel().getColumn(4).setPreferredWidth(75);
            table.getColumnModel().getColumn(4).setMinWidth(75);
            table.getColumnModel().getColumn(4).setMaxWidth(75);

            table.getColumnModel().getColumn(5).setWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setMaxWidth(0);

            table.getColumnModel().getColumn(6).setWidth(0);
            table.getColumnModel().getColumn(6).setPreferredWidth(0);
            table.getColumnModel().getColumn(6).setMinWidth(0);
            table.getColumnModel().getColumn(6).setMaxWidth(0);

            table.getColumnModel().getColumn(7).setWidth(30);
            table.getColumnModel().getColumn(7).setPreferredWidth(30);
            table.getColumnModel().getColumn(7).setMinWidth(30);
            table.getColumnModel().getColumn(7).setMaxWidth(30);
            table.expandAll();
    }
    
    private void SetDisplayParametersPrinted(JXTreeTable table) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRootVisible(false);
            table.setBounds(0, 0, ListaBancosPessoasImpressas.getWidth(), ListaBancosPessoasImpressas.getHeight());
            table.setVisible(true);
            //table.setRowHeight(50);

            table.getColumnModel().getColumn(0).setWidth(102);
            table.getColumnModel().getColumn(0).setPreferredWidth(102);
            table.getColumnModel().getColumn(0).setMinWidth(102);
            table.getColumnModel().getColumn(0).setMaxWidth(102);

            table.getColumnModel().getColumn(1).setWidth(247);
            table.getColumnModel().getColumn(1).setPreferredWidth(247);
            table.getColumnModel().getColumn(1).setMinWidth(247);
            table.getColumnModel().getColumn(1).setMaxWidth(247);

            table.getColumnModel().getColumn(2).setWidth(75);
            table.getColumnModel().getColumn(2).setPreferredWidth(75);
            table.getColumnModel().getColumn(2).setMinWidth(75);
            table.getColumnModel().getColumn(2).setMaxWidth(75);

            table.getColumnModel().getColumn(3).setWidth(75);
            table.getColumnModel().getColumn(3).setPreferredWidth(75);
            table.getColumnModel().getColumn(3).setMinWidth(75);
            table.getColumnModel().getColumn(3).setMaxWidth(75);

            table.getColumnModel().getColumn(4).setWidth(75);
            table.getColumnModel().getColumn(4).setPreferredWidth(75);
            table.getColumnModel().getColumn(4).setMinWidth(75);
            table.getColumnModel().getColumn(4).setMaxWidth(75);

            table.getColumnModel().getColumn(5).setWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setMaxWidth(0);

            table.getColumnModel().getColumn(6).setWidth(0);
            table.getColumnModel().getColumn(6).setPreferredWidth(0);
            table.getColumnModel().getColumn(6).setMinWidth(0);
            table.getColumnModel().getColumn(6).setMaxWidth(0);

            table.getColumnModel().getColumn(7).setWidth(0);
            table.getColumnModel().getColumn(7).setPreferredWidth(0);
            table.getColumnModel().getColumn(7).setMinWidth(0);
            table.getColumnModel().getColumn(7).setMaxWidth(0);
            table.expandAll();
    }
    
    private boolean AnalisaGeracao() {
        boolean retorno = true; boolean selecao = false;
        if (bancosPix != null) {
            for (int b = 0; b <= bancosPix.size() - 1; b++) {
                for (PessoasPix p : bancosPix.get(b).getPessoasPix()) {
                    if (p.getTag()) {
                        selecao = true;
                        if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(p.getVencimentoRec(), "dd/MM/yyyy"), new Date()) > 0) {
                           if (p.getVencimentoBol().equalsIgnoreCase("")) {
                                JOptionPane.showMessageDialog(this, "Para vencimentos menor que a data atual,\n" + 
                                                                     "o campo <boleto> não pode ser banco.");
                                retorno = false;
                                treeTable.scrollRowToVisible(b);
                                return retorno;
                            }

                            if (Dates.DateDiff(Dates.DIA, new Date(), Dates.StringtoDate(p.getVencimentoBol(), "dd/MM/yyyy")) < 0) {
                                JOptionPane.showMessageDialog(this,"A data do campo <boleto> não pode ser menor que a data atual e " + 
                                                                        "nem estar em branco.");
                                retorno = false;
                                treeTable.scrollRowToVisible(b);
                                return retorno;
                            }
                        }
                    }
                }
            }
            if (!selecao) {
                JOptionPane.showMessageDialog(this, "Você deve selecionar ao menos 1(hum( locatário!");
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Você deve primeiro Lista e depois selecionar os locatários para impressão.");
            retorno = false;
        }

        return retorno;
    }

    private String ChecaTermino(String contrato) {
        String msg = "";        
        Object[][] campos = null;
        try {
            campos = conn.ReadFieldsTable(new String[] {"dtinicio","dttermino","dtadito","dtseguro",
                    FuncoesGlobais.Subst("((Month(StrDate(dttermino)) = &1. AND Year(StrDate(dttermino)) = &2.)) AS pinta",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))})}, "CARTEIRA", 
                    FuncoesGlobais.Subst("contrato = '" + contrato + "' AND ((Month(StrDate(dtinicio)) >= &1. AND " + 
                    "Year(StrDate(dttermino)) >= &2.) OR (Month(StrDate(dttermino)) >= &1. AND Year(StrDate(dttermino)) = &2.))",
                    new String[] {String.valueOf(Dates.iMonth(new Date())), String.valueOf(Dates.iYear(new Date()))}));
        } catch (Exception e) {}
        if (campos != null) {
            String tmesanor = (String)campos[3][3]; if (tmesanor == null) tmesanor = "";
            String tdtini = campos[0][3].toString();
            Date _inic = Dates.StringtoDate(tdtini,"dd/MM/yyyy");
            Date _comp = new Date();
            if (_inic.getYear() != _comp.getYear()) {   
                if (tmesanor.isEmpty()) {
                    String meses = null;
                    try {meses = conn.ReadParameters("REAJNUM");} catch (Exception e) {meses = "1";}
                    if (_inic.getMonth() - 1 >= _comp.getMonth() - 1) {
                        if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) <= Integer.valueOf(meses)) {
                            if (((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) > 0) {
                                msg = "Faltam " + ((_inic.getMonth() - 1) - (_comp.getMonth() - 1)) + " Mes(es) para o reajuste!!!";
                            } else msg = "";
                        }
                    } else if (_inic.getMonth() == _comp.getMonth()) {
                        msg = "Este é o mês do reajuste!!!";
                        if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                    }
                } else {
                    boolean reaj = Integer.valueOf(tmesanor.substring(3,7)) >= Dates.iYear(new Date());
                    if (!reaj) {
                        if (_inic.getMonth() - 1 > _comp.getMonth() - 1) {
                            String meses = null;
                            try {meses = conn.ReadParameters("REAJNUM");} catch (Exception e) {meses = "1";}
                            msg = "Faltam " + meses + " Mes(es) para o reajuste!!!";
                        } else if (_inic.getMonth() == _comp.getMonth()) {
                            msg = "Este é o mês do reajuste!!!";
                            if (campos[4][3].equals("1")) msg += "    Termino de contrato!!!";
                        }
                    }
                }
            }

        }
        return msg;
    }
    private ReciboPix CreateReciboPix(String rgprp, String rgimv, String contrato, String vencto, String dataBol) throws SQLException {
        Object[][] Pix = PayLoad.LerBancoPIX(pixBanco.getSelectedItem().toString());

        Collections gVar = VariaveisGlobais.dCliente;

        ReciboPix bean1 = new ReciboPix();
        bean1.setempNome(gVar.get("empresa").toUpperCase().trim());
        bean1.setempEndL1(gVar.get("endereco") + ", " + gVar.get("numero") + gVar.get("complemento") + " - " + gVar.get("bairro"));
        bean1.setempEndL2(gVar.get("cidade") + " - " + gVar.get("estado") + " - CEP " + gVar.get("cep"));
        bean1.setempEndL3("Tel.: " + gVar.get("telefone"));
        bean1.setempEndL4(gVar.get("hpage") + " / " + gVar.get("email"));

        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/boleta/" + VariaveisGlobais.icoBoleta);

        Object[][] msgboleta = null;
        try {
            msgboleta = conn.ReadFieldsTable(new String[] {"msgboleta","dtnasc"}, "locatarios", "contrato = '" + contrato + "'");
        } catch (SQLException e) {}
        
        String mbol = "" + conn.ReadParameters("MSGBOL10");
        if (msgboleta != null) {
            if (!msgboleta[0][3].toString().isEmpty()) mbol = msgboleta[0][3].toString();
        }
        
        bean1.setlocaMsgL01(mbol);

        String txtAniversario = "";
        if ("TRUE".equals(conn.ReadParameters("ANIVERSARIO").toUpperCase())) {
        txtAniversario = "" + conn.ReadParameters("MSGANIVERSARIO");
        } else {
        if (("TRUE".equals(conn.ReadParameters("FERIADOS").toUpperCase()))) { }
        }

        String msg = ChecaTermino(contrato);
        if (!msg.trim().equals("")) {
            bean1.setlocaMsgL02(msg);
        } else {
            if (msgboleta != null) {
                if (msgboleta[1][3] != null) {
                    if (Dates.DateFormata("MM", Dates.StringtoDate(msgboleta[1][3].toString().substring(0, 10), "yyyy-MM-dd")).equals(Dates.DateFormata("MM", Dates.StringtoDate(vencto,"dd-MM-yyyy")))) {
                        bean1.setlocaMsgL02(txtAniversario);
                    }
                }
            }
        }

        // 13-11-2017 - Reimpressão com nova data de boleta
        if (dataBol != null) {
            bean1.setlocaMsgL02("Calculos feitos em referencia a data original de Vencimento " + vencto);
        }

        String[][] linhas = Recalcula(rgprp, rgimv, contrato, vencto);
        float[] totais = CalcularRecibo(rgprp, rgimv, contrato, vencto);

        // exp, mul, jur, cor
        //float expediente = totais[0], multa = totais[1], juros = totais[2], correcao = totais[3];
        float expediente = 0, multa = 0, juros = 0, correcao = 0;
        
        if (VariaveisGlobais.boletoEP || VariaveisGlobais.boletoSomaEP) expediente = totais[0];
        if (VariaveisGlobais.boletoMU) { multa = totais[1]; } else { totais[4] -= totais[1]; }
        if (VariaveisGlobais.boletoJU) { juros = totais[2]; } else { totais[4] -= totais[2]; }
        if (VariaveisGlobais.boletoCO) { correcao = totais[3]; } else { totais[4] -= totais[3]; }
        float tRecibo = totais[4];
        
        // Atualizar Recibo parando MU/JU/CO/EP nesta data
        AlteraMUJUCOEP(contrato,vencto,multa,juros,correcao,expediente);
        
        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.format(multa);

        if ((VariaveisGlobais.boletoEP && expediente > 0) && !VariaveisGlobais.boletoSomaEP) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("EP");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(expediente);
            }
        } else if (VariaveisGlobais.boletoEP && VariaveisGlobais.boletoSomaEP) {
            float alrec = LerValor.StringToFloat(linhas[0][2]);
            linhas[0][2] = LerValor.floatToCurrency(alrec + expediente, 2);
            expediente = 0;
        } else if (!VariaveisGlobais.boletoEP && !VariaveisGlobais.boletoSomaEP) {
            tRecibo -= totais[0];
            expediente = 0;
        }

        if (multa > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("MU");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(multa);
            }
        }

        if (juros > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("JU");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(juros);
            }
        }

        if (correcao > 0) {
            int pos = AchaVazio(linhas);
            if (pos > -1) {
                linhas[pos][0] = gVar.get("CO");
                linhas[pos][1] = "-";
                linhas[pos][2] = df.format(correcao);
            }
        }

        try {
            bean1.setlocaDescL01(linhas[0][0]);
            bean1.setlocaCpL01(linhas[0][1]);
            bean1.setlocaVrL01(linhas[0][2]);
            bean1.setlocaDescL02(linhas[1][0]); 
            bean1.setlocaCpL02(linhas[1][1]);
            bean1.setlocaVrL02(linhas[1][2]);
            bean1.setlocaDescL03(linhas[2][0]);
            bean1.setlocaCpL03(linhas[2][1]);
            bean1.setlocaVrL03(linhas[2][2]);
            bean1.setlocaDescL04(linhas[3][0]);
            bean1.setlocaCpL04(linhas[3][1]);
            bean1.setlocaVrL04(linhas[3][2]);
            bean1.setlocaDescL05(linhas[4][0]);
            bean1.setlocaCpL05(linhas[4][1]);
            bean1.setlocaVrL05(linhas[4][2]);
            bean1.setlocaDescL06(linhas[5][0]);
            bean1.setlocaCpL06(linhas[5][1]);
            bean1.setlocaVrL06(linhas[5][2]);
            bean1.setlocaDescL07(linhas[6][0]);
            bean1.setlocaCpL07(linhas[6][1]);
            bean1.setlocaVrL07(linhas[6][2]);
            bean1.setlocaDescL08(linhas[7][0]);
            bean1.setlocaCpL08(linhas[7][1]);
            bean1.setlocaVrL08(linhas[7][2]);
            bean1.setlocaDescL09(linhas[8][0]);
            bean1.setlocaCpL09(linhas[8][1]);
            bean1.setlocaVrL09(linhas[8][2]);
            bean1.setlocaDescL10(linhas[9][0]);
            bean1.setlocaCpL10(linhas[9][1]);
            bean1.setlocaVrL10(linhas[9][2]);
            bean1.setlocaDescL11(linhas[10][0]);
            bean1.setlocaCpL11(linhas[10][1]);
            bean1.setlocaVrL11(linhas[10][2]);
            bean1.setlocaDescL12(linhas[11][0]);
            bean1.setlocaCpL12(linhas[11][1]);
            bean1.setlocaVrL12(linhas[11][2]);
            bean1.setlocaDescL13(linhas[12][0]);
            bean1.setlocaCpL13(linhas[12][1]);
            bean1.setlocaVrL13(linhas[12][2]);
            bean1.setlocaDescL14(linhas[13][0]);
            bean1.setlocaCpL14(linhas[13][1]);
            bean1.setlocaVrL14(linhas[13][2]);
        } catch (Exception ex) {}

        //if (dataBol != null) {
        //    bean1.setpixDadosVencimento(dataBol);
        //} else {
            bean1.setpixDadosVencimento(vencto);
        //}
        
        String cValor = new Bancos.bancos(VariaveisGlobais.conexao).Valor4Boleta(LerValor.floatToCurrency(tRecibo,2));  // valor da boleta
        bean1.setpixDadosVrdoc(df.format(tRecibo));

        String nNumero = "";       
        nNumero = FuncoesGlobais.StrZero(Pix[3][3].toString(),11); 

        // Atualizar Nosso Numero
        nNumero = String.valueOf(Integer.valueOf(nNumero) + 1);

        if (Double.valueOf(nNumero) != 0) {
            PayLoad.GravarNnumeroPIX(Pix[2][3].toString(), nNumero);
        } else {
            JOptionPane.showMessageDialog(null, "Nosso numero não pode ser 0(ZERO)!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
            System.exit(1);
        }

        nNumero = new PayLoad().NossoNumeroPix(nNumero, 10);
        
        /***************************************************
         * Gravar Nosso Numero no Arquivo de RECIBO para
         * posterior baixa. e Avisar que remessa = 'N'
         */
        try {
            String recUpdSql = "UPDATE recibo SET nnumero = '" + "PIX" + nNumero + 
                            "', remessa = 'N', dtvencbol = "  + (dataBol == null ? dataBol + ", ": "'" + Dates.StringtoString(dataBol, "dd/MM/yyyy","yyyy/MM/dd") + "', ") + 
                            "emailbol = 'N' WHERE contrato = '" + contrato + "' AND " +
                            "dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd",
                            Dates.StringtoDate(vencto, "dd/MM/yyyy")) + "';";
            conn.CommandExecute(recUpdSql);
        } catch (Exception err) {err.printStackTrace();}

        bean1.setpixNNumero(nNumero);
        bean1.setpixDadosNumdoc(rgprp + "/" + contrato);

        Object[][] dadosSac = conn.ReadFieldsTable(new String[] {"nomerazao", "cpfcnpj"}, "locatarios", "contrato = '" + contrato + "'");

        bean1.setsacDadosNome(dadosSac[0][3].toString());  // Nome do Sacado
        bean1.setsacDadosCpfcnpj("CNPJ/CPF: " + dadosSac[1][3]);  // Cpf ou Cnpj do Sacado

        Object[][] endSac = conn.ReadFieldsTable(new String[] {"end", "num", "compl", "bairro", "cidade", "estado", "cep"}, "imoveis", "rgimv = '" + rgimv + "'");
        bean1.setsacDadosEndereco(endSac[0][3].toString());
        bean1.setsacDadosNumero(endSac[1][3].toString());
        bean1.setsacDadosCompl(endSac[2][3].toString());
        bean1.setsacDadosBairro(endSac[3][3].toString());
        bean1.setsacDadosCidade(endSac[4][3].toString());
        bean1.setsacDadosEstado(endSac[5][3].toString());
        bean1.setsacDadosCep(endSac[6][3].toString());

        // Pix
        PayLoad payLoad = new PayLoad();
        payLoad.setPixKey(PixEmpresa(Pix[1][3].toString()));
        payLoad.setDescription(gVar.get("recibo") + " Vencimento " + vencto);
        payLoad.setAmount(tRecibo);
        payLoad.setMerchantName(gVar.get("empresa").trim().toUpperCase());
        payLoad.setMerchantCity("SAO PAULO");
        payLoad.setTxId("PIX" + nNumero);
        
        Date dataHoje = new Date();
        if (dataBol != null) {
            dataHoje = Dates.StringtoDate(dataBol, "dd-MM-yyyy");
        }
        bean1.setpixDadosDatadoc(Dates.DatetoString(dataHoje)); 
        bean1.setpixValidadePIX(Dates.DatetoString(Dates.DateAdd(Dates.DIA, 10, dataHoje)));
 
        String PixCopiaCola = payLoad.getPayload();
        Map<EncodeHintType, Object> qrParam = new HashMap<EncodeHintType, Object> ();
        qrParam.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        qrParam.put (EncodeHintType.CHARACTER_SET, "UTF-8" );

        BarcodeQRCode code25 = new BarcodeQRCode(PixCopiaCola, 66, 66, qrParam);
        Image cdbar = null;
        cdbar = code25.createAwtImage(Color.BLACK, Color.WHITE);
        
        bean1.setPixCopiaeCola(PixCopiaCola);
        bean1.setPixImage(cdbar);
        
        return bean1;
    }
    
    /**
     * Retorna KeyPix sem formatação
     * @param string pix
     * @return string
     */
    private String PixEmpresa(String pix) {
        String retorno = pix;
        if (!pix.contains("@")) retorno = pix.replace(".", "").replace("-", "").replace("/", "").replace("(", "").replace(")", "");
        return retorno;
    }
    
    private void AlteraMUJUCOEP(String contrato, String vecto, float multa,float juros,float correcao,float expediente) {

        String auxCpo = VariaveisGlobais.ccampos;
        
        String vMulta = ""; String vJuros = ""; String vCorrecao = ""; String vTaxa = "";
        
        // Multa
        if (multa > 0) {
            vMulta = "MU" + FuncoesGlobais.GravaValor(LerValor.FloatToString(multa));
        } else {
            vMulta = "MU";
        }
        
        // Juros
        if (juros > 0) {
            vJuros = "JU" + FuncoesGlobais.GravaValor(LerValor.FloatToString(juros));
        } else {
            vJuros = "JU";
        }
        
        // Correção
        if (correcao > 0) {
            vCorrecao = "CO" + FuncoesGlobais.GravaValor(LerValor.FloatToString(correcao));
        } else {
            vCorrecao = "CO";
        }
        
        // Taxa
        if (expediente > 0) {
            vTaxa = "EP" + FuncoesGlobais.GravaValor(LerValor.FloatToString(expediente));
        } else {
            vTaxa = "EP";
        }
        
        // Limpar Ocorrencias
        String oldMU = BuscaXX(auxCpo, "MU");
        String oldJU = BuscaXX(auxCpo, "JU");
        String oldCO = BuscaXX(auxCpo, "CO");
        String oldEP = BuscaXX(auxCpo, "EP");
        
        auxCpo = auxCpo.replace(oldMU, "");
        auxCpo = auxCpo.replace(oldJU, "");
        auxCpo = auxCpo.replace(oldCO, "");
        auxCpo = auxCpo.replace(oldEP, "");
        
        // Grava Novas Ocorrencias
        String mujucoep = "";
        if (!"".equals(vMulta)) {
            mujucoep += vMulta + ":";
        }
        if (!"".equals(vJuros)) {
            mujucoep += vJuros + ":";
        }
        if (!"".equals(vCorrecao)) {
            mujucoep += vCorrecao + ":";
        }
        if (!"".equals(vTaxa)) {
            mujucoep += vTaxa + ":";
        }
        
        if (mujucoep.equalsIgnoreCase("MU:JU:CO:EP:") == false) {
            int pos = auxCpo.indexOf("AL:");
            auxCpo = auxCpo.substring(0, pos + 3) + mujucoep + auxCpo.substring(pos + 3);
        
            String SQLtxt = "UPDATE recibo SET campo = '" + auxCpo + "' WHERE contrato = '" + contrato +
                        "' AND dtvencimento = '" + Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(vecto,"dd/MM/yyyy")) + "';";

            conn.CommandExecute(SQLtxt);
        }
    }

    private String BuscaXX(String zcampo, String oque) {
        String zRet = "";
        int i = zcampo.indexOf(oque);
        if (i >= 0) {
            // Achou
            String auxCpo = zcampo.substring(i + 2);
            if (auxCpo.length() > 0) {
                if (":".equals(auxCpo.substring(0,1))) {
                    zRet = oque + ":";
                } else {
                    zRet = oque + auxCpo.substring(0,10) + ":";
                }
            } else zRet = oque + ":";
        }
        
        return zRet;
    }
    
    private int AchaVazio(String[][] value) {
        int r = -1;

        for (int i=0;i<value.length;i++) {
            if (value[i][0] == null || "".equals(value[i][0])) {r = i; break;}
        }

        return r;
    }

    public String[][] Recalcula(String rgprp, String rgimv, String contrato, String vencimento) {
        String[][] linhas = null;
        try {
            linhas = MontaTela(rgprp, rgimv, contrato, vencimento);
        } catch (Exception ex) {} 

        return linhas;
    }

    private String IPTU(String vecto, String campo, String rgimv) {
        String pIptu = ""; Integer pant = 0;
        try { pIptu = conn.ReadParameters("IPTU"); } catch (Exception e) {}
        try { pant = Integer.valueOf(conn.ReadParameters("IPTUANT"));  } catch (Exception e) {}
        if (pIptu == null || pIptu.equalsIgnoreCase("")) return campo;
        
        Boolean eiptu = campo.contains(pIptu + ":");
        if (eiptu) return campo;
        
        String[] meses = {"","jan","fev","mar","abr","mai","jun","jul","ago","set","out","nov","dez"};
        String ddia = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(0, 2);
        String dmes = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(3, 5);
        String dano  = Dates.StringtoString(vecto, "dd-MM-yyyy", "dd-MM-yyyy").substring(6, 10);
        String umes = Dates.DateFormata("dd-MM-yyyy", new Date()).substring(3, 5);
        String uano = Dates.DateFormata("dd-MM-yyyy", new Date()).substring(6, 10);
        
        String wmes = ""; String wValor = "0000000000";
        
        String wSql = "SELECT p.* FROM iptu p, imoveis i WHERE InStr(i.matriculas,p.matricula) > 0 AND p.ano = '" + dano + "' AND i.rgimv = '" + rgimv + "';";
        ResultSet ws = conn.OpenTable(wSql, null);
        try {
            while (ws.next()) {
                wmes = ws.getString(meses[Integer.valueOf(dmes)]);
                String[] avar = wmes.split(";");
                if (avar.length > 0) {
                    for (int i=0;i<avar.length;i++) {
                        String[] rvar = avar[i].split(",");
                        if (!rvar[0].trim().equalsIgnoreCase("")) {
                            if (Dates.DateDiff(Dates.DIA, new Date(), Dates.DateAdd(Dates.DIA, (pant * -1), Dates.StringtoDate(rvar[0], "dd-MM-yyyy"))) > 0) {
                                wValor = rvar[1];
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return campo + ";" + (!wValor.equalsIgnoreCase("0000000000") ? pIptu + ":2:" + wValor + ":0000:NT:RZ:ET:IP" : "");
    }
    
    public String[][] MontaTela(String rgprp, String rgimv, String contrato, String vecto) throws SQLException, ParseException {

        String sql = "SELECT * FROM recibo WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.OpenTable(sql, null);

        String[][] linhas = null; 
        if (pResult.first()) {
            String tcampo = IPTU(vecto, pResult.getString("campo"), rgimv);
            DepuraCampos a = new DepuraCampos(tcampo);
            VariaveisGlobais.ccampos = tcampo;

//            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
//            VariaveisGlobais.ccampos = pResult.getString("campo");
            linhas = new String[14][3];

            a.SplitCampos();
            // Ordena Matriz
            Arrays.sort(a.aCampos, new Comparator() {
                private int pos1 = 3;
                private int pos2 = 4;
                @Override
                public int compare(Object o1, Object o2) {
                    String p1 = ((String)o1).substring(pos1, pos2);
                    String p2 = ((String)o2).substring(pos1, pos2);
                    return p1.compareTo(p2);
                }
            });

            // Monta campos
            for (int i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar(i);
                if (Campo.length > 0) {
                    //MontaCampos(Campo, i);
                    linhas[i][0] = Campo[0];
                    linhas[i][1] = ("00/00".equals(Campo[3]) || "00/0000".equals(Campo[3]) || "".equals(Campo[3]) || "99/00".equals(Campo[3]) || "99/0000".equals(Campo[3]) ? "-" : Campo[3]) ;
                    linhas[i][2] = Campo[1];
                }
            }
        }

        conn.CloseTable(pResult);

        return linhas;
    }

    private float[] CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto) {
        if ("".equals(vecto.trim())) { return null; }

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}

        String campo = ""; String rcampo = ""; boolean mCartVazio = false;
        String sql = "SELECT * FROM recibo WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.OpenTable(sql, null);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");

                // Aqui recebe o IPTU
                campo = IPTU(vecto, campo, rgimv);
                
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
        }
        conn.CloseTable(pResult);

        float exp = 0, multa = 0, juros = 0, correcao = 0;

        try {
            exp = rc.TaxaExp(campo);
        } catch (SQLException ex) {}

        try {
            multa = rc.Multa(campo, vecto, false);
        } catch (SQLException ex) {}

        try {
            juros = rc.Juros(campo, vecto);
        } catch (SQLException ex) {}

        try {
            correcao = rc.Correcao(campo, vecto);
        } catch (SQLException ex) {}

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        tRecibo += exp + multa + juros + correcao;

        float[] retorno = new float[5];
        retorno[0] = exp; retorno[1] = multa; retorno[2] = juros; retorno[3] = correcao; retorno[4] = tRecibo;
        return retorno;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner AnoRef;
    private javax.swing.JButton FontBackColor;
    private javax.swing.JToggleButton FontBold;
    private javax.swing.JButton FontColor;
    private javax.swing.JToggleButton FontItalic;
    private javax.swing.JComboBox FontName;
    private javax.swing.JComboBox FontSize;
    private javax.swing.JToggleButton FontUnderLine;
    private javax.swing.JScrollPane ListaBancosPessoas;
    private javax.swing.JScrollPane ListaBancosPessoasImpressas;
    private javax.swing.JSpinner MesRef;
    private javax.swing.JToggleButton TextAlignCenter;
    private javax.swing.JToggleButton TextAlignJustified;
    private javax.swing.JToggleButton TextAlignLeft;
    private javax.swing.JToggleButton TextAlignRigth;
    private com.toedter.calendar.JDateChooser VencBoleto;
    private javax.swing.JButton btnEditarCadastro;
    private javax.swing.JButton btnEnviarSelecao;
    private javax.swing.JButton btnEnviarTodos;
    private javax.swing.JButton btnListarBoletasEmail;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField edtSubJect;
    private javax.swing.JFormattedTextField jContrato;
    private com.toedter.calendar.JDateChooser jFinal;
    private javax.swing.JButton jGerar;
    private com.toedter.calendar.JDateChooser jInicial;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JButton jListar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPeriodo;
    private javax.swing.JProgressBar jProgress;
    private javax.swing.JProgressBar jProgressEmail;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JCheckBox jSemEnvio;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPaneBoletas;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JRadioButton jatrasados;
    private javax.swing.JRadioButton jemdia;
    private javax.swing.JRadioButton jperiodo;
    private javax.swing.JRadioButton jtodos;
    private javax.swing.JLabel lbl_Status;
    private javax.swing.JScrollPane pScroll;
    private javax.swing.JComboBox<String> pixBanco;
    private javax.swing.JTable tblEmails;
    // End of variables declaration//GEN-END:variables

    // Editor
    private void EditorEmail() {
        Action boldAction = new BoldAction();
        boldAction.putValue(Action.NAME, "");
        Action italicAction = new ItalicAction();
        italicAction.putValue(Action.NAME, "");
        Action underlineAction = new UnderlineAction();
        underlineAction.putValue(Action.NAME, "");

        Action alignleftAction = new AlignLeftAction();
        Action alignrightAction = new AlignRightAction();
        Action aligncenterAction = new AlignCenterAction();
        Action alignjustifiedAction = new AlignJustifiedAction();

        Action fontsizeAction = new FontSizeAction();
        Action foregroundAction = new ForegroundAction();
        Action backgroundAction = new BackgroundAction();
        Action formatTextAction = new FontAndSizeAction();

        FontBold.setAction(boldAction);
        FontBold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2532_text_bold.png")));
        FontItalic.setAction(italicAction);
        FontItalic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2533_text_italic.png")));
        FontUnderLine.setAction(underlineAction);
        FontUnderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2536_text_underline.png")));

        TextAlignLeft.setAction(alignleftAction);
        TextAlignLeft.setText(null);
        TextAlignLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_left.png")));
        TextAlignLeft.setToolTipText("Alinhamento a Esquerda");

        TextAlignRigth.setAction(alignrightAction);
        TextAlignRigth.setText(null);
        TextAlignRigth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_right.png")));
        TextAlignRigth.setToolTipText("Alinhamento a Direita");

        TextAlignCenter.setAction(aligncenterAction);
        TextAlignCenter.setText(null);
        TextAlignCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_center.png")));
        TextAlignCenter.setToolTipText("Alinhamento ao Centro");

        TextAlignJustified.setAction(alignjustifiedAction);
        TextAlignJustified.setText(null);
        TextAlignJustified.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_justify.png")));
        TextAlignJustified.setToolTipText("Alinhamento Justificado");

        ListarFontes();
        FontName.setAction(fontsizeAction);
        FontName.setToolTipText("Estilo da Fonte");
        FontSize.setAction(fontsizeAction);
        FontSize.setToolTipText("Tamanho da Fonte");

        FontColor.setAction(foregroundAction);
        FontColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_foregroundcolor.png")));
        FontColor.setToolTipText("Cor da Fonte");

        FontBackColor.setAction(backgroundAction);
        FontBackColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_backgroundcolor.png")));
        FontBackColor.setToolTipText("Cor do Fundo");

        _htmlPane.setEditable(true);
        pScroll.setViewportView(_htmlPane);

        HTMLEditorKit htmlEditor = new HTMLEditorKit();
        _htmlPane.setEditorKit(htmlEditor);
        String aSubject = ""; String aBody = "";
        //try { aBody = ManipuladorArquivo.leitor("reports/EmailBody.txt"); } catch (IOException ex) {}
        //try { aSubject = ManipuladorArquivo.leitor("reports/EmailSubJect.txt"); } catch (IOException ex) {}
        edtSubJect.setText(aSubject);
        String htmlString = aBody; 
        Document doc=htmlEditor.createDefaultDocument();
        _htmlPane.setDocument(doc);
        _htmlPane.setText(htmlString);
    }

    
    class FontSizeAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 584531387732416339L;
        private String family;
        private float fontSize;

        public FontSizeAction() {
            super("Font and Size");
        }

        public String toString() {
            return "Font and Size";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            family = (String) FontName.getSelectedItem();
            fontSize = Float.parseFloat(FontSize.getSelectedItem().toString());
            MutableAttributeSet attr = null;
            if (editor != null) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, family);
                StyleConstants.setFontSize(attr, (int) fontSize);
                setCharacterAttributes(editor, attr, false);
                editor.requestFocus();
            }
        }
    }

    class FontCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(
                list,value,index,isSelected,cellHasFocus);
            Font font = new Font((String)value, Font.PLAIN, 20);
            label.setFont(font);
            return label;
        }
    }

    private void ListarFontes() {
        GraphicsEnvironment ge = GraphicsEnvironment.
            getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        for (String f : fonts) {
            FontName.addItem(f);
        }
        FontName.setRenderer(new FontCellRenderer());
        //FontName.setSelectedItem(family);
    }

    class AlignJustifiedAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignJustifiedAction() {
            super("align-justified");
        }

        public String toString() {
            return "AlignJustified";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_JUSTIFIED);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignCenterAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignCenterAction() {
            super("align-center");
        }

        public String toString() {
            return "AlignCenter";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_CENTER);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignRightAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignRightAction() {
            super("align-right");
        }

        public String toString() {
            return "AlignRight";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_RIGHT);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class AlignLeftAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1749670038684056758L;

        public AlignLeftAction() {
            super("align-left");
        }

        public String toString() {
            return "AlignLeft";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setAlignment(sas, StyleConstants.ALIGN_LEFT);
                setParagraphAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class UnderlineAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 1794670038684056758L;

        public UnderlineAction() {
            super("font-underline");
        }

        public String toString() {
            return "Underline";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean underline = (StyleConstants.isUnderline(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setUnderline(sas, underline);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class BoldAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 9174670038684056758L;

        public BoldAction() {
            super("font-bold");
        }

        public String toString() {
            return "Bold";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean bold = (StyleConstants.isBold(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setBold(sas, bold);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class ItalicAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = -1428340091100055456L;

        public ItalicAction() {
            super("font-italic");
        }

        public String toString() {
            return "Italic";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                MutableAttributeSet attr = kit.getInputAttributes();
                boolean italic = (StyleConstants.isItalic(attr)) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setItalic(sas, italic);
                setCharacterAttributes(editor, sas, false);
                editor.requestFocus();
            }
        }
    }

    class ForegroundAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 6384632651737400352L;
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = new JDialog();
        boolean noChange = false;
        boolean cancelled = false;

        public ForegroundAction() {
            super("");
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            if (editor == null) {
                JOptionPane.showMessageDialog(null,
                    "You need to select the editor pane before you can change the color.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return;
            }
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            fg = StyleConstants.getForeground(as);
            if (fg == null) {
                fg = Color.BLACK;
            }
            colorChooser.setColor(fg);
            JButton accept = new JButton("OK");
            accept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    fg = colorChooser.getColor();
                    dialog.dispose();
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                   cancelled = true;
                    dialog.dispose();
                }
            });
            JButton none = new JButton("None");
            none.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    noChange = true;
                    dialog.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(accept);
            buttons.add(none);
            buttons.add(cancel);
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(colorChooser, BorderLayout.CENTER);
            dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
            dialog.setModal(true);
            dialog.pack();
            dialog.setVisible(true);
            if (!cancelled) {
                MutableAttributeSet attr = null;
                if (editor != null) {
                    if (fg != null && !noChange) {
                        attr = new SimpleAttributeSet();
                        StyleConstants.setForeground(attr, fg);
                        setCharacterAttributes(editor, attr, false);
                    }
                }
            }// end if color != null
            noChange = false;
            cancelled = false;
        }
        private Color fg;
    }

    class FontAndSizeAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 584531387732416339L;
        private String family;
        private float fontSize;
        JDialog formatText;
        private boolean accept = false;
        JComboBox fontFamilyChooser;
        JComboBox fontSizeChooser;

        public FontAndSizeAction() {
            super("Font and Size");
        }

        public String toString() {
            return "Font and Size";
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            family = StyleConstants.getFontFamily(as);
            fontSize = StyleConstants.getFontSize(as);
            formatText = new JDialog(new JFrame(), "Font and Size", true);
            formatText.getContentPane().setLayout(new BorderLayout());
            JPanel choosers = new JPanel();
            choosers.setLayout(new GridLayout(2, 1));
            JPanel fontFamilyPanel = new JPanel();
            fontFamilyPanel.add(new JLabel("Font"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            fontFamilyChooser = new JComboBox();
            for (int i = 0; i < fontNames.length; i++) {
                fontFamilyChooser.addItem(fontNames[i]);
            }
            fontFamilyChooser.setSelectedItem(family);
            fontFamilyPanel.add(fontFamilyChooser);
            choosers.add(fontFamilyPanel);
            JPanel fontSizePanel = new JPanel();
            fontSizePanel.add(new JLabel("Size"));
            fontSizeChooser = new JComboBox();
            fontSizeChooser.setEditable(true);
            fontSizeChooser.addItem(new Float(4));
            fontSizeChooser.addItem(new Float(8));
            fontSizeChooser.addItem(new Float(12));
            fontSizeChooser.addItem(new Float(16));
            fontSizeChooser.addItem(new Float(20));
            fontSizeChooser.addItem(new Float(24));
            fontSizeChooser.setSelectedItem(new Float(fontSize));
            fontSizePanel.add(fontSizeChooser);
            choosers.add(fontSizePanel);
            JButton ok = new JButton("OK");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    accept = true;
                    formatText.dispose();
                    family = (String) fontFamilyChooser.getSelectedItem();
                    fontSize = Float.parseFloat(fontSizeChooser.getSelectedItem().toString());
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    formatText.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(ok);
            buttons.add(cancel);
            formatText.getContentPane().add(choosers, BorderLayout.CENTER);
            formatText.getContentPane().add(buttons, BorderLayout.SOUTH);
            formatText.pack();
            formatText.setVisible(true);
            MutableAttributeSet attr = null;
            if (editor != null && accept) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, family);
                StyleConstants.setFontSize(attr, (int) fontSize);
                setCharacterAttributes(editor, attr, false);
            }
        }
    }

    class UndoListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }   
    }

    class UndoAction extends AbstractAction {
        public UndoAction() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.undo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(undoManager.canUndo());
        }
    }

    class RedoAction extends AbstractAction {
        public RedoAction() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            if (this.isEnabled()) {
                undoManager.redo();
                undoAction.update();
                redoAction.update();
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(undoManager.canRedo());
        }
    }

    class BackgroundAction extends StyledEditorKit.StyledTextAction {
        private static final long serialVersionUID = 3684632651737400352L;
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = new JDialog();
        boolean noChange = false;
        boolean cancelled = false;

        public BackgroundAction() {
            super("");
        }

        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = (JEditorPane) getEditor(e);
            if (editor == null) {
                JOptionPane.showMessageDialog(null,
                    "You need to select the editor pane before you can change the color.", "Error",
                JOptionPane.ERROR_MESSAGE);
                return;
            }
            int p0 = editor.getSelectionStart();
            StyledDocument doc = getStyledDocument(editor);
            Element paragraph = doc.getCharacterElement(p0);
            AttributeSet as = paragraph.getAttributes();
            fg = StyleConstants.getBackground(as);
            if (fg == null) {
                fg = Color.BLACK;
            }
            colorChooser.setColor(fg);
            JButton accept = new JButton("OK");
            accept.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    fg = colorChooser.getColor();
                    dialog.dispose();
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                   cancelled = true;
                    dialog.dispose();
                }
            });
            JButton none = new JButton("None");
            none.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    noChange = true;
                    dialog.dispose();
                }
            });
            JPanel buttons = new JPanel();
            buttons.add(accept);
            buttons.add(none);
            buttons.add(cancel);
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(colorChooser, BorderLayout.CENTER);
            dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
            dialog.setModal(true);
            dialog.pack();
            dialog.setVisible(true);
            if (!cancelled) {
                MutableAttributeSet attr = null;
                if (editor != null) {
                    if (fg != null && !noChange) {
                        attr = new SimpleAttributeSet();
                        StyleConstants.setBackground(attr, fg);
                        setCharacterAttributes(editor, attr, false);
                    }
                }
            }// end if color != null
            noChange = false;
            cancelled = false;
        }
        private Color fg;
    }
}