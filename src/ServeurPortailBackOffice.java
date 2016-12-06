import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import java.net.InetSocketAddress;

/**
 * Classe correspondant Ã  un serveur Http simple.
 * Le serveur écoute sur le port 8080 sur le contexte 'authentication.html'.
 * Le résultat est une simple page qui affiche les données envoyées en POST et en GET
 * @author Cyril Rabat
 * @version 2015/06/25
 */
public class ServeurPortailBackOffice {

    public static void main(String[] args) {	
        HttpServer serveur = null;
        try {
            serveur = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch(IOException e) {
            System.err.println("Erreur lors de la création du serveur " + e);
            System.exit(-1);
        }

        serveur.createContext("/authentication.html", new LoginHandler());
        serveur.setExecutor(null);
        serveur.start();
        
        /*
        serveur.createContext("/usersurvey.html", new UserSurveyHandler());
        serveur.setExecutor(null);
        serveur.start();
         */
	System.out.println("Serveur démarré. Pressez CRTL+C pour arrêter.");
    }

}