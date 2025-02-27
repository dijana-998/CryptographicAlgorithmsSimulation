package simulacija;

import enkripcija.Enkripcija;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulacija {

    private static final String PUTANJA_DO_SIMULACIJE = "src\\simulacije\\";

    public static void simulacija(String korisnickoIme, PrivateKey privatniKljuc, PublicKey javniKljuc) {

        File korisnikFolder = new File(PUTANJA_DO_SIMULACIJE + korisnickoIme);
        if (!korisnikFolder.exists()) {
            korisnikFolder.mkdirs();
        }
        Scanner scanner = new Scanner(System.in);
        List<String> ucitano = dekriptujIverifikuj(korisnickoIme, javniKljuc);
        if (ucitano == null) {
            System.out.println("Doslo je do greske,potpis nije validan.");
        } else
        {
            boolean promjena = false;
            int odabir = -1;
            while (odabir != 0) {
                System.out.println("Odaberite opciju:");
                System.out.println("1.Ispis prethodnih simulacija:");
                System.out.println("2.Dodaj novu simulaciju:");
                System.out.println("0.Kraj");
                odabir = scanner.nextInt();
                scanner.nextLine();
                switch (odabir) {
                    case 1:
                        if (ucitano.isEmpty()) {
                            System.out.println("Nema simulacija");
                            break;
                        } else {
                            System.out.println("Simulacije:");
                            for (String sim : ucitano) {
                                System.out.println(sim);
                            }
                        }
                        break;
                    case 2:
                        int odabir_alg = -1;
                        while (odabir_alg != 0) {
                            System.out.println("Odaberite algoritam:");
                            System.out.println("1.Myszkowski");
                            System.out.println("2.RailFence");
                            System.out.println("3.Playfair");
                            System.out.println("0.Kraj");
                            odabir_alg = scanner.nextInt();
                            scanner.nextLine();
                            switch (odabir_alg) {
                                case 1:
                                    System.out.println("Unesite tekst:");
                                    String saRazmakom = scanner.nextLine();
                                    String tekst = saRazmakom.replaceAll("\\s", "");
                                    if (tekst.length() <= 100) {
                                        System.out.println("Unesite ključ (samo slova):");
                                        String kljucsaRazmakom = scanner.nextLine();
                                        String bezRazmaka = kljucsaRazmakom.replaceAll("\\s", "").toUpperCase();
                                        String kljuc = bezRazmaka;
                                        Myszowski mysz = new Myszowski(tekst, kljuc);
                                        String str1 = mysz.sifrovanje(tekst);
                                        System.out.println("Sifrat:" + str1);
                                        ucitano.add(pripremiSimulaciju("Myszkowski", kljuc, 0, tekst, str1));
                                        promjena = true;
                                    } else {
                                        System.out.println("Duzina teksta je veca od 100 karaktera.Ponovite proces");

                                    }
                                    break;
                                case 2:
                                    System.out.println("Unesite tekst:");
                                    String saRazmakom2 = scanner.nextLine();
                                    String tekst2 = saRazmakom2.replaceAll("\\s", "");
                                    if (tekst2.length() <= 100)
                                    {
                                        boolean validanUnos = false;
                                        int broj_kolosjeka = 0;
                                        while (!validanUnos)
                                        {
                                            System.out.println("Unesite broj kolosjeka:");
                                            try {
                                                broj_kolosjeka = Integer.parseInt(scanner.nextLine().trim());
                                                if (broj_kolosjeka <= 0) {
                                                    System.out.println("Broj kolosjeka mora biti pozitivan broj.");
                                                } else {
                                                    validanUnos = true;
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Neispravan unos broja kolosjeka. Unesite ponovo.");
                                            }
                                        }
                                        RailFence rf = new RailFence(tekst2, broj_kolosjeka);
                                        String str2 = rf.sifrovanje(tekst2);
                                        System.out.println("Sifrat:" + str2);
                                        ucitano.add(pripremiSimulaciju("RailFence", "", broj_kolosjeka, tekst2, str2));
                                        promjena = true;
                                    } else {
                                        System.out.println("Duzina teksta je veca od 100 karaktera.Ponovite proces");
                                    }
                                    break;
                                case 3:
                                    System.out.println("Unesite tekst:");
                                    String saRazmakom3 = scanner.nextLine();
                                    String tekst3 = saRazmakom3.replaceAll("\\s", "").toUpperCase();
                                    if (tekst3.length() <= 100) {
                                        System.out.println("Unesite kljuc:");
                                        String kljucsaRazmakom = scanner.nextLine();
                                        String bezRazmaka = kljucsaRazmakom.replaceAll("\\s", "").toUpperCase();
                                        String kljuc3 = bezRazmaka;
                                        Playfair playfair = new Playfair(kljuc3);
                                        String sifrat = playfair.sifrovanje(tekst3);
                                        System.out.println("Šifrat:");
                                        System.out.println(sifrat);
                                        ucitano.add(pripremiSimulaciju("PlayFair", kljuc3, 0, tekst3, sifrat));
                                        promjena = true;
                                    } else {
                                        System.out.println("Duzina teksta je veca od 100 karaktera.Ponovite proces");
                                    }
                                    break;
                                case 0:
                                    System.out.println("Kraj");
                                    break;
                                default:
                                    System.out.println("Pogresan unos.Pokusajte ponovo");
                            }

                        }
                        break;
                    case 0:
                        if (promjena) {
                            potpisiIEnkriptuj(ucitano, korisnickoIme, privatniKljuc);
                        } else {
                            System.out.println("Kraj");
                        }
                        break;
                    default:
                        System.out.println("Pogresan unos pokusajte ponovo.");
                }

            }
        }
    }
    private static String pripremiSimulaciju(String algoritam, String kljuc, int brojKolosjeka, String tekst, String sifrat) {
        if (brojKolosjeka == 0) {
            return tekst.toUpperCase() + "|" + algoritam.toUpperCase() + "|" + kljuc.toUpperCase() + "|" + sifrat.toUpperCase() + "\n";
        } else {
            return tekst.toUpperCase() + "|" + algoritam.toUpperCase() + "|" + brojKolosjeka + "|" + sifrat.toUpperCase() + "\n";
        }
    }
    private static void potpisiIEnkriptuj(List<String> simulacije,String korisnickoIme,PrivateKey privatniKljuc)
    {
        try {
            byte[] bajtovi = listaUBajtove(simulacije);
            byte[] potpis = Enkripcija.potpisiFajl(privatniKljuc, bajtovi);
            System.out.println("Fajl je potpisan.");
            Enkripcija.cuvajPotpis(potpis, korisnickoIme);
            byte[] enkripcija=Enkripcija.enkripcijaFajla(bajtovi);
            Enkripcija.cuvajSimulaciju(enkripcija,korisnickoIme);
        }catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Greska prilikom potpisivanja i enkripcije!");
        }
    }

    private static List<String> dekriptujIverifikuj(String korisnickoIme, PublicKey javniKljuc) {
        String putanjaDoSimulacije = PUTANJA_DO_SIMULACIJE + korisnickoIme + File.separator + "simulacija.txt";
        String putanjaDoPotpisa = PUTANJA_DO_SIMULACIJE + korisnickoIme + File.separator + "potpis.txt";
        File simulacijaa = new File(putanjaDoSimulacije);
        File potpiss = new File(putanjaDoPotpisa);
        List<String> praznaLista = new ArrayList<>(0);
        if (simulacijaa.exists() && potpiss.exists()) {
            byte[] simulacija = Enkripcija.getSimulacija(korisnickoIme);
            byte[] dekriptovano = Enkripcija.dekripcijaFajla(simulacija);
            byte[] potpis = Enkripcija.getPotpis(korisnickoIme);

            if (Enkripcija.verifikacijaPotpisa(javniKljuc, dekriptovano, potpis)) {
                System.out.println("Verifikacija uspjesna!");
                try {
                    List<String> dek = bajtoviUListu(dekriptovano);
                    return dek;
                }catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Greska kod ucitavanja simulacija");
                    return null;
                }
            }
            else
            {
                System.out.println("Greska prilikom verifikacije.Doslo je do izmjena!");
                return null;
            }
        }
        else
        {
            System.out.println("Ne postoji simulacija.txt ili potpis.txt.");
            return praznaLista;
        }
    }
    public static byte[] listaUBajtove(List<String> lista) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)) {
            dos.writeInt(lista.size());
            for (String element : lista) {
                dos.writeUTF(element);
            }
            return bos.toByteArray();
        }
    }
    public static List<String> bajtoviUListu(byte[] byteArray) throws IOException {
        List<String> lista = new ArrayList<>();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
             DataInputStream dis = new DataInputStream(bis)) {
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                String element = dis.readUTF();
                lista.add(element);
            }
        }
        return lista;
    }
}
