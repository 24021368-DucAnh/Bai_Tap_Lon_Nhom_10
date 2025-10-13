package org.example.arkanoid.objects;

/** Gạch vàng (không phá được), có animation theo dải frame. */
public final class GoldBrick extends Brick {
    private int frameIndex = 0;
    private double timer = 0.02, acc = 0.0; // giống C++: 0.02s
    private final int frames; // tổng số khung trong strip (cột)

    public GoldBrick(double x, double y, int w, int h, int framesInStrip) {
        super(x, y, w, h);
        this.destructible = false;
        this.hp = this.maxHp = Integer.MAX_VALUE;
        this.points = 0;
        this.skinCode = 'u'; // unbreakable gold
        this.frames = Math.max(framesInStrip, 1);
    }

    @Override
    public boolean onCollisionEnter() {
        // chạm vào thì animate (ở đây animation chạy liên tục, bạn có thể gọi Animate() riêng)
        return false; // không bao giờ bị phá
    }

    @Override
    public void update(double dt) {
        acc += dt;
        if (acc >= timer) {
            acc -= timer;
            frameIndex = (frameIndex + 1) % frames;
        }
    }

    public int getFrameIndex() { return frameIndex; }
    public int getFrameCount() { return frames; }
}
