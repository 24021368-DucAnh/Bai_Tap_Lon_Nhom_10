package org.example.arkanoid.objects;

/** Gạch Boss: rất to, nhiều máu. Bạn có thể gắn skinCode riêng nếu có sprite. */
public final class BossBrick extends Brick {
    public BossBrick(double centerX, double centerY) {
        super(centerX - 200, centerY - 200, 400, 400); // đặt giữa
        this.destructible = true;
        this.hp = this.maxHp = 64;
        this.points = 10_000_000;
        this.skinCode = 'k'; // ví dụ 'k' = boss (đổi theo registry nếu muốn)
    }

    @Override
    public boolean onCollisionEnter() {
        hp--;
        return isDestroyed();
    }
}
