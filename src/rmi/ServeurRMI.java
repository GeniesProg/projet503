package rmi;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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
import rmi.GestionnaireSondages;
import utilisateurs.GestionnaireDistant;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServeurRMI {

    public static void main(String[] args) throws RemoteException, JSONException {
    
    	ServeurRMI.demandeCertificat();
    	/*try {
          LocateRegistry.createRegistry(1099);
		} catch(RemoteException e) {
		    System.err.println("Erreur lors de la recuperation du registry : " + e);
            System.exit(-1);
        }*/
    	
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
    	    int numSondage = sondage.getInt("id");
    	    int active = sondage.getInt("active");
    	    JSONArray questions = sondage.getJSONArray("questions");
    	    ArrayList<Question> tabQuestions = new ArrayList<>();
    	    for (int j = 0; j < questions.length() ; j++) {
    	    	JSONObject question = questions.getJSONObject(j);
    	    	Question q = new Question(question.getInt("numero"), question.getString("intitule"), numSondage);
    	    	JSONArray reponses = question.getJSONArray("reponses");
    	    	ArrayList<Reponse> tabReponses = new ArrayList<>();
    	    	for (int k = 0; k < reponses.length(); k++){
    	    		JSONObject reponse = reponses.getJSONObject(k);
    	    		Reponse r = new Reponse(reponse.getString("lettre"), reponse.getString("libelle"), q.getNumero(), numSondage);
    	    		tabReponses.add(r);
    	    	}
    	    	q.setReponses(tabReponses);
    	    	tabQuestions.add(q);
    	    }
    	    
    	    Sondage s = new Sondage(sondage.getInt("id"), sondage.getString("titre"), tabQuestions, active);
    	    try {
				Naming.rebind("sondage"+s.getId(), s);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	    sondages.add((ISondage)s);
    	      	
    	
		try {	   
			GestionnaireSondages as = new GestionnaireSondages(sondages);
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
    	
	
    System.out.println("Serveur RMI lancé, sondages créés");	
    }
    
    public static void demandeCertificat() {
    	String privateKeyFile="clefs/privee"+ServeurRMI.class.getSimpleName()+".bin";
        String publiqueKeyFile="clefs/publique"+ServeurRMI.class.getSimpleName()+".bin";
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
    	    encodedKey+=ServeurRMI.class.getSimpleName();
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