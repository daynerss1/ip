package barry;

import java.util.Arrays;
import java.util.List;

import barry.exception.BarryException;
import barry.parser.ParsedInput;
import barry.parser.Parser;
import barry.storage.Storage;
import barry.task.Deadline;
import barry.task.Event;
import barry.task.Task;
import barry.task.TaskList;
import barry.task.ToDo;
import barry.ui.Ui;

/**
 * The main entry point and orchestrator for the Barry chatbot application.
 *
 * <p>Barry coordinates interactions between the {@code Ui} (user I/O), {@code Parser} (command parsing),
 * {@code TaskList} (in-memory task state), and {@code Storage} (persistence).
 * It runs the main input loop, executes user commands, and saves tasks whenever the list changes.</p>
 */
public class Barry {
    private final Ui ui;
    private final TaskList userList;
    private final Storage storage;

    /**
     * Creates a new Barry chatbot instance.
     * Initializes UI and storage components, then attempts to load
     * saved tasks from the given file path.
     * If loading fails (e.g., corrupted file),
     * Barry starts with an empty task list and reports the error to the user.
     *
     * @param filePath Relative path to the save file (e.g.,"./data/barry.txt").
     */
    public Barry(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (BarryException e) {
            loaded = new TaskList();
            ui.showLoadingError("Saved data was corrupted. Starting a new file. " + e.getMessage());
        }
        this.userList = loaded;
    }

    /**
     * Runs the main interaction loop of the chatbot.
     *
     * Reads user commands from the UI, parses them using {@link barry.parser.Parser},
     * executes the corresponding operations on the task list,
     * and saves changes via {@link barry.storage.Storage}
     * when the task list is modified.
     */
    public void run() {
        ui.showWelcome();

        boolean isRunning = true;
        while (isRunning) {
            try {
                String input = ui.readCommand();
                ParsedInput p = Parser.parse(input);

                switch (p.type) {
                case LIST: {
                    ui.showTaskList(this.userList);
                    break;
                }
                case TODO: {
                    Task task = new ToDo(p.name);
                    userList.add(task);
                    ui.showTaskAdded(task, userList.size());
                    storage.save(userList);
                    break;
                }
                case DEADLINE: {
                    Task task = new Deadline(p.name, p.by);
                    userList.add(task);
                    ui.showTaskAdded(task, userList.size());
                    storage.save(userList);
                    break;
                }
                case EVENT: {
                    Task task = new Event(p.name, p.start, p.end);
                    userList.add(task);
                    ui.showTaskAdded(task, userList.size());
                    storage.save(userList);
                    break;
                }
                case MARK: {
                    handleMark(p.taskNumbers);
                    storage.save(userList);
                    break;
                }
                case UNMARK: {
                    handleUnmark(p.taskNumbers);
                    storage.save(userList);
                    break;
                }
                case DELETE: {
                    handleDelete(p.taskNumbers);
                    storage.save(userList);
                    break;
                }
                case FIND: {
                    List<TaskList.IndexedTask> matches = userList.findByKeyword(p.name);
                    ui.showFindResults(matches);
                    break;
                }
                case BYE: {
                    ui.showBye();
                    isRunning = false;
                    break;
                }
                default:
                    throw new BarryException("Invalid command: To add a new task, "
                            + "start the command with 'todo', 'deadline', or 'event'");
                }
            } catch (BarryException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    private void handleMark(int[] nums) throws BarryException {
        for (int n : nums) {
            userList.checkIndex1Based(n);
            Task task = userList.get(n - 1);
            task.mark();
            ui.showTaskMarked(task);
        }
    }

    private void handleUnmark(int[] nums) throws BarryException {
        for (int n : nums) {
            userList.checkIndex1Based(n);
            Task task = userList.get(n - 1);
            task.unmark();
            ui.showTaskUnmarked(task);
        }
    }

    private void handleDelete(int[] nums) throws BarryException {
        int[] copy = Arrays.copyOf(nums, nums.length);
        Arrays.sort(copy);

        for (int i = copy.length - 1; i >= 0; i--) {
            int n = copy[i];
            userList.checkIndex1Based(n);
            Task removed = userList.remove(n - 1);
            ui.showTaskDeleted(removed, userList.size());
        }
    }

    /**
     * Entry point of the application.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Barry("./data/barry.txt").run();
    }
}
