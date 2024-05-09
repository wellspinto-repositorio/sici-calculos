package Funcoes;

public class ProceduresSQL {
    Db conn = VariaveisGlobais.conexao;
    
    public boolean GeraMovto(String cParam) {
        String sqlPROC = "DROP PROCEDURE IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`GeraMovto`";
        conn.CommandExecute(sqlPROC);
        String SQLproc  = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` PROCEDURE `" + VariaveisGlobais.sqlDbName + "`.`GeraMovto`()\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE GER_AL_IN00 BOOLEAN DEFAULT " + VariaveisGlobais.GER_AL_IN00 + ";";
               SQLproc += "\tDECLARE eBol Int DEFAULT 0;\n"; 
               SQLproc += "\tDECLARE sRgp VARCHAR(6);\n"; 
               SQLproc += "\tDECLARE sRgi VARCHAR(6);\n";
               SQLproc += "\tDECLARE sCtr VARCHAR(6);\n";
               SQLproc += "\tDECLARE sVecto VARCHAR(10);\n";
               SQLproc += "\tDECLARE sCampo LONGTEXT;\n";
               SQLproc += "\tDECLARE sBusca LONGTEXT;\n";
               SQLproc += "\tDECLARE sUpCpo LONGTEXT;\n";
               SQLproc += "\tDECLARE iPos Int;\n";
               SQLproc += "\tDECLARE sDesc LONGTEXT;\n";
               SQLproc += "\tDECLARE sDife LONGTEXT;\n";
               SQLproc += "\tDECLARE sSegu LONGTEXT;\n";
               SQLproc += "\tDECLARE done BOOLEAN;\n";
               SQLproc += "\tDECLARE sCp LONGTEXT;\n";
               SQLproc += "\tDECLARE sPart1 VARCHAR(2);\n";
               SQLproc += "\tDECLARE sPart2 VARCHAR(4);\n";
               SQLproc += "\tDECLARE cur1 CURSOR FOR SELECT l.boleta, c.rgprp, c.rgimv, c.contrato, c.dtvencimento, c.campo FROM carteira c, locatarios l WHERE c.contrato = l.contrato" + ("".equals(cParam.trim()) ? ";" : " AND (" + cParam + ");") + "\n";
               SQLproc += "\tDECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;\n\n";
               SQLproc += "\tOPEN cur1;\n";
               SQLproc += "\tMLOOP: LOOP\n";
               SQLproc += "\tFETCH cur1 INTO eBol, SRgp, sRgi, sCtr, sVecto, sCampo;\n";   
               SQLproc += "\tIF done THEN LEAVE MLOOP; END IF;\n";
               SQLproc += "\tSET sDesc = gDesconto(sCtr, Right(sVecto, 7));\n";
               SQLproc += "\tSET sDife = gDiferenca(sCtr, Right(sVecto, 7));\n";
               SQLproc += "\tSET sSegu = gSeguro(sCtr, Right(sVecto, 7));\n";
               SQLproc += "\tSET sBusca = sCampo;\n";
               SQLproc += "\tWHILE InStr(sBusca, ':*') > 0 DO\n";
               SQLproc += "\t\tSET sBusca = Replace(sBusca, ':*','');\n";
               SQLproc += "\tEND WHILE;\n";
               SQLproc += "\tSET sUpCpo = sBusca;\n";
               SQLproc += "\tSET iPos = InStr(sBusca, ':DV');\n";
               SQLproc += "\tIF iPos > 0 THEN\n";
               SQLproc += "\t\tSET sBusca = CONCAT(Left(sBusca, iPos - 1), Mid(sBusca, iPos + 5));\n";
               SQLproc += "\tEND IF;\n";
               SQLproc += "\tSET sBusca = Replace(CONCAT(sBusca, ';', sDesc, ';', sDife, ';', sSegu), '::',':');\n";
               SQLproc += "\tWHILE InStr(sBusca, '::') > 0 DO\n";
               SQLproc += "\t\tSET sBusca = Replace(sBusca, '::',':');\n";
               SQLproc += "\tEND WHILE;\n";
               SQLproc += "\tWHILE InStr(sBusca, ';;') > 0 DO\n";
               SQLproc += "\t\tSET sBusca = Replace(sBusca, ';;',';');\n";
               SQLproc += "\tEND WHILE;\n";
               SQLproc += "\tIF Left(sBusca, 1) = ';' THEN SET sBusca = MID(sBusca,2); END IF;\n";
               SQLproc += "\tIF Right(sBusca, 1) = ';' THEN SET sBusca = MID(sBusca,1,LENGTH(sBusca) - 1); END IF;\n";
               SQLproc += "\tIF GER_AL_IN00 THEN\n";
               SQLproc += "\t\tINSERT INTO tmprecibo (rgprp, rgimv, contrato, campo, dtvencimento, tag) VALUES(sRgp, sRgi, sCtr, sBusca, CONCAT(Right(sVecto, 4), '-',MID(sVecto,4,2),'-',LEFT(sVecto,2)), ' ');\n";
               SQLproc += "\t\tUPDATE carteira SET dtvencimento = ProxVecto(sUpCpo, sVecto), campo = AtRemove(CotaParc(campo)) WHERE contrato = sCtr;\n";
               SQLproc += "\tELSE\n";
               SQLproc += "\t\tIF Mid(sBusca,1,5) = '01:1:' AND InStr(sBusca, 'AL') > 0 THEN\n";
               SQLproc += "\t\t\tSET sCp = Mid(sBusca, 17, 6);\n";
               SQLproc += "\t\t\tIF Mid(sCp, 5, 1) = ':' THEN SET sCp = LEFT(sCp,4); END IF;\n";
               SQLproc += "\t\t\tIF CHAR_LENGTH(sCp) = 4 THEN\n";
               SQLproc += "\t\t\t\tSET sPart1 = LEFT(TRIM(sCp),2);\n";
               SQLproc += "\t\t\t\tSET sPart2 = RIGHT(TRIM(sCp),2);\n";
               SQLproc += "\t\t\tELSE\n";
               SQLproc += "\t\t\t\tSET sPart1 = LEFT(TRIM(sCp),2);\n";
               SQLproc += "\t\t\t\tSET sPart2 = RIGHT(TRIM(sCp),4);\n";
               SQLproc += "\t\t\tEND IF;\n";
               SQLproc += "\t\t\tIF CAST(sPart1 AS UNSIGNED INTEGER) != 0 AND CAST(sPart2 AS UNSIGNED INTEGER) != 0 THEN\n";
               SQLproc += "\t\t\t\tINSERT INTO tmprecibo (rgprp, rgimv, contrato, campo, dtvencimento, tag) VALUES(sRgp, sRgi, sCtr, sBusca, CONCAT(Right(sVecto, 4), '-',MID(sVecto,4,2),'-',LEFT(sVecto,2)), ' ');\n";
               SQLproc += "\t\t\t\tUPDATE carteira SET dtvencimento = ProxVecto(sUpCpo, sVecto), campo = AtRemove(CotaParc(campo)) WHERE contrato = sCtr;\n";
               SQLproc += "\t\t\tEND IF;\n";
               SQLproc += "\t\tEND IF;\n";
               SQLproc += "\tEND IF;\n";
               SQLproc += "\tEND LOOP MLOOP;\n";
               SQLproc += "\tCLOSE cur1;\n";
               SQLproc += "\tINSERT INTO recibo (rgprp,rgimv,contrato,campo,dtvencimento,tag) select rgprp,rgimv,contrato,campo,dtvencimento,tag FROM tmprecibo;\n";
               SQLproc += "\tDELETE FROM tmprecibo;\n";
               SQLproc += "END";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public void CriarMySqlProcedures(String qual, String cParam) {
        boolean retorno = true;
        
        if (("ALL".equals(qual.toUpperCase()) || "GERAMOVTO".equals(qual.toUpperCase())) && cParam != null) {
            GeraMovto(cParam);
        } 
        if ("ALL".equals(qual.toUpperCase()) || "ATREMOVE".equals(qual.toUpperCase())) {
            AtRemove();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "ATUNUPGRADE".equals(qual.toUpperCase())) {
            AtUnUpgrade();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "COTAPARC".equals(qual.toUpperCase())) {
            CotaParc();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "COUNTSTR".equals(qual.toUpperCase())) {
            CountStr();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "CRIPTANOME".equals(qual.toUpperCase())) {
            CriptaNome();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "DECRIPTANOME".equals(qual.toUpperCase())) {
            DecriptaNome();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "GDESCONTO".equals(qual.toUpperCase())) {
            gDesconto();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "GDIFERENCA".equals(qual.toUpperCase())) {
            gDiferenca();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "GSEGURO".equals(qual.toUpperCase())) {
            gSeguro();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "JGERAL".equals(qual.toUpperCase())) {
            jgeral();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "PEGARECIBO".equals(qual.toUpperCase())) {
            PegaRecibo();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "PLUSVAL".equals(qual.toUpperCase())) {
            plusVal();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "PROXVECTO".equals(qual.toUpperCase())) {
            ProxVecto();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "PROXVECTO2".equals(qual.toUpperCase())) {
            ProxVecto2();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "RCAMPOS".equals(qual.toUpperCase())) {
            RCampos();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "RETAVDATARID2".equals(qual.toUpperCase())) {
            RetAvDataRid2();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "RETAVDESCRID2".equals(qual.toUpperCase())) {
            RetAvDescRid2();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "RETAVTIPORID2".equals(qual.toUpperCase())) {
            RetAvTipoRid2();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "RETAVUSERRID2".equals(qual.toUpperCase())) {
            RetAvUserRid2();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "RETAVVALORRID2".equals(qual.toUpperCase())) {
            RetAvValorRid2();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "STRDATE".equals(qual.toUpperCase())) {
            StrDate();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "STRVAL".equals(qual.toUpperCase())) {
            StrVal();
        } 
        if ("ALL".equals(qual.toUpperCase()) || "VALSTR".equals(qual.toUpperCase())) {
            ValStr();
        }      
        if ("ALL".equals(qual.toUpperCase()) || "REMOVE_ACCENTS".equals(qual.toUpperCase())) {
            remove_accents();
        }      
    }
    
    public boolean AtRemove() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`AtRemove`";
        conn.CommandExecute(sqlPROC);
        String SQLproc  = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`AtRemove`(sCampo LONGTEXT) RETURNS longtext CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE iPos Int;\n";
               SQLproc += "\tDECLARE sCp LONGTEXT;\n";
               SQLproc += "\tDECLARE sCpos LONGTEXT;\n"; 
               SQLproc += "\tDECLARE sAux LONGTEXT;\n"; 
               SQLproc += "\tSET sCpos = '';\n";
               SQLproc += "\tSET sCampo = CONCAT(sCampo,';');\n";
               SQLproc += "\tSET iPos = InStr(sCampo, ';');\n";
               SQLproc += "\tWHILE iPos > 0 DO\n";
               SQLproc += "\t\tSET sAux = Mid(sCampo, 1, iPos - 1);\n";
               SQLproc += "\t\tSET sCampo = Mid(sCampo, iPos + 1);\n";
               SQLproc += "\t\tSET sCp = Mid(sAux, 22, 8);\n";
               SQLproc += "\t\tIF AscII(Mid(sCp, 1, 1)) >= 65 AND AscII(Mid(sCp,1,1)) <= 90 THEN SET sCp = ''; END IF;\n";
               SQLproc += "\t\tif sCp != '' AND InStr(sAux,':AT') > 0 THEN\n";
               SQLproc += "\t\t\tSET sAux = REPLACE(sAux,CONCAT(':',sCp),'');\n";
               SQLproc += "\t\tEnd If;\n";
               SQLproc += "\t\tSET sCpos = CONCAT(sCpos, ';', sAux);\n";
               SQLproc += "\t\tSET iPos = InStr(sCampo, ';');\n";
               SQLproc += "\tEND WHILE;\n";
               SQLproc += "\tRETURN MID(sCpos,2);\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean AtUnUpgrade() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`AtUnUpgrade`";
        conn.CommandExecute(sqlPROC);
        String SQLproc =  "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`AtUnUpgrade`(sCampo LONGTEXT) RETURNS tinyint(1)\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE iPos Int; \n" ;
               SQLproc += "\tDECLARE sCp LONGTEXT; \n";
               SQLproc += "\tDECLARE sAux LONGTEXT; \n";
               SQLproc += "\tDECLARE bRet boolean;\n";
               SQLproc += "\tSET bRet = false;\n";
               SQLproc += "\tSET sCampo = CONCAT(sCampo,';'); \n";
               SQLproc += "\tSET iPos = InStr(sCampo, ';'); \n";
               SQLproc += "\tWHILE iPos > 0 DO   \n";
               SQLproc += "\t\tSET sAux = Mid(sCampo, 1, iPos - 1);\n";
               SQLproc += "\t\tSET sCampo = Mid(sCampo, iPos + 1);\n";
               SQLproc += "\t\tSET sCp = Mid(sAux, 22, 8);\n";
               SQLproc += "\t\tIF AscII(Mid(sCp, 1, 1)) >= 65 AND AscII(Mid(sCp,1,1)) <= 90 THEN SET sCp = ''; END IF;\n";
               SQLproc += "\t\tif sCp = '' AND InStr(sAux,':AT') > 0 THEN\n";
               SQLproc += "\t\t\tSET bRet = true;\n";
               SQLproc += "\t\tEnd If;\n";
               SQLproc += "\t\tSET iPos = InStr(sCampo, ';');\n";
               SQLproc += "\tEND WHILE ; \n";
               SQLproc += "\tRETURN bRet;\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }

    public boolean CotaParc() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`CotaParc`";
        conn.CommandExecute(sqlPROC);
        String SQLproc =  "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`CotaParc`(sCampo LONGTEXT) RETURNS longtext CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE GER_NT_IN00 LONGTEXT DEFAULT \"" + VariaveisGlobais.GER_NT_IN00 + "\";\n";
               SQLproc += "\tDECLARE iPos Int; \n";
               SQLproc += "\tDECLARE sCp LONGTEXT; \n";
               SQLproc += "\tDECLARE sCpos LONGTEXT; \n";
               SQLproc += "\tDECLARE sAux LONGTEXT; \n";
               SQLproc += "\tDECLARE sPart1 VARCHAR(2); \n";
               SQLproc += "\tDECLARE sPart2 VARCHAR(4); \n";
               SQLproc += "\tSET sPart1 = '00'; \n";
               SQLproc += "\tSET sPart2 = '0000'; \n";
               SQLproc += "\tSET sCpos = ''; \n";
               SQLproc += "\tSET sCampo = CONCAT(sCampo,';'); \n";
               SQLproc += "\tSET iPos = InStr(sCampo, ';'); \n";
               SQLproc += "\tWHILE iPos > 0 DO   \n";
               SQLproc += "\t\tSET sAux = Mid(sCampo, 1, iPos - 1);\n";
               SQLproc += "\t\tSET sCampo = Mid(sCampo, iPos + 1);\n";
               SQLproc += "\t\tSET sCp = Mid(sAux, 17, 6);\n";
               SQLproc += "\t\tIF Mid(sCp, 5, 1) = ':' THEN SET sCp = LEFT(sCp,4); END IF;\n";
               SQLproc += "\t\tIF CHAR_LENGTH(sCp) = 4 THEN\n";
               SQLproc += "\t\t\tSET sPart1 = LEFT(TRIM(sCp),2);\n";
               SQLproc += "\t\t\tSET sPart2 = RIGHT(TRIM(sCp),2);\n";
               SQLproc += "\t\t\tIF sPart1 = sPart2 AND sPart1 <> '00' AND sPart2 <> '00' THEN\n";
               SQLproc += "\t\t\t\tSET sPart1 = '00';\n";
               SQLproc += "\t\t\t\tSET sPart2 = '00';\n";
               SQLproc += "\t\t\tELSEIF CAST(sPart1 AS UNSIGNED INTEGER) < CAST(sPart2 AS UNSIGNED INTEGER) THEN\n";
               SQLproc += "\t\t\t\tSET sPart1 = RIGHT(CONCAT('00',CAST(sPart1 AS UNSIGNED INTEGER) + 1),2);\n";
               SQLproc += "\t\t\tEND IF;\n";
               SQLproc += "\t\t\tSET sAux = REPLACE(sAux,sCp,CONCAT(sPart1,sPart2));\n";
               SQLproc += "\t\tELSE\n";
               SQLproc += "\t\t\tSET sPart1 = LEFT(TRIM(sCp),2);\n";
               SQLproc += "\t\t\tSET sPart2 = RIGHT(TRIM(sCp),4);\n";
               SQLproc += "\t\t\tIF sPart1 = '12' AND sPart1 <> '00' AND sPart2 <> '0000' THEN\n";
               SQLproc += "\t\t\t\tSET sPart1 = '00';\n";
               SQLproc += "\t\t\t\tSET sPart2 = '0000';\n";
               SQLproc += "\t\t\tELSEIF CAST(sPart1 AS UNSIGNED INTEGER) < 12 THEN\n";
               SQLproc += "\t\t\t\tSET sPart1 = RIGHT(CONCAT('00',CAST(sPart1 AS UNSIGNED INTEGER) + 1),2);\n";
               SQLproc += "\t\t\tEND IF;\n";
               SQLproc += "\t\t\tSET sAux = REPLACE(sAux,sCp,CONCAT(sPart1,sPart2));\n";
               SQLproc += "\t\tEND IF;\n";
               SQLproc += "\t\tIF InStr(sAux, 'NT') > 0 AND CAST(sPart1 AS UNSIGNED INTEGER) = 0 AND CAST(sPart2 AS UNSIGNED INTEGER) = 0 THEN\n";
               SQLproc += "\t\t\tIF GER_NT_IN00 = 'ZERAR' THEN\n";
               SQLproc += "\t\t\t\tSET sAux = CONCAT(Mid(sAux,1,5),'0000000000',Mid(sAux,16));\n";
               SQLproc += "\t\t\tELSEIF GER_NT_IN00 = 'APAGAR' THEN\n";
               SQLproc += "\t\t\t\tSET sAux = '';\n";
               SQLproc += "\t\t\tEND IF;\n";
               SQLproc += "\t\tEND IF;\n";
               SQLproc += "\t\tIF sAux <> '' THEN\n";
               SQLproc += "\t\t\tSET sCpos = CONCAT(sCpos, ';', sAux);\n";
               SQLproc += "\t\t END IF;\n";
               SQLproc += "\t\tSET iPos = InStr(sCampo, ';'); \n";
               SQLproc += "\tEND WHILE ;\n";
               SQLproc += "\tRETURN MID(sCpos,2);\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean CountStr() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`CountStr`";
        conn.CommandExecute(sqlPROC);
        String SQLproc =  "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`CountStr`(cCampo LongText, patern VarChar(30)) RETURNS int(11)\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE nStr Int; \n";
               SQLproc += "\tDECLARE nRet Int; \n";
               SQLproc += "\tSET i = 1; \n";
               SQLproc += "\tSET nStr = LENGTH(cCampo); \n";
               SQLproc += "\tSET nRet = 0; \n";
               SQLproc += "\tWHILE i <= nStr DO \n";
               SQLproc += "\t\tIF Mid(cCampo, i, 1) = Trim(patern) THEN SET nRet = nRet + 1; END IF;\n";
               SQLproc += "\t\tSET i = i + 1; \n";
               SQLproc += "\tEND WHILE; \n";
               SQLproc += "\tRETURN nRet; \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean CriptaNome() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`CriptaNome`";
        conn.CommandExecute(sqlPROC);
        String SQLproc =  "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`CriptaNome`(sValue text) RETURNS text CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE i Int;\n";
               SQLproc += "\tDECLARE iLen Int;\n";
               SQLproc += "\tDECLARE sRet text;\n";
               SQLproc += "\tDECLARE sChar CHAR(2);	\n";
               SQLproc += "\tSET sRet = ''; \n";
               SQLproc += "\tSET i = 1; \n";
               SQLproc += "\tSET iLen = LENGTH(UCase(Trim(sValue))); \n";
               SQLproc += "\tIF iLen > 0 THEN \n";
               SQLproc += "\t\tWHILE i <= iLen DO \n";
               SQLproc += "\t\t\tSET sChar = CAST(ASCII(Mid(UCase(Trim(sValue)), i, 1)) AS CHAR(2));\n";
               SQLproc += "\t\t\tSET sRet = CONCAT(sRet, sChar);\n";
               SQLproc += "\t\t\tSET i = i + 1;\n";
               SQLproc += "\t\tEND WHILE;\n";
               SQLproc += "\tEND IF;\n";
               SQLproc += "\tRETURN sRet;\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean DecriptaNome() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`DecriptaNome`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`DecriptaNome`(sValue text) RETURNS text CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE i Int;\n";
               SQLproc += "\tDECLARE iLen Int; \n";
               SQLproc += "\tDECLARE sRet text; \n";
               SQLproc += "\tDECLARE Letra CHAR(2);\n";
               SQLproc += "\tDECLARE passo Int;\n";
               SQLproc += "\tSET i = 1;\n";
               SQLproc += "\tSET passo = 2;\n";
               SQLproc += "\tSET iLen = LENGTH(UCase(Trim(sValue))); \n";
               SQLproc += "\tIF iLen > 0 THEN \n";
               SQLproc += "\t\tWHILE i <= iLen DO\n";
               SQLproc += "\t\t\tSET Letra = SUBSTRING(sValue, i, passo);\n";
               SQLproc += "\t\t\tSET sRet = CONCAT_WS('', sRet, Char(Letra));\n";
               SQLproc += "\t\t\tSET i = i + passo;\n";
               SQLproc += "\t\tEND WHILE; \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tRETURN sRet;\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean gDesconto() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`gDesconto`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`gDesconto`(sContrato CHAR(6), sReferencia CHAR(7)) RETURNS longtext CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE a LONGTEXT; \n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE b LONGTEXT; \n";
               SQLproc += "\tDECLARE done BOOLEAN DEFAULT FALSE; \n";
               SQLproc += "\tDECLARE cur1 CURSOR FOR SELECT CONCAT( 'DC:2:',valor,':','0000',':DC:',sigla,':DS',CriptaNome(descricao)) AS campo FROM descontos WHERE contrato = sContrato and referencia = sReferencia; \n";
               SQLproc += "\tDECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; \n";
               SQLproc += "\tSET i = 0; \n";
               SQLproc += "\tOPEN cur1; \n";
               SQLproc += "\tREPEAT FETCH cur1 INTO a; \n";
               SQLproc += "\tSET b = CONCAT_WS( ';',b,a); \n";
               SQLproc += "\tSET i = i + 1; \n";
               SQLproc += "\tIF i >= ( SELECT COUNT(referencia) FROM descontos WHERE contrato = sContrato AND referencia = sReferencia) THEN \n";
               SQLproc += "\t\tSET done = TRUE; \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tUNTIL done END REPEAT; \n";
               SQLproc += "\tCLOSE cur1; \n";
               SQLproc += "\tRETURN Trim(b); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean gDiferenca() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`gDiferenca`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`gDiferenca`(sContrato CHAR(6), sReferencia CHAR(7)) RETURNS longtext CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE a LONGTEXT; \n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE b LONGTEXT; \n";
               SQLproc += "\tDECLARE done BOOLEAN DEFAULT FALSE; \n";
               SQLproc += "\tDECLARE cur1 CURSOR FOR SELECT CONCAT( 'DF:2:',valor,':','0000',':DF:',sigla,':DS',CriptaNome(descricao)) AS campo FROM diferenca WHERE contrato = sContrato and referencia = sReferencia; \n";
               SQLproc += "\tDECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; \n";
               SQLproc += "\tSET i = 0; \n";
               SQLproc += "\tOPEN cur1; \n";
               SQLproc += "\tREPEAT FETCH cur1 INTO a; \n";
               SQLproc += "\tSET b = CONCAT_WS( ';',b,a); SET i = i + 1; \n";
               SQLproc += "\tIF i >= ( SELECT COUNT(referencia) FROM diferenca WHERE contrato = sContrato AND referencia = sReferencia) THEN SET done = TRUE; END IF ; \n";
               SQLproc += "\tUNTIL done END REPEAT; \n";
               SQLproc += "\tCLOSE cur1; \n";
               SQLproc += "\tRETURN Trim(b); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean gSeguro() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`gSeguro`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`gSeguro`(sContrato CHAR(6), sReferencia CHAR(7)) RETURNS longtext CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE a LONGTEXT; \n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE b LONGTEXT; \n";
               SQLproc += "\tDECLARE done BOOLEAN DEFAULT FALSE; \n";
               SQLproc += "\tDECLARE cur1 CURSOR FOR SELECT CONCAT( 'SG:3:',valor,':','0000',':SG:',sigla) AS campo FROM seguros WHERE contrato = sContrato and referencia = sReferencia; \n";
               SQLproc += "\tDECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE; \n";
               SQLproc += "\tSET i = 0; \n";
               SQLproc += "\tOPEN cur1; \n";
               SQLproc += "\tREPEAT FETCH cur1 INTO a; \n";
               SQLproc += "\tSET b = CONCAT_WS( ';',b,a); \n";
               SQLproc += "\tSET i = i + 1; \n";
               SQLproc += "\tIF i >= ( SELECT COUNT(referencia) FROM seguros WHERE contrato = sContrato AND referencia = sReferencia) THEN SET done = TRUE; END IF ; \n";
               SQLproc += "\tUNTIL done END REPEAT; \n";
               SQLproc += "\tCLOSE cur1; \n";
               SQLproc += "\tRETURN Trim(b); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean jgeral() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`jgeral`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`jgeral`(sValue Text) RETURNS text CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tRETURN RIGHT(sValue,INSTR(REVERSE(sValue),':') -1);";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean PegaRecibo() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`PegaRecibo`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`PegaRecibo`(contrato longtext) RETURNS char(10) CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tRETURN DATE_FORMAT((SELECT r.dtvencimento FROM recibo r WHERE r.contrato = contrato LIMIT 0,1),'%Y-%m-%d');\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean plusVal() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`plusVal`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`plusVal`(sValue Char(10), sTipo Char(3)) RETURNS decimal(10,2)\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE nRet DECIMAL(10, 2); \n";
               SQLproc += "\tSET nRet = StrVal(sValue); \n";
               SQLproc += "\tif sTipo = 'DEB' THEN SET nRet = nRet * -1; END IF; \n";
               SQLproc += "\tRETURN nRet; \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean ProxVecto() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`ProxVecto`";
        conn.CommandExecute(sqlPROC);
        String SQLproc =  "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`ProxVecto`(sCampo LongText, sVecto Char(10)) RETURNS char(10) CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE ultDiaMes Int; \n";
               SQLproc += "\tDECLARE iPos Int; \n";
               SQLproc += "\tDECLARE iDay Int; \n";
               SQLproc += "\tDECLARE rDay Int; \n";
               SQLproc += "\tDECLARE sMes Date; \n";
               SQLproc += "\tDECLARE iMes Int; \n";
               SQLproc += "\tDECLARE iAno Int; \n";
               SQLproc += "\tSET iMes = CAST(Mid(sVecto, 4, 2) AS UNSIGNED Int); \n";
               SQLproc += "\tSET iANO = CAST(Mid(sVecto, 7, 4) AS UNSIGNED Int); \n";
               SQLproc += "\tSET iMes = iMes + 1; \n";
               SQLproc += "\tIF iMes > 12 THEN SET iMes = 1; \n";
               SQLproc += "\tSET iAno = iAno + 1; END IF; \n";
               SQLproc += "\tSET sMes = CAST(CONCAT(Right(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4),'-',RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2),'-01') AS DATE);\n";
               SQLproc += "\tSET ultDiaMes = Day(LAST_DAY(sMes)); \n";
               SQLproc += "\tSET iPos = InStr(Trim(sCampo), 'DV'); \n";
               SQLproc += "\tIF iPos > 0 THEN \n";
               SQLproc += "\t\tSET iDay = CAST(Mid(Trim(sCampo), iPos + 2, 2) AS UNSIGNED Int); \n";
               SQLproc += "\t\tIF iDay > ultDiaMes THEN \n";
               SQLproc += "\t\t\tSET rDay = ultDiaMes; \n";
               SQLproc += "\t\tELSE \n";
               SQLproc += "\t\t\tSET rDay = iDay; \n";
               SQLproc += "\t\tEND IF; \n";
               SQLproc += "\tELSE \n";
               SQLproc += "\t\tSET iDay = CAST(LEFT(sVecto,2) AS UNSIGNED Int); \n";
               SQLproc += "\t\tIF iDay > ultDiaMes THEN \n";
               SQLproc += "\t\t\tSET rDay = ultDiaMes; \n";
               SQLproc += "\t\tELSE \n";
               SQLproc += "\t\t\tSET rDay = iDay; \n";
               SQLproc += "\t\tEND IF; \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tRETURN CONCAT(Right(CONCAT('00',TRIM(CAST(rDay AS CHAR(2)))),2),'/',RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2),'/',RIGHT(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4)); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean ProxVecto2() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`ProxVecto2`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`ProxVecto2`(sCampo LongText, sVecto Char(10), sVecto2 Char(10)) RETURNS char(10) CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE ultDiaMes Int; \n";
               SQLproc += "\tDECLARE iPos Int; \n";
               SQLproc += "\tDECLARE iDay Int; \n";
               SQLproc += "\tDECLARE rDay Int; \n";
               SQLproc += "\tDECLARE sMes Date; \n";
               SQLproc += "\tDECLARE iMes Int; \n";
               SQLproc += "\tDECLARE iAno Int; \n\n";
               SQLproc += "\t#IF CAST(Mid(sVecto,7,4) AS UNSIGNED Int) > 0 THEN\n";
               SQLproc += "\t\t#SET sVecto = ConCat(Mid(sVecto,7,4), \"-\", Mid(sVecto,4,2), \"-\", Mid(sVecto,1,2));\n";
               SQLproc += "\t#END IF;\n\n";
               SQLproc += "\tSET iMes = CAST(Mid(sVecto, 4, 2) AS UNSIGNED Int);\n";
               SQLproc += "\tSET iANO = CAST(Mid(sVecto, 7, 4) AS UNSIGNED Int); \n";
               SQLproc += "\tSET iMes = iMes + 1; \n";
               SQLproc += "\tIF iMes > 12 THEN \n";
               SQLproc += "\t\tSET iMes = 1; \n";
               SQLproc += "\t\tSET iAno = iAno + 1; \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tSET sMes = CAST(CONCAT(Right(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4),'-',RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2),'-01') AS DATE);\n";
               SQLproc += "\tSET ultDiaMes = Day(LAST_DAY(sMes)); \n";
               SQLproc += "\tSET iPos = InStr(Trim(sCampo), 'DV'); \n";
               SQLproc += "\tIF iPos > 0 THEN \n";
               SQLproc += "\t\tSET iDay = CAST(Mid(Trim(sCampo), iPos + 2, 2) AS UNSIGNED Int); \n";
               SQLproc += "\t\tIF iDay > ultDiaMes THEN \n";
               SQLproc += "\t\t\tSET rDay = ultDiaMes; \n";
               SQLproc += "\t\tELSE \n";
               SQLproc += "\t\t\tSET rDay = iDay; \n";
               SQLproc += "\t\tEND IF; \n";
               SQLproc += "\tELSE \n";
               SQLproc += "\t\tSET iDay = CAST(LEFT(sVecto2,2) AS UNSIGNED Int); \n";
               SQLproc += "\t\tIF iDay > ultDiaMes THEN \n";
               SQLproc += "\t\t\tSET rDay = ultDiaMes; \n";
               SQLproc += "\t\tELSE\n";
               SQLproc += "\t\t\tSET rDay = iDay; \n";
               SQLproc += "\t\tEND IF; \n";
               SQLproc += "\tEND IF; \n\n";
               SQLproc += "	RETURN CONCAT(RIGHT(CONCAT('0000',TRIM(CAST(iAno AS CHAR(4)))),4), '-', RIGHT(CONCAT('00',TRIM(CAST(iMes AS CHAR(2)))),2), '-', Right(CONCAT('00',TRIM(CAST(rDay AS CHAR(2)))),2));\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
    
    public boolean RCampos() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`RCampos`";
        conn.CommandExecute(sqlPROC);
        String SQLproc =  "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`RCampos`(sValue CHAR(2)) RETURNS longtext CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE sRet   longtext;\n";
               SQLproc += "\tDECLARE sBusc1 longtext;\n";
               SQLproc += "\tDECLARE sBusc2 longtext;\n\n";
               SQLproc += "\tSET sBusc1 = (SELECT Upper(a.DESCR) AS descricao FROM adm a WHERE a.CODIGO = sValue);\n";
               SQLproc += "\tIF IsNull(sBusc1) THEN\n";
               SQLproc += "\t\tSET sBusc2 = (SELECT Upper(l.CART_DESCR) FROM lancart l WHERE l.CART_CODIGO = sValue);\n";
               SQLproc += "\t\tIF IsNull(sBusc2) THEN\n";
               SQLproc += "\t\t\tIF sValue = 'DF' THEN SET sRet = 'DIFERENCA'; END IF;\n";
               SQLproc += "\t\t\tIF sValue = 'DC' THEN SET sRet = 'DESCONTO'; END IF;\n";
               SQLproc += "\t\tELSE\n";
               SQLproc += "\t\t\tSET sRet = sBusc2;\n";
               SQLproc += "\t\tEND IF;\n";
               SQLproc += "\tELSE\n";
               SQLproc += "\t\tSET sRet = sBusc1;\n";
               SQLproc += "\tEND IF;\n";
               SQLproc += "\tRETURN sRet; \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean RetAvDataRid2() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`RetAvDataRid2`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`RetAvDataRid2`(sValue Text) RETURNS date\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE iLen Int; \n";
               SQLproc += "\tDECLARE iPto Int; \n";
               SQLproc += "\tDECLARE lRet CHAR(8); \n";
               SQLproc += "\tDECLARE bLog Boolean; \n";
               SQLproc += "\tSET iLen = LENGTH(UCase(Trim(sValue))); \n";
               SQLproc += "\tSET i = 1; \n";
               SQLproc += "\tSET iPto = 0; \n";
               SQLproc += "\tSET bLog = TRUE; \n";
               SQLproc += "\tIF iLen > 0 THEN \n";
               SQLproc += "\t\tWHILE bLog DO \n";
               SQLproc += "\t\t\tIF i >= iLen THEN \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tIF iPto = 7 THEN \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tIF MID(sValue, i, 1) = ':' THEN \n";
               SQLproc += "\t\t\t\tSET iPto = iPto + 1; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tSET i = i + 1; \n";
               SQLproc += "\t\tEND WHILE; \n";
               SQLproc += "\t\tSET lRet = MID(sValue,i - 1,8); \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tRETURN CAST(CONCAT(MID(lRet, 5, 4), '-', MID(lRet, 3, 2), '-', MID(lRet, 1, 2)) AS DATE); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean RetAvDescRid2() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`RetAvDescRid2`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`RetAvDescRid2`(sValue Text) RETURNS text CHARSET latin1\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE iLen Int; \n";
               SQLproc += "\tDECLARE iPto Int; \n";
               SQLproc += "\tDECLARE lRet Text; \n";
               SQLproc += "\tDECLARE bLog Boolean; \n";
               SQLproc += "\tSET iLen = LENGTH(UCase(Trim(sValue))); SET i = 1; \n";
               SQLproc += "\tSET iPto = 0; \n";
               SQLproc += "\tSET bLog = TRUE; \n";
               SQLproc += "\tIF iLen > 0 THEN \n";
               SQLproc += "\t\tWHILE bLog DO \n";
               SQLproc += "\t\t\tIF i >= iLen THEN \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tIF iPto = 10 THEN \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE;  \n";
               SQLproc += "\t\t\tEND IF;  \n";
               SQLproc += "\t\t\tIF MID(sValue, i, 1) = ':' THEN \n";
               SQLproc += "\t\t\t\tSET iPto = iPto + 1; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tSET i = i + 1; \n";
               SQLproc += "\t\tEND WHILE; \n";
               SQLproc += "\t\tSET lRet = MID(sValue,i - 1); \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tRETURN MID(lret,1,InStr(lRet,':') - 1); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean RetAvTipoRid2() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`RetAvTipoRid2`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`RetAvTipoRid2`(sValue Text) RETURNS char(3) CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE iLen Int; \n";
               SQLproc += "\tDECLARE iPto Int; \n";
               SQLproc += "\tDECLARE lRet CHAR(3); \n";
               SQLproc += "\tDECLARE bLog Boolean; \n";
               SQLproc += "\tSET iLen = LENGTH(UCase(Trim(sValue))); \n";
               SQLproc += "\tSET i = 1; \n";
               SQLproc += "\tSET iPto = 0; \n";
               SQLproc += "\tSET bLog = TRUE; \n";
               SQLproc += "\tIF iLen > 0 THEN \n";
               SQLproc += "\t\tWHILE bLog DO \n";
               SQLproc += "\t\t\tIF i >= iLen THEN  \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tIF iPto = 8 THEN \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE; \n";
               SQLproc += "\t\t\tEND IF;  \n";
               SQLproc += "\t\t\tIF MID(sValue, i, 1) = ':' THEN \n";
               SQLproc += "\t\t\t\tSET iPto = iPto + 1; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tSET i = i + 1; \n";
               SQLproc += "\t\tEND WHILE; \n";
               SQLproc += "\t\tSET lRet = MID(sValue,i - 1,3); \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tRETURN lRet; \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean RetAvUserRid2() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`RetAvUserRid2`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`RetAvUserRid2`(sValue Text) RETURNS text CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tRETURN RIGHT(sValue,INSTR(REVERSE(sValue),':') -1); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean RetAvValorRid2() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`RetAvValorRid2`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`RetAvValorRid2`(sValue Text) RETURNS decimal(10,2)\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE i Int; \n";
               SQLproc += "\tDECLARE iLen Int; \n";
               SQLproc += "\tDECLARE iPto Int; \n";
               SQLproc += "\tDECLARE lRet CHAR(10); \n";
               SQLproc += "\tDECLARE bLog Boolean; \n";
               SQLproc += "\tSET iLen = LENGTH(UCase(Trim(sValue))); \n";
               SQLproc += "\tSET i = 1; \n";
               SQLproc += "\tSET iPto = 0; \n";
               SQLproc += "\tSET bLog = TRUE; \n";
               SQLproc += "\tIF iLen > 0 THEN \n";
               SQLproc += "\t\tWHILE bLog DO \n";
               SQLproc += "\t\t\tIF i >= iLen THEN \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE; \n";
               SQLproc += "\t\t\tEND IF; \n";
               SQLproc += "\t\t\tIF iPto = 2 THEN \n";
               SQLproc += "\t\t\t\tSET bLog = FALSE; \n";
               SQLproc += "\t\t\tEND IF;  \n";
               SQLproc += "\t\t\tIF MID(sValue, i, 1) = ':' THEN  \n";
               SQLproc += "\t\t\t\tSET iPto = iPto + 1; \n";
               SQLproc += "\t\t\tEND IF;  \n";
               SQLproc += "\t\t\tSET i = i + 1; \n";
               SQLproc += "\t\tEND WHILE; \n";
               SQLproc += "\t\tSET lRet = MID(sValue,i - 1,10); \n";
               SQLproc += "\tEND IF; \n";
               SQLproc += "\tRETURN CAST(CAST(MID(lRet, 1, 8) AS UNSIGNED INT) + (CAST(MID(lRet, 9, 2) AS UNSIGNED INT) / 100) AS DECIMAL(10,2)); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean StrDate() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`StrDate`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`StrDate`(value VARCHAR(10)) RETURNS date\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tDECLARE dia CHAR(2);\n";
               SQLproc += "\tDECLARE mes CHAR(2);\n";
               SQLproc += "\tDECLARE ano CHAR(4);\n\n";
               SQLproc += "\tSET dia = MID(value, 1, 2);\n";
               SQLproc += "\tSET mes = MID(value, 4, 2);\n";
               SQLproc += "\tSET ano = MID(value, 7);\n\n";
               SQLproc += "\tRETURN CAST(CONCAT(ano,'-',mes,'-',dia) AS DATE);\n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean StrVal() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`StrVal`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`StrVal`(sValue Char(10)) RETURNS decimal(10,2)\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE nRet DECIMAL(10, 2); \n";
               SQLproc += "SET nRet = CAST(CONCAT(CAST(Mid(sValue, 1, 8) AS UNSIGNED Int), '.',RIGHT(sValue,2)) AS DECIMAL(10,2)); \n";
               SQLproc += "\tRETURN nRet; \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean ValStr() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`ValStr`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`ValStr`(iValue DECIMAL(10,2)) RETURNS char(10) CHARSET latin1\n";
               SQLproc += "BEGIN \n";
               SQLproc += "\tDECLARE iValor CHAR(20); \n";
               SQLproc += "\tDECLARE iCento CHAR(4); \n";
               SQLproc += "\tSET iValor = Right(CONCAT('00000000',CAST(TRUNCATE(iValue,0) AS CHAR(10))),8); \n";
               SQLproc += "\tSET iCento = CAST(((iValue - TRUNCATE(iValue, 0)) * 100) AS CHAR); \n";
               SQLproc += "\tRETURN CONCAT(iValor, iCento); \n";
               SQLproc += "END\n";
        return conn.CommandExecute(SQLproc) > 0;
    }
        
    public boolean remove_accents() {
        String sqlPROC = "DROP FUNCTION IF EXISTS `" + VariaveisGlobais.sqlDbName + "`.`remove_accents`";
        conn.CommandExecute(sqlPROC);
        String SQLproc = "CREATE DEFINER=`" + VariaveisGlobais.sqlUser + "`@`%` FUNCTION `" + VariaveisGlobais.sqlDbName + "`.`remove_accents`(textvalue varchar(20000)) RETURNS varchar(20000) CHARSET utf8\n";
               SQLproc += "BEGIN\n";
               SQLproc += "\tSET @textvalue = textvalue;\n\n";
               SQLproc += "\t-- ACCENTS\n";
               SQLproc += "\tSET @withaccents = 'ŠšŽžÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÑÒÓÔÕÖØÙÚÛÜÝŸÞàáâãäåæçèéêëìíîïñòóôõöøùúûüýÿþƒ';\n";
               SQLproc += "\tSET @withoutaccents = 'SsZzAAAAAAACEEEEIIIINOOOOOOUUUUYYBaaaaaaaceeeeiiiinoooooouuuuyybf';\n";
               SQLproc += "\tSET @count = length(@withaccents);\n\n";
               SQLproc += "\tWHILE @count > 0 do\n";
               SQLproc += "\t\tSET @textvalue = replace(@textvalue, substring(@withaccents, @count, 1), substring(@withoutaccents, @count, 1));\n";
               SQLproc += "\t\tSET @count = @count - 1;\n";
               SQLproc += "\tEND WHILE;\n\n";
               SQLproc += "\t-- SPECIAL CHARS\n";
               SQLproc += "\tSET @special = '!@#$%¨&*()_+=§¹²³£¢¬\"`´{[^~}]<,>.:;?/°ºª+*|\\\\''';\n";
               SQLproc += "\tSET @count = length(@special);\n";
               SQLproc += "\tWHILE @count > 0 do\n";
               SQLproc += "\t\tSET @textvalue = replace(@textvalue, substring(@special, @count, 1), '');\n";
               SQLproc += "\t\tSET @count = @count - 1;\n";
               SQLproc += "\tEND WHILE;\n\n";
               SQLproc += "\tRETURN @textvalue;\n";
               SQLproc += "END";
        return conn.CommandExecute(SQLproc) > 0;
    }        
}
