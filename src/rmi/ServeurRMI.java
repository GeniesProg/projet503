package rmi;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmi.ArraySondage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class ServeurRMI {

    public static void main(String[] args) throws RemoteException, JSONException {
    
    	try {
          LocateRegistry.createRegistry(1099);
		} catch(RemoteException e) {
		    System.err.println("Erreur lors de la recuperation du registry : " + e);
            System.exit(-1);
        }
    	
    	//Récupération des données JSON
    	String nomFichier = "sondages.json";
    	FileInputStream fs = null;
    	try {
    	    fs = new FileInputStream(nomFichier);
    	} catch(FileNotFoundException e) {
    	    System.err.println("Fichier '" + nomFichier + "' introuvable");
    	    System.exit(-1);
    	}
    	
    	String json = new String();
    	Scanner scanner = new Scanner(fs);
    	while(scanner.hasNext())
    	    json += scanner.nextLine();
    	scanner.close();
    	
    	JSONObject objet = new JSONObject(json);
    	JSONArray tableau = objet.getJSONArray("sondages");
    	
    	ArrayList<ISondage> sondages = new ArrayList<ISondage>();
    	
    	for(int i = 0; i < tableau.length(); i++) {
    	    JSONObject sondage = tableau.getJSONObject(i);
    	    JSONArray questions = sondage.getJSONArray("questions");
    	    ArrayList<Question> tabQuestions = new ArrayList<>();
    	    for (int j = 0; j < questions.length() ; j++) {
    	    	JSONObject question = questions.getJSONObject(j);
    	    	Question q = new Question(question.getInt("numero"), question.getString("intitule"));
    	    	JSONArray reponses = question.getJSONArray("reponses");
    	    	ArrayList<Reponse> tabReponses = new ArrayList<>();
    	    	for (int k = 0; k < reponses.length(); k++){
    	    		JSONObject reponse = reponses.getJSONObject(k);
    	    		Reponse r = new Reponse(reponse.getString("lettre"), reponse.getString("libelle"));
    	    		tabReponses.add(r);
    	    	}
    	    	q.setReponses(tabReponses);
    	    	tabQuestions.add(q);
    	    }
    	    
    	    Sondage s = new Sondage(sondage.getInt("id"), sondage.getString("titre"), tabQuestions);
    	    sondages.add((ISondage)s);
    	      	
    	
		try {	   
			ArraySondage as = new ArraySondage(sondages);
			//Naming.rebind("sondages", sondages);
			Naming.rebind("sondages", as);
			
		    
		} catch(RemoteException e) {
		    System.err.println("Erreur lors de l'enregistrement : " + e);
		    System.exit(-1);
		} catch(MalformedURLException e) {
		    System.err.println("URL mal formée : " + e);
		    System.exit(-1);
		}
    }
    }
}