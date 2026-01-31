package barry.parser;

import java.time.LocalDateTime;

/**
 * Represents a parsed user command and its extracted arguments.
 *
 * <p>{@code ParsedInput} is produced by {@link barry.parser.Parser} and consumed by the main application
 * to execute the intended action. Depending on the command type, it may store a task description,
 * date/time values, and/or one or more task numbers.</p>
 */
public class ParsedInput {
    public final Command type;

    // For TODO / DEADLINE / EVENT
    public final String name;

    // For DEADLINE
    public final LocalDateTime by;

    // For EVENT
    public final LocalDateTime start;
    public final LocalDateTime end;

    // For MARK / UNMARK / DELETE
    public final int[] taskNumbers;

    private ParsedInput(Command type,
                        String name,
                        LocalDateTime by,
                        LocalDateTime start,
                        LocalDateTime end,
                        int[] taskNumbers) {
        this.type = type;
        this.name = name;
        this.by = by;
        this.start = start;
        this.end = end;
        this.taskNumbers = taskNumbers;
    }

    /**
     * Creates a parsed representation for commands that have no arguments (e.g., LIST, BYE).
     *
     * @param type The command type.
     * @return A {@code ParsedInput} representing the command.
     */
    public static ParsedInput simple(Command type) {
        return new ParsedInput(type, null, null, null, null, null);
    }

    /**
     * Creates a parsed representation of a TODO command.
     *
     * @param name The todo task description.
     * @return A {@code ParsedInput} containing the todo description.
     */
    public static ParsedInput todo(String name) {
        return new ParsedInput(Command.TODO, name, null, null, null, null);
    }

    /**
     * Creates a parsed representation of a DEADLINE command.
     *
     * @param name The deadline task description.
     * @param by The deadline date/time.
     * @return A {@code ParsedInput} containing deadline details.
     */
    public static ParsedInput deadline(String name, LocalDateTime by) {
        return new ParsedInput(Command.DEADLINE, name, by, null, null, null);
    }

    /**
     * Creates a parsed representation of an EVENT command.
     *
     * @param name The event description.
     * @param start The start date/time.
     * @param end The end date/time.
     * @return A {@code ParsedInput} containing event details.
     */
    public static ParsedInput event(String name, LocalDateTime start, LocalDateTime end) {
        return new ParsedInput(Command.EVENT, name, null, start, end, null);
    }

    /**
     * Creates a parsed representation of commands that operate on one or more task numbers
     * (e.g., MARK, UNMARK, DELETE).
     *
     * @param type The command type.
     * @param taskNumbers One or more 1-based task indices.
     * @return A {@code ParsedInput} containing the task numbers.
     */
    public static ParsedInput numbers(Command type, int[] taskNumbers) {
        return new ParsedInput(type, null, null, null, null, taskNumbers);
    }

    /**
     * Creates a parsed representation of a FIND command.
     *
     * @param keyword The keyword to filter tasks by.
     * @return A {@code ParsedInput} containing the specified keyword.
     */
    public static ParsedInput find(String keyword) {
        return new ParsedInput(Command.FIND, keyword, null, null, null, null);
    }
}
