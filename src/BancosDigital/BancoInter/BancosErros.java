package BancosDigital.BancoInter;

public class BancosErros {
    private String contrato;
    private String nome;
    private String vencimento;
    private String codigo;
    private String message;

    public BancosErros(String contrato, String nome, String vencimento, String codigo, String message) {
        this.contrato = contrato;
        this.nome = nome;
        this.vencimento = vencimento;
        this.codigo = codigo;
        this.message = message;
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

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }   
}
