package barry.task;

import barry.exception.BarryException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the in-memory list of tasks managed by the Barry chatbot.
 *
 * <p>This class encapsulates the internal task collection and provides operations to add, remove,
 * retrieve, and validate access to tasks. It centralizes task-list-related logic such as index checking,
 * reducing direct manipulation of the underlying list by other components.</p>
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a task list initialized with the given tasks.
     *
     * @param tasks Initial tasks to store in the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified 0-based index.
     *
     * @param index 0-based index of the task to remove.
     * @return The removed task.
     * @throws IndexOutOfBoundsException If index is out of range.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified 0-based index.
     *
     * @param index 0-based index of the task.
     * @return The task at that position.
     * @throws IndexOutOfBoundsException If index is out of range.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Validates that a 1-based task number refers to an existing task in this list.
     *
     * @param taskNum 1-based task number (e.g., 1 refers to the first task).
     * @throws BarryException If the task number is out of range.
     */
    public void checkIndex1Based(int taskNum) throws BarryException {
        if (taskNum < 1 || taskNum > size()) {
            throw new BarryException("Task number out of range.");
        }
    }
}
