package simulacija;

public class Playfair {
    private final char[][] kljucMatrica;

    public Playfair()
    {
      kljucMatrica = new char[][]{{}};;
    }
    public Playfair(String kljuc) {
        if (kljuc != null && !kljuc.trim().isEmpty()) {
            kljucMatrica = kljuc_matrica(kljuc);
        } else {
            kljucMatrica = new char[][]{{}};
        }
    }

    private char[][] kljuc_matrica(String kljuc) {
        String kljucZamjena=kljuc.replace("J","I");
        String paddedKljuc = kljucZamjena + "ABCDEFGHIKLMNOPQRSTUVWXYZ";
        char[][] matrica = new char[5][5];
        boolean[] koristenaSlova = new boolean[26];

        int red = 0, kolona = 0;
        for (char c : paddedKljuc.toCharArray()) {
            if (!koristenaSlova[c - 'A']) {
                matrica[red][kolona] = c;
                koristenaSlova[c - 'A'] = true;
                kolona++;
                if (kolona == 5) {
                    kolona = 0;
                    red++;
                }
            }
        }

        return matrica;
    }

    private String[] pripremaParova(String tekst) {
        tekst = tekst.replaceAll("J", "I");
        if (tekst.length() % 2 != 0)
            tekst += "X";
        String[] parovi = new String[tekst.length() / 2];
        for (int i = 0, j = 0; i < tekst.length(); i += 2, j++) {
            parovi[j] = tekst.substring(i, i + 2);
        }
        return parovi;
    }

    private String enkripcijaParova(String parovi) {
        char prvoSlovo = parovi.charAt(0);
        char drugoSlovo = parovi.charAt(1);
        int[] poz1 = nadiPoziciju(prvoSlovo);
        int[] poz2 = nadiPoziciju(drugoSlovo);

        if (poz1[0] == poz2[0]) {
            return "" + kljucMatrica[poz1[0]][(poz1[1] + 1) % 5] + kljucMatrica[poz2[0]][(poz2[1] + 1) % 5];
        } else if (poz1[1] == poz2[1]) {
            return "" + kljucMatrica[(poz1[0] + 1) % 5][poz1[1]] + kljucMatrica[(poz2[0] + 1) % 5][poz2[1]];
        } else {
            return "" + kljucMatrica[poz1[0]][poz2[1]] + kljucMatrica[poz2[0]][poz1[1]];
        }
    }

    private int[] nadiPoziciju(char c) {
        int[] poz = new int[2];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (kljucMatrica[i][j] == c) {
                    poz[0] = i;
                    poz[1] = j;
                    return poz;
                }
            }
        }
        return poz;
    }

    public String sifrovanje(String tekst) {
        StringBuilder sifrat = new StringBuilder();
        String[] parovi = pripremaParova(tekst);
        for (String par : parovi ) {
            sifrat.append(enkripcijaParova(par));
        }
        return sifrat.toString();
    }
}