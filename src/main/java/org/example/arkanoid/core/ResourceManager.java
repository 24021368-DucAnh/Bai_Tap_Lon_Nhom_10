package org.example.arkanoid.core;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

/**
 * Lớp tiện ích để tải và lưu trữ tài nguyên game dùng chung.
 */
public class ResourceManager {

    // Đường dẫn
    private static final String FONT_PATH = "/font/pixel.ttf";
    private static final String PADDLE_IMAGE_PATH = "/images/Paddle.png";
    private static final String LIFE_ICON_PATH = "/images/heart.png";

    // Tài nguyên
    public static Font textFont;
    public static Font uiFont;
    public static Font titleFont;
    public static Font buttonFont;

    public static Image paddleImage;
    public static Image lifeIcon;
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
            uiFont = Font.loadFont(fontStream, 24);
        } catch (Exception e) {
            System.err.println("Lỗi tải font UI: " + e.getMessage());
            uiFont = Font.font("Arial", FontWeight.BOLD, 24);
        }

        // Tải ảnh Paddle
        try (InputStream imageStream = ResourceManager.class.getResourceAsStream(PADDLE_IMAGE_PATH)) {
            if (imageStream == null) throw new Exception("Không tìm thấy ảnh: " + PADDLE_IMAGE_PATH);
            paddleImage = new Image(imageStream);
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh Paddle: " + e.getMessage());
        }

        // Tải ảnh Máu
        try (InputStream imageStream = ResourceManager.class.getResourceAsStream(LIFE_ICON_PATH)) {
            if (imageStream == null) throw new Exception("Không tìm thấy ảnh: " + LIFE_ICON_PATH);
            lifeIcon = new Image(imageStream, 40, 40, true, true);
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh Máu: " + e.getMessage());
            lifeIcon = null;
        }
    }
}