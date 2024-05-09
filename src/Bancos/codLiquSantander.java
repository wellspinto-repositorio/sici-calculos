package Bancos;

import java.util.HashMap;
import java.util.Map;

public class codLiquSantander {
    private Map<String, String> liquidacao = new HashMap<String,String>();
    private Map<String, String> ocorrencias = new HashMap<String,String>();
    private Map<String, String> entradasrej = new HashMap<String,String>();
    private Map<String, String> alteracaorej = new HashMap<String,String>();
    private Map<String, String> instrucaorej = new HashMap<String,String>();
    private Map<String, String> baixasrej = new HashMap<String,String>();

    public codLiquSantander() {
        // Liquidação
        this.liquidacao = new HashMap<String,String>();
        this.liquidacao.put("01","POR SALDO");
        this.liquidacao.put("02","POR CONTA");
        this.liquidacao.put("03", "NO PROPRIO BANCO");
        this.liquidacao.put("04", "COMPENSAÇÃO ELETRONICA");
        this.liquidacao.put("05", "COMPENSAÇÃO CONVENCIONAL");
        this.liquidacao.put("06", "ARQUIVO MAGNETICO");
        this.liquidacao.put("07", "APOS FERIADO LOCAL");
        this.liquidacao.put("08", "EM CARTORIO");
        this.liquidacao.put("09", "PAGAMENTO PARCIAL");

        // Ocorrencias
        this.ocorrencias = new HashMap<String,String>();
        this.ocorrencias.put("02", "ENTRADA CONFIRMADA");
        this.ocorrencias.put("03", "ENTRADA REJEITADA");
        this.ocorrencias.put("04" ,"TRANSFERENCIA CARTEIRA SIMPLES");
        this.ocorrencias.put("05" ,"TRANSFERENCIA CARTEIRA DESC/PENHOR/VENDOR/FIDC");
        this.ocorrencias.put("06" ,"LIQUIDAÇÃO NORMAL");
        this.ocorrencias.put("08" ,"CONFIRMAÇÃO DO RECEBIMENTO DO CANCELAMENTO DO DESCONTO");
        this.ocorrencias.put("09" ,"BAIXA SIMPLES");
        this.ocorrencias.put("10" ,"BAIXA POR TER SIDO LIQUIDADO");
        this.ocorrencias.put("11" ,"EM SER (SÓ NO RETORNO MENSAL)");
        this.ocorrencias.put("12" ,"ABATIMENTO CONCEDIDO");
        this.ocorrencias.put("13" ,"ABATIMENTO CANCELADO");
        this.ocorrencias.put("14" ,"VENCIMENTO ALTERADO");
        this.ocorrencias.put("17" ,"LIQUIDAÇÃO APOS BAIXA OU TITULO NÃO REGISTRADO");
        this.ocorrencias.put("19" ,"CONFIRMAÇÃO RECEBIMENTO DE INSTRUÇÃO DE PROTESTO");
        this.ocorrencias.put("20" ,"CONFIRMAÇÃO RECEBIMENTO DE INSTRUÇÃO DE SUSTAÇÃO / NAO PROTESTAR");
        this.ocorrencias.put("23" ,"PROTESTO ENVIADO A CARTÓRIO");
        this.ocorrencias.put("24" ,"INSTRUÇÃO DE PROTESTO SUSTADA");
        this.ocorrencias.put("25" ,"PROTESTO E BAIXADO");
        this.ocorrencias.put("26" ,"INSTRUÇÃO REJEITADA");
        this.ocorrencias.put("27" ,"CONFIRMAÇÃO DO PEDIDO DE ALTERAÇÃO DE OUTROS DADOS");
        this.ocorrencias.put("28" ,"DEBITO TARIFA/CUSTAS");
        this.ocorrencias.put("29" ,"OCORRENCIAS DO PAGADOR");
        this.ocorrencias.put("30" ,"ALTERAÇÃO DE DADOS REJEITADA");
        this.ocorrencias.put("32" ,"CODIGO IOF INVALIDO");
        this.ocorrencias.put("51" ,"TITULO DDA RECONHECIDO PELO PAGADOR");
        this.ocorrencias.put("52" ,"TITULO DDA NÃO RECONHECIDO PELO PAGADOR");
        this.ocorrencias.put("53" ,"TITULO DDA RECUSADO PELA CIP");
        this.ocorrencias.put("61" ,"CONFIRMAÇÃO DE ALTERAÇÃO DO VALOR NOMINAL DO TITULO");
        this.ocorrencias.put("91" ,"CONFIRMAÇÃO DE ALTERAÇÃO DO VALOR MINIMO OU PERCENTUAL MINIMO");
        this.ocorrencias.put("92" ,"CONFIRMAÇÃO DE ALTERAÇÃO DO VALOR MAXIMO OU PERCENTUAL MAXIMO");
        this.ocorrencias.put("93" ,"BAIXA OPERACIONAL");
        this.ocorrencias.put("94" ,"CANCELAMENTO DE BAIXA OPERACIONAL");
        this.ocorrencias.put("A4" ,"PAGADOR DDA");
        
    }

    public String getLiquidacao(String key) {
        return liquidacao.get(key).toString();
    }

    public String getOcorrencias(String key) {
        return ocorrencias.get(key).toString();
    }

    public String getEntradasrej(String key) {
        return entradasrej.get(key).toString();
    }

    public String getAlteracaorej(String key) {
        return alteracaorej.get(key).toString();
    }

    public String getInstrucaorej(String key) {
        return instrucaorej.get(key).toString();
    }

    public String getBaixasrej(String key) {
        return baixasrej.get(key).toString();
    }
}
