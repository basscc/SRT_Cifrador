package functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import utils.Header;
import utils.Options;

public class Hash {

	private int itCount;
	
	private int messageBytes; // Stores the number of bytes in the message
	private byte[] CalculatedHash; // Stores the Hash calculated from the message
	private byte[] StoredHash; // Stores the Hash from the header

	public Hash() {

		this.itCount = 1;
		this.messageBytes = -1;
	}
	
	public void HashFile(File file, String chosenHash, String pw) throws Exception {

		// Checking if type Hash or MAC
		// IF hash
		if (Options.isTypeAlgorithm(Options.hashAlgorithms, chosenHash)) {

			// Create the file streams
			FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
			
			// Generate output file
			String name = file.getAbsolutePath();
			name = name.substring(0, name.length() - 4);
			OutputStream outFile = new FileOutputStream(name + ".hsh");

			// Handling the password
			byte[] secret = pw.getBytes(StandardCharsets.UTF_8);

			//
			MessageDigest md = MessageDigest.getInstance(chosenHash);
			md.update(secret);

			//
			DigestInputStream inDigest = new DigestInputStream(inFile, md);

			//
			byte[] bloque = new byte[2048];
			int i = 0;
			while (i != -1) {
				i = inDigest.read(bloque);
			}
			md = inDigest.getMessageDigest();

			// TODO ??? Calculate the number of bytes in the message and store it to print it later
			messageBytes = 0;
			while (bloque[messageBytes] != '\0') {
				messageBytes++;
			}

			//
			byte[] resumen = md.digest();
			
			// Save the calculated hash code to print it later
			CalculatedHash = new byte[resumen.length];
			for (int h = 0; h<resumen.length; h++) {
				CalculatedHash[h] = resumen[h];
			}

			// Creating the header
			Header header = new Header(Options.OP_HASH_MAC, "none", chosenHash, resumen);
			header.save(outFile);

			inDigest.close();
			inFile.close();

			FileInputStream inFile2 = new FileInputStream(file.getAbsolutePath());
			int a = 0;
			while ((a = inFile2.read(bloque)) != -1) {
				outFile.write(bloque, 0, a);
			}

			inFile2.close();
			outFile.close();
			
		// IF MAC
		} else if (Options.isTypeAlgorithm(Options.macAlgorithms, chosenHash)) {

			// Create file streams
			FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
			OutputStream outFile = new FileOutputStream(file.getAbsolutePath() + ".mac");

			// Get the MAC algorithm instance 
			Mac mac = Mac.getInstance(chosenHash);
			
			// Get the user password for PBE
			char[] pass = pw.toCharArray();
			
			// Create byte salt for PBE
			SecureRandom random = new SecureRandom();
			byte[] saltBytes = new byte[8];
			random.nextBytes(saltBytes);

			// Generate session key
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec spec = new PBEKeySpec(pass, saltBytes, itCount, mac.getMacLength());
			SecretKey secretKey = skf.generateSecret(spec);
			
			// Initiate MAC algorithm with session key
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
			FileInputStream inFile2 = new FileInputStream(file.getAbsolutePath());
			int c = 0;
			while ((c = inFile2.read(block)) != -1) {
				outFile.write(block, 0, c);
			}

			//
			inFile.close();
			inFile2.close();
			outFile.close();
		} else {
			throw new Exception("ERROR : Hashing algorithm not recognised, the operation has failed");
		}
	}

	public void verify(File file, String pw) throws Exception {

		// Create the file streams
		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());

		// Creating the header
		Header header = new Header();
		header.load(inFile);
		
