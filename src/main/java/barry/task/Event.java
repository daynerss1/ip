package barry.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event task that occurs over a time range.
 *
 * <p>An {@code Event} extends {@code Task} by storing a start and end {@link java.time.LocalDateTime},
 * representing the time interval during which the event occurs.</p>
 */
public class Event extends Task {
    private static final DateTimeFormatter FORMAT_OUT = DateTimeFormatter
            .ofPattern("MMM dd yyyy HH:mm");
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Constructs an event task.
     *
     * @param name Event description.
     * @param start Start date/time.
     * @param end End date/time.
     */
    public Event(String name, LocalDateTime start, LocalDateTime end) {
        super(name);
        assert start != null : "start must not be null";
        assert end != null : "end must not be null";
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getFrom() {
        return this.start;
    }

    public LocalDateTime getTo() {
        return this.end;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + start.format(FORMAT_OUT) + " to: "
                + end.format(FORMAT_OUT) + ")";
    }
}
