import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.Modality;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Insets;
import javafx.scene.control.*;


public class App extends Application {
    private ListView<String> listView;
    private Label selectedItemLabel;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login Form Popup");
        displayLoginFormPopup();

        primaryStage.setTitle("JavaFX ListView App");
        Screen screen = Screen.getPrimary();

        // Set the stage's dimensions to match the screen's size
        primaryStage.setWidth(screen.getVisualBounds().getWidth());
        primaryStage.setHeight(screen.getVisualBounds().getHeight());

        ObservableList<String> items = FXCollections.observableArrayList(
            "Item 1", "Item 2", "Item 3", "Item 4"
        );

        // Create a ListView
        listView = new ListView<>(items);
        listView.getStyleClass().add("list-view");

        // Create a Label for displaying selected item info
        selectedItemLabel = new Label("Select an item from the list");

        // Create a Button for adding items
        Button addItemButton = new Button("Add Item");
        addItemButton.setOnAction(e -> {
            String newItem = "New Item " + (listView.getItems().size() + 1);
            listView.getItems().add(newItem);
            selectedItemLabel.setText("Selected Item: " + newItem); // Update the label
        });

        // Create a layout with two panes side by side
        HBox layout = new HBox(10); // 10 is the spacing between panes
        layout.getChildren().addAll(listView, selectedItemLabel);

        // Create a layout for the button
        VBox buttonLayout = new VBox(10);
        buttonLayout.getChildren().addAll(addItemButton);

        // Combine the main layout and the button layout
        HBox root = new HBox(10);
        root.getChildren().addAll(layout, buttonLayout);

        // Create the scene and add the root layout
        Scene scene = new Scene(root, 500, 300);

        // Reference the CSS file for styling
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.close();
    }

    private void displayLoginFormPopup() {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        popupStage.setTitle("Login Form");

        // Create form components
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        // Handle login button click
        loginButton.setOnAction(event -> {
            // Check the credentials (For simplicity, use "demo" as username and "password" as password)
            if ("demo".equals(usernameField.getText()) && "password".equals(passwordField.getText())) {
                // Close the popup and proceed to the main application
                popupStage.close();
                showMainApplication();
            } else {
                // Invalid credentials, show an error message
                showErrorAlert();
            }
        });

        // Create a layout for the form
        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(10));
        formLayout.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        // Create a scene for the form and set it as the content of the popup stage
        Scene popupScene = new Scene(formLayout);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Credentials");
        alert.setHeaderText(null);
        alert.setContentText("Invalid username or password. Please try again.");
        alert.showAndWait();
    }

    private void showMainApplication() {
        
        // Enter fullscreen mode
        // primaryStage.setFullScreen(true);

        // Create an ObservableList for the ListView
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