		// Checking if type Hash or MAC
		// IF hash
		if (Options.isTypeAlgorithm(Options.hashAlgorithms, header.getAlgorithm2())) {

			MessageDigest md = MessageDigest.getInstance(header.getAlgorithm2());
			
			byte[] pass = pw.getBytes(StandardCharsets.UTF_8);
			md.update(pass);

			DigestInputStream dis = new DigestInputStream(inFile, md);

			// Get the file content
			byte[] bloque = new byte[2048];
			int i = 0;
			while (i != -1) {
				i = dis.read(bloque);
			}
			
			// Calculate the number of bytes in the message and store it to print it later
			messageBytes = 0;
	        while (bloque[messageBytes] != '\0') {  	
	        	messageBytes++;
	        }

			// Get the message ressume
			md = dis.getMessageDigest();
			byte[] resumen = md.digest();

			// Save the calculated hash code to print later
			CalculatedHash = new byte[resumen.length];
			for (int h = 0; h<resumen.length; h++) {
				CalculatedHash[h] = resumen[h];
			}
			
			// Get the ressume in the header to print later
			StoredHash = header.getData();

			// Check if the ressume is equal to the header
			if (MessageDigest.isEqual(resumen, header.getData())) {
				
				// Get the streams
				String name = file.getAbsolutePath();
				name = name.substring(0, name.length() - 4);
				OutputStream outFile = new FileOutputStream(name + ".cla"); // remove extension and apply the new one
				
				outFile.write(bloque, 0, messageBytes);

				outFile.close();
				System.out.println("El archivo ha sido verificado correctamente.");

			} else {
				System.out.println("El archivo NO ha sido verificado. Inténtelo de nuevo.");
			}

			dis.close();
			inFile.close();
		
		// IF MAC
		} else if (Options.isTypeAlgorithm(Options.macAlgorithms, header.getAlgorithm2())) {
			
			// Get the MAC algorithm instance 
			Mac mac = Mac.getInstance(header.getAlgorithm2());
			
			// Get the user password for PBE
			char[] passwd = pw.toCharArray();
			
			// Create byte salt for PBE
			SecureRandom random = new SecureRandom();
			byte[] saltBytes = new byte[8];
			random.nextBytes(saltBytes);

			// Generate session key
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			PBEKeySpec spec = new PBEKeySpec(passwd, saltBytes, itCount, mac.getMacLength());
			SecretKey secretKey = skf.generateSecret(spec);
			
			// Initiate MAC algorithm with session key
			mac.init(secretKey);

			// A continuación procesaríamos la información a resumir, leyendo del stream de
			// entrada y pasando los datos al algoritmo MAC
			// mediante el método 'update()' .
			// Cuando se llegue al final del mismo, para recuperar calcular y recuperar el
			// código mac se invocaría el método 'doFinal()':
			byte[] block = new byte[1024];
			while (inFile.read(block) != -1) {
				mac.update(block);
			}

			// Calcular y recuperar el cï¿½digo mac
			byte[] macCode = mac.doFinal();

			if (MessageDigest.isEqual(macCode, header.getData())) {
				String name = file.getAbsolutePath();
				name = name.substring(0, name.length() - 4);

				// Fichero de salida
				OutputStream outFile = new FileOutputStream(name + ".cla");

				// Fichero de entrada
				FileInputStream inFich3 = new FileInputStream(file.getAbsolutePath());

				// Escritura en el fichero de salida
				int b;

				while ((b = inFich3.read(block)) != -1) {
					outFile.write(block, 0, b);
				}

				// Cierre de flujos
				inFich3.close();
				outFile.close();
				System.out.println("El archivo ha sido verificado correctamente.");
			} else {
				System.out.println("El archivo NO ha podido ser verificado. Inténtelo de nuevo.");
			}
			// Cierre del fichero de entrada
			inFile.close();
		} else {
			throw new Exception("ERROR : Hashing algorithm not recognised, the operation has failed");
		}
	}
	
	public int getMessageBytes() {
		return messageBytes;
	}
	
	public byte[] getCalculatedHash() {
		return CalculatedHash;
	}
	
	public byte[] getStoredHash() {
		return StoredHash;
	}
}
