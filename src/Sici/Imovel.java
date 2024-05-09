package Sici;

import Funcoes.CEPEndereco;
import Funcoes.CamposScreen;
import Funcoes.ClienteViaCepWS;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.LimitedTextField;
import Funcoes.Pad;
import Funcoes.StreamFile;
import Funcoes.VariaveisGlobais;
import Protocolo.Calculos;
import Sici.Locatarios.BuscaCep;
import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Imovel extends javax.swing.JDialog {
    private Db conn = VariaveisGlobais.conexao;
    //private ResultSet pResult = conn.OpenTable("SELECT * FROM imoveis ORDER BY rgprp, rgimv;", ResultSet.CONCUR_UPDATABLE, null);
    private boolean bNew = false;
    private String _botoes = null;

    // Botoes que acompanham a tela
    private boolean _Incluir = true;
    private boolean _Alterar = true;
    private boolean _Inativar = true;
    private boolean _Carteira = true;
    private boolean _Baixar = true;
    private boolean _Fotos = true;
    
    public void setBotoes(String _botoes) {
        this._botoes = _botoes;
    }
            
    public Imovel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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

                    Pos = FuncoesGlobais.IndexOf(btn, "inativar");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Inativar = new Boolean(_btn[1].replace("\"", ""));
                    }

                    Pos = FuncoesGlobais.IndexOf(btn, "carteira");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Carteira = new Boolean(_btn[1].replace("\"", ""));
                    }

                    Pos = FuncoesGlobais.IndexOf(btn, "baixar");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Baixar = new Boolean(_btn[1].replace("\"", ""));
                    }

                    Pos = FuncoesGlobais.IndexOf(btn, "fotos");
                    if (Pos > -1) {
                        String[] _btn = btn[Pos].split("=");
                        _Fotos = new Boolean(_btn[1].replace("\"", ""));
                    }
                }
                
                iBtIncluir.setEnabled(_Incluir);
                iBtExcluir.setEnabled(_Inativar);
                iBtCarteira.setEnabled(_Carteira);
                jbtBaixar.setEnabled(_Baixar);
                iBtFotos.setEnabled(_Fotos);
                iBtGravar.setEnabled(_Alterar);
            }
        });        
        
        //Centraliza a janela.
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = (int) (dimension.getWidth() - this.getSize().getWidth() ) / 2;
        int y = (int) (dimension.getHeight() - this.getSize().getHeight()) / 2;
        this.setLocation(x,y);

        // Colocando enter para pular de campo
        HashSet conj = new HashSet(this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        conj.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, conj);

        if (!"".equals(VariaveisGlobais.rgimv)) {
            try { MoveToImovel(VariaveisGlobais.rgprp, VariaveisGlobais.rgimv); } catch (Exception ex) {}
        } else {
            LimpaDados();
            iBtIncluir.setEnabled(true && _Incluir);
            iBtCarteira.setEnabled(false && _Carteira);
            iBtGravar.setEnabled(false && _Alterar);
            iBtRetornar.setEnabled(true);
            jbtBaixar.setEnabled(false && _Baixar);
            iBtExcluir.setEnabled(true && _Inativar);
        }

        new CamposScreen(iCep,"CEP");
        iTipoImv.requestFocus();
    }

    private boolean MoveToImovel(String srgprp, String srgimv) {
        boolean achei = false;
        ResultSet pResult = conn.OpenTable("SELECT * FROM imoveis WHERE `rgprp` = :rgprp AND `rgimv` = :rgimv ORDER BY `rgprp`, `rgimv` LIMIT 1;", new Object[][] {
            {"int", "rgprp", Integer.parseInt(srgprp)},
            {"int", "rgimv", Integer.parseInt(srgimv)}
        });
        try {
            while (pResult.next()) {
                achei = true;
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(pResult);
        if (!achei) LimpaDados();
        try { LerDados(); } catch (Exception ex) {}
        return achei;
    }
    
    private void LimpaDados() {
        iRgimv.setText("");
        iTipoImv.setSelectedIndex(0);
        iUrbRural.setSelectedIndex(0);
        iSituacao.setSelectedIndex(0);
        iEndereco.setText("");
        iNumero.setText("");
        iCplto.setText("");
        iBairro.setText("");
        iCidade.setText("");
        iCodCidade.setSelectedIndex(-1);
        iEstado.setText("");
        iCep.setText("");
        iDescricao.setText("");
        iMsg.setText("");
        iObs.setText("");
        iReter.setSelected(false);
        iMatriculas.removeAllItems();
    }

    private void LerDados() throws SQLException {
        ResultSet pResult = conn.OpenTable("SELECT * FROM imoveis WHERE `rgprp` = :rgprp AND `rgimv` = :rgimv ORDER BY `rgprp`, `rgimv` LIMIT 1;", new Object[][] {
            {"int", "rgprp", Integer.parseInt(VariaveisGlobais.rgprp)},
            {"int", "rgimv", Integer.parseInt(VariaveisGlobais.rgimv)}
        });
        
        boolean achei = false;
        try {
            while (pResult.next()) {
                achei = true;
                
                iRgimv.setText(pResult.getString("rgimv"));

                String pTipoImv = pResult.getString("tpimovel").trim().toLowerCase();
                String sTipoImv[] = {"residencial","não residencial","comercial"};
                Integer nPos = FuncoesGlobais.IndexOf(sTipoImv,pTipoImv) ;
                if (nPos == -1) {
                    nPos = 0;
                }
                iTipoImv.setSelectedIndex(nPos);

                String pUrbRural = pResult.getString("tpurbrural").trim().toLowerCase();
                String sUrbRural[] = {"urbano","rural"};
                Integer nPos2 = FuncoesGlobais.IndexOf(sUrbRural,pUrbRural) ;
                if (nPos2 == -1) {
                    nPos2 = 0;
                }
                iUrbRural.setSelectedIndex(nPos2);

                String pSituacao = pResult.getString("situacao").trim().toLowerCase();

                iSituacao.getModel().setSelectedItem(pSituacao);
                iEndereco.setText(pResult.getString("end"));
                iNumero.setText(pResult.getString("num"));
                iCplto.setText(pResult.getString("compl"));
                iBairro.setText(pResult.getString("bairro"));
                iCidade.setText(pResult.getString("cidade"));

                // Buscar cod cidade
                String _codCid = pResult.getString("codcid"); int index = -1; boolean _acheiCidade = false;
                for (index = 0; index <= iCodCidade.getItemCount() - 1; index++) {
                    if (_codCid.substring(0,4).equalsIgnoreCase(iCodCidade.getItemAt(index).substring(0, 4))) {
                        _acheiCidade = true;
                        break;
                    }
                }
                if (!_acheiCidade) index = -1;
                iCodCidade.setSelectedIndex(index);

                iEstado.setText(pResult.getString("estado").toUpperCase());
                iCep.setText(pResult.getString("cep"));

                iDescricao.setText(pResult.getString("especifica"));
                iMsg.setText(pResult.getString("menssagem"));
                iObs.setText(pResult.getString("obs"));

                // Atualização de 07/07/2014
                try {
                    iReter.setSelected(pResult.getBoolean("reter"));
                    String matr = pResult.getString("matriculas");
                    if (matr != null) {
                        String mtr[] = matr.split(";");
                        for (int i=0;i<mtr.length;i++) {
                            String mtc[] = mtr[i].split(":");
                            iMatriculas.addItem(mtc[0] + " - " + mtc[1]);
                        }
                    }
                } catch (Exception e) {e.printStackTrace();}
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(pResult);
        if (!achei) LimpaDados();        
    }
    
    private String LerLegendas() {
        ResultSet lg = conn.OpenTable("SELECT CART_CODIGO, CART_DESCR FROM lancart WHERE CART_TAXA = '1' ORDER BY CART_CODIGO;", null);
        String outTxt = "<html><center>T A X A S</center><br>";
        try {
            while (lg.next()) {
                outTxt += lg.getString("CART_CODIGO") + " - " + lg.getString("CART_DESCR") + "<br>";
            }
            outTxt += "</html>";
        } catch (Exception e) {}
        conn.CloseTable(lg);
        return outTxt;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        iRgimv = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        iTipoImv = new javax.swing.JComboBox<>();
        iUrbRural = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        iSituacao = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        iEndereco = new LimitedTextField(60);
        jbtBuscaCep = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        iNumero = new LimitedTextField(10);
        jLabel6 = new javax.swing.JLabel();
        iCplto = new LimitedTextField(15);
        jLabel7 = new javax.swing.JLabel();
        iBairro = new LimitedTextField(25);
        jLabel8 = new javax.swing.JLabel();
        iCidade = new LimitedTextField(25);
        jLabel9 = new javax.swing.JLabel();
        iCodCidade = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        iEstado = new LimitedTextField(2);
        jLabel11 = new javax.swing.JLabel();
        iCep = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        iMatriculas = new javax.swing.JComboBox<>();
        mtDel = new javax.swing.JButton();
        iReter = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        iDescricao = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        iMsg = new LimitedTextField(60);
        iObs = new LimitedTextField(60);
        jPanel2 = new javax.swing.JPanel();
        iBtIncluir = new javax.swing.JButton();
        iBtExcluir = new javax.swing.JButton();
        iBtCarteira = new javax.swing.JButton();
        jbtBaixar = new javax.swing.JButton();
        iBtFotos = new javax.swing.JButton();
        iBtGravar = new javax.swing.JButton();
        iBtRetornar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(".:: Imóvel do Proprietário.");
        setAlwaysOnTop(true);
        setMaximumSize(new java.awt.Dimension(624, 399));
        setMinimumSize(new java.awt.Dimension(624, 399));
        setModal(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "[ Informações do Imóvel]"));

        jLabel1.setText("RgImv:");

        iRgimv.setEditable(false);

        jLabel2.setText("Tipo:");

        iTipoImv.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Residencial", "Não Residencial", "Comercial" }));

        iUrbRural.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Urbano", "Rural" }));

        jLabel3.setText("Situação:");

        iSituacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ação", "Pendencia", "Reservado" }));

        jLabel4.setText("Endereço::");

        jbtBuscaCep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/find.png"))); // NOI18N
        jbtBuscaCep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBuscaCepActionPerformed(evt);
            }
        });

        jLabel5.setText("Num.:");

        jLabel6.setText("Cplto:");

        jLabel7.setText("Bairro:");

        jLabel8.setText("Cidade:");

        jLabel9.setText("Cod.Municipio:");

        iCodCidade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5801 - ANGRA DOS REIS\t", "2919 - APERIBE\t", "5803 - ARARUAMA\t", "2925 - AREAL\t", "0770 - ARMACAO DE BUZIOS\t", "5927 - ARRAIAL DO CABO\t", "5805 - BARRA DO PIRAI\t", "5807 - BARRA MANSA\t", "2909 - BELFORD ROXO\t", "5809 - BOM JARDIM\t", "5811 - BOM JESUS DO ITABAPOANA\t", "5813 - CABO FRIO\t", "5815 - CACHOEIRAS DE MACACU\t", "5817 - CAMBUCI\t", "5819 - CAMPOS DOS GOYTACAZES\t", "5821 - CANTAGALO\t", "0772 - CARAPEBUS\t", "2915 - CARDOSO MOREIRA\t", "5823 - CARMO\t", "5825 - CASIMIRO DE ABREU\t", "2927 - COMENDADOR LEVY GASPARIAN\t", "5827 - CONCEICAO DE MACABU\t", "5829 - CORDEIRO\t", "5831 - DUAS BARRAS\t", "5833 - DUQUE DE CAXIAS\t", "5835 - ENGENHEIRO PAULO DE FRONTIN\t", "2907 - GUAPIMIRIM\t", "0774 - IGUABA GRANDE\t", "5837 - ITABORAI\t", "5839 - ITAGUAI\t", "5929 - ITALVA\t", "5841 - ITAOCARA\t", "5843 - ITAPERUNA\t", "6003 - ITATIAIA\t", "2913 - JAPERI\t", "5845 - LAJE DO MURIAE\t", "5847 - MACAE\t", "0776 - MACUCO\t", "5849 - MAGE\t", "5851 - MANGARATIBA\t", "5853 - MARICA\t", "5855 - MENDES\t", "1116 - MESQUITA\t", "5857 - MIGUEL PEREIRA\t", "5859 - MIRACEMA\t", "5861 - NATIVIDADE\t", "5863 - NILOPOLIS\t", "5865 - NITEROI\t", "5867 - NOVA FRIBURGO\t", "5869 - NOVA IGUACU\t", "5871 - PARACAMBI\t", "5873 - PARAIBA DO SUL\t", "5875 - PARATI\t", "6005 - PATY DO ALFERES\t", "0000 - PELINCA\t", "5877 - PETROPOLIS\t", "0778 - PINHEIRAL\t", "0778 - PINHEIRAL\t", "5879 - PIRAI\t", "5881 - PORCIUNCULA\t", "0780 - PORTO REAL\t", "2923 - QUATIS\t", "2911 - QUEIMADOS\t", "6007 - QUISSAMA\t", "5883 - RESENDE\t", "5885 - RIO BONITO\t", "5887 - RIO CLARO\t", "5889 - RIO DAS FLORES\t", "2921 - RIO DAS OSTRAS\t", "6001 - RIO DE JANEIRO\t", "5891 - SANTA MARIA MADALENA\t", "5893 - SANTO ANTONIO DE PADUA\t", "5895 - SAO FIDELIS\t", "0782 - SAO FRANCISCO DE ITABAPOANA\t", "5897 - SAO GONCALO\t", "5899 - SAO JOAO DA BARRA\t", "5901 - SAO JOAO DE MERITI\t", "0784 - SAO JOSE DE UBA\t", "6009 - SAO JOSE DO VALE DO RIO PRETO\t", "5903 - SAO PEDRO DA ALDEIA\t", "5905 - SAO SEBASTIAO DO ALTO\t", "5907 - SAPUCAIA\t", "5909 - SAQUAREMA\t", "0786 - SEROPEDICA\t", "5911 - SILVA JARDIM\t", "5913 - SUMIDOURO\t", "0788 - TANGUA\t", "5915 - TERESOPOLIS\t", "5917 - TRAJANO DE MORAIS\t", "5919 - TRES RIOS\t", "5921 - VALENCA\t", "2917 - VARRE-SAI\t", "5923 - VASSOURAS\t", "5925 - VOLTA REDONDA" }));

        jLabel10.setText("Estado:");

        jLabel11.setText("Cep:");

        iCep.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                iCepFocusLost(evt);
            }
        });

        jLabel12.setText("Mats: das taxas cadastradas:");

        mtDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/stop.png"))); // NOI18N
        mtDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mtDelActionPerformed(evt);
            }
        });

        iReter.setText("Quando vazio reter taxas.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jbtBuscaCep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iCplto))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iCodCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iCep, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iTipoImv, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iUrbRural, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iMatriculas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(mtDel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(iReter)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(iRgimv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(iTipoImv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iUrbRural, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(iSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(iNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jbtBuscaCep)
                    .addComponent(iEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(iCplto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(iBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(iCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(iCodCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(iEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(iCep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(iMatriculas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(iReter, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(mtDel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/editor_0304_text_backgroundcolor.png"))); // NOI18N
        jLabel13.setText("Especificações do Imóvel");
        jLabel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        iDescricao.setColumns(20);
        iDescricao.setRows(5);
        jScrollPane1.setViewportView(iDescricao);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/webmaster_2531_text_align_justify.png"))); // NOI18N
        jLabel14.setText("Textos de Mensagem / Observações");
        jLabel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "[ Opções ]"));

        iBtIncluir.setText("Incluir");
        iBtIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iBtIncluirActionPerformed(evt);
            }
        });

        iBtExcluir.setText("Inativar");
        iBtExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iBtExcluirActionPerformed(evt);
            }
        });

        iBtCarteira.setText("Carteira");
        iBtCarteira.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iBtCarteiraActionPerformed(evt);
            }
        });

        jbtBaixar.setText("Baixar");
        jbtBaixar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtBaixarActionPerformed(evt);
            }
        });

        iBtFotos.setText("Fotos");
        iBtFotos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iBtFotosActionPerformed(evt);
            }
        });

        iBtGravar.setText("Gravar");
        iBtGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iBtGravarActionPerformed(evt);
            }
        });

        iBtRetornar.setText("Retornar");
        iBtRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iBtRetornarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iBtIncluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iBtExcluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iBtCarteira)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtBaixar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iBtFotos)
                .addGap(18, 18, 18)
                .addComponent(iBtGravar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iBtRetornar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iBtIncluir)
                    .addComponent(iBtExcluir)
                    .addComponent(iBtCarteira)
                    .addComponent(jbtBaixar)
                    .addComponent(iBtFotos)
                    .addComponent(iBtGravar)
                    .addComponent(iBtRetornar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(iMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(iObs, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iMsg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iObs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iBtGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iBtGravarActionPerformed
        if (iTipoImv.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(null, "Campo 'TIPO DO IMOVEL' não pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iTipoImv.requestFocus();
            return;
        }

        if (iUrbRural.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(null, "Campo 'URBANO/RURAL' não pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iUrbRural.requestFocus();
            return;
        }

        if (iSituacao.getModel().getSelectedItem().toString().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'SITUAÇÃO DO IMÓVEL' não pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iSituacao.requestFocus();
            return;
        }

        if (iEndereco.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'ENDEREÇO' não pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iEndereco.requestFocus();
            return;
        }

        if (iNumero.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'NUMERO' não pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iNumero.requestFocus();
            return;
        }

        if (iBairro.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'BAIRRO' não pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iBairro.requestFocus();
            return;
        }

        if (iCidade.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'CIDADE' não pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iCidade.requestFocus();
            return;
        }

        if (iCodCidade.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Campo 'CODIGO DA CIDADE' Obrigatório!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iCodCidade.requestFocus();
            return;
        }

        if (iEstado.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'ESTADO' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iEstado.requestFocus();
            return;
        }

        if (iCep.getText().trim().replace("-", "").length() == 0) {
            JOptionPane.showMessageDialog(null, "Campo 'CEP' nào pode ser vazio!!!", "Erro", JOptionPane.ERROR_MESSAGE);
            iCep.requestFocus();
            return;
        }

        GravarDados();

        bNew = false;

        iBtIncluir.setEnabled(true && _Incluir);
        iBtCarteira.setEnabled(true && _Carteira);
        iBtGravar.setEnabled(true && _Alterar);
        iBtRetornar.setEnabled(true);
        iBtExcluir.setEnabled(true && _Inativar);
        jbtBaixar.setEnabled(true && _Baixar);
    }//GEN-LAST:event_iBtGravarActionPerformed

    private void iBtRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iBtRetornarActionPerformed
        if (bNew) {
            try {
                LerDados();
                iBtIncluir.setEnabled(true && _Incluir);
                iBtCarteira.setEnabled(true && _Carteira);
                iBtGravar.setEnabled(true && _Alterar);
                iBtRetornar.setEnabled(true);
                iBtExcluir.setEnabled(true && _Inativar);
                jbtBaixar.setEnabled(true && _Baixar);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            bNew = false;
        } else this.dispose();
    }//GEN-LAST:event_iBtRetornarActionPerformed

    private void GravarDados() {
        int iNewRgImv = 0;
        if (bNew) {
            int NewRgImv = 0;
            try {
                NewRgImv = Integer.parseInt(conn.ReadParameters("PROP" + VariaveisGlobais.rgprp.trim()));
            } catch (SQLException ex) {
                NewRgImv = 0;
            } catch (NumberFormatException ex) {
                NewRgImv = 0;
            }
            int PropInc = 1;
            iNewRgImv = NewRgImv + PropInc;

            String cPar[] = {"PROP" + VariaveisGlobais.rgprp.trim(),String.valueOf(iNewRgImv),"NUMERICO"};
            try {
                conn.SaveParameters(cPar);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                iNewRgImv = Integer.parseInt(iRgimv.getText());
            } catch (NumberFormatException numEx) {
                JOptionPane.showMessageDialog(this, "Você não clicou no botão <Incluir>.");
                return;
            }
        }

        String matriculas = FuncoesGlobais.join(FuncoesGlobais.ComboLista(iMatriculas),";");

        String query = ""; Object[][] param = null;
        if (bNew) {
            query = "INSERT INTO `imoveis` (`rgprp`, `rgimv`, `tpimovel`, `tpurbrural`, `situacao`, `end`, " + 
                    "`num`, `compl`, `bairro`, `cidade`, `codcid`, `estado`, `cep`, `especifica`, `menssagem`, `obs`, `reter`, `matriculas`) " + 
                    "VALUES (:rgprp, :rgimv, :tpimovel, :tpurbrural, :situacao, :end, :num, :compl, :bairro, :cidade, " + 
                    ":codcid, :estado, :cep, :especifica, :menssagem, :obs, :reter, :matriculas);";
            param = new Object[][] {
                {"int", "rgprp", Integer.parseInt(VariaveisGlobais.rgprp)},
                {"int", "rgimv", Integer.parseInt(VariaveisGlobais.rgprp)+ iNewRgImv},
                {"string", "tpimovel", iTipoImv.getSelectedItem().toString().trim()},
                {"string", "tpurbrural", iUrbRural.getSelectedItem().toString().trim()},
                {"string", "situacao", iSituacao.getModel().getSelectedItem().toString().trim()},
                {"string", "end", iEndereco.getText().trim()},
                {"string", "num", iNumero.getText().trim()},
                {"string", "compl", iCplto.getText().trim()},
                {"string", "bairro", iBairro.getText().trim()},
                {"string", "cidade", iCidade.getText().trim()},
                {"string", "codcid", iCodCidade.getSelectedItem().toString().trim().subSequence(0, 4)},
                {"string", "estado", iEstado.getText().toUpperCase().trim()},
                {"string", "cep", iCep.getText().trim()},
                {"string", "especifica", iDescricao.getText().trim()},
                {"string", "menssagem", iMsg.getText().trim()},
                {"string", "obs", iObs.getText().trim()},
                {"boolean", "reter", iReter.isSelected()},
                {"string", "matriculas", !matriculas.isEmpty() ? matriculas : null}
            };
        } else {
            query = "UPDATE `imoveis` SET `tpimovel` = :tpimovel, `tpurbrural` = :tpurbrural, " + 
                    "`situacao` = :situacao, `end` = :end, `num` = :num, `compl` = :compl, `bairro` = :bairro, `cidade` = :cidade, " + 
                    "`codcid` = :codcid, `estado` = :estado, `cep` = :cep, `especifica` = :espec, `menssagem` = :menssa, " + 
                    "`obs` = :obs, `reter` = :reter, `matriculas` = :matri WHERE `rgprp` = :rgprp AND `rgimv` = :rgimv;";
            param = new Object[][] {
                {"string", "tpimovel", iTipoImv.getSelectedItem().toString().trim()},
                {"string", "tpurbrural", iUrbRural.getSelectedItem().toString().trim()},
                {"string", "situacao", iSituacao.getModel().getSelectedItem().toString().trim()},
                {"string", "end", iEndereco.getText().trim()},
                {"string", "num", iNumero.getText().trim()},
                {"string", "compl", iCplto.getText().trim()},
                {"string", "bairro", iBairro.getText().trim()},
                {"string", "cidade", iCidade.getText().trim()},
                {"string", "codcid", iCodCidade.getSelectedItem().toString().trim().subSequence(0, 4)},
                {"string", "estado", iEstado.getText().toUpperCase().trim()},
                {"string", "cep", iCep.getText().trim()},
                {"string", "espec", (iDescricao.getText().trim().isEmpty() ? null : iDescricao.getText().trim())},
                {"string", "menssa", (iMsg.getText().trim().isEmpty() ? null : iMsg.getText().trim())},
                {"string", "obs", (iObs.getText().trim().isEmpty() ? null : iObs.getText().trim())},
                {"boolean", "reter", iReter.isSelected()},
                {"string", "matri", !matriculas.isEmpty() ? matriculas : null},
                {"int", "rgprp", Integer.parseInt(VariaveisGlobais.rgprp)},
                {"int", "rgimv", iNewRgImv}
            };
        }

        conn.CommandExecute(query, param);
        
        if (bNew) {
            try {
                iRgimv.setText(Integer.toString(Integer.parseInt(VariaveisGlobais.rgprp)+ iNewRgImv));
            } catch (NumberFormatException exep) { return; }
        }

        if (!bNew) {
            if (iSituacao.getModel().getSelectedItem().toString().toUpperCase().equalsIgnoreCase("AÇÃO")) {
                // Exporta
                int seq = 1;
                String LF = "\r\n";

                String output = ""; String header = ""; String tipo2 = "";

                // Arquivo tipo H - Informações gerais do calculo
                ResultSet dbRecibos = conn.OpenTable("SELECT * FROM recibo r WHERE r.rgprp = :rgprp AND r.rgimv = :rgimv AND r.tag <> 'X' ORDER BY r.dtvencimento;", new Object[][] {
                    {"int", "rgprp", Integer.parseInt(VariaveisGlobais.rgprp)},
                    {"int", "rgimv", iNewRgImv}
                });

                Date dtInicial = null; Date DtFinal = null; String sContrato = "";
                try {
                    dbRecibos.first();
                    sContrato = dbRecibos.getString("contrato");
                    dtInicial = dbRecibos.getDate("dtvencimento");
                    dbRecibos.last();
                    DtFinal =  dbRecibos.getDate("dtvencimento");
                } catch (Exception e) {}
                conn.CloseTable(dbRecibos);

                Calculos rc = new Calculos();
                try {
                    rc.Inicializa(VariaveisGlobais.rgprp, String.valueOf(iNewRgImv).replace(".0",""), sContrato);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                ResultSet dbCarteira = conn.OpenTable("SELECT dtinicio FROM carteira WHERE contrato :contrato;", new Object[][] {{"string", "contrato", sContrato}});
                String dtContrato = null;
                try {
                    while (dbCarteira.next()) {
                        dtContrato = Dates.StringtoString(dbCarteira.getString("dtinicio"),"dd-MM-yyyy", "MM");
                    }
                } catch (Exception e) {}
                conn.CloseTable(dbCarteira);

                header = "H";                                                                   // H - para Header
                header += FuncoesGlobais.StrZero(String.valueOf(seq).replace(".0", ""), 10);    // sequencial
                header += Dates.DateFormata("yyyyMMdd", new Date());                            // Data Processamento
                header += Dates.DateFormata("yyyyMMdd", dtInicial);                             // dtinicial
                header += Dates.DateFormata("yyyyMMdd", DtFinal);                               // dtdinal
                header += FuncoesGlobais.GravaValores(LerValor.FloatToString(rc.multa_res), 4); // Multa contratual %
                header += FuncoesGlobais.StrZero(dtContrato, 2);                                // Mês de Reajuste
                header += FuncoesGlobais.GravaValores(LerValor.FloatToString(rc.per_jur), 4);   // Juros Processual %
                header += FuncoesGlobais.GravaValores("0,0000", 4);                             // Honorarios %
                header += FuncoesGlobais.GravaValores("0,00", 2);                               // Custas $
                header += FuncoesGlobais.myLetra(new Pad("", 60).RPad());                       // Nome do Advogado
                header += FuncoesGlobais.myLetra(new Pad("", 15).RPad());                       // OAB/EST
                header += FuncoesGlobais.Space(248);                                            // RESERVADO (Espaços em branco)

                seq++;

                // Arquivo Tipo 2 - Informações Prop/Inq/Imovel
                ResultSet dbDados = conn.OpenTable("SELECT p.rgprp, p.nome, p.cpfcnpj, i.rgimv, i.`end` as ende, i.`num` as numero, " + 
                          "i.compl, i.bairro, i.cidade, i.estado, i.cep, l.contrato, l.nomerazao, l.cpfcnpj FROM proprietarios p, imoveis i, " + 
                        "locatarios l WHERE p.rgprp = i.rgprp and i.rgimv = l.rgimv and l.contrato = :contrato;", new Object[][] {{"int", "contrato", Integer.parseInt(sContrato)}});
                String prgprp = "", pnome = "", pcpfcnpj = "", irgimv = "", iend = "", inum = "";
                String icompl = "", ibairro = "", icidade = "", iestado = "", icep = "";
                String lcontrato = "", lnomerazao = "", lcpfcnpj = "";
                try {
                    while (dbDados.next()) {
                        prgprp = dbDados.getString("p.rgprp"); 
                        pnome =  dbDados.getString("p.nome"); 
                        pcpfcnpj =  dbDados.getString("p.cpfcnpj");
                        irgimv =  dbDados.getString("i.rgimv"); 
                        iend =  dbDados.getString("i.ende");
                        inum =  dbDados.getString("i.numero"); 
                        icompl =  dbDados.getString("i.compl");
                        ibairro =  dbDados.getString("i.bairro"); 
                        icidade =  dbDados.getString("i.cidade"); 
                        iestado =  dbDados.getString("i.estado"); 
                        icep =  dbDados.getString("i.cep"); 
                        lcontrato =  dbDados.getString("l.contrato"); 
                        lnomerazao =  dbDados.getString("l.nomerazao"); 
                        lcpfcnpj =  dbDados.getString("l.cpfcnpj"); 
                    }
                } catch (Exception e) {}
                conn.CloseTable(dbDados);

                tipo2 = "2";                                                                     // Tipo 2
                tipo2 += FuncoesGlobais.StrZero(String.valueOf(seq).replace(".0", ""), 10);      // sequencial
                tipo2 += FuncoesGlobais.StrZero(prgprp, 6);                                      // Registro do proprietário
                tipo2 += FuncoesGlobais.myLetra(new Pad(pnome, 60).RPad());                      // Nome do proprietário
                tipo2 += FuncoesGlobais.StrZero(FuncoesGlobais.rmvNumero(pcpfcnpj.trim()), 18);  // CPF ou CNPJ do proprietário
                tipo2 += FuncoesGlobais.StrZero(irgimv, 6);                                      // Registro do imóvel
                tipo2 += FuncoesGlobais.myLetra(new Pad(iend, 60).RPad());                       // Endereço do imóvel
                tipo2 += FuncoesGlobais.myLetra(new Pad(inum, 25).RPad());                       // Número do imóvel
                tipo2 += FuncoesGlobais.myLetra(new Pad(icompl, 25).RPad());                     // Complemento do imóvel
                tipo2 += FuncoesGlobais.myLetra(new Pad(ibairro, 25).RPad());                    // Bairro do imóvel
                tipo2 += FuncoesGlobais.myLetra(new Pad(icidade, 25).RPad());                    // Cidade do imóvel
                tipo2 += FuncoesGlobais.myLetra(new Pad(iestado, 2).RPad());                     // Estado do imóvel
                tipo2 += FuncoesGlobais.StrZero(FuncoesGlobais.rmvNumero(icep.trim()), 8);       // Cep do imóvel
                tipo2 += FuncoesGlobais.StrZero(lcontrato, 6);                                   // Contrato do locatário
                tipo2 += FuncoesGlobais.myLetra(new Pad(lnomerazao, 60).RPad());                 // Nome do locatário
                tipo2 += FuncoesGlobais.StrZero(FuncoesGlobais.rmvNumero(lcpfcnpj.trim()), 18);  // CPF ou CNPJ do locatário
                tipo2 += FuncoesGlobais.Space(45);                                               // RESERVADO (Espaços em branco)
                    
                seq++;

                if (!new File("acao").exists()) {
                    boolean sucess = new File("acao").mkdirs();
                    if (!sucess) { System.out.println("Não consegui criar acao"); System.exit(1); }
                }
                
                StreamFile filler = new StreamFile(new String[] {"acao/" + prgprp + "_" + irgimv + "_" + lcontrato + "_" + FuncoesGlobais.myLetra(pnome.trim()) + "_X_" + FuncoesGlobais.myLetra(lnomerazao.trim()) + ".cal"});
                if (filler.Open()) {
                    
                    filler.Print(header + LF);
                    filler.Print(tipo2 + LF);

                    // Arquivo Tipo 3 - Informações Iniciais
                    dbRecibos = conn.OpenTable("SELECT * FROM recibo r WHERE r.rgprp = :rgprp AND r.rgimv = :rgimv AND r.contrato = :contrato AND r.tag <> 'X' ORDER BY r.dtvencimento;", new Object[][] {
                        {"string", "rgprp", prgprp},
                        {"string", "rgimv", irgimv},
                        {"string", "contrato", lcontrato}
                    });
                    
                    try {
                        while (dbRecibos.next()) {
                            Date rvencimento = null; String rcampo = null; 
                            String rmulta = "0", rjuros = "1", rcorrecao = "1";
                            rvencimento = dbRecibos.getDate("dtvencimento");
                            rcampo = dbRecibos.getString("campo");
                            String[][] rcampos = FuncoesGlobais.treeArray(rcampo, true);

                            for (int i=0;i<rcampo.length();i++) {
                                output = "3";                                                                             // Tipo 3
                                output += FuncoesGlobais.StrZero(String.valueOf(seq).replace(".0", ""), 10);              // sequencial
                                output += Dates.DateFormata("yyyyMMdd", rvencimento);                                     // vencimento
                                output += FuncoesGlobais.myLetra(new Pad(rcampos[i][rcampos[i].length - 1], 20).RPad());  // descrição
                                output += FuncoesGlobais.GravaValores(LerValor.FormatNumber(rcampos[i][2], 2), 2);        // valor $
                                output += rcampos[i][4].equalsIgnoreCase("AL") ? "1" : "0";                               // multa (0 - não, 1 - sim)
                                output += "1";                                                                            // juros (0 - não, 1 - sim)
                                output += "1";                                                                            // correção (0 - não, 1 - sim)
                                output += FuncoesGlobais.Space(348);                                                      // RESERVADO (Espaços em branco)

                                filler.Print(output + LF);

                                seq++;
                            }
                        }
                    } catch (Exception e) {}
                    
                    // Arquivo Tipo 4 - Informações Finais
                    
                    // Arquivo Tipo T - Trayler
                    output = "T";                                                                 // Tipo T - Trayler
                    output += FuncoesGlobais.StrZero(String.valueOf(seq).replace(".0", ""), 10);  // sequencial
                    output += "0000000001";                                                       // Total Arquivos Tipo T
                    output += "0000000001";                                                       // Total Arquivos Tipo 2
                    output += FuncoesGlobais.StrZero(String.valueOf("0").replace(".0", ""), 10);  // Total Arquivos Tipo 3
                    output += "0000000001";                                                       // Total Arquivos Tipo 4
                    output += FuncoesGlobais.StrZero(String.valueOf(--seq).replace(".0", ""), 10);  // Total de Linhas
                    output += FuncoesGlobais.Space(339);                                          // RESERVADO (Espaços em branco)
                    
                    filler.Print(output + LF);
                }
                filler.Close();        
            }
        }
    }
        
    private void mtDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mtDelActionPerformed
        if(iMatriculas.getSelectedIndex() > -1) iMatriculas.removeItemAt(iMatriculas.getSelectedIndex());
    }//GEN-LAST:event_mtDelActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {                                  
        // Legendas das taxas
        iMatriculas.setToolTipText(LerLegendas());
    }
    
    private void iBtIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iBtIncluirActionPerformed
        LimpaDados();
        iSituacao.getModel().setSelectedItem("Vazio");
        bNew = true;
        iBtIncluir.setEnabled(false && _Incluir);
        iBtCarteira.setEnabled(false && _Carteira);
        iBtGravar.setEnabled(true && _Alterar);
        iBtRetornar.setEnabled(true);
        iBtExcluir.setEnabled(false && _Inativar);
        jbtBaixar.setEnabled(false && _Baixar);

        iTipoImv.requestFocus();
    }//GEN-LAST:event_iBtIncluirActionPerformed

    private void jbtBaixarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBaixarActionPerformed
        if ("VAZIO".equals(iSituacao.getSelectedItem().toString().toUpperCase().trim())) return;

        VariaveisGlobais.situacao = "";
        jBaixaImovel oBaixa = null;
        oBaixa = new jBaixaImovel(null, true);
        oBaixa.setVisible(true);
        if (VariaveisGlobais.situacao.equalsIgnoreCase("VAZIO")) {
            iSituacao.getModel().setSelectedItem("VAZIO");
            String query = "UPDATE `imoveis` SET `situacao` = :situacao WHERE `rgprp` = :rgprp AND `rgimv` = :rgimv;";
            conn.CommandExecute(query, new Object[][] {
                {"int", "rgprp", Integer.parseInt(VariaveisGlobais.rgprp)},
                {"int", "rgimv", Integer.parseInt(VariaveisGlobais.rgimv)},
                {"int", "situacao", iSituacao.getModel().getSelectedItem().toString().toString()}
            });

            conn.CommandExecute("UPDATE locatarios set fiador1uf = 'X' WHERE rgimv = :rgimv';", new Object[][] {{"int", "rgimv", Integer.parseInt(iRgimv.getText().trim())}});
            
            // Grava informacoes de carteira
            String[] campos_carteira = {"rgprp","rgimv","contrato","campo","dtinicio","dttermino","dtultrecebimento","dtvencimento","vrseguro","pcseguro","dtseguro"};
            Object[][] dados_carteira = null;
            try {dados_carteira = conn.ReadFieldsTable(campos_carteira, "carteira", "rgprp = :rgprp AND rgimv = :rgimv", new Object[][] {
                {"int", "rgprp", Integer.parseInt(VariaveisGlobais.rgprp)},
                {"int", "rgimv", Integer.parseInt(VariaveisGlobais.rgimv)}
            });} catch (Exception e) {}
            if (dados_carteira != null) {
                String auxCampos = dados_carteira[3][3] + "," + dados_carteira[4][3] + "," + dados_carteira[5][3] + "," + dados_carteira[6][3] + "," + dados_carteira[7][3] + "," + dados_carteira[8][3] + "," + dados_carteira[9][3] + "," + dados_carteira[10][3];
                String auxSql = "INSERT INTO auxiliar (`conta`, `rgprp`, `rgimv`, `contrato`, `campo`) VALUES (:conta, :rgprp, :rgimv, :contrato, :campo)";
                conn.CommandExecute(auxSql, new Object[][] {
                    {"string", "conta", "CAR"},
                    {"string", "rgprp", dados_carteira[0][3].toString()},
                    {"string", "rgimv", dados_carteira[1][3].toString()},
                    {"string", "contrato", dados_carteira[2][3].toString()},
                    {"string", "campo", auxCampos}                    
                });
            }
            conn.CommandExecute("DELETE FROM carteira WHERE rgimv = :rgimv;", new Object[][] {{"string", "rgimv", dados_carteira[1][3].toString()}});
        }
    }//GEN-LAST:event_jbtBaixarActionPerformed

    private void jbtBuscaCepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtBuscaCepActionPerformed
        BuscaCep oCep = new BuscaCep(null, true);
        oCep.setVisible(true);

        Object[] dados;
        dados = oCep.dados;
        oCep = null;

        if (dados != null) {
            iEndereco.setText(dados[0].toString() + " " + dados[1].toString());
            iBairro.setText(dados[2].toString());
            iCidade.setText(dados[3].toString());
            iEstado.setText(dados[4].toString());
            iCep.setText(dados[5].toString());

            iNumero.requestFocus();
        }
    }//GEN-LAST:event_jbtBuscaCepActionPerformed

    private void iBtExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iBtExcluirActionPerformed
        JOptionPane.showMessageDialog(this, "Esta rotina esta temporariamente dsabilitada!");
    }//GEN-LAST:event_iBtExcluirActionPerformed

    private void iBtCarteiraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iBtCarteiraActionPerformed
        JOptionPane.showMessageDialog(this, "Esta rotina esta temporariamente dsabilitada!");
    }//GEN-LAST:event_iBtCarteiraActionPerformed

    private void iBtFotosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iBtFotosActionPerformed
        JOptionPane.showMessageDialog(this, "Esta rotina esta temporariamente dsabilitada!");
    }//GEN-LAST:event_iBtFotosActionPerformed

    private void iCepFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_iCepFocusLost
        if (iEndereco.getText().trim().equals("") && iCep.getText().trim() != "") {
            CEPEndereco cepEnder = ClienteViaCepWS.buscarCep(iCep.getText());
            if (cepEnder != null) {
                iEndereco.setText(cepEnder.getLogradouro());
                iNumero.setText("");
                iCplto.setText("");
                iBairro.setText(cepEnder.getBairro());
                iCidade.setText(cepEnder.getLocalidade());
                iEstado.setText(cepEnder.getUf());

                iNumero.requestFocus();
            }
        }  
    }//GEN-LAST:event_iCepFocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Imovel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Imovel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Imovel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Imovel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Imovel dialog = new Imovel(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField iBairro;
    private javax.swing.JButton iBtCarteira;
    private javax.swing.JButton iBtExcluir;
    private javax.swing.JButton iBtFotos;
    private javax.swing.JButton iBtGravar;
    private javax.swing.JButton iBtIncluir;
    private javax.swing.JButton iBtRetornar;
    private javax.swing.JTextField iCep;
    private javax.swing.JTextField iCidade;
    private javax.swing.JComboBox<String> iCodCidade;
    private javax.swing.JTextField iCplto;
    private javax.swing.JTextArea iDescricao;
    private javax.swing.JTextField iEndereco;
    private javax.swing.JTextField iEstado;
    private javax.swing.JComboBox<String> iMatriculas;
    private javax.swing.JTextField iMsg;
    private javax.swing.JTextField iNumero;
    private javax.swing.JTextField iObs;
    private javax.swing.JCheckBox iReter;
    private javax.swing.JTextField iRgimv;
    private javax.swing.JComboBox<String> iSituacao;
    private javax.swing.JComboBox<String> iTipoImv;
    private javax.swing.JComboBox<String> iUrbRural;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtBaixar;
    private javax.swing.JButton jbtBuscaCep;
    private javax.swing.JButton mtDel;
    // End of variables declaration//GEN-END:variables
}
