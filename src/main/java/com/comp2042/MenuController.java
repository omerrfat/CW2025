package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.net.URL;

public class MenuController {

    @FXML
    private Button playButton;
    @FXML
    private Button controlsButton;
    @FXML
    private Button exitButton;

    @FXML
    private void initialize() {
        playButton.setOnAction(e -> startGame());
        controlsButton.setOnAction(e -> showControls());
        exitButton.setOnAction(e -> ((Stage) exitButton.getScene().getWindow()).close());
    }

    private void startGame() {
        try {
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            GuiController controller = fxmlLoader.getController();

            Stage stage = (Stage) playButton.getScene().getWindow();
            Scene gameScene = new Scene(root, 300, 510);
            stage.setScene(gameScene);
            stage.setTitle("TetrisJFX");
            stage.show();

            new GameController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showControls() {
        Alert controls = new Alert(Alert.AlertType.INFORMATION);
        controls.setTitle("Controls");
        controls.setHeaderText("Game Controls");
        controls.setContentText(
                "← / → : Move Left / Right\n" +
                        "↓ : Move Down\n" +
                        "↑ : Rotate\n" +
                        "P : Pause / Resume\n" +
                        "R : Restart Game\n" +
                        "Space : Hard Drop"
        );
        controls.showAndWait();
    }
}