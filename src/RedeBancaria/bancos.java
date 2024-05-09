package RedeBancaria;

public class bancos {
    private int id;
    private String banco;
    private String bancodv;
    private String agencia;
    private String agenciadv;
    private String conta;
    private String contadv;
    private String carteira;
    private String beneficiario;
    private String path;
    private String crtfile;
    private String keyfile;
    private String pfxfile;
    private String clientid;
    private String clientsecret;

    public bancos(
            int id, 
            String banco, 
            String bancodv, 
            String agencia, 
            String agenciadv, 
            String conta, 
            String contadv, 
            String carteira, 
            String beneficiario, 
            String path, 
            String ctrfile, 
            String keyfile, 
            String pfxfile, 
            String clientid, 
            String clientsecret) {
        this.id = id;
        this.banco = banco;
        this.bancodv = bancodv;
        this.agencia = agencia;
        this.agenciadv = agenciadv;
        this.conta = conta;
        this.contadv = contadv;
        this.carteira = carteira;
        this.beneficiario = beneficiario;
        this.path = path;
        this.crtfile = ctrfile;
        this.keyfile = keyfile;
        this.pfxfile = pfxfile;
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

    public String getContadv() {
        return contadv;
    }

    public void setContadv(String contadv) {
        this.contadv = contadv;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCrtfile() {
        return crtfile;
    }

    public void setCtrfile(String crtfile) {
        this.crtfile = crtfile;
    }

    public String getKeyfile() {
        return keyfile;
    }

    public void setKeyfile(String keyfile) {
        this.keyfile = keyfile;
    }

    public String getPfxfile() {
        return pfxfile;
    }

    public void setPfxfile(String pfxfile) {
        this.pfxfile = pfxfile;
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
