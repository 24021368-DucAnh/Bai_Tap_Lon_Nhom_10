package org.example.arkanoid.UIUX;

import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class QuitScreen extends UIScreen{
    private final String[] options = {"YES", "NO"};
    private final Rectangle2D[] buttonRects;

    private final double BUTTON_WIDTH = 150;
    private final double BUTTON_HEIGHT = 50;
    private final double BUTTON_SPACING = 40;

    private long score = 0;

    public QuitScreen(double gameWidth, double gameHeight) {
        super(gameWidth,gameHeight);

        this.buttonRects = new Rectangle2D[options.length];

        double buttonY = gameHeight / 2.0 + 80;
        double yesButtonX = (gameWidth / 2.0) - BUTTON_SPACING - BUTTON_WIDTH;
        double noButtonX = (gameWidth / 2.0) + BUTTON_SPACING;

        this.buttonRects[0] = new Rectangle2D(yesButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.buttonRects[1] = new Rectangle2D(noButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameWidth, gameHeight);

        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFont(ResourceManager.uiFont);
        gc.setFill(new Color(0, 0, 0, 0.7)); // Shadow
        gc.fillText("Save this score?", gameWidth / 2.0 + 4, (gameHeight / 2.0 - 80) + 4);
        gc.setFill(Color.WHITE); // Main text
        gc.fillText("Save this score?", gameWidth / 2.0, gameHeight / 2.0 - 80);

        gc.setFont(ResourceManager.uiFont);
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, gameWidth / 2.0, gameHeight / 2.0);

        // 5. Vẽ các nút
        gc.setFont(ResourceManager.buttonFont);

        for (int i = 0; i < options.length; i++) {
            Rectangle2D rect = buttonRects[i];

            if (i == hoverIndex) {
                gc.setStroke(Color.CYAN);
                gc.setFill(Color.WHITE);
            } else {
                gc.setStroke(Color.WHITE);
                gc.setFill(Color.WHITE);
            }
            gc.setLineWidth(2);
            gc.strokeRoundRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), 15, 15);

            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(options[i],
                    rect.getMinX() + rect.getWidth() / 2,
                    rect.getMinY() + rect.getHeight() / 2);
        }
        gc.setTextBaseline(VPos.BASELINE); // Reset
    }

    @Override
    public void handleMouseMove(MouseEvent event) {
        hoverIndex = -1;
        for (int i = 0; i < buttonRects.length; i++) {
            if (buttonRects[i].contains(event.getX(), event.getY())) {
                hoverIndex = i;
                break;
            }
        }
    }

    public QuitAction handleMouseClick(MouseEvent event) {
        for (int i = 0; i < buttonRects.length; i++) {
            if (buttonRects[i].contains(event.getX(), event.getY())) {
                if (i == 0) {
                    return QuitAction.YES;
                } else {
                    return QuitAction.NO;
                }
            }
        }
        return QuitAction.NONE;
    }

    @Override
    public void reset() {
        super.reset();
    }

    public void setScore(long score) {
        this.score = score;
    }
}