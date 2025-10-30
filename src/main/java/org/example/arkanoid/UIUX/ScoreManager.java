package org.example.arkanoid.UIUX; // (Giả sử bạn muốn file này ở UIUX)

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private static final String FILE_NAME = "src/main/resources/scores.txt";

    private static final int MAX_SAVES = 5;

    /**
     * Tải danh sách 5 điểm cao nhất.
     */
    public static List<Long> loadHighScores() {
        List<Long> highScores = new ArrayList<>();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("Không tìm thấy file điểm (Sẽ tạo mới): " + FILE_NAME);
            return highScores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    long score = Long.parseLong(line.trim());
                    if (score > 0) {
                        highScores.add(score);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Bỏ qua dòng lỗi trong file điểm: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Lỗi không tìm thấy file điểm: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Lỗi khi tải điểm cao: " + e.getMessage());
        }

        highScores.sort(Collections.reverseOrder());

        while (highScores.size() > MAX_SAVES) {
            highScores.remove(highScores.size() - 1);
        }

        return highScores;
    }

    public static void addScore(long newScore) {
        if (newScore <= 0) {
            return;
        }
        List<Long> highScores = loadHighScores();
        highScores.add(newScore);
        highScores.sort(Collections.reverseOrder());
        while (highScores.size() > MAX_SAVES) {
            highScores.remove(highScores.size() - 1);
        }

        saveHighScoresToFile(highScores);
    }

    /**
     * Ghi đè file save với danh sách điểm mới.
     */
    private static void saveHighScoresToFile(List<Long> scores) {
        try {
            File scoreFile = new File(FILE_NAME);

            File directory = scoreFile.getParentFile();

            // Kiểm tra xem thư mục cha có tồn tại không
            if (directory != null && !directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Đã tạo thư mục: " + directory.getPath());
                } else {
                    System.err.println("LỖI: Không thể tạo thư mục: " + directory.getPath());
                    return;
                }
            }

            // Ghi file vào đường dẫn đầy đủ
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile))) {
                for (long score : scores) {
                    writer.write(String.valueOf(score));
                    writer.newLine();
                }
                System.out.println("Đã lưu top 5 điểm cao vào: " + FILE_NAME);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu điểm cao: " + e.getMessage());
        }
    }

    public static long getHighestScore() {
        List<Long> scores = loadHighScores();
        return scores.isEmpty() ? 0 : scores.get(0);
    }
}