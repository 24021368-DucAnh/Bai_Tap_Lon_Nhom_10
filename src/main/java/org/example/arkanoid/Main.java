package org.example.arkanoid;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import org.example.arkanoid.UIUX.*;
import org.example.arkanoid.core.GameLoop;
import org.example.arkanoid.core.GameManager;
import javafx.scene.canvas.Canvas;
import org.example.arkanoid.core.GameNavigator;

public class Main extends Application implements GameNavigator {
    private final int WINDOW_WIDTH = 700;
    private final int WINDOW_HEIGHT = 950;

    // --- Game UI ---
    private final double UI_AREA_X = 0;
    private final double UI_AREA_Y = 0;
    private final double UI_AREA_WIDTH = WINDOW_WIDTH;
    private final double UI_AREA_HEIGHT = 100;

    // --- Background ---
    private final double BACKGROUND_PANE_X = 0;
    private final double BACKGROUND_PANE_Y = UI_AREA_HEIGHT;
    private final double BACKGROUND_PANE_WIDTH = WINDOW_WIDTH;
    private final double BACKGROUND_PANE_HEIGHT = WINDOW_HEIGHT - UI_AREA_HEIGHT;

    // --- Frame ---
    private final double FRAME_THICKNESS_LEFT = 20;
    private final double FRAME_THICKNESS_RIGHT = 20;
    private final double FRAME_THICKNESS_TOP = 20;
    private final double FRAME_THICKNESS_BOTTOM = 0;

    // --- Khu vực Game---
    // Vị trí của Canvas (bên trong khung)
    private final double GAME_AREA_X = BACKGROUND_PANE_X + FRAME_THICKNESS_LEFT; // 0 + 20 = 20
    private final double GAME_AREA_Y = BACKGROUND_PANE_Y + FRAME_THICKNESS_TOP;  // 100 + 20 = 120
    // Kích thước của Canvas
    private final double GAME_AREA_WIDTH = BACKGROUND_PANE_WIDTH - FRAME_THICKNESS_LEFT - FRAME_THICKNESS_RIGHT;
    private final double GAME_AREA_HEIGHT = BACKGROUND_PANE_HEIGHT - FRAME_THICKNESS_TOP - FRAME_THICKNESS_BOTTOM;

    private Stage primaryStage;
    private Scene startScene;
    private Scene gameScene;
    private Scene howToPlayScene;

    private StartScreen startScreen;
    private GameLoop gameLoop;
    private BackgroundManager backgroundManager;
    private GameManager gameManager;
    private GameUI gameUI;
    private Canvas gameCanvas;
    private Canvas uiCanvas;
    private GraphicsContext uiGC;

    private HowToPlayScreen howToPlayScreen;
    private ScoreboardScreen scoreboardScreen;
    private Scene scoreboardScene; // Scene cho scoreboard
    private AnimationTimer scoreboardLoop; // Vòng lặp render cho scoreboard (vì nó có hover)
    private AnimationTimer howToPlayLoop;

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
        Pane startPane = this.startScreen.createContent(
                this::startGame,
                this::showScoreboardScreen,
                this::showHowToPlayScreen,
                Platform::exit
        );

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
        this.backgroundManager = new BackgroundManager((int)this.BACKGROUND_PANE_WIDTH, (int)this.BACKGROUND_PANE_HEIGHT);
        Pane gameBackgroundPane = backgroundManager.getBackgroundPane();

        // Khu vực chơi game
        gameBackgroundPane.setLayoutX(this.BACKGROUND_PANE_X);
        gameBackgroundPane.setLayoutY(this.BACKGROUND_PANE_Y);

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

        if (this.scoreboardLoop != null) {
            this.scoreboardLoop.stop();
        }
        if (this.howToPlayLoop != null) {
            this.howToPlayLoop.stop();
        }

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

    private void showScoreboardScreen() {
        if (this.scoreboardScene == null) {
            Pane scoreboardPane = new Pane();
            scoreboardPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            Canvas scoreboardCanvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
            GraphicsContext sbgc = scoreboardCanvas.getGraphicsContext2D();

            this.scoreboardScreen = new ScoreboardScreen(WINDOW_WIDTH, WINDOW_HEIGHT);

            scoreboardCanvas.setOnMouseMoved(e -> this.scoreboardScreen.handleMouseMove(e));
            scoreboardCanvas.setOnMouseClicked(e -> {
                ScoreboardAction action = this.scoreboardScreen.handleMouseClick(e);
                if (action == ScoreboardAction.GOTO_MENU) {
                    goToStartScreen();
                }
            });

            // Tạo 1 vòng lặp render riêng cho Scoreboard để vẽ hover
            this.scoreboardLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    scoreboardScreen.render(sbgc);
                }
            };

            scoreboardPane.getChildren().add(scoreboardCanvas);
            this.scoreboardScene = new Scene(scoreboardPane);
        }

        this.primaryStage.setScene(this.scoreboardScene);
        this.scoreboardScreen.reset();
        this.scoreboardScreen.setNewScore(-1);
        this.scoreboardLoop.start();
    }

    private void showHowToPlayScreen() {
        // Chỉ tạo 1 lần
        if (this.howToPlayScene == null) { // <-- SỬA LỖI 1: Dùng howToPlayScene
            Pane howToPlayPane = new Pane();
            howToPlayPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            Canvas howToPlayCanvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
            GraphicsContext htpGC = howToPlayCanvas.getGraphicsContext2D();

            this.howToPlayScreen = new HowToPlayScreen(WINDOW_WIDTH, WINDOW_HEIGHT);

            // Gắn sự kiện chuột
            howToPlayCanvas.setOnMouseMoved(e -> this.howToPlayScreen.handleMouseMove(e));
            howToPlayCanvas.setOnMouseClicked(e -> {
                ScoreboardAction action = this.howToPlayScreen.handleMouseClick(e);
                if (action == ScoreboardAction.GOTO_MENU) {
                    goToStartScreen(); // Quay về menu
                }
            });

            // Tạo 1 vòng lặp render riêng
            this.howToPlayLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    howToPlayScreen.render(htpGC);
                }
            };

            howToPlayPane.getChildren().add(howToPlayCanvas);
            this.howToPlayScene = new Scene(howToPlayPane); // <-- SỬA LỖI 2: Gán vào howToPlayScene
        }

        this.primaryStage.setScene(this.howToPlayScene); // <-- SỬA LỖI 3: Dùng howToPlayScene
        this.howToPlayScreen.reset();
        this.howToPlayLoop.start();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
