package backOffice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import rmi.IGestionnaireSondages;

public class GestionActivation implements HttpHandler {

	@Override
	public void handle(HttpExchange t) throws IOException {
		String reponse = 
				"<html>"
				  +"<head>"
				   + "<title>Page admin</title>"
				    +"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
				  +"</head>"
				  +"<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">"
					+"<p style=\"text-align:center;padding:5px;color:white;background:#172183;\">Vos changements ont bien été pris en compte!</p>"
					+ "<form action=\"http://localhost:8080/admin.html\">"
					+ "<button style=\"border: none;color: #ffffff;display: block;margin: auto;background: #172183;padding: 5px 20px;cursor:pointer;\">Retour</button>"
					+ "</form>";
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
        
        IGestionnaireSondages array = null;
		try {
		    array = (IGestionnaireSondages)Naming.lookup("rmi://localhost/sondages");
		} catch(NotBoundException e) {
		    System.err.println("Pas possible d'accéder à l'objet distant : " + e);
		    System.exit(-1);
		} catch(MalformedURLException e) {
		    System.err.println("URL mal forme : " + e);
		    System.exit(-1);
		} catch(RemoteException e) {
		    System.err.println("Pas possible d'accéder à l'objet distant : " + e);
		    System.exit(-1);
		}		
        
        String [] elements = query.split("&");
        for (int i = 0 ; i < elements.length ; i++) {
        	String[] curr = elements[i].split("=");
        	String s = curr[0];
        	int active = Integer.parseInt(curr[1]);
        	int sondage = Integer.parseInt(s.substring(1, s.length()));
        	array.updateActivation(sondage, active);
        }
		
		reponse +="</body></html>";
		// Envoi de l'en-tête Http
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