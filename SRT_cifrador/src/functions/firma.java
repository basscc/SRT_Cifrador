package functions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import utils.Header;
import utils.Options;
// todos los todo del documento son comentarios mios (piti)
//TODO: esta vaina deduzco que es para generar el par de claves, no pillo que es eso de serial Version UID pero no parece importante
class Llaves implements Serializable {
	private static final long serialVersionUID = 1L;

	/* Variables de la clase LLaves */
	private PrivateKey pkprivate;
	private PublicKey pkpublic;

	/* Metodos de la calse LLaves */
	public void setPublicKey(PublicKey pkp) {
		this.pkpublic = pkp;
	}

	public PublicKey getPublicKey() {
		return this.pkpublic;
	}

	public void setPrivateKey(PrivateKey ppkpr) {
		this.pkprivate = ppkpr;
	}

	public PrivateKey getPrivateKey() {
		return this.pkprivate;
	}
}

public class firma {

	static byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };

	/* PRACTICA 4 */

	//TODO: esta vaina ha salido de un enlace del campus http://chuwiki.chuidiang.org/index.php?title=Serializaci%C3%B3n_de_objetos_en_java  en principio esta bien
	public static void generadorClaves() {
		try {
			// Generar el par de claves
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA"); // TODO: esto esta en el campus a medias, en RSA pone algoritmo, no se por que usa RSA por defecto, parametro?
			kpg.initialize(512);
			KeyPair keyPair = kpg.generateKeyPair();

			PrivateKey pkPrivate = keyPair.getPrivate();
			PublicKey pkPublic = keyPair.getPublic();

			// Uso de la clase serializable para generar las llaves
			Llaves llaves = new Llaves();

			llaves.setPrivateKey(pkPrivate);
			llaves.setPublicKey(pkPublic);

			// Trasformar clase en bytes
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objStream = new ObjectOutputStream(byteStream);

			objStream.writeObject(llaves);
			objStream.close();
			byte[] bytes = byteStream.toByteArray();

			FileOutputStream salida = new FileOutputStream("prueba.key");
			salida.write(bytes);
			salida.close();

			System.out.println("Las claves se han generado correctamente");

		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

	}

	public static PrivateKey obtenerClavePrivada() {
		PrivateKey clavePrivada = null;
		try {
			FileInputStream entrada = new FileInputStream("prueba.key");

			int nBytes = entrada.available();
			byte[] bytes = new byte[nBytes];

			entrada.read(bytes);
			entrada.close();

			ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
			ObjectInputStream objStream = new ObjectInputStream(byteStream);

			Llaves llaves = (Llaves) objStream.readObject();
			objStream.close();

			clavePrivada = llaves.getPrivateKey();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clavePrivada;

	}

	public static PublicKey obtenerClavePublica() {
		PublicKey clavePublica = null;
		try {
			FileInputStream entrada = new FileInputStream("prueba.key");
			int nBytes = entrada.available();
			byte[] bytes = new byte[nBytes];

			entrada.read(bytes);
			entrada.close();

			ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes); // bytes es el byte[]
			ObjectInputStream objStream = new ObjectInputStream(byteStream);
			Llaves llaves = (Llaves) objStream.readObject();

			objStream.close();
			clavePublica = llaves.getPublicKey();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clavePublica;

	}
	
	public static void firmar(File fichero, String algoritmoFirma)
			throws Exception {

		String algoritmo1 = "none";
		FileInputStream ficheroEntrada = new FileInputStream(fichero.getAbsolutePath());

		String nomSalida = fichero + ".fir";
		FileOutputStream salida = new FileOutputStream(nomSalida);

		Signature dsa = Signature.getInstance(algoritmoFirma); //TODO: campus tambien, aqui si lo pasa por parametro y eso esta bien, es el que eliges en el menu
		PrivateKey pkr = obtenerClavePrivada();
		dsa.initSign(pkr);

		byte[] datos = new byte[1];
		while (ficheroEntrada.read(datos) != -1) {
			dsa.update(datos);
		}

		byte[] sig = dsa.sign();

		Header header = new Header(Options.OP_SIGNED, algoritmo1, algoritmoFirma, sig);
		header.save(salida);

		ficheroEntrada.close();
		ficheroEntrada = new FileInputStream(fichero.getAbsolutePath());

		while (ficheroEntrada.read(datos) != -1) {
			salida.write(datos);
		}
		ficheroEntrada.close();
		salida.close();
		System.out.println("El fichero se ha firmado correctamente");

	}
	
	public static boolean verificarFirma(File fichero)
			throws Exception {
		boolean verificado = false;
		FileInputStream entrada = new FileInputStream(fichero.getAbsolutePath());

		String nombreSalida = fichero.getAbsolutePath().substring(0, fichero.getAbsolutePath().length() - 4) + ".cla";

		// Preparacion de la cabecera
		Header header = new Header();
		header.load(entrada);
		Signature dsa = Signature.getInstance(header.getAlgorithm2());

		dsa.initVerify(obtenerClavePublica());

		byte[] datos = new byte[1];
		while (entrada.read(datos) != -1) {
			dsa.update(datos);
		}
		byte[] sig = header.getData();
		verificado = dsa.verify(sig);

		if (verificado) {
			System.out.println("Firma verificada correctamente");
			entrada.close();
			entrada = new FileInputStream(fichero.getAbsolutePath());
			FileOutputStream salida = new FileOutputStream(nombreSalida);
			header.load(entrada);// para leer la cabecera
			while (entrada.read(datos) != -1) {
				salida.write(datos);
			}
			salida.close();
			entrada.close();
		} else {
			System.out.println("No se pudo verificar la firma");
		}
		return verificado;
	}

	public static void cifradoClaves(File fichero)
			throws Exception {

		byte[] data = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, 0x09, 0x0f, 0x0a };

		String algoritmo1 = "RSA/ECB/PKCS1Padding";
		String algoritmo2 = "none";

		Cipher c = Cipher.getInstance(algoritmo1); //TODO: segun el campus no podemos usar CipherInputStream/CipherOutputStrea pero si podemos usar la clase que ya tenemos, aunque parece que asi esta bien
		c.init(c.ENCRYPT_MODE, obtenerClavePublica());

		// Obtencion del ficheor para el cifrado
		FileInputStream ficheroEntrada = new FileInputStream(fichero.getAbsolutePath());
		FileOutputStream ficheroSalida = new FileOutputStream(fichero + ".cif");

		Header h = new Header(Options.OP_PUBLIC_CIPHER, algoritmo1, algoritmo2, data);
		h.save(ficheroSalida);

		int bloqueSize = 53; // tamanio de bloque
		byte[] bloque = new byte[bloqueSize];
		byte resultado[];
		int lectura = 0;
		while ((lectura = ficheroEntrada.read(bloque)) != -1) {
			resultado = c.doFinal(bloque);
			ficheroSalida.write(resultado, 0, 64);
		}

		ficheroSalida.close();
		ficheroEntrada.close();

		System.out.println("El fichero se ha cifrado correctamente con las claves");
	}

	public static void descifradoClaves(File fichero) throws Exception {

		FileInputStream ficheroEntrada = new FileInputStream(fichero.getAbsolutePath());
		String nomFichero = fichero.getAbsolutePath();
		nomFichero = nomFichero.substring(0, nomFichero.length() - 4);

		FileOutputStream salida = new FileOutputStream(nomFichero + ".cla");

		Header header = new Header();
		header.load(ficheroEntrada);

		PrivateKey pkr = obtenerClavePrivada();
		Cipher c = Cipher.getInstance(header.getAlgorithm1());
		c.init(c.DECRYPT_MODE, pkr);

		int tamano = 64; // tamanio de bloque
		byte[] bloque = new byte[tamano];
		int read;
		while ((read = ficheroEntrada.read(bloque)) != -1) {
			byte out[] = c.doFinal(bloque);
			salida.write(out);
		}
		salida.close();
		ficheroEntrada.close();
		System.out.println("El fichero se ha descifrado correctamente con las claves");

	}



	

	public static void main(String[] args) throws Exception {
		String tec;
		BufferedReader lec = new BufferedReader(new InputStreamReader(System.in));

		Boolean continuar = true;

		do {
			System.out.println("Seleccione la operación que desee:");
			System.out.println("1: Generar Claves\n" + "2: Firmar Fichero\n" + "3: Verificar Firma\n"
					+ "4: Cifrar fichero con claves\n" + "5: Descifrar fichero con claves");
			tec = lec.readLine();
			switch (tec) {
			case "1":
				generadorClaves();
				break;
			case "2":
				System.out.println("Introduzca el nombre del fichero que desea firmar: ");
				tec="";
				tec = lec.readLine();
				File fichero = new File(tec);
				System.out.println("¿Qué algoritmo desea utilizar para la fima? ");
				System.out.println("A: SHA1withRSA" + "\n" + "B: SHAwithDSA" + "\n" + "C: MD2withRSA" + "\n" + "D: MD5withRSA");
				tec = lec.readLine();
				switch (tec) {
				case "A":
					firmar(fichero, "SHA1withRSA");
					break;
				case "B":
					firmar(fichero, "SHAwithDSA");
					break;
				case "C":
					firmar(fichero, "MD2withRSA");
					break;
				case "D":
					firmar(fichero, "MD5withRSA");
					break;
				default:
					System.out.println("La respuesta es incorrecta, vuelva a comenzar");
					break;
				}

				break;
			case "3":
				System.out.println("Introduzca el nombre fichero sobre el que desea verificar la firma: ");
				tec="";
				tec = lec.readLine();
				File ficheroFirma = new File(tec);
				verificarFirma(ficheroFirma);
				break;
			case "4":
				System.out.println("Introduzca el nombre del fichero que desea cifrar con las claves: ");
				tec="";
				tec = lec.readLine();
				File ficheroClave = new File(tec);
				cifradoClaves(ficheroClave);
				break;
			case "5":
				System.out.println("Introduzca el nombre del fichero que desea descifrar con las claves: ");
				tec="";
				tec = lec.readLine();
				File ficheroClaveDes = new File(tec);
				descifradoClaves(ficheroClaveDes);
				break;
			default:
				System.out.println("La respuesta es incorrecta, vuelva a ejecutar el proyecto");
				break;
			}
			System.out.println("¿Desea realizar otra operacion?");
			System.out.println("1: Si\n" + "2: No");
			tec=lec.readLine();
			if(!tec.equals("1")) {
				continuar=false;
			}

		} while (continuar);

	}
}
