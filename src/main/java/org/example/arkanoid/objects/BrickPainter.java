package org.example.arkanoid.objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/** Vẽ Brick theo skinCode/type. Nếu có ảnh -> vẽ ảnh; nếu thiếu -> vẽ màu fallback. */
public final class BrickPainter {
    private BrickPainter(){}

    public static void draw(GraphicsContext g, Brick b) {
        // Silver?
        if (b instanceof SilverBrick silver) {
            if (drawStrip(g, BrickSkinRegistry.silverStrip(), b.getX(), b.getY(), b.getWidth(), b.getHeight(), silver.getFrameIndex(), 6)) {
                return;
            }
        }
        // Gold?
        if (b instanceof GoldBrick gold) {
            if (drawStrip(g, BrickSkinRegistry.goldStrip(), b.getX(), b.getY(), b.getWidth(), b.getHeight(), gold.getFrameIndex(), gold.getFrameCount())) {
                return;
            }
        }
        // Color (normal)
        Image img = BrickSkinRegistry.colorImage(b.getSkinCode());
        if (img != null) {
            g.drawImage(img, b.getX(), b.getY(), b.getWidth(), b.getHeight());
            return;
        }

        // Fallback màu nếu thiếu ảnh
        drawFallbackColor(g, b);
    }

    /** Vẽ từ sprite-strip ngang: frameIndex {0..frameCount-1}. */
    private static boolean drawStrip(GraphicsContext g, Image strip, double x, double y, double w, double h,
                                     int frameIndex, int frameCount) {
        if (strip == null || frameCount <= 0) return false;
        double fw = strip.getWidth() / frameCount; // width mỗi frame trong strip
        double fh = strip.getHeight();
        double sx = frameIndex * fw;
        g.drawImage(strip,                   // image
                sx, 0, fw, fh,           // source rect
                x,  y,  w,  h);          // dest rect
        return true;
    }

    private static void drawFallbackColor(GraphicsContext g, Brick b) {
        Color color = switch (b.getSkinCode()) {
            case 'w' -> Color.web("#fafafa");
            case 'o' -> Color.web("#8d6e63");
            case 't' -> Color.web("#00897b");
            case 'g' -> Color.web("#66bb6a");
            case 'r' -> Color.web("#ef5350");
            case 'b' -> Color.web("#3949ab");
            case 'p' -> Color.web("#f06292");
            case 'y' -> Color.web("#ffd54f");
            case 's' -> (b.getHp() * 1.0 / b.getMaxHp()) <= 0.5 ? Color.web("#bdbdbd") : Color.web("#9e9e9e"); // xám
            case 'u' -> Color.web("#ffca28"); // vàng
            default  -> Color.web("#90a4ae");
        };
        g.setFill(color);
        g.fillRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        g.setStroke(Color.color(0,0,0,0.25));
        g.strokeRect(b.getX()+0.5, b.getY()+0.5, b.getWidth()-1, b.getHeight()-1);
    }
}
