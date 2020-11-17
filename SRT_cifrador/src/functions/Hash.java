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
		this.salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae }; // TODO VER SI
																										// HAY QUE
																										// GENERARLO O
																										// ES ESTATICO)
	}

	public void HashFile(File file, String chosenHash, String pw) throws Exception {

		// Checking if type Hash or MAC
		if (Options.isTypeAlgorithm(Options.hashAlgorithms, chosenHash)) {

			// Create the file streams
			FileInputStream inFile = new FileInputStream(file.getAbsolutePath());

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
		} else if (Options.isTypeAlgorithm(Options.macAlgorithms, chosenHash)) {

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
		} else {
			throw new Exception("ERROR : Hashing algorythm not recognised, the operation has failed");
		}
	}

	public void verify(File file, String pw) throws Exception {

		//System.out.println(file.getAbsolutePath());
		// Create the file streams
		//FileInputStream inFile = new FileInputStream(file.getAbsolutePath());

		
		/*
		// Header
		Header header = new Header();
		header.load(inFile);
		
		// Checking if type Hash or MAC
		if (Options.isTypeAlgorithm(Options.hashAlgorithms, header.getAlgorithm2())) {
			
			MessageDigest md = MessageDigest.getInstance(header.getAlgorithm2());
			
			byte[] pass = pw.getBytes(StandardCharsets.UTF_8);
			md.update(pass);
			
			DigestInputStream dis = new DigestInputStream(inFile, md);
			
			// Bucle que va obteniendo los bloques del fichero que queramos verificar
			byte[] bloque = new byte[1024];
			int i = 0;
			while (i != -1) {
				i = dis.read(bloque);
			}

			// Obtencion del resumen completo
			md = dis.getMessageDigest();
			byte[] resumen = md.digest();

			// Comprobar si los resumenes son iguales para verificar
			if (MessageDigest.isEqual(resumen, header.getData())) {
				String nombre = file.getAbsolutePath();
				nombre = nombre.substring(0, nombre.length() - 4);

				// Fichero de salida
				OutputStream salidaFichero = new FileOutputStream(nombre + ".cla");

				// Fichero de entrada
				FileInputStream inFich2 = new FileInputStream(file.getAbsolutePath());

				// Escritura en el fichero de salida
				int z = 0;
				while ((z = inFich2.read(bloque)) != -1) {
					salidaFichero.write(bloque, 0, z);
				}

				// Cierre de flujos
				inFich2.close();
				salidaFichero.close();
				System.out.println("El archivo ha sido verificado correctamente.");

			} else {
				System.out.println("El archivo NO ha sido verificado. Inténtelo de nuevo.");
			}


			// Cierre de flujos
			dis.close();
			inFile.close();
		}else {
			
			if (Options.isTypeAlgorithm(Options.macAlgorithms, header.getAlgorithm2())) {
				// Mac: Esta clase representa al algoritmo mac, y se encarga de realizar el
				// trabajo. Permite seleccionar el algoritmo mac a utilizar
				Mac mac = Mac.getInstance(header.getAlgorithm2());

				char[] passwd = pw.toCharArray();
				int counter = 1;

				// Creación de una clave secreta a partir de la password del usuario (usando el
				// estándar PBKDF2) e inicio del algoritmo MAC:
				SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				PBEKeySpec spec = new PBEKeySpec(passwd, salt, counter, mac.getMacLength());
				SecretKey secretKey = skf.generateSecret(spec);
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
					String nombre = file.getAbsolutePath();
					nombre = nombre.substring(0, nombre.length() - 4);

					// Fichero de salida
					OutputStream salidaFichero = new FileOutputStream(nombre + ".cla");

					// Fichero de entrada
					FileInputStream inFich3 = new FileInputStream(file.getAbsolutePath());

					// Escritura en el fichero de salida
					int b;

					while ((b = inFich3.read(block)) != -1) {
						salidaFichero.write(block, 0, b);
					}

					// Cierre de flujos
					inFich3.close();
					salidaFichero.close();
					System.out.println("El archivo ha sido verificado correctamente.");
				} else {
					System.out.println("El archivo NO ha podido ser verificado. Inténtelo de nuevo.");
				}
				// Cierre del fichero de entrada
				inFile.close();
			}
		}
			*/
	}


}
