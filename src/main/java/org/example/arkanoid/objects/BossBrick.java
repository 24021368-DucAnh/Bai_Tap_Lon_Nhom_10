package org.example.arkanoid.objects;

import org.example.arkanoid.core.GameManager; // Import từ package 'core' của bạn
import java.util.Random;

/**
 * Gạch Boss: Rất to, nhiều máu.
 * ĐÃ NÂNG CẤP: Giờ có thể tự tạo thiên thạch.
 */
public final class BossBrick extends Brick {

    // Tham chiếu đến GameManager để gọi hàm spawnMeteorAt
    private final GameManager gameManager;

    // Timer cho cú bắn "chính" (từ Boss)
    private double mainShotTimer;
    private static final double MAIN_SHOT_COOLDOWN = 3;

    // Timer để đếm ngược thời gian bắn
    private double showerTimer;// Tần suất: Cứ 0.6 giây rơi 1 cục (bạn có thể đổi số này)
    private static final double SHOWER_COOLDOWN = 0.5;
    private final Random random = new Random();

    private double powerUpTimer;
    // Cứ 7.5 giây thả 1 power-up (bạn có thể đổi số này)
    private static final double POWER_UP_COOLDOWN = 6;

    // Thời gian chờ giữa 2 lần bắn (ví dụ: 3 giây)
    private static final double SHOOT_COOLDOWN = 3.0;

    /**
     * Constructor (Hàm dựng) của BossBrick.
     * @param centerX Vị trí X TÂM ĐIỂM của Boss
     * @param centerY Vị trí Y TÂM ĐIỂM của Boss
     * @param gm Tham chiếu đến GameManager
     */
    public BossBrick(double centerX, double centerY, GameManager gm) {
        // Gọi constructor của Brick
        // Kích thước 400x400, vị trí (x,y) là góc trên-trái
        super(centerX - 200, centerY - 200, 400, 400);

        this.gameManager = gm; // Lưu lại tham chiếu GameManager
        this.destructible = true;  // Có thể bị phá
        this.hp = this.maxHp = 36; // 36 máu
        this.points = 10_000_000;  // Rất nhiều điểm
        this.skinCode = 'k';       // Ký tự 'k' để BrickPainter nhận diện

        this.mainShotTimer = MAIN_SHOT_COOLDOWN;
        this.showerTimer = SHOWER_COOLDOWN;
        this.powerUpTimer = POWER_UP_COOLDOWN;
    }

    /**
     * Được gọi khi bóng va chạm.
     */
    @Override
    public boolean onCollisionEnter() {
        hp--;
        return isDestroyed(); // Trả về true nếu hp <= 0
    }

    /**
     * Hàm này được GameManager gọi MỖI FRAME.
     * Dùng để đếm giờ và bắn thiên thạch.
     */
    @Override
    public void update(double dt) {
        mainShotTimer -= dt;
        if (mainShotTimer <= 0) {
            mainShotTimer += MAIN_SHOT_COOLDOWN;

            // Vị trí spawn (giữa-đáy của Boss)
            double spawnX = this.x + (this.width / 2.0);
            double spawnY = this.y + this.height;

            gameManager.spawnMeteorAt(spawnX, spawnY);
        }

        // --- 2. THÊM MỚI: Cập nhật Mưa Thiên Thạch ---
        showerTimer -= dt;
        if (showerTimer <= 0) {
            showerTimer += SHOWER_COOLDOWN; // Reset timer mưa

            // Lấy chiều rộng màn hình từ GameManager
            double gameWidth = gameManager.getGameWidth();

            // Tạo vị trí X ngẫu nhiên (ví dụ: 10% lề 2 bên)
            double margin = gameWidth * 0.1;
            double randomX = margin + (random.nextDouble() * (gameWidth - margin * 2));

            // Vị trí Y (trên đỉnh màn hình)
            double spawnY = 0;

            // Gọi GameManager để tạo thiên thạch
            gameManager.spawnMeteorAt(randomX, spawnY);
        }

        // --- 3. THÊM MỚI: Cập nhật Thả Power-Up ---
        powerUpTimer -= dt;
        if (powerUpTimer <= 0) {
            powerUpTimer += POWER_UP_COOLDOWN; // Reset timer

            // Vị trí rơi (ví dụ: trung tâm Boss)
            double spawnX = this.x + (this.width / 2.0);
            double spawnY = this.y + (this.height / 2.0); // Rơi từ giữa người

            // Gọi hàm mới của GameManager
            gameManager.spawnRandomPowerUpAt(spawnX, spawnY);
        }
    }
}

