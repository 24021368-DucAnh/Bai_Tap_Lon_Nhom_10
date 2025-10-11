package org.example.arkanoid.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;


public class GameLoop extends AnimationTimer {
    private final GameManager gameManager;

    public GameLoop(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Mỗi lần handle được gọi, gọi update và render từ GameManager.
     */
    @Override
    public void handle(long now) {
        gameManager.update();
        gameManager.render();
    }
}