package barry.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private static final DateTimeFormatter FORMAT_OUT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    private final LocalDateTime deadline;

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
