package org.example.arkanoid.UIUX;

import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class HowToPlayScreen extends UIScreen {

    private Rectangle2D backButton;
    private Image howToPlayImage;

    public HowToPlayScreen(double gameWidth, double gameHeight) {
        super(gameWidth, gameHeight);

        this.howToPlayImage = ResourceManager.howToPlayBackground;

        // Vị trí nút
        double btnWidth = 350;
        double btnHeight = 70;
        double margin = 75; // Cách lề

        // Căn lề giữa
        double btnX = (gameWidth - btnWidth) / 2.0;
        // Căn lề dưới
        double btnY = gameHeight - btnHeight - margin;

        this.backButton = new Rectangle2D(btnX, btnY, btnWidth, btnHeight);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // Vẽ ảnh howtoplay
        if (howToPlayImage != null) {
            gc.drawImage(howToPlayImage, 0, 0, gameWidth, gameHeight);
        } else {
            // Dự phòng
            gc.setFill(Color.WHITE);
            gc.setFont(ResourceManager.uiFont);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Không tìm thấy ảnh 'howtoplay.png'!", gameWidth / 2.0, gameHeight / 2.0);
        }

        // Vẽ nút Return to Menu
        gc.setFont(ResourceManager.buttonFont);
        if (hoverIndex == 0) {
            gc.setStroke(Color.CYAN);
            gc.setFill(Color.CYAN);
        } else {
            gc.setStroke(Color.WHITE);
            gc.setFill(Color.WHITE);
        }
        gc.setLineWidth(2);
        gc.strokeRoundRect(backButton.getMinX(), backButton.getMinY(),
                backButton.getWidth(), backButton.getHeight(),
                15, 15);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Return to Menu",
                backButton.getMinX() + backButton.getWidth() / 2,
                backButton.getMinY() + backButton.getHeight() / 2);
        gc.setTextBaseline(VPos.BASELINE); // Reset
    }

    @Override
    public void handleMouseMove(MouseEvent event) {
        if (backButton.contains(event.getX(), event.getY())) {
            this.hoverIndex = 0;
        } else {
            this.hoverIndex = -1;
        }
    }

    public ScoreboardAction handleMouseClick(MouseEvent event) {
        if (hoverIndex == 0) {
            return ScoreboardAction.GOTO_MENU;
        }
        return ScoreboardAction.NONE;
    }
}

