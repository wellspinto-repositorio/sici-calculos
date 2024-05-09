package Funcoes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Config {
    public Db conn = VariaveisGlobais.conexao;
    
    public Config() {
        if (conn == null) {
            String[] _hostl = VariaveisGlobais.remote.split(",");
            VariaveisGlobais.sqlHost = _hostl[1].split(":")[0];
            VariaveisGlobais.sqlPort= Integer.parseInt(_hostl[1].split(":")[1]);

            // Abre Conexão
            if (conn == null) conn = new Db(null);  
            VariaveisGlobais.conexao = conn;
        }
    }

    public Object Reading(String value, String defaultValue, String local) {
        Object retorno = _Reading(value, local);        
        return retorno != null ? retorno : defaultValue;
    }
    public Object Reading(String value, boolean defaultValue, String local) {
        Object retorno = _Reading(value, local);        
        return retorno != null ? retorno : defaultValue;
    }
    public Object Reading(String value, int defaultValue, String local) {
        Object retorno = _Reading(value, local);        
        return retorno != null ? retorno : defaultValue;
    }
    
    private Object _Reading(String value, String local) {
        Object retorno = null;
        String selectSQL = "SELECT `variavel`, `conteudo`, `tipo` FROM `config` WHERE `variavel` = :variavel AND `local` = :local LIMIT 1;";
        ResultSet rs = conn.OpenTable(selectSQL, new Object[][] {
            {"string", "variavel", value.trim()},
            {"string", "local", local.trim().toUpperCase()}
        });
        try {
            while (rs.next()) {
                String _tipo = rs.getString("tipo").trim().toUpperCase();
                String _conteudo = rs.getString("conteudo");
                if (_tipo.equalsIgnoreCase("TEXTO")) {
                    retorno = _conteudo;
                } else if (_tipo.equalsIgnoreCase("LOGICA")) {
                    retorno = new Boolean(_conteudo);                    
                } else if (_tipo.equalsIgnoreCase("NUMERICO")) {
                    retorno = Integer.parseInt(_conteudo);
                }
            }
        } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
        conn.CloseTable(rs);
        return retorno;
    }
    
    public boolean Saveing(String variable, String type, String value, String local) {
        if (local.trim().toUpperCase().equalsIgnoreCase("LOCAL")) {
            InetAddress localhost = null;
            try { 
                localhost = InetAddress.getLocalHost(); 
                String name = localhost.getHostName();
                local = name.trim().toUpperCase();
            } catch (UnknownHostException uhEx) { return false; }
        }
        String querySQL = ""; Object[][] param = null;
        if (_Reading(variable, local) != null) {
           querySQL = "UPDATE `config` SET `conteudo` = :conteudo WHERE `variavel` = :variavel AND `local` = :local;";
           param = new Object[][] {
               {"string", "conteudo", value},
               {"string", "variavel", variable},
               {"string", "local", local}
           };
        } else {
            querySQL = "INSERT INTO `config` (`variavel`, `tipo`, `conteudo`, `local`) VALUES (:variavel, :tipo, :conteudo, :local);";
            param = new Object[][] {
                {"string", "variavel", variable},
                {"string", "tipo", type},
                {"string", "conteudo", value},
                {"string", "local", local}
            };                        
        }
        return conn.CommandExecute(querySQL, param) > 0;
    }
}
