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
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Title
        Text title = new Text("TETRIS");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        title.setFill(Color.BLUEVIOLET);
        title.setTextAlignment(TextAlignment.CENTER);

        // Buttons
        Button playButton = new Button("Play");
        Button controlsButton = new Button("Controls");
        Button exitButton = new Button("Exit");

        String buttonStyle = "-fx-font-size: 16px; -fx-padding: 8 20; -fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white;";
        playButton.setStyle(buttonStyle);
        controlsButton.setStyle(buttonStyle);
        exitButton.setStyle(buttonStyle);

        // Layout
        VBox menuLayout = new VBox(20, title, playButton, controlsButton, exitButton);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPrefSize(400, 500);

        // proper background image handling
        try {
            // Attempt to get the resource URL
            URL imageUrl = getClass().getResource("/tetris-figures-retro-game-vector-illustration_756957-674.jpg");

            if (imageUrl != null) {
                // Resource found, proceed with loading
                Image backgroundImage = new Image(imageUrl.toExternalForm());
                BackgroundImage bgImage = new BackgroundImage(
                        backgroundImage,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(100, 100, true, true, false, false)
                );
                menuLayout.setBackground(new Background(bgImage));
                System.out.println("Background image loaded successfully!"); // Added confirmation
            } else {
                // Resource was not found (imageUrl is null)
                System.err.println("ERROR: Background image resource not found at /backgrounds/retro.png");
                System.err.println("(Check the file path and resource folder structure in your project.)");
            }

        } catch (Exception e) {
            // Catch other exceptions during image creation or background setting
            System.err.println("Could not load background image: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for full debugging info
        }

        Scene menuScene = new Scene(menuLayout);

        // Buttons
        playButton.setOnAction(e -> startGame(primaryStage));
        controlsButton.setOnAction(e -> showControls());
        exitButton.setOnAction(e -> primaryStage.close());

        primaryStage.setTitle("Tetris");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

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
