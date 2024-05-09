package Bancos;

import Funcoes.Dates;
import Funcoes.FuncoesGlobais;
import static Funcoes.FuncoesGlobais.StrZero;
import Funcoes.LerValor;
import Funcoes.Pad;
import Funcoes.StreamFile;
import Funcoes.StringManager;
import Funcoes.VariaveisGlobais;
import Protocolo.Calculos;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author supervisor
 */
public class itau {
    private bancos bco = new bancos(VariaveisGlobais.conexao);          
    
    public String NossoNumeroItau(String value, int tam) {
        String valor1 = StringManager.Right(FuncoesGlobais.StrZero("0", tam - 1) +
                        Integer.valueOf(value).toString().trim(),tam - 1);
        String valor3 = bco.getAgencia() + bco.getConta() + bco.getCarteira() + valor1;
        String valor2 = bco.CalcDig10(valor3);
        return valor1 + valor2;
    }
    
    public String FatorVencimento(String fator, String vencimento) {
        String retorno = "0000";
        if (vencimento.length() < 8) {retorno = "0000";} else {
            retorno = String.valueOf(Dates.DateDiff(Dates.DIA, Dates.StringtoDate(fator, "dd/MM/yyyy"), Dates.StringtoDate(vencimento, "dd/MM/yyyy")));
        }
        
        return retorno;
    }

    public String CalcDig11(String cadeia, int limitesup, int lflag) {

        int mult; int total; int nresto; int ndig; int pos;
        String retorno = "";
        
        mult = 1 + (cadeia.length() % (limitesup - 1));
        if (mult == 1) { mult = limitesup; }
        
        total = 0;
        for (pos=0;pos<=cadeia.length()-1;pos++) {
            total += Integer.valueOf(cadeia.substring(pos, pos + 1)) * mult;
            mult -= 1;
            if (mult == 1) mult = limitesup;
        }
        
        nresto = (total % 11);
        if (lflag == 1) { retorno = String.valueOf(nresto); } else {
            if (nresto == 0 || nresto == 1 || nresto == 10) { ndig = 1; } else ndig = 11 - nresto;
            retorno = String.valueOf(ndig);
        }
        return retorno;
    }
    
    public String CodBar(String vencimento,String valor,String nossonumero) {
        String strcodbar; String dv3;
        strcodbar = bco.getBanco() + bco.getMoeda() + 
                    FatorVencimento("07/10/1997", vencimento) + 
                    bco.Valor4Boleta(valor) + 
                    bco.getCarteira() + nossonumero + bco.getAgencia() + 
                    bco.getConta() + bco.getCtaDv() + "000";
        dv3 = CalcDig11(strcodbar,9,0);
        return bco.getBanco() + bco.getMoeda() + dv3 + 
               FatorVencimento("07/10/1997", vencimento) + 
               bco.Valor4Boleta(valor) + bco.getCarteira() + nossonumero +
               bco.getAgencia() + bco.getConta() + bco.getCtaDv() + "000";
    }

    public String LinhaDigitavel(String codigobarras) {
        String cmplivre; String campo1, campo2, campo3, campo4, campo5;
        
        cmplivre = codigobarras.substring(19,44);
        campo1 = codigobarras.substring(0,4) + cmplivre.substring(0,5);
        campo1 += bco.CalcDig10(campo1);
        campo1 = campo1.substring(0, 5) + "." + campo1.substring(5,10);
        
        campo2 = cmplivre.substring(5,15);
        campo2 += bco.CalcDig10(campo2);
        campo2 = campo2.substring(0,5) + "." + campo2.substring(5, 11);
        
        campo3 = cmplivre.substring(15, 25);
        campo3 += bco.CalcDig10(campo3);
        campo3 = campo3.substring(0, 5) + "." + campo3.substring(5, 11);
        
        campo4 = codigobarras.substring(4, 5);
        
        campo5 = codigobarras.substring(5, 19);
        
        if (Float.valueOf(campo5) == 0) campo5 = "000";
        
        return campo1 + "  " + campo2 + "  " + campo3 + "  " + campo4 + "  " + campo5;
    }

