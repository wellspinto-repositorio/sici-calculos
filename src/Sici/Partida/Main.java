package Sici.Partida;

import Funcoes.Criptografia;
import Funcoes.SettingPwd;
import Funcoes.VariaveisGlobais;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Seta todas as rotinas de formatação para Português Brasileiro
        Locale.setDefault(new Locale("pt", "BR"));
        
        JWindow aguarde = new JWindow();

        JLabel label = new JLabel("Carregando...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(Color.RED);
        label.setSize(300,50);

        JProgressBar pgb = new JProgressBar();
        pgb.setIndeterminate(true);

        aguarde.getContentPane().setLayout(new BorderLayout());
        aguarde.getContentPane().add(label, BorderLayout.NORTH);
        aguarde.getContentPane().add(pgb, BorderLayout.CENTER);
        aguarde.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - aguarde.getWidth()) / 2;
        int y = (screenSize.height - aguarde.getHeight()) / 2;
        aguarde.setLocation(x, y);

        //aguarde.setSize(300, 50);
        aguarde.setVisible(true);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Timer t = new Timer();
                t.schedule(new RemindTask(), 3 * 1000);

                FlatLightLaf.setup();
                UIManager.put( "Button.arc", 999 );
                UIManager.put( "Component.arc", 999 );
                UIManager.put( "ProgressBar.arc", 999 );
                UIManager.put( "TextComponent.arc", 999 );
                UIManager.put( "Component.focusWidth", 1 );
                UIManager.put( "Component.focusColor", new Color(60,98,140));
                UIManager.put( "ScrollBar.thumbArc", 999 );
                UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
                UIManager.put("PasswordField.showRevealButton", true);
                UIManager.put("TitlePane.menuBarEmbedded", true);        
                UIManager.put("Table.alternateRowColor", new Color(128, 128, 128));
                UIManager.put("Table.selectionBackground", new Color(10, 66, 107));
                //UIManager.put("Table.selectionForeground", Color.RED);

                LerSettings();
                t.cancel();
                newLogin.main(new String[] {""});                

                return null;
            }

            @Override
            protected void done() {
                aguarde.dispose();
            }
        };
        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                    worker.removePropertyChangeListener(this);
                    aguarde.dispose();
                }
            }
        });

        worker.execute();                                        
    }
    
    private static class RemindTask extends TimerTask {
        @Override
        public void run() {
            System.exit(0);
        }
    }

    private static void LerSettings() {        
        new SettingPwd();
        
        //VariaveisGlobais.sqlUser = System.getProperty("dbUser", "root"); // 30/04/2024
        String crypData = System.getProperty("Id", "/IXEKtdYlssksF+hUV4/yg==");
        String decrypData = Criptografia.decrypt(crypData, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
        VariaveisGlobais.sqlUser = decrypData;
        
        // Pega o nome do banco de dados em .aut
        String _netRemote = System.getProperty("Remote", null);
        if (_netRemote != null) {
            String netRemote = Criptografia.decrypt(_netRemote, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);
            String _tmpTemote[] = netRemote.split(",");
            VariaveisGlobais.sqlAlias = _tmpTemote[0];
            VariaveisGlobais.sqlHost = _tmpTemote[1].split(":")[0];
            VariaveisGlobais.sqlPort = Integer.parseInt(_tmpTemote[1].split(":")[1]);
            VariaveisGlobais.sqlDbName = _tmpTemote[2];
            
            VariaveisGlobais.remote = netRemote;
        }
        
        VariaveisGlobais.units.clear();
        String[] _hostl = VariaveisGlobais.remote.split(",");
        if (_hostl.length > 1) VariaveisGlobais.units.add(new String[] {_hostl[0], _hostl[1], _hostl[2]});
        for (int w=1;w<=99;w++) {
            String remotn = System.getProperty("Remote"  + new DecimalFormat("00").format(w), null);
            if (remotn == null) continue;
            remotn = Criptografia.decrypt(remotn, Criptografia.ALGORITMO_AES, Criptografia.ALGORITMO_AES);

            String[] _host1 = null; String alias = "", host = "", dbname = "";
            if (!"".equals(remotn)) {
                _host1 = remotn.split(",");
                if (_host1.length > 1) {
                    alias = _host1[0];
                    host  = _host1[1];
                    dbname  = _host1[2];
                }
            }
            if (!"".equals(host)) VariaveisGlobais.units.add(new String[] {alias, host, dbname});
        }
    }
}
