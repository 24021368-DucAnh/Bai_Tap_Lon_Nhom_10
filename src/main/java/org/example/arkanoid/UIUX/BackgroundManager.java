package org.example.arkanoid.UIUX;

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
        String videoPath = getClass().getResource("/canhnhayvina.mp4").toExternalForm();
        this.videoPlayer = new MediaPlayer(new Media(videoPath));
        this.videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.videoPlayer.setMute(true);
        this.mediaView = new MediaView(this.videoPlayer);

        // Tải và khởi tạo Music Player
        String musicPath = getClass().getResource("/tamlongson.mp3").toExternalForm();
        this.musicPlayer = new MediaPlayer(new Media(musicPath));
        this.musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.musicPlayer.setVolume(0.3); //Chỉnh volume
    }

    public MediaView getMediaView() {
        return this.mediaView;
    }

    public void startMedia() {
        // Đồng bộ hóa Video
        this.videoPlayer.setOnReady(() -> {
            this.mediaView.setFitWidth(gameWidth);
            this.mediaView.setFitHeight(gameHeight);
            this.mediaView.setPreserveRatio(false);

            videoPlayer.play();
        });

        // Đồng bộ hóa Music
        this.musicPlayer.setOnReady(() -> {
            musicPlayer.play();
        });
    }

}
