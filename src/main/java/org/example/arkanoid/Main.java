package org.example.arkanoid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.core.GameLoop;
import org.example.arkanoid.core.GameManager;
import javafx.scene.canvas.Canvas;

public class Main extends Application {
    private final int GAME_WIDTH = 800;
    private final int GAME_HEIGHT = 600;

    /**
     * Tạo window, màu background
     */
    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Tạo GameManager
        GameManager gameManager = new GameManager(GAME_WIDTH, GAME_HEIGHT);
        gameManager.init(); // Khởi tạo các đối tượng game

        Pane root = new Pane(canvas);
        root.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        // "white" cho màu trắng, "black" cho màu đen
        root.setStyle("-fx-background-color: black;");

        // Tạo Scene
        Scene scene = new Scene(root);

        //Chuyển việc xử lý phím cho GameManager
        scene.setOnKeyPressed(event -> gameManager.handleKeyEvent(event));
        scene.setOnKeyReleased(event -> gameManager.handleKeyEvent(event));

        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);      // Không cho phép thay đổi kích thước
        primaryStage.show();                   // Hiển thị cửa sổ

        //Gameloop
        GameLoop gameLoop = new GameLoop(gameManager, gc);
        gameLoop.start();
        System.out.println("Ứng dụng Arkanoid đã khởi động. Game Loop đang chạy...");

    }

    public static void main(String[] args) {
        launch(args);
    }

}
