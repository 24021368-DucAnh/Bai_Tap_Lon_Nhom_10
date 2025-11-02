package org.example.arkanoid.objects;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/** Map ký tự (skinCode) -> ảnh. Với Silver/Gold dùng sprite-strip.
 *  Đổi đường dẫn ảnh cho khớp file PNG của bạn trong resources/images/bricks/.
 */
public final class BrickSkinRegistry {
    private BrickSkinRegistry(){}

    // ảnh đơn cho color bricks
    private static final Map<Character, Image> COLOR = new HashMap<>();

    // silver strip (6 frame: 0..5)
    private static Image SILVER_STRIP;

    // gold strip (N frame, ví dụ 12)
    private static Image GOLD_STRIP;
    private static Image BOSS_IMAGE;

    public static void initDefaults() {
        // NORMAL colors (đổi theo tên file của bạn)
        COLOR.put('w', img("/images/bricks/normal_white.png"));
        COLOR.put('o', img("/images/bricks/normal_brown.png"));
        COLOR.put('t', img("/images/bricks/normal_teal.png"));
        COLOR.put('g', img("/images/bricks/normal_green.png"));
        COLOR.put('r', img("/images/bricks/normal_red.png"));
        COLOR.put('b', img("/images/bricks/normal_blue.png"));
        COLOR.put('p', img("/images/bricks/normal_pink.png"));
        COLOR.put('y', img("/images/bricks/normal_yellow.png"));

        // STRIP images (đổi tên file khớp sprite-sheet của bạn)
        SILVER_STRIP = img("/images/bricks/silver_strip.png"); // chứa 6 frame ngang
        GOLD_STRIP   = img("/images/bricks/gold_strip.png");   // chứa N frame ngang
        BOSS_IMAGE   = img("/images/bricks/bossBrick.png");
    }

    public static Image colorImage(char code) {
        return COLOR.get(Character.toLowerCase(code));
    }

    public static Image silverStrip() { return SILVER_STRIP; }
    public static Image goldStrip()   { return GOLD_STRIP; }
    public static Image bossImage() { return BOSS_IMAGE; }

    private static Image img(String path) {
        var url = BrickSkinRegistry.class.getResource(path);
        if (url == null) { System.err.println("Missing image: " + path); return null; }
        return new Image(url.toExternalForm());
    }
}
