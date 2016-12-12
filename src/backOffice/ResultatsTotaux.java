package backOffice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import rmi.IGestionnaireSondages;
import rmi.ISondage;

public class ResultatsTotaux implements HttpHandler {

	@Override
	public void handle(HttpExchange t) throws IOException {
		String reponse = 
				"<html>"
				  +"<head>"
				   + "<title>Page admin</title>"
				    +"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
				  +"</head>"
				  +"<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">"
					;
		
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

		ArrayList<ISondage> sondages = null;
		try {
			sondages = array.getSondages();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0 ; i < sondages.size() ; i++) {
			reponse += array.affichageTotal(sondages.get(i).getId());
		}
		reponse += "<form action=\"http://localhost:8080/admin.html\">"
				+ "<button style=\"border: none;color: #ffffff;display: block;margin: auto;background: #172183;padding: 5px 20px;cursor:pointer;\">Retour</button>"
				+ "</form>";
		reponse +="</body></html>";
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
