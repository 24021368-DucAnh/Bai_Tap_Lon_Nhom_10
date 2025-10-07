package org.example.arkanoid.objects;

public class Ball extends MovableObject {
    // private double directionX, directionY;
    private double speed;
    private final double initialSpeed = 600d;
    private double radius;
    private int directionX; // -1 (trái) / 1 (phải)
    private int directionY; // -1 (lên) / 1 (xuống)

    public Ball(double x, double y, int width, int height,
                double dx, double dy, double speed,
                int directionX, int directionY) {
        super(x, y, width, height, dx, dy);
        this.speed = speed;
        this.directionX = directionX;
        this.directionY = directionY;

    }

    public boolean checkCollision(GameObject other) {
        return false;
    }

    public void bounceOff(GameObject other) {

    }

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

    public int getDirectionX() {
        return directionX;
    }

    public void setDirectionX(int directionX) {
        this.directionX = directionX;
    }

    public int getDirectionY() {
        return directionY;
    }

    public void setDirectionY(int directionY) {
        this.directionY = directionY;
    }
}