    public String Remessa(String nrlote, String fileName, String movimento, String[][] lista, String tipo) {
        if (lista.length == 0) return "Lista vazia!";
        
        bco.LerBancoAvulso("341");
        
        File diretorio = new File("remessa"); if (!diretorio.exists()) { diretorio.mkdirs(); }
        
        String nroLote = nrlote;
        
        File arquivo = new File("remessa/" + fileName + nroLote + ".rem");
        if (arquivo.exists()) {
            JOptionPane.showMessageDialog(null, "Arquivo de remessa ja existe!!!\n\nTente novamente com outro nome.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return "Arquivo de remessa ja existe!";
        }
        
        String LF = "\r\n";
        
        String _banco = bco.getBanco();
        if (tipo == "A") {
            String[] variaveis = {"LOTE_" + _banco, nrlote,"NUMERICO"};
            try { VariaveisGlobais.conexao.SaveParameters(variaveis); } catch (Exception e) {}
        }
        String nmEmp = new Pad(VariaveisGlobais.dCliente.get("empresa"),30).RPad();
        String icEmp = FuncoesGlobais.StrZero(bco.rmvNumero(VariaveisGlobais.dCliente.get("cnpj")),14);
        int ctalinhas = 1;
        StreamFile filler = new StreamFile(new String[] {"remessa/" + fileName + nroLote + ".rem"});
        if (filler.Open()) {
            String codBaco = _banco;
            String loteSer = "0000";
            String tipoSer = "0";
            String reserVd = FuncoesGlobais.Space(9);
            String tipoInc = "2";
            String inscEmp = icEmp;  // cnpj
            String codTran = FuncoesGlobais.Space(20);
            String reseVad = "0" + bco.getAgencia() + " 0000000" + bco.getConta() + " " + bco.getCtaDv();
            String nomeEmp = nmEmp; // 30 digitos
            String nomeBan = new Pad("BANCO ITAU SA",30).RPad(); // 30 digitos
            String resVado = FuncoesGlobais.Space(10); // 10 digitos
            String codRems = "1";
            String dtGerac = Dates.DateFormata("ddMMyyyy", new Date()); // data atual
            String rservDo = Dates.DateFormata("HHmmss", new Date()); // Hora da geração do Arquivo
            String numSequ = FuncoesGlobais.StrZero(nroLote, 6); // 6 digitos de 1 a 999999 <
            String Versaos = "040";
            String reSerVa = "00000" + FuncoesGlobais.Space(54) + "000" + FuncoesGlobais.Space(12);
                    
            String output = codBaco + loteSer + tipoSer + reserVd + tipoInc +
                            inscEmp + codTran + reseVad + nomeEmp + nomeBan +
                            resVado + codRems + dtGerac + rservDo + numSequ +
                            Versaos + reSerVa;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            String codBco = _banco; 
            String loteRe = "0001"; 
            String tpRems = "1";
            String tpOper = "R";
            String tpServ = "01";
            String reseVd = "00";
            String nversa = "030";
            String resedo = FuncoesGlobais.Space(1);
            String tpInsc = "2";
            String cnpjEp = "0" + icEmp;
            String resVdo = FuncoesGlobais.Space(20) + "0";
            String codTrE = bco.getAgencia() + " 0000000" + bco.getConta(); //"340500007926383";
            String rseVdo =  " " + bco.getCtaDv();
            String nomeCd = nmEmp; // 30dig
            String mensN1 = FuncoesGlobais.Space(40); // 40dig
            String mensN2 = FuncoesGlobais.Space(40); // 40dig
            String numRRt = "00000000"; // 8dig
            String dtgrav = Dates.DateFormata("ddMMyyyy", new Date()) + "00000000";
            String reVado = FuncoesGlobais.Space(33);

            output = codBco + loteRe + tpRems + tpOper + tpServ + reseVd +
                     nversa + resedo + tpInsc + cnpjEp + resVdo + codTrE +
                     rseVdo + nomeCd + mensN1 + mensN2 + numRRt + dtgrav +
                     reVado;
            filler.Print(output + LF);
        }

        ctalinhas += 1;
        
        int contarecibos = 0; float totrecibos = 0f;
        for (int i=0; i < lista.length; i++) {
            if (filler.Open()) {
                String _rgprp = null;
                String _rgimv = null;
                String _contrato = null;
                
                String _nome = null;
                String _cpfcnpj = null;
                
                String _ender = null;       // = vCampos[4][3].trim() + ", " + vCampos[5][3].trim() + " " + vCampos[6][3].trim();
                String _bairro = null;
                String _cidade = null;
                String _estado = null;
                String _cep = null;
                
                String _vencto = null;
                String _valor = null ;
                String _rnnumero = null;    // = lista[i][3].substring(0, 12);
                String _rnnumerodac = null; // = lista[i][3].substring(12, 13);

                Object[][] vCampos = null;
                try {
                    vCampos = VariaveisGlobais.conexao.ReadFieldsTable(new String[] {"l.contrato", "l.rgprp", "l.rgimv", "l.aviso", "i.end", "i.num","i.compl", "i.bairro", "i.cidade", "i.estado", "i.cep", "p.nome", "l.cpfcnpj", "l.nomerazao"}, "locatarios l, imoveis i, proprietarios p", "(l.rgprp = i.rgprp AND l.rgimv = i.rgimv AND l.rgprp = p.rgprp) AND l.contrato = '" + lista[i][0] + "'");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                if (vCampos == null) {
                    try {
                        vCampos = VariaveisGlobais.conexao.ReadFieldsTable(new String[] {"rgprp", "end", "num","compl", "bairro", "cidade", "estado", "cep", "nome", "cpfcnpj"}, "proprietarios", "rgprp = '" + lista[i][0] + "'");
                    } catch (SQLException ex) {}

                    if (vCampos != null) {
                        _rgprp = "";
                        _rgimv = "";
                        _contrato = vCampos[0][3].toString();

                        _nome = vCampos[8][3].toString().trim();
                        _cpfcnpj = vCampos[9][3].toString().trim();

                        _ender = vCampos[1][3].toString().trim() + ", " + vCampos[2][3].toString().trim() + " " + vCampos[3][3].toString().trim();
                        _bairro = vCampos[4][3].toString().trim();
                        _cidade = vCampos[5][3].toString().trim();
                        _estado = vCampos[6][3].toString().trim();
                        _cep = vCampos[7][3].toString().trim();
                    } else {
                        // Avulsos
                    }
                } else {
                    _rgprp = vCampos[1][3].toString();
                    _rgimv = vCampos[2][3].toString();
                    _contrato = vCampos[0][3].toString();

                    _nome = vCampos[13][3].toString().trim();
                    _cpfcnpj = vCampos[12][3].toString().trim();

                    _ender = vCampos[4][3].toString().trim() + ", " + vCampos[5][3].toString().trim() + " " + vCampos[6][3].toString().trim();
                    _bairro = vCampos[7][3].toString().trim();
                    _cidade = vCampos[8][3].toString().trim();
                    _estado = vCampos[9][3].toString().trim();
                    _cep = vCampos[10][3].toString().trim();
                }
                _vencto = lista[i][1];
                _valor = lista[i][2];
                _rnnumero = lista[i][3].substring(0, 12);
                _rnnumerodac = lista[i][3].substring(12, 13);
                
                // P
                String codBcC = _banco; 
                String nrReme = "0001";
                String tpRegi = "3";
                String nrSequ = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                String cdSegR = "P";
                String rsvDos = FuncoesGlobais.Space(1);
                String cdMvRm = movimento + "0"; // 01 - Entrada de título
                String agCedn = bco.getAgencia() + " 0000000"; //"3405";  // agencia do cedente
                String digAgc = bco.getConta() + FuncoesGlobais.Space(1); // CalcDig11N(bco.getAgencia());  //"3"; // digito verificador
                String numCoC = bco.getCtaDv();
                String digCoC = bco.getCarteira();
                String contCb = FuncoesGlobais.StrZero(_rnnumero, 8);
                String digtCb = _rnnumerodac;
                String rservo = FuncoesGlobais.Space(8);
                String nnumer = "00000";
                String tpoCob = "";
                String formCd = "";
                String tipoDc = "";
                String rsvad1 = "";
                String rsvad2 = "";
                String numDoc = new Pad(_contrato,10).RPad() + FuncoesGlobais.Space(5);
                String dtavtt = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); // "ddmmaaaa"; // data de vencimento do titulo
                String vrnmtt = bco.fmtNumero(_valor); //"000000000123129"; // valor nominal do titulo
                String agencb = "00000"; // agencia encarregada
                String digaec = "0"; // digito
                String rsvado = "";
                String esptit = "05"; /// 05 - recibo
                String idtitu = "N";
                String dtemti = Dates.DateFormata("ddMMyyyy", new Date()); //"ddmmaaaa"; // data emissao do titulo
                String cdjuti = "0"; // codigo juros do titulo
                String dtjrmo = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data juros mora
                
                
                // 28-06-2017 8h58m
                BigDecimal valor = new BigDecimal(_valor.replace(".", "").replace(",", ".")).multiply(new BigDecimal("0.00033"));
                //float valor = LerValor.StringToFloat(_valor) * 0.00033f;
                //String vrmtxm = bco.fmtNumero(LerValor.FloatToString(valor)); //"000000000000041"; // valor ou taxa de mora (aluguel * 0,0333)
                String vrmtxm = bco.fmtNumero(valor.toPlainString().replace(".", ","));
                String cddesc = "0"; // codigo desconto
                String dtdesc = "00000000"; // data desconto
                String vrpecd = "000000000000000"; // valor ou percentual de desconto
                String vriofr = "000000000000000"; // iof a ser recolhido
                String vrabti = "000000000000000"; // valor abatimento
                String idttep = FuncoesGlobais.Space(25);
                String cdprot = "0"; // codigo para protesto 0 - Sem instrução
                String nrdpro = "00"; // numero de dias para protesto
                String cdbxdv = "1"; // codigo baixa devolucao (2)
                String revdao = "";
                String nrdibd = "30"; // numero de dias baixa devolucao
                String cdmoed = "0000000000000"; // codigo moeda
                String revado = FuncoesGlobais.Space(1);

                String output = codBcC + nrReme + tpRegi + nrSequ + cdSegR + rsvDos +
                                cdMvRm + agCedn + digAgc + numCoC + digCoC + contCb +
                                digtCb + rservo + nnumer + tpoCob + formCd + tipoDc +
                                rsvad1 + rsvad2 + numDoc + dtavtt + vrnmtt + agencb +
                                digaec + rsvado + esptit + idtitu + dtemti + cdjuti +
                                dtjrmo + vrmtxm + cddesc + dtdesc + vrpecd + vriofr +
                                vrabti + idttep + cdprot + nrdpro + cdbxdv + revdao +
                                nrdibd + cdmoed + revado;
                filler.Print(output + LF);
        
                ctalinhas += 1;
                
                // Para uso do trayler do lote
                contarecibos += 1;
                totrecibos += LerValor.StringToFloat(_valor);
                        
                if (movimento.equalsIgnoreCase("01")) {
                    /*
                    Marca remessa = 'S' 
                    */
                    String wSql = "UPDATE recibo SET remessa = 'S' WHERE nnumero LIKE '%" + bco.SoNumeroSemZerosAEsq(_rnnumero)  + "%';";
                    try {
                        VariaveisGlobais.conexao.CommandExecute(wSql, null);
                    } catch (Exception e) {}


                    // Sqgmento Q
                    String cdbcoc = _banco; 
                    String nrltre = "0001";
                    String tiporg = "3";

                    String nrSeqq = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                    String cdregt = "Q";
                    String bracos = FuncoesGlobais.Space(1);
                    String cdmvrm = movimento; // ou 02 - pedido de baixa
                    
                    String _tcpfcnpj = bco.rmvLetras(bco.rmvNumero(_cpfcnpj));
                    String cpfCNPJ = (_tcpfcnpj.length() == 11 ? "1" : "2");
                    String tpinss = cpfCNPJ; // tipo inscricao sacado
                    
                    String inscsc = FuncoesGlobais.StrZero(_tcpfcnpj,15); //"000000000000000"; // CPF/CNPJ
                    String nmesac = FuncoesGlobais.myLetra(new Pad(_nome.toUpperCase(),30).RPad()) + FuncoesGlobais.Space(10); //"(40)"; // nome do sacado
                    String endsac = FuncoesGlobais.myLetra(new Pad(_ender,40).RPad().toUpperCase()); //"(40)"; // endereco 
                    String baisac = FuncoesGlobais.myLetra(new Pad(_bairro,15).RPad().toUpperCase()); // "(15)"; // bairro
                    String cepsac = FuncoesGlobais.myLetra(new Pad(_cep.substring(0, 5),5).RPad().toUpperCase()); // "(5)";  // cep
                    String cepsus = FuncoesGlobais.myLetra(new Pad(_cep.substring(6, 9),3).RPad().toUpperCase()); // "(3)";  // sufixo cep
                    String cidsac = FuncoesGlobais.myLetra(new Pad(_cidade,15).RPad().toUpperCase()); //"(15)"; // cidade
                    String ufsaca = FuncoesGlobais.myLetra(new Pad(_estado,2).RPad().toUpperCase()); //"RJ";   // UF
                    String demais = "0000000000000000" + FuncoesGlobais.Space(40) + "000" + FuncoesGlobais.Space(28);

                    output = cdbcoc + nrltre + tiporg + nrSeqq + cdregt + bracos +
                             cdmvrm + tpinss + inscsc + nmesac + endsac + baisac +
                             cepsac + cepsus + cidsac + ufsaca + demais;
                    filler.Print(output + LF);

                    ctalinhas += 1;

                    // R
                    String cbcodc = _banco;
                    String nrlotr = "0001";
                    String tporeg = "3";

                    String nrSeqr = StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                    String cdgseg = "R";
                    String spacob = FuncoesGlobais.Space(1);
                    String cdomot = movimento;  // ou 02 - baixa
                    String cdgdes = "0"; // codigo desconto
                    String dtdes2 = "00000000"; // data desconto 2
                    String vrpccd = "000000000000000"; // valor perc desco
                    String brac24 = "0";
                    String cdmult = "00000000" + "000000000000000" + "2"; // codigo da multa (1 - fixo / 2 - perc)
                    String dtamul = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data multa

                    String vrpcap = "000000000001000"; // vr/per multa 10%
                    String bran10 = FuncoesGlobais.Space(10);
                    String msge03 = FuncoesGlobais.Space(40); // msg 3
                    String msge04 = FuncoesGlobais.Space(60); // msg 4
                    String branfn = "00000000" + "00000000" + " " + "000000000000" + "  " + "0" + FuncoesGlobais.Space(9);

                    output = cbcodc + nrlotr + tporeg + nrSeqr + cdgseg + spacob +
                             cdomot + cdgdes + dtdes2 + vrpccd + brac24 + cdmult +
                             dtamul + vrpcap + bran10 + msge03 + msge04 + branfn;
                    filler.Print(output + LF);

                    ctalinhas += 1;

                    String msgBol01 = null; try { msgBol01 = VariaveisGlobais.conexao.ReadParameters("MSGBOL1"); } catch (SQLException e) {} if (msgBol01 == null) msgBol01 = "";
                    String msgBol02 = null; try { msgBol02 = VariaveisGlobais.conexao.ReadParameters("MSGBOL2"); } catch (SQLException e) {} if (msgBol02 == null) msgBol02 = "";
                    String msgBol03 = null; try { msgBol03 = VariaveisGlobais.conexao.ReadParameters("MSGBOL3"); } catch (SQLException e) {} if (msgBol03 == null) msgBol03 = "";
                    String msgBol04 = null; try { msgBol04 = VariaveisGlobais.conexao.ReadParameters("MSGBOL4"); } catch (SQLException e) {} if (msgBol04 == null) msgBol04 = "";
                    String msgBol05 = null; try { msgBol05 = VariaveisGlobais.conexao.ReadParameters("MSGBOL5"); } catch (SQLException e) {} if (msgBol05 == null) msgBol05 = "";
                    String msgBol06 = null; try { msgBol06 = VariaveisGlobais.conexao.ReadParameters("MSGBOL6"); } catch (SQLException e) {} if (msgBol06 == null) msgBol06 = "";
                    String msgBol07 = null; try { msgBol07 = VariaveisGlobais.conexao.ReadParameters("MSGBOL7"); } catch (SQLException e) {} if (msgBol07 == null) msgBol07 = "";
                    String msgBol08 = null; try { msgBol08 = VariaveisGlobais.conexao.ReadParameters("MSGBOL8"); } catch (SQLException e) {} if (msgBol08 == null) msgBol08 = "";
                    String msgBol09 = null; try { msgBol09 = VariaveisGlobais.conexao.ReadParameters("MSGBOL9"); } catch (SQLException e) {} if (msgBol09 == null) msgBol09 = "";

                    Calculos rc = new Calculos(); String _tipoimovel = null;
                    try {
                        rc.Inicializa(_rgprp, _rgimv, _contrato);
                        _tipoimovel = rc.TipoImovel();
                    } catch (Exception ex) {_tipoimovel = "RESIDENCIAL";}
                    Date tvecto = Dates.StringtoDate(_vencto,"dd/MM/yyyy");
                    String carVecto = Dates.DateFormata("dd/MM/yyyy", 
                                    Dates.DateAdd(Dates.DIA, (int)rc.dia_mul, tvecto));

                    String ln08 = "";
                    if ("".equals(msgBol08)) {
                        ln08 = "APÓS O DIA " + carVecto + " MULTA DE 2% + ENCARGOS DE 0,333% AO DIA DE ATRASO.";
                    } else {
                        // [VENCIMENTO] - Mostra Vencimento
                        // [CARENCIA] - Mostra Vencimento + Carencia
                        // [MULTA] - Mostra Juros
                        // [ENCARGOS] - Mostra Encargos
                        ln08 = msgBol08.replace("[VENCIMENTO]", Dates.DateFormata("dd/MM/yyyy", tvecto));
                        ln08 = ln08.replace("[CARENCIA]", carVecto);
                        ln08 = ln08.replace("[MULTA]", String.valueOf(_tipoimovel.equalsIgnoreCase("RESIDENCIAL") ? rc.multa_res : rc.multa_com).replace(".0", "") + "%");
                        ln08 = ln08.replace("[ENCARGOS]", "0,333%");
                    }
                    msgBol08 = ln08;
                    msgBol09 = ("".equals(msgBol09) ? "NÃO RECEBER APÓS 30 DIAS DO VENCIMENTO." : msgBol09);

                } 
            }
        }

        if (filler.Open()) {
            // trailer lote
            String cdgcom = _banco; 
            String nrores = "0001";
            String tporgt = "5";
            String brantl = FuncoesGlobais.Space(9);
            String qtdrlt = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 1), 6); //"000000"; // quantidade reg no lote
            String brcolt = FuncoesGlobais.StrZero(String.valueOf(contarecibos), 6) + "00" + bco.fmtNumero(LerValor.FloatToString(totrecibos)) + 
                            "000000" + "00000000000000000" + FuncoesGlobais.StrZero("0", 46) + FuncoesGlobais.Space(8) + FuncoesGlobais.Space(117);

            String output = cdgcom + nrores + tporgt + brantl + qtdrlt + brcolt;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            // trailer arquivo remessa
            String cgdcop = _banco; 
            String nrolte = "9999";
            String tpregi = "9";
            String brcoat = FuncoesGlobais.Space(9);
            String qtdlaq = "000001"; // quantidade de lotes do arquivo
            String qtdrga = FuncoesGlobais.StrZero(String.valueOf(ctalinhas), 6); //"000000"; // quantidade reg do arquivo tipo=0+1+2+3+5+9
            String brcalt = FuncoesGlobais.Space(211);

            output = cgdcop + nrolte + tpregi + brcoat + qtdlaq + qtdrga + brcalt;
            filler.Print(output + LF);
        }        
        filler.Close();        
        
