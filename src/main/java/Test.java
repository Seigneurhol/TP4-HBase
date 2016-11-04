import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by Seigneurhol on 04/11/2016.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean continuer = true;
        System.out.println("Please write your name :");
        String name = sc.nextLine();
        System.out.println("Please write your firstName :");
        String firstName = sc.nextLine();
        System.out.println("Please write your gender (m or f) :");
        String gender = sc.nextLine();
        System.out.println("Please write your BFF name:");
        String BFF = sc.nextLine();
        ArrayList<String> friends = new ArrayList<String>();
        while(continuer) {
            System.out.println("Please write your friend name :");
            friends.add(sc.nextLine());
            System.out.println("Would you like to add more friend ? (Y or N) :");
            String choice = sc.nextLine();
            if (Objects.equals(choice, "N") || Objects.equals(choice, "n")) {
                continuer = false;
            }
        }
    }
}
