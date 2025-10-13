
package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import org.example.arkanoid.objects.*;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;


public class GameManager {
    private final double gameWidth;
    private final double gameHeight;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();

    public GameManager(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    /**
     * Khởi tạo tất cả các đối tượng game.
     */
    public void init() {
        //---------Paddle-------------
        final int PADDLE_WIDTH = 250;
        final int PADDLE_HEIGHT = 150;
        final int PADDLE_OFFSET_FROM_BOTTOM = 50; // Khoảng cách với bề dưới window

        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0; // Căn giữa paddle
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        this.paddle = new Paddle(
                initialPaddleX,
                initialPaddleY,
                PADDLE_WIDTH, PADDLE_HEIGHT,
                "file:src/main/java/org/example/arkanoid/assets/Paddle.png");

        //---------Brick-------------
        //Tải tất cả hình ảnh
        BrickSkinRegistry.initDefaults();

        //Gọi StageLoader để đọc file "Stage_1.txt" và tạo các đối tượng Brick.
        int stageToLoad = 1;
        System.out.println("Đang tải màn chơi: " + stageToLoad);
        this.bricks = StageLoader.loadFromIndex(stageToLoad, this.gameWidth);

        if (this.bricks.isEmpty()) {
            System.err.println("Không tải được gạch cho màn " + stageToLoad + ". Hãy kiểm tra file /resources/stages/Stage_1.txt");
        } else {
            System.out.println("Tải thành công " + this.bricks.size() + " viên gạch.");
        }
        //Ball
        final int BALL_DIAMETER = 20;
        double initialBallX = gameWidth / 2;
        double initialBallY = gameHeight / 2;
        ball = new Ball(initialBallX, initialBallY, BALL_DIAMETER, BALL_DIAMETER,
                1, 1, 2, 1, 1); // dx, dy = 1; speed = 2; hướng = 1
    }

    /**
     * Cập nhật logic cho tất cả đối tượng trong game.
     */
    public void update(double deltaTime) {
        paddle.update(deltaTime);
        ball.update(deltaTime);


        for (Brick brick : bricks) {
            brick.update(deltaTime);
        }
    }

    /**
     * Vẽ lại tất cả đối tượng lên màn hình.
     */
    public void render(GraphicsContext gc) {
        // Xóa toàn bộ màn hình trước khi vẽ lại
        gc.clearRect(0, 0, gameWidth, gameHeight);

        //  Vẽ paddle
        paddle.render(gc);

        // Vẽ brick
        for (Brick brick : bricks) {
            BrickPainter.draw(gc, brick);
        }
    }

    /**
     * Xử lý sự kiện nhấn/thả phím
     */
    public void handleKeyEvent(KeyEvent event) {
        boolean isPressed = event.getEventType() == KeyEvent.KEY_PRESSED;
        KeyCode code = event.getCode();

        if (code == KeyCode.A) {
            paddle.setMovingLeft(isPressed);
        } else if (code == KeyCode.D) {
            paddle.setMovingRight(isPressed);
        }
    }
}