package org.example.arkanoid.UIUX;
import javafx.scene.media.AudioClip;

import java.net.URL;

public final class SoundEffectManager {
    private static AudioClip hitSound;
    private static AudioClip ballPowerupSound;
    private static AudioClip deathSound;
    private static AudioClip extraLifeSound;
    private static AudioClip gameOverSound;
    private static AudioClip paddlePowerupSound;
    private static AudioClip roundStartSound;
    private static AudioClip levelswitch;

    static {
        hitSound = loadClip("/sound/sound effect/hitsound.wav");
        ballPowerupSound = loadClip("/sound/sound effect/ball powerup.wav");
        deathSound = loadClip("/sound/sound effect/death sound.wav");
        extraLifeSound = loadClip("/sound/sound effect/extra life.wav");
        gameOverSound = loadClip("/sound/sound effect/Game Over.mp3");
        paddlePowerupSound = loadClip("/sound/sound effect/paddle powerup.wav");
        roundStartSound = loadClip("/sound/sound effect/Round Start.mp3");
        levelswitch = loadClip("/sound/sound effect/switchlevel.mp3");
    }

    private SoundEffectManager() {
    }

    private static AudioClip loadClip(String path) {
        try {
            URL resource = SoundEffectManager.class.getResource(path);
            if (resource != null) {
                return new AudioClip(resource.toExternalForm());
            } else {
                System.err.println("Không tìm thấy file âm thanh tại: " + path);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải file âm thanh: " + path);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Phát âm thanh khi bóng va chạm vào gạch.
     */
    public static void playHitSound() {
        if (hitSound != null) {
            hitSound.play();
        }
    }

    /**
     * Phát âm thanh khi nhận power-up bóng.
     */
    public static void playBallPowerupSound() {
        if (ballPowerupSound != null) {
            ballPowerupSound.play(1.5);
        }
    }

    /**
     * Phát âm thanh khi mất mạng.
     */
    public static void playDeathSound() {
        if (deathSound != null) {
            deathSound.play(0.5);
        }
    }

    /**
     * Phát âm thanh khi nhận thêm mạng.
     */
    public static void playExtraLifeSound() {
        if (extraLifeSound != null) {
            extraLifeSound.play();
        }
    }

    /**
     * Phát âm thanh Game Over.
     */
    public static void playGameOverSound() {
        if (gameOverSound != null) {
            gameOverSound.play();
        }
    }

    /**
     * Phát âm thanh khi nhận power-up thanh trượt.
     */
    public static void playPaddlePowerupSound() {
        if (paddlePowerupSound != null) {
            paddlePowerupSound.play(2);
        }
    }

    /**
     * Phát âm thanh bắt đầu màn chơi.
     */
    public static void playRoundStartSound() {
        if (roundStartSound != null) {
            roundStartSound.play();
        }
    }

    /**
     * Phát âm thanh sang vòng mới.
     */
    public static void playLevelSwitchSound() {
        if (levelswitch!= null) {
            levelswitch.play();
        }
    }
}
