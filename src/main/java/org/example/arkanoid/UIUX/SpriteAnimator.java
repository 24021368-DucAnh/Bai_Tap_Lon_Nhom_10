package org.example.arkanoid.UIUX; // Hoặc package của bạn

import javafx.scene.image.Image;

/**
 * Quản lý một chuỗi ảnh động (animation)
 * Nó theo dõi frame hiện tại dựa trên thời gian (dt).
 */
public class SpriteAnimator {

    private final Image[] frames;     // Mảng chứa tất cả các frame ảnh
    private final double frameDuration;  // Thời gian hiển thị 1 frame (giây)

    private final boolean loops;

    private int currentFrameIndex;  // Frame hiện tại đang là frame nào
    private double accumulator;       // Biến đếm thời gian

    public SpriteAnimator(Image[] frames, double frameDuration) {
        // Gọi constructor mới, mặc định loops = true
        this(frames, frameDuration, true);
    }

    /**
     * @param frames Mảng các ảnh (đã được tải)
     * @param frameDuration Thời gian (giây) cho mỗi frame. Ví dụ: 0.1 = 10 frame/giây
     */
    public SpriteAnimator(Image[] frames, double frameDuration, boolean loops) {
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.loops = loops;
        this.currentFrameIndex = 0;
        this.accumulator = 0.0;
    }

    /**
     * Cập nhật logic animation dựa trên delta time (dt)
     * @param dt Thời gian trôi qua từ frame trước
     */
    public void update(double dt) {
        if (frames == null || frames.length == 0) return;

        if (!loops && currentFrameIndex == frames.length - 1) {
            return;
        }

        accumulator += dt;

        while (accumulator >= frameDuration) {
            accumulator -= frameDuration;

            if (loops) {
                // Kiểu cũ: Lặp lại
                currentFrameIndex = (currentFrameIndex + 1) % frames.length;
            } else if (currentFrameIndex < frames.length - 1) {
                // Kiểu mới: Tăng lên cho đến khi kịch frame
                currentFrameIndex++;
            }
        }
    }

    /**
     * Lấy ảnh của frame hiện tại để vẽ
     * @return Image của frame hiện tại
     */
    public Image getFrame() {
        if (frames == null || frames.length == 0) return null;
        return frames[currentFrameIndex];
    }
}