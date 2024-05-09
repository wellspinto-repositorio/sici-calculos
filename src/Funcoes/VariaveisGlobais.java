package Funcoes;

import Movimento.jRecebtos;
import Movimento.jRecebtos_fake;
import Sici.Partida.Collections;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;

public class VariaveisGlobais {
    static public boolean isNuvem = true;
    
    static public boolean debug = false;
    static public boolean withPool = false;
    
    // variaveis do supermenu
    static public JDesktopPane jPanePrin = null;

    // Variaveis de login
    static public String usuario = "";
    static public String funcao = "";

    // Variaveis para conexão
    static public String  remote  = "";
    static public ArrayList<String[]> units = new ArrayList<>();
    static public String sqlAlias = "Máquina Local";
    static public String sqlHost = "127.0.0.1";
    static public int sqlPort = 3306;
    static public String sqlUser = "root";
    static public String sqlPwd = "7kf51b";
    static public String sqlDbName = "sici";    
    static public String sqlDrive = "com.mysql.cj.jdbc.Driver";
    static public String sqlUrl = "jdbc:mysql://";
    static public String sqlConn = "?autoReconnect=true&Pooling=True&wait_timeout=30000&connectTimeout=5000&interactive_timeout=30000&socketTimeout=30000&createDatabaseIfNotExist=true&useTimezone=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
    
    // Variaveis para tela de Imoveis
    static public String rgprp = "";
    static public String rgimv = "";

    // Variaveis da Baixa de Imoveis
    static public String situacao = "";
    static public String historico = "";

    // Variaveis para tela de Sócios
    static public String mContrato = "";
    static public int mQtdSoc = 0;
    static public int mPosSoc = 0;
    static public ResultSet pResult = null;

    // Variaveis fiadores
    static public String frgprp = "";
    static public String frgimv = "";
    static public String fcontrato = "";
    static public String fnome = "";
    static public boolean isBloqueado = false;
    
    // Variaveis para carteira
    static public String ccontrato = "";
    static public String crgprp = "";
    static public String crgimv = "";
    static public String ccampos = "";

    static public Collections dCliente = new Collections();
    static public Collections cContas = new Collections();

    // para a rotina pag/rec
    static public jRecebtos rTela = null;
    static public jRecebtos_fake rTela_fake = null;
    
    // file path
    static public String DefaultFilePath = "./";

    // Ordenacao de Matriz
    static public int Inicio = 1;
    static public int Final = 1;

    // parametros fixo
    static public boolean impPropDiv = true;
    static public boolean bShowCotaParcela = true;
    static public boolean bShowCotaParcelaExtrato = true;
    static public boolean ShowRecebimentoExtrato = true;
    static public boolean ShowLabelsDatasExtrato = false;
    static public int nviasRecibo = 2;
    
    // tela de liberacao retençoes
    static public String lbr_rgprp = "";
    
    static public String marca = "";
    static public String icoBoleta = "";
    static public String icoExtrato = "";
    static public String extPreview = "ExtratoPreview.jasper";
    static public String extPrint = "Extrato.jasper";
    
    // Impressão de boleto
    static public boolean boletoMU = false;
    static public boolean boletoJU = false;
    static public boolean boletoCO = false;
    static public boolean boletoEP = false;
    static public boolean boletoSomaEP = false;
    
    // Impressão no Extrato
    static public boolean extADM = false;
    
    // Impressão no extrato das comissoes sobre MU/JU/CO/EP
    static public boolean extMU = false;
    static public boolean extJU = false;
    static public boolean extCO = false;
    static public boolean extEP = false;
    
    // variavel para tela de recebimento
    static public String rrgprp = "";
    static public String rrgimv = "";
    static public String rcontrato = "";

    static public String  versao = null;
    static public boolean miscelaneas = false;
    static public boolean showrecvalores = false;

    static public String protocolomenu = "";
    static public boolean local;
    
    // Conexao compartilhada
    static public Db conexao = null;
    
    // geracao
    static public boolean gerMulSelect = false;
    
