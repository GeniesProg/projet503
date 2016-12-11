package backOffice;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import utilisateurs.ServeurTCP;

import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;



class LoginHandler implements HttpHandler {

    public void handle(HttpExchange t) {
        String reponse = "<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">"
        		+ "<p style=\"text-align:center;padding:5px;color:white;background:#172183;\">Authentification</p>";
        
        URI requestedUri = t.getRequestURI();
        String query = requestedUri.getRawQuery();

        reponse += query;
        
        reponse += "</body>";
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

        //-------------------CLIENT TCP---------------------//
        
        Socket socket = null;
    	try {
    	    socket = new Socket("localhost", ServeurTCP.portEcoute);
    	} catch(UnknownHostException e) {
    	    System.err.println("Erreur sur l'hôte : " + e);
    	    System.exit(-1);
    	} catch(IOException e) {
    	    System.err.println("Cr�ation de la socket impossible (loginhandler) : " + e);
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
    	output.println("0_"+query);
     
    	// Recupération de la réponse du serveur TCP
    	String reponseTCP ="";
    	try {
    	    reponseTCP = input.readLine();
    	} catch(IOException e) {
    	    System.err.println("Erreur lors de la lecture : " + e);
    	    System.exit(-1);
    	}
    	reponse+="<p>"+reponseTCP+"</p>";
    	// Fermeture des flux et de la socket
    	try {
    	    input.close();
    	    output.close();
    	    socket.close();
    	} catch(IOException e) {
    	    System.err.println("Erreur lors de la fermeture des flux et de la socket : " + e);
    	    System.exit(-1);
    	}
        
    	//-------------------------------------------------------------------------//
 
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
        
        //--------------------------------------------------//
        
    }

}