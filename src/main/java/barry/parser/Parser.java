package barry.parser;

import barry.exception.BarryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses raw user command strings into structured {@link barry.parser.ParsedInput} objects.
 *
 * <p>The parser validates command syntax and argument formats
 * (e.g., required flags such as {@code /by},
 * {@code /from}, {@code /to}, and date/time formats).
 */
public class Parser {
    private static final DateTimeFormatter IN_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Parses a raw user command string into a structured {@link ParsedInput}.
     *
     * <p>This method validates the command format and arguments
     * (e.g., date/time formatting, required flags).
     * It does not validate indices against the current task list size;
     * that is handled by {@code TaskList}.</p>
     *
     * @param input Raw command entered by the user.
     * @return A structured representation of the command and its arguments.
     * @throws BarryException If the input is empty, the command is unknown,
     * or the arguments are invalid.
     */
    public static ParsedInput parse(String input) throws BarryException {
        if (input == null || input.trim().isEmpty()) {
            throw new BarryException("Input command cannot be empty.");
        }

        Command type = getCommandType(input);

        switch (type) {
        case LIST:
        case BYE:
            return ParsedInput.simple(type);
        // fall through since LIST and BYE do not require additional parameters
        case TODO:
            return parseTodo(input);

        case DEADLINE:
            return parseDeadline(input);

        case EVENT:
            return parseEvent(input);

        case MARK:
        case UNMARK:
        case DELETE:
            return parseNumbers(type, input);
        // fall through since MARK, UNMARK, and DELETE all require additional parsing of index numbers
        default:
            throw new BarryException("Unknown command.");
        }
    }

    private static Command getCommandType(String input) throws BarryException {
        String firstWord = input.split("\\s+")[0].toLowerCase();

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
            throw new BarryException("Invalid command: Use 'todo', 'deadline', 'event', 'list', 'mark', "
                    + "'unmark', 'delete', or 'bye'");
        }
    }

    private static ParsedInput parseTodo(String input) throws BarryException {
        if (input.trim().toLowerCase().equals("todo")) {
            throw new BarryException("Oops! The description of a ToDo cannot be empty.");
        } else {
            String name = input.substring(5);
            if (name.isEmpty()) {
                throw new BarryException("Oops! The description of a ToDo cannot be empty.");
            }
            return ParsedInput.todo(name);
        }
    }

    private static ParsedInput parseDeadline(String input) throws BarryException {
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
        return ParsedInput.deadline(name, by);
    }

    private static ParsedInput parseEvent(String input) throws BarryException {
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

        return ParsedInput.event(name, start, end);
    }

    private static ParsedInput parseNumbers(Command type, String input) throws BarryException {
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length <= 1) {
            throw new BarryException("You must specify at least one task number.");
        }
        int[] nums = new int[tokens.length - 1];
        for (int i = 1; i < tokens.length; i++) {
            nums[i - 1] = parseTaskNumber(tokens[i]);
        }

        return ParsedInput.numbers(type, nums);
    }

    private static LocalDateTime parseDateTime(String s) throws BarryException {
        try {
            return LocalDateTime.parse(s.trim(), IN_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BarryException("Invalid date/time. Use yyyy-MM-dd HHmm (e.g., 2026-01-30 1400).");
        }
    }

    private static int parseTaskNumber(String s) throws BarryException {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new BarryException("Task numbers must be integers.");
        }
    }
}
