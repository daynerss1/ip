package barry;

import barry.ui.Ui;
import barry.storage.Storage;
import barry.task.TaskList;
import barry.task.Task;
import barry.task.ToDo;
import barry.task.Deadline;
import barry.task.Event;
import barry.parser.Parser;
import barry.parser.ParsedInput;
import barry.exception.BarryException;

import java.util.Arrays;
import java.util.List;

public class Barry {
    private final Ui ui;
    private final TaskList userList;
    private final Storage storage;

    public Barry(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (BarryException e) {
            loaded = new TaskList();
            ui.showError("Saved data was corrupted. Starting a new file. " + e.getMessage());
        }
        this.userList = loaded;
    }

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

    public static void main(String[] args) {
        new Barry("./data/barry.txt").run();
    }
}
