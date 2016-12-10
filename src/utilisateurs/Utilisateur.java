package utilisateurs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utilisateur {

	private static String fichier = "utilisateurs.json";
	private String login;
	private String password;
	private int type;
	private ArrayList<String> reponses;
	
	public Utilisateur(String login, String password, int type) {
		this.login = login;
		this.password = password;
		this.type = type;
	}	

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public static String authentification(String login, String mdp) {
		//this.charger();
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
		    		message+="<meta http-equiv=\"refresh\" content=\"2; url=http://localhost:8080/admin.html\">";	
		    	} else if (type == 1) {
		    		message+="<meta http-equiv=\"refresh\" content=\"2; url=http://localhost:8080/user.html\">";
		    	}
		    	message += "<form id=\"troll\"action=\"http://localhost:8080/user.html\" method=\"post\">"
		        		+"<input type=\"text\" style=\"display:none\" name=\"login\" value=\""+ login + "\">"
		        		 //+ "<input type=\"submit\" value=\"Submit\">"
		        				+ "</form>";
		    	message += "<script>document.getElementById(\"troll\").submit();</script>";
		    }
		    k++;
		}
		if(!validate) message="Mauvais couple login/password";
		
		return message;
	}
}
