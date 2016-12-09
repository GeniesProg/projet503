package utilisateurs;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGestionnaireDistant extends Remote{	
	
	public void ajouter(String login, String mdp, int type) throws RemoteException;
	
	public void supprimer(String login) throws RemoteException;	
	
	public String authentification(String login, String mdp) throws RemoteException;
	
	public void charger();
	
	public void sauvegarder();
}