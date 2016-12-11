package utilisateurs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class GestionnaireDistant implements IGestionnaireDistant, Serializable {
	private String fichier;
	private String fichierDonnees;
	private ArrayList<Utilisateur> utilisateurs ;
	private ArrayList<Donnee> donnees;
	private int nbUsers;
	
	public GestionnaireDistant() throws RemoteException {
		this.fichier = "utilisateurs.json";
		this.fichierDonnees = "rep.json";
		this.utilisateurs = new ArrayList<Utilisateur>();
		this.donnees = new ArrayList<Donnee>();		
		this.chargerDonnees();
	}
	
	public void ajouter(String login, String mdp, int type){
		utilisateurs.add(new Utilisateur(login, mdp, type));
		this.sauvegarder();
	}
	
	
	public void supprimer(String login){
		for (int i = 0 ; i < utilisateurs.size() ; i++) {
			if (login.equals(utilisateurs.get(i).getLogin())) {
				utilisateurs.remove(i);
			}
		}
		this.sauvegarder();
	}
	
	public void sauvegarder() {
		JSONObject j = new JSONObject();
		
		JSONArray a = new JSONArray();
		for (Utilisateur u: utilisateurs) {
			JSONObject ujson = new JSONObject();
			ujson.put("login", u.getLogin());
			ujson.put("mdp", u.getPassword());
			ujson.put("type", u.getType());
			a.put(ujson);
		}
		
		j.put("utilisateurs", a);
				
		FileWriter fs = null ; 
		
		try {
		    fs = new FileWriter(fichier);
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'ouverture du fichier " + fichier);
		    System.err.println(e);
		    System.exit(-1);
		}
		try {
		    j.write(fs);
		    fs.flush();
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'écriture dans le fichier : " + e);
		    System.exit(-1);
		}
		
	}
	
	public void charger() {
		FileInputStream fs = null;
		try {
		    fs = new FileInputStream(fichier);
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier '" + fichier + "' introuvable");
		    System.exit(-1);
		}
	 
		// Récupération de la chaîne JSON depuis le fichier
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();
	 
		// Création d'un objet JSON
		JSONObject objet = new JSONObject(json);		
		
		JSONArray tableau = objet.getJSONArray("utilisateurs");
		
		ArrayList<Utilisateur> nouveau = new ArrayList<Utilisateur>();
		for(int i = 0; i < tableau.length(); i++) {
		    JSONObject element = tableau.getJSONObject(i);
		    Utilisateur u = new Utilisateur(element.getString("login"),
		    		element.getString("mdp"),
		    		element.getInt("type"));
		    nouveau.add(u);
		    
		}
		utilisateurs = nouveau ;
		nbUsers = utilisateurs.size();
	}
	
	public String authentification(String login, String mdp) {
		String message = "";
		FileInputStream fs = null;
		try {
		    fs = new FileInputStream(fichier);
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier" + fichier +  "introuvable");
		    System.exit(-1);
		}
	 
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();

		JSONObject objet = new JSONObject(json);
	 
		JSONArray tableau = objet.getJSONArray("utilisateurs");
		boolean validate=false;
		int k =0;
		while(!validate && k<tableau.length()){
		    JSONObject element = tableau.getJSONObject(k);
		    if(element.getString("login").equals(login) && element.getString("mdp").equals(mdp)){
		    	validate=true;
		    	message="<p>Connexion réussie, "+ element.getString("login") +" vous allez être redirigé vers votre portail.</p>";
		    	int type = element.getInt("type");
		    	if (type == 0) {
			    	message += "<form id=\"troll\"action=\"http://localhost:8080/admin.html\" method=\"post\">"
			        		+"<input type=\"text\" style=\"display:none\" name=\"login\" value=\""+ login + "\">"
			        				+ "</form>";
		    	} else if (type == 1) {
			    	message += "<form id=\"troll\"action=\"http://localhost:8080/user.html\" method=\"post\">"
			        		+"<input type=\"text\" style=\"display:none\" name=\"login\" value=\""+ login + "\">"
			        				+ "</form>";
		    	}

		    	message += "<script>setTimeout(function(){document.getElementById(\"troll\").submit();}, 3000);</script>";
		    }
		    k++;
		}
		if(!validate) message="Mauvais couple login/password";
		
		return message;
	}
	@Override
	public int getNbUsers() throws RemoteException {
		return this.utilisateurs.size();
	}
	
	@Override
	public int getNbDonnees() throws RemoteException {
		return this.donnees.size();
	}

	@Override
	public ArrayList<Utilisateur> getUtilisateurs() throws RemoteException {
		return utilisateurs;
	}


	@Override
	public void ajouterDonnee(String login, int sondage, String[] reponses) throws RemoteException {
		this.chargerDonnees();
		this.donnees.add(new Donnee(login, sondage, reponses));
		this.sauvegarderDonnees();
	}


	@Override
	public void chargerDonnees() {
		FileInputStream fs = null;
		try {
		    fs = new FileInputStream(this.fichierDonnees);
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier '" + this.fichierDonnees + "' introuvable");
		    System.exit(-1);
		}
	 
		// Récupération de la chaîne JSON depuis le fichier
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();
	 
		// Création d'un objet JSON
		JSONObject objet = new JSONObject(json);		
		JSONArray tableau = objet.getJSONArray("reponses");
		
		ArrayList<Donnee> nouveau = new ArrayList<>();
		for (int i = 0; i< tableau.length(); i++) {
			JSONObject element = tableau.getJSONObject(i);
			
			JSONArray liste = element.getJSONArray("liste");
			String[] r = new String[liste.length()];
			for (int j = 0; j < liste.length(); j++) {
				JSONObject o = liste.getJSONObject(j);				
				r[j] = o.getString(String.valueOf(j+1)); 
			}
			Donnee d = new Donnee(element.getString("login"),
					element.getInt("sondage"), r);
			nouveau.add(d);
		}
		this.donnees = nouveau;
	}

	public void sauvegarderDonnees() {
		JSONObject j = new JSONObject();		
		JSONArray a = new JSONArray();
		for (Donnee d: this.donnees) {
			JSONObject donnjson = new JSONObject();
			donnjson.put("login", d.getLogin());
			donnjson.put("sondage", d.getSondage());
			JSONArray liste = new JSONArray();
			for (int i = 0; i < d.getReponses().length; i++) {
				JSONObject o = new JSONObject();
				o.put(String.valueOf(i+1), d.getReponses()[i]);
				liste.put(o);
			}
			donnjson.put("liste", liste);
			a.put(donnjson);
		}
		j.put("reponses", a);
		
		FileWriter fs = null ; 
		
		try {
		    fs = new FileWriter(this.fichierDonnees);
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'ouverture du fichier " + this.fichierDonnees);
		    System.err.println(e);
		    System.exit(-1);
		}
		try {
		    j.write(fs);
		    fs.flush();
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'écriture dans le fichier : " + e);
		    System.exit(-1);
		}		
	}

	@Override
	public boolean aRepondu(String login, int sondage) throws RemoteException {
		boolean trouve = false;
		FileInputStream fs = null;
		try {
		    fs = new FileInputStream(this.fichierDonnees);
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier '" + this.fichierDonnees + "' introuvable");
		    System.exit(-1);
		}
	 
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();
	 
		JSONObject objet = new JSONObject(json);	
		JSONArray a = objet.getJSONArray("reponses");
		int i = 0;
		while (!trouve && i < a.length()) {
			JSONObject element = a.getJSONObject(i);
			String user = element.getString("login");
			int s = element.getInt("sondage");
			if (user.equals(login) && s==sondage) {
				trouve = true;
			}
			i++;
		}
		return trouve;
	}
}
