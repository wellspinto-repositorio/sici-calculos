package Movimento.BoletasCentral;

import Bancos.*;
import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import static Funcoes.FuncoesGlobais.disableLookInComboBox;
import Funcoes.LerValor;
import Funcoes.StringManager;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.gmail.GmailAPI;
import static Funcoes.gmail.GmailOperations.createEmailWithAttachment;
import static Funcoes.gmail.GmailOperations.createMessageWithEmail;
import Funcoes.jDirectory;
import Funcoes.jTableControl;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import Sici.Partida.Collections;
import boleta.Boleta;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.jdesktop.swingx.JXTreeTable;

public class CentralBoletas extends javax.swing.JInternalFrame {
    private JXTreeTable treeTable;
    private List<BancosBoleta> bancosBoleta;

    private JXTreeTable treeTablePrinted;
    
    private JXTreeTable treeTableRemessa;
    private List<BancosBoleta> bancosBoletaRemessa;

    Db conn = VariaveisGlobais.conexao;
    String[] month;
    int[] dmonth;
    
    jTableControl tabela = new jTableControl(true);    
    
    CentralBoletasConsulta centralboletasconsulta = new CentralBoletasConsulta();
    CentralBoletasBaixas centralboletasbaixas = new CentralBoletasBaixas();

    private String _botoes = null;

    // Botoes que acompanham a tela
    private boolean _Geracao = true;
    private boolean _Remessa = true;
    private boolean _Email = true;
    private boolean _Consultas = true;
    private boolean _Baixas = true;
    
    public void setBotoes(String _botoes) {
        this._botoes = _botoes;
    }
    
    public CentralBoletas() {
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

                    Pos = FuncoesGlobais.IndexOf(btn, "remessa");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Remessa = new Boolean(_btn[1].replace("\"", ""));
                    }

