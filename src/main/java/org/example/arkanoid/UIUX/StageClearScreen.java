package org.example.arkanoid.UIUX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class StageClearScreen extends UIScreen{
    // Biến theo dõi tổng thời gian
    private double elapsedTime = 0;
    // Biến theo dõi timer cho hiệu ứng dấu chấm
    private double dotsTimer = 0;
    // Biến đếm số dấu chấm
    private int dotCount = 0;

    // "Loading..." xuất hiện sau 1 giây
    private final double LOADING_TEXT_DELAY = 1.0;
    // ốc độ của hiệu ứng dấu chấm mỗi 0.5 giây
    private final double DOT_ANIMATION_INTERVAL = 0.5;

    private int stageCompleted = 1;

    public StageClearScreen(double gameWidth, double gameHeight) {
        super(gameWidth,gameHeight);
    }

    public void update(double deltaTime) {
        elapsedTime += deltaTime;
        if (elapsedTime >= LOADING_TEXT_DELAY) {
            dotsTimer += deltaTime;
            if (dotsTimer >= DOT_ANIMATION_INTERVAL) {
                dotCount = (dotCount + 1) % 4;
                dotsTimer = 0; // reset
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // Vẽ chữ "STAGE COMPLETED"
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(ResourceManager.uiFont);
        gc.fillText("STAGE " + stageCompleted + " COMPLETED!", gameWidth / 2, gameHeight / 2 - 20);

        // Vẽ "Loading..."
        if (elapsedTime >= LOADING_TEXT_DELAY) {
            String loadingText = "Loading";
            switch (dotCount) {
                case 1: loadingText += "."; break;
                case 2: loadingText += ".."; break;
                case 3: loadingText += "..."; break;
            }

            gc.setFont(ResourceManager.uiFont);
            gc.fillText(loadingText, gameWidth / 2, gameHeight / 2 + 40);
        }
    }

    @Override
    public void handleMouseMove(MouseEvent event) {
    }
    /**
     * Reset lại trạng thái khi bắt đầu chuyển màn.
     */
    @Override
    public void reset() {
        super.reset();
        elapsedTime = 0;
        dotsTimer = 0;
        dotCount = 0;
    }

    public void setStageCompleted(int stage) {
        this.stageCompleted = stage;
    }
}