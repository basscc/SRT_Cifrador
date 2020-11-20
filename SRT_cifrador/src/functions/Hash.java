package functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import utils.Header;
import utils.Options;

public class Hash {
	
	private int itCount;
	private byte[] salt; 
	
	public Hash() {
		
		this.itCount = 1;
		this.salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae }; //TODO VER SI HAY QUE GENERARLO O ES ESTATICO)
	}
	
	public void HashFile (File file, String chosenHash, String pw) throws Exception {
		
		// Checking if type Hash or MAC
		if (Options.isTypeAlgorithm(Options.hashAlgorithms, chosenHash)) {
			
			// Create the file streams
			FileInputStream inFile = new FileInputStream(file.getAbsolutePath());

			String name = file.getAbsolutePath();
			name = name.substring(0, name.length()-4);
			OutputStream outFile = new FileOutputStream(name + ".hsh");
			
			// Handling the password
			byte[] secret = pw.getBytes(StandardCharsets.UTF_8);
			
			//
			MessageDigest md = MessageDigest.getInstance(chosenHash);
			md.update(secret);
			
			//
			DigestInputStream inDigest = new DigestInputStream(inFile, md);
			
			//
			byte[] bloque = new byte[1024];
			int i = 0;
			while (i != -1) {
				i = inDigest.read(bloque);
			}
			md = inDigest.getMessageDigest();
			
			// 
			byte[] resumen = md.digest();
			
			// Creating the header
			Header header = new Header(Options.OP_HASH_MAC, "none", chosenHash, resumen);
			header.save(outFile);
			
			//
			int a = 0;
			while ((a = inFile.read(bloque)) != -1) {
				outFile.write(bloque, 0, a);
			}
			
			// 
			inDigest.close();
			inFile.close();
			outFile.close();
		}
		else if (Options.isTypeAlgorithm(Options.macAlgorithms, chosenHash)) {
			
			// Create file streams
			FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
			OutputStream outFile = new FileOutputStream(file.getAbsolutePath() + ".mac");
			
			// 
			Mac mac = Mac.getInstance(chosenHash);
			
			char[] pass = pw.toCharArray();
			
			//
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec spec = new PBEKeySpec(pass, salt, itCount, mac.getMacLength());
			SecretKey secretKey = skf.generateSecret(spec);
			
			mac.init(secretKey);
			
			// 
			byte[] block = new byte[1024];
			while (inFile.read(block) != -1) {
				mac.update(block);
			}

			// 
			byte[] macCode = mac.doFinal();

			// Creating the header
			Header head = new Header(Options.OP_HASH_MAC, "none", chosenHash, macCode);
			head.save(outFile);

			//
			int c = 0;
			while ((c = inFile.read(block)) != -1) {
				outFile.write(block, 0, c);
			}

			// 
			inFile.close();
			outFile.close();
		}
		else {
			throw new Exception("ERROR : Hashing algorythm not recognised, the operation has failed");
		}
	}

}
