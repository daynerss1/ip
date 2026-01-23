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
            } else if (input.startsWith("mark") || input.startsWith("unmark")) {
                String op = input.startsWith("mark") ? "mark" : "unmark";
                String[] taskNums = input.split(" ");
                if (taskNums.length > 1) {
                    System.out.println(DIVIDER);
                    if (op.equals("mark")) {
                        for (int i = 1; i < taskNums.length; i++) {
                            int number = Integer.parseInt(taskNums[i]);
                            userList.get(number - 1).mark();
                        }
                    } else {
                        for (int i = 1; i < taskNums.length; i++) {
                            int number = Integer.parseInt(taskNums[i]);
                            userList.get(number - 1).unmark();
                        }
                    }
                    System.out.println(DIVIDER);
                }
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
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println((i + 1) + ". " + taskList.get(i).toString());
        }
    }
}
