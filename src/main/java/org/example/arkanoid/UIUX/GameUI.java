package org.example.arkanoid.UIUX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;

public class GameUI {
    // TÀI NGUYÊN
    private static final String FONT_PATH = "/font/pixel.ttf";
    private static final String LIFE_ICON_PATH = "/images/heart1.png";

    private Font uiFont;
    private Image lifeIcon;
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
        loadResources();
    }

    private void loadResources() {
        // Tải Font
        try (InputStream fontStream = getClass().getResourceAsStream(FONT_PATH)) {
            if (fontStream == null) throw new Exception("Không tìm thấy font: " + FONT_PATH);
            uiFont = Font.loadFont(fontStream, 24); // Cỡ chữ 24
        } catch (Exception e) {
            System.err.println("Lỗi tải font UI: " + e.getMessage());
            uiFont = Font.font("Arial", FontWeight.BOLD, 24);
        }

        // Tải ảnh Máu
        try (InputStream imageStream = getClass().getResourceAsStream(LIFE_ICON_PATH)) {
            if (imageStream == null) throw new Exception("Không tìm thấy ảnh: " + LIFE_ICON_PATH);
            lifeIcon = new Image(imageStream, iconWidth, iconHeight, true, true);
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh Máu: " + e.getMessage() + ". Sẽ dùng text thay thế.");
            lifeIcon = null;
        }
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

    public void render(GraphicsContext gc) {

        // Vị trí Y cách ở dưới bottomPadding
        double yPos = gameHeight - bottomPadding;

        // Vẽ Máu (Góc dưới bên trái)
        gc.setFont(uiFont);
        gc.setTextAlign(TextAlignment.LEFT);

        if (lifeIcon != null) {
            // Vẽ bằng ảnh
            double spacing = 5; // 5px giữa các icon
            double startX = margin;
            // Căn icon nằm ngay trên baseline của text
            double iconY = yPos - iconHeight;

            for (int i = 0; i < lives; i++) {
                gc.drawImage(lifeIcon, startX + i * (iconWidth + spacing), iconY);
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
        double xPos = gameWidth - margin; // Vị trí X

        gc.setTextAlign(TextAlignment.RIGHT);

        // Vẽ bóng
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillText(scoreText, xPos + 2, yPos + 2);

        // Vẽ chữ
        gc.setFill(Color.WHITE);
        gc.fillText(scoreText, xPos, yPos);
    }
}
