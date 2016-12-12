package rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface IGestionnaireSondages extends Remote{
	public ArrayList<ISondage> getSondages() throws RemoteException;
	
	public void updateCompta(String json) throws RemoteException;
	public String affichageTotal(int sondage) throws RemoteException;
	public void chargerCompta() throws RemoteException;
	public void sauvegarderCompta() throws RemoteException;
	public Compta createComtpaFromSondage(Sondage s) throws RemoteException;
	public void ajouterCompta(Compta c) throws RemoteException;
	
	public void updateActivation(int sondage, int activation) throws RemoteException;
	public void chargerSondages() throws RemoteException;
	public void sauvegarderSondages() throws RemoteException;
	public void ajouterSondage(int n, String titre, ArrayList<Question> questions, int active) throws RemoteException;
	public boolean estActive(ISondage s) throws RemoteException;
}
