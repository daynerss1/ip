package barry.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 *
 * <p>Implementation note (author perspective):
 * I used AI assistance to rapidly iterate on the GUI structure for this class.
 * AI helped me map visual requirements (asymmetric chat layout, error highlighting,
 * and CSS-driven styling) into concrete JavaFX code, while I reviewed and kept
 * the final architecture aligned with my existing codebase.</p>
 */
public class DialogBox extends HBox {
    private static final String STYLE_DIALOG_BOX = "dialog-box";
    private static final String STYLE_USER_DIALOG = "user-dialog";
    private static final String STYLE_BARRY_DIALOG = "barry-dialog";
    private static final String STYLE_BARRY_ERROR_DIALOG = "barry-error-dialog";
    private static final String STYLE_LABEL = "label";
    private static final String STYLE_DIALOG_TEXT = "dialog-text";
    private static final String STYLE_USER_TEXT = "user-text";
    private static final String STYLE_BARRY_TEXT = "barry-text";
    private static final String STYLE_BARRY_ERROR_TEXT = "barry-error-text";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    private void applyUserStyle() {
        // I used AI to suggest keeping style assignment centralized so UI tweaks stay in CSS.
        getStyleClass().setAll(STYLE_DIALOG_BOX, STYLE_USER_DIALOG);
        dialog.getStyleClass().setAll(STYLE_LABEL, STYLE_DIALOG_TEXT, STYLE_USER_TEXT);
    }

    private void applyBarryStyle(boolean isError) {
        // AI suggested exposing an explicit error branch so invalid-command replies stand out visually.
        if (isError) {
            getStyleClass().setAll(STYLE_DIALOG_BOX, STYLE_BARRY_ERROR_DIALOG);
            dialog.getStyleClass().setAll(STYLE_LABEL, STYLE_DIALOG_TEXT, STYLE_BARRY_ERROR_TEXT);
            return;
        }
        getStyleClass().setAll(STYLE_DIALOG_BOX, STYLE_BARRY_DIALOG);
        dialog.getStyleClass().setAll(STYLE_LABEL, STYLE_DIALOG_TEXT, STYLE_BARRY_TEXT);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.applyUserStyle();
        return db;
    }

    public static DialogBox getBarryDialog(String text, Image img) {
        return getBarryDialog(text, img, false);
    }

    public static DialogBox getBarryDialog(String text, Image img, boolean isError) {
        var db = new DialogBox(text, img);
        db.flip();
        db.applyBarryStyle(isError);
        return db;
    }
}
