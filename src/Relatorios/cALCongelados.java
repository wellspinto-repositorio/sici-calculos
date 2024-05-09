package Relatorios;

import java.util.Date;

/**
 *
 * @author YOGA 510
 */
public class cALCongelados {
    private String rgprp;
    private String rgimv;
    private String contrato;
    private String nomeloca;
    private Date dtvencimento;
    private Date dtinicioctr;
    private Date dtfimctr;
    private Date dtaditoctr;
    private Date dtultrecto;

    public cALCongelados(String rgprp, String rgimv, String contrato, String nomeloca, Date dtvencimento, Date dtinicioctr, Date dtfimctr, Date dtaditoctr, Date dtultrecto) {
        this.rgprp = rgprp;
        this.rgimv = rgimv;
        this.contrato = contrato;
        this.nomeloca = nomeloca;
        this.dtvencimento = dtvencimento;
        this.dtinicioctr = dtinicioctr;
        this.dtfimctr = dtfimctr;
        this.dtaditoctr = dtaditoctr;
        this.dtultrecto = dtultrecto;
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

    public String getNomeloca() {
        return nomeloca;
    }

    public void setNomeloca(String nomeloca) {
        this.nomeloca = nomeloca;
    }

    public Date getDtvencimento() {
        return dtvencimento;
    }

    public void setDtvencimento(Date dtvencimento) {
        this.dtvencimento = dtvencimento;
    }

    public Date getDtinicioctr() {
        return dtinicioctr;
    }

    public void setDtinicioctr(Date dtinicioctr) {
        this.dtinicioctr = dtinicioctr;
    }

    public Date getDtfimctr() {
        return dtfimctr;
    }

    public void setDtfimctr(Date dtfimctr) {
        this.dtfimctr = dtfimctr;
    }

    public Date getDtaditoctr() {
        return dtaditoctr;
    }

    public void setDtaditoctr(Date dtaditoctr) {
        this.dtaditoctr = dtaditoctr;
    }

    public Date getDtultrecto() {
        return dtultrecto;
    }

    public void setDtultrecto(Date dtultrecto) {
        this.dtultrecto = dtultrecto;
    }

    @Override
    public String toString() {
        return "cALCongelados{" + "rgprp=" + rgprp + ", rgimv=" + rgimv + ", contrato=" + contrato + ", nomeloca=" + nomeloca + ", dtvencimento=" + dtvencimento + ", dtinicioctr=" + dtinicioctr + ", dtfimctr=" + dtfimctr + ", dtaditoctr=" + dtaditoctr + ", dtultrecto=" + dtultrecto + '}';
    }    
}
