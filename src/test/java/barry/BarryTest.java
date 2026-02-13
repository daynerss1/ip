package barry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

        assertTrue(response.contains("Here are the commands you can use:"));
        assertTrue(response.contains("todo <description>"));
        assertTrue(response.contains("find <keyword>"));
    }

    @Test
    void firstRun_noExistingDataFile_showsStartupMessage() {
        Barry barry = new Barry(tempDir.resolve("barry-first-run.txt").toString());

        String startupMessage = barry.consumeStartupMessage();

        assertNotNull(startupMessage);
        assertTrue(startupMessage.contains("first run"));
        assertTrue(startupMessage.contains("Type 'help' to view all commands."));
    }

    @Test
    void firstRun_noExistingDataFile_usesShortWelcomeMessage() {
        Barry barry = new Barry(tempDir.resolve("barry-first-run-welcome.txt").toString());

        String welcomeMessage = barry.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Hello, I'm Barry!"));
        assertFalse(welcomeMessage.contains("What can I do for you?"));
    }
}
