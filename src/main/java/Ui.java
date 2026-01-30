import java.util.Scanner;
import java.util.ArrayList;

public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner sc = new Scanner(System.in);

    public void showWelcome() {
        System.out.println(DIVIDER + "\nHello! I'm Barry.\nWhat can I do for you?\n" + DIVIDER);
    }

    public String readCommand() {
        return sc.nextLine().trim();
    }

    public void showBye() {
        System.out.println(DIVIDER + "\nBye. Hope to see you again soon!\n" + DIVIDER);
    }

    public void showError(String msg) {
        System.out.println(DIVIDER);
        System.out.println(msg);
        System.out.println(DIVIDER);
    }

    public void showLoadingError(String msg) {
        showError("Problem loading saved tasks: " + msg);
    }

    public void showTaskAdded(Task task, int size) {
        System.out.println(DIVIDER);
        System.out.println("Got it. I've added this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    public void showTaskDeleted(Task task, int size) {
        System.out.println(DIVIDER);
        System.out.println("Noted. I've removed this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + size + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    public void showTaskMarked(Task task) {
        System.out.println(DIVIDER);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task.toString());
        System.out.println(DIVIDER);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println(DIVIDER);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task.toString());
        System.out.println(DIVIDER);
    }

    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(DIVIDER);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(DIVIDER);
    }
}
