package simulacija;
import java.util.Arrays;

public class Myszowski
{
    private String tekst;
    private String kljuc;

    public Myszowski()
    {
        tekst="";
        kljuc="";
    }
    public Myszowski(String tekst,String kljuc)
    {
        this.tekst=tekst;
        this.kljuc=kljuc;
    }


    public String sifrovanje(String tekst)
    {
        if (tekst == null || tekst.trim().isEmpty()) {
            return "";
        }

        if (kljuc == null || kljuc.trim().isEmpty()) {
            return "";
        }

        int[] redosljed_kolona=redosljed(kljuc);
        char[][] blok=gradnjaBloka(tekst,-1,kljuc.length(),0);
        char[][] trans_blok=transp_blok(blok,redosljed_kolona);
        String sifrat=citaj_blok(trans_blok);

        return sifrat;
    }
    public static int[] redosljed(String kljuc)
    {
        if (kljuc == null || kljuc.trim().isEmpty()) {
            return new int[]{};
        }
        String velikaSlovaKljuc=kljuc.toUpperCase();
        char[] nizSlova=velikaSlovaKljuc.toCharArray();
        Arrays.sort(nizSlova);
        int[] rezultat=new int[kljuc.length()];
        int trenutniIndeks=1;
        int odIndeksa=0;

        for(int i=0;i<kljuc.length();i++)
        {
            if( i==0 || nizSlova[i]==nizSlova[i-1])
            {
                int pozicija=velikaSlovaKljuc.indexOf(nizSlova[i],odIndeksa);
                odIndeksa=pozicija+1;
                rezultat[pozicija]=trenutniIndeks;
            }
            else
            {
                trenutniIndeks++;
                odIndeksa=0;
                int pozicija=velikaSlovaKljuc.indexOf(nizSlova[i],odIndeksa);
                odIndeksa=pozicija+1;
                rezultat[pozicija]=trenutniIndeks;
            }
        }
        return rezultat;
    }

    private char[][] transp_blok(char[][] blok,int[] broj)
    {
        if (blok == null || blok.length == 0 || blok[0].length == 0) {
            return new char[][]{{}};
        }

        if (broj == null || broj.length == 0) {
            return blok;
        }
        int red=blok.length;
        int kolona=blok[0].length;
        char[][] rezultat=new char[red+1][kolona];
        int trenutni_broj=1;
        int trenutna_kolona=0;
        int nadi_poziciju=0;
        while(trenutna_kolona<kolona)
        {
            int poz= pronalazenje_indeksa(broj,trenutni_broj,nadi_poziciju);
            if(poz==-1)
            {
                trenutni_broj++;
                nadi_poziciju=0;
            }
            else
            {
                rezultat[0][trenutna_kolona]=(char)('0'+broj[poz]);
                for(int i=0;i<red;i++)
                {
                    rezultat[i+1][trenutna_kolona]=blok[i][poz];
                }
                trenutna_kolona++;
                nadi_poziciju=poz+1;
            }
        }
        return rezultat;
    }

    private String citaj_blok(char[][] trans_blok)
    {
        if (trans_blok == null || trans_blok.length == 0 || trans_blok[0].length == 0) {
            return "";
        }

        int start_poz=0;
        int krajnja_poz=0;
        int kol=trans_blok[0].length;
        StringBuilder sb= new StringBuilder();
        while(krajnja_poz<kol)
        {
            if(trans_blok[0][krajnja_poz]==trans_blok[0][start_poz])
            {
                krajnja_poz++;
            }
            else
            {
                sb.append(citaj_kolonu(trans_blok,start_poz,krajnja_poz-1));
                start_poz=krajnja_poz;
            }
        }
        sb.append(citaj_kolonu(trans_blok,start_poz,krajnja_poz-1));
        return sb.toString();
    }

    private String citaj_kolonu(char[][] trans_blok,int start_poz,int krajnja_poz)
    {
        if(start_poz>krajnja_poz)
            return "";
        StringBuilder sb=new StringBuilder();
        int red=trans_blok.length;
        int kol=trans_blok[0].length;
        assert(krajnja_poz<kol);
        for(int i=1;i<red;i++)
        {
            for(int j=start_poz;j<=krajnja_poz;j++)
            {
                if(trans_blok[i][j]!='\0')
                {
                    sb.append(trans_blok[i][j]);
                }
            }
        }
        return sb.toString();
    }

    public static char[][] gradnjaBloka(String tekst,int red,int duzinaKljuca,int direkcija)
    {
        if (tekst == null || tekst.trim().isEmpty()) {
            return new char[][]{{}};
        }
        String velikaSlovaTekst=tekst.toUpperCase();
        if(red==-1)
        {
            red=velikaSlovaTekst.length()/duzinaKljuca;
        }
        if(velikaSlovaTekst.length() % duzinaKljuca !=0)
        {
            red++;
        }
        char[][] rezultat=new char[red][duzinaKljuca];
        if(direkcija==1)
        {
            int trenutni_red=0;
            int trenutna_kolona=0;
            for(int i=0;i<velikaSlovaTekst.length();i++)
            {
                rezultat[trenutni_red][trenutna_kolona]=velikaSlovaTekst.charAt(i);
                if(trenutni_red==red-1)
                {
                    trenutni_red=0;
                    trenutna_kolona++;
                }
                else
                {
                    trenutni_red++;
                }
            }
        }
        else
        {
            int trenutni_red=0;
            int trenutna_kolona=0;
            for(int i=0;i<velikaSlovaTekst.length();i++)
            {
                rezultat[trenutni_red][trenutna_kolona]=velikaSlovaTekst.charAt(i);
                if(trenutna_kolona==duzinaKljuca-1)
                {
                    trenutni_red++;
                    trenutna_kolona=0;
                }
                else
                {
                    trenutna_kolona++;
                }
            }
        }
        return rezultat;
    }
    public static int pronalazenje_indeksa(int[] niz,int broj,int pocetni_indeks)
    {
        for(int i=pocetni_indeks;i<niz.length;i++)
        {
            if(niz[i]==broj)
                return i;
        }
        return -1;
    }
}
