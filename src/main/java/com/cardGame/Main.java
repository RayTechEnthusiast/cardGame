package com.cardGame;

import javax.swing.SwingUtilities;

public class Main {
    /** Pre: program starts. Post: opens the game window. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlackMench game = new BlackMench();
            new GUI(game);
        });
    }
}
