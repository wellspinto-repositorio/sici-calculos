package Funcoes.Telefones;

public class Telefones {
    private String ddd;
    private String telefone;
    private String tipo;

    public Telefones(String ddd, String telefone, String tipo) {
        this.ddd = ddd;
        this.telefone = telefone;
        this.tipo = tipo;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "(" + ddd + ") " + telefone + " - " + tipo;
    }        
}
