package org.example.arkanoid.UIUX;


import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class PauseScreen {

    private final double gameWidth;
    private final double gameHeight;

    private final String[] options = {"Resume", "Return to Menu"};
    private final Rectangle2D[] buttonRects;
    private int hoverIndex = -1; // -1 = không hover

    public PauseScreen(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        this.buttonRects = new Rectangle2D[options.length];

        // Kích thước và vị trí các nút
        final double BUTTON_WIDTH = 350;
        final double BUTTON_HEIGHT = 70;
        final double BUTTON_SPACING = 30;
        double centerX = gameWidth / 2.0;
        double startY = gameHeight / 2.0 - 40;

        for (int i = 0; i < options.length; i++) {
            double buttonX = centerX - BUTTON_WIDTH / 2;
            double buttonY = startY + i * (BUTTON_HEIGHT + BUTTON_SPACING);
            buttonRects[i] = new Rectangle2D(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    /**
     * Xử lý sự kiện click chuột.
     */
    public PauseAction handleMouseClick(MouseEvent event) {
        for (int i = 0; i < buttonRects.length; i++) {
            if (buttonRects[i].contains(event.getX(), event.getY())) {
                return (i == 0) ? PauseAction.RESUME : PauseAction.GOTO_MENU;
            }
        }
        return PauseAction.NONE;
    }

    /**
     * Xử lý sự kiện di chuyển chuột để cập nhật hiệu ứng hover.
     */
    public void handleMouseMove(MouseEvent event) {
        hoverIndex = -1; // Reset
        for (int i = 0; i < buttonRects.length; i++) {
            if (buttonRects[i].contains(event.getX(), event.getY())) {
                hoverIndex = i;
                break;
            }
        }
    }

    /**
     * Phím ESC để resume.
     */
    public PauseAction handleKeyInput(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.ESCAPE) {
            return PauseAction.RESUME;
        }
        return PauseAction.NONE;
    }


    /**
     * Vẽ màn hình pause.
     */
    public void render(GraphicsContext gc) {
        // Vẽ lớp nền mờ (giống code cũ của bạn)
        gc.setFill(new Color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // Vẽ chữ "PAUSED"
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(ResourceManager.textFont);
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillText("PAUSED", gameWidth / 2.0 + 4, gameHeight / 2.0 - 100 + 4);
        gc.setFill(Color.WHITE);
        gc.fillText("PAUSED", gameWidth / 2.0, gameHeight / 2.0 - 100);

        // Vẽ các nút
        gc.setFont(ResourceManager.buttonFont); // Dùng font UI cho nút

        for (int i = 0; i < options.length; i++) {
            Rectangle2D rect = buttonRects[i];

            // Đặt màu dựa trên trạng thái hover
            if (i == hoverIndex) {
                gc.setStroke(Color.CYAN);
                gc.setFill(Color.WHITE);
            } else {
                gc.setStroke(Color.WHITE);
                gc.setFill(Color.WHITE);
            }

            gc.setLineWidth(2);
            gc.strokeRoundRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), 15, 15);

            // Vẽ chữ bên trong nút
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER); // Căn chữ vào giữa
            gc.fillText(options[i],
                    rect.getMinX() + rect.getWidth() / 2,
                    rect.getMinY() + rect.getHeight() / 2);
        }

        gc.setTextBaseline(VPos.BASELINE); // Reset
    }

    /**
     * Reset trạng thái hover.
     */
    public void reset() {
        this.hoverIndex = -1;
    }
}
