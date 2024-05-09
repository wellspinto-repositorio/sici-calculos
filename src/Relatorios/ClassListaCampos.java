package Relatorios;

import java.math.BigDecimal;
import java.util.Date;

public class ClassListaCampos {
    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nome;
    private Date dtvencimento;
    private Date dtrecebimento;
    private BigDecimal campo;

    public ClassListaCampos(String rgprp, String rgimv, String contrato, String nome, Date dtvencimento, Date dtrecebimento, BigDecimal campo) {
        this.rgprp = rgprp;
        this.rgimv = rgimv;
        this.contrato = contrato;
        this.nome = nome;
        this.dtvencimento = dtvencimento;
        this.dtrecebimento = dtrecebimento;
        this.campo = campo;
    }

    public String getRgprp() {
        return rgprp;
    }

    public void setRgprp(String rgprp) {
        this.rgprp = rgprp;
    }

    public String getRgimv() {
        return rgimv;
    }

    public void setRgimv(String rgimv) {
        this.rgimv = rgimv;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDtvencimento() {
        return dtvencimento;
    }

    public void setDtvencimento(Date dtvencimento) {
        this.dtvencimento = dtvencimento;
    }

    public Date getDtrecebimento() {
        return dtrecebimento;
    }

    public void setDtrecebimento(Date dtrecebimento) {
        this.dtrecebimento = dtrecebimento;
    }

    public BigDecimal getCampo() {
        return campo;
    }

    public void setCampo(BigDecimal campo) {
        this.campo = campo;
    }        
}
