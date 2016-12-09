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
	private String fichier = "utilisateurs.json";
	private ArrayList<Utilisateur> utilisateurs = new ArrayList<>();	
	
	public void ajouter(String login, String mdp, int type){
		utilisateurs.add(new Utilisateur(login, mdp, type));
		sauvegarder();
	}
	
	public void supprimer(String login){
		for (int i = 0 ; i < utilisateurs.size() ; i++) {
			if (login.equals(utilisateurs.get(i).getLogin())) {
				utilisateurs.remove(i);
			}
		}
		sauvegarder();
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
	}
	
	public String authentification(String login, String mdp) {
		this.charger();
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
		    		message+="<meta http-equiv=\"refresh\" content=\"1; url=http://localhost:8080/admin.html\">";	
		    	} else if (type == 1) {
		    		message+="<meta http-equiv=\"refresh\" content=\"1; url=http://localhost:8080/user.html\">";
		    	}
		    	
		    }
		    k++;
		}
		if(!validate) message="Mauvais couple login/password";
		
		return message;
	}

	@Override
	public String getFichier() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFichier(String fichier) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean exists(String login) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
}
