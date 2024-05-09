package Sici.Partida.Acesso;

public class classSubMenu {
    private int id;
    private String botoes;
    private String nome;
    private String chamada;

    public classSubMenu(int id, String botoes, String nome, String chamada) {
        this.id = id;
        this.botoes = botoes;
        this.nome = nome;
        this.chamada = chamada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBotoes() {
        return botoes;
    }

    public void setBotoes(String botoes) {
        this.botoes = botoes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getChamada() {
        return chamada;
    }

    public void setChamada(String chamada) {
        this.chamada = chamada;
    }        
}
