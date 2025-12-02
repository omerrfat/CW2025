package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * NotificationPanel - Displays notifications (score bonuses, line clears) with
 * animation.
 * 
 * Responsibilities:
 * - Shows temporary notification messages (e.g., "+100 Points", "Line Clear!")
 * - Animates notifications with fade and translation effects
 * - Automatically removes notifications after display duration
 * - Applies glow effect for visual emphasis
 * 
 * Animation: Parallel fade-out and upward translation over 1.5 seconds
 * 
 * @author Umer Imran
 * @version 2.0
 */
public class NotificationPanel extends BorderPane {

    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(3000), this);
        tt.setFromY(this.getLayoutY());
        tt.setToY(this.getLayoutY() - 60);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}
