package Movimento.AdmParametros;

import Funcoes.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;

/**
 *
 * @author supervisor
 */
public class jADMParametros extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    String tag = "UPDATE";
    
    jADMContas aDMContas = new jADMContas();
    jADMDados aDMDados = new jADMDados();
    jADMRecibos aDMRecibos = new jADMRecibos();
    jADMDemais aDMDemais = new jADMDemais();
    jADMEmail aDMEmail = new jADMEmail();
    jADMBancos aDMBancos = new jADMBancos();
    jADMMensagens aDMMensagens = new jADMMensagens();
    jADMCobrancas aDMCobrancas = new jADMCobrancas();
    
    /**
     * Creates new form jADMParametros
     */
    public jADMParametros() {
        initComponents();

        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/contasAdm.svg",16,16);
        setFrameIcon(icone);        
        
        AtualizaNomes();
        LerParamCalculos();
                        
        // Adm Contas
        aDMContas.setVisible(true);
        aDMContas.setEnabled(true);
        aDMContas.setBounds(0, 0, 772,314); 
        aDMContas.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelContas.add(aDMContas); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelContas.repaint();
        jPanelContas.setEnabled(true);
        
        // Adm Dados
        aDMDados.setVisible(true);
        aDMDados.setEnabled(true);
        aDMDados.setBounds(0, 0, 772,314); 
        aDMDados.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelDados.add(aDMDados); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelDados.repaint();
        jPanelDados.setEnabled(true);
        
        // Recibos fake
        aDMRecibos.setVisible(true);
        aDMRecibos.setEnabled(true);
        aDMRecibos.setBounds(0, 0, 772,314); 
        aDMRecibos.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelRecibos.add(aDMRecibos); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelRecibos.repaint();
        jPanelRecibos.setEnabled(true);
        
        // Demais parametros
        aDMDemais.setVisible(true);
        aDMDemais.setEnabled(true);
        aDMDemais.setBounds(0, 0, 772,314); 
        aDMDemais.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelDemais.add(aDMDemais); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelDemais.repaint();
        jPanelDemais.setEnabled(true);
        
        // Email GMail
        aDMEmail.setVisible(true);
        aDMEmail.setEnabled(true);
        aDMEmail.setBounds(0, 0, 772,314); 
        aDMEmail.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelGmail.add(aDMEmail); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelGmail.repaint();
        jPanelGmail.setEnabled(true);
        
        // Bancos
        aDMBancos.setVisible(true);
        aDMBancos.setEnabled(true);
        aDMBancos.setBounds(0, 0, 772,314); 
        aDMBancos.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelBancos.add(aDMBancos); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelBancos.repaint();
        jPanelBancos.setEnabled(true);
        
        // Mensagens
        aDMMensagens.setVisible(true);
        aDMMensagens.setEnabled(true);
        aDMMensagens.setBounds(-10, 0, 772,614); 
        aDMBancos.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelMensagens.add(aDMMensagens); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelMensagens.repaint();
        jPanelMensagens.setEnabled(true);
        
        // Cobranças
        aDMCobrancas.setVisible(true);
        aDMCobrancas.setEnabled(true);
        aDMCobrancas.setBounds(-10, 0, 772,614); 
        aDMBancos.setBorder(BorderFactory.createEmptyBorder());
        try { jPanelCobranca.add(aDMCobrancas); } catch (IllegalArgumentException ex) { ex.printStackTrace(); }
        jPanelCobranca.repaint();
        jPanelCobranca.setEnabled(true);
        
    }

    private void AtualizaNomes() {
        jAbasCalculos.setTitleAt(0, VariaveisGlobais.dCliente.get("MU"));
        jAbasCalculos.setTitleAt(1, VariaveisGlobais.dCliente.get("JU"));
        jAbasCalculos.setTitleAt(2, VariaveisGlobais.dCliente.get("CO"));
        jAbasCalculos.setTitleAt(3, "Taxas");
    }
           
    private void LerParamCalculos() {
        try {
            jTALUG.setSelected(Boolean.valueOf(conn.ReadParameters("talug")));
            jTBRLIQ.setSelected(Boolean.valueOf(conn.ReadParameters("tbrliq")));
            jTCORRECAO.setSelected(Boolean.valueOf(conn.ReadParameters("tcorrecao")));
            jTJUROS.setSelected(Boolean.valueOf(conn.ReadParameters("tjuros")));
            jTMULTA.setSelected(Boolean.valueOf(conn.ReadParameters("tmulta")));
            jTSEGURO.setSelected(Boolean.valueOf(conn.ReadParameters("tseguro")));
            jTTAXA.setSelected(Boolean.valueOf(conn.ReadParameters("ttaxa")));

            jMALUG.setSelected(Boolean.valueOf(conn.ReadParameters("malug")));
            jMCORRECAO.setSelected(Boolean.valueOf(conn.ReadParameters("mcorrecao")));
            jMEXPE.setSelected(Boolean.valueOf(conn.ReadParameters("mexpe")));
            jMJUROS.setSelected(Boolean.valueOf(conn.ReadParameters("mjuros")));
            jMTAXA.setSelected(Boolean.valueOf(conn.ReadParameters("mtaxa")));

            jJALUG.setSelected(Boolean.valueOf(conn.ReadParameters("jalug")));
            jJCORRECAO.setSelected(Boolean.valueOf(conn.ReadParameters("jcorrecao")));
            jJEXPE.setSelected(Boolean.valueOf(conn.ReadParameters("jexpe")));
            jJMULTA.setSelected(Boolean.valueOf(conn.ReadParameters("jmulta")));
            jJSEGURO.setSelected(Boolean.valueOf(conn.ReadParameters("jseguro")));
            jJTAXA.setSelected(Boolean.valueOf(conn.ReadParameters("jtaxa")));

            jCALUG.setSelected(Boolean.valueOf(conn.ReadParameters("calug")));
            jCEXPE.setSelected(Boolean.valueOf(conn.ReadParameters("cexpe")));
            jCJUROS.setSelected(Boolean.valueOf(conn.ReadParameters("cjuros")));
            jCMULTA.setSelected(Boolean.valueOf(conn.ReadParameters("cmulta")));
            jCSEGURO.setSelected(Boolean.valueOf(conn.ReadParameters("cseguro")));
            jCTAXA.setSelected(Boolean.valueOf(conn.ReadParameters("ctaxa")));
        } catch (Exception ex) {}
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel14 = new javax.swing.JPanel();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jAbasCalculos = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jMALUG = new javax.swing.JCheckBox();
        jMCORRECAO = new javax.swing.JCheckBox();
        jMEXPE = new javax.swing.JCheckBox();
        jMJUROS = new javax.swing.JCheckBox();
        jMTAXA = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jJALUG = new javax.swing.JCheckBox();
        jJCORRECAO = new javax.swing.JCheckBox();
        jJEXPE = new javax.swing.JCheckBox();
        jJMULTA = new javax.swing.JCheckBox();
        jJTAXA = new javax.swing.JCheckBox();
        jJSEGURO = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jCALUG = new javax.swing.JCheckBox();
        jCEXPE = new javax.swing.JCheckBox();
        jCJUROS = new javax.swing.JCheckBox();
        jCMULTA = new javax.swing.JCheckBox();
        jCSEGURO = new javax.swing.JCheckBox();
        jCTAXA = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jTALUG = new javax.swing.JCheckBox();
        jTBRLIQ = new javax.swing.JCheckBox();
        jTCORRECAO = new javax.swing.JCheckBox();
        jTJUROS = new javax.swing.JCheckBox();
        jTMULTA = new javax.swing.JCheckBox();
        jTSEGURO = new javax.swing.JCheckBox();
        jTTAXA = new javax.swing.JCheckBox();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanelContas = new javax.swing.JPanel();
        jPanelDados = new javax.swing.JPanel();
        jPanelDemais = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanelMensagens = new javax.swing.JPanel();
        jPanelBancos = new javax.swing.JPanel();
        jTabbedGmail = new javax.swing.JTabbedPane();
        jPanelGmail = new javax.swing.JPanel();
        jPanelRecibos = new javax.swing.JPanel();
        jPanelCobranca = new javax.swing.JPanel();
        jbtGravarParametros = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Dados e Parametros da ADM ::.");
        setVisible(true);

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jMALUG.setText("Calcula multa incluindo os campos AL na soma. (MALUG)");

        jMCORRECAO.setText("Calcula multa incluindo a CORREÇÃO na soma. (MCORRECAO)");

        jMEXPE.setText("Calcula multa incluindo a Taxa de EXPEDIENTE na soma. (MEXPE)");

        jMJUROS.setText("Calcula multa incluindo o JUROS na soma. (MJUROS)");

        jMTAXA.setText("Calcula multa incluindo os campos NT na soma. (MTAXA)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jMALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMCORRECAO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMEXPE, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jMJUROS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jMALUG)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMCORRECAO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMEXPE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMJUROS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMTAXA)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jAbasCalculos.addTab("MU", jPanel2);

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jJALUG.setText("Calcula juros incluindo os campos AL na soma. (JALUG)");

        jJCORRECAO.setText("Calcula juros incluindo a CORREÇÃO na soma. (JCORRECAO)");

        jJEXPE.setText("Calcula juros incluindo a Taxa de EXPEDIENTE na soma. (JEXPE)");

        jJMULTA.setText("Calcula juros incluindo a MULTA na soma. (JMULTA)");

        jJTAXA.setText("Calcula juros incluindo as TAXAS=NT na soma. (JTAXA)");

        jJSEGURO.setText("Calcula juros incluindo o SEGURO na soma. (JSEGURO)");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jJALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jJCORRECAO, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJEXPE, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJMULTA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jJSEGURO, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jJALUG)
                .addGap(2, 2, 2)
                .addComponent(jJCORRECAO)
                .addGap(2, 2, 2)
                .addComponent(jJEXPE)
                .addGap(2, 2, 2)
                .addComponent(jJMULTA)
                .addGap(2, 2, 2)
                .addComponent(jJSEGURO)
                .addGap(2, 2, 2)
                .addComponent(jJTAXA))
        );

        jAbasCalculos.addTab("JU", jPanel3);

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jCALUG.setText("Calcula correção sobre campos AL. (CALUG)");

        jCEXPE.setText("Calcula correção incluindo a taxa de expediente na soma. (CEXPE)");

        jCJUROS.setText("Calcula correção incluindo o JUROS na soma. (CJUROS)");

        jCMULTA.setText("Calcula correção incluindo a MULTA na soma. (CMULTA)");

        jCSEGURO.setText("Calcula correção incluindo o SEGURO na soma. (CSEGURO)");

        jCTAXA.setText("Calcula correção incluindo as TAXA=NT na soma. (CTAXA)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCEXPE, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCJUROS, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCMULTA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jCSEGURO, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCALUG)
                .addGap(2, 2, 2)
                .addComponent(jCEXPE)
                .addGap(2, 2, 2)
                .addComponent(jCJUROS)
                .addGap(2, 2, 2)
                .addComponent(jCMULTA)
                .addGap(2, 2, 2)
                .addComponent(jCSEGURO)
                .addGap(2, 2, 2)
                .addComponent(jCTAXA))
        );

        jAbasCalculos.addTab("CO", jPanel4);

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jTALUG.setText("Soma campos NT com campos AL na soma. (TALUG)");

        jTBRLIQ.setText("TRUE PARA calcular sobre o BRUTO e FALSE para calcular sobre o LIQUIDO. (TBRLIQ)");

        jTCORRECAO.setText("Soma os CAMPOS NT para calcular a CORREÇÃO. (TCORRECAO)");

        jTJUROS.setText("Soma os campos NT para calcular o JUROS. (TJUROS)");

        jTMULTA.setText("Soma os campos NT para calcular MULTAS. (TMULTA)");

        jTSEGURO.setText("oma os campos NT para calcular SEGURO. (TSEGURO)");

        jTTAXA.setText("Soma os campos NT para calcular a Taxa de EXPEDIENTE. (TTAXA)");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTALUG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTBRLIQ, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                    .addComponent(jTCORRECAO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTJUROS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTSEGURO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTMULTA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTTAXA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jTALUG)
                .addGap(1, 1, 1)
                .addComponent(jTBRLIQ)
                .addGap(1, 1, 1)
                .addComponent(jTCORRECAO)
                .addGap(1, 1, 1)
                .addComponent(jTJUROS)
                .addGap(1, 1, 1)
                .addComponent(jTMULTA)
                .addGap(1, 1, 1)
                .addComponent(jTSEGURO)
                .addGap(1, 1, 1)
                .addComponent(jTTAXA)
                .addGap(2, 2, 2))
        );

        jAbasCalculos.addTab("Taxas", jPanel5);

        jTabbedPane2.setMaximumSize(new java.awt.Dimension(772, 345));
        jTabbedPane2.setMinimumSize(new java.awt.Dimension(772, 345));
        jTabbedPane2.setPreferredSize(new java.awt.Dimension(772, 345));

        jPanelContas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        javax.swing.GroupLayout jPanelContasLayout = new javax.swing.GroupLayout(jPanelContas);
        jPanelContas.setLayout(jPanelContasLayout);
        jPanelContasLayout.setHorizontalGroup(
            jPanelContasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 768, Short.MAX_VALUE)
        );
        jPanelContasLayout.setVerticalGroup(
            jPanelContasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Contas ADM", jPanelContas);

        jPanelDados.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        javax.swing.GroupLayout jPanelDadosLayout = new javax.swing.GroupLayout(jPanelDados);
        jPanelDados.setLayout(jPanelDadosLayout);
        jPanelDadosLayout.setHorizontalGroup(
            jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 768, Short.MAX_VALUE)
        );
        jPanelDadosLayout.setVerticalGroup(
            jPanelDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Dados ADM", jPanelDados);

        jPanelDemais.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        javax.swing.GroupLayout jPanelDemaisLayout = new javax.swing.GroupLayout(jPanelDemais);
        jPanelDemais.setLayout(jPanelDemaisLayout);
        jPanelDemaisLayout.setHorizontalGroup(
            jPanelDemaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 768, Short.MAX_VALUE)
        );
        jPanelDemaisLayout.setVerticalGroup(
            jPanelDemaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Demais parametros", jPanelDemais);

        javax.swing.GroupLayout jPanelMensagensLayout = new javax.swing.GroupLayout(jPanelMensagens);
        jPanelMensagens.setLayout(jPanelMensagensLayout);
        jPanelMensagensLayout.setHorizontalGroup(
            jPanelMensagensLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 770, Short.MAX_VALUE)
        );
        jPanelMensagensLayout.setVerticalGroup(
            jPanelMensagensLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
        );

        jScrollPane4.setViewportView(jPanelMensagens);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Mensagens do Boleto", jPanel16);

        javax.swing.GroupLayout jPanelBancosLayout = new javax.swing.GroupLayout(jPanelBancos);
        jPanelBancos.setLayout(jPanelBancosLayout);
        jPanelBancosLayout.setHorizontalGroup(
            jPanelBancosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 772, Short.MAX_VALUE)
        );
        jPanelBancosLayout.setVerticalGroup(
            jPanelBancosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Banco", jPanelBancos);

        javax.swing.GroupLayout jPanelGmailLayout = new javax.swing.GroupLayout(jPanelGmail);
        jPanelGmail.setLayout(jPanelGmailLayout);
        jPanelGmailLayout.setHorizontalGroup(
            jPanelGmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 772, Short.MAX_VALUE)
        );
        jPanelGmailLayout.setVerticalGroup(
            jPanelGmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 253, Short.MAX_VALUE)
        );

        jTabbedGmail.addTab("GMail", jPanelGmail);

        jTabbedPane2.addTab("EMail", jTabbedGmail);

        jPanelRecibos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        javax.swing.GroupLayout jPanelRecibosLayout = new javax.swing.GroupLayout(jPanelRecibos);
        jPanelRecibos.setLayout(jPanelRecibosLayout);
        jPanelRecibosLayout.setHorizontalGroup(
            jPanelRecibosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 768, Short.MAX_VALUE)
        );
        jPanelRecibosLayout.setVerticalGroup(
            jPanelRecibosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Emissão Recibo", jPanelRecibos);

        javax.swing.GroupLayout jPanelCobrancaLayout = new javax.swing.GroupLayout(jPanelCobranca);
        jPanelCobranca.setLayout(jPanelCobrancaLayout);
        jPanelCobrancaLayout.setHorizontalGroup(
            jPanelCobrancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 772, Short.MAX_VALUE)
        );
        jPanelCobrancaLayout.setVerticalGroup(
            jPanelCobrancaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Cobrança Automática", jPanelCobranca);

        jbtGravarParametros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Figuras/ok.png"))); // NOI18N
        jbtGravarParametros.setText("Gravar Parametros");
        jbtGravarParametros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtGravarParametrosActionPerformed(evt);
            }
        });

        jSeparator1.setForeground(new java.awt.Color(1, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtGravarParametros, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jAbasCalculos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 774, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 772, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAbasCalculos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtGravarParametros)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtGravarParametrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtGravarParametrosActionPerformed
        try {
            String[][] param = {{"TALUG","LOGICO",(jTALUG.isSelected() ? "TRUE" : "FALSE")},
                {"TBRLIQ","LOGICO",(jTBRLIQ.isSelected() ? "TRUE" : "FALSE")},
                {"TCORRECAO","LOGICO",(jTCORRECAO.isSelected() ? "TRUE" : "FALSE")},
                {"TJUROS","LOGICO",(jTJUROS.isSelected() ? "TRUE" : "FALSE")},
                {"TMULTA","LOGICO",(jTMULTA.isSelected() ? "TRUE" : "FALSE")},
                {"TSEGURO","LOGICO",(jTSEGURO.isSelected() ? "TRUE" : "FALSE")},
                {"TTAXA","LOGICO",(jTTAXA.isSelected() ? "TRUE" : "FALSE")},
                {"MALUG","LOGICO",(jMALUG.isSelected() ? "TRUE" : "FALSE")},
                {"MCORRECAO","LOGICO",(jMCORRECAO.isSelected() ? "TRUE" : "FALSE")},
                {"MEXPE","LOGICO",(jMEXPE.isSelected() ? "TRUE" : "FALSE")},
                {"MJUROS","LOGICO",(jMJUROS.isSelected() ? "TRUE" : "FALSE")},
                {"MTAXA","LOGICO",(jMTAXA.isSelected() ? "TRUE" : "FALSE")},
                {"JALUG","LOGICO",(jJALUG.isSelected() ? "TRUE" : "FALSE")},
                {"JCORRECAO","LOGICO",(jJCORRECAO.isSelected() ? "TRUE" : "FALSE")},
                {"JEXPE","LOGICO",(jJEXPE.isSelected() ? "TRUE" : "FALSE")},
                {"JMULTA","LOGICO",(jJMULTA.isSelected() ? "TRUE" : "FALSE")},
                {"JSEGURO","LOGICO",(jJSEGURO.isSelected() ? "TRUE" : "FALSE")},
                {"JTAXA","LOGICO",(jJTAXA.isSelected() ? "TRUE" : "FALSE")},
                {"CALUG","LOGICO",(jCALUG.isSelected() ? "TRUE" : "FALSE")},
                {"CEXPE","LOGICO",(jCEXPE.isSelected() ? "TRUE" : "FALSE")},
                {"CJUROS","LOGICO",(jCJUROS.isSelected() ? "TRUE" : "FALSE")},
                {"CMULTA","LOGICO",(jCMULTA.isSelected() ? "TRUE" : "FALSE")},
                {"CSEGURO","LOGICO",(jCSEGURO.isSelected() ? "TRUE" : "FALSE")},
                {"CTAXA","LOGICO",(jCTAXA.isSelected() ? "TRUE" : "FALSE")}};

            conn.GravarMultiParametros(param);
        } catch (Exception ex) {ex.printStackTrace();}
    }//GEN-LAST:event_jbtGravarParametrosActionPerformed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JTabbedPane jAbasCalculos;
    private javax.swing.JCheckBox jCALUG;
    private javax.swing.JCheckBox jCEXPE;
    private javax.swing.JCheckBox jCJUROS;
    private javax.swing.JCheckBox jCMULTA;
    private javax.swing.JCheckBox jCSEGURO;
    private javax.swing.JCheckBox jCTAXA;
    private javax.swing.JCheckBox jJALUG;
    private javax.swing.JCheckBox jJCORRECAO;
    private javax.swing.JCheckBox jJEXPE;
    private javax.swing.JCheckBox jJMULTA;
    private javax.swing.JCheckBox jJSEGURO;
    private javax.swing.JCheckBox jJTAXA;
    private javax.swing.JCheckBox jMALUG;
    private javax.swing.JCheckBox jMCORRECAO;
    private javax.swing.JCheckBox jMEXPE;
    private javax.swing.JCheckBox jMJUROS;
    private javax.swing.JCheckBox jMTAXA;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelBancos;
    private javax.swing.JPanel jPanelCobranca;
    private javax.swing.JPanel jPanelContas;
    private javax.swing.JPanel jPanelDados;
    private javax.swing.JPanel jPanelDemais;
    private javax.swing.JPanel jPanelGmail;
    private javax.swing.JPanel jPanelMensagens;
    private javax.swing.JPanel jPanelRecibos;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox jTALUG;
    private javax.swing.JCheckBox jTBRLIQ;
    private javax.swing.JCheckBox jTCORRECAO;
    private javax.swing.JCheckBox jTJUROS;
    private javax.swing.JCheckBox jTMULTA;
    private javax.swing.JCheckBox jTSEGURO;
    private javax.swing.JCheckBox jTTAXA;
    private javax.swing.JTabbedPane jTabbedGmail;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JButton jbtGravarParametros;
    // End of variables declaration//GEN-END:variables
}
