package org.example.arkanoid.UIUX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public abstract class UIScreen {
    protected final double gameWidth;
    protected final double gameHeight;
    protected int hoverIndex = -1; // Trạng thái hover chung cho các nút

    public UIScreen(double gameWidth, double gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    /**
     * Vẽ màn hình.
     */
    public abstract void render(GraphicsContext gc);

    /**
     * Xử lý di chuột.
     */
    public abstract void handleMouseMove(MouseEvent event);

    /**
     * Reset trạng thái
     */
    public void reset() {
        this.hoverIndex = -1;
    }
}
