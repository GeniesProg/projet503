package backOffice;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import rmi.IArraySondage;
import rmi.ISondage;

import com.sun.net.httpserver.Headers;

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


public class SondageHandler implements HttpHandler{

	public void handle(HttpExchange t) throws IOException {
		
		String reponse = "<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">";
				//+ "<p>coucou y aura des sondages ici bientot</p>";
		
		URI requestedUri = t.getRequestURI();
        String query = requestedUri.getRawQuery();

        // Utilisation d'un flux pour lire les donn�es du message Http
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(t.getRequestBody(),"utf-8"));
        } catch(UnsupportedEncodingException e) {
            System.err.println("Erreur lors de la r�cup�ration du flux " + e);
            System.exit(-1);
        }
	
        // R�cup�ration des donn�es en POST
        try {
            query = br.readLine();
        } catch(IOException e) {
            System.err.println("Erreur lors de la lecture d'une ligne " + e);
            System.exit(-1);
        }
        //reponse += query;
        String[] parts = query.split("&");
        String nom = parts[1].split("=")[1];
        String titre = parts[0].split("=")[0];
        String num = parts[0].split("=")[1];
        reponse += "<p style=\"text-align:center;padding:5px;color:white;background:#172183;\">>" + nom +", vous avez choisi le sondage numéro "+ num +", "+ titre + "</p>";
        
        ISondage s = null ;
		try {
		    s = (ISondage)Naming.lookup("rmi://localhost/sondage"+num);
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
		
		reponse += "<p>" + s.affichage();
		reponse += "<input type=\"hidden\" name=\"login\" value=\""+ nom +"\">" + "</p>" + "</form></body>";
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
