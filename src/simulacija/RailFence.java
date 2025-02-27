package simulacija;

public class RailFence {

    private int broj_kolosjeka;
    private String tekst;

    public RailFence()
    {
       tekst="";
       broj_kolosjeka=1;
    }
    public RailFence(String tekst,int broj_kolosjeka)
    {
        this.broj_kolosjeka=broj_kolosjeka;
        this.tekst=tekst;
    }

    public String sifrovanje(String tekst)
    {
        char [][] matrica=gradnja_matrice(tekst);
        String sifrat=citanje_matrice(matrica);
        return sifrat;
    }

    private char[][] gradnja_matrice(String tekst)
    {
        String velikaSlova=tekst.toUpperCase();
        char[][] rezultat=new char[broj_kolosjeka][velikaSlova.length()];
        int pozicija=0;
        boolean dole=true;
        for(int i=0;i<velikaSlova.length();i++)
        {
            rezultat[pozicija][i]=velikaSlova.charAt(i);
            if (dole)
            {
                if(pozicija==broj_kolosjeka-1)
                {
                    dole=false;
                    pozicija--;
                }
                else
                {
                    pozicija++;
                }
            }
            else
            {
                if(pozicija==0)
                {
                    dole=true;
                    pozicija++;
                }
                else
                {
                    pozicija--;
                }
            }
        }
        return rezultat;
    }

    private String citanje_matrice(char[][] matrica)
    {
       StringBuilder sb=new StringBuilder();
       for(int i=0;i<matrica.length;i++)
       {
           for(int j=0;j<matrica[0].length;j++)
           {
               if(matrica[i][j]!='\0')
               {
                   sb.append(matrica[i][j]);
               }
           }
       }
       return sb.toString();
    }
}
