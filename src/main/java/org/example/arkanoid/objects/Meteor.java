package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.arkanoid.UIUX.ResourceManager; // <-- THÊM MỚI
import org.example.arkanoid.UIUX.SpriteAnimator;

/**
 * Đối tượng thiên thạch rơi xuống, gây sát thương cho Paddle.
 */
public class Meteor extends MovableObject {

    private SpriteAnimator animator;
    // Tốc độ animation (ví dụ: 0.1s/frame = 10fps). Bạn có thể đổi số này.
    private static final double ANIMATION_SPEED = 0.5;
    private static final int METEOR_DISPLAY_SIZE = 35;

    /**
     * Constructor mới: Nhận vào VỊ TRÍ TRUNG TÂM (center) để spawn.
     * Nó sẽ tự lấy kích thước ảnh và tự căn chỉnh toạ độ (x, y).
     */
    public Meteor(double spawnCenterX, double spawnCenterY, double speed) {
        // Gọi super() với giá trị tạm, ta sẽ set x,y,w,h ngay sau đây
        super(0, 0, METEOR_DISPLAY_SIZE, METEOR_DISPLAY_SIZE);
        this.dy = speed;

        Image[] frames = ResourceManager.getMeteorAnimation();

        if (frames != null && frames.length > 0) {
            // Khởi tạo animator
            this.animator = new SpriteAnimator(frames, ANIMATION_SPEED, false);
        } else {
            // Fallback nếu không tải được ảnh
            System.err.println("Không tải được animation Meteor, dùng kích thước 20x20");
            this.width = METEOR_DISPLAY_SIZE;
            this.height = METEOR_DISPLAY_SIZE;
            this.animator = null;
        }

        // Sau khi có width/height, set toạ độ (x, y) (góc trên trái)
        // để thiên thạch được CĂN GIỮA tại điểm spawn
        this.x = spawnCenterX - (this.width / 2.0);
        this.y = spawnCenterY - (this.height / 2.0);
    }

    @Override
    public void update(double deltaTime) {
        // Di chuyển theo vận tốc (dx, dy) đã set trong constructor
        move(deltaTime);

        if (animator != null) {
            animator.update(deltaTime);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Vẽ tạm một hình Oval màu cam/đỏ
        if (animator != null) {
            Image frame = animator.getFrame();
            if (frame != null) {
                gc.drawImage(frame, this.x, this.y, this.width, this.height);
                return; // Đã vẽ xong
            }
        }

        // Fallback: Nếu animator bị lỗi, vẽ hình oval đỏ như cũ
        gc.setFill(Color.ORANGERED);
        gc.fillOval(this.x, this.y, this.width, this.height);
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
