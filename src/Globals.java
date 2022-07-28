import javafx.stage.Stage;

/**
 *  Used to store globally scoped variables to be shared between classes
 */
public interface Globals {
    Database database = new Database();
    User authenticatedUser = new User();
}
