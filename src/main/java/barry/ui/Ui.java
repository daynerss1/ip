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
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Displays the welcome message at the start of the program.
     */
    public String formatWelcome() {
        return formatLines(
                "Ahoy, I'm Captain Barry.",
                "Tell me what to chart next."
        );
    }

    /**
     * Displays a short welcome message for first-run startup flows.
     */
    public String formatWelcomeShort() {
        return formatLines("Ahoy, I'm Captain Barry.");
    }

    /**
     * Displays the farewell message when the user exits the program.
     */
    public String formatBye() {
        return formatLines("Smooth sailing. See you at the next port.");
    }

    /**
     * Displays the in-app help page showing available commands and examples.
     */
    public String formatHelp() {
        return formatLines(
                "Navigation commands:",
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
        return formatLines("Storm warning: " + msg);
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
        return formatLines(lines);
    }

    /**
     * Displays a message indicating a task was added successfully.
     *
     * @param task The task that was added.
     * @param size The updated number of tasks in the list.
     */
    public String formatTaskAdded(Task task, int size) {
        assert task != null : "task must not be null";
        return formatLines(
                "Aye, I've logged this task:",
                task.toString(),
                "You now have " + size + " tasks on the chart."
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
                "Aye, I've removed " + modifier + ":",
                "You now have " + size + " tasks on the chart.",
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
                "Marked " + modifier + " as complete:",
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
                "Reopened " + modifier + ":",
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

        if (tasks.size() == 0) {
            sb.append("Your chart is clear.").append(LINE_SEPARATOR);
        } else {
            sb.append("Current charted tasks:").append(LINE_SEPARATOR);
            appendTaskList(sb, tasks);
        }
        return sb.toString().trim();
    }

    /**
     * Displays the list of tasks that matches the keyword specified.
     *
     * @param matches The list of tasks that matches the keyword specified, with their correct indexes.
     */
    public String formatFindResults(List<TaskList.IndexedTask> matches) {
        assert matches != null : "matches must not be null";
        if (matches.isEmpty()) {
            return formatLines("No matching tasks on the chart.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Matching charted tasks:").append(LINE_SEPARATOR);
        appendMatches(sb, matches);
        return sb.toString().trim();
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
        sb.append(header).append(LINE_SEPARATOR);
        appendTasks(sb, tasks);
        return sb.toString().trim();
    }

    private String formatMultipleTasksWithSummary(String header, String summary, List<Task> tasks) {
        assert header != null : "header must not be null";
        assert summary != null : "summary must not be null";
        assert tasks != null : "tasks must not be null";
        StringBuilder sb = new StringBuilder();
        sb.append(header).append(LINE_SEPARATOR);
        appendTasks(sb, tasks);
        sb.append(summary).append(LINE_SEPARATOR);
        return sb.toString().trim();
    }

    private void appendTasks(StringBuilder sb, List<Task> tasks) {
        assert sb != null : "string builder must not be null";
        assert tasks != null : "tasks must not be null";
        String body = tasks.stream()
                .map(Object::toString)
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
        sb.append(body);
    }

    private String formatLines(String... lines) {
        assert lines != null : "lines must not be null";
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(LINE_SEPARATOR);
        }
        return sb.toString().trim();
    }
}
