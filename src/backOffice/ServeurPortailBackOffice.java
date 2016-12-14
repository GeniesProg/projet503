package backOffice;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;

import com.sun.net.httpserver.HttpServer;

import certificats.CryptTools;
import certificats.GenerationClesRSA;
import certificats.GestionClesRSA;
import certificats.ServeurUDP;
import rmi.ServeurRMI;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONException;

public class ServeurPortailBackOffice {

    public static void main(String[] args) {
    	
    	ServeurPortailBackOffice.demandeCertificat();
    	System.out.println("passage");
        HttpServer serveur = null;
        try {
            serveur = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch(IOException e) {
            System.err.println("Erreur lors de la création du serveur " + e);
            System.exit(-1);
        }

        serveur.createContext("/authentication.html", new LoginHandler());
        serveur.createContext("/index.html", new IndexHandler());
        serveur.createContext("/admin.html", new AdminHandler());
        serveur.createContext("/user.html", new UserHandler());
        serveur.createContext("/sondage.html", new SondageHandler());
        serveur.createContext("/submit.html", new SubmitHandler());
        serveur.createContext("/resultats.html", new ResultatsTotaux());
        serveur.createContext("/activation.html", new ActivationHandler());
        serveur.createContext("/gestionActivation.html", new GestionActivation());
        serveur.createContext("/creation.html", new CreationHandler());
        serveur.createContext("/gestionCreation.html", new GestionCreation());
        serveur.setExecutor(null);
        serveur.start();
        
	System.out.println("Serveur portail b-o démarré.");	
    }
    
    public static void demandeCertificat() {
    	String privateKeyFile="clefs/privee"+ServeurPortailBackOffice.class.getSimpleName()+".bin";
        String publiqueKeyFile="clefs/publique"+ServeurPortailBackOffice.class.getSimpleName()+".bin";
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
    	    encodedKey+=ServeurPortailBackOffice.class.getSimpleName();
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
