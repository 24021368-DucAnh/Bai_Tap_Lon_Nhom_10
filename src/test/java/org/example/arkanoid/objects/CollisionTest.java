// File: src/test/java/org/example/arkanoid/objects/CollisionTest.java
package org.example.arkanoid.objects;

// Import các lớp JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Import GameManager để constructor của Ball không báo lỗi
import org.example.arkanoid.core.GameManager;

import static org.junit.jupiter.api.Assertions.*; // Import các hàm kiểm tra (assert)

/**
 * Lớp này dùng để test logic va chạm (AABB) trong GameObject.
 * Chúng ta dùng Ball làm đối tượng test vì nó kế thừa hàm checkCollision.
 */
class CollisionTest {

    // Tạo 2 vật thể để test.
    private Ball mainObject;
    private Ball otherObject;
    private final double GAME_WIDTH = 800;
    private final double GAME_HEIGHT = 600;

    // Hàm này chạy trước mỗi @Test
    @BeforeEach
    void setUp() {
        // Tạo 1 vật thể chính tại (100, 100) kích thước 10x10
        // (x, y, đường kính, tốc độ, rộng game, cao game, gameManager)
        mainObject = new Ball(100, 100, 10, 0, GAME_WIDTH, GAME_HEIGHT, null); // <-- TRUYỀN NULL

        // Tạo 1 vật thể phụ, kích thước 20x20
        otherObject = new Ball(0, 0, 20, 0, GAME_WIDTH, GAME_HEIGHT, null); // <-- TRUYỀN NULL
    }

    @Test
    @DisplayName("Test va chạm khi 2 vật thể chồng lên nhau")
    void testCollision_WhenOverlapping() {
        // Đặt 'otherObject' (20x20) tại (105, 105)
        // 'mainObject' (10x10) đang ở (100, 100)
        // -> Chúng chắc chắn chồng lên nhau
        otherObject.x = 105;
        otherObject.y = 105;

        // Mong đợi hàm checkCollision trả về true
        assertTrue(mainObject.checkCollision(otherObject), "Phải có va chạm khi chồng lên nhau");
        assertTrue(otherObject.checkCollision(mainObject), "Va chạm phải đúng theo cả 2 chiều");
    }

    @Test
    @DisplayName("Test KHÔNG va chạm khi 2 vật thể ở xa nhau")
    void testCollision_WhenNotOverlapping() {
        // Đặt 'otherObject' (20x20) tại (500, 500)
        // 'mainObject' (10x10) đang ở (100, 100)
        otherObject.x = 500;
        otherObject.y = 500;

        // Mong đợi hàm checkCollision trả về false
        assertFalse(mainObject.checkCollision(otherObject), "Không thể va chạm khi ở xa");
    }

    @Test
    @DisplayName("Test KHÔNG va chạm khi 2 vật thể chạm cạnh (cạnh trái)")
    void testCollision_WhenTouchingEdge_Left() {
        // 'mainObject' (10x10) ở (100, 100). Cạnh phải ở x = 110.
        // Đặt 'otherObject' (20x20) ở (110, 100). Cạnh trái ở x = 110.
        // -> Chúng chỉ chạm cạnh, không giao nhau (vì AABB dùng < và >)
        otherObject.x = 110;
        otherObject.y = 100;

        assertFalse(mainObject.checkCollision(otherObject), "Chạm cạnh không được tính là va chạm");
    }

    @Test
    @DisplayName("Test KHÔNG va chạm khi 2 vật thể chạm cạnh (cạnh trên)")
    void testCollision_WhenTouchingEdge_Top() {
        // 'mainObject' (10x10) ở (100, 100). Cạnh dưới ở y = 110.
        // Đặt 'otherObject' (20x20) ở (100, 110). Cạnh trên ở y = 110.
        otherObject.x = 100;
        otherObject.y = 110;

        assertFalse(mainObject.checkCollision(otherObject), "Chạm cạnh không được tính là va chạm");
    }

    @Test
    @DisplayName("Test va chạm khi 1 vật thể nằm lọt bên trong")
    void testCollision_WhenInside() {
        // 'mainObject' (10x10) ở (100, 100).
        // Đặt 'otherObject' (lớn hơn, 100x100) ở (90, 90)
        // -> 'mainObject' sẽ nằm lọt bên trong 'otherObject'
        otherObject.width = 100;
        otherObject.height = 100;
        otherObject.x = 90;
        otherObject.y = 90;

        assertTrue(mainObject.checkCollision(otherObject), "Phải va chạm khi nằm lọt bên trong");
        assertTrue(otherObject.checkCollision(mainObject), "Va chạm phải đúng theo cả 2 chiều");
    }
}