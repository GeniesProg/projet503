package utilisateurs;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ServeurRMIUtilisateurs {
	public static void main(String[] args) {
    	
		try {
            LocateRegistry.createRegistry(1099);
  		} catch(RemoteException e) {
  		    System.err.println("Erreur lors de la recuperation du registry : " + e);
              System.exit(-1);
        }
		try {
		    GestionnaireDistant g = new GestionnaireDistant();
		    Naming.rebind("utilisateurs", g);
		} catch(RemoteException e) {
		    System.err.println("Erreur lors de l'enregistrement : " + e);
		    System.exit(-1);
		} catch(MalformedURLException e) {
		    System.err.println("URL mal formée : " + e);
		    System.exit(-1);
		}
		System.out.println("Le serveur RMI de gestion des utilisateurs a démarré");
	}
	
}
