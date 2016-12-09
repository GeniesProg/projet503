package utilisateurs;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGestionnaireDistant extends Remote{
	
	public String getFichier() throws RemoteException;
	
	public void setFichier(String fichier)throws RemoteException;
	
	public void ajouter(String login, String mdp, int type) throws RemoteException;
	
	public void supprimer(String login) throws RemoteException;
	
	public boolean exists(String login)throws RemoteException;
	
	public String authentification(String login, String mdp) throws RemoteException;
	
	public void charger();
	
	public void sauvegarder();
}