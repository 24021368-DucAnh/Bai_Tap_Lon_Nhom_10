package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    protected double x;
    protected double y;

    protected int height;
    protected int width;

    public GameObject() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public GameObject(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    /**
     * Kiểm tra va chạm hình chữ nhật (AABB collision detection).
     * @param other Đối tượng khác để kiểm tra va chạm.
     * @return true nếu có va chạm, ngược lại là false.
     */
    public boolean checkCollision(GameObject other) {
        // AABB - Axis-Aligned Bounding Box
        return this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y;
    }


    public abstract void update(double deltaTime);

    public abstract void render(GraphicsContext gc);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
