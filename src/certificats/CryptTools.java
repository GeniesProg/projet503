package certificats;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CryptTools {
	public static String encrypt(PublicKey key, String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA");   
        cipher.init(Cipher.ENCRYPT_MODE, key);  
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF-8")));
    }
	
	public static String decrypt(PrivateKey privateKey, String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA");   
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        return new String(cipher.doFinal(Base64.getDecoder().decode((text.getBytes("UTF-8")))));
    }
}
