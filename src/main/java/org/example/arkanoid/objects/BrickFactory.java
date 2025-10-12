package org.example.arkanoid.objects;

/** Tạo Brick đúng loại từ code ký tự stage và index stage (để Silver tính HP). */
public final class BrickFactory {
    private BrickFactory() {}

    public static Brick fromCode(char code, double x, double y, int w, int h, int stageIndex) {
        code = Character.toLowerCase(code);
        return switch (code) {
            case 's' -> new SilverBrick(x, y, w, h, stageIndex); // xám 2+ HP
            case 'u' -> new GoldBrick(x, y, w, h, 12);           // vàng không phá được, strip 12 khung (đổi nếu khác)
            // case 'k' -> new BossBrick(...); // nếu muốn
            default  -> new ColorBrick(x, y, w, h, code);        // normal nhiều màu
        };
    }

    public static Brick normal(double x, double y, int w, int h, char code) {
        return new ColorBrick(x, y, w, h, code);
    }

    public static Brick strong(double x, double y, int w, int h, int stageIndex) {
        return new SilverBrick(x, y, w, h, stageIndex);
    }

    public static Brick unbreakable(double x, double y, int w, int h, int frames) {
        return new GoldBrick(x, y, w, h, frames);
    }
}
