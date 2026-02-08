package barry;

import java.util.ArrayList;
import java.util.Arrays;

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
    private String startupMessage = null;

    /**
     * Creates a new Barry chatbot instance.
     * Initializes UI and storage components, then attempts to load
     * saved tasks from the given file path.
     * If loading fails (e.g., corrupted file),
     * Barry starts with an empty task list and reports the error to the user.
     *
     * @param filePath Relative path to the save file (e.g., "./data/barry.txt").
     */
    public Barry(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (BarryException e) {
            loaded = new TaskList();
            startupMessage = ui.formatLoadingError(
                    "Saved data was corrupted. Starting a new file. "
                            + e.getMessage()
            );
        }
        this.userList = loaded;
    }

    /**
     * Helps the FXML controller to print the welcome message.
     * @return The welcome message.
     */
    public String getWelcomeMessage() {
        return ui.formatWelcome();
    }

    /**
     * Helps the FMXL controller to print the load error message, if applicable.
     * @return the load error message, if any.
     */
    public String consumeStartupMessage() {
        String msg = startupMessage;
        startupMessage = null;
        return msg;
    }

    /**
     * Runs the main interaction loop of the chatbot.
     * Reads user commands from the UI, parses them using {@link barry.parser.Parser},
     * executes the corresponding operations on the task list,
     * and saves changes via {@link barry.storage.Storage}
     * when the task list is modified.
     */
    public String getResponse(String input) {
        try {
            ParsedInput p = Parser.parse(input);

            switch (p.type) {
            case LIST:
                return ui.formatTaskList(userList);

            case TODO: {
                Task task = new ToDo(p.name);
                userList.add(task);
                storage.save(userList);
                return ui.formatTaskAdded(task, userList.size());
            }

            case DEADLINE: {
                Task task = new Deadline(p.name, p.by);
                userList.add(task);
                storage.save(userList);
                return ui.formatTaskAdded(task, userList.size());
            }

            case EVENT: {
                Task task = new Event(p.name, p.start, p.end);
                userList.add(task);
                storage.save(userList);
                return ui.formatTaskAdded(task, userList.size());
            }

            case MARK:
                return handleMarkToString(p.taskNumbers);

            case UNMARK:
                return handleUnmarkToString(p.taskNumbers);

            case DELETE:
                return handleDeleteToString(p.taskNumbers);

            case FIND:
                return ui.formatFindResults(userList.findByKeyword(p.name));

            case BYE:
                return ui.formatBye();

            default:
                return ui.formatError("Unknown command.");
            }
        } catch (BarryException e) {
            return ui.formatError(e.getMessage());
        }
    }

    private String handleMarkToString(int... nums) throws BarryException {
        StringBuilder sb = new StringBuilder();
        for (int n : nums) {
            userList.checkIndex1Based(n);
            Task task = userList.get(n - 1);
            task.mark();
            sb.append(ui.formatTaskMarked(task)).append("\n");
        }
        storage.save(userList);
        return sb.toString().trim();
    }

    private String handleUnmarkToString(int... nums) throws BarryException {
        StringBuilder sb = new StringBuilder();
        for (int n : nums) {
            userList.checkIndex1Based(n);
            Task task = userList.get(n - 1);
            task.unmark();
            sb.append(ui.formatTaskUnmarked(task)).append("\n");
        }
        storage.save(userList);
        return sb.toString().trim();
    }

    private String handleDeleteToString(int... nums) throws BarryException {
        StringBuilder sb = new StringBuilder();
        int[] copy = Arrays.copyOf(nums, nums.length);
        Arrays.sort(copy);
        ArrayList<String> outputPlaceholder = new ArrayList<>();
        int initialSize = userList.size();
        for (int n : nums) {
            Task taskToDelete = userList.get(n - 1);
            outputPlaceholder.add(ui.formatTaskDeleted(taskToDelete, --initialSize));
        }

        for (int i = copy.length - 1; i >= 0; i--) {
            int n = copy[i];
            userList.checkIndex1Based(n);
            userList.remove(n - 1);
        }
        storage.save(userList);
        for (String s : outputPlaceholder) {
            sb.append(s);
        }
        return sb.toString().trim();
    }
}
