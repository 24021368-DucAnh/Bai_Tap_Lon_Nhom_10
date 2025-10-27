package org.example.arkanoid;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.UIUX.BackgroundManager;
import org.example.arkanoid.UIUX.StartScreen;
import org.example.arkanoid.core.GameLoop;
import org.example.arkanoid.core.GameManager;
import javafx.scene.canvas.Canvas;
import org.example.arkanoid.core.GameNavigator;

public class Main extends Application implements GameNavigator {
    private final int GAME_WIDTH = 600;
    private final int GAME_HEIGHT = 800;

    private Stage primaryStage;
    private Scene startScene;

    private StartScreen startScreen;      // Lưu lại scene của màn hình bắt đầu
    private GameLoop gameLoop;        // Lưu lại vòng lặp game để có thể stop()
    private BackgroundManager backgroundManager; // Lưu lại để có thể stop media

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Arkanoid");

        // Tạo màn hình bắt đầu
        this.startScreen = new StartScreen(GAME_WIDTH, GAME_HEIGHT);

        // Gọi startGame() khi nhấn nút Start
        // Thoát khi nhấn nút Exit
        Pane startPane = this.startScreen.createContent(this::startGame, Platform::exit);

        this.startScene = new Scene(startPane);

        primaryStage.setScene(this.startScene);
        primaryStage.setResizable(false);      // Không cho phép thay đổi kích thước
        primaryStage.show();                   // Hiển thị cửa sổ

    }

    public void startGame() {
        Canvas canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Tạo BackgroundManager
        this.backgroundManager = new BackgroundManager(GAME_WIDTH, GAME_HEIGHT);

        // Bắt đầu và tải background và music
        this.backgroundManager.startMedia();

        // Tạo GameManager
        GameManager gameManager = new GameManager(GAME_WIDTH, GAME_HEIGHT, this);
        gameManager.init(); // Khởi tạo các đối tượng game

        // Thêm video trước canvas (game)
        Pane root = new Pane(this.backgroundManager.getMediaView(), canvas);
        root.setPrefSize(GAME_WIDTH, GAME_HEIGHT);

        // Tạo Scene
        Scene scene = new Scene(root);

        //Chuyển việc xử lý phím cho GameManager
        scene.setOnKeyPressed(gameManager::handleKeyEvent);
        scene.setOnKeyReleased(gameManager::handleKeyEvent);

        // Chuyển việc xử lý chuột cho GameManager
        scene.setOnMouseClicked(gameManager::handleMouseClick);
        scene.setOnMouseMoved(gameManager::handleMouseMove);

        // Chuyển Stage sang Scene của game
        primaryStage.setScene(scene);

        // Yêu cầu focus vào scene để nhận phím
        root.requestFocus();

        //Gameloop
        this.gameLoop = new GameLoop(gameManager, gc);
        this.gameLoop.start();

    }

    @Override
    public void goToStartScreen() {
        // Dừng GameLoop
        if (this.gameLoop != null) {
            this.gameLoop.stop();
            this.gameLoop = null;
        }

        // Dừng background
        if (this.backgroundManager != null) {
            this.backgroundManager.stopMedia();
            this.backgroundManager = null;
        }

        // Chuyển Stage trở lại màn hình bắt đầu
        if (this.primaryStage != null && this.startScene != null) {
            primaryStage.setScene(this.startScene);

            if (this.startScreen != null) {
                this.startScreen.playMusic();
            }
        }
    }

    @Override
    public void retryGame() {
        if (this.gameLoop != null) {
            this.gameLoop.stop();
        }
        if (this.backgroundManager != null) {
            this.backgroundManager.stopMedia();
            this.backgroundManager = null;
        }

        startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
