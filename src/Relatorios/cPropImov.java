package Relatorios;

public class cPropImov {
    private String rgprp; 
    private String nome; 
    private String cpfcnpj;
    private String rgimv; 
    private String endereco; 
    private String bairro; 
    private String cidade; 
    private String estado; 
    private String contrato; 
    private String nomerazao; 
    private String dtvencimento; 
    private String dtinicio; 
    private String dttermino;
    private String dtultrecebimento; 
    private String aluguel;

    public cPropImov(String rgprp, String nome, String cpfcnpj, String rgimv, 
            String endereco, String bairro, String cidade, String estado, 
            String contrato, String nomerazao, String dtvencimento,
            String dtinicio, String dttermino, String dtultrecebimento, String aluguel) {

        this.rgprp = rgprp;
        this.nome = nome;
        this.cpfcnpj = cpfcnpj;
        this.rgimv = rgimv;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.contrato = contrato;
        this.nomerazao = nomerazao;
        this.dtvencimento = dtvencimento;
        this.dtinicio = dtinicio;
        this.dttermino = dttermino;
        this.dtultrecebimento = dtultrecebimento;
        this.aluguel = aluguel;
    }

    public String getRgprp() { return rgprp; }
    public void setRgprp(String rgprp) { this.rgprp = rgprp; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpfcnpj() { return cpfcnpj; }
    public void setCpfcnpj(String cpfcnpj) { this.cpfcnpj = cpfcnpj; }

    public String getRgimv() { return rgimv; }
    public void setRgimv(String rgimv) { this.rgimv = rgimv; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getContrato() { return contrato; }
    public void setContrato(String contrato) { this.contrato = contrato; }

    public String getNomerazao() { return nomerazao; }
    public void setNomerazao(String nomerazao) { this.nomerazao = nomerazao; }

    public String getDtvencimento() { return dtvencimento; }
    public void setDtvencimento(String dtvencimento) { this.dtvencimento = dtvencimento; }

    public String getDtinicio() { return dtinicio; }
    public void setDtinicio(String dtinicio) { this.dtinicio = dtinicio; }

    public String getDttermino() { return dttermino; }
    public void setDttermino(String dttermino) { this.dttermino = dttermino; }

    public String getDtultrecebimento() { return dtultrecebimento; }
    public void setDtultrecebimento(String dtultrecebimento) { this.dtultrecebimento = dtultrecebimento; }

    public String getAluguel() { return aluguel; }
    public void setAluguel(String aluguel) { this.aluguel = aluguel; }    
}
