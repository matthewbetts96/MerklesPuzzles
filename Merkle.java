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
	/*		keep this for now, will be useful later
			System.out.println("concatarray = " + byteArrayToString(c));
			//These return the original inputs by splitting the byte array 
			byte[] arr2 = Arrays.copyOfRange(c, 18, 26);
	   		System.out.println("DESkey (after copy) = " + byteArrayToString(arr2));

			byte[] arr3 = Arrays.copyOfRange(c, 0, 15);
	   		System.out.println("puzzlestart (after copy) = " + byteArrayToString(arr3));*/

public class Merkle {

	static Cipher cipher;

	public static void main (String[] args){
		try { 
			new File("puzzles.txt").delete();
		} catch(Exception e) { System.out.print("Error. It's possible the file doesn't exist yet."); }
		
		int n = 1;
		while(n <= 1024){
			createPuzzles(n);
			n++;
		}

		boolean solved = false;
		int attempts = 0;
		
		String puzzleToCrack = selectPuzzle();
		System.out.println("Puzzle to crack: " + puzzleToCrack);
		byte[] ciphertext = stringToByteArray(puzzleToCrack);
		System.out.print("Key cracking beginning..");
		
		byte[] decrypted = null;
		while(!solved){
			attempts++;
			try {
				decrypted = decrypt(ciphertext, attempts);
				if(decrypted != null){
					solved = true;
				}
			} catch (Exception e){ System.err.println("Caught Exception: " + e.getMessage()); }

		}
		System.out.println("Key Cracked. " + attempts + " attempts.");
		System.out.println(byteArrayToString(decrypted));

		byte[] arr1 = Arrays.copyOfRange(decrypted, 0, 25);
	   	System.out.println("Should be all A's = " + byteArrayToString(arr1));
		byte[] arr2 = Arrays.copyOfRange(decrypted, 16, 18);
	   	System.out.println("2 digit num = " + byteArrayToSmallInt(arr2));
		byte[] arr3 = Arrays.copyOfRange(decrypted, 19, 25);
	   	System.out.println("random key = " + byteArrayToString(arr3));
	}

	public static byte[] decrypt(byte[] ciphertext, int attempts) throws Exception {
		cipher = Cipher.getInstance("DES");
		byte[] combinedKey = new byte[8];
		byte[] possibleKeyEnd = new byte[6];
		byte[] bytedAttempts = (smallIntToByteArray(attempts));
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(bytedAttempts); //2 bytes
			outputStream.write(possibleKeyEnd); //6 bytes
			combinedKey = outputStream.toByteArray();
		} catch (Exception e) { System.err.println("Caught Exception: " + e.getMessage()); }

