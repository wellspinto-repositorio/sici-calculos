package Funcoes;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author YOGA 510
 */
public class Db {
    static public Connection conn = null;
    private String url = "";
    
    public Db(JFrame he) {
        url = VariaveisGlobais.sqlUrl + VariaveisGlobais.sqlHost + ":" + VariaveisGlobais.sqlPort + "/" + VariaveisGlobais.sqlDbName + VariaveisGlobais.sqlConn;
        OpenDb(he);
    }      
    
    private Connection OpenDb(JFrame he) {
        try {
            if (VariaveisGlobais.conexao == null) {
                Class.forName(VariaveisGlobais.sqlDrive);
                conn = DriverManager.getConnection(url, VariaveisGlobais.sqlUser, VariaveisGlobais.sqlPwd);
            } else {
                VariaveisGlobais.conexao = null;
                conn = null;
                OpenDb(he);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            conn = null;
            VariaveisGlobais.conexao = null;
            System.out.println(ex);
            JOptionPane.showMessageDialog(he, "Unidade OffLine!!!\nTente novamente...", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println(url);
        return conn;
    }
        
    public void CloseDb() {
        if (VariaveisGlobais.conexao != null) {
            try { conn.close(); } catch (SQLException ex) { }
            VariaveisGlobais.conexao = null;
        }
    }
    
    private NamedPreparedStatement Parameters(NamedPreparedStatement stm, Object[][] param) {
        try {
            for (Object[] item : param) {
                switch (item[0].toString().trim().toLowerCase()) {
                    case "int" : 
                        stm.setInt(item[1].toString(), (int) item[2]); 
                        break;
                    case "bigint":
                        stm.setInt(item[1].toString(), (Integer) item[2]);
                        break;
                    case "date":
                        stm.setDate(item[1].toString(), Dates.toSqlDate((java.util.Date) item[2]));
                        break;
                    case "string": 
                        stm.setString(item[1].toString(), (String) item[2]);
                        break;
                    case "decimal":
                        stm.setBigDecimal(item[1].toString(), (BigDecimal) item[2]);
                        break;
                    case "boolean":
                        stm.setBoolean(item[1].toString(), (Boolean) item[2]);
                        break;
                    case "float":
                        stm.setFloat(item[1].toString(), (Float) item[2]);
                        break;
                    case "double":
                        stm.setDouble(item[1].toString(), (Double) item[2]);
                        break;
                }
            }       
        } catch (SQLException e) { e.printStackTrace(); }
        
        return stm;
    }
    
    public ResultSet OpenTable(String sqlString, Object[][] param) {
        if (VariaveisGlobais.isNuvem) while (!isDbConnected()) {}
        
        ResultSet hResult = null;
        
        NamedPreparedStatement stm = null;
        try {
            stm = NamedPreparedStatement.prepareStatement(conn, sqlString, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        if (param != null && param.length > 0) stm = Parameters(stm, param);
            
        try {
            hResult = stm.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return hResult;
    }

    public static boolean isDbConnected() {
        try {
            if (!conn.isValid(5)) {
                System.out.println("Entrei aqui");
                Db xcon = new Db(null);
                VariaveisGlobais.conexao = xcon;
            }
        } catch (SQLException ex) {
            System.out.println("Erro inesperado.\nError: " + ex.getErrorCode() + "\n" + ex.getMessage());
        }
        
        final String CHECK_SQL_QUERY = "SELECT 1";
        boolean isConnected = false;
        try {
            final PreparedStatement statement = conn.prepareStatement(CHECK_SQL_QUERY);
            isConnected = true;
        } catch (SQLException | NullPointerException e) {
            System.out.println("Erro de statment.\nError: " + e.getMessage());
        }
        return isConnected;
    }
    
    public ResultSet OpenTable(String sqlString, int type, Object[][] param) {
        if (VariaveisGlobais.isNuvem) while (!isDbConnected()) {}
        
        ResultSet hResult = null;
        
        NamedPreparedStatement stm = null;
        try {
            stm = NamedPreparedStatement.prepareStatement(conn, sqlString, type);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        if (param != null && param.length > 0) stm = Parameters(stm, param);
            
        try {
            hResult = stm.executeQuery();           
        } catch (SQLException ex) {
//            if (ex.getErrorCode() == 0) {
//                System.out.println("SELECT 2");
//                while (!isDbConnected()) {}
//                try { Thread.sleep(3000); } catch (InterruptedException ext) {}
//            } else 
            ex.printStackTrace();
        }

        return hResult;
    }

    public void CloseTable(ResultSet rs) {
        try { rs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
    }
   
    public int RecordCount(ResultSet hrs) {
        int retorno = 0;
        try {
            int pos = hrs.getRow();
            hrs.last();
            retorno = hrs.getRow();
            hrs.beforeFirst();
            if (pos > 0) hrs.absolute(pos);
        } catch (SQLException e) {retorno = 0;}
        return retorno;
    }    

    public NamedPreparedStatement CommandStatment(String sqlString, Object[][] param) {
        if (VariaveisGlobais.isNuvem) while (!isDbConnected()) {}

        NamedPreparedStatement stm = null;
        try {
            stm = NamedPreparedStatement.prepareStatementII(conn, sqlString, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (param != null && param.length > 0) stm = Parameters(stm, param);

        return stm;
    }

    public int CommandExecute(String sqlString, Object[][] param) {
        if (VariaveisGlobais.isNuvem) while (!isDbConnected()) {}
        
        int hRetorno = 0;

        NamedPreparedStatement stm = null;
        try {
            stm = NamedPreparedStatement.prepareStatement(conn, sqlString, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (param != null && param.length > 0) stm = Parameters(stm, param);

        try {
            hRetorno = stm.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hRetorno;
    }

    public int CommandExecute(String sqlString) {
        if (VariaveisGlobais.isNuvem) while (!isDbConnected()) {}
        
        int hRetorno = 0;

        NamedPreparedStatement stm = null;
        try {
            stm = NamedPreparedStatement.prepareStatement(conn, sqlString, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            hRetorno = stm.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hRetorno;
    }

    public String CreateSqlText(String aFiels[][], String cTableName, String cWhere, String cTipo) {
        int i = 0;
        String cRet = "";
        String auxCpo = "";

        if (cTipo.equals("INSERT")) {
            cRet = "INSERT INTO " + cTableName.toLowerCase() + " (";
            auxCpo = "VALUES (";

            for (i=0;i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0].toLowerCase() + ",";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    auxCpo += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") auxCpo += aFiels[i][1] + ",";
            }

            cRet = cRet.substring(0,cRet.length() -1) + ") "
                 + auxCpo.substring(0,auxCpo.length() - 1) + ")";

        } else if (cTipo.equals("UPDATE")) {
            cRet = "UPDATE " + cTableName.toLowerCase() + " SET ";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0].toLowerCase() + "=";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    cRet += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") {
                    cRet += aFiels[i][1] + ",";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + " WHERE " + cWhere;

        } else if (cTipo.equals("SELECT")) {
            cRet = "SELECT";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += ((aFiels.equals("")) ? aFiels[i][0].toLowerCase() : aFiels[i][1].toLowerCase() + ", ");
            }

            cRet = cRet.substring(0, cRet.length() - 2) + " FROM " + cTableName.toLowerCase()
                 + ((cWhere.equals("")) ? " WHERE " + cWhere : ";");

        }

        return cRet;
    }
    
    public static String CreateSqlText2(String aFiels[][], String cTableName, String cWhere, String cTipo) {
        int i = 0;
        String cRet = "";
        String auxCpo = "";

        if (cTipo.equals("INSERT")) {
            cRet = "INSERT INTO " + cTableName.toLowerCase() + " (";
            auxCpo = "VALUES (";

            for (i=0;i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += aFiels[i][0].toLowerCase() + ",";
                    auxCpo += "'" + aFiels[i][2] + "',";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + ") "
                 + auxCpo.substring(0,auxCpo.length() - 1) + ")";

        } else if (cTipo.equals("UPDATE")) {
            cRet = "UPDATE " + cTableName.toLowerCase() + " SET ";

            for (i=0; i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += aFiels[i][0].toLowerCase() + "=";
                    cRet += "'" + aFiels[i][2] + "',";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + " WHERE " + cWhere;

        } else if (cTipo.equals("SELECT")) {
            cRet = "SELECT ";

            for (i=0; i <= aFiels.length - 1; i++) {
                if (!"".equals(aFiels[i][0])) {
                    cRet += (!"".equals(aFiels[i][2]) ? aFiels[i][0].toLowerCase() : aFiels[i][2].toLowerCase()) + ", ";
                }
            }

            cRet = cRet.substring(0, cRet.length() - 2) + " FROM " + cTableName
                 + (!"".equals(cWhere.trim()) ? " WHERE " + cWhere : "") + ";";
        }

        return cRet;
    }
    
    public String ReadParameters(String cVar) throws SQLException {
        String rVar = null;
        Object[][] param = new Object[][] {
            {"string", "variavel", cVar.trim().toLowerCase()}
        };
        ResultSet hResult = OpenTable("SELECT variavel, conteudo, tipo FROM parametros WHERE LOWER(TRIM(variavel)) = :variavel;", param);

        if (hResult.first()) {
            rVar = hResult.getString("conteudo");
        }

        return rVar;
    }

    /**
     * GravarParametros
     */
    public boolean SaveParameters(String cVar[]) throws SQLException {
        boolean rVar = false;
        boolean bInsert = false;
        String sql = "";

        bInsert = (ReadParameters(cVar[0]) == null);
        Object[][] param = null;
        if (bInsert) {
            sql = "INSERT INTO parametros (variavel, tipo, conteudo) VALUES (:variavel, :tipo, :conteudo);";
            param = new Object[][] {
                {"string", "variavel", cVar[0]},
                {"string", "tipo", cVar[1]},
                {"string", "conteudo", cVar[2]}  
            };
        } else {
            sql = "UPDATE parametros SET conteudo = :conteudo WHERE variavel = :variavel;";
            param = new Object[][] {
                {"string", "conteudo", cVar[1]},
                {"string", "variavel", cVar[0]}
            };
        }

        rVar = (CommandExecute(sql, param)) > 0;
        return rVar;
    }
    
    public boolean GravarMultiParametros(String cVar[][]) throws SQLException {
        boolean bInsert = false;
        int i = 0; int nVar = 0;

        for (i=0;i<=cVar.length - 1;i++) {
            String sql = "";

            if (!"".equals(cVar[i][0])) {
                bInsert = (ReadParameters(cVar[i][0]) == null);
                if (bInsert) {
                    sql = "INSERT INTO parametros (variavel, tipo, conteudo) VALUES ('" + cVar[i][0] + "','" + cVar[i][1] + "','" + cVar[i][2] + "')";
                } else {
                    sql = "UPDATE parametros SET conteudo = '" + cVar[i][2] + "' WHERE variavel = '" + cVar[i][0] + "';";
                }

                nVar += CommandExecute(sql);
            }
        }
        return (nVar > 0);
    }    

    public Object[][] ReadFieldsTable(String[] aCampos, String tbNome, String sWhere) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos.toLowerCase() + " FROM " + tbNome.toLowerCase() + " WHERE " + sWhere;
        ResultSet tmpResult = OpenTable(sSql, null);
        ResultSetMetaData md = tmpResult.getMetaData();
        String[][] vRetorno = new String[aCampos.length][4];
        int i = 0;

        while (tmpResult.next()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                vRetorno[i][0] = md.getColumnName(i + 1);
                vRetorno[i][1] =  md.getColumnTypeName(i + 1);

                // Work field name
                String variavel = aCampos[i].trim();
                if (variavel.toLowerCase().contains(" as ")) {
                    variavel = variavel.substring(variavel.toLowerCase().indexOf(" as") + 3).trim();
                } 
                try {
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(variavel).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }
                try {
                    vRetorno[i][3] = tmpResult.getString(variavel);
                    if (vRetorno[i][3] == null) vRetorno[i][3] = "";
                } catch (NullPointerException ex) { vRetorno[i][3] = ""; }
            }
        }
        
        CloseTable(tmpResult);

        if (i == 0) vRetorno = null;        
        return vRetorno;
    }    
    
    public Object[][] ReadFieldsTable(String[] aCampos, String tbNome, String sWhere, Object[][] param) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos.toLowerCase() + " FROM " + tbNome.toLowerCase() + " WHERE " + sWhere;
        ResultSet tmpResult = OpenTable(sSql, param);
        ResultSetMetaData md = tmpResult.getMetaData();
        String[][] vRetorno = new String[aCampos.length][4];
        int i = 0;

        while (tmpResult.next()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                vRetorno[i][0] = md.getColumnName(i + 1);
                vRetorno[i][1] =  md.getColumnTypeName(i + 1);

                // Work field name
                String variavel = aCampos[i].trim();
                if (variavel.toLowerCase().contains(" as ")) {
                    variavel = variavel.substring(variavel.toLowerCase().indexOf(" as") + 3).trim();
                } 
                try {
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(variavel).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }
                try {
                    vRetorno[i][3] = tmpResult.getString(variavel);
                    if (vRetorno[i][3] == null) vRetorno[i][3] = "";
                } catch (NullPointerException ex) { vRetorno[i][3] = ""; }
            }
        }
        
        CloseTable(tmpResult);

        if (i == 0) vRetorno = null;        
        return vRetorno;
    }    
    
    public void CreateAuxiliartmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `auxiliartmp` ( " +
                "  `autoid` float NOT NULL AUTO_INCREMENT, " +
                "  `conta` varchar(3) DEFAULT NULL, " +
                "  `rgprp` varchar(6) NOT NULL DEFAULT '', " +
                "  `rgimv` varchar(6) NOT NULL DEFAULT '', " +
                "  `contrato` varchar(7) NOT NULL DEFAULT '', " +
                "  `campo` text, " +
                "  `dtvencimento` date NOT NULL DEFAULT '0000-00-00', " +
                "  `dtrecebimento` date NOT NULL DEFAULT '0000-00-00', " +
                "  `rc_aut` double NOT NULL DEFAULT '0', " +
                "  PRIMARY KEY (`autoid`) " +
                ") ENGINE=MyISAM AUTO_INCREMENT=35267 DEFAULT CHARSET=latin1";
                
        if (!ExistTable("auxiliartmp"))  CommandExecute(sString, null);
    }

    public void CreateExtBancotmp() throws SQLException {
        String sString = "CREATE TABLE `extbancotmp` ( " +
                        "  `ch_data` date DEFAULT NULL, " +
                        "  `ch_data2` datetime DEFAULT NULL, " +
                        "  `ch_banco` varchar(5) DEFAULT NULL, " +
                        "  `ch_agencia` varchar(4) DEFAULT NULL, " +
                        "  `ch_ncheque` varchar(8) DEFAULT NULL, " +
                        "  `ch_valor` decimal(19,4) DEFAULT NULL, " +
                        "  `ch_etda` varchar(3) DEFAULT NULL, " +
                        "  `ch_desc` varchar(20) DEFAULT NULL, " +
                        "  `ch_dtdepos` date DEFAULT NULL, " +
                        "  `ch_bcoor` varchar(5) DEFAULT NULL, " +
                        "  `ch_autenticacao` varchar(20) DEFAULT NULL, " +
                        "  `ch_autent` varchar(6) DEFAULT NULL, " +
                        "  `ch_ncaixa` varchar(30) DEFAULT NULL " +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        if (!ExistTable("ExtBancotmp")) CommandExecute(sString, null);
    }
    
    public void CreateChequestmp() throws SQLException {
        String sString = "CREATE TABLE `chequestmp` ( " +
                        "  `ch_data` date DEFAULT NULL, " +
                        "  `ch_data2` date DEFAULT NULL, " +
                        "  `ch_banco` varchar(5) DEFAULT NULL, " +
                        "  `ch_agencia` varchar(4) DEFAULT NULL, " +
                        "  `ch_ncheque` varchar(8) DEFAULT NULL, " +
                        "  `ch_valor` decimal(19,4) DEFAULT NULL, " +
                        "  `ch_ncaixa` varchar(25) DEFAULT NULL, " +
                        "  `ch_autenticacao` varchar(20) DEFAULT NULL, " +
                        "  `ch_autent` varchar(6) DEFAULT NULL " +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        if (!ExistTable("Chequestmp")) CommandExecute(sString, null);
    }
    
    public void CreateCaixatmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `caixatmp` ( " +
                "  `cx_aut` double DEFAULT NULL COMMENT 'Numero da Autenticação', " +
                "  `cx_data` datetime DEFAULT NULL COMMENT 'Data Autenticação', " +
                "  `cx_hora` varchar(8) DEFAULT NULL COMMENT 'Hora Autenticação', " +
                "  `cx_logado` varchar(30) DEFAULT NULL COMMENT 'Caixa Logado', " +
                "  `cx_contrato` varchar(6) DEFAULT NULL COMMENT 'Numero do Contrato', " +
                "  `cx_rgprp` varchar(6) DEFAULT NULL COMMENT 'Numero do Proprietário', " +
                "  `cx_rgimv` varchar(6) DEFAULT NULL COMMENT 'Numero do Imóvel', " +
                "  `cx_oper` varchar(3) DEFAULT NULL COMMENT 'Tipo Operação (CRE ou DEB)', " +
                "  `cx_vrdn` decimal(19,4) unsigned DEFAULT NULL COMMENT 'Valor em Dinheiro', " +
                "  `cx_vrch` decimal(19,4) unsigned DEFAULT NULL COMMENT 'Valor em Cheque', " +
                "  `cx_chrel` varchar(255) DEFAULT NULL COMMENT 'Relação dos cheques', " +
                "  `cx_tipopg` varchar(2) DEFAULT NULL COMMENT 'Tipo pag/rec (DN,CH,CP,CT,CD,DP)', " +
                "  `cx_doc` varchar(30) DEFAULT NULL COMMENT 'Documento Emitido', " +
                "  `cx_ndocs` int(11) DEFAULT '0' " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
                
        if (!ExistTable("caixatmp")) CommandExecute(sString, null);
    }
    
    public void CreateRazaotmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `razaotmp` ( " +
                "  `rgprp` varchar(6) NOT NULL DEFAULT '', " +
                "  `rgimv` varchar(6) NOT NULL DEFAULT '', " +
                "  `contrato` varchar(7) NOT NULL DEFAULT '', " +
                "  `campo` text, " +
                "  `dtvencimento` date NOT NULL DEFAULT '0000-00-00', " +
                "  `dtrecebimento` date NOT NULL DEFAULT '0000-00-00', " +
                "  `tag` varchar(1) DEFAULT ' ', " +
                "  `rc_aut` double NOT NULL DEFAULT '0', " +
                "  `rz_aut` double NOT NULL DEFAULT '0', " +
                "  `av_aut` double NOT NULL DEFAULT '0' " +
                ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        if (!ExistTable("razaotmp")) CommandExecute(sString, null);
    }
    
    public void CreateAvisotmp() throws SQLException {
        String sString = "";
        
        sString = "CREATE TABLE `avisostmp` (" +
                "  `autoid` bigint(20) unsigned NOT NULL AUTO_INCREMENT, " +
                "  `rid` varchar(1) DEFAULT NULL, " +
                "  `registro` varchar(6) DEFAULT NULL, " +
                "  `campo` text, " +
                "  `tag` varchar(1) DEFAULT ' ', " +
                "  `autenticacao` int(11) NOT NULL, " +
                "  `et_aut` double NOT NULL DEFAULT '0', " +
                "  PRIMARY KEY (`autoid`) " +
                ") ENGINE=MyISAM AUTO_INCREMENT=12156 DEFAULT CHARSET=latin1";
        if (!ExistTable("avisostmp")) CommandExecute(sString, null);
    }
    
    public void CreateArqAux() throws SQLException {
        String sString = "";

        sString = "CREATE TABLE  `" + VariaveisGlobais.sqlDbName + "`.`auxiliar` (";
        sString += "  `autoid` FLOAT  NOT NULL AUTO_INCREMENT,";
        sString += "  `conta` VARCHAR(3),";
        sString += "  `rgprp` varchar(6) NOT NULL default '',";
        sString += "  `rgimv` varchar(6) NOT NULL default '',";
        sString += "  `contrato` varchar(7) NOT NULL default '',";
        sString += "  `campo` text,";
        sString += "  `dtvencimento` date NOT NULL default '0000-00-00',";
        sString += "  `dtrecebimento` date NOT NULL default '0000-00-00',";
        sString += "  `rc_aut` double NOT NULL default '0',";
        sString += "   PRIMARY KEY (`autoid`)";
        sString += ") ENGINE=MyISAM DEFAULT CHARSET=latin1";

        if (!ExistTable("auxiliar")) CommandExecute(sString, null);        
    }
        
    public void CreateVisitas() throws SQLException {
        String sString = "";
        sString = "CREATE TABLE `" + VariaveisGlobais.sqlDbName + "`.`visitas` (";
        sString += "`ord` int(11) NOT NULL AUTO_INCREMENT,";
        sString += "`rgimv` varchar(6) NOT NULL,";
        sString += "`end` varchar(100) NOT NULL,";
        sString += "`dv_nome` varchar(60) DEFAULT NULL,";
        sString += "`dv_docto` varchar(60) DEFAULT NULL,";
        sString += "`dv_telefone` varchar(15) DEFAULT NULL,";
        sString += "`dv_dthrETD` datetime DEFAULT NULL,";
        sString += "`dv_dthrETA` datetime DEFAULT NULL,";
        sString += "`dv_historico` text,";
        sString += "PRIMARY KEY (`ord`)";
        sString += ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='arquivo contendo visitas a imóvel vazio com opnião dos visitantes'";
        if (!ExistTable("visitas")) CommandExecute(sString, null);        
    }
    
    public boolean ExistTable(String TableName) throws SQLException {
        ResultSet tbl = OpenTable("SHOW TABLES LIKE :tablename;", new Object[][] {{"string", "tablename", TableName.toLowerCase()}});
        tbl.last();
        boolean retorno = tbl.getRow() > 0;
        tbl.beforeFirst();
        CloseTable(tbl);
        return retorno;
    }
    
    public void LancarCheques(String[][] aTrancicao, String nAut) {
        for (int i=0;i<aTrancicao.length;i++) {
            if (!"".equals(aTrancicao[i][1].trim()) && "CRE".equals(aTrancicao[i][6])) {
                String cSql = "INSERT INTO cheques (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_ncaixa, ch_autenticacao) " +
                   "VALUES (:chdata, :chdata2, :chbanco, :chagencia, :chncheque, :chvalor, :chncaixa, :chautenticacao)";

                Object[][] param = new Object[][] {
                    {"date", "chdata", new Date()},
                    {"date", "chdata2", ("".equals(aTrancicao[i][0].trim()) ? null : Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))},
                    {"string", "chbanco", aTrancicao[i][1]},
                    {"string", "chagencia", aTrancicao[i][2]},
                    {"string", "chncheque", aTrancicao[i][3]},
                    {"decimal", "chvalor", new BigDecimal(aTrancicao[i][4])},
                    {"string", "chncaixa", VariaveisGlobais.usuario},
                    {"String", "chautenticacao", nAut}
                };
            
                CommandExecute(cSql, param);
            }
            
            if (!"DN".equals(aTrancicao[i][5]) || !"CT".equals(aTrancicao[i][5])) {
                // ExtBancario
                String cSql = "INSERT INTO extbanco (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_etda, ch_ncaixa, ch_autenticacao) " +
                   "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.')";

                cSql = FuncoesGlobais.Subst(cSql, new String[] {
                    Dates.DateFormata("yyyy-MM-dd", new Date()),
                    ("".equals(aTrancicao[i][0].trim()) ? "0000-00-00" : Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))), 
                    aTrancicao[i][1], 
                    aTrancicao[i][2], 
                    aTrancicao[i][3], 
                    aTrancicao[i][4],
                    aTrancicao[i][5],
                    VariaveisGlobais.usuario, 
                    nAut});

                CommandExecute(cSql, null);
            }
        }        
    }
    
    public void LancarCaixa(String[] oper, String[][] aTranscicao, String nAut) {
        Date now = new Date();
        
        for (int i=0; i<aTranscicao.length; i++) {
            String Sql = "INSERT INTO caixa (cx_aut, cx_data, cx_hora, cx_logado, cx_contrato, cx_rgprp, " + 
            "cx_rgimv, cx_oper, cx_vrdn, cx_vrch, cx_chrel, cx_tipopg, cx_doc, cx_ndocs) " + 
            "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.', '&10.', '&11.', '&12.', '&13.', '&14.')";
            String rel = "";
            String valor = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4]),2)));
            if (aTranscicao[i][5].equals("DN") || aTranscicao[i][5].equals("CT")) {
                rel = (!"".equals(aTranscicao[i][8]) ? "CN:" + aTranscicao[i][8] + " " : "") + aTranscicao[i][5] + ":" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    valor,"0",rel,aTranscicao[i][5],aTranscicao[i][7],"1"});
            } else if (aTranscicao[i][5].equals("CH") || aTranscicao[i][5].equals("CP")) {
                rel = (!"".equals(aTranscicao[i][0]) ? "DT:" + aTranscicao[i][0].trim() + " " : "") + 
                        "BC:" + aTranscicao[i][1].trim() + " AG:" + aTranscicao[i][2].trim() +
                        " CH:" + aTranscicao[i][3].trim() + "             " + " VR:" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    "0",valor, rel ,aTranscicao[i][5],aTranscicao[i][7],"1"});            
            }
            CommandExecute(Sql, null);
        }
        
        if (!"VARIOS_RECIBOS".equals(aTranscicao[aTranscicao.length -1][8].trim().toUpperCase())) {
            LancarCheques(aTranscicao, nAut);
        }
    }

    public void LancarChequestmp(String[][] aTrancicao, String nAut) {
        for (int i=0;i<aTrancicao.length;i++) {
            if (!"".equals(aTrancicao[i][1].trim()) && "CRE".equals(aTrancicao[i][6])) {
                String cSql = "INSERT INTO chequestmp (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_ncaixa, ch_autenticacao) " +
                   "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.')";

                cSql = FuncoesGlobais.Subst(cSql, new String[] {
                    Dates.DateFormata("yyyy-MM-dd", new Date()),
                    ("".equals(aTrancicao[i][0].trim()) ? "0000-00-00" : Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))), 
                    aTrancicao[i][1], 
                    aTrancicao[i][2], 
                    aTrancicao[i][3], 
                    aTrancicao[i][4],
                    VariaveisGlobais.usuario, 
                    nAut});
            
                try {
                    CreateChequestmp();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                CommandExecute(cSql, null);
            }
            
            if (!"DN".equals(aTrancicao[i][5]) || !"CT".equals(aTrancicao[i][5])) {
                // ExtBancario
                String cSql = "INSERT INTO extbancotmp (ch_data, ch_data2, ch_banco, ch_agencia, ch_ncheque, ch_valor, ch_etda, ch_ncaixa, ch_autenticacao) " +
                   "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.')";

                cSql = FuncoesGlobais.Subst(cSql, new String[] {
                    Dates.DateFormata("yyyy-MM-dd", new Date()),
                    ("".equals(aTrancicao[i][0].trim()) ? "0000-00-00" : Dates.DateFormata("yyyy/MM/dd", Dates.StringtoDate(aTrancicao[i][0],"dd/MM/yyyy"))), 
                    aTrancicao[i][1], 
                    aTrancicao[i][2], 
                    aTrancicao[i][3], 
                    aTrancicao[i][4],
                    aTrancicao[i][5],
                    VariaveisGlobais.usuario, 
                    nAut});

                try {
                    CreateExtBancotmp();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                CommandExecute(cSql, null);
            }
        }        
    }
    
    public void LancarCaixatmp(String[] oper, String[][] aTranscicao, String nAut) {
        Date now = new Date();
        
        for (int i=0; i<aTranscicao.length; i++) {
            String Sql = "INSERT INTO caixatmp (cx_aut, cx_data, cx_hora, cx_logado, cx_contrato, cx_rgprp, " + 
            "cx_rgimv, cx_oper, cx_vrdn, cx_vrch, cx_chrel, cx_tipopg, cx_doc, cx_ndocs) " + 
            "VALUES ('&1.', '&2.', '&3.', '&4.', '&5.', '&6.', '&7.', '&8.', '&9.', '&10.', '&11.', '&12.', '&13.', '&14.')";
            String rel = "";
            String valor = String.valueOf(LerValor.StringToFloat(LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4]),2)));
            if (aTranscicao[i][5].equals("DN") || aTranscicao[i][5].equals("CT")) {
                rel = (!"".equals(aTranscicao[i][8]) ? "CN:" + aTranscicao[i][8] + " " : "") + aTranscicao[i][5] + ":" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    valor,"0",rel,aTranscicao[i][5],aTranscicao[i][7],"1"});
            } else if (aTranscicao[i][5].equals("CH") || aTranscicao[i][5].equals("CP")) {
                rel = (!"".equals(aTranscicao[i][0]) ? "DT:" + aTranscicao[i][0].trim() + " " : "") + 
                        "BC:" + aTranscicao[i][1].trim() + " AG:" + aTranscicao[i][2].trim() +
                        " CH:" + aTranscicao[i][3].trim() + "             " + " VR:" + LerValor.floatToCurrency(Float.valueOf(aTranscicao[i][4].trim()),2);
                Sql = FuncoesGlobais.Subst(Sql, new String[] {
                    nAut, Dates.DateFormata("yyyy-MM-dd", now), Dates.DateFormata("HH:mm:ss", now),
                    VariaveisGlobais.usuario, oper[2], oper[0], oper[1], aTranscicao[i][6], 
                    "0",valor, rel ,aTranscicao[i][5],aTranscicao[i][7],"1"});            
            }
            
            try {
                CreateCaixatmp();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            CommandExecute(Sql, null);
        }
        
        if (!"VARIOS_RECIBOS".equals(aTranscicao[aTranscicao.length -1][8].trim().toUpperCase())) {
            LancarChequestmp(aTranscicao, nAut);
        }
    }

    public void Auditor(String cVelho, String cNovo) {
        if (!ExisteTabelaAuditor()) return;
        
        try {
            CommandExecute("INSERT INTO auditor (usuario, datahora, origem, maquina, velho, novo) VALUES ('" +
            VariaveisGlobais.usuario + "','" + Dates.DateFormata("yyyy-MM-dd hh:mm:ss", new java.util.Date()) +
            "','" + VariaveisGlobais.marca + "','" + VariaveisGlobais.sqlHost + "','" +
            cVelho.toUpperCase() + "','" + cNovo.toUpperCase() + "')", null);
        } catch (Exception err) {}        
    }
    
    private boolean ExisteTabelaAuditor() {
        boolean ret = true;
        String sql = "CREATE TABLE `auditor` ( " +
                     "`usuario` varchar(25) DEFAULT NULL, " +
                     "`datahora` datetime DEFAULT NULL, " +
                     "`velho` varchar(255) DEFAULT NULL, " +
                     "`novo` varchar(255) DEFAULT NULL, " +
                     "`origem` varchar(30) DEFAULT NULL, " +
                     "`maquina` varchar(60) DEFAULT NULL " +
                     ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

        try {
            if (!ExistTable("auditor")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaFichas() {
        boolean ret = true;
        String sql = "CREATE TABLE `fichas` (\n" +
        "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
        "  `contrato` varchar(6) DEFAULT NULL,\n" +
        "  `dtvencimento` varchar(10) DEFAULT NULL,\n" +
        "  `anotacoes` longtext,\n" +
        "  PRIMARY KEY (`id`)\n" +
        ") ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='fichas amarelas'";
        
        try {
            if (!ExistTable("fichas")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaBloquetos() {
        boolean ret = true;
        String sql = "CREATE TABLE `bloquetos` (\n" +
                    "  `rgprp` varchar(6) DEFAULT NULL,\n" +
                    "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                    "  `contrato` varchar(6) DEFAULT NULL,\n" +
                    "  `nome` varchar(70) DEFAULT NULL,\n" +
                    "  `vencimento` datetime DEFAULT NULL,\n" +
                    "  `valor` varchar(15) DEFAULT NULL,\n" +
                    "  `nnumero` varchar(20) DEFAULT NULL,\n" +
                    "  `remessa` varchar(1) DEFAULT 'N',\n" +
                    ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
        
        try {
            if (!ExistTable("bloquetos")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteTabelaConcessionarias() {
        boolean ret = true;
        String sql = "CREATE  TABLE `concessionarias` (\n" +
                    "  `codigo` VARCHAR(2) NULL ,\n" +
                    "  `idconta` VARCHAR(3) NULL ,\n" +
                    "  `matricula` VARCHAR(4) NULL ,\n" +
                    "  `valor` VARCHAR(4) NULL ,\n" +
                    "  `vencimento` VARCHAR(4) NULL ,\n" +
                    "  `vctoformato` VARCHAR(4) NULL );";
        
        try {
            if (!ExistTable("Concessionarias")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaataxas() {
        boolean ret = true;
        String sql = "CREATE TABLE `ataxas` (\n" +
                    "  `idataxas` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `tipo` varchar(1) DEFAULT NULL,\n" +
                    "  `matricula` varchar(20) DEFAULT NULL,\n" +
                    "  `vencimento` date DEFAULT NULL,\n" +
                    "  `valor` decimal(10,2) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`idataxas`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("ataxas")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaAntecip() {
        boolean ret = true;
        String sql = "CREATE TABLE `antecipados` (\n" +
                     "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                     "  `rgprp` varchar(6) DEFAULT NULL,\n" +
                     "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                     "  `contrato` varchar(6) DEFAULT NULL,\n" +
                     "  `campo` text,\n" +
                     "  `dtvencimento` date DEFAULT NULL,\n" +
                     "  `dtpagamento` date DEFAULT NULL,\n" +
                     "  `at_aut` double DEFAULT NULL,\n" +
                     "  `dtrecebimento` date DEFAULT NULL,\n" +
                     "  `rc_aut` double DEFAULT NULL,\n" +
                     "  PRIMARY KEY (`id`)\n" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("antecipados")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public boolean ExisteTabelaAdAviso() {
        boolean ret = true;
        String sql = "CREATE TABLE `adavisos` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `rgprp` varchar(6) DEFAULT NULL,\n" +
                    "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                    "  `contrato` varchar(6) DEFAULT NULL,\n" +
                    "  `texto` text,\n" +
                    "  `valor` float DEFAULT NULL,\n" +
                    "  `tipo` varchar(1) DEFAULT NULL,\n" +
                    "  `vencimento` date DEFAULT NULL,\n" +
                    "  `data` date DEFAULT NULL,\n" +
                    "  `logado` varchar(20) DEFAULT NULL,\n" +
                    "  `ad_aut` double DEFAULT '0',\n" +
                    "  `et_aut` double DEFAULT '0',\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("adavisos")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteTabelaiptu() {
        boolean ret = true;
        String sql = "CREATE TABLE `iptu` (\n" +
                    "  `ano` varchar(4) DEFAULT NULL,\n" +
                    "  `rgimv` varchar(6) DEFAULT NULL,\n" +
                    "  `matricula` varchar(20) DEFAULT NULL,\n" +
                    "  `jan` varchar(100) DEFAULT NULL,\n" +
                    "  `fev` varchar(100) DEFAULT NULL,\n" +
                    "  `mar` varchar(100) DEFAULT NULL,\n" +
                    "  `abr` varchar(100) DEFAULT NULL,\n" +
                    "  `mai` varchar(100) DEFAULT NULL,\n" +
                    "  `jun` varchar(100) DEFAULT NULL,\n" +
                    "  `jul` varchar(100) DEFAULT NULL,\n" +
                    "  `ago` varchar(100) DEFAULT NULL,\n" +
                    "  `set` varchar(100) DEFAULT NULL,\n" +
                    "  `out` varchar(100) DEFAULT NULL,\n" +
                    "  `nov` varchar(100) DEFAULT NULL,\n" +
                    "  `dez` varchar(100) DEFAULT NULL,\n" +
                    "  `ord` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  PRIMARY KEY (`ord`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("iptu")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteTabelaDimob() {
        boolean ret = true;
        String sql = "CREATE TABLE `dimob` (\n" +
                    "  `autonum` double NOT NULL AUTO_INCREMENT,\n" +
                    "  `rgprp` varchar(6) NOT NULL, \n" +
                    "  `rgimv` varchar(6) NOT NULL, \n" +
                    "  `contrato` varchar(6) NOT NULL, \n" +
                    "  `cpfcnpjlocador` varchar(14) NOT NULL,\n" +
                    "  `nomelocador` varchar(60) NOT NULL,\n" +
                    "  `cpfcnpjlocatario` varchar(14) NOT NULL,\n" +
                    "  `nomelocatario` varchar(60) NOT NULL,\n" +
                    "  `numerocontrato` varchar(6) NOT NULL,\n" +
                    "  `datacontrato` varchar(8) NOT NULL,\n" +
                    "  `valorjan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaojan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostojan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfjan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcjan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mujan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jujan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cojan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tejan` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorfev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaofev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostofev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dffev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcfev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mufev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jufev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cofev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tefev` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valormar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaomar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostomar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfmar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcmar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mumar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jumar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `temar` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teabr` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valormai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaomai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostomai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfmai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcmai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mumai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jumai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `temai` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorjun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaojun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostojun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfjun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcjun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mujun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jujun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cojun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tejun` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorjul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaojul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostojul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfjul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcjul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mujul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `jujul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `cojul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tejul` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teago` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teset` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valorout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaoout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostoout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `muout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `juout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `coout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `teout` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valornov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaonov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostonov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfnov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcnov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `munov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `junov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `conov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tenov` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `valordez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `comissaodez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `impostodez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dfdez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `dcdez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `mudez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `judez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `codez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tedez` varchar(14) NOT NULL DEFAULT '00000000000000',\n" +
                    "  `tipoimovel` varchar(1) NOT NULL DEFAULT 'U',\n" +
                    "  `endimovel` varchar(60) NOT NULL,\n" +
                    "  `cepimovel` varchar(8) NOT NULL,\n" +
                    "  `codmunimovel` varchar(4) NOT NULL,\n" +
                    "  `ufimovel` varchar(2) NOT NULL,\n" +
                    "  PRIMARY KEY (`autonum`)\n" +
                    ") ENGINE=MyISAM AUTO_INCREMENT=9742 DEFAULT CHARSET=latin1;";
        try {
            if (!ExistTable("dimob")) {
                CommandExecute(sql, null);
            }
        } catch (Exception e) {ret = false;}
        
        return ret;
    }

    public boolean ExisteFuncRMVADIANTA() {
        boolean ret = true;
        String sql;
        sql = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`RMVADIANTA`";
        CommandExecute(sql, null);

        sql = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`rmvAdianta`(sCampo LONGTEXT) RETURNS longtext CHARSET latin1\n" +
              "BEGIN\n" +
              "	declare iPos int;\n" +
              "	declare tCampos LONGTEXT;\n" +
              "	set iPos = InStr(sCampo,':AD');\n" +
              "	set tCampos = MID(sCampo, iPos, 20);\n" +
              "RETURN Replace(sCampo,tCampos,'');\n" +
              "END;";
        try {
            CommandExecute(sql, null);
        } catch (Exception e) {ret = false;}
        
        return ret;
    }
    
    public void BancoPix() {
        String createSQL = "CREATE TABLE IF NOT EXISTS `" + VariaveisGlobais.sqlDbName + "`.`bancos_pix` (`id` int(11) NOT NULL AUTO_INCREMENT, `tipo` varchar(10) COLLATE utf8_bin DEFAULT NULL, `chave` varchar(45) COLLATE utf8_bin DEFAULT NULL, `banco` varchar(45) COLLATE utf8_bin DEFAULT NULL, `nnumero` int(11) DEFAULT '0', PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";
        try { CommandExecute(createSQL, null); } catch (Exception e) {}
        return;
    }

    public Timestamp getDateTimeServer() {
        Timestamp datahora = null;
        String sql = "select current_timestamp datahora;";
        ResultSet rs = null;
        try {
            rs = OpenTable(sql, null);
        } catch (Exception e) { return null; }
        try {
            while (rs.next()) {
                datahora = rs.getTimestamp("datahora");
            }
        } catch (SQLException e) {datahora = new Timestamp(System.currentTimeMillis());}
        CloseTable(rs);
        return datahora;
    }    
    
}