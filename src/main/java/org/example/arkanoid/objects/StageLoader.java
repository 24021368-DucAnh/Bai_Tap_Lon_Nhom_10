package org.example.arkanoid.objects;

import org.example.arkanoid.core.GameManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Đọc file layout màn chơi từ thư mục resources.
 * Lớp này mô phỏng logic của code C++ mẫu: sử dụng một lưới 13 cột cố định,
 * kích thước gạch cố định, và tự động căn giữa toàn bộ bố cục.
 */
public final class StageLoader {

    // --- CẤU HÌNH CỐ ĐỊNH (KHÔNG THAY ĐỔI) ---
    private static final String FOLDER = "/stages/";
    private static final String PREFIX = "Stage_";
    private static final String EXT    = ".txt";

    // --- CÁC THAM SỐ CHUẨN CỦA GAME (BÁM THEO C++ MẪU) ---
    // Game luôn được thiết kế trên một lưới có 13 cột.
    private static final int GRID_COLUMNS = 13;
    // Kích thước cố định cho mỗi viên gạch như bạn yêu cầu.
    private static final double BRICK_WIDTH = 44;
    private static final double BRICK_HEIGHT = 22;

    /**
     * Constructor private để ngăn việc tạo đối tượng từ lớp tiện ích này.
     */
    private StageLoader() {}

    /**
     * Tải một màn chơi từ file .txt dựa trên chỉ số (index).
     * Đây là phương thức chính mà GameManager sẽ gọi.
     * @param index Số thứ tự của màn chơi (ví dụ: 1 cho Stage_1.txt).
     * @param canvasW Chiều rộng của khu vực game (ví dụ: 800).
     * @return Một danh sách các đối tượng Brick đã được tạo và đặt đúng vị trí.
     */
    public static List<Brick> loadFromIndex(int index, double canvasW, GameManager gameManager) {
        String stageFileName = PREFIX + index + EXT;
        List<Brick> bricks = new ArrayList<>();

        // Sử dụng try-with-resources để đảm bảo file được đóng đúng cách
        try (InputStream is = StageLoader.class.getResourceAsStream(FOLDER + stageFileName)) {
            // 1. Kiểm tra xem file có tồn tại không
            if (is == null) {
                System.err.println("Lỗi nghiêm trọng: Không tìm thấy file stage: " + FOLDER + stageFileName);
                return bricks; // Trả về danh sách rỗng
            }

            // 2. Đọc tất cả các dòng từ file
            List<String> lines = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.toList());

            // 3. Tính toán vị trí để căn giữa toàn bộ khối gạch
            double totalGridLayoutWidth = GRID_COLUMNS * BRICK_WIDTH;
            double horizontalMargin = (canvasW - totalGridLayoutWidth) / 2.0;
            double topMargin = 60; // Khoảng cách từ đỉnh màn hình

            // 4. Lặp qua từng dòng (hàng) và từng ký tự (cột) để tạo gạch
            for (int r = 0; r < lines.size(); r++) {
                String line = lines.get(r);
                for (int c = 0; c < line.length(); c++) {
                    // Nếu cột hiện tại vượt quá lưới 13 cột, bỏ qua
                    if (c >= GRID_COLUMNS) {
                        break;
                    }

                    char brickCode = line.charAt(c);

                    // Nếu ký tự là '*' hoặc ' ', đó là một ô trống, bỏ qua
                    if (brickCode == '*' || brickCode == ' ') {
                        continue;
                    }

                    // Tính toán vị trí chính xác của viên gạch trên màn hình
                    double x = horizontalMargin + (c * BRICK_WIDTH);
                    double y = topMargin + (r * BRICK_HEIGHT);

                    // Dùng "nhà máy" BrickFactory để tạo ra đúng loại gạch
                    Brick brick = BrickFactory.fromCode(brickCode, x, y, (int) BRICK_WIDTH, (int) BRICK_HEIGHT, index, gameManager);

                    // Gán mã skin để BrickPainter biết vẽ ảnh nào (dù không cần thiết vì Factory đã làm)
                    brick.setSkinCode(brickCode);

                    bricks.add(brick);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bricks;
    }
}