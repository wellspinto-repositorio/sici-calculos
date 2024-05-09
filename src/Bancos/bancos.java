package Bancos;

import Funcoes.*;
import Protocolo.Calculos;
import Protocolo.DepuraCampos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;

public class bancos {    
    static Db conn = null;

    static private String Agencia = "0000";
    static private String ContaCed = "000000";
    static private String CtaDv = "0";
    static private String Banco = "000";
    static private String BancoDv = "0";
    static private String Cart = "000";
    static private String Moeda = "9";
    static private String Tarifa = "6,00";
    static private String Nnumero = "0000000000";
    static private String Identificacao = "000000000";
    static private String IdentDv = "0";
    static private String Logo = "resources/logoBancos/033.jpg";

    public bancos(Db conn) {
        this.conn = conn; 
    }
    
    static public void setAgencia(String value) {
        Agencia = value;
    }
    static public String getAgencia() {
        return Agencia;
    }

    static public void setConta(String value) {
        ContaCed = value;
    }
    static public String getConta() {
        return ContaCed;
    }

    static public void setCtaDv(String value) {
        CtaDv = value;
    }
    static public String getCtaDv() {
        return CtaDv;
    }

    static public void setBanco(String value) {
        Banco = value;
    }
    static public String getBanco() {
        return Banco;
    }

    static public void setBancoDv(String value) {
        BancoDv = value;
    }
    static public String getBancoDv() {
        return BancoDv;
    }

    static public void setCarteira(String value) {
        Cart = value;
    }
    static public String getCarteira() {
        return Cart;
    }

    static public void setMoeda(String value) {
        Moeda = value;
    }
    static public String getMoeda() {
        return Moeda;
    }

    static public void setTarifa(String value) {
        Tarifa = value;
    }
    static public String getTarifa() {
        return Tarifa;
    }

    static public void setNnumero(String value) {
        Nnumero = value;
    }
    static public String getNnumero() {
        return Nnumero;
    }

    static public String getIdentificacao() {
        return Identificacao;
    }
    static public void setIdentificacao(String value) {
        Identificacao = value;
    }
    
    static public String getIdentDv() {
        return IdentDv;
    }
    static public void setIdentDv(String value) {
        IdentDv = value;
    }

    static public void setLogo(String value) {
        Logo = "resources/logoBancos/" + value + ".jpg";
    }    
    static public String getLogo() {
        return Logo;
    }    
    
    static public String CalcDig10(String cadeia) {
        int mult; int total; int res; int pos;
        mult = (cadeia.length() % 2);
        mult += 1; total = 0;
        for (pos=0;pos<=cadeia.length()-1;pos++) {
            res = Integer.parseInt(cadeia.substring(pos, pos + 1)) * mult;
            if (res > 9) { res = (res / 10) + (res % 10); }
            total += res;
            if (mult == 2) { mult =1; } else mult = 2;
        }
        total = ((10 - (total % 10)) % 10);
        return  String.valueOf(total);
    }
    
    static public String CalcDig11(String cadeia, int limitesup, int lflag) {

        int mult; int total; int nresto; int ndig; int pos;
        String retorno = "";
        
        mult = 1 + (cadeia.length() % (limitesup - 1));
        if (mult == 1) { mult = limitesup; }
        
        total = 0;
        for (pos=0;pos<=cadeia.length()-1;pos++) {
            total += Integer.parseInt(cadeia.substring(pos, pos + 1)) * mult;
            mult -= 1;
            if (mult == 1) mult = limitesup;
        }
        
        nresto = (total % 11);
        if (lflag == 1) { retorno = String.valueOf(nresto); } else {
            if (nresto == 0 || nresto == 1) {
                ndig = 0; 
            } else if (nresto > 9) { 
                ndig = 1; 
            } else ndig = 11 - nresto;
            retorno = String.valueOf(ndig);
        }
        return retorno;
    }

    static public String Valor4Boleta(String valor) {
        String valor1 = "0000000000" + valor.replace(" ", "").replace(",", "").replace(".", "").replace("-", "");
        return valor1.substring(valor1.length() - 10, valor1.length());
    }    

