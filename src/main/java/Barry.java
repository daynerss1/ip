import java.util.Scanner;
import java.util.ArrayList;

public class Barry {
    private static final String DIVIDER = "____________________________________________________________";

    public static void main(String[] args) {
        ArrayList<Task> userList = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.println(DIVIDER + "\nHello! I'm Barry\nWhat can I do for you?\n" + DIVIDER);
        String input = sc.nextLine().trim();
        while (!input.equals("bye")) {
            parseInput(input, userList);
            input = sc.nextLine();
        }
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }

    public static void parseInput(String input, ArrayList<Task> userList) {
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
        } else if (input.startsWith("todo ")) {
            String name = input.substring(5);
            ToDo newToDo = new ToDo(name);
            userList.add(newToDo);
            printAddedMessage(newToDo, userList.size());
        } else if (input.startsWith("deadline ")) {
            String[] parts = input.substring(9).split("/by", 2);
            String name = parts[0].trim();
            String by = parts[1].trim();
            Deadline newDeadline = new Deadline(name, by);
            userList.add(newDeadline);
            printAddedMessage(newDeadline, userList.size());
        } else if (input.startsWith("event ")) {
            String[] parts = input.substring(6).split("/from", 2); // This is name + date/time
            String name = parts[0].trim();
            String[] times = parts[1].split("/to", 2);
            String start = times[0].trim();
            String end = times[1].trim();
            Event newEvent = new Event(name, start, end);
            userList.add(newEvent);
            printAddedMessage(newEvent, userList.size());
        } else {
            System.out.println(DIVIDER + "\nInvalid format: To add a new task, start the command with 'todo', 'deadline', or 'event'\n" + DIVIDER);
        }
    }

    public static void listTasks(ArrayList<Task> taskList) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println((i + 1) + ". " + taskList.get(i).toString());
        }
    }

    public static void printAddedMessage(Task task, int size) {
        System.out.println(DIVIDER + "\nGot it. I've added this task:\n" + task.toString());
        System.out.println("Now you have " + size + " tasks in the list.\n" + DIVIDER);
    }
}
