package org.example.arkanoid.UIUX;

import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class GameOverScreen {

    private final double gameWidth;
    private final double gameHeight;

    private final String[] options = {"Retry", "Return to Menu"};
    private final Rectangle2D[] buttonRects;
    private int hoverIndex = -1;

    public GameOverScreen(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.buttonRects = new Rectangle2D[options.length];

        final double BUTTON_WIDTH = 350;
        final double BUTTON_HEIGHT = 70;
        final double BUTTON_SPACING = 30;
        double centerX = gameWidth / 2.0;

        // Vị trí Y của nút đầu tiên (Retry)
        double startY = gameHeight / 2.0 + 30;

        for (int i = 0; i < options.length; i++) {
            double buttonX = centerX - BUTTON_WIDTH / 2;
            double buttonY = startY + i * (BUTTON_HEIGHT + BUTTON_SPACING);
            buttonRects[i] = new Rectangle2D(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    public GameOverAction handleMouseClick(MouseEvent event) {
        for (int i = 0; i < buttonRects.length; i++) {
            if (buttonRects[i].contains(event.getX(), event.getY())) {
                if (i == 0) {
                    return GameOverAction.RETRY;
                } else if (i == 1) {
                    return GameOverAction.GOTO_MENU;
                }
            }
        }
        return GameOverAction.NONE;
    }

    public void handleMouseMove(MouseEvent event) {
        hoverIndex = -1;
        for (int i = 0; i < buttonRects.length; i++) {
            if (buttonRects[i].contains(event.getX(), event.getY())) {
                hoverIndex = i;
                break;
            }
        }
    }

    /**
     * Vẽ màn hình Game Over.
     * @param gc GraphicsContext
     * @param score Điểm số cuối cùng (truyền từ GameUI)
     */
    public void render(GraphicsContext gc, long score) {
        // Vẽ lớp nền mờ
        gc.setFill(new Color(0, 0, 0, 0.8)); // Tối hơn một chút so với pause
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // Vẽ chữ "GAME OVER"
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(ResourceManager.textFont);
        gc.setFill(Color.RED);
        gc.fillText("GAME OVER", gameWidth / 2.0, gameHeight / 2.0 - 100);

        // Vẽ Điểm số
        gc.setFont(ResourceManager.uiFont);
        gc.setFill(Color.WHITE);
        gc.fillText("Your Score: " + score, gameWidth / 2.0, gameHeight / 2.0 - 20);

        // Vẽ các nút
        gc.setFont(ResourceManager.buttonFont);
        gc.setFill(Color.WHITE);
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

            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(options[i],
                    rect.getMinX() + rect.getWidth() / 2,
                    rect.getMinY() + rect.getHeight() / 2);
        }

        gc.setTextBaseline(VPos.BASELINE);
    }

    public void reset() {
        this.hoverIndex = -1;
    }
}