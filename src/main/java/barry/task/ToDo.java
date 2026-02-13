package barry.task;

/**
 * Represents a todo task that contains only a description and completion status.
 *
 * <p>A {@code ToDo} has no associated date/time; it is simply an item the user intends to do.</p>
 */
public class ToDo extends Task {
    private static final String TASK_TYPE_ICON = "[T]";

    /**
     * Constructs a ToDo task with the given description.
     *
     * @param name Task description.
     */
    public ToDo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return TASK_TYPE_ICON + super.toString();
    }
}
