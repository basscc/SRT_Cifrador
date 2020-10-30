import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Cypher {
	
	/*TODO Borrar este msj
	 * 
	 * Se necesitan 3 arrays de datos (formato byte)
	 * 
	 * 1- Mensaje (pasar de string a bytes) que se desea encriptar
	 * 2- Clave
	 * 3- Vector de inicialización
	 * 
	 * Para desencriptar mas de lo mismo
	 * 
	 * 1- Contraseña
	 * 2- Salt
	 * 3- Entero con numero de iteraciones
	 * 
	 */
	
	/*TODO Borrar esto
	 * 
	 * Hay que modularizar esto, una clase para encriptar y otra para desencriptar
	 * 
	 * Quiza se puede utilizar una init para las variables previas (salt?) (iteraciones?)
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
    
    private void init() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
    	
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
}
