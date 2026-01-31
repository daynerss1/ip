package barry.exception;

/**
 * Represents exceptions specific to the Barry chatbot.
 *
 * Used to signal user input errors, corrupted data files,
 * and other application-level failures
 * that should be reported to the user in a controlled way.
 */
public class BarryException extends Exception {

    /**
     * Constructs a BarryException with the specified message.
     *
     * @param msg Error message describing the failure.
     */
    public BarryException(String msg) {
        super(msg);
    }
}
