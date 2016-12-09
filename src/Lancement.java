import java.rmi.RemoteException;

import org.json.JSONException;

import backOffice.ServeurPortailBackOffice;
import rmi.ServeurRMI;
import utilisateurs.ServeurRMIUtilisateurs;
import utilisateurs.ServeurTCP;

public class Lancement {

	public static void main(String[] args) {	
		ServeurPortailBackOffice.main(args);
		ServeurTCP.main(args);
		try {
			ServeurRMI.main(args);			
		} catch (RemoteException | JSONException e) {
			System.out.println("Problème RMI");
			e.printStackTrace();
		}
		
		
	}
	
}
