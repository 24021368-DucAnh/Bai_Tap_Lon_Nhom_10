package org.example.arkanoid.UIUX;
import javafx.scene.media.AudioClip;

import java.net.URL;

public class SoundEffectManager {
    private AudioClip HitSound;

    public SoundEffectManager() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            URL resource = getClass().getResource("/sound/sound effect/Hitsound.wav");
            if (resource != null) {
                HitSound = new AudioClip(resource.toExternalForm());
            } else {
                System.err.println("Không tìm thấy file âm thanh va chạm");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải file âm thanh.");
            e.printStackTrace();
        }
    }

    /**
     * Phát âm thanh khi bóng va chạm vào gạch.
     */
    public void playHitSound() {
        if (HitSound != null) {
            HitSound.play();
        }
    }
}
