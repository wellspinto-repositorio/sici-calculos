package Sici.Locatarios;

import Funcoes.*;
import Sici.jTelsCont;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class jLocatarios extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    private boolean bNew = false;
    private String _botoes = null;

    // Botoes que acompanham a tela
    private boolean _Incluir = true;
    private boolean _Alterar = true;
    private boolean _Excluir = true;
    private boolean _Carteira = true;
    private boolean _Fiador = true;
    private boolean _Pagtos = true;
    
    public void setBotoes(String _botoes) {
        this._botoes = _botoes;
    }
        
    /** Creates new form jLocatarios */
    public jLocatarios() {
        initComponents();

        SwingUtilities.invokeLater(() -> {
            if (_botoes != null) {
                String[] btn = _botoes.split(" ");
                int Pos = FuncoesGlobais.IndexOf(btn, "inclusao");
                if (Pos > -1) {
                    String[] _btn = btn[Pos].split("=");
                    _Incluir = new Boolean(_btn[1].replace("\"", ""));
                }
                
                Pos = FuncoesGlobais.IndexOf(btn, "alteracao");
                if (Pos > -1) {
                    String[] _btn = btn[Pos].split("=");
                    _Alterar = new Boolean(_btn[1].replace("\"", ""));
                }
                
                Pos = FuncoesGlobais.IndexOf(btn, "exclusao");
                if (Pos > -1) {
                    String[] _btn = btn[Pos].split("=");
                    _Excluir = new Boolean(_btn[1].replace("\"", ""));
                }
                
                Pos = FuncoesGlobais.IndexOf(btn, "carteira");
                if (Pos > -1) {
                    String[] _btn = btn[Pos].split("=");
                    _Carteira = new Boolean(_btn[1].replace("\"", ""));
                }
                
                Pos = FuncoesGlobais.IndexOf(btn, "fiador");
                if (Pos > -1) {
                    String[] _btn = btn[Pos].split("=");
                    _Fiador = new Boolean(_btn[1].replace("\"", ""));
                }
                
                Pos = FuncoesGlobais.IndexOf(btn, "pagamentos");
                if (Pos > -1) {
                    String[] _btn = btn[Pos].split("=");
                    _Pagtos = new Boolean(_btn[1].replace("\"", ""));
                }
            }
            
            btIncluir.setEnabled(_Incluir);
            btExcluir.setEnabled(_Excluir);
            btCarteira.setEnabled(_Carteira);
            btFiador.setEnabled(_Fiador);
            btPagtos.setEnabled(_Pagtos);
            btGravar.setEnabled(_Alterar);
            
            btFicha.setEnabled(_Incluir);
            btExcluirFicha.setEnabled(_Excluir);
        });        
        
        // Icone da tela
        ImageIcon icone = new FlatSVGIcon("menuIcons/locatário.svg",16,16);
        setFrameIcon(icone);
        
        if (!VariaveisGlobais.Iloca) {
            //setBackground(new java.awt.Color (237, 236, 235));
        } else { setBackground(new java.awt.Color(255, 0, 0)); }
        
        try {LerDados(null);} catch (SQLException ex) {}

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        SelectAll();
        
        mTipoDoc.setEditable(false); mTipoDoc.setEnabled(false);
        mCpfCnpj.setEnabled(false);
        
        new CamposScreen(mCpfCnpj,"CPF");        
    }

    private void SelectAll() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent ke) -> {
            if(ke.getID()==KeyEvent.KEY_RELEASED) {
                int key = ke.getKeyCode();
                if(key == KeyEvent.VK_ENTER) {
                    Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if(comp instanceof JTextField) {
                        ((JTextField)comp).selectAll();
                    }
                }
            }
            return false;
        });
    }

    public boolean MoveToLoca(String campo, String seek) throws SQLException {
        boolean achei = false;
        String sInativos = ""; Object[][] param = null;
        sInativos = (!VariaveisGlobais.Iloca ? "WHERE " + campo + " = :contrato AND (fiador1uf <> 'X' OR IsNull(fiador1uf))" : "WHERE " + campo + " = :contrato AND ( fiador1uf = 'X'");
        param = new Object[][] {{"int", "contrato", Integer.parseInt(seek)}};
        ResultSet pResult = conn.OpenTable("SELECT * FROM locatarios " + sInativos + " ORDER BY contrato LIMIT 1;", ResultSet.CONCUR_UPDATABLE, param);

        int _contrato =  -1;
        try {
            while (pResult.next()) {
                _contrato = pResult.getInt("contrato");
                achei = true;
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(pResult);
        if (!achei) LimpaDados();
        try { LerDados(_contrato); } catch (Exception ex) {}
        return achei;
    }

    private void LerDados(Object contrato) throws SQLException {
        String sInativos = ""; Object[][] param = null;
        if (contrato == null) {
            sInativos = (!VariaveisGlobais.Iloca ? "WHERE fiador1uf <> 'X' OR IsNull(fiador1uf)" : "WHERE fiador1uf = 'X'");
        } else {
            sInativos = (!VariaveisGlobais.Iloca ? "WHERE contrato = :contrato AND (fiador1uf <> 'X' OR IsNull(fiador1uf))" : "WHERE contrato = :contrato AND fiador1uf = 'X'");
            param = new Object[][] {{"int", "contrato", Integer.parseInt(contrato.toString())}};
        }
        ResultSet pResult = conn.OpenTable("SELECT * FROM locatarios " + sInativos + " ORDER BY contrato LIMIT 1;", ResultSet.CONCUR_UPDATABLE, param);
        
        if (conn.RecordCount(pResult) <= 0) {
            conn.CloseTable(pResult);
            return;
        }

        try {
            while (pResult.next()) {
                mRgprp.setText(Integer.toString(pResult.getInt("rgprp")));
                mRgimv.setText(Integer.toString(pResult.getInt("rgimv")));
                mTpImv.setText(pResult.getString("tpimovel"));
                mContrato.setText(pResult.getString("contrato"));
                if (pResult.getString("tploca").toUpperCase().contains("F")) {
                    mTipoDoc.setSelectedIndex(0);
                    mCpfCnpj.setText(pResult.getString("cpfcnpj"));

                    jDados.setEnabledAt(0, false);
                    jDados.setEnabledAt(1, true);
                    jDados.setSelectedIndex(1);
                } else {
                    mTipoDoc.setSelectedIndex(1);
                    mCpfCnpj.setText(pResult.getString("cpfcnpj"));

                    jDados.setEnabledAt(0, true);
                    jDados.setEnabledAt(1, false);
                    jDados.setSelectedIndex(0);
                }
                mIdentidade.setText(pResult.getString("rginsc"));

                if (mTipoDoc.getSelectedIndex() == 0) {
                    // Pessoa Física (limpar dados dos campos juridica)
                    mfNome.setText(pResult.getString("nomerazao"));
                    mfSexo.getModel().setSelectedItem(pResult.getString("sexo"));

                    try {mfDtNasc.setDate(Dates.StringtoDate(pResult.getString("dtnasc"), "yyyy-MM-dd"));} catch (Exception ex) {mfDtNasc.setDate(null);}
                    mfNacionalidade.setText(pResult.getString("naciona"));

                    mfTel.removeAllItems();
                    String[] telef = null;
                    try { 
                        String _telef = pResult.getString("tel");
                        if (_telef != null) {
                            telef = _telef.trim().split(";");
                            for (String telef1 : telef) {
                                if (!telef1.trim().equalsIgnoreCase("")) {
                                    String[] tmptel = telef1.split(",");
                                    String teltmp = tmptel[0];
                                    if (tmptel.length > 1) teltmp += " * " + tmptel[1];
                                    mfTel.addItem(teltmp);
                                }
                            }
                        }
                    } catch (SQLException e) {}
                    
                    String pCivil = pResult.getString("ecivil").trim().toLowerCase();
                    String sCivil[] = {"solteiro","solteira","casado","casada","separado","separada","divorciado","divorciada","viuvo","viuva"};
                    Integer nPos = FuncoesGlobais.IndexOf2(sCivil,pCivil) ;
                    switch (nPos) {
                        case -1:
                            nPos = 0;
                            break;
                        case 0:
                        case 1:
                            nPos = 0;
                            break;
                        case 2:
                        case 3:
                            nPos = 1;
                            break;
                        case 4:
                        case 5:
                            nPos = 2;
                            break;
                        case 6:
                        case 7:
                            nPos = 3;
                            break;
                        case 8:
                        case 9:
                            nPos = 4;
                            break;
                        default:
                            nPos = 0;
                            break;
                    }

                    mfEstCivil.setSelectedIndex(nPos);
                    mfMae.setText(pResult.getString("mae"));
                    mfPai.setText(pResult.getString("pai"));
                    mfEmpresa.setText(pResult.getString("empresa"));

                    try {mfDtAdmis.setDate(Dates.StringtoDate(pResult.getString("dtadmis"), "yyyy-MM-dd"));} catch (SQLException ex) {mfDtAdmis.setDate(null);}
                    mfEndereco.setText(pResult.getString("end"));
                    mfNumero.setText(pResult.getString("num"));
                    mfCplto.setText(pResult.getString("compl"));
                    mfBairro.setText(pResult.getString("bairro"));
                    mfCidade.setText(pResult.getString("cidade"));
                    mfEstado.setText(pResult.getString("estado"));
                    mfCep.setText(pResult.getString("cep"));
                    mfTelEmpresa.setText(FuncoesGlobais.rmvNumero(pResult.getString("tel")));
                    mfRamalEmpresa.setText(pResult.getString("ramal"));
                    mfCargo.setText(pResult.getString("cargo"));
                    mfSalario.setText(pResult.getString("salario"));
                    mfEmail.setText(pResult.getString("email"));
                    mfConjugue.setText(pResult.getString("conjugue"));
                    mfConjSexo.getModel().setSelectedItem(pResult.getString("conjsexo"));
                    try {mfDtNascConj.setDate(Dates.StringtoDate(pResult.getString("cjdtnasc"), "yyyy-MM-dd"));} catch (SQLException ex) {mfDtNascConj.setDate(null);}
                    mfCpfConj.setText(pResult.getString("cjcpf"));
                    mfIdentidadeConj.setText(pResult.getString("cjrg"));
                    mfSalarioConj.setText(pResult.getString("cjsalario"));
                } else {
                    // Pessoa Jurica (limpar dados dos campos física)
                    mjRazao.setText(pResult.getString("nomerazao"));
                    mjFantasia.setText(pResult.getString("fantasia"));
                    mjEndereco.setText(pResult.getString("end"));
                    mjNumero.setText(pResult.getString("num"));
                    mjCplto.setText(pResult.getString("compl"));
                    mjBairro.setText(pResult.getString("bairro"));
                    mjCidade.setText(pResult.getString("cidade"));
                    mjEstado.setText(pResult.getString("estado"));
                    mjCep.setText(pResult.getString("cep"));
                    
                    mjTel.removeAllItems();
                    String[] telef = null;
                    try { 
                        String _telef = pResult.getString("tel");
                        if (_telef != null) {
                            telef = _telef.trim().split(";");
                            for (String telef1 : telef) {
                                if (!telef1.trim().equalsIgnoreCase("")) {
                                    String[] tmptel = telef1.split(",");
                                    String teltmp = tmptel[0];
                                    if (tmptel.length > 1) teltmp += " * " + tmptel[1];
                                    mjTel.addItem(teltmp);
                                }
                            }
                        }
                    } catch (SQLException e) {}

                    try {mjDtContratoSocial.setDate(Dates.StringtoDate(pResult.getString("dtnasc"), "yyyy-MM-dd"));} catch (Exception ex) {mjDtContratoSocial.setDate(null);}
                    mjEmail.setText(pResult.getString("email"));

                    FillSocios(jSocios);
                }

                mAviso.setText(pResult.getString("aviso"));
                mBoleta.setSelected((pResult.getInt("boleta") != 0));
                jcbxBcoBoleta.setEnabled(mBoleta.isSelected());
                jcbxEnvio.setSelectedIndex(pResult.getInt("envio"));
                jCheckBoxCobAut.setSelected((pResult.getInt("cobaut") != 0));
                jSpinnerCobDias.setValue(pResult.getInt("cobdias"));
                jCheckBoxCobEmail.setSelected((pResult.getInt("cobemail") != 0));
                jCheckBoxCobWhatsApp.setSelected((pResult.getInt("cobwhatsapp") != 0));

                String tbco = ""; int nbco = 0;
                try {
                    tbco = pResult.getString("bcobol");
                    if (tbco.equalsIgnoreCase("104")) {
                        nbco = 0;
                    } else if (tbco.equalsIgnoreCase("341")) {
                        nbco = 1;
                    } else if (tbco.equalsIgnoreCase("033")) {
                        nbco = 2;
                    } else if (tbco.equalsIgnoreCase("001")) {
                        nbco = 3;
                    } else if (tbco.equalsIgnoreCase("237")) {
                        nbco = 4;
                    } else if (tbco.equalsIgnoreCase("077")) {
                        nbco = 5;
                    } else nbco = -1;
                } catch (SQLException e) {}
                jcbxBcoBoleta.setSelectedIndex(nbco);

                mMsgBol.setText(pResult.getString("msgboleta"));

                FillFiadores(tbFiadores, String.valueOf(pResult.getInt("contrato")));

                if ((" " + pResult.getString("fiador1uf")).trim().equals("X")) {
                    btIncluir.setEnabled(false && _Incluir);
                    btCarteira.setEnabled(false && _Carteira);
                    btGravar.setEnabled(false && _Alterar);
                    VariaveisGlobais.isBloqueado = true;

                    jDadosIniciais.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Locatário INATIVO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(255, 0, 0))); // NOI18N
                    jDadosIniciais.setFont(new java.awt.Font("SansSerif", 0, 8)); // NOI18N
                } else {
                    btIncluir.setEnabled(true && _Incluir);
                    btCarteira.setEnabled(true && _Carteira);
                    btGravar.setEnabled(true && _Alterar);
                    VariaveisGlobais.isBloqueado = false;

                    jDadosIniciais.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                    jDadosIniciais.setFont(new java.awt.Font("SansSerif", 0, 8)); // NOI18N
                }
            }
        } catch (SQLException sqlEx) {sqlEx.printStackTrace();}
        conn.CloseTable(pResult);
        
        SwingUtilities.invokeLater(() -> {
            if (mTipoDoc.getSelectedIndex() == 0) {
                mfNome.requestFocus();
            } else {
                mjRazao.requestFocus();
            }
        });        
    }
    
    private void LimpaDados() {
        mRgprp.setText("");
        mRgimv.setText("");
        mTpImv.setText("");
        mContrato.setText("");

        mCpfCnpj.setText("");
        mIdentidade.setText("");
        
        jFisica.setVisible(true);
        jJuridica.setVisible(false);

        // Pessoa Física (limpar dados dos campos juridica)
        mfNome.setText("");
        mfSexo.setSelectedIndex(0);
        mfDtNasc.setDate(null);
        mfNacionalidade.setText("");
        mfEstCivil.setSelectedIndex(0);

        mfTel.removeAllItems();
        mfTel.setSelectedItem("");            

        mfMae.setText("");
        mfPai.setText("");
        mfEmpresa.setText("");
        mfDtAdmis.setDate(null);
        mfEndereco.setText("");
        mfNumero.setText("");
        mfCplto.setText("");
        mfBairro.setText("");
        mfCidade.setText("");
        mfEstado.setText("");
        mfCep.setText("");
        mfTelEmpresa.setText("");
        mfRamalEmpresa.setText("");
        mfCargo.setText("");
        mfSalario.setText("");
        mfEmail.setText("");
        mfConjugue.setText("");
        mfConjSexo.setSelectedIndex(0);
        mfDtNascConj.setDate(null);
        mfCpfConj.setText("");
        mfIdentidadeConj.setText("");
        mfSalarioConj.setText("");
        TableControl.header(tbFiadores, new String[][] {{"contrato","cpfcnpj","nomerazao"},{"50","120","500"}});

        jFisica.setVisible(true);
        jJuridica.setVisible(false);

        // Pessoa Jurica (limpar dados dos campos física)
        mjRazao.setText("");
        mjFantasia.setText("");
        mjEndereco.setText("");
        mjNumero.setText("");
        mjCplto.setText("");
        mjBairro.setText("");
        mjCidade.setText("");
        mjEstado.setText("");
        mjCep.setText("");

        mjTel.removeAllItems();
        mjTel.setSelectedItem("");            

        mjDtContratoSocial.setDate(null);
        mjEmail.setText("");

        TableControl.header(jSocios, new String[][] {{"cpfcnpj","nomerazao","cargo"},{"150","500","150"}});
        
        mAviso.setText("");
        mMsgBol.setText("");
        
        mBoleta.setSelected(false);
        jCheckBoxCobAut.setSelected(false);
        jSpinnerCobDias.setValue(0);
        jCheckBoxCobEmail.setSelected(false);
        jCheckBoxCobWhatsApp.setSelected(false);
        
        jcbxBcoBoleta.setEnabled(false);
        jcbxEnvio.setEnabled(false);
        jCheckBoxCobAut.setEnabled(false);
        jSpinnerCobDias.setEnabled(false);
        jCheckBoxCobEmail.setEnabled(false);
        jCheckBoxCobWhatsApp.setEnabled(false);
        
        jcbxBcoBoleta.setSelectedIndex(0);
        jcbxEnvio.setSelectedIndex(0);        
    }

    private void GravarDados() throws SQLException {
        int iNewContrato = 0;
        if (bNew) {
            int NewContrato = 0;
            NewContrato = Integer.parseInt(conn.ReadParameters("CONTRATO"));
            iNewContrato = NewContrato + 1;

            String cPar[] = {"CONTRATO",String.valueOf(iNewContrato),"NUMERICO"};
            try {
                conn.SaveParameters(cPar);
            } catch (SQLException ex) { ex.printStackTrace(); }

        } else iNewContrato = Integer.parseInt(mContrato.getText());
        
        String telef = "";
        for (int w=0; w < mfTel.getItemCount(); w++) {
            String tmptel = mfTel.getModel().getElementAt(w).toString();
            try {
                telef += tmptel.substring(0, tmptel.indexOf("*") - 1) + "," + tmptel.substring(tmptel.indexOf("*") + 1) + ";";
            } catch (Exception e) {telef = tmptel;}
        }
        
        String jtelef = "";
        for (int w=0; w < mjTel.getItemCount(); w++) {
            String tmptel = mjTel.getModel().getElementAt(w).toString();
            try {
                jtelef += tmptel.substring(0, tmptel.indexOf("*") - 1) + "," + tmptel.substring(tmptel.indexOf("*") + 1) + ";";
            } catch (Exception e) {jtelef = tmptel;}
        }
        
        String query = ""; Object[][] param = null;
        if (bNew) {
            if (mTipoDoc.getSelectedIndex() == 0) {
                query = "INSERT INTO `locatarios` (`rgprp`, `rgimv`, `tpimovel`, `contrato`, `tploca`, `cpfcnpj`, `rginsc`, `nomerazao`, " + 
                        "`sexo`, `dtnasc`, `naciona`, `ecivil`, `tel`, `mae`, " + 
                        "`pai`, `empresa`, `dtadmis`, `end`, `num`, `compl`, `bairro`, `cidade`, `estado`, `cep`, `fiador1tel`, `ramal`, " + 
                        "`cargo`, `salario`, `email`, `conjugue`, `conjsexo`, `cjdtnasc`, `cjcpf`, `cjrg`, `cjsalario`, " + 
                        "`aviso`, `msgboleta`, `boleta`, `envio`, `bcobol`, `cobaut`, `cobdias`, `cobemail`, `cobwhatsapp`) VALUES (" + 
                        ":rgprp, :rgimv, :tpimovel, :contrato, :tploca, :cpfcnpj, :rginsc, :nomerazao, " + 
                        ":sexo, :dtnasc, :naciona, :ecivil, :tel, :mae, " + 
                        ":pai, :empresa, :dtadmis, :end, :num, :compl, :bairro, :cidade, :estado, :cep, :fiador1tel, :ramal, " + 
                        ":cargo, :salario, :email, :conjugue, :conjsexo, :cjdtnasc, :cjcpf, :cjrg, :cjsalario, " + 
                        ":aviso, :msgboleta, :boleta, :envio, :bcobol, :cobaut, :cobdias, :cobemail, :cobwhatsapp);";
                param = new Object[][] {
                    {"int", "rgprp", Integer.parseInt(mRgprp.getText().trim())},
                    {"int", "rgimv", Integer.parseInt(mRgimv.getText().trim())},
                    {"string", "tpimovel", mTpImv.getText().trim()},
                    {"int", "contrato", iNewContrato},
                    {"string", "tploca", mTipoDoc.getSelectedItem().toString().trim()},
                    {"string", "cpfcnpj", mCpfCnpj.getText().trim()},
                    {"string", "rginsc", mIdentidade.getText().trim()},
                    {"string", "nomerazao", mfNome.getText().trim()},
                    {"string", "sexo", mfSexo.getSelectedItem().toString().trim()},
                    {"date", "dtnasc", mfDtNasc.getDate()},
                    {"string", "naciona", mfNacionalidade.getText().trim()},
                    {"string", "ecivil", (mfEstCivil.getSelectedItem().toString() + "          ").substring(0, 10)},
                    {"string", "tel", !telef.isEmpty() ? telef : null},
                    {"string", "mae", mfMae.getText().trim()},
                    {"string", "pai", mfPai.getText().trim()},
                    {"string", "empresa", mfEmpresa.getText().trim()},
                    {"date", "dtadmis", mfDtAdmis.getDate()},
                    {"string", "end", mfEndereco.getText().trim()},
                    {"string", "num", mfNumero.getText().trim()},
                    {"string", "compl", mfCplto.getText().trim()},
                    {"string", "bairro", mfBairro.getText().trim()},
                    {"string", "cidade", mfCidade.getText().trim()},
                    {"string", "estado", mfEstado.getText().trim()},
                    {"string", "cep", mfCep.getText().trim()},
                    {"string", "fiador1tel", mfTelEmpresa.getText().trim()},
                    {"string", "ramal", mfRamalEmpresa.getText().trim()},
                    {"string", "cargo", mfCargo.getText().trim()},
                    {"string", "salario", mfSalario.getText().trim()},
                    {"string", "email", mfEmail.getText().trim()},
                    {"string", "conjugue", mfConjugue.getText().trim()},
                    {"string", "conjsexo", mfConjSexo.getSelectedItem().toString().trim()},
                    {"date", "cjdtnasc", mfDtNascConj.getDate()},
                    {"string", "cjcpf", mfCpfConj.getText().trim()},
                    {"string", "cjrg", mfIdentidadeConj.getText().trim()},
                    {"string", "cjsalario", mfSalarioConj.getText().trim()},
                    {"string", "aviso",mAviso.getText().trim()},
                    {"string", "msgboleta", mMsgBol.getText().trim()},
                    {"boolean", "boleta", mBoleta.isSelected()},
                    {"int", "envio",jcbxEnvio.getSelectedIndex()},
                    {"string", "bcobol", jcbxBcoBoleta.getSelectedItem().toString().trim().substring(0, 3)},
                    {"boolean", "cobaut", jCheckBoxCobAut.isSelected()},
                    {"int", "cobdias", (int)jSpinnerCobDias.getValue()},
                    {"boolean", "cobemail", jCheckBoxCobEmail.isSelected()},
                    {"boolean", "cobwhatsapp", jCheckBoxCobWhatsApp.isSelected()}                    
                };
            } else {
                query = "INSERT INTO `locatarios` (`rgprp`, `rgimv`, `tpimovel`, `contrato`, `tploca`, `cpfcnpj`, `rginsc`, " + 
                        "`nomerazao`, `fantasia`, `end`, `num`, `compl`, `bairro`, `cidade`, `estado`, `cep`, `tel`, `dtnasc`, " + 
                        "`email`, `aviso`, `msgboleta`, `boleta`, `envio`, `bcobol`, `cobaut`, `cobdias`, `cobemail`, `cobwhatsapp`) VALUES (" + 
                        ":rgprp, :rgimv, :tpimovel, :contrato, :tploca, :cpfcnpj, :rginsc, " +
                        ":nomerazao, :fantasia, :end, :num, :compl, :bairro, :cidade, :estado, :cep, :tel, :dtnasc, " + 
                        ":email, :aviso, :msgboleta, :boleta, :envio, :bcobol, :cobaut, :cobdias, :cobemail, :cobwhatsapp);";
                param = new Object[][] {
                    {"int", "rgprp", Integer.parseInt(mRgprp.getText().trim())},
                    {"int", "rgimv", Integer.parseInt(mRgimv.getText().trim())},
                    {"string", "tpimovel", mTpImv.getText().trim()},
                    {"int", "contrato", iNewContrato},
                    {"string", "tploca", mTipoDoc.getSelectedItem().toString().trim()},
                    {"String", "cpfcnpj", mCpfCnpj.getText().trim()},
                    {"string", "rginsc", mIdentidade.getText().trim()},
                    {"string", "nomerazao", mjRazao.getText().trim()},
                    {"string", "fantasia", mjFantasia.getText().trim()},
                    {"string", "end", mjEndereco.getText().trim()},
                    {"string", "num", mjNumero.getText().trim()},
                    {"string", "compl", mjCplto.getText().trim()},
                    {"string", "bairro", mjBairro.getText().trim()},
                    {"string", "cidade", mjCidade.getText().trim()},
                    {"string", "estado", mjEstado.getText().trim()},
                    {"string", "cep", mjCep.getText()},
                    {"string", "tel", !jtelef.isEmpty() ? jtelef : null},
                    {"date", "dtnasc", mjDtContratoSocial.getDate()},
                    {"string", "email", mjEmail.getText().trim()},
                    {"string", "aviso",mAviso.getText().trim()},
                    {"string", "msgboleta", mMsgBol.getText().trim()},
                    {"boolean", "boleta", mBoleta.isSelected()},
                    {"int", "envio",jcbxEnvio.getSelectedIndex()},
                    {"string", "bcobol", jcbxBcoBoleta.getSelectedItem().toString().trim().substring(0, 3)},
                    {"boolean", "cobaut", jCheckBoxCobAut.isSelected()},
                    {"int", "cobdias", (int)jSpinnerCobDias.getValue()},
                    {"boolean", "cobemail", jCheckBoxCobEmail.isSelected()},
                    {"boolean", "cobwhatsapp", jCheckBoxCobWhatsApp.isSelected()}
                };
            }
            mContrato.setText(Integer.toString(iNewContrato));
        } else {
            if (mTipoDoc.getSelectedIndex() == 0) {
                query = "UPDATE `locatarios` SET `rginsc` = :rginsc, `nomerazao` = :nomerazao, `sexo` = :sexo, `dtnasc` = :dtnasc, " + 
                        "`naciona` = :naciona, `ecivil` = :ecivil, `tel` = :tel, `mae` = :mae, `pai` = :pai, `empresa` = :empresa, " + 
                        "`dtadmis` = :dtadmis, `end` = :end, `num` = :num, `compl` = :compl, `bairro` = :bairro, `cidade` = :cidade, " + 
                        "`estado` = :estado, `cep` = :cep, `fiador1tel` = :fiador1tel, `ramal` = :ramal, `cargo` = :cargo, " + 
                        "`salario` = :salario, `email` = :email, `conjugue` = :conjugue, `conjsexo` = :conjsexo, `cjdtnasc` = :cjdtnasc, " + 
                        "`cjcpf` = :cjcpf, `cjrg` = :cjrg, `cjsalario` = :cjsalario, `aviso` = :aviso, `msgboleta` = :msgboleta, `boleta` = :boleta, " + 
                        "`envio` = :envio, `bcobol` = :bcobol, `cobaut` = :cobaut, `cobdias` = :cobdias, `cobemail` = :cobemail, " + 
                        "`cobwhatsapp` = :cobwhatsapp " + 
                        "WHERE `rgprp` = :rgprp AND `rgimv` = :rgimv AND `contrato` = :contrato;";
                param = new Object[][] {
                    {"string", "rginsc", mIdentidade.getText().trim()},
                    {"string", "nomerazao", mfNome.getText().trim()},
                    {"string", "sexo", mfSexo.getSelectedItem().toString().trim()},
                    {"date", "dtnasc", mfDtNasc.getDate()},
                    {"string", "naciona", mfNacionalidade.getText().trim()},
                    {"string", "ecivil", (mfEstCivil.getSelectedItem().toString() + "          ").substring(0, 10)},
                    {"string", "tel", !telef.isEmpty() ? telef : null},
                    {"string", "mae", mfMae.getText().trim()},
                    {"string", "pai", mfPai.getText().trim()},
                    {"string", "empresa", mfEmpresa.getText().trim()},
                    {"date", "dtadmis", mfDtAdmis.getDate()},
                    {"string", "end", mfEndereco.getText().trim()},
                    {"string", "num", mfNumero.getText().trim()},
                    {"string", "compl", mfCplto.getText().trim()},
                    {"string", "bairro", mfBairro.getText().trim()},
                    {"string", "cidade", mfCidade.getText().trim()},
                    {"string", "estado", mfEstado.getText().trim()},
                    {"string", "cep", mfCep.getText().trim()},
                    {"string", "fiador1tel",mfTelEmpresa.getText().trim()},
                    {"string", "ramal", mfRamalEmpresa.getText().trim()},
                    {"string", "cargo", mfCargo.getText().trim()},
                    {"string", "salario", mfSalario.getText().trim()},
                    {"string", "email", mfEmail.getText().trim()},
                    {"string", "conjugue", mfConjugue.getText().trim()},
                    {"string", "conjsexo", mfConjSexo.getSelectedItem().toString().trim()},
                    {"date", "cjdtnasc", mfDtNascConj.getDate()},
                    {"string", "cjcpf", mfCpfConj.getText().trim()},
                    {"string", "cjrg", mfIdentidadeConj.getText().trim()},
                    {"string", "cjsalario", mfSalarioConj.getText().trim()},
                    {"string", "aviso",mAviso.getText().trim()},
                    {"string", "msgboleta", mMsgBol.getText().trim()},
                    {"boolean", "boleta", mBoleta.isSelected()},
                    {"int", "envio",jcbxEnvio.getSelectedIndex()},
                    {"string", "bcobol", jcbxBcoBoleta.getSelectedItem().toString().trim().substring(0, 3)},
                    {"boolean", "cobaut", jCheckBoxCobAut.isSelected()},
                    {"int", "cobdias", (int)jSpinnerCobDias.getValue()},
                    {"boolean", "cobemail", jCheckBoxCobEmail.isSelected()},
                    {"boolean", "cobwhatsapp", jCheckBoxCobWhatsApp.isSelected()},
                    {"int", "rgprp", Integer.parseInt(mRgprp.getText().trim())},
                    {"int", "rgimv", Integer.parseInt(mRgimv.getText().trim())},
                    {"int", "contrato", Integer.parseInt(mContrato.getText().trim())}
                };
            } else {
                query = "UPDATE `locatarios` SET `rginsc` = :rginsc, `nomerazao` = :nomerazao, `fantasia` = :fantasia, `end` = :end, " + 
                        "`num` = :num, `compl` = :compl, `bairro` = :bairro, `cidade` = :cidade, `estado` = :estado, `cep` = :cep, " + 
                        "`tel` = :tel, `dtnasc` = :dtnasc, `email` = :email, `aviso` = :aviso, `msgboleta` = :msgboleta, `boleta` = :boleta, " + 
                        "`envio` = :envio, `bcobol` = :bcobol, `cobaut` = :cobaut, `cobdias` = :cobdias, `cobemail` = :cobemail, " + 
                        "`cobwhatsapp` = :cobwhatsapp WHERE `rgprp` = :rgprp AND `rgimv` = :rgimv AND `contrato` = :contrato;";
                param = new Object[][] {
                    {"string", "rginsc", mIdentidade.getText().trim()},
                    {"string", "nomerazao", mjRazao.getText().trim()},
                    {"string", "fantasia", mjFantasia.getText().trim()},
                    {"string", "end", mjEndereco.getText().trim()},
                    {"string", "num", mjNumero.getText().trim()},
                    {"string", "compl", mjCplto.getText().trim()},
                    {"string", "bairro", mjBairro.getText().trim()},
                    {"string", "cidade", mjCidade.getText().trim()},
                    {"string", "estado", mjEstado.getText().trim()},
                    {"string", "cep", mjCep.getText()},
                    {"string", "tel", !jtelef.isEmpty() ? jtelef : null},
                    {"date", "dtnasc", mjDtContratoSocial.getDate()},
                    {"string", "email", mjEmail.getText().trim()},
                    {"string", "aviso",mAviso.getText().trim()},
                    {"string", "msgboleta", mMsgBol.getText().trim()},
                    {"boolean", "boleta", mBoleta.isSelected()},
                    {"int", "envio",jcbxEnvio.getSelectedIndex()},
                    {"string", "bcobol", jcbxBcoBoleta.getSelectedItem().toString().trim().substring(0, 3)},
                    {"boolean", "cobaut", jCheckBoxCobAut.isSelected()},
                    {"int", "cobdias", (int)jSpinnerCobDias.getValue()},
                    {"boolean", "cobemail", jCheckBoxCobEmail.isSelected()},
                    {"boolean", "cobwhatsapp", jCheckBoxCobWhatsApp.isSelected()},
                    {"int", "rgprp", Integer.parseInt(mRgprp.getText().trim())},
                    {"int", "rgimv", Integer.parseInt(mRgimv.getText().trim())},
                    {"int", "contrato", Integer.parseInt(mContrato.getText().trim())}
                };
            }
        }

        conn.CommandExecute(query, param);

        if (bNew) mContrato.setText(Integer.toString(iNewContrato));
    }

    private void FillSocios(JTable table) {
        String sInativos = ""; Object[][] param = null;
        sInativos = (!VariaveisGlobais.Iloca ? "WHERE contrato = :contrato AND (fiador1uf <> 'X' OR IsNull(fiador1uf))" : "WHERE contrato = :contrato AND fiador1uf = 'X'");
        param = new Object[][] {{"int", "contrato", Integer.parseInt(mContrato.getText().trim())}};
        ResultSet pResult = conn.OpenTable("SELECT * FROM locatarios " + sInativos + " ORDER BY contrato LIMIT 1;", ResultSet.CONCUR_UPDATABLE, param);

        // Seta Cabecario
        TableControl.header(table, new String[][] {{"cpfcnpj","nomerazao","cargo"},{"150","500","150"}});

        try {
            while (pResult.next()) {
                int i = 0;
                for (i=1;i<=4;i++) {
                    String sCpfCnpj = null;
                    try {
                        sCpfCnpj = pResult.getString("sociocpf" + Integer.toString(i));
                    } catch (SQLException ex) { ex.printStackTrace(); }
                    String sNomeRazao = null;
                    try {
                        sNomeRazao = pResult.getString("socionome" + Integer.toString(i));
                    } catch (SQLException ex) { ex.printStackTrace(); }
                    String sCargo = null;
                    try {
                        sCargo = pResult.getString("sociocargo" + Integer.toString(i));
                    } catch (SQLException ex) { ex.printStackTrace(); }
                    
                    if (sCpfCnpj != null) {
                        TableControl.add(table, new String[][]{{sCpfCnpj, sNomeRazao, sCargo},{"C","L","L"}}, true);
                    }
                }
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(pResult);        
    }

    private void FillFiadores(JTable table, String contrato) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"contrato","cpfcnpj","nomerazao"},{"50","120","500"}});

        String sSql = "SELECT contrato, cpfcnpj, nomerazao FROM fiadores WHERE contrato = '" + contrato + "' ORDER BY nomerazao;";
        ResultSet imResult = this.conn.OpenTable(sSql, null);

        try {
            while (imResult.next()) {
                String tcontrato = String.valueOf(imResult.getInt("contrato"));
                String tcpfcnpj = imResult.getString("cpfcnpj");
                String tnome = imResult.getString("nomerazao").toUpperCase().trim() ;

                TableControl.add(table, new String[][]{{tcontrato, tcpfcnpj, tnome},{"C","C","L"}}, true);

            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        conn.CloseTable(imResult);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDadosIniciais = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        mTpImv = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        mContrato = new LimitedTextField(6);
        jLabel6 = new javax.swing.JLabel();
        mIdentidade = new LimitedTextField(20);
        mRgprp = new javax.swing.JTextField();
        mRgimv = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        mTipoDoc = new javax.swing.JComboBox<>();
        lbCpfCnpj = new javax.swing.JLabel();
        mCpfCnpj = new javax.swing.JTextField();
        jDados = new javax.swing.JTabbedPane();
        jJuridica = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        mjRazao = new LimitedTextField(60);
        jLabel47 = new javax.swing.JLabel();
        mjFantasia = new LimitedTextField(60);
        jLabel48 = new javax.swing.JLabel();
        mjEndereco = new LimitedTextField(60);
        jLabel49 = new javax.swing.JLabel();
        mjNumero = new LimitedTextField(10);
        jLabel50 = new javax.swing.JLabel();
        mjCplto = new LimitedTextField(15);
        jLabel51 = new javax.swing.JLabel();
        mjBairro = new LimitedTextField(25);
        jLabel52 = new javax.swing.JLabel();
        mjCidade = new LimitedTextField(25);
        jLabel53 = new javax.swing.JLabel();
        mjEstado = new LimitedTextField(2);
        jLabel54 = new javax.swing.JLabel();
        mjCep = new javax.swing.JFormattedTextField();
        jLabel55 = new javax.swing.JLabel();
        mjDtContratoSocial = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel58 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        mjEmail = new LimitedTextField(100);
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jSocios = new javax.swing.JTable();
        btFicha = new javax.swing.JButton();
        btExcluirFicha = new javax.swing.JButton();
        jbtBuscaCep = new javax.swing.JButton();
        mjTel = new javax.swing.JComboBox<>();
        plus = new javax.swing.JLabel();
        minus = new javax.swing.JLabel();
        jFisica = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        mfNome = new LimitedTextField(60);
        jLabel9 = new javax.swing.JLabel();
        mfDtNasc = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        mfSexo = new javax.swing.JComboBox();
        plusFisica = new javax.swing.JLabel();
        minusFisica = new javax.swing.JLabel();
        mfNacionalidade = new LimitedTextField(25);
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        mfMae = new LimitedTextField(60);
        mfPai = new LimitedTextField(60);
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        mfEmpresa = new LimitedTextField(60);
        jLabel16 = new javax.swing.JLabel();
        mfDtAdmis = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel17 = new javax.swing.JLabel();
        mfEndereco = new LimitedTextField(60);
        jLabel18 = new javax.swing.JLabel();
        mfNumero = new LimitedTextField(10);
        mfCplto = new LimitedTextField(15);
        jLabel20 = new javax.swing.JLabel();
        mfBairro = new LimitedTextField(25);
        jLabel21 = new javax.swing.JLabel();
        mfCidade = new LimitedTextField(25);
        jLabel22 = new javax.swing.JLabel();
        mfEstado = new LimitedTextField(2);
        jLabel23 = new javax.swing.JLabel();
        mfCep = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        mfTelEmpresa = new javax.swing.JFormattedTextField();
        mfRamalEmpresa = new LimitedTextField(4);
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        mfCargo = new LimitedTextField(25);
        jLabel27 = new javax.swing.JLabel();
        mfSalario = new javax.swing.JFormattedTextField();
        jLabel28 = new javax.swing.JLabel();
        mfEmail = new LimitedTextField(100);
        jLabel29 = new javax.swing.JLabel();
        mfConjugue = new LimitedTextField(60);
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        mfIdentidadeConj = new LimitedTextField(15);
        jLabel33 = new javax.swing.JLabel();
        mfSalarioConj = new javax.swing.JFormattedTextField();
        jLabel61 = new javax.swing.JLabel();
        mfCpfConj = new javax.swing.JFormattedTextField();
        jbtBuscaCep1 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        mfConjSexo = new javax.swing.JComboBox();
        mfTel = new javax.swing.JComboBox<>();
        mfDtNascConj = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        mfEstCivil = new javax.swing.JComboBox();
        jDadosAvisos = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        mAviso = new LimitedTextField(60);
        mBoleta = new javax.swing.JCheckBox();
        jLabel38 = new javax.swing.JLabel();
        mMsgBol = new LimitedTextField(60);
        jcbxBcoBoleta = new javax.swing.JComboBox();
        jLabel63 = new javax.swing.JLabel();
        jcbxEnvio = new javax.swing.JComboBox();
        jLabel64 = new javax.swing.JLabel();
        jCheckBoxCobAut = new javax.swing.JCheckBox();
        jSpinnerCobDias = new javax.swing.JSpinner();
        jCheckBoxCobEmail = new javax.swing.JCheckBox();
        jCheckBoxCobWhatsApp = new javax.swing.JCheckBox();
        jBotoes = new javax.swing.JPanel();
        btIncluir = new javax.swing.JButton();
        btCarteira = new javax.swing.JButton();
        btTras = new javax.swing.JButton();
        btFrente = new javax.swing.JButton();
        btIrPara = new javax.swing.JButton();
        btGravar = new javax.swing.JButton();
        btRetornar = new javax.swing.JButton();
        btFiador = new javax.swing.JButton();
        btPagtos = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbFiadores = new javax.swing.JTable();
        jLabel65 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Cadastro de Locatários / Condominos");
        setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        setMaximumSize(new java.awt.Dimension(766, 656));
        setMinimumSize(new java.awt.Dimension(766, 656));
        setNormalBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setVisible(true);
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

        jDadosIniciais.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jDadosIniciais.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel1.setText("Prop.:");

        jLabel2.setText("Imóvel:");

        jLabel3.setText("Tp.Imóvel:");

        mTpImv.setDisabledTextColor(new java.awt.Color(147, 147, 1));
        mTpImv.setEnabled(false);

        jLabel4.setText("Contrato:");

        mContrato.setEditable(false);
        mContrato.setForeground(new java.awt.Color(0, 41, 255));
        mContrato.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mContrato.setDisabledTextColor(new java.awt.Color(0, 41, 255));
        mContrato.setEnabled(false);
        mContrato.setName("mContrato"); // NOI18N
        mContrato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mContratoFocusLost(evt);
            }
        });

        jLabel6.setText("RG/Insc:");

        mRgprp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mRgprp.setDisabledTextColor(new java.awt.Color(21, 1, 176));
        mRgprp.setEnabled(false);

        mRgimv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mRgimv.setDisabledTextColor(new java.awt.Color(1, 169, 37));
        mRgimv.setEnabled(false);

        jLabel39.setText("Tipo:");

        mTipoDoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "F", "J" }));
        mTipoDoc.setEnabled(false);
        mTipoDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mTipoDocActionPerformed(evt);
            }
        });

        lbCpfCnpj.setText("CPF:");

        mCpfCnpj.setEnabled(false);
        mCpfCnpj.setMaximumSize(new java.awt.Dimension(117, 22));
        mCpfCnpj.setMinimumSize(new java.awt.Dimension(117, 22));
        mCpfCnpj.setPreferredSize(new java.awt.Dimension(117, 22));
        mCpfCnpj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mCpfCnpjFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jDadosIniciaisLayout = new javax.swing.GroupLayout(jDadosIniciais);
        jDadosIniciais.setLayout(jDadosIniciaisLayout);
        jDadosIniciaisLayout.setHorizontalGroup(
            jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mTpImv, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mTipoDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(173, 173, 173)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mContrato, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
                    .addComponent(mIdentidade))
                .addGap(86, 86, 86))
        );
        jDadosIniciaisLayout.setVerticalGroup(
            jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                        .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mTpImv, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbCpfCnpj)
                            .addComponent(mTipoDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39)))
                    .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                        .addComponent(mContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mIdentidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDados.setMaximumSize(new java.awt.Dimension(641, 347));
        jDados.setMinimumSize(new java.awt.Dimension(641, 347));
        jDados.setPreferredSize(new java.awt.Dimension(641, 347));

        jJuridica.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jJuridica.setMaximumSize(new java.awt.Dimension(641, 329));
        jJuridica.setMinimumSize(new java.awt.Dimension(641, 329));
        jJuridica.setPreferredSize(new java.awt.Dimension(641, 329));

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Razão:");

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Fantasia:");

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel48.setText("Endereço:");

        mjEndereco.setName("mjEndereco"); // NOI18N

        jLabel49.setText("N°.:");

        mjNumero.setName("mjNumero"); // NOI18N

        jLabel50.setText("Cplto:");

        mjCplto.setName("mjCplto"); // NOI18N

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("Bairro:");

        mjBairro.setName("mjBairro"); // NOI18N

        jLabel52.setText("Cidade:");

        mjCidade.setName("mjCidade"); // NOI18N

        jLabel53.setText("UF:");

        mjEstado.setName("mjEstado"); // NOI18N

        jLabel54.setText("Cep:");

        try {
            mjCep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mjCep.setText("");

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Telefone:");

        mjDtContratoSocial.setDate(new java.util.Date(-2208977612000L));

        jLabel58.setText("Dt.Contr Socl:");

        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel60.setText("E-Mail:");

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Sócios"), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED)));
        jPanel1.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jSocios.setAutoCreateRowSorter(true);
        jSocios.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jSocios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jSocios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jSocios.getTableHeader().setResizingAllowed(false);
        jSocios.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jSocios);

        btFicha.setText("Ficha");
        btFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFichaActionPerformed(evt);
            }
        });

        btExcluirFicha.setText("Excluir");
        btExcluirFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirFichaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btFicha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btExcluirFicha))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btFicha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                        .addComponent(btExcluirFicha))))
        );

        jbtBuscaCep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        jbtBuscaCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBuscaCepActionPerformed(evt);
            }
        });

        plus.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        plus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/plus.png"))); // NOI18N
        plus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        plus.setPreferredSize(new java.awt.Dimension(10, 10));
        plus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                plusMouseReleased(evt);
            }
        });

        minus.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        minus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/minus.png"))); // NOI18N
        minus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        minus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                minusMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jJuridicaLayout = new javax.swing.GroupLayout(jJuridica);
        jJuridica.setLayout(jJuridicaLayout);
        jJuridicaLayout.setHorizontalGroup(
            jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jJuridicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jJuridicaLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                            .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mjEmail)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jJuridicaLayout.createSequentialGroup()
                                .addComponent(mjEndereco)
                                .addGap(0, 0, 0)
                                .addComponent(jbtBuscaCep, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel49)
                                .addGap(4, 4, 4)
                                .addComponent(mjNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel50)
                                .addGap(4, 4, 4)
                                .addComponent(mjCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(mjFantasia)
                            .addComponent(mjRazao, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jJuridicaLayout.createSequentialGroup()
                                .addComponent(mjBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jJuridicaLayout.createSequentialGroup()
                                .addComponent(mjTel, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(plus, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(minus, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jJuridicaLayout.createSequentialGroup()
                                        .addComponent(jLabel58)
                                        .addGap(4, 4, 4)
                                        .addComponent(mjDtContratoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jJuridicaLayout.createSequentialGroup()
                                        .addComponent(jLabel53)
                                        .addGap(5, 5, 5)
                                        .addComponent(mjEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel54)
                                        .addGap(4, 4, 4)
                                        .addComponent(mjCep, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap())))
        );

        jJuridicaLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel46, jLabel47, jLabel48, jLabel51, jLabel55, jLabel60});

        jJuridicaLayout.setVerticalGroup(
            jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jJuridicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addComponent(mjRazao, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mjFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(mjEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jbtBuscaCep, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(mjBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjCep, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mjDtContratoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(minus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mjTel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(plus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mjEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jJuridicaLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel46, jLabel47, jLabel48, jLabel51, jLabel55, jLabel60});

        jDados.addTab("Jurídica", jJuridica);

        jFisica.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jFisica.setEnabled(false);
        jFisica.setMaximumSize(new java.awt.Dimension(641, 329));
        jFisica.setMinimumSize(new java.awt.Dimension(641, 329));
        jFisica.setName(""); // NOI18N
        jFisica.setPreferredSize(new java.awt.Dimension(641, 329));
        jFisica.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Nome:");
        jFisica.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 73, 22));
        jFisica.add(mfNome, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 13, 279, -1));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Dt.Nasc:");
        jFisica.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(481, 12, 51, 24));

        mfDtNasc.setDate(new java.util.Date(-2208977612000L));
        jFisica.add(mfDtNasc, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 12, 95, 24));

        mfSexo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "F" }));
        jFisica.add(mfSexo, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 13, 50, -1));

        plusFisica.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        plusFisica.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        plusFisica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/plus.png"))); // NOI18N
        plusFisica.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        plusFisica.setPreferredSize(new java.awt.Dimension(10, 10));
        plusFisica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                plusFisicaMouseReleased(evt);
            }
        });
        jFisica.add(plusFisica, new org.netbeans.lib.awtextra.AbsoluteConstraints(598, 41, 18, 22));

        minusFisica.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        minusFisica.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minusFisica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/minus.png"))); // NOI18N
        minusFisica.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        minusFisica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                minusFisicaMouseReleased(evt);
            }
        });
        jFisica.add(minusFisica, new org.netbeans.lib.awtextra.AbsoluteConstraints(616, 41, 17, 22));

        mfNacionalidade.setName("mfNacionalidade"); // NOI18N
        jFisica.add(mfNacionalidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 40, 149, 24));

        jLabel15.setText("Est.Civil:");
        jFisica.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(246, 44, 50, -1));

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Nacional.:");
        jFisica.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 41, 73, 22));

        jLabel10.setText("Tels.:");
        jFisica.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 30, 24));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Mãe:");
        jFisica.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 69, 73, 22));
        jFisica.add(mfMae, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 68, 255, 24));
        jFisica.add(mfPai, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 67, 228, 24));

        jLabel12.setText("Pai:");
        jFisica.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(378, 67, -1, 24));

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Empresa:");
        jFisica.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 98, 73, 22));
        jFisica.add(mfEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 97, 368, 24));

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Dt.Adm:");
        jFisica.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(471, 97, 50, 24));

        mfDtAdmis.setDate(new java.util.Date(-2208977612000L));
        jFisica.add(mfDtAdmis, new org.netbeans.lib.awtextra.AbsoluteConstraints(531, 97, -1, 24));

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Endereço:");
        jFisica.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 126, 73, 22));

        mfEndereco.setName("mfEndereco"); // NOI18N
        jFisica.add(mfEndereco, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 125, 262, 24));

        jLabel18.setText("N°.:");
        jFisica.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(391, 125, -1, 24));

        mfNumero.setName("mfNumero"); // NOI18N
        jFisica.add(mfNumero, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 125, 53, 24));

        mfCplto.setName("mfCplto"); // NOI18N
        jFisica.add(mfCplto, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 125, 123, 24));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Bairro:");
        jFisica.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 155, 73, 22));

        mfBairro.setName("mfBairro"); // NOI18N
        jFisica.add(mfBairro, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 154, 147, 24));

        jLabel21.setText("Cidade:");
        jFisica.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 154, -1, 24));

        mfCidade.setName("mfCidade"); // NOI18N
        jFisica.add(mfCidade, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 154, 150, 24));

        jLabel22.setText("UF:");
        jFisica.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(462, 154, -1, 24));

        mfEstado.setName("mfEstado"); // NOI18N
        jFisica.add(mfEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(487, 154, 30, 24));

        jLabel23.setText("Cep:");
        jFisica.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(523, 154, -1, 24));

        try {
            mfCep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("*****-***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mfCep.setText("");
        jFisica.add(mfCep, new org.netbeans.lib.awtextra.AbsoluteConstraints(553, 154, 80, 24));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Telefone:");
        jFisica.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 184, 73, 22));

        try {
            mfTelEmpresa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)*####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mfTelEmpresa.setText("");
        jFisica.add(mfTelEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 183, 107, 24));

        mfRamalEmpresa.setName("mNumero"); // NOI18N
        jFisica.add(mfRamalEmpresa, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 183, 49, 24));

        jLabel25.setText("Ramal:");
        jFisica.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 183, -1, 24));

        jLabel26.setText("Cargo:");
        jFisica.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(304, 183, -1, 24));

        mfCargo.setName("mCidade"); // NOI18N
        jFisica.add(mfCargo, new org.netbeans.lib.awtextra.AbsoluteConstraints(345, 183, 133, 24));

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("Salário:");
        jFisica.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(484, 183, 61, 24));

        mfSalario.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mfSalario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mfSalario.setText("0,00");
        jFisica.add(mfSalario, new org.netbeans.lib.awtextra.AbsoluteConstraints(551, 183, 82, 24));

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("E-Mail:");
        jFisica.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 215, 73, 22));

        mfEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mfEmailActionPerformed(evt);
            }
        });
        jFisica.add(mfEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 214, 542, 24));

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Conjugue:");
        jFisica.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 245, 79, 22));
        jFisica.add(mfConjugue, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 244, 421, 24));

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Dt.Nasc:");
        jFisica.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 275, 73, 22));

        jLabel31.setText("Cpfj:");
        jFisica.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(208, 274, -1, 24));

        jLabel32.setText("RG:");
        jFisica.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(358, 274, -1, 24));
        jFisica.add(mfIdentidadeConj, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 274, 115, 24));

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Salário:");
        jFisica.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(501, 274, 50, 24));

        mfSalarioConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mfSalarioConj.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mfSalarioConj.setText("0,00");
        jFisica.add(mfSalarioConj, new org.netbeans.lib.awtextra.AbsoluteConstraints(557, 274, 76, 24));

        jLabel61.setText("Cplto");
        jFisica.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, 125, -1, 24));

        try {
            mfCpfConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***.***.***-**")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mfCpfConj.setText("999.999.999-99");
        jFisica.add(mfCpfConj, new org.netbeans.lib.awtextra.AbsoluteConstraints(239, 274, 101, 24));

        jbtBuscaCep1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        jbtBuscaCep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBuscaCep1ActionPerformed(evt);
            }
        });
        jFisica.add(jbtBuscaCep1, new org.netbeans.lib.awtextra.AbsoluteConstraints(353, 125, 32, -1));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Sexo:");
        jFisica.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(376, 16, 43, -1));

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel62.setText("Sexo:");
        jFisica.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 248, 55, -1));

        mfConjSexo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "F" }));
        jFisica.add(mfConjSexo, new org.netbeans.lib.awtextra.AbsoluteConstraints(581, 245, 52, -1));

        mfTel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jFisica.add(mfTel, new org.netbeans.lib.awtextra.AbsoluteConstraints(438, 41, 160, -1));

        mfDtNascConj.setDate(new java.util.Date(-2208977612000L));
        jFisica.add(mfDtNascConj, new org.netbeans.lib.awtextra.AbsoluteConstraints(91, 274, 93, 24));

        mfEstCivil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Solteriro(a)", "Casado(a)", "Separado(a)", "Divorciado(a)", "Viuvo(a)" }));
        mfEstCivil.setName("mfEstCivil"); // NOI18N
        jFisica.add(mfEstCivil, new org.netbeans.lib.awtextra.AbsoluteConstraints(302, 41, 100, -1));

        jDados.addTab("Física", jFisica);

        jDadosAvisos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel37.setText("Aviso:");

        mBoleta.setText("Boleta");
        mBoleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mBoletaActionPerformed(evt);
            }
        });

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("Mensagem:");

        jcbxBcoBoleta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "104 - CEF", "341 - Itaú", "033 - Santander", "001 - Banco do Brasil", "237 - Bradesco", "077 - Inter" }));

        jLabel63.setText("Dias Ant:");

        jcbxEnvio.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 - Em mãos", "2 - Correio Eletrônico", "3 - Correspondeência" }));

        jLabel64.setText("Envio:");

        jCheckBoxCobAut.setText("Cob. aut.");

        jCheckBoxCobEmail.setText("E-Mail");

        jCheckBoxCobWhatsApp.setText("WhatsApp");

        javax.swing.GroupLayout jDadosAvisosLayout = new javax.swing.GroupLayout(jDadosAvisos);
        jDadosAvisos.setLayout(jDadosAvisosLayout);
        jDadosAvisosLayout.setHorizontalGroup(
            jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosAvisosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mBoleta)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDadosAvisosLayout.createSequentialGroup()
                        .addComponent(mAviso)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mMsgBol, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDadosAvisosLayout.createSequentialGroup()
                        .addComponent(jcbxBcoBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbxEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBoxCobAut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel63)
                        .addGap(0, 0, 0)
                        .addComponent(jSpinnerCobDias, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxCobEmail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxCobWhatsApp)))
                .addContainerGap())
        );
        jDadosAvisosLayout.setVerticalGroup(
            jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosAvisosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mMsgBol, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jcbxBcoBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel63)
                        .addComponent(jcbxEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCheckBoxCobAut)
                        .addComponent(jLabel64))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDadosAvisosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSpinnerCobDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCheckBoxCobEmail)
                        .addComponent(jCheckBoxCobWhatsApp)))
                .addGap(2, 2, 2))
        );

        jDadosAvisosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel37, mBoleta});

        jBotoes.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        btIncluir.setText("Incluir");
        btIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIncluirActionPerformed(evt);
            }
        });

        btCarteira.setText("Carteira");
        btCarteira.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCarteiraActionPerformed(evt);
            }
        });

        btTras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/previous.png"))); // NOI18N
        btTras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTrasActionPerformed(evt);
            }
        });

        btFrente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/next.png"))); // NOI18N
        btFrente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFrenteActionPerformed(evt);
            }
        });

        btIrPara.setText("Ir Para");
        btIrPara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIrParaActionPerformed(evt);
            }
        });

        btGravar.setText("Gravar");
        btGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGravarActionPerformed(evt);
            }
        });

        btRetornar.setText("Retornar");
        btRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRetornarActionPerformed(evt);
            }
        });

        btFiador.setText("Fiador");
        btFiador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFiadorActionPerformed(evt);
            }
        });

        btPagtos.setText("Pagtos");
        btPagtos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPagtosActionPerformed(evt);
            }
        });

        btExcluir.setText("Excluir");
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jBotoesLayout = new javax.swing.GroupLayout(jBotoes);
        jBotoes.setLayout(jBotoesLayout);
        jBotoesLayout.setHorizontalGroup(
            jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btIncluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btCarteira, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jBotoesLayout.createSequentialGroup()
                        .addComponent(btTras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btFrente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btIrPara, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btPagtos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btFiador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btRetornar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btGravar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jBotoesLayout.setVerticalGroup(
            jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btIncluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btExcluir)
                .addGap(25, 25, 25)
                .addComponent(btCarteira)
                .addGap(30, 30, 30)
                .addGroup(jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btTras)
                    .addComponent(btFrente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btIrPara)
                .addGap(45, 45, 45)
                .addComponent(btFiador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPagtos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(btGravar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRetornar)
                .addContainerGap())
        );

        tbFiadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbFiadores);

        jLabel65.setBackground(java.awt.SystemColor.activeCaption);
        jLabel65.setText(".:: Fiadores");
        jLabel65.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel65.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jDados, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDadosIniciais, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addComponent(jBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jDadosAvisos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jDadosIniciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jDados, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jDadosAvisos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel65)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mContratoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mContratoFocusLost
        boolean achei = false;
        try {
            achei = MoveToLoca("contrato", mContrato.getText());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        mContrato.setEditable(false);
        mContrato.setEnabled(false);
}//GEN-LAST:event_mContratoFocusLost

    private void btIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIncluirActionPerformed
        bNew = true;

        // Bloqueio dos botões
        btIncluir.setEnabled(false && _Incluir);
        btCarteira.setEnabled(false && _Carteira);
        btTras.setEnabled(false);
        btFrente.setEnabled(false);
        btIrPara.setEnabled(false);
        btFiador.setEnabled(false && _Fiador);
        btGravar.setEnabled(true && _Alterar);
        btRetornar.setEnabled(true);
        btFicha.setEnabled(false && _Incluir);
        btExcluirFicha.setEnabled(false && _Excluir);

        jLocaInclusao oInc= new jLocaInclusao(null, closable);
        String action = (String)oInc.showDialog();
        if(action.equals(jLocaInclusao.CANCELCMD)) {
            bNew = false;

            // Bloqueio dos botões
            btIncluir.setEnabled(true && _Incluir);
            btCarteira.setEnabled(true && _Carteira);
            btTras.setEnabled(true);
            btFrente.setEnabled(true);
            btIrPara.setEnabled(true);
            btFiador.setEnabled(true && _Fiador);
            btGravar.setEnabled(true && _Alterar);
            btRetornar.setEnabled(true);
            btFicha.setEnabled(true && _Incluir);
            btExcluirFicha.setEnabled(true && _Excluir);
            try { LerDados(null); } catch (SQLException sqlEX) {}
        } else {
            LimpaDados();

            mTipoDoc.setEditable(false); mTipoDoc.setEnabled(true);
            mCpfCnpj.setEnabled(true);
            mTipoDoc.setSelectedItem(0);

            String tRgprp = oInc.getRgprp();
            String tRgimv = oInc.getRgimv();
            String tContrato = oInc.getContrato();
            String tTpImv = oInc.getTpImv();

            mRgprp.setText(tRgprp);
            mRgimv.setText(tRgimv);
            mContrato.setText(tContrato);
            mTpImv.setText(tTpImv);
            
            mTipoDoc.requestFocus();
        }
    }//GEN-LAST:event_btIncluirActionPerformed

    private void btCarteiraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCarteiraActionPerformed
        String gContrato = mContrato.getText().trim();
        VariaveisGlobais.ccontrato = mContrato.getText();
        VariaveisGlobais.crgprp = mRgprp.getText();
        VariaveisGlobais.crgimv = mRgimv.getText();
  
        jCarteira oCart = new jCarteira(gContrato, null, closable);
        oCart.setBotoes(_botoes);
        oCart.setVisible(true);
}//GEN-LAST:event_btCarteiraActionPerformed

    private void btIrParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIrParaActionPerformed
        mContrato.setEnabled(true);
        mContrato.setEditable(true);
        mContrato.selectAll();
        mContrato.requestFocus();
}//GEN-LAST:event_btIrParaActionPerformed

    private void btGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGravarActionPerformed

        if (mTipoDoc.getSelectedIndex() == 0 && mfNome.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'NOME' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mfNome.requestFocus();
            return;
        }

        if (mTipoDoc.getSelectedIndex() == 1 && mjRazao.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'RAZÃO' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mjRazao.requestFocus();
            return;
        }

        if (mCpfCnpj.getText().replace(".", "").replace("-", "").trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'CPF/CNPJ' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mCpfCnpj.requestFocus();
            return;
        }

        try {
            GravarDados();
            conn.CommandExecute("UPDATE imoveis SET situacao = 'OCUPADO' WHERE rgprp = '" + mRgprp.getText().trim() + "' AND rgimv = '" + mRgimv.getText().trim() + "';");
        } catch (SQLException ex) { ex.printStackTrace();}

        bNew = false;

        // Bloqueio dos botões
        btIncluir.setEnabled(true && _Incluir);
        btCarteira.setEnabled(true && _Carteira);
        btTras.setEnabled(true);
        btFrente.setEnabled(true);
        btIrPara.setEnabled(true);
        btFiador.setEnabled(true && _Fiador);
        btGravar.setEnabled(true && _Alterar);
        btRetornar.setEnabled(true);
        btFicha.setEnabled(true && _Incluir);
        btExcluirFicha.setEnabled(true && _Excluir);
    }//GEN-LAST:event_btGravarActionPerformed

    private void btRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRetornarActionPerformed
        if (bNew) {
            try { LerDados(false); } catch (SQLException ex) { ex.printStackTrace();}
            // Bloqueio dos botões
            btIncluir.setEnabled(true && _Incluir);
            btCarteira.setEnabled(true && _Carteira);
            btTras.setEnabled(true);
            btFrente.setEnabled(true);
            btIrPara.setEnabled(true);
            btFiador.setEnabled(true && _Fiador);
            btGravar.setEnabled(true && _Alterar);
            btRetornar.setEnabled(true);
            btFicha.setEnabled(true && _Incluir);
            btExcluirFicha.setEnabled(true && _Excluir);
            
            bNew = false;
        } else this.dispose();
}//GEN-LAST:event_btRetornarActionPerformed

    private void btFiadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFiadorActionPerformed
        VariaveisGlobais.frgprp = mRgprp.getText();
        VariaveisGlobais.frgimv = mRgimv.getText();
        VariaveisGlobais.fcontrato = (tbFiadores.getSelectedRow() >  -1 ? tbFiadores.getModel().getValueAt(tbFiadores.getSelectedRow(), 0).toString() : mContrato.getText());
        VariaveisGlobais.fnome = (tbFiadores.getSelectedRow() >  -1 ? tbFiadores.getModel().getValueAt(tbFiadores.getSelectedRow(), 2).toString() : "");

        jFiadores oFia = null;
        oFia = new jFiadores(null, closable);
        oFia.setBotoes(_botoes);
        oFia.setVisible(true);
        FillFiadores(tbFiadores, mContrato.getText());
}//GEN-LAST:event_btFiadorActionPerformed

    private void btPagtosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPagtosActionPerformed
        jPagtos oTela = new jPagtos(null, true);
        oTela.MontaLista(mContrato.getText(), mRgprp.getText());
        oTela.setEnabled(true); oTela.setVisible(true);
}//GEN-LAST:event_btPagtosActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        String[] sql;
        Object[] options = { "Sim", "Não" };
        int n = JOptionPane.showOptionDialog(this,
                "Deseja excluir este locatario ? \nIra apagar todas as informações...\nSem retorno.",
                "Atenção", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            int n2 = JOptionPane.showOptionDialog(this,
                "Têm certeza?\nSem retorno.",
                "Atenção", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (n2 == JOptionPane.YES_OPTION) {
                sql = new String[] {
                    "DELETE FROM `auxiliar` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `avisos` WHERE registro = '" + mContrato.getText() + "';",
                    "DELETE FROM `caixa` WHERE cx_contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `caixabck` WHERE cx_contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `carteira` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `descontos` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `diferenca` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `extrato` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `fiadores` WHERE contrato = '" + mContrato.getText() + "';",
                    "UPDATE `imoveis` SET situacao = 'vazio' WHERE rgimv = '" + mRgimv.getText() + "';",
                    "DELETE FROM `locatarios` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `fiadores` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `recibo` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `retencao` WHERE contrato = '" + mContrato.getText() + "';",
                    "DELETE FROM `seguros` WHERE contrato = '" + mContrato.getText() + "';"
                };

                for (String nsql : sql) {
                    try {conn.CommandExecute(nsql);} catch (Exception e) {e.printStackTrace();}
                }
                try {
                    conn.Auditor("EXCLUSAO: LOCATARIO", mContrato.getText());
                } catch (Exception e) {}

                JOptionPane.showMessageDialog(null, "Locatário excluido!!!");
                try {LerDados(true);} catch (SQLException ex) {}
            }            
        }
    }//GEN-LAST:event_btExcluirActionPerformed

    private void btFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFichaActionPerformed
        int selRow = jSocios.getSelectedRow();
        if (selRow > -1) {
            VariaveisGlobais.mContrato = mContrato.getText();
            VariaveisGlobais.mQtdSoc = jSocios.getRowCount();
            VariaveisGlobais.mPosSoc = selRow + 1;
        } else {
            VariaveisGlobais.mContrato = ""; //mContrato.getText();
            VariaveisGlobais.mQtdSoc = jSocios.getRowCount();
            VariaveisGlobais.mPosSoc = 0;
        }

        try {
            jFichaSocios oSoc = new jFichaSocios(null, closable);
            oSoc.setVisible(true);
        } catch (SQLException ex) { ex.printStackTrace(); }

        FillSocios(jSocios);
}//GEN-LAST:event_btFichaActionPerformed

    private void btExcluirFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirFichaActionPerformed
        Object[] options = { "Sim", "Não" };
        int i = JOptionPane.showOptionDialog(null,
                "Tem certeza que deseja Excluir este Sócio", "Excluir",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        if (i == JOptionPane.YES_OPTION) {
            try {
                ExcluirSocio();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        FillSocios(jSocios);
}//GEN-LAST:event_btExcluirFichaActionPerformed

    private void ExcluirSocio() throws SQLException {
        int selRow = jSocios.getSelectedRow();
        if (selRow > -1) {
            String query = "UPDATE `locatarios` SET `socionome` = null, `sociodtnasc` = null, `socionac` = null, `socioecivil` = null, `sociocpf` = null, " + 
                           "`sociorg = null`, `sociosalario` = null, `sociocargo` = null, `sociomae` = null, `sociopai` = null WHERE `contrato` = :contrato;";
            conn.CommandExecute(query, new Object[][] {{"int", "contrato", Integer.parseInt(mContrato.getText().trim())}});
        }
    }

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // Retornat status de inativos
        VariaveisGlobais.Iloca = false;
    }//GEN-LAST:event_formInternalFrameClosing

    private void jbtBuscaCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBuscaCepActionPerformed
        BuscaCep oCep = new BuscaCep(null, true);
        oCep.setVisible(true);

        Object[] dados = oCep.dados;
        oCep = null;

        if (dados != null) {
            mjEndereco.setText(dados[0].toString() + " " + dados[1].toString());
            mjBairro.setText(dados[2].toString());
            mjCidade.setText(dados[3].toString());
            mjEstado.setText(dados[4].toString());
            mjCep.setText(dados[5].toString());

            mjNumero.requestFocus();
        }
    }//GEN-LAST:event_jbtBuscaCepActionPerformed

    private void jbtBuscaCep1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBuscaCep1ActionPerformed
        BuscaCep oCep = new BuscaCep(null, true);
        oCep.setVisible(true);

        Object[] dados = oCep.dados;
        oCep = null;

        if (dados != null) {
            mfEndereco.setText(dados[0].toString() + " " + dados[1].toString());
            mfBairro.setText(dados[2].toString());
            mfCidade.setText(dados[3].toString());
            mfEstado.setText(dados[4].toString());
            mfCep.setText(dados[5].toString());

            mfNumero.requestFocus();
        }
    }//GEN-LAST:event_jbtBuscaCep1ActionPerformed

    private void mBoletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mBoletaActionPerformed
        //ALTER TABLE `jgeral`.`locatarios` ADD COLUMN `bcobol` VARCHAR(3) NULL  AFTER `cjcpf` ;
        jcbxBcoBoleta.setEnabled(mBoleta.isSelected());
        jcbxEnvio.setEnabled(mBoleta.isSelected());
        jCheckBoxCobAut.setEnabled(mBoleta.isSelected());
        jSpinnerCobDias.setEnabled(mBoleta.isSelected());
        jCheckBoxCobEmail.setEnabled(mBoleta.isSelected());
        jCheckBoxCobWhatsApp.setEnabled(mBoleta.isSelected());
    }//GEN-LAST:event_mBoletaActionPerformed

    private void mTipoDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mTipoDocActionPerformed
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (bNew) mCpfCnpj.setText(null);
                if (mTipoDoc.getSelectedIndex() == 0) {
                    lbCpfCnpj.setText("CPF:");
                    new CamposScreen(mCpfCnpj,"CPF");
                } else {
                    lbCpfCnpj.setText("CNPJ:");
                    new CamposScreen(mCpfCnpj,"CNPJ");
                }
                
                if (bNew) {
                   if (mTipoDoc.getSelectedIndex() == 0) {
                       jDados.setEnabledAt(0, false);
                       jDados.setEnabledAt(1, true);
                       jDados.setSelectedIndex(1);
                   } else {
                       jDados.setEnabledAt(0, true);
                       jDados.setEnabledAt(1, false);
                       jDados.setSelectedIndex(0);
                   }           
               }                
                
                mCpfCnpj.requestFocusInWindow();
            };
        });
    }//GEN-LAST:event_mTipoDocActionPerformed

    private void btTrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTrasActionPerformed
        String sInativos = (!VariaveisGlobais.Iloca ? "WHERE contrato < :contrato AND (fiador1uf <> 'X' OR IsNull(fiador1uf))" : "WHERE contrato > :contrato AND fiador1uf = 'X'");
        Object[][] param = new Object[][] {{"int", "contrato", Integer.parseInt(mContrato.getText().trim())}};
        ResultSet pResult = conn.OpenTable("SELECT * FROM locatarios " + sInativos + " ORDER BY contrato DESC LIMIT 1;", ResultSet.CONCUR_UPDATABLE, param);
        int _contrato = -1;
        try {
            while (pResult.next()) {
                _contrato = pResult.getInt("contrato");
            }
            conn.CloseTable(pResult);
            if (_contrato > -1) LerDados(_contrato);
        } catch (SQLException es) {conn.CloseTable(pResult);}
    }//GEN-LAST:event_btTrasActionPerformed

    private void btFrenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFrenteActionPerformed
        String sInativos = (!VariaveisGlobais.Iloca ? "WHERE contrato > :contrato AND (fiador1uf <> 'X' OR IsNull(fiador1uf))" : "WHERE contrato > :contrato AND fiador1uf = 'X'");
        Object[][] param = new Object[][] {{"int", "contrato", Integer.parseInt(mContrato.getText().trim())}};
        ResultSet pResult = conn.OpenTable("SELECT * FROM locatarios " + sInativos + " ORDER BY contrato LIMIT 1;", ResultSet.CONCUR_UPDATABLE, param);
        int _contrato = -1;
        try {
            while (pResult.next()) {
                _contrato = pResult.getInt("contrato");
            }
            conn.CloseTable(pResult);
            if (_contrato > -1) LerDados(_contrato);
        } catch (SQLException es) {conn.CloseTable(pResult);}
    }//GEN-LAST:event_btFrenteActionPerformed

    private void mCpfCnpjFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mCpfCnpjFocusLost
                
    }//GEN-LAST:event_mCpfCnpjFocusLost

    private void plusMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plusMouseReleased
        if (!plus.isEnabled()) return;

        jTelsCont oTela = new jTelsCont(null, true);
        oTela.setVisible(true);
        String[] aTelCon = oTela.get();
        if (aTelCon != null) {
            mjTel.addItem(aTelCon[0] + " * " + aTelCon[1]);
        }
    }//GEN-LAST:event_plusMouseReleased

    private void minusMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minusMouseReleased
        if (!minus.isEnabled()) return;

        if(mjTel.getSelectedIndex() > -1) mjTel.removeItemAt(mjTel.getSelectedIndex());
    }//GEN-LAST:event_minusMouseReleased

    private void plusFisicaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plusFisicaMouseReleased
        if (!plus.isEnabled()) return;

        jTelsCont oTela = new jTelsCont(null, true);
        oTela.setVisible(true);
        String[] aTelCon = oTela.get();
        if (aTelCon != null) {
            mfTel.addItem(aTelCon[0] + " * " + aTelCon[1]);
        }
    }//GEN-LAST:event_plusFisicaMouseReleased

    private void minusFisicaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minusFisicaMouseReleased
        if (!minus.isEnabled()) return;

        if(mfTel.getSelectedIndex() > -1) mfTel.removeItemAt(mfTel.getSelectedIndex());
    }//GEN-LAST:event_minusFisicaMouseReleased

    private void mfEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mfEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mfEmailActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCarteira;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btExcluirFicha;
    private javax.swing.JButton btFiador;
    private javax.swing.JButton btFicha;
    private javax.swing.JButton btFrente;
    private javax.swing.JButton btGravar;
    private javax.swing.JButton btIncluir;
    private javax.swing.JButton btIrPara;
    private javax.swing.JButton btPagtos;
    private javax.swing.JButton btRetornar;
    private javax.swing.JButton btTras;
    private javax.swing.JPanel jBotoes;
    private javax.swing.JCheckBox jCheckBoxCobAut;
    private javax.swing.JCheckBox jCheckBoxCobEmail;
    private javax.swing.JCheckBox jCheckBoxCobWhatsApp;
    private javax.swing.JTabbedPane jDados;
    private javax.swing.JPanel jDadosAvisos;
    private javax.swing.JPanel jDadosIniciais;
    private javax.swing.JPanel jFisica;
    private javax.swing.JPanel jJuridica;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jSocios;
    private javax.swing.JSpinner jSpinnerCobDias;
    private javax.swing.JButton jbtBuscaCep;
    private javax.swing.JButton jbtBuscaCep1;
    private javax.swing.JComboBox jcbxBcoBoleta;
    private javax.swing.JComboBox jcbxEnvio;
    private javax.swing.JLabel lbCpfCnpj;
    private javax.swing.JTextField mAviso;
    private javax.swing.JCheckBox mBoleta;
    private javax.swing.JTextField mContrato;
    private javax.swing.JTextField mCpfCnpj;
    private javax.swing.JTextField mIdentidade;
    private javax.swing.JTextField mMsgBol;
    private javax.swing.JTextField mRgimv;
    private javax.swing.JTextField mRgprp;
    private javax.swing.JComboBox<String> mTipoDoc;
    private javax.swing.JTextField mTpImv;
    private javax.swing.JTextField mfBairro;
    private javax.swing.JTextField mfCargo;
    private javax.swing.JFormattedTextField mfCep;
    private javax.swing.JTextField mfCidade;
    private javax.swing.JComboBox mfConjSexo;
    private javax.swing.JTextField mfConjugue;
    private javax.swing.JFormattedTextField mfCpfConj;
    private javax.swing.JTextField mfCplto;
    private com.toedter.calendar.JDateChooser mfDtAdmis;
    private com.toedter.calendar.JDateChooser mfDtNasc;
    private com.toedter.calendar.JDateChooser mfDtNascConj;
    private javax.swing.JTextField mfEmail;
    private javax.swing.JTextField mfEmpresa;
    private javax.swing.JTextField mfEndereco;
    private javax.swing.JComboBox mfEstCivil;
    private javax.swing.JTextField mfEstado;
    private javax.swing.JTextField mfIdentidadeConj;
    private javax.swing.JTextField mfMae;
    private javax.swing.JTextField mfNacionalidade;
    private javax.swing.JTextField mfNome;
    private javax.swing.JTextField mfNumero;
    private javax.swing.JTextField mfPai;
    private javax.swing.JTextField mfRamalEmpresa;
    private javax.swing.JFormattedTextField mfSalario;
    private javax.swing.JFormattedTextField mfSalarioConj;
    private javax.swing.JComboBox mfSexo;
    private javax.swing.JComboBox<String> mfTel;
    private javax.swing.JFormattedTextField mfTelEmpresa;
    private javax.swing.JLabel minus;
    private javax.swing.JLabel minusFisica;
    private javax.swing.JTextField mjBairro;
    private javax.swing.JFormattedTextField mjCep;
    private javax.swing.JTextField mjCidade;
    private javax.swing.JTextField mjCplto;
    private com.toedter.calendar.JDateChooser mjDtContratoSocial;
    private javax.swing.JTextField mjEmail;
    private javax.swing.JTextField mjEndereco;
    private javax.swing.JTextField mjEstado;
    private javax.swing.JTextField mjFantasia;
    private javax.swing.JTextField mjNumero;
    private javax.swing.JTextField mjRazao;
    private javax.swing.JComboBox<String> mjTel;
    private javax.swing.JLabel plus;
    private javax.swing.JLabel plusFisica;
    private javax.swing.JTable tbFiadores;
    // End of variables declaration//GEN-END:variables
}