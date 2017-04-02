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
 * Methods to crack a puzzle either with brute force or with a key 
 * @author Matthew Betts
 */
public class Decrypt {
	
	static Cipher cipher;
	
	/**
	* Takes a puzzle as a byte array, creates a key and then tries to open the puzzle. The return 
	* type will be null if the key is the wrong size, or the start of the decrypted puzzle does not 
	* equal 16 bytes of 0's. If it is the right size it will return a decrypted puzzle in the form 
	* of a byte array.
	* @param ciphertext A that is to be cracked 
	* @return			A byte array or null based on the key being tried
	* @throws 			Exception
	*/
	public static byte[] cracking(byte[] ciphertext) throws Exception {
		CryptoLib crypto = new CryptoLib();
		
		cipher = Cipher.getInstance("DES");
		//Fix the final key to 8 bytes 
		byte[] combinedKey = new byte[8];
		
		//Makes the first 2 bytes of the key random data 
		byte[] bytedAttempts = new byte[2];
		new SecureRandom().nextBytes(bytedAttempts);
		
		//The last 6 are all 0's (default byte array is all 0's)
		byte[] keyEnd = new byte[6];
		
		//Append the byte arrays together
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(bytedAttempts); //2 bytes
			outputStream.write(keyEnd); //6 bytes
			combinedKey = outputStream.toByteArray();
		} catch (Exception e) { System.err.println("Caught Exception: " + e.getMessage()); }
		
		//Pass the byte array to be turned into a Secret Key 
		SecretKey key = crypto.createKey(combinedKey);
		
		//Try to open the puzzle with the current key, if the key is wrong it will return null
		try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(ciphertext);
			byte[] emptyArray = new byte[15]; //Creates an empty byte array 
			byte[] arr1 = Arrays.copyOfRange(decrypted, 0, 15);
			
			/*	
			*	Compares the start of the "decrypted" puzzle with an empty byte array, if the 
			*	start of the puzzle is equal to the empty array (both should be all 0's if the  	
			*	decryption is correct) then it will return the decrypted puzzle.
			*/ 
			if(crypto.byteArrayToString(arr1).equals(crypto.byteArrayToString(emptyArray))){
				return decrypted;
			}
            return null;
        } catch (Exception e) { }
        return null;
	}
	
	/**
	* A method that opens a encrypted message using a secret key and returns the decrypted message.
	* @param encyptedMsg A encrypted message that is to be cracked 
	* @param SecretKey 	A secret key that will open the message
	* @return			A decrypted message string
	* @throws 			Exception
	*/
	public String decrypting(String encyptedMsg, SecretKey key) throws Exception{
		CryptoLib crypto = new CryptoLib();
		cipher = Cipher.getInstance("DES");
		byte[] bytedEncyptedMsg = crypto.stringToByteArray(encyptedMsg);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedKey = cipher.doFinal(bytedEncyptedMsg);
		String decryptedMsg = new String(decryptedKey);
		return decryptedMsg;
	} 
}