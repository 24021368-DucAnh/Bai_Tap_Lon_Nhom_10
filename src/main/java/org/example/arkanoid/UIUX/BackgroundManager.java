package org.example.arkanoid.UIUX;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;

public class BackgroundManager {
    private final Pane backgroundPane;

    public BackgroundManager(int width, int height) {
        Image bgImage = ResourceManager.gameBackground;
        Image topFrameImage = ResourceManager.frameTop;
        Image leftFrameImage = ResourceManager.frameLeft;
        Image rightFrameImage = ResourceManager.frameRight;

        this.backgroundPane = new StackPane();

        // Tạo nền
        if (bgImage != null) {
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(width);
            bgView.setFitHeight(height);
            backgroundPane.getChildren().add(bgView);
            StackPane.setAlignment(bgView, Pos.CENTER);
        } else {
            System.err.println("BackgroundManager: Không tìm thấy gameBackground từ ResourceManager.");
        }

        // Khung trên
        if (topFrameImage != null) {
            ImageView topFrameView = new ImageView(topFrameImage);
            topFrameView.setFitWidth(width);
            topFrameView.setPreserveRatio(true);
            backgroundPane.getChildren().add(topFrameView);
            StackPane.setAlignment(topFrameView, Pos.TOP_CENTER); // Căn lên trên
        }

        // Khung trái
        if (leftFrameImage != null) {
            ImageView leftFrameView = new ImageView(leftFrameImage);
            leftFrameView.setFitHeight(height);
            leftFrameView.setPreserveRatio(true);
            backgroundPane.getChildren().add(leftFrameView);
            StackPane.setAlignment(leftFrameView, Pos.CENTER_LEFT); // Căn sang trái
        }

        // Khung phải
        if (rightFrameImage != null) {
            ImageView rightFrameView = new ImageView(rightFrameImage);
            rightFrameView.setFitHeight(height);
            rightFrameView.setPreserveRatio(true);
            backgroundPane.getChildren().add(rightFrameView);
            StackPane.setAlignment(rightFrameView, Pos.CENTER_RIGHT); // Căn sang phải
        }
    }

    public Pane getBackgroundPane() {
        return this.backgroundPane;
    }
}
