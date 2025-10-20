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
    private int HP = 3;

    // Thêm 2 biến để biết kích thước màn hình
    private final double gameWidth;
    private final double gameHeight;
    private final GameManager gameManager;

    public Ball(double x, double y, int diameter, double initialSpeed, double gameWidth, double gameHeight, GameManager gameManager) {
        super(x, y, diameter, diameter, 0, 0);
        this.radius = diameter / 2.0;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        //Khởi tạo vector vận tốc
        this.velocity = new Point2D(initialSpeed, -initialSpeed);
        this.gameManager = gameManager;
        this.HP = 3;
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

    @Override
    public void update(double deltaTime) {
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

        //Bóng chạm đáy thì GameOver
        if (y + radius > gameHeight) {
            this.minusHP();
            if(this.getHP()==0) {
                gameManager.setGameOver();
                this.velocity = new Point2D(0, 0);
            }
            else {
                this.velocity = new Point2D(this.velocity.getX(), -this.velocity.getY());
            }
        }
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

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }
    public void minusHP() {
        this.HP --;
    }
}
