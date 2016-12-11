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
  + "<link rel=\"stylesheet\" type=\"text/css\" href=\"theme.css\">"
  +"</head>"
  + "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>"
  +"<body style=\"font-family: Georgia, Times, serif;\">"
    +"<form style=\"padding:20px;width:400px;border:1px solid #172183\" method=\"post\" action=\"http://localhost:8080/authentication.html\">"
    + "<fieldset style=\"\">"
    + "<ol style=\"list-style-type: none;margin: 0 auto;margin-left:-42px;\">"
    +"<legend style=\"text-align:center;padding:5px;color:white;background:#172183;\">Page de connexion</legend>"
    + "<li style=\"padding:5px;\"><label >Login</label><input style=\"margin-left:72px;\" type=\"text\" name=\"login\"/></li>"
     + "<li style=\"padding:5px;\"><label>Password</label><input style=\"margin-left:45px;\" type=\"password\" name=\"password\"/></li>"
     + "</ol>"
      +"</fieldset><fieldset><button style=\"border: none;color: #ffffff;display: block;margin: auto;background: #172183;padding: 5px 20px;\">Connexion</button></fieldset>"
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
