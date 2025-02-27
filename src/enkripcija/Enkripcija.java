package enkripcija;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

public class Enkripcija {

    private static final String PUTANJA_DO_POTPISA="src\\simulacije";

    private static final byte[] KLJUC_BAJTOVI = Hex.decode("000102030405060708090a0b0c0d0e0f");

    private static final byte[] IV = Hex.decode("9f741fdb5d8845bdb48a94394e84f8a3");
    public static byte[] potpisiFajl(PrivateKey privatniKljuc, byte[] fajlZaPotpisati) {

        try {
            Signature potpis = Signature.getInstance("SHA256withRSA");
            potpis.initSign(privatniKljuc);
            potpis.update(fajlZaPotpisati);
            return potpis.sign();
        } catch (Exception e) {
            return null;
        }
    }
    public static void cuvajPotpis(byte[] potpis, String korisnickoIme) {
        String putanjaPotpis = PUTANJA_DO_POTPISA+File.separator + korisnickoIme +File.separator +"potpis.txt";

        try {
            FileOutputStream fos = new FileOutputStream(putanjaPotpis);
            fos.write(potpis);
            fos.close();
            System.out.println("Potpis je spašen u datoteci: " + putanjaPotpis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[] enkripcijaFajla(byte[] fajlZaEnkripciju) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec kljuc = new SecretKeySpec(KLJUC_BAJTOVI, "AES");
            Cipher sifrat = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
            sifrat.init(Cipher.ENCRYPT_MODE, kljuc, new IvParameterSpec(IV));
            byte[] output;
            output = sifrat.doFinal(fajlZaEnkripciju);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cuvajSimulaciju(byte[] enkripcija,String korisnickoIme)
    {
        String putanjaDoSimualcije=PUTANJA_DO_POTPISA+File.separator + korisnickoIme +File.separator +"simulacija.txt";
        try {
            FileOutputStream fos = new FileOutputStream(putanjaDoSimualcije);
            fos.write(enkripcija);
            fos.close();
            System.out.println("Putanja do simulacije: " + putanjaDoSimualcije);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Greska prilikom cuvanja simulacije");
        }
    }

    public static byte[] dekripcijaFajla(byte[] fajlZaDekripciju)
    {

        try {
            Security.addProvider(new BouncyCastleProvider());
            SecretKeySpec kljuc = new SecretKeySpec(KLJUC_BAJTOVI, "AES");
            Cipher sifrat = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
            sifrat.init(Cipher.DECRYPT_MODE, kljuc, new IvParameterSpec(IV));
            byte[] finalniIzlaz = new byte[sifrat.getOutputSize(fajlZaDekripciju.length)];
            int duzina = sifrat.update(fajlZaDekripciju, 0, fajlZaDekripciju.length, finalniIzlaz, 0);
            duzina += sifrat.doFinal(finalniIzlaz, duzina);
            return Arrays.copyOfRange(finalniIzlaz, 0, duzina);
        }catch (javax.crypto.IllegalBlockSizeException e)
        {
            System.out.println("Doslo je do greske prilikom dekripcije fajla."+e.getMessage());
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Greska prilikom dekripcije fajla");
            return null;
        }
    }
    public static boolean verifikacijaPotpisa(PublicKey javniKljuc, byte[] fajlZaVerifikaciju, byte[] potpis) {

        try
        {
            Signature potpisVerifikuj = Signature.getInstance("SHA256withRSA");
            potpisVerifikuj.initVerify(javniKljuc);
            potpisVerifikuj.update(fajlZaVerifikaciju);
            return potpisVerifikuj.verify(potpis);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }
    public static byte[] getPotpis(String korisnickoIme)
    {
        try
        {
            File fajl = new File(PUTANJA_DO_POTPISA+File.separator + korisnickoIme + File.separator + "potpis.txt");
            FileInputStream fis = new FileInputStream(fajl);
            byte[]  potpis = fis.readAllBytes();
            fis.close();
            return potpis;
        } catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Greska prilikom dohvatanja potpisa.");
            return null;
        }
    }

    public static byte[] getSimulacija(String korisnickoIme)
    {
        byte[] simulacija = null;
        try {
            File fajl = new File(PUTANJA_DO_POTPISA+File.separator + korisnickoIme + File.separator + "simulacija.txt");
            FileInputStream fis = new FileInputStream(fajl);
            byte[]  simulacijaBajtovi = fis.readAllBytes();
            fis.close();
            return simulacijaBajtovi;
        }catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Greska prilikom dohvatanja simulacije fajla");
        }
        return simulacija;
    }
}
