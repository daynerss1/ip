package barry.ui;

import java.util.List;

import barry.task.Task;
import barry.task.TaskList;

/**
 * Handles all user-facing input/output for the Barry chatbot.
 *
 * <p>This class is responsible for reading commands from standard input and displaying messages
 * (e.g., welcome text, task lists, confirmations, and error messages) in a consistent format.</p>
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Displays the welcome message at the start of the program.
     */
    public String formatWelcome() {
        return String.join("\n",
                DIVIDER,
                "Hello! I'm Barry.",
                "What can I do for you?",
                DIVIDER
        );
    }

    /**
     * Displays the farewell message when the user exits the program.
     */
    public String formatBye() {
        return String.join("\n",
                DIVIDER,
                "Bye. Hope to see you again soon!",
                DIVIDER
        );
    }

    /**
     * Displays an error message to the user in a consistent UI format.
     *
     * @param msg The error message to display.
     */
    public String formatError(String msg) {
        return String.join("\n",
                DIVIDER, msg, DIVIDER
        );
    }

    /**
     * Displays an error message related to loading saved data.
     *
     * @param msg Details of the loading failure.
     */
    public String formatLoadingError(String msg) {
        return formatError("Problem loading saved tasks: " + msg);
    }

    /**
     * Displays a message indicating a task was added successfully.
     *
     * @param task The task that was added.
     * @param size The updated number of tasks in the list.
     */
    public String formatTaskAdded(Task task, int size) {
        return String.join("\n",
                DIVIDER,
                "Got it. I've added this task:",
                task.toString(),
                "Now you have " + size + " tasks in the list.",
                DIVIDER
        );
    }

    /**
     * Displays a message indicating a task was deleted successfully.
     *
     * @param task The task that was removed.
     * @param size The updated number of tasks in the list.
     */
    public String formatTaskDeleted(Task task, int size) {
        return String.join("\n",
                DIVIDER,
                "Noted. I've removed this task:",
                task.toString(),
                "Now you have " + size + " tasks in the list.",
                DIVIDER
        );
    }

    /**
     * Displays a message indicating a task was marked as done.
     *
     * @param task The task that was marked.
     */
    public String formatTaskMarked(Task task) {
        return String.join("\n",
                DIVIDER,
                "Nice! I've marked this task as done:",
                task.toString(),
                DIVIDER
        );
    }

    /**
     * Displays a message indicating a task was unmarked (set as not done).
     *
     * @param task The task that was unmarked.
     */

    public String formatTaskUnmarked(Task task) {
        return String.join("\n",
                DIVIDER,
                "OK, I've marked this task as not done yet:",
                task.toString(),
                DIVIDER
        );
    }

    /**
     * Displays the tasks currently stored in the task list.
     *
     * @param tasks The task list to display.
     */
    public String formatTaskList(TaskList tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append("\n");

        if (tasks.size() == 0) {
            sb.append("Your task list is currently empty.\n");
        } else {
            sb.append("Here are the tasks in your list:\n");
            for (int i = 0; i < tasks.size(); i++) {
                sb.append(i + 1).append(".").append(tasks.get(i)).append("\n");
            }
        }

        sb.append(DIVIDER);
        return sb.toString();
    }

    /**
     * Displays the list of tasks that matches the keyword specified.
     *
     * @param matches The list of tasks that matches the keyword specified, with their correct indexes.
     */
    public String formatFindResults(List<TaskList.IndexedTask> matches) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append("\n");

        if (matches.isEmpty()) {
            sb.append("No matching tasks found.\n");
            sb.append(DIVIDER);
            return sb.toString();
        }

        sb.append("Here are the matching tasks in your list:\n");
        for (TaskList.IndexedTask it : matches) {
            sb.append(it.index1Based).append(".").append(it.task).append("\n");
        }

        sb.append(DIVIDER);
        return sb.toString();
    }
}
