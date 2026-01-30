import java.io.IOException;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.List;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents the data file in the hard disk that stores the user's task list.
 * Automatically loads the data in the file when the driver is started, and automatically
 * updates the save file whenever the task list changes.
 */

public class Storage {
    private final Path filePath;
    private static final DateTimeFormatter SAVE_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HHmm");

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

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

    public void ensureParentDirExists() throws BarryException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {                   // if parent is null, there is no parent
                Files.createDirectories(parent);    // directory to be created
            }
        } catch (IOException e) {
            throw new BarryException("Failed to create data folder: " + e.getMessage());
        }
    }

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

    public void save(List<Task> tasks) throws BarryException {
        ensureParentDirExists();

        try (FileWriter fw = new FileWriter(filePath.toFile(), false)) {
            for (Task task : tasks) {
                fw.write(taskToLine(task));
                fw.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new BarryException("Failed to save tasks: " + e.getMessage());
        }
    }

    public String taskToLine(Task task) throws BarryException {
        String done = task.isDone() ? "1" : "0";
        final String SEPARATOR = " | ";

        if (task instanceof ToDo) {
            return "T" + SEPARATOR + done + SEPARATOR + task.getName();

        } else if (task instanceof Deadline) {
            Deadline dlTask = (Deadline) task;
            String byString = dlTask.getBy().format(SAVE_DATE_TIME_FORMAT);
            return "D" + SEPARATOR + done + SEPARATOR + dlTask.getName() + SEPARATOR + byString;

        } else if (task instanceof Event) {
            Event eventTask = (Event) task;
            String startString = eventTask.getFrom().format(SAVE_DATE_TIME_FORMAT);
            String endString = eventTask.getTo().format(SAVE_DATE_TIME_FORMAT);
            return "E" + SEPARATOR + done + SEPARATOR + eventTask.getName() + SEPARATOR
                    + startString + SEPARATOR + endString;
        } else {
            throw new BarryException("Unknown task type, unable to save.");
        }
    }
}
