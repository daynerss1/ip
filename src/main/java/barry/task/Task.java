package barry.task;

/**
 * Represents a generic task with a description and completion status.
 *
 * <p>{@code Task} is the abstract concept underlying all task types in the application.
 * Subclasses such as {@code ToDo}, {@code Deadline}, and {@code Event} extend this class
 * to include task-type-specific data while reusing the common done/undone behavior.</p>
 */
public abstract class Task {
    private static final String DONE_MARK = "X";
    private static final String UNDONE_MARK = " ";
    private final String name;
    private boolean done;

    /**
     * Constructs a task with the given description.
     *
     * @param name Task description.
     */
    public Task(String name) {
        assert name != null : "name must not be null";
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

    /**
     * Returns whether another task has the same task details as this task.
     * Task details include task type and task-specific content.
     *
     * @param other task to compare against.
     * @return true if both tasks represent the same details.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null) {
            return false;
        }
        return this.getClass().equals(other.getClass())
                && this.getName().equals(other.getName());
    }

    @Override
    public String toString() {
        String completion = this.done ? DONE_MARK : UNDONE_MARK;
        return "[" + completion + "] " + this.name;
    }
}
