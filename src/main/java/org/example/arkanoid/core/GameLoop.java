package org.example.arkanoid.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;



public class GameLoop extends AnimationTimer {
    private long lastTime = 0;
    private int updateCount = 0;
    private final GameManager gameManager;

    public GameLoop(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Mỗi lần handle được gọi, gọi update và render từ GameManager.
     */
    @Override
    public void handle(long now) {
        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        // Tính toán thời gian trôi qua (delta time) tính bằng giây
        double deltaTime = (now - lastTime) / 1_000_000_000.0;

        // Cập nhật logic game
        //gameManager.update(deltaTime);
        gameManager.update();
        gameManager.render();

        lastTime = now;
        updateCount++;
        if (updateCount % 60 == 0) {
            System.out.println("GameLoop: Cập nhật lần thứ " + updateCount + ". Thời gian trôi qua: " + deltaTime + " giây.");
        }

    }
}
