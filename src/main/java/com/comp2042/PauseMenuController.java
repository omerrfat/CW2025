package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.net.URL;

/**
 * PauseMenuController - Controls the pause menu UI during gameplay.
 * Responsibilities:
 * - Manages pause menu button interactions (Resume, Controls, Main Menu)
 * - Handles scene transitions between the pause menu and other menus
 * - Provides animated button feedback with neon glow effects
 * - Color-coded buttons: Green for Resume, Cyan for Controls, Orange for Main
 * Menu
 * - Implements smooth scale and drop shadow animations on hover/press
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class PauseMenuController {

    @FXML
    private Button resumeButton;
    @FXML
    private Button controlsButton;
    @FXML
    private Button mainMenuButton;

    private Stage pauseStage;
    private GuiController guiController;

    @FXML
    private void initialize() {
        resumeButton.setOnAction(e -> resume());
        controlsButton.setOnAction(e -> showControls());
        mainMenuButton.setOnAction(e -> goToMainMenu());

        addButtonAnimation(resumeButton);
        addButtonAnimation(controlsButton);
        addButtonAnimation(mainMenuButton);
    }

    /**
     *
     * resume the game and close the pause menu.
     */
    private void resume() {
        if (pauseStage != null) {
            pauseStage.close();
        }
        if (guiController != null) {
            guiController.resumeFromPause();
        }
    }

    /**
     * show the controls' dialog.
     */
    private void showControls() {
        Alert controls = new Alert(Alert.AlertType.INFORMATION);
        controls.setTitle("⌨ GAME CONTROLS");
        controls.setHeaderText("Master the Game");
        controls.setContentText(
                """
                        MOVEMENT:
                          ← / A  →  / D    Move Left / Right
                          ↓ / S             Move Down (Soft Drop)
                          Space             Hard Drop (Instant Fall)

                        ROTATION:
                          ↑ / W             Rotate Brick

                        GAME CONTROLS:
                          P                  Pause / Resume Game
                          ESC                Open Pause Menu
                          R / N              Restart Game

                        STRATEGY TIPS:
                          • Plan your moves ahead
                          • Fill lines completely to clear them
                          • Stack strategically for combos
                          • Use the ghost piece to guide placement""");
        controls.setResizable(true);
        controls.showAndWait();
    }

    /**
     * return to the main menu from pause.
     */
    private void goToMainMenu() {
        try {
            // Close the pause menu first
            if (pauseStage != null) {
                pauseStage.close();
            }

            // Get the main game stage (owner of pause stage)
            assert pauseStage != null;
            Stage gameStage = (Stage) pauseStage.getOwner();
            if (gameStage == null) {
                gameStage = (Stage) mainMenuButton.getScene().getWindow();
            }

            URL location = getClass().getClassLoader().getResource("menu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            javafx.scene.Parent root = fxmlLoader.load();

            Scene menuScene = new Scene(root, 600, 700);
            gameStage.setScene(menuScene);
            gameStage.setTitle("Tetris - Main Menu");
            gameStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add smooth hover animation to buttons with neon glow effects.
     */
    private void addButtonAnimation(Button button) {
        button.setOnMouseEntered(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150),
                    button);
            st.setToX(1.12);
            st.setToY(1.12);

            DropShadow glow = new DropShadow();
            glow.setRadius(15);
            glow.setSpread(0.5);

            if (button == resumeButton) {
                glow.setColor(Color.web("#00cc00"));
            } else if (button == controlsButton) {
                glow.setColor(Color.web("#00d4ff"));
            } else if (button == mainMenuButton) {
                glow.setColor(Color.web("#ff6b35"));
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

    /**
     * set the parent GuiController and pause Stage.
     */
    public void setPauseContext(GuiController guiController, Stage pauseStage) {
        this.guiController = guiController;
        this.pauseStage = pauseStage;

        // Add slide-in animation when the pause menu is shown
        pauseStage.setOnShown(e -> animateSlideIn());
    }

    /**
     * Animate the pause menu sliding in from the top with a smooth scale effect
     */
    private void animateSlideIn() {
        javafx.scene.layout.StackPane root = (javafx.scene.layout.StackPane) pauseStage.getScene().getRoot();

        // Set initial state - scaled down and transparent
        root.setScaleX(0.8);
        root.setScaleY(0.8);
        root.setOpacity(0.0);

        // Create scale transition
        javafx.animation.ScaleTransition scaleTransition = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(400), root);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        // Create fade transition
        javafx.animation.FadeTransition fadeTransition = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(400), root);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        // Run both transitions in parallel
        javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition(
                scaleTransition, fadeTransition);
        parallelTransition.play();
    }

    /**
     * handle ESC key to close the pause menu and resume.
     */
    public void handleKeyEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            resume();
            event.consume();
        }
    }
}
