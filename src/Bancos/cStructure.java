package Bancos;

public class cStructure {
    private int id = -1;
    private String banco = "";
    private String tipo = "";
    private String tparq = "";
    private String campo = "";
    private int inicial = -1;
    private int finalizacao = -1;
    private String an = "";
    private int tamanho = -1;
    private int decimais = -1;
    private String notas = "";
    private Object value = "";

    public cStructure() {}    
    public cStructure(int id, String banco, String tipo, String tparq, 
            String campo, int inicial, int finalizacao, String an, 
            int tamanho, int decimais, String notas, Object value) {
        this.id = id;
        this.banco = banco;
        this.tipo = tipo;
        this.tparq = tparq;
        this.campo = campo;
        this.inicial = inicial;
        this.finalizacao = finalizacao;
        this.an = an;
        this.tamanho = tamanho;
        this.decimais = decimais;
        this.notas = notas;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTparq() {
        return tparq;
    }

    public void setTparq(String tparq) {
        this.tparq = tparq;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public int getInicial() {
        return inicial;
    }

    public void setInicial(int inicial) {
        this.inicial = inicial;
    }

    public int getFinalizacao() {
        return finalizacao;
    }

    public void setFinalizacao(int finalizacao) {
        this.finalizacao = finalizacao;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getDecimais() {
        return decimais;
    }

    public void setDecimais(int decimais) {
        this.decimais = decimais;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }            

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }        
}
