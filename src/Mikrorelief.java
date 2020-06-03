import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Mikrorelief {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField login_field;

    @FXML
    private TextField password_field;

    @FXML
    private Button authSingInButton;

    @FXML
    private Button loginSignUpButton;

    @FXML
    void initialize() {
        authSingInButton.setOnAction(event -> System.out.println("вы нажали на кнопку"));

    }
}
