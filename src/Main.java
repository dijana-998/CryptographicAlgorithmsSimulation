import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Registracija registracija = new Registracija();
        int odgovor = -1;
        while (odgovor != 0) {
            System.out.println("=================");
            System.out.println("1.Registracija");
            System.out.println("2.Login");
            System.out.println("0.Kraj");
            System.out.println("=================");

            if (scanner.hasNextInt()) {
                odgovor = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Pogresan unos. Pokusajte ponovo!");
                scanner.nextLine();
                continue;
            }

            switch (odgovor) {
                case 1:
                    registracija.registracija();
                    System.out.println("Uspjesna registracija");
                    break;
                case 2:
                    Login log = new Login();
                    log.login();
                    break;
                case 0:
                    System.out.println("Kraj");
                    break;
                default:
                    System.out.println("Pogresan unos. Pokusajte ponovo!");
                    break;
            }
        }
    }
}