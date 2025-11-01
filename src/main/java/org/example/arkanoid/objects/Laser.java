// File: src/main/java/org/example/arkanoid/objects/Laser.java
package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.arkanoid.UIUX.ResourceManager;

public class Laser extends MovableObject {

    private static final double LASER_SPEED = -600.0; // Tốc độ bay lên (âm)
    private Image laserImage;

    public Laser(double x, double y) {
        super(x, y, 0, 0); // Kích thước sẽ được đặt từ ảnh

        this.laserImage = ResourceManager.laserImage; // Lấy ảnh đã tải
        if (this.laserImage != null) {
            this.width = (int) this.laserImage.getWidth();
            this.height = (int) this.laserImage.getHeight();
        } else {

            this.width = 5;
            this.height = 15;
        }

        this.dy = LASER_SPEED; // Đặt vận tốc bay lên
        this.dx = 0;
    }

    @Override
    public void update(double dt) {
        move(dt); // Tự động di chuyển theo dy
    }

    @Override
    public void render(GraphicsContext gc) {
        if (this.laserImage != null) {
            gc.drawImage(laserImage, x, y, width, height);
        } else {

            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(x, y, width, height);
        }
    }

    /**
     * Kiểm tra xem laser đã bay ra khỏi màn hình (phía trên) chưa
     */
    public boolean isOffScreenTop() {
        return this.y + this.height < 0;
    }
}