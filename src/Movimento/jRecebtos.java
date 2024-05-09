package Movimento;

import Funcoes.Autenticacao;
import Funcoes.AutoCompletion;
import Funcoes.ComponentSearch;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.MergePDF;
import Funcoes.Pad;
import Funcoes.StringManager;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import Funcoes.WordWrap;
import Funcoes.gmail.GmailAPI;
import static Funcoes.gmail.GmailOperations.createEmailWithAttachment;
import static Funcoes.gmail.GmailOperations.createMessageWithEmail;
import Funcoes.jPDF;
import Funcoes.toPrint;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import Protocolo.DivideCC;
import Transicao.jReceber;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.lowagie.text.Element;
import Sici.Partida.Collections;
import Sici.jRetenLoc;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import javax.swing.*;

public class jRecebtos extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    String rgprp = ""; String rgimv = ""; String contrato = ""; String rcampo = "";
    boolean executando = false; boolean mCartVazio = false;
    boolean bExecNome = false, bExecCodigo = false, eBloq = false;

    jReceber tRec = new jReceber();
    JPanel pnlDigite = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "jpnDigite"));
    JButton btnLancar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtLancar"));
    JButton btnCancelar = (JButton) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "jbtCancelar"));
    JPanel pnlBotoes = (JPanel) tRec.getComponent(ComponentSearch.ComponentSearch(tRec, "pnlBotoes"));
    JToggleButton btDN = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgDN"));
    JToggleButton btCH = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCH"));
    JToggleButton btCT = (JToggleButton) pnlBotoes.getComponent(ComponentSearch.ComponentSearch(pnlBotoes, "jtgCT"));
    JFormattedTextField jResto = (JFormattedTextField) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "JRESTO"));
    //JFormattedTextField jTroco = (JFormattedTextField) pnlDigite.getComponent(ComponentSearch.ComponentSearch(pnlDigite, "JTROCO"));

    // campos de calculos
    float exp = 0;
    float multa = 0;
    float juros = 0;
    float correcao = 0;

    private String _botoes = null;

    // Botoes que acompanham a tela
    private boolean _PodeReceber = true;
    
    public void setBotoes(String _botoes) {
        this._botoes = _botoes;
    }
    
    private void InitjReceber() {
        tRec.setVisible(true);
        tRec.setEnabled(true && _PodeReceber);
        tRec.setLocation(0,0);
        //tRec.setBounds(0, 0, 10,250); //470, 290);
        //jpRecebe.setBounds(new Rectangle(550, 253));
        //tRec.setBounds(new Rectangle(jpRecebe.getWidth() - 100, jpRecebe.getHeight() - 5));
        try { jpRecebe.add(tRec); } catch (java.lang.IllegalArgumentException ex) { ex.printStackTrace(); }
        jpRecebe.repaint();
        jpRecebe.setEnabled(true);

        btnLancar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int estacerto = tRec.Lancar();
                if (estacerto > -1) {
                    String msg = null;
                    if (estacerto == 0) msg = "Não preencheu o campo Banco!!!";
                    if (estacerto == 1) msg = "Não preencheu o campo Agencia!!!";
                    if (estacerto == 2) msg = "Não preencheu o campo Numero do Cheque!!!";
                    if (estacerto == 3) msg = "Não preencheu o campo Numero do Cartão!!!";
                    if (msg != null) JOptionPane.showMessageDialog(null, msg, "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                if (tRec.bprintdoc) {
                    try {
                        try {
                            Imprimir();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                        jContrato.setEnabled(true);
                        jNomeLoca.setEnabled(true);
                        jContrato.requestFocus();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tRec.Cancelar()) {
                    jContrato.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.requestFocus();
                }
                jVencimentos.setEnabled(true);
                jQtde.setEnabled(true);
                /////////////////jtgbAvulso.setEnabled(true);
            }            
        });

        btDN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentos.setEnabled(false);
                jQtde.setEnabled(false);
                ///////////////////jtgbAvulso.setEnabled(false);
            }
        });

        btCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentos.setEnabled(false);
                jQtde.setEnabled(false);
                ////////////////////jtgbAvulso.setEnabled(false);
            }
        });

        btCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentos.setEnabled(false);
                jQtde.setEnabled(false);
                //////////////////jtgbAvulso.setEnabled(false);
            }
        });

        repaint();
    }

    private void VerificaBloq(String tcontrato) {
        eBloq = false;
        Object[][] aBloq = null;
        try {aBloq = conn.ReadFieldsTable(new String[] {"historico"}, "LocaBloq", FuncoesGlobais.Subst("contrato = '&1.'",new String[] {tcontrato}));} catch (Exception ex) {}
        if (aBloq != null) {
            jMsgBloqLoca jBloqLoc = new jMsgBloqLoca(null, rootPaneCheckingEnabled);
            jBloqLoc.SetarMotivo(aBloq[0][3].toString());
            jBloqLoc.setVisible(true);        
            eBloq = true;
        } 
    }
    
    /** Creates new form jAltRecibos */
    public jRecebtos() {
        initComponents();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (_botoes != null) {
                    String[] btn = _botoes.split(" ");
                    int Pos = FuncoesGlobais.IndexOf(btn, "podereceber");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _PodeReceber = new Boolean(_btn[1].replace("\"", ""));
                    }
                }
            }
        });        
        
        // Icone da tela
        ImageIcon icone = new FlatSVGIcon("menuIcons/recebimentos.svg",16,16);
        setFrameIcon(icone);
        
        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        InitjReceber();

        FillCombos(); FillNNumero();
        AutoCompletion.enable(jContrato);
        AutoCompletion.enable(jNomeLoca);
        AutoCompletion.enable(jNNumero);
        jNNumero.setEnabled(true);
        
        ComboBoxEditor edit = jNomeLoca.getEditor();
        Component comp = edit.getEditorComponent();
        comp.addFocusListener( new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Verifica bloqueio
                VerificaBloq(jContrato.getSelectedItem().toString());
                if (eBloq) {
                    jContrato.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.requestFocus();
                    jVencimentos.setEnabled(true);
                    jQtde.setEnabled(true);                    
                    return;
                }
                
                Object[][] vCampos = null;
                try {
                    vCampos = conn.ReadFieldsTable(
                       new String[] {"l.contrato", "l.rgprp", "l.rgimv", 
                           "CONCAT('<html>','Observações: ',i.obs,'<br>','Mensagem de Avisos: ',l.aviso,'</html>') AS aviso", 
                           "i.end", "i.num","i.compl", "i.bairro", "i.cidade", 
                           "i.estado", "i.cep", "p.nome"}, 
                           "locatarios l, imoveis i, proprietarios p", 
                           "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp) AND l.contrato = '" + 
                           jContrato.getSelectedItem().toString() + "'");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                rgprp = vCampos[1][3].toString();
                rgimv = vCampos[2][3].toString();
                contrato = vCampos[0][3].toString();

                SetaCampos(vCampos);
                try {
                    executando = true;
                    SpinnerModel sm = new SpinnerNumberModel(300, 1, 300, 1);
                    jQtde.setModel(sm);
                    ListaVectos();
                    executando = false;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                boolean bbloq = true;
                if (jVencimentos.getItemCount() > 0) {
                    //jQtde.setValue(jVencimentos.getItemCount());
                    SpinnerModel sm = new SpinnerNumberModel(jVencimentos.getItemCount(), 1, jVencimentos.getItemCount(), 1);
                    jQtde.setModel(sm);

                    Recalcula();
                } else {
                    JOptionPane.showMessageDialog(null, "Não existe recibo(s) para este contrato!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    jVencimentos.setEnabled(true);
                    jQtde.setEnabled(true);
                    lblQtde.setVisible(true);
                    ///////////////////jtgbAvulso.setEnabled(true);
                    jNomeLoca.setEnabled(true);
                    jContrato.setEnabled(true);
                    jContrato.requestFocus();
                    bbloq = false;
                }

                if (bbloq) {
                    jContrato.setEnabled(false);
                    jNomeLoca.setEnabled(false);
                }

                tRec.Enable(true && _PodeReceber);
                tRec.rgimv = rgimv; tRec.rgprp = rgprp; tRec.contrato = contrato; tRec.acao = "RC"; tRec.operacao = "CRE";
                btnLancar.setEnabled(false);
                tRec.LimpaTransicao();

                // 20/10/2011 FillAvisoLoca(jAvisos, jContrato.getSelectedItem().toString().trim());
                jvrRetencao.setText(VrAvisoLoca(contrato));
                
                // Aviso Sobre termino de Contrato
                String msg = ChecaTermino(contrato);
                if (!msg.trim().equals("")) {
                    // Mensagem
                    JOptionPane.showMessageDialog(null, msg, "Atenção", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
                ///////////////jtgbAvulso.setSelected(false);
                ///////////////jtgbAvulso.setEnabled(true);
                lblQtde.setVisible(true);
                jQtde.setVisible(true);
                tRec.Enable(false && _PodeReceber);
                tRec.LimpaTransicao();
            }
        });

        ComboBoxEditor editContrato = jContrato.getEditor();
        Component compContrato = editContrato.getEditorComponent();
        compContrato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tRec.Enable(false && _PodeReceber);
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
                ////////////////jtgbAvulso.setSelected(false);
                ////////////////jtgbAvulso.setEnabled(true);
                lblQtde.setVisible(true);
                jQtde.setVisible(true);
                tRec.Enable(false && _PodeReceber);
                //jTroco.setText("0,00");
            }
        });

        ComboBoxEditor editNNumero = jNNumero.getEditor();
        Component compNNumero = editNNumero.getEditorComponent();
        compNNumero.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tRec.Enable(false && _PodeReceber);
                
                if (!BuscaNNumero(jNNumero.getSelectedItem().toString().trim())) {
                    jNNumero.requestFocus();
                    return;
                }
                
                jContrato.setEnabled(true);
                jNomeLoca.setEnabled(true);                
                jContrato.requestFocus();                                                 
            }

            public void focusGained(java.awt.event.FocusEvent evt) {
                LimpaTela();
                ////////////////jtgbAvulso.setSelected(false);
                ////////////////jtgbAvulso.setEnabled(true);
                lblQtde.setVisible(true);
                jQtde.setVisible(true);
                tRec.Enable(false && _PodeReceber);
                //jTroco.setText("0,00");
                
                jContrato.setEnabled(false);
                jNomeLoca.setEnabled(false);
            }
        });
