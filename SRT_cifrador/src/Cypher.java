import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import utils.Header;
import utils.Options;

public class Cypher {

	private int itCount;

	public Cypher() {

		this.itCount = 1024;
	}

	public void cipherFile(File file, String alg, String pw) throws Exception {

		// Create password
		char[] pass = pw.toCharArray();
		PBEKeySpec pbeKeySpec = new PBEKeySpec(pass);

		// Create byte salt
		SecureRandom random = new SecureRandom();
		byte[] saltBytes = new byte[8];
		random.nextBytes(saltBytes);

		// Create header
		Header header = new Header(Options.OP_SYMMETRIC_CIPHER, alg, "none", saltBytes);

		// Set options
		PBEParameterSpec pPS = new PBEParameterSpec(header.getData(), itCount);

		// Generate session key based on algorythm
		SecretKeyFactory kFactory = SecretKeyFactory.getInstance(header.getAlgorithm1());
		SecretKey sKey = kFactory.generateSecret(pbeKeySpec);

		// Set and initialize cypher using an algorythm, and also set the key and
		// parameters
		Cipher c = Cipher.getInstance(alg);
		c.init(Cipher.ENCRYPT_MODE, sKey, pPS);

		// Get streams to operate on the file
		OutputStream fileOUT = new FileOutputStream(file + ".cif"); // Add .cif sufix
		header.save(fileOUT);
		FileInputStream fileIN = new FileInputStream(file);
		CipherOutputStream cOut = new CipherOutputStream(fileOUT, c);

		// Cypher file
		int isEmpty; // If there are no more bits on the file, .read returns -1
		byte[] block = new byte[16];
		while ((isEmpty = fileIN.read(block)) != -1) {
			cOut.write(block, 0, isEmpty);
			// TODO BORRAR System.out.println("encrypted: " + Hex.toHexString(block));
		}
		cOut.close();
		fileIN.close();
		fileOUT.close();
	}

	public void decipherFile(File file, String pw) throws Exception {

		// Get the stream of the IN file
		FileInputStream fileIN = new FileInputStream(file);

		// Read header
		Header header = new Header();

		if (header.load(fileIN)) {
			
			// Create password
			char[] pass = pw.toCharArray();

			// Set options
			PBEKeySpec pbeKeySpec = new PBEKeySpec(pass);
			PBEParameterSpec pPS = new PBEParameterSpec(header.getData(), itCount);

			// Generate session key based on algorythm
			SecretKeyFactory kFactory = SecretKeyFactory.getInstance(header.getAlgorithm1());
			SecretKey sKey = kFactory.generateSecret(pbeKeySpec);

			// Set and initialize cypher using an algorythm, and also set the key and
			// parameters
			Cipher c = Cipher.getInstance(header.getAlgorithm1());
			c.init(Cipher.DECRYPT_MODE, sKey, pPS);

			// Get streams to operate on the file
			String name = file.getAbsolutePath();
			name = name.substring(0, name.length() - 4); // Eliminate .cif sufix
			FileOutputStream fileOUT = new FileOutputStream(name);
			CipherInputStream cIn = new CipherInputStream(fileIN, c);

			// De-cypher file
			int isEmpty; // If there are no more bits on the file, .read returns -1
			byte[] block = new byte[1024];
			while ((isEmpty = cIn.read(block)) != -1) {
				fileOUT.write(block, 0, isEmpty);
			}
			cIn.close();
			fileOUT.close();
		} else {
			throw new Exception("ERROR : File header could not be read, it cannot be deciphered");
		}

		fileIN.close();
	}
}
