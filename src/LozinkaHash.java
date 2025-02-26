import korisnik.Korisnik;
import org.bouncycastle.util.encoders.Hex;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class LozinkaHash {

    private static final String HASH_ALG="SHA-256";
    private static final int DUZINA_SALT=16;

   public static String otisakLozinke(String lozinka)
   {
       try
       {
           byte[] salt=generisanjeSalta();
           byte[] hashLoz=hashLozinke(lozinka,salt,HASH_ALG);
           String s= Hex.toHexString(salt);
           String hp=Hex.toHexString(hashLoz);
           return s+"$"+hp;
       }catch (NoSuchAlgorithmException e)
       {
           throw new RuntimeException("Greska prilikom hashiranja lozinke",e);
       }
   }

   public static boolean provjeriLozinku(Korisnik korisnik,String lozinka) {
       try {
           String[] dijelovi = korisnik.getLozinka().split("\\$");
           byte[] saltKorisnik = Hex.decode(dijelovi[0]);
           byte[] hashLozinkeKorisnik = Hex.decode(dijelovi[1]);
           byte[] hashLozinkeLogin = hashLozinke(lozinka, saltKorisnik, HASH_ALG);
           if (MessageDigest.isEqual(hashLozinkeKorisnik, hashLozinkeLogin)) {
               return true;
           }
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }
       return false;
   }

   private static byte[] generisanjeSalta()
   {
       byte[] salt=new byte[DUZINA_SALT];
       SecureRandom random=new SecureRandom();
       random.nextBytes(salt);
       return salt;
   }

   private static byte[] hashLozinke(String lozinka,byte[] salt,String algoritam)throws NoSuchAlgorithmException
   {
       MessageDigest md=MessageDigest.getInstance(algoritam);
       md.update(salt);
       return md.digest(lozinka.getBytes());
   }
}
