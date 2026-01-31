package barry.parser;

import barry.exception.BarryException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ParserTest {

    @Test
    void parse_deadline_validFormat_parsesCorrectly() throws Exception {
        ParsedInput p = Parser.parse("deadline return book /by 2026-01-30 1400");

        assertEquals(Command.DEADLINE, p.type);
        assertEquals("return book", p.name);
        assertNotNull(p.by);
        assertEquals(2026, p.by.getYear());
        assertEquals(1, p.by.getMonthValue());
        assertEquals(30, p.by.getDayOfMonth());
        assertEquals(14, p.by.getHour());
        assertEquals(0, p.by.getMinute());
    }

    @Test
    void parse_deadline_missingBy_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class,
                () -> Parser.parse("deadline return book"));
        assertTrue(e.getMessage().toLowerCase().contains("/by"));
    }

    @Test
    void parse_event_endBeforeStart_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class,
                () -> Parser.parse("event meeting /from 2026-01-30 1600 /to 2026-01-30 1500"));

        assertTrue(e.getMessage().toLowerCase().contains("before"));
    }

    @Test
    void parse_mark_multipleNumbers_parsesAll() throws Exception {
        ParsedInput p = Parser.parse("mark 1 3 5");

        assertEquals(Command.MARK, p.type);
        assertArrayEquals(new int[]{1, 3, 5}, p.taskNumbers);
    }

    @Test
    void parse_mark_nonInteger_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class,
                () -> Parser.parse("mark a"));

        assertTrue(e.getMessage().toLowerCase().contains("integer"));
    }
}

