package BancosDigital.Santander;

import java.math.BigDecimal;
import java.util.Date;

public class classSantanderConsulta {
    private Date Emissao;
    private Date Vencimento;
    private Date Pagamento;
    private String SeuNumero;
    private String NossoNumero;
    private String CpfCnpj;
    private String Sacado;
    private BigDecimal valorNominal;
    private BigDecimal valorRecebido;
    private String Status;

    public classSantanderConsulta(Date Emissao, Date Vencimento, Date Pagamento, String SeuNumero, String NossoNumero, String CpfCnpj, String Sacado, BigDecimal valorNominal, BigDecimal valorRecebido, String Status) {
        this.Emissao = Emissao;
        this.Vencimento = Vencimento;
        this.Pagamento = Pagamento;
        this.SeuNumero = SeuNumero;
        this.NossoNumero = NossoNumero;
        this.CpfCnpj = CpfCnpj;
        this.Sacado = Sacado;
        this.valorNominal = valorNominal;
        this.valorRecebido = valorRecebido;
        this.Status = Status;
    }

    public Date getEmissao() {
        return Emissao;
    }

    public void setEmissao(Date Emissao) {
        this.Emissao = Emissao;
    }

    public Date getVencimento() {
        return Vencimento;
    }

    public void setVencimento(Date Vencimento) {
        this.Vencimento = Vencimento;
    }

    public Date getPagamento() {
        return Pagamento;
    }

    public void setPagamento(Date Pagamento) {
        this.Pagamento = Pagamento;
    }

    public String getSeuNumero() {
        return SeuNumero;
    }

    public void setSeuNumero(String SeuNumero) {
        this.SeuNumero = SeuNumero;
    }

    public String getNossoNumero() {
        return NossoNumero;
    }

    public void setNossoNumero(String NossoNumero) {
        this.NossoNumero = NossoNumero;
    }

    public String getCpfCnpj() {
        return CpfCnpj;
    }

    public void setCpfCnpj(String CpfCnpj) {
        this.CpfCnpj = CpfCnpj;
    }

    public String getSacado() {
        return Sacado;
    }

    public void setSacado(String Sacado) {
        this.Sacado = Sacado;
    }

    public BigDecimal getValorNominal() {
        return valorNominal;
    }

    public void setValorNominal(BigDecimal valorNominal) {
        this.valorNominal = valorNominal;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }   
}
