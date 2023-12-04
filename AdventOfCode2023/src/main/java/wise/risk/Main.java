package wise.risk;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean continueAof = true;
        while (continueAof) {
            System.out.println("For which day do you want the result ?");
            Scanner sc = new Scanner(System.in);
            try {
                switch (sc.nextInt()) {
                    case 1:
                        Day1.answer();
                        break;
                    case 2:
                        Day2.answer();
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                        throw new UnsupportedOperationException();
                    default:
                        continueAof = false;
                        break;
                }
            } catch (UnsupportedOperationException e) {
                System.err.println("This day is not ready");
            } catch (URISyntaxException | IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}