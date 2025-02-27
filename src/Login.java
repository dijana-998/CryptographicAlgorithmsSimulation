import korisnik.BazaKorisnika;
import korisnik.Korisnik;
import simulacija.Simulacija;
import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class Login {
    Scanner scanner=new Scanner(System.in);
    private static final String PUTANJA_DO_PKCS12="src\\PKCS12";
    public void login(){
        try {
            String imePKCS12 = "";
            do {
                System.out.println("Unesite samo naziv PKCS12 datoteke kojoj zelite pristupiti(potrebno dodati .p12):");
                imePKCS12 = scanner.nextLine();
                String punaPutanja = PUTANJA_DO_PKCS12 + File.separator + imePKCS12;
                File fajl = new File(punaPutanja);
                if (fajl.exists())
                {
                    System.out.println("Putanja postoji.");
                    System.out.println("Putanja do sertifikata i kljuca:"+punaPutanja);
                    String lozinkaPKCS12 = "";
                    do
                    {
                        System.out.println("Unesite lozinku za pristup PKCS12 datoteci");
                        lozinkaPKCS12 = scanner.nextLine();
                    }
                    while (lozinkaPKCS12.isEmpty());
                    if (provjeriLozinkuPKCS12(punaPutanja,lozinkaPKCS12)) {
                        System.out.println("Citanje iz PKCS12");
                        KeyStore keyStore = Sertifikat.citajPKCS12(punaPutanja, lozinkaPKCS12);
                        X509Certificate sertifikat = Sertifikat.getKorisnickiSertifikatIzPKCS12(keyStore);
                        if(Sertifikat.validnostSertifikata(sertifikat))
                        {
                            System.out.println("Sertifikat je validan.");
                            if(!Sertifikat.povucen(sertifikat))
                            {
                                System.out.println("Sertifikat nije suspendovan");
                                PrivateKey privatniKljuc = Sertifikat.getPrivatniKljuc(keyStore, lozinkaPKCS12);
                                PublicKey javniKljuc = sertifikat.getPublicKey();
                                String korisnickoIme = "";
                                do {
                                    System.out.println("Unesite korisnicko ime:");
                                    korisnickoIme = scanner.nextLine();
                                }
                                while (korisnickoIme.isEmpty());
                                String lozinka = "";
                                do {
                                    System.out.println("Unesite lozinku:");
                                    lozinka = scanner.nextLine();
                                }
                                while (lozinka.isEmpty());

                                if (provjeriKorisnika(korisnickoIme, lozinka)) {
                                    System.out.println("Uspjesno ste se prijavili.");
                                    Simulacija.simulacija(korisnickoIme, privatniKljuc, javniKljuc);
                                    break;
                                } else {
                                    System.out.println("Neuspjesna prijava.Pogresno korisnicko ime ili lozinka");
                                }
                            }
                            else
                            {
                                System.out.println("Sertifikat je povucen");
                                break;
                            }
                        }
                        else
                        {
                            System.out.println("Provjera sertifikata je izvrsena,on nije validan.");
                            break;
                        }
                    }
                    else
                    {
                        System.out.println("Pogresna lozinka pkcs12.");
                    }
                }
                else
                {
                    System.out.println("Putanja ne postoji.");
                    break;
                }
            } while (true);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean provjeriKorisnika(String korisnickoIme,String lozinka)
    {
        Korisnik korisnik=BazaKorisnika.getKorisnik(korisnickoIme);
        if(BazaKorisnika.postojiKorisnik(korisnik.getKorisnickoIme()))
        {
            return LozinkaHash.provjeriLozinku(korisnik,lozinka);
        }
        return false;
    }

    private boolean provjeriLozinkuPKCS12(String putanjaDoPKCS12,String lozinka)
    {
        try {
            KeyStore keyStore = Sertifikat.citajPKCS12(putanjaDoPKCS12, lozinka);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }
}
