package org.example.arkanoid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private final int GAME_WIDTH = 800;
    private final int GAME_HEIGHT = 600;

    /**
     * Tạo window, màu background
     */
    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();
        root.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        // "white" cho màu trắng, "black" cho màu đen
        root.setStyle("-fx-background-color: white;");

        // Tạo Scene
        Scene scene = new Scene(root);

        // window
        primaryStage.setTitle("Arkanoid Game"); // Đặt tiêu đề
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);      // Không cho phép thay đổi kích thước
        primaryStage.show();                   // Hiển thị cửa sổ


    }

    public static void main(String[] args) {
        launch(args);
    }

}
