package utilisateurs;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServeurTCP {

    public static final int portEcoute = 5001;

    public static void main(String[] args) {
	// Création de la socket serveur
	ServerSocket socketServeur = null;
	try {	
	    socketServeur = new ServerSocket(portEcoute);
	    System.out.println("Serveur TCP lancé");
	} catch(IOException e) {
	    System.err.println("Création de la socket impossible (coté serveur): " + e);
	    System.exit(-1);
	}

	// Attente d'une connexion du LoginHandler
	while(true) {
	Socket socketConnexion = null;
	
	try {
	    socketConnexion = socketServeur.accept();
	} catch(IOException e) {
	    System.err.println("Erreur lors de l'attente d'une connexion : " + e);
	    System.exit(-1);
	}

	// Association d'un flux d'entrée et de sortie
	BufferedReader inputConnexion = null;
	PrintWriter outputConnexion = null;
	
	try {
	    inputConnexion = new BufferedReader(new InputStreamReader(socketConnexion.getInputStream()));
	    outputConnexion = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketConnexion.getOutputStream())), true);
	} catch(IOException e) {
	    System.err.println("Association des flux impossible : " + e);
	    System.exit(-1);
	}

	// Lecture du couple login mdp
	String message = "";
	try {
	    message = inputConnexion.readLine();
	} catch(IOException e) {
	    System.err.println("Erreur lors de la lecture : " + e);
	    System.exit(-1);
	}
	//------------------------------------------------------------------------------------------
	if (message.split("_")[0].equals("0")) {
		//Décomposition de la chaine pour obtenir le couple login mdp
		String part1=message.split("&")[0];
		String part2=message.split("&")[1];
		String log=part1.split("=")[1];
		String mdp=part2.split("=")[1];
					
		IGestionnaireDistant g = null;

		try {
		    g = (IGestionnaireDistant)Naming.lookup("rmi://localhost/utilisateurs");
		} catch(NotBoundException e) {
		    System.err.println("Pas possible d'accéder à l'objet distant : " + e);
		    System.exit(-1);
		} catch(MalformedURLException e) {
		    System.err.println("URL mal forme : " + e);
		    System.exit(-1);
		} catch(RemoteException e) {
		    System.err.println("Pas possible d'accéder à l'objet distant : " + e);
		    System.exit(-1);
		}
		String reponse = "def";
		try {
			reponse = g.authentification(log, mdp);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Envoi de la réponse selon la validité
		outputConnexion.println(reponse);
	} else {
		ArrayList<Utilisateur> users = new ArrayList<>();
		FileInputStream fs = null;
		try {
		    fs = new FileInputStream("utilisateurs.json");
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier '" + "utilisateurs.json" + "' introuvable");
		    System.exit(-1);
		}
	 
		// Récupération de la chaîne JSON depuis le fichier
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();
		
		JSONObject objet = new JSONObject(json);		
		
		JSONArray tableau = objet.getJSONArray("utilisateurs");
		
	}
	// Fermeture des flux et des sockets
	/*try {
	    inputConnexion.close();
	    outputConnexion.close();
	    socketConnexion.close();
	    socketServeur.close();
	} catch(IOException e) {
	    System.err.println("Erreur lors de la fermeture des flux et des sockets : " + e);
	    System.exit(-1);
	}*/
	}
	
    }
}