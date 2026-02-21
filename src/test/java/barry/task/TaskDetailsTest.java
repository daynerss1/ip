package barry.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task-detail equality behavior via {@code hasSameDetails}.
 *
 * <p>Author note: AI assistance was used to generate an initial set of scenarios,
 * and they were validated to ensure alignment with the project's expected behavior.</p>
 */
class TaskDetailsTest {

    @Test
    void todo_sameNameAndHasSameDetails_true() {
        Task a = new ToDo("read");
        Task b = new ToDo("read");
        b.mark();

        assertTrue(a.hasSameDetails(b));
    }

    @Test
    void deadline_differentDateAndHasSameDetails_false() {
        Task a = new Deadline("submit", LocalDateTime.of(2026, 2, 1, 10, 0));
        Task b = new Deadline("submit", LocalDateTime.of(2026, 2, 2, 10, 0));

        assertFalse(a.hasSameDetails(b));
    }

    @Test
    void event_sameFieldsAndHasSameDetails_true() {
        Task a = new Event("meeting", LocalDateTime.of(2026, 2, 1, 10, 0),
                LocalDateTime.of(2026, 2, 1, 12, 0));
        Task b = new Event("meeting", LocalDateTime.of(2026, 2, 1, 10, 0),
                LocalDateTime.of(2026, 2, 1, 12, 0));

        assertTrue(a.hasSameDetails(b));
    }

    @Test
    void differentTaskTypes_sameNameAndHasSameDetails_false() {
        Task todo = new ToDo("plan");
        Task deadline = new Deadline("plan", LocalDateTime.of(2026, 2, 1, 10, 0));

        assertFalse(todo.hasSameDetails(deadline));
    }
}
