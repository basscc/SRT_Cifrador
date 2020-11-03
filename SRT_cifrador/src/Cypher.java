import java.io.ByteArrayInputStream;
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
	// TODO Borrar "PBEWithMD5AndDES"; "PBEWithMD5AndDES";

	/*
	 * TODO Borrar esto
	 * 
	 * Hay que modularizar esto, una clase para encriptar y otra para desencriptar
	 * 
	 * Quiza se puede utilizar una init para las variables previas (salt?)
	 * (iteraciones?)
	 * 
	 * Mover ese Spam de try/catch
	 * 
	 */
	
	private static int itCount;

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
		Header header = new Header(Options.OP_SYMMETRIC_CIPHER, alg, "", saltBytes);

		// Set options
		PBEParameterSpec pPS = new PBEParameterSpec(header.getData(), itCount);

		// Generate session key based on algorythm
		SecretKeyFactory kFactory = SecretKeyFactory.getInstance(header.getAlgorithm1());
		SecretKey sKey = kFactory.generateSecret(pbeKeySpec);

		// Set and initialize cypher using an algorythm, and also set the key and parameters 
		Cipher c = Cipher.getInstance(alg);
		c.init(Cipher.ENCRYPT_MODE, sKey, pPS);

		// Get streams to operate on the file
		OutputStream fileOUT = new FileOutputStream(file + ".cif"); // Add .cif sufix
		header.save(fileOUT);
		FileInputStream ficheroEntrada = new FileInputStream(file);
		CipherOutputStream cOut = new CipherOutputStream(fileOUT, c);

		// Cypher file
		int isEmpty; // If there are no more bits on the file, .read returns -1
		byte[] block = new byte[16];
		while ((isEmpty = ficheroEntrada.read(block)) != -1) {
			cOut.write(block, 0, isEmpty);
			// TODO BORRAR System.out.println("encrypted: " + Hex.toHexString(block));
		}
		cOut.close();
		ficheroEntrada.close();
		fileOUT.close();
	}

	public static void decipherFile() throws Exception {
		//esto no sirve para absolutamente nada porque luego en el inputstream
		// te da outofbounds si dejas solo "f" y si pones "f.getName" te dice que 
		// no encuentra el fichero, asi que he metido el fichero en las carpetas del proyecto
		// y ahi si lo encuentra 
		File f = new File("C:\\Users\\javipiti\\Desktop\\pruebaaa.txt.cif");
		String pw = "asd";
		System.out.println(f.getName());

		// Get the stream of the IN file
		InputStream ficheroEntrada = new FileInputStream(f.getName());

		//aqui me daba error y esto lo he sacado de lo de estas tias, no se muy bien
		// que hace
		ByteArrayInputStream stream = new ByteArrayInputStream(f.getName().getBytes());
		// Read header
		Header header = new Header();
		header.load(stream);

		// Create password
		char[] pass = pw.toCharArray();

		
		// Set options
		PBEKeySpec pbeKeySpec = new PBEKeySpec(pass);
		PBEParameterSpec pPS = new PBEParameterSpec(header.getData(), itCount);
		
		// y aqui me daba error lo del algoritmo y ahi me he quedado 
		// Generate session key based on algorythm
		SecretKeyFactory kFactory = SecretKeyFactory.getInstance(header.getAlgorithm1());
		SecretKey sKey = kFactory.generateSecret(pbeKeySpec);

		// Set and initialize cypher using an algorythm, and also set the key and parameters 
		Cipher c = Cipher.getInstance(header.getAlgorithm1());
		c.init(Cipher.ENCRYPT_MODE, sKey, pPS);

		// Get streams to operate on the file
		String nombre = f.getAbsolutePath();
		nombre = nombre.substring(0, nombre.length() - 4); // Eliminate .cif sufix
		FileOutputStream ficheroSalida = new FileOutputStream(nombre);
		CipherInputStream cIn = new CipherInputStream(ficheroEntrada, c);

		// De-cypher file	
		int isEmpty; // If there are no more bits on the file, .read returns -1
		byte[] bloque = new byte[1024];
		while ((isEmpty = cIn.read(bloque)) != -1) {
			ficheroSalida.write(bloque, 0, isEmpty);
		}
		cIn.close();
		ficheroEntrada.close();
		ficheroSalida.close();
	}
	public static void main(String[] args) throws Exception {
		
		decipherFile();
	}
}
