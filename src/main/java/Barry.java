import java.util.Scanner;
import java.util.ArrayList;

public class Barry {
    private static final String DIVIDER = "____________________________________________________________";
    public static void main(String[] args) {
        ArrayList<String> userList = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.println(DIVIDER + "\nHello! I'm Barry\nWhat can I do for you?\n" + DIVIDER);
        String input = sc.nextLine();
        while (!input.equals("bye")) {
            if (input.equals("list")) {
                System.out.println(DIVIDER);
                for (int i = 0; i < userList.size(); i++) {
                    System.out.println((i+1) + ". " + userList.get(i));
                }
                System.out.println(DIVIDER);
            } else {
                userList.add(input);
                System.out.println(DIVIDER + "\nadded: " + input + "\n" + DIVIDER);
            }
            input = sc.nextLine();
        }
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }
}
