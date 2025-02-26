import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.*;

import korisnik.Korisnik;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class Sertifikat {

    private static final String PUTANJA_DO_CA_SERTIFIKATA = "src\\sertifikati\\CA\\rootca.pem";
    private static final String PUTANJA_DO_CA_KLJUCA = "src\\sertifikati\\CA\\rootkey.pem";
    private static final String PUTANJA_DO_KORISNICKIH_SERTIFIKATA="src\\sertifikati\\korisnicki_sertifikati\\";
    private static final String PUTANJA_DO_CRL="src\\sertifikati\\CRL\\";
    private static long serijskiBrojBaza=System.currentTimeMillis();
    private static final int JEDAN_MJESEC_U_SATIMA=730;

    private static final String POTPIS_ALGORITAM="SHA256withRSA";

    public static X509CertificateHolder getCASertifikat() throws IOException, CertificateException {
        FileInputStream fis = new FileInputStream(PUTANJA_DO_CA_SERTIFIKATA);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate sertifikat = (X509Certificate) cf.generateCertificate(fis);
        X509CertificateHolder caSertifikat = new JcaX509CertificateHolder(sertifikat);
        fis.close();
        return caSertifikat;
    }

    public static Date racunajDatum(int satiUBuducnosti) {
        long sekunde = System.currentTimeMillis() / 1000;
        return new Date((sekunde + ((long) (satiUBuducnosti) * 60 * 60)) * 1000);
    }

    public static synchronized BigInteger racunajSerijskiBroj()
    {
        return BigInteger.valueOf(serijskiBrojBaza++);
    }

    public static X509Certificate pretvoriX509CertificateHolder( X509CertificateHolder sertHolder)throws GeneralSecurityException
    {
        Security.addProvider(new BouncyCastleProvider());
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(sertHolder);
    }

    public static X509CertificateHolder pretvoriX509Sertifikat(X509Certificate sertifikat) throws CertificateException
    {
        Security.addProvider(new BouncyCastleProvider());
        return new JcaX509CertificateHolder(sertifikat);
    }
    public static X509Certificate kreiranjeKorisnickogSertifikata(Korisnik korisnik, PublicKey javniKljuc) throws Exception
    {
        try
        {
            X509CertificateHolder CASertifikat=getCASertifikat();
            PrivateKey CAKljuc=RSAKljuc.getCAPrivatniKljuc();

            X500NameBuilder x500NameBuilder= new X500NameBuilder(BCStyle.INSTANCE)
                    .addRDN(BCStyle.C,"BA")
                    .addRDN(BCStyle.ST,"RS")
                    .addRDN(BCStyle.O,"Elektrotehnicki fakultet")
                    .addRDN(BCStyle.CN,korisnik.getKorisnickoIme())
                    .addRDN(BCStyle.EmailAddress,korisnik.getEmail())
                    .addRDN(BCStyle.TELEPHONE_NUMBER,korisnik.getTelefon());

            X500Name subjectUser = x500NameBuilder.build();
            X509v3CertificateBuilder gradnjaSertifikata = new JcaX509v3CertificateBuilder(CASertifikat.getSubject(), racunajSerijskiBroj(), racunajDatum(0), racunajDatum(JEDAN_MJESEC_U_SATIMA * 6), subjectUser, javniKljuc);
            JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
            gradnjaSertifikata.addExtension(Extension.authorityKeyIdentifier,
                            false, extUtils.createAuthorityKeyIdentifier(CASertifikat))
                    .addExtension(Extension.subjectKeyIdentifier,
                            false, extUtils.createSubjectKeyIdentifier(javniKljuc))
                    .addExtension(Extension.keyUsage,
                            true, new KeyUsage(
                                    KeyUsage.digitalSignature));

            ContentSigner potpisivac= new JcaContentSignerBuilder(POTPIS_ALGORITAM).setProvider("BC").build(CAKljuc);

            X509Certificate korisnickiSertifikat= pretvoriX509CertificateHolder(gradnjaSertifikata.build(potpisivac));
            sacuvajKorisnickiSertifikat(korisnickiSertifikat);

            return korisnickiSertifikat;
        }catch (Exception e)
        {
            throw new Exception("Greska prilikom kreiranja korisnickog sertifikata");
        }
    }
    private static void sacuvajKorisnickiSertifikat(X509Certificate korisnickiSertifikat) throws IOException
    {
        File korisnickiFolderSertifikat= new File(PUTANJA_DO_KORISNICKIH_SERTIFIKATA);
        File korisnickiFileSertifikat= new File(korisnickiFolderSertifikat,korisnickiSertifikat.getSerialNumber()+".crt");

        FileWriter fw=new FileWriter(korisnickiFileSertifikat);
        JcaPEMWriter pemWriter = new JcaPEMWriter(fw);
        pemWriter.writeObject(korisnickiSertifikat);
        pemWriter.close();
        fw.close();
    }
    private static X509Certificate getKorisnickiSertifikat(BigInteger serijskiBroj) throws CertificateException,IOException
    {
        File korisnickiSertifikatiFolder= new File(PUTANJA_DO_KORISNICKIH_SERTIFIKATA);
        File korisnickiSeritifikatFile= new File(korisnickiSertifikatiFolder,serijskiBroj.toString()+".crt");

        try
        {
            FileInputStream fis= new FileInputStream(korisnickiSeritifikatFile);
            CertificateFactory cf= CertificateFactory.getInstance("X.509");
            X509Certificate sertifikat=(X509Certificate) cf.generateCertificate(fis);
            fis.close();
            return sertifikat;
        }catch (FileNotFoundException e)
        {
            return null;
        }
    }

    public static void kreirajPKCS12(Korisnik korisnik) throws Exception
    {
        try {
            String lozinka = "";
            Scanner scanner = new Scanner(System.in);
            do {
                System.out.println("Unesite lozinku za zaštitu PKCS12:");
                lozinka = scanner.nextLine();
            }
            while (lozinka.isEmpty());
            Security.addProvider(new BouncyCastleProvider());
            KeyStore store = KeyStore.getInstance("PKCS12", "BC");
            store.load(null, null);
            store.setKeyEntry(korisnik.getKorisnickoIme(), korisnik.getKorisnickiParKljuceva().getPrivate(), null, new java.security.cert.Certificate[]{korisnik.getKorisnickiSertifikat()});

            FileOutputStream outputStream = new FileOutputStream("src\\PKCS12\\"+korisnik.getKorisnickoIme()+".p12");
            store.store(outputStream, lozinka.toCharArray());
            outputStream.close();
            //scanner.close();
        }catch (Exception e)
        {
            throw new Exception("Greska prilikom kreiranja PKCS12");
        }
    }

    public static KeyStore citajPKCS12(String putanjaDoPCKS12,String lozinka)throws Exception
    {
        try
        {
            Security.addProvider(new BouncyCastleProvider());
            FileInputStream fileInputStream= new FileInputStream(putanjaDoPCKS12);
            KeyStore keyStore= KeyStore.getInstance("PKCS12","BC");
            keyStore.load(fileInputStream,lozinka.toCharArray());
            fileInputStream.close();
            return keyStore;
        }catch (Exception e)
        {
            throw new Exception("Greska prilikom citanja PKCS12");
        }
    }
    public static X509Certificate getKorisnickiSertifikatIzPKCS12(KeyStore keyStore)throws Exception
    {
        try
        {
            Enumeration<String> aliasi= keyStore.aliases();
            String alias =aliasi.nextElement();
            X509Certificate sertifikat=(X509Certificate)keyStore.getCertificate(alias);
            return sertifikat;
        }catch (Exception e)
        {
            throw new Exception("Greska prilikom ucitavanja korisnickog sertifikata iz PCKS12.");
        }
    }

    public static boolean validnostSertifikata(X509Certificate sertifikat)
    {
        try
        {
            X509Certificate CA=pretvoriX509CertificateHolder(getCASertifikat());
            if(Arrays.equals(sertifikat.getIssuerUniqueID(),CA.getSubjectUniqueID()))
            {
                sertifikat.verify(CA.getPublicKey());
                sertifikat.checkValidity();
                boolean[] upotrebaKljuca=sertifikat.getKeyUsage();
                if(!upotrebaKljuca[0])
                {
                    System.out.println("Upotreba ključa digitalnog potpisa nije omogućena!");
                    return false;
                }
                return true;
            }
            return false;
        }catch (CertificateExpiredException e)
        {
            System.out.println("Sertifikat je istekao");
            return false;
        }catch (IOException | CertificateException e)
        {
            System.out.println("Sertifikat nije jos validan");
            return false;
        }catch (GeneralSecurityException e)
        {
            System.out.println("Sertifikat nije jos potpisan od strane pouzdanog CA");
            return false;
        }
    }
    public static PrivateKey getPrivatniKljuc(KeyStore keyStore, String lozinkaPKCS12) throws Exception {
        Enumeration<String> aliases = keyStore.aliases();
        String alias = null;
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                break;
            }
        }
        if (alias == null) {
            throw new Exception("Nije pronađen privatni ključ u PKCS12 keystore-u.");
        }
        return (PrivateKey) keyStore.getKey(alias, lozinkaPKCS12.toCharArray());
    }
    public static boolean povucen(X509Certificate sertifikat) throws IOException, CRLException {
        if (crlPostoji()) {
            X509CRLHolder crlHolder = getCRL();
            Security.addProvider(new BouncyCastleProvider());
            X509CRL crl = new JcaX509CRLConverter().setProvider("BC").getCRL(crlHolder);
            if (!Objects.isNull(crl.getRevokedCertificates())) {
                Set<X509CRLEntry> revokedCertificates = new HashSet<>(crl.getRevokedCertificates());
                for (X509CRLEntry entry : revokedCertificates) {
                    if (entry.getSerialNumber().equals(sertifikat.getSerialNumber())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean crlPostoji()
    {
        File folder = new File(PUTANJA_DO_CRL);
        File file = new File(folder, "crl.pem");
        return file.exists();
    }
    public static X509CRLHolder getCRL() throws IOException {
        File folder = new File(PUTANJA_DO_CRL);
        File file = new File(folder, "crl.pem");

        FileInputStream fis = new FileInputStream(file);
        PEMParser pemParser = new PEMParser(new InputStreamReader(fis));
        X509CRLHolder crlHolder = (X509CRLHolder) pemParser.readObject();
        pemParser.close();
        fis.close();
        return crlHolder;
    }
}
