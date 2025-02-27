import java.io.FileReader;
import java.io.IOException;
import java.security.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class RSAKljuc {

    private static final int VELICINA_KLJUCA=2048;
    private static final String KLJUC_ALGORITAM="RSA";
    private static final String PUTANJA_DO_CA_KLJUCA="src\\sertifikati\\CA\\rootkey.pem";

    public static KeyPair generisanjeParaKljuceva() throws Exception
    {
        try
        {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator kljucGenerator=KeyPairGenerator.getInstance(KLJUC_ALGORITAM,"BC");
            kljucGenerator.initialize(VELICINA_KLJUCA,new SecureRandom());
            return kljucGenerator.generateKeyPair();
        }
        catch (Exception e)
        {
            throw new Exception("Greska prilikom generisanja kljuca");
        }
    }

    public static PrivateKey getCAPrivatniKljuc() throws IOException
    {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser pemParser= new PEMParser(new FileReader(PUTANJA_DO_CA_KLJUCA));
        PEMKeyPair pemKljucPar= (PEMKeyPair) pemParser.readObject();
        JcaPEMKeyConverter konvertor= new JcaPEMKeyConverter().setProvider("BC");
        PrivateKey caKljuc= konvertor.getPrivateKey(pemKljucPar.getPrivateKeyInfo());
        pemParser.close();
        return caKljuc;
    }

}
