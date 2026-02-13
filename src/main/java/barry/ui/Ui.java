package barry.ui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Displays the welcome message at the start of the program.
     */
    public String formatWelcome() {
        return formatWithDivider(
                "Hello! I'm Barry.",
                "What can I do for you?"
        );
    }

    /**
     * Displays the farewell message when the user exits the program.
     */
    public String formatBye() {
        return formatWithDivider("Bye. Hope to see you again soon!");
    }

    /**
     * Displays the in-app help page showing available commands and examples.
     */
    public String formatHelp() {
        return formatWithDivider(
                "Here are the commands you can use:",
                "list",
                "help",
                "todo <description>",
                "deadline <description> /by yyyy-MM-dd HHmm",
                "event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm",
                "mark <task number> [more task numbers...]",
                "unmark <task number> [more task numbers...]",
                "delete <task number> [more task numbers...]",
                "find <keyword>",
                "bye"
        );
    }

    /**
     * Displays an error message to the user in a consistent UI format.
     *
     * @param msg The error message to display.
     */
    public String formatError(String msg) {
        assert msg != null : "error message must not be null";
        return formatWithDivider(msg);
    }

    /**
     * Displays an error message related to loading saved data.
     *
     * @param msg Details of the loading failure.
     */
    public String formatLoadingError(String msg) {
        assert msg != null : "loading error message must not be null";
        return formatError("Problem loading saved tasks: " + msg);
    }

    /**
     * Displays a startup informational message.
     *
     * @param lines One or more lines to display.
     */
    public String formatStartupInfo(String... lines) {
        return formatWithDivider(lines);
    }

    /**
     * Displays a message indicating a task was added successfully.
     *
     * @param task The task that was added.
     * @param size The updated number of tasks in the list.
     */
    public String formatTaskAdded(Task task, int size) {
        assert task != null : "task must not be null";
        return formatWithDivider(
                "Got it. I've added this task:",
                task.toString(),
                "Now you have " + size + " tasks in the list."
        );
    }

    /**
     * Displays a message indicating tasks were deleted successfully.
     *
     * @param size The updated number of tasks in the list.
     * @param tasks The tasks that were removed.
     */
    public String formatTaskDeleted(int size, List<Task> tasks) {
        assert tasks != null : "tasks must not be null";
        String modifier = (tasks.size() > 1) ? "these tasks" : "this task";

        return formatMultipleTasksWithSummary(
                "Noted. I've removed " + modifier + " :",
                "Now you have " + size + " tasks in the list.",
                tasks
        );
    }

    /**
     * Displays a message indicating tasks were marked as done.
     *
     * @param tasks The tasks that were marked.
     */
    public String formatTaskMarked(List<Task> tasks) {
        assert tasks != null : "tasks must not be null";
        String modifier = (tasks.size() > 1) ? "these tasks" : "this task";

        return formatMultipleTasks(
                "Nice! I've marked " + modifier + " as done:",
                tasks
        );
    }

    /**
     * Displays a message indicating tasks were unmarked (set as not done).
     *
     * @param tasks The tasks that were unmarked.
     */
    public String formatTaskUnmarked(List<Task> tasks) {
        assert tasks != null : "tasks must not be null";
        String modifier = (tasks.size() > 1) ? "these tasks" : "this task";

        return formatMultipleTasks(
                "OK, I've marked " + modifier + " as not done yet:",
                tasks
        );
    }

    /**
     * Displays the tasks currently stored in the task list.
     *
     * @param tasks The task list to display.
     */
    public String formatTaskList(TaskList tasks) {
        assert tasks != null : "tasks must not be null";
        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append(LINE_SEPARATOR);

        if (tasks.size() == 0) {
            sb.append("Your task list is currently empty.").append(LINE_SEPARATOR);
        } else {
            sb.append("Here are the tasks in your list:").append(LINE_SEPARATOR);
            appendTaskList(sb, tasks);
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
        assert matches != null : "matches must not be null";
        if (matches.isEmpty()) {
            return formatWithDivider("No matching tasks found.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append(LINE_SEPARATOR);
        sb.append("Here are the matching tasks in your list:").append(LINE_SEPARATOR);
        appendMatches(sb, matches);

        sb.append(DIVIDER);
        return sb.toString();
    }

    private void appendTaskList(StringBuilder sb, TaskList tasks) {
        assert sb != null : "string builder must not be null";
        assert tasks != null : "tasks must not be null";
        String body = IntStream.range(0, tasks.size())
                    .mapToObj(i -> (i + 1) + "." + tasks.getTask(i))
                    .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
        sb.append(body);
    }

    private void appendMatches(StringBuilder sb, List<TaskList.IndexedTask> matches) {
        assert sb != null : "string builder must not be null";
        assert matches != null : "matches must not be null";
        String body = matches.stream()
                .map(indexedTask -> indexedTask.index1Based + "." + indexedTask.task)
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
        sb.append(body);
    }

    private String formatMultipleTasks(String header, List<Task> tasks) {
        assert header != null : "header must not be null";
        assert tasks != null : "tasks must not be null";
        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append(LINE_SEPARATOR);
        sb.append(header).append(LINE_SEPARATOR);
        appendTasks(sb, tasks);
        sb.append(DIVIDER);
        return sb.toString();
    }

    private String formatMultipleTasksWithSummary(String header, String summary, List<Task> tasks) {
        assert header != null : "header must not be null";
        assert summary != null : "summary must not be null";
        assert tasks != null : "tasks must not be null";
        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append(LINE_SEPARATOR);
        sb.append(header).append(LINE_SEPARATOR);
        appendTasks(sb, tasks);
        sb.append(summary).append(LINE_SEPARATOR);
        sb.append(DIVIDER);
        return sb.toString();
    }

    private void appendTasks(StringBuilder sb, List<Task> tasks) {
        assert sb != null : "string builder must not be null";
        assert tasks != null : "tasks must not be null";
        String body = tasks.stream()
                .map(Object::toString)
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
        sb.append(body);
    }

    private String formatWithDivider(String... lines) {
        assert lines != null : "lines must not be null";
        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append(LINE_SEPARATOR);
        for (String line : lines) {
            sb.append(line).append(LINE_SEPARATOR);
        }
        sb.append(DIVIDER);
        return sb.toString();
    }
}
