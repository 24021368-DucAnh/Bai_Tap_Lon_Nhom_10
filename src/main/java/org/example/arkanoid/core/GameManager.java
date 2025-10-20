package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import org.example.arkanoid.objects.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    private final double gameWidth;
    private final double gameHeight;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private PowerUpManager powerUpManager;
    private boolean isGameOver = false;

    public GameManager(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    public void init() {
        //---------Paddle-------------
        final int PADDLE_WIDTH = 46; // Giảm kích thước paddle một chút cho dễ chơi
        final int PADDLE_HEIGHT = 20;
        final int PADDLE_OFFSET_FROM_BOTTOM = 50;

        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0;
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        this.powerUpManager = new PowerUpManager();

        // Hình ảnh paddle có thể cần điều chỉnh lại cho phù hợp với W/H mới
        this.paddle = new Paddle(
                initialPaddleX,
                initialPaddleY,
                PADDLE_WIDTH, PADDLE_HEIGHT,
                "file:src/main/resources/images/Paddle.png");

        //---------Brick-------------
        BrickSkinRegistry.initDefaults();
        int stageToLoad = 1;
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
        paddle.update(deltaTime);
        ball.update(deltaTime);

        // --- Xử lý va chạm ---

        // 1. Va chạm giữa Bóng và Paddle
        if (ball.checkCollision(paddle)) {
            ball.bounceOff(paddle);
        }

        // 2. Va chạm giữa Bóng và Gạch
        // Dùng Iterator để có thể xóa phần tử khỏi List một cách an toàn trong lúc lặp
        Iterator<Brick> brickIterator = bricks.iterator();
        while (brickIterator.hasNext()) {
            Brick brick = brickIterator.next();
            if (ball.checkCollision(brick)) {
                ball.bounceOff(brick);
                powerUpManager.trySpawnPowerUp(brick);
                brickIterator.remove(); // Xóa viên gạch khỏi danh sách

                // TODO: Thêm điểm cho người chơi ở đây
                break; // Chỉ xử lý va chạm với 1 viên gạch mỗi frame để tránh lỗi
            }
        }

        // Cập nhật tất cả các Power-up đang rơi
        powerUpManager.update(deltaTime, gameHeight);
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
    }

    public void handleKeyEvent(KeyEvent event) {
        boolean isPressed = event.getEventType() == KeyEvent.KEY_PRESSED;
        KeyCode code = event.getCode();

        if (code == KeyCode.A || code == KeyCode.LEFT) {
            paddle.setMovingLeft(isPressed);
        } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
            paddle.setMovingRight(isPressed);
        }
    }
}
