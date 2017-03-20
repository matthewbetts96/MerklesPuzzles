import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.util.*;
import java.io.*;
import java.security.SecureRandom;

public class Test {
	public static void main (String[] args){

		//Creates byte arry (16 byte) of all 0's
		byte[] puzzleStart = new byte[16];
		System.out.println("puzzleStart = " + puzzleStart);
		
		//Creates a 8 byte length byte array and fills it with ('secure') random data    
		byte[] key = new byte[8];
		new SecureRandom().nextBytes(key);
		System.out.println("key (string) = " + byteArrayToString(key));

		//concatenates 2 (or more) byte arrays 
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(puzzleStart);
			outputStream.write(key);
			byte[] c = outputStream.toByteArray();

			System.out.println("concatarray = " + byteArrayToString(c));


			//These return the original inputs by splitting the byte array 
			byte[] arr2 = Arrays.copyOfRange(c, 16, 32);
	   		System.out.println("key (after copy) = " + byteArrayToString(arr2));

			byte[] arr3 = Arrays.copyOfRange(c, 0, 15);
	   		System.out.println("puzzlestart (after copy) = " + byteArrayToString(arr3));
		} catch (Exception e) {
			  System.err.println("Caught IOException: " + e.getMessage());
		}

		/*
			try {
				System.out.println(createKey(key));
			} catch (InvalidKeySpecException e) {
			    System.err.println("Caught IOException: " + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
			    System.err.println("Caught IOException: " + e.getMessage());
			} catch (InvalidKeyException e) {
			    System.err.println("Caught IOException: " + e.getMessage());
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			    System.err.println("Caught IOException: " + e.getMessage());
			}*/
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


