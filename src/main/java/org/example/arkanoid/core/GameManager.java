
package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import org.example.arkanoid.objects.Paddle;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import org.example.arkanoid.objects.GameObject;


public class GameManager {
    private final GraphicsContext gc;
    private final double gameWidth;
    private final double gameHeight;

    private Paddle paddle;


    public GameManager(GraphicsContext gc, double gameWidth, double gameHeight) {
        this.gc = gc;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    /**
     * Khởi tạo tất cả các đối tượng game.
     */
    public void init() {

        this.paddle = new Paddle(gc,
                (gameWidth - 150) / 2,
                gameHeight - 50, // Căn giữa paddle
                150, 25,
                "file:src/main/java/org/example/arkanoid/assets/Paddle.png");

    }

    /**
     * Cập nhật logic cho tất cả đối tượng trong game.
     */
    public void update() {
        paddle.update();

    }

    /**
     * Vẽ lại tất cả đối tượng lên màn hình.
     */
    public void render() {
        // 1. Xóa toàn bộ màn hình trước khi vẽ lại
        gc.clearRect(0, 0, gameWidth, gameHeight);

        // 2. Vẽ paddle
        paddle.render();

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