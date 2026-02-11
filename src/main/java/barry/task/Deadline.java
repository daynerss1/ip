package barry.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task that must be completed by a specific date/time.
 *
 * <p>A {@code Deadline} extends {@code Task} by storing a {@link java.time.LocalDateTime} indicating
 * when the task is due.</p>
 */
public class Deadline extends Task {
    private static final DateTimeFormatter FORMAT_OUT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    private static final String TASK_TYPE_ICON = "[D]";
    private static final String LABEL_BY_PREFIX = " (by: ";
    private static final String LABEL_END = ")";
    private final LocalDateTime deadline;

    /**
     * Constructs a deadline task.
     *
     * @param name Task description.
     * @param deadline Deadline date/time.
     */
    public Deadline(String name, LocalDateTime deadline) {
        super(name);
        this.deadline = deadline;
    }

    public LocalDateTime getBy() {
        return this.deadline;
    }

    @Override
    public String toString() {
        return TASK_TYPE_ICON + super.toString()
                + LABEL_BY_PREFIX + formatDeadline(deadline) + LABEL_END;
    }

    private static String formatDeadline(LocalDateTime deadline) {
        return deadline.format(FORMAT_OUT);
    }
}
