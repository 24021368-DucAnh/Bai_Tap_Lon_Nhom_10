package org.example.arkanoid.UIUX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.example.arkanoid.core.ResourceManager;

public class GameUI {
    private double iconWidth = 40;
    private double iconHeight = 40;

    // TRẠNG THÁI
    private int score;
    private int lives;

    // VỊ TRÍ
    private final double gameWidth;
    private final double gameHeight;
    private final double margin = 15.0; // Khoảng cách lề
    private final double bottomPadding = 15.0; // Khoảng cách lề dưới

    public GameUI(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.score = 0;
        this.lives = 3; // Mặc định
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void addLives(int lives) {
        if (this.lives < 3) {
            this.lives++;
        }
    }

    public void loseLife() {
        if (this.lives > 0) {
            this.lives--;
        }
    }

    public int getScore() {
        return this.score;
    }

    public void render(GraphicsContext gc) {

        // Vị trí Y cách ở dưới bottomPadding
        double yPos = gameHeight - bottomPadding;

        // Vẽ Máu (Góc dưới bên trái)
        double xIconPos = gameWidth - margin;
        gc.setFont(ResourceManager.uiFont);
        gc.setTextAlign(TextAlignment.RIGHT);

        if (ResourceManager.lifeIcon != null) {
            // Vẽ bằng ảnh
            double spacing = 5; // 5px giữa các icon
            // Căn icon nằm ngay trên baseline của text
            double iconY = yPos - iconHeight;

            for (int i = 0; i < lives; i++) {
                double iconX = xIconPos - (i + 1) * iconWidth - i * spacing;
                gc.drawImage(ResourceManager.lifeIcon, iconX, iconY);
            }
        } else {
            // Dự phòng
            String livesText = "LIVES: " + this.lives;

            // Vẽ bóng
            gc.setFill(new Color(0, 0, 0, 0.7));
            gc.fillText(livesText, margin + 2, yPos + 2);

            // Vẽ chữ
            gc.setFill(Color.WHITE);
            gc.fillText(livesText, margin, yPos);
        }

        //Vẽ Điểm (Góc dưới bên phải)
        String scoreText = "SCORE: " + this.score;
        double xScorePos = margin; // Vị trí X

        gc.setTextAlign(TextAlignment.LEFT);

        // Vẽ bóng
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillText(scoreText, xScorePos + 2, yPos + 2);

        // Vẽ chữ
        gc.setFill(Color.WHITE);
        gc.fillText(scoreText, xScorePos, yPos);
    }
}
