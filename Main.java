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
import java.util.Scanner;

/**
 * A class that contains the main method and a method to randomly select a puzzle.
 * @author Matthew Betts
 */
public class Main {
	
	/**
   * This is the main method that calls the relevant methods to create, select, crack and then show key 
   * encryption/decryption. For a more detailed description as to how the brute force works see the cracking
   * method in the decrypt class.
   * @param args Unused.
   */
	public static void main (String[] args) throws Exception{
		
		//Creates objects so we can reference the other files methods
		CryptoLib crypto = new CryptoLib();
		puzzleGenerator generator = new puzzleGenerator();
		Decrypt decryptor = new Decrypt();
		Encrypt encryptor = new Encrypt();
		
		//Removes puzzles.txt
		try { 
			new File("puzzles.txt").delete();
		} catch(Exception e) { System.out.print("Error. It's possible the file doesn't exist yet."); }
		
		//Creates 1024 puzzles
		int n = 0;
		while(n <= 1024){
			generator.createPuzzles(n);
			n++;
		}
		//Selects a puzzle from those that were just made
		String puzzleToCrack = selectPuzzle();
		System.out.println("Puzzle to crack: " + puzzleToCrack);
		byte[] ciphertext = crypto.stringToByteArray(puzzleToCrack);
		System.out.println("Key cracking beginning..");
		System.out.println("----------------------------------");
		
		byte[] decrypted = null;
		boolean solved = false;
		int attempts = 0;
		
		//Attempts to brute force the key 
		while(!solved){
			attempts++;
			try {
				decrypted = decryptor.cracking(ciphertext);
				if(decrypted != null){
					solved = true;
				}
			} catch (Exception e){ System.err.println("Caught Exception: " + e.getMessage()); }
		}
		//Prints out the number of attempts and the decrypted puzzle in string format
		System.out.println("Key Cracked. " + attempts + " attempts.");
		System.out.print("Full (decrypted) Puzzle = ");
		System.out.print(crypto.byteArrayToString(decrypted));
		System.out.println("");
		
		//We then spilt the puzzle number up to get the key and the number of the puzzle
		byte[] finalPuzzleNum = Arrays.copyOfRange(decrypted, 16, 18);
		System.out.println("Puzzle Number = " + crypto.byteArrayToSmallInt(finalPuzzleNum));
		byte[] finalBytedKey = Arrays.copyOfRange(decrypted, 18, 26);
		SecretKey finalKey = crypto.createKey(finalBytedKey);
		System.out.println("Key = " + finalKey);
		
		//Ask for the users input 
		Scanner sc = new Scanner(System.in);
		System.out.println("----------------------------------");
		System.out.print("Enter the message to be sent: ");
		String messageToBeSent = sc.nextLine();
		
		byte[] bytedMessage = messageToBeSent.getBytes();
		
		//Encrypt the users message using the key gained from the cracked puzzle 
		String finalEncryptedMsg = encryptor.encrypt(bytedMessage,finalKey);
		System.out.println("Final Encrypted Msg: " + finalEncryptedMsg);
		
		//Decrypt it now using the key 
		String finalDecryptedMsg = decryptor.decrypting(finalEncryptedMsg,finalKey);
		System.out.println("Final Decrypted Msg: " + finalDecryptedMsg);
	}
	
	/**
	* A method that randomly selects a puzzle from the puzzles.txt file
	* @return		A puzzle as a string 
	*/
	public static String selectPuzzle(){
		System.out.println("Carefully selecting a puzzle...");
		ArrayList<String> puzzleList = new ArrayList<String>();
		Random rand = new Random();

		//Loads all puzzles from puzzles.txt into an array list 
        try(BufferedReader br = new BufferedReader(new FileReader("puzzles.txt"))) {
    		for(String line; (line = br.readLine()) != null; ) {
        		puzzleList.add(line);
		    }
		} catch(IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
		
		//Selects a puzzle who is in a index equal to the randomly generated number
		String output = puzzleList.get(rand.nextInt(puzzleList.size() - 1) + 1);
		return output;
	}
}
