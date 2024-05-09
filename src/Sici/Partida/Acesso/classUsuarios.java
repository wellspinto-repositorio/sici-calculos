package Sici.Partida.Acesso;

public class classUsuarios {
    private String codigo;
    private String nome;
    private String acesso;

    public classUsuarios(String codigo, String nome, String acesso) {
        this.codigo = codigo;
        this.nome = nome;
        this.acesso = acesso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAcesso() {
        return acesso;
    }

    public void setAcesso(String acesso) {
        this.acesso = acesso;
    }
}
