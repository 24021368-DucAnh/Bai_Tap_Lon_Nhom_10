package org.example.arkanoid.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.UIUX.GameUI;


public class GameLoop extends AnimationTimer {
    private long lastTime = 0;
    //private int updateCount = 0;
    private final GameManager gameManager;
    private final GraphicsContext gameGC; // Graphics cho Game
    private final GameUI gameUI;
    private final GraphicsContext uiGC;

    public GameLoop(GameManager gameManager, GraphicsContext gameGC, GameUI gameUI, GraphicsContext uiGC) {
        this.gameManager = gameManager;
        this.gameGC = gameGC;
        this.gameUI = gameUI;
        this.uiGC = uiGC;
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

        lastTime = now;

        // Cập nhật logic game
        gameManager.update(deltaTime);
        gameManager.render(gameGC);

        gameUI.setScore(gameManager.getScore());
        gameUI.setLives(gameManager.getHp());
        gameUI.updateStage(gameManager.getStage());
        gameUI.render(uiGC);

        /*updateCount++;
        if (updateCount % 60 == 0) {
            System.out.println("GameLoop: Cập nhật lần thứ " + updateCount + ". Thời gian trôi qua: " + deltaTime + " giây.");
        }*/

    }
}
