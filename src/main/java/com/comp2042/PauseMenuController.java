package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.net.URL;

/**
 * Controller for the pause menu that appears when ESC is pressed during
 * gameplay.
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
     * show the controls dialog.
     */
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
                        "  ESC                Open Pause Menu\n" +
                        "  R / N              Restart Game\n\n" +
                        "STRATEGY TIPS:\n" +
                        "  • Plan your moves ahead\n" +
                        "  • Fill lines completely to clear them\n" +
                        "  • Stack strategically for combos\n" +
                        "  • Use the ghost piece to guide placement");
        controls.setResizable(true);
        controls.showAndWait();
    }

    /**
     * return to main menu from pause.
     */
    private void goToMainMenu() {
        try {
            URL location = getClass().getClassLoader().getResource("menu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            javafx.scene.Parent root = fxmlLoader.load();

            Stage stage = (Stage) mainMenuButton.getScene().getWindow();

            Scene menuScene = new Scene(root, 600, 700);
            stage.setScene(menuScene);
            stage.setTitle("Tetris - Main Menu");
            stage.show();

            if (pauseStage != null) {
                pauseStage.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add smooth hover animation to buttons.
     */
    private void addButtonAnimation(Button button) {
        button.setOnMouseEntered(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150),
                    button);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();
        });

        button.setOnMouseExited(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150),
                    button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    /**
     * set the parent GuiController and pause Stage.
     */
    public void setPauseContext(GuiController guiController, Stage pauseStage) {
        this.guiController = guiController;
        this.pauseStage = pauseStage;
    }

    /**
     * handle ESC key to close pause menu and resume.
     */
    public void handleKeyEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            resume();
            event.consume();
        }
    }
}
