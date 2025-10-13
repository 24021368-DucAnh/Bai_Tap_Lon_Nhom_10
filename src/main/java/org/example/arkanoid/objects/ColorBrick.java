package org.example.arkanoid.objects;

/** Gạch thường 1-hit vỡ. Màu/ảnh dựa theo skinCode: w,o,t,g,r,b,p,y. */
public final class ColorBrick extends Brick {

    public ColorBrick(double x, double y, int w, int h, char colorCode) {
        super(x, y, w, h);
        this.destructible = true;
        this.hp = this.maxHp = 1;
        this.skinCode = Character.toLowerCase(colorCode);

        this.points = switch (this.skinCode) {
            case 'b' -> 100;  // blue
            case 'g' -> 80;
            case 'o' -> 60;   // orange
            case 'p' -> 110;  // pink
            case 'r' -> 90;   // red
            case 'w' -> 50;   // white (có thể dùng texture riêng)
            case 't' -> 70;   // teal/turquoise
            case 'y' -> 120;  // yellow
            default  -> 50;
        };
    }

    @Override
    public boolean onCollisionEnter() {
        if (!destructible) return false;
        hp--;               // 1 hit vỡ
        return isDestroyed();
    }

    @Override public void update(double deltaTime){}
}
