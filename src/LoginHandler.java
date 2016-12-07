import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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


/**
 * Classe correspondant au handler sur le contexte 'authentication.html'.
 * @author Cyril Rabat
 * @version 2015/06/25
 */
//test
class LoginHandler implements HttpHandler {

    public void handle(HttpExchange t) {
        String reponse = "<h1>Authentification</h1>";

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

        //-------------------CLIENT TCP---------------------//
        
        Socket socket = null;
    	try {
    	    socket = new Socket("localhost", ServeurTCP.portEcoute);
    	} catch(UnknownHostException e) {
    	    System.err.println("Erreur sur l'h�te : " + e);
    	    System.exit(-1);
    	} catch(IOException e) {
    	    System.err.println("Cr�ation de la socket impossible : " + e);
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
    	output.println(query);
     
    	// Recup�ration de la r�ponse du serveur TCP
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
 
        // Envoi de l'en-t�te Http
        try {
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, 0);
        } catch(IOException e) {
            System.err.println("Erreur lors de l'envoi de l'en-t�te : " + e);
            System.exit(-1);
        }

        // Envoi du corps (donn�es HTML)
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