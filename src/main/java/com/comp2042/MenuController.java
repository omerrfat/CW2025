package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.net.URL;

/**
 * MenuController - Controls the main game menu UI.
 * 
 * Responsibilities:
 * - Manages main menu button interactions (Play, Controls, Exit)
 * - Handles scene transitions to game and controls menu
 * - Provides animated button feedback with neon glow effects
 * - Color-coded buttons: Orange for Play, Cyan for Controls, Gray for Exit
 * - Implements smooth scale and drop shadow animations on hover/press
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class MenuController {

    @FXML
    private Button playButton;
    @FXML
    private Button controlsButton;
    @FXML
    private Button exitButton;
    @FXML
    private javafx.scene.text.Text titleText;

    @FXML
    private void initialize() {
        playButton.setOnAction(e -> startGame());
        controlsButton.setOnAction(e -> showControls());
        exitButton.setOnAction(e -> ((Stage) exitButton.getScene().getWindow()).close());

        // add button animations
        addButtonAnimation(playButton);
        addButtonAnimation(controlsButton);
        addButtonAnimation(exitButton);

        // Add title pulse animation
        if (titleText != null) {
            addTitlePulse(titleText);
        }
    }

    private void addButtonAnimation(Button button) {
        button.setOnMouseEntered(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150),
                    button);
            st.setToX(1.12);
            st.setToY(1.12);

            DropShadow glow = new DropShadow();
            glow.setRadius(15);
            glow.setSpread(0.5);

            if (button == playButton) {
                glow.setColor(Color.web("#ef531aff"));
            } else if (button == controlsButton) {
                glow.setColor(Color.web("#00d4ff"));
            } else {
                glow.setColor(Color.web("#666666"));
            }

            button.setEffect(glow);
            st.play();
        });

        button.setOnMouseExited(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150),
                    button);
            st.setToX(1.0);
            st.setToY(1.0);
            button.setEffect(null);
            st.play();
        });

        button.setOnMousePressed(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(100),
                    button);
            st.setToX(0.95);
            st.setToY(0.95);
            st.play();
        });

        button.setOnMouseReleased(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(100),
                    button);
            st.setToX(1.12);
            st.setToY(1.12);
            st.play();
        });
    }

    private void addTitlePulse(javafx.scene.text.Text titleText) {
        javafx.animation.ScaleTransition pulse = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(1500),
                titleText);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.02);
        pulse.setToY(1.02);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulse.play();
    }

    private void startGame() {
        try {
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();
            GuiController controller = fxmlLoader.getController();

            Stage stage = (Stage) playButton.getScene().getWindow();
            Scene gameScene = new Scene(root);
            stage.setResizable(false);
            stage.sizeToScene();
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
        controls.setTitle("⌨ GAME CONTROLS");
        controls.setHeaderText("Master the Game");
        controls.setContentText(
                "MOVEMENT:\n" +
                        "  ← / A  →  / D    Move Left / Right\n" +
                        "  ↓ / S             Move Down (Soft Drop)\n" +
                        "  Space             Hard Drop (Instant Fall)\n\n" +
                        "ROTATION:\n" +
                        "  ↑ / W             Rotate Brick\n\n" +
                        "GAME CONTROLS:\n" +
                        "  P                  Pause / Resume Game\n" +
                        "  R / N              Restart Game\n\n" +
                        "STRATEGY TIPS:\n" +
                        "  • Plan your moves ahead\n" +
                        "  • Fill lines completely to clear them\n" +
                        "  • Stack strategically for combos\n" +
                        "  • Use the ghost piece to guide placement");
        controls.setResizable(true);
        controls.showAndWait();
    }
}