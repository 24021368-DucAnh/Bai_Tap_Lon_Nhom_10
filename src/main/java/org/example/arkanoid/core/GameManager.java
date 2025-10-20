package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.FontWeight;
import org.example.arkanoid.UIUX.GameUI;
import org.example.arkanoid.UIUX.SoundEffectManager;
import org.example.arkanoid.objects.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;


import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    private final double gameWidth;
    private final double gameHeight;

    // Biến trạng thái để pause game
    private boolean isPaused = false;

    //Biến tài nguyên ảnh, font
    private static final String FONT_PATH = "/font/pixel.ttf";
    private static final String PADDLE_IMAGE_PATH = "/images/Paddle.png";

    private GameUI gameUI;
    private Font pausedFont;
    private Image paddleImage;

    // Sound effect
    private SoundEffectManager soundEffectManager;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private PowerUpManager powerUpManager;
    private boolean isGameOver = false;

    public GameManager(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    /**
     * Tải TẤT CẢ tài nguyên (font, ảnh, âm thanh)
     */
    private void loadResources() {
        // Tải Font
        try (InputStream fontStream = getClass().getResourceAsStream(FONT_PATH)) {
            if (fontStream == null) {
                throw new Exception("Không tìm thấy font: " + FONT_PATH);
            }
            pausedFont = Font.loadFont(fontStream, 60);
        } catch (Exception e) {
            System.err.println("Lỗi tải font: " + e.getMessage());
            pausedFont = Font.font("Impact", FontWeight.BOLD, 60);
        }

        // Tải ảnh Paddle
        try (InputStream imageStream = getClass().getResourceAsStream(PADDLE_IMAGE_PATH)) {
            if (imageStream == null) {
                throw new Exception("Không tìm thấy ảnh: " + PADDLE_IMAGE_PATH);
            }
            paddleImage = new Image(imageStream);
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh Paddle: " + e.getMessage());
        }

        // Khởi tạo Sound Manager
        this.soundEffectManager = new SoundEffectManager();
    }

    public void init() {

        //---------------Tải tài nguyên----------------
        this.gameUI = new GameUI(gameWidth, gameHeight);
        loadResources();

        //---------Paddle-------------
        final int PADDLE_WIDTH = 46; // Giảm kích thước paddle một chút cho dễ chơi
        final int PADDLE_HEIGHT = 20;
        final int PADDLE_OFFSET_FROM_BOTTOM = 100; // Cách lề dưới 1 khoảng y

        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0;
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        this.powerUpManager = new PowerUpManager();

        // Hình ảnh paddle có thể cần điều chỉnh lại cho phù hợp với W/H mới
        this.paddle = new Paddle(
                initialPaddleX,
                initialPaddleY,
                PADDLE_WIDTH, PADDLE_HEIGHT,
                paddleImage);

        //---------Brick-------------
        BrickSkinRegistry.initDefaults();
        int stageToLoad = 5;
        System.out.println("Đang tải màn chơi: " + stageToLoad);
        this.bricks = StageLoader.loadFromIndex(stageToLoad, this.gameWidth);

        if (this.bricks.isEmpty()) {
            System.err.println("Không tải được gạch cho màn " + stageToLoad + ". Hãy kiểm tra file /resources/stages/Stage_1.txt");
        } else {
            System.out.println("Tải thành công " + this.bricks.size() + " viên gạch.");
        }

        //---------Ball-------------
        final int BALL_DIAMETER = 20;
        final double BALL_SPEED = 200; // Tốc độ hợp lý hơn (pixels / giây)
        double initialBallX = gameWidth / 2;
        double initialBallY = gameHeight / 2;

        // Gọi constructor mới của Ball
        ball = new Ball(initialBallX, initialBallY, BALL_DIAMETER, BALL_SPEED, gameWidth, gameHeight, this);
    }

    //setGameOver
    public boolean isGameOver() {
        return this.isGameOver;
    }
    public void setGameOver() {
        this.isGameOver = true;
        System.out.println("Đã chuyển sang trại thái GameOver !");
    }
    public void update(double deltaTime) {

        //Check nếu gameOver thì không update gì nữa
        if (isGameOver) {
            return;
        }
        if (isPaused) {
            return;
        }

        paddle.update(deltaTime);
        ball.update(deltaTime);

        // --- Xử lý va chạm ---

        //  Va chạm giữa Bóng và Paddle
        if (ball.checkCollision(paddle)) {
            ball.bounceOff(paddle);
            soundEffectManager.playHitSound();
        }

        // Va chạm giữa Bóng và Gạch
        // Dùng Iterator để có thể xóa phần tử khỏi List một cách an toàn trong lúc lặp
        Iterator<Brick> brickIterator = bricks.iterator();
        while (brickIterator.hasNext()) {
            Brick brick = brickIterator.next();
            if (ball.checkCollision(brick)) {
                ball.bounceOff(brick);
                soundEffectManager.playHitSound();
                powerUpManager.trySpawnPowerUp(brick);
                brickIterator.remove(); // Xóa viên gạch khỏi danh sách

                // TODO: Thêm điểm cho người chơi ở đây
                break; // Chỉ xử lý va chạm với 1 viên gạch mỗi frame để tránh lỗi
            }
        }

        // Cập nhật tất cả các Power-up đang rơi
        powerUpManager.update(deltaTime, gameHeight, paddle);
    }

    public void render(GraphicsContext gc) {
        // Xóa toàn bộ màn hình trước khi vẽ lại
        gc.clearRect(0, 0, gameWidth, gameHeight);

        //In gameOver
        if (isGameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 50));
            double textWidth = 270;
            gc.fillText("Game Over", (gameWidth - textWidth) / 2, gameHeight / 2);
        // Vẽ gameUI
        gameUI.render(gc);

        // Vẽ paddle
        paddle.render(gc);

        } else {

            // Vẽ paddle
            paddle.render(gc);

            // Vẽ bóng
            ball.render(gc);

            powerUpManager.render(gc);

            // Vẽ những viên gạch còn lại
            for (Brick brick : bricks) {
                BrickPainter.draw(gc, brick);
            }
        }

        if (isPaused) {
            // Vẽ một lớp màu đen mờ lên trên toàn bộ màn hình
            gc.setFill(new Color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, gameWidth, gameHeight);

            // Vẽ chữ "PAUSED"
            gc.setFont(pausedFont);
            gc.setTextAlign(TextAlignment.CENTER);

            // Vẽ bóng đổ
            gc.setFill(new Color(0, 0, 0, 0.7));
            gc.fillText("PAUSED", gameWidth / 2.0 + 4, gameHeight / 2.0 + 4);

            // Vẽ chữ
            gc.setFill(Color.WHITE);
            gc.fillText("PAUSED", gameWidth / 2.0, gameHeight / 2.0);
        }
    }

    public void handleKeyEvent(KeyEvent event) {
        KeyCode code = event.getCode();

        // Xử lý pause/unpause khi phím được NHẤN
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getCode() == KeyCode.ESCAPE) {
                isPaused = !isPaused; // true -> false, false -> true
            }
        }

        // Nếu game đang bị tạm dừng, không xử lý các phím di chuyển
        if (isPaused) {
            return;
        }

        boolean isPressed = event.getEventType() == KeyEvent.KEY_PRESSED;

        if (code == KeyCode.A || code == KeyCode.LEFT) {
            paddle.setMovingLeft(isPressed);
        } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
            paddle.setMovingRight(isPressed);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }
}
