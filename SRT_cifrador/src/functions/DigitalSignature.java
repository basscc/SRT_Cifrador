package functions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import javax.crypto.Cipher;
import utils.Header;
import utils.Options;

// TODO: todos los todo del documento son comentarios mios (piti)

class Keys implements Serializable {
	private static final long serialVersionUID = 8799656478674716638L;

	// Variables
	private PublicKey pku;
	private PrivateKey pkr;

	// Methods
	public void setPublicKey(PublicKey pku2) {
		this.pku = pku2;
	}

	public PublicKey getPublicKey() {
		return this.pku;
	}

	public void setPrivateKey(PrivateKey pkr2) {
		this.pkr = pkr2;
	}

	public PrivateKey getPrivateKey() {
		return this.pkr;
	}
}

public class DigitalSignature {

	// TODO: salt estatica, cambiar?
	static byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };

	// TODO: esta vaina ha salido de un enlace del campus
	// http://chuwiki.chuidiang.org/index.php?title=Serializaci%C3%B3n_de_objetos_en_java
	// en principio esta bien
	public static void generadorClaves() {
		try {
			// Getting instance for key pair generation
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA"); // TODO: esto esta en el campus a medias, en RSA
																		// pone algoritmo, y mas abajo pone [DSA], RSA.
																		// lo que
																		// esta entre corchetes se supone que no
																		// funciona, porque en firma el sha1 no se que
																		// tampoco rula
			// Initialize the generator
			kpg.initialize(512);
			// Generate a key pair
			KeyPair keyPair = kpg.generateKeyPair();

			// Access to the components
			PublicKey pku = keyPair.getPublic();
			PrivateKey pkr = keyPair.getPrivate();

			// Use of serializable class to key generation
			Keys keys = new Keys();

			keys.setPublicKey(pku);
			keys.setPrivateKey(pkr);

			// Transform object to bytes[]
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			os.writeObject(keys);
			os.close();
			byte[] bytes = bs.toByteArray(); // it returns byte[]

			FileOutputStream outFile = new FileOutputStream("prueba.key");
			outFile.write(bytes);
			outFile.close();

		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

	}

	public static PublicKey getPublicKey() {
		PublicKey pku = null;
		try {
			// Getting bytes stream
			FileInputStream inFile = new FileInputStream("prueba.key");
			int numBytes = inFile.available();
			byte[] bytes = new byte[numBytes];
			inFile.read(bytes);
			inFile.close();

			// Transform bytes[] to object
			ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
			ObjectInputStream os = new ObjectInputStream(bs);
			Keys keys = (Keys) os.readObject();
			os.close();

			pku = keys.getPublicKey();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return pku;
	}

	public static PrivateKey getPrivateKey() {
		PrivateKey pkr = null;
		try {
			// Getting bytes stream
			FileInputStream inFile = new FileInputStream("prueba.key");
			int numBytes = inFile.available();
			byte[] bytes = new byte[numBytes];
			inFile.read(bytes);
			inFile.close();

			// Transform bytes[] to object
			ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
			ObjectInputStream os = new ObjectInputStream(bs);
			Keys keys = (Keys) os.readObject();
			os.close();

			pkr = keys.getPrivateKey();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return pkr;
	}

	public static void sign(File file, String alg) throws Exception {

		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
		FileOutputStream outFile = new FileOutputStream(
				file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".fir");

		// Creation of a sign
		// Get the instance of the object
		Signature dsa = Signature.getInstance(alg);
		// Initiation to sign with private key
		dsa.initSign(getPrivateKey());
		// Process the information
		byte[] block = new byte[1];
		while (inFile.read(block) != -1) {
			dsa.update(block);
		}
		// Get the sign
		byte[] sig = dsa.sign();

		Header header = new Header(Options.OP_SIGNED, "none", alg, sig);
		header.save(outFile);

		inFile.close();
		inFile = new FileInputStream(file.getAbsolutePath());

		while (inFile.read(block) != -1) {
			outFile.write(block);
		}
		inFile.close();
		outFile.close();

	}

	public static boolean verifySign(File file) throws Exception {

		boolean verifies = false;
		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());

		Header header = new Header();
		header.load(inFile);
		// Verify the sign
		// Getting instance of the object
		Signature dsa = Signature.getInstance(header.getAlgorithm2());
		// Initiation to verify with public key
		dsa.initVerify(getPublicKey());
		// Process the information
		byte[] block = new byte[1];
		while (inFile.read(block) != -1) {
			dsa.update(block);
		}
		// Get the sign
		byte[] sig = header.getData();
		// Verified the sign
		verifies = dsa.verify(sig);
		if (verifies) {
			inFile.close();
			inFile = new FileInputStream(file.getAbsolutePath());
			FileOutputStream outFile = new FileOutputStream(
					file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".cla");
			header.load(inFile);// para leer la cabecera
			while (inFile.read(block) != -1) {
				outFile.write(block);
			}
			outFile.close();
			inFile.close();
		} else {
			// TODO:
			System.out.println("No se pudo verificar la firma");
		}
		return verifies;
	}

	public static void keyCipher(File file) throws Exception {

		// TODO: semilla estatica, cambiar?
		byte[] data = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, 0x09, 0x0f, 0x0a };

		String alg1 = "RSA/ECB/PKCS1Padding";
		String alg2 = "none";

		// Getting instance
		Cipher c = Cipher.getInstance(alg1);
		// Initiation to ciphe with public key
		c.init(c.ENCRYPT_MODE, getPublicKey());

		// Obtain the file to ciphe
		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
		FileOutputStream outFile = new FileOutputStream(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".cif");

		Header header = new Header(Options.OP_PUBLIC_CIPHER, alg1, alg2, data);
		header.save(outFile);
		// Ciphe each block
		int blockSize = 53;
		byte[] block = new byte[blockSize];
		byte out[];
		while ((inFile.read(block)) != -1) {
			out = c.doFinal(block);
			outFile.write(out, 0, 64);
		}

		outFile.close();
		inFile.close();
	}

	public static void keyDecipher(File file) throws Exception {

		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
		FileOutputStream outFile = new FileOutputStream(
				file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".cla");

		Header header = new Header();
		header.load(inFile);

		// Getting the instance
		Cipher c = Cipher.getInstance(header.getAlgorithm1());
		// Initiation for decryption with private key
		c.init(c.DECRYPT_MODE, getPrivateKey());

		// Deciphe each block
		int blockSize = 64;
		byte[] block = new byte[blockSize];
		while ((inFile.read(block)) != -1) {
			byte out[] = c.doFinal(block);
			outFile.write(out);
		}
		outFile.close();
		inFile.close();

	}
}
