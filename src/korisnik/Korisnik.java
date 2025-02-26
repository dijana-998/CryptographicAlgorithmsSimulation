package korisnik;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
public class Korisnik {

    private String korisnickoIme;
    private String lozinka;
    private String ime;
    private String prezime;
    private String email;
    private String telefon;

    private X509Certificate korisnickiSertifikat = null;

    private KeyPair korisnickiParKljuceva = null;


    public Korisnik() {
        this.korisnickoIme = "";
        this.lozinka = "";
        this.ime = "";
        this.prezime = "";
        this.email = "";
        this.telefon = "";
    }

    public Korisnik(String korisnickoIme, String lozinka, String ime, String prezime, String email, String telefon) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.telefon = telefon;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setKorisnickiSertifikat(X509Certificate korisnickiSertifikat) {
        this.korisnickiSertifikat = korisnickiSertifikat;
    }

    public void setKorisnickiParKljuceva(KeyPair korisnickiParKljuceva) {
        this.korisnickiParKljuceva = korisnickiParKljuceva;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefon() {
        return telefon;
    }

    public X509Certificate getKorisnickiSertifikat() {
        return korisnickiSertifikat;
    }

    public KeyPair getKorisnickiParKljuceva()
    {
        return korisnickiParKljuceva;
    }

}

