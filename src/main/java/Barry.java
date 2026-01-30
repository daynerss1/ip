import java.util.Scanner;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Barry {
    private static final String DIVIDER = "____________________________________________________________";
    private static final DateTimeFormatter IN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public static void main(String[] args) {
        Storage storage = new Storage("./data/barry.txt");
        ArrayList<Task> userList;
        try {
            userList = storage.load();
        } catch (BarryException e) {
            userList = new ArrayList<>();
            printErrorMessage("Saved data was corrupted. Starting a new file. " + e.getMessage());
        }
        Scanner sc = new Scanner(System.in);
        System.out.println(DIVIDER + "\nHello! I'm Barry\nWhat can I do for you?\n" + DIVIDER);
        String input = sc.nextLine().trim();
        while (!input.equals("bye")) {
            try {
                parseInput(input, userList, storage);
            } catch (BarryException e) {
                printErrorMessage(e.getMessage());
            }
            input = sc.nextLine().trim();
        }
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }

    public static Command getCommandType(String input) throws BarryException {
        if (input.isEmpty()) {
            throw new BarryException("Input command cannot be empty.");
        }
        String firstWord = input.split(" ")[0].toLowerCase();
        switch (firstWord) {
        case "list":
            return Command.LIST;
        case "todo":
            return Command.TODO;
        case "deadline":
            return Command.DEADLINE;
        case "event":
            return Command.EVENT;
        case "mark":
            return Command.MARK;
        case "unmark":
            return Command.UNMARK;
        case "delete":
            return Command.DELETE;
        case "bye":
            return Command.BYE;
        default:
            throw new BarryException("Invalid command: To add a new task, start the command with 'todo', 'deadline', or 'event'");
        }
    }

    public static void parseInput(String input, ArrayList<Task> userList, Storage storage) throws BarryException {
        Command command = getCommandType(input);
        switch (command) {
        case LIST: {
            listTasks(userList);
            break;
        }
        case TODO: {
            handleTodo(input, userList, storage);
            break;
        }
        case DEADLINE: {
            handleDeadline(input, userList, storage);
            break;
        }
        case EVENT: {
            handleEvent(input, userList, storage);
            break;
        }
        case DELETE: {
            handleDelete(input, userList, storage);
            break;
        }
        case MARK: {
            handleMark(input, userList, storage);
            break;
        }
        case UNMARK: {
            handleUnmark(input, userList, storage);
            break;
        }
        case BYE: {
            break;
        }
        }
    }

    public static void handleMark(String input, ArrayList<Task> userList, Storage storage) throws BarryException {
        String[] taskNums = input.split(" ");
        if (taskNums.length > 1) {
            System.out.println(DIVIDER);
            for (int i = 1; i < taskNums.length; i++) {
                int number = Integer.parseInt(taskNums[i]);
                userList.get(number - 1).mark();
            }
            storage.save(userList);
            System.out.println(DIVIDER);
        } else {
            throw new BarryException("You need to specify 1 or more tasks to mark.");
        }
    }

    public static void handleUnmark(String input, ArrayList<Task> userList, Storage storage) throws BarryException {
        String[] taskNums = input.split(" ");
        if (taskNums.length > 1) {
            System.out.println(DIVIDER);
            for (int i = 1; i < taskNums.length; i++) {
                int number = Integer.parseInt(taskNums[i]);
                userList.get(number - 1).unmark();
            }
            storage.save(userList);
            System.out.println(DIVIDER);
        } else {
            throw new BarryException("You need to specify 1 or more tasks to unmark.");
        }
    }

    public static void handleDelete(String input, ArrayList<Task> userList, Storage storage) throws BarryException {
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
            storage.save(userList);
            System.out.println("Now you have " + userList.size() + " tasks in the list.\n" + DIVIDER);
        } else {
            throw new BarryException("You need to specify 1 or more tasks to delete.");
        }
    }

    public static void handleTodo(String input, ArrayList<Task> userList, Storage storage) throws BarryException {
        if (input.trim().toLowerCase().equals("todo")) {
            throw new BarryException("Oops! The description of a ToDo cannot be empty.");
        } else {
            String name = input.substring(5);
            ToDo newToDo = new ToDo(name);
            userList.add(newToDo);
            printAddedMessage(newToDo, userList.size());
            storage.save(userList);
        }
    }

    public static void handleDeadline(String input, ArrayList<Task> userList, Storage storage) throws BarryException {
        if (input.trim().toLowerCase().equals("deadline")) {
            throw new BarryException("Oops! The description of a Deadline cannot be empty.");
        }
        String[] parts = input.substring(9).split("/by", 2);
        if (parts.length == 1) {
            throw new BarryException("You need to input a date for the deadline of this task! "
                    + "Specify one by appending '/by yyyy-MM-dd HHmm' to the Deadline.");
        }
        String name = parts[0].trim();
        if (name.isEmpty()) {
            throw new BarryException("Oops! The description of a Deadline cannot be empty.");
        }
        String byString = parts[1].trim();
        if (byString.isEmpty()) {
            throw new BarryException("The Deadline's date/time cannot be empty."
                    + "Specify one by appending '/by yyyy-MM-dd HHmm' to the Deadline.");
        }

        LocalDateTime by = parseDateTime(byString);
        Deadline newDeadline = new Deadline(name, by);
        userList.add(newDeadline);
        printAddedMessage(newDeadline, userList.size());
        storage.save(userList);
    }

    public static void handleEvent(String input, ArrayList<Task> userList, Storage storage) throws BarryException {
        if (input.trim().toLowerCase().equals("event")) {
            throw new BarryException("Oops! The description of an Event cannot be empty.");
        }
        String[] parts = input.substring(6).split("/from", 2); // This is name + date/time
        if (parts.length == 1) {
            throw new BarryException("An event needs a starting time!"
                    + "Specify one by appending '/from yyyy-MM-dd HHmm' to the Event.");
        }
        String name = parts[0].trim();
        if (name.isEmpty()) {
            throw new BarryException("Oops! The description of an Event cannot be empty.");
        }
        String[] times = parts[1].split("/to", 2);
        if (times.length == 1) {
            throw new BarryException("An event needs an end time! "
                    + "Specify one by appending '/to yyyy-MM-dd HHmm' to the Event.");
        }
        String startString = times[0].trim();
        String endString = times[1].trim();

        if (startString.isEmpty()) {
            throw new BarryException("Start time cannot be empty! "
                    + "Specify one by appending '/from yyyy-MM-dd HHmm' to the Event.");
        }
        if (endString.isEmpty()) {
            throw new BarryException("End time cannot be empty! "
                    + "Specify one by appending '/to yyyy-MM-dd HHmm' to the Event.");
        }

        LocalDateTime start = parseDateTime(startString);
        LocalDateTime end = parseDateTime(endString);
        if (end.isBefore(start)) {
            throw new BarryException("Event's end time cannot be before its start time!");
        }

        Event newEvent = new Event(name, start, end);
        userList.add(newEvent);
        printAddedMessage(newEvent, userList.size());
        storage.save(userList);
    }

    private static LocalDateTime parseDateTime(String s) throws BarryException {
        try {
            return LocalDateTime.parse(s.trim(), IN_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BarryException("Invalid date/time. Use yyyy-MM-dd HHmm (e.g., 2026-01-30 1400).");
        }
    }

    public static void listTasks(ArrayList<Task> taskList) {
        System.out.println(DIVIDER);
        if (taskList.isEmpty()) {
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
