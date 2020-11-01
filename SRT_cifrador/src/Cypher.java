import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import utils.Header;
import utils.Options;

public class Cypher {
	// "PBEWithMD5AndDES"; "PBEWithMD5AndDES";
	/*
	 * TODO Borrar este msj
	 * 
	 * Se necesitan 3 arrays de datos (formato byte)
	 * 
	 * 1- Mensaje (pasar de string a bytes) que se desea encriptar 2- Clave 3-
	 * Vector de inicialización
	 * 
	 * Para desencriptar mas de lo mismo
	 * 
	 * 1- Contraseña 2- Salt 3- Entero con numero de iteraciones
	 * 
	 */

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

	private PBEKeySpec pbeKeySpec;
	private PBEParameterSpec pbeParam;

	private SecretKeyFactory kFactory;
	private SecretKey sKey;

	private Cipher c;
	private CipherOutputStream cOut;

	public Cypher() {

		try {
			init();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {

		// Create password
		char[] pass = "testeando".toCharArray();
		pbeKeySpec = new PBEKeySpec(pass);

		// Create byte salt
		Random random = new Random();
		byte[] saltBytes = new byte[32];
		random.nextBytes(saltBytes);

		// Create iteration count
		int itCount = 1024;

		pbeParam = new PBEParameterSpec(saltBytes, itCount);

		kFactory = SecretKeyFactory.getInstance("PBEWithSHAAnd3KeyTripleDES", "BC");
		sKey = kFactory.generateSecret(pbeKeySpec);

		c = Cipher.getInstance("PBEWithSHAAnd3KeyTripleDES", "BC");
		c.init(Cipher.ENCRYPT_MODE, sKey, pbeParam);
	}

	public static void cipherFile(File file, String alg, String pw) throws Exception {

		int itCount = 1024;

		// Create password
		char[] pass = "testeando".toCharArray();
		pbeKeySpec = new PBEKeySpec(pass);

		// Create byte salt
		SecureRandom random = new SecureRandom();
		byte[] saltBytes = new byte[32];
		random.nextBytes(saltBytes);

		// Create header

		Header header = new Header(Options.OP_SYMMETRIC_CIPHER, alg, "", saltBytes);

		PBEParameterSpec pPS = new PBEParameterSpec(header.getData(), itCount);

		kFactory = SecretKeyFactory.getInstance(header.getAlgorithm1());
		sKey = kFactory.generateSecret(pbeKeySpec);

		c = Cipher.getInstance(alg);
		c.init(Cipher.ENCRYPT_MODE, sKey, pbeParam);

		// Flujos de ficheros
		OutputStream ficheroSalida = new FileOutputStream(file + ".cif");
		header.save(ficheroSalida);
		FileInputStream ficheroEntrada = new FileInputStream(file);

		CipherOutputStream cos = new CipherOutputStream(ficheroSalida, c);

		// Accion de cifrado de fichero
		int iterlec;
		byte[] block = new byte[16];
		while ((iterlec = ficheroEntrada.read(block)) != -1) {
			cos.write(block, 0, iterlec);
			// System.out.println("encrypted: " + Hex.toHexString(block));
		}
		cos.close();
		ficheroEntrada.close();
		ficheroSalida.close();
		System.out.println("El fichero se ha cifrado correctamente");
	}

	public void decipherFile(File fichero, String password) throws Exception {
		// Fichero de entrada
		InputStream ficheroEntrada = new FileInputStream(fichero);

		// Cabecera
		Header header = new Header();
		header.load(ficheroEntrada);

		// Generacion de clave
		char[] pass = "testeando".toCharArray();
		int iterationCount = 1024;

		// SecrectKeyFactory: Nos permitirá generar la clave de sesión para el algoritmo
		// elegido; la crearemos indicando el algoritmo para el que se quiere la clave
		PBEKeySpec pbeKeySpec = new PBEKeySpec(pass);
		PBEParameterSpec pPS = new PBEParameterSpec(header.getData(), iterationCount);

		kFactory = SecretKeyFactory.getInstance(header.getAlgorithm1());
		sKey = kFactory.generateSecret(pbeKeySpec);

		// Descifrado del fichero
		// Cipher: Clase cifrador; la crearemos indicando el algoritmo y después la
		// iniciaremos con la clave y los parámetros

		c = Cipher.getInstance(header.getAlgorithm1());
		c.init(Cipher.ENCRYPT_MODE, sKey, pbeParam);

		// Accion de generacion de ficheros de salida
		String nombre = fichero.getAbsolutePath();
		nombre = nombre.substring(0, nombre.length() - 4);
		FileOutputStream ficheroSalida = new FileOutputStream(nombre);
		CipherInputStream cos = new CipherInputStream(ficheroEntrada, c);

		byte[] bloque = new byte[1024];
		int iterlec;
		while ((iterlec = cos.read(bloque)) != -1) {
			ficheroSalida.write(bloque, 0, iterlec);
		}

		cos.close();
		ficheroEntrada.close();
		ficheroSalida.close();
		System.out.println("El fichero se ha descifrado correctamente");

	}
}
