package barry.ui;

import barry.Barry;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Barry barry;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image barryImage = new Image(this.getClass().getResourceAsStream("/images/CaptainBarry.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setBarry(Barry b) {
        this.barry = b;

        dialogContainer.getChildren().add(
                DialogBox.getBarryDialog(barry.getWelcomeMessage(), barryImage)
        );

        String startUp;
        startUp = barry.consumeStartupMessage();
        if (startUp != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getBarryDialog(startUp, barryImage)
            );
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = barry.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBarryDialog(response, barryImage, barry.wasLastResponseError())
        );
        userInput.clear();

        if (input.trim().equalsIgnoreCase("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
