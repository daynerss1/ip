package barry.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import barry.exception.BarryException;
import barry.task.Deadline;
import barry.task.Event;
import barry.task.Task;
import barry.task.TaskList;
import barry.task.ToDo;

/**
 * Represents the data file in the hard disk that stores the user's task list.
 * Automatically loads the data in the file when the driver is started, and automatically
 * updates the save file whenever the task list changes.
 */
public class Storage {
    private static final String SAVE_DATE_TIME_PATTERN = "yyyy-MM-dd HHmm";
    private static final DateTimeFormatter SAVE_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern(SAVE_DATE_TIME_PATTERN);
    private static final String DONE_FLAG_TRUE = "1";
    private static final String DONE_FLAG_FALSE = "0";
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String FIELD_SEPARATOR = " | ";
    private static final String INITIAL_FILE_CREATION_MESSAGE = "No data file exists yet. Starting an empty task list!";
    private static final String ERROR_LOAD_FAILED = "Failed to load saved tasks: ";
    private static final String ERROR_CREATE_FOLDER_FAILED = "Failed to create data folder: ";
    private static final String ERROR_CORRUPTED_LINE = "Corrupted save file line: ";
    private static final String ERROR_CORRUPTED_DONE_FLAG = "Corrupted done flag in line: ";
    private static final String ERROR_CORRUPTED_DEADLINE = "Corrupted deadline line: ";
    private static final String ERROR_CORRUPTED_EVENT = "Corrupted event line: ";
    private static final String ERROR_UNKNOWN_TASK_TYPE = "Unknown task type in data file: ";
    private static final String ERROR_CORRUPTED_DATE_TIME = "Corrupted date/time in data file line: ";
    private static final String ERROR_UNKNOWN_TASK_TYPE_SAVE = "Unknown task type, unable to save.";
    private final Path filePath;

