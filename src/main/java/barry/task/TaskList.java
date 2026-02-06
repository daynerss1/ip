package barry.task;

import java.util.ArrayList;
import java.util.List;

import barry.exception.BarryException;

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

    /**
     * Helper class to ensure that the index of all tasks is 1-based.
     */
    public static class IndexedTask {
        public final int index1Based;
        public final Task task;

        /**
         * Constructor for the IndexedTask class.
         *
         * @param index1Based 1-based index number of the task in the user's list.
         * @param task The corresponding task in the list.
         */
        public IndexedTask(int index1Based, Task task) {
            this.index1Based = index1Based;
            this.task = task;
        }
    }

    /**
     * Finds tasks whose description contains the given keyword (case-insensitive).
     *
     * @param keyword Keyword to search for.
     * @return A list of matching tasks paired with their 1-based indices in the current task list.
     */
    public List<IndexedTask> findByKeyword(String keyword) {
        String key = keyword.toLowerCase();
        List<IndexedTask> matches = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t.getName().toLowerCase().contains(key)) {
                matches.add(new IndexedTask(i + 1, t));
            }
        }
        return matches;
    }
}
