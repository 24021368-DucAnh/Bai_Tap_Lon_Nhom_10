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

public class Main extends Application {
    private final int GAME_WIDTH = 600;
    private final int GAME_HEIGHT = 800;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Arkanoid");

        // Tạo màn hình bắt đầu
        StartScreen startScreen = new StartScreen(GAME_WIDTH, GAME_HEIGHT);

        // Gọi startGame() khi nhấn nút Start
        // Thoát khi nhấn nút Exit
        Pane startPane = startScreen.createContent(this::startGame, Platform::exit);

        Scene startScene = new Scene(startPane);

        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);      // Không cho phép thay đổi kích thước
        primaryStage.show();                   // Hiển thị cửa sổ
    }

    public void startGame() {
        Canvas canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Tạo BackgroundManager
        BackgroundManager backgroundManager = new BackgroundManager(GAME_WIDTH, GAME_HEIGHT);

        // Bắt đầu và tải background và music
        backgroundManager.startMedia();

        // Tạo GameManager
        GameManager gameManager = new GameManager(GAME_WIDTH, GAME_HEIGHT);
        gameManager.init(); // Khởi tạo các đối tượng game

        /*Pane root = new Pane(canvas);
        root.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        // "white" cho màu trắng, "black" cho màu đen
        root.setStyle("-fx-background-color: black;");*/

        // Thêm video trước canvas (game)
        Pane root = new Pane(backgroundManager.getMediaView(), canvas);
        root.setPrefSize(GAME_WIDTH, GAME_HEIGHT);

        // Tạo Scene
        Scene scene = new Scene(root);

        //Chuyển việc xử lý phím cho GameManager
        scene.setOnKeyPressed(event -> gameManager.handleKeyEvent(event));
        scene.setOnKeyReleased(event -> gameManager.handleKeyEvent(event));

        // Chuyển Stage sang Scene của game
        primaryStage.setScene(scene);

        // Yêu cầu focus vào scene để nhận phím
        root.requestFocus();

        //Gameloop
        GameLoop gameLoop = new GameLoop(gameManager, gc);
        gameLoop.start();
        //System.out.println("Ứng dụng Arkanoid đã khởi động. Game Loop đang chạy...");

    }

    public static void main(String[] args) {
        launch(args);
    }

}
