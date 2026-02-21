package barry.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.Set;

import barry.exception.BarryException;

/**
 * Parses raw user command strings into structured {@link barry.parser.ParsedInput} objects.
 *
 * <p>The parser validates command syntax and argument formats
 * (e.g., required flags such as {@code /by},
 * {@code /from}, {@code /to}, and date/time formats).
 */
public class Parser {
    private static final String INPUT_DATE_PATTERN = "uuuu-MM-dd HHmm";
    private static final DateTimeFormatter IN_DATE_FORMAT =
            DateTimeFormatter.ofPattern(INPUT_DATE_PATTERN).withResolverStyle(ResolverStyle.STRICT);
    private static final String ERROR_EMPTY_INPUT = "Input command cannot be empty.";
    private static final String ERROR_INVALID_COMMAND = "Invalid command: Use 'todo', 'deadline', 'event', 'list', "
            + "'mark', 'unmark', 'delete', 'find', 'help', or 'bye'";
    private static final String ERROR_EXTRA_ARGUMENTS = "This command does not accept extra arguments.";
    private static final String ERROR_TODO_EMPTY = "Oops! The description of a ToDo cannot be empty.";
    private static final String ERROR_DEADLINE_EMPTY = "Oops! The description of a Deadline cannot be empty.";
    private static final String ERROR_DEADLINE_MISSING_BY = "You need to input a date for the deadline of this task! "
            + "Specify one by appending '/by yyyy-MM-dd HHmm' to the Deadline.";
    private static final String ERROR_DEADLINE_BY_EMPTY = "The Deadline's date/time cannot be empty. "
            + "Specify one by appending '/by yyyy-MM-dd HHmm' to the Deadline.";
    private static final String ERROR_DEADLINE_MULTIPLE_BY = "Deadline accepts exactly one '/by' flag.";
    private static final String ERROR_EVENT_EMPTY = "Oops! The description of an Event cannot be empty.";
    private static final String ERROR_EVENT_MISSING_FROM = "An event needs a starting time! "
            + "Specify one by appending '/from yyyy-MM-dd HHmm' to the Event.";
    private static final String ERROR_EVENT_MISSING_TO = "An event needs an end time! "
            + "Specify one by appending '/to yyyy-MM-dd HHmm' to the Event.";
    private static final String ERROR_EVENT_START_EMPTY = "Start time cannot be empty! "
            + "Specify one by appending '/from yyyy-MM-dd HHmm' to the Event.";
    private static final String ERROR_EVENT_END_EMPTY = "End time cannot be empty! "
            + "Specify one by appending '/to yyyy-MM-dd HHmm' to the Event.";
    private static final String ERROR_EVENT_MULTIPLE_FROM = "Event accepts exactly one '/from' flag.";
    private static final String ERROR_EVENT_MULTIPLE_TO = "Event accepts exactly one '/to' flag.";
    private static final String ERROR_EVENT_END_NOT_AFTER_START =
            "Event's end time must be later than its start time.";
    private static final String ERROR_NUMBERS_REQUIRED = "You must specify at least one task number.";
    private static final String ERROR_NUMBERS_NOT_INTEGER = "Task numbers must be integers.";
    private static final String ERROR_NUMBERS_NON_POSITIVE = "Task numbers must be positive integers.";
    private static final String ERROR_NUMBERS_DUPLICATE = "Duplicate task numbers are not allowed.";
    private static final String ERROR_FIND_EMPTY = "Find what? Please provide a keyword.";
    private static final String ERROR_INVALID_DATE_TIME =
            "Invalid date/time. Use yyyy-MM-dd HHmm (e.g., 2026-01-30 1400).";

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
     * @throws BarryException If the input is empty, the command is unknown, or the arguments are invalid.
     */
    public static ParsedInput parse(String input) throws BarryException {
        ensureInputIsNotBlank(input);
        String normalizedInput = input.trim();
        Command type = parseCommandWord(normalizedInput);
        return parseByCommand(type, normalizedInput);
    }

    private static void ensureInputIsNotBlank(String input) throws BarryException {
        if (input == null || input.trim().isEmpty()) {
            throw new BarryException(ERROR_EMPTY_INPUT);
        }
        assert input != null : "input must not be null";
    }

    private static Command parseCommandWord(String input) throws BarryException {
        String firstWord = input.split("\\s+")[0].toLowerCase();
        assert !firstWord.isEmpty() : "command word must not be empty";

        switch (firstWord) {
        case "list":
            return Command.LIST;
        case "help":
            return Command.HELP;
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
        case "find":
            return Command.FIND;
        default:
            throw new BarryException(ERROR_INVALID_COMMAND);
        }
    }

    private static ParsedInput parseByCommand(Command type, String input) throws BarryException {
        switch (type) {
        case LIST:
            ensureNoExtraArguments(input, "list");
            return ParsedInput.simple(type);
        case HELP:
            ensureNoExtraArguments(input, "help");
            return ParsedInput.simple(type);
        case BYE: // Intentional fallthrough as LIST, HELP, and BYE require no arguments
            ensureNoExtraArguments(input, "bye");
            return ParsedInput.simple(type);
        case TODO:
            return parseTodo(input);
        case DEADLINE:
            return parseDeadline(input);
        case EVENT:
            return parseEvent(input);
        case FIND:
            return parseFind(input);
        case MARK:
        case UNMARK:
        case DELETE: // Intentional fallthrough as MARK, UNMARK, and DELETE require the same processing.
            return parseNumbers(type, input);
        default:
            throw new BarryException(ERROR_INVALID_COMMAND); // Defensive;
            // check is also done in parseCommandWord method.
        }
    }

