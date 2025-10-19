package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;

public abstract class MovableObject extends GameObject {
    protected double dx;
    protected double dy;

    public MovableObject() {
        super();
        this.dx = 0;
        this.dy = 0;
    }

    public MovableObject(double x, double y, int width, int height) {
        super(x, y, width, height);
        this.dx = 0;
        this.dy = 0;
    }

    public MovableObject(double x, double y, int width, int height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    protected void move() {
        this.x += dx;
        this.y += dy;
    }

    protected void move(double dt) {
        // Công thức di chuyển chuẩn: vị trí += vận tốc * thời gian
        this.x += dx * dt;
        this.y += dy * dt;
    }

    @Override
    public abstract void update(double deltaTime);

    @Override
    public abstract void render(GraphicsContext graphicsContext);

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public void setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
