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

public class Decrypt {
	
	static Cipher cipher;
	
	public static byte[] decrypt(byte[] ciphertext, int attempts) throws Exception {
		CryptoLib crypto = new CryptoLib();
		
		cipher = Cipher.getInstance("DES");
		
		byte[] combinedKey = new byte[8];
		byte[] possibleKeyEnd = new byte[6];
		byte[] bytedAttempts = new byte[2];
		new SecureRandom().nextBytes(bytedAttempts);
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(bytedAttempts); //2 bytes
			outputStream.write(possibleKeyEnd); //6 bytes
			combinedKey = outputStream.toByteArray();
		} catch (Exception e) { System.err.println("Caught Exception: " + e.getMessage()); }
		
		SecretKey key = crypto.createKey(combinedKey);
		
		try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(ciphertext);
			
			byte[] emptyArray = new byte[15];
			byte[] arr1 = Arrays.copyOfRange(decrypted, 0, 15);
			if(crypto.byteArrayToString(arr1).equals(crypto.byteArrayToString(emptyArray))){
				return decrypted;
			}
            return null;
        } catch (Exception e) {
        	//System.err.println("Caught Exception: " + e.getMessage());
        }
        return null;
	}
}