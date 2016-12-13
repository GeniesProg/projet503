package certificats;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

/**
 * Classe correspondant à un client UDP.
 * La chaine de caractères "Bonjour" est envoyée au serveur.
 * Le port d'écoute du serveur est indiqué dans la classe ServeurUDP.
 * @author Cyril Rabat
 * @version 07/10/2013
 */
public class ClientUDP {
	
    public static void main(String[] args) {
    String privateKeyFile="privee"+ClientUDP.class.getSimpleName()+".bin";
    String publiqueKeyFile="publique"+ClientUDP.class.getSimpleName()+".bin";
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
	    encodedKey+=ClientUDP.class.getSimpleName();
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