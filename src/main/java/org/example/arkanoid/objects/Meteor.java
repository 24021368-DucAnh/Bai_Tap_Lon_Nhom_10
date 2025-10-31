package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Đối tượng thiên thạch rơi xuống, gây sát thương cho Paddle.
 */
public class Meteor extends MovableObject {

    public Meteor(double x, double y, int width, int height, double speed) {
        // Kế thừa MovableObject, chỉ đặt vận tốc dy (rơi xuống)
        super(x, y, width, height, 0, speed);
    }

    @Override
    public void update(double deltaTime) {
        // Di chuyển theo vận tốc (dx, dy) đã set trong constructor
        move(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        // Vẽ tạm một hình Oval màu cam/đỏ
        gc.setFill(Color.ORANGERED);
        gc.fillOval(this.x, this.y, this.width, this.height);

        // Thêm một viền màu vàng cho đẹp hơn
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeOval(this.x, this.y, this.width, this.height);
    }

    /**
     * Kiểm tra xem thiên thạch đã rơi ra khỏi màn hình chưa.
     * @param gameHeight Chiều cao màn hình.
     * @return true nếu đã ra ngoài.
     */
    public boolean isOffScreen(double gameHeight) {
        // Chỉ cần kiểm tra cạnh trên (y) có vượt quá đáy màn hình không
        return this.y > gameHeight;
    }
}