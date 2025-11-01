package org.example.arkanoid.objects;

//Sử dụng Point2D để cải tiến sử dụng vector cho vận tốc bóng

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.arkanoid.core.GameManager;

public class Ball extends MovableObject {
    //Loại bỏ speed,directionX,directionY, đưa về dạng vector velocity chứa thông tin về hướng và tốc độ
    private Point2D velocity;
    private double radius;

    // Thêm 2 biến để biết kích thước màn hình
    private final double gameWidth;
    private final double gameHeight;
    private final GameManager gameManager;
    public boolean isSticky = true; // ĐẦU GAME BÓNG DÍNH VỚI PADDLE
    private Paddle paddle; //Mục đích : Tham chiếu để đồng bộ vị trí bóng và paddle

    public Ball(double x, double y, int diameter, double initialSpeed, double gameWidth, double gameHeight, GameManager gameManager, boolean isSticky) {
        super(x, y, diameter, diameter, 0, 0);
        this.radius = diameter / 2.0;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.gameManager = gameManager;
        //Ban đầu bóng chưa bay, chờ ấn space
        this.isSticky = isSticky; // Gán trạng thái dính từ tham số

        if (isSticky) {
            // Nếu là bóng dính, cho vận tốc bằng 0 (chờ ấn SPACE)
            this.velocity = new Point2D(0, 0);
        } else {
            // Nếu không dính (bóng power-up), cho bay lên ngay
            // Bạn có thể chỉnh hướng bay ngẫu nhiên, nhưng bay thẳng lên là đơn giản nhất
            this.velocity = new Point2D(0, -initialSpeed);
        }
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    // Hàm thả bóng ra lúc start game
    public void releaseFromPaddle(double initialSpeed) {
        if (isSticky) {
            isSticky = false;
            //Bóng được phóng lên trên
            double horizontalSpeed = (Math.random() > 0.5 ? 1 : -1) * 60.0;
            double verticalSpeed = -initialSpeed;
            this.velocity = new Point2D(horizontalSpeed, verticalSpeed);
        }
    }

    /**
     * Kiểm tra va chạm hình chữ nhật giữa bóng và đối tượng khác.
     */
    public boolean checkCollision(GameObject other) {
        // Tọa độ các cạnh của bóng
        double ballLeft = x - radius;
        double ballRight = x + radius;
        double ballTop = y - radius;
        double ballBottom = y + radius;

        // Tọa độ các cạnh của đối tượng kia, lấy lần lượt 4 góc của hình chữ nhật
        double otherLeft = other.getX();
        double otherRight = other.getX() + other.getWidth();
        double otherTop = other.getY();
        double otherBottom = other.getY() + other.getHeight();

        // Kiểm tra xem có chồng lấn không
        return ballRight > otherLeft && ballLeft < otherRight && ballBottom > otherTop && ballTop < otherBottom;
    }

    /**
     * Logic nảy lại được cải tiến để xác định hướng nảy chính xác hơn, thao tác trên velocity
     */
    public void bounceOff(GameObject other) {
        //gốc toạ độ nằm ở góc trên bên trái của sổ, x tăng dần theo chiều ngang và y tăng dần theo chiều dọc
        double ballBottom = y + radius;
        double ballTop = y - radius;
        double ballLeft = x - radius;
        double ballRight = x + radius;

        double otherTop = other.getY();
        double otherBottom = other.getY() + other.getHeight();
        double otherLeft = other.getX();
        double otherRight = other.getX() + other.getWidth();

        // Tính toán độ chồng lấn (overlap) giữa bóng và vật thể bằng cách tính chiều cao, bề rộng vùng giao nhau, theo thuật toán va chạm AABB
        double overlapX = Math.min(ballRight, otherRight) - Math.max(ballLeft, otherLeft);
        double overlapY = Math.min(ballBottom, otherBottom) - Math.max(ballTop, otherTop);

        // Nếu độ chồng lấn theo chiều ngang lớn hơn chiều dọc, va chạm xảy ra ở mặt trên
        if (overlapX > overlapY) {
            this.velocity = new Point2D(this.velocity.getX(), -this.velocity.getY());
            // Đẩy nhẹ bóng ra để tránh bị kẹt trong vật thể
            if (y < other.getY()) {
                y = other.getY() - radius;
            } else { // Đập vào mặt dưới của vật thể
                y = other.getY() + other.getHeight() + radius;
            }
        } else { // Ngược lại, va chạm xảy ra ở mặt trái/phải
            this.velocity = new Point2D(-this.velocity.getX(), this.velocity.getY());
            // Đẩy nhẹ bóng ra để tránh bị kẹt
            if (x < other.getX()) { // Đập vào mặt trái
                x = other.getX() - radius;
            } else { // Đập vào mặt phải
                x = other.getX() + other.getWidth() + radius;
            }
        }
    }


    /**
     * Hàm move() không còn cần thiết, logic được gộp vào update()
     */
    @Override
    public void move() {
    }

    public void scaleSpeed(double factor) {
        // chỉ đổi độ lớn vector, giữ hướng
        double vx = velocity.getX(), vy = velocity.getY();
        double speed = Math.hypot(vx, vy);
        if (speed == 0) return;
        double newSpeed = Math.max(50, speed * factor);  // tránh =0
        double nx = vx * (newSpeed / speed);
        double ny = vy * (newSpeed / speed);
        this.velocity = new Point2D(nx, ny);
    }

    public double currentSpeed() {
        return Math.hypot(velocity.getX(), velocity.getY());
    }

    @Override
    public void update(double deltaTime) {
        //Vị trí ban đầu : Ngay bên trên paddle
        if (isSticky && paddle != null) {
            this.x = paddle.getX() + paddle.getWidth() / 2.0;
            this.y = paddle.getY() - radius - 1;
        } else {
            // Cập nhật vị trí dựa trên tốc độ, hướng và deltaTime
            this.x += this.velocity.getX() * deltaTime;
            this.y += this.velocity.getY() * deltaTime;

            // Kiểm tra va chạm với biên màn hình
            // Va chạm biên trái hoặc phải
            if ((x - radius < 0 && velocity.getX() < 0) || (x + radius > gameWidth && velocity.getX() > 0)) {
                this.velocity = new Point2D(-this.velocity.getX(), this.velocity.getY());
            }

            // Va chạm biên trên
            if (y - radius < 0 && velocity.getY() < 0) {
                this.velocity = new Point2D(this.velocity.getX(), -this.velocity.getY());
            }
        }
    }

    /**
     * Xử lý va chạm bóng với paddle.
     * Bóng nảy góc theo vị trí chạm, cộng vận tốc paddle ngang.
     */
    public void bounceOff(Paddle paddle) {
        if (isSticky) return; // Nếu bóng đang dính paddle thì không đổi vận tốc

        double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
        double ballDist = this.x - paddleCenter;
        double maxDist = paddle.getWidth() / 2.0;

        double normalized = Math.max(-1, Math.min(1, ballDist / maxDist));
        double maxAngle = Math.toRadians(60);
        double angle = normalized * maxAngle;

        double speed = Math.hypot(velocity.getX(), velocity.getY());

        double newVx = speed * Math.sin(angle);
        double newVy = -speed * Math.cos(angle);

        // Lấy vận tốc ngang paddle cộng vào ball (chỉ khi không dính)
        newVx += 0.35 * paddle.getVelocityX();

        double newSpeed = Math.hypot(newVx, newVy);
        if (newSpeed != 0) {
            newVx *= (speed / newSpeed);
            newVy *= (speed / newSpeed);
        }

        this.velocity = new Point2D(newVx, newVy);

        // Đẩy bóng lên khỏi paddle tránh dính khung hình tiếp
        this.y = paddle.getY() - this.radius - 1;
    }


    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    // --- Getter và Setter ---
    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public double getRadius() {
        return radius;
    }
}
