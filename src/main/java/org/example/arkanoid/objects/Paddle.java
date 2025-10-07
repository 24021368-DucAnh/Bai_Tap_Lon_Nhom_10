package org.example.arkanoid.objects;

public class Paddle extends MovableObject {
    private double speed;
    //private Powerup...

    private final double initialSpeed = 500d;

    public Paddle(double x, double y, int width, int height) {
        // Paddle chỉ di chuyển theo chiều ngang => dY ban đầu là 0
        super(x, y, width, height, 0, 0);
        this.speed = initialSpeed;
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

    @Override
    public void move() {

    }

    @Override
    public void update() {
    }

    @Override
    public void render() {

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
}
