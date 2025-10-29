package org.example.arkanoid;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.UIUX.BackgroundManager;
import org.example.arkanoid.UIUX.GameUI;
import org.example.arkanoid.UIUX.StartScreen;
import org.example.arkanoid.core.GameLoop;
import org.example.arkanoid.core.GameManager;
import javafx.scene.canvas.Canvas;
import org.example.arkanoid.core.GameNavigator;
import org.example.arkanoid.core.ResourceManager;

public class Main extends Application implements GameNavigator {
    private final int WINDOW_WIDTH = 700;
    private final int WINDOW_HEIGHT = 950;

    private final double GAME_AREA_X = 20;      // Lề trái 20px
    private final double GAME_AREA_Y = 100;     // Lề trên (chiều cao UI) 100px

    // (600 - 20 lề trái - 20 lề phải)
    private final double GAME_AREA_WIDTH = 660;
    // (900 - 100 lề trên)
    private final double GAME_AREA_HEIGHT = 850;

    // Khu vực cho UI (nằm ở trên cùng)
    private final double UI_AREA_WIDTH = WINDOW_WIDTH; // Rộng bằng cửa sổ
    private final double UI_AREA_HEIGHT = GAME_AREA_Y; // Cao bằng lề trên của game

    private Stage primaryStage;
    private Scene startScene;
    private Scene gameScene;

    private StartScreen startScreen;
    private GameLoop gameLoop;
    private BackgroundManager backgroundManager;
    private GameManager gameManager;
    private GameUI gameUI;
    private Canvas gameCanvas;
    private Canvas uiCanvas;
    private GraphicsContext uiGC;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Arkanoid");

        //-------- Tải tài nguyên ----------
        ResourceManager.loadAllResources();

        // Tạo màn hình bắt đầu
        this.startScreen = new StartScreen(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Gọi startGame() khi nhấn nút Start
        // Thoát khi nhấn nút Exit
        Pane startPane = this.startScreen.createContent(this::startGame, Platform::exit);

        this.startScene = new Scene(startPane);

        this.primaryStage.setScene(this.startScene);
        this.primaryStage.setResizable(false);      // Không cho phép thay đổi kích thước
        this.primaryStage.show();                   // Hiển thị cửa sổ

    }

    public void startGame() {
        // Pane gốc để chứa mọi thứ
        Pane gameRoot = new Pane();
        gameRoot.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameRoot.setStyle("-fx-background-color: black;");

        // Tạo BackgroundManager game
        this.backgroundManager = new BackgroundManager((int)GAME_AREA_WIDTH, (int)GAME_AREA_HEIGHT);
        Pane gameBackgroundPane = backgroundManager.getBackgroundPane();

        // Khu vực chơi game
        gameBackgroundPane.setLayoutX(GAME_AREA_X);
        gameBackgroundPane.setLayoutY(GAME_AREA_Y);

        gameRoot.getChildren().add(gameBackgroundPane);

        // Game Canvas
        this.gameCanvas = new Canvas(GAME_AREA_WIDTH, GAME_AREA_HEIGHT);
        this.gameCanvas.setLayoutX(GAME_AREA_X);
        this.gameCanvas.setLayoutY(GAME_AREA_Y);
        gameRoot.getChildren().add(this.gameCanvas);
        GraphicsContext gameGC = this.gameCanvas.getGraphicsContext2D();

        // UI Canvas
        this.uiCanvas = new Canvas(UI_AREA_WIDTH, UI_AREA_HEIGHT);
        this.uiCanvas.setLayoutX(0);
        this.uiCanvas.setLayoutY(0);
        gameRoot.getChildren().add(this.uiCanvas);
        this.uiGC = this.uiCanvas.getGraphicsContext2D();


        this.gameUI = new GameUI(UI_AREA_WIDTH, UI_AREA_HEIGHT);


        // Tạo GameManager
        this.gameManager = new GameManager(GAME_AREA_WIDTH, GAME_AREA_HEIGHT, this);
        // Khởi tạo các đối tượng game
        this.gameManager.init();

        // Tạo Scene
        this.gameScene = new Scene(gameRoot);

        //Chuyển việc xử lý phím cho GameManager
        this.gameScene.setOnKeyPressed(this.gameManager::handleKeyEvent);
        this.gameScene.setOnKeyReleased(this.gameManager::handleKeyEvent);

        // Chuyển việc xử lý chuột cho GameManager
        this.gameCanvas.setOnMouseClicked(this.gameManager::handleMouseClick);
        this.gameCanvas.setOnMouseMoved(this.gameManager::handleMouseMove);

        // Chuyển Stage sang Scene của game
        this.primaryStage.setScene(gameScene);

        // Yêu cầu focus vào scene để nhận phím
        gameRoot.requestFocus();

        //Gameloop
        this.gameLoop = new GameLoop(gameManager, gameGC, gameUI, uiGC);
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
        this.backgroundManager = null;


        // Chuyển Stage trở lại màn hình bắt đầu
        if (this.primaryStage != null && this.startScene != null) {
            this.primaryStage.setScene(this.startScene);

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
        this.backgroundManager = null;

        startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
