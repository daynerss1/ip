package barry.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import barry.exception.BarryException;
import barry.task.Deadline;
import barry.task.Event;
import barry.task.Task;
import barry.task.TaskList;
import barry.task.ToDo;

class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void taskToLineAndParseLine_roundTrip_deadline() throws Exception {
        Storage storage = new Storage("./data/test.txt"); // path won’t be used here
        LocalDateTime by = LocalDateTime.of(2026, 1, 30, 14, 0);

        Task original = new Deadline("return book", by);
        original.mark();

        String line = storage.taskToLine(original);
        Task parsed = storage.parseLineToTasks(line);

        assertTrue(parsed instanceof Deadline);
        assertTrue(parsed.isDone());
        assertEquals("return book", parsed.getName());

        Deadline d = (Deadline) parsed;
        assertEquals(by, d.getBy());
    }

    @Test
    void taskToLineAndParseLine_roundTrip_event() throws Exception {
        Storage storage = new Storage("./data/test.txt");
        LocalDateTime start = LocalDateTime.of(2026, 1, 30, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 30, 16, 0);

        Task original = new Event("meeting", start, end);
        String line = storage.taskToLine(original);

        Task parsed = storage.parseLineToTasks(line);

        assertTrue(parsed instanceof Event);
        Event e = (Event) parsed;
        assertEquals("meeting", e.getName());
        assertEquals(start, e.getFrom());
        assertEquals(end, e.getTo());
    }

    @Test
    void parseLine_corruptedType_throwsBarryException() {
        Storage storage = new Storage("./data/test.txt");

        assertThrows(BarryException.class, () -> storage
                .parseLineToTasks("X | 0 | something"));
    }

    @Test
    void parseLine_corruptedDoneFlag_throwsBarryException() {
        Storage storage = new Storage("./data/test.txt");

        assertThrows(BarryException.class, () -> storage
                .parseLineToTasks("T | 9 | read book"));
    }

    @Test
    void parseLine_deadlineMissingDate_throwsBarryException() {
        Storage storage = new Storage("./data/test.txt");

        assertThrows(BarryException.class, () -> storage
                .parseLineToTasks("D | 0 | return book"));
    }

    @Test
    void dataFileExists_beforeAndAfterSave_reflectsState() throws Exception {
        Path file = tempDir.resolve("barry-storage-exists.txt");
        Storage storage = new Storage(file.toString());
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("a"));

        assertTrue(!storage.dataFileExists());
        storage.save(taskList);
        assertTrue(storage.dataFileExists());
    }

    @Test
    void saveAndLoad_roundTrip_preservesTasks() throws Exception {
        Path file = tempDir.resolve("barry-storage-roundtrip.txt");
        Storage storage = new Storage(file.toString());
        TaskList toSave = new TaskList();

        Task todo = new ToDo("read");
        Task deadline = new Deadline("submit", LocalDateTime.of(2026, 2, 1, 10, 0));
        deadline.mark();
        Task event = new Event("meeting", LocalDateTime.of(2026, 2, 1, 12, 0),
                LocalDateTime.of(2026, 2, 1, 13, 0));
        toSave.addTask(todo);
        toSave.addTask(deadline);
        toSave.addTask(event);

        storage.save(toSave);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0) instanceof ToDo);
        assertTrue(loaded.get(1) instanceof Deadline);
        assertTrue(loaded.get(1).isDone());
        assertTrue(loaded.get(2) instanceof Event);
        assertEquals("read", loaded.get(0).getName());
    }
}
