import java.rmi.RemoteException;

import org.json.JSONException;

import backOffice.GestionCreation;
import backOffice.ServeurPortailBackOffice;
import certificats.ServeurUDP;
import rmi.ServeurRMI;
import utilisateurs.ServeurRMIUtilisateurs;
import utilisateurs.ServeurTCP;

public class Lancement {

	public static void main(String[] args) {
		/*Thread t1 = new Thread(new Runnable() {
			public void run() {
				ServeurUDP.main(args);	
			}
		});
		t1.start();*/

		ServeurPortailBackOffice.main(args);
		ServeurRMIUtilisateurs.main(args);
		try {
			ServeurRMI.main(args);			
		} catch (RemoteException | JSONException e) {
			System.out.println("Problème RMI");
			e.printStackTrace();
		}
		
		ServeurTCP.main(args);
		
	}
	
}
