package barry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BarryTest {

    @TempDir
    Path tempDir;

    @Test
    void getResponse_helpCommand_returnsHelpPage() {
        Barry barry = new Barry(tempDir.resolve("barry-help.txt").toString());

        String response = barry.getResponse("help");

        assertTrue(response.contains("Navigation commands:"));
        assertTrue(response.contains("todo <description>"));
        assertTrue(response.contains("find <keyword>"));
        assertFalse(response.contains("___"));
    }

    @Test
    void firstRun_noExistingDataFile_showsStartupMessage() {
        Barry barry = new Barry(tempDir.resolve("barry-first-run.txt").toString());

        String startupMessage = barry.consumeStartupMessage();

        assertNotNull(startupMessage);
        assertTrue(startupMessage.contains("First voyage"));
        assertTrue(startupMessage.contains("Type 'help' to view all navigation commands."));
    }

    @Test
    void firstRun_noExistingDataFile_usesShortWelcomeMessage() {
        Barry barry = new Barry(tempDir.resolve("barry-first-run-welcome.txt").toString());

        String welcomeMessage = barry.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Ahoy, I'm Captain Barry."));
        assertFalse(welcomeMessage.contains("What can I do for you?"));
    }

    @Test
    void getResponse_errorAndSuccess_updatesErrorState() {
        Barry barry = new Barry(tempDir.resolve("barry-error-state.txt").toString());

        barry.getResponse("bad-command");
        assertTrue(barry.wasLastResponseError());

        barry.getResponse("help");
        assertFalse(barry.wasLastResponseError());
    }

    @Test
    void getResponse_duplicateTask_returnsError() {
        Barry barry = new Barry(tempDir.resolve("barry-duplicate-task.txt").toString());

        String firstResponse = barry.getResponse("todo read book");
        String duplicateResponse = barry.getResponse("todo read book");

        assertTrue(firstResponse.toLowerCase().contains("logged"));
        assertTrue(duplicateResponse.toLowerCase().contains("duplicate task"));
        assertTrue(barry.wasLastResponseError());
    }

    @Test
    void getResponse_byeWithTrailingSpaces_exitsMessageReturned() {
        Barry barry = new Barry(tempDir.resolve("barry-bye-spaces.txt").toString());

        String response = barry.getResponse(" bye   ");

        assertTrue(response.toLowerCase().contains("smooth sailing"));
        assertFalse(barry.wasLastResponseError());
    }

    @Test
    void getResponse_byeWithExtraText_returnsError() {
        Barry barry = new Barry(tempDir.resolve("barry-bye-extra.txt").toString());

        String response = barry.getResponse("bye x");

        assertTrue(response.toLowerCase().contains("extra arguments"));
        assertTrue(barry.wasLastResponseError());
    }
}
