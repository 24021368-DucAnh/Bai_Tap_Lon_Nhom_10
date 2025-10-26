package org.example.arkanoid.objects;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.core.GameManager;


public class Paddle extends MovableObject {
    private double speed;
    private Image image;
    private GraphicsContext gc;


    //private Powerup...
    private final int originalWidth;
    private boolean isGrown = false;
    private double growTimer = 0;
    private final double POWER_UP_DURATION = 10.0;


    private boolean movingLeft = false;
    private boolean movingRight = false;
    private final double initialSpeed = 350d;
    private final GameManager gameManager;

    public Paddle(double x, double y, int width, int height, Image image, GameManager gameManager) {
        // Paddle chỉ di chuyển theo chiều ngang => dY ban đầu là 0
        super(x, y, width, height, 0, 0);
        this.speed = initialSpeed;
        this.image = image;

        this.originalWidth = width;
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
        if (x + width > 800) x = 800 - width;
    }

    public void applyPowerUp(PowerUpType powerUpType) {
        System.out.println("Đã nhận được Power-up: " + powerUpType);

        switch (powerUpType) {
            case PADDLE_GROW:
                if (!isGrown) {
                    this.width = (int)(this.width * 1.5);
                    this.x = this.x - (this.width - originalWidth) / 2.0;
                    isGrown = true;
                }
                this.growTimer = POWER_UP_DURATION;
                break;

            case ADD_BALL:
                gameManager.addBall(); // Đã hoạt động
                break;

            case ADD_LIFE:
                // **GỌI HÀM CỦA GAMEMANAGER**
                gameManager.addHP(); // Sửa từ mainBall.addHP()
                break;
        }
    }

    @Override
    public void update(double deltaTime) {
        this.move(deltaTime);
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
}
