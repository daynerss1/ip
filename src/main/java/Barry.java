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
            try {
                parseInput(input, userList);
            } catch (BarryException e) {
                printErrorMessage(e.getMessage());
            }
            input = sc.nextLine();
        }
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }

    public static void parseInput(String input, ArrayList<Task> userList) throws BarryException {
        if (input.equals("list")) {
            listTasks(userList);
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
            } else {
                throw new BarryException("You need to specify 1 or more tasks to " + op + ".");
            }
        } else if (input.startsWith("delete")) {
            String[] taskNums = input.split(" ");
            if (taskNums.length > 1) {
                System.out.println(DIVIDER);
                for (int i = 1; i < taskNums.length; i++) {
                    int number = Integer.parseInt(taskNums[i]);
                    Task removedTask = userList.get(number - 1);
                    userList.remove(number - 1);
                    System.out.println("Noted. I've removed this task.");
                    System.out.println(removedTask.toString());
                }
                System.out.println("Now you have " + userList.size() + " tasks in the list.\n" + DIVIDER);
            } else {
                throw new BarryException("You need to specify 1 or more tasks to delete.");
            }
        } else if (input.startsWith("todo ")) {
            String name = input.substring(5);
            ToDo newToDo = new ToDo(name);
            userList.add(newToDo);
            printAddedMessage(newToDo, userList.size());
        } else if (input.startsWith("deadline ")) {
            String[] parts = input.substring(9).split("/by", 2);
            if (parts.length == 1) {
                throw new BarryException("You need to input a date for the deadline of this task!");
            }
            String name = parts[0].trim();
            String by = parts[1].trim();
            Deadline newDeadline = new Deadline(name, by);
            userList.add(newDeadline);
            printAddedMessage(newDeadline, userList.size());
        } else if (input.startsWith("event ")) {
            String[] parts = input.substring(6).split("/from", 2); // This is name + date/time
            if (parts.length == 1) {
                throw new BarryException("An event needs a starting time! Specify one by typing '/by <date/time>");
            }
            String name = parts[0].trim();
            String[] times = parts[1].split("/to", 2);
            if (times.length == 1) {
                throw new BarryException("An event needs an end time! Specify one by typing '/to <date/time>");
            }
            String start = times[0].trim();
            String end = times[1].trim();
            Event newEvent = new Event(name, start, end);
            userList.add(newEvent);
            printAddedMessage(newEvent, userList.size());
        } else if (input.equals("event") || input.equals("download") || input.equals("todo")) {
            String classifier = input.equals("event") ? "an ": "a ";
            throw new BarryException("Oops! The description of " + classifier + input + " cannot be empty.");
        }
        else {
            throw new BarryException("Invalid command: To add a new task, start the command with 'todo', 'deadline', or 'event'");
        }
    }

    public static void listTasks(ArrayList<Task> taskList) {
        System.out.println(DIVIDER);
        if (taskList.size() == 0) {
            System.out.println("Your task list is currently empty.\n" + DIVIDER);
            return;
        }
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println((i + 1) + ". " + taskList.get(i).toString());
        }
        System.out.println(DIVIDER);
    }

    public static void printAddedMessage(Task task, int size) {
        System.out.println(DIVIDER + "\nGot it. I've added this task:\n" + task.toString());
        System.out.println("Now you have " + size + " tasks in the list.\n" + DIVIDER);
    }

    public static void printErrorMessage(String msg) {
        System.out.println(DIVIDER + "\n" + msg + "\n" + DIVIDER);
    }
}
