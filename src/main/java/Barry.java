import java.util.Scanner;

public class Barry {
    private static final String DIVIDER = "____________________________________________________________";
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println(DIVIDER + "\nHello! I'm Barry\nWhat can I do for you?\n" + DIVIDER);
        String input = sc.nextLine();
        while (!input.equals("bye")) {
            System.out.println(DIVIDER + "\n" + input + "\n" + DIVIDER);
            input = sc.nextLine();
        }
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }
}
