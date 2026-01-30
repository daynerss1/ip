import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Barry {
    private static final DateTimeFormatter IN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/barry.txt");

        TaskList userList;
        try {
            userList = new TaskList(storage.load());
        } catch (BarryException e) {
            userList = new TaskList();
            ui.showError("Saved data was corrupted. Starting a new file. " + e.getMessage());
        }

        ui.showWelcome();
        String input = ui.readCommand();
        while (!input.equals("bye")) {
            try {
                parseInput(input, userList, storage, ui);
            } catch (BarryException e) {
                ui.showError(e.getMessage());
            }
            input = ui.readCommand();
        }
        ui.showBye();
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

    public static void parseInput(String input, TaskList userList, Storage storage, Ui ui) throws BarryException {
        Command command = getCommandType(input);
        switch (command) {
        case LIST: {
            ui.showTaskList(userList);
            break;
        }
        case TODO: {
            handleTodo(input, userList, storage, ui);
            break;
        }
        case DEADLINE: {
            handleDeadline(input, userList, storage, ui);
            break;
        }
        case EVENT: {
            handleEvent(input, userList, storage, ui);
            break;
        }
        case DELETE: {
            handleDelete(input, userList, storage, ui);
            break;
        }
        case MARK: {
            handleMark(input, userList, storage, ui);
            break;
        }
        case UNMARK: {
            handleUnmark(input, userList, storage, ui);
            break;
        }
        case BYE: {
            break;
        }
        }
    }

    private static int parseTaskNumber(String s) throws BarryException {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new BarryException("Task numbers must be integers.");
        }
    }

    public static void handleMark(
            String input, TaskList userList, Storage storage, Ui ui) throws BarryException {
        String[] taskNums = input.split("\\s+");
        if (taskNums.length > 1) {
            for (int i = 1; i < taskNums.length; i++) {
                int number = parseTaskNumber(taskNums[i]);      // 1-based
                userList.checkIndex1Based(number);
                userList.get(number - 1).mark();
                ui.showTaskMarked(userList.get(number - 1));
            }
        } else {
            throw new BarryException("You need to specify 1 or more tasks to mark.");
        }
    }

    public static void handleUnmark(String input, TaskList userList, Storage storage, Ui ui) throws BarryException {
        String[] taskNums = input.split("\\s+");
        if (taskNums.length > 1) {
            for (int i = 1; i < taskNums.length; i++) {
                int number = parseTaskNumber(taskNums[i]);
                userList.checkIndex1Based(number);
                userList.get(number - 1).unmark();
                ui.showTaskUnmarked(userList.get(number - 1));
            }
        } else {
            throw new BarryException("You need to specify 1 or more tasks to unmark.");
        }
    }

    public static void handleDelete(String input, TaskList userList, Storage storage, Ui ui) throws BarryException {
        String[] taskNums = input.split("\\s+");
        if (taskNums.length > 1) {
            for (int i = 1; i < taskNums.length; i++) {
                int number = parseTaskNumber(taskNums[i]);
                userList.checkIndex1Based(number);
                ui.showTaskDeleted(userList.get(number - 1), userList.size() - 1);
                userList.remove(number - 1);
            }
            storage.save(userList);
        } else {
            throw new BarryException("You need to specify 1 or more tasks to delete.");
        }
    }

    public static void handleTodo(String input, TaskList userList, Storage storage, Ui ui) throws BarryException {
        if (input.trim().toLowerCase().equals("todo")) {
            throw new BarryException("Oops! The description of a ToDo cannot be empty.");
        } else {
            String name = input.substring(5);
            ToDo newToDo = new ToDo(name);
            userList.add(newToDo);
            ui.showTaskAdded(newToDo, userList.size());
            storage.save(userList);
        }
    }

    public static void handleDeadline(String input, TaskList userList, Storage storage, Ui ui) throws BarryException {
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
        ui.showTaskAdded(newDeadline, userList.size());
        storage.save(userList);
    }

    public static void handleEvent(String input, TaskList userList, Storage storage, Ui ui) throws BarryException {
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
        ui.showTaskAdded(newEvent, userList.size());
        storage.save(userList);
    }

    private static LocalDateTime parseDateTime(String s) throws BarryException {
        try {
            return LocalDateTime.parse(s.trim(), IN_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BarryException("Invalid date/time. Use yyyy-MM-dd HHmm (e.g., 2026-01-30 1400).");
        }
    }
}
