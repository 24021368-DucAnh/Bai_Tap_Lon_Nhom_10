package org.example.arkanoid.UIUX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class StageClearScreen {
    private double gameWidth;
    private double gameHeight;

    // Biến theo dõi tổng thời gian
    private double elapsedTime = 0;
    // Biến theo dõi timer cho hiệu ứng dấu chấm
    private double dotsTimer = 0;
    // Biến đếm số dấu chấm (0, 1, 2, 3)
    private int dotCount = 0;

    // Hằng số: "Loading..." xuất hiện sau 1 giây
    private final double LOADING_TEXT_DELAY = 1.0;
    // Hằng số: Tốc độ của hiệu ứng dấu chấm (mỗi 0.5 giây)
    private final double DOT_ANIMATION_INTERVAL = 0.5;

    public StageClearScreen(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    /**
     * Cập nhật logic của màn hình chờ (được gọi bởi GameManager).
     * @param deltaTime Thời gian trôi qua từ frame trước.
     */
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

    /**
     * Vẽ màn hình chờ (được gọi bởi GameManager).
     * @param stageCompleted Số thứ tự của màn vừa hoàn thành.
     */
    public void render(GraphicsContext gc, int stageCompleted) {
        // 1. Vẽ nền mờ (che đi màn chơi cũ)
        gc.setFill(Color.rgb(0, 0, 0, 0.7)); // Màu đen, mờ 70%
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // 2. Vẽ chữ "STAGE CLEAR" (luôn luôn hiện)
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(ResourceManager.uiFont);
        gc.fillText("STAGE " + stageCompleted + " CLEAR!", gameWidth / 2, gameHeight / 2 - 20);

        // 3. Chỉ vẽ "Loading..." sau khi đã qua thời gian trễ
        if (elapsedTime >= LOADING_TEXT_DELAY) {
            // Xây dựng chuỗi "Loading..." dựa trên dotCount
            String loadingText = "Loading";
            switch (dotCount) {
                case 1: loadingText += "."; break;
                case 2: loadingText += ".."; break;
                case 3: loadingText += "..."; break;
            }

            // Vẽ chữ "Loading..."
            gc.setFont(ResourceManager.uiFont);
            gc.fillText(loadingText, gameWidth / 2, gameHeight / 2 + 40);
        }
    }

    /**
     * Reset lại trạng thái của màn hình chờ (được gọi khi bắt đầu chuyển màn).
     */
    public void reset() {
        elapsedTime = 0;
        dotsTimer = 0;
        dotCount = 0;
    }
}