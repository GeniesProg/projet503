package backOffice;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;


public class IndexHandler implements HttpHandler {

	public void handle(HttpExchange t) {
        String reponse = "<html>"
  +"<head>"
   + "<title>Formulaire de connexion au portail back office (Http)</title>"
  +"</head>"
  + "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
  +"<body>"
   + "<h1>Page de connexion</h1>"

    +"<form method=\"post\" action=\"http://localhost:8080/authentication.html\">"
    +"<label>Login</label><input type=\"text\" name=\"login\"/>"
     + "<label>Password</label><input type=\"password\" name=\"password\"/>"
      +"<button>Connexion</button>"
      +"</form>"
  +"</body>"
+"</html>";    
        
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