        if (tipo == "A") JOptionPane.showMessageDialog(null, "Arquivo de remessa " + fileName + nroLote + ".rem" + " gerado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        return fileName + nroLote + ".rem";
    }

    public void RemessaAvulsa(String nrlote, String fileName, String movimento, String[][] lista, String[] vCampos) {
        if (lista.length == 0) return;
        
        bco.LerBancoAvulso("341");
        
        File diretorio = new File("remessa"); if (!diretorio.exists()) { diretorio.mkdirs(); }
        
        String nroLote = nrlote;
        
        File arquivo = new File("remessa/" + bco.getBanco() + fileName + nroLote + ".rem");
        if (arquivo.exists()) {
            JOptionPane.showMessageDialog(null, "Arquivo de remessa ja existe!!!\n\nTente novamente com outro nome.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String LF = "\r\n";
        
        String _banco = bco.getBanco();
        String[] variaveis = {"LOTE_" + _banco, nrlote,"NUMERICO"};
        try { VariaveisGlobais.conexao.SaveParameters(variaveis); } catch (Exception e) {}
        
        String nmEmp = new Pad(VariaveisGlobais.dCliente.get("empresa"),30).RPad();
        String icEmp = FuncoesGlobais.StrZero(bco.rmvNumero(VariaveisGlobais.dCliente.get("cnpj")),14);
        int ctalinhas = 1;
        StreamFile filler = new StreamFile(new String[] {"remessa/" + bco.getBanco() + fileName + nroLote + ".rem"});
        if (filler.Open()) {
            String codBaco = _banco;
            String loteSer = "0000";
            String tipoSer = "0";
            String reserVd = FuncoesGlobais.Space(9);
            String tipoInc = "2";
            String inscEmp = icEmp;  // cnpj
            String codTran = FuncoesGlobais.Space(20);
            String reseVad = "0" + bco.getAgencia() + " 0000000" + bco.getConta() + " " + bco.getCtaDv();
            String nomeEmp = nmEmp; // 30 digitos
            String nomeBan = new Pad("BANCO ITAU SA",30).RPad(); // 30 digitos
            String resVado = FuncoesGlobais.Space(10); // 10 digitos
            String codRems = "1";
            String dtGerac = Dates.DateFormata("ddMMyyyy", new Date()); // data atual
            String rservDo = Dates.DateFormata("HHmmss", new Date()); // Hora da geração do Arquivo
            String numSequ = FuncoesGlobais.StrZero(nroLote, 6); // 6 digitos de 1 a 999999 <
            String Versaos = "040";
            String reSerVa = "00000" + FuncoesGlobais.Space(54) + "000" + FuncoesGlobais.Space(12);
                    
            String output = codBaco + loteSer + tipoSer + reserVd + tipoInc +
                            inscEmp + codTran + reseVad + nomeEmp + nomeBan +
                            resVado + codRems + dtGerac + rservDo + numSequ +
                            Versaos + reSerVa;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            String codBco = _banco; 
            String loteRe = "0001"; 
            String tpRems = "1";
            String tpOper = "R";
            String tpServ = "01";
            String reseVd = "00";
            String nversa = "030";
            String resedo = FuncoesGlobais.Space(1);
            String tpInsc = "2";
            String cnpjEp = "0" + icEmp;
            String resVdo = FuncoesGlobais.Space(20) + "0";
            String codTrE = bco.getAgencia() + " 0000000" + bco.getConta(); //"340500007926383";
            String rseVdo =  " " + bco.getCtaDv();
            String nomeCd = nmEmp; // 30dig
            String mensN1 = FuncoesGlobais.Space(40); // 40dig
            String mensN2 = FuncoesGlobais.Space(40); // 40dig
            String numRRt = "00000000"; // 8dig
            String dtgrav = Dates.DateFormata("ddMMyyyy", new Date()) + "00000000";
            String reVado = FuncoesGlobais.Space(33);

            output = codBco + loteRe + tpRems + tpOper + tpServ + reseVd +
                     nversa + resedo + tpInsc + cnpjEp + resVdo + codTrE +
                     rseVdo + nomeCd + mensN1 + mensN2 + numRRt + dtgrav +
                     reVado;
            filler.Print(output + LF);
        }

        ctalinhas += 1;
        
        int contarecibos = 0; float totrecibos = 0f;
        for (int i=0; i < lista.length; i++) {
            if (filler.Open()) {
                String _rgprp = vCampos[1];
                String _rgimv = vCampos[2];
                String _contrato = vCampos[0];
                
                String _nome = vCampos[14].trim();
                String _cpfcnpj = vCampos[13].trim();
                
                String _ender = vCampos[4].trim() + ", " + vCampos[5].trim() + " " + vCampos[6].trim();
                String _bairro = vCampos[7].trim();
                String _cidade = vCampos[8].trim();
                String _estado = vCampos[9].trim();
                String _cep = vCampos[10].trim();
                
                String _vencto = lista[i][1];
                String _valor = lista[i][2];
                String _rnnumero = lista[i][3].substring(0, 12);
                String _rnnumerodac = lista[i][3].substring(12, 13);
                
                // P
                String codBcC = _banco; 
                String nrReme = "0001";
                String tpRegi = "3";
                String nrSequ = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                String cdSegR = "P";
                String rsvDos = FuncoesGlobais.Space(1);
                String cdMvRm = movimento + "0"; // 01 - Entrada de título
                String agCedn = bco.getAgencia() + " 0000000"; //"3405";  // agencia do cedente
                String digAgc = bco.getConta() + FuncoesGlobais.Space(1); // CalcDig11N(bco.getAgencia());  //"3"; // digito verificador
                String numCoC = bco.getCtaDv();
                String digCoC = bco.getCarteira();
                String contCb = FuncoesGlobais.StrZero(_rnnumero, 8);
                String digtCb = _rnnumerodac;
                String rservo = FuncoesGlobais.Space(8);
                String nnumer = "00000";
                String tpoCob = "";
                String formCd = "";
                String tipoDc = "";
                String rsvad1 = "";
                String rsvad2 = "";
                String numDoc = new Pad(_contrato,10).RPad() + FuncoesGlobais.Space(5);
                String dtavtt = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); // "ddmmaaaa"; // data de vencimento do titulo
                String vrnmtt = bco.fmtNumero(_valor); //"000000000123129"; // valor nominal do titulo
                String agencb = "00000"; // agencia encarregada
                String digaec = "0"; // digito
                String rsvado = "";
                String esptit = "05"; /// 05 - recibo
                String idtitu = "N";
                String dtemti = Dates.DateFormata("ddMMyyyy", new Date()); //"ddmmaaaa"; // data emissao do titulo
                String cdjuti = "0"; // codigo juros do titulo
                String dtjrmo = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data juros mora
                
                
                // 28-06-2017 8h58m
                BigDecimal valor = new BigDecimal(_valor.replace(".", "").replace(",", ".")).multiply(new BigDecimal("0.00033"));
                //float valor = LerValor.StringToFloat(_valor) * 0.00033f;
                //String vrmtxm = bco.fmtNumero(LerValor.FloatToString(valor)); //"000000000000041"; // valor ou taxa de mora (aluguel * 0,0333)
                String vrmtxm = bco.fmtNumero(valor.toPlainString().replace(".", ","));
                String cddesc = "0"; // codigo desconto
                String dtdesc = "00000000"; // data desconto
                String vrpecd = "000000000000000"; // valor ou percentual de desconto
                String vriofr = "000000000000000"; // iof a ser recolhido
                String vrabti = "000000000000000"; // valor abatimento
                String idttep = FuncoesGlobais.Space(25);
                String cdprot = "0"; // codigo para protesto 0 - Sem instrução
                String nrdpro = "00"; // numero de dias para protesto
                String cdbxdv = "1"; // codigo baixa devolucao (2)
                String revdao = "";
                String nrdibd = "15"; // numero de dias baixa devolucao
                String cdmoed = "0000000000000"; // codigo moeda
                String revado = FuncoesGlobais.Space(1);

                String output = codBcC + nrReme + tpRegi + nrSequ + cdSegR + rsvDos +
                                cdMvRm + agCedn + digAgc + numCoC + digCoC + contCb +
                                digtCb + rservo + nnumer + tpoCob + formCd + tipoDc +
                                rsvad1 + rsvad2 + numDoc + dtavtt + vrnmtt + agencb +
                                digaec + rsvado + esptit + idtitu + dtemti + cdjuti +
                                dtjrmo + vrmtxm + cddesc + dtdesc + vrpecd + vriofr +
                                vrabti + idttep + cdprot + nrdpro + cdbxdv + revdao +
                                nrdibd + cdmoed + revado;
                filler.Print(output + LF);
        
                ctalinhas += 1;
                
                // Para uso do trayler do lote
                contarecibos += 1;
                totrecibos += LerValor.StringToFloat(_valor);
                        
                if (movimento.equalsIgnoreCase("01")) {
                    /*
                    Marca remessa = 'S' 
                    */
                    String wSql = "UPDATE bloquetos SET remessa = 'S' WHERE nnumero LIKE '%" + _rnnumero  + "%';";
                    try {
                        VariaveisGlobais.conexao.CommandExecute(wSql, null);
                    } catch (Exception e) {}


                    // Sqgmento Q
                    String cdbcoc = _banco; 
                    String nrltre = "0001";
                    String tiporg = "3";

                    String nrSeqq = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                    String cdregt = "Q";
                    String bracos = FuncoesGlobais.Space(1);
                    String cdmvrm = movimento; // ou 02 - pedido de baixa
                    
                    String _tcpfcnpj = bco.rmvLetras(bco.rmvNumero(_cpfcnpj));
                    String cpfCNPJ = (_tcpfcnpj.length() == 11 ? "1" : "2");
                    String tpinss = cpfCNPJ; // tipo inscricao sacado
                    
                    String inscsc = FuncoesGlobais.StrZero(cpfCNPJ,15); //"000000000000000"; // CPF/CNPJ
                    String nmesac = FuncoesGlobais.myLetra(new Pad(_nome.toUpperCase(),30).RPad()) + FuncoesGlobais.Space(10); //"(40)"; // nome do sacado
                    String endsac = FuncoesGlobais.myLetra(new Pad(_ender,40).RPad().toUpperCase()); //"(40)"; // endereco 
                    String baisac = FuncoesGlobais.myLetra(new Pad(_bairro,15).RPad().toUpperCase()); // "(15)"; // bairro
                    String cepsac = FuncoesGlobais.myLetra(new Pad(_cep.substring(0, 5),5).RPad().toUpperCase()); // "(5)";  // cep
                    String cepsus = FuncoesGlobais.myLetra(new Pad(_cep.substring(6, 9),3).RPad().toUpperCase()); // "(3)";  // sufixo cep
                    String cidsac = FuncoesGlobais.myLetra(new Pad(_cidade,15).RPad().toUpperCase()); //"(15)"; // cidade
                    String ufsaca = FuncoesGlobais.myLetra(new Pad(_estado,2).RPad().toUpperCase()); //"RJ";   // UF
                    String demais = "0000000000000000" + FuncoesGlobais.Space(40) + "000" + FuncoesGlobais.Space(28);

                    output = cdbcoc + nrltre + tiporg + nrSeqq + cdregt + bracos +
                             cdmvrm + tpinss + inscsc + nmesac + endsac + baisac +
                             cepsac + cepsus + cidsac + ufsaca + demais;
                    filler.Print(output + LF);

                    ctalinhas += 1;

                    // R
                    String cbcodc = _banco;
                    String nrlotr = "0001";
                    String tporeg = "3";

                    String nrSeqr = StrZero(String.valueOf(ctalinhas - 2), 5); //numero de seq do lote
                    String cdgseg = "R";
                    String spacob = FuncoesGlobais.Space(1);
                    String cdomot = movimento;  // ou 02 - baixa
                    String cdgdes = "0"; // codigo desconto
                    String dtdes2 = "00000000"; // data desconto 2
                    String vrpccd = "000000000000000"; // valor perc desco
                    String brac24 = "0";
                    String cdmult = "00000000" + "000000000000000" + "2"; // codigo da multa (1 - fixo / 2 - perc)
                    String dtamul = Dates.StringtoString(_vencto,"dd/MM/yyyy","ddMMyyyy"); //"ddmmaaaa"; // data multa

                    String vrpcap = "000000000001000"; // vr/per multa 10%
                    String bran10 = FuncoesGlobais.Space(10);
                    String msge03 = FuncoesGlobais.Space(40); // msg 3
                    String msge04 = FuncoesGlobais.Space(60); // msg 4
                    String branfn = "00000000" + "00000000" + " " + "000000000000" + "  " + "0" + FuncoesGlobais.Space(9);

                    output = cbcodc + nrlotr + tporeg + nrSeqr + cdgseg + spacob +
                             cdomot + cdgdes + dtdes2 + vrpccd + brac24 + cdmult +
                             dtamul + vrpcap + bran10 + msge03 + msge04 + branfn;
                    filler.Print(output + LF);

                    ctalinhas += 1;
                }
            }
        }

        if (filler.Open()) {
            // trailer lote
            String cdgcom = _banco; 
            String nrores = "0001";
            String tporgt = "5";
            String brantl = FuncoesGlobais.Space(9);
            String qtdrlt = FuncoesGlobais.StrZero(String.valueOf(ctalinhas - 1), 6); //"000000"; // quantidade reg no lote
            String brcolt = FuncoesGlobais.StrZero(String.valueOf(contarecibos), 6) + "00" + bco.fmtNumero(LerValor.FloatToString(totrecibos)) + 
                            "000000" + "00000000000000000" + FuncoesGlobais.StrZero("0", 46) + FuncoesGlobais.Space(8) + FuncoesGlobais.Space(117);

            String output = cdgcom + nrores + tporgt + brantl + qtdrlt + brcolt;
            filler.Print(output + LF);

            ctalinhas += 1;
            
            // trailer arquivo remessa
            String cgdcop = _banco; 
            String nrolte = "9999";
            String tpregi = "9";
            String brcoat = FuncoesGlobais.Space(9);
            String qtdlaq = "000001"; // quantidade de lotes do arquivo
            String qtdrga = FuncoesGlobais.StrZero(String.valueOf(ctalinhas), 6); //"000000"; // quantidade reg do arquivo tipo=0+1+2+3+5+9
            String brcalt = FuncoesGlobais.Space(211);

            output = cgdcop + nrolte + tpregi + brcoat + qtdlaq + qtdrga + brcalt;
            filler.Print(output + LF);
        }        
        filler.Close();        
        
        JOptionPane.showMessageDialog(null, "Arquivo de remessa " + bco.getBanco() + fileName + nroLote + ".rem" + " gerado com sucesso!!!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
    }    
}