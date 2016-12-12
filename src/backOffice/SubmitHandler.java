package backOffice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import rmi.IArraySondage;
import rmi.ISondage;
import utilisateurs.ServeurTCP;

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
        String reponse = "";
        //reponse += query + "<br>";
        //1-group_1=A&1-group_2=A&1-group_3=A
        JSONObject j = new JSONObject();
        JSONArray a = new JSONArray();
        String sondage = "def";
        String[] parts = query.split("&");
        for (int i = 0 ; i < parts.length - 1 ; i++) {
        	JSONObject y = new JSONObject();
        	String[] p2 = parts[i].split("=");
        	sondage = p2[0].split("-")[0];
        	String question = p2[0].split("_")[1];
        	String rep = p2[1];
        	y.put(question, rep);
        	a.put(y);        	
        }
        j.put("liste", a);
        j.put("login", parts[parts.length-1].split("=")[1]);
        j.put("sondage", sondage);
        String fichier = "reponses/sondage"+sondage+".json";

        //reponse += j.toString();   

        IArraySondage gest = null ;
		try {
			gest = (IArraySondage)Naming.lookup("rmi://localhost/sondages");
	        
		} catch(NotBoundException e) {
		    System.err.println("Pas possible d'accéder à l'objet distant (not bound): " + e);
		    System.exit(-1);
		} catch(MalformedURLException e) {
		    System.err.println("URL mal forme : " + e);
		    System.exit(-1);
		} catch(RemoteException e) {
		    System.err.println("Pas possible d'accéder à l'objet distant (remote) : " + e);
		    System.exit(-1);
		}

		gest.updateCompta(j.toString());
		reponse += "<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">";
		reponse += gest.affichageTotal(Integer.parseInt(sondage));
		reponse += "</body>";
        BufferedWriter out = new BufferedWriter(new FileWriter(fichier));
        out.write(j.toString());
        out.close();
        
        //DISCUSSION POUR UPDATE L'USER---------------------------------------------------------------------
        Socket socket = null;
    	try {
    	    socket = new Socket("localhost", ServeurTCP.portEcoute);
    	} catch(UnknownHostException e) {
    	    System.err.println("Erreur sur l'hôte : " + e);
    	    System.exit(-1);
    	} catch(IOException e) {
    	    System.err.println("Création de la socket impossible (apres le submit) : " + e);
    	    System.exit(-1);
    	}
     
    	// Association d'un flux d'entr�e et de sortie
    	BufferedReader input = null;
    	PrintWriter output = null;
    	try {
    	    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	    output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    	} catch(IOException e) {
    	    System.err.println("Association des flux impossible : " + e);
    	    System.exit(-1);
    	}    	
    	// Envoi du couple login password
    	output.println("1_"+j.toString());
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
