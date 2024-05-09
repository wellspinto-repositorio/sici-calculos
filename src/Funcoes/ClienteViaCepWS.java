package Funcoes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Samic
 */
public class ClienteViaCepWS {
    public ClienteViaCepWS() {}
    
    public static CEPEndereco buscarCep(String cep) {
        String json = null;

        try {
            URL url = new URL("http://viacep.com.br/ws/"+ cep +"/json");
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuilder jsonSb = new StringBuilder();

            br.lines().forEach(l -> jsonSb.append(l.trim()));

            json = jsonSb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CEPEndereco retorno = null;
        if (json != null) {
            JSONObject my_obj = null;
            try {
                my_obj = new JSONObject(json);

                // Setar dados
                retorno = new CEPEndereco();
                retorno.setLogradouro(my_obj.getString("logradouro"));
                retorno.setComplemento(my_obj.getString("complemento"));
                retorno.setBairro(my_obj.getString("bairro"));
                retorno.setLocalidade(my_obj.getString("localidade"));
                retorno.setUf(my_obj.getString("uf"));
                retorno.setCep(my_obj.getString("cep"));
                retorno.setIbge(my_obj.getString("ibge"));
                retorno.setSiaf(my_obj.getString("siafi"));
            } catch (Exception e) {
                e.printStackTrace();
                retorno = null;
            }
            
        }
        return retorno;
    }    
    
    public static List<CEPEndereco> buscaEnderecos(String uf, String cidade, String logradouro) {
        String json = null;

        try {
            URL url = new URL("http://viacep.com.br/ws/"+ uf + "/" + cidade.replace(" ", "%20") + "/" + logradouro.replace(" ", "%20") + "/json");
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonSb = new StringBuilder();
            br.lines().forEach(l -> jsonSb.append(l.trim()));

            json = jsonSb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        List<CEPEndereco> retorno = new ArrayList<>();
        if (json != null) {
            JSONArray  my_array = null;
            try {
                my_array = new JSONArray(json);

                for (int i=0; i < my_array.length(); i++) {
                    CEPEndereco myEnder = new CEPEndereco();
                    myEnder.setLogradouro(my_array.getJSONObject(i).getString("logradouro"));
                    myEnder.setComplemento(my_array.getJSONObject(i).getString("complemento"));
                    myEnder.setBairro(my_array.getJSONObject(i).getString("bairro"));
                    myEnder.setLocalidade(my_array.getJSONObject(i).getString("localidade"));
                    myEnder.setUf(my_array.getJSONObject(i).getString("uf"));
                    myEnder.setCep(my_array.getJSONObject(i).getString("cep"));
                    myEnder.setIbge(my_array.getJSONObject(i).getString("ibge"));
                    myEnder.setSiaf(my_array.getJSONObject(i).getString("siafi"));
                    retorno.add(myEnder);
                    //System.out.println(my_array.getJSONObject(i));
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }                        
        }
        return retorno;
    }    
}