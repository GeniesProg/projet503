package rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface IArraySondage extends Remote{
	public ArrayList<ISondage> getSondages() throws RemoteException;
	
	public void updateCompta(String json) throws RemoteException;
	public String affichageTotal(int sondage) throws RemoteException;
	public void chargerCompta() throws RemoteException;
	public void sauvegarderCompta() throws RemoteException;
}
