package korisnik;

import java.io.*;
import java.util.ArrayList;

public class BazaKorisnika {

    private static final String PUTANJA_DO_KORISNIKA="src\\Korisnici";

    public static void pisiKorisnika(Korisnik korisnik)
    {
        try
        {
            BufferedWriter pisi=new BufferedWriter(new FileWriter(PUTANJA_DO_KORISNIKA,true));
            pisi.write(korisnik.getKorisnickoIme() + "," + korisnik.getLozinka() + "," + korisnik.getIme() + "," + korisnik.getPrezime() + "," + korisnik.getEmail() + "," + korisnik.getTelefon());
            pisi.newLine();
            pisi.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<Korisnik> citajKorisnike()
    {
        ArrayList<Korisnik> korisnici=new ArrayList<>();
        try {
            BufferedReader citaj= new BufferedReader(new FileReader(PUTANJA_DO_KORISNIKA));
            String linija;
            while((linija= citaj.readLine())!=null)
            {
                String [] podaci=linija.split(",");
                Korisnik korisnik=new Korisnik(podaci[0],podaci[1],podaci[2],podaci[3],podaci[4],podaci[5]);
                korisnici.add(korisnik);
            }
            citaj.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return korisnici;
    }

    public static Korisnik getKorisnik(String korisnickoIme)
    {
        Korisnik korisnici=new Korisnik();
        try
        {
            BufferedReader citaj=new BufferedReader(new FileReader(PUTANJA_DO_KORISNIKA));
            String linija;
            while((linija=citaj.readLine())!=null)
            {
                String [] podaci=linija.split(",");
                Korisnik korisnik=new Korisnik(podaci[0],podaci[1],podaci[2],podaci[3],podaci[4],podaci[5]);
                if(korisnik.getKorisnickoIme().equals(korisnickoIme))
                {
                    korisnici=korisnik;
                    break;
                }
            }
            citaj.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return korisnici;
    }

    public static boolean postojiKorisnik(String korisnickoIme)
    {
        ArrayList<Korisnik> korisnici=citajKorisnike();
        for(Korisnik korisnik:korisnici)
        {
            if(korisnik.getKorisnickoIme().equals(korisnickoIme))
            {
                return true;
            }
        }
        return false;
    }

}
