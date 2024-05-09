package Funcoes;

public class CEPEndereco {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String ibge;
    private String siaf;

    public CEPEndereco() {}

    public String getSiaf() {
        return siaf;
    }
    
    public void setSiaf(String siaf) {
        this.siaf = siaf;
    }
    
    public String getCep() {
            return cep;
    }

    public void setCep(String cep) {
            this.cep = cep;
    }

    public String getLogradouro() {
            return logradouro;
    }

    public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
    }

    public String getComplemento() {
            return complemento;
    }

    public void setComplemento(String complemento) {
            this.complemento = complemento;
    }

    public String getBairro() {
            return bairro;
    }

    public void setBairro(String bairro) {
            this.bairro = bairro;
    }

    public String getLocalidade() {
            return localidade;
    }

    public void setLocalidade(String localidade) {
            this.localidade = localidade;
    }

    public String getUf() {
            return uf;
    }

    public void setUf(String uf) {
            this.uf = uf;
    }

    public String getIbge() {
            return ibge;
    }

    public void setIbge(String ibge) {
            this.ibge = ibge;
    }
}