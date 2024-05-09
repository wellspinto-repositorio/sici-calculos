package Sici.Partida;

public class classMensagens {
    private boolean bEmail;
    private boolean bWhatsapp;
    private String email;
    private String whatsapp;
    private String mensagem;
    private int dias;
    private String documento;

    public classMensagens(boolean bEmail, boolean bWhatsapp, String email, String whatsapp, String mensagem, int dias, String documento) {
        this.bEmail = bEmail;
        this.bWhatsapp = bWhatsapp;
        this.email = email;
        this.whatsapp = whatsapp;
        this.mensagem = mensagem;
        this.dias = dias;
        this.documento = documento;
    }

    public boolean isbEmail() {
        return bEmail;
    }

    public void setbEmail(boolean bEmail) {
        this.bEmail = bEmail;
    }

    public boolean isbWhatsapp() {
        return bWhatsapp;
    }

    public void setbWhatsapp(boolean bWhatsapp) {
        this.bWhatsapp = bWhatsapp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
    
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }        

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }        

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }    
}