    //Inativos
    static public boolean Iloca = false;
    static public boolean IProp = false;
    
    // Impressão 
    // lp   - para linux/unix
    // copy - para windows
    //static public String extPrintCmd = "lp ";
    static public String bcobol = "itau";
    static public boolean scroll = false;
    public static String myLogo = "";
    public static List<String> myLogos = new ArrayList<>();
    public static double imgTimer = 15;
    
    public static boolean ShowDocBoleta = true;
    public static Boolean ExtratoTotal = true;
    
    public static float[] bobinaSize = {215f, 730f, 12, 2, -2, 2};
    //public static String externalcmd = null;
    
    // Printers
    public static String PrinterMode = "NORMAL";  // NORMAL | EXTERNA
    public static String Thermica = null;
    public static String ThermicaMode = "NORMAL";
    public static String Printer = null;
    public static String Preview = null;
    public static String PreviewType = null;
    public static String Externo = null;
    public static String Externo2 = null;
    public static String PrinterType = "PDF";
    public static Boolean statPrinter = true;
    
    public static Boolean bloqAdianta = false;
    public static Boolean dimob = false;
    
    // Controle de impressão
    public static String AdiantAviso  = "";
    public static String Adiantamento = "";
    public static String Aviso        = "";
    public static String AvisoPre     = "";
    public static String Caixa        = "";
    public static String Deposito     = "";
    public static String Despesas     = "";
    public static String Extrato      = "";
    public static String ExtratoSocio = "";
    public static String Boleta       = "";
    public static String PassCaixa    = "";
    public static String Recibo       = "";
    
    // site informaçoes
    public static String siteIP = "";
    public static String siteUser = "";
    public static String sitePwd = "";
    public static String siteDbName = "";

    // Busca Globalizada
    public static javax.swing.JTextField jBuscar;
    
    // Licença
    public static String Licenca = "";
    
    // Regra Calculos
    public static boolean RegraCalculos = true;

    // Controla exibição de itens em retenção no extrato
    public static boolean ShowRetencaoExtrato = false;
    public static boolean JuntaALDCExtrato = false;
    public static boolean JuntaALDFExtrato = false;
    
    // Parametro do Extrato
    public static boolean ExtOrdAut;
    
    // parametros de conecxão de gmail
    public static boolean OUTLOOK = false;
    public static String CLIENT_ID;
    public static String PROJECT_ID; 
    public static String AUTH_URI;
    public static String TOKEN_URI; 
    public static String AUTH_PROVIDER_X509_CERT_URL;
    public static String CLIENT_SECRET;
    public static String REDIRECT_URIS;
    public static String AUTH_CODE;
    public static String ACCESS_TOKEN;
    public static String REFRESH_TOKEN;
    
    //
    public static String RETORNO_PATH;
    public static String REMESSA_PATH;
    public static String SYSTEM_PATH = System.getProperty("user.dir");
    
    // parametros para processamento e geração 
    public static boolean GER_AL_IN00 = true;
    /**
     * GER_AL_IN00
     * 
     * Se campo AL cota for 0000 o sistema podera
     *                   Gerar movimento
     *                   Não gerar movimento
     */
    public static String GER_NT_IN00 = "MANTER";   // MANTER; ZERAR; APAGAR
    /**
     * GER_NT_IN00
     * 
     * Se cota for 0000 o sistema podera
     *             Manter o registro
     *             Zerar o registro
     *             Apagar o registro
     */
            
    // Variaveil WEBSWING
    public static boolean webswing = false;
    
