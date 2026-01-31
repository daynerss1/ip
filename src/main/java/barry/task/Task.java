package barry.task;

/**
 * Represents a generic task with a description and completion status.
 *
 * <p>{@code Task} is the abstract concept underlying all task types in the application.
 * Subclasses such as {@code ToDo}, {@code Deadline}, and {@code Event} extend this class
 * to include task-type-specific data while reusing the common done/undone behavior.</p>
 */
public class Task {
    private final String name;
    private boolean done;

    /**
     * Constructs a task with the given description.
     *
     * @param name Task description.
     */
    public Task(String name) {
        this.name = name;
        this.done = false;
    }

    public void mark() {
        this.done = true;
    }

    public void unmark() {
        this.done = false;
    }

    public boolean isDone() {
        return this.done;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        String completion = this.done ? "X" : " ";
        return "[" + completion + "] " + this.name;
    }
}
