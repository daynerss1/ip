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
    private static final DateTimeFormatter SAVE_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HHmm");
    private final Path filePath;

    /**
     * Constructs a storage component that reads/writes tasks to a file.
     *
     * @param filePath Relative path to the save file (e.g., {@code "./data/barry.txt"}).
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the save file.
     *
     * If the parent directory does not exist, it will be created.
     * If the save file does not exist,
     * this method returns an empty task list.
     *
     * @return A list of tasks loaded from disk.
     * @throws BarryException If the save file exists but cannot be read or contains corrupted lines.
     */
    public ArrayList<Task> load() throws BarryException {
        ensureParentDirExists(); // throws BarryException if unable to create parent directory
        if (!Files.exists(this.filePath)) {
            System.out.println("No data file exists yet. Starting an empty task list!");
            return new ArrayList<>();
        }

        try {
            List<String> linesFromFile = Files.readAllLines(this.filePath);
            ArrayList<Task> tasks = new ArrayList<>();
            for (String line : linesFromFile) {
                if (!line.trim().isEmpty()) {
                    tasks.add(parseLineToTasks(line)); // throws BarryException if unable to parse line
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new BarryException("Failed to load saved tasks: " + e.getMessage());
        }
    }

    /**
     * Ensures the parent directory of the save file exists by creating it if needed.
     *
     * @throws BarryException If the folder cannot be created due to an I/O error.
     */
    public void ensureParentDirExists() throws BarryException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new BarryException("Failed to create data folder: " + e.getMessage());
        }
    }

    /**
     * Parses a single line from the save file into a {@link Task}.
     *
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
        Task task;
        // Follow format from CS2103 iP task description
        String[] parts = line.split("\\s*\\|\\s*"); // Split by " | "
        if (parts.length < 3) {
            throw new BarryException("Corrupted save file line: " + line);
        }

        String type = parts[0].trim();
        String doneString = parts[1].trim();
        String desc = parts[2].trim();

        boolean isDone = false;
        if (doneString.equals("1")) {
            isDone = true;
        } else if (!doneString.equals("0")) {
            throw new BarryException("Corrupted done flag in line: " + line);
        }

        try {
            switch (type) {
            case "T":
                task = new ToDo(desc);
                break;
            case "D":
                if (parts.length < 4) {
                    throw new BarryException("Corrupted deadline line: " + line);
                }
                LocalDateTime by = LocalDateTime.parse(parts[3].trim(), SAVE_DATE_TIME_FORMAT);
                task = new Deadline(desc, by);
                break;
            case "E":
                if (parts.length < 5) {
                    throw new BarryException("Corrupted event line: " + line);
                }
                LocalDateTime start = LocalDateTime.parse(parts[3].trim(), SAVE_DATE_TIME_FORMAT);
                LocalDateTime end = LocalDateTime.parse(parts[4].trim(), SAVE_DATE_TIME_FORMAT);
                task = new Event(desc, start, end);
                break;
            default:
                throw new BarryException("Unknown task type in data file: " + line);
            }
        } catch (DateTimeParseException e) {
            throw new BarryException("Corrupted date/time in data file line: " + line);
        }

        if (isDone) {
            task.mark();
        } else {
            task.unmark();
        }

        return task;
    }

    /**
     * Saves the given tasks to the save file.
     *
     * Overwrites any existing file contents. Creates the parent directory if necessary.
     *
     * @param tasks The tasks to save.
     * @throws BarryException If writing fails due to an I/O error.
     */
    public void save(TaskList tasks) throws BarryException {
        ensureParentDirExists();

        try (FileWriter fw = new FileWriter(filePath.toFile(), false)) {
            for (int i = 0; i < tasks.size(); i++) {
                fw.write(taskToLine(tasks.get(i)));
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
        String done = task.isDone() ? "1" : "0";
        final String separator = " | ";

        if (task instanceof ToDo) {
            return "T" + separator + done + separator + task.getName();

        } else if (task instanceof Deadline) {
            Deadline dlTask = (Deadline) task;
            String byString = dlTask.getBy().format(SAVE_DATE_TIME_FORMAT);
            return "D" + separator + done + separator + dlTask.getName() + separator + byString;

        } else if (task instanceof Event) {
            Event eventTask = (Event) task;
            String startString = eventTask.getFrom().format(SAVE_DATE_TIME_FORMAT);
            String endString = eventTask.getTo().format(SAVE_DATE_TIME_FORMAT);
            return "E" + separator + done + separator + eventTask.getName() + separator
                    + startString + separator + endString;
        } else {
            throw new BarryException("Unknown task type, unable to save.");
        }
    }
}
