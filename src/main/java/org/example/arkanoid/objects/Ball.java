package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
        // Check va chạm khối
        double otherX = other.getX();
        double otherY = other.getY();
        int otherWidth = other.getWidth();
        int otherHeight = other.getHeight();

        boolean collisionX = x + radius > otherX && x - radius < otherX + otherWidth;
        boolean collisionY = y + radius > otherY && y - radius < otherY + otherHeight;

        return collisionX && collisionY;
    }

    public void bounceOff(GameObject other) {
        // Dựa vào vị trí va chạm mà đảo ngược hướng
        // Va chạm từ trái hoặc phải thì đảo hướng X
        if (x < other.getX() || x > other.getX() + other.getWidth()) {
            directionX = -directionX;
        }
        // Va chạm trên dưới thì đảo ngược Y
        if (y < other.getY() || y > other.getY() + other.getHeight()) {
            directionY = -directionY;
        }
    }

    @Override
    public void move() {
        this.x += dx * directionX * speed;
        this.y += dy * directionY * speed;
    }

    @Override
    public void update(double deltaTime) {
        move();
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
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
