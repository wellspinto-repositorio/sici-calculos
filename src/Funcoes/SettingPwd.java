package Funcoes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.JOptionPane;

/**
 *
 * @author Samic
 */
public class SettingPwd {
    private static final String dbPassWordSettingPath = System.getProperty("user.dir") + "/Sici.aut";
    
    public SettingPwd() {
        boolean exists = (new File(dbPassWordSettingPath)).exists();
        if (!exists) {
            JOptionPane.showMessageDialog(null, "Programa sem autorização para funcionar!!!\n\n" + 
                    "Entre em contato com WellSoft Cel.: (21)97665-9897", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        Properties p;
        FileInputStream propFile = null;
        try {
            propFile = new FileInputStream(dbPassWordSettingPath);
            p = new Properties(System.getProperties()){
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            p.load(propFile);

            System.setProperties(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                propFile.close();
            } catch (IOException ioEx) {}
        }
        System.out.println(dbPassWordSettingPath);
    }    
}