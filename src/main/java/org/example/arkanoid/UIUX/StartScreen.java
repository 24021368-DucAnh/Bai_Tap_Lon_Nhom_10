package org.example.arkanoid.UIUX;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.net.URL;

public class StartScreen {

    private final int screenWidth;
    private final int screenHeight;

    private static final String BACKGROUND_IMAGE_PATH = "/images/startbg.jpg";
    private static final String BACKGROUND_MUSIC_PATH = "/sound/sound effect/Game Start.mp3";

    private MediaPlayer backgroundMusicPlayer;

    public StartScreen(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        ResourceManager.loadAllResources();
        initializeMusic();
    }

    private void initializeMusic() {
        try {
            URL resource = getClass().getResource(BACKGROUND_MUSIC_PATH);
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                backgroundMusicPlayer = new MediaPlayer(media);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

                backgroundMusicPlayer.setVolume(1.5); // 50% âm lượng
            } else {
                System.err.println("Không tìm thấy file nhạc nền: " + BACKGROUND_MUSIC_PATH);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải nhạc nền: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }

    public void playMusic() {
        if (backgroundMusicPlayer != null && backgroundMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            backgroundMusicPlayer.play();
        }
    }

    /**
     * Tạo và trả về một Pane chứa tất cả các thành phần của màn hình bắt đầu.
     *  onStartGame Bắt đầu Game khi nút "Start Game" được nhấn.
     * onExitGame Thoát chương trình khi nút "Exit" được nhấn.
     */
    public Pane createContent(Runnable onStartGame, Runnable onExitGame) {
        // Sử dụng VBox để căn giữa theo chiều dọc
        VBox layout = new VBox(20); // 20 là khoảng cách giữa các thành phần
        layout.setPrefSize(screenWidth, screenHeight);
        layout.setAlignment(Pos.CENTER);

        try {
            String imageUrl = getClass().getResource(BACKGROUND_IMAGE_PATH).toExternalForm();
            layout.setStyle(
                    "-fx-background-image: url('" + imageUrl + "'); " +
                            "-fx-background-size: cover; " + // 'cover' để ảnh phủ hết, 'contain' để vừa vặn
                            "-fx-background-position: center center;"
            );
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh nền: " + e.getMessage() + ". Sử dụng màu đen.");
            layout.setStyle("-fx-background-color: black;");
        }

        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }

        // Tiêu đề Game
        Label title = new Label("ARKANOID");
        title.setFont(ResourceManager.titleFont);
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 10, 0.7, 4, 4);");

        // Nút "Start Game"
        Button startButton = createStyledButton("Start Game");
        startButton.setOnAction(e -> {
            stopMusic();
            onStartGame.run();
            SoundEffectManager.playRoundStartSound();
        });

        // Nút "Exit"
        Button exitButton = createStyledButton("Exit");
        exitButton.setOnAction(e -> {
            stopMusic();
            onExitGame.run();
        });

        // Cho vào layout
        layout.getChildren().addAll(title, startButton, exitButton);

        return layout;
    }

    /**
     * Tạo Button
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(300);

        String fontFamily = ResourceManager.buttonFont.getFamily();
        double fontSize = ResourceManager.buttonFont.getSize();

        String fontStyle = String.format("-fx-font-family: '%s'; -fx-font-size: %.0fpx; ", fontFamily, fontSize);

        // Style cơ bản
        String baseStyle = fontStyle +
                "-fx-background-color: #333333; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-radius: 5px;";

        // Style khi di chuột qua
        String hoverStyle = fontStyle +
                "-fx-background-color: #555555; " +
                "-fx-text-fill: white; " +
                "-fx-border-color: cyan; " +
                "-fx-border-width: 2px; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-radius: 5px;";

        button.setStyle(baseStyle);

        // Thêm hiệu ứng khi di chuột qua
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));

        // Trở về style cũ khi chuột rời đi
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }
}
