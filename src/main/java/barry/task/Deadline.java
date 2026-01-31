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
        return "[D]" + super.toString() + " (by: " + deadline.format(FORMAT_OUT) + ")";
    }
}