    public static void LerGlobal(Config config) {
        /*********************************************************************** 
        *  Regrais .global
        ************************************************************************
        * Regras que atendem a todos no sistemas
        ************************************************************************/
        VariaveisGlobais.RegraCalculos = (boolean) config.Reading("RegraCalculos", true, "GLOBAL");        
        VariaveisGlobais.bShowCotaParcela = (boolean) config.Reading("bShowCotaParcela", true, "GLOBAL");        
        VariaveisGlobais.bShowCotaParcelaExtrato = (boolean) config.Reading("bShowCotaParcelaExtrato", true,"GLOBAL");
        VariaveisGlobais.ShowRecebimentoExtrato = (boolean) config.Reading("ShowRecebimentoExtrato", true, "GLOBAL");
        VariaveisGlobais.ShowLabelsDatasExtrato = (boolean) config.Reading("DatasRecPagExtrato", false,"GLOBAL");
        VariaveisGlobais.miscelaneas = (boolean) config.Reading("Micelaneas", false,"GLOBAL");
        VariaveisGlobais.showrecvalores = (boolean) config.Reading("ShowRecValores", false,"GLOBAL");
        VariaveisGlobais.gerMulSelect = (boolean) config.Reading("gerMulSelect", false, "GLOBAL");
        VariaveisGlobais.scroll = (boolean) config.Reading("scroll", false,"GLOBAL");
        VariaveisGlobais.ShowDocBoleta = (boolean) config.Reading("ShowDocBoleta", true,"GLOBAL");
        VariaveisGlobais.ExtratoTotal = (boolean) config.Reading("ExtratoTotal", true,"GLOBAL");
        VariaveisGlobais.ShowRetencaoExtrato = (boolean) config.Reading("ShowRetencaoExtrato", true,"GLOBAL");
        VariaveisGlobais.JuntaALDCExtrato = (boolean) config.Reading("JuntaALDCExtrato", false,"GLOBAL");
        VariaveisGlobais.JuntaALDFExtrato = (boolean) config.Reading("JuntaALDFExtrato", true,"GLOBAL");

        String BobSize[] = ((String) config.Reading("bobinaSize", "215, 730, 12, 10, 0, 2","GLOBAL")).split(",");
        VariaveisGlobais.bobinaSize = new float[] {Float.valueOf(BobSize[0]),Float.valueOf(BobSize[1]),Float.valueOf(BobSize[2]),
                                                   Float.valueOf(BobSize[3]),Float.valueOf(BobSize[4]),Float.valueOf(BobSize[5])};
        // boletos
        VariaveisGlobais.boletoMU = (boolean) config.Reading("boletoMU", false,"GLOBAL");
        VariaveisGlobais.boletoJU = (boolean) config.Reading("boletoJU", false,"GLOBAL");
        VariaveisGlobais.boletoCO = (boolean) config.Reading("boletoCO", false,"GLOBAL");
        VariaveisGlobais.boletoEP = (boolean) config.Reading("boletoEP", false,"GLOBAL");
        VariaveisGlobais.boletoSomaEP = (boolean) config.Reading("boletoSomaEP", true,"GLOBAL");

        // extrato
        VariaveisGlobais.extADM = (boolean) config.Reading("extADM", false,"GLOBAL");

        // extrato MU/JU/CO/EP comissão
        VariaveisGlobais.extMU = (boolean) config.Reading("extMU", false,"GLOBAL");
        VariaveisGlobais.extJU = (boolean) config.Reading("extJU", false,"GLOBAL");
        VariaveisGlobais.extCO = (boolean) config.Reading("extCO", false,"GLOBAL"); 
        VariaveisGlobais.extEP = (boolean) config.Reading("extEP", false,"GLOBAL"); 

        VariaveisGlobais.bloqAdianta = (boolean) config.Reading("bloqAdianta", false,"GLOBAL");
        VariaveisGlobais.dimob = (boolean) config.Reading("Dimob", false,"GLOBAL");
        
        VariaveisGlobais.debug = (boolean) config.Reading("Debug", false,"GLOBAL");
        VariaveisGlobais.withPool = (boolean) config.Reading("WithPool", false,"GLOBAL");

        // para processamento da geração de movimento
        VariaveisGlobais.GER_AL_IN00 = (boolean) config.Reading("GER_AL_IN00", true,"GLOBAL");
        VariaveisGlobais.GER_NT_IN00 = (String) config.Reading("GER_NT_IN00", "MANTER", "GLOBAL");

        VariaveisGlobais.RETORNO_PATH = (String) config.Reading("RETORNOPATH", System.getProperty("user.dir"), "GLOBAL");     
        VariaveisGlobais.REMESSA_PATH = (String) config.Reading("REMESSAPATH", System.getProperty("user.dir"),"GLOBAL");             
        
        // Controle de Impressão
        VariaveisGlobais.AdiantAviso  = (String) config.Reading("AdiantAviso", "INTERNA", "GLOBAL");
        VariaveisGlobais.Adiantamento = (String) config.Reading("Adiantamento", "INTERNA", "GLOBAL");
        VariaveisGlobais.Aviso        = (String) config.Reading("Aviso", "INTERNA", "GLOBAL");
        VariaveisGlobais.AvisoPre     = (String) config.Reading("AvisoPre", "INTERNA", "GLOBAL");
        VariaveisGlobais.Caixa        = (String) config.Reading("Caixa", "INTERNA", "GLOBAL");
        VariaveisGlobais.Deposito     = (String) config.Reading("Deposito", "INTERNA","GLOBAL" );
        VariaveisGlobais.Despesas     = (String) config.Reading("Despesas", "INTERNA", "GLOBAL");
        VariaveisGlobais.Extrato      = (String) config.Reading("Extrato", "INTERNA", "GLOBAL");
        VariaveisGlobais.ExtratoSocio = (String) config.Reading("ExtratoSocio", "INTERNA", "GLOBAL");
        VariaveisGlobais.Boleta       = (String) config.Reading("Boleta", "INTERNA", "GLOBAL");
        VariaveisGlobais.PassCaixa    = (String) config.Reading("PassCaixa", "INTERNA", "GLOBAL");
        VariaveisGlobais.Recibo       = (String) config.Reading("Recibo", "INTERNA", "GLOBAL");

        // Testa para ver se é um arquivo ou pasta

        VariaveisGlobais.myLogo = (String) config.Reading("BackGroundImage", "fundoimobilis.png", "GLOBAL");       
        if (new File(VariaveisGlobais.myLogo).isDirectory()) {
            String[] images = new File(VariaveisGlobais.myLogo).list();            
            for (String item : images) VariaveisGlobais.myLogos.add(VariaveisGlobais.myLogo + File.separator + item);
        }
        // Variavel que controla o tempo de transição
        VariaveisGlobais.imgTimer = (double)((int) config.Reading("BackGroundTimer", 15, "GLOBAL"));               
        
        // Variaveis WEBSWSING
        VariaveisGlobais.webswing = (boolean) config.Reading("webswing", false,"GLOBAL");
    }
    
