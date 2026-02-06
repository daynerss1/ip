package barry.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import barry.exception.BarryException;
import barry.task.Deadline;
import barry.task.Event;
import barry.task.Task;

class StorageTest {

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
}