                    Pos = FuncoesGlobais.IndexOf(btn, "email");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Email = new Boolean(_btn[1].replace("\"", ""));
                    }

                    Pos = FuncoesGlobais.IndexOf(btn, "consultas");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Consultas = new Boolean(_btn[1].replace("\"", ""));
                    }

                    Pos = FuncoesGlobais.IndexOf(btn, "baixas");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Baixas = new Boolean(_btn[1].replace("\"", ""));
                    }
                }
                
                if (!_Geracao) jTabbedPaneBoletas.removeTabAt(jTabbedPaneBoletas.indexOfTab("Geração"));
                if (!_Remessa) jTabbedPaneBoletas.removeTabAt(jTabbedPaneBoletas.indexOfTab("Remessa"));
                if (!_Email) jTabbedPaneBoletas.removeTabAt(jTabbedPaneBoletas.indexOfTab("E-Mail"));
                if (!_Consultas) jTabbedPaneBoletas.removeTabAt(jTabbedPaneBoletas.indexOfTab("Consultas"));
                if (!_Baixas) jTabbedPaneBoletas.removeTabAt(jTabbedPaneBoletas.indexOfTab("Baixas"));
            }
        });        
        
        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/boletas.svg",16,16);
        setFrameIcon(icone);        

        rDownload.setVisible(VariaveisGlobais.webswing);
        jScrollPane3.setSize(791, (VariaveisGlobais.webswing ? 181 : 225));        
        jScrollPane3.setLocation(0, 0);
        tblBancosRemessa.setSize(791, (VariaveisGlobais.webswing ? 181 : 225));        
        tblBancosRemessa.setLocation(0, 0);
        
        jSemEnvio.setVisible(VariaveisGlobais.funcao.equalsIgnoreCase("sys"));
        
        lbl_Status.setText("");
        VencBoleto.setMinSelectableDate(Calendar.getInstance().getTime());  
        VencBoleto.setDate(new Date());
        JTextFieldDateEditor editor = (JTextFieldDateEditor) VencBoleto.getDateEditor();
        editor.setEditable(false);
        
        // Tela de consultas [801, 513]
        centralboletasconsulta.setVisible(true);
        centralboletasconsulta.setEnabled(true);
        centralboletasconsulta.setBounds(0, 0, 841,513); 
        centralboletasconsulta.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelConsultas.add(centralboletasconsulta); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelConsultas.repaint();
        jPanelConsultas.setEnabled(true);
        
        // Tela de baixas
        centralboletasbaixas.setVisible(true);
        centralboletasbaixas.setEnabled(true);
        centralboletasbaixas.setBounds(0, 0, 841,513); 
        centralboletasbaixas.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelBaixas.add(centralboletasbaixas); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelBaixas.repaint();
        jPanelBaixas.setEnabled(true);
        
        this.month = new String[]{"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        this.dmonth = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        MesRef.setValue(this.month[(new Date()).getMonth()]);
        AnoRef.setValue(1900 + new Date().getYear());
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
        ListaBancosPessoas = new javax.swing.JScrollPane();
        ListaBancosPessoasImpressas = new javax.swing.JScrollPane();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblBancosRemessa = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        rDownload = new javax.swing.JButton();
        ListaBancosPessoasRemessa = new javax.swing.JScrollPane();
        jPanel14 = new javax.swing.JPanel();
        jListarRemessa = new javax.swing.JButton();
        btnGerarRemessa = new javax.swing.JButton();
        jProgressRemessa = new javax.swing.JProgressBar();
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
        jScrollPane4 = new javax.swing.JScrollPane();
        jMensagem = new javax.swing.JEditorPane();
        jLabel9 = new javax.swing.JLabel();
        jPanelConsultas = new javax.swing.JPanel();
        jPanelBaixas = new javax.swing.JPanel();

        jToggleButton2.setText("jToggleButton2");

        setClosable(true);
        setTitle(".:: Central de Boletas");
        setMaximumSize(new java.awt.Dimension(865, 592));
        setMinimumSize(new java.awt.Dimension(865, 592));
        setPreferredSize(new java.awt.Dimension(865, 592));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        jTabbedPaneBoletas.setMaximumSize(new java.awt.Dimension(836, 586));
        jTabbedPaneBoletas.setMinimumSize(new java.awt.Dimension(799, 544));
        jTabbedPaneBoletas.setPreferredSize(new java.awt.Dimension(799, 544));

        jPanel1.setMaximumSize(new java.awt.Dimension(801, 513));
        jPanel1.setMinimumSize(new java.awt.Dimension(801, 513));
        jPanel1.setPreferredSize(new java.awt.Dimension(801, 513));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setMaximumSize(new java.awt.Dimension(232, 501));
        jPanel2.setMinimumSize(new java.awt.Dimension(232, 501));
        jPanel2.setPreferredSize(new java.awt.Dimension(232, 501));

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

        jLabel8.setText("Vencimento Boleto");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8)
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel15Layout.createSequentialGroup()
                    .addComponent(VencBoleto, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel8)
                .addContainerGap(45, Short.MAX_VALUE))
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel15Layout.createSequentialGroup()
                    .addGap(33, 33, 33)
                    .addComponent(VencBoleto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(19, Short.MAX_VALUE)))
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
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jemdia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jatrasados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtodos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jperiodo))
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jInicial, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
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
                    .addComponent(jContrato, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
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
        jGerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        jGerar.setText("Gerar Boletos");
        jGerar.setToolTipText("");
        jGerar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jGerar.setIconTextGap(5);
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_Status, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jListar, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jListar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jGerar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(lbl_Status, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ListaBancosPessoas.setBackground(new java.awt.Color(255, 255, 255));
        ListaBancosPessoas.setMaximumSize(new java.awt.Dimension(592, 300));
        ListaBancosPessoas.setMinimumSize(new java.awt.Dimension(592, 300));
        ListaBancosPessoas.setName(""); // NOI18N
        ListaBancosPessoas.setPreferredSize(new java.awt.Dimension(592, 300));

        ListaBancosPessoasImpressas.setBackground(new java.awt.Color(255, 255, 255));
        ListaBancosPessoasImpressas.setMaximumSize(new java.awt.Dimension(592, 153));
        ListaBancosPessoasImpressas.setMinimumSize(new java.awt.Dimension(592, 153));
        ListaBancosPessoasImpressas.setPreferredSize(new java.awt.Dimension(592, 153));

        jLabel6.setBackground(new java.awt.Color(153, 204, 255));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Asp.net_0031_color1_16x16.gif"))); // NOI18N
        jLabel6.setText("Boletos já impressos, para reimprimi-los ultilize a função de 2ª via no menu de Relatórios");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel6.setIconTextGap(8);
        jLabel6.setOpaque(true);

        jLabel7.setBackground(new java.awt.Color(153, 204, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/plus.png"))); // NOI18N
        jLabel7.setText("Selecione na Lista os locatários para impressão do boleto.");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel7.setIconTextGap(8);
        jLabel7.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ListaBancosPessoas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ListaBancosPessoasImpressas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(ListaBancosPessoasImpressas, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ListaBancosPessoas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPaneBoletas.addTab("Geração", jPanel1);

        jPanel5.setMaximumSize(new java.awt.Dimension(801, 513));
        jPanel5.setMinimumSize(new java.awt.Dimension(801, 513));
        jPanel5.setPreferredSize(new java.awt.Dimension(801, 513));

        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel7.setMaximumSize(new java.awt.Dimension(829, 276));
        jPanel7.setMinimumSize(new java.awt.Dimension(829, 276));
        jPanel7.setPreferredSize(new java.awt.Dimension(829, 276));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Remessa(s)"));
        jPanel8.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jPanel8.setMaximumSize(new java.awt.Dimension(813, 260));
        jPanel8.setMinimumSize(new java.awt.Dimension(813, 260));
        jPanel8.setPreferredSize(new java.awt.Dimension(813, 260));

        jScrollPane3.setMaximumSize(new java.awt.Dimension(791, 225));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(791, 225));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(791, 225));

        tblBancosRemessa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Banco", "Nome Banco", "Lote", "Remessa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBancosRemessa.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblBancosRemessa);
        if (tblBancosRemessa.getColumnModel().getColumnCount() > 0) {
            tblBancosRemessa.getColumnModel().getColumn(0).setResizable(false);
            tblBancosRemessa.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblBancosRemessa.getColumnModel().getColumn(1).setResizable(false);
        }

        jLabel4.setText("jLabel4");

        rDownload.setText("Download");
        rDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rDownloadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(rDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ListaBancosPessoasRemessa.setMaximumSize(new java.awt.Dimension(661, 219));
        ListaBancosPessoasRemessa.setMinimumSize(new java.awt.Dimension(661, 219));
        ListaBancosPessoasRemessa.setPreferredSize(new java.awt.Dimension(661, 219));

        jPanel14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel14.setMaximumSize(new java.awt.Dimension(162, 219));
        jPanel14.setMinimumSize(new java.awt.Dimension(162, 219));
        jPanel14.setPreferredSize(new java.awt.Dimension(162, 219));

        jListarRemessa.setFont(new java.awt.Font("Dialog", 1, 9)); // NOI18N
        jListarRemessa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/previous.png"))); // NOI18N
        jListarRemessa.setText("Listar Boletos");
        jListarRemessa.setActionCommand("Listar Boletas");
        jListarRemessa.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jListarRemessa.setIconTextGap(5);
        jListarRemessa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jListarRemessaActionPerformed(evt);
            }
        });

        btnGerarRemessa.setText("Gerar Remessa");
        btnGerarRemessa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarRemessaActionPerformed(evt);
            }
        });

        jProgressRemessa.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jProgressRemessa.setStringPainted(true);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jListarRemessa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressRemessa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGerarRemessa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jListarRemessa, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGerarRemessa, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressRemessa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(ListaBancosPessoasRemessa, javax.swing.GroupLayout.PREFERRED_SIZE, 661, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ListaBancosPessoasRemessa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneBoletas.addTab("Remessa", jPanel5);

        jPanel9.setMaximumSize(new java.awt.Dimension(841, 513));
        jPanel9.setMinimumSize(new java.awt.Dimension(841, 513));

        tblEmails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Contrato", "Nome", "Vencimento", "Boleta", "nnumero", "FileName"
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
        tblEmails.setMaximumSize(new java.awt.Dimension(621, 263));
        tblEmails.setMinimumSize(new java.awt.Dimension(621, 263));
        tblEmails.setPreferredSize(new java.awt.Dimension(621, 263));
        jScrollPane5.setViewportView(tblEmails);

        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel10.setMaximumSize(new java.awt.Dimension(202, 263));
        jPanel10.setMinimumSize(new java.awt.Dimension(202, 263));
        jPanel10.setOpaque(false);

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnListarBoletasEmail)
                    .addComponent(jSemEnvio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviarTodos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviarSelecao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarCadastro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGap(30, 30, 30)
                .addComponent(jProgressEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel11.setMaximumSize(new java.awt.Dimension(829, 231));
        jPanel11.setMinimumSize(new java.awt.Dimension(829, 231));
        jPanel11.setPreferredSize(new java.awt.Dimension(829, 231));

        jLabel5.setText("Assunto:");

        jMensagem.setContentType("\"text/html\""); // NOI18N
        jMensagem.setMaximumSize(new java.awt.Dimension(813, 160));
        jMensagem.setMinimumSize(new java.awt.Dimension(813, 160));
        jMensagem.setPreferredSize(new java.awt.Dimension(813, 160));
        jScrollPane4.setViewportView(jMensagem);

        jLabel9.setBackground(new java.awt.Color(204, 204, 255));
        jLabel9.setText("Mensagem:");
        jLabel9.setOpaque(true);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 813, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 813, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edtSubJect, javax.swing.GroupLayout.PREFERRED_SIZE, 761, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPaneBoletas.addTab("E-Mail", jPanel9);

        jPanelConsultas.setMaximumSize(new java.awt.Dimension(801, 513));
        jPanelConsultas.setMinimumSize(new java.awt.Dimension(801, 513));
        jPanelConsultas.setPreferredSize(new java.awt.Dimension(801, 513));

        javax.swing.GroupLayout jPanelConsultasLayout = new javax.swing.GroupLayout(jPanelConsultas);
        jPanelConsultas.setLayout(jPanelConsultasLayout);
        jPanelConsultasLayout.setHorizontalGroup(
            jPanelConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 841, Short.MAX_VALUE)
        );
        jPanelConsultasLayout.setVerticalGroup(
            jPanelConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
        );

        jTabbedPaneBoletas.addTab("Consultas", jPanelConsultas);

        jPanelBaixas.setMaximumSize(new java.awt.Dimension(801, 513));
        jPanelBaixas.setMinimumSize(new java.awt.Dimension(801, 513));
        jPanelBaixas.setPreferredSize(new java.awt.Dimension(801, 513));

        javax.swing.GroupLayout jPanelBaixasLayout = new javax.swing.GroupLayout(jPanelBaixas);
        jPanelBaixas.setLayout(jPanelBaixasLayout);
        jPanelBaixasLayout.setHorizontalGroup(
            jPanelBaixasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 841, Short.MAX_VALUE)
        );
        jPanelBaixasLayout.setVerticalGroup(
            jPanelBaixasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
        );

        jTabbedPaneBoletas.addTab("Baixas", jPanelBaixas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPaneBoletas, javax.swing.GroupLayout.DEFAULT_SIZE, 841, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPaneBoletas, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPaneBoletas.getAccessibleContext().setAccessibleName("Consultas");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListarRemessaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jListarRemessaActionPerformed
        lbl_Status.setText("Criando Lista...");
        jProgressRemessa.setValue(0);

        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " +
        "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome, r.dtvencbol FROM recibo r, locatarios l, carteira c, bancos b " +
        "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null or Trim(l.fiador1uf) = '') AND (r.contrato = l.contrato and c.contrato = " +
        "l.contrato) AND (b.codigo = l.bcobol) AND r.remessa = 'N' AND (r.nnumero is not null or r.nnumero <> '') and " +
        "year(r.dtvencimento) <= '" + AnoRef.getValue() + "' ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);

        bancosBoletaRemessa = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaRemessa = new ArrayList<PessoasBoleta>();

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
        String tvencbol = null;
        int tpenvio = 0;
        int rcount = 0;
        try {
            String oldBco = ""; String oldBcoNome = "";
            rcount = conn.RecordCount(rs);
            while (rs.next()) {
                trgprp = String.valueOf(rs.getInt("rgprp"));
                trgimv = String.valueOf(rs.getInt("rgimv"));
                tcontrato = rs.getString("contrato").toUpperCase();
                tnome = rs.getString("nomerazao").trim();
                tnnumero = rs.getString("nnumero");
                tvencto = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(rs.getString("dtvencimento").toUpperCase(),"yyyy-MM-dd"));

                tvencbol = rs.getString("dtvencbol");
                if (tvencbol != null) tvencbol = Dates.DateFormata("dd-MM-yyyy", Dates.StringtoDate(tvencbol.toUpperCase(),"yyyy-MM-dd"));

                tnnumero = rs.getString("nnumero");
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";

                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoletaRemessa.isEmpty()) bancosBoletaRemessa.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaRemessa, new Boolean(false)));
                    pessoasBoletaRemessa = new ArrayList<PessoasBoleta>();
                }

                String DataBoleto = "";
                //if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());
                if (tvencbol != null) DataBoleto = tvencbol;
                pessoasBoletaRemessa.add(new PessoasBoleta(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false), tnnumero));

                oldBco = tbcobol; oldBcoNome = tbcobolnome;

                int pgs = ((b++ * 100) / rcount) + 1;
                jProgressRemessa.setValue(pgs);
            }
        } catch (SQLException ex) {}
        conn.CloseTable(rs);

        int pgs = 100;
        try {
            pgs = ((b++ * 100) / rcount) + 1;
        } catch (Exception e) {}
        jProgressRemessa.setValue(pgs);

        if (!pessoasBoletaRemessa.isEmpty()) {
            bancosBoletaRemessa.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaRemessa, new Boolean(false)));
        }

        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoletaRemessa);
            treeTableRemessa = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParametersRemessa(treeTableRemessa);
            ListaBancosPessoasRemessa.setViewportView(treeTableRemessa);
            ListaBancosPessoasRemessa.repaint();
        }
        
        {
            TableControl.header(tblBancosRemessa, new String[][] {{"Banco","Nome Banco","Lote","Remessa"},{"80","120","100","100"}});
            
            for (BancosBoleta os : bancosBoletaRemessa) {
                String _banco = os.getBanco();
                int lotenr = 0; try { lotenr = Integer.valueOf(conn.ReadParameters("LOTE_" + _banco)); } catch (Exception e) {}
                
                TableControl.add(tblBancosRemessa, new String[][]{{os.getBanco(), os.getNomeBanco(), String.valueOf(lotenr), ""},{"C","L","C","C"}}, true);
            }
        }

        lbl_Status.setText("");
        jProgressRemessa.setValue(0);
    }//GEN-LAST:event_jListarRemessaActionPerformed

    private void jGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGerarActionPerformed
        if (!AnalisaGeracao()) return;

        jGerar.setEnabled(false);
        bancos bco = new bancos(conn);
        
        String rgprp;
        String rgimv;
        String contrato = null;
        String vencto = null;
        String vectoBol = null;

        // Limpa lista de email
        for (int b = 0; b <= bancosBoleta.size() - 1; b++) {
            for (PessoasBoleta p : bancosBoleta.get(b).getPessoasBoleta()) {
                if (p.getTag()) {
                    rgprp = p.getRgprp();
                    rgimv = p.getRgimv();
                    contrato = p.getContrato();
                    vencto = p.getVencimentoRec();
                    vectoBol = p.getVencimentoBol();
                    if (vectoBol != null) {
                        if (vectoBol.trim().equalsIgnoreCase("")) vectoBol = null;
                    }

                    Boleta Bean1 = null;
                    try {
                        Bean1 = CreateBoleta(rgprp, rgimv, contrato, vencto, vectoBol, null);

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
                            Bean1.getbolDadosVrdoc(),
                            Bean1.getbolDadosNnumero(),
                            "N"
                        });
                        try {
                            if (conn.ExisteTabelaBloquetos()) conn.CommandExecute(cSql);
                        } catch (Exception e) {e.printStackTrace();}

                        List<Boleta> lista = new ArrayList<Boleta>();
                        lista.add(Bean1);

                        JRDataSource jrds = new JRBeanCollectionDataSource(lista);
                        try {
                            String fileName = "reports/Boletos.jasper";
                            JasperPrint print = JasperFillManager.fillReport(fileName, null, jrds);

                            // Create a PDF exporter
                            JRExporter exporter = new JRPdfExporter();

                            new jDirectory("reports/Boletas/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
                            String pathName = "reports/Boletas/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/";

                            // Configure the exporter (set output file name and print object)
                            String outFileName = pathName + contrato + "_" + Bean1.getsacDadosNome() + "_" + vencto + "_" + Bean1.getbolDadosNnumero().substring((bco.getBanco().equalsIgnoreCase("341") ? 4 : 3)) + ".pdf";
                            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

                            try {
                                String recUpdSql = "UPDATE recibo SET boletapath = '" + outFileName + "' WHERE contrato = '" + contrato + "' AND " +
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
                    } catch (Exception e)  { e.printStackTrace(); }
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Gerado com sucesso.");
        jListar.doClick();

        jGerar.setEnabled(true);
    }//GEN-LAST:event_jGerarActionPerformed

    private void jListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jListarActionPerformed
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

    private void btnGerarRemessaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarRemessaActionPerformed
        for (BancosBoleta os : bancosBoletaRemessa) {
            String _banco = os.getBanco();
            int pos = TableControl.seek(tblBancosRemessa, 0, _banco);
            String nrlote = tblBancosRemessa.getValueAt(pos, 2).toString();
                
            String[][] relacao = {};
            for (PessoasBoleta p : os.getPessoasBoleta()) {
                if (p.getTag()) {
                    Object[][] vCampos = null;
                    try {
                        vCampos = conn.ReadFieldsTable(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "r.nnumero"}, "locatarios l, imoveis i, proprietarios p, recibo r", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp AND r.contrato = l.contrato) AND l.contrato = '" + p.getContrato() + "'");
                    } catch (SQLException ex) { }

                    String _rgprp = p.getRgprp();
                    String _rgimv = p.getRgimv();
                    String _contrato = p.getContrato();
                    String _vencto = p.getVencimentoRec();
                    String _rnnumero = p.getNnumero();
                    String _dataBol = p.getVencimentoBol();
                    if (_dataBol.equalsIgnoreCase("")) _dataBol = null;

                    Boleta bean1 = new Boleta();                
                    try { bean1 = CreateBoletaRemessa(_rgprp, _rgimv, _contrato, _vencto, _rnnumero, _dataBol); } catch (Exception ex) { }

                    relacao = FuncoesGlobais.ArraysAdd(relacao, new String[] {_contrato, bean1.getbolDadosVencimento(), bean1.getbolDadosVrdoc(), FuncoesGlobais.StrZero(bean1.getbolDadosNnumero().substring(3).trim(),13)});
                }
            }

            String operacao = "01"; String sRemessa = "";
            if (_banco.equalsIgnoreCase("033")) {
                // Colocado aqui para testar 07-12-2016 12h41m
                if (relacao.length > 0) sRemessa = new Bancos.Santander().Remessa(nrlote, "", operacao, relacao, ""); 
            } else if (_banco.equalsIgnoreCase("341")) {
                if (relacao.length > 0) sRemessa = new Bancos.itau().Remessa(nrlote, "341", operacao, relacao, "");
            }
            
            if (relacao.length > 0) {
                tblBancosRemessa.setValueAt(sRemessa, pos, 3);

                // Atualiza numero do lote
                nrlote = String.valueOf(Integer.valueOf(nrlote) + 1);
                try{ conn.SaveParameters(new String[] {"LOTE_" + _banco, nrlote, "NUMERICO"}); } catch (SQLException ex) {}
            }    
        }
        JOptionPane.showMessageDialog(this, "Remessa(s) geradas com sucesso.\n" + 
                "Transmita as remessas abaixo para seu respectivo banco.\n\n" + 
                "Antes de fechar esta mensagem, anote o(s) número(s) da(s) remessa(s).");
        jListarRemessa.doClick();
    }//GEN-LAST:event_btnGerarRemessaActionPerformed

    private void btnListarBoletasEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarBoletasEmailActionPerformed
        String Sql = "SELECT r.rgprp, r.rgimv, r.contrato, l.nomerazao, r.campo, r.dtvencimento, r.nnumero, c.dtultrecebimento, " + 
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome, r.dtvencbol, r.boletapath FROM recibo r, locatarios l, carteira c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null or Trim(l.fiador1uf) = '') AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) AND (r.remessa = 'S' AND r.emailbol = 'N' AND l.envio = '1') AND (b.codigo = l.bcobol) ORDER BY l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);

        TableControl.header(tblEmails, new String[][] {{"contrato","nome","vencimento","Boleta","nnumero","fileName","Status"},{"60","200","70","70","120","0","30"}});
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
        try {htmlText = jMensagem.getDocument().getText(0, jMensagem.getDocument().getEndPosition().getOffset());} catch (BadLocationException ba) {}
        if (htmlText.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "O campo Mensagem não pode ser vazio!");
            btnEnviarTodos.setEnabled(true);
            jMensagem.requestFocus();
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
                //Outlook email = new Outlook();
                try {            
                    String To = EmailLoca.trim().toLowerCase();
                    String Subject = edtSubJect.getText().trim();
                    String Body = jMensagem.getDocument().getText(0,jMensagem.getDocument().getLength());
                    String[] Attachments = attachMent;
                    
                    Gmail service = GmailAPI.getGmailService();
                    MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(System.getProperty("user.dir") + "/" + filename));
                    Message message = createMessageWithEmail(Mimemessage);
                    message = service.users().messages().send("me", message).execute();

                    System.out.println("Message id: " + message.getId());
                    System.out.println(message.toPrettyString());
                    if (message.getId() != null) {
                        conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                            Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                        JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                    }
                    tblEmails.getModel().setValueAt(message.getId() != null ? "Ok" : "Err", modelRow, 6);
                        
//                    email.Send(To, null, Subject, Body, Attachments);
//                    if (!email.isSend()) {
//                        JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
//                    } else {
//                        conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
//                            Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
//                        JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
//                    }
//                    tblEmails.getModel().setValueAt(email.isSend() ? "Ok" : "Err", modelRow, 6);
                } catch (HeadlessException | IOException | GeneralSecurityException | MessagingException | BadLocationException ex) {
                    ex.printStackTrace();
                //} finally {
                //    email = null;
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
        try {htmlText = jMensagem.getDocument().getText(0, jMensagem.getDocument().getEndPosition().getOffset());} catch (BadLocationException ba) {}
        if (htmlText.trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "O campo Mensagem não pode ser vazio!");
            btnEnviarSelecao.setEnabled(true);
            jMensagem.requestFocus();
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
                //Outlook email = new Outlook();
                try {            
                    String To = EmailLoca.trim().toLowerCase();
                    String Subject = edtSubJect.getText().trim();
                    String Body = jMensagem.getDocument().getText(0,jMensagem.getDocument().getLength());
                    String[] Attachments = attachMent;
                    
                    Gmail service = GmailAPI.getGmailService();
                    MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(System.getProperty("user.dir") + "/" + filename));
                    Message message = createMessageWithEmail(Mimemessage);
                    message = service.users().messages().send("me", message).execute();

                    System.out.println("Message id: " + message.getId());
                    System.out.println(message.toPrettyString());
                    if (message.getId() != null) {
                        conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
                            Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
                        JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                    }
                    tblEmails.getModel().setValueAt(message.getId() != null ? "Ok" : "Err", modelRow, 6);
                    
//                    email.Send(To, null, Subject, Body, Attachments);
//                    if (!email.isSend()) {
//                        JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
//                    } else {
//                        conn.CommandExecute("UPDATE recibo SET emailbol = 'S' WHERE contrato = '" + contrato + "' AND dtvencimento = '" +
//                            Dates.StringtoString(vencto, "dd/MM/yyyy", "yyyy/MM/dd") + "';");
//                        JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
//                    }
//                    tblEmails.getModel().setValueAt(email.isSend() ? "Ok" : "Err", modelRow, 6);
                } catch (HeadlessException | IOException | GeneralSecurityException | MessagingException | BadLocationException ex) {
                    ex.printStackTrace();
                //} finally {
                //    email = null;
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

  
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        
    }//GEN-LAST:event_formMousePressed

    private void rDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rDownloadActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        disableLookInComboBox(fileChooser);
        fileChooser.setControlButtonsAreShown(false);
        fileChooser.setFileHidingEnabled(true);

        // Defina o diretório inicial
        File initialDirectory = new File(VariaveisGlobais.REMESSA_PATH);
        fileChooser.setCurrentDirectory(initialDirectory);

        fileChooser.setDialogTitle("Download de arquivos.");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Arquivo selecionado: " + selectedFile.getAbsolutePath());
        }                
    }//GEN-LAST:event_rDownloadActionPerformed

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
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM recibo r, locatarios l, carteira c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null or l.fiador1uf = '') AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento >= '" + 
                     Dates.DateFormata("yyyy-MM-dd", iDataRef) + 
                     "' and r.dtvencimento <= '" + Dates.DateFormata("yyyy-MM-dd", new Date(iAnoRef - 1900, FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString()), this.dmonth[FuncoesGlobais.IndexOf(this.month, this.MesRef.getValue().toString())])) + "') AND (Year(r.dtvencimento) = " + 
                     AnoRef.getValue() + ") " + sContrato + " AND (b.codigo = l.bcobol) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
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
        int tpenvio = 0;
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
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
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
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
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
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM recibo r, locatarios l, carteira c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null or l.fiador1uf = '') AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento < '" + 
                     Dates.DateFormata("yyyy-MM-dd", new Date()) + "') AND (Year(r.dtvencimento) = " + 
                     AnoRef.getValue() + ") " + sContrato + " AND (b.codigo = l.bcobol) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
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
        int tpenvio = 0;
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
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
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
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
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
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM recibo r, locatarios l, carteira c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null or l.fiador1uf = '') AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) " + sContrato + " AND (b.codigo = l.bcobol) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
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
        int tpenvio = 0;
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
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
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
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
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
                     "l.boleta, 1 gerados, l.envio, l.bcobol, b.nome as bconome FROM recibo r, locatarios l, carteira c, bancos b " + 
                     "where l.boleta = -1 AND (r.tag <> 'X') AND (l.fiador1uf is null or Trim(l.fiador1uf) = '') AND (r.contrato = l.contrato and c.contrato = " + 
                     "l.contrato) and (r.dtvencimento >= '" + 
                     Dates.DateFormata("yyyy-MM-dd", jInicial.getDate()) + "' AND r.dtvencimento <= '" + 
                     Dates.DateFormata("yyyy-MM-dd", jFinal.getDate()) + "') " + 
                     sContrato + " AND (b.codigo = l.bcobol) ORDER BY l.bcobol, l.nomerazao;";
        ResultSet rs = conn.OpenTable(Sql, null);
        
        bancosBoleta = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoleta = new ArrayList<PessoasBoleta>();

        // Boletos Printed
        List<BancosBoleta> bancosBoletaPrinted = new ArrayList<BancosBoleta>();
        List<PessoasBoleta> pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
        
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
        int tpenvio = 0;
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
                tbcobol = rs.getString("bcobol");
                tbcobolnome = rs.getString("bconome");
                tpenvio = rs.getInt("envio");
                String tenvio = "";
                if (tpenvio == 0) tenvio = "EM MÃOS";
                if (tpenvio == 1) tenvio = "EMAIL";
                if (tpenvio == 2) tenvio = "CORREIO";
                
                if (oldBco.equalsIgnoreCase("")) { oldBco = tbcobol; oldBcoNome = tbcobolnome; }
                if (!oldBco.equalsIgnoreCase(tbcobol)) {
                    if (!pessoasBoleta.isEmpty()) bancosBoleta.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoleta, new Boolean(false)));
                    if (!pessoasBoletaPrinted.isEmpty()) bancosBoletaPrinted.add(new BancosBoleta(oldBco, oldBcoNome, pessoasBoletaPrinted));
                    pessoasBoleta = new ArrayList<PessoasBoleta>();
                    pessoasBoletaPrinted = new ArrayList<PessoasBoleta>();
                }
                
                String DataBoleto = "";
                if (Dates.DateDiff(Dates.DIA, Dates.StringtoDate(tvencto, "dd/MM/yyyy"), new Date()) > 0) DataBoleto = Dates.DateFormata("dd/MM/yyyy", VencBoleto.getDate());                
                if (tnnumero == null || "".equals(tnnumero)) {
                    pessoasBoleta.add(new PessoasBoleta(tcontrato, tnome, tvencto, DataBoleto, tenvio, trgprp, trgimv, new Boolean(false)));        
                } else {
                    pessoasBoletaPrinted.add(new PessoasBoleta(tcontrato, tnome, tvencto, tnnumero, tenvio, trgprp, trgimv, new Boolean(false)));        
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
            bancosBoleta.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoleta, new Boolean(false)));
        }      
        if (!pessoasBoletaPrinted.isEmpty()) {
            bancosBoletaPrinted.add(new BancosBoleta(tbcobol, tbcobolnome, pessoasBoletaPrinted));
        }               
        
        // Boletas não impressas
        {
            BoletaTreeTableModel boletaTreeTableModel = new BoletaTreeTableModel(bancosBoleta);
            treeTable = new JXTreeTable(boletaTreeTableModel);
            SetDisplayParameters(treeTable);
            ListaBancosPessoas.setViewportView(treeTable);
            ListaBancosPessoas.repaint();
        }

        // Boletas impressas
        {
            BoletaTreeTableModel boletaTreeTableModelPrinted = new BoletaTreeTableModel(bancosBoletaPrinted);
            treeTablePrinted = new JXTreeTable(boletaTreeTableModelPrinted);       
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
    
    private void SetDisplayParametersRemessa(JXTreeTable table) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRootVisible(false);
            table.setBounds(0, 0, ListaBancosPessoasRemessa.getWidth(), ListaBancosPessoasRemessa.getHeight());
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
    
    private boolean AnalisaGeracao() {
        boolean retorno = true; boolean selecao = false;
        if (bancosBoleta != null) {
            for (int b = 0; b <= bancosBoleta.size() - 1; b++) {
                for (PessoasBoleta p : bancosBoleta.get(b).getPessoasBoleta()) {
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
            String tmesanor = campos[3][3].toString(); if (tmesanor == null) tmesanor = "";
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
    
    private Boleta CreateBoletaRemessa(String rgprp, String rgimv, String contrato, String vencto, String rnnumero, String dataBol) throws SQLException {
        new Bancos.bancos(conn).LerBanco(contrato);
        bancos bco = new bancos(conn);
        
        Collections gVar = VariaveisGlobais.dCliente;

        Boleta bean1 = new Boleta();
        bean1.setempNome(gVar.get("empresa").toUpperCase().trim());
        bean1.setempEndL1(gVar.get("endereco") + ", " + gVar.get("numero") + gVar.get("complemento") + " - " + gVar.get("bairro"));
        bean1.setempEndL2(gVar.get("cidade") + " - " + gVar.get("estado") + " - CEP " + gVar.get("cep"));
        bean1.setempEndL3("Tel.: " + gVar.get("telefone"));
        bean1.setempEndL4(gVar.get("hpage") + " / " + gVar.get("email"));

        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/boleta/" + VariaveisGlobais.icoBoleta);

        // 28-02-2018 - Reimpressão com nova data de boleta
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
        } catch (Exception ex) {}

        // 13-11-2017 - Reimpressão com outro vencimento
        if (dataBol != null) {
            bean1.setbolDadosVencimento(dataBol);
        } else {
            bean1.setbolDadosVencimento(vencto);
        }

        String cValor = bco.Valor4Boleta(LerValor.floatToCurrency(tRecibo,2));  // valor da boleta
        bean1.setbolDadosVrdoc(df.format(tRecibo));

        String banco = (bco.getBanco().equalsIgnoreCase("104") ? "cef" : (bco.getBanco().equalsIgnoreCase("341") ? "itau" : "santander"));
        String nNumero = "";
        
        String showNossoNumero = ""; String showCarteira = "";

        bean1.setbolDadosNnumero(rnnumero); //showNossoNumero);
        bean1.setbolDadosNumdoc(rgprp + "/" + contrato);

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

        if (banco.toLowerCase().equalsIgnoreCase("itau")) {
            bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
            bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NA IMOBILIARIA.");
            bean1.setbolDadosEspeciedoc("RC");
        } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
            bean1.setbcoMsgL01("PAGAR PREFERENCIALMENTE NAS CASAS LOTERICAS ATE O VALOR LIMITE");
            bean1.setbcoMsgL02("");
            bean1.setbolDadosEspeciedoc("RC");
        } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
            bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
            bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NA IMOBILIARIA.");
            bean1.setbolDadosEspeciedoc("DM");
        }

        bean1.setbolDadosCedente(gVar.get("empresa").toUpperCase() + "   - CNPJ: " + gVar.get("cnpj"));
        bean1.setbolDadosDatadoc(Dates.DatetoString(new Date()));
        bean1.setbolDadosAceite("N");
        bean1.setbolDadosDtproc(Dates.DatetoString(new Date()));
        bean1.setbolDadosUsobco("");
        bean1.setbolDadosCarteira(showCarteira);
        bean1.setbolDadosEspecie("R$");
        bean1.setbolDadosQtde("");
        bean1.setbolDadosValor("");
 
        String msgBol01 = conn.ReadParameters("MSGBOL1"); if (msgBol01 == null) msgBol01 = "";
        String msgBol02 = conn.ReadParameters("MSGBOL2"); if (msgBol02 == null) msgBol02 = "";
        String msgBol03 = conn.ReadParameters("MSGBOL3"); if (msgBol03 == null) msgBol03 = "";
        String msgBol04 = conn.ReadParameters("MSGBOL4"); if (msgBol04 == null) msgBol04 = "";
        String msgBol05 = conn.ReadParameters("MSGBOL5"); if (msgBol05 == null) msgBol05 = "";
        String msgBol06 = conn.ReadParameters("MSGBOL6"); if (msgBol06 == null) msgBol06 = "";
        String msgBol07 = conn.ReadParameters("MSGBOL7"); if (msgBol07 == null) msgBol07 = "";
        String msgBol08 = conn.ReadParameters("MSGBOL8"); if (msgBol08 == null) msgBol08 = "";
        String msgBol09 = conn.ReadParameters("MSGBOL9"); if (msgBol09 == null) msgBol09 = "";

        bean1.setbolDadosMsg01(msgBol01);
        bean1.setbolDadosMsg02(msgBol02);
        bean1.setbolDadosMsg03(msgBol03);
        bean1.setbolDadosMsg04(msgBol04);
        bean1.setbolDadosMsg05(msgBol05);
        bean1.setbolDadosMsg06(msgBol06);
        bean1.setbolDadosMsg07(msgBol07);

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}
        Date tvecto = Dates.StringtoDate(dataBol != null ? dataBol : vencto,"dd/MM/yyyy");
        String carVecto = Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, (int)rc.dia_mul, tvecto));

        String ln08 = "";
        if ("".equals(msgBol08)) {
            ln08 = "APÓS O DIA " + carVecto + " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.";
        } else {
            // [VENCIMENTO] - Mostra Vencimento
            // [CARENCIA] - Mostra Vencimento + Carencia
            // [MULTA] - Mostra Juros
            // [ENCARGOS] - Mostra Encargos
            ln08 = msgBol08.replace("[VENCIMENTO]", Dates.DateFormata("dd/MM/yyyy", tvecto));
            ln08 = ln08.replace("[CARENCIA]", carVecto);
            ln08 = ln08.replace("[MULTA]", String.valueOf(rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", "") + "%");
            ln08 = ln08.replace("[ENCARGOS]", "0,333%");
        }
        bean1.setbolDadosMsg08(ln08);
        //bean1.setbolDadosMsg08("".equals(msgBol08) ? "APÓS O DIA " + carVecto + 
        //    " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO." : "" + msgBol08);
        
        bean1.setbolDadosMsg09("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);

        return bean1;

    }

    public Boleta CreateBoleta(String rgprp, String rgimv, String contrato, String vencto, String dataBol, String[] dadosAdicionais) throws SQLException {
        new Bancos.bancos(conn).LerBanco(contrato);
        bancos bco = new bancos(conn);
        
        Collections gVar = VariaveisGlobais.dCliente;

        Boleta bean1 = new Boleta();
        bean1.setempNome(gVar.get("empresa").toUpperCase().trim());
        bean1.setempEndL1(gVar.get("endereco") + ", " + gVar.get("numero") + gVar.get("complemento") + " - " + gVar.get("bairro"));
        bean1.setempEndL2(gVar.get("cidade") + " - " + gVar.get("estado") + " - CEP " + gVar.get("cep"));
        bean1.setempEndL3("Tel.: " + gVar.get("telefone"));
        bean1.setempEndL4(gVar.get("hpage") + " / " + gVar.get("email"));

        // Logo da Imobiliaria
        bean1.setlogoLocation("resources/logos/boleta/" + VariaveisGlobais.icoBoleta);

        Object[][] msgboleta = null;
        try {
            String[] fields = new String[] {"msgboleta","dtnasc"};
            msgboleta = conn.ReadFieldsTable(fields, "locatarios", "contrato = :contrato", new Object[][] {{"string", "contrato", contrato}});
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
                if (!msgboleta[1][3].toString().isEmpty()) {
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

        // Logo do Banco
        bean1.setlogoBanco(bco.getLogo());
        
        bean1.setnumeroBanco(bco.getBanco() + "-" + bco.getBancoDv());

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

        if (dataBol != null) {
            bean1.setbolDadosVencimento(dataBol);
        } else {
            bean1.setbolDadosVencimento(vencto);
        }
        
        String cValor = bco.Valor4Boleta(LerValor.floatToCurrency(tRecibo,2));  // valor da boleta
        bean1.setbolDadosVrdoc(df.format(tRecibo));

        String banco = "";
        if (bco.getBanco().equalsIgnoreCase("104")) {
            banco = "cef";
        } else if (bco.getBanco().equalsIgnoreCase("341")) {
            banco = "itau";
        } else if (bco.getBanco().equalsIgnoreCase("033")) {
            banco = "santander";
        } else if (bco.getBanco().equalsIgnoreCase("001")) {
            banco = "bb";
        } else if (bco.getBanco().equalsIgnoreCase("237")) {
            banco = "bd";
        } else banco = "";
        
        String nNumero = "";
        String showNossoNumero = ""; 
        String dtgeracao = null;
        if (dadosAdicionais == null) {
            nNumero = FuncoesGlobais.StrZero(bco.getNnumero().trim(),11); 
            //int innumero = Integer.valueOf(npart2) + 1;
            double innumero = Double.valueOf(nNumero) + 1;
            //String snnumero = String.valueOf(npart1 + FuncoesGlobais.StrZero(String.valueOf(innumero),5));
            //if (Double.valueOf(snnumero) != 0) {
            if (innumero != 0) {
                //bco.GravarNnumero(bco.getBanco(), snnumero);
                bco.GravarNnumero(bco.getBanco(), String.valueOf(innumero));
            } else {
                JOptionPane.showMessageDialog(null, "Nosso numero não pode ser 0(ZERO)!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
                System.exit(1);
            }

            if (banco.equalsIgnoreCase("itau")) {
                nNumero =  new Bancos.itau().NossoNumeroItau(nNumero, 9);                    
            } else if (banco.equalsIgnoreCase("cef")) {
                nNumero =  new Bancos.CEF().NossoNumero(nNumero, 16);                                        
            } else if (banco.equalsIgnoreCase("santander")) {
                nNumero =  new Bancos.Santander().NossoNumero(nNumero, 13);
            } else if (banco.equalsIgnoreCase("bb")) {
                nNumero = new Bancos.bb().NossoNumeroBB(bco.getBancoDv(),nNumero);
            } else if (banco.equalsIgnoreCase("bd")) {
                nNumero = new Bancos.bradesco().NossoNumeroBradesco(nNumero.substring(0, 11),bco.getCarteira());
            }
            
            /***************************************************
             * Gravar Nosso Numero no Arquivo de RECIBO para
             * posterior baixa. e Avisar que remessa = 'N'
             */
            dtgeracao = Dates.DateFormata("dd-MM-yyyy", new Date());
            try {
                String recUpdSql = "UPDATE recibo SET nnumero = '" + bco.getBanco() + StringManager.Right(nNumero,12) + 
                                "', remessa = 'N', dtvencbol = "  + (dataBol == null ? Dates.StringtoString(dataBol, "dd/MM/yyyy","yyyy/MM/dd") + ", " : "'" + Dates.StringtoString(dataBol, "dd/MM/yyyy","yyyy/MM/dd") + "', ") + 
                                "emailbol = 'N', dtgeracao = '" + Dates.StringtoString(dtgeracao, "dd-MM-yyyy", "yyyy-MM-dd") + "' WHERE contrato = '" + contrato + "' AND " +
                                "dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd",
                                Dates.StringtoDate(vencto, "dd/MM/yyyy")) + "';";
                conn.CommandExecute(recUpdSql);
            } catch (Exception err) {err.printStackTrace();}
        } else {
            String _daNNumero = "0" + dadosAdicionais[0].toString();
            showNossoNumero = _daNNumero.substring(0, _daNNumero.length());
            nNumero = showNossoNumero;
            dtgeracao = dadosAdicionais[1].toString();
        }

        if (banco.toLowerCase().equalsIgnoreCase("itau")) {
            bean1.setbolDadosAgcodced(bco.getAgencia() + " / " + bco.getConta() + "-" + bco.getCtaDv());
        } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
            bean1.setbolDadosAgcodced(bco.getAgencia() + " / " + bco.getConta() + "-" + bco.getCtaDv());
        } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
            bean1.setbolDadosAgcodced(bco.getAgencia() + "-" + new Bancos.Santander().CalcDig11N(bco.getAgencia()) + " / " + bco.getCtaDv());
        } else if (banco.toLowerCase().equalsIgnoreCase("bb")) {
            bean1.setbolDadosAgcodced(FuncoesGlobais.StrZero(bco.getAgencia(),5) + "-" + new Bancos.bb().CalcDig11N(FuncoesGlobais.StrZero(bco.getAgencia(),5)) + " / " + 
                                      FuncoesGlobais.StrZero(bco.getCtaDv(),12) + "-" + new Bancos.bb().CalcDig11N(FuncoesGlobais.StrZero(bco.getCtaDv(),12)));
        } else if (banco.toLowerCase().equalsIgnoreCase("bd")) {
            bean1.setbolDadosAgcodced(bco.getAgencia() + 
                    "-" + 
                    new Bancos.bradesco().CalcDig11Bradesco(bco.getAgencia()) + 
                    " / " + 
                    FuncoesGlobais.StrZero(bco.getCtaDv(),7) + 
                    "-" + 
                    new Bancos.bradesco().CalcDig11Bradesco(bco.getCtaDv()) 
            );
        }

        String cdBarras = ""; String lnDig = "";
        if (banco.toLowerCase().equalsIgnoreCase("itau")) {
            cdBarras = new Bancos.itau().CodBar(dataBol != null ? dataBol : vencto, cValor,nNumero);
            lnDig = new Bancos.itau().LinhaDigitavel(cdBarras);  
        } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
            cdBarras = new Bancos.CEF().CodBar(dataBol != null ? dataBol : vencto,cValor,nNumero);
            lnDig = new Bancos.CEF().linhadigitavel(nNumero,dataBol != null ? dataBol : vencto,cValor);
        } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
            cdBarras = new Bancos.Santander().CodBar(dataBol != null ? dataBol : vencto, cValor, nNumero);
            lnDig = new Bancos.Santander().LinhaDigitavel(nNumero, cdBarras.substring(4, 5), dataBol != null ? dataBol : vencto, cValor);
        } else if (banco.toLowerCase().equalsIgnoreCase("bb")) {
            cdBarras = new Bancos.bb().CodBar(dataBol != null ? dataBol : vencto, cValor, nNumero.substring(0, 11));
            lnDig = new Bancos.bb().LinhaDigitavel(cdBarras, cdBarras.substring(4, 5), dataBol != null ? dataBol : vencto, cValor);
        } else if (banco.toLowerCase().equalsIgnoreCase("bd")) {
            cdBarras = new Bancos.bradesco().CodBar(dataBol != null ? dataBol : vencto, cValor, nNumero.substring(0, 11));
            lnDig = new Bancos.bradesco().LinhaDigitavel(cdBarras, dataBol != null ? dataBol : vencto, cValor);
        }
        
        String showCarteira = ""; 
        if (banco.equalsIgnoreCase("itau")) {
            showCarteira = bco.getCarteira();
            showNossoNumero = bco.getCarteira() + "/" + nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
        } else if (banco.equalsIgnoreCase("cef")) {
            showCarteira = "SR";
            showNossoNumero = bco.getCarteira() +  nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
        } else if (banco.equalsIgnoreCase("santander")) {
            showCarteira = bco.getCarteira();
            showNossoNumero = nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
        } else if (banco.equalsIgnoreCase("bb")) {
            showCarteira = bco.getCarteira();
            showNossoNumero = nNumero.substring(0, nNumero.length() -1) + "-" + nNumero.substring(nNumero.length() - 1, nNumero.length());
        } else if (banco.equalsIgnoreCase("bd")) {
            showCarteira = bco.getCarteira();
            showNossoNumero = bco.getCarteira() + "/" + nNumero.substring(0, 11) + "-" + nNumero.substring(11, 12);
        }
        
        bean1.setbolDadosNnumero(showNossoNumero);
        bean1.setbolDadosNumdoc(rgprp + "/" + contrato);

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

        bean1.setcodDadosDigitavel(lnDig);
        bean1.setcodDadosBarras(cdBarras);

        
        String msgCabBol01 = conn.ReadParameters("MSGCABBOL1"); if (msgCabBol01 == null) msgCabBol01 = "";
        String msgCabBol02 = conn.ReadParameters("MSGCABBOL2"); if (msgCabBol02 == null) msgCabBol02 = "";
        String msgCabBolDoc = conn.ReadParameters("MSGCABBOLDOC"); if (msgCabBolDoc == null) msgCabBolDoc = "";

        if (!msgCabBol01.isEmpty() && !msgCabBol02.isEmpty() && !msgCabBolDoc.isEmpty()) {
            bean1.setbcoMsgL01(msgCabBol01);
            bean1.setbcoMsgL02(msgCabBol02);
            bean1.setbolDadosEspeciedoc(msgCabBolDoc);
        } else {
            if (banco.toLowerCase().equalsIgnoreCase("itau")) {
                bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO ITAU..");
                bean1.setbolDadosEspeciedoc("RC");
            } else if (banco.toLowerCase().equalsIgnoreCase("cef")) {
                bean1.setbcoMsgL01("PAGAR PREFERENCIALMENTE NAS CASAS LOTERICAS ATE O VALOR LIMITE");
                bean1.setbcoMsgL02("");
                bean1.setbolDadosEspeciedoc("RC");
            } else if (banco.toLowerCase().equalsIgnoreCase("santander")) {
                bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO SANTANDER..");
                bean1.setbolDadosEspeciedoc("DM");
            } else if (banco.toLowerCase().equalsIgnoreCase("bb")) {
                bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO BANCO DO BRASIL..");
                bean1.setbolDadosEspeciedoc("RC");            
            } else if (banco.toLowerCase().equalsIgnoreCase("bd")) {
                bean1.setbcoMsgL01("ATÉ O VENCIMENTO, PAGAVEL EM QUALQUER BANCO OU PELA INTERNET.");
                bean1.setbcoMsgL02("APÓS O VENCIMENTO, SOMENTE NO BANCO BRADESCO S/A..");
                bean1.setbolDadosEspeciedoc("RC");            
            }
        }
        
        bean1.setbolDadosCedente(gVar.get("empresa").toUpperCase() + (VariaveisGlobais.ShowDocBoleta ? "   - CNPJ: " + gVar.get("cnpj") : ""));
        bean1.setbolDadosDatadoc(dtgeracao);
        bean1.setbolDadosAceite("N");
        bean1.setbolDadosDtproc(dtgeracao);
        bean1.setbolDadosUsobco("");
        bean1.setbolDadosCarteira(showCarteira);
        bean1.setbolDadosEspecie("R$");
        bean1.setbolDadosQtde("");
        bean1.setbolDadosValor("");
 
        String msgBol01 = conn.ReadParameters("MSGBOL1"); if (msgBol01 == null) msgBol01 = "";
        String msgBol02 = conn.ReadParameters("MSGBOL2"); if (msgBol02 == null) msgBol02 = "";
        String msgBol03 = conn.ReadParameters("MSGBOL3"); if (msgBol03 == null) msgBol03 = "";
        String msgBol04 = conn.ReadParameters("MSGBOL4"); if (msgBol04 == null) msgBol04 = "";
        String msgBol05 = conn.ReadParameters("MSGBOL5"); if (msgBol05 == null) msgBol05 = "";
        String msgBol06 = conn.ReadParameters("MSGBOL6"); if (msgBol06 == null) msgBol06 = "";
        String msgBol07 = conn.ReadParameters("MSGBOL7"); if (msgBol07 == null) msgBol07 = "";
        String msgBol08 = conn.ReadParameters("MSGBOL8"); if (msgBol08 == null) msgBol08 = "";
        String msgBol09 = conn.ReadParameters("MSGBOL9"); if (msgBol09 == null) msgBol09 = "";

        bean1.setbolDadosMsg01(msgBol01);
        bean1.setbolDadosMsg02(msgBol02);
        bean1.setbolDadosMsg03(msgBol03);
        bean1.setbolDadosMsg04(msgBol04);
        bean1.setbolDadosMsg05(msgBol05);
        bean1.setbolDadosMsg06(msgBol06);
        bean1.setbolDadosMsg07(msgBol07);

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}

        Date tvecto = Dates.StringtoDate(dataBol != null ? dataBol : vencto,"dd/MM/yyyy");
        String carVecto = Dates.DateFormata("dd/MM/yyyy", 
                        Dates.DateAdd(Dates.DIA, (int)rc.dia_mul, tvecto));

        String ln08 = "";
        if ("".equals(msgBol08)) {
            ln08 = "APÓS O DIA " + carVecto + " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.";
        } else {
            // [VENCIMENTO] - Mostra Vencimento
            // [CARENCIA] - Mostra Vencimento + Carencia
            // [MULTA] - Mostra Juros
            // [ENCARGOS] - Mostra Encargos
            ln08 = msgBol08.replace("[VENCIMENTO]", Dates.DateFormata("dd/MM/yyyy", tvecto));
            ln08 = ln08.replace("[CARENCIA]", carVecto);
            ln08 = ln08.replace("[MULTA]", String.valueOf(rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", "") + "%");
            ln08 = ln08.replace("[ENCARGOS]", "0,033%");
        }
        bean1.setbolDadosMsg08(ln08);
//        bean1.setbolDadosMsg08("".equals(msgBol08) ? "APÓS O DIA " + carVecto + 
//            " MULTA DE " + (rc.TipoImovel().equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com) + "% + ENCARGOS DE 0,333% AO DIA DE ATRASO." : "" + msgBol08);
        bean1.setbolDadosMsg09("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);

        bean1.setbolDadosDesconto("");
        bean1.setbolDadosMora("");
        bean1.setbolDadosVrcobrado("");

        return bean1;
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
                    linhas[i][1] = ("00/00".equals(Campo[3]) || "00/0000".equals(Campo[3]) || "".equals(Campo[3]) ? "-" : Campo[3]) ;
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
    private javax.swing.JScrollPane ListaBancosPessoas;
    private javax.swing.JScrollPane ListaBancosPessoasImpressas;
    private javax.swing.JScrollPane ListaBancosPessoasRemessa;
    private javax.swing.JSpinner MesRef;
    private com.toedter.calendar.JDateChooser VencBoleto;
    private javax.swing.JButton btnEditarCadastro;
    private javax.swing.JButton btnEnviarSelecao;
    private javax.swing.JButton btnEnviarTodos;
    private javax.swing.JButton btnGerarRemessa;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jListar;
    private javax.swing.JButton jListarRemessa;
    private javax.swing.JEditorPane jMensagem;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelBaixas;
    private javax.swing.JPanel jPanelConsultas;
    private javax.swing.JPanel jPeriodo;
    private javax.swing.JProgressBar jProgress;
    private javax.swing.JProgressBar jProgressEmail;
    private javax.swing.JProgressBar jProgressRemessa;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JCheckBox jSemEnvio;
    private javax.swing.JTabbedPane jTabbedPaneBoletas;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JRadioButton jatrasados;
    private javax.swing.JRadioButton jemdia;
    private javax.swing.JRadioButton jperiodo;
    private javax.swing.JRadioButton jtodos;
    private javax.swing.JLabel lbl_Status;
    private javax.swing.JButton rDownload;
    private javax.swing.JTable tblBancosRemessa;
    private javax.swing.JTable tblEmails;
    // End of variables declaration//GEN-END:variables

}