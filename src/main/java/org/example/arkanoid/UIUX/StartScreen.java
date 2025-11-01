package org.example.arkanoid.UIUX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.ImageView;
import java.net.URL;

public class StartScreen {

    private final int screenWidth;
    private final int screenHeight;

    private static final String BACKGROUND_MUSIC_PATH = "/sound/sound effect/Game Start.mp3";

    private MediaPlayer backgroundMusicPlayer;

    public StartScreen(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
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
     *  onShowScoreBoard Hiện scoreboard đã có.
     *  onShowHowToPlay Hiện cách chơi.
     * onExitGame Thoát chương trình khi nút "Exit" được nhấn.
     */
    public Pane createContent(Runnable onStartGame, Runnable onShowScoreboard, Runnable onShowHowToPlay, Runnable onExitGame) {
        StackPane rootPane = new StackPane();
        rootPane.setPrefSize(screenWidth, screenHeight);

        VBox centerLayout = new VBox(20); // 20 là khoảng cách giữa các thành phần
        centerLayout.setAlignment(Pos.CENTER);

        if (ResourceManager.startScreenBackground != null) {
            ImageView backgroundView = new ImageView(ResourceManager.startScreenBackground);
            backgroundView.setFitWidth(screenWidth);
            backgroundView.setFitHeight(screenHeight);
            rootPane.getChildren().add(backgroundView); // Thêm làm lớp dưới cùng
        } else {
            System.err.println("Không thể tải ảnh nền từ ResourceManager. Sử dụng màu đen.");
            rootPane.setStyle("-fx-background-color: black;");
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

        // --- Scoreboard ---
        Button scoreboardButton = createStyledButton("Scoreboard");
        scoreboardButton.setOnAction(e -> {
            stopMusic();
            onShowScoreboard.run();
        });

        // Nút "Exit"
        Button exitButton = createStyledButton("Exit");
        exitButton.setOnAction(e -> {
            stopMusic();
            onExitGame.run();
        });

        // Cho vào layout
        centerLayout.getChildren().addAll(title, startButton, scoreboardButton, exitButton);

        Node howToPlayIcon = createHelpNode(onShowHowToPlay);

        rootPane.getChildren().add(centerLayout);
        StackPane.setAlignment(centerLayout, Pos.CENTER);

        rootPane.getChildren().add(howToPlayIcon);
        StackPane.setAlignment(howToPlayIcon, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(howToPlayIcon, new Insets(0, 20, 20, 0));
        return rootPane;
    }

    /**
     * Tạo nút ?.
     */
    private Node createHelpNode(Runnable onShowHowToPlay) {
        ImageView iconView = new ImageView(ResourceManager.helpIcon);

        iconView.setFitWidth(125); // Đặt kích thước
        iconView.setFitHeight(125);
        iconView.setPreserveRatio(true);
        iconView.setOpacity(0.8);
        // Hiệu ứng
        iconView.setOnMouseEntered(e -> {
            iconView.setOpacity(1.0);
            iconView.setScaleX(1.1);
            iconView.setScaleY(1.1);
        });
        iconView.setOnMouseExited(e -> {
            iconView.setOpacity(0.8);
            iconView.setScaleX(1.0);
            iconView.setScaleY(1.0);
        });

        iconView.setOnMouseClicked(e -> {
            onShowHowToPlay.run();
            SoundEffectManager.playHitSound();
        });
        return iconView;
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
