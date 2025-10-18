package org.example.arkanoid.objects;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;


public class Paddle extends MovableObject {
    private double speed;
    private Image image;
    private GraphicsContext gc;
    //private Powerup...
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private final double initialSpeed = 350d;

    public Paddle(double x, double y, int width, int height, String imagepath) {
        // Paddle chỉ di chuyển theo chiều ngang => dY ban đầu là 0
        super(x, y, width, height, 0, 0);
        this.speed = initialSpeed;
        this.image = new Image(imagepath);
        //this.currentPowerUp = null;
        //this.powerUpDurationLeft = 0;
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
        // Phương thức này sẽ được dùng trong tương lai để kích hoạt hiệu ứng
        // Hiện tại có thể để trống
        System.out.println("Đã va chạm với power-up: " + powerUpType);
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
