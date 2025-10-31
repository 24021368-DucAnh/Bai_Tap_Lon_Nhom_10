package org.example.arkanoid.UIUX;

import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class ScoreboardScreen extends UIScreen{
    private Rectangle2D backButton;

    // Biến để lưu điểm mới và trạng thái
    private long newScore = 0;
    private boolean hasSaved = false; // Cờ để đảm bảo chỉ lưu 1 lần

    public ScoreboardScreen(double gameWidth, double gameHeight) {
        super(gameWidth,gameHeight);

        // Vị trí nút "Return to Menu"
        double btnWidth = 350;
        double btnHeight = 70;
        double btnX = (gameWidth - btnWidth) / 2.0;
        double btnY = gameHeight - btnHeight - 200; // Cách đáy
        this.backButton = new Rectangle2D(btnX, btnY, btnWidth, btnHeight);
    }

    public void setNewScore(long score) {
        this.newScore = score;
        this.hasSaved = false;
    }

    @Override
    public void render(GraphicsContext gc) {
        // Chỉ lưu điểm 1 lần
        if (!hasSaved) {
            ScoreManager.addScore(newScore);
            hasSaved = true;
        }

        // Vẽ nền
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameWidth, gameHeight);

        // Tải danh sách điểm
        List<Long> topScores = ScoreManager.loadHighScores();

        // Cài đặt vẽ chữ
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        // Vẽ Top Highest Scores
        double currentY = 100; // Vị trí Y bắt đầu
        gc.setFont(ResourceManager.uiFont);
        gc.fillText("Top Highest Scores", gameWidth / 2.0, currentY);
        currentY += 80;

        boolean alreadyHighlighted = false;

        // Vẽ 5 điểm cao nhất
        gc.setFont(ResourceManager.buttonFont);
        for (int i = 0; i < topScores.size(); i++) {
            String text = (i + 1) + ". " + topScores.get(i);

            Long scoreInList = topScores.get(i);

            // Chỉ highlight nếu chưa được highlight
            if (!alreadyHighlighted && scoreInList.equals(newScore)) {
                gc.setFill(Color.CYAN);
                text += " (Your Score)";
                alreadyHighlighted = true;
            }

            gc.fillText(text, gameWidth / 2.0, currentY);
            currentY += 60;
            gc.setFill(Color.WHITE);
        }

        // Vẽ Điểm Vừa Lưu
        // Nếu điểm vừa lưu không có trong top 5, hiển thị nó ở dưới
        if (!topScores.contains(newScore)) {
            currentY += 40; // Thêm khoảng cách
            gc.setFill(Color.CYAN);
            gc.fillText("Your Score: " + newScore, gameWidth / 2.0, currentY);
            gc.setFill(Color.WHITE);
        }

        // Vẽ nút "Return to Menu"
        gc.setFont(ResourceManager.buttonFont);
        if (hoverIndex == 0) {
            gc.setStroke(Color.CYAN);
        } else {
            gc.setStroke(Color.WHITE);
        }
        gc.setLineWidth(2);
        gc.strokeRoundRect(backButton.getMinX(), backButton.getMinY(),
                            backButton.getWidth(), backButton.getHeight(),
                        15, 15);

        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Return to Menu",
                    backButton.getMinX() + backButton.getWidth() / 2,
                    backButton.getMinY() + backButton.getHeight() / 2);
        gc.setTextBaseline(VPos.BASELINE); // Reset
    }

    @Override
    public void handleMouseMove(MouseEvent event) {
        if (backButton.contains(event.getX(), event.getY())) {
            this.hoverIndex = 0;
        } else {
            this.hoverIndex = -1;
        }
    }

    public ScoreboardAction handleMouseClick(MouseEvent event) {
        if (hoverIndex == 0) {
            return ScoreboardAction.GOTO_MENU;
        }
        return ScoreboardAction.NONE;
    }

    @Override
    public void reset() {
        super.reset();
    }
}