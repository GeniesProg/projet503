package rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ISondage extends Remote {

    public int getId() throws RemoteException;
    
    public String getTitre() throws RemoteException;
    
    public String affichage() throws RemoteException;

}