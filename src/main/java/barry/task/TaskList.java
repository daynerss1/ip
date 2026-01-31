package barry.task;

import barry.exception.BarryException;
import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public void checkIndex1Based(int taskNum) throws BarryException {
        if (taskNum < 1 || taskNum > size()) {
            throw new BarryException("Task number out of range.");
        }
    }

    public static class IndexedTask {
        public final int index1Based;
        public final Task task;

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
