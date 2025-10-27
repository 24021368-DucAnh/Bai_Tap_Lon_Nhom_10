package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Đại diện cho một vật phẩm Power-up đang rơi trên màn hình.
 */
// Sửa lại để kế thừa từ MovableObject
public class PowerUp extends MovableObject {

    private final PowerUpType type;

    // Sửa lại constructor cho đúng
    public PowerUp(double x, double y, int width, int height, PowerUpType type) {
        super(x, y, width, height); // Gọi constructor của MovableObject
        this.type = type;
        this.dy = 150; // Vận tốc rơi xuống (pixels/giây)
    }

    @Override
    public void update(double dt) {
        // Gọi phương thức move(dt) từ lớp cha để nó tự động rơi xuống
        move(dt);
    }

    @Override
    public void render(GraphicsContext gc) {
        // Vẽ màu thay thế để phân biệt
        gc.setFill(getPowerUpColor());
        gc.fillRect(this.x, this.y, this.width, this.height);
        gc.setStroke(Color.WHITE);
        gc.strokeRect(this.x, this.y, this.width, this.height);
    }

    private Color getPowerUpColor() {
        return switch (type) {
            case ADD_LIFE -> Color.LIGHTPINK;
            case ADD_BALL -> Color.LIGHTBLUE;
            case PADDLE_GROW -> Color.LIGHTGREEN;
            default -> Color.YELLOW; // Thêm default để tránh lỗi
        };
    }

    public PowerUpType getType() {
        return type;
    }

    public boolean isOffScreen(double gameHeight) {
        return this.y > gameHeight;
    }
}