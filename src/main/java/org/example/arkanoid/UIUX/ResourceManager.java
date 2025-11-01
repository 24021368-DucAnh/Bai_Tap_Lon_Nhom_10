package org.example.arkanoid.UIUX;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.arkanoid.objects.PowerUpType;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Lớp tiện ích để tải và lưu trữ tài nguyên game dùng chung.
 */
public class ResourceManager {

    // Đường dẫn
    private static final String FONT_PATH = "/font/pixel.ttf";
    private static final String PADDLE_IMAGE_PATH = "/images/Paddle.png";
    private static final String LIFE_ICON_PATH = "/images/heart.png";
    private static final String POWERUP_PATH_PREFIX = "/images/PowerUp/";
    private static final String START_BG_PATH = "/images/startbg.jpg";
    private static final String HOWTOPLAY_BG_PATH = "/images/howtoplay.jpg";
    private static final String HELP_ICON_PATH = "/images/questionicon.png";

    private static final String GAME_BG_PREFIX = "/images/gamebg/";
    private static final String GAME_BG_PATH = GAME_BG_PREFIX + "gamebg.jpg";
    private static final String FRAME_TOP_PATH = GAME_BG_PREFIX + "frame_top.png";
    private static final String FRAME_LEFT_PATH = GAME_BG_PREFIX + "frame_left.png";
    private static final String FRAME_RIGHT_PATH = GAME_BG_PREFIX + "frame_right.png";

    // Tài nguyên
    public static Font textFont;
    public static Font uiFont;
    public static Font titleFont;
    public static Font buttonFont;

    public static Image paddleImage;
    public static Image lifeIcon;
    public static Image startScreenBackground;
    public static Image howToPlayBackground;
    public static Image helpIcon;
    public static Image gameBackground;
    public static Image frameTop;
    public static Image frameLeft;
    public static Image frameRight;


    private static final Map<PowerUpType, Image[]> powerUpAnimations = new EnumMap<>(PowerUpType.class);
    /**
     * Tải tất cả tài nguyên cần thiết cho game.
     * Gọi hàm này một lần khi game khởi động.
     */
    public static void loadAllResources() {
        // Tải font cho tiêu đề
        try (InputStream titleStream = ResourceManager.class.getResourceAsStream(FONT_PATH)) {
            if (titleStream == null) {
                throw new Exception("Không tìm thấy file font tiêu đề: " + FONT_PATH);
            }
            // Tải font
            titleFont = Font.loadFont(titleStream, 70); //kích thước
        } catch (Exception e) {
            System.err.println("Lỗi tải font tiêu đề: " + e.getMessage() + ". Sử dụng font mặc định.");
            // Font dự phòng nếu tải lỗi
            titleFont = Font.font("Impact", FontWeight.BOLD, 75);
        }

        // Tải font cho nút
        try (InputStream buttonStream = ResourceManager.class.getResourceAsStream(FONT_PATH)) {
            if (buttonStream == null) {
                throw new Exception("Không tìm thấy file font nút: " + FONT_PATH);
            }
            // Tải font
            buttonFont = Font.loadFont(buttonStream, 24); //kích thước
        } catch (Exception e) {
            System.err.println("Lỗi tải font nút: " + e.getMessage() + ". Sử dụng font mặc định.");
            // Font dự phòng nếu tải lỗi
            buttonFont = Font.font("Arial", FontWeight.BOLD, 24);
        }

        // Tải Font
        try (InputStream fontStream = ResourceManager.class.getResourceAsStream(FONT_PATH)) {
            if (fontStream == null) throw new Exception("Không tìm thấy font: " + FONT_PATH);
            textFont = Font.loadFont(fontStream, 60);
        } catch (Exception e) {
            System.err.println("Lỗi tải font: " + e.getMessage());
            textFont = Font.font("Impact", FontWeight.BOLD, 60);
        }

        // Tải Font UI
        try (InputStream fontStream = ResourceManager.class.getResourceAsStream(FONT_PATH)) {
            if (fontStream == null) throw new Exception("Không tìm thấy font: " + FONT_PATH);
            uiFont = Font.loadFont(fontStream, 30);
        } catch (Exception e) {
            System.err.println("Lỗi tải font UI: " + e.getMessage());
            uiFont = Font.font("Arial", FontWeight.BOLD, 24);
        }

        // --- Tải ảnh ---
        paddleImage = loadImage(PADDLE_IMAGE_PATH);
        lifeIcon = loadImage(LIFE_ICON_PATH, 40, 40);
        startScreenBackground = loadImage(START_BG_PATH);
        howToPlayBackground = loadImage(HOWTOPLAY_BG_PATH);
        helpIcon = loadImage(HELP_ICON_PATH);

        gameBackground = loadImage(GAME_BG_PATH);
        frameTop = loadImage(FRAME_TOP_PATH);
        frameLeft = loadImage(FRAME_LEFT_PATH);
        frameRight = loadImage(FRAME_RIGHT_PATH);

        // --- ANIMATION POWER-UP ---

        // Tải 8 frame cho ADD_LIFE (ví dụ: /images/powerups/add_life_1.png -> 8)
        powerUpAnimations.put(PowerUpType.PADDLE_GROW,
                loadAnimation(POWERUP_PATH_PREFIX + "powerup_expand_", 8));

        // Tải 8 frame cho ADD_BALL (ví dụ: /images/powerups/add_ball_1.png -> 8)
        powerUpAnimations.put(PowerUpType.ADD_BALL,
                loadAnimation(POWERUP_PATH_PREFIX + "powerup_duplicate_", 8));

        powerUpAnimations.put(PowerUpType.ADD_LIFE,
                loadAnimation(POWERUP_PATH_PREFIX + "powerup_life_", 8));
    }

    private static Image[] loadAnimation(String basePath, int frameCount) {
        Image[] frames = new Image[frameCount];
        boolean success = true;

        for (int i = 0; i < frameCount; i++) {
            String path = basePath + (i + 1) + ".png"; // Tải file từ 1 -> 8
            try (InputStream stream = ResourceManager.class.getResourceAsStream(path)) {
                if (stream == null) {
                    throw new Exception("Không tìm thấy file: " + path);
                }
                frames[i] = new Image(stream);
            } catch (Exception e) {
                System.err.println("Lỗi tải animation frame: " + e.getMessage());
                success = false; // Nếu thiếu 1 frame, coi như hỏng cả
                break;
            }
        }

        if (!success) {
            System.err.println("Không tải được animation cho: " + basePath);
            return null;
        }

        System.out.println("Tải thành công animation: " + basePath + " (" + frameCount + " frames)");
        return frames;
    }

    /**
     * Tải ảnh không resize
     */
    private static Image loadImage(String path) {
        try (InputStream stream = ResourceManager.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new Exception("Không tìm thấy ảnh: " + path);
            }
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tải ảnh có resize
     */
    private static Image loadImage(String path, double width, double height) {
        try (InputStream stream = ResourceManager.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new Exception("Không tìm thấy ảnh: " + path);
            }
            return new Image(stream, width, height, true, true); // Resize
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh: " + e.getMessage());
            return null;
        }
    }

    public static Image[] getPowerUpAnimation(PowerUpType type) {
        return powerUpAnimations.get(type);
    }
}