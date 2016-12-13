package rmi;
import java.rmi.server.UnicastRemoteObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Sondage extends UnicastRemoteObject implements ISondage {

    private static int idCount=0;
    private int id;
    private String titre;
    private ArrayList<Question> quesAndRep ;
    private int[][] compta;
    private int active;   
    
    public Sondage(int id, String titre, ArrayList<Question> tab, int active) throws RemoteException {
    	this.titre = titre;
    	this.quesAndRep = new ArrayList<>(tab);
    	this.id = id;
    	this.compta = new int[tab.size()][];
    	this.active = active;
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
    	res+="<button style=\"border: none;color: #ffffff;display: block;margin: auto;background: #172183;padding: 5px 20px;cursor:pointer;\" type=\"submit\" value=\"Submit\">Submit</button>";
    	res+="</ul>";
    	return res;
    }

	@Override
	public int[][] getcompta() throws RemoteException {
		return this.compta;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public ArrayList<Question> getQuesAndRep() {
		return quesAndRep;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public void setQuesAndRep(ArrayList<Question> quesAndRep) {
		this.quesAndRep = quesAndRep;
	}
	
}