package certificats;

import java.security.Signature;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * Classe permettant de signer un fichier avec une clé privée stockée dans
 * un fichier. La signature est sauvegardée dans un fichier.
 * @author Cyril Rabat
 * @version 19/10/2015
 */
public class SignatureFichier {

    /**
     * Méthode principale.
     * @param args[0] nom du fichier contenant la clé privée
     * @param args[1] nom du fichier à signer
     * @param args[2] nom du fichier dans lequel sauvegarder la signature
     */
    public static void signer(String keyFile, String signedFile, String saveFile) {

	// Reconstruction de la clé
	PrivateKey clePrivee = GestionClesRSA.lectureClePrivee(keyFile);

	// Création de la signature
	Signature signature = null;
	try {
	    signature = Signature.getInstance("SHA1withRSA");
	} catch(NoSuchAlgorithmException e) {
	    System.err.println("Erreur lors de l'initialisation de la signature : " + e);
	    System.exit(-1);
	}

	// Initialisation de la signature
	try { 
	    signature.initSign(clePrivee);
	} catch(InvalidKeyException e) {
	    System.err.println("Clé privée invalide : " + e);
	    System.exit(-1);
	}

	// Mise-à-jour de la signature par rapport au contenu du fichier
	try {
	    BufferedInputStream fichier = new BufferedInputStream(new FileInputStream(signedFile));
	    byte[] tampon = new byte[1024];
	    int n;
	    while (fichier.available() != 0) {
		n = fichier.read(tampon);
		signature.update(tampon, 0, n);
	    }
	    fichier.close();
	} catch(IOException e) {
	    System.err.println("Erreur lors de la lecture du fichier à signer : " + e);
	    System.exit(-1);
	}
	catch(SignatureException e) {
	    System.err.println("Erreur lors de la mise-à-jour de la signature : " + e);
	    System.exit(-1);
	}

	// Sauvegarde de la signature du fichier
	try {
	    FileOutputStream fichier = new FileOutputStream(saveFile);
	    fichier.write(signature.sign());
	    fichier.close();
	} catch(SignatureException e) {
	    System.err.println("Erreur lors de la récupération de la signature : " + e);
	    System.exit(-1);
	} catch(IOException e) {
	    System.err.println("Erreur lors de la sauvegarde de la signature : " + e);
	    System.exit(-1);
	}
    }

}