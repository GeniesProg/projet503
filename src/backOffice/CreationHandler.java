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
import java.util.ArrayList;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class CreationHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange t) throws IOException {
		String reponse = 
				"<html>"
				  +"<head>"
				   + "<title>Page admin</title>"
				    +"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
				  +"</head>"
				  +"<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">"
				  + "<p style=\"text-align:center;padding:5px;color:white;background:#172183;\">Vous êtes dans l'interface de création de sondage, laissez le(s) dernier(s) champ(s) de réponses vide si vous ne voulez pas 4 réponses!</p>";
		
		 URI requestedUri = t.getRequestURI();
	     String query = requestedUri.getRawQuery();
	        
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
        
        int questions = Integer.parseInt(query.split("=")[1]);
        
        String form = "<form action=\"http://localhost:8080/gestionCreation.html\" method=\"post\">";
        form += "Titre: <input type=\"text\" name=\"sondage\"><br>";
        for (int i = 1 ; i <= questions ; i++) {
        	form += "<p style=\"\">Q" + i + " <input style=\"width:75%;\" type=\"text\" name=\"Q" + i + "\"></p>";
        	for (int j = 1 ; j <= 4; j++) {
        		form += "<p style=\"margin-left:25px;width:75%;\">R" + j + "<input type=\"text\" name=\""+i + "_" + j + "\"></p>";
        	}
        }
        form += "<button style=\"border: none;color: #ffffff;background: #172183;padding: 5px 20px;cursor:pointer;\">Commencer la création</button>"
        		 + "</form>";
		
		reponse += form + "</body></html>";
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
