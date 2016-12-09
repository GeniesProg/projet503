package utilisateurs;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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
	//------------------------------------------------------------------------------------------
	
	//Décomposition de la chaine pour obtenir le couple login mdp
	String part1=message.split("&")[0];
	String part2=message.split("&")[1];
	String log=part1.split("=")[1];
	String mdp=part2.split("=")[1];
	
	
	String reponse = Utilisateur.authentification(log, mdp);
	// Envoi de la réponse selon la validité
	output.println(reponse);	
	
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