package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {
    private double speed;
    private double radius;
    private int directionX; // -1 (trái) / 1 (phải)
    private int directionY; // -1 (lên) / 1 (xuống)

    // Thêm 2 biến để biết kích thước màn hình
    private final double gameWidth;
    private final double gameHeight;

    public Ball(double x, double y, int diameter, double speed, double gameWidth, double gameHeight) {
        super(x, y, diameter, diameter, 0, 0);
        this.speed = speed;
        this.radius = diameter / 2.0;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        // Thiết lập hướng ban đầu (ví dụ: đi sang phải và đi lên)
        this.directionX = 1;
        this.directionY = -1;
    }

    /**
     * Kiểm tra va chạm hình chữ nhật giữa bóng và đối tượng khác.
     */
    public boolean checkCollision(GameObject other) {
        // Tọa độ các cạnh của bóng
        double ballLeft = x - radius;
        double ballRight = x + radius;
        double ballTop = y - radius;
        double ballBottom = y + radius;

        // Tọa độ các cạnh của đối tượng kia
        double otherLeft = other.getX();
        double otherRight = other.getX() + other.getWidth();
        double otherTop = other.getY();
        double otherBottom = other.getY() + other.getHeight();

        // Kiểm tra xem có chồng lấn không
        return ballRight > otherLeft && ballLeft < otherRight && ballBottom > otherTop && ballTop < otherBottom;
    }

    /**
     * Logic nảy lại được cải tiến để xác định hướng nảy chính xác hơn.
     */
    public void bounceOff(GameObject other) {
        double ballBottom = y + radius;
        double ballTop = y - radius;
        double ballLeft = x - radius;
        double ballRight = x + radius;

        double otherTop = other.getY();
        double otherBottom = other.getY() + other.getHeight();
        double otherLeft = other.getX();
        double otherRight = other.getX() + other.getWidth();

        // Tính toán độ chồng lấn (overlap) giữa bóng và vật thể
        double overlapX = Math.min(ballRight, otherRight) - Math.max(ballLeft, otherLeft);
        double overlapY = Math.min(ballBottom, otherBottom) - Math.max(ballTop, otherTop);

        // Nếu độ chồng lấn theo chiều ngang lớn hơn chiều dọc, va chạm xảy ra ở mặt trên/dưới
        if (overlapX > overlapY) {
            directionY = -directionY;
            // Đẩy nhẹ bóng ra để tránh bị kẹt trong vật thể
            if (y < other.getY()) { // Đập vào mặt trên của vật thể
                y = other.getY() - radius;
            } else { // Đập vào mặt dưới của vật thể
                y = other.getY() + other.getHeight() + radius;
            }
        } else { // Ngược lại, va chạm xảy ra ở mặt trái/phải
            directionX = -directionX;
            // Đẩy nhẹ bóng ra để tránh bị kẹt
            if (x < other.getX()) { // Đập vào mặt trái
                x = other.getX() - radius;
            } else { // Đập vào mặt phải
                x = other.getX() + other.getWidth() + radius;
            }
        }
    }


    /**
     * Hàm move() không còn cần thiết, logic được gộp vào update()
     */
    @Override
    public void move() {
        // Bỏ trống hoặc xóa bỏ hoàn toàn
    }

    @Override
    public void update(double deltaTime) {
        // Cập nhật vị trí dựa trên tốc độ, hướng và deltaTime
        this.x += directionX * speed * deltaTime;
        this.y += directionY * speed * deltaTime;

        // Kiểm tra va chạm với biên màn hình
        // Va chạm biên trái
        if (x - radius < 0) {
            x = radius;
            directionX = -directionX;
        }
        // Va chạm biên phải
        else if (x + radius > gameWidth) {
            x = gameWidth - radius;
            directionX = -directionX;
        }

        // Va chạm biên trên
        if (y - radius < 0) {
            y = radius;
            directionY = -directionY;
        }

        // Nếu bóng rơi xuống dưới đáy màn hình (game over)
        // Hiện tại chỉ cho nảy lại để test
        if (y + radius > gameHeight) {
            y = gameHeight - radius;
            directionY = -directionY;
            //Kết thúc game
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    // --- Getter và Setter ---
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public int getDirectionX() { return directionX; }
    public void setDirectionX(int directionX) { this.directionX = directionX; }
    public int getDirectionY() { return directionY; }
    public void setDirectionY(int directionY) { this.directionY = directionY; }
}
