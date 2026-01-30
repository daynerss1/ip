import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    private static final DateTimeFormatter FORMAT_OUT = DateTimeFormatter
            .ofPattern("MMM dd yyyy HH:mm");
    private LocalDateTime start;
    private LocalDateTime end;

    public Event(String name, LocalDateTime start, LocalDateTime end) {
        super(name);
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