    public static void LerConf(Config config) {
        InetAddress localhost = null;
        try { localhost = InetAddress.getLocalHost(); } catch (UnknownHostException uhEx) {}
        String name = localhost.getHostName();
        String ip = localhost.getHostAddress();

        /************************************************************************
        * Regras que atendem a máquina aonde o Sistema esta sendo executado
        ************************************************************************/
        // Printers
        VariaveisGlobais.Thermica = (String) config.Reading("Thermica", null, name);
        VariaveisGlobais.Printer = (String) config.Reading("Printer", null, name);
        VariaveisGlobais.Preview = (String) config.Reading("Preview", null, name);
        VariaveisGlobais.PreviewType = (String) config.Reading("PreviewType", "INTERNO", name);
        VariaveisGlobais.Externo = (String) config.Reading("Externo", null, name);
        VariaveisGlobais.Externo2 = (String) config.Reading("Externo2", null, name);
        
        // Extrato
        VariaveisGlobais.extPreview = (String) config.Reading("extPreview", "ExtratoPreview.jasper", name);
        VariaveisGlobais.extPrint = (String) config.Reading("extPrint", "Extrato.jasper", name);
        
        // Recibos
        VariaveisGlobais.nviasRecibo = (int) config.Reading("nviasRecibo", 2, name);

        // Forma de Envio E-Mail
        VariaveisGlobais.OUTLOOK = (boolean) config.Reading("OUTLOOK", false,name);
    }
}
