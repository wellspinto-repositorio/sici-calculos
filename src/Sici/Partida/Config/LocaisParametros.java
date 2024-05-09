package Sici.Partida.Config;

import Funcoes.Config;
import Funcoes.FuncoesGlobais;
import Funcoes.VariaveisGlobais;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class LocaisParametros extends javax.swing.JInternalFrame {

    public LocaisParametros() {
        initComponents();

        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/configlocal.svg",16,16);
        setFrameIcon(icone);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                iniciaComponents();
            }
        });        
    }

    private void iniciaComponents() {
        List<PrintersListClass> printers = new PrintersList().list();       
        
        // Impressora thermica
        for (PrintersListClass printer : printers)  _thermalPrint.addItem(printer.getName());
        _thermalPrint.addActionListener(e -> {
            int selectedItem = _thermalPrint.getSelectedIndex();
            String value = printers.get(selectedItem).getIp() + ":" + printers.get(selectedItem).getName() + ":" + printers.get(selectedItem).getShare() + ":*";
            if (!new Config().Saveing("Thermica", "TEXTO", value, "LOCAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                VariaveisGlobais.Thermica = value;
            }
        });        
        String _thermica = VariaveisGlobais.Thermica;
        if (_thermica != null) {
            Object[] thermica = VariaveisGlobais.Thermica.split(":");
            int posPrinter = -1;
            for (int i = 0; i<printers.size(); i++) {
                if (printers.get(i).getName().equalsIgnoreCase(thermica[1].toString())) {
                    posPrinter = i;
                    break;
                }
            }
            _thermalPrint.setSelectedIndex(posPrinter);
        }
        
        // Impressora de Documentos
        for (PrintersListClass printer : printers)  _normalPrinter.addItem(printer.getName());
        _normalPrinter.addActionListener(e -> {
            int selectedItem = _normalPrinter.getSelectedIndex();
            String value = printers.get(selectedItem).getIp() + ":" + printers.get(selectedItem).getName() + ":" + printers.get(selectedItem).getShare() + ":*";
            if (!new Config().Saveing("Printer", "TEXTO", value, "LOCAL")) {
                JOptionPane.showMessageDialog(this, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
            } else {
                VariaveisGlobais.Printer = value;
            }
        });        
        String _printer = VariaveisGlobais.Printer;
        if (_printer != null) {
            Object[] normal = VariaveisGlobais.Printer.split(":");
            int posPrinter = -1;
            for (int i = 0; i<printers.size(); i++) {
                if (printers.get(i).getName().equalsIgnoreCase(normal[1].toString())) {
                    posPrinter = i;
                    break;
                }
            }
            _normalPrinter.setSelectedIndex(posPrinter);
        }
        
        preview.setText(VariaveisGlobais.Preview);
        preview.setToolTipText("Use para substituir as seguintes chaves:\n" + 
                               "[IP] - pega o ip da impressora escolhida.\n" +
                               "[PRINTER] - pega o nome da impressora escolhida.\n" +
                               "[PRINTERSHARE] - pega o nome compartilhado da impressora escolhida.\n" +
                               "[FILENAME] - pega o caminho e nome do documento a ser impresso.");
        preview.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String value = preview.getText().trim().isEmpty() ? null : preview.getText().trim();
                if (!new Config().Saveing("Preview", "TEXTO", value, "LOCAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                };
            }
        });
        previewButton.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherArquivo("Selecione o programa de visualização", System.getProperty("user.dir") + File.separator,"Arquivos...", "exe", "com");
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            preview.setText("\"" + nomeArq.trim() + "\"");
        });
                                
        externo.setText(VariaveisGlobais.Externo);
        externo.setToolTipText("Use para substituir as seguintes chaves:\n" + 
                               "[IP] - pega o ip da impressora escolhida.\n" +
                               "[PRINTER] - pega o nome da impressora escolhida.\n" +
                               "[PRINTERSHARE] - pega o nome compartilhado da impressora escolhida.\n" +
                               "[FILENAME] - pega o caminho e nome do documento a ser impresso.");
        externo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String value = externo.getText().trim().isEmpty() ? null : externo.getText().trim();
                if (!new Config().Saveing("Externo", "TEXTO", value, "LOCAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                };
            }
        });
        externoButton.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherArquivo("Selecione o programa de impressão externo.", System.getProperty("user.dir") + File.separator,"Arquivos...", "exe", "com");
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            externo.setText("\"" + nomeArq.trim() + "\"");
        });
        
        externo2.setText(VariaveisGlobais.Externo2);
        externo2.setToolTipText("Use para substituir as seguintes chaves:\n" + 
                               "[IP] - pega o ip da impressora escolhida.\n" +
                               "[PRINTER] - pega o nome da impressora escolhida.\n" +
                               "[PRINTERSHARE] - pega o nome compartilhado da impressora escolhida.\n" +
                               "[FILENAME] - pega o caminho e nome do documento a ser impresso.");
        externo2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String value = externo2.getText().trim().isEmpty() ? null : externo2.getText().trim();
                if (!new Config().Saveing("Externo2", "TEXTO", value, "LOCAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                };
            }
        });
        externo2Button.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherArquivo("Selecione o programa de impressão externo2.", System.getProperty("user.dir") + File.separator,"Arquivos...", "exe", "com");
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            externo2.setText("\"" + nomeArq.trim() + "\"");
        });
        
        previewDoc.setText(VariaveisGlobais.extPreview);
        previewDoc.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String value = previewDoc.getText().trim().isEmpty() ? null : previewDoc.getText().trim();
                if (!new Config().Saveing("extPreview", "TEXTO", value, "LOCAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                };
            }
        });
        previewDocButton.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherArquivo("Selecione o Arquivo para preview do Extrado.", VariaveisGlobais.SYSTEM_PATH + File.separator + "reports","Arquivos de Impressão.", "jasper");
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            previewDoc.setText(nomeArq.trim());
        });
        
        printDoc.setText(VariaveisGlobais.extPrint);
        printDoc.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String value = printDoc.getText().trim().isEmpty() ? null : printDoc.getText().trim();
                if (!new Config().Saveing("extPrint", "TEXTO", value, "LOCAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                };
            }
        });
        printDocButton.addActionListener((ActionEvent e) -> {
            String nomeArq = FuncoesGlobais.escolherArquivo("Selecione o Arquivo para impressão do Extrado.", VariaveisGlobais.SYSTEM_PATH + File.separator + "reports","Arquivos de Impressão.", "jasper");
            if (nomeArq == null) {
                JOptionPane.showMessageDialog(this, "Nada foi selecionado.");
                return;
            }
            printDoc.setText(nomeArq.trim());
        });
        
        nrecibos.setValue(VariaveisGlobais.nviasRecibo);
        nrecibos.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String value = nrecibos.getValue().toString();
                if (!new Config().Saveing("nviasRecibo", "TEXTO", value, "LOCAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                };
            }
        });
        
        outlook.setSelected(VariaveisGlobais.OUTLOOK);
        outlook.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = outlook.isSelected() ? "true" : "false";
                if (!new Config().Saveing("OUTLOOK", "LOGICA", value, "LOCAL")) {
                    JOptionPane.showMessageDialog(null, "Não foi possivel gravar esta configuração!\nTente novamente...\n\nSe persistir chame suporte técnico.", "Erro", JOptionPane.ERROR_MESSAGE);
                };
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        _thermalPrint = new javax.swing.JComboBox<>();
        _normalPrinter = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        preview = new javax.swing.JTextField();
        previewButton = new javax.swing.JButton();
        externoButton = new javax.swing.JButton();
        externo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        externo2 = new javax.swing.JTextField();
        externo2Button = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        previewDoc = new javax.swing.JTextField();
        previewDocButton = new javax.swing.JButton();
        printDoc = new javax.swing.JTextField();
        printDocButton = new javax.swing.JButton();
        outlook = new javax.swing.JCheckBox();
        nrecibos = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle(".:: Parametros Locais.");
        setDoubleBuffered(true);
        setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        setMaximumSize(new java.awt.Dimension(789, 319));
        setMinimumSize(new java.awt.Dimension(789, 319));
        setNormalBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setOpaque(true);
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("[ Seleção das Impressoras ]"));

        jLabel1.setText("Recibos:");

        jLabel2.setText("Documentos:");

        _thermalPrint.setMaximumSize(new java.awt.Dimension(296, 22));
        _thermalPrint.setMinimumSize(new java.awt.Dimension(296, 22));
        _thermalPrint.setPreferredSize(new java.awt.Dimension(296, 22));

        _normalPrinter.setMaximumSize(new java.awt.Dimension(296, 22));
        _normalPrinter.setMinimumSize(new java.awt.Dimension(296, 22));
        _normalPrinter.setPreferredSize(new java.awt.Dimension(296, 22));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_thermalPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_normalPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(230, 230, 230))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(_thermalPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_normalPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        jLabel3.setText("Preview:");

        previewButton.setText("...");

        externoButton.setText("...");

        jLabel7.setText("Externo:");

        jLabel8.setText("Externo2");

        externo2Button.setText("...");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("[ Documento do Extrato ]"));

        jLabel4.setText("Pré-Visualização:");

        jLabel5.setText("Impressão:");

        previewDocButton.setText("...");

        printDocButton.setText("...");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(previewDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(previewDocButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(printDocButton))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(previewDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(previewDocButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(printDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(printDocButton)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        outlook.setText("Usar OUTLOOK para enviar e-mails.");

        nrecibos.setMaximumSize(new java.awt.Dimension(66, 24));

        jLabel6.setText("Número de vias impressa na impressora de Recibos:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preview)
                        .addGap(0, 0, 0)
                        .addComponent(previewButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(externo)
                        .addGap(0, 0, 0)
                        .addComponent(externoButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(externo2)
                        .addGap(0, 0, 0)
                        .addComponent(externo2Button))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nrecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(187, 187, 187)
                        .addComponent(outlook)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(preview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(previewButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(externo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(externoButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(externo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(externo2Button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(nrecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outlook))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> _normalPrinter;
    private javax.swing.JComboBox<String> _thermalPrint;
    private javax.swing.JTextField externo;
    private javax.swing.JTextField externo2;
    private javax.swing.JButton externo2Button;
    private javax.swing.JButton externoButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner nrecibos;
    private javax.swing.JCheckBox outlook;
    private javax.swing.JTextField preview;
    private javax.swing.JButton previewButton;
    private javax.swing.JTextField previewDoc;
    private javax.swing.JButton previewDocButton;
    private javax.swing.JTextField printDoc;
    private javax.swing.JButton printDocButton;
    // End of variables declaration//GEN-END:variables
}
