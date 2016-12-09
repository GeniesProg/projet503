package backOffice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class SubmitHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange t) throws IOException {
		URI requestedUri = t.getRequestURI();
        String query = requestedUri.getRawQuery();

        // Utilisation d'un flux pour lire les données du message Http
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(t.getRequestBody(),"utf-8"));
        } catch(UnsupportedEncodingException e) {
            System.err.println("Erreur lors de la récupération du flux " + e);
            System.exit(-1);
        }
	
        // Récupération des données en POST
        try {
            query = br.readLine();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture d'une ligne " + e);
            System.exit(-1);
        }
        
        String reponse = query + "<br>";
        //1-group_1=A&1-group_2=A&1-group_3=A
        /*JSONObject j = new JSONObject();
        JSONArray a = new JSONArray();
        String sondage = "def";
        String[] parts = query.split("&");
        for (int i = 0 ; i < parts.length ; i++) {
        	JSONObject y = new JSONObject();
        	String[] p2 = parts[i].split("=");
        	sondage = p2[0].split("-")[0];
        	String question = p2[0].split("_")[1];
        	String rep = p2[1];
        	y.put(question, rep);
        	a.put(y);        	
        }
        j.put("s"+sondage, a);
        String fichier = "reponses/sondage"+sondage+".json";

        reponse += j.toString();*/
        
        /*BufferedWriter out = new BufferedWriter(new FileWriter(fichier));
        out.write(j.toString());
        out.close();*/
     // Envoi de l'entête Http
        try {
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, 0);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-tête : " + e);
            System.exit(-1);
        }

        // Envoi du corps (données HTML)
        try {
            OutputStream os = t.getResponseBody();
            os.write(reponse.getBytes());
            os.close();
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi du corps : " + e);
        }
		
	}

}
