package barry.parser;

import java.time.LocalDateTime;

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

    public static ParsedInput simple(Command type) {
        return new ParsedInput(type, null, null, null, null, null);
    }

    public static ParsedInput todo(String name) {
        return new ParsedInput(Command.TODO, name, null, null, null, null);
    }

    public static ParsedInput deadline(String name, LocalDateTime by) {
        return new ParsedInput(Command.DEADLINE, name, by, null, null, null);
    }

    public static ParsedInput event(String name, LocalDateTime start, LocalDateTime end) {
        return new ParsedInput(Command.EVENT, name, null, start, end, null);
    }

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
