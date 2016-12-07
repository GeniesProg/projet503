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
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Classe correspondant à un serveur TCP.
 * Le client envoie la chaine 'Bonjour' et lit une réponse de la part du serveur.
 * Le client envoie ensuite la chaine 'Au revoir' et lit une réponse.
 * Le numéro de port du serveur est spécifié dans la classe ServeurTCP.
 * @author Cyril Rabat
 * @version 07/10/2013
 */
public class ServeurTCP {

    public static final int portEcoute = 5001;

    public static void main(String[] args) {
	// Cr�ation de la socket serveur
	ServerSocket socketServeur = null;
	try {	
	    socketServeur = new ServerSocket(portEcoute);
	    System.out.println("Serveur TCP lanc�");
	} catch(IOException e) {
	    System.err.println("Cr�ation de la socket impossible : " + e);
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

	// Association d'un flux d'entr�e et de sortie
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
	
	//D�composition de la chaine pour obtenir le couple login mdp
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
		    fs = new FileInputStream("logs.json");
		} catch(FileNotFoundException e) {
		    System.err.println("Fichier logs.json introuvable");
		    System.exit(-1);
		}
	 
		// R�cup�ration de la cha�ne JSON depuis le fichier
		String json = new String();
		Scanner scanner = new Scanner(fs);
		while(scanner.hasNext())
		    json += scanner.nextLine();
		scanner.close();
	 
		//System.out.println(json);
		// Cr�ation d'un objet JSON
		JSONObject objet = new JSONObject(json);
	 
		// Affichage � l'�cran
		JSONArray tableau = objet.getJSONArray("users");
		boolean validate=false;
			int k =0;
			while(!validate && k<tableau.length()){
			    JSONObject element = tableau.getJSONObject(k);
			    if(element.getString("login").equals(log) && element.getString("password").equals(mdp)){
			    	validate=true;
			    	message="<p>Connexion réussie</p>";
			    	//message+="<script>document.location.href='pageAdmin.html'</script>";
			    	message+="<meta http-equiv=\"refresh\" content=\"3; url=http://localhost:8080/admin.html\">";
			    }
			    k++;
			}
	if(!validate) message="Mauvais couple login/password";
	// Envoi de la r�ponse selon la validit�
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