		SecretKey key = generateDESKey(combinedKey);
		try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(ciphertext);
            return decrypted;
        } catch (Exception e) {
        	//System.err.println("Caught Exception: " + e.getMessage());
        }
        return null;
	}


	//Collects all puzzles and returns the string of the chosen puzzle
	public static String selectPuzzle(){
		System.out.println("Carefully selecting a puzzle...");
		ArrayList<String> puzzleList = new ArrayList<String>();
		Random rand = new Random();

        try(BufferedReader br = new BufferedReader(new FileReader("puzzles.txt"))) {
    		for(String line; (line = br.readLine()) != null; ) {
        		puzzleList.add(line);
		    }
		} catch(IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}

		String output = puzzleList.get(rand.nextInt(puzzleList.size() - 1) + 1);
		return output;
	}
	
	public static void createPuzzles(int puzzleNum){
		if(puzzleNum == 1){
			System.out.println("Creating those super hard puzzles...");
		}
		if(puzzleNum == 1024){
			System.out.println("Finally done!");
		}
		//Creates byte arry (16 byte) of all 0's for the start of the puzzle
		byte[] puzzleStart = new byte[16];
		
		//Byte array to store the unique puzzle number
		byte[] uniqueNum = new byte[2];
		uniqueNum = smallIntToByteArray(puzzleNum);
		
		//Creates a 8 byte length byte array and fills it with ('secure') random data    
		byte[] key = new byte[8];
		new SecureRandom().nextBytes(key);
			
		SecretKey puzzleKey = generateDESKey(key);
		
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
		SecretKey puzzleEncryptKey = generateDESKey(fullKey);
		
		String encryptedText = "";
		try {
			encryptedText = encrypt(puzzle, puzzleEncryptKey);
		} catch (Exception e){ System.err.println("Caught Exception: " + e.getMessage()); }
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter("puzzles.txt", true));
			out.println(encryptedText);
			out.close();
		} catch (IOException e) {
			System.err.println("Caught Exception: " + e.getMessage());
		}
	}
		
	//Creates DESKey 
	//Had to put it in it's own method as calling the createKey method required 
	//the 3 catch blocks and were taking up lots of space
	public static SecretKey generateDESKey(byte[] key){
		SecretKey DESKey = null;
		try {
			DESKey = createKey(key);
		} catch (NoSuchAlgorithmException e) {
			  System.err.println("Caught NoSuchAlgorithmException: " + e.getMessage());
		} catch (InvalidKeySpecException e) {
			  System.err.println("Caught InvalidKeySpecException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			  System.err.println("Caught InvalidKeyException: " + e.getMessage());
		}		
		return DESKey;
	}
	
	//Note: This method is not my own and is taken from the Lab 3 AES work (with a few changes)
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

/**
 * A collection of useful methods for CSF207's Coursework 2.
 * @author Phillip James
 */
	/**
	 * Create a new DES key from a given byte array. 
	 * You should use this whenever you need to create a DES key from a particular array of bytes.
	 * @param keyData An array of bytes to be used as a key. This is expected to be an array of 8 bytes.
	 * @return A DES key computed from the given byte array.
	 */
	public static SecretKey createKey(byte[] keyData) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException
	{
		if(keyData.length != 8){
			throw new IllegalArgumentException("Incorrect Array length expecting 64-bits / 8 bytes.");
		}
		else{
			SecretKeyFactory sf = SecretKeyFactory.getInstance("DES");
			DESKeySpec keySpec = new DESKeySpec(keyData);
			return sf.generateSecret(keySpec);
		}
	}
	
	
	/*
	 * Convert a small integer (between 0 and 65535 inclusively) into an array of bytes of length 2.
	 * @param i The integer to convert.
	 * @return An array of bytes of length 2 representing the given integer in bytes (big endian).
	 */
	public static byte[] smallIntToByteArray(int i){
		if(i >= 65536){
			throw new IllegalArgumentException("Integer too large, expected range 0-65535.");
		}
		else{
			byte[] bytesOfNumber = ByteBuffer.allocate(4).putInt(i).array();
			return Arrays.copyOfRange(bytesOfNumber,2,4);
		}
	}
	
	/*
	 * Convert an array of bytes of length 2 into a small integer (between 0 and 65535 inclusively).
	 * @param bytes A byte array of length 2 (big endian). 
	 * @returns The computed integer from the array of bytes. 
	 */
	public static int byteArrayToSmallInt(byte[] bytes){
		byte[] number = new byte[4];
		number[2] = bytes[0];
		number[3] = bytes[1];
						
		ByteBuffer bb = ByteBuffer.wrap(number);
		return bb.getInt();
	}
	
	
	/*
	 * Convert an array of bytes into a string representation
	 * (using the Base64 binary-to-text encoding scheme - See Wikipedia).
	 * @param bytes An array of bytes to be converted into a string. 
	 * @returns A string representation of the given bytes.
	 */
	public static String byteArrayToString(byte[] bytes){
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(bytes);
	}
	
	/*
	 * Convert a string (in Base64 binary-to-text
	 * encoding scheme - See Wikipedia) into an array of bytes.
	 * @param s A string to be converted into an array of bytes. 
	 * @returns An array of bytes representing the given string.
	 */	
	public static byte[] stringToByteArray(String s){
		Base64.Decoder decoder  = Base64.getDecoder();
		return decoder.decode(s);
	}
}



