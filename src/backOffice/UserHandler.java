package backOffice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import rmi.IArraySondage;
import rmi.ISondage;
import utilisateurs.IGestionnaireDistant;

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
import java.util.ArrayList;

public class UserHandler implements HttpHandler {

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
        
        //String nom = query.split("=")[1];
		String reponse = 
		"<html>"
		  +"<head>"
		   + "<title>Page utilisateur</title>"
		    +"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
		   +"<script>"		  
		    +"</script>"
		  +"</head>";
		
		String nom = query.split("=")[1];
		reponse += "<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">"
			+"<p style=\"text-align:center;padding:5px;color:white;background:#172183;\">La page de l'ami " + nom +"</p>";
		//reponse += query;
		IArraySondage so = null;
		// Récupération du sondage distant
		try {
		    so = (IArraySondage)Naming.lookup("rmi://localhost/sondages");
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
		
		
		
		ArrayList<ISondage> sondages = null;
		try {
			sondages = so.getSondages();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		IGestionnaireDistant g = null;
		try {
		    g = (IGestionnaireDistant)Naming.lookup("rmi://localhost/utilisateurs");
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
		
		String test = "";
		for (int j = 0 ; j < sondages.size(); j++) {
			ISondage s = sondages.get(j);
			test += "<form action=\"http://localhost:8080/sondage.html\" method=\"post\">";
			if (g.aRepondu(nom, s.getId())) {
				try {				
					test+=
					  "<button style=\"cursor:not-allowed\" type=\"submit\" name=\""+s.getTitre()+"\" value=\""+s.getId()+"\" class=\"btn-link\" disabled>"+s.getTitre()+"</button>"
					  + "Vous avez deja répondu à ce sondage!";
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {				
					test+= "<button type=\"submit\" name=\""+s.getTitre()+"\" value=\""+s.getId()+"\" class=\"btn-link\">"+s.getTitre()+"</button>";

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			test+= "<input type=\"hidden\" name=\"login\" value=\""+ nom +"\"></form>";
	    }
		
		reponse += test;
		
		reponse += "</body>"
		+"</html>";
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
