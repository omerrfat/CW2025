package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.*;
/**
 * @author Umer Imran
 * @version 2.0
 */
public class Main extends Application {

    // updating Start method to display a simple start menu, that displays Play, Controls and Exit
    @Override
    public void start(Stage primaryStage) throws Exception {
        // --- Title text ---
        Text title = new Text("TETRIS");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        title.setFill(Color.DARKBLUE);
        title.setTextAlignment(TextAlignment.CENTER);

        // --- Buttons ---
        Button playButton = new Button("Play");
        Button controlsButton = new Button("Controls");
        Button exitButton = new Button("Exit");

        // --- Style buttons ---
        String buttonStyle = "-fx-font-size: 16px; -fx-padding: 8 20;";
        playButton.setStyle(buttonStyle);
        controlsButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);

        // --- Layout for menu ---
        VBox menuLayout = new VBox(20, title, playButton, controlsButton, exitButton);
        menuLayout.setAlignment(Pos.CENTER);
        Scene menuScene = new Scene(menuLayout, 400, 500);

        // --- Button Actions ---
        playButton.setOnAction(e -> startGame(primaryStage));
        controlsButton.setOnAction(e -> showControls());
        exitButton.setOnAction(e -> primaryStage.close());

        // --- Display menu ---
        primaryStage.setTitle("Tetris");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    /**
     * loads the actual Tetris game scene (from FXML)
     */
    private void startGame(Stage primaryStage) {
        try {
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location, (ResourceBundle) null);
            Parent root = fxmlLoader.load();
            GuiController controller = fxmlLoader.getController();

            Scene gameScene = new Scene(root, 300, 510);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("TetrisJFX");
            primaryStage.show();

            new GameController(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * shows a small pop-up with key controls
     */
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

    public static void main(String[] args) {
        launch(args);
    }
}
