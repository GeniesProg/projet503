package rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface IArraySondage extends Remote{
	public ArrayList<ISondage> getSondages() throws RemoteException;
}
