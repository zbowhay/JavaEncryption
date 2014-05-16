import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ZCrypt {
	public static void main(String args[]){
		//When everything is functional create a series of Scanner(System.in)'s to prompt the user for the neccesary parameters.
		// also ask them if they are running the program to encrypt a document or decrypt a document.
		String p = "C:\\Users\\Zac\\Documents\\toBeEncrypted.txt";
		Scanner input = new Scanner(System.in);
		System.out.println("Please type in the path to the .txt file you wish to have encrypted.");
		System.out.println("Use the following format: C:\\Users\\Zac\\Documents\\toBeEncrypted.txt");
		String filePath = input.next();
		System.out.println(encrypt(filePath,5));
		System.out.println();
		System.out.println("-------DECRYPTING PORTION-------");
		System.out.println();
		System.out.println(decrypt(filePath,"13175"));
		input.close();
		// RANGE FOR ASCII CONVERSION NEEDS TO BE BETWEEN 0 and 127 OTHERWISE IT GLITCHES AND RETURNS 8's ON DECRYPT
		
	}
	/**
	 * encrypt will access the text file at path and encrypt it using a randomly generated key of numbers based on keyLength
	 * @param path path to text file
	 * @param keyLength desired length of randomly generated key.
	 * @return WILL BECOME VOID, RETURN IS ONLY HERE FOR TESTING PURPOSES--------
	 */
	public static ArrayList<String> encrypt(String path, int keyLength){
		// READ FILE AND PUT SEPERATE LINES INTO ArrayList<String> text
		ArrayList<String> text = new ArrayList<String>();
		try{
			Scanner read = new Scanner(new File(path));
			while(read.hasNext()){
				text.add(read.nextLine());
			}
			
		}catch (FileNotFoundException e){
			System.out.println("file not found.");
		}
		System.out.println("File Contents: " + text);
		
		// ---------BEGIN ENCRYPTING----------
		
		//String KEY = generateKey(keyLength);
		String KEY = "13175";
		System.out.println("KEY: " + KEY);// MIGHT WANT TO KEEP THIS HERE OR MOVE IT----------
		runEncryption(text, KEY);
		
		// -----------REWRITE TO FILE------------
		
		try{
			File file = new File(path);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int x = 0; x < text.size(); x++){
				bw.write(text.get(x));
				bw.newLine();
			}
			bw.close();
		}catch (IOException e){
			System.out.println("Error: File could not be written to.");
		}
		
		
		return text;
	}
	
	
	public static ArrayList<String> decrypt(String path, String key){
		// READ FILE AND PUT SEPERATE LINES INTO ArrayList<String> text
			ArrayList<String> text = new ArrayList<String>();
			try{
				Scanner read = new Scanner(new File(path));
				while(read.hasNext()){
					text.add(read.nextLine());
				}
				
			}catch (FileNotFoundException e){
				System.out.println("file not found.");
			}
			System.out.println("File Contents: " + text);
			
			// --------BEGIN DECRYPTING---------
			
			System.out.println("Decrypting with key: " + key);
			runDecryption(text,key);
			
			// -----------REWRITE TO FILE------------
			
			try{
				File file = new File(path);
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				for(int x = 0; x < text.size(); x++){
					bw.write(text.get(x));
					bw.newLine();
				}
				bw.close();
			}catch (IOException e){
				System.out.println("Error: File could not be written to.");
			}
			
			
			return text;
	}
	
	/**
	 * generateKey will generate a random key with the desired number of digits.
	 * @param digits determines how many digits will be in the key.
	 * @return the randomly generated key.
	 */
	public static String generateKey(int digits){
		String sKey = "";
		Random num = new Random();
		for(int a = 0; a < digits; a++){
			sKey += num.nextInt(10);
		}
		return sKey;
	}
	
	
	public static void runDecryption(ArrayList<String> contents, String key){
		//--------GENERATE ASCII VALUES----------
				ArrayList<Integer> ascii = new ArrayList<Integer>();
				for(int a = 0; a < contents.size(); a++){
					// runs through each element in CONTENTS
					for(int b = 0; b < contents.get(a).length(); b++){
						//runs through each char in the current CONTENTS element and casts it to an int
						ascii.add((int)contents.get(a).charAt(b));
					}
					ascii.add(888);
					// add the number 888 after each sList element has been processed so that later when the ascii is converted back
					// into Strings there will be a reference number for line breaks.  if statement will be necessary prior to adding key values
					// that checks for 888 and skips that element if it is there so that the key doesn't change its value.
				}
				System.out.println("Original: " + ascii);// DELETE ME-----------------------------
				
				//---------CONVERTS ASCII VALUES USING KEY----------
				int placeHolder = 0; // used to cycle through key.
				// DEBUG if document is to large: the for-loop may need to be run with LONGS and not INTS
				for(int c = 0; c < ascii.size(); c++){
					if(ascii.get(c) == 888){
						// skips all 888 values
					}else{
						// change ascii values in accordance with KEY
						if(placeHolder == key.length()){// used to reset re-cycle through the key
							placeHolder = 0;
							int temp = ascii.get(c) - Integer.parseInt(key.substring(placeHolder, placeHolder + 1));
							// keeps all ascii values between 0-127
							if(temp < 0){
								temp = fixUnderage(temp , 0);
							}
							ascii.set(c, temp);
						}else{
							int temp2 = ascii.get(c) - Integer.parseInt(key.substring(placeHolder, placeHolder + 1));
							// keeps all ascii values between 0-127
							if(temp2 < 0){
								temp2 = fixUnderage(temp2 , 0);
							}
							ascii.set(c, temp2);
						}
						placeHolder++;
					}
				}
				System.out.println("converted: " + ascii);// DELETE ME------------------------------------ 
				
				// EMPTY Contents
				for(int d = contents.size() - 1; d >= 0; d--){
					contents.remove(d);
				}
				
				// ---------REFILL sList WITH DECRYPTED TEXT------------
				String segment = "";
				for(int e = 0; e < ascii.size(); e++){
					if(ascii.get(e) == 888){
						contents.add(segment);
						segment = "";
					}else{
						segment += Character.toString((char)ascii.get(e).intValue());
					}
				}
				System.out.println("Decrypted : " + contents);
	}
	
	/**
	 * runEncryption takes sList and converts each character into an ascii value. Then the ascii values are changed in accordance 
	 * with the key.  Finally, the ADJUSTED ascii values are then converted back into encrypted text.
	 * @param sList ArrayList<String> that will be encrypted
	 * @param key A string of numbers used as an encryption key. Ex: "34294"
	 */
	public static void runEncryption(ArrayList<String> sList, String key){		
		//--------GENERATE ASCII VALUES----------
		ArrayList<Integer> ascii = new ArrayList<Integer>();
		for(int a = 0; a < sList.size(); a++){
			// runs through each element in sList
			for(int b = 0; b < sList.get(a).length(); b++){
				//runs through each char in the current sList element and casts it to an int
				ascii.add((int)sList.get(a).charAt(b));
			}
			ascii.add(888);
			// add the number 888 after each sList element has been processed so that later when the ascii is converted back
			// into Strings there will be a reference number for line breaks.  if statement will be necessary prior to adding key values
			// that checks for 888 and skips that element if it is there so that the key doesn't change its value.
		}
		System.out.println("Original: " + ascii);// DELETE ME-----------------------------
		
		//---------CONVERTS ASCII VALUES USING KEY----------
		int placeHolder = 0; // used to cycle through key.
		// DEBUG if document is to large: the for-loop may need to be run with LONGS and not INTS
		for(int c = 0; c < ascii.size(); c++){
			if(ascii.get(c) == 888){
				// skips all 888 values
			}else{
				// change ascii values in accordance with KEY
				if(placeHolder == key.length()){// used to reset re-cycle through the key
					placeHolder = 0;
					int temp = ascii.get(c) + Integer.parseInt(key.substring(placeHolder, placeHolder + 1));
					// keeps all ascii values between 0-127
					if(temp > 127){
						temp = fixOverage(temp , 127);
					}
					ascii.set(c, temp);
				}else{
					int temp2 = ascii.get(c) + Integer.parseInt(key.substring(placeHolder, placeHolder + 1));
					// keeps all ascii values between 0-127
					if(temp2 > 127){
						temp2 = fixOverage(temp2 , 127);
					}
					ascii.set(c, temp2);
				}
				placeHolder++;
			}
		}
		System.out.println("converted: " + ascii);// DELETE ME------------------------------------ 
		
		// EMPTY sList
		for(int d = sList.size() - 1; d >= 0; d--){
			sList.remove(d);
		}
		
		// ---------REFILL sList WITH ENCRYPTED TEXT------------
		String segment = "";
		for(int e = 0; e < ascii.size(); e++){
			if(ascii.get(e) == 888){
				sList.add(segment);
				segment = "";
			}else{
				segment += Character.toString((char)ascii.get(e).intValue());
			}
		}
		System.out.println("Encrypted : " + sList);
		
	}
	
	
	
	
	/**
	 * fixOverage will be used in an if-statement to take all numbers in the ascii ArrayList<Integer> that are over 255 and reset them using the difference between (over and limit) + -1. 
	 * @param over int that is over 255 that needs to be reset.
	 * @param limit int that represents the limit that over isnt suppose to be above.
	 * @return the reset int.
	 */
	public static int fixOverage(int over, int limit){
		int difference = over - limit;
		return -1 + difference;
	}
	
	public static int fixUnderage(int under, int limit){
		int difference = limit - under;
		return 128 - difference;
	}
}
