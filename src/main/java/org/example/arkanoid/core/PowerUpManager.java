package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.UIUX.SoundEffectManager;
import org.example.arkanoid.objects.Brick;
import org.example.arkanoid.objects.PowerUp;
import org.example.arkanoid.objects.PowerUpType;
import org.example.arkanoid.objects.Paddle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class PowerUpManager {

    private List<PowerUp> activePowerUps = new ArrayList<>();
    private final Random random = new Random();

    private final GameManager gameManager;
    public PowerUpManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    /**
     * Cập nhật trạng thái của tất cả các Power-up đang hoạt động.
     * @param dt Thời gian trôi qua giữa các frame.
     * @param gameHeight Chiều cao của màn hình game để kiểm tra vật phẩm có rơi ra ngoài không.
     */


    public void update(double dt, double gameHeight,Paddle paddle) {
        // Dùng Iterator để xóa phần tử một cách an toàn trong lúc lặp
        activePowerUps.removeIf(powerUp -> {
            powerUp.update(dt);
            if (powerUp.checkCollision(paddle)) {
                paddle.applyPowerUp(powerUp.getType());
                return true;
            }
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

            PowerUpType[] allTypes = PowerUpType.values();
            PowerUpType randomType;
            // Nếu người chơi đang đầy máu
            if (gameManager.getHp() == 3) {
                // Lặp cho đến khi chọn được một power-up KHÔNG PHẢI là ADD_LIFE
                do {
                    randomType = allTypes[random.nextInt(allTypes.length)];
                } while (randomType == PowerUpType.ADD_LIFE);

                System.out.println("Đầy máu! Đổi power-up (nếu là ADD_LIFE) sang: " + randomType);
            } else {
                // Nếu chưa đầy máu, chọn ngẫu nhiên như bình thường
                randomType = allTypes[random.nextInt(allTypes.length)];
            }

            // 2. Tạo đối tượng PowerUp ở trung tâm viên gạch
            double px_center = destroyedBrick.getX() + (destroyedBrick.getWidth() / 2.0);
            double py_center = destroyedBrick.getY() + (destroyedBrick.getHeight() / 2.0);
            PowerUp newPowerUp = new PowerUp(px_center, py_center, randomType);

            // 3. (Quan trọng) Căn giữa PowerUp sau khi nó tự lấy W/H từ ảnh
            // Dịch X và Y về
            newPowerUp.setX(px_center - (newPowerUp.getWidth() / 2.0));
            newPowerUp.setY(py_center - (newPowerUp.getHeight() / 2.0));

            activePowerUps.add(newPowerUp);
        }
    }

    public void spawnRandomPowerUpAt(double spawnX, double spawnY) {
        // 1. Chọn ngẫu nhiên một loại power-up
        PowerUpType[] allTypes = PowerUpType.values();
        PowerUpType randomType;

        // Logic thông minh: Nếu người chơi đang đầy máu (giả sử 3 là max)
        if (gameManager.getHp() >= 3) {
            // Lặp cho đến khi chọn được một power-up KHÔNG PHẢI là ADD_LIFE
            do {
                randomType = allTypes[random.nextInt(allTypes.length)];
            } while (randomType == PowerUpType.ADD_LIFE);
        } else {
            // Nếu chưa đầy máu, chọn ngẫu nhiên như bình thường
            randomType = allTypes[random.nextInt(allTypes.length)];
        }

        // 2. Tạo đối tượng PowerUp
        PowerUp newPowerUp = new PowerUp(spawnX, spawnY, randomType);

        // 3. Căn giữa PowerUp (vì constructor PowerUp nhận tâm, nhưng sau đó
        //    nó tự lấy W/H từ ảnh, ta cần căn lại)
        newPowerUp.setX(spawnX - (newPowerUp.getWidth() / 2.0));
        newPowerUp.setY(spawnY - (newPowerUp.getHeight() / 2.0));

        // 4. Thêm vào danh sách
        activePowerUps.add(newPowerUp);
    }

    public void clearPowerUps() {
        activePowerUps.clear();
    }
}