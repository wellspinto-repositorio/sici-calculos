package Sici.Partida.Config;

public class PrintersListClass {
    private String ip;
    private String name;
    private String share;
    private boolean isdefault;

    public PrintersListClass(String ip, String name, String share, boolean isdefault) {
        this.ip = ip;
        this.name = name;
        this.share = share;
        this.isdefault = isdefault;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public boolean isIsdefault() {
        return isdefault;
    }

    public void setIsdefault(boolean isdefault) {
        this.isdefault = isdefault;
    }        

    @Override
    public String toString() {
        return name;
    }        
}
