package rmi;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ArraySondage extends UnicastRemoteObject implements IArraySondage  {

	private ArrayList<ISondage> sondages;
	
    public ArraySondage(ArrayList<ISondage> sondages) throws RemoteException {
    	this.sondages = new ArrayList<>(sondages);
    }

	public ArrayList<ISondage> getSondages() throws RemoteException {		
		return this.sondages;
	}
}
