package BancosDigital.Santander;

import BancosDigital.PEMImporter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

public class Santander {
    public final String URI_SANDBOX_TOKEN = "https://trust-sandbox.api.santander.com.br/auth/oauth/v2/token";
    public final String URI_SANTANDER_TOKEN = ": https://trust-open.api.santander.com.br/auth/oauth/v2/token";
    public final String URI_SANDBOX_WORKSPACE = "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/workspaces";
    public final String URI_SANTANDER_WORKSPACE = ": https://trust-open.api.santander.com.br/collection_bill_management/v2/workspaces/";
    public final String URI_SANDBOX_BOLETA_INSERT = "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/workspaces/";
    public final String URI_SANTANDER_BOLETA_INSERT = ": https://trust-open.api.santander.com.br/collection_bill_management/v2/workspaces/";
    public final String URI_SANDBOX_BOLETA_CONSULTA = "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/";
    public final String URI_SANTANDER_BOLETA_CONSULTA = ": https://trust-open.api.santander.com.br/collection_bill_management/v2/";
    public final String URI_SANDBOX_BOLETA_SELECT = "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/";
    public final String URI_SANTANDER_BOLETA_SELECT = " https://trust-open.api.santander.com.br/collection_bill_management/v2/";
    
    /**
    / Retono em caso de erro
    / Retorna na função insertBoleta
    **/
    private String codErro;
    private String msgErro;
    
    // Parametros Token 
    private String grant_type = "client_credentials";
    
    public String getCodErro() { return codErro; }
    public String getMsgErro() { return msgErro; }
    
    public static void main(String[] args) throws Exception {
        Santander c = new Santander();
        Object[] tk = c.gToken(new String[] {
            "FMIaKFyMnjdI5AATenlM10qSduVa7Az1",
            "oIjOYE5ub8JT9scg",
            System.getProperty("user.dir") + "/cert/",
            "SAMIC1809.crt",
            "SAMIC1809.key",
            "https://trust-sandbox.api.santander.com.br/auth/oauth/v2/token"
        });
        Object[] stk = (Object[])tk[1];
        String token = stk[0].toString();
        //System.out.println("token: " + token);
        Object[] workspace = c.insertWorkSpace(new String[] {
            token,
            "FMIaKFyMnjdI5AATenlM10qSduVa7Az1",
            "1234567",
            "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/workspaces"
        });
        System.out.println("workspace: " + workspace[0].toString() + " - " + workspace[1].toString());
        if (!workspace[0].equals(201)) return;
        
        SantanderPagador santanderPagador = new SantanderPagador();
        santanderPagador.setNsuCode("1");
        santanderPagador.setNsuDate("02-10-2023");
        santanderPagador.setIssueDate("02-10-2023"); // dtgeração
        santanderPagador.setDueDate("15-10-2023"); //dtvecto
        santanderPagador.setNominalValue(new BigDecimal("1000"));
        santanderPagador.setBankNumber("777777");
        santanderPagador.setClientNumber("1000/10500");
        santanderPagador.setPayer_name("Wellington de Souza Pinto");
        santanderPagador.setPayer_address("Rua Taubate 64");
        santanderPagador.setPayer_neighborhood("Nova cidade");
        santanderPagador.setPayer_city("Sao Goncalo");
        santanderPagador.setPayer_state("RJ");
        santanderPagador.setPayer_zipCode("24456060");
        santanderPagador.setPayer_documentType("CPF");
        santanderPagador.setPayer_documentNumber("01903033799");
        
        santanderPagador.setKey_dictKey("01903033799");
        santanderPagador.setKey_type("CPF");
        
        String json = santanderPagador.build();
        
        Object[] wks = (Object[])workspace[1];
        String workspaceId = wks[0].toString();
               
        Object[] insert = c.insertBoleta(new String[] {
            token,
            "FMIaKFyMnjdI5AATenlM10qSduVa7Az1",
            workspaceId,
            System.getProperty("user.dir") + "/cert/",
            "SAMIC1809.crt",
            "SAMIC1809.key",
            "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/workspaces/"
        }, json);
        
        Object[] select = c.selectBoleta(new String[] {
            System.getProperty("user.dir") + "/cert/",
            "SAMIC1809.crt",
            "SAMIC1809.key",
            token,
            workspaceId,
            "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/workspaces/",
            "FMIaKFyMnjdI5AATenlM10qSduVa7Az1"
        }, "123.2022-12-12.P.1234567.123");
        
        Object[] consulta = c.consultaBoleta(new String[] {
            System.getProperty("user.dir") + "/cert/",
            "SAMIC1809.crt",
            "SAMIC1809.key",
            token,
            "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/",
            "FMIaKFyMnjdI5AATenlM10qSduVa7Az1"
        }, "1234567890123","123");

        //SantanderPagador pagador = new SantanderPagador();
        //pagador.setNsuCode("1");
        //System.out.println( pagador
        //       .build()                                
        //);
        
    }

