package barry.ui;

import java.util.List;
import java.util.Scanner;

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
    private final Scanner sc = new Scanner(System.in);

    /**
     * Displays the welcome message at the start of the program.
     */
    public void showWelcome() {
        System.out.println(DIVIDER + "\nHello! I'm Barry.\nWhat can I do for you?\n" + DIVIDER);
    }

    /**
     * Reads a single line of user input.
     *
     * @return The trimmed command string entered by the user.
     */
    public String readCommand() {
        return sc.nextLine().trim();
    }

    /**
     * Displays the farewell message when the user exits the program.
     */
    public void showBye() {
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }

    /**
     * Displays an error message to the user in a consistent UI format.
     *
     * @param msg The error message to display.
     */
    public void showError(String msg) {
        System.out.println(DIVIDER);
        System.out.println(msg);
        System.out.println(DIVIDER);
    }

    /**
     * Displays an error message related to loading saved data.
     *
     * @param msg Details of the loading failure.
     */
    public void showLoadingError(String msg) {
        showError("Problem loading saved tasks: " + msg);
    }

    /**
     * Displays a message indicating a task was added successfully.
     *
     * @param task The task that was added.
     * @param size The updated number of tasks in the list.
     */
    public void showTaskAdded(Task task, int size) {
        System.out.println(DIVIDER);
        System.out.println("Got it. I've added this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays a message indicating a task was deleted successfully.
     *
     * @param task The task that was removed.
     * @param size The updated number of tasks in the list.
     */
    public void showTaskDeleted(Task task, int size) {
        System.out.println(DIVIDER);
        System.out.println("Noted. I've removed this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays a message indicating a task was marked as done.
     *
     * @param task The task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println(DIVIDER);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task.toString());
        System.out.println(DIVIDER);
    }

    /**
     * Displays a message indicating a task was unmarked (set as not done).
     *
     * @param task The task that was unmarked.
     */

    public void showTaskUnmarked(Task task) {
        System.out.println(DIVIDER);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task.toString());
        System.out.println(DIVIDER);
    }

    /**
     * Displays the tasks currently stored in the task list.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(DIVIDER);
        if (tasks.size() == 0) {
            System.out.println("Your task list is currently empty.\n" + DIVIDER);
            return;
        }
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toString());
        }
        System.out.println(DIVIDER);
    }

    /**
     * Displays the list of tasks that matches the keyword specified.
     *
     * @param matches The list of tasks that matches the keyword specified, with their correct indexes.
     */
    public void showFindResults(List<TaskList.IndexedTask> matches) {
        System.out.println(DIVIDER);
        if (matches.isEmpty()) {
            System.out.println("No matching tasks found.");
            System.out.println(DIVIDER);
            return;
        }

        System.out.println("Here are the matching tasks in your list:");
        for (TaskList.IndexedTask it : matches) {
            System.out.println(it.index1Based + "." + it.task);
        }
        System.out.println(DIVIDER);
    }
}
