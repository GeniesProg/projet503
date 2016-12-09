package rmi;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Sondage extends UnicastRemoteObject implements ISondage {

    private static int idCount=0;
    private int id;
    private String titre;
    private ArrayList<Question> quesAndRep ;

    /**
     * Constructeur par défaut.
     */
    public Sondage() throws RemoteException {    	
		this.id=idCount;
		this.titre="defaut";
		this.quesAndRep=null;
		idCount++;
    }
    
    public Sondage(int id, String titre, ArrayList<Question> tab) throws RemoteException {
    	this.titre = titre;
    	this.quesAndRep = new ArrayList<>(tab);
    	this.id = id;
    	idCount++;
    }
    
    public int getId() throws RemoteException {
    	return this.id;
    }
    
    public String getTitre() throws RemoteException {
    	return this.titre;
    }
    
    public String affichage() throws RemoteException {
    	String res = "<form action=\"http://localhost:8080/submit.html\" method=\"post\" name=\""+ this.id + "\"><ul>" + this.id + ":" + this.titre;
    	for (int i = 0; i < this.quesAndRep.size();i++) {
    		res+= "<ul>"+quesAndRep.get(i)+"</ul>";
    	}
    	res+="<button type=\"submit\" value=\"Submit\">Submit</button>";
    	res+="</ul>";
    	return res;
    }
}