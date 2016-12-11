package backOffice;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;


public class AdminHandler implements HttpHandler {

	public void handle(HttpExchange t) throws IOException {
		String reponse = 
		"<html>"
		  +"<head>"
		   + "<title>Page admin</title>"
		    +"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
		  +"</head>"
		  +"<body style=\"font-family: Georgia, Times, serif;padding:20px;width:400px;border:1px solid #172183;\">"
			+"<p style=\"text-align:center;padding:5px;color:white;background:#172183;\">Bienvenue sur ta page l'admin!</p>";
		
		reponse +="</body></html>";
		// Envoi de l'en-tête Http
        try {
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, reponse.length());
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