    private static ParsedInput parseTodo(String input) throws BarryException {
        String name = extractRemainderAfterCommand(input, "todo");
        assert name != null : "todo name must not be null";
        ensureNotEmpty(name, ERROR_TODO_EMPTY);
        return ParsedInput.todo(name);
    }

    private static ParsedInput parseDeadline(String input) throws BarryException {
        String remainder = extractRemainderAfterCommand(input, "deadline");
        assert remainder != null : "deadline remaining details must not be null";
        ensureNotEmpty(remainder, ERROR_DEADLINE_EMPTY);
        ensureSingleFlagOccurrence(remainder, "/by", ERROR_DEADLINE_MULTIPLE_BY);

        String[] parts = splitOnFlagOrThrow(remainder, "/by", ERROR_DEADLINE_MISSING_BY);
        String name = parts[0].trim();
        assert name != null : "deadline name must not be null";
        ensureNotEmpty(name, ERROR_DEADLINE_EMPTY);

        String byString = parts[1].trim();
        assert byString != null : "deadline by string must not be null";
        ensureNotEmpty(byString, ERROR_DEADLINE_BY_EMPTY);

        LocalDateTime by = parseDateTime(byString);
        return ParsedInput.deadline(name, by);
    }

    private static ParsedInput parseEvent(String input) throws BarryException {
        String remainder = extractRemainderAfterCommand(input, "event");
        assert remainder != null : "event remaining details must not be null";
        ensureNotEmpty(remainder, ERROR_EVENT_EMPTY);
        ensureSingleFlagOccurrence(remainder, "/from", ERROR_EVENT_MULTIPLE_FROM);
        ensureSingleFlagOccurrence(remainder, "/to", ERROR_EVENT_MULTIPLE_TO);

        String[] parts = splitOnFlagOrThrow(remainder, "/from", ERROR_EVENT_MISSING_FROM);
        String name = parts[0].trim();
        assert name != null : "event name must not be null";
        ensureNotEmpty(name, ERROR_EVENT_EMPTY);

        String[] times = splitOnFlagOrThrow(parts[1], "/to", ERROR_EVENT_MISSING_TO);
        String startString = times[0].trim();
        assert startString != null : "event start string must not be null";
        ensureNotEmpty(startString, ERROR_EVENT_START_EMPTY);

        String endString = times[1].trim();
        assert endString != null : "event end string must not be null";
        ensureNotEmpty(endString, ERROR_EVENT_END_EMPTY);

        LocalDateTime start = parseDateTime(startString);
        LocalDateTime end = parseDateTime(endString);
        if (!end.isAfter(start)) {
            throw new BarryException(ERROR_EVENT_END_NOT_AFTER_START);
        }

        return ParsedInput.event(name, start, end);
    }

    private static ParsedInput parseNumbers(Command type, String input) throws BarryException {
        String[] tokens = input.trim().split("\\s+");
        if (tokens.length <= 1) {
            throw new BarryException(ERROR_NUMBERS_REQUIRED);
        }
        int[] nums = new int[tokens.length - 1];
        Set<Integer> seen = new HashSet<>();
        for (int i = 1; i < tokens.length; i++) {
            int parsedNumber = parseTaskNumber(tokens[i]);
            if (parsedNumber <= 0) {
                throw new BarryException(ERROR_NUMBERS_NON_POSITIVE);
            }
            if (!seen.add(parsedNumber)) {
                throw new BarryException(ERROR_NUMBERS_DUPLICATE);
            }
            nums[i - 1] = parsedNumber;
        }

        return ParsedInput.numbers(type, nums);
    }

    private static ParsedInput parseFind(String input) throws BarryException {
        String keyword = extractRemainderAfterCommand(input, "find");
        assert keyword != null : "find keyword must not be null";
        ensureNotEmpty(keyword, ERROR_FIND_EMPTY);
        return ParsedInput.find(keyword);
    }

    private static LocalDateTime parseDateTime(String s) throws BarryException {
        assert s != null : "date time string must not be null";
        try {
            return LocalDateTime.parse(s.trim(), IN_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BarryException(ERROR_INVALID_DATE_TIME);
        }
    }

    private static String extractRemainderAfterCommand(String input, String commandWord) {
        String trimmed = input.trim();
        if (trimmed.length() <= commandWord.length()) {
            return "";
        }
        return trimmed.substring(commandWord.length()).trim();
    }

    private static String[] splitOnFlagOrThrow(String input, String flag, String errorMessage) throws BarryException {
        String[] parts = input.split(flag, 2);
        if (parts.length < 2) {
            throw new BarryException(errorMessage);
        }
        return parts;
    }

    private static void ensureSingleFlagOccurrence(
            String input, String flag, String errorMessage) throws BarryException {
        if (countOccurrences(input, flag) > 1) {
            throw new BarryException(errorMessage);
        }
    }

    private static int countOccurrences(String text, String target) {
        int count = 0;
        int fromIndex = 0;
        while (true) {
            int foundIndex = text.indexOf(target, fromIndex);
            if (foundIndex < 0) {
                break;
            }
            count++;
            fromIndex = foundIndex + target.length();
        }
        return count;
    }

    private static void ensureNotEmpty(String value, String errorMessage) throws BarryException {
        if (value == null || value.isEmpty()) {
            throw new BarryException(errorMessage);
        }
    }

    private static void ensureNoExtraArguments(
            String input, String commandWord) throws BarryException {
        if (!input.equalsIgnoreCase(commandWord)) {
            throw new BarryException(ERROR_EXTRA_ARGUMENTS);
        }
    }

    private static int parseTaskNumber(String s) throws BarryException {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new BarryException(ERROR_NUMBERS_NOT_INTEGER);
        }
    }
}
