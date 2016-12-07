import java.io.IOException;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

/**
 * Classe correspondant à un serveur Http simple.
 * Le serveur �coute sur le port 8080 sur le contexte 'authentication.html'.
 * Le r�sultat est une simple page qui affiche les donn�es envoy�es en POST et en GET
 * @author Cyril Rabat
 * @version 2015/06/25
 */
public class ServeurPortailBackOffice {

    public static void main(String[] args) {	
        HttpServer serveur = null;
        try {
            serveur = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch(IOException e) {
            System.err.println("Erreur lors de la cr�ation du serveur " + e);
            System.exit(-1);
        }

        serveur.createContext("/authentication.html", new LoginHandler());
        serveur.createContext("/index.html", new IndexHandler());
        serveur.createContext("/admin.html", new AdminHandler());
        serveur.createContext("/user.html", new UserHandler());
        serveur.createContext("/sondage.html", new SondageHandler());
        serveur.setExecutor(null);
        serveur.start();
        
	System.out.println("Serveur d�marr�. Pressez CRTL+C pour arr�ter.");
    }

}
