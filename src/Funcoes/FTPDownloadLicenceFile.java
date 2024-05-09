package Funcoes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
 
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
/**
 *
 * @author YOGA 510
 */
public class FTPDownloadLicenceFile {
    public boolean Licence(String _server, String _user, String _pass) {
        boolean retorno = false;
        
        String server = _server;
        int port = 21;
        String user = _user;
        String pass = _pass;
        if (_server.contains(":")) {
            int ipos = _server.indexOf(":");
            String _tmpServer = _server.substring(0,ipos);
            int _tmpPort = Integer.parseInt(_server.substring(ipos + 1));
            server = _tmpServer;
            port = _tmpPort;
        }
 
        FTPClient ftpClient = new FTPClient();
        try {
 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            String _licensa = Criptografia.decrypt(VariaveisGlobais.Licenca.trim(), Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES); 
            
            // APPROACH #1: using retrieveFile(String, OutputStream)
            String remoteFile1 = "License/" + _licensa + ".aut";
            File downloadFile1 = new File(System.getProperty("user.dir") + "/" + _licensa + ".aut");
            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            outputStream1.close();
 
            if (success) {
                System.out.println("Arquivo de licensa baixado com sucesso.");
                if (new File(System.getProperty("user.dir") + "/Sici.aut").delete()) {
                    if (!downloadFile1.renameTo(new File(System.getProperty("user.dir") + "/Sici.aut"))) {
                        System.out.println("Problemas ao renomear licensa!");
                        return false;
                    }
                } else {
                    System.out.println("Problemas ao apagar arquivo Sici.aut!");
                    return false;
                }
                retorno = true;
            } 
            downloadFile1.delete();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            retorno = false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                retorno = false;
            }
        }
        return retorno;
    }    
}
