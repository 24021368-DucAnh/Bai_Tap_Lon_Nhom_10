package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;

public class PowerUp extends GameObject {
    private String type;
    private int duration;

    public PowerUp() {
        super();
        this.type = "null";
        this.duration = 0;
    }

    public PowerUp(double x, double y, int width, int height, String type, int duration) {
        super(x, y, width, height);

        this.type = type;
        this.duration = duration;
    }

    public PowerUp(String type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public void applyEffect (Paddle paddle) {

    }

    public void removeEffect(Paddle paddle) {

    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {

    }
}
