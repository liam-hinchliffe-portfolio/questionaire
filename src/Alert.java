import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Alert {
    Alert (String title, String message) {
        // Create new window for the form where new users can be made
        Stage newUserDialog = new Stage();
        // Only allow the current window to be in focus
        newUserDialog.initModality(Modality.APPLICATION_MODAL);
        newUserDialog.setTitle(title);

        Label body = new Label();
        body.setText(message);

        // Create the layout for the scene
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        // Set max size of layout
        layout.maxHeight(500);
        layout.maxWidth(500);
        // Set padding
        layout.setSpacing(10);
        layout.getChildren().addAll(body);

        // Show the window
        Scene newUserDialogScene = new Scene(layout, 500, 500);
        newUserDialog.setScene(newUserDialogScene);
        newUserDialog.show();
    }

    Alert() {
        this("Alert", "Alert notification");
    }

}
