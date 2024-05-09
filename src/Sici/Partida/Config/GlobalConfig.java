package Sici.Partida.Config;

import Funcoes.Config;
import Funcoes.FuncoesGlobais;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GlobalConfig extends javax.swing.JInternalFrame {

    public GlobalConfig() {
        initComponents();
        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/configglobal.svg",16,16);
        setFrameIcon(icone);
        
        
        RegraCalculos.addActionListener(e -> {
            String value = RegraCalculos.getSelectedItem().toString();
            if (!new Config().Saveing("RegraCalculos", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        int selectedIndex = (boolean)new Config().Reading("RegraCalculos", true, "GLOBAL") ? 0 : 1;
        RegraCalculos.setSelectedIndex(selectedIndex);
        
        bShowCotaParcela.addActionListener(e -> {
            String value = bShowCotaParcela.getSelectedItem().toString();
            if (!new Config().Saveing("bShowCotaParcela", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("bShowCotaParcela", true, "GLOBAL") ? 0 : 1;        
        bShowCotaParcela.setSelectedIndex(selectedIndex);
        
        bShowCotaParcelaExtrato.addActionListener(e -> {
            String value = bShowCotaParcelaExtrato.getSelectedItem().toString();
            if (!new Config().Saveing("bShowCotaParcelaExtrato", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("bShowCotaParcelaExtrato", true, "GLOBAL") ? 0 : 1;        
        bShowCotaParcelaExtrato.setSelectedIndex(selectedIndex);
        
        ShowRecebimentoExtrato.addActionListener(e -> {
            String value = ShowRecebimentoExtrato.getSelectedItem().toString();
            if (!new Config().Saveing("ShowRecebimentoExtrato", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("ShowRecebimentoExtrato", true, "GLOBAL") ? 0 : 1;        
        ShowRecebimentoExtrato.setSelectedIndex(selectedIndex);
        
        ShowLabelsDatasExtrato.addActionListener(e -> {
            String value = ShowLabelsDatasExtrato.getSelectedItem().toString();
            if (!new Config().Saveing("DatasRecPagExtrato", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("DatasRecPagExtrato", true, "GLOBAL") ? 0 : 1;        
        ShowLabelsDatasExtrato.setSelectedIndex(selectedIndex);
        
        miscelaneas.addActionListener(e -> {
            String value = miscelaneas.getSelectedItem().toString();
            if (!new Config().Saveing("Micelaneas", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("Micelaneas", true, "GLOBAL") ? 0 : 1;        
        miscelaneas.setSelectedIndex(selectedIndex);
        
        showrecvalores.addActionListener(e -> {
            String value = showrecvalores.getSelectedItem().toString();
            if (!new Config().Saveing("ShowRecValores", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("ShowRecValores", true, "GLOBAL") ? 0 : 1;        
        showrecvalores.setSelectedIndex(selectedIndex);
        
        gerMulSelect.addActionListener(e -> {
            String value = gerMulSelect.getSelectedItem().toString();
            if (!new Config().Saveing("gerMulSelect", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("gerMulSelect", true, "GLOBAL") ? 0 : 1;        
        gerMulSelect.setSelectedIndex(selectedIndex);
     
        scroll.addActionListener(e -> {
            String value = scroll.getSelectedItem().toString();
            if (!new Config().Saveing("scroll", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("scroll", true, "GLOBAL") ? 0 : 1;        
        scroll.setSelectedIndex(selectedIndex);
     
        ShowDocBoleta.addActionListener(e -> {
            String value = ShowDocBoleta.getSelectedItem().toString();
            if (!new Config().Saveing("ShowDocBoleta", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("ShowDocBoleta", true, "GLOBAL") ? 0 : 1;        
        ShowDocBoleta.setSelectedIndex(selectedIndex);
     
        ExtratoTotal.addActionListener(e -> {
            String value = ExtratoTotal.getSelectedItem().toString();
            if (!new Config().Saveing("ExtratoTotal", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("ExtratoTotal", true, "GLOBAL") ? 0 : 1;        
        ExtratoTotal.setSelectedIndex(selectedIndex);
        
        ShowRetencaoExtrato.addActionListener(e -> {
            String value = ShowRetencaoExtrato.getSelectedItem().toString();
            if (!new Config().Saveing("ShowRetencaoExtrato", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("ShowRetencaoExtrato", true, "GLOBAL") ? 0 : 1;        
        ShowRetencaoExtrato.setSelectedIndex(selectedIndex);
        
        JuntaALDCExtrato.addActionListener(e -> {
            String value = JuntaALDCExtrato.getSelectedItem().toString();
            if (!new Config().Saveing("JuntaALDCExtrato", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("JuntaALDCExtrato", true, "GLOBAL") ? 0 : 1;        
        JuntaALDCExtrato.setSelectedIndex(selectedIndex);
        
        JuntaALDFExtrato.addActionListener(e -> {
            String value = JuntaALDFExtrato.getSelectedItem().toString();
            if (!new Config().Saveing("JuntaALDFExtrato", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("JuntaALDFExtrato", true, "GLOBAL") ? 0 : 1;        
        JuntaALDFExtrato.setSelectedIndex(selectedIndex);
        
        boletoMU.addActionListener(e -> {
            String value = boletoMU.getSelectedItem().toString();
            if (!new Config().Saveing("boletoMU", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("boletoMU", true, "GLOBAL") ? 0 : 1;        
        boletoMU.setSelectedIndex(selectedIndex);
        
        boletoJU.addActionListener(e -> {
            String value = boletoJU.getSelectedItem().toString();
            if (!new Config().Saveing("boletoJU", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("boletoJU", true, "GLOBAL") ? 0 : 1;        
        boletoJU.setSelectedIndex(selectedIndex);
        
        boletoCO.addActionListener(e -> {
            String value = boletoCO.getSelectedItem().toString();
            if (!new Config().Saveing("boletoCO", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("boletoCO", true, "GLOBAL") ? 0 : 1;        
        boletoCO.setSelectedIndex(selectedIndex);
        
        boletoEP.addActionListener(e -> {
            String value = boletoEP.getSelectedItem().toString();
            if (!new Config().Saveing("boletoEP", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("boletoEP", true, "GLOBAL") ? 0 : 1;        
        boletoEP.setSelectedIndex(selectedIndex);
        
        boletoSomaEP.addActionListener(e -> {
            String value = boletoSomaEP.getSelectedItem().toString();
            if (!new Config().Saveing("boletoSomaEP", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("boletoSomaEP", true, "GLOBAL") ? 0 : 1;        
        boletoSomaEP.setSelectedIndex(selectedIndex);
        
        extADM.addActionListener(e -> {
            String value = extADM.getSelectedItem().toString();
            if (!new Config().Saveing("extADM", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("extADM", true, "GLOBAL") ? 0 : 1;        
        extADM.setSelectedIndex(selectedIndex);
        
        extMU.addActionListener(e -> {
            String value = extMU.getSelectedItem().toString();
            if (!new Config().Saveing("extMU", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("extMU", true, "GLOBAL") ? 0 : 1;        
        extMU.setSelectedIndex(selectedIndex);
        
        extJU.addActionListener(e -> {
            String value = extJU.getSelectedItem().toString();
            if (!new Config().Saveing("extJU", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("extJU", true, "GLOBAL") ? 0 : 1;        
        extJU.setSelectedIndex(selectedIndex);
        
        extCO.addActionListener(e -> {
            String value = extCO.getSelectedItem().toString();
            if (!new Config().Saveing("extCO", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("extCO", true, "GLOBAL") ? 0 : 1;        
        extCO.setSelectedIndex(selectedIndex);
        
        extEP.addActionListener(e -> {
            String value = extEP.getSelectedItem().toString();
            if (!new Config().Saveing("extEP", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("extEP", true, "GLOBAL") ? 0 : 1;        
        extEP.setSelectedIndex(selectedIndex);
        
        bloqAdianta.addActionListener(e -> {
            String value = bloqAdianta.getSelectedItem().toString();
            if (!new Config().Saveing("bloqAdianta", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("bloqAdianta", true, "GLOBAL") ? 0 : 1;        
        bloqAdianta.setSelectedIndex(selectedIndex);
        
        dimob.addActionListener(e -> {
            String value = dimob.getSelectedItem().toString();
            if (!new Config().Saveing("Dimob", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("Dimob", true, "GLOBAL") ? 0 : 1;        
        dimob.setSelectedIndex(selectedIndex);
        
        debug.addActionListener(e -> {
            String value = debug.getSelectedItem().toString();
            if (!new Config().Saveing("Debug", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("Debug", true, "GLOBAL") ? 0 : 1;        
        debug.setSelectedIndex(selectedIndex);
        
        withPool.addActionListener(e -> {
            String value = withPool.getSelectedItem().toString();
            if (!new Config().Saveing("WithPool", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("WithPool", true, "GLOBAL") ? 0 : 1;        
        withPool.setSelectedIndex(selectedIndex);
        
        GER_AL_IN00.addActionListener(e -> {
            String value = GER_AL_IN00.getSelectedItem().toString();
            if (!new Config().Saveing("GER_AL_IN00", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        selectedIndex = (boolean)new Config().Reading("GER_AL_IN00", true, "GLOBAL") ? 0 : 1;        
        GER_AL_IN00.setSelectedIndex(selectedIndex);
        
        GER_NT_IN00.addActionListener(e -> {
            String value = GER_NT_IN00.getSelectedItem().toString();
            if (!new Config().Saveing("GER_NT_IN00", "LOGICA", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        if (new Config().Reading("GER_NT_IN00", true, "GLOBAL").toString().equalsIgnoreCase("MANTER")) {
            selectedIndex = 0;
        } else if (new Config().Reading("GER_NT_IN00", true, "GLOBAL").toString().equalsIgnoreCase("ZERAR")) {
            selectedIndex = 1;
        } else selectedIndex = 2;     
        GER_NT_IN00.setSelectedIndex(selectedIndex);
        
        bobinaSize.setText(new Config().Reading("bobinaSize", "215, 730, 12, 10, 0, 2", "GLOBAL").toString());
        bobinaSize.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {                
                String value = bobinaSize.getText().trim().isEmpty() ? null : bobinaSize.getText().trim();
                if (value.split(",").length != 6) {
                    JOptionPane.showMessageDialog(null, "Este parametro contêm 6(seis) numeros deparados por virgula.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    bobinaSize.requestFocus();
                    return;
                }
                if (!new Config().Saveing("bobinaSize", "TEXTO", value, "GLOBAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        RETORNO_PATH.setText(new Config().Reading("RETORNOPATH", System.getProperty("user.dir"), "GLOBAL").toString());
        RETORNO_PATH.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {                
                String value = RETORNO_PATH.getText().trim().isEmpty() ? System.getProperty("user.dir") : RETORNO_PATH.getText().trim();
                if (!new Config().Saveing("RETORNOPATH", "TEXTO", value, "GLOBAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        RETORNO_PATH_BTN.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherPastas("Selecione a pasta dos arquivos de retorno", RETORNO_PATH.getText());
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            RETORNO_PATH.setText(nomeArq.trim());
            RETORNO_PATH.requestFocus();
        });
                
        REMESSA_PATH.setText(new Config().Reading("REMESSAPATH", System.getProperty("user.dir"), "GLOBAL").toString());
        REMESSA_PATH.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {                
                String value = REMESSA_PATH.getText().trim().isEmpty() ? System.getProperty("user.dir") : REMESSA_PATH.getText().trim();
                if (!new Config().Saveing("REMESSAPATH", "TEXTO", value, "GLOBAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        REMESSA_PATH_BTN.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherPastas("Selecione a pasta dos arquivos de remessa", REMESSA_PATH.getText());
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            REMESSA_PATH.setText(nomeArq.trim());
            REMESSA_PATH.requestFocus();
        });
                
        myLogo.setText(new Config().Reading("BackGroundImage", "fundoimobilis.png", "GLOBAL").toString());
        myLogo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {                
                String value = myLogo.getText().trim().isEmpty() ? "fundoimobilis.png" : myLogo.getText().trim();
                if (!new Config().Saveing("BackGroundImage", "TEXTO", value, "GLOBAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        myLogo_btn.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherArquivo("Selecione a pasta ou arquivos para background", System.getProperty("user.dir") + File.separator, "Imagens & Figuras", "png", "bmp", "jpg");
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            myLogo.setText(nomeArq.trim());
            myLogo.requestFocus();
        });
                
        imgTimer.setValue((int)new Config().Reading("BackGroundTimer", 15, "GLOBAL"));
        imgTimer.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = (int)imgTimer.getValue() < 15 ? 15 : (int)imgTimer.getValue();
                if (!new Config().Saveing("BackGroundTimer", "NUMERICO", String.valueOf(value), "GLOBAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        impRecibos.addActionListener(e -> {
            String value = impRecibos.getSelectedItem().toString();
            if (!new Config().Saveing("AdiantAviso", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Adiantamento", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Aviso", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("AvisoPre", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Caixa", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Deposito", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Despesas", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Extrato", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("ExtratoSocio", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Boleta", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("PassCaixa", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            if (!new Config().Saveing("Recibo", "TEXTO", value, "GLOBAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        if (new Config().Reading("AdiantAviso", "INTERNA", "GLOBAL").toString().equalsIgnoreCase("INTERNA")) {
            selectedIndex = 0;
        } else selectedIndex = 1;     
        impRecibos.setSelectedIndex(selectedIndex);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        RegraCalculos = new javax.swing.JComboBox<>();
        bShowCotaParcela = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        bShowCotaParcelaExtrato = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        ShowRecebimentoExtrato = new javax.swing.JComboBox<>();
        ShowLabelsDatasExtrato = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        miscelaneas = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        showrecvalores = new javax.swing.JComboBox<>();
        gerMulSelect = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        scroll = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ShowDocBoleta = new javax.swing.JComboBox<>();
        ExtratoTotal = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ShowRetencaoExtrato = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        JuntaALDCExtrato = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        JuntaALDFExtrato = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        boletoMU = new javax.swing.JComboBox<>();
        boletoJU = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        boletoCO = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        boletoEP = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        boletoSomaEP = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        extADM = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        extMU = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        extJU = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        extCO = new javax.swing.JComboBox<>();
        extEP = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        bloqAdianta = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        dimob = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        debug = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        withPool = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        GER_AL_IN00 = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        GER_NT_IN00 = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        bobinaSize = new javax.swing.JTextField();
        RETORNO_PATH = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        RETORNO_PATH_BTN = new javax.swing.JButton();
        REMESSA_PATH = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        REMESSA_PATH_BTN = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        myLogo = new javax.swing.JTextField();
        myLogo_btn = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        imgTimer = new javax.swing.JSpinner();
        jLabel40 = new javax.swing.JLabel();
        impRecibos = new javax.swing.JComboBox<>();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Configurações Globais");
        setMaximumSize(new java.awt.Dimension(659, 514));
        setMinimumSize(new java.awt.Dimension(659, 514));

        jLabel1.setText("RegraCalculos:");

        RegraCalculos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        bShowCotaParcela.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel2.setText("bShowCotaParcela:");

        jLabel3.setText("bShowCotaParcelaExtrato:");

        bShowCotaParcelaExtrato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel4.setText("ShowRecebimentoExtrato:");

        ShowRecebimentoExtrato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        ShowLabelsDatasExtrato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel5.setText("ShowLabelsDatasExtrato:");

        miscelaneas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel6.setText("miscelaneas:");

        jLabel7.setText("showrecvalores:");

        showrecvalores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        gerMulSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel8.setText("gerMulSelect:");

        scroll.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel9.setText("scroll:");

        jLabel10.setText("ShowDocBoleta:");

        ShowDocBoleta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        ExtratoTotal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel11.setText("ExtratoTotal:");

        jLabel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ShowRetencaoExtrato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel13.setText("ShowRetencaoExtrato:");

        JuntaALDCExtrato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel14.setText("JuntaALDCExtrato:");

        JuntaALDFExtrato.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel15.setText("JuntaALDFExtrato:");

        jLabel16.setText("boletoMU:");

        boletoMU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        boletoJU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel17.setText("boletoJU:");

        boletoCO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel18.setText("boletoCO:");

        boletoEP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel19.setText("boletoEP:");

        boletoSomaEP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel20.setText("boletoSomaEP");

        extADM.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel21.setText("extADM:");

        extMU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel22.setText("extMU:");

        extJU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel23.setText("extJU:");

        jLabel24.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel25.setText("extCO:");

        extCO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        extEP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel26.setText("extEP:");

        bloqAdianta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel27.setText("bloqAdianta:");

        dimob.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel28.setText("dimob:");

        debug.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel29.setText("debug:");

        withPool.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel30.setText("withPool:");

        GER_AL_IN00.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "true", "false" }));

        jLabel31.setText("GER_AL_IN00:");

        GER_NT_IN00.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MANTER", "ZERAR", "APAGAR" }));

        jLabel32.setText("GER_NT_IN00:");

        jLabel34.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel35.setText("bobinaSize:");

        jLabel36.setText("RETORNO_PATH:");

        RETORNO_PATH_BTN.setText("...");

        jLabel37.setText("REMESSA_PATH:");

        REMESSA_PATH_BTN.setText("...");

        jLabel38.setText("myLogo:");

        myLogo_btn.setText("...");

        jLabel33.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel39.setText("imgTimer:");

        jLabel40.setText("Impressõa do sistema:");

        impRecibos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INTERNA", "EXTERNA" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(RegraCalculos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bShowCotaParcela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bShowCotaParcelaExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ShowRecebimentoExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ShowLabelsDatasExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(miscelaneas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(showrecvalores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(gerMulSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ShowDocBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ExtratoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ShowRetencaoExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JuntaALDCExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JuntaALDFExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boletoMU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boletoJU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boletoCO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boletoEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(boletoSomaEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(extADM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(extMU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(extJU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(extCO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(extEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bloqAdianta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dimob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel29)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(debug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel30)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(withPool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel31)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(GER_AL_IN00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel32)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(GER_NT_IN00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel38)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(myLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jLabel37)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(REMESSA_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(REMESSA_PATH_BTN)
                                            .addComponent(myLogo_btn)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(jLabel35)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(bobinaSize))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addComponent(jLabel36)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(RETORNO_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, 0)
                                            .addComponent(RETORNO_PATH_BTN))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel39)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(imgTimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel40)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(impRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel25)
                                .addComponent(extCO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26)
                                .addComponent(extEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel27)
                                .addComponent(bloqAdianta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel28)
                                .addComponent(dimob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel29)
                                .addComponent(debug, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(withPool, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel31)
                                .addComponent(GER_AL_IN00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel32)
                                .addComponent(GER_NT_IN00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(ShowRetencaoExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel14)
                                .addComponent(JuntaALDCExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(JuntaALDFExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel16)
                                .addComponent(boletoMU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(boletoJU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(boletoCO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel19)
                                .addComponent(boletoEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20)
                                .addComponent(boletoSomaEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel21)
                                .addComponent(extADM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel22)
                                .addComponent(extMU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel23)
                                .addComponent(extJU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(RegraCalculos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(bShowCotaParcela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(bShowCotaParcelaExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(ShowRecebimentoExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(ShowLabelsDatasExtrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(miscelaneas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(showrecvalores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(gerMulSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(ShowDocBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(ExtratoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(imgTimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel40)
                            .addComponent(impRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(bobinaSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel36)
                            .addComponent(RETORNO_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RETORNO_PATH_BTN))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(REMESSA_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(REMESSA_PATH_BTN))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(myLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(myLogo_btn)))
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ExtratoTotal;
    private javax.swing.JComboBox<String> GER_AL_IN00;
    private javax.swing.JComboBox<String> GER_NT_IN00;
    private javax.swing.JComboBox<String> JuntaALDCExtrato;
    private javax.swing.JComboBox<String> JuntaALDFExtrato;
    private javax.swing.JTextField REMESSA_PATH;
    private javax.swing.JButton REMESSA_PATH_BTN;
    private javax.swing.JTextField RETORNO_PATH;
    private javax.swing.JButton RETORNO_PATH_BTN;
    private javax.swing.JComboBox<String> RegraCalculos;
    private javax.swing.JComboBox<String> ShowDocBoleta;
    private javax.swing.JComboBox<String> ShowLabelsDatasExtrato;
    private javax.swing.JComboBox<String> ShowRecebimentoExtrato;
    private javax.swing.JComboBox<String> ShowRetencaoExtrato;
    private javax.swing.JComboBox<String> bShowCotaParcela;
    private javax.swing.JComboBox<String> bShowCotaParcelaExtrato;
    private javax.swing.JComboBox<String> bloqAdianta;
    private javax.swing.JTextField bobinaSize;
    private javax.swing.JComboBox<String> boletoCO;
    private javax.swing.JComboBox<String> boletoEP;
    private javax.swing.JComboBox<String> boletoJU;
    private javax.swing.JComboBox<String> boletoMU;
    private javax.swing.JComboBox<String> boletoSomaEP;
    private javax.swing.JComboBox<String> debug;
    private javax.swing.JComboBox<String> dimob;
    private javax.swing.JComboBox<String> extADM;
    private javax.swing.JComboBox<String> extCO;
    private javax.swing.JComboBox<String> extEP;
    private javax.swing.JComboBox<String> extJU;
    private javax.swing.JComboBox<String> extMU;
    private javax.swing.JComboBox<String> gerMulSelect;
    private javax.swing.JSpinner imgTimer;
    private javax.swing.JComboBox<String> impRecibos;
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
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox<String> miscelaneas;
    private javax.swing.JTextField myLogo;
    private javax.swing.JButton myLogo_btn;
    private javax.swing.JComboBox<String> scroll;
    private javax.swing.JComboBox<String> showrecvalores;
    private javax.swing.JComboBox<String> withPool;
    // End of variables declaration//GEN-END:variables
}
