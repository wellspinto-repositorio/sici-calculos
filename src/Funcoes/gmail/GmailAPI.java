package Funcoes.gmail;

import Funcoes.VariaveisGlobais;
import Funcoes.jDirectory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import org.json.JSONException;

/**
 *
 * @author YOGA 510
 */
public class GmailAPI {
	/*
            1.Get code : 
                https://accounts.google.com/o/oauth2/v2/auth?
                 scope=https://mail.google.com&
                 access_type=offline&
                 redirect_uri=http://localhost&
                 response_type=code&
                 client_id=[Client ID]
            2. Get access_token and refresh_token
                curl \
               --request POST \
               --data "code=[Authentcation code from authorization link]&client_id=[Application Client Id]&client_secret=[Application Client Secret]&redirect_uri=http://localhost&grant_type=authorization_code" \
               https://accounts.google.com/o/oauth2/token
            3.Get new access_token using refresh_token
                curl \
                --request POST \
                --data "client_id=[your_client_id]&client_secret=[your_client_secret]&refresh_token=[refresh_token]&grant_type=refresh_token" \
                https://accounts.google.com/o/oauth2/token
	
        */
    
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String user = "me";
	static Gmail service = null;
        //private static File filePath = new File("C:\\cert\\" + "client_secret.json");
        
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		//getGmailService();		
	}

	public static Gmail getGmailService() throws IOException, GeneralSecurityException {
                String fPath = System.getProperty("user.dir") + "/cert/" + VariaveisGlobais.dCliente.get("marca").trim() + "_credentials.json";
                if (!new File(fPath).exists()) return null;
                File filePath = new File(fPath);
		InputStream in = new FileInputStream(filePath); 
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Credential builder

		Credential authorize = new GoogleCredential.Builder().setTransport(GoogleNetHttpTransport.newTrustedTransport())
				.setJsonFactory(JSON_FACTORY)
				.setClientSecrets(clientSecrets.getDetails().getClientId().toString(),
						clientSecrets.getDetails().getClientSecret().toString())
				.build().setAccessToken(getAccessToken()).setRefreshToken(
						                  VariaveisGlobais.REFRESH_TOKEN);

		// Create Gmail service
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize)
				.setApplicationName(VariaveisGlobais.PROJECT_ID).build();

		return service;
	}

	private static String getAccessToken() {
		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("grant_type", "refresh_token");
			params.put("client_id", VariaveisGlobais.CLIENT_ID); 
			params.put("client_secret", VariaveisGlobais.CLIENT_SECRET); 
			params.put("refresh_token", VariaveisGlobais.REFRESH_TOKEN); 

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0) {
					postData.append('&');
				}
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			URL url = new URL("https://accounts.google.com/o/oauth2/token");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.getOutputStream().write(postDataBytes);

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
			}

			JSONObject json = new JSONObject(buffer.toString());
			String accessToken = json.getString("access_token");
			return accessToken;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

        public static String[] getToken(String auth_code, String client_id, String client_secret, String redirect_uri) {
		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("code", auth_code);
			params.put("client_id", client_id); 
			params.put("client_secret", client_secret); 
			params.put("redirect_uri", redirect_uri); 
                        params.put("grant_type", "authorization_code");

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0) {
					postData.append('&');
				}
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			URL url = new URL("https://accounts.google.com/o/oauth2/token");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			con.getOutputStream().write(postDataBytes);

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
			}

			JSONObject json = new JSONObject(buffer.toString());
			String accessToken = json.getString("access_token");
                        String refreshToken = json.getString("refresh_token");
			return new String[] {accessToken, refreshToken};
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
                    
        public static void openWebpage(URI uri) {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public static void openWebpage(URL url) {
            try {
                openWebpage(url.toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }        
        
    // Configuration Routines    
    public static void AuthGmail(String PathFile) throws MalformedURLException, FileNotFoundException, IOException {
        final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	File filePath = new File(PathFile);
        
        InputStream in = new FileInputStream(filePath); 
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        
        String client_id = clientSecrets.getDetails().get("client_id").toString();
        String project_id = clientSecrets.getDetails().get("project_id").toString();
        String auth_uri = clientSecrets.getDetails().get("auth_uri").toString();
        String token_uri = clientSecrets.getDetails().get("token_uri").toString();
        String auth_provider_x509_cert_url = clientSecrets.getDetails().get("auth_provider_x509_cert_url").toString();
        String client_secret = clientSecrets.getDetails().get("client_secret").toString();
        String redirect_uris = clientSecrets.getDetails().get("redirect_uris").toString().replace("[", "").replace("]", "");
        
        // Get Code para gerar Token
        openWebpage(new URL("https://accounts.google.com/o/oauth2/v2/auth?scope=https://mail.google.com&access_type=offline&redirect_uri=http://localhost&response_type=code&client_id=" + client_id));
    }
    
    public static void LoadGmailJSon(String PathFile, String auth_code) throws IOException, GeneralSecurityException {
        final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	File filePath = new File(PathFile); 
        
        InputStream in = new FileInputStream(filePath); 
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        
        String client_id = clientSecrets.getDetails().get("client_id").toString();
        String project_id = clientSecrets.getDetails().get("project_id").toString();
        String auth_uri = clientSecrets.getDetails().get("auth_uri").toString();
        String token_uri = clientSecrets.getDetails().get("token_uri").toString();
        String auth_provider_x509_cert_url = clientSecrets.getDetails().get("auth_provider_x509_cert_url").toString();
        String client_secret = clientSecrets.getDetails().get("client_secret").toString();
        String redirect_uris = clientSecrets.getDetails().get("redirect_uris").toString().replace("[", "").replace("]", "");
        
        String[] Tokens = GmailAPI.getToken(auth_code, client_id, client_secret, redirect_uris);
        String access_token = Tokens[0].toString();
        String refresh_token = Tokens[1].toString();
        
        CreateJSon(client_id, project_id, auth_uri, token_uri, auth_provider_x509_cert_url, client_secret, redirect_uris, auth_code, access_token, refresh_token);
    }

    private static void CreateJSon(
            String client_id, 
            String project_id, 
            String auth_uri, 
            String token_uri, 
            String auth_provider_x509_cert_url,
            String client_secret,
            String redirect_uris,
            String auth_code, 
            String access_token, 
            String refresh_token
            ) {
       JsonObject model = Json.createObjectBuilder()
               .add("installed",
                       Json.createObjectBuilder()
                               .add("client_id", client_id)
                               .add("project_id", project_id)
                               .add("auth_uri", auth_uri)
                               .add("token_uri", token_uri)
                               .add("auth_provider_x509_cert_url", auth_provider_x509_cert_url)
                               .add("client_secret", client_secret)
                               .add("redirect_uris", Json.createArrayBuilder().add(redirect_uris))
               )
               .add("j4rent_parameters", 
                       Json.createObjectBuilder()
                               .add("auth_code", auth_code)
                               .add("access_token", access_token)
                               .add("refresh_token", refresh_token)).build(); 
       
       new jDirectory(System.getProperty("user.dir") + "/cert/");
       String path = System.getProperty("user.dir") + "/cert/" + VariaveisGlobais.dCliente.get("marca").trim() + "_credentials.json";
       if (new File(path).exists()) new File(path).delete();
       
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.write(model.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void ReadJSon() throws JSONException {
        String path = System.getProperty("user.dir") + "/cert/" + VariaveisGlobais.dCliente.get("marca").trim() + "_credentials.json";
        if (!new File(path).exists()) {
            VariaveisGlobais.OUTLOOK = true;
            return;
        }
        JSONObject json = null;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<Object> collect = bufferedReader.lines().collect(Collectors.toList());
            StringBuilder jsonTemp = new StringBuilder();
            for (Object s : collect) {
                jsonTemp.append(s);
            }
            json = new JSONObject(jsonTemp.toString());
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Não foi possível ler o arquivo json");
        }

        if (json != null) {
            System.out.println("Lido a arquivo " + path);
            
            JSONObject installed = new JSONObject(json.get("installed").toString());
            VariaveisGlobais.CLIENT_ID = installed.get("client_id").toString();
            VariaveisGlobais.PROJECT_ID = installed.get("project_id").toString();
            VariaveisGlobais.AUTH_URI = installed.get("auth_uri").toString();
            VariaveisGlobais.TOKEN_URI = installed.get("token_uri").toString();
            VariaveisGlobais.AUTH_PROVIDER_X509_CERT_URL = installed.get("auth_provider_x509_cert_url").toString();
            VariaveisGlobais.CLIENT_SECRET = installed.get("client_secret").toString();
            VariaveisGlobais.REDIRECT_URIS = installed.get("redirect_uris").toString().replace("[", "").replace("]", "").replaceAll("\"", "");

            JSONObject j4rent_parameters = new JSONObject(json.get("j4rent_parameters").toString());
            VariaveisGlobais.AUTH_CODE = j4rent_parameters.get("auth_code").toString();
            VariaveisGlobais.ACCESS_TOKEN = j4rent_parameters.get("access_token").toString();
            VariaveisGlobais.REFRESH_TOKEN = j4rent_parameters.get("refresh_token").toString(); 
        }        
    }       
}