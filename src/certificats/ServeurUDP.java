package certificats;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.json.JSONObject;

public class ServeurUDP {

    public static int portEcoute = 2025;
    public static String name = "Autorit� de certification";
    
    
    public static void main(String[] args) {
    GenerationClesRSA.generateKeys("priveeAutorite.bin", "publiqueAutorite.bin");
    // Création de la socket
	DatagramSocket socket = null;
	try {	    
	    socket = new DatagramSocket(portEcoute);
	} catch(SocketException e) {
	    System.err.println("Erreur lors de la création de la socket : " + e);
	    System.exit(-1);
	}

	// Création du message
	byte[] tampon = new byte[1024];
	DatagramPacket msg = new DatagramPacket(tampon, tampon.length);

	// Lecture du message du client
	String randomString="";
	String texte="";
	String className="";
	String encryptedPublicKey="";
	try {
	    socket.receive(msg);
	    texte = new String(msg.getData(), 0, msg.getLength());
	    //System.out.println(texte+" recu depuis le client (encodedKey)");
	    
	    String[] encryptedParts = texte.split("~~~");
		encryptedPublicKey = encryptedParts[0];
		className = encryptedParts[1];
	    
	    //restauration de la cl� publique du client
	    byte[] decodedKey = Base64.getDecoder().decode(encryptedPublicKey);
	    X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(decodedKey);
        KeyFactory kf=null;
		try {
			kf = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PublicKey publicKeyClient=null;
	    try {
			publicKeyClient = kf.generatePublic(X509publicKey);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
	    randomString = UUID.randomUUID().toString().replaceAll("-", "");
	    //System.out.println(randomString+" gener� par le serveur (randomString)");
	    String encodedRandomString="";
	    byte[] encodedRandomStringByte=null;
	    String encodedKeyServer="";
	    try {
			encodedRandomString=CryptTools.encrypt(publicKeyClient, randomString);
			//System.out.println(encodedRandomString+" gener� par le serveur (encodedRandomString)");
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
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    PublicKey publicKeyServer=GestionClesRSA.lectureClePublique("publiqueAutorite.bin");
	    encodedKeyServer = Base64.getEncoder().encodeToString(publicKeyServer.getEncoded());
	    
	    encodedRandomString=encodedRandomString+"~~~"+encodedKeyServer;
		encodedRandomStringByte = encodedRandomString.getBytes();
		DatagramPacket msgRandomString = new DatagramPacket(
					encodedRandomStringByte,
					encodedRandomString.length(),
					msg.getAddress(),
				    msg.getPort());
	    try {
		    socket.send(msgRandomString);
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'envoi du message (msgRandomString): " + e);
		    System.exit(-1);
		}
	    
	} catch(IOException e) {
	    System.err.println("Erreur lors de la réception du message : " + e);
	    System.exit(-1);
	}

	byte[] tampon2 = new byte[1024];
	DatagramPacket msg2 = new DatagramPacket(tampon2, tampon2.length);
	String texte2="";
	try {
	    socket.receive(msg2);
	    texte2 = new String(msg2.getData(), 0, msg2.getLength());
	    //System.out.println(texte2+" recu depuis le client (encodedRandomString)");
	    
	}catch(IOException e) {
	    System.err.println("Erreur lors de la réception du message : " + e);
	    System.exit(-1);
	}
	
	PrivateKey privateKeyServer=GestionClesRSA.lectureClePrivee("priveeAutorite.bin");
	String decodedRandomStringClient="";
	try {
		decodedRandomStringClient=CryptTools.decrypt(privateKeyServer, texte2);
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
	
	if(decodedRandomStringClient.equals(randomString)){
		System.out.println("Challenge r�ussi !");
		
		Certificat c=new Certificat(ServeurUDP.name, msg.getAddress().getHostAddress(), msg.getPort(),className,encryptedPublicKey, "1/1/2017");
		JSONObject objet = new JSONObject();
		JSONObject test = c.tojson();
		objet.put("certificats", test);
		
		// Cr�ation du fichier de sortie
		FileWriter fs = null;
		String jsonFile="certificat"+className+".json";
		try {
		    fs = new FileWriter(jsonFile);
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'ouverture du fichier certificat.json");
		    System.err.println(e);
		    System.exit(-1);
		}
	 
		// Sauvegarde dans le fichier
		try {
		    objet.write(fs);
		    fs.flush();
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'�criture dans le fichier : " + e);
		    System.exit(-1);
		}
		
		SignatureFichier.signer("priveeAutorite.bin", jsonFile, "signatures"+className+".bin");
		
		File f=new File(jsonFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(f);
		} catch(IOException e) {
		    System.err.println("Erreur lors de la s�rialisation : " + e);
		    System.exit(-1);
		}
	 
		// Cr�ation et envoi du segment UDP
		try {
		    byte[] donneesCertif = baos.toByteArray();
		    DatagramPacket certif = new DatagramPacket(
		    			donneesCertif,
		    			donneesCertif.length,
		    			msg.getAddress(),
						msg.getPort());
		    socket.send(certif);
		    System.out.println("Certificat envoy� au client");
		} catch(UnknownHostException e) {
		    System.err.println("Erreur lors de la cr�ation de l'adresse : " + e);
		    System.exit(-1); 
		} catch(IOException e) {
		    System.err.println("Erreur lors de l'envoi du message : " + e);
		    System.exit(-1);
		}
		
		
		
	}else System.out.println("Challenge rat� !");
	
	// Fermeture de la socket

	    socket.close();
    }

}