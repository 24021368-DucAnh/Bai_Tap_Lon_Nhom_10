package org.example.arkanoid.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.example.arkanoid.UIUX.*;
import org.example.arkanoid.objects.*;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameManager {
    private final double gameWidth;
    private final double gameHeight;
    private int HP = 3;

    private GameState currentState = GameState.PLAYING;
    private final GameNavigator navigator; // Navigator gọi về Main
    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;

    private GameUI gameUI;



    private Paddle paddle;
    private List<Ball> balls = new ArrayList<>();
    private List<Brick> bricks = new ArrayList<>();
    private PowerUpManager powerUpManager;

    private int currentStage = 1;
    private boolean isGameWon = false;
    private final int MAX_STAGES = 32;

    public GameManager(double gameWidth, double gameHeight, GameNavigator navigator) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.navigator = navigator;
    }

    public void init() {

        //---------------Tải tài nguyên----------------
        ResourceManager.loadAllResources();
        this.gameUI = new GameUI(gameWidth, gameHeight);

        //---------Paddle-------------
        final int PADDLE_WIDTH = 46; // Giảm kích thước paddle một chút cho dễ chơi
        final int PADDLE_HEIGHT = 20;
        final int PADDLE_OFFSET_FROM_BOTTOM = 100; // Cách lề dưới 1 khoảng y

        // Hình ảnh paddle có thể cần điều chỉnh lại cho phù hợp với W/H mới
        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0;
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        this.paddle = new Paddle(
                initialPaddleX,
                initialPaddleY,
                PADDLE_WIDTH, PADDLE_HEIGHT,
                ResourceManager.paddleImage,
                this);

        //---------Brick-------------
        BrickSkinRegistry.initDefaults();
        int stageToLoad = 1;
        System.out.println("Đang tải màn chơi: " + stageToLoad);
        this.bricks = StageLoader.loadFromIndex(stageToLoad, this.gameWidth);

        if (this.bricks.isEmpty()) {
            System.err.println("Không tải được gạch cho màn " + stageToLoad + ". Hãy kiểm tra file /resources/stages/Stage_1.txt");
        } else {
            System.out.println("Tải thành công " + this.bricks.size() + " viên gạch.");
        }

        //---------Ball-------------
        respawnBall();

        //------ PowerUp ---------
        this.powerUpManager = new PowerUpManager();

        //------ Pause Screen -----
        this.pauseScreen = new PauseScreen(gameWidth, gameHeight);

        //------ Game Over Screen -----
        this.gameOverScreen = new GameOverScreen(gameWidth, gameHeight);

        // Đặt trạng thái ban đầu
        this.currentState = GameState.PLAYING;

        this.isGameWon = false;
        loadStage(1);
    }

    private void loadStage(int stageIndex) {
        // Kiểm tra xem có vượt quá số màn tối đa không
        if (stageIndex > MAX_STAGES) {
            setGameWon(); // Nếu hết màn thì thắng
            return;
        }

        this.currentStage = stageIndex;
        System.out.println("Đang tải màn chơi: " + stageIndex);

        // Tải gạch cho màn mới
        this.bricks = StageLoader.loadFromIndex(stageIndex, this.gameWidth);

        if (this.bricks == null || this.bricks.isEmpty()) {
            // Nếu StageLoader trả về null hoặc rỗng (lỗi hoặc hết màn)
            System.err.println("Không thể tải màn " + stageIndex + ". Kiểm tra file hoặc MAX_STAGES.");
            // Bạn có thể chọn setGameWon() ở đây nếu muốn
            setGameWon(); // Coi như thắng nếu không tải được màn tiếp
        } else {
            System.out.println("Tải thành công " + this.bricks.size() + " viên gạch cho màn " + stageIndex);
            // Reset lại paddle và bóng
            resetPaddleAndBalls();
            // Cập nhật UI (nếu có)
            if (gameUI != null) {
                gameUI.updateStage(currentStage); // Giả sử GameUI có hàm này
            }
        }
    }

    private void resetPaddleAndBalls() {
        //---------Paddle-------------
        final int PADDLE_WIDTH = (int) ResourceManager.paddleImage.getWidth(); // Lấy từ ảnh
        final int PADDLE_HEIGHT = (int) ResourceManager.paddleImage.getHeight();
        final int PADDLE_OFFSET_FROM_BOTTOM = 100;

        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0;
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        // Nếu paddle chưa được tạo thì tạo mới, nếu có rồi thì chỉ đặt lại vị trí
        if (this.paddle == null) {
            this.paddle = new Paddle(
                    initialPaddleX, initialPaddleY,
                    PADDLE_WIDTH, PADDLE_HEIGHT,
                    ResourceManager.paddleImage, this // Truyền GameManager vào Paddle
            );
        } else {
            this.paddle.setX(initialPaddleX);
            this.paddle.setY(initialPaddleY);
            // TODO: Reset trạng thái power-up của paddle (ví dụ: kích thước) nếu cần
            // this.paddle.resetSize();
        }

        //---------Ball-------------
        // Xóa hết bóng cũ
        this.balls.clear();
        // Tạo một bóng mới ở giữa
        spawnNewBall();
    }

    private void spawnNewBall() {
        final int BALL_DIAMETER = 20;
        final double BALL_SPEED = 250; // Tốc độ ban đầu
        double initialBallX = gameWidth / 2.0;
        // Đặt bóng ngay trên paddle hoặc giữa màn hình
        double initialBallY = (paddle != null) ? paddle.getY() - BALL_DIAMETER : gameHeight / 2.0;

        Ball newBall = new Ball(initialBallX, initialBallY, BALL_DIAMETER, BALL_SPEED, gameWidth, gameHeight, this);
        this.balls.add(newBall);
    }

    public void setGameWon() {
        this.currentState = GameState.PLAYING; // Tạm thời để vẽ win, có thể tạo state WINNING
        this.isGameWon = true;
        this.balls.clear(); // Xóa bóng khi thắng
        System.out.println("Chúc mừng! Bạn đã phá đảo!");
        // TODO: Có thể hiển thị màn hình chiến thắng thay vì chỉ chữ
    }

    //setGameOver
    public boolean isGameOver() {
        return this.currentState == GameState.GAME_OVER;
    }
    public void setGameOver() {
        this.currentState = GameState.GAME_OVER;
        if (this.gameOverScreen != null) { // Thêm kiểm tra an toàn
            this.gameOverScreen.reset(); // Reset hover cho nút
        }
        System.out.println("GameOver !");
    }

    public void addBall() {
        // Tạo bóng mới ở ngay trên paddle
        double newBallX = paddle.getX() + (paddle.getWidth() / 2.0);
        double newBallY = paddle.getY() - 20; // Hơi cao hơn paddle


        Ball newBall = new Ball(newBallX, newBallY, 20, 250.0, gameWidth, gameHeight, this);
        this.balls.add(newBall);
    }


    private void respawnBall() {
        double initialBallX = gameWidth / 2.0;
        double initialBallY = gameHeight / 2.0;
        Ball ball = new Ball(initialBallX, initialBallY, 20, 250.0, gameWidth, gameHeight, this);
        this.balls.add(ball);
    }

    /**
     * Update theo dt
     * @param deltaTime
     */
    public void update(double deltaTime) {
        if (currentState != GameState.PLAYING || isGameWon) {
            return;
        }

        paddle.update(deltaTime);
        // Dùng Iterator để có thể xóa bóng khi nó rơi ra ngoài
        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            ball.update(deltaTime);

            // 1. Va chạm Bóng và Paddle
            if (ball.checkCollision(paddle)) {
                ball.bounceOff(paddle);
                SoundEffectManager.playHitSound();
            }

            // 2. Va chạm Bóng và Gạch
            Iterator<Brick> brickIterator = bricks.iterator();
            while (brickIterator.hasNext()) {
                Brick brick = brickIterator.next();
                boolean brickWasDestroyed = false; // Cờ để theo dõi gạch đã vỡ chưa

                // Lặp qua từng quả bóng
                for (Ball balL : balls) {
                    if (balL.checkCollision(brick)) {
                        balL.bounceOff(brick); // Bóng nảy ra
                        SoundEffectManager.playHitSound();

                        // GỌI HÀM TRỪ MÁU:
                        boolean isDestroyed = brick.onCollisionEnter();

                        if (isDestroyed) {
                            powerUpManager.trySpawnPowerUp(brick);
                            brickWasDestroyed = true; // Đánh dấu là gạch này đã vỡ
                            int points = 100; // Thêm điểm
                            if (this.gameUI != null) {
                                this.gameUI.addScore(points);
                            }
                        }

                        // Một quả bóng chỉ va chạm 1 gạch mỗi frame
                        break;
                    }
                }

                // Xóa gạch (remove) NẾU nó đã bị phá hủy
                if (brickWasDestroyed) {
                    brickIterator.remove();
                }
            }
            // 3. Xử lý bóng rơi ra ngoài
            if (ball.getY() - ball.getRadius() > gameHeight) {
                ballIterator.remove(); // Xóa bóng này khỏi danh sách
                System.out.println("Một quả bóng đã rơi ra ngoài.");
            }
        }
        /** Xu ly mat bong */
        if (balls.isEmpty()) {
            this.HP--; // Trừ mạng
            System.out.println("Mất 1 mạng! Còn lại: " + this.HP);
            SoundEffectManager.playDeathSound();

            if (this.HP <= 0) {
                setGameOver();
                System.out.println("GAME OVER");
            } else {
                respawnBall(); // Còn mạng, hồi sinh 1 bóng
            }
        }

        // Cập nhật tất cả các Power-up đang rơi
        powerUpManager.update(deltaTime, gameHeight, paddle);

        // Đồng bộ HP từ GameManager sang GameUI
        if (this.gameUI != null) {
            this.gameUI.setLives(this.HP);
        }

        boolean stageComplete = true;
        if (bricks.isEmpty()) { // Nhanh hơn nếu list rỗng
            stageComplete = true;
        } else {
            for (Brick b : bricks) {
                if (b.isDestructible()) { // Nếu còn gạch phá được
                    stageComplete = false;
                    break;
                }
            }
        }


        if (stageComplete && currentState == GameState.PLAYING && !isGameWon) {
            System.out.println("Hoàn thành màn " + currentStage + "!");
            // Dừng bóng và paddle tạm thời (tùy chọn)
            // balls.clear(); // Hoặc dừng vận tốc bóng
            // paddle.stopMoving();

            // Tải màn tiếp theo
            loadStage(currentStage + 1);
            // Dừng việc update của frame này lại để tránh lỗi
            return;
        }
    }


    public void render(GraphicsContext gc) {
        // Xóa toàn bộ màn hình trước khi vẽ lại
        gc.clearRect(0, 0, gameWidth, gameHeight);


        // Vẽ gameUI
        gameUI.render(gc);

        // Vẽ paddle
        paddle.render(gc);

        // Vẽ bóng
        for (Ball ball : balls) {
            ball.render(gc);
        }

        powerUpManager.render(gc);

        // Vẽ những viên gạch còn lại
        for (Brick brick : bricks) {
            BrickPainter.draw(gc, brick);
        }

        // Vẽ các lớp UI
        if (currentState == GameState.GAME_OVER) {
            int finalScore = gameUI.getScore(); // Lấy điểm thật
            gameOverScreen.render(gc, finalScore);
        } else if (currentState == GameState.PAUSED) {
            pauseScreen.render(gc);
        }
    }

    public void addHP() {
        this.HP++;
        System.out.println("Đã cộng 1 mạng! Mạng hiện tại: " + this.HP);
    }


    public int getHp() {
        return this.HP;
    }

    public void handleKeyEvent(KeyEvent event) {
        KeyCode code = event.getCode();

        if (currentState == GameState.GAME_OVER) {
            return;
        }

        //Xử lý Pause/Unpause
        if (event.getEventType() == KeyEvent.KEY_PRESSED && code == KeyCode.ESCAPE) {
            if (currentState == GameState.PLAYING) {
                currentState = GameState.PAUSED;
                pauseScreen.reset(); // Reset trạng thái hover của nút
            } else if (currentState == GameState.PAUSED) {
                currentState = GameState.PLAYING;
            }
            return;
        }

        if (currentState == GameState.PAUSED) {
            return;
        }


        // Nếu đang PLAYING, xử lý paddle
        if (currentState == GameState.PLAYING) {
            boolean isPressed = event.getEventType() == KeyEvent.KEY_PRESSED;
            if (code == KeyCode.A || code == KeyCode.LEFT) {
                paddle.setMovingLeft(isPressed);
            } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                paddle.setMovingRight(isPressed);
            }
        }
    }

    /**
     * Xử lý sự kiện click chuột.
     */
    public void handleMouseClick(MouseEvent event) {
        if (currentState == GameState.PAUSED) {
            PauseAction action = pauseScreen.handleMouseClick(event);

            switch (action) {
                case RESUME:
                    currentState = GameState.PLAYING;
                    break;
                case GOTO_MENU:
                    if (navigator != null) {
                        navigator.goToStartScreen();
                    }
                    break;
                case NONE:
                    break;
            }
        }
        else if (currentState == GameState.GAME_OVER) {
            if (gameOverScreen != null) {
                GameOverAction action = gameOverScreen.handleMouseClick(event);
                switch (action) {
                    case RETRY:
                        if (navigator != null) {
                            navigator.retryGame();
                        }
                        break;
                    case GOTO_MENU:
                        if (navigator != null) {
                            navigator.goToStartScreen();
                        }
                        break;
                }
            }
        }
    }

    /**
     * Xử lý sự kiện di chuyển chuột.
     */
    public void handleMouseMove(MouseEvent event) {
        if (currentState == GameState.PAUSED) {
            pauseScreen.handleMouseMove(event);
        } else if (currentState == GameState.GAME_OVER) {
            if (gameOverScreen != null) {
                gameOverScreen.handleMouseMove(event);
            }
        }
    }
}