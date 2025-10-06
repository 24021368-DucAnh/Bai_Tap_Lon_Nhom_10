package org.example.arkanoid.objects;

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

    public abstract void update();

    public abstract void render();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
