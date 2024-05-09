package Sici.Locatarios;

import Funcoes.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
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

public class jLocatarios_old extends javax.swing.JInternalFrame {
    String sInativos = (!VariaveisGlobais.Iloca ? "WHERE fiador1uf <> 'X' OR IsNull(fiador1uf)" : "WHERE fiador1uf = 'X'");
    Db conn = VariaveisGlobais.conexao;
    ResultSet pResult = conn.OpenTable("SELECT * FROM locatarios " + sInativos + " ORDER BY contrato;", ResultSet.CONCUR_UPDATABLE, null);
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
    public jLocatarios_old() {
        initComponents();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
            }
        });        
        
        // Icone da tela
        ImageIcon icone = new FlatSVGIcon("menuIcons/locatário.svg",16,16);
        setFrameIcon(icone);
        
        if (!VariaveisGlobais.Iloca) {
            //setBackground(new java.awt.Color (237, 236, 235));
        } else { setBackground(new java.awt.Color(255, 0, 0)); }
        
        try {LerDados(true);} catch (SQLException ex) {}

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        SelectAll();
    }

    private void SelectAll() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher(){
            public boolean dispatchKeyEvent(KeyEvent ke){
                if(ke.getID()==KeyEvent.KEY_RELEASED) {
                    int key = ke.getKeyCode();
                    if(key == KeyEvent.VK_ENTER) {
                        Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                        if(comp instanceof JTextField) {
                            ((JTextField)comp).selectAll();
                        }
                    }
                }
                return false;}});
    }

    public boolean MoveToLoca(String campo, String seek) throws SQLException {
        boolean achei = false;
        try {
            pResult.first();
            while (pResult.next()) {
                if (pResult.getInt(campo) == Integer.parseInt(seek)) {
                    achei = true;
                    break;
                }
            }
        } catch (Exception ex) {}
        if (!achei) pResult.first();
        LerDados(false);
        return achei;
    }

    private void LerDados(boolean bFirst) throws SQLException {
        if (conn.RecordCount(pResult) <= 0) {return;}

        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        if (bFirst) pResult.first();
        mRgprp.setText(Integer.toString(pResult.getInt("rgprp")));
        mRgimv.setText(Integer.toString(pResult.getInt("rgimv")));
        mTpImv.setText(pResult.getString("tpimovel"));
        mContrato.setText(pResult.getString("contrato"));
        if (pResult.getString("tploca").toUpperCase().contains("F")) {
            jrbFisica.setSelected(true);
            mCpf.setText(pResult.getString("cpfcnpj"));
            mCnpj.setText("");

            jrbFisica.setEnabled(true);
            jrbJuridica.setEnabled(false);
            jDados.setEnabledAt(0, false);
            jDados.setEnabledAt(1, true);
            jDados.setSelectedIndex(1);

            mCpf.setEnabled(true);
            mCnpj.setEnabled(false);
        } else {
            jrbJuridica.setSelected(true);
            mCnpj.setText(pResult.getString("cpfcnpj"));
            mCpf.setText("");

            jrbFisica.setEnabled(false);
            jrbJuridica.setEnabled(true);

            jDados.setEnabledAt(0, true);
            jDados.setEnabledAt(1, false);
            jDados.setSelectedIndex(0);

            mCpf.setEnabled(false);
            mCnpj.setEnabled(true);
        }
        mIdentidade.setText(pResult.getString("rginsc"));

        if (jrbFisica.isSelected()) {
            // Pessoa Física (limpar dados dos campos juridica)
            mfNome.setText(pResult.getString("nomerazao"));
            mfSexo.getModel().setSelectedItem(pResult.getString("sexo"));
            
            try {mfDtNasc.setDate(Dates.StringtoDate(pResult.getString("dtnasc"), "yyyy-MM-dd"));} catch (Exception ex) {mfDtNasc.setDate(null);}
            mfNacionalidade.setText(pResult.getString("naciona"));

            mfTel2.setText(FuncoesGlobais.rmvNumero(pResult.getString("fiador1tel")));            

            String pCivil = pResult.getString("ecivil").trim().toLowerCase();
            String sCivil[] = {"solteiro","solteira","casado","casada","separado","separada","divorciado","divorciada","viuvo","viuva"};
            Integer nPos = FuncoesGlobais.IndexOf2(sCivil,pCivil) ;
            if (nPos == -1) {
                nPos = 0;
            }
            else if (nPos == 0 || nPos == 1){
                nPos = 0;
            }
            else if (nPos == 2 || nPos == 3) {
                nPos = 1;
            }
            else if (nPos == 4 || nPos == 5) {
                nPos = 2;
            }
            else if (nPos == 6 || nPos == 7) {
                nPos = 3;
            }
            else if (nPos == 8 || nPos == 9 ) {
                nPos = 4;
            }
            else
                nPos = 0;

            mfEstCivil.setSelectedIndex(nPos);

            mfTel1.setText(FuncoesGlobais.rmvNumero(pResult.getString("celular")));
            mfMae.setText(pResult.getString("mae"));
            mfPai.setText(pResult.getString("pai"));
            mfEmpresa.setText(pResult.getString("empresa"));

            try {mfDtAdmis.setDate(Dates.StringtoDate(pResult.getString("dtadmis"), "yyyy-MM-dd"));} catch (Exception ex) {mfDtAdmis.setDate(null);}
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
            try {mfDtNascConj.setDate(Dates.StringtoDate(pResult.getString("cjdtnasc"), "yyyy-MM-dd"));} catch (Exception ex) {mfDtNascConj.setDate(null);}
            mfCpfConj.setText(pResult.getString("cjcpf"));
            mfIdentidadeConj.setText(pResult.getString("cjrg"));
            mfSalarioConj.setText(pResult.getString("cjsalario"));
            mfEmpresaConj.setText(pResult.getString("cjempresa"));
            mfEmpresaTelConj.setText(FuncoesGlobais.rmvNumero(pResult.getString("cjtel")));
            mfEmpresaRamalConj.setText(pResult.getString("cjramal"));
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
            mjTelefone.setText(FuncoesGlobais.rmvNumero(pResult.getString("tel")));
            mjRamal.setText(pResult.getString("ramal"));
            mjCelular.setText(FuncoesGlobais.rmvNumero(pResult.getString("celular")));
            try {mjDtContratoSocial.setDate(Dates.StringtoDate(pResult.getString("dtnasc"), "yyyy-MM-dd"));} catch (Exception ex) {mjDtContratoSocial.setDate(null);}

            mjEmail.setText(pResult.getString("email"));

            FillSocios(jSocios, pResult);
        }

        mAviso.setText(pResult.getString("aviso"));
        mBoleta.setSelected((pResult.getInt("boleta") == 0 ? false : true));
        jcbxBcoBoleta.setEnabled(mBoleta.isSelected());
        jcbxEnvio.setSelectedIndex(pResult.getInt("envio"));
        jCheckBoxCobAut.setSelected(pResult.getInt("cobaut") == 0 ? false : true);
        jSpinnerCobDias.setValue(pResult.getInt("cobdias"));
        jCheckBoxCobEmail.setSelected(pResult.getInt("cobemail") == 0 ? false : true);
        jCheckBoxCobWhatsApp.setSelected(pResult.getInt("cobwhatsapp") == 0 ? false : true);
        
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
        } catch (Exception e) {}
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
    
    private void LimpaDados() {
        mRgprp.setText("");
        mRgimv.setText("");
        mTpImv.setText("");
        mContrato.setText("");

        jrbFisica.setSelected(true);
        mCpf.setValue("");
        mCnpj.setText("");

        jFisica.setVisible(true);
        jJuridica.setVisible(false);

        jrbFisica.setEnabled(true);
        jrbJuridica.setEnabled(false);

        mIdentidade.setText("");

        // Pessoa Física (limpar dados dos campos juridica)
        mfNome.setText("");
        mfSexo.setSelectedIndex(0);
        mfDtNasc.setDate(null);
        mfNacionalidade.setText("");
        mfEstCivil.setSelectedIndex(0);
        mfTel1.setText("");
        mfTel2.setText("");
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
        mfEmpresaConj.setText("");
        mfEmpresaTelConj.setText("");
        mfEmpresaRamalConj.setText("");

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
        mjTelefone.setText("");
        mfTel2.setText("");
        mjRamal.setText("");
        mjCelular.setText("");
        mjDtContratoSocial.setDate(null);
        mjEmail.setText("");

        TableControl.header(jSocios, new String[][] {{"cpfcnpj","nomerazao","cargo"},{"150","500","150"}});

        mAviso.setText("");
        
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
        
        try {jcbxBcoBoleta.setSelectedItem("");} catch (Exception e) {e.printStackTrace();}
        mMsgBol.setText("");
        try {jcbxEnvio.setSelectedItem("");} catch (Exception e) {e.printStackTrace();}
        
        TableControl.header(tbFiadores, new String[][] {{"contrato","cpfcnpj","nomerazao"},{"50","120","500"}});
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

        if (bNew) {
            pResult.moveToInsertRow();

            pResult.updateInt("rgprp", Integer.parseInt(mRgprp.getText().trim()));
            pResult.updateInt("rgimv", Integer.parseInt(mRgimv.getText().trim()));
            pResult.updateInt("contrato", iNewContrato);
            pResult.updateString("tploca", (jrbFisica.isSelected() ? "F" : "J"));

            mContrato.setText(Integer.toString(iNewContrato));
        }

        if (jrbFisica.isSelected()) {
            pResult.updateString("cpfcnpj", mCpf.getText());
            pResult.updateString("rginsc", mIdentidade.getText());

            pResult.updateString("nomerazao", mfNome.getText());
            try {pResult.updateString("sexo", mfSexo.getSelectedItem().toString());} catch (Exception e) {}

            try {if (mfDtNasc.getDate() != null) pResult.updateString("dtnasc", Dates.DateFormata("yyyy-MM-dd", mfDtNasc.getDate()));} catch (Exception ex) {ex.printStackTrace();}
            pResult.updateString("naciona", mfNacionalidade.getText());
            pResult.updateString("ecivil", (mfEstCivil.getSelectedItem().toString() + "          ").substring(0, 10));
            pResult.updateString("celular", mfTel1.getText());
            pResult.updateString("mae", mfMae.getText());
            pResult.updateString("pai",mfPai.getText());
            pResult.updateString("empresa", mfEmpresa.getText());


            try {if (mfDtAdmis.getDate() != null) pResult.updateString("dtadmis",Dates.DateFormata("yyyy-MM-dd",mfDtAdmis.getDate()));} catch (Exception ex) {ex.printStackTrace();}

            pResult.updateString("end",mfEndereco.getText());
            pResult.updateString("num",mfNumero.getText());
            pResult.updateString("compl", mfCplto.getText());
            pResult.updateString("bairro", mfBairro.getText());
            pResult.updateString("cidade", mfCidade.getText());
            pResult.updateString("estado", mfEstado.getText());
            pResult.updateString("cep", mfCep.getText());
            pResult.updateString("tel", mfTelEmpresa.getText());
            pResult.updateString("fiador1tel",mfTel2.getText());            
            pResult.updateString("ramal", mfRamalEmpresa.getText());
            pResult.updateString("cargo", mfCargo.getText());
            pResult.updateString("salario", mfSalario.getText());
            pResult.updateString("email", mfEmail.getText());
            pResult.updateString("conjugue", mfConjugue.getText());

            try {pResult.updateString("conjsexo", mfConjSexo.getSelectedItem().toString());} catch (Exception e) {}

            try {if (mfDtNascConj.getDate() != null) pResult.updateString("cjdtnasc", Dates.DateFormata("yyyy-MM-dd", mfDtNascConj.getDate()));} catch (Exception ex) {ex.printStackTrace();}

            pResult.updateString("cjcpf", mfCpfConj.getText());
            pResult.updateString("cjrg", mfIdentidadeConj.getText());
            pResult.updateString("cjsalario", mfSalarioConj.getText());
            pResult.updateString("cjempresa", mfEmpresaConj.getText());
            pResult.updateString("cjtel", mfEmpresaTelConj.getText());
            pResult.updateString("cjramal", mfEmpresaRamalConj.getText());
        } else {
            pResult.updateString("cpfcnpj", mCnpj.getText());
            pResult.updateString("rginsc", mIdentidade.getText());

            pResult.updateString("nomerazao", mjRazao.getText());
            pResult.updateString("fantasia", mjFantasia.getText());
            pResult.updateString("end", mjEndereco.getText());
            pResult.updateString("num", mjNumero.getText());
            pResult.updateString("compl", mjCplto.getText());
            pResult.updateString("bairro", mjBairro.getText());
            pResult.updateString("cidade", mjCidade.getText());
            pResult.updateString("estado", mjEstado.getText());
            pResult.updateString("cep", mjCep.getText());
            pResult.updateString("tel", mjTelefone.getText());
            pResult.updateString("fiador1tel",mfTel2.getText());
            pResult.updateString("ramal", mjRamal.getText());
            pResult.updateString("celular", mjCelular.getText());

            try {if (mjDtContratoSocial.getDate() != null) pResult.updateString("dtnasc", Dates.DateFormata("yyyy-MM-dd", mjDtContratoSocial.getDate()));} catch (Exception ex) {ex.printStackTrace();}

            pResult.updateString("email", mjEmail.getText());
        }

        pResult.updateString("tpimovel", mTpImv.getText().trim());

        pResult.updateString("aviso",mAviso.getText());
        pResult.updateInt("boleta",(mBoleta.isSelected() ? -1 : 0));
        pResult.updateInt("envio",jcbxEnvio.getSelectedIndex());
        
        try {
            pResult.updateString("bcobol", jcbxBcoBoleta.getSelectedItem().toString().trim().substring(0, 3));
        }catch (Exception e) {e.printStackTrace();}
        
        // 21-03-2023 15h04m
        pResult.updateInt("cobaut", jCheckBoxCobAut.isSelected() ? -1 : 0);
        pResult.updateInt("cobdias", (int)jSpinnerCobDias.getValue());
        pResult.updateInt("cobemail", jCheckBoxCobEmail.isSelected() ? -1 : 0);
        pResult.updateInt("cobwhatsapp", jCheckBoxCobWhatsApp.isSelected() ? -1 : 0);
        //
        
        pResult.updateString("msgboleta", mMsgBol.getText());

        if (bNew) {
            mContrato.setText(Integer.toString(iNewContrato));
            pResult.insertRow();
        } else {
            pResult.updateRow();
        }

    }

    private void FillSocios(JTable table, ResultSet sResult) {
        // Seta Cabecario
        TableControl.header(table, new String[][] {{"cpfcnpj","nomerazao","cargo"},{"150","500","150"}});

        int i = 0;
        for (i=1;i<=4;i++) {
            String sCpfCnpj = null;
            try {
                sCpfCnpj = sResult.getString("sociocpf" + Integer.toString(i));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String sNomeRazao = null;
            try {
                sNomeRazao = sResult.getString("socionome" + Integer.toString(i));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String sCargo = null;
            try {
                sCargo = sResult.getString("sociocargo" + Integer.toString(i));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (sCpfCnpj != null) {
                TableControl.add(table, new String[][]{{sCpfCnpj, sNomeRazao, sCargo},{"C","L","L"}}, true);
            }
        }

        return;
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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        conn.CloseTable(imResult);

        return;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jDadosIniciais = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        mTpImv = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        mContrato = new LimitedTextField(6);
        jrbFisica = new javax.swing.JRadioButton();
        jrbJuridica = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        mIdentidade = new LimitedTextField(20);
        mCpf = new javax.swing.JFormattedTextField();
        mRgprp = new javax.swing.JTextField();
        mRgimv = new javax.swing.JTextField();
        mCnpj = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
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
        mjTelefone = new javax.swing.JFormattedTextField();
        jLabel56 = new javax.swing.JLabel();
        mjRamal = new LimitedTextField(4);
        jLabel57 = new javax.swing.JLabel();
        mjCelular = new javax.swing.JFormattedTextField();
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
        jFisica = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        mfNome = new LimitedTextField(60);
        jLabel9 = new javax.swing.JLabel();
        mfDtNasc = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        mfNacionalidade = new LimitedTextField(25);
        jLabel15 = new javax.swing.JLabel();
        mfEstCivil = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        mfTel2 = new javax.swing.JFormattedTextField();
        mfTel1 = new javax.swing.JFormattedTextField();
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
        mfDtNascConj = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        mfIdentidadeConj = new LimitedTextField(15);
        jLabel33 = new javax.swing.JLabel();
        mfSalarioConj = new javax.swing.JFormattedTextField();
        jLabel34 = new javax.swing.JLabel();
        mfEmpresaConj = new LimitedTextField(60);
        jLabel35 = new javax.swing.JLabel();
        mfEmpresaTelConj = new javax.swing.JFormattedTextField();
        jLabel36 = new javax.swing.JLabel();
        mfEmpresaRamalConj = new LimitedTextField(4);
        jLabel61 = new javax.swing.JLabel();
        mfCpfConj = new javax.swing.JFormattedTextField();
        jbtBuscaCep1 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        mfSexo = new javax.swing.JComboBox();
        jLabel62 = new javax.swing.JLabel();
        mfConjSexo = new javax.swing.JComboBox();
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

        buttonGroup1.add(jrbFisica);
        jrbFisica.setSelected(true);
        jrbFisica.setText("Física");
        jrbFisica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbFisicaActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbJuridica);
        jrbJuridica.setText("Jurídica");
        jrbJuridica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbJuridicaActionPerformed(evt);
            }
        });

        jLabel5.setText("Cnpj:");

        jLabel6.setText("RG/Insc:");

        try {
            mCpf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mCpf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mCpfFocusLost(evt);
            }
        });

        mRgprp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mRgprp.setDisabledTextColor(new java.awt.Color(21, 1, 176));
        mRgprp.setEnabled(false);

        mRgimv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mRgimv.setDisabledTextColor(new java.awt.Color(1, 169, 37));
        mRgimv.setEnabled(false);

        try {
            mCnpj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##.###.###/####-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        mCnpj.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mCnpjFocusLost(evt);
            }
        });

        jLabel7.setText("Cpf:");

        javax.swing.GroupLayout jDadosIniciaisLayout = new javax.swing.GroupLayout(jDadosIniciais);
        jDadosIniciais.setLayout(jDadosIniciaisLayout);
        jDadosIniciaisLayout.setHorizontalGroup(
            jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addComponent(mTpImv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                        .addComponent(jrbFisica)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jrbJuridica)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addGap(8, 8, 8)
                        .addComponent(mCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mIdentidade, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jDadosIniciaisLayout.setVerticalGroup(
            jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mTpImv, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDadosIniciaisLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mIdentidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jDadosIniciaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jrbFisica)
                        .addComponent(jrbJuridica)
                        .addComponent(mCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jJuridica.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jJuridica.setPreferredSize(new java.awt.Dimension(808, 329));

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Razão Social:");

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Nome Fantasia:");

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

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Telefone:");

        try {
            mjTelefone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)*####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel56.setText("Ramal:");

        jLabel57.setText("Celular:");

        try {
            mjCelular.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)*####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

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
                .addComponent(jScrollPane4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btFicha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btExcluirFicha))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btFicha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btExcluirFicha)
                        .addGap(65, 65, 65))))
        );

        jbtBuscaCep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        jbtBuscaCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBuscaCepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jJuridicaLayout = new javax.swing.GroupLayout(jJuridica);
        jJuridica.setLayout(jJuridicaLayout);
        jJuridicaLayout.setHorizontalGroup(
            jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jJuridicaLayout.createSequentialGroup()
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jJuridicaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jJuridicaLayout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjFantasia))
                            .addGroup(jJuridicaLayout.createSequentialGroup()
                                .addComponent(jLabel48)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtBuscaCep, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(jLabel49)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jJuridicaLayout.createSequentialGroup()
                                        .addComponent(jLabel53)
                                        .addGap(10, 10, 10)
                                        .addComponent(mjEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jJuridicaLayout.createSequentialGroup()
                                        .addComponent(jLabel50)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mjCplto))))))
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mjRazao))
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel60)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mjEmail))
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jJuridicaLayout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                                .addComponent(jLabel54))
                            .addGroup(jJuridicaLayout.createSequentialGroup()
                                .addComponent(jLabel55)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel56)
                                .addGap(1, 1, 1)
                                .addComponent(mjRamal, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel57)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mjCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel58)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mjCep)
                            .addComponent(mjDtContratoSocial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jJuridicaLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel46, jLabel47, jLabel48, jLabel51, jLabel55, jLabel60});

        jJuridicaLayout.setVerticalGroup(
            jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jJuridicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjRazao, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mjFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mjEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mjNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mjCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtBuscaCep, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mjBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mjEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mjCep, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(mjCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jJuridicaLayout.createSequentialGroup()
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjRamal, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mjCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jJuridicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mjEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(mjDtContratoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jJuridicaLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel46, jLabel47, jLabel48, jLabel51, jLabel55, jLabel60});

        jDados.addTab("Jurídica", jJuridica);

        jFisica.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jFisica.setEnabled(false);
        jFisica.setMaximumSize(new java.awt.Dimension(763, 334));
        jFisica.setMinimumSize(new java.awt.Dimension(763, 334));
        jFisica.setPreferredSize(new java.awt.Dimension(763, 334));

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Nome:");

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel9.setText("Dt.Nasc:");

        mfDtNasc.setDate(new java.util.Date(-2208977612000L));

        mfNacionalidade.setName("mfNacionalidade"); // NOI18N

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel15.setText("Est.Civil:");

        mfEstCivil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Solteriro(a)", "Casado(a)", "Separado(a)", "Divorciado(a)", "Viuvo(a)" }));
        mfEstCivil.setName("mfEstCivil"); // NOI18N

        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Nacionalidade:");

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setText("Tels.:");

        try {
            mfTel2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)*####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            mfTel1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)*####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Mãe:");

        mfMae.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mfMaeKeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel12.setText("Pai:");

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Empresa:");

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel16.setText("Dt.Adm:");

        mfDtAdmis.setDate(new java.util.Date(-2208977612000L));

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Endereço:");

        mfEndereco.setName("mfEndereco"); // NOI18N

        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel18.setText("N°.:");

        mfNumero.setName("mfNumero"); // NOI18N

        mfCplto.setName("mfCplto"); // NOI18N

        jLabel20.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Bairro:");

        mfBairro.setName("mfBairro"); // NOI18N

        jLabel21.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel21.setText("Cidade:");

        mfCidade.setName("mfCidade"); // NOI18N

        jLabel22.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel22.setText("UF:");

        mfEstado.setName("mfEstado"); // NOI18N

        jLabel23.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel23.setText("Cep:");

        try {
            mfCep.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("*****-***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel24.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Telefone:");

        try {
            mfTelEmpresa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)*####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        mfRamalEmpresa.setName("mNumero"); // NOI18N

        jLabel25.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel25.setText("Ramal:");

        jLabel26.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel26.setText("Cargo:");

        mfCargo.setName("mCidade"); // NOI18N

        jLabel27.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel27.setText("Salário:");

        mfSalario.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mfSalario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mfSalario.setText("0,00");

        jLabel28.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("E-Mail:");

        jLabel29.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Conjugue:");

        jLabel30.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Dt.Nasc:");

        mfDtNascConj.setDate(new java.util.Date(-2208977612000L));

        jLabel31.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel31.setText("Cpfj:");

        jLabel32.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel32.setText("RG:");

        jLabel33.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel33.setText("Salário:");

        mfSalarioConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        mfSalarioConj.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mfSalarioConj.setText("0,00");

        jLabel34.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("Empresa:");

        jLabel35.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel35.setText("Telefones:");

        try {
            mfEmpresaTelConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)*####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel36.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel36.setText("Ramal:");

        mfEmpresaRamalConj.setName("mNumero"); // NOI18N

        jLabel61.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel61.setText("Cplto");

        try {
            mfCpfConj.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***.***.***-**")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jbtBuscaCep1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        jbtBuscaCep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBuscaCep1ActionPerformed(evt);
            }
        });

        jLabel19.setText("Sexo:");

        mfSexo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "F" }));

        jLabel62.setText("Sexo:");

        mfConjSexo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "F" }));

        javax.swing.GroupLayout jFisicaLayout = new javax.swing.GroupLayout(jFisica);
        jFisica.setLayout(jFisicaLayout);
        jFisicaLayout.setHorizontalGroup(
            jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFisicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jFisicaLayout.createSequentialGroup()
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jFisicaLayout.createSequentialGroup()
                                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addComponent(mfDtNascConj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel31)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfCpfConj, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(mfEmpresaConj, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addComponent(jLabel32)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfIdentidadeConj, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(jLabel35)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfEmpresaTelConj, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel36)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfEmpresaRamalConj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jFisicaLayout.createSequentialGroup()
                                            .addComponent(jLabel33)
                                            .addGap(3, 3, 3)
                                            .addComponent(mfSalarioConj, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(mfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jFisicaLayout.createSequentialGroup()
                                                .addComponent(mfConjugue, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel62)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfConjSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(jFisicaLayout.createSequentialGroup()
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jFisicaLayout.createSequentialGroup()
                                        .addComponent(mfEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel16))
                                    .addGroup(jFisicaLayout.createSequentialGroup()
                                        .addComponent(mfEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jbtBuscaCep1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18)
                                        .addGap(2, 2, 2)
                                        .addComponent(mfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jFisicaLayout.createSequentialGroup()
                                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addComponent(mfBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel21))
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addComponent(mfTelEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel25)
                                                .addGap(6, 6, 6)
                                                .addComponent(mfRamalEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(jLabel26)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfCargo))
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addComponent(mfCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel22)))
                                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addGap(36, 36, 36)
                                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jFisicaLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel23)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(mfCep, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFisicaLayout.createSequentialGroup()
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jFisicaLayout.createSequentialGroup()
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(mfNome)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jFisicaLayout.createSequentialGroup()
                                        .addComponent(mfNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mfEstCivil, 0, 127, Short.MAX_VALUE))
                                    .addComponent(mfMae, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jFisicaLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(mfPai))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFisicaLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mfTel1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mfTel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFisicaLayout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mfSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mfDtNasc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                            .addGroup(jFisicaLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(mfDtAdmis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jFisicaLayout.createSequentialGroup()
                                        .addComponent(jLabel61)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mfCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(42, 42, 42))))
        );
        jFisicaLayout.setVerticalGroup(
            jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFisicaLayout.createSequentialGroup()
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mfNome, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfDtNasc, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(mfSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(mfNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfTel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfTel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfEstCivil, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mfMae, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mfPai, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mfDtAdmis, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfCplto, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtBuscaCep1, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfCep, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(mfTelEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfRamalEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFisicaLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mfSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFisicaLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jFisicaLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mfDtNascConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(mfIdentidadeConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(mfCpfConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(mfSalarioConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFisicaLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                            .addComponent(mfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mfConjugue, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel62)
                            .addComponent(mfConjSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)))
                .addGroup(jFisicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(mfEmpresaConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfEmpresaTelConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mfEmpresaRamalConj, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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

        jLabel63.setText("Dias Antecedência:");

        jcbxEnvio.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 - Em mãos", "2 - Correio Eletrônico", "3 - Correspondeência" }));

        jLabel64.setText("Forma de Envio:");

        jCheckBoxCobAut.setText("Cobrança automática");

        jCheckBoxCobEmail.setText("E-Mail");
        jCheckBoxCobEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxCobEmailActionPerformed(evt);
            }
        });

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
                        .addComponent(mMsgBol, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDadosAvisosLayout.createSequentialGroup()
                        .addComponent(jcbxBcoBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbxEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxCobAut)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addComponent(btIncluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btExcluir)
                .addGap(31, 31, 31)
                .addComponent(btCarteira)
                .addGap(30, 30, 30)
                .addGroup(jBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btTras)
                    .addComponent(btFrente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btIrPara)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btFiador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPagtos)
                .addGap(76, 76, 76)
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

        jLabel65.setText("Fiadores");
        jLabel65.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDadosAvisos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDadosIniciais, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jDados, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 860, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jDadosIniciais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDados, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBotoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDadosAvisos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
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
            try {
                LerDados(false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            LimpaDados();

            String tRgprp = oInc.getRgprp();
            String tRgimv = oInc.getRgimv();
            String tContrato = oInc.getContrato();
            String tTpImv = oInc.getTpImv();

            mRgprp.setText(tRgprp);
            mRgimv.setText(tRgimv);
            mContrato.setText(tContrato);
            mTpImv.setText(tTpImv);

            jrbFisica.setEnabled(true);
            jrbJuridica.setEnabled(true);
            jDados.setEnabledAt(0, false);
            jDados.setEnabledAt(1, false);
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

    private void btTrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTrasActionPerformed
        try {
            boolean previous = pResult.previous();
            if (previous) LerDados(false);
        } catch (SQLException ex) {}
}//GEN-LAST:event_btTrasActionPerformed

    private void btFrenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFrenteActionPerformed
        try {
            boolean next = pResult.next();
            if (next) LerDados(false);
        } catch (SQLException ex) {}
}//GEN-LAST:event_btFrenteActionPerformed

    private void btIrParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIrParaActionPerformed
        mContrato.setEnabled(true);
        mContrato.setEditable(true);
        mContrato.selectAll();
        mContrato.requestFocus();
}//GEN-LAST:event_btIrParaActionPerformed

    private void btGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGravarActionPerformed

        if (jrbFisica.isSelected() && mfNome.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'NOME' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mfNome.requestFocus();
            return;
        }

        if (jrbJuridica.isSelected() && mjRazao.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'RAZÃO' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mjRazao.requestFocus();
            return;
        }

        if (jrbFisica.isSelected() && mCpf.getText().replace(".", "").replace("-", "").trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'CPF' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mCpf.requestFocus();
            return;
        }

        if (jrbJuridica.isSelected() && mCnpj.getText().replace(".", "").replace("-", "").replace("/","").trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'CNPJ' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            mCnpj.requestFocus();
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
            try {
                LerDados(false);

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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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

//        Object[] options = { "Sim", "Não" };
//        int n = JOptionPane.showOptionDialog(null,
//                "Deseja excluir este locatário ? \nIra apagar todas as informações...\nSem retorno.",
//                "Atenção", JOptionPane.YES_NO_OPTION,
//                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
//        if (n == JOptionPane.YES_OPTION) {
//            String sql = "DELETE FROM locatarios WHERE contrato='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            sql = "DELETE FROM carteira WHERE contrato='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            //sql = "DELETE FROM recibo WHERE contrato='" + mContrato.getText() + "';";
//            //conn.ExecutarComando(sql);
//
//            sql = "DELETE FROM descontos WHERE contrato='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            sql = "DELETE FROM diferenca WHERE contrato='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            sql = "DELETE FROM seguros WHERE contrato='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            //sql = "DELETE FROM auxiliar WHERE contrato='" + mContrato.getText() + "';";
//            //conn.ExecutarComando(sql);
//
//            sql = "DELETE FROM avisos WHERE registro='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            //sql = "DELETE FROM caixa WHERE cx_contrato='" + mContrato.getText() + "';";
//            //conn.ExecutarComando(sql);
//
//            //sql = "DELETE FROM caixabck WHERE cx_contrato='" + mContrato.getText() + "';";
//            //conn.ExecutarComando(sql);
//
//            //sql = "DELETE FROM extrato WHERE contrato='" + mContrato.getText() + "';";
//            //conn.ExecutarComando(sql);
//
//            sql = "DELETE FROM fiadores WHERE contrato='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            //sql = "DELETE FROM imposto WHERE contrato='" + mContrato.getText() + "';";
//            //conn.ExecutarComando(sql);
//
//            //sql = "DELETE FROM razao WHERE contrato='" + mContrato.getText() + "';";
//            //conn.ExecutarComando(sql);
//
//            sql = "DELETE FROM retencao WHERE contrato='" + mContrato.getText() + "';";
//            conn.ExecutarComando(sql);
//
//            JOptionPane.showMessageDialog(null, "Locatário excluido!!!");
//            this.dispose();
//        }
    }//GEN-LAST:event_btExcluirActionPerformed

    private void btFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFichaActionPerformed
        int selRow = jSocios.getSelectedRow();
        if (selRow > -1) {
            VariaveisGlobais.pResult = pResult;
            VariaveisGlobais.mContrato = mContrato.getText();
            VariaveisGlobais.mQtdSoc = jSocios.getRowCount();
            VariaveisGlobais.mPosSoc = selRow + 1;
        } else {
            VariaveisGlobais.pResult = pResult;
            VariaveisGlobais.mContrato = ""; //mContrato.getText();
            VariaveisGlobais.mQtdSoc = jSocios.getRowCount();
            VariaveisGlobais.mPosSoc = 0;
        }

        try {
            jFichaSocios oSoc = new jFichaSocios(null, closable);
            oSoc.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Atualiza table
        try {pResult.refreshRow();} catch (SQLException e) {}
        FillSocios(jSocios, pResult);
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
        FillSocios(jSocios, pResult);
}//GEN-LAST:event_btExcluirFichaActionPerformed

    private void ExcluirSocio() throws SQLException {
        int selRow = jSocios.getSelectedRow();
        if (selRow > -1) {
            pResult.updateString("socionome" + (selRow + 1), "");
            pResult.updateString("sociodtnasc" + (selRow + 1), "0000-00-00");
            pResult.updateString("socionac" + (selRow + 1), "");
            pResult.updateString("socioecivil" + (selRow + 1), "");
            pResult.updateString("sociocpf" + (selRow + 1), null);
            pResult.updateString("sociorg" + (selRow + 1), "");
            pResult.updateString("sociosalario" + (selRow + 1), "");
            pResult.updateString("sociocargo" + (selRow + 1), "");
            pResult.updateString("sociomae" + (selRow + 1), "");
            pResult.updateString("sociopai" + (selRow + 1),"");

            pResult.updateRow();
        }

    }

    private void jrbFisicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbFisicaActionPerformed
        mCpf.setEnabled(true);
        jDados.setEnabledAt(1, true);
        jDados.setEnabledAt(0, false);
        mCnpj.setEnabled(false);
        jDados.setSelectedIndex(1);
    }//GEN-LAST:event_jrbFisicaActionPerformed

    private void jrbJuridicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbJuridicaActionPerformed
        mCpf.setEnabled(false);
        jDados.setEnabledAt(1, false);
        jDados.setEnabledAt(0, true);
        mCnpj.setEnabled(true);
        jDados.setSelectedIndex(0);
    }//GEN-LAST:event_jrbJuridicaActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // Retornat status de inativos
        VariaveisGlobais.Iloca = false;
    }//GEN-LAST:event_formInternalFrameClosing

    private void mfMaeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mfMaeKeyPressed
        
    }//GEN-LAST:event_mfMaeKeyPressed

    private void mCpfFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mCpfFocusLost
        if (!FuncoesGlobais.ValidarCPFCNPJ(mCpf.getText())) mCpf.requestFocus();
    }//GEN-LAST:event_mCpfFocusLost

    private void mCnpjFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mCnpjFocusLost
        if (!FuncoesGlobais.ValidarCPFCNPJ(mCnpj.getText())) mCnpj.requestFocus();
    }//GEN-LAST:event_mCnpjFocusLost

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

    private void jCheckBoxCobEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxCobEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxCobEmailActionPerformed
    
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
    private javax.swing.ButtonGroup buttonGroup1;
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
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel7;
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
    private javax.swing.JRadioButton jrbFisica;
    private javax.swing.JRadioButton jrbJuridica;
    private javax.swing.JTextField mAviso;
    private javax.swing.JCheckBox mBoleta;
    private javax.swing.JFormattedTextField mCnpj;
    private javax.swing.JTextField mContrato;
    private javax.swing.JFormattedTextField mCpf;
    private javax.swing.JTextField mIdentidade;
    private javax.swing.JTextField mMsgBol;
    private javax.swing.JTextField mRgimv;
    private javax.swing.JTextField mRgprp;
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
    private javax.swing.JTextField mfEmpresaConj;
    private javax.swing.JTextField mfEmpresaRamalConj;
    private javax.swing.JFormattedTextField mfEmpresaTelConj;
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
    private javax.swing.JFormattedTextField mfTel1;
    private javax.swing.JFormattedTextField mfTel2;
    private javax.swing.JFormattedTextField mfTelEmpresa;
    private javax.swing.JTextField mjBairro;
    private javax.swing.JFormattedTextField mjCelular;
    private javax.swing.JFormattedTextField mjCep;
    private javax.swing.JTextField mjCidade;
    private javax.swing.JTextField mjCplto;
    private com.toedter.calendar.JDateChooser mjDtContratoSocial;
    private javax.swing.JTextField mjEmail;
    private javax.swing.JTextField mjEndereco;
    private javax.swing.JTextField mjEstado;
    private javax.swing.JTextField mjFantasia;
    private javax.swing.JTextField mjNumero;
    private javax.swing.JTextField mjRamal;
    private javax.swing.JTextField mjRazao;
    private javax.swing.JFormattedTextField mjTelefone;
    private javax.swing.JTable tbFiadores;
    // End of variables declaration//GEN-END:variables
}