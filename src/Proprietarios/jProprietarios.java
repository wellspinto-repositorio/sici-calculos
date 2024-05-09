package Proprietarios;

import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.VariaveisGlobais;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.ImageIcon;

public class jProprietarios extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    private boolean _new = false;
    private String _rgprp = null;

    public void MoveFicha(String _rgprp) {
        this._rgprp = _rgprp;
    }
        
    public jProprietarios() {
        initComponents();

        // Icone da tela
        ImageIcon icone = new FlatSVGIcon("menuIcons/proprietario.svg",16,16);
        setFrameIcon(icone);
        
        LimpaFicha();
        if (LerFicha(_rgprp)) {
            btnIncluir.setEnabled(true);
            btnAlterar.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnTras.setEnabled(true);
            btnFrente.setEnabled(true);
            btnIrPara.setEnabled(true);
            btnConjugue.setEnabled(true);
            btnMensagens.setEnabled(true);
            btnGravar.setEnabled(false);
            btnRetornar.setEnabled(true);
        } else {
            btnIncluir.setEnabled(true);
            btnAlterar.setEnabled(false);
            btnExcluir.setEnabled(false);
            btnTras.setEnabled(false);
            btnFrente.setEnabled(false);
            btnIrPara.setEnabled(false);
            btnConjugue.setEnabled(false);
            btnMensagens.setEnabled(false);
            btnGravar.setEnabled(false);
            btnRetornar.setEnabled(true);
        }
    }

    private void EnableFicha( boolean value ) {
        pCodigo.setEnabled(value);
        pIdentificador.setEnabled(value);
        pNomeRazao.setEnabled(value);
        pEndereco.setEnabled(value);
        pbtnSearch.setEnabled(value);
        pNumero.setEnabled(value);
        pComplto.setEnabled(value);
        pBairro.setEnabled(value);
        pCidade.setEnabled(value);
        pEstado.setEnabled(value);
        pCep.setEnabled(value);
        pTelefones.setEnabled(value);
        pProfissao.setEnabled(value);
        pNacionalidade.setEnabled(value);
        pNacionaNome.setEnabled(value);
        pEstCivil.setEnabled(value);
        pDtNasc.setEnabled(value);
        pSexo.setEnabled(value);
        pDocTipo.setEnabled(value);
        pDocumentoNumero.setEnabled(value);
        pCpfCnpj.setEnabled(value);
        pRepresentante.setEnabled(value);
        pTelRepresentante.setEnabled(value);
        pEmails.setEnabled(value);
        pBanco.setEnabled(value);
        pAgencia.setEnabled(value);
        pConta.setEnabled(value);
    }
    
    private void LimpaFicha() {
        pCodigo.setText("");
        pIdentificador.setText("");
        pNomeRazao.setText("");
        pEndereco.setText("");
        pNumero.setText("");
        pComplto.setText("");
        pBairro.setText("");
        pCidade.setText("");
        pEstado.setSelectedIndex(-1);
        pCep.setText("");
        pTelefones.removeAllItems(); pTelefones.setSelectedItem(-1);
        pProfissao.setText("");
        pNacionalidade.setSelectedIndex(-1);
        pNacionaNome.setText("");
        pEstCivil.setSelectedItem(-1);
        pDtNasc.setText("");
        pSexo.setSelectedItem(-1);
        pDocTipo.setSelectedItem(-1);
        pDocumentoNumero.setText("");
        pCpfCnpj.setText("");
        pRepresentante.setText("");
        pTelRepresentante.removeAllItems(); pTelRepresentante.setSelectedIndex(-1);
        pEmails.removeAllItems(); pEmails.setSelectedIndex(-1);
        pBanco.setSelectedIndex(-1);
        pAgencia.setText("");
        pConta.setText("");        
    }
    
    private boolean LerFicha(String rgprp) {
        boolean retorno = false;
        String where = "WHERE rgprp = :rgprp ";
        if (rgprp == null) where = "";
        String selectSQL = "SELECT * FROM proprietários " + where + "LIMIT 1;";
        ResultSet rs = conn.OpenTable(selectSQL, new Object[][] {{"string", "rgprp", rgprp}});
        try {
            while (rs.next()) {
                retorno = true;
                String _pcodigo = null; try { _pcodigo = rs.getString("rgprp"); } catch (SQLException ex) {}
                pCodigo.setText(_pcodigo);
                String _pidentificador = null; try { _pidentificador = rs.getString("identificador"); } catch (SQLException ex) {}
                pIdentificador.setText(_pidentificador);
                String _pnomerazao = null; try { _pnomerazao = rs.getString("nomerazao"); } catch (SQLException ex) {}
                pNomeRazao.setText(_pnomerazao);
                String _pendereco = null; try { _pendereco = rs.getString("endereco"); } catch (SQLException ex) {}
                pEndereco.setText(_pendereco);
                String _pnumero = null; try { _pnumero = rs.getString("num"); } catch (SQLException ex) {}
                pNumero.setText(_pnumero);
                String _pcompl = null; try { _pcompl = rs.getString("compl"); } catch (SQLException ex) {}
                pComplto.setText(_pcompl);
                String _pbairro = null; try { _pbairro = rs.getString("bairro"); } catch (SQLException ex) {}
                pBairro.setText(_pbairro);
                String _pcidade = null; try { _pcidade = rs.getString("cidade"); } catch (SQLException ex) {}
                pCidade.setText(_pcidade);
                String _pestado = null; try { _pestado = rs.getString("estado"); } catch (SQLException ex) {}
                pEstado.getEditor().setItem(_pestado);
                String _pcep = null; try { _pcep = rs.getString("cep"); } catch (SQLException ex) {}
                pCep.setText(_pcep);
                String _ptelefones = null; try { _ptelefones = rs.getString("telefone"); } catch (SQLException ex) {}
                // Aqui rotina de telefones
                String _pprofissao = null; try { _pprofissao = rs.getString("profissao"); } catch (SQLException ex) {}
                pProfissao.setText(_pprofissao);
                String _pnacionalidade = null; try { _pnacionalidade = rs.getString("nacionalidade"); } catch (SQLException ex) {}
                pNacionalidade.getEditor().setItem(_pnacionalidade);
                String _pnacionanome = null; try { _pnacionanome = rs.getString("nacionanome"); } catch (SQLException ex) {}
                pNacionaNome.setText(_pnacionanome);
                String _pestcivil = null; try { _pestcivil = rs.getString("estcivil"); } catch (SQLException ex) {}
                pEstCivil.getEditor().setItem(_pestcivil);
                Date _pdtnasc = null; try { _pdtnasc = rs.getDate("dtnasc"); } catch (SQLException ex) {}
                pDtNasc.setText(Dates.DateFormata("dd-MM-yyyy", _pdtnasc));
                String _psexo = null; try { _psexo = rs.getString("sexo"); } catch (SQLException ex) {}
                pSexo.getEditor().setItem(_psexo);
                String _pdoctipo = null; try { _pdoctipo = rs.getString("doctipo"); } catch (SQLException ex) {}
                pDocTipo.getEditor().setItem(_pdoctipo);
                String _pdocumentonumero = null; try { _pdocumentonumero = rs.getString("rginsc"); } catch (SQLException ex) {}
                pDocumentoNumero.setText(_pdocumentonumero);
                String _pcpfcnpj = null; try { _pcpfcnpj = rs.getString("cpfcnpj"); } catch (SQLException ex) {}
                pCpfCnpj.setText(_pcpfcnpj);
                String _prepresentante = null; try { _prepresentante = rs.getString("representante"); } catch (SQLException ex) {}
                pRepresentante.setText(_prepresentante);
                String _ptelrepresentante = null; try { _ptelrepresentante = rs.getString("telrepres"); } catch (SQLException ex) {}
                // rotina de telefones
                String _pemails = null; try { _pemails = rs.getString("email"); } catch (SQLException ex) {}
                // rotina de emails
                String _pbanco = null; try { _pbanco = rs.getString("banco"); } catch (SQLException ex) {}
                pBanco.getEditor().setItem(_pbanco);
                String _pagencia = null; try { _pagencia = rs.getString("agencia"); } catch (SQLException ex) {}
                pAgencia.setText(_pagencia);
                String _pconta = null; try { _pconta = rs.getString("conta"); } catch (SQLException ex) {}
                pConta.setText(_pconta);                
            }
        } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
        conn.CloseTable(rs);
        
        return retorno;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pCodigo = new javax.swing.JTextField();
        pIdentificador = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pNomeRazao = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pEndereco = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        pNumero = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pComplto = new javax.swing.JTextField();
        pbtnSearch = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        pBairro = new javax.swing.JTextField();
        pCidade = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        pEstado = new javax.swing.JComboBox<>();
        pCep = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        pTelefones = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        pProfissao = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        pRepresentante = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        pNacionalidade = new javax.swing.JComboBox<>();
        pNacionaNome = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        pEstCivil = new javax.swing.JComboBox<>();
        pSexo = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        pDtNasc = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        pDocTipo = new javax.swing.JComboBox<>();
        pDocumentoNumero = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        pCpfCnpj = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        pTelRepresentante = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        pEmails = new javax.swing.JComboBox<>();
        pBanco = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        pAgencia = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        pConta = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnIncluir = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnTras = new javax.swing.JButton();
        btnFrente = new javax.swing.JButton();
        btnIrPara = new javax.swing.JButton();
        btnConjugue = new javax.swing.JButton();
        btnMensagens = new javax.swing.JButton();
        btnGravar = new javax.swing.JButton();
        btnRetornar = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pImoveis = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Cadastro de Proprietários");
        setMaximumSize(new java.awt.Dimension(755, 545));
        setMinimumSize(new java.awt.Dimension(755, 545));

        jLabel1.setText("Código:");

        jLabel2.setText("Identificador:");

        jLabel3.setText("Nome/Razão Social:");

        jLabel4.setText("Endereço:");

        jLabel5.setText("Número:");

        jLabel6.setText("Complemento:");

        pbtnSearch.setIcon(new FlatSVGIcon("Proprietarios/icones/propsearch.svg",16,16));
        pbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbtnSearchActionPerformed(evt);
            }
        });

        jLabel7.setText("Bairro:");

        jLabel8.setText("Cidade:");

        jLabel9.setText("Estado:");

        pCep.setText("99999-999");

        jLabel10.setText("Cep:");

        jLabel11.setText("Telefones:");

        pTelefones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "21 97665-9897 Celular" }));

        jLabel12.setText("Profissão:");

        jLabel13.setText("R:epresentante");

        jLabel14.setText("Nacionalidade:");

        pNacionalidade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Brasileira", "Extrangeira" }));

        jLabel15.setText("Estado Cívil:");

        pEstCivil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Solteiro", "Casado", "União Estável", "Separado", "Divorciado", "Viúvo" }));

        pSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Feminino", "Não-binário", "Gênero Fluido", "Agênero", "Bigênero", "Gênero Neutro", "Pangênero", "Genderqueer", "Transgênero", "Dois espiritos", "Androgino", "Neutrois", "Poligênero", "Questionando/Indeciso" }));

        jLabel16.setText("Sexo:");

        jLabel17.setText("Dt.Nascimento:");

        pDtNasc.setColumns(10);
        pDtNasc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        pDtNasc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pDtNasc.setText("01/01/2023");

        jLabel18.setText("Doc.Identificação:");

        pDocTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(IM)Inscrição Municipal", "(IE)Inscriçaõ Estadual", "(RG)Registro Geral", "(CNH)Carteira de Habilitação", "(CTPS)Carteira de Trabalho" }));

        jLabel19.setText("Número:");

        jLabel20.setText("CPF/CNPJ:");

        pTelRepresentante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "21 97665-9897 Celular" }));

        jLabel21.setText("Telefones:");

        jLabel22.setText("E-Mails:");

        pBanco.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "001", "237", "104", "033", "341", "422", "208", "745", "077", "212", " " }));

        jLabel23.setText("Banco:");

        jLabel24.setText("Agência:");

        jLabel25.setText("Conta:");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnIncluir.setText("Incluir");

        btnAlterar.setText("Alterar");

        btnExcluir.setText("Excluir");

        btnTras.setText("<");

        btnFrente.setText(">");
        btnFrente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFrenteActionPerformed(evt);
            }
        });

        btnIrPara.setText("Ir Para");
        btnIrPara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIrParaActionPerformed(evt);
            }
        });

        btnConjugue.setText("Conjugue");
        btnConjugue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConjugueActionPerformed(evt);
            }
        });

        btnMensagens.setText("Mensagens");
        btnMensagens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMensagensActionPerformed(evt);
            }
        });

        btnGravar.setText("Gravar");
        btnGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        btnRetornar.setText("Retornar");
        btnRetornar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetornarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnIncluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAlterar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnTras, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFrente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnIrPara, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConjugue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMensagens, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGravar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRetornar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIncluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAlterar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTras)
                    .addComponent(btnFrente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIrPara)
                .addGap(29, 29, 29)
                .addComponent(btnConjugue)
                .addGap(2, 2, 2)
                .addComponent(btnMensagens)
                .addGap(60, 60, 60)
                .addComponent(btnGravar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRetornar)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jLabel26.setText("Imóveis do Proprietário");
        jLabel26.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pImoveis.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(pImoveis);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15)
                                            .addComponent(pEstCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(pRepresentante, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel13))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(jLabel21))
                                                    .addComponent(pTelRepresentante, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(pEmails, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel22))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(pBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel23))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel24)
                                                    .addComponent(pAgencia, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(pConta, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel25)))
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel7)
                                                        .addComponent(pBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel8)
                                                        .addComponent(pCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(pEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel9))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel10)
                                                        .addComponent(pCep, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(pEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGap(0, 0, 0)
                                                            .addComponent(pbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jLabel4))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel5)
                                                        .addComponent(pNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(pComplto, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel6)))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel11)
                                                        .addComponent(pTelefones, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel12)
                                                        .addComponent(pProfissao, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel14)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(pNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(pNacionaNome, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(pDtNasc, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel17))
                                                    .addGap(145, 145, 145)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel16)
                                                        .addComponent(pSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(pDocTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(pDocumentoNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGap(73, 73, 73)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(pCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(pCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel1))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel2)
                                                    .addComponent(pIdentificador, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel3)
                                                    .addComponent(pNomeRazao, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addGap(0, 0, 0)
                                                .addComponent(pCep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel3)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(pNomeRazao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel1)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(pCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel2)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(pIdentificador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel4)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(pEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(pbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel5)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(pNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel6)
                                                        .addGap(24, 24, 24))
                                                    .addComponent(pComplto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel7)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(pBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel8)
                                                        .addGap(0, 0, 0)
                                                        .addComponent(pCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel9)
                                                        .addGap(24, 24, 24))
                                                    .addComponent(pEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel11)
                                                .addGap(0, 0, 0)
                                                .addComponent(pTelefones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel12)
                                                .addGap(0, 0, 0)
                                                .addComponent(pProfissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel14)
                                                .addGap(0, 0, 0)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(pNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(pNacionaNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel15)
                                                .addGap(0, 0, 0)
                                                .addComponent(pEstCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel17)
                                                    .addGap(22, 22, 22))
                                                .addComponent(pDtNasc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(pSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addGap(24, 24, 24)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addGap(24, 24, 24))
                                    .addComponent(pDocTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel19)
                                            .addGap(24, 24, 24))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(pDocumentoNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(pCpfCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(24, 24, 24)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(0, 0, 0)
                                .addComponent(pRepresentante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(0, 0, 0)
                                .addComponent(pTelRepresentante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(0, 0, 0)
                                .addComponent(pEmails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel23)
                                    .addGap(24, 24, 24))
                                .addComponent(pBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(0, 0, 0)
                                .addComponent(pAgencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addGap(0, 0, 0)
                                .addComponent(pConta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel26)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbtnSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pbtnSearchActionPerformed

    private void btnFrenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFrenteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFrenteActionPerformed

    private void btnIrParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIrParaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnIrParaActionPerformed

    private void btnConjugueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConjugueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnConjugueActionPerformed

    private void btnMensagensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMensagensActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMensagensActionPerformed

    private void btnGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGravarActionPerformed

    private void btnRetornarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetornarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRetornarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnConjugue;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFrente;
    private javax.swing.JButton btnGravar;
    private javax.swing.JButton btnIncluir;
    private javax.swing.JButton btnIrPara;
    private javax.swing.JButton btnMensagens;
    private javax.swing.JButton btnRetornar;
    private javax.swing.JButton btnTras;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField pAgencia;
    private javax.swing.JTextField pBairro;
    private javax.swing.JComboBox<String> pBanco;
    private javax.swing.JTextField pCep;
    private javax.swing.JTextField pCidade;
    private javax.swing.JTextField pCodigo;
    private javax.swing.JTextField pComplto;
    private javax.swing.JTextField pConta;
    private javax.swing.JTextField pCpfCnpj;
    private javax.swing.JComboBox<String> pDocTipo;
    private javax.swing.JTextField pDocumentoNumero;
    private javax.swing.JFormattedTextField pDtNasc;
    private javax.swing.JComboBox<String> pEmails;
    private javax.swing.JTextField pEndereco;
    private javax.swing.JComboBox<String> pEstCivil;
    private javax.swing.JComboBox<String> pEstado;
    private javax.swing.JTextField pIdentificador;
    private javax.swing.JTable pImoveis;
    private javax.swing.JTextField pNacionaNome;
    private javax.swing.JComboBox<String> pNacionalidade;
    private javax.swing.JTextField pNomeRazao;
    private javax.swing.JTextField pNumero;
    private javax.swing.JTextField pProfissao;
    private javax.swing.JTextField pRepresentante;
    private javax.swing.JComboBox<String> pSexo;
    private javax.swing.JComboBox<String> pTelRepresentante;
    private javax.swing.JComboBox<String> pTelefones;
    private javax.swing.JButton pbtnSearch;
    // End of variables declaration//GEN-END:variables
}
