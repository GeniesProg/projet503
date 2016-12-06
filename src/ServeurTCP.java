import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe correspondant Ã  un serveur TCP.
 * Le numÃ©ro de port du serveur est spÃ©cifiÃ© dans la classe ServeurTCP.
 * @author Cyril Rabat
 * @version 07/10/2013
 */
public class ServeurTCP {

    public static final int portEcoute = 5001;

    public static void main(String[] args) {
	// Création de la socket serveur
	ServerSocket socketServeur = null;
	try {	
	    socketServeur = new ServerSocket(portEcoute);
	    System.out.println("Serveur TCP lancé");
	} catch(IOException e) {
	    System.err.println("Création de la socket impossible : " + e);
	    System.exit(-1);
	}

	// Attente d'une connexion du LoginHandler
	Socket socketClient = null;
	try {
	    socketClient = socketServeur.accept();
	} catch(IOException e) {
	    System.err.println("Erreur lors de l'attente d'une connexion : " + e);
	    System.exit(-1);
	}

	// Association d'un flux d'entrée et de sortie
	BufferedReader input = null;
	PrintWriter output = null;
	try {
	    input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
	    output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream())), true);
	} catch(IOException e) {
	    System.err.println("Association des flux impossible : " + e);
	    System.exit(-1);
	}

	// Lecture du couple login mdp
	String message = "";
	try {
	    message = input.readLine();
	} catch(IOException e) {
	    System.err.println("Erreur lors de la lecture : " + e);
	    System.exit(-1);
	}
	
	//Décomposition de la chaine pour obtenir le couple login mdp
	String[] parts=message.split("&");
	String part1=parts[0];
	String part2=parts[1];
	String[] partsLog=part1.split("=");
	String[] partsMdp=part2.split("=");
	String log=partsLog[1];
	String mdp=partsMdp[1];
	
	//Comparaison du couple avec ceux contenus dans le fichier logs.json
	
	// Ouverture du fichier
		FileInputStream fs = null;
		try {
		    fs = new FileInputStream("F:/workspace/projet503/src/logs.json");
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier logs.json introuvable");
		    System.exit(-1);
		}
	 
		// Récupération de la chaîne JSON depuis le fichier
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();
	 
		//System.out.println(json);
		// Création d'un objet JSON
		JSONObject objet = new JSONObject(json);
	 
		// Affichage à l'écran
		JSONArray tableau = objet.getJSONArray("users");
		boolean validate=false;
			int k =0;
			while(!validate && k<tableau.length()){
			    JSONObject element = tableau.getJSONObject(k);
			    if(element.getString("login").equals(log) && element.getString("password").equals(mdp)){
			    	validate=true;
			    	message="<p>Connexion réussie</p>";
			    }
			    k++;
			}
	if(!validate) message="Mauvais couple login/password";
	// Envoi de la réponse selon la validité
	output.println(message);
	
	
	
	// Fermeture des flux et des sockets
	try {
	    input.close();
	    output.close();
	    socketClient.close();
	    socketServeur.close();
	} catch(IOException e) {
	    System.err.println("Erreur lors de la fermeture des flux et des sockets : " + e);
	    System.exit(-1);
	}
	
    }

}