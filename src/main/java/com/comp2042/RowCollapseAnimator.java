package com.comp2042;

import com.comp2042.util.Constants;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

/**
 * helper class that animates the collapse of rows by creating overlay
 * rectangles
 * representing the filled cells from the "before" board snapshot and
 * translating
 * them down to their new positions.
 */
public final class RowCollapseAnimator {

    private RowCollapseAnimator() {
    }

    public static void animateCollapse(Pane rootPane,
            GridPane gameGrid,
            int[][] beforeMatrix,
            int[] clearedRows,
            IntFunction<Paint> colorProvider,
            Runnable onFinished) {

        if (beforeMatrix == null || clearedRows == null || clearedRows.length == 0) {
            if (onFinished != null)
                onFinished.run();
            return;
        }

        // sort cleared rows to make computations predictable
        int[] sortedCleared = Arrays.copyOf(clearedRows, clearedRows.length);
        Arrays.sort(sortedCleared);

        double cellSize = Constants.BRICK_SIZE;
        double hGap = gameGrid.getHgap();
        double vGap = gameGrid.getVgap();

        List<Animation> transitions = new ArrayList<>();
        List<Rectangle> overlays = new ArrayList<>();

        // only animate cells that were filled in the snapshot (beforeMatrix[r][c] != 0)
        for (int r = 0; r < beforeMatrix.length; r++) {
            for (int c = 0; c < beforeMatrix[r].length; c++) {
                int colorCode = beforeMatrix[r][c];
                if (colorCode == 0)
                    continue;

                // only build overlays for visible rows in the GridPane (GUI uses an offset of
                // 2)
                if (r < 2)
                    continue;

                // compute count of cleared rows below the current row -> that's how many rows
                // it will drop
                int delta = 0;
                for (int cleared : sortedCleared) {
                    if (cleared > r)
                        delta++;
                }

                if (delta == 0)
                    continue; // this cell doesn't move

                // compute absolute coordinates relative to rootPane
                double originX = gameGrid.getLayoutX() + c * (cellSize + hGap);
                double originY = gameGrid.getLayoutY() + (r - 2) * (cellSize + vGap);

                Rectangle overlay = new Rectangle(cellSize, cellSize);
                overlay.setFill(colorProvider.apply(colorCode));
                overlay.setArcWidth(Constants.BRICK_ARC_SIZE);
                overlay.setArcHeight(Constants.BRICK_ARC_SIZE);
                overlay.setLayoutX(originX);
                overlay.setLayoutY(originY);

                overlays.add(overlay);
                rootPane.getChildren().add(overlay);

                TranslateTransition tt = new TranslateTransition(Duration.millis(180 + 40 * delta), overlay);
                double translateY = delta * (cellSize + vGap);
                tt.setByY(translateY);
                // smooth easing and a small stagger based on how far the cell needs to drop
                tt.setInterpolator(Interpolator.EASE_BOTH);
                if (delta > 1) {
                    tt.setDelay(Duration.millis(20L * (delta - 1)));
                }
                overlay.setMouseTransparent(true);
                transitions.add(tt);
            }
        }

        if (transitions.isEmpty()) {
            // Nothing to move — clean up overlays (if any) and finish
            overlays.forEach(rootPane.getChildren()::remove);
            if (onFinished != null)
                onFinished.run();
            return;
        }

        ParallelTransition pt = new ParallelTransition();
        pt.getChildren().addAll(transitions);
        pt.setOnFinished(e -> {
            // cleanup overlay rectangles
            overlays.forEach(rootPane.getChildren()::remove);
            if (onFinished != null)
                onFinished.run();
        });
        pt.play();
    }
}
