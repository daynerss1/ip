package barry.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import barry.exception.BarryException;

class ParserTest {

    @Test
    void parseDeadline_validFormat_parsesCorrectly() throws Exception {
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
    void parseDeadline_missingBy_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("deadline return book"));
        assertTrue(e.getMessage().toLowerCase().contains("/by"));
    }

    @Test
    void parseEvent_endBeforeStart_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("event meeting /from 2026-01-30 1600 /to 2026-01-30 1500"));

        assertTrue(e.getMessage().toLowerCase().contains("later"));
    }

    @Test
    void parseEvent_sameStartAndEnd_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("event standup /from 2026-01-30 1600 /to 2026-01-30 1600"));

        assertTrue(e.getMessage().toLowerCase().contains("later"));
    }

    @Test
    void parseMark_multipleNumbers_parsesAll() throws Exception {
        ParsedInput p = Parser.parse("mark 1 3 5");

        assertEquals(Command.MARK, p.type);
        assertArrayEquals(new int[]{1, 3, 5}, p.taskNumbers);
    }

    @Test
    void parseMark_nonInteger_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("mark a"));

        assertTrue(e.getMessage().toLowerCase().contains("integer"));
    }

    @Test
    void parseMark_duplicateTaskNumbers_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("mark 1 1"));

        assertTrue(e.getMessage().toLowerCase().contains("duplicate"));
    }

    @Test
    void parseMark_nonPositiveTaskNumber_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("mark 0"));

        assertTrue(e.getMessage().toLowerCase().contains("positive"));
    }

    @Test
    void parseDeadline_multipleByFlags_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("deadline submit report /by 2026-01-30 1600 /by 2026-01-31 1600"));

        assertTrue(e.getMessage().contains("exactly one '/by'"));
    }

    @Test
    void parseEvent_multipleFromFlags_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("event demo /from 2026-01-30 1600 /from 2026-01-30 1700 /to 2026-01-30 1800"));

        assertTrue(e.getMessage().contains("exactly one '/from'"));
    }

    @Test
    void parseEvent_multipleToFlags_throwsBarryException() {
        BarryException e = assertThrows(BarryException.class, () -> Parser
                .parse("event demo /from 2026-01-30 1600 /to 2026-01-30 1700 /to 2026-01-30 1800"));

        assertTrue(e.getMessage().contains("exactly one '/to'"));
    }

    @Test
    void parseHelp_noArguments_parsesCorrectly() throws Exception {
        ParsedInput p = Parser.parse("help");

        assertEquals(Command.HELP, p.type);
    }
}
