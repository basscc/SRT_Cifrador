package functions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class KeyStorage {

	private Keys[] keyStorage;

	public KeyStorage() {

		this.keyStorage = new Keys[100];
	}

	/*
	 *  Alt constructor with file input
	 */
	public KeyStorage(File miksFile, String pwKs) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException {

		this.keyStorage = new Keys[100];

		// Get keyStore instance
		KeyStore keyStore;
		keyStore = KeyStore.getInstance("JKS");

		// Read file and get password
		FileInputStream fileIN = new FileInputStream(miksFile.getAbsolutePath());
		char[] pwks = pwKs.toCharArray(); 
		
		// Try to load the keyStore with given inputs
		keyStore.load(fileIN, pwks);

		int cont = 0;

		Enumeration<String> enumAlias = keyStore.aliases();
		String alias;

		while (enumAlias.hasMoreElements()) {

			alias = (String) enumAlias.nextElement();

			if (keyStore.isKeyEntry(alias)) {
				PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, pwks);
				java.security.cert.Certificate cert = keyStore.getCertificate(alias);
				PublicKey publicKey = cert.getPublicKey();

				Keys key = new Keys();
				key.setPublicKey(publicKey);
				key.setPrivateKey(privateKey);

				keyStorage[cont] = new Keys();
				keyStorage[cont].setAlias(alias);
				keyStorage[cont].setPublicKey(key.getPublicKey());
				keyStorage[cont].setPrivateKey(key.getPrivateKey());
				cont++;
			}
		}
	}
	
	// TODO : Este metodo, primera linea? que hace o intenta hacer? exportar las claves? entonces para que necesita la chosenKey?
	/*
	 * Import keys by file?
	 */
	public void importKeys(File miksFile, String password, int pos)
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {

		try {
			Keys[] ks = keyStorage(miksFile, password);
			Keys chosenKey = chooseKey(ks, pos);
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			os.writeObject(chosenKey);
			os.close();
			
			byte[] bytes = bs.toByteArray();
			FileOutputStream outFile = new FileOutputStream("practica5.key");
			outFile.write(bytes);
			outFile.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Returns the key at position i
	 */
	public Keys chooseKey(Keys[] keyStorage, int i) {
		return keyStorage[i];
	}

	public Keys[] getKeyStorage() {
		return keyStorage;
	}

	public void setKeyStorage(Keys[] keyStorage) {
		this.keyStorage = keyStorage;
	}
}
