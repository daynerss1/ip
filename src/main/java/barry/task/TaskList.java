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
}
