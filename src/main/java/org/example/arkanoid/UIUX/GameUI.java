package org.example.arkanoid.UIUX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.arkanoid.core.ResourceManager;

public class GameUI {
    private double iconWidth = 40;
    private double iconHeight = 40;

    // TRẠNG THÁI
    private int score;
    private int lives;
    private int currentStage = 1;

    // VỊ TRÍ
    private final double gameWidth;
    private final double gameHeight;
    private final double margin = 15.0; // Khoảng cách lề

    public GameUI(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.score = 0;
        this.lives = 3; // Mặc định
    }

    public void setScore (int score) {
        this.score = score;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void render(GraphicsContext gc) {

        gc.clearRect(0, 0, gameWidth, gameHeight);

        // --- Vị trí chung ---
        // Căn chỉnh lề trên
        double topAlignY = 20.0;

        double line1Y = topAlignY + 20; // Baseline cho dòng 1 (SCORE, STAGE)
        double line2Y = line1Y + 25; // Baseline cho dòng 2 (Số)

        double iconTopY = topAlignY - 10;

        gc.setFont(ResourceManager.uiFont);
        Font oldFont = gc.getFont();
        Font biggerFont = oldFont;

        if (ResourceManager.uiFont != null) {
            biggerFont = Font.font(ResourceManager.uiFont.getFamily(), ResourceManager.uiFont.getSize() + 4);
        }

        // Vẽ Score
        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(oldFont);
        gc.fillText("SCORE", margin, line1Y);

        // Điểm
        gc.setFill(Color.WHITE);
        gc.setFont(biggerFont); // Dùng font to
        gc.fillText(String.valueOf(this.score), margin, line2Y);


        // Vẽ Stage
        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);
        double centerX = gameWidth / 2;
        gc.setFont(oldFont);
        gc.fillText("STAGE", centerX, line1Y);

        // Màn đang chơi
        gc.setFill(Color.WHITE);
        gc.setFont(biggerFont); // Dùng font to
        gc.fillText(String.valueOf(this.currentStage), centerX, line2Y);

        gc.setFont(oldFont);

        // Vẽ Máu
        //dự phòng
        gc.setFill(Color.WHITE);
        double xIconPos = gameWidth - margin;
        gc.setTextAlign(TextAlignment.RIGHT);

        if (ResourceManager.lifeIcon != null) {
            double spacing = 5; // 5px giữa các icon

            for (int i = 0; i < lives; i++) {
                // Vẽ từ PHẢI sang TRÁI
                double iconX = xIconPos - (i + 1) * this.iconWidth - i * spacing;
                gc.drawImage(ResourceManager.lifeIcon, iconX, iconTopY, this.iconWidth, this.iconHeight);
            }
        } else {
            // Dự phòng nếu không có ảnh
            String livesText = "LIVES: " + this.lives;
            gc.fillText(livesText, xIconPos, line1Y);
        }
    }

    public void updateStage(int stage) {
        this.currentStage = stage;
    }
}
