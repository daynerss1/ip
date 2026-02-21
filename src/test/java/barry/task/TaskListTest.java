package barry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import barry.exception.BarryException;

/**
 * Tests for {@link TaskList}.
 *
 * <p>Author note: AI assistance was used to help draft and structure these test cases.
 * They were reviewed and adjusted to match this project's expected behavior.</p>
 */
class TaskListTest {

    @Test
    void addAndRemoveTask_updatesSize() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read"));
        list.addTask(new ToDo("write"));

        assertEquals(2, list.size());
        list.removeTask(0);
        assertEquals(1, list.size());
        assertEquals("write", list.getTask(0).getName());
    }

    @Test
    void ensureIndexInRange1Based_outOfRange_throwsBarryException() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("only task"));

        assertThrows(BarryException.class, () -> list.ensureIndexInRange1Based(0));
        assertThrows(BarryException.class, () -> list.ensureIndexInRange1Based(2));
    }

    @Test
    void findByKeyword_caseInsensitive_returnsMatchingIndexes() {
        TaskList list = new TaskList(List.of(
                new ToDo("Read Book"),
                new ToDo("buy milk"),
                new ToDo("book flight")
        ));

        List<TaskList.IndexedTask> matches = list.findByKeyword("BOOK");

        assertEquals(2, matches.size());
        assertEquals(1, matches.get(0).index1Based);
        assertEquals(3, matches.get(1).index1Based);
        assertTrue(matches.get(0).task.getName().toLowerCase().contains("book"));
    }
}
