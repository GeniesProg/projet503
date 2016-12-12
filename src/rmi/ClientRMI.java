package rmi;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;

/**
 * Client permettant d'interroger la personne sur le serveur distant.
 * @author Cyril Rabat
 * @version 22/10/2013
 */
public class ClientRMI {

    /**
     * Méthode principale.
     * @param args inutilisé
     */
    public static void main(String[] args) {
	IGestionnaireSondages so = null;

	// Récupération du sondage distant
	try {
	    so = (IGestionnaireSondages)Naming.lookup("rmi://localhost/sondages");
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
		sondages = so.getSondages();
	} catch (RemoteException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	for (int j = 0 ; j < sondages.size(); j++) {
		try {
			System.out.println(sondages.get(j).getTitre());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    }
}