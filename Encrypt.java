import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.util.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Method to encrypt a puzzle 
 * @author Matthew Betts
 */
public class Encrypt{
	static Cipher cipher;
	
	/**
	* A method that takes in a byte array and a secret key. It uses the secret key to encrypt the byte array 
	* and returns an encrypted string.
	* @param plainByte 	A byte array that is to be encrypted 
	* @param secretKey 	A SecretKey that will be used to encrypt the plainbyte 
	* @return			An encrypted string
	* @throws 			Exception
	*/
	public static String encrypt(byte[] plainByte, SecretKey secretKey) throws Exception {
		cipher = Cipher.getInstance("DES");
		//Initialise the cipher to be in encrypt mode, using the given key.
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		
		//Perform the encryption
		byte[] encryptedByte = cipher.doFinal(plainByte);
		
		//Get a new Base64 (ASCII) encoder and use it to convert ciphertext back to a string
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		//System.out.println(encryptedText);
		return encryptedText;
	}
}
