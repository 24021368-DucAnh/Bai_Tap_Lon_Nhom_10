package org.example.arkanoid.UIUX;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.Pane;

public class BackgroundManager {
    private final MediaPlayer videoPlayer;
    private final MediaPlayer musicPlayer;
    private final MediaView mediaView;

    private final int gameWidth;
    private final int gameHeight;

    public BackgroundManager(int width, int height) {
        this.gameWidth = width;
        this.gameHeight = height;

        // Tải và khởi tạo Video Player
        String videoPath = getClass().getResource("/sound/background/canhnhayvina.mp4").toExternalForm();
        this.videoPlayer = new MediaPlayer(new Media(videoPath));
        this.videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.videoPlayer.setMute(true);
        this.mediaView = new MediaView(this.videoPlayer);

        this.mediaView.setFitWidth(gameWidth);
        this.mediaView.setFitHeight(gameHeight);
        this.mediaView.setPreserveRatio(false);

        // Tải và khởi tạo Music Player
        String musicPath = getClass().getResource("/sound/background/tamlongson.mp3").toExternalForm();
        this.musicPlayer = new MediaPlayer(new Media(musicPath));
        this.musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.musicPlayer.setVolume(0.3); //Chỉnh volume

        mediaView.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    // Khi MediaView đã được gắn vào Scene, BẮT ĐẦU PHÁT VIDEO.
                    videoPlayer.play();
                    // Xóa listener này đi để nó không chạy lại
                    mediaView.sceneProperty().removeListener(this);
                }
            }
        });
    }

    public MediaView getMediaView() {
        return this.mediaView;
    }

    public void startMedia() {
        musicPlayer.play();

    }

    public void stopMedia() {
        if (videoPlayer != null) {
            videoPlayer.stop();
        }
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
}
