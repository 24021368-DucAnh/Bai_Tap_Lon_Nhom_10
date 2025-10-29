package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image; // THÊM IMPORT
import org.example.arkanoid.UIUX.ResourceManager; // THÊM IMPORT
import org.example.arkanoid.UIUX.SpriteAnimator;

/**
 * Đại diện cho một vật phẩm Power-up đang rơi trên màn hình.
 */
// Sửa lại để kế thừa từ MovableObject
public class PowerUp extends MovableObject {

    private final PowerUpType type;

    private SpriteAnimator animator;
    // THÊM MỚI: Tốc độ animation (ví dụ: 0.1s/frame = 10fps)
    private static final double ANIMATION_SPEED = 0.1;

    // Sửa lại constructor cho đúng
    public PowerUp(double x, double y, PowerUpType type) {
        super(x, y, 0, 0); // Gọi constructor của MovableObject
        this.type = type;
        this.dy = 150; // Vận tốc rơi xuống (pixels/giây)

        Image[] animationFrames = ResourceManager.getPowerUpAnimation(type);

        if (animationFrames != null && animationFrames.length > 0) {
            // Lấy kích thước (w/h) từ frame ảnh đầu tiên
            this.width = (int) animationFrames[0].getWidth();
            this.height = (int) animationFrames[0].getHeight();

            // Khởi tạo animator
            this.animator = new SpriteAnimator(animationFrames, ANIMATION_SPEED);
        } else {
            // Fallback nếu không có ảnh: tự đặt kích thước và không có animator
            System.err.println("Không có animation cho " + type + ", dùng kích thước fallback.");
            this.width = 30;
            this.height = 15;
            this.animator = null;
        }
    }

    @Override
    public void update(double dt) {
        // Gọi phương thức move(dt) từ lớp cha để nó tự động rơi xuống
        move(dt);

        // THAY ĐỔI: Cập nhật animator
        if (animator != null) {
            animator.update(dt);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (animator != null) {
            Image frame = animator.getFrame();
            if (frame != null) {
                gc.drawImage(frame, this.x, this.y, this.width, this.height);
                return; // Đã vẽ xong
            }
        }

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