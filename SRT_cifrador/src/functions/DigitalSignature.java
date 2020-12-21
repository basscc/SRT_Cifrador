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

class Keys implements Serializable {
	private static final long serialVersionUID = 8799656478674716638L;

	// Variables
	private PublicKey pku;
	private PrivateKey pkr;
	private String alias; // For key storage

	// Methods
	public void setPublicKey(PublicKey pku) {
		this.pku = pku;
	}

	public PublicKey getPublicKey() {
		return this.pku;
	}

	public void setPrivateKey(PrivateKey pkr) {
		this.pkr = pkr;
	}

	public PrivateKey getPrivateKey() {
		return this.pkr;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return this.alias;
	}
}

public class DigitalSignature {

	static byte[] salt = new byte[] { 0x53, 0x45, 0x43, 0x52, 0x45, 0x54, 0x4f, 0x53 };

	/*
	 * Method to generate a key ring for later use in digital signature
	 */
	public void keyGeneration() throws NoSuchAlgorithmException, IOException {

		// Getting instance for key pair generation
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

		// Initialize the generator
		kpg.initialize(512);

		// Generate a key pair
		KeyPair keyPair = kpg.generateKeyPair();

		// Access to the components
		PublicKey pku = keyPair.getPublic();
		PrivateKey pkr = keyPair.getPrivate();

		// Use of serializable class to set the keys
		Keys keys = new Keys();
		keys.setPublicKey(pku);
		keys.setPrivateKey(pkr);

		// Transform object to bytes[]
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bs);
		os.writeObject(keys);
		os.close();
		byte[] bytes = bs.toByteArray();

		// Generate output file
		FileOutputStream outFile = new FileOutputStream("practica5.key");
		outFile.write(bytes);

		outFile.close();
	}

	public PublicKey getPublicKey() {

		PublicKey pku = null;

		try {
			// Get file stream
			FileInputStream inFile = new FileInputStream("practica5.key");
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

	public PrivateKey getPrivateKey() {

		PrivateKey pkr = null;

		try {
			// Get file stream
			FileInputStream inFile = new FileInputStream("practica5.key");
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

	/*
	 * Method to sign a file with a signature
	 */
	public void sign(File file, String alg) throws Exception {

		// Get streams
		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
		FileOutputStream outFile = new FileOutputStream(
				file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".fir");

		// Get the instance of the object
		Signature dsa = Signature.getInstance(alg);

		// Sign with private key
		dsa.initSign(getPrivateKey());

		// Process the information
		byte[] block = new byte[1];
		while (inFile.read(block) != -1) {
			dsa.update(block);
		}

		// Get the sign
		byte[] sig = dsa.sign();

		// Save the signed header
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

	/*
	 * Method to verify a file signature
	 */
	public boolean verifySign(File file) throws Exception {

		boolean verified = false;

		// Get file stream
		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());

		// Read file header
		Header header = new Header();
		header.load(inFile);

		// Getting instance of the object
		Signature dsa = Signature.getInstance(header.getAlgorithm2());

		// Initiation to verify with public key
		dsa.initVerify(getPublicKey());

		// Process the information
		byte[] block = new byte[1];
		while (inFile.read(block) != -1) {
			dsa.update(block);
		}

		// Get the signature from the header
		byte[] sig = header.getData();

		// Verify the sign
		verified = dsa.verify(sig);

		if (verified) {
			inFile.close();
			inFile = new FileInputStream(file.getAbsolutePath());
			FileOutputStream outFile = new FileOutputStream(
					file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".cla");
			header.load(inFile);
			while (inFile.read(block) != -1) {
				outFile.write(block);
			}
			outFile.close();
		}
		else {
			throw new Exception("Firma no verificada, no coincide");
		}

		inFile.close();
		return verified;
	}

	/*
	 * Method to cipher a file with a key The output is a file with extension .cif
	 */
	public void keyCipher(File file) throws Exception {

		byte[] data = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, 0x09, 0x0f, 0x0a };

		String alg1 = "RSA/ECB/PKCS1Padding";
		String alg2 = "none";

		// Initiate cipher with header alg and pku
		Cipher c = Cipher.getInstance(alg1);
		c.init(Cipher.ENCRYPT_MODE, getPublicKey());

		// Get streams
		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
		FileOutputStream outFile = new FileOutputStream(
				file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".cif");

		Header header = new Header(Options.OP_PUBLIC_CIPHER, alg1, alg2, data);
		header.save(outFile);

		// Cipher each block
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

	/*
	 * Method to decipher a file with a key The output is a file with extension .cla
	 */
	public void keyDecipher(File file) throws Exception {

		// Get streams
		FileInputStream inFile = new FileInputStream(file.getAbsolutePath());
		FileOutputStream outFile = new FileOutputStream(
				file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".cla");

		// Read header
		Header header = new Header();
		header.load(inFile);

		// Initiate decipher with header alg and pkr
		Cipher c = Cipher.getInstance(header.getAlgorithm1());
		c.init(Cipher.DECRYPT_MODE, getPrivateKey());

		// Decipher each block
		int blockSize = 64;
		int messageBytes = 0;
		byte[] block = new byte[blockSize];
		while ((inFile.read(block)) != -1) {
			
			messageBytes = 0;	
			byte out[] = c.doFinal(block);
			while (out[messageBytes] != '\0') {
				messageBytes++;
			}
			
			outFile.write(out, 0, messageBytes);
		}

		outFile.close();
		inFile.close();
	}
}
