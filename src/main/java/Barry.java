import java.util.Scanner;
import java.util.ArrayList;

public class Barry {
    private static final String DIVIDER = "____________________________________________________________";
    private static ArrayList<Task> userList = new ArrayList<>();
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println(DIVIDER + "\nHello! I'm Barry\nWhat can I do for you?\n" + DIVIDER);
        String input = sc.nextLine();
        while (!input.equals("bye")) {
            if (input.equals("list")) {
                System.out.println(DIVIDER);
                listTasks(userList);
                System.out.println(DIVIDER);
            } else {
                Task newTask = new Task(input);
                userList.add(newTask);
                System.out.println(DIVIDER + "\nadded: " + input + "\n" + DIVIDER);
            }
            input = sc.nextLine();
        }
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }

    public static void listTasks(ArrayList<Task> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println((i+1) + ". " + taskList.get(i).toString());
        }
    }
}
