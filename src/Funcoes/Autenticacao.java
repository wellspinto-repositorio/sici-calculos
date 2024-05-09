/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcoes;

import java.sql.ResultSet;

/**
 *
 * @author supervisor
 */
public class Autenticacao {
    static double nAut = 1;
    static Db conn = VariaveisGlobais.conexao;
    
    public static double getAut() {
        waitUnlock();
        while (!lockAut()) {}
        return nAut;
    }
    
    public static boolean lockAut() {
        boolean sucesso = false;
        String sAut = "*" + FuncoesGlobais.StrZero(Double.valueOf(nAut).toString(), 10);
        String psql = "UPDATE parametros SET conteudo = '" + sAut + "' WHERE variavel = 'AUTENTICACAO';";
        try {
            sucesso = conn.CommandExecute(psql, null) > 0;
        } catch (Exception e) {sucesso = false; e.printStackTrace();}
        return sucesso;
    }
    
    public static boolean unlockAut() {
        boolean sucesso = false;
        String sAut = FuncoesGlobais.StrZero(Double.valueOf(nAut).toString(), 10);
        String psql = "UPDATE parametros SET conteudo = '" + sAut + "' WHERE variavel = 'AUTENTICACAO';";
        try {
            sucesso = conn.CommandExecute(psql, null) > 0;
        } catch (Exception e) {sucesso = false; e.printStackTrace();}
        return sucesso;
    }
    
    public static boolean setAut(double dAut, double inc) {
        boolean sucesso = false; nAut = dAut;
        String sAut = "*" + FuncoesGlobais.StrZero(Double.valueOf(nAut + inc).toString(), 10);
        String psql = "UPDATE parametros SET conteudo = '" + sAut + "' WHERE variavel = 'AUTENTICACAO';";
        try {
            sucesso = conn.CommandExecute(psql, null) > 0;
        } catch (Exception e) {sucesso = false; e.printStackTrace();}
        
        if (sucesso) {
            nAut = nAut + inc;
            while (!unlockAut()) {}
        }
        return sucesso;
    }
    
    static void waitUnlock() {
        boolean sucesso = true; String sAut = "";
        while (sucesso) {
            try {
                String psql = "SELECT conteudo FROM parametros WHERE variavel = 'AUTENTICACAO' LIMIT 1;";
                ResultSet prs = conn.OpenTable(psql, null);
                while (prs.next()) {
                    sAut = prs.getString("conteudo");
                    if (sAut.contains(",")) {
                        sAut = "0000000000" + sAut.substring(0, sAut.indexOf(","));
                        sAut = StringManager.Right(sAut, 10);
                    }
                    
                    if (sAut.substring(0, 1).equalsIgnoreCase("*")) {
                        sucesso = true;
                    } else {
                        sucesso = false;
                    }
                }
                conn.CloseTable(prs);
            } catch (Exception e) {e.printStackTrace();}
        }
        nAut = Double.valueOf(sAut);
    }
}
