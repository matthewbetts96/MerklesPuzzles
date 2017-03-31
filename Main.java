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

public class Main {
	public static void main (String[] args) throws Exception{
		CryptoLib crypto = new CryptoLib();
		puzzleGenerator generator = new puzzleGenerator();
		Decrypt decryptor = new Decrypt();
		Encrypt encryptor = new Encrypt();
		
		try { 
			new File("puzzles.txt").delete();
		} catch(Exception e) { System.out.print("Error. It's possible the file doesn't exist yet."); }
		int n = 0;
		while(n <= 1024){
			generator.createPuzzles(n);
			n++;
		}
		
		String puzzleToCrack = selectPuzzle();
		System.out.println("Puzzle to crack: " + puzzleToCrack);
		byte[] ciphertext = crypto.stringToByteArray(puzzleToCrack);
		System.out.println("Key cracking beginning..");
		
		byte[] decrypted = null;
		boolean solved = false;
		int attempts = 0;
		while(!solved){
			attempts++;
			try {
				decrypted = decryptor.cracking(ciphertext);
				if(decrypted != null){
					solved = true;
				}
			} catch (Exception e){ System.err.println("Caught Exception: " + e.getMessage()); }
		}
		System.out.println("Key Cracked. " + attempts + " attempts.");
		System.out.print("Full (decrypted) Puzzle = ");
		System.out.print(crypto.byteArrayToString(decrypted));
		System.out.println("");
		
		byte[] finalPuzzleNum = Arrays.copyOfRange(decrypted, 16, 18);
		System.out.println("Puzzle Number = " + crypto.byteArrayToSmallInt(finalPuzzleNum));
		byte[] finalBytedKey = Arrays.copyOfRange(decrypted, 18, 26);
		SecretKey finalKey = crypto.createKey(finalBytedKey);
		System.out.println("Key = " + finalKey);
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter the message to be sent: ");
		String messageToBeSent = sc.nextLine();
		
		//System.out.println(crypto.byteArrayToSmallInt(crypto.stringToByteArray(messageToBeSent)));
		byte[] bytedMessage = messageToBeSent.getBytes();
		
		String finalEncryptedMsg = encryptor.encrypt(bytedMessage,finalKey);
		System.out.println("Final Encrypted Msg: " + finalEncryptedMsg);
		
		String finalDecryptedMsg = decryptor.decrypting(finalEncryptedMsg,finalKey);
		System.out.println("Final Decrypted Msg: " + finalDecryptedMsg);
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
}
