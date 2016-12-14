package utilisateurs;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import certificats.CryptTools;
import certificats.GenerationClesRSA;
import certificats.GestionClesRSA;
import certificats.ServeurUDP;

public class ServeurTCP {

    public static final int portEcoute = 5001;

    public static void main(String[] args) {
	// Création de la socket serveur
    ServeurTCP.demandeCertificat();
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
		if (message.split("_")[0].equals("0")) {
			//Décomposition de la chaine pour obtenir le couple login mdp
			String part1=message.split("&")[0];
			String part2=message.split("&")[1];
			String log=part1.split("=")[1];
			String mdp=part2.split("=")[1];
						
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
			System.out.println(message);
			String rep = message.split("_")[1];
	
			JSONObject object = new JSONObject(rep);
			JSONArray a = object.getJSONArray("liste");
			String []r = new String[a.length()];
			for (int i = 0; i< a.length() ; i++) {
				JSONObject o = a.getJSONObject(i);
				r[i] = o.getString(String.valueOf(i+1));
			}
			
			try {
				g.ajouterDonnee(object.getString("login"), object.getInt("sondage"), r);
			} catch (RemoteException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				System.out.println(g.getNbDonnees());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			socketConnexion.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socketConnexion.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    public static void demandeCertificat() {
    	String privateKeyFile="clefs/privee"+ServeurTCP.class.getSimpleName()+".bin";
        String publiqueKeyFile="clefs/publique"+ServeurTCP.class.getSimpleName()+".bin";
    	GenerationClesRSA.generateKeys(privateKeyFile, publiqueKeyFile);
        DatagramSocket socket = null;
    	// Création de la socket
    	try {
    	    socket = new DatagramSocket();
    	} catch(SocketException e) {
    	    System.err.println("Erreur lors de la création de la socket : " + e);
    	    System.exit(-1);
    	}
    	// Création du message
    	DatagramPacket msg = null;
    	String encodedKey="";
    	try {
    	    InetAddress adresse = InetAddress.getByName(null);
    	    
    	    //recup�ration de la cl� publique du client et conversion en string
    	    PublicKey publicKey=GestionClesRSA.lectureClePublique(publiqueKeyFile);
    	    encodedKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
    	    encodedKey+="~~~";
    	    encodedKey+=ServeurTCP.class.getSimpleName();
    	    byte[] tampon = encodedKey.getBytes();
    	    msg = new DatagramPacket(tampon,
    				     tampon.length,
    				     adresse,
    				     ServeurUDP.portEcoute);
    	    
    	} catch(UnknownHostException e) {
    	    System.err.println("Erreur lors de la création du message : " + e);
    	    System.exit(-1);
    	}

    	// Envoi du message
    	try {
    	    socket.send(msg);
    	    //System.out.println(encodedKey+" envoy� vers le serveur (encodedKey)");
    	} catch(IOException e) {
    	    System.err.println("Erreur lors de l'envoi du message : " + e);
    	    System.exit(-1);
    	}
    	
    	// Création du message
    		byte[] b = new byte[1024];
    		DatagramPacket reponse = new DatagramPacket(b, b.length);
    		String encryptedMsg="";
    		// Lecture du message du serveur
    		try {
    		    socket.receive(reponse);
    		    encryptedMsg = new String(reponse.getData(), 0, reponse.getLength());
    		    //System.out.println(encryptedMsg+"   Reponse du serveur (texte)");
    		} catch(IOException e) {
    		    System.err.println("Erreur lors de la réception du message : " + e);
    		    System.exit(-1);
    		}
    		
    		String[] encryptedParts = encryptedMsg.split("~~~");
    		String encryptedRandomString = encryptedParts[0];
    		String encryptedPublicKeyServer = encryptedParts[1];
    		
    		PrivateKey privateKey=GestionClesRSA.lectureClePrivee(privateKeyFile);
    		String decodedRandomString="";
    		//System.out.println("Chaine encod�e "+encryptedRandomString);
    		try {
    			decodedRandomString=CryptTools.decrypt(privateKey, encryptedRandomString);
    		} catch (InvalidKeyException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NoSuchAlgorithmException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NoSuchPaddingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IllegalBlockSizeException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (UnsupportedEncodingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (BadPaddingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		byte[] keyBytes=null;
    		try {
    			keyBytes = Base64.getDecoder().decode(encryptedPublicKeyServer.getBytes("utf-8"));
    		} catch (UnsupportedEncodingException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    		KeyFactory keyFactory=null;
    		try {
    			keyFactory = KeyFactory.getInstance("RSA");
    		} catch (NoSuchAlgorithmException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		PublicKey publicKeyServer=null;
    		try {
    			publicKeyServer = keyFactory.generatePublic(spec);
    		} catch (InvalidKeySpecException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		String encodedRandomString="";
    		try {
    			encodedRandomString=CryptTools.encrypt(publicKeyServer, decodedRandomString);
    		} catch (InvalidKeyException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NoSuchAlgorithmException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NoSuchPaddingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IllegalBlockSizeException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (UnsupportedEncodingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (BadPaddingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

    		try{
    		InetAddress adresse = InetAddress.getByName(null);
    		 byte[] tampon = encodedRandomString.getBytes();
    		    msg = new DatagramPacket(tampon,
    					     tampon.length,
    					     adresse,
    					     ServeurUDP.portEcoute);
    		    
    		} catch(UnknownHostException e) {
    		    System.err.println("Erreur lors de la création du message : " + e);
    		    System.exit(-1);
    		}

    		// Envoi du message
    		try {
    		    socket.send(msg);
    		    //System.out.println(encodedRandomString+" envoy� vers le serveur (encodedRandomString)");
    		} catch(IOException e) {
    		    System.err.println("Erreur lors de l'envoi du message : " + e);
    		    System.exit(-1);
    		}
    		
    		// Lecture du certificat
    		DatagramPacket certifRecu = null;
    		try {
    		    byte[] tamp = new byte[1024];
    		    certifRecu = new DatagramPacket(tamp, tamp.length);
    		    socket.receive(certifRecu);
    		} catch(IOException e) {
    		    System.err.println("Erreur lors de la r�ception du message : " + e);
    		    System.exit(-1);
    		}
    	 
    		// R�cup�ration de l'objet
    		try {
    		    ByteArrayInputStream bais = new ByteArrayInputStream(certifRecu.getData());
    		    ObjectInputStream ois = new ObjectInputStream(bais);
    		    File f = (File) ois.readObject();
    	 
    		    System.out.println("Certificat recu : "+f );
    		} catch(ClassNotFoundException e) {
    		    System.err.println("Objet re�u non reconnu : " + e);
    		    System.exit(-1);
    		} catch(IOException e) {
    		    System.err.println("Erreur lors de la r�cup�ration de l'objet : " + e);
    		    System.exit(-1);
    		}    		
    	// Fermeture de la socket
    	socket.close();        
    }
}