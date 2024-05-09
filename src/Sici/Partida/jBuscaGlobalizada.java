package Sici.Partida;

import Funcoes.CentralizaTela;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.TableControl;
import Funcoes.VariaveisGlobais;
import static Sici.Partida.Acesso.StringToDocumentToString.pegaListSubMenus;
import static Sici.Partida.Acesso.StringToDocumentToString.subMenuConverter;
import Sici.Partida.Acesso.classSubMenu;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JInternalFrame;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author softeletronic
 */
public class jBuscaGlobalizada extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    private boolean isHeuristica = true;
    private boolean isDeactived = false;
    private boolean isCaseSensitive = false;

    public void setIsCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
        Buscar();        
    }
    
    public void setIsHeuristica(boolean isHeuristica) {
        this.isHeuristica = isHeuristica;
        Buscar();        
    }

    public void setIsDeactived(boolean isDeactived) {
        this.isDeactived = isDeactived;
        Buscar();        
    }
    
    public enum BuscarPOR {
        PROPRIETARIOS,
        IMOVEIS,
        LOCATARIOS,
        FIADORES,
        BOLETAS
    }
    private BuscarPOR buscarpor = BuscarPOR.PROPRIETARIOS;

    public void setBuscarpor(BuscarPOR buscarpor) {
        this.buscarpor = buscarpor;
        switch (buscarpor) {
            case PROPRIETARIOS:
                TableControl.header(jtbBuscas, new String[][] {{"rgprp", "nome", "telefone", "cpf/cnpj", "endereço de correspondência"},{"80","500","120","150","500"}});                
                break;
            case IMOVEIS:
                TableControl.header(jtbBuscas, new String[][] {{"rgprp", "rgimv", "endereço"},{"80","80","500"}});
                break;
            case LOCATARIOS:
                TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "cpf/cnpj", "nome", "endereço", "endereço de correspondência"},{"80","80","150","500","500","500"}});
                break;
            case FIADORES:
                TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "nome", "cpf/cnpj"},{"80","80","500","150"}});
                break;
            case BOLETAS:
                TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "nome", "cpf/cnpj", "vencimento", "tag"},{"80","80","500","150","80","10"}});
                break;
            default:
                // PROPRIETARIOS
                TableControl.header(jtbBuscas, new String[][] {{"rgprp", "nome", "telefone", "cpf/cnpj", "endereço de correspondência"},{"80","500","120","150","500"}});                                
        }
        Buscar();
    }
    
    /**
     * Creates new form jBuscaGlobalizada
     */
    public jBuscaGlobalizada() {
        initComponents();

        putClientProperty("TitlePane.useWindowDecorations", Boolean.valueOf(false));
        putClientProperty("TitlePane.showIcon", Boolean.valueOf(false));
        putClientProperty("TitlePane.centerTitleIfMenuBarEmbedded", Boolean.valueOf(true));
        
        ImageIcon icone = new ImageIcon("/Figuras/seach.png");
        setFrameIcon(icone);
        
        VariaveisGlobais.jBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
              warn();
            }
            public void removeUpdate(DocumentEvent e) {
              warn();
            }
            public void insertUpdate(DocumentEvent e) {
              warn();
            }

            public void warn() {
                if (VariaveisGlobais.jBuscar.getText().trim().equals("")) TableControl.Clear(jtbBuscas);
                if (VariaveisGlobais.jBuscar.getText().trim().equals("") || VariaveisGlobais.jBuscar.getText().trim().length() < 3) {
                    setVisible(false);
                    return;
                }
            }
        });
        
        VariaveisGlobais.jBuscar.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                int xpos = VariaveisGlobais.jPanePrin.getWidth() - getWidth();
                int ypos = 0; //40;
                setLocation(xpos, ypos);

                setVisible(VariaveisGlobais.jBuscar.getText().trim().length() >= 3);
                //VariaveisGlobais.jBuscar.requestFocus();
            };
            public void focusLost(FocusEvent e) {
                setVisible(false);
            }
        });
        
        VariaveisGlobais.jBuscar.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                int xpos = VariaveisGlobais.jPanePrin.getWidth() - getWidth();
                int ypos = 0; //40;
                setLocation(xpos, ypos);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                    return;
                }

                if (VariaveisGlobais.jBuscar.getText().trim().equals("")) TableControl.Clear(jtbBuscas);
                if (VariaveisGlobais.jBuscar.getText().trim().equals("") || VariaveisGlobais.jBuscar.getText().trim().length() < 3) {
                    setVisible(false);
                    return;
                }
                
                Buscar();
                setVisible(true);
                VariaveisGlobais.jBuscar.requestFocus();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbBuscas = new javax.swing.JTable();

        jtbBuscas.setAutoCreateRowSorter(true);
        jtbBuscas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jtbBuscas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtbBuscas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtbBuscas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbBuscasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtbBuscas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtbBuscasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbBuscasMouseClicked
        if (evt.getClickCount() == 2) {
            int selRow = jtbBuscas.getSelectedRow();
            Object valor = null;
            if (selRow > -1) valor = jtbBuscas.getModel().getValueAt(selRow, 0);
            String _class = ""; String _method = ""; String[] _args = {};
            if (buscarpor == BuscarPOR.PROPRIETARIOS || buscarpor == BuscarPOR.IMOVEIS) {
                _class = "Sici.jProprietarios";
                _method = "MoveToProp";
                _args = new String[] {"rgprp", valor.toString()};
            } else if (buscarpor == BuscarPOR.LOCATARIOS || buscarpor == BuscarPOR.FIADORES) {
                _class = "Sici.Locatarios.jLocatarios";
                _method = "MoveToLoca";
                _args = new String[] {"contrato", valor.toString()};
            }
            if (!_class.isEmpty()) {
                try {
                    Class classe = null;
                    classe = Class.forName(_class);
                    JInternalFrame frame = (JInternalFrame) classe.newInstance();

                    // Setar os botoes
                    String xmlStr = VariaveisGlobais.protocolomenu;
                    String _botoes = "";
                    if (xmlStr != null) {
                        List<String> subMenus = pegaListSubMenus(xmlStr);
                        for (String menus : subMenus) {
                            if (menus == null) continue;

                            List<classSubMenu> _sub = subMenuConverter(menus);
                            for (classSubMenu ssub : _sub) {
                                if (ssub.getChamada().equalsIgnoreCase(_class)) {
                                    _botoes = ssub.getBotoes();
                                    break;
                                }
                            }
                            if(!_botoes.isEmpty()) break;
                        }

                        if (!_botoes.isEmpty()) {
                            Class[] btnParam = new Class[1];
                            btnParam[0] = String.class;
                            String[] btnArgs = new String[] {_botoes};
                            Method mtd = classe.getMethod("setBotoes", btnParam);
                            mtd.invoke(frame, btnArgs);
                        }
                    }

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

                VariaveisGlobais.jBuscar.setText("");
                setVisible(false);
            }
        }
    }//GEN-LAST:event_jtbBuscasMouseClicked

    private void Buscar() {
        String[] gbProp = {"p.rgprp", "p.nome", "p.tel", "p.cpfcnpj", "p.cor_end"};
        String[] gbLoca = {"l.contrato", "l.rgimv", "l.cpfcnpj", "l.nomerazao", "l.end", "l.cor_end", "l.aviso"};
        String[] gbImvl = {"i.rgprp", "i.rgimv", "i.end", "i.num", "i.compl", "i.situacao", "i.matriculas", "i.obs"};
        String[] gbFiad = {"f.nomerazao", "f.contrato", "f.rgimv", "f.cpfcnpj"};
        //String[] gbBole = {"b.nnumero", "b.contrato", "b.nome","b.vencimento","b.valor"};
        String[] gbBole = {"r.nnumero", "r.contrato", "(SELECT l.nomerazao FROM locatarios l WHERE l.contrato = r.contrato) AS nomerazao", "r.dtvencimento", "r.tag"};
        String cSql = "";
        String sBusca = isCaseSensitive ? VariaveisGlobais.jBuscar.getText().trim() : VariaveisGlobais.jBuscar.getText().trim().toLowerCase();

        if(sBusca.equals("")) {
            TableControl.Clear(jtbBuscas);
            return;
        } 
            
        if (buscarpor == BuscarPOR.PROPRIETARIOS) {
            String sInativos = "";
            if (!isDeactived) {
                sInativos = "(Lower(Trim(p.status)) = 'ativo') AND (";
                VariaveisGlobais.IProp = false;
            } else {
                sInativos = "(Lower(Trim(p.status)) <> 'ativo') AND (";
                VariaveisGlobais.IProp = true;
            }
            String jgbProp = FuncoesGlobais.join(gbProp, ",");
            cSql =  "SELECT " + jgbProp + " FROM proprietarios p WHERE " + sInativos + (isCaseSensitive ? "BINARY " : "") + gbProp[0].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            (isCaseSensitive ? "BINARY " : "") + gbProp[1].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbProp[2].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            (isCaseSensitive ? "BINARY " : "") + gbProp[3].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbProp[4].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%')";

            TableControl.header(jtbBuscas, new String[][] {{"rgprp", "nome", "telefone", "cpf/cnpj", "endereço de correspondência"},{"80","500","120","150","500"}});
            ResultSet pResult = conn.OpenTable(cSql, null);
            try {
                while (pResult.next()) {
                    String trgprp = String.valueOf(pResult.getInt("p.rgprp"));
                    String tnome = pResult.getString("p.nome");

                    String ttel = "";                    
                    String[] telef = null;
                    try { telef = pResult.getString("p.tel").split(";"); } catch (Exception e) {}
                    if (telef != null) {
                        for (int w=0;w<telef.length;w++) {
                            if (!telef[w].trim().equalsIgnoreCase("")) {
                                String[] tmptel = telef[w].split(",");

                                String teltmp = tmptel[0];
                                if (tmptel.length > 1) teltmp += " * " + tmptel[1];
                                ttel += teltmp + " / ";
                            }
                        }
                        if (ttel.length() > 0) ttel = ttel.substring(0, ttel.length() -3);
                    }

                    String tcpfcnpj = pResult.getString("p.cpfcnpj");
                    String tend = pResult.getString("p.cor_end");
                    TableControl.add(jtbBuscas, new String[][]{{trgprp, tnome, ttel, tcpfcnpj, tend},{"C","L","C","C","L"}},true);
                }
            } catch (SQLException ex) {
                //ex.printStackTrace();
            }
            conn.CloseTable(pResult);

        } else if (buscarpor == BuscarPOR.LOCATARIOS) {
            String sInativos = "";
            if (!isDeactived) {
                sInativos = "(l.fiador1uf <> 'X' OR IsNull(l.fiador1uf)) AND (";
                VariaveisGlobais.Iloca = false;
            } else {
                sInativos = "(l.fiador1uf = 'X') AND (";
                VariaveisGlobais.Iloca = true;
            }
            cSql = "SELECT " + FuncoesGlobais.join(gbLoca,",") + " FROM locatarios l WHERE " + sInativos + (isCaseSensitive ? "BINARY " : "") + gbLoca[0].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            (isCaseSensitive ? "BINARY " : "") + gbLoca[1].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbLoca[2].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            (isCaseSensitive ? "BINARY " : "") + gbLoca[3].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbLoca[4].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            (isCaseSensitive ? "BINARY " : "") + gbLoca[5].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbLoca[6].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%')";

            TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "cpf/cnpj", "nome", "endereço", "endereço de correspondência","aviso"},{"80","80","150","500","500","0","100"}});
            ResultSet pResult = this.conn.OpenTable(cSql, null);
            try {
                while (pResult.next()) {
                    String tcontrato = String.valueOf(pResult.getInt("l.contrato"));
                    String trgimv = String.valueOf(pResult.getInt("l.rgimv"));
                    String tcpfcnpj = pResult.getString("l.cpfcnpj");
                    String tnome = pResult.getString("l.nomerazao");
                    String tend = pResult.getString("l.end");
                    String tendcor = pResult.getString("l.cor_end");
                    String taviso = pResult.getString("l.aviso");
                    TableControl.add(jtbBuscas, new String[][]{{tcontrato, trgimv, tcpfcnpj, tnome, tend, tendcor, taviso},{"C","C","C","L","L","L","L"}},true);
                }
            } catch (SQLException ex) {
                //ex.printStackTrace();
            }
            conn.CloseTable(pResult);

        } else if (buscarpor == BuscarPOR.IMOVEIS) {
            String sInativos = "";
            cSql = "SELECT " + FuncoesGlobais.join(gbImvl,",") + " FROM imoveis i WHERE " + (isCaseSensitive ? "BINARY " : "") + gbImvl[0].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
            (isCaseSensitive ? "BINARY " : "") + gbImvl[1].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbImvl[2].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca +
                    "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbImvl[6].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " + (isCaseSensitive ? "BINARY " : "") + gbImvl[7].toLowerCase() + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%';";

            TableControl.header(jtbBuscas, new String[][] {{"rgprp", "rgimv", "endereço","obs"},{"80","80","500","100"}});
            ResultSet pResult = this.conn.OpenTable(cSql, null);
            try {
                while (pResult.next()) {
                    String trgprp = String.valueOf(pResult.getInt("i.rgprp"));
                    String trgimv = String.valueOf(pResult.getInt("i.rgimv"));
                    String tend = pResult.getString("i.end").trim() + ", " + pResult.getString("i.num").trim() + " " + pResult.getString("i.compl").trim();
                    String tsit = pResult.getString("i.situacao");
                    String tobs = pResult.getString("i.obs");
                    
                    if (isDeactived) {
                        if (tsit.equalsIgnoreCase("DESATIVADO")) TableControl.add(jtbBuscas, new String[][]{{trgprp, trgimv, tend, tobs},{"C","C","L","L"}},true);
                    } else {
                        if (!tsit.equalsIgnoreCase("DESATIVADO")) TableControl.add(jtbBuscas, new String[][]{{trgprp, trgimv, tend,tobs},{"C","C","L","L"}},true);
                    }
                    
                }
            } catch (SQLException ex) {
                //ex.printStackTrace();
            }
            conn.CloseTable(pResult);

        } else if (buscarpor == BuscarPOR.FIADORES) {
            cSql = "SELECT " + FuncoesGlobais.join(gbFiad,",") + " FROM fiadores f WHERE (" + (isCaseSensitive ? "BINARY " : "") + gbFiad[0].toLowerCase() + (isHeuristica ? ") LIKE '%" : ") LIKE '") + sBusca + "%' OR (" +
            (isCaseSensitive ? "BINARY " : "") + gbFiad[1].toLowerCase() + (isHeuristica ? ") LIKE '%" : ") LIKE '") + sBusca + "%' OR (" + (isCaseSensitive ? "BINARY " : "") + gbFiad[2].toLowerCase() + (isHeuristica ? ") LIKE '%" : ") LIKE '") + sBusca +
            "%' OR (" + (isCaseSensitive ? "BINARY " : "") + gbFiad[3].toLowerCase() + (isHeuristica ? ") LIKE '%" : ") LIKE '") + sBusca + "%'";

            TableControl.header(jtbBuscas, new String[][] {{"contrato", "rgimv", "nome", "cpf/cnpj"},{"80","80","500","150"}});
            ResultSet pResult = this.conn.OpenTable(cSql, null);
            try {
                while (pResult.next()) {
                    String tcontrato = String.valueOf(pResult.getInt("f.contrato"));
                    String trgimv = String.valueOf(pResult.getInt("f.rgimv"));
                    String tnome = pResult.getString("f.nomerazao").trim();
                    String tcpfcnpj = pResult.getString("f.cpfcnpj").trim();
                    TableControl.add(jtbBuscas, new String[][]{{tcontrato, trgimv, tnome, tcpfcnpj},{"C","C","L","C"}},true);
                }
            } catch (SQLException ex) {
                //ex.printStackTrace();
            }
            conn.CloseTable(pResult);
        } else if (buscarpor == BuscarPOR.BOLETAS) {        
            cSql = "SELECT r.nnumero, r.contrato, (SELECT l.nomerazao FROM locatarios l WHERE l.contrato = r.contrato) AS nomerazao, " +
                   "r.dtvencimento, r.tag FROM recibo r WHERE " +
                   "Trim(r.nnumero) <> '' AND " +
                   (isCaseSensitive ? "BINARY " : "") + "r.nnumero " + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' OR " +
                   (isCaseSensitive ? "BINARY " : "") + "r.contrato " + (isHeuristica ? " LIKE '%" : " LIKE '") + sBusca + "%' " +
                   "ORDER BY r.dtvencimento DESC LIMIT 50;";
            
            TableControl.header(jtbBuscas, new String[][] {{"nnumero", "contrato", "nome", "vencimento","tag"},{"120","70","250","90","80"}});
            ResultSet pResult = this.conn.OpenTable(cSql, null);
            try {
                while (pResult.next()) {
                    String tnnumero = pResult.getString("nnumero");
                    String tcontrato = String.valueOf(pResult.getInt("contrato"));
                    String tnome = pResult.getString("nomerazao").trim();
                    String tvecto = Dates.DateFormata("dd/MM/yyyy", pResult.getDate("dtvencimento"));
                    String tvalor = pResult.getString("tag");
                    TableControl.add(jtbBuscas, new String[][]{{tnnumero, tcontrato, tnome, tvecto, tvalor},{"C","C","L","C","R"}},true);
                }
            } catch (SQLException ex) {
                //ex.printStackTrace();
            }
            conn.CloseTable(pResult);
       }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtbBuscas;
    // End of variables declaration//GEN-END:variables
}
