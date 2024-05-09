package BancosDigital.BancoInter;

import java.math.BigDecimal;
import java.util.Date;

public class classInterConsulta {
    private Date dataEmissao;
    private Date dataVencimento;
    private Date dataPagamento;
    private String seuNumero;
    private String nossoNumero;
    private String cnpjCpfSacado;
    private String nomeSacado;
    private BigDecimal valorMulta;
    private BigDecimal valorJuros;
    private BigDecimal valorNominal;
    private String situacao;
    private boolean baixado;

    public classInterConsulta(Date dataEmissao, 
                              Date dataVencimento, 
                              Date dataPagamento, 
                              String seuNumero, 
                              String nossoNumero, 
                              String cnpjCpfSacado, 
                              String nomeSacado, 
                              BigDecimal valorMulta, 
                              BigDecimal valorJuros,
                              BigDecimal valorNominal,
                              String situacao,
                              boolean baixado) {
        this.dataEmissao = dataEmissao;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.seuNumero = seuNumero;
        this.nossoNumero = nossoNumero;
        this.cnpjCpfSacado = cnpjCpfSacado;
        this.nomeSacado = nomeSacado;
        this.valorMulta = valorMulta;
        this.valorJuros = valorJuros;
        this.valorNominal = valorNominal;
        this.situacao = situacao;
        this.baixado = baixado;
    }

    public Date getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(Date dataEmissao) { this.dataEmissao = dataEmissao; }

    public Date getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(Date dataVencimento) { this.dataVencimento = dataVencimento; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }

    public String getSeuNumero() { return seuNumero; }
    public void setSeuNumero(String seuNumero) { this.seuNumero = seuNumero; }
    
    public String getNossoNumero() { return nossoNumero; }
    public void setNossoNumero(String nossoNumero) { this.nossoNumero = nossoNumero; }

    public String getCnpjCpfSacado() { return cnpjCpfSacado; }
    public void setCnpjCpfSacado(String cnpjCpfSacado) { this.cnpjCpfSacado = cnpjCpfSacado; }

    public String getNomeSacado() { return nomeSacado; }
    public void setNomeSacado(String nomeSacado) { this.nomeSacado = nomeSacado; }

    public BigDecimal getValorMulta() { return valorMulta; }
    public void setValorMulta(BigDecimal valorMulta) { this.valorMulta = valorMulta; }

    public BigDecimal getValorJuros() { return valorJuros; }
    public void setValorJuros(BigDecimal valorJuros) { this.valorJuros = valorJuros; }

    public BigDecimal getValorNominal() { return valorNominal; }
    public void setValorNominal(BigDecimal valorNominal) { this.valorNominal = valorNominal; }
    
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public boolean getBaixado() { return baixado; }
    public void setBaixado(boolean baixado) { this.baixado = baixado; }
    
    @Override
    public String toString() {
        return "classInterConsulta{" + "dataEmissao=" + dataEmissao + 
               ", dataVencimento=" + dataVencimento + ", seuNumero=" + seuNumero + 
               ", nossoNumero=" + nossoNumero + ", cnpjCpfSacado=" + cnpjCpfSacado + 
               ", nomeSacado=" + nomeSacado + ", valorMulta=" + valorMulta + 
               ", valorJuros=" + valorJuros + ", valorNominal=" + valorNominal + 
               ", situacao=" + situacao + '}';
    }
    
}
