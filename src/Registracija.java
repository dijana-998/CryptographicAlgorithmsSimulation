import java.util.Scanner;
import korisnik.BazaKorisnika;
import korisnik.Korisnik;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Registracija {
    private Korisnik korisnik=new Korisnik();
    private Scanner scanner=new Scanner(System.in);
    public void registracija() {

        System.out.println("Dobrodosli u sistem registracije korisnika.");
        System.out.println("Molimo unesite sljedece podatke:");

        String korisnickoIme=citajKorisnickoIme();
        korisnik.setKorisnickoIme(korisnickoIme);

        System.out.println("Lozinka:");
        String lozinka = scanner.nextLine();
        String hashLozinke= LozinkaHash.otisakLozinke(lozinka);
        korisnik.setLozinka(hashLozinke);

        System.out.println("Ime:");
        String ime = scanner.nextLine();
        korisnik.setIme(ime);
        System.out.println("Prezime:");
        String prezime = scanner.nextLine();
        korisnik.setPrezime(prezime);
        String email = citajEmail();
        korisnik.setEmail(email);
        String telefon = citajTelefon();
        korisnik.setTelefon(telefon);
        try
        {
            KeyPair korisnickiParKljuceva=RSAKljuc.generisanjeParaKljuceva();
            X509Certificate korisnickiSertifikat=Sertifikat.kreiranjeKorisnickogSertifikata(korisnik,korisnickiParKljuceva.getPublic());
            korisnik.setKorisnickiParKljuceva(korisnickiParKljuceva);
            korisnik.setKorisnickiSertifikat(korisnickiSertifikat);
            Sertifikat.kreirajPKCS12(korisnik);
            BazaKorisnika.pisiKorisnika(korisnik);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    private String citajKorisnickoIme(){
        String korisnickoIme="";
        do{
            System.out.println("Korisnicko ime: ");
            korisnickoIme = scanner.nextLine();
        }while(!postoji(korisnickoIme));

        return korisnickoIme;
    }
    private boolean postoji(String korisnickoIme){
        if(BazaKorisnika.postojiKorisnik(korisnickoIme))
        {
                System.out.println("Korisnicko ime vec postoji.Unesite drugo.");
                return false;
        }
            return true;
        }

    private String citajTelefon() {
        String telefon = "";
        do {
            System.out.println("Telefon (9 cifara): ");
            telefon = scanner.nextLine();
        } while (!provjeriBrojTelefona(telefon));

        return telefon;
    }

    private boolean provjeriBrojTelefona(String brojTelefona) {
        if (brojTelefona.length() != 9) {
            System.out.println("Broj telefona mora sadržavati tačno 9 cifara.");
            return false;
        }

        try {
            long broj = Long.parseLong(brojTelefona);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Neispravan format broja telefona.");
            return false;
        }
    }
    private String citajEmail() {
        String email = "";
        do {
            System.out.println("Email: ");
            email = scanner.nextLine();
        } while (!provjeriEmail(email));

        return email;
    }

    private boolean provjeriEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
