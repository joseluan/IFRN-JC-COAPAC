package br.com.ifrn.coapac.utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class API {

    private static String token;
    private static String matricula;
    private static String senha;
    private static HashMap<String, String> meus_dados;

    public API(String matricula, String senha) {
        setMatricula(matricula);
        setSenha(senha);
    }

    @SuppressWarnings("unchecked")
	public String buscarToken() throws IOException {
        //Pegando o Token com POST
        Form form = Form.form();
        form.add("username", API.matricula);
        form.add("password", API.senha);

        HttpResponse response = Request.Post("https://suap.ifrn.edu.br/api/v2/autenticacao/token/")
                .bodyForm(form.build()).execute().returnResponse();
        HashMap<String, String> tokenHM;
        if (response != null) {
            InputStream source = response.getEntity().getContent();
            Reader reader = new InputStreamReader(source);
            Gson gson = new Gson();
            tokenHM = gson.fromJson(reader, HashMap.class); //você pode ultilizar o Gson ou org.json 
            return tokenHM.get("token").toString();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
	public HashMap<String, String> buscarDados() throws IOException {
    	String url = "https://suap.ifrn.edu.br/api/v2/minhas-informacoes/meus-dados/";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Authorization", "JWT " + buscarToken());
		con.setRequestProperty("Accept", "application/json");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		Gson gson = new Gson();
		HashMap<String, String> meus_dados = gson.fromJson(response.toString(),
														   HashMap.class); //você pode ultilizar o Gson ou org.json 
   
		return meus_dados;
    }

    //get e set
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        API.token = token;
    }

    public String getMatricula() {
        return matricula;
    }

    public static void setMatricula(String matricula) {
        API.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public static void setSenha(String senha) {
        API.senha = senha;
    }

    public static HashMap<String, String> getMeus_dados() {
        return meus_dados;
    }

    public static void setMeus_dados(HashMap<String, String> meus_dados) {
        API.meus_dados = meus_dados;
    }

}