    /**
     * Constructs a storage component that reads/writes tasks to a file.
     *
     * @param filePath Relative path to the save file (e.g., {@code "./data/barry.txt"}).
     */
    public Storage(String filePath) {
        assert filePath != null : "filePath must not be null";
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the save file.
     * If the parent directory does not exist, it will be created.
     * If the save file does not exist,
     * this method returns an empty task list.
     *
     * @return A list of tasks loaded from disk.
     * @throws BarryException If the save file exists but cannot be read or contains corrupted lines.
     */
    public ArrayList<Task> load() throws BarryException {
        ensureParentDirectoryExists();
        List<String> linesFromFile = readAllLinesOrEmpty();
        assert linesFromFile != null : "lines from file must not be null";
        return parseTasksFromLines(linesFromFile);
    }

    /**
     * Ensures the parent directory of the save file exists by creating it if needed.
     *
     * @throws BarryException If the folder cannot be created due to an I/O error.
     */
    public void ensureParentDirectoryExists() throws BarryException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new BarryException(ERROR_CREATE_FOLDER_FAILED + e.getMessage());
        }
    }

    private List<String> readAllLinesOrEmpty() throws BarryException {
        // Guard condition if file path does not exist yet.
        if (!Files.exists(this.filePath)) {
            System.out.println(INITIAL_FILE_CREATION_MESSAGE);
            return new ArrayList<>();
        }

        try {
            return Files.readAllLines(this.filePath);
        } catch (IOException e) {
            throw new BarryException(ERROR_LOAD_FAILED + e.getMessage());
        }
    }

    private ArrayList<Task> parseTasksFromLines(List<String> linesFromFile) throws BarryException {
        ArrayList<Task> tasks = new ArrayList<>();
        for (String line : linesFromFile) {
            if (!line.trim().isEmpty()) {
                tasks.add(parseLineToTasks(line));
            }
        }
        return tasks;
    }

    /**
     * Parses a single line from the save file into a {@link Task}.
     * Expected format (pipe-separated):
     * <ul>
     *   <li>{@code T | doneFlag | description}</li>
     *   <li>{@code D | doneFlag | description | yyyy-MM-dd HHmm}</li>
     *   <li>{@code E | doneFlag | description | yyyy-MM-dd HHmm | yyyy-MM-dd HHmm}</li>
     * </ul>
     *
     * @param line A non-empty line from the save file.
     * @return A task constructed from the data in the line.
     * @throws BarryException If the line format, done flag, or task type is invalid/corrupted.
     */
    public Task parseLineToTasks(String line) throws BarryException {
        assert line != null : "line must not be null";
        String[] parts = splitSaveLine(line);
        boolean isDone = parseDoneFlag(parts[1].trim(), line);
        Task task = parseTaskFromParts(parts, line);
        assert task != null : "task must not be null";
        applyDoneState(task, isDone);
        return task;
    }

    private String[] splitSaveLine(String line) throws BarryException {
        String[] parts = line.split("\\s*\\|\\s*");
        assert parts.length >= 1 : "parts must not be empty";

        // All lines have at least these 3 parts: type | doneState | description.
        if (parts.length < 3) {
            throw new BarryException(ERROR_CORRUPTED_LINE + line);
        }
        return parts;
    }

    private boolean parseDoneFlag(String doneString, String line) throws BarryException {
        if (DONE_FLAG_TRUE.equals(doneString)) {
            return true;
        }
        if (DONE_FLAG_FALSE.equals(doneString)) {
            return false;
        }
        throw new BarryException(ERROR_CORRUPTED_DONE_FLAG + line);
    }

    private Task parseTaskFromParts(String[] parts, String line) throws BarryException {
        String type = parts[0].trim();
        String desc = parts[2].trim();

        try {
            switch (type) {
            case TYPE_TODO:
                return new ToDo(desc);
            case TYPE_DEADLINE:
                // Deadline has 4 parts: type, doneState, description, byDate
                ensureMinimumParts(parts, 4, ERROR_CORRUPTED_DEADLINE + line);
                LocalDateTime by = parseSavedDateTime(parts[3].trim(), line);
                return new Deadline(desc, by);
            case TYPE_EVENT:
                // Event has 5 parts: type, doneState, description, startDate, endDate
                ensureMinimumParts(parts, 5, ERROR_CORRUPTED_EVENT + line);
                LocalDateTime start = parseSavedDateTime(parts[3].trim(), line);
                LocalDateTime end = parseSavedDateTime(parts[4].trim(), line);
                return new Event(desc, start, end);
            default:
                throw new BarryException(ERROR_UNKNOWN_TASK_TYPE + line);
            }
        } catch (DateTimeParseException e) {
            throw new BarryException(ERROR_CORRUPTED_DATE_TIME + line);
        }
    }

    private void ensureMinimumParts(String[] parts, int minParts, String errorMessage) throws BarryException {
        if (parts.length < minParts) {
            throw new BarryException(errorMessage);
        }
    }

    private void applyDoneState(Task task, boolean isDone) {
        assert task != null : "task must not be null";
        // Default task on loading is a new, unmarked Task object.
        if (isDone) {
            task.mark();
        }
    }

    /**
     * Saves the given tasks to the save file.
     * Overwrites any existing file contents. Creates the parent directory if necessary.
     *
     * @param tasks The tasks to save.
     * @throws BarryException If writing fails due to an I/O error.
     */
    public void save(TaskList tasks) throws BarryException {
        ensureParentDirectoryExists();

        try (FileWriter fw = new FileWriter(filePath.toFile(), false)) {
            for (int i = 0; i < tasks.size(); i++) {
                fw.write(taskToLine(tasks.getTask(i)));
                fw.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new BarryException("Failed to save tasks: " + e.getMessage());
        }
    }

    /**
     * Converts a {@link Task} into a single-line representation suitable for saving to disk.
     *
     * @param task The task to convert.
     * @return A pipe-separated line encoding the task.
     * @throws BarryException If the task type is unknown and cannot be serialized.
     */
    public String taskToLine(Task task) throws BarryException {
        String done = task.isDone() ? DONE_FLAG_TRUE : DONE_FLAG_FALSE;

        if (task instanceof ToDo todoTask) {
            return serializeToDo(todoTask, done);
        }
        if (task instanceof Deadline deadlineTask) {
            return serializeDeadline(deadlineTask, done);
        }
        if (task instanceof Event eventTask) {
            return serializeEvent(eventTask, done);
        }

        throw new BarryException(ERROR_UNKNOWN_TASK_TYPE_SAVE);
    }

    private LocalDateTime parseSavedDateTime(String value, String line) {
        return LocalDateTime.parse(value, SAVE_DATE_TIME_FORMAT);
    }

    private String serializeToDo(ToDo task, String done) {
        return TYPE_TODO + FIELD_SEPARATOR + done + FIELD_SEPARATOR + task.getName();
    }

    private String serializeDeadline(Deadline task, String done) {
        String byString = task.getBy().format(SAVE_DATE_TIME_FORMAT);
        return TYPE_DEADLINE + FIELD_SEPARATOR + done + FIELD_SEPARATOR + task.getName()
                + FIELD_SEPARATOR + byString;
    }

    private String serializeEvent(Event task, String done) {
        String startString = task.getFrom().format(SAVE_DATE_TIME_FORMAT);
        String endString = task.getTo().format(SAVE_DATE_TIME_FORMAT);
        return TYPE_EVENT + FIELD_SEPARATOR + done + FIELD_SEPARATOR + task.getName()
                + FIELD_SEPARATOR + startString + FIELD_SEPARATOR + endString;
    }
}
