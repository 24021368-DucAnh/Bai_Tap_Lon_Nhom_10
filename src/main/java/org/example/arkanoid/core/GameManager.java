package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import org.example.arkanoid.objects.*;
import javafx.scene.input.KeyCode;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    private final double gameWidth;
    private final double gameHeight;

    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();

    public GameManager(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    public void init() {
        // Khởi tạo paddle
        final int PADDLE_WIDTH = 250;
        final int PADDLE_HEIGHT = 150;
        final int PADDLE_OFFSET_FROM_BOTTOM = 50; // Khoảng cách với bề dưới window

        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0;
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        // Hình ảnh paddle có thể cần điều chỉnh lại cho phù hợp với W/H mới
        this.paddle = new Paddle(
                initialPaddleX,
                initialPaddleY,
                PADDLE_WIDTH, PADDLE_HEIGHT,
                "file:src/main/java/org/example/arkanoid/assets/Paddle.png");

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
        final double BALL_SPEED = 350.0; // Tốc độ hợp lý hơn (pixels / giây)
        double initialBallX = gameWidth / 2;
        double initialBallY = gameHeight / 2;

        // Gọi constructor mới của Ball
        ball = new Ball(initialBallX, initialBallY, BALL_DIAMETER, BALL_SPEED, gameWidth, gameHeight);
    }

    public void update(double deltaTime) {
        paddle.update(deltaTime);

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
                brickIterator.remove(); // Xóa viên gạch khỏi danh sách
                // TODO: Thêm điểm cho người chơi ở đây
                break; // Chỉ xử lý va chạm với 1 viên gạch mỗi frame để tránh lỗi
            }
        }
    }

    public void render(GraphicsContext gc) {
        // 1. Xóa toàn bộ màn hình trước khi vẽ lại
        gc.clearRect(0, 0, gameWidth, gameHeight);

        // Vẽ paddle
        paddle.render(gc);

        // Vẽ bóng
        ball.render(gc);

        // Vẽ những viên gạch còn lại
        for (Brick brick : bricks) {
            BrickPainter.draw(gc, brick);
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