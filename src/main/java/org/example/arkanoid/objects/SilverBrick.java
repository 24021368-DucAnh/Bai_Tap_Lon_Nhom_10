package org.example.arkanoid.objects;

/** Gạch xám: HP = 2 + stage/8, khung nứt theo %HP còn lại. */
public final class SilverBrick extends Brick {
    // Số mức nứt: 0..5 (giống switch trong C++; 0 = nguyên vẹn)
    private int frameIndex = 0;

    public SilverBrick(double x, double y, int w, int h, int stageNumber) {
        super(x, y, w, h);
        this.destructible = true;
        this.maxHp = this.hp = 2 + (stageNumber / 8);
        this.points = stageNumber * 50;
        this.skinCode = 's'; // để Painter chọn sprite silver
        pickBrokenFrame(); // init frame theo hp hiện tại
    }

    @Override
    public boolean onCollisionEnter() {
        if (!destructible) return false;
        hp--;
        pickBrokenFrame();
        return isDestroyed(); // true -> xóa
    }

    private void pickBrokenFrame() {
        double percent = (hp * 100.0) / maxHp;
        if (percent >= 100.0)      frameIndex = 0;
        else if (percent >= 83.2)  frameIndex = 1;
        else if (percent >= 66.5)  frameIndex = 2;
        else if (percent >= 50.0)  frameIndex = 3;
        else if (percent >= 33.2)  frameIndex = 4;
        else                       frameIndex = 5;
    }

    /** Painter dùng để cắt frame trong sprite-strip silver. */
    public int getFrameIndex() { return frameIndex; }
}
