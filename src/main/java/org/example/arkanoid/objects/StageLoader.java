package org.example.arkanoid.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Đọc ASCII map từ src/main/resources/stages/Stage_<n>.txt qua classpath (portable). */
public final class StageLoader {
    private StageLoader() {}

    private static final String FOLDER = "/stages/";
    private static final String PREFIX = "Stage_";
    private static final String EXT    = ".txt";

    /** Ví dụ: loadFromIndex(0, 800) -> đọc /stages/Stage_0.txt */
    public static List<Brick> loadFromIndex(int index, double canvasW) {
        String name = PREFIX + index + EXT;
        return loadFromResource(name, canvasW, index);
    }

    /** Ví dụ: loadFromText("Stage_0.txt", 800) — cố gắng đoán stageIndex từ tên file; nếu không đoán được, mặc định 0. */
    public static List<Brick> loadFromText(String stageFile, double canvasW) {
        int guessedIndex = guessIndex(stageFile); // Stage_12.txt -> 12
        return loadFromResource(stageFile, canvasW, guessedIndex);
    }

    // -------------------- CORE LOADER --------------------
    private static List<Brick> loadFromResource(String stageFile, double canvasW, int stageIndex) {
        List<Brick> bricks = new ArrayList<>();

        try (InputStream is = StageLoader.class.getResourceAsStream(FOLDER + stageFile)) {
            if (is == null) {
                System.err.println("Không tìm thấy file stage trong resources" + FOLDER + stageFile);
                return bricks;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                // Đọc tất cả dòng, loại bỏ CRLF và BOM nếu có
                List<String> raw = reader.lines().map(StageLoader::cleanLine).toList();
                if (raw.isEmpty()) return bricks;

                // Cho phép mỗi dòng dài/ngắn khác nhau -> lấy độ dài lớn nhất làm số cột
                int rows = raw.size();
                int cols = raw.stream().mapToInt(String::length).max().orElse(0);
                if (cols == 0) return bricks;

                // Layout chuẩn, có thể chỉnh nếu muốn
                double pad = 4;
                double top = 60;
                double brickW = (canvasW - pad * (cols + 1)) / cols;
                double brickH = 22;

                for (int r = 0; r < rows; r++) {
                    String line = raw.get(r);
                    for (int c = 0; c < cols; c++) {
                        char ch = charAtSafe(line, c);
                        if (ch == '*' || ch == ' ' || ch == '\t' || ch == 0) continue;

                        ch = Character.toLowerCase(ch); // chuẩn hoá
                        double x = pad + c * (brickW + pad);
                        double y = top + r * (brickH + pad);

                        // → dùng Factory tạo đúng loại theo ký tự + truyền stageIndex cho SilverBrick tính HP
                        Brick brick = BrickFactory.fromCode(ch, x, y, (int) brickW, (int) brickH, stageIndex);
                        brick.setSkinCode(ch); // để Painter chọn sprite/màu
                        bricks.add(brick);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bricks;
    }

    // -------------------- HELPERS --------------------
    private static String cleanLine(String s) {
        if (s == null) return "";
        // bỏ CR (Windows CRLF) và BOM (nếu có)
        String out = s.replace("\r", "");
        if (!out.isEmpty() && out.charAt(0) == '\uFEFF') {
            out = out.substring(1);
        }
        return out;
    }

    /** Lấy ký tự an toàn: nếu c >= length -> trả về 0 (coi như trống). */
    private static char charAtSafe(String s, int c) {
        if (s == null || c < 0 || c >= s.length()) return 0;
        return s.charAt(c);
    }

    /** Đoán số index từ tên file kiểu Stage_12.txt. Không tìm được -> 0. */
    private static int guessIndex(String name) {
        try {
            int u = name.lastIndexOf('_');
            int dot = name.lastIndexOf('.');
            if (u >= 0 && dot > u) {
                return Integer.parseInt(name.substring(u + 1, dot));
            }
        } catch (Exception ignore) {}
        return 0;
    }
}