    static public void ReadBanco() {
        try {setAgencia(conn.ReadParameters("AGENCIA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setConta(conn.ReadParameters("CONTACED").trim());} catch (Exception err) {err.printStackTrace();}
        try {setCtaDv(conn.ReadParameters("CTADV").trim());} catch (Exception err) {err.printStackTrace();}
        try {setBanco(conn.ReadParameters("BANCO").trim());} catch (Exception err) {err.printStackTrace();}
        try {setCarteira(conn.ReadParameters("CARTEIRA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setMoeda(conn.ReadParameters("MOEDA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setTarifa(conn.ReadParameters("TARIFA").trim());} catch (Exception err) {err.printStackTrace();}
        try {setLogo(conn.ReadParameters("BANCO").trim());} catch (Exception err) {err.printStackTrace();}
    }
    
    static public void SaveBanco() {
        try {conn.SaveParameters(new String[] {"AGENCIA", getAgencia(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.SaveParameters(new String[] {"CONTACED", getConta(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.SaveParameters(new String[] {"CTADV", getCtaDv(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.SaveParameters(new String[] {"BANCO", getBanco(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.SaveParameters(new String[] {"CARTEIRA", getCarteira(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.SaveParameters(new String[] {"MOEDA", getMoeda(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
        try {conn.SaveParameters(new String[] {"TARIFA", getTarifa(), "TEXTO"});} catch (Exception err) {err.printStackTrace();}
    }
    
    static public void LerBanco(String contrato) {
        Object[][] bancobol = null;
        try {
            String[] fields = new String[] {"bcobol"};
            bancobol = conn.ReadFieldsTable(fields, "locatarios", "contrato = :contrato", new Object[][] {{"string","contrato", contrato}});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bancobol != null) {
            Object[][] dadosBol = null;
            try {
                dadosBol = conn.ReadFieldsTable(
                        new String[] {"agencia","conta","conta_dv","nbanco","nbancodv","carteira","moeda","tarifa","nnumero","identificacao", "identdv"}, 
                        "contas_boletas", 
                        "Trim(nbanco) = '" + bancobol[0][3].toString().trim() + "'"
                        );
            } catch (Exception e) {e.printStackTrace();}
            
            if (dadosBol != null) {
                try {setAgencia(dadosBol[0][3].toString().trim());} catch (Exception err) {}
                try {setConta(dadosBol[1][3].toString().trim());} catch (Exception err) {}
                try {setCtaDv(dadosBol[2][3].toString().trim());} catch (Exception err) {}
                try {setBanco(dadosBol[3][3].toString().trim());} catch (Exception err) {}
                try {setBancoDv(dadosBol[4][3].toString().trim());} catch (Exception err) {}
                try {setCarteira(dadosBol[5][3].toString().trim());} catch (Exception err) {}
                try {setMoeda(dadosBol[6][3].toString().trim());} catch (Exception err) {}
                try {setTarifa(dadosBol[7][3].toString().trim());} catch (Exception err) {}
                try {setLogo(dadosBol[3][3].toString().trim());} catch (Exception err) {}
                
                try {if (dadosBol[9][3] != null) setIdentificacao(dadosBol[9][3].toString().trim());} catch (Exception err) {}
                try {if (dadosBol[10][3] != null) setIdentDv(dadosBol[10][3].toString().trim());} catch (Exception err) {}
                
                try {setNnumero(dadosBol[8][3].toString().trim());} catch (Exception err) {}
            }
        }
    }
    
    static public void GravarNnumero(String nbanco, String Value) {
        Object[][] dadosBol = null; double oldnnumero = -1;
        try { dadosBol = conn.ReadFieldsTable( new String[] {"nnumero"}, "contas_boletas", "Trim(nbanco) = '" + nbanco.trim() + "'"); } catch (Exception e) {e.printStackTrace();}
        if (dadosBol != null) {
            oldnnumero = Double.parseDouble(dadosBol[0][3].toString());
        } else {
            JOptionPane.showMessageDialog(null, "Houve um problema ao ler nnumero!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
            System.exit(1);
        }
        
        if (Double.parseDouble(Value) > oldnnumero) {
            String sql = "UPDATE contas_boletas SET nnumero = '" + Value + "' WHERE Trim(nbanco) = '" + nbanco + "';";
            try { conn.CommandExecute(sql, null);} catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Não consegui gravar Nnumero!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
                System.exit(1);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nnumero não coerente!!!\nContacte o administrador do sistema.\nTel.:(21)2701-0261 / 98552-1405");
            System.exit(1);
        }
    }

    static public void LerBancoAvulso(String banco) {
        Object[][] dadosBol = null;
        try {
            dadosBol = conn.ReadFieldsTable(
                    new String[] {"agencia","conta","conta_dv","nbanco","carteira","moeda","tarifa","nnumero","identificacao","identdv"}, 
                    "contas_boletas", 
                    "Trim(nbanco) = '" + banco.trim() + "'"
                    );
        } catch (Exception e) {e.printStackTrace();}

        if (dadosBol != null) {
            try {setAgencia(dadosBol[0][3].toString().trim());} catch (Exception err) {}
            try {setConta(dadosBol[1][3].toString().trim());} catch (Exception err) {}
            try {setCtaDv(dadosBol[2][3].toString().trim());} catch (Exception err) {}
            try {setBanco(dadosBol[3][3].toString().trim());} catch (Exception err) {}
            try {setCarteira(dadosBol[4][3].toString().trim());} catch (Exception err) {}
            try {setMoeda(dadosBol[5][3].toString().trim());} catch (Exception err) {}
            try {setTarifa(dadosBol[6][3].toString().trim());} catch (Exception err) {}
            try {setLogo(dadosBol[3][3].toString().trim());} catch (Exception err) {}
            
            try {if (dadosBol[8][3] != null) setIdentificacao(dadosBol[8][3].toString().trim());} catch (Exception err) {}
            try {if (dadosBol[9][3] != null) setIdentDv(dadosBol[9][3].toString().trim());} catch (Exception err) {}
            
            try {setNnumero(dadosBol[7][3].toString().trim());} catch (Exception err) {}
        }
    }
    
    static public String fmtNumero(String value) {
        String numero = "000000000000000";
        value = value.substring(0, value.indexOf(",") + 3);
        String saida = (numero + rmvNumero(value)).trim();
        return saida.substring(saida.length() - 15);
    }
    
    static public String rmvNumero(String value) {
        String ret = "";
        for (int i=0;i<value.length();i++) {
            if (value.substring(i, i + 1).equalsIgnoreCase(".") || value.substring(i, i + 1).equalsIgnoreCase("/") || value.substring(i, i + 1).equalsIgnoreCase("-") || value.substring(i, i + 1).equalsIgnoreCase(",") || value.substring(i, i + 1).equalsIgnoreCase(" ")){
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }

    static public String rmvLetras(String value) {
        String ret = "";
        for (int i=0; i<value.length();i++) {
            char letra = value.charAt(i);
            if (value.substring(i, i + 1).equalsIgnoreCase(":")) {
                //
            } else if ((int)letra < 48 || (int)letra > 57) {                  
                //
            } else {
                ret += value.substring(i, i + 1);
            }
        }
        return ret;
    }

    static public String[][] Recalcula(String rgprp, String rgimv, String contrato, String vencimento) {
        String[][] linhas = null;
        try {
            linhas = MontaTela2(rgprp, rgimv, contrato, vencimento);
        } catch (Exception ex) {} 

        return linhas;
    }
    
    static public String[][] MontaTela2(String rgprp, String rgimv, String contrato, String vecto) throws SQLException, bsh.ParseException {
        String sql = "SELECT * FROM recibo WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.OpenTable(sql, null);

        String[][] linhas = null;
        if (pResult.first()) {
            DepuraCampos a = new DepuraCampos(pResult.getString("campo"));
            VariaveisGlobais.ccampos = pResult.getString("campo");
            linhas = new String[14][3];

            a.SplitCampos();
            // Ordena Matriz
            Arrays.sort(a.aCampos, new Comparator()
            {
            private int pos1 = 3;
            private int pos2 = 4;
            @Override
            public int compare(Object o1, Object o2) {
                String p1 = ((String)o1).substring(pos1, pos2);
                String p2 = ((String)o2).substring(pos1, pos2);
                return p1.compareTo(p2);
            }
            });

            // Monta campos
            for (int i=0; i<= a.length() - 1; i++) {
                String[] Campo = a.Depurar(i);
                if (Campo.length > 0) {
                    //MontaCampos(Campo, i);
                    linhas[i][0] = Campo[0];
                    linhas[i][1] = ("00/00".equals(Campo[3]) || "00/0000".equals(Campo[3]) || "".equals(Campo[3]) ? "-" : Campo[3]) ;
                    linhas[i][2] = Campo[1];
                }
            }
        }

        conn.CloseTable(pResult);

        return linhas;
    }
    
    static public float[] CalcularRecibo(String rgprp, String rgimv, String contrato, String vecto) {
        if ("".equals(vecto.trim())) { return null; }

        Calculos rc = new Calculos();
        try {
            rc.Inicializa(rgprp, rgimv, contrato);
        } catch (SQLException ex) {}

        String campo = ""; String rcampo = ""; boolean mCartVazio = false;
        String sql = "SELECT * FROM recibo WHERE contrato = '" + contrato + "' AND dtvencimento = '" + Dates.DateFormata("yyyy-MM-dd", Dates.StringtoDate(vecto, "dd/MM/yyyy")) + "';";
        ResultSet pResult = conn.OpenTable(sql, null);
        try {
            if (pResult.first()) {
                campo = pResult.getString("campo");
                rcampo = campo;
                mCartVazio = ("".equals(rcampo.trim()));
            }
        } catch (SQLException ex) { rcampo = ""; }
        conn.CloseTable(pResult);

        float exp = 0, multa = 0, juros = 0, correcao = 0;

        try { exp = rc.TaxaExp(campo); } catch (SQLException ex) {}
        try { multa = rc.Multa(campo, vecto, false); } catch (SQLException ex) {}
        try { juros = rc.Juros(campo, vecto); } catch (SQLException ex) {}
        try { correcao = rc.Correcao(campo, vecto); } catch (SQLException ex) {}

        float tRecibo = 0;
        tRecibo = Calculos.RetValorCampos(campo);
        tRecibo += exp + multa + juros + correcao;

        float[] retorno = new float[5];
        retorno[0] = exp; retorno[1] = multa; retorno[2] = juros; retorno[3] = correcao; retorno[4] = tRecibo;
        return retorno;
    }
    
    static public int AchaVazio(String[][] value) {
        int r = -1;

        for (int i=0;i<value.length;i++) {
            if (value[i][0] == null || "".equals(value[i][0])) {r = i; break;}
        }

        return r;
    }

    static public String SoNumeroSemZerosAEsq(String numero) {
        String Retorno = "";
        for (int i=0;i<numero.length();i++) {
            if (!numero.substring(i,i+1).equalsIgnoreCase("0")) {
                Retorno += numero.substring(i);
                break;
            }
        }
        return Retorno;
    }
    
    static public cStructure[][] Strutura(String banco) {
        // REGISTRO HEADER DO ARQUIVO
        cStructure[] header = {};
        String structSQL = "SELECT id, campo, inicial, final, " + 
                        "an, tamanho,decimais, notas FROM bancos_struc " + 
                        "WHERE banco = :banco AND tipo = :tipo AND " + 
                        "tparq = :tparq ORDER BY id;";
        ResultSet rs = conn.OpenTable(structSQL, new Object[][] {
            {"string", "banco", banco},
            {"string", "tipo", "RET"},
            {"string", "tparq", "REGISTRO HEADER DO ARQUIVO"}
        });
        
        int _id = -1; String _banco = "033"; String _tipo = "RET";
        String _tparq = "REGISTRO HEADER DO ARQUIVO";
        String _campo = ""; int _inicial = -1, _final = -1, _tamanho = -1, _decimais = -1;
        String _an = "", _notas = "";
        try {
            while (rs.next()) {
                try { _id = rs.getInt("id"); } catch (SQLException exSQL) { _id = -1; }
                try { _campo = rs.getString("campo"); } catch (SQLException exSQL) { _campo = ""; }
                try { _inicial = rs.getInt("inicial"); } catch (SQLException exSQL) { _inicial = -1; }
                try { _final = rs.getInt("final"); } catch (SQLException exSQL) { _final = -1; }
                try { _an = rs.getString("an"); } catch (SQLException exSQL) { _an = ""; }
                try { _tamanho = rs.getInt("tamanho"); } catch (SQLException exSQL) { _tamanho = -1; }
                try { _decimais = rs.getInt("decimais"); } catch (SQLException exSQL) { _decimais = -1; }
                try { _notas = rs.getString("notas"); } catch (SQLException exSQL) { _notas = ""; }
                
                header = elementoAdd(header, new cStructure(_id, _banco, _tipo, _tparq, _campo, _inicial,
                        _final, _an, _tamanho, _decimais, _notas, null));
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        
        cStructure[] headerLote = {};
        rs = conn.OpenTable(structSQL, new Object[][] {
            {"string", "banco", banco},
            {"string", "tipo", "RET"},
            {"string", "tparq", "REGISTRO HEADER DO LOTE"}
        });
        _tparq = "REGISTRO HEADER DO LOTE";
        try {
            while (rs.next()) {
                try { _id = rs.getInt("id"); } catch (SQLException exSQL) { _id = -1; }
                try { _campo = rs.getString("campo"); } catch (SQLException exSQL) { _campo = ""; }
                try { _inicial = rs.getInt("inicial"); } catch (SQLException exSQL) { _inicial = -1; }
                try { _final = rs.getInt("final"); } catch (SQLException exSQL) { _final = -1; }
                try { _an = rs.getString("an"); } catch (SQLException exSQL) { _an = ""; }
                try { _tamanho = rs.getInt("tamanho"); } catch (SQLException exSQL) { _tamanho = -1; }
                try { _decimais = rs.getInt("decimais"); } catch (SQLException exSQL) { _decimais = -1; }
                try { _notas = rs.getString("notas"); } catch (SQLException exSQL) { _notas = ""; }
                
                headerLote = elementoAdd(headerLote, new cStructure(_id, _banco, _tipo, _tparq, _campo, _inicial,
                        _final, _an, _tamanho, _decimais, _notas, null));
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        
        cStructure[] segmentoT = {};
        rs = conn.OpenTable(structSQL, new Object[][] {
            {"string", "banco", banco},
            {"string", "tipo", "RET"},
            {"string", "tparq", "REGISTRO DETALHE SEGMENTO T"}
        });
        _tparq = "REGISTRO DETALHE SEGMENTO T";
        try {
            while (rs.next()) {
                try { _id = rs.getInt("id"); } catch (SQLException exSQL) { _id = -1; }
                try { _campo = rs.getString("campo"); } catch (SQLException exSQL) { _campo = ""; }
                try { _inicial = rs.getInt("inicial"); } catch (SQLException exSQL) { _inicial = -1; }
                try { _final = rs.getInt("final"); } catch (SQLException exSQL) { _final = -1; }
                try { _an = rs.getString("an"); } catch (SQLException exSQL) { _an = ""; }
                try { _tamanho = rs.getInt("tamanho"); } catch (SQLException exSQL) { _tamanho = -1; }
                try { _decimais = rs.getInt("decimais"); } catch (SQLException exSQL) { _decimais = -1; }
                try { _notas = rs.getString("notas"); } catch (SQLException exSQL) { _notas = ""; }
                
                segmentoT = elementoAdd(segmentoT, new cStructure(_id, _banco, _tipo, _tparq, _campo, _inicial,
                        _final, _an, _tamanho, _decimais, _notas, null));
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        
        cStructure[] segmentoU = {};
        rs = conn.OpenTable(structSQL, new Object[][] {
            {"string", "banco", banco},
            {"string", "tipo", "RET"},
            {"string", "tparq", "REGISTRO DETALHE SEGMENTO U"}
        });
        _tparq = "REGISTRO DETALHE SEGMENTO U";
        try {
            while (rs.next()) {
                try { _id = rs.getInt("id"); } catch (SQLException exSQL) { _id = -1; }
                try { _campo = rs.getString("campo"); } catch (SQLException exSQL) { _campo = ""; }
                try { _inicial = rs.getInt("inicial"); } catch (SQLException exSQL) { _inicial = -1; }
                try { _final = rs.getInt("final"); } catch (SQLException exSQL) { _final = -1; }
                try { _an = rs.getString("an"); } catch (SQLException exSQL) { _an = ""; }
                try { _tamanho = rs.getInt("tamanho"); } catch (SQLException exSQL) { _tamanho = -1; }
                try { _decimais = rs.getInt("decimais"); } catch (SQLException exSQL) { _decimais = -1; }
                try { _notas = rs.getString("notas"); } catch (SQLException exSQL) { _notas = ""; }
                
                segmentoU = elementoAdd(segmentoU, new cStructure(_id, _banco, _tipo, _tparq, _campo, _inicial,
                        _final, _an, _tamanho, _decimais, _notas, null));
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        
        cStructure[] trailerLote = {};
        rs = conn.OpenTable(structSQL, new Object[][] {
            {"string", "banco", banco},
            {"string", "tipo", "RET"},
            {"string", "tparq", "TRAILER DE LOTE"}
        });
        _tparq = "TRAILER DE LOTE";
        try {
            while (rs.next()) {
                try { _id = rs.getInt("id"); } catch (SQLException exSQL) { _id = -1; }
                try { _campo = rs.getString("campo"); } catch (SQLException exSQL) { _campo = ""; }
                try { _inicial = rs.getInt("inicial"); } catch (SQLException exSQL) { _inicial = -1; }
                try { _final = rs.getInt("final"); } catch (SQLException exSQL) { _final = -1; }
                try { _an = rs.getString("an"); } catch (SQLException exSQL) { _an = ""; }
                try { _tamanho = rs.getInt("tamanho"); } catch (SQLException exSQL) { _tamanho = -1; }
                try { _decimais = rs.getInt("decimais"); } catch (SQLException exSQL) { _decimais = -1; }
                try { _notas = rs.getString("notas"); } catch (SQLException exSQL) { _notas = ""; }
                
                trailerLote = elementoAdd(trailerLote, new cStructure(_id, _banco, _tipo, _tparq, _campo, _inicial,
                        _final, _an, _tamanho, _decimais, _notas, null));
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        
        cStructure[] trailerArquivo = {};
        rs = conn.OpenTable(structSQL, new Object[][] {
            {"string", "banco", banco},
            {"string", "tipo", "RET"},
            {"string", "tparq", "TRAILER DE ARQUIVO"}
        });
        _tparq = "TRAILER DE ARQUIVO";
        try {
            while (rs.next()) {
                try { _id = rs.getInt("id"); } catch (SQLException exSQL) { _id = -1; }
                try { _campo = rs.getString("campo"); } catch (SQLException exSQL) { _campo = ""; }
                try { _inicial = rs.getInt("inicial"); } catch (SQLException exSQL) { _inicial = -1; }
                try { _final = rs.getInt("final"); } catch (SQLException exSQL) { _final = -1; }
                try { _an = rs.getString("an"); } catch (SQLException exSQL) { _an = ""; }
                try { _tamanho = rs.getInt("tamanho"); } catch (SQLException exSQL) { _tamanho = -1; }
                try { _decimais = rs.getInt("decimais"); } catch (SQLException exSQL) { _decimais = -1; }
                try { _notas = rs.getString("notas"); } catch (SQLException exSQL) { _notas = ""; }
                
                trailerArquivo = elementoAdd(trailerArquivo, new cStructure(_id, _banco, _tipo, _tparq, _campo, _inicial,
                        _final, _an, _tamanho, _decimais, _notas, null));
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(rs);
        
        cStructure[][] retorno = {};
        retorno = elementosAdd(retorno, header);
        retorno = elementosAdd(retorno, headerLote);
        retorno = elementosAdd(retorno, segmentoT);
        retorno = elementosAdd(retorno, segmentoU);
        retorno = elementosAdd(retorno, trailerLote);
        retorno = elementosAdd(retorno, trailerArquivo);
                
        return retorno;
    }    
    
    static public String[] loadReturnFile(String fileName) {
        String[] Linhas = {};
        BufferedReader reader = null;
        try {
            FileInputStream stream = new FileInputStream(fileName);
            InputStreamReader ireader = new InputStreamReader(stream);
            reader = new BufferedReader(ireader);

            String linha = reader.readLine();
            while (linha != null) {
                Linhas = FuncoesGlobais.ArrayAdd(Linhas, linha);
                linha = reader.readLine();
            }   
        } catch (IOException ioEx) {} finally {       
            try {reader.close();} catch (IOException oiEx) {}
        }
        
        return Linhas;
    }
    
    static public cStructure[] elementoAdd(cStructure[] mArray, cStructure value) {
        cStructure[] temp = new cStructure[mArray.length+1];

        System.arraycopy(mArray,0,temp,0,mArray.length);

        temp[mArray.length] = value;

        return temp;
    }
    
    static public cStructure[][] elementosAdd(cStructure[][] mArray, cStructure[] value) {
        cStructure[][] temp = new cStructure[mArray.length + 1][value.length];
        System.arraycopy(mArray, 0, temp, 0, mArray.length);

        for (int i=0; i<value.length;i++) {
            temp[mArray.length][i] = value[i];
        }
        return temp;
    }    
    
    static public cStructure buscaElementos(List<cStructure> value, int id) {
        cStructure retorno = null;
        for (cStructure item : value) {
            if (item.getId() == id) {
                retorno = item;
                break;
            }
        }
                
        return retorno;
    }
    
    static public cStructure buscaElementos(cStructure[] value, int id) {
        cStructure retorno = null;
        for (int p = 0; p <= value.length - 1; p++) {
            if (value[p].getId() == id) {
                retorno = value[p];
                break;
            }
        }
        
        return retorno;
    }
    
    static public String fmtData(String value) {
        return value.substring(0,2) + "-" + value.substring(2,4) + "-" + value.substring(4);
    }    
    
    static public String fmtValue(String value) {
        String Inteiro = value.substring(0,value.length() - 2);
        String Decimal = value.substring(value.length() - 2);

        float Valor = Float.parseFloat(Inteiro) + (Float.valueOf(Decimal) / 100);

        DecimalFormat v = new DecimalFormat();
        v.applyPattern("#,##0.00");

        return v.format(Valor);        
    }
    
    static public String fmtSeuNumero(String value) {
        return String.valueOf(Double.parseDouble(value)).replace(".0", "");
    }
    
    static public String fmtNossoNumero(String nnumero, String dac) {
        return String.valueOf(Double.parseDouble(nnumero)).replace(".0", "") + "-" + String.valueOf(Double.parseDouble(dac)).replace(".0", "");
    }
    
    static int getPoscRetorno(String banco, String tipoarq, String campo) {
        String strucPosSQL = "SELECT posicao FROM bancos_struc_pos WHERE banco = :banco AND tipoarq = :tipoarq AND campo = :campo LIMIT 1;";
        ResultSet prs = conn.OpenTable(strucPosSQL, new Object[][] {
            {"string", "banco", banco},
            {"string", "tipoarq", tipoarq},
            {"string", "campo", campo}
        });
        
        int posicao = -1;
        try {
            while (prs.next()) {
                posicao = prs.getInt("posicao");
            }
        } catch (SQLException sqlEx) {}
        conn.CloseTable(prs);
        
        return posicao;
    }
    
    static public cRetorno retorno(String fileName, String banco) {
        if (!new File(fileName).exists()) {
            JOptionPane.showMessageDialog(null, "Arquivo inexistente!");
            return null;
        }        
        
        // Ler o arquivo de retorno do banco
        final String[] Linhas = loadReturnFile(fileName);

        // 
        final cStructure[][] structure = Strutura(banco);
        
        // Inicializa Listas
        cStructure[] header = structure[0];
        cStructure[] headerLote = structure[1];
        cStructure[] segmentoT = structure[2];
        cStructure[] segmentoU = structure[3];
        cStructure[] trailerLote = structure[4];
        cStructure[] trailerArquivo = structure[5];
        
        // REGISTRO DO ARQUIVO
        for (cStructure item : header) {
            int _inicial = item.getInicial();
            int _final = item.getFinalizacao();
            int _tamanho = item.getTamanho();
            int _decimais = item.getDecimais();
            String _an = item.getAn();
            
            String _value = Linhas[0].substring(_inicial - 1, _final);
            item.setValue(_value);
        }
               
        // REGISTRO DO HEADER DE LOTE
        for (cStructure item : headerLote) {
            int _inicial = item.getInicial();
            int _final = item.getFinalizacao();
            int _tamanho = item.getTamanho();
            int _decimais = item.getDecimais();
            String _an = item.getAn();
            
            String _value = Linhas[1].substring(_inicial - 1, _final);
            item.setValue(_value);
        }

        cStructure[][] _segmentsT = {}; cStructure[][] _segmentsU = {};
        for (int linha = 2; linha <= Linhas.length - 2; linha++) {
            String _segmentos = Linhas[linha].substring(13,14);            

            cStructure[] _segmentosT = {}; 
            if (_segmentos.equalsIgnoreCase("T")) {
                // SEGMENTO T
                for (int i = 0; i <= segmentoT.length - 1; i++) {
                    int _id = segmentoT[i].getId();
                    String _bank = segmentoT[i].getBanco();
                    String _tipo = segmentoT[i].getTipo();
                    String _campo = segmentoT[i].getCampo();                    
                    int _inicial = segmentoT[i].getInicial();
                    int _final = segmentoT[i].getFinalizacao();
                    String _an = segmentoT[i].getAn();                   
                    int _tamanho = segmentoT[i].getTamanho();
                    int _decimais = segmentoT[i].getDecimais();
                    String _notas = segmentoT[i].getNotas();
                    Object _value = segmentoT[i].getValue();
                    
                    String _newvalue = Linhas[linha].substring(_inicial - 1, _final);
                    _value = _newvalue;
                    
                    _segmentosT = elementoAdd(_segmentosT, 
                            new cStructure(
                                    _id, 
                                    _bank,
                                    _tipo,
                                    _an,
                                    _campo,
                                    _inicial,
                                    _final, 
                                    _an, 
                                    _tamanho, 
                                    _decimais, 
                                    _notas, 
                                    _value
                            )
                    );
                }
                
                _segmentsT = elementosAdd(_segmentsT, _segmentosT);
            }
            
            cStructure[] _segmentosU = {}; 
            if (_segmentos.equalsIgnoreCase("U")) {
                // SEGMENTO U
                for (int i = 0; i <= segmentoU.length - 1; i++) {
                    int _id = segmentoU[i].getId();
                    String _bank = segmentoU[i].getBanco();
                    String _tipo = segmentoU[i].getTipo();
                    String _campo = segmentoU[i].getCampo();                    
                    int _inicial = segmentoU[i].getInicial();
                    int _final = segmentoU[i].getFinalizacao();
                    String _an = segmentoU[i].getAn();                   
                    int _tamanho = segmentoU[i].getTamanho();
                    int _decimais = segmentoU[i].getDecimais();
                    String _notas = segmentoU[i].getNotas();
                    Object _value = segmentoU[i].getValue();
                    
                    String _newvalue = Linhas[linha].substring(_inicial - 1, _final);
                    _value = _newvalue;
                    
                    _segmentosU = elementoAdd(_segmentosU, 
                            new cStructure(
                                    _id, 
                                    _bank,
                                    _tipo,
                                    _an,
                                    _campo,
                                    _inicial,
                                    _final, 
                                    _an, 
                                    _tamanho, 
                                    _decimais, 
                                    _notas, 
                                    _value
                            )
                    );
                }                
                _segmentsU = elementosAdd(_segmentsU, _segmentosU);
            }
        }
        
        // REGISTRO DO TRAILER DE LOTE
        for (cStructure item : trailerLote) {
            int _inicial = item.getInicial();
            int _final = item.getFinalizacao();
            int _tamanho = item.getTamanho();
            int _decimais = item.getDecimais();
            String _an = item.getAn();
            
            String _value = Linhas[Linhas.length - 2].substring(_inicial - 1, _final);
            item.setValue(_value);
        }
               
        // REGISTRO DO TRAILER DE ARQUIVO
        for (cStructure item : trailerArquivo) {
            int _inicial = item.getInicial();
            int _final = item.getFinalizacao();
            int _tamanho = item.getTamanho();
            int _decimais = item.getDecimais();
            String _an = item.getAn();
            
            String _value = Linhas[Linhas.length - 1].substring(_inicial - 1, _final);
            item.setValue(_value);
        }
              
        List<cSegmentoT> _Tsegmento = new ArrayList();
        List<cSegmentoU> _Usegmento = new ArrayList();        
        for (int pos = 0; pos <= _segmentsT.length - 1; pos++) {
            _Tsegmento.add(new cSegmentoT(
                    buscaElementos(_segmentsT[pos],getPoscRetorno(banco,"segmentoT","IDENTIFICAÇÃO DA OCORRÊNCIA")).getValue().toString(),                   // IDENTIFICAÇÃO DA OCORRÊNCIA
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","IDENTIFICAÇÃO DO TÍTULO NO BANCO NOSSO NÚMERO")).getValue().toString(),// IDENTIFICAÇÃO DO TÍTULO NO BANCO NOSSO NÚMERO
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","DÍGITO DE AUTO-CONFERÊNCIA NOSSO NÚMERO")).getValue().toString(),   // DÍGITO DE AUTO-CONFERÊNCIA NOSSO NÚMERO
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","NÚMERO DOCUMENTO DE COBRANÇA SEU NÚMERO")).getValue().toString(),    // N.º DOCUMENTO DE COBRANÇA SEU NÚMERO
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","DATA DO VENCIMENTO DO TÍTULO")).getValue().toString(),              // DATA DO VENCIMENTO DO TÍTULO
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","VALOR NOMINAL DO TÍTULO")).getValue().toString(),                     // VALOR NOMINAL DO TÍTULO
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","AG. COBRADORA, AG. DE LIQUIDAÇÃO OU BAIXA")).getValue().toString(),    // AG. COBRADORA, AG. DE LIQUIDAÇÃO OU BAIXA
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","DÍGITO AUTO-CONFERÊNCIA AGÊNCIA COBRADORA")).getValue().toString(), // DÍGITO AUTO-CONFERÊNCIA AGÊNCIA COBRADORA
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","TIPO DE INSCRIÇÃO DO PAGADOR")).getValue().toString(),            // TIPO DE INSCRIÇÃO DO PAGADOR
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","NÚMERO DE INSCRIÇÃO DO PAGADOR")).getValue().toString(),            // NÚMERO DE INSCRIÇÃO DO PAGADOR
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","NOME DO PAGADOR")).getValue().toString(),                           // NOME DO PAGADOR
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","TARIFAS/CUSTAS")).getValue().toString(),                               // TARIFAS/CUSTAS
                    "",                                                                                                                                                                  // REJEIÇÃO
                    buscaElementos(_segmentsT[pos], getPoscRetorno(banco,"segmentoT","LIQUIDAÇÃO")).getValue().toString()                               // LIQUIDAÇÃO
            ));
            
            _Usegmento.add(new cSegmentoU(
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","IDENTIFICAÇÃO DA OCORRÊNCIA")).getValue().toString(),        // IDENTIFICAÇÃO DA OCORRÊNCIA
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","JUROS DE MORA/MULTA")).getValue().toString(),             // JUROS DE MORA/MULTA
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","VALOR DO DESCONTO CONCEDIDO")).getValue().toString(),       // VALOR DO DESCONTO CONCEDIDO
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","VALOR DO ABATIMENTO CONCEDIDO")).getValue().toString(),   // VALOR DO ABATIMENTO CONCEDIDO
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","VALOR DA OCORRÊNCIA DO PAGADOR")).getValue().toString(),   // VALOR DA OCORRÊNCIA DO PAGADOR
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","VALOR LANÇADO EM CONTA CORRENTE")).getValue().toString(),  // VALOR LANÇADO EM CONTA CORRENTE
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","DATA DA OCORRÊNCIA DO PAGADOR")).getValue().toString(),    // DATA DA OCORRÊNCIA DO PAGADOR
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","DATA DE CRÉDITO DESTA LIQUIDAÇÃO")).getValue().toString(),// DATA DE CRÉDITO DESTA LIQUIDAÇÃO
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","CÓDIGO DA OCORRÊNCIA DO PAGADOR")).getValue().toString(),// CÓDIGO DA OCORRÊNCIA DO PAGADOR
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","DATA DA OCORRÊNCIA DO PAGADOR")).getValue().toString(),     // DATA DA OCORRÊNCIA DO PAGADOR
                    buscaElementos(_segmentsU[pos], getPoscRetorno(banco,"segmentoU","VALOR DA OCORRÊNCIA DO PAGADOR")).getValue().toString()   // VALOR DA OCORRÊNCIA DO PAGADOR
            ));            
        }
        
        cRetorno retorno = new cRetorno(
            buscaElementos(header, getPoscRetorno(banco,"header","NÚMERO DO BANCO NA CÂMARA DE COMPENSAÇÃO")).getValue().toString(), // NÚMERO DO BANCO NA CÂMARA DE COMPENSAÇÃO
            buscaElementos(header, getPoscRetorno(banco,"header","TIPO DE INSCRIÇÃO DA EMPRESA")).getValue().toString(),           // TIPO DE INSCRIÇÃO DA EMPRESA
            buscaElementos(header, getPoscRetorno(banco,"header","NÚMERO DE INSCRIÇÃO DA EMPRESA")).getValue().toString(),           // NÚMERO DE INSCRIÇÃO DA EMPRESA
            buscaElementos(header, getPoscRetorno(banco,"header","TIPO DE REGISTRO")).getValue().toString(),                      // "TIPO DE REGISTRO"
            buscaElementos(headerLote, getPoscRetorno(banco,"headerLote","DATA DO CRÉDITO")).getValue().toString(),              // DATA DO CRÉDITO
            _Tsegmento,
            _Usegmento,
            buscaElementos(trailerLote, getPoscRetorno(banco,"trailerLote","QUANTIDADE DE REGISTROS DO LOTE")).getValue().toString(),        // QUANTIDADE DE REGISTROS DO LOTE
            buscaElementos(trailerLote, getPoscRetorno(banco,"trailerLote","QTDE. DE TÍTULOS EM COBRANÇA SIMPLES")).getValue().toString(),// QTDE. DE TÍTULOS EM COBRANÇA SIMPLES
            buscaElementos(trailerLote, getPoscRetorno(banco,"trailerLote","QTDE. DE TÍTULOS EM COBRANÇA VINCULADA")).getValue().toString(),// QTDE. DE TÍTULOS EM COBRANÇA VINCULADA
            buscaElementos(trailerLote, getPoscRetorno(banco,"trailerLote","VALOR TOTAL TÍTULOS EM COBRANÇA VINCULADA")).getValue().toString(), // VALOR TOTAL TÍTULOS EM COBRANÇA VINCULADA
            buscaElementos(trailerArquivo, getPoscRetorno(banco,"trailerArquivo","CÓDIGO DO LOTE")).getValue().toString(),                     // CÓDIGO DO LOTE
            buscaElementos(trailerArquivo, getPoscRetorno(banco,"trailerArquivo","QUANTIDADE DE REGISTROS DO ARQUIVO")).getValue().toString()    // QUANTIDADE DE REGISTROS DO ARQUIVO
        );
        
        return header.length > 0 ? retorno : null;
    }    
}
