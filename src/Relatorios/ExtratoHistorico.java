package Relatorios;

import Funcoes.AutoCompletion;
import Funcoes.Dates;
import Funcoes.Db;
import Funcoes.FuncoesGlobais;
import Funcoes.LerValor;
import Funcoes.StringManager;
import Funcoes.StringUtils;
import Funcoes.VariaveisGlobais;
import Funcoes.gmail.GmailAPI;
import static Funcoes.gmail.GmailOperations.createEmailWithAttachment;
import static Funcoes.gmail.GmailOperations.createMessageWithEmail;
import Funcoes.tempFile;
import Funcoes.toPreview;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.lowagie.text.Font;
import Sici.Partida.Collections;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class ExtratoHistorico extends javax.swing.JInternalFrame {
    Db conn = VariaveisGlobais.conexao;
    boolean bExecNome = false, bExecCodigo = false;
    
    public ExtratoHistorico() {
        initComponents();
        
        // Icone da tela
        FlatSVGIcon icone = new FlatSVGIcon("menuIcons/ExtratoHistorico.svg",16,16);
        setFrameIcon(icone);        
        
        FillCombos();
        AutoCompletion.enable(jRgprp);
        AutoCompletion.enable(jNomeProp);
     
        ehStatus.setText("");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(0);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
    }

    private void FillCombos() {
        String sSql = "SELECT distinct p.rgprp, p.nome, '' AS tag FROM proprietarios p WHERE Upper(p.status) = 'ATIVO' ORDER BY p.nome;";
        ResultSet imResult = conn.OpenTable(sSql, null);
        int nRecord = conn.RecordCount(imResult);
        
        List<Object[]> props = new ArrayList<Object[]>();
        try {
            while (imResult.next()) {
                if (!imResult.getString("tag").equalsIgnoreCase("X")) {
                    props.add(new Object[] {String.valueOf(imResult.getInt("rgprp")), imResult.getString("nome")});
                } else {
                    Object[][] aAvisos = null;
                    aAvisos = conn.ReadFieldsTable(new String[] {"tag"}, "avisos", "rid = 0 and registro = '" + String.valueOf(imResult.getInt("rgprp")) + "' AND tag != 'X'");
                    if (aAvisos != null) {
                        props.add(new Object[] {String.valueOf(imResult.getInt("rgprp")), imResult.getString("nome")});
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        conn.CloseTable(imResult);
        
        // Deixa somente quem tem saldo  
        List<Object[]> props_final = new ArrayList<Object[]>();
        for (Object[] item : props) props_final.add(item);

        // Ordena Lista
        props_final.sort(new Comparator<Object[]>(){
            @Override
            public int compare(Object[] o1, Object[] o2)
            {
               return o1[1].toString().compareTo(o2[1].toString());
            }
        });
        
        jRgprp.removeAllItems();
        jNomeProp.removeAllItems();
        for (Object[] item : props_final) {
            jRgprp.addItem(item[0].toString());
            jNomeProp.addItem(item[1].toString());
        }
    }
    
    private void HistoricoPreview(Date dtini, Date dtfim, String rgprp) {
        jGerar.setEnabled(false);

        Collections gVar = VariaveisGlobais.dCliente;
        List<classExtratoHistorico> lista = new ArrayList<>();
        
        List<Date> dataExtHist = retDatas(dtini, dtfim, rgprp);
        if (dataExtHist == null) {
            JOptionPane.showMessageDialog(this, "Não há movimento neste período de datas.");

            jGerar.setEnabled(true);

            ehStatus.setText("");
            ehProgress.setIndeterminate(false);
            ehProgress.setMaximum(0);
            ehProgress.setMinimum(0);
            ehProgress.setValue(0);
            
            return;
        }
        
        String selectSQL = "select 'EXT' tipo, e.rgprp, e.rgimv, e.contrato, e.campo, e.dtvencimento, e.dtrecebimento, " +
        "e.tag, e.rc_aut, e.et_aut, e.pr_sdant from extrato e WHERE (e.rgprp = :rgprp) AND e.et_aut != 0 AND " +
        "e.dtrecebimento BETWEEN :dtini1 AND :dtfim1 " +
        "union SELECT 'AVI' tipo, a.registro rgprp, null rgimv, null contrato, a.campo, " +
        "RetAvDataRid2(a.campo) dtvencimento, RetAvDataRid2(a.campo) dtrecebimento, " +
        "a.tag, a.autenticacao rc_aut, a.et_aut, 0 pr_sdant FROM avisos a " +
        "where (a.registro = :registro) AND a.rid = 0 and a.et_aut != 0 AND " + 
        "RetAvDataRid2(a.campo) BETWEEN :dtini2 AND :dtfim2 " + 
        "union SELECT 'AUX' tipo, x.contrato rgprp, null rgimv, null contrato, x.campo, "  +
        "x.dtvencimento, x.dtrecebimento, 'X' tag, x.rc_aut, x.rc_aut et_aut, 0 pr_sdant " +
        "FROM auxiliar x WHERE x.conta = 'EXT' AND x.contrato = :auxiliar AND " +
        "x.dtrecebimento BETWEEN :dtini3 AND :dtfim3 " +
        "ORDER BY 7;";

        ehStatus.setText("Montando o relatório...  [AGUARDE!]");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(dataExtHist.size());
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
        int posPg = 0;
        
        BigDecimal saldo = new BigDecimal(0);
        for (Date data : dataExtHist) {
            ehStatus.setText("Montando o relatório...  [AGUARDE!]");
            ehProgress.setValue(posPg++);
            
            // Saldo de Abertura
            BigDecimal saldoAbertura = pegaSaldoAnterior(dataExtHist.get(0), rgprp);
            if (saldoAbertura != null) {
                if (saldoAbertura.signum() < 0) {
                    saldo = saldo.subtract(saldoAbertura);
                    lista.add(new classExtratoHistorico(data, "Saldo Abertura", null, null, null, saldoAbertura));
                } else {
                    saldo = saldo.add(saldoAbertura);
                    lista.add(new classExtratoHistorico(data, "Saldo Abertura", null, null, null, saldoAbertura));
                }
            }

            ResultSet rs = conn.OpenTable(selectSQL, new Object[][] {
                {"string", "rgprp", rgprp},
                {"string", "registro", rgprp},
                {"string", "auxiliar", rgprp},
                {"date", "dtini1", data},
                {"date", "dtfim1", data},
                {"date", "dtini2", data},
                {"date", "dtfim2", data},
                {"date", "dtini3", data},
                {"date", "dtfim3", data}
            });
            
            try {
                String _tipo = null, _rgprp = null, _rgimv = null, _contrato = null;
                while (rs.next()) {
                    try { _tipo = rs.getString("tipo"); } catch (SQLException e) { _tipo = null; }
                    try { _rgprp = rs.getString("rgprp"); } catch (SQLException e) { _rgprp = null; }
                    try { _rgimv = rs.getString("rgimv"); } catch (SQLException e) { _rgimv = null; }
                    try { _contrato = rs.getString("contrato"); } catch (SQLException e) { _contrato = null; }
                    
                    if (_tipo.equalsIgnoreCase("AVI")) {
                        // Aviso
                        String tmpCampo = "" + rs.getString("campo");
                        String tmpAuten = "" + rs.getString("rc_aut");
                        String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, false);
                        String sinq = FuncoesGlobais.DecriptaNome(rCampos[0][10]) + " - " + rCampos[0][7].substring(0, 2) + "/" + rCampos[0][7].substring(2,4) + "/" + rCampos[0][7].substring(4) + " - " + tmpAuten;
                        if (!"".equals(sinq.trim())) {
                            java.awt.Font ft = new java.awt.Font("Arial",Font.NORMAL,9);
                            List aLinhas = StringUtils.wrap(sinq, getFontMetrics(ft), 220);
                            for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { lista.add(new classExtratoHistorico(data, (String)linha.next(), null, null, null, null)); }
                            int posicaoValor = lista.size() - 1;
                            if ("CRE".equals(rCampos[0][8])) {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                                lista.get(posicaoValor).setCredito(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                            } else {
                                saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                                lista.get(posicaoValor).setDebito(new BigDecimal(LerValor.FormatNumber(rCampos[0][2],2).replace(".", "").replace(",", ".")));
                            }
                            lista.get(posicaoValor).setSaldo(saldo);
                        }
                    } if (_tipo.equalsIgnoreCase("EXT")) {
                        // Extrato
                        String tmpCampo = rs.getString("campo");
                        String[][] rCampos = FuncoesGlobais.treeArray(tmpCampo, true);

                        for (int j = 0; j<rCampos.length; j++) {

                            if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][2],2)) == 0) continue;

                            String tpCampo = rCampos[j][rCampos[j].length - 1];
                            String tpCota = "";
                            if (VariaveisGlobais.bShowCotaParcelaExtrato) {
                                String spart1 = "", spart2 = "", scotaparc = "";
                                if (!"".equals(rCampos[j][3].trim())) {
                                    spart1 = rCampos[j][3].trim().substring(0, 2);
                                    spart2 = rCampos[j][3].trim().substring(2);
                                } else {
                                    spart1 = "00"; spart2 = "0000";
                                }
                                if (!"00".equals(spart1) && "0000".equals(spart2)) {
                                    spart1 = "00";
                                } else if ("00".equals(spart1) && !"0000".equals(spart2)) {
                                    spart2 = "0000";
                                }
                                scotaparc = spart1 + spart2;
                                tpCota = ("0000".equals(scotaparc) || "000000".equals(scotaparc) || "".equals(scotaparc) ? "       " : scotaparc.substring(0,2) + "/" + scotaparc.substring(2));
                            }
                            boolean bRetc = (FuncoesGlobais.IndexOf(rCampos[j], "RT") > -1) || (FuncoesGlobais.IndexOf(rCampos[j], "AT") > -1);
                            if ("AL".equals(rCampos[j][4])) {
                                if (LerValor.isNumeric(rCampos[j][0])) {
                                    Object[][] hBusca = conn.ReadFieldsTable(new String[] {"end", "num", "compl"}, "imoveis", "rgimv = '" + rs.getString("rgimv") + "'");

                                    java.awt.Font ft = new java.awt.Font("Arial",Font.NORMAL,8);
                                    String imv = rs.getString("rgimv").trim() + " - " + hBusca[0][3].toString().trim() + ", " + hBusca[1][3].toString().trim() + " " + hBusca[2][3].toString().trim();
                                    List aLinhas = StringUtils.wrap(imv, getFontMetrics(ft), 257);
                                    for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { lista.add(new classExtratoHistorico(data,  StringManager.ConvStr((String) linha.next()).replace("ò", " "), null, null, null, null)); }

                                    String loc = conn.ReadFieldsTable(new String[] {"nomerazao"}, "locatarios", "contrato = '" + rs.getString("contrato") + "'")[0][3].toString();
                                    aLinhas = null;
                                    aLinhas = StringUtils.wrap(loc, getFontMetrics(ft), 257);
                                    for (Iterator linha = aLinhas.iterator(); linha.hasNext();) { lista.add(new classExtratoHistorico(data, StringManager.ConvStr((String) linha.next()).replace("ò", " "), null, null, null, null)); }

                                    int posValor = lista.size() - 1;
                                    lista.get(posValor).setReferencia("[" + Dates.DateFormata("dd-MM-yyyy", rs.getDate("dtvencimento")) + "] - " + rs.getString("rc_aut"));
                                    
                                    saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    lista.add(new classExtratoHistorico(data, tpCampo, tpCota, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));

                                    int nPos = FuncoesGlobais.IndexOf(rCampos[j], "CM");
                                    if (nPos > -1) {
                                        saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                        lista.add(new classExtratoHistorico(data, gVar.get("CM"), null, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), saldo));
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(9),2)) > 0) {
                                            String wAD = rCampos[j][nPos].split("@")[1];
                                            saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, "Adiantamento", null, null, new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")), saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "MU");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("MU"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "JU");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("JU"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "CO");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("CO"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }

                                    nPos = FuncoesGlobais.IndexOf(rCampos[j], "EP");
                                    if (nPos > -1) {
                                        if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2)) > 0) {
                                            saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")));
                                            lista.add(new classExtratoHistorico(data, gVar.get("EP"), null, new BigDecimal(LerValor.FormatNumber(rCampos[j][nPos].substring(2),2).replace(".", "").replace(",", ".")), null, saldo));
                                        }
                                    }
                                } else {
                                    saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                    lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                                }
                            } else if (FuncoesGlobais.IndexOf(rCampos[j], "AD") > -1) {
                                int nPos = FuncoesGlobais.IndexOf(rCampos[j], "AD");
                                if (LerValor.StringToFloat(LerValor.FormatNumber(rCampos[j][nPos].split("@")[1],2)) > 0) {
                                    String wAD = rCampos[j][nPos].split("@")[1];
                                    saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")));
                                    lista.add(new classExtratoHistorico(data, "Adiantamento - " + rCampos[j][nPos].split("@")[0].substring(2), null, null, new BigDecimal(LerValor.FormatNumber(wAD,2).replace(".", "").replace(",", ".")), saldo));
                                }                        
                            } else if ("DC".equals(rCampos[j][4])) {
                                saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), saldo));
                            } else if ("DF".equals(rCampos[j][4])) {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                            } else if ("SG".equals(rCampos[j][4])) {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                            } else {
                                saldo = saldo.add(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                if (bRetc) saldo = saldo.subtract(new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")));
                                lista.add(new classExtratoHistorico(data, tpCampo, null, new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")), (bRetc ? new BigDecimal(LerValor.FormatNumber(rCampos[j][2],2).replace(".", "").replace(",", ".")) : null), saldo));
                            }
                        }
                    } else if (_tipo.equalsIgnoreCase("AUX")) {
                        // AUXILIAR
                        String tmpCampo = "" + rs.getString("campo");
                        String tmpAuten = "" + rs.getString("rc_aut");
                        String tmpValor = LerValor.FormatNumber(tmpCampo.substring(5,15),2);
                        lista.add(new classExtratoHistorico(data, "Retirada", tmpAuten, null, null, null)); 
                        int posicaoValor = lista.size() - 1;
                        saldo = saldo.subtract(new BigDecimal(tmpValor.replace(".", "").replace(",", ".")));
                        lista.get(posicaoValor).setDebito(new BigDecimal(tmpValor.replace(".", "").replace(",", ".")));
                        lista.get(posicaoValor).setSaldo(saldo);
                    }
                }
            } catch (SQLException sqlEx) {}
            conn.CloseTable(rs);
        }

        ehStatus.setText("Relatório pronto...   [Visualização!]");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(0);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
        
        Object dados_prop[][] = null;
        String nomeProp = "";
        try {
            dados_prop = conn.ReadFieldsTable(new String[] {"banco", "agencia", "conta", "favorecido","cpfcnpj","saldoant","nome"}, "proprietarios", "rgprp = '" + rgprp + "'");
            nomeProp = dados_prop[6][3].toString();
        } catch (SQLException ex) {}
        
        JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);

        String sFileName = new tempFile("pdf").getsPathNameExt();
        String docName = new tempFile().getTempFileName(sFileName);
        String pathName = new tempFile().getTempPath();
        String FileNamePdf = pathName + docName;

        try {
            Map parametros = new HashMap();
            parametros.put("usuario", VariaveisGlobais.usuario);
            parametros.put("logo", "resources" + File.separator + "logos" + File.separator + "extrato" + File.separator + VariaveisGlobais.icoExtrato);
            parametros.put("proprietario", rgprp.trim() + " - " + nomeProp.trim());
            parametros.put("dtini", dtini);
            parametros.put("dtfim", dtfim);

            String nameReport = "ExtratoHistorico.jasper"; // Aqui vai o nome do relatório
            String fileName = "reports" + File.separator + nameReport;
            JasperPrint print = JasperFillManager.fillReport(fileName, parametros, jrds);

            // Create a PDF exporter
            JRExporter exporter = new JRPdfExporter();

            // Configure the exporter (set output file name and print object)
            String outFileName = FileNamePdf;
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFileName);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (!FileNamePdf.isEmpty()) new toPreview(FileNamePdf);
        
        if (false) {
            Object[][] EmailLocaDados = null;
            try { EmailLocaDados = conn.ReadFieldsTable(new String[] {"nome","email"}, "proprietarios", "rgprp = :rgprp", new Object[][] {{"string", "rgprp", rgprp}}); } catch (SQLException sqlEx) {}
            String EmailLoca = EmailLocaDados[1][3].toString().toLowerCase();
            boolean emailvalido = (EmailLoca.indexOf("@") > 0) && (EmailLoca.indexOf("@")+1 < (EmailLoca.lastIndexOf(".")) && (EmailLoca.lastIndexOf(".") < EmailLoca.length()) );
            if (emailvalido) {
                try {            
                    String To = EmailLoca.trim().toLowerCase();
                    String Subject = "Extrato do Mês";
                    String Body = "Documento em Anexo no formato pdf";
                    String[] Attachments = new String[] {System.getProperty("user.dir") + "/" + FileNamePdf};

                    Gmail service = GmailAPI.getGmailService();
                    MimeMessage Mimemessage = createEmailWithAttachment(To,"me",Subject,Body,new File(System.getProperty("user.dir") + "/" + FileNamePdf));
                    Message message = createMessageWithEmail(Mimemessage);
                    message = service.users().messages().send("me", message).execute();

                    System.out.println("Message id: " + message.getId());
                    System.out.println(message.toPrettyString());
                    if (message.getId() != null) {
                        JOptionPane.showMessageDialog(null, "Enviado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Erro ao enviar!!!\n\nTente novamente...", "Atenção", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (HeadlessException | IOException | GeneralSecurityException | MessagingException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        ehStatus.setText("");
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(0);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);

        jGerar.setEnabled(true);
    }
    
    private BigDecimal pegaSaldoAnterior(Date data, String rgprp) {
        ehStatus.setText("Pegando saldo anterior... [Aguarde]");

        BigDecimal retorno = null;
        
        String selectSQL = "select e.pr_sdant from extrato e WHERE (e.rgprp = :rgprp) AND e.et_aut != 0 AND " +
        "e.dtrecebimento BETWEEN :data1 AND :data2 LIMIT 1 ";
        ResultSet rs = conn.OpenTable(selectSQL, new Object[][] {
            {"string", "rgprp", rgprp},
            {"date", "data1", data},
            {"date", "data2", data}
        });

        int recCount = conn.RecordCount(rs);
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(recCount);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);

        int posPg = 0;
        try {
            BigDecimal saldoAnterior = null;        
            while (rs.next()) {
                try { saldoAnterior = rs.getBigDecimal("pr_sdant"); } catch (SQLException ex) { saldoAnterior = null; }
                
                ehProgress.setValue(posPg++);
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        return retorno;
    }
    
    private List<Date> retDatas(Date dtini, Date dtfim, String rgprp) {
        ehStatus.setText("Preparando informações... [Aguarde]");

        List<Date> retorno = new ArrayList<>();
        String selectSQL = "select distinct e.dtrecebimento from extrato e " +
        "WHERE (e.rgprp = :rgprp) AND e.et_aut != 0 AND " +
        "e.dtrecebimento BETWEEN :dtini1 AND :dtfim1 " +
        "union SELECT distinct RetAvDataRid2(a.campo) dtrecebimento " +
        "FROM avisos a WHERE (a.registro = :registro) AND a.rid = 0 and a.et_aut != 0 AND " +
        "RetAvDataRid2(a.campo) BETWEEN :dtini2 AND :dtfim2 " + 
        "union SELECT distinct x.dtrecebimento FROM auxiliar x WHERE x.conta = 'EXT' AND " +
        "x.contrato = :auxiliar AND x.dtrecebimento BETWEEN :dtini3 AND :dtfim3 ORDER BY 1;";

        ResultSet rs = conn.OpenTable(selectSQL, new Object[][] {
            {"string", "rgprp", rgprp},
            {"string", "registro", rgprp},
            {"string", "auxiliar", rgprp},
            {"date", "dtini1", dtini},
            {"date", "dtfim1", dtfim},
            {"date", "dtini2", dtini},
            {"date", "dtfim2", dtfim},
            {"date", "dtini3", dtini},
            {"date", "dtfim3", dtfim}
        });
        
        int recCount = conn.RecordCount(rs);
        ehProgress.setIndeterminate(false);
        ehProgress.setMaximum(recCount);
        ehProgress.setMinimum(0);
        ehProgress.setValue(0);
                
        try {                    
            Date datas = null; int posPg = 0;
            while (rs.next()) {
                try { datas = rs.getDate("dtrecebimento"); } catch (SQLException ex) { datas = null; }
                if (datas != null) retorno.add(datas);
                
                ehProgress.setValue(posPg++);
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        return retorno.size() > 0 ? retorno : null;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jGerar = new javax.swing.JButton();
        jDtIni = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel2 = new javax.swing.JLabel();
        jDtFim = new com.toedter.calendar.JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
        jLabel3 = new javax.swing.JLabel();
        jRgprp = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jNomeProp = new javax.swing.JComboBox();
        ehStatus = new javax.swing.JLabel();
        ehProgress = new javax.swing.JProgressBar();

        setClosable(true);
        setTitle(".:: Extrato Historico por período.");

        jLabel1.setText("Data:");

        jGerar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Actions-document-print-preview-icon.png"))); // NOI18N
        jGerar.setText("Preview");
        jGerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGerarActionPerformed(evt);
            }
        });

        jLabel2.setText("Até");

        jLabel3.setText("Rgprp:");

        jRgprp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRgprpActionPerformed(evt);
            }
        });

        jLabel4.setText("Nome:");

        jNomeProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNomePropActionPerformed(evt);
            }
        });

        ehStatus.setText("Selecionando o período... [AGUARDE!]");

        ehProgress.setIndeterminate(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ehStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNomeProp, 0, 430, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDtIni, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDtFim, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jGerar))
                    .addComponent(ehProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jRgprp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jNomeProp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jGerar, javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jDtFim, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jDtIni, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ehStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ehProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jDtFim, jDtIni});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jGerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGerarActionPerformed
        if (!Dates.isDateValid(Dates.DateFormata("dd-MM-yyyy", jDtIni.getDate()), "dd-MM-yyyy")) {
            JOptionPane.showMessageDialog(this, "Data inicial vazia ou inválida!", "Atenção", JOptionPane.ERROR_MESSAGE);
            jDtIni.requestFocus();
            return;
        }

        if (!Dates.isDateValid(Dates.DateFormata("dd-MM-yyyy", jDtFim.getDate()), "dd-MM-yyyy")) {
            JOptionPane.showMessageDialog(this, "Data final vazia ou inválida!", "Atenção", JOptionPane.ERROR_MESSAGE);
            jDtFim.requestFocus();
            return;
        }

        new SimpleThread().start();
    }//GEN-LAST:event_jGerarActionPerformed

    class SimpleThread extends Thread {
        public SimpleThread() {
        }
        public void run() {
            HistoricoPreview(jDtIni.getDate(), jDtFim.getDate(), jRgprp.getSelectedItem().toString());
        }
    }
    
    private void jRgprpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRgprpActionPerformed
        if (!bExecNome) {
            int pos = jRgprp.getSelectedIndex();
            if (jNomeProp.getItemCount() > 0) {bExecCodigo = true; jNomeProp.setSelectedIndex(pos); bExecCodigo = false;}
        }
    }//GEN-LAST:event_jRgprpActionPerformed

    private void jNomePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNomePropActionPerformed
        if (!bExecCodigo) {
            int pos = jNomeProp.getSelectedIndex();
            if (jRgprp.getItemCount() > 0) {bExecNome = true; jRgprp.setSelectedIndex(pos); bExecNome = false; }
        }
    }//GEN-LAST:event_jNomePropActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar ehProgress;
    private javax.swing.JLabel ehStatus;
    private com.toedter.calendar.JDateChooser jDtFim;
    private com.toedter.calendar.JDateChooser jDtIni;
    private javax.swing.JButton jGerar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox jNomeProp;
    private javax.swing.JComboBox jRgprp;
    // End of variables declaration//GEN-END:variables
}