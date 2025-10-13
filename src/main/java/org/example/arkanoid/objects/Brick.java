package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;

/** Base cho mọi loại gạch: vị trí, kích thước (kế thừa GameObject),
 *  điểm, hp, phá được hay không, và skinCode (ký tự từ layout).
 */
public abstract class Brick extends GameObject {
    protected int points;          // điểm khi phá
    protected boolean destructible; // có phá được?
    protected int hp, maxHp;       // máu hiện tại / tối đa
    protected char skinCode = '\0';// ký tự mã (w,o,t,g,r,b,p,y,s,u,...)

    protected Brick(double x, double y, int w, int h) {
        super(x, y, w, h);
    }

    /** Gọi khi bóng chạm gạch. Trả về true nếu gạch bị phá (hp <= 0 và destructible). */
    public abstract boolean onCollisionEnter();

    /** Cho hiệu ứng/animation (Gold), Silver đổi frame nếu muốn, mặc định no-op. */

    public abstract void update(double dt);

    /** Đã bị phá chưa (chỉ áp dụng cho destructible). */
    public boolean isDestroyed() {
        return destructible && hp <= 0;
    }

    public boolean isDestructible() { return destructible; }
    public int getPoints() { return points; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }

    public char getSkinCode() { return skinCode; }
    public void setSkinCode(char c) { skinCode = Character.toLowerCase(c); }


    @Override public void render(GraphicsContext gc) {/* drawn by BrickPainter */}
}
