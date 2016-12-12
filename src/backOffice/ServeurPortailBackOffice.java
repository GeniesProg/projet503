package backOffice;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;

import rmi.ServeurRMI;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;

import org.json.JSONException;

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
        serveur.createContext("/index.html", new IndexHandler());
        serveur.createContext("/admin.html", new AdminHandler());
        serveur.createContext("/user.html", new UserHandler());
        serveur.createContext("/sondage.html", new SondageHandler());
        serveur.createContext("/submit.html", new SubmitHandler());
        serveur.createContext("/resultats.html", new ResultatsTotaux());
        serveur.createContext("/activation.html", new ActivationHandler());
        serveur.createContext("/gestionActivation.html", new GestionActivation());
        serveur.setExecutor(null);
        serveur.start();
        
	System.out.println("Serveur portail b-o démarré.");	
    }
}
