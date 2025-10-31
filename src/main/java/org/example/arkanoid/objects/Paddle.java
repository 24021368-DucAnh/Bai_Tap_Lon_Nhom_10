package org.example.arkanoid.objects;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.UIUX.SoundEffectManager;
import org.example.arkanoid.core.GameManager;


public class Paddle extends MovableObject {
    private double speed;
    private Image image;
    private GraphicsContext gc;

    private boolean movingLeft = false;
    private boolean movingRight = false;
    private final double initialSpeed = 350d;
    private final GameManager gameManager;
    private final int originalWidth; // Lưu chiều rộng gốc
    private boolean isGrown = false;

    //Dành cho Paddle_Grow
    private double powerUpTimer = 0.0; // Bộ đếm thời gian
    private final double POWER_UP_DURATION =7.0; // 7 GIÂY
    private final double gameBoundWidth;

    //Dành cho Laser
    private boolean hasLaser = false;
    private double laserTimer = 0.0;
    private final double LASER_DURATION = 3.0;


    public Paddle(double x, double y, int width, int height, Image image,
                  GameManager gameManager) {
        super(x, y, width, height, 0, 0);
        this.speed = initialSpeed;
        this.image = image;

        // **QUAN TRỌNG: Lưu lại chiều rộng ban đầu**
        this.originalWidth = width;
        this.gameBoundWidth = gameManager.getGameWidth();
        this.gameManager = gameManager;
    }

    public void moveLeft() {
        this.dx = -speed;
    }

    public void moveRight() {

        this.dx = speed;
    }

    public void stopMoving() {
        this.dx = 0;
    }

    /*public void applyPowerUp(PowerUp.PowerUpType powerUpType) {

    }*/

    /** Di chuyen */
    public void move(double deltaTime) {
        if (movingLeft && !movingRight) {
            moveLeft();
        } else if (!movingLeft && movingRight) {
            moveRight();
        } else {
            stopMoving();
        }
        // Cập nhật vị trí
        x += dx * deltaTime;;
        y += dy;

        // Giới hạn paddle trong màn hình
        if (x < 0) x = 0;
        if (x + width > this.gameBoundWidth) x = this.gameBoundWidth - width;
    }

    public void applyPowerUp(PowerUpType powerUpType) {
        System.out.println("Đã nhận được Power-up: " + powerUpType);


        switch (powerUpType) {
            case PADDLE_GROW:
                SoundEffectManager.playPaddlePowerupSound();
                if (!isGrown) {
                    this.width = (int)(this.originalWidth * 1.5); // Tăng 50% từ KÍCH THƯỚC GỐC
                    // Căn lại paddle để nó lớn ra từ tâm
                    this.x = this.x - (this.width - originalWidth) / 2.0;
                    isGrown = true;
                }

                // Dù đang lớn hay không, cứ ăn là reset timer
                this.powerUpTimer = POWER_UP_DURATION;
                break;


            case ADD_BALL:
                SoundEffectManager.playBallPowerupSound();
                gameManager.addBall(); // Đã hoạt động
                break;


            case ADD_LIFE:
                // **GỌI HÀM CỦA GAMEMANAGER**
                if(gameManager.getHp() < 3) {
                    gameManager.addHP();

                }
                SoundEffectManager.playExtraLifeSound();
                break;

            case LASER:
                this.hasLaser = true;
                this.laserTimer = LASER_DURATION; // Reset timer

                break;
        }
    }


    @Override
    public void update(double deltaTime) {

        this.move(deltaTime);

        // --- THÊM LOGIC ĐẾM NGƯỢC POWER-UP ---
        if (isGrown) {
            // 1. Đếm ngược
            powerUpTimer -= deltaTime; // Trừ đi thời gian đã trôi qua

            // 2. Kiểm tra nếu hết giờ
            if (powerUpTimer <= 0) {
                // Hết giờ, trả paddle về trạng thái ban đầu
                // Căn lại vị trí x TRƯỚC KHI thay đổi width
                this.x = this.x + (this.width - originalWidth) / 2.0;

                this.width = originalWidth; // Trả về kích thước gốc
                isGrown = false;
                powerUpTimer = 0.0; // Đặt về 0 cho chắc
                System.out.println("Power-up PADDLE_GROW đã hết hạn!");
            }
        }

        if (hasLaser) {
            laserTimer -= deltaTime;
            if (laserTimer <= 0) {
                hasLaser = false;
                laserTimer = 0.0;
                System.out.println("Power-up LASER đã hết hạn!");
            }
        }
    }


    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getInitialSpeed() {
        return initialSpeed;
    }
    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }
    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public boolean hasLaser() {
        return this.hasLaser;
    }
}
