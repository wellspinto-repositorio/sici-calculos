package Sici.Partida.Acesso;

public class classMenu {
    private int id;
    private String descricao;
    private String atalho;
    private String icone;
    private String chamada;
    private boolean senha;
    private String botoes;
    private String tooltips;

    public classMenu(int id, String descricao, String atalho, String icone, String chamada, boolean senha, String botoes, String tooltips) {
        this.id = id;
        this.descricao = descricao;
        this.atalho = atalho;
        this.icone = icone;
        this.chamada = chamada;
        this.senha = senha;
        this.botoes = botoes;
        this.tooltips = tooltips;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAtalho() {
        return atalho;
    }

    public void setAtalho(String atalho) {
        this.atalho = atalho;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getChamada() {
        return chamada;
    }

    public void setChamada(String chamada) {
        this.chamada = chamada;
    }

    public boolean isSenha() {
        return senha;
    }

    public void setSenha(boolean senha) {
        this.senha = senha;
    }

    public String getBotoes() {
        return botoes;
    }

    public void setBotoes(String botoes) {
        this.botoes = botoes;
    }        

    public String getTooltips() {
        return tooltips;
    }

    public void setTooltips(String tooltips) {
        this.tooltips = tooltips;
    }    
}
