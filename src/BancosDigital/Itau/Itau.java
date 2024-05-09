package BancosDigital.Itau;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Itau {
    public static String token_teste_uri = "https://devportal.itau.com.br/api/jwt";
    public static String boleto_incluir_teste_uri = "https://devportal.itau.com.br/sandboxapi/cash_management_ext_v2/v2/boletos";
    public static String boleto_baixar_teste_uri = "https://devportal.itau.com.br/sandboxapi/cash_management_ext_v2/v2/boletos";
    public static String boleto_consultar_teste_uri = "https://devportal.itau.com.br/sandboxapi/cash_management_ext_v2/v2/boletos";
    
    /**
    / Retono em caso de erro
    / Retorna na função insertBoleta
    **/
    private String codErro;
    private String msgErro;
    
    // Parametros Token 
    private String grant_type = "client_credentials";
    private static String clientId = "11aee6a0-aa09-377f-a278-d27362229203";
    private static String clientSecret = "7a38e2cb-47ae-4a4c-abc8-35346e306769";
    
    public String getCodErro() { return codErro; }
    public String getMsgErro() { return msgErro; }
    
    public static void main(String[] args) throws Exception {
        Object[] tk = new Itau().gToken(new String[] {clientId, clientSecret, token_teste_uri});
        Object[] stk = (Object[])tk[1];
        String token = stk[0].toString();
        System.out.println("token: " + token);
        
        ItauPagador itauPagador = new ItauPagador();
        itauPagador.setEtapa_processo_boleto("validacao");
        
        BeneficiarioItau beneficiario = new BeneficiarioItau();
        beneficiario.setId_beneficiario("340600000010");
        itauPagador.setBeneficiario(beneficiario);
        
        DadosBoletoItau dadosboleto = new DadosBoletoItau();
        dadosboleto.setDescricao_instrumento_cobranca("boleto");
        dadosboleto.setTipo_boleto("a vista");
        dadosboleto.setCodigo_carteira("109");
        dadosboleto.setValor_total_titulo("00000000000000100");
        dadosboleto.setCodigo_especie("1");
        itauPagador.setDado_boleto(dadosboleto);
        
        Pagador pagador = new Pagador();
        Pessoa pessoa = new Pessoa();
        pessoa.setNome_pessoa("Wellington de Souza Pinto");
        TipoPessoa tipoPessoa = new TipoPessoa();
        tipoPessoa.setCodigo_tipo_pessoa("F");
        tipoPessoa.setNumero_cadastro("01903033799");
        pessoa.setTipo_pessoa(tipoPessoa);
        pagador.setPessoa(pessoa);
        itauPagador.setPagador(pagador);
        
        Endereco endereco = new Endereco();
        endereco.setNome_logradouro("Rua Professor Mesquita, 130 - Casa 1 - Parte");
        endereco.setNome_bairro("Nova Cidade");
        endereco.setNome_cidade("Sao Goncalo");
        endereco.setSigla_UF("RJ");
        endereco.setNumero_CEP("24455556");
        itauPagador.setEndereco(endereco);
        
        DadosIndividuaisBoleto dadosIndividuaisBoleto = new DadosIndividuaisBoleto();
        dadosIndividuaisBoleto.setData_vencimento("2023-09-30");
        //dadosIndividuaisBoleto.setData_limite_pagamento("0");
        dadosIndividuaisBoleto.setNumero_nosso_numero("00000010");
        dadosIndividuaisBoleto.setValor_titulo("00000000000000100");
        itauPagador.setDados_individuais_boleto(dadosIndividuaisBoleto);
        
        itauPagador.setTexto_seu_numero("1000/1001");
        
        Juros juros = new Juros();
        juros.setCodigo_tipo_juros("91");
        juros.setData_juros("01");
        juros.setPercentual_juros("000000100000");
        itauPagador.setJuros(juros);
        
        Multa multa = new Multa();
        multa.setCodigo_tipo_multa("02");
        multa.setQuantidade_dias_multa("01");
        multa.setPercentual_multa("000001000000");
        itauPagador.setMulta(multa);
        
        RecebimentoDivergente recebimentoDivergente = new RecebimentoDivergente();
        recebimentoDivergente.setCodigo_tipo_autorizacao("03");
        itauPagador.setRecebimento_divergente(recebimentoDivergente);
        
        Protesto protesto = new Protesto();
        protesto.setProtesto("false");
        itauPagador.setProtesto(protesto);
        
        Negativacao negativacao = new Negativacao();
        negativacao.setNegativacao("false");
        itauPagador.setNegativacao(negativacao);
        
        // Mensagens
        itauPagador.setMessage1("APOS O VENCIMENTO MULTA DE 10% POR MES.");
        itauPagador.setMessage2("APOS O VENCIMENTO JUROS DE MORA DE 1% POR DIA.");
        //itauPagador.setMessage2("");
        //itauPagador.setMessage2("");

        String json = itauPagador.build();
        System.out.println("json: " + json);
        
        Itau itau = new Itau();
        //Object[] consulta = itau.selectBoleta(new String[] {token, boleto_consultar_teste_uri}, "340600000010", "109", "00000001");
        //Object[] baixa = itau.baixaBoleta(new String[] {token, boleto_baixar_teste_uri}, "34060000001010900000001");
        //Object[] inserir = itau.insertBoleta(new String[] {token,boleto_incluir_teste_uri}, json);
        
        
        System.out.println("...");
    }

    public Object[] insertBoleta(String[] dados, String json_message) throws Exception{
        // Pega dados
        String token = dados[0].trim();
        String url_ws = dados[1].trim();

        byte[] postData = json_message.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("x-itau-apikey", token);
        uc.setRequestProperty("x-sandbox-token", token);
        uc.setRequestProperty("data-raw", "");
        uc.setRequestProperty("charset", "utf-8");
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
        uc.setUseCaches( false );
        
        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
        ));
        String message = br.readLine();
        
        String[] infoMessage = null; JSONObject jsonOb = null;
        if (statusCode != 200) {
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
            String strjson = myfunction(jsonOb,"dado_boleto").toString();
            strjson = myfunction(new JSONObject(strjson),"dados_individuais_boleto").toString();
            strjson = strjson.substring(1, strjson.length() - 1);
            jsonOb = new JSONObject(strjson);
            String dtvencimento = myfunction(jsonOb,"data_vencimento").toString();
            String codigoBarras = myfunction(jsonOb,"codigo_barras").toString();
            String linhaDigitavel = myfunction(jsonOb,"numero_linha_digitavel").toString();
            infoMessage = new String[] {dtvencimento, codigoBarras, linhaDigitavel};
            
            codErro = null; msgErro = null;
        }       
       
       uc.disconnect();
       
       return new Object[] {statusCode, infoMessage};
    }

    public Object[] selectBoleta(String[] dados, String id_beneficiario, String codigo_carteira, String nosso_numero) throws Exception{
        // {situacao_geral_boleto} testar condição
        
        // Pega dados
        String token = dados[0].toString().trim();
        String url_ws = dados[1].toString().trim();
        
        String urlParameters = "id_beneficiario=" + id_beneficiario + "&codigo_carteira=" + codigo_carteira + "&nosso_numero=" + nosso_numero;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;        
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("x-itau-apikey", token);
        uc.setRequestProperty("x-sandbox-token", token);
        uc.setRequestProperty("data-raw", "");
        uc.setRequestProperty("charset", "utf-8");
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
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
        } catch (Exception e) { e.printStackTrace(); }
        
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
            JSONObject jsonOb = new JSONObject(message);
            infoMessage = new Object[] {jsonOb};
        }       
       
       uc.disconnect();
       return new Object[] {statusCode, infoMessage};       
    }

    public Object[] baixaBoleta(String[] dados, String idboleto) throws Exception{
        // Pega dados
        String token = dados[0].trim();
        String url_ws = dados[1].trim();

        String urlParameters = idboleto + "/baixa";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Type", "application/json");
        uc.setRequestProperty("x-itau-apikey", token);
        uc.setRequestProperty("x-sandbox-token", token);
        uc.setRequestProperty("data-raw", "");
        uc.setRequestProperty("charset", "utf-8");
        uc.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
        uc.setUseCaches( false );
        
        try( DataOutputStream wr = new DataOutputStream( uc.getOutputStream())) {
           wr.write( postData );
        }        
        
        int statusCode = uc.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? uc.getInputStream() : uc.getErrorStream()
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
        String url_ws = dados[2].trim();

        String urlParameters = "{\"client_id\": \"" + clientid + "\", \"client_secret\": \"" + clientsecret + "\"}";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        
        uc.setRequestProperty("Content-Type", "application/json"); 
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
            infoMessage = new Object[] {jsonOb};
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
        String url_ws = dados[2].trim();

        String urlParameters = "{\"client_id\": \"" + clientid + "\", \"client_secret\": \"" + clientsecret + "\"}";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
                
        URL url = new URL(url_ws);
        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
        // define que vai enviar dados da requisição
        uc.setDoOutput(true);
        uc.setInstanceFollowRedirects( false );
        uc.setRequestMethod("POST");
        
        uc.setRequestProperty("Content-Type", "application/json"); 
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
    
}
