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
/*
 * Developed by:
 * 
 * Carlos Salguero Sánchez
 * Javier Tovar Pacheco
 * 
 * UNEX - 2020 - SRT
 */

public class KeyStorage {

	private Keys[] keyStorage;

	public KeyStorage() {

		this.keyStorage = new Keys[500];
	}

	/*
	 * Alt constructor with file input and password integrity check
	 */
	public KeyStorage(File miksFile, String pwKs) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException {

		this.keyStorage = new Keys[500];

		// Get keyStore instance
		KeyStore keyStore;
		keyStore = KeyStore.getInstance("JKS");

		// Read file and get password
		FileInputStream fileIN = new FileInputStream(miksFile.getAbsolutePath());
		char[] pwks = pwKs.toCharArray();

		// Try to load the keyStore with given inputs
		keyStore.load(fileIN, pwks);

		int cont = 0;

		Enumeration<String> e = keyStore.aliases();
		String alias;

		while (e.hasMoreElements()) {

			alias = (String) e.nextElement();

			if (keyStore.isKeyEntry(alias)) {
				PrivateKey pkr = (PrivateKey) keyStore.getKey(alias, pwks);
				java.security.cert.Certificate cert = keyStore.getCertificate(alias);
				PublicKey pku = cert.getPublicKey();

				Keys key = new Keys();
				key.setPublicKey(pku);
				key.setPrivateKey(pkr);

				keyStorage[cont] = new Keys();
				keyStorage[cont].setAlias(alias);
				keyStorage[cont].setPublicKey(key.getPublicKey());
				keyStorage[cont].setPrivateKey(key.getPrivateKey());
				cont++;
			}
		}
	}

	/*
	 * Export selected key to file
	 */
	public void exportKey(int pos) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, NullPointerException {

		try {
			// Select key from storage and transform to byte array

			Keys chosenKey = chooseKey(getKeyStorage(), pos);
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			os.writeObject(chosenKey);
			os.close();
			byte[] bytes = bs.toByteArray();

			// Write key bytes to output file
			FileOutputStream outFile = new FileOutputStream("practica5.key");
			outFile.write(bytes);
			outFile.close();

			System.out.println("Se ha generado el fichero de clave practica5.key \n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Returns the key at position i
	 */
	public Keys chooseKey(Keys[] keyStorage, int i) throws NullPointerException {
		if (keyStorage[i] == null)
			throw new NullPointerException("La clave elegida no existe");

		return keyStorage[i];
	}

	/*
	 * Show all stored keys in console
	 */
	public void showKeys() {

		Boolean fin = false;

		System.out.println("Mostrando listado de claves\n");
		
		for (int i = 0; i < getKeyStorage().length && !fin; i++) {
			if (keyStorage[i] == null)
				fin = true;

			if (!fin)
				System.out.println(i + ". " + keyStorage[i].getAlias() + "\n");
		}

	}

	public Keys[] getKeyStorage() {
		return keyStorage;
	}

	public void setKeyStorage(Keys[] keyStorage) {
		this.keyStorage = keyStorage;
	}
}