    public Object[] insertWorkSpace(String[] dados) throws Exception{    
        // Pega dados
        String token = dados[0].trim();
        String client_id = dados[1].trim();
        String beneficiario = dados[2].trim();
        String url_ws = dados[3].trim();

        String query = "query getPropertiesByIds($SearchCriteriaByIds: SearchCriteriaByIdsInput) {\n"
        + "  getPropertiesByIds(searchCriteriaByIds: $SearchCriteriaByIds) {\n"
        + "  \"type\": \"BILLING\","
        //+ "  \"description\": \"Workspace de Cobrança\",\n"
        + "    \"covenants\": [\n" 
        + "        {\n" 
        + "            \"code\": \"" + beneficiario + "\"\n" 
        + "        }\n" 
        + "    ]\n"
        + "  properties {\n"
        + "      propertyId\n"
        + "    }\n"
        + "  }\n"
        + "}";

        String variable = "{\n"
        + " \"SearchCriteriaByIds\": {\n"
        + "     \"propertyIds\": [\n"
        + "         00000000\n"
        + "     ]\n"
        + " }\n"
        + "}";

        Map<String, Object> variables = new HashMap<>();

        variables.put("query", query);
        variables.put("variables", variable);

        JSONObject jsonobj; 
        jsonobj = new JSONObject(variables);           

        // Imprime json
        //System.out.println(jsonobj.toString());
        String postDataUri =  jsonobj.toString();
        byte[] postData = postDataUri.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        File crtFile = new File(System.getProperty("user.dir") + "/cert/" + "SAMIC1809.crt");
        File keyFile = new File(System.getProperty("user.dir") + "/cert/" + "SAMIC1809.key");

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setRequestMethod("POST");        
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("X-Application-Key", client_id);
        uc.setRequestProperty("Authorization", "Bearer " + token);
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
        uc.setDoOutput(true);

        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode / 100 == 2) ? uc.getInputStream() : uc.getErrorStream()
        ));

        String message = ""; 
        String out;
        while ((out = br.readLine()) != null) {
           message += out;
       }

        String[] infoMessage = null; JSONObject jsonOb = null;
        if (statusCode != 201) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {                    
                    JSONArray arrJson = null;
                    try {
                        jsonOb = new JSONObject(message);      
                        arrJson = jsonOb.getJSONArray("violacoes");
                    } catch (JSONException jex) {} finally {
                        if (arrJson != null) {
                            if (arrJson.length() != 0 ) {
                                infoMessage = new String[] {arrJson.getString(0)};                                
                            } else infoMessage = new String[] { jsonOb.getString("detail")};
                        } else {
                            infoMessage = new String[] { jsonOb.getString("detail")};
                        }                    
                    }
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
            codErro = String.valueOf(statusCode); msgErro = infoMessage != null ? infoMessage[0].toString() : null;
        } else {
            jsonOb = new JSONObject(message);
            String workspaceId = myfunction(jsonOb,"id").toString();
            infoMessage = new String[] {workspaceId};
            
            codErro = null; msgErro = null;
        }       
        
        return new Object[] {statusCode, infoMessage};
    }

    public Object[] insertBoleta(String[] dados, String json_message) throws Exception{
        // Pega dados
        String token = dados[0].trim();
        String clientId = dados[1].trim();
        String workspaceId = dados[2].trim();
        String path = dados[3].trim();
        File crtFile = new File(path + dados[4]);
        File keyFile = new File(path + dados[5]);
        String url_ws = dados[6].trim() + "/" + workspaceId + "/bank_slips";

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        
                
        byte[] postData = json_message.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("X-Application-Key", clientId);
        uc.setRequestProperty("Authorization", "Bearer " + token);
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));

        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 201) ? uc.getInputStream() : uc.getErrorStream()
        ));

        String message = ""; 
        String out;
        while ((out = br.readLine()) != null) {
           message += out;
       }
        
        String[] infoMessage = null; JSONObject jsonOb = null;
        if (statusCode != 201) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {                    
                    jsonOb = new JSONObject(message);
                    String errCode = myfunction(jsonOb, "_errorCode").toString();
                    String errMessage = myfunction(jsonOb, "_message").toString();
                    String errDetails = myfunction(jsonOb, "_details").toString();
                    
                    infoMessage = new String[] {errCode, errMessage, errDetails};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
            codErro = String.valueOf(statusCode); msgErro = infoMessage != null ? infoMessage[0].toString() : null;
        } else {
            jsonOb = new JSONObject(message);
            String bankNumber = myfunction(jsonOb, "bankNumber").toString();
            String clientNumber = myfunction(jsonOb, "clientNumber").toString(); 
            
            String codigoBarras = ""; try {codigoBarras = myfunction(jsonOb,"barcode").toString();} catch (Exception jes) {}
            String linhaDigitavel = ""; try {linhaDigitavel = myfunction(jsonOb,"digitableLine").toString();} catch (Exception jes) {}
            String qrCode = ""; try {qrCode = myfunction(jsonOb,"qrCodePix").toString();} catch (Exception jes) {}
            
            
            infoMessage = new String[] {bankNumber, clientNumber, codigoBarras, linhaDigitavel, qrCode};
            
            codErro = null; msgErro = null;
        }       
       
       uc.disconnect();
       
       return new Object[] {statusCode, infoMessage};
    }

    public Object[] selectBoleta(String[] dados, String BankSlipId) throws Exception{
        // Pega dados
        String path = dados[0].toString();
        String crt = dados[1].toString();
        String key = dados[2].toString();
        String token = dados[3].toString().trim();
        
        String workspaceId = dados[4].trim();
        String url_ws = dados[5].trim() + workspaceId + "/bank_slips/" + BankSlipId;
        String clientId = dados[6].toString();
        
        if (!(new File(path + crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path + key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path + crt);
        File keyFile = new File(path + key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("X-Application-Key", clientId);
        uc.setRequestProperty("Authorization", "Bearer " + token);
        
        int statusCode = -1;
        BufferedReader br = null;
        
        try {
            statusCode = uc.getResponseCode();
            br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
            ));
        } catch (Exception e) { e.printStackTrace(); }
        
        String message = ""; 
        String out;
        while ((out = br.readLine()) != null) {
           message += out;
       }

        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message != null) {
                infoMessage = new String[] {"Erro desconhecido."};

                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);
                    String errCode = myfunction(jsonOb, "_errorCode").toString();
                    String errMessage = myfunction(jsonOb, "_message").toString();
                    String errDetails = myfunction(jsonOb, "_details").toString();
                    
                    infoMessage = new String[] {errCode, errMessage, errDetails};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            infoMessage = new Object[] {jsonOb};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }

    public Object[] consultaBoleta(String[] dados, String BenefCode, String bankNumber) throws Exception{
        // Pega dados
        String path = dados[0].toString();
        String crt = dados[1].toString();
        String key = dados[2].toString();
        String token = dados[3].toString().trim();
        
        String url_ws = dados[4].trim() + "bills" + "?beneficiaryCode=\"" + BenefCode + "\"&bankNumber=\"" + bankNumber + "\"";
        String clientId = dados[5].toString();
        
        if (!(new File(path + crt)).exists()) return new Object[] {-1, new String[] {"Não achei o Certificado."}};
        if (!(new File(path + key)).exists()) return new Object[] {-1, new String[] {"Não achei a Chave Privada."}};

        File crtFile = new File(path + crt);
        File keyFile = new File(path + key);

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        String json_data = "{" +
                                "\"beneficiaryCode\": \"" + BenefCode + "\"," +
                                "\"bankNumber\": \"" + bankNumber + "\"" +
                            "}";
        byte[] postData = json_data.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;        
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        uc.setDoOutput(true);
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("X-Application-Key", clientId);
        uc.setRequestProperty("Authorization", "Bearer " + token);
        
        int statusCode = -1;
        BufferedReader br = null;
        
        try {
            statusCode = uc.getResponseCode();
            br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
            ));
        } catch (Exception e) { e.printStackTrace(); }
        
        String message = ""; 
        String out;
        while ((out = br.readLine()) != null) {
           message += out;
       }

        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message != null) {
                infoMessage = new String[] {"Erro desconhecido."};

                if (statusCode == 401 || statusCode == 403 ) {
                    infoMessage = new String[] {"Sem permissão."};
                } else if (statusCode >= 404 && statusCode < 500) {
                    infoMessage = new String[] {"Não Encontrado."};
                } else if (statusCode >= 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    String errCode = "";
                    String errMessage = "";
                    String errDetails = "";
                    try {
                        JSONObject jsonOb = new JSONObject(message);
                        errCode = myfunction(jsonOb, "_errorCode").toString();
                        errMessage = myfunction(jsonOb, "_message").toString();
                        errDetails = myfunction(jsonOb, "_details").toString();
                    } catch (Exception ex) {}
                    
                    infoMessage = new String[] {errCode, errMessage, errDetails};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            infoMessage = new Object[] {jsonOb};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }

    public Object[] baixaBoleta(String[] dados) throws Exception{
        // Pega dados
        String workspaceId = dados[0].trim();
        String beneficiario = dados[1].trim();
        String nnumero = dados[2].trim();
        String path = dados[3].trim();
        File crtFile = new File(path + dados[4]);
        File keyFile = new File(path + dados[5]);
        String url_ws = dados[6].trim();

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        String json_data = "{" +
                                "\"covenantCode\": \"" + nnumero + "\"," +
                                "\"bankNumber\": \"" + beneficiario + "\"," +
                                "\"operation\": \"BAIXAR\"\n" +
                            "}";
        byte[] postData = json_data.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setRequestMethod("PATCH");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("Accept", "application/json");
        uc.setRequestProperty("WORKSPACE_ID", workspaceId);
        uc.setRequestProperty("charset", "utf-8");
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));

        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
        
        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 204) ? uc.getInputStream() : uc.getErrorStream()
        ));
        String message = br.readLine();
        
        String out;
        while ((out = br.readLine()) != null) {
           System.out.println(out);
       }
        
        String[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};
            } else {
                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("_message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            }
        } else {
            infoMessage = new String[] {"Sucesso"};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};  
    }

    public Object[] getToken(String[] dados) throws Exception{
        // Pega dados
        String clientid = dados[0].trim();
        String clientsecret = dados[1].trim();
        String path = dados[2].trim();
        File crtFile = new File(path + dados[3]);
        File keyFile = new File(path + dados[4]);
        String url_ws = dados[5].trim();

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        String urlParameters = "client_id=" + clientid + "&client_secret=" + clientsecret + "&grant_type=" + grant_type;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
        uc.setRequestProperty( "charset", "utf-8");
        uc.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        uc.setUseCaches( false );
        
        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
              
        int statusCode = -1;
        BufferedReader br = null;
        
        try {
            statusCode = uc.getResponseCode();
            br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
            ));
        } catch (Exception e) {} // e.printStackTrace(); }
        
        String message = null;
        try {
            message = br.readLine();
        } catch (Exception ex) {}
        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};

                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 403) {
                    infoMessage = new String[] {"Acesso negado."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            } else {
                infoMessage = new String[] {message};
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            String token = myfunction(jsonOb,"access_token").toString();
            String scope = myfunction(jsonOb,"scope").toString();
            String expires_in = myfunction(jsonOb,"expires_in").toString();
            infoMessage = new String[] {token, scope, expires_in};                        
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }
    
    public Object myfunction(JSONObject x,String y) throws JSONException {
        Object finalresult = null;    
        JSONArray keys =  x.names();
        for(int i=0;i<keys.length();i++) {
            if(finalresult!=null) {
                return finalresult;                     //To kill the recursion
            }

            String current_key = keys.get(i).toString();
            if(current_key.equals(y)) {
                finalresult = x.get(current_key);
                return finalresult;
            }

            if(x.get(current_key).getClass().getName().equals("org.json.JSONObject")) {
                myfunction((JSONObject) x.get(current_key),y);
            }
            else if(x.get(current_key).getClass().getName().equals("org.json.JSONArray")) {
                for(int j=0;j<((JSONArray) x.get(current_key)).length();j++) {
                    if(((JSONArray) x.get(current_key)).get(j).getClass().getName().equals("org.json.JSONObject")) {
                        myfunction((JSONObject)((JSONArray) x.get(current_key)).get(j),y);
                    }
                }
            }
        }
        return null;
    }        
    
    
    public Object[] gToken(String[] dados) throws Exception{
        // Pega dados
        String clientid = dados[0].trim();
        String clientsecret = dados[1].trim();
        String path = dados[2].trim();
        File crtFile = new File(path + dados[3]);
        File keyFile = new File(path + dados[4]);
        String url_ws = dados[5].trim();

        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "samic");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "samic");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        

        String urlParameters = "client_id=" + clientid + "&client_secret=" + clientsecret + "&grant_type=" + grant_type;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
                
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        uc.setSSLSocketFactory(sslContext.getSocketFactory());
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
        uc.setRequestProperty( "charset", "utf-8");
        uc.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        uc.setUseCaches( false );
        
        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
              
        int statusCode = -1;
        BufferedReader br = null;
        
        try {
            statusCode = uc.getResponseCode();
            br = new BufferedReader(new InputStreamReader(
               (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
            ));
        } catch (Exception e) {} // e.printStackTrace(); }

        String message = br.readLine();
        
        String out;
        while ((out = br.readLine()) != null) {
           message += out;
       }
        
        Object[] infoMessage = null;
        if (statusCode != 200) {
            if (message == null) {
                infoMessage = new String[] {"Erro desconhecido."};

                if (statusCode == 401) {
                    infoMessage = new String[] {"Erro de Autenticação."};
                } else if (statusCode == 403) {
                    infoMessage = new String[] {"Acesso negado."};
                } else if (statusCode == 500) {
                    infoMessage = new String[] {"Erro Interno no servidor."};
                } else if (statusCode == 400) {
                    JSONObject jsonOb = new JSONObject(message);      
                    JSONArray arrJson=jsonOb.getJSONArray("message");
                    infoMessage = new String[] {arrJson.getString(0)};
                } else {
                    infoMessage = new String[] {"Erro desconhecido."};
                }
            } else {
                infoMessage = new String[] {message};
            }
        } else {
            JSONObject jsonOb = new JSONObject(message);
            String token = myfunction(jsonOb,"access_token").toString();
            String scope = myfunction(jsonOb,"scope").toString();
            String expires_in = myfunction(jsonOb,"expires_in").toString();
            infoMessage = new String[] {token, scope, expires_in};                        
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }    
}
/*
        File crtFile = new File("C:\\cert\\SAMIC1809.crt");
        File keyFile = new File("C:\\cert\\SAMIC1809.key");
        
        KeyStore keyStore = PEMImporter.createKeyStore(keyFile, crtFile, "ericasantos");
        SSLContext sslContext = PEMImporter.createSSLFactory(keyFile, crtFile, "ericasantos");
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();        
        
        LayeredConnectionSocketFactory sslf = new SSLConnectionSocketFactory(sslContext);
                
        String line, queryString, url;
        url = "https://trust-sandbox.api.santander.com.br/collection_bill_management/v2/workspaces";
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        client =  HttpClients.custom().setSSLSocketFactory(sslf).build();//HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("X-Application-Key", "FMIaKFyMnjdI5AATenlM10qSduVa7Az1");
        httpPost.addHeader("Authorization", "Bearer eyJraWQiOiI1MDZhY2QwYS0zN2M2LTQ2NjktYWYwZS0yODRiMDk1MTIzZGIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJGTUlhS0Z5TW5qZEk1QUFUZW5sTTEwcVNkdVZhN0F6MSIsImF1ZCI6IlNhbnRhbmRlciBPcGVuIEFQSSIsImNsaWVudElkIjoiRk1JYUtGeU1uamRJNUFBVGVubE0xMHFTZHVWYTdBejEiLCJhenAiOiJGTUlhS0Z5TW5qZEk1QUFUZW5sTTEwcVNkdVZhN0F6MSIsImlzcyI6IlNhbnRhbmRlciBKV1QgQXV0aG9yaXR5IFNhbmRib3giLCJ0eXAiOiJCZWFyZXIiLCJjbmYiOnsieDV0I1MyNTYiOiIwZjkyZDM1YTYwZjEwYzA2MTUyNmY3NGU5MjI3YzAzMDgxNDg5ZDhmIn0sIm9wYXF1ZVRva2VuIjoiYVhCSjRURmRHWHJtSXdZRWs1UndUVUZWUTVVbCIsImV4cCI6MTY5NTc1MTc3NCwidmMiOnsiY3JlZGVudGlhbFN1YmplY3QiOnsibmFtZSI6ImNucGpfcGFyY2Vpcm8iLCJ2YWx1ZSI6IjY4LjY2Ny44NTRcLzAwMDEtNjAifX0sImlhdCI6MTY5NTc1MDg3NCwianRpIjoiN2NmOWI1MjAtZjFmZS00MzJkLTliZWMtOWE0MTVlYjk5YTAxIn0.R-Uc3MVvGOCii-kdqjJ64umaK66-IMVOlFP2p8NmWjO_bnc7kOCK-FSZYKcUNZ81TUsnoPAPxmuhx6FTZFqjWmlR-BN4rcrPsQKq-xpKMVZnW1zMRt3auEPBby8XjnpbnL_F4LEzmWifdGhJLNt_fbp9S7EofuVo0JnLMu1H5blXeu-PDeUvAKd5N0FEeMzP6o45ymX951qlIlqJ0jjENQLk14rNbfj5_68OlcitPFietukfMvoRXQsbSXLRCqWgABQ_hR5VdcamTf0PPsaG3LOTSOeyBc8aTwPv5xpwcRnqCAEIq-75j36UxSpQyCco_taCrgLsevlEFumU_X_7fA");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");

          try {            
                // Original
                String query = "query getPropertiesByIds($SearchCriteriaByIds: SearchCriteriaByIdsInput) {\n"
                + "  getPropertiesByIds(searchCriteriaByIds: $SearchCriteriaByIds) {\n"
                + "  \"type\": \"BILLING\","
                //+ "  \"description\": \"Workspace de Cobrança\",\n"
                + "    \"covenants\": [\n" 
                + "        {\n" 
                + "            \"code\": \"1234567\"\n" 
                + "        }\n" 
                + "    ]\n"
                + "  properties {\n"
                + "      propertyId\n"
                + "    }\n"
                + "  }\n"
                + "}";
        
                String variable = "{\n"
                + " \"SearchCriteriaByIds\": {\n"
                + "     \"propertyIds\": [\n"
                + "         134388,\n"
                + "         424023,\n"
                + "         134388,\n"
                + "         22549064\n"
                + "     ]\n"
                + " }\n"
                + "}";
                                
//            String query = "{\n" +
//                           "    \"type\": \"BILLING\",\n" +
//                           "    \"description\": \"Workspace de Cobrança\",\n" +
//                           "    \"covenants\": [\n" +
//                           "        {\n" +
//                           "            \"code\": \"1234567\"\n" +
//                           "        }\n" +
//                           "    ]\n" +
//                           "}";
//            String variable = "{}";
            
            Map<String, Object> variables = new HashMap<>();
            
            variables.put("query", query);
            variables.put("variables", variable);
            
            JSONObject jsonobj; 
            jsonobj = new JSONObject(variables);           

            // Imprime json
            System.out.println(jsonobj.toString());
              
            StringEntity entity = new StringEntity(jsonobj.toString());
            httpPost.setEntity(entity);
            
            response = client.execute(httpPost);
            System.out.println("response: " + response);

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder builder = new StringBuilder();

            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }

            System.out.println(builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/