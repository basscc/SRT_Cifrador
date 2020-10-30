import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Cypher {

    PBEKeySpec pbeKeySpec;
    PBEParameterSpec pPS;
    SecretKeyFactory kf;
    SecretKey sKey;
    
    Cipher c;
    CipherOutputStream cos;

    public Cypher() {
    	
    	init();	
    }
    
    private void init() {
    	
    	char[] pass = {'t', 'e', 's', 't'};
    	
    	pbeKeySpec = new PBEKeySpec(pass);
    	pPS = new PBEParameterSpec(salt,iterationCount);
    	
    	kf = SecrectKeyFactory.getInstance(algorithm);
    	kf.generateSecrect(pbeKeySpec);
    	
    	c = Cipher.getInstance(algorithm);
    	c.init(Cipher.ENCRYPT_MODE,sKey,pPS);
    }
}
