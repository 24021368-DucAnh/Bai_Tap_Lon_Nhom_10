package org.example.arkanoid.objects;

public class Brick extends GameObject {
    private int hitPoints;
    private String type;

    public Brick() {
        super();
        this.hitPoints = 0;
        this.type = "null";
    }

    public Brick(double x, double y, int width, int height) {
        super(x, y, width, height);
        this.hitPoints = 0;
        this.type = "null";
    }

    public Brick(double x, double y, int width, int height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
    }

    public void takeHit() {
        if (this.hitPoints > 0) {
            this.hitPoints--;
        }
    }

    public boolean isDestroyed() {
        return this.hitPoints <= 0;
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
