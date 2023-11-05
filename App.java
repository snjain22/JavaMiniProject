import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class App extends Application {
    private Stage primaryStage;
    private ListView<String> websiteListView;
    private TextArea detailsArea;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Password Manager");

        // Check for authentication
        if (!authenticateUser()) {
            return; // Exit if authentication fails
        }

        setupMainApplication();
    }

    private boolean authenticateUser() {
        Stage authStage = new Stage();
        authStage.initModality(Modality.APPLICATION_MODAL);
        authStage.initOwner(primaryStage);
        authStage.setTitle("Authentication");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        
        Button loginButton = new Button("Login");

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if ("DEMO".equals(username) && "PASSWORD".equals(password)) {
                authStage.close();
            } else {
                showErrorAlert("Authentication Failed", "Invalid username or password.");
            }
        });

        VBox authLayout = new VBox(10);
        authLayout.setPadding(new Insets(10));
        authLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        Scene authScene = new Scene(authLayout);
        authStage.setScene(authScene);
        authStage.showAndWait();

        return !authStage.isShowing();
    }

    private void setupMainApplication() {
        primaryStage.setTitle("Password Manager");

        websiteListView = new ListView<>();
        websiteListView.getItems().addAll("Website 1", "Website 2", "Website 3");

        websiteListView.setOnMouseClicked(e -> showDetails());

        Button addPasswordButton = new Button("Add Password");
        addPasswordButton.setOnAction(e -> showPasswordForm());

        HBox mainLayout = new HBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(websiteListView, addPasswordButton);

        detailsArea = new TextArea("Password Manager");
        detailsArea.setEditable(false);
        detailsArea.setMinWidth(300);

        VBox mainPane = new VBox(10);
        mainPane.getChildren().addAll(mainLayout, detailsArea);

        Scene mainScene = new Scene(mainPane, 600, 400);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void showPasswordForm() {
        Stage passwordFormStage = new Stage();
        passwordFormStage.initModality(Modality.APPLICATION_MODAL);
        passwordFormStage.initOwner(primaryStage);
        passwordFormStage.setTitle("Add Password");

        Label websiteLabel = new Label("Website:");
        TextField websiteField = new TextField();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        CheckBox showPasswordCheckbox = new CheckBox("Show Password");

        Button saveButton = new Button("Save");

        showPasswordCheckbox.setOnAction(e -> {
            passwordField.setManaged(showPasswordCheckbox.isSelected());
            passwordField.setVisible(showPasswordCheckbox.isSelected());
        });

        saveButton.setOnAction(e -> {
            String website = websiteField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            websiteListView.getItems().add(website);
            passwordFormStage.close();
        });

        VBox passwordFormLayout = new VBox(10);
        passwordFormLayout.setPadding(new Insets(10));
        passwordFormLayout.getChildren().addAll(websiteLabel, websiteField, usernameLabel, usernameField, passwordLabel, passwordField, showPasswordCheckbox, saveButton);

        Scene passwordFormScene = new Scene(passwordFormLayout);
        passwordFormStage.setScene(passwordFormScene);
        passwordFormStage.showAndWait();
    }

    private void showDetails() {
        String selectedWebsite = websiteListView.getSelectionModel().getSelectedItem();
        if (selectedWebsite != null) {
            detailsArea.setText("Website: " + selectedWebsite + "\nUsername: USERNAME\nPassword: PASSWORD");
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
