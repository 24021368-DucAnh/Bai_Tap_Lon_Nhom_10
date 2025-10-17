package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.objects.Brick;
import org.example.arkanoid.objects.PowerUp;
import org.example.arkanoid.objects.PowerUpType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PowerUpManager {

    private List<PowerUp> activePowerUps = new ArrayList<>();
    private final Random random = new Random();

    /**
     * Cập nhật trạng thái của tất cả các Power-up đang hoạt động.
     * @param dt Thời gian trôi qua giữa các frame.
     * @param gameHeight Chiều cao của màn hình game để kiểm tra vật phẩm có rơi ra ngoài không.
     */
    public void update(double dt, double gameHeight) {
        // Dùng Iterator để xóa phần tử một cách an toàn trong lúc lặp
        activePowerUps.removeIf(powerUp -> {
            powerUp.update(dt);
            return powerUp.isOffScreen(gameHeight); // Xóa nếu rơi ra khỏi màn hình
        });
    }

    /**
     * Vẽ tất cả các Power-up đang hoạt động.
     * @param gc "Cây cọ" để vẽ lên Canvas.
     */
    public void render(GraphicsContext gc) {
        for (PowerUp powerUp : activePowerUps) {
            powerUp.render(gc);
        }
    }

    /**
     * Thử tạo một Power-up tại vị trí của viên gạch vừa vỡ.
     * @param destroyedBrick Viên gạch bị phá hủy.
     */
    public void trySpawnPowerUp(Brick destroyedBrick) {
        // Tỉ lệ 25% rơi ra power-up (bạn có thể thay đổi số 25)
        if (random.nextInt(100) < 25) {
            // 1. Chọn ngẫu nhiên một loại power-up
            PowerUpType[] allTypes = PowerUpType.values();
            PowerUpType randomType = allTypes[random.nextInt(allTypes.length)];

            // 2. Tạo đối tượng PowerUp ở trung tâm viên gạch
            double pw = 30; // Kích thước power-up
            double ph = 15;
            double px = destroyedBrick.getX() + (destroyedBrick.getWidth() / 2) - (pw / 2);
            double py = destroyedBrick.getY();

            activePowerUps.add(new PowerUp(px, py, (int) pw, (int) ph, randomType));
        }
    }
}