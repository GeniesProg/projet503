package rmi;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Sondage extends UnicastRemoteObject implements ISondage {

    private static int idCount=0;
    private int id;
    private String titre;
    private ArrayList<Question> quesAndRep ;
    private int[][] compta;

    /**
     * Constructeur par défaut.
     */
    public Sondage() throws RemoteException {    	
		this.id=idCount;
		this.titre="defaut";
		this.quesAndRep=null;
		this.compta=null;
		idCount++;
    }
    
    public Sondage(int id, String titre, ArrayList<Question> tab) throws RemoteException {
    	this.titre = titre;
    	this.quesAndRep = new ArrayList<>(tab);
    	this.id = id;
    	this.compta = new int[tab.size()][];
    	
    	for (int i = 0 ; i < tab.size() ; i++) {
    		this.compta[i] = new int[this.quesAndRep.get(i).nbReponses()];
    	}
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

	@Override
	public void updateCompta(String json) throws RemoteException {
		JSONObject objet = new JSONObject(json);
		JSONArray a = objet.getJSONArray("liste");
		
		for (int i = 0 ; i < a.length() ; i++) {
			JSONObject element = a.getJSONObject(i);
			System.out.println(element);
			System.out.println(element.getString(String.valueOf(i+1)));
			if (element.getString(String.valueOf(i+1)).equals("A")){
				this.compta[i][0]++;
			} else if (element.getString(String.valueOf(i+1)).equals("B")) {
				//this.compta[i][1]++;
			} else if (element.getString(String.valueOf(i+1)).equals("C")) {
				//this.compta[i][2]++;
			} else if (element.getString(String.valueOf(i+1)).equals("D")) {
				//this.compta[i][3]++;
			}
		}
	}

	@Override
	public String affichageTotal() throws RemoteException {
		String s = "<p>Les résultats totaux: </p>";
		for (int i = 0 ; i < this.compta.length ; i++) {
			s += "Question " + (i+1) + ": <br>"; 
			for (int j = 0 ; j < this.compta[i].length; j++) {
				s+= "     ";
				if (j == 0) s+= "A";
				else if (j == 1) s+= "B";
				else if (j == 2) s+= "C";
				else if (j == 3) s+= "D";
				s+= ": " + this.compta[i][j] + "<br>";
			}
		}
		return s;
	}

	@Override
	public int[][] getcompta() throws RemoteException {
		// TODO Auto-generated method stub
		return this.compta;
	}
	

	
}