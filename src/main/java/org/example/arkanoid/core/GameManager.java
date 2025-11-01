package org.example.arkanoid.core;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.example.arkanoid.UIUX.*;
import org.example.arkanoid.objects.*;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class GameManager {
    private final double gameWidth;
    private final double gameHeight;
    private int HP = 3;
    private long score = 0;

    private GameState currentState = GameState.PLAYING;
    private final GameNavigator navigator; // Navigator gọi về Main

    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;
    private StageClearScreen stageClearScreen;
    private QuitScreen quitScreen;
    private ScoreboardScreen scoreboardScreen;

    private double stageTransitionTimer = 0;
    private final double STAGE_TRANSITION_DURATION = 3.0;

    private Paddle paddle;
    private List<Ball> balls = new ArrayList<>();
    private List<Brick> bricks = new ArrayList<>();
    private List<Laser> activeLasers = new ArrayList<>();
    private static final double LASER_COOLDOWN = 0.25; // 0.25 giây delay mỗi phát bắn
    private double laserCooldownTimer = 0.0; // Bộ đếm thời gian hồi chiêu
    private PowerUpManager powerUpManager;

    private List<Meteor> activeMeteors = new ArrayList<>();
    private final Random random = new Random();
    private static final int METEOR_SPAWN_CHANCE = 15;
    private static final double METEOR_SPEED = 200.0;

    private double shakeTimer = 0.0;
    private double shakeIntensity = 0.0;
    private double cameraOffsetX = 0.0;
    private double cameraOffsetY = 0.0;

    private int currentStage = 1;
    private boolean isGameWon = false;
    private final int MAX_STAGES = 32;

    public GameManager(double gameWidth, double gameHeight, GameNavigator navigator) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.navigator = navigator;
    }

    public void init() {

        //---------Paddle-------------
        final int PADDLE_WIDTH = (int) ResourceManager.paddleImage.getWidth();
        final int PADDLE_HEIGHT = (int) ResourceManager.paddleImage.getHeight();
        final int PADDLE_OFFSET_FROM_BOTTOM = 100; // Cách lề dưới 1 khoảng y

        // Hình ảnh paddle có thể cần điều chỉnh lại cho phù hợp với W/H mới
        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0;
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        this.paddle = new Paddle(
                initialPaddleX,
                initialPaddleY,
                PADDLE_WIDTH, PADDLE_HEIGHT,
                ResourceManager.paddleImage,
                this
                );

        //---------Brick-------------
        BrickSkinRegistry.initDefaults();

        //------ PowerUp ---------
        this.powerUpManager = new PowerUpManager(this);
        this.activeMeteors.clear();

        //------ Pause Screen -----
        this.pauseScreen = new PauseScreen(gameWidth, gameHeight);

        //------ Game Over Screen -----
        this.gameOverScreen = new GameOverScreen(gameWidth, gameHeight);

        //------ Stage Clear Screen -----
        this.stageClearScreen = new StageClearScreen(gameWidth, gameHeight);

        //------ Quit Screen -----
        this.quitScreen = new QuitScreen(gameWidth, gameHeight);

        //------ Scoreboard Screen -----
        this.scoreboardScreen = new ScoreboardScreen(gameWidth, gameHeight);

        // Đặt trạng thái ban đầu
        this.currentState = GameState.PLAYING;

        this.isGameWon = false;
        loadStage(1);
    }

    /**
     * Update theo dt
     * @param deltaTime
     */
    public void update(double deltaTime) {
        if (shakeTimer > 0) {
            shakeTimer -= deltaTime;
            if (shakeTimer <= 0) {
                // Hết giờ, trả camera về 0
                shakeTimer = 0;
                cameraOffsetX = 0;
                cameraOffsetY = 0;
            } else {
                // Vẫn đang rung, tạo vị trí ngẫu nhiên
                cameraOffsetX = (random.nextDouble() - 0.5) * 2 * shakeIntensity;
                cameraOffsetY = (random.nextDouble() - 0.5) * 2 * shakeIntensity;
            }
        }

        if (currentState == GameState.PAUSED
                || isGameWon
                || currentState == GameState.GAME_OVER
                || currentState == GameState.QUIT_MENU
                || currentState == GameState.SCOREBOARD) {
            return;
        }

        if (currentState == GameState.STAGE_TRANSITION) {
            stageTransitionTimer -= deltaTime; // Đếm ngược
            stageClearScreen.update(deltaTime);

            if (stageTransitionTimer <= 0) {
                loadStage(currentStage + 1);

                if (!isGameWon) {
                    currentState = GameState.PLAYING; // Bắt đầu chơi màn mới
                }
            }
            return;
        }

        paddle.update(deltaTime);
        // Dùng Iterator để có thể xóa bóng khi nó rơi ra ngoài
        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            ball.update(deltaTime);

            // 1. Va chạm Bóng và Paddle
            if (!ball.isSticky && ball.checkCollision(paddle)) {
                ball.bounceOff(paddle);
                SoundEffectManager.playHitSound();
            } else if (ball.isSticky) {
                //Bóng đang dính nên không tính đây là va chạm
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
                            trySpawnMeteor(brick);
                            brickWasDestroyed = true; // Đánh dấu là gạch này đã vỡ
                            long points = 100; // Thêm điểm
                            this.score += points;
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
        if (balls.isEmpty() && !isGameWon) {
            this.HP--; // Trừ mạng

            triggerScreenShake(10.0, 0.3);

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

        Iterator<Meteor> meteorIter = activeMeteors.iterator();
        while (meteorIter.hasNext()) {
            Meteor meteor = meteorIter.next();
            meteor.update(deltaTime);

            // 1. Kiểm tra va chạm với Paddle
            if (meteor.checkCollision(paddle)) {
                this.HP--; // Trừ mạng
                System.out.println("Bị thiên thạch đâm! Còn lại: " + this.HP);
                SoundEffectManager.playDeathSound(); // Tạm dùng âm thanh mất mạng

                meteorIter.remove(); // Xóa thiên thạch

                // Kiểm tra game over ngay lập tức
                if (this.HP <= 0) {
                    setGameOver();
                    System.out.println("GAME OVER (do thiên thạch)");
                }
            }
            // 2. Kiểm tra nếu rơi ra khỏi màn hình
            else if (meteor.isOffScreen(gameHeight)) {
                meteorIter.remove();
            }
        }

        Iterator<Laser> laserIterator = activeLasers.iterator();
        while (laserIterator.hasNext()) {
            Laser laser = laserIterator.next();
            laser.update(deltaTime); // Di chuyển laser

            boolean laserHit = false;
            Iterator<Brick> brickIterator = bricks.iterator();
            while (brickIterator.hasNext()) {
                Brick brick = brickIterator.next();

                if (laser.checkCollision(brick)) {
                    if (brick.onCollisionEnter()) { // <-- THAY ĐỔI QUAN TRỌNG
                        // Gạch đã bị phá hủy
                        powerUpManager.trySpawnPowerUp(brick);
                        this.score += 100;
                        brickIterator.remove();
                        SoundEffectManager.playHitSound();
                    } else {

                        SoundEffectManager.playHitSound();
                    }

                    laserHit = true; // Đánh dấu laser đã va chạm (bắn trúng)
                    break; // Một laser chỉ va chạm 1 lần
                }
            }

            // 2. Xóa laser nếu nó trúng gạch HOẶC bay ra khỏi màn hình
            if (laserHit || laser.isOffScreenTop()) {
                laserIterator.remove();
            }
        }

        // Đếm ngược thời gian hồi của laser
        if (laserCooldownTimer > 0) {
            laserCooldownTimer -= deltaTime;
        }


        boolean stageComplete = true;
        if (bricks.isEmpty()) {
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

            currentState = GameState.STAGE_TRANSITION;
            stageTransitionTimer = STAGE_TRANSITION_DURATION; // Đặt hẹn giờ
            stageClearScreen.setStageCompleted(currentStage);
            stageClearScreen.reset(); // Reset hiệu ứng "Loading..."

            balls.clear();

            SoundEffectManager.playLevelSwitchSound();

            return;
        }
    }


    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, gameWidth, gameHeight);

        gc.save(); // Lưu trạng thái canvas (trước khi dịch chuyển)
        gc.translate(cameraOffsetX, cameraOffsetY);

        // Vẽ paddle
        paddle.render(gc);

        // Vẽ bóng
        for (Ball ball : balls) {
            ball.render(gc);
        }

        powerUpManager.render(gc);

        for (Meteor meteor : activeMeteors) {
            meteor.render(gc);
        }

        // Vẽ những viên gạch còn lại
        for (Brick brick : bricks) {
            BrickPainter.draw(gc, brick);
        }

        gc.restore();

        for (Laser laser : activeLasers) {
            laser.render(gc);
        }
        // Vẽ các lớp UI
        switch (currentState) {
            case GAME_OVER:
                gameOverScreen.render(gc);
                break;
            case PAUSED:
                pauseScreen.render(gc);
                break;
            case STAGE_TRANSITION:
                stageClearScreen.render(gc);
                break;
            case QUIT_MENU:
                quitScreen.render(gc);
                break;
            case SCOREBOARD:
                scoreboardScreen.render(gc);
        }
    }

    private void triggerScreenShake(double intensity, double duration) {
        this.shakeIntensity = intensity;
        this.shakeTimer = duration;
    }

    public void addHP() {
        this.HP++;
        System.out.println("Đã cộng 1 mạng! Mạng hiện tại: " + this.HP);
    }


    public int getHp() {
        return this.HP;
    }

    public long getScore() {
        return this.score;
    }

    public int getStage() {
        return this.currentStage;
    }

    public boolean isGameWon() {
        return this.isGameWon;
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
        }
    }

    private void resetPaddleAndBalls() {
        //---------Paddle-------------
        final int PADDLE_WIDTH = (int) ResourceManager.paddleImage.getWidth();
        final int PADDLE_HEIGHT = (int) ResourceManager.paddleImage.getHeight();
        final int PADDLE_OFFSET_FROM_BOTTOM = 100;

        double initialPaddleX = (gameWidth - PADDLE_WIDTH) / 2.0;
        double initialPaddleY = gameHeight - PADDLE_HEIGHT - PADDLE_OFFSET_FROM_BOTTOM;

        // Nếu paddle chưa được tạo thì tạo mới, nếu có rồi thì chỉ đặt lại vị trí
        if (this.paddle == null) {
            this.paddle = new Paddle(
                    initialPaddleX, initialPaddleY,
                    PADDLE_WIDTH, PADDLE_HEIGHT,
                    ResourceManager.paddleImage, this
            );
        } else {
            this.paddle.setX(initialPaddleX);
            this.paddle.setY(initialPaddleY);

        }


        // Xóa hết bóng cũ
        this.balls.clear();
        this.activeMeteors.clear();
        this.activeLasers.clear();
        this.powerUpManager.clearPowerUps();
        this.paddle.resetPaddle();

        // Tạo một bóng mới ở giữa
        spawnNewBall();
    }

    private void spawnNewBall() {
        final int BALL_DIAMETER = 20;
        final double BALL_SPEED = 250; // Tốc độ ban đầu
        double initialBallX = paddle.getX() + (paddle.getWidth() / 2.0);
        // Đặt bóng ngay trên paddle
        double initialBallY = paddle.getY() - 20;

        Ball newBall = new Ball(initialBallX, initialBallY, BALL_DIAMETER, BALL_SPEED, gameWidth, gameHeight, this, true);
        newBall.setPaddle(paddle);
        this.balls.add(newBall);
    }

    public void setGameWon() {
        this.isGameWon = true;
        this.balls.clear();
        System.out.println("Chúc mừng! Bạn đã phá đảo!");

        // Chuyển sang màn hình Scoreboard
        scoreboardScreen.setNewScore(this.score); // Báo điểm
        scoreboardScreen.reset();
        currentState = GameState.SCOREBOARD;
    }

    //setGameOver
    public boolean isGameOver() {
        return this.currentState == GameState.GAME_OVER;
    }

    public void setGameOver() {
        this.currentState = GameState.GAME_OVER;
        if (this.gameOverScreen != null) { // Thêm kiểm tra an toàn
            this.gameOverScreen.setScore(this.score);
            this.gameOverScreen.reset(); // Reset hover cho nút
        }
        System.out.println("GameOver !");
    }

    public void addBall() {
        // Vị trí spawn bóng (ngay trên giữa paddle)
        double spawnX = paddle.getX() + (paddle.getWidth() / 2.0);
        double spawnY = paddle.getY() - 20; // Hơi cao hơn paddle

        final double TOTAL_SPEED = 350.0; // Tốc độ gốc

        // Tính toán vận tốc cho các bóng bay chéo
        // Dùng định lý Pytago: vx^2 + vy^2 = TOTAL_SPEED^2
        // Chúng ta chọn vy cố định và tìm vx
        final double vY_Diagonal = -300.0; // Vận tốc bay lên của bóng chéo
        // Tính vX: vX = sqrt(TOTAL_SPEED^2 - vY_Diagonal^2)
        final double vX_Diagonal = Math.sqrt(TOTAL_SPEED * TOTAL_SPEED - vY_Diagonal * vY_Diagonal); // ≈ 180.3

        // Danh sách vận tốc cho 3 bóng
        Point2D[] velocities = new Point2D[] {
                new Point2D(-vX_Diagonal, vY_Diagonal), // Bóng 1: Bay chéo sang trái (Tốc độ ≈ 350)
                new Point2D(0, -TOTAL_SPEED),           // Bóng 2: Bay thẳng lên (Tốc độ = 350)
                new Point2D(vX_Diagonal, vY_Diagonal)   // Bóng 3: Bay chéo sang phải (Tốc độ ≈ 350)
        };

        // Tạo 3 quả bóng
        for (int i = 0; i < 3; i++) {
            Ball newBall = new Ball(
                    spawnX,
                    spawnY,
                    20, // BALL_DIAMETER
                    TOTAL_SPEED, // (Giá trị này không còn quan trọng vì ta setVelocity ngay sau)
                    gameWidth,
                    gameHeight,
                    this,
                    false
            );

            // Đặt vận tốc đã tính
            newBall.setVelocity(velocities[i]);

            // Thêm bóng vào danh sách
            this.balls.add(newBall);
        }
    }


    private void respawnBall() {
        // Tái sử dụng logic tạo bóng mới dính trên paddle
        spawnNewBall();
    }

    private void trySpawnMeteor(Brick destroyedBrick) {
        // Tỉ lệ rơi là METEOR_SPAWN_CHANCE (ví dụ: 15%)
        if (random.nextInt(100) < METEOR_SPAWN_CHANCE) {
            // Tạo ở trung tâm viên gạch
            double spawnCenterX = destroyedBrick.getX() + (destroyedBrick.getWidth() / 2.0);
            double spawnCenterY = destroyedBrick.getY() + (destroyedBrick.getHeight() / 2.0);

            Meteor meteor = new Meteor(spawnCenterX, spawnCenterY, METEOR_SPEED);
            activeMeteors.add(meteor);
        }
    }

    public void fireLaser() {

        if (!paddle.hasLaser() || laserCooldownTimer > 0) {
            return; // Chưa thể bắn, thoát ra
        }

        // **ĐẶT LẠI COOLDOWN**
        laserCooldownTimer = LASER_COOLDOWN;

        SoundEffectManager.playFireLaserSound();

        double spawnY = paddle.getY() - 10; // Hơi cao hơn paddle 1 chút
        double spawnX1 = paddle.getX() + (paddle.getWidth() * 0.2);
        double spawnX2 = paddle.getX() + (paddle.getWidth() * 0.8);

        activeLasers.add(new Laser(spawnX1, spawnY));
        activeLasers.add(new Laser(spawnX2, spawnY));
    }

    public void handleKeyEvent(KeyEvent event) {
        KeyCode code = event.getCode();

        switch (currentState) {
            case PLAYING:
                // Xử lý Pause
                if (event.getEventType() == KeyEvent.KEY_PRESSED && code == KeyCode.ESCAPE) {
                    currentState = GameState.PAUSED;
                    pauseScreen.reset();
                    return; // Thoát khỏi hàm ngay lập tức
                }

                // Xử lý di chuyển
                boolean isPressed = event.getEventType() == KeyEvent.KEY_PRESSED;
                if (code == KeyCode.A || code == KeyCode.LEFT) {
                    paddle.setMovingLeft(isPressed);
                } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                    paddle.setMovingRight(isPressed);
                }

                // Xử lý phím Space (bắn bóng)
                if (event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.SPACE) {
                    for (Ball ball : balls) {
                        if (ball.isSticky) {
                            ball.releaseFromPaddle(250);
                        }
                    }
                }
                break;

            case PAUSED:
                PauseAction pAction = pauseScreen.handleKeyInput(event);
                if (pAction == PauseAction.RESUME) {
                    currentState = GameState.PLAYING;
                }
                break;

            case GAME_OVER:
            case STAGE_TRANSITION:
            case QUIT_MENU:
            case SCOREBOARD:
            default:
                break;
        }
    }

    /**
     * Xử lý sự kiện click chuột.
     */
    public void handleMouseClick(MouseEvent event) {
        switch (currentState) {
            case PAUSED:
                PauseAction pAction = pauseScreen.handleMouseClick(event);
                switch (pAction) {
                    case RESUME:
                        currentState = GameState.PLAYING;
                        break;
                    case GOTO_MENU:
                        quitScreen.setScore(this.score);
                        quitScreen.reset();
                        currentState = GameState.QUIT_MENU;
                        quitScreen.reset();
                        break;
                }
                break;

            case GAME_OVER:
                GameOverAction goAction = gameOverScreen.handleMouseClick(event);
                switch (goAction) {
                    case RETRY:
                        if (navigator != null) navigator.retryGame();
                        break;
                    case GOTO_MENU:
                        quitScreen.setScore(this.score);
                        quitScreen.reset();
                        currentState = GameState.QUIT_MENU;
                        break;
                }
                break;

            case QUIT_MENU:
                QuitAction qAction = quitScreen.handleMouseClick(event);
                switch (qAction) {
                    case YES:
                        scoreboardScreen.setNewScore(this.score);
                        scoreboardScreen.reset();
                        currentState = GameState.SCOREBOARD;
                        break;
                    case NO:
                        if (navigator != null) navigator.goToStartScreen();
                        break;
                }
                break;

            case SCOREBOARD:
                ScoreboardAction sAction = scoreboardScreen.handleMouseClick(event);
                if (sAction == ScoreboardAction.GOTO_MENU) {
                    if (navigator != null) navigator.goToStartScreen();
                }
                break;

            case PLAYING:
                fireLaser();
                break;
            case STAGE_TRANSITION:
            default:
                break;
        }
    }

    /**
     * Xử lý sự kiện di chuyển chuột.
     */
    public void handleMouseMove(MouseEvent event) {
        switch (currentState) {
            case PAUSED:
                pauseScreen.handleMouseMove(event);
                break;
            case GAME_OVER:
                gameOverScreen.handleMouseMove(event);
                break;
            case QUIT_MENU:
                quitScreen.handleMouseMove(event);
                break;
            case SCOREBOARD:
                scoreboardScreen.handleMouseMove(event);
                break;

            case PLAYING:
            case STAGE_TRANSITION:
            default:
                break;
        }
    }

    public double getGameWidth() {
        return this.gameWidth;
    }
}