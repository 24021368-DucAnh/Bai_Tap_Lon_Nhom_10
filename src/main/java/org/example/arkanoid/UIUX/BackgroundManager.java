package org.example.arkanoid.UIUX;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;

public class BackgroundManager {
    private final Pane backgroundPane;

    public BackgroundManager(int width, int height) {
        Image bgImage = new Image(getClass().getResource("/images/gamebg/gamebg.jpg").toExternalForm());
        Image topFrameImage = new Image(getClass().getResource("/images/gamebg/frame_top.png").toExternalForm());
        Image leftFrameImage = new Image(getClass().getResource("/images/gamebg/frame_left.png").toExternalForm());
        Image rightFrameImage = new Image(getClass().getResource("/images/gamebg/frame_right.png").toExternalForm());

        // Tạo nền
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(width);
        bgView.setFitHeight(height);

        // Khung trên
        ImageView topFrameView = new ImageView(topFrameImage);
        topFrameView.setFitWidth(width);
        topFrameView.setPreserveRatio(true);

        // Khung trái
        ImageView leftFrameView = new ImageView(leftFrameImage);
        leftFrameView.setFitHeight(height);
        leftFrameView.setPreserveRatio(true);

        // Khung phải
        ImageView rightFrameView = new ImageView(rightFrameImage);
        rightFrameView.setFitHeight(height);
        rightFrameView.setPreserveRatio(true);

        this.backgroundPane = new StackPane();


        backgroundPane.getChildren().add(bgView);
        StackPane.setAlignment(bgView, Pos.CENTER);

        backgroundPane.getChildren().add(topFrameView);
        StackPane.setAlignment(topFrameView, Pos.TOP_CENTER); // Căn lên trên

        backgroundPane.getChildren().add(leftFrameView);
        StackPane.setAlignment(leftFrameView, Pos.CENTER_LEFT); // Căn sang trái

        backgroundPane.getChildren().add(rightFrameView);
        StackPane.setAlignment(rightFrameView, Pos.CENTER_RIGHT); // Căn sang phải

    }

    public Pane getBackgroundPane() {
        return this.backgroundPane;
    }
}
