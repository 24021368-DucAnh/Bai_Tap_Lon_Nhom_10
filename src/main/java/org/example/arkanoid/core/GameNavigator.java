package org.example.arkanoid.core;

public interface GameNavigator {
    /**
     * Yêu cầu quay trở lại Start screen.
     */
    void goToStartScreen();
    /**
     * Yêu cầu thử lại màn chơi.
     */
    void retryGame();
}