//       ListCellRenderer renderer = jVencimentos.getRenderer();
//       jVencimentos.setRenderer(new FixDisabledCBRenderer(jVencimentos, renderer));
//       jContrato.setRenderer(new MyRenderer());
//       jNomeLoca.setRenderer(new MyRenderer());

        repaint();
    }
    
    private boolean BuscaNNumero(String nnumero) {
        if (nnumero.equalsIgnoreCase("")) return true;
        Object[][] boleta = null; boolean ret = false;
        try {
            if (nnumero.contains("PIX")) {
                boleta = conn.ReadFieldsTable(new String[] {"contrato"}, "recibo", "nnumero = :nnumero", new Object[][] {{"string", "nnumero", nnumero}});
            } else {
                boleta = conn.ReadFieldsTable(new String[] {"contrato"}, "recibo", "nnumero regexp '" + nnumero + "$'"); //"nnumero Like '%" + nnumero + "%'");
            }
        } catch (Exception e) {e.printStackTrace();}
        if (boleta == null) {
            return ret;
        } else {
            boolean achei = false; int i = 0;
            for (i = 0; i < jContrato.getItemCount(); i++) {
                if (jContrato.getItemAt(i).toString().trim().equalsIgnoreCase(boleta[0][3].toString())) {
                    achei = true;
                    break;
                }
            }
            if (achei) {
                jContrato.setSelectedIndex(i);
                ret = true;
            } else {
                ret = false;
                JOptionPane.showMessageDialog(this, "Contrato relativo ao NNUMERO não encontrado!!!");
            }
            return ret;
        }
    }

    class MyRenderer extends JLabel implements ListCellRenderer {
        public MyRenderer() {
                setOpaque(true);
                setHorizontalAlignment(LEFT);
                setVerticalAlignment(CENTER);
        }
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (isSelected) {
                    setBackground(Color.WHITE); //the background color of the selected item
                    setForeground(Color.RED); //the textcolor of the selected item
                } else {
                    setBackground(Color.WHITE); //the background color of the other items
                    setForeground(Color.RED); //the textcolor of the other items
                }
                setText(value.toString());
                return this;
        }
    }

    class FixDisabledCBRenderer implements ListCellRenderer {
        private JComboBox comboBox;
        private ListCellRenderer renderer;
        public FixDisabledCBRenderer(JComboBox combo, ListCellRenderer renderer) {
            comboBox = combo;
            this.renderer = renderer;
        }
        public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
            Component return_value = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (return_value instanceof JComponent) ((JComponent)return_value).setOpaque(comboBox.isEnabled());
            comboBox.setEditable(true);
            //comboBox.getEditor().getEditorComponent().setEnabled(false);
            if (isSelected) {
                comboBox.getEditor().getEditorComponent().setBackground(Color.WHITE); //the background color of the selected item
                comboBox.getEditor().getEditorComponent().setForeground(Color.RED); //the textcolor of the selected item
            } else {
                comboBox.getEditor().getEditorComponent().setBackground(Color.WHITE); //the background color of the other items
                comboBox.getEditor().getEditorComponent().setForeground(Color.RED); //the textcolor of the other items
            }
            //comboBox.setEditable(true);
            return return_value;
        }
    }

    private String IPTU(String vecto, String campo) {
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
        
        String wSql = "SELECT p.* FROM iptu p, imoveis i WHERE InStr(i.matriculas,p.matricula) > 0 AND p.ano = '" + dano + "' AND i.rgimv = '" + this.rgimv + "';";
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

    private void CalcularRecibo(String vecto) {
        if ("".equals(vecto.trim())) { return; }

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(this.rgprp, this.rgimv, this.contrato);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String campo = "";
        String sql = "SELECT * FROM recibo WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.OpenTable(sql, null);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");
                
                // Aqui recebe o IPTU
                campo = IPTU(vecto, campo);
                
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) {
            rcampo = "";
            ex.printStackTrace();
        }
        conn.CloseTable(pResult);

        try {
            exp = rc.TaxaExp(campo);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            multa = rc.Multa(campo, vecto, false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            juros = rc.Juros(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            correcao = rc.Correcao(campo, vecto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        jVrRecibo.setValue(tRecibo + exp + multa + juros + correcao);

//        jResto.setValue(tRecibo + exp + multa + juros + correcao);
//        tRec.vrAREC = LerValor.StringToFloat(jResto.getText());

    }

    private String ChecaTermino(String contrato) {
        String msg = "";        
        Object[][] campos = null;
        try {
            String[] fields = new String[] {
                    "dtinicio",
                    "dttermino",
                    "dtadito",
                    "dtseguro", 
                    "(Month(StrDate(dttermino)) = :dtterminomes AND Year(StrDate(dttermino)) = :dtterminoano) AS pinta",
            };
            Object[][] param = new Object[][] {
                {"int", "dtterminomes", Dates.iMonth(new Date())},
                {"int", "dtterminoano", Dates.iYear(new Date())},
                {"string", "contrato", contrato},
                {"int", "dtinicio1", Dates.iMonth(new Date())},
                {"int", "dttermino1", Dates.iYear(new Date())},
                {"int", "dttermino2", Dates.iMonth(new Date())},
                {"int", "dttermino3", Dates.iYear(new Date())}
            };
            String where = "contrato = :contrato AND ((Month(StrDate(dtinicio)) >= :dtinicio1 AND " + 
                    "Year(StrDate(dttermino)) >= :dttermino1) OR (Month(StrDate(dttermino)) >= :dttermino2 AND Year(StrDate(dttermino)) = :dttermino3))";
            campos = conn.ReadFieldsTable(fields,"carteira",where, param);
        } catch (Exception e) {}
        if (campos != null) {
            String tmesanor = (campos[3][3] == null ? tmesanor = "" : campos[3][3].toString());
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
    
    private void SetaCampos(Object[][] vCampos) {
        jRgimv.setText(vCampos[2][3].toString());
        jNomeProp.setText(vCampos[11][3].toString());
        jEndereco.setText(vCampos[4][3].toString().trim() + ", " + vCampos[5][3].toString().trim() + " " + vCampos[6][3].toString());
        jBairro.setText(vCampos[7][3].toString());
        jCidade.setText(vCampos[8][3].toString());
        jEstado.setText(vCampos[9][3].toString());
        jCep.setText(vCampos[10][3].toString());
        jObs.setText(vCampos[3][3].toString());
    }

    private void LimpaTela() {
        jRgimv.setText("");
        jNomeProp.setText("");
        jEndereco.setText("");
        jBairro.setText("");
        jCidade.setText("");
        jEstado.setText("");
        jCep.setText("");
        jObs.setText("");

        jctCampos.removeAll();
        jctCampos.repaint();

        executando = true;
        jVencimentos.removeAllItems();
        executando = false;

        jVrRecibo.setValue(0);
        jResto.setValue(0);
        jQtde.setValue(0);
        jTotalRecibos.setValue(0);

        // 20/10/2011 TableControl.header(jAvisos, new String[][] {{"desc","valor"},{"380","90"}});
    }

    private void ListaVectos() throws SQLException {
        String sql = "SELECT * FROM recibo WHERE tag <> 'X' AND contrato = '" + contrato + "' ORDER BY dtvencimento LIMIT &1.;";
        sql = FuncoesGlobais.Subst(sql, new String[] {String.valueOf(jQtde.getValue())});
        ResultSet hRs = conn.OpenTable(sql, null);

        try {
            jVencimentos.removeAllItems();
        } catch (Exception ex) { ex.printStackTrace(); }

        while (hRs.next()) {
            Date jVecto = null;
            try {
                jVecto = (java.util.Date) hRs.getDate("dtvencimento");
            } catch (SQLException ex) { ex.printStackTrace(); }
            jVencimentos.addItem(Dates.DateFormata("dd/MM/yyyy", jVecto));
        }
        conn.CloseTable(hRs);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jContrato = new javax.swing.JComboBox();
        jNomeLoca = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jRgimv = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jNomeProp = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jEndereco = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jBairro = new javax.swing.JLabel();
        jCidade = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jEstado = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jCep = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jObs = new javax.swing.JLabel();
        jNNumero = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jScroll = new javax.swing.JScrollPane();
        jctCampos = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jVencimentos = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jVrRecibo = new javax.swing.JFormattedTextField();
        jTotalRecibos = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        lblQtde = new javax.swing.JLabel();
        jQtde = new javax.swing.JSpinner();
        jtgbAvulso = new javax.swing.JToggleButton();
        jchb1Via = new javax.swing.JCheckBox();
        jpRecebe = new javax.swing.JPanel();
        jbtretencão = new javax.swing.JButton();
        jvrRetencao = new javax.swing.JLabel();
        jImprimir = new javax.swing.JButton();
        jImprimir1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Recebimentos ::.");
        setMaximumSize(new java.awt.Dimension(856, 610));
        setMinimumSize(new java.awt.Dimension(856, 610));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel1.setText("Contrato");
        jLabel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel1.setOpaque(true);

        jLabel2.setText("Nome");
        jLabel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel2.setOpaque(true);

        jContrato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jContratoActionPerformed(evt);
            }
        });

        jNomeLoca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomeLocaActionPerformed(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N

        jLabel3.setText("RgImv:");
        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jRgimv.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jRgimv.setOpaque(true);

        jLabel5.setText("Proprietário:");
        jLabel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jNomeProp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jNomeProp.setOpaque(true);

        jLabel7.setText("End.:");
        jLabel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jEndereco.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jEndereco.setOpaque(true);

        jLabel9.setText("Bairro:");
        jLabel9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jBairro.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jBairro.setOpaque(true);

        jCidade.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jCidade.setOpaque(true);

        jLabel12.setText("Cidade:");
        jLabel12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jEstado.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jEstado.setOpaque(true);

        jLabel14.setText("Estado:");
        jLabel14.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jCep.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jCep.setOpaque(true);

        jLabel16.setText("Cep:");
        jLabel16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setText("Endereço:");
        jLabel8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCep, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeProp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 711, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCep, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel3});

        jLabel17.setText("Comunicado Importânte");
        jLabel17.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel17.setOpaque(true);

        jObs.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jObs.setOpaque(true);

        jNNumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNNumeroActionPerformed(evt);
            }
        });

        jLabel10.setText("NNumero:");
        jLabel10.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel10.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jObs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10)
                        .addGap(1, 1, 1)
                        .addComponent(jNNumero, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jNomeLoca, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jNNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jObs, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jContrato, jLabel1, jLabel2, jNomeLoca});

        jScroll.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Recibo"), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jctCampos.setEnabled(false);

        javax.swing.GroupLayout jctCamposLayout = new javax.swing.GroupLayout(jctCampos);
        jctCampos.setLayout(jctCamposLayout);
        jctCamposLayout.setHorizontalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );
        jctCamposLayout.setVerticalGroup(
            jctCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        jScroll.setViewportView(jctCampos);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Venctos:");

        jVencimentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVencimentosActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Vr.Recibo:");

        jVrRecibo.setEditable(false);
        jVrRecibo.setForeground(new java.awt.Color(255, 51, 51));
        jVrRecibo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jVrRecibo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jVrRecibo.setText("0,00");
        jVrRecibo.setDisabledTextColor(new java.awt.Color(255, 51, 51));
        jVrRecibo.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jTotalRecibos.setEditable(false);
        jTotalRecibos.setForeground(new java.awt.Color(255, 51, 51));
        jTotalRecibos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        jTotalRecibos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTotalRecibos.setText("0,00");
        jTotalRecibos.setDisabledTextColor(new java.awt.Color(255, 51, 51));
        jTotalRecibos.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Tot.Recibos:");

        lblQtde.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblQtde.setText("Qtde:");

        jQtde.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jQtdeStateChanged(evt);
            }
        });

        jtgbAvulso.setText("Avulso");
        jtgbAvulso.setEnabled(false);
        jtgbAvulso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtgbAvulsoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtgbAvulso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(65, 65, 65)
                        .addComponent(lblQtde)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jQtde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTotalRecibos)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jQtde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblQtde, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23)
                        .addComponent(jVencimentos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtgbAvulso, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jVrRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTotalRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jchb1Via.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jchb1Via.setText("Imprime 1° Via.");

        jpRecebe.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jpRecebe.setMaximumSize(new java.awt.Dimension(481, 253));
        jpRecebe.setMinimumSize(new java.awt.Dimension(481, 253));
        jpRecebe.setPreferredSize(new java.awt.Dimension(481, 253));
        jpRecebe.setLayout(new java.awt.CardLayout());

        jbtretencão.setText("Valores retidos do Locatário");
        jbtretencão.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtretencãoActionPerformed(evt);
            }
        });

        jvrRetencao.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jvrRetencao.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jvrRetencao.setText("0,00");
        jvrRetencao.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jvrRetencao.setOpaque(true);

        jImprimir.setText("Imprimir");
        jImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImprimirActionPerformed(evt);
            }
        });

        jImprimir1.setText("Enviar Lista p/email");
        jImprimir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImprimir1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jchb1Via, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jImprimir)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jImprimir1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jbtretencão, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jvrRetencao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jchb1Via)
                            .addComponent(jImprimir)
                            .addComponent(jImprimir1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtretencão, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jvrRetencao)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpRecebe, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FillCombos() {
        String sSql = "SELECT contrato as codigo, UPPER(nomerazao) as nome FROM locatarios WHERE fiador1uf <> 'X' OR IsNull(fiador1uf) ORDER BY UPPER(nomerazao);";

        jContrato.removeAllItems();
        jNomeLoca.removeAllItems();
        if (!sSql.isEmpty()) {
            ResultSet imResult = this.conn.OpenTable(sSql, null);

            try {
                while (imResult.next()) {
                    jContrato.addItem(String.valueOf(imResult.getInt("codigo")));
                    jNomeLoca.addItem(imResult.getString("nome"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            conn.CloseTable(imResult);
        }
    }
    
    private void FillNNumero() {
        String sSql = "SELECT nnumero FROM recibo WHERE tag <> 'X' AND nnumero <> '';";

        jNNumero.removeAllItems();
        jNNumero.addItem("");
        if (!sSql.isEmpty()) {
            ResultSet imResult = this.conn.OpenTable(sSql, null);

            try {
                while (imResult.next()) {
                    String pnnumero = imResult.getString("nnumero");
                    pnnumero = pnnumero == null ? "" : pnnumero;
                    String tnnumero = pnnumero;
                    if (!pnnumero.contains("PIX")) tnnumero = String.valueOf(Integer.valueOf(tnnumero.substring(3)));
                    jNNumero.addItem(tnnumero);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            conn.CloseTable(imResult);
        }
    }

    private void jContratoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jContratoActionPerformed
        if (!bExecNome) {
            int pos = jContrato.getSelectedIndex();
            if (jNomeLoca.getItemCount() > 0) {bExecCodigo = true; jNomeLoca.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jContratoActionPerformed

    private void jNomeLocaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomeLocaActionPerformed
        if (!bExecCodigo) {
            int pos = jNomeLoca.getSelectedIndex();
            if (jContrato.getItemCount() > 0) {bExecNome = true; jContrato.setSelectedIndex(pos); bExecNome = false;}
        }
    }//GEN-LAST:event_jNomeLocaActionPerformed

    private void jVencimentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jVencimentosActionPerformed
        Recalcula();
        repaint();
}//GEN-LAST:event_jVencimentosActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
       try  {
            this.requestFocus();
            this.setSelected(true);
       } catch (java.beans.PropertyVetoException e)  { e.printStackTrace(); }
    }//GEN-LAST:event_formFocusGained

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
    }//GEN-LAST:event_formInternalFrameClosing

    private void jQtdeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jQtdeStateChanged
        try {
            executando = true;
            ListaVectos();
            executando = false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Recalcula();
        tRec.Enable(true && _PodeReceber);
        tRec.rgimv = rgimv; tRec.rgprp = rgprp; tRec.contrato = contrato; tRec.acao = "RC"; tRec.operacao = "CRE";
        btnLancar.setEnabled(false);
        tRec.LimpaTransicao();
        
        repaint();
    }//GEN-LAST:event_jQtdeStateChanged

    private void jtgbAvulsoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtgbAvulsoActionPerformed
        //Rotina de Impresão do Avuldo
        lblQtde.setVisible(!jtgbAvulso.isSelected());
        jQtde.setVisible(!jtgbAvulso.isSelected());
        Recalcula();
    }//GEN-LAST:event_jtgbAvulsoActionPerformed

    private void jbtretencãoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtretencãoActionPerformed
        // chama tela de retençao de locatarios
        VariaveisGlobais.rcontrato = jContrato.getSelectedItem().toString();
        jRetenLoc oImv = null;
        oImv = new jRetenLoc(null, closable);
        oImv.setVisible(true);
        
    }//GEN-LAST:event_jbtretencãoActionPerformed

    private void jNNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNNumeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jNNumeroActionPerformed

    private void jImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jImprimirActionPerformed
        ImprimeReciboPDF(-1, new String[][] {},jVrRecibo.getText(), "F",1,1f);
    }//GEN-LAST:event_jImprimirActionPerformed

    private void jImprimir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jImprimir1ActionPerformed
        Listar();
    }//GEN-LAST:event_jImprimir1ActionPerformed

    private String VrAvisoLoca(String sLoca) {
        float fTotCred = 0, fTotDeb = 0;
        String retorno = "0,00";
        
        String sSql = FuncoesGlobais.Subst("SELECT campo FROM avisos WHERE registro = '&1.' AND rid = '4';", new String[] {sLoca});
        ResultSet imResult = conn.OpenTable(sSql, null);

        try {
            while (imResult.next()) {
                String tmpCampo = imResult.getString("campo");
                String rCampos[][] = FuncoesGlobais.treeArray(tmpCampo, false);

                String sInq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" +
                              rCampos[0][7].substring(2, 4) + "/" + rCampos[0][7].substring(4, 8);

                if (!"".equals(sInq.trim())) {
                    String aLinhas[] = WordWrap.wrap(sInq, 38).split("\n");

                    for (int l =0;l<aLinhas.length;l++) {
                        String desc = ""; String valor = "";

                        if (l == aLinhas.length - 1) {
                            desc = aLinhas[l];
                            valor = LerValor.FormatNumber(rCampos[0][2], 2);
                        } else {
                            desc = aLinhas[l];
                            valor = "";
                        }
                    }
                }

                if ("CRE".equals(rCampos[0][8])) {
                    fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                } else {
                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                }
            }
            retorno = LerValor.floatToCurrency(fTotCred - fTotDeb, 2);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        conn.CloseTable(imResult);
        return retorno;
    }

    private boolean Distribuicao(String nAut, String vencto) throws SQLException {
        DepuraCampos zCampos; String[] zCampo;

        String[][] aCC = DivideCC.Divisao(rgimv);
        String[][] jCampo;
        String[] gmpCampo, difCampo;
        String[][] tmpCampo;
        String[][] divaCampo = {};
        String[] admValores = {};
        String[][] gCampo = {};

        String[] admPer = null;
        admPer = new Calculos().percADM(rgprp, rgimv);

        String[][] dvCampo;

        float fDC = 0, fDF = 0;

        float[] aComissao = new Calculos().percComissao(rgprp, rgimv);
        float fComissao = aComissao[0]; float rComissao = aComissao[1];
        String sComissao = FuncoesGlobais.GravaValores(String.valueOf(fComissao).replace(".", ","), 3);

        String[] sVenctos = {vencto};

        int i = 0;
        //for (int i=0; i<sVenctos.length; i++) {
            String tCampo = CriticaCampo(contrato, sVenctos[i]);
            
            // Checa IPTU Automático
            tCampo = IPTU(sVenctos[i], tCampo);
            
            String[] aCampo = tCampo.split(";");

            // Captura descontas/diferencas
            jCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"), false);
            gmpCampo = null; fDC = 0; 
            difCampo = null; fDF = 0;
            for (int w=0;w<jCampo.length;w++) {
                gmpCampo = null;
                gmpCampo = FuncoesGlobais.treeSeekArray2(jCampo, "DC", w, w);
                if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                    fDC += ("LQ".equals(gmpCampo[4]) ? LerValor.StringToFloat(gmpCampo[3]) : 0);
                } else fDC += 0;

                difCampo = null;
                difCampo = FuncoesGlobais.treeSeekArray2(jCampo, "DF", w, w);                        
                if (Integer.valueOf(difCampo[0]) > -1 && Integer.valueOf(difCampo[1]) > -1) {
                    fDF += ("LQ".equals(difCampo[4]) ? LerValor.StringToFloat(difCampo[3]) : 0);
                } else fDF += 0;
            }
            // ------------- FIM Descontos/Diferenças
            
            for (int j=0; j < aCampo.length; j++) {
                int k = aCampo[j].indexOf("AL");
                if (k > -1) {
                    //zCampos = new DepuraCampos(aCampo[j]);
                    zCampos = new DepuraCampos(tCampo);
                    VariaveisGlobais.ccampos = aCampo[j];
                    zCampos.SplitCampos();
                    zCampo = zCampos.Depurar(j); 

                    if ("AL".equals(aCampo[j].split(":")[4])) {
                        float calc1 = LerValor.StringToFloat(zCampo[1]) + fDF - fDC;
                        float calc2 = fComissao / 100;
                        float reslt = calc1 * calc2;
                        
                        // Nova modelo Comissao
                        String valor = "";
                        if (fComissao != 0 && rComissao == 0) {
                            valor = String.valueOf(reslt).replace(".", ",");
                        // Artualizado em 04-04-2022 - } else if (fComissao == 0 && rComissao != 0) {
                        } else if (fComissao != 0 && rComissao != 0) {
                            valor = String.valueOf(rComissao).replace(".", ",");
                        } else {
                            valor = String.valueOf(reslt).replace(".", ",");
                        }
                        aCampo[j] += ":CM" + FuncoesGlobais.GravaValores(valor, 2);
                    }
//                }
                    tmpCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"),false);

                    // Ja foi adiantado
                    boolean bad = (FuncoesGlobais.treeSeekArray(tmpCampo, "AD", 0)[0].equalsIgnoreCase("-1") ? false : true);
                        
                    if (!"DC".equals(aCampo[j].split(":")[4]) || !"DF".equals(aCampo[j].split(":")[4]) || !"SG".equals(aCampo[j].split(":")[4])) {
                        admValores = new String[] {};
                        // Separa valores da ADM
                        // Separa Multa
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "MU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[0])) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "MU" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[0])) / 100));
                            String valor_mu = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_mu);
                        } else {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                        }

                        // Juros
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "JU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[1])) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "JU" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[1])) / 100));
                            String valor_ju = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_ju);
                        } else {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, "0");
                        }

                        // Correção
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "CO", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[2])) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CO" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[2])) / 100));
                            String valor_co = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_co);
                        } else admValores = FuncoesGlobais.ArrayAdd(admValores, "0");

                        // Expediente
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "EP", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = LerValor.StringToFloat(gmpCampo[3]) - (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[3])) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            if (valor1 > 0) {
                                tmpCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "EP" + FuncoesGlobais.GravaValores(valor, 2);
                            } else {
                                tmpCampo = FuncoesGlobais.ArraysDelSub(tmpCampo, Integer.valueOf(gmpCampo[0]), Integer.valueOf(gmpCampo[1]));
                            }
                            float valor3 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat((bad ? "100" : admPer[3])) / 100));
                            String valor_ep = String.valueOf(valor3).replace(".", ",");
                            admValores = FuncoesGlobais.ArrayAdd(admValores, valor_ep);
                        } else admValores = FuncoesGlobais.ArrayAdd(admValores, "0");

                        // Separa comissão
                        gmpCampo = FuncoesGlobais.treeSeekArray(tmpCampo, "CM", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            admValores = FuncoesGlobais.ArrayAdd(admValores, gmpCampo[3]);
                        } else admValores = FuncoesGlobais.ArrayAdd(admValores, "0");

                        aCampo[j] = FuncoesGlobais.join(tmpCampo[j], ":");

                        // Envio dos Valores da ADM
                        if (LerValor.StringToFloat(admValores[0]) + LerValor.StringToFloat(admValores[1]) + LerValor.StringToFloat(admValores[2]) + LerValor.StringToFloat(admValores[3]) > 0) {
                            gCampo = FuncoesGlobais.ArraysAdd(gCampo, new String[] {admValores[0], admValores[1], admValores[2], admValores[3], sVenctos[i]});
                        }
                    }
                }
            }

            // Clona Variavel
            String[] oCampo = aCampo;

            for (int l=0;l<aCC.length;l++) {
                dvCampo = FuncoesGlobais.treeArray(FuncoesGlobais.join(aCampo, ";"), false);
                for (int m=0;m<dvCampo.length;m++){
                    //';
                    float part1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                    float part2 = LerValor.StringToFloat(aCC[l][1].replace(".", ",")) / 100;
                    if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) part2 = 1;

                    // 15-12-2016 12h30m
                    if (l==0) {
                        if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) part2 = 1;
                    } else {
                        if (FuncoesGlobais.IndexOf(dvCampo[m], "AL") < 0) {
                            part2 = 0;
                        } else part2 = 1;
                    }

                    String vrfinal = LerValor.FloatToString(part1 * part2);
                    
                    //dvCampo[m][2] = winger.GravarValor(winger.LerValor(dvCampo[m][2]) * If(FuncoesGlobais.ArraFind(dvCampo[m], "AL") < 0, 1, (aCC[l][1] / 100)));
                    
                    if (aCC[l][2].trim().toUpperCase().equalsIgnoreCase("TRUE")) {
                        //'; divide tudo
                        float fValor1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                        float fValor2 = Float.valueOf(aCC[l][1].replace(",", ".")) / 100;
                        dvCampo[m][2] = FuncoesGlobais.GravaValores(LerValor.FloatToString(fValor1 * fValor2),2);
                    } else {
                        try {
                            // 06/01/2017 8h10m
                            if ("AL".equals(dvCampo[m][4]) || "AL".equals(dvCampo[m][5])) {
                                float fValor1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                                float fValor2 = Float.valueOf(aCC[l][1].replace(",", ".")) / 100;
                                dvCampo[m][2] = FuncoesGlobais.GravaValores(LerValor.FloatToString(fValor1 * fValor2),2);
                            } else {
                                dvCampo[m][2] = FuncoesGlobais.GravaValores(vrfinal, 2);                    
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            // 06/01/2017 8h10m
                            if ("AL".equals(dvCampo[m][4])) {
                                float fValor1 = LerValor.StringToFloat(LerValor.FormatNumber(dvCampo[m][2], 2));
                                float fValor2 = Float.valueOf(aCC[l][1].replace(",", ".")) / 100;
                                dvCampo[m][2] = FuncoesGlobais.GravaValores(LerValor.FloatToString(fValor1 * fValor2),2);
                            } else {
                                dvCampo[m][2] = FuncoesGlobais.GravaValores(vrfinal, 2);                    
                            }
                        }
                    }

                    if ("AL".equals(dvCampo[m][4])) {
                        // Separa Multa
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "MU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "MU" + FuncoesGlobais.GravaValores(valor, 2);
                        }


                        // Separa juros
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "JU", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "JU" + FuncoesGlobais.GravaValores(valor, 2);
                        }

                        // Separa Correção
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "CO", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CO" + FuncoesGlobais.GravaValores(valor, 2);
                        }

                        // Separa Expediente
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "EP", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1]) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "EP" + FuncoesGlobais.GravaValores(valor, 2);
                        }

                        // Separa Comissao
                        gmpCampo = new String[] {};
                        gmpCampo = FuncoesGlobais.treeSeekArray(dvCampo, "CM", 0);
                        if (Integer.valueOf(gmpCampo[0]) > -1 && Integer.valueOf(gmpCampo[1]) > -1) {
                            float valor1 = (LerValor.StringToFloat(gmpCampo[3]) * (LerValor.StringToFloat(aCC[l][1].replace(".", ",")) / 100));
                            String valor = String.valueOf(valor1).replace(".", ",");
                            dvCampo[Integer.valueOf(gmpCampo[0])][Integer.valueOf(gmpCampo[1])] = "CM" + FuncoesGlobais.GravaValores(valor, 2);
                        }
                    }
                }

                //String[] tpCampo = {contrato.trim(), aCC[l][0], rgimv, FuncoesGlobais.SuperJoin(dvCampo, l == 0)};
                String[] tpCampo = {contrato.trim(), aCC[l][0], rgimv, FuncoesGlobais.SuperJoin(dvCampo, true)};
                divaCampo = FuncoesGlobais.ArraysAdd(divaCampo, tpCampo);
            }

            for (int l=0; l<aCC.length; l++) {
                aCampo = null; aCampo = divaCampo[l][3].split(";");

                String[] rCampo = {};
                String[] eCampo = {};
                String[] iCampo = {};

                String[] lCampo = {};
                String[] xCampo = {};
                String[] pCampo = {};
                String[] sCampo = {};
                String[] hCampo = {};

                for (int j=0; j<aCampo.length; j++) {
                    String[] wCampo = aCampo[j].split(":");

                    int k = aCampo[j].indexOf("AL");
                    if (k > -1) lCampo = FuncoesGlobais.ArrayAdd(lCampo, aCampo[j]);

                    // Retenção
                    k = aCampo[j].indexOf("RT");
                    if (k > -1) sCampo = FuncoesGlobais.ArrayAdd(sCampo, aCampo[j]);

                    // Antecipados
                    k = aCampo[j].indexOf("AT");
                    if (k > -1) hCampo = FuncoesGlobais.ArrayAdd(hCampo, aCampo[j]);

                    // Expediente
                    k = FuncoesGlobais.IndexOf(wCampo, "EP");
                    if (k > -1) pCampo = FuncoesGlobais.ArrayAdd(pCampo, "EP" + wCampo[k].substring(wCampo[k].length() - 10, wCampo[k].length()) + ":0000:EP");

                    // Taxas -------------
                    k = aCampo[j].indexOf("AL");
                    if (k < 0) xCampo = FuncoesGlobais.ArrayAdd(xCampo, aCampo[j]);

                    // Campos Razao
                    k = aCampo[j].indexOf("RZ");
                    if (k > -1) rCampo = FuncoesGlobais.ArrayAdd(rCampo, aCampo[j]);

                    k = aCampo[j].indexOf("ET");
                    if (k > -1) eCampo = FuncoesGlobais.ArrayAdd(eCampo, aCampo[j]);

                    k = aCampo[j].indexOf("IP");
                    if (k > -1) iCampo = FuncoesGlobais.ArrayAdd(iCampo, aCampo[j]);
                }

                //String[] tpCampo = {lCampo, pCampo, xCampo, rCampo, eCampo, iCampo, sCampo};
                String slCampo = "", spCampo = "", sxCampo = "", srCampo = "", seCampo = "", siCampo = "", ssCampo = "", waCampo = "";
                try {slCampo = FuncoesGlobais.join(lCampo, ";");} catch (Exception ex) {slCampo = "";}
                try {spCampo = FuncoesGlobais.join(pCampo, ";");} catch (Exception ex) {spCampo = "";}
                try {sxCampo = FuncoesGlobais.join(rCampo, ";");} catch (Exception ex) {sxCampo = "";}
                try {srCampo = FuncoesGlobais.join(rCampo, ";");} catch (Exception ex) {srCampo = "";}
                try {seCampo = FuncoesGlobais.join(eCampo, ";");} catch (Exception ex) {seCampo = "";}
                try {siCampo = FuncoesGlobais.join(iCampo, ";");} catch (Exception ex) {siCampo = "";}
                try {ssCampo = FuncoesGlobais.join(sCampo, ";");} catch (Exception ex) {ssCampo = "";}
                
                // 29/09/2014 11h
                try {waCampo = FuncoesGlobais.join(hCampo, ";");} catch (Exception ex) {waCampo = "";}
                String finalCampos = slCampo + "," + spCampo + "," + sxCampo + "," + srCampo + "," + seCampo + "," + siCampo + "," + ssCampo; // + "," + waCampo;
                divaCampo[l] = FuncoesGlobais.ArrayAdd(divaCampo[l], finalCampos);
            }

            boolean sucesso = true;
            for (int l=0;l<gCampo.length;l++) {
                String sADMCPOS = "";
                for (int p=0;p<gCampo[l].length - 1;p++) {
                    if (LerValor.StringToFloat(gCampo[l][p]) != 0) {
                        String sSql = "INSERT INTO razao (rgprp, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.',' ')";

                        String par1 = "GG";
                        String par2 = "GG:9:" +
                                      FuncoesGlobais.GravaValores(gCampo[l][p],2) +
                                      ":000000:GG:ET:" +
                                      FuncoesGlobais.StrZero(nAut.trim(), 6) +
                                      VariaveisGlobais.cContas.get("GG") +
                                      FuncoesGlobais.Choose(p + 1,new String[] {"", "MU", "JU", "CO", "EP"}) +
                                      ":" + Dates.DateFormata("yyyyMMdd", new Date()) +
                                      ":CRE:DN:656877:" +
                                      VariaveisGlobais.usuario;
                        String par3 = Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy"));
                        String par4 = Dates.DateFormata("yyyy-MM-dd", new Date());
                        String par5 = nAut;
                        sSql = FuncoesGlobais.Subst(sSql, new String[] {par1, par2, par3, par4, par5});
                        
                        try {
                            // Grava no Razao
                            conn.CommandExecute(sSql);
                        } catch (Exception e) { sucesso = false; }
                        
                        sADMCPOS += FuncoesGlobais.Choose(p + 1, new String[] {"", "MU", "JU", "CO", "EP"}) + FuncoesGlobais.GravaValores(gCampo[l][p].replace(".", ","),2) + ":";
                    }
                }
                if (!"".equals(sADMCPOS)) {
                    sADMCPOS = sADMCPOS.substring(0, sADMCPOS.length() - 1);
                    conn.CreateArqAux();
                    String sSql = "INSERT INTO auxiliar (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, conta) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.')";
                    String[] variavel = new String[] {contrato, rgprp, rgimv, sADMCPOS,
                           Dates.DateFormata("yyyy-MM-dd",Dates.StringtoDate(gCampo[l][gCampo[l].length - 1],"dd/MM/yyyy")),
                           Dates.DateFormata("yyyy-MM-dd", new Date()),
                           nAut,
                           "ADM"};
                    sSql = FuncoesGlobais.Subst(sSql, variavel);
                    
                    try {
                        conn.CommandExecute(sSql);
                    } catch (Exception e) { sucesso = false; }
                }
            }

            for (int l=0;l<divaCampo.length;l++) {
                // Splitar Itens do DivaCampos Elemento (4)
                String[] divaSplit = divaCampo[l][4].split(",");

                // gravar no arquivo auxiliar
                conn.CreateArqAux();
                String sSql = "INSERT INTO auxiliar (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, conta) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.')";
                sSql = FuncoesGlobais.Subst(sSql, new String[] {
                       divaCampo[l][0],
                       divaCampo[l][1],
                       divaCampo[l][2],
                       divaCampo[l][3],
                       Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                       Dates.DateFormata("yyyy-MM-dd", new Date()), nAut, "REC"});
                
                try {
                    conn.CommandExecute(sSql);
                } catch (Exception e) { sucesso = false; }

                // gravar gravar razao na tabela
                if (!"".equals(divaSplit[3].trim())) {
                    sSql = "INSERT INTO razao (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.', ' ')";
                    String[] par1 = {divaCampo[l][0],
                                     divaCampo[l][1],
                                     divaCampo[l][2],
                                     divaSplit[3],
                                     Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                     Dates.DateFormata("yyyy-MM-dd", new Date()),
                                     nAut};
                    sSql = FuncoesGlobais.Subst(sSql, par1);
                    
                    try {
                        conn.CommandExecute(sSql);
                    } catch (Exception e) { sucesso = false; }
                }

                // gravar extrato na tabela
                if (!"".equals(divaSplit[4].trim())) {
                    sSql = "INSERT INTO extrato (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.', ' ')";
                    String[] par1 = {divaCampo[l][0],
                                     divaCampo[l][1],
                                     divaCampo[l][2],
                                     divaSplit[4],
                                     Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                     Dates.DateFormata("yyyy-MM-dd", new Date()),
                                     nAut};
                    sSql = FuncoesGlobais.Subst(sSql, par1);
                    
                    try {
                        conn.CommandExecute(sSql);
                    } catch (Exception e) { sucesso = false; }
                }

                // Grava imposto na tabela
                if (divaSplit.length >= 6) {
                        if (!"".equals(divaSplit[5].trim())) {
                            sSql = "INSERT INTO imposto (contrato, rgprp, rgimv, campo, dtvencimento, dtrecebimento, rc_aut, tag) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.',' ')";
                            String[] par1 = {divaCampo[l][0],
                                             divaCampo[l][1],
                                             divaCampo[l][2],
                                             divaSplit[5],
                                             Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                             Dates.DateFormata("yyyy-MM-dd", new Date()),
                                             nAut};
                            sSql = FuncoesGlobais.Subst(sSql, par1);

                            try {
                                conn.CommandExecute(sSql);
                            } catch (Exception e) { sucesso = false; }
                    }
                }
                
                // Grava Retenções na tabela
                if (divaSplit.length > 6) {
                    String[] retencaoSplit = divaSplit[6].split(";");
                    for (int n=0;n<retencaoSplit.length;n++) {
                        sSql = "INSERT INTO retencao (contrato, rgprp, rgimv, campo, vencimento, rc_aut, tag, gat, rt_aut) VALUES ('&1.','&2.','&3.','&4.','&5.','&6.','&7.','&8.','&9.')";
                        String[] par1 = {divaCampo[l][0],
                                         divaCampo[l][1],
                                         divaCampo[l][2],
                                         retencaoSplit[n],
                                         Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(sVenctos[i], "dd/MM/yyyy")),
                                         nAut,"0"," ","0"};
                        sSql = FuncoesGlobais.Subst(sSql, par1);
                        
                        try {
                            conn.CommandExecute(sSql);
                        } catch (Exception e) { sucesso = false; }
                    }
                }
            }
            
            // Atualiza Antecipados 29/09/2014
            for (int h=0;h<jCampo.length;h++) {
                if (jCampo[h][jCampo[h].length - 1].equalsIgnoreCase("AT")) {
                    if (Dates.isDateValid(jCampo[h][4], "ddMMyyyy")) {
                        String sSql = "UPDATE antecipados SET dtrecebimento = '&1.', rc_aut = '&2.' WHERE contrato = '&3.' AND dtvencimento = '&4.' AND Mid(campo,1,2) = '&5.';";
                        sSql = FuncoesGlobais.Subst(sSql, new String[] {
                                Dates.DateFormata("yyyy-MM-dd", new Date()),
                            nAut, contrato, Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jCampo[h][4], "ddMMyyyy")), jCampo[h][0]});
                        
                        System.out.println(sSql);
                        try {
                            conn.CommandExecute(sSql);
                        } catch (Exception e) { sucesso = false; }
                    }
                }
            }
        //}
        return sucesso;
    }

    private String CriticaCampo(String nContrato, String dVecto) throws SQLException {
        String[] aCampo; int tCampo = 0; String ctCampo = "";
        String cSELECT = "SELECT * FROM recibo WHERE contrato = '" + nContrato + "' AND Cast(dtVencimento as CHAR(10)) = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(dVecto,"dd-MM-yyyy")) + "';";
        ResultSet Data1 = conn.OpenTable(cSELECT, null);

        boolean cbMulta, cbJuros, cbCorrecao, cbTaxa = false;
        float tbTaxa, tbMulta, tbJurosxt, tbCorrecao = 0;

        String tmpCampo = "";
        if (Data1.next()) {
            tmpCampo = "" + Data1.getString("campo");

            aCampo = tmpCampo.split(";");
            aCampo = FuncoesGlobais.OrdenaMatriz(aCampo, 3, 1, true);
            tCampo = aCampo.length - 1;

            cbMulta = tmpCampo.contains("MU");
            cbJuros = tmpCampo.contains("JU");
            cbCorrecao = tmpCampo.contains("CO");
            cbTaxa = tmpCampo.contains("EP");

            Calculos rc = new Calculos();
            rc.Inicializa(this.rgprp, this.rgimv, this.contrato);
            tbTaxa = rc.TaxaExp(tmpCampo);
            tbMulta = rc.Multa(tmpCampo, dVecto, false);
            tbJurosxt = rc.Juros(tmpCampo, dVecto);
            tbCorrecao = rc.Correcao(tmpCampo, dVecto);

            for (int nConta=0; nConta <= tCampo; nConta++) {
                ctCampo = aCampo[nConta];
                String[] rCampos = ctCampo.split(":");

                if ("AL".equals(rCampos[4])) {
                    int iMulta = FuncoesGlobais.IndexOf(rCampos,"MU");
                    if (iMulta > -1) {
                        rCampos[iMulta] = "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "MU" + FuncoesGlobais.GravaValores(String.valueOf(tbMulta).replace(".", ","), 2));
                    }

                    int iJuros = FuncoesGlobais.IndexOf(rCampos,"JU");
                    if (iJuros > -1) {
                        rCampos[iJuros] = "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "JU" + FuncoesGlobais.GravaValores(String.valueOf(tbJurosxt).replace(".", ","), 2));
                    }

                    int iCorrecao = FuncoesGlobais.IndexOf(rCampos,"CO");
                    if (iCorrecao > -1) {
                        rCampos[iCorrecao] = "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "CO" + FuncoesGlobais.GravaValores(String.valueOf(tbCorrecao).replace(".", ","), 2));
                    }

                    int iExp = FuncoesGlobais.IndexOf(rCampos,"EP");
                    if (iExp > -1) {
                        rCampos[iExp] = "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2);
                    } else {
                        rCampos = FuncoesGlobais.ArrayAdd(rCampos, "EP" + FuncoesGlobais.GravaValores(String.valueOf(tbTaxa).replace(".", ","), 2));
                    }
                }
                aCampo[nConta] = FuncoesGlobais.join(rCampos, ":");
            }
            tmpCampo = FuncoesGlobais.join(aCampo, ";");
        }
        conn.CloseTable(Data1);

        return tmpCampo;
    }

    public void Recalcula() {
        if (!executando) {
            if (jVencimentos.getItemCount() > 0) {
                try {
                    MontaTela(jVencimentos.getSelectedItem().toString());
                } catch (SQLException ex) {
                    //ex.printStackTrace();
                } catch (ParseException ex) {
                    //ex.printStackTrace();
                }

                CalcularRecibo(jVencimentos.getSelectedItem().toString());

                //
                int i = rcampo.split(";").length;
                Collections gVar = VariaveisGlobais.dCliente;

                if (multa > 0) MontaCampos2(gVar.get("MU"), multa, ++i);
                if (juros > 0) MontaCampos2(gVar.get("JU"), juros, ++i);
                if (correcao > 0) MontaCampos2(gVar.get("CO"), correcao, ++i);
                if (exp > 0) MontaCampos2(gVar.get("EP"), exp, ++i);

                float totRecibos = Totaliza();
                if (!jtgbAvulso.isSelected()) {
                    jTotalRecibos.setValue(totRecibos);
                    jResto.setValue(totRecibos);
                } else {
                    jTotalRecibos.setValue(jVrRecibo.getValue());
                    jResto.setValue(jVrRecibo.getValue());
                }
                tRec.vrAREC = LerValor.StringToFloat(jResto.getText());

            } else {
                LimpaTela();
            }
        }
    }

    public float Totaliza() {
        int j = 0; int k = jVencimentos.getItemCount() - 1;
        float tRecibo = 0;

        for (j=0;j<=k;j++) {
            Calculos rc = new Calculos();
            try {
                rc.Inicializa(this.rgprp, this.rgimv, this.contrato);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            String vecto = jVencimentos.getItemAt(j).toString();
            String campo = "";
            String sql = "SELECT * FROM recibo WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
            ResultSet pResult = conn.OpenTable(sql, null);
            try {
                if (pResult.first()) {
                    campo = pResult.getString("campo");

                    // Aqui recebe o IPTU
                    campo = IPTU(vecto, campo);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            conn.CloseTable(pResult);

            float texp = 0;
            float tmulta = 0;
            float tjuros = 0;
            float tcorrecao = 0;

            try {
                texp = rc.TaxaExp(campo);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                tmulta = rc.Multa(campo, vecto, false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                tjuros = rc.Juros(campo, vecto);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                tcorrecao = rc.Correcao(campo, vecto);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            float stRecibo = 0;
            stRecibo = Calculos.RetValorCampos(campo);
            tRecibo += LerValor.StringToFloat(LerValor.floatToCurrency(stRecibo + texp + tmulta + tjuros + tcorrecao,2));

        }

        return tRecibo;
    }

    public void MontaTela(String vecto) throws SQLException, ParseException {
        if ("".equals(vecto.trim())) { return; }

        // Limpa campos
        jctCampos.removeAll();
        jctCampos.repaint();

        String sql = "SELECT * FROM recibo WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.OpenTable(sql, null);

        if (pResult.first()) {
            String tcampo = IPTU(vecto, pResult.getString("campo"));
            DepuraCampos a = new DepuraCampos(tcampo);
            VariaveisGlobais.ccampos = tcampo;
            
//            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
//            VariaveisGlobais.ccampos = pResult.getString("campo");

            a.SplitCampos();
            // Ordena Matriz
            Arrays.sort (a.aCampos, new Comparator()
            {
            private int pos1 = 3;
            private int pos2 = 4;
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(pos1, pos2);
                String p2 = ((String)o2).substring(pos1, pos2);
                return p1.compareTo(p2);
            }
            });

            // Monta campos
            int i = 0;
            for (i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar(i);
                if (Campo.length > 0) {
                    MontaCampos(Campo, i);
                }
            }
        }

        conn.CloseTable(pResult);
    }

    private void MontaCampos(String[] aCampos, int i) {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();

        int at = 20; int llg = 180 - (VariaveisGlobais.bShowCotaParcela ? 50 : 0); int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;
//        int at = 18; int llg = 150 - (VariaveisGlobais.bShowCotaParcela ? 50 : 0); int ltf = 90; int lcp = 60; int lcc = 218;
//        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(aCampos[0]);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(defaults.getColor("Label.Foreground"));
        lb.setBounds(0 + left, 0 + (at * i) + top, llg, at);
        lb.setName("Label" + i);
        jctCampos.add(lb);

        JFormattedTextField cp = null;
        if (VariaveisGlobais.bShowCotaParcela) {
            cp = new JFormattedTextField();
            try {
                cp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter((!"C".equals(aCampos[5]) ? "##/####" : "##/##"))));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            cp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            cp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            cp.setText(("".equals(aCampos[3]) ? "00/00" + (!"C".equals(aCampos[5]) ? "00" : "") : aCampos[3]));
            cp.setVisible(true);
            cp.setForeground(defaults.getColor("FormattedTextField.Foreground")); //new Color(0,128,0));
            cp.setDisabledTextColor(new Color(0,128,0));
            cp.setBounds(lb.getX() + llg, 0 + (at * i) + top, lcp, at);
            cp.setName("Cota" + i);
            cp.setEditable(false);
            jctCampos.add(cp);
        }

        JFormattedTextField tf = new JFormattedTextField();
        tf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        tf.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tf.setText(aCampos[1]);
        tf.setVisible(true);
        tf.setDisabledTextColor(defaults.getColor("FormattedTextField.Foreground")); //new Color(0,128,0));
        tf.setBounds((VariaveisGlobais.bShowCotaParcela ? cp.getX() + lcp : lb.getX() + llg), 0 + (at * i) + top, ltf, at);
        tf.setName("Field" + i);
        tf.setEditable(false);
        jctCampos.add(tf);

        jctCampos.repaint();
    }

    private void MontaCampos2(String Label, float Valor, int i) {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();

        int at = 20; int llg = 180 + (VariaveisGlobais.bShowCotaParcela ? 10 : 0); int ltf = 120; int lcp = 60; int lcc = 278;
        int top = 5; int left = 5;
//        int at = 18; int llg = 150 + (VariaveisGlobais.bShowCotaParcela ? 10 : 0); int ltf = 90; int lcp = 60; int lcc = 278;
//        int top = 5; int left = 5;

        JLabel lb = new JLabel();
        lb.setText(Label);
        lb.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        lb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb.setVisible(true);
        lb.setForeground(defaults.getColor("Label.Foreground")); //Color.BLACK);
        lb.setBounds(0 + left, 0 + (at * i) + top, llg, at);
        lb.setName("Label" + i);
        jctCampos.add(lb);

        JFormattedTextField tf = new JFormattedTextField();
        tf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        tf.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tf.setValue(Valor);
        tf.setVisible(true);
        tf.setDisabledTextColor(defaults.getColor("FormattedTextField.Foreground")); //new Color(0,128,0));
        tf.setBounds(lb.getX() + llg, 0 + (at * i) + top, ltf, at);
        tf.setName("Field" + i);
        tf.setEditable(false);
        jctCampos.add(tf);

        jctCampos.repaint();
    }

    private void ExtornaRecibo(String nAut) {
        try {
            String sql = "";
            sql = FuncoesGlobais.Subst("DELETE FROM Cheques WHERE ch_autenticacao = '&1.'", new String[] {nAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM razao WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM retencao WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM imposto WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("DELETE FROM extrato WHERE rc_aut = '&1.'", new String[] {nAut});
            conn.CommandExecute(sql);

            sql = FuncoesGlobais.Subst("UPDATE recibo SET tag = ' ', autenticacao = '0' WHERE autenticacao = '&1.'", new String[] {nAut});
            conn.CommandExecute(sql);        
        } catch (Exception e) {}
    }
    
    public void Imprimir() throws FileNotFoundException, IOException, SQLException {
        float nAut = 0;
        float vias = LerValor.StringToFloat(jQtde.getValue().toString());
        float nvias;
        boolean sucesso = true;
        
        String[][] aTrancicao = tRec.Transicao("RC");
        if (aTrancicao.length <= 0 ) return;

        if (jchb1Via.isSelected()) {nvias = 1;} else {nvias = VariaveisGlobais.nviasRecibo;} // pegar no parametros quantidade de vias que a imobiliaria imprime
        if (!jtgbAvulso.isSelected()) {
            //nAut = SubRecibo(vias,nvias,aTrancicao,nAut);
            nAut = SubRecibo2(vias,nvias,aTrancicao,nAut);
        } else {
            // Autenticacao
            nAut = (float)Autenticacao.getAut();
            if (!Autenticacao.setAut((double)nAut, 1)) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            //nAut = LerValor.StringToFloat(conn.ReadParameters("AUTENTICACAO"));
            //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

            float rc = LerValor.StringToFloat(jVrRecibo.getText());
            
            // imprime recibo
            for (int v=1;v<=nvias;v++) { 
                ImprimeReciboPDF(nAut, aTrancicao, LerValor.floatToCurrency(rc, 2), "F",1,vias);
            }

            try {
                conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, aTrancicao, String.valueOf((int)nAut).replace(".0", ""));
            } catch (Exception e) { sucesso = false; }

            if (sucesso) {
                sucesso = Distribuicao(String.valueOf((int)nAut).replace(".0", ""), jVencimentos.getSelectedItem().toString());
            }
            if (!sucesso) {
                ExtornaRecibo(String.valueOf((int)nAut).replace(".0", ""));
            }
        }

        if (nAut > -1 && sucesso) {
            // Elimina os recibos
            if (!jtgbAvulso.isSelected()) {
                for (int j=0;j<vias;j++) {
                    jVencimentos.setSelectedIndex(j);
                    String sql = "UPDATE recibo SET tag = 'X', autenticacao = '" + String.valueOf((int)nAut).replace(".0", "") + "' WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(), "dd/MM/yyyy")) + "';";
                    
                    try {
                        conn.CommandExecute(sql);
                    } catch (Exception e) { sucesso = false; }
                }
            } else {
                String sql = "UPDATE recibo SET tag = 'X', autenticacao = '" + String.valueOf((int)nAut).replace(".0", "") + "' WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(jVencimentos.getSelectedItem().toString(), "dd/MM/yyyy")) + "';";
                
                try {
                    conn.CommandExecute(sql);
                } catch (Exception e) { sucesso = false; }
            }

            if (!sucesso) { ExtornaRecibo(String.valueOf((int)nAut).replace(".0", "")); } 
            
            try {
                String sql = "UPDATE carteira SET dtultrecebimento = '" + Dates.DateFormata("dd-MM-yyyy",Dates.StringtoDate(jVencimentos.getSelectedItem().toString(),"dd-MM-yyyy")) + "' WHERE contrato = '" + contrato + "';";
                conn.CommandExecute(sql);
            } catch (Exception e) { 
                sucesso = false;
                e.printStackTrace(); 
            }
            
            
        } else if (!sucesso) {
            JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
        }
    }

    private float SubRecibo2(float vias, float nvias, String[][] aTrancicao, float nAut) {
        float nret = -1;
        try {
            nAut = (float)Autenticacao.getAut();
            //nAut = LerValor.StringToFloat(conn.ReadParameters("AUTENTICACAO"));
            for (int i=0;i<vias;i++) {
                jVencimentos.setSelectedIndex(i);
                
                float rc = LerValor.StringToFloat(jVrRecibo.getText());
                String[][] lcto =  null;
                lcto = aTrancicao;
                
                if (i != vias - 1) {
                    //lcto = new String[][] {{"","","","",String.valueOf(rc),"CT","CRE","RC","VARIOS_RECIBOS"}};
                    lcto[lcto.length - 1][8] = "VARIOS_RECIBOS";
                } else { lcto[lcto.length - 1][8] = ""; }
                //new String[][] {{"","","","",String.valueOf(rc),aTrancicao[0][5],aTrancicao[0][6],aTrancicao[0][7],aTrancicao[0][8]}};
                
                for (int v=1;v<=nvias;v++) {
                    ImprimeReciboPDF(nAut, lcto,LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"),i+1,vias);
                }
                
                if (i == vias - 1) {
                    conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, lcto,String.valueOf((int)nAut).replace(".0", ""));
                    if (!Autenticacao.setAut((double)nAut, 1)) {
                        JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                    //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});
                }
                boolean sucesso = Distribuicao(String.valueOf((int)nAut).replace(".0", ""),jVencimentos.getSelectedItem().toString());
                if (!sucesso) { 
                    ExtornaRecibo(String.valueOf((int)nAut).replace(".0", ""));
                    JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                }
            }
            nret = nAut;
        } catch (Exception e) {e.printStackTrace();}
        return nret;
    }
    
    private float SubRecibo(float vias, float nvias, String[][] aTrancicao, float nAut) {
        try {
            for (int i=0;i<vias;i++) {
                jVencimentos.setSelectedIndex(i);

                float dn = 0; int c = 0; int inc = 0;
                if ("".equals(aTrancicao[0][1].trim())) {dn = Float.valueOf(aTrancicao[0][4]); c=0;} else {dn=0; c=-1;}
                float rc = LerValor.StringToFloat(jVrRecibo.getText());
                float ch = LerValor.StringToFloat(LerValor.floatToCurrency(0,2));

                inc = c;
                String[][] especies = {};

                if (LerValor.StringToFloat(LerValor.floatToCurrency(rc,2)) <= LerValor.StringToFloat(LerValor.floatToCurrency(dn,2))) {
                    dn = LerValor.StringToFloat(LerValor.floatToCurrency(dn - rc,2));
                    aTrancicao[0][4] = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(dn, 2)));

                    // imprime recibo
                    nAut = (float)Autenticacao.getAut();
                    //nAut = LerValor.StringToFloat(conn.ReadParameters("AUTENTICACAO"));
                    String[][] lcto = new String[][] {{"","","","",String.valueOf(rc),aTrancicao[0][5],aTrancicao[0][6],aTrancicao[0][7],aTrancicao[0][8]}};
                    for (int v=1;v<=nvias;v++) {
                        ImprimeReciboPDF(nAut, lcto,LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"),i+1,vias);
                    }
                    conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, lcto,String.valueOf((int)nAut).replace(".0", ""));
                    if (!Autenticacao.setAut((double)nAut, 1)) {
                        JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                    //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});
                                       
                    boolean sucesso = Distribuicao(String.valueOf((int)nAut).replace(".0", ""),jVencimentos.getSelectedItem().toString());
                    if (!sucesso) { 
                        ExtornaRecibo(String.valueOf((int)nAut).replace(".0", ""));
                        JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                    }
                   
                } else {
                    // Autenticacao
                    nAut = (float)Autenticacao.getAut();
                    if (!Autenticacao.setAut((double)nAut, 1)) {
                        JOptionPane.showMessageDialog(null, "Erro ao gravar autenticacão!!!\nChane o suporte técnico...", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                        return -1;
                    }
                    //nAut = LerValor.StringToFloat(conn.ReadParameters("AUTENTICACAO"));
                    //conn.GravarParametros(new String[] {"AUTENTICACAO",LerValor.FloatToString(nAut + 1),"NUMERICO"});

                    if (LerValor.StringToFloat(LerValor.floatToCurrency(dn,2)) > 0) { especies = FuncoesGlobais.ArraysAdd(especies, new String[] {"","","","",String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(dn, 2))),aTrancicao[0][5],aTrancicao[0][6],aTrancicao[0][7],aTrancicao[0][8]});}

                    while (c<aTrancicao.length) {
                        c++;
                        ch += Float.valueOf(aTrancicao[c][4]);

                        if (LerValor.StringToFloat(LerValor.floatToCurrency(rc,2)) <= LerValor.StringToFloat(LerValor.floatToCurrency(dn + ch,2))) {
                            float vrch = LerValor.StringToFloat(LerValor.floatToCurrency(0,2));
                            if (LerValor.StringToFloat(LerValor.floatToCurrency(dn,2)) > 0) {
                                vrch = LerValor.StringToFloat(LerValor.floatToCurrency(rc - dn,2));
                            } else {
                                vrch = Float.valueOf(aTrancicao[c][4]) - (LerValor.StringToFloat(LerValor.floatToCurrency(ch - rc,2)));
                            }
                            //especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],LerValor.floatToCurrency(vrch, 2),aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            if (LerValor.StringToFloat(LerValor.floatToCurrency(vrch + dn,2)) >= rc) {
                                especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],aTrancicao[c][4],aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            } else {
                                especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],String.valueOf(vrch),aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            }

                            // imprime dn,ch
                            for (int v=1;v<=nvias;v++) {
                                ImprimeReciboPDF(nAut, especies, LerValor.floatToCurrency(rc, 2), (i==vias ? "F" : "P"),i+1,vias);
                            }
                            conn.LancarCaixa(new String[] {rgprp, rgimv, contrato}, especies,String.valueOf((int)nAut).replace(".0", ""));
                            
                            boolean sucesso = Distribuicao(String.valueOf((int)nAut).replace(".0", ""),jVencimentos.getSelectedItem().toString());
                            if (!sucesso) { 
                                ExtornaRecibo(String.valueOf((int)nAut).replace(".0", "")); 
                                JOptionPane.showMessageDialog(null, "Banco de dados ocupado!!!\nOperação sem sucesso...\nInultilize o Recibo e tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                            }
                            
                            ch = LerValor.StringToFloat(LerValor.floatToCurrency(ch,2)) - (LerValor.StringToFloat(LerValor.floatToCurrency(rc - dn,2)));

                            // zera dinheiro
                            if (dn > 0) aTrancicao[0][4] = "0.00";

                            if (ch > 0) {
                                aTrancicao[c][4] = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(ch, 2)));
                            } else {
                                aTrancicao = FuncoesGlobais.ArraysDel(aTrancicao, c);
                            }
                            break;
                        } else {
                            especies = FuncoesGlobais.ArraysAdd(especies, new String[] {aTrancicao[c][0],aTrancicao[c][1],aTrancicao[c][2],aTrancicao[c][3],aTrancicao[c][4],aTrancicao[c][5],aTrancicao[c][6],aTrancicao[c][7],aTrancicao[c][8]});
                            aTrancicao = FuncoesGlobais.ArraysDel(aTrancicao, c); c=inc;
                        }
                    }

                }
             }        
        } catch (Exception e) {}
        return nAut;
    }

    private void FillAvisoLoca(JTable table, String sLoca) {
        float fTotCred = 0, fTotDeb = 0;

        // Seta Cabecario
        TableControl.header(table, new String[][] {{"desc","valor"},{"380","90"}});

        String sSql = FuncoesGlobais.Subst("SELECT campo FROM avisos WHERE registro = '&1.' AND rid = '4';", new String[] {sLoca});
        ResultSet imResult = this.conn.OpenTable(sSql, null);

        try {
            while (imResult.next()) {
                String tmpCampo = imResult.getString("campo");
                String rCampos[][] = FuncoesGlobais.treeArray(tmpCampo, false);

                String sInq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" +
                              rCampos[0][7].substring(2, 4) + "/" + rCampos[0][7].substring(4, 8);

                if (!"".equals(sInq.trim())) {
                    String aLinhas[] = WordWrap.wrap(sInq, 38).split("\n");

                    for (int l =0;l<aLinhas.length;l++) {
                        String desc = ""; String valor = "";

                        if (l == aLinhas.length - 1) {
                            desc = aLinhas[l];
                            valor = LerValor.FormatNumber(rCampos[0][2], 2);
                        } else {
                            desc = aLinhas[l];
                            valor = "";
                        }

                        TableControl.add(table, new String[][]{{desc, valor},{"L","R"}}, true);
                    }
                }

                if ("CRE".equals(rCampos[0][8])) {
                    fTotCred += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                } else {
                    fTotDeb += LerValor.StringToFloat(LerValor.FormatNumber(rCampos[0][2], 2));
                }
            }
            TableControl.add(table, new String[][]{{"Total Geral =>", LerValor.floatToCurrency(fTotCred - fTotDeb, 2)},{"R","R"}}, true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        conn.CloseTable(imResult);

    }

    public String ImprimeReciboPDF(float nAut, String[][] Valores, String ValorRec, String cutPaper,int via, float nRecibos) {
       System.out.println("ImprimeReciboPDF");
       
       float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();
        pdf.setPathName("reports/Recibos/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String docID = "_" + contrato + ".pdf";
        String docName = "RC_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + 
                FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-" + 
                FuncoesGlobais.StrZero(String.valueOf((int)via), 2) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nRecibos), 2) + docID;
        if (nAut < 0) {
            docName = "RC_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + "0000000" + "-" + 
                FuncoesGlobais.StrZero(String.valueOf((int)via), 2) + "_" + FuncoesGlobais.StrZero(String.valueOf((int)nRecibos), 2) + docID;
        }
        
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            img = com.itextpdf.text.Image.getInstance("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        try {
            if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
                p = pdf.print(gVar.get("tipodoc"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
                pdf.doc_add(p);
            }
        } catch (Exception e) {}
        
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel/Fax:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print((nAut >0 ? gVar.get("recibo") : "D E M O N S T R A T I V O"), pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        PdfPCell cell1 = new PdfPCell(new Phrase("CAIXA: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Dados do locatario
        columnWidths = new float[] {35, 65 };
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        cell1 = new PdfPCell(new Phrase("Locatário: " + contrato,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(jNomeLoca.getSelectedItem().toString()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        cell1 = new PdfPCell(new Phrase("Imóvel: " + jRgimv.getText(),font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("Vencimento: " + jVencimentos.getSelectedItem().toString(),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);
        
        p = pdf.print(StringManager.ConvStr(jEndereco.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr(jBairro.getText() + " - " + jCidade.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr(jEstado.getText() + " - Cep: " + jCep.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("C/P",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);
        PdfPCell cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        Object[][] linhas = ListaCamposRecibo_pdf();
        for (int i=0; i<linhas.length;i++) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase((String) linhas[i][0],font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase((String) linhas[i][1],font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);
            cell3 = new PdfPCell(new Phrase((String) linhas[i][2], font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);
        }
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase("==========", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase(jVrRecibo.getText(), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {35, 65};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Propriet(s): " + rgprp,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.TOP);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(jNomeProp.getText()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.TOP);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        if (VariaveisGlobais.impPropDiv) {
            Object[][] div = {};
            try {div = conn.ReadFieldsTable(new String[] {"benefs"}, "divisao", "rgprp = '" + rgprp + "' AND rgimv = '" + rgimv + "'");} catch (SQLException ex) {}
            if (div != null) {
                if (div.length > 0) {
                    for (int q=0;q<div.length;q++) {
                        String[] cpos = div[q][3].toString().split(";");
                        for (int z=0; z<cpos.length;z++) {
                            String[] scpos = cpos[z].split(":");
                            if (scpos.length > 0) {
                                Object[][] nmProDiv = {};
                                try {nmProDiv = conn.ReadFieldsTable(new String[] {"nome"}, "proprietarios", "rgprp = '" + scpos[0] + "'");} catch (SQLException ex) {}
                                try {
                                    font.setColor(BaseColor.BLACK);
                                    cell1 = new PdfPCell(new Phrase("Propriet(s): " + scpos[0],font));
                                    cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell1.setBorder(Rectangle.NO_BORDER);
                                    cell1.setBackgroundColor(BaseColor.WHITE);
                                    table.addCell(cell1);
                                    cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(nmProDiv[0][3].toString()),font));
                                    cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell2.setBorder(Rectangle.NO_BORDER);
                                    cell2.setBackgroundColor(BaseColor.WHITE);
                                    table.addCell(cell2);
                                } catch (Exception e) {}
                            }
                        }
                    }
                }
            }
        }
        table.completeRow();
        pdf.doc_add(table);
        
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        // Mensagem do Recibo
        String msgem = "";
        try {
            try { msgem = conn.ReadFieldsTable(new String[] {"msgboleta"}, "locatarios", "contrato = '" + contrato + "'")[0][3].toString();} catch (SQLException ex) {msgem = "";}
            if (!msgem.trim().equalsIgnoreCase("")) {
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("__________ MENSAGEM __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print(msgem, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
            }
        } catch (Exception e) {}

        if (nAut > 0) {
            p = pdf.print("__________ VALOR(ES) LANCADOS __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            for (int i=0;i<Valores.length;i++) {
                String bLinha = "";
                if (!"".equals(Valores[i][1].trim())) {
                    bLinha = "BCO:" + new Pad(Valores[i][1],3).RPad() + " AG:" + new Pad(Valores[i][2],4).RPad() + " CH:" + new Pad(Valores[i][3],8).RPad() + " DT: " + new Pad(Valores[i][0],10).CPad() + " VR:" + new Pad(Valores[i][4],10).LPad();
                } else {
                    bLinha = (Valores[i][5].trim().toUpperCase().equalsIgnoreCase("CT") ? "BC" : Valores[i][5].trim().toUpperCase()) +  ":" + new Pad(Valores[i][4],10).LPad();
                }

                p = pdf.print(bLinha, pdf.HELVETICA, 6, pdf.NORMAL, pdf.RIGHT, pdf.BLACK);
                pdf.doc_add(p);
            }

            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);

            p = pdf.print("Este recibo não quita qualquer débito anterior.", pdf.HELVETICA, 6, pdf.BOLDITALIC, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);

            l = new LineSeparator();
            l.setPercentage(100f);
            p = pdf.print("", pdf.HELVETICA, 7, pdf.BOLDITALIC, pdf.LEFT, pdf.BLACK);
            p.add(new Chunk(l));
            pdf.doc_add(p);

            // Imprimir Autenticação
            p = pdf.print(VariaveisGlobais.dCliente.get("marca").trim() + "RC" + FuncoesGlobais.StrZero(String.valueOf((int)nAut), 7) + "-" +
                          FuncoesGlobais.StrZero(String.valueOf((int)via), 2) + "/" + FuncoesGlobais.StrZero(String.valueOf((int)nRecibos), 2) + 
                          Dates.DateFormata("ddMMyyyyHHmmss", new Date()) + FuncoesGlobais.GravaValores(ValorRec, 2) + VariaveisGlobais.usuario, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
            pdf.doc_add(p);
            
            PdfContentByte cb = pdf.writer().getDirectContent();
            BarcodeInter25 code25 = new BarcodeInter25();
            String barra = FuncoesGlobais.StrZero(String.valueOf((int)nAut),16);
            code25.setCode(barra);
            code25.setChecksumText(true);
            code25.setFont(null);
            com.itextpdf.text.Image cdbar = code25.createImageWithBarcode(cb, null, null);
            cdbar.setAlignment(Element.ALIGN_CENTER);
            pdf.doc_add(cdbar);            
        }

        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        //pdf.print();
        new toPrint(pdf.getPathName() + docName, "THERMICA",VariaveisGlobais.Recibo);
        String retorno = pdf.getPathName() + docName;
        pdf.setPathName("");
        pdf.setDocName("");
        
        return retorno;
   }

   public String ImprimeReciboPIXPDF(String ValorRec, String ChavePix, String pixID) {
       System.out.println("ImprimeReciboPIXPDF");
       
       float[] columnWidths = {};
        Collections gVar = VariaveisGlobais.dCliente;
        jPDF pdf = new jPDF();
        
        // Checa se diretorio existe
        File diretorio = new File("reports/Pix");
        //if (!diretorio.exists()) { diretorio.mkdirs(); }
        
        pdf.setPathName("reports/Pix/" + Dates.iYear(new Date()) + "/" + Dates.Month(new Date()) + "/");
        String docName = "PIX_" + Dates.DateFormata("ddMMyyyy", new Date()) + "_" + contrato + "_" + 
                Dates.StringtoString(jVencimentos.getSelectedItem().toString(),"dd/MM/yyyy","ddMMyyyy") + "_" + pixID + ".pdf";
        pdf.setDocName(docName);
        
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);

        pdf.open();
        
        // Logo
        com.itextpdf.text.Image img;
        try {
            img = com.itextpdf.text.Image.getInstance("resources/logos/boleta/" + VariaveisGlobais.dCliente.get("marca").trim() + ".gif");
            img.setAlignment(Element.ALIGN_LEFT);        
            pdf.doc_add(img);
        } catch (Exception e) { e.printStackTrace(); }
        
        Paragraph p;
        
        p = pdf.print(gVar.get("empresa"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        try {
            if (!gVar.get("cnpj").trim().equals("") || gVar.get("cnpj") != null) {
                p = pdf.print(gVar.get("tipodoc"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT,pdf.BLACK);
                pdf.doc_add(p);
            }
        } catch (Exception e) {}
        
        p = pdf.print(gVar.get("endereco") + ", " + gVar.get("numero") + " " + gVar.get("complemento"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(gVar.get("bairro") + " - " + gVar.get("cidade") + " - " + gVar.get("estado") + " - " + gVar.get("cep"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Tel/Fax:" + gVar.get("telefone"), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("RECIBO DE COMPENSAÇÃO", pdf.HELVETICA, 12, pdf.BOLD, pdf.CENTER, pdf.BLUE);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        columnWidths = new float[] {37, 63 };
        PdfPTable table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        
        PdfPCell cell1 = new PdfPCell(new Phrase("Gerado por: " + VariaveisGlobais.usuario,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        PdfPCell cell2 = new PdfPCell(new Phrase("Data/Hora: " + Dates.DateFormata("dd/MM/yyyy HH:mm", new Date()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        LineSeparator l = new LineSeparator();
        l.setPercentage(100f);
        p.add(new Chunk(l));
        pdf.doc_add(p);

        // Dados do locatario
        columnWidths = new float[] {35, 65 };
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);

        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);        
        cell1 = new PdfPCell(new Phrase("Locatário: " + contrato,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(jNomeLoca.getSelectedItem().toString()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        
        font = new com.itextpdf.text.Font(bf, 9, Font.PLAIN);
        cell1 = new PdfPCell(new Phrase("Imóvel: " + jRgimv.getText(),font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("Vencimento: " + jVencimentos.getSelectedItem().toString(),font));
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
        table.completeRow();
        pdf.doc_add(table);
        
        p = pdf.print(StringManager.ConvStr(jEndereco.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr(jBairro.getText() + " - " + jCidade.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print(StringManager.ConvStr(jEstado.getText() + " - Cep: " + jCep.getText()), pdf.HELVETICA, 9, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        // Cabeçario do recibo
        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font.setColor(BaseColor.WHITE);
        cell1 = new PdfPCell(new Phrase("DESCRIMINAÇÃO",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("C/P",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell2);
        PdfPCell cell3 = new PdfPCell(new Phrase("VALOR", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.BLACK);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        columnWidths = new float[] {50, 20, 30};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        Object[][] linhas = ListaCamposRecibo_pdf();
        for (int i=0; i<linhas.length;i++) {
            // Dados do recibo
            font.setColor(BaseColor.BLACK);
            cell1 = new PdfPCell(new Phrase((String) linhas[i][0],font));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell1);
            cell2 = new PdfPCell(new Phrase((String) linhas[i][1],font));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell2);
            cell3 = new PdfPCell(new Phrase((String) linhas[i][2], font));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setBackgroundColor(BaseColor.WHITE);
            table.addCell(cell3);
        }
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase("==========", font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);

        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Total do Recibo",font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase("",font));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);
        cell3 = new PdfPCell(new Phrase(jVrRecibo.getText(), font));
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell3);
        table.completeRow();
        pdf.doc_add(table);

        p = pdf.print("\n", pdf.HELVETICA, 9, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);

        columnWidths = new float[] {35, 65};
        table = new PdfPTable(columnWidths);
        table.setHeaderRows(0);
        table.setWidthPercentage(100);
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        font.setColor(BaseColor.BLACK);
        cell1 = new PdfPCell(new Phrase("Propriet(s): " + rgprp,font));
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setBorder(Rectangle.TOP);
        cell1.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell1);
        cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(jNomeProp.getText()),font));
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setBorder(Rectangle.TOP);
        cell2.setBackgroundColor(BaseColor.WHITE);
        table.addCell(cell2);

        if (VariaveisGlobais.impPropDiv) {
            Object[][] div = {};
            try {div = conn.ReadFieldsTable(new String[] {"benefs"}, "divisao", "rgprp = '" + rgprp + "' AND rgimv = '" + rgimv + "'");} catch (SQLException ex) {}
            if (div != null) {
                if (div.length > 0) {
                    for (int q=0;q<div.length;q++) {
                        String[] cpos = div[q][3].toString().split(";");
                        for (int z=0; z<cpos.length;z++) {
                            String[] scpos = cpos[z].split(":");
                            if (scpos.length > 0) {
                                Object[][] nmProDiv = {};
                                try {nmProDiv = conn.ReadFieldsTable(new String[] {"nome"}, "proprietarios", "rgprp = '" + scpos[0] + "'");} catch (SQLException ex) {}
                                try {
                                    font.setColor(BaseColor.BLACK);
                                    cell1 = new PdfPCell(new Phrase("Propriet(s): " + scpos[0],font));
                                    cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell1.setBorder(Rectangle.NO_BORDER);
                                    cell1.setBackgroundColor(BaseColor.WHITE);
                                    table.addCell(cell1);
                                    cell2 = new PdfPCell(new Phrase(StringManager.ConvStr(nmProDiv[0][3].toString()),font));
                                    cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell2.setBorder(Rectangle.NO_BORDER);
                                    cell2.setBackgroundColor(BaseColor.WHITE);
                                    table.addCell(cell2);
                                } catch (Exception e) {}
                            }
                        }
                    }
                }
            }
        }
        table.completeRow();
        pdf.doc_add(table);
        
        font = new com.itextpdf.text.Font(bf, 8, Font.PLAIN);
        // Mensagem do Recibo
        String msgem = "";
        try {
            try { msgem = conn.ReadFieldsTable(new String[] {"msgboleta"}, "locatarios", "contrato = '" + contrato + "'")[0][3].toString();} catch (SQLException ex) {msgem = "";}
            if (!msgem.trim().equalsIgnoreCase("")) {
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("__________ MENSAGEM __________", pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print(msgem, pdf.HELVETICA, 7, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
                pdf.doc_add(p);
                p = pdf.print("\n", pdf.HELVETICA, 7, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
                pdf.doc_add(p);
            }
        } catch (Exception e) {}

        p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("O recebimento depois do prazo de pagamento gera encargos, multa, juros e correção, no próximo vencimento.", pdf.HELVETICA, 6, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("ABAIXO O QRCODE E O CÓDIGO COPIA E COLA DO PIX", pdf.HELVETICA, 6, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        
        Map<EncodeHintType, Object> qrParam = new HashMap<EncodeHintType, Object> ();
        qrParam.put ( EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M );
        qrParam.put ( EncodeHintType.CHARACTER_SET, "UTF-8" );

        BarcodeQRCode code25 = new BarcodeQRCode(ChavePix, 66, 66, qrParam);
        com.itextpdf.text.Image cdbar = null;
        try { cdbar = code25.getImage(); } catch (Exception e) {}
        cdbar.setAlignment(Element.ALIGN_CENTER);
        pdf.doc_add(cdbar);            

        // Copia e Cola
        p = pdf.print(ChavePix, pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);

        p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.CENTER, pdf.BLACK);
        pdf.doc_add(p);
        p = pdf.print("Anexe o comprovante de pagamento do PIX juntamente a este demonstrativo para comprovação do pagamento.", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
        pdf.doc_add(p);
        
        // Pula linhas (6) / corta papel
        for (int k=1;k<=6;k++) { 
            p = pdf.print("\n", pdf.HELVETICA, 6, pdf.NORMAL, pdf.LEFT, pdf.BLACK);
            pdf.doc_add(p);
        }
        
        pdf.close();
        pdf.setPathName("");
        pdf.setDocName("");
        return docName;
   }
   
    private Object[][] ListaCamposRecibo_pdf() {
        Object[][] linhas = {};
        String cCampos[] = VariaveisGlobais.ccampos.split(";");
        Arrays.sort (cCampos, new Comparator()
        {
            private int pos1 = 3;
            private int pos2 = 4;
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(pos1, pos2);
                String p2 = ((String)o2).substring(pos1, pos2);
                return p1.compareTo(p2);
            }
        });

        int i = 0; int maxcpos = jctCampos.getComponentCount(); int j = 0;
        String saida = ""; String nmCampo = ""; int newLine = 0;
        String posAnt = "";
        
        String pitem = "", pcota = "", pvalor = "";
        for (i=0; i <= maxcpos - 1; i++) {
            if (jctCampos.getComponent(i) instanceof JLabel) {
                saida = ((JLabel) jctCampos.getComponent(i)).getText();
                nmCampo = ((JLabel) jctCampos.getComponent(i)).getName();
                newLine = 0;

                String alinha = "";
                if (!VariaveisGlobais.bShowCotaParcela) {
                    alinha = new Pad(saida, 25).RPad();
                } else {
                    alinha = new Pad(saida, 20).RPad();
                }
                saida = alinha;
                posAnt = "1";
                
                // Nome do item
                pitem = saida.trim();
            } else if (jctCampos.getComponent(i) instanceof JFormattedTextField) {
                saida = ((JFormattedTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JFormattedTextField) jctCampos.getComponent(i)).getName();
                newLine = 1;

                String alinha = ""; 
                if (VariaveisGlobais.bShowCotaParcela) {
                    if (nmCampo.toLowerCase().trim().indexOf("cota") > -1) {
                        pcota = new Pad(("00/00".equals(saida.trim()) || "00/0000".equals(saida.trim()) || "99/00".equals(saida.trim()) || "99/0000".equals(saida.trim()) ? "       " : saida), 7).LPad();
                        newLine = 0;
                        posAnt = "2";
                    } else {
                        if ("2".equals(posAnt)) {
                            alinha = new Pad(saida, 29).LPad();
                        } else {
                            alinha = new Pad(saida, 29 + 7).LPad();
                        }
                        newLine = 1;
                    }
                    saida = alinha;
                } else {
                    alinha = new Pad(saida, 31).LPad();
                    saida = alinha;
                }
                
                // Valor do Item
                pvalor = saida.trim();
            } else if (jctCampos.getComponent(i) instanceof JTextField) {
                saida = ((JTextField) jctCampos.getComponent(i)).getText();
                nmCampo = ((JTextField) jctCampos.getComponent(i)).getName();
                newLine = 0;
                
                // Cota/Parcela do Item
                pcota = saida.trim();
            } else if (jctCampos.getComponent(i) instanceof JCheckBox) {
                boolean simnao = ((JCheckBox) jctCampos.getComponent(i)).isSelected();
                saida = "";
                nmCampo = ((JCheckBox) jctCampos.getComponent(i)).getName();
                newLine = 0;
            } else { saida = ""; newLine = 0; pitem = ""; pcota = ""; pvalor = "";}

            if (newLine > 0) {
                Object[] tmp =  {pitem,pcota,pvalor};
                linhas =  FuncoesGlobais.ObjectsAdd(linhas, tmp);
                pitem = ""; pcota = ""; pvalor = "";
            }
        }
        return linhas;
    }

    public void Listar() {
        String[] pRecibos = {};
        int vias = jVencimentos.getItemCount();
        for (int i=0;i<vias;i++) {
            jVencimentos.setSelectedIndex(i);
            float rc = LerValor.StringToFloat(jVrRecibo.getText());

            // imprime recibo
            String doc = ImprimeReciboPDF(-1, new String[][] {}, LerValor.floatToCurrency(rc, 2), "F",i+1,vias);
            
            if (doc.trim() != "") pRecibos = FuncoesGlobais.ArrayAdd(pRecibos, doc);
        }
        if (pRecibos.length > 0) {
            String tmpdir = System.getProperty("java.io.tmpdir");
            String separator = FileSystems.getDefault().getSeparator();
            String outFileName = tmpdir + separator + contrato + "_" + Dates.DateFormata("ddMMyyyy", new Date()) + 
                    "_" + FuncoesGlobais.StrZero(String.valueOf((int)vias), 2) + "_openrecibos.pdf";
            try {
               List<InputStream> pdfs = new ArrayList<InputStream>();
               for (String item : pRecibos) { pdfs.add(new FileInputStream(item)); }
               OutputStream output = new FileOutputStream(outFileName);
               MergePDF.concatPDFs(pdfs, output, true);
            } catch (Exception e) {
               e.printStackTrace();
            }
            
            try {
                Object[][] EmailLocaDados = null;
                try {
                    EmailLocaDados = conn.ReadFieldsTable(new String[] {"nomerazao","email"}, "locatarios", "contrato = '" + contrato + "'");
                } catch (SQLException e) {}

                String EmailLoca = null;
                if (EmailLocaDados != null) EmailLoca = EmailLocaDados[1][3].toString().toLowerCase();

                String To = EmailLoca.trim().toLowerCase();
                String Subject = "Recibo(s) em Aberto(s)";
                String Body = "Em anexos o(s) recibo(s) que constam em aberto em nosso sistema.\n\nQualquer dúvida entrar em contato.\n\nA Gerência.";

                Gmail service = GmailAPI.getGmailService();
                MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(outFileName));
                Message message = createMessageWithEmail(Mimemessage);
                message = service.users().messages().send("me", message).execute();

                System.out.println("Message id: " + message.getId());
                System.out.println(message.toPrettyString());
                if (message.getId() == null) {
                    JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nVerifique o endereço de email e Tente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente, se persistir chame o suporte...", "Atenção", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            // Deleta Previews
            for (String item : pRecibos) {
                new File(item).delete();
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jBairro;
    private javax.swing.JLabel jCep;
    private javax.swing.JLabel jCidade;
    private javax.swing.JComboBox jContrato;
    private javax.swing.JLabel jEndereco;
    private javax.swing.JLabel jEstado;
    private javax.swing.JButton jImprimir;
    private javax.swing.JButton jImprimir1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox jNNumero;
    private javax.swing.JComboBox jNomeLoca;
    private javax.swing.JLabel jNomeProp;
    private javax.swing.JLabel jObs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner jQtde;
    private javax.swing.JLabel jRgimv;
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JFormattedTextField jTotalRecibos;
    private javax.swing.JComboBox jVencimentos;
    private javax.swing.JFormattedTextField jVrRecibo;
    private javax.swing.JButton jbtretencão;
    private javax.swing.JCheckBox jchb1Via;
    private javax.swing.JPanel jctCampos;
    private javax.swing.JPanel jpRecebe;
    private javax.swing.JToggleButton jtgbAvulso;
    private javax.swing.JLabel jvrRetencao;
    private javax.swing.JLabel lblQtde;
    // End of variables declaration//GEN-END:variables

}