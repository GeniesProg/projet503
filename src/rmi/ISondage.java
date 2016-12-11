package rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface ISondage extends Remote {

    public int getId() throws RemoteException;
    
    public String getTitre() throws RemoteException;
    public int[][]  getcompta() throws RemoteException;
    public String affichage() throws RemoteException;
    public String affichageTotal() throws RemoteException;    

}