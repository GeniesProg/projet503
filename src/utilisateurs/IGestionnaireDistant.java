package utilisateurs;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IGestionnaireDistant extends Remote{	
	
	public void ajouter(String login, String mdp, int type) throws RemoteException;	
	public void supprimer(String login) throws RemoteException;		
	public String authentification(String login, String mdp) throws RemoteException;	
	public int getNbUsers() throws RemoteException;
	public ArrayList<Utilisateur> getUtilisateurs() throws RemoteException;
    public boolean aRepondu(String login, int sondage) throws RemoteException;
	public int getNbDonnees() throws RemoteException;
	public void ajouterDonnee(String login, int sondage, String[] reponses) throws RemoteException;
	
	public void chargerDonnees();
	public void sauvegarderDonnees();
	public void charger();	
	public void sauvegarder();
}