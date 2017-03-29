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

public class puzzleGenerator {
	private CryptoLib crypto = new CryptoLib();
	private Encrypt encryptor = new Encrypt();
	
	public void createPuzzles(int puzzleNum){
		if(puzzleNum == 1){
			System.out.println("Creating those super hard puzzles...");
		}
		if(puzzleNum == 1024){
			System.out.println("Puzzle creation complete.");
		}
		//Creates byte arry (16 byte) of all 0's for the start of the puzzle
		byte[] puzzleStart = new byte[16];
		
		//Byte array to store the unique puzzle number
		byte[] uniqueNum = new byte[2];
		uniqueNum = crypto.smallIntToByteArray(puzzleNum);
		
		//Creates a 8 byte length byte array and fills it with ('secure') random data    
		byte[] key = new byte[8];
		new SecureRandom().nextBytes(key);
		
		SecretKey puzzleKey = null;
		try {
			puzzleKey = crypto.createKey(key);
		} catch (NoSuchAlgorithmException e) { System.err.println("Caught NoSuchAlgorithmException: " + e.getMessage());
		} catch (InvalidKeySpecException e) { System.err.println("Caught InvalidKeySpecException: " + e.getMessage());
		} catch (InvalidKeyException e) { System.err.println("Caught InvalidKeyException: " + e.getMessage()); }		
		
		
		byte[] bytedKey = new byte[8];
		bytedKey = puzzleKey.getEncoded();
		
		//concatenates 2 (or more) byte arrays 
		byte[] puzzle = new byte[26];
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(puzzleStart); //16 byte
			outputStream.write(uniqueNum); //2 byte
			outputStream.write(bytedKey); //8 byte
			puzzle = outputStream.toByteArray();
		} catch (Exception e) { System.err.println("Caught Exception: " + e.getMessage()); }

		//Create the byte array to be turned into the key to encrypt the puzzle
		//2 bytes of random data 
		byte[] keyStart = new byte[2];
		new SecureRandom().nextBytes(keyStart);
		
		//6 bytes of 0's
		byte[] keyEnd = new byte[6];
		
		//Concat them together
		byte[] fullKey = new byte[8];
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(keyStart);
			outputStream.write(keyEnd);
			fullKey = outputStream.toByteArray();
		} catch (Exception e) { System.err.println("Caught Exception: " + e.getMessage()); }
	
		//Creates DESKey to encrypt the puzzle from the above byte array 
		SecretKey puzzleEncryptKey = null;
		try {
			puzzleEncryptKey = crypto.createKey(fullKey);
		} catch (NoSuchAlgorithmException e) { System.err.println("Caught NoSuchAlgorithmException: " + e.getMessage());
		} catch (InvalidKeySpecException e) { System.err.println("Caught InvalidKeySpecException: " + e.getMessage());
		} catch (InvalidKeyException e) { System.err.println("Caught InvalidKeyException: " + e.getMessage()); }		
		
		String encryptedText = "";
		try {
			encryptedText = encryptor.encrypt(puzzle, puzzleEncryptKey);
		} catch (Exception e){ System.err.println("Caught Exception: " + e.getMessage()); }
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter("puzzles.txt", true));
			out.println(encryptedText);
			out.close();
		} catch (IOException e) {
			System.err.println("Caught Exception: " + e.getMessage());
		}
	}
}