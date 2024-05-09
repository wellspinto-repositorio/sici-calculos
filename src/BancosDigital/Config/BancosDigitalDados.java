package BancosDigital.Config;

public class BancosDigitalDados {
    private int id;
    private String banco;
    private String bancodv;
    private String agencia;
    private String agenciadv;
    private String conta;
    private String carteira;
    private String beneficiario;
    private String operador;
    private String nnumero;
    private String path;
    private String crtfile;
    private String keyfile;
    private String clientid;
    private String clientsecret;

    public BancosDigitalDados(int id, String banco, String bancodv, String agencia, String agenciadv, String conta, String carteira, String beneficiario, String operador, String nnumero, String path, String crtfile, String keyfile, String clientid, String clientsecret) {
        this.id = id;
        this.banco = banco;
        this.bancodv = bancodv;
        this.agencia = agencia;
        this.agenciadv = agenciadv;
        this.conta = conta;
        this.carteira = carteira;
        this.beneficiario = beneficiario;
        this.operador = operador;
        this.nnumero = nnumero;
        this.path = path;
        this.crtfile = crtfile;
        this.keyfile = keyfile;
        this.clientid = clientid;
        this.clientsecret = clientsecret;
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

    public String getBancodv() {
        return bancodv;
    }

    public void setBancodv(String bancodv) {
        this.bancodv = bancodv;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getAgenciadv() {
        return agenciadv;
    }

    public void setAgenciadv(String agenciadv) {
        this.agenciadv = agenciadv;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getCarteira() {
        return carteira;
    }

    public void setCarteira(String carteira) {
        this.carteira = carteira;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getNnumero() {
        return nnumero;
    }

    public void setNnumero(String nnumero) {
        this.nnumero = nnumero;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCrtfile() {
        return crtfile;
    }

    public void setCrtfile(String crtfile) {
        this.crtfile = crtfile;
    }

    public String getKeyfile() {
        return keyfile;
    }

    public void setKeyfile(String keyfile) {
        this.keyfile = keyfile;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClientsecret() {
        return clientsecret;
    }

    public void setClientsecret(String clientsecret) {
        this.clientsecret = clientsecret;
    }        